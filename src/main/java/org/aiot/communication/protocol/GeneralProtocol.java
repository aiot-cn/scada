package org.aiot.communication.protocol;

import org.aiot.infc.ProtocolInfc;
import org.aiot.infc.device.DeviceInfc;
import org.aiot.lang.Command;
import org.aiot.lang.annotation.AoReflect;
import org.aiot.main.Constants;
import org.aiot.model.enums.CdataEnum;
import org.aiot.model.table.*;
import org.aiot.service.BaseService;
import org.aiot.service.DeviceService;
import org.aiot.util.CalcUtil;
import org.aiot.util.SysUtil;
import org.nutz.lang.Strings;
import org.nutz.log.Logs;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static org.aiot.main.Constants.ioc;

@AoReflect("通用")
public class GeneralProtocol implements ProtocolInfc {
    public static final Map<String,Long> lastBuildTime = new HashMap<>();
    @Override
    public void build(Command command){
        Object[] format = command.getArgs();
        TDevice device = command.getDevice();
        DeviceCommand deviceCommand = command.getDeviceCommand();
        Integer invl = deviceCommand.getInvl();
        if(invl != null){
            String key = device.getId()+"_"+deviceCommand.getId();
            //下次时间
            long afterTime = lastBuildTime.getOrDefault(key,0L) + invl * 1000;
            long time0 = afterTime - System.currentTimeMillis();
            if(time0 > 0){
                throw new RuntimeException("下次执行时间"+(time0/1000)+"S后");
            }else{
                lastBuildTime.put(key,System.currentTimeMillis());
            }
        }
        String content = deviceCommand.getContent();
        try {
            //真实指令 @:地址位 $1:CRC16校验
            if(device != null)
                content = content.replace("@", Strings.sNull(device.getAddress(), ""));
            if(content.contains("${"))
                content = Constants.format(content);


            //如果有参数
            if(format != null && format.length > 0){
                // x|x|x ，值对应参数n相应位置
                int index = 0;
                Matcher matcher = Pattern.compile("\\s?(\\w+\\|)+\\w+\\s?").matcher(content);
                while(matcher.find()){
                    String[] v = matcher.group().split("\\|");
                    try{
                        String val = v[Integer.parseInt(format[index]+"")].trim();
                        content = matcher.replaceFirst(val);
                    }catch (Exception e) {
                        Logs.get().errorf("指令异常:%s,%s,%s,%d",device.getName(),content,format[0],index);
                    }

                    index++;
                }

                //{n}... 参数替换
                content = MessageFormat.format(content,format);
            }

            //以$xx开头的，执行函数替换
            Matcher matcher = Pattern.compile("\\$\\w+\\s?").matcher(content);
            while (matcher.find()) {
                String con = matcher.replaceFirst("").replaceAll(" ", "");
                Object val = SysUtil.scriptByName(matcher.group().trim(), con ,device);
                content = matcher.replaceFirst(val == null ? "" : val.toString());
                matcher.reset(content);
            }

            //删除所有空格
            if(deviceCommand.getIsHex())
                content = content.replaceAll("[ ]", "");
            else
                content = content.replaceAll("\\$!", "\\$");

            content = content.replaceAll("\\\\r\\\\n", "\r\n");

            String crc = (String) SysUtil.scriptByName(deviceCommand.getCrc(), content,command);
            //校验码需要最后添加
            if(Strings.isNotBlank(crc)){
                content += crc;
            }
            command.setContent(content);
            command.setDataToSend(deviceCommand.getIsHex() ?  CalcUtil.hexToByte(content) : content.getBytes("GBK"));  ;

        }catch (Exception e){
            String argStr = "";
            if(format != null && format.length > 0)
                argStr = Arrays.stream(format).map(Object::toString).collect(Collectors.joining(","));
            String msg = String.format("设备%s#%s,指令%s:%s(%s) 生成错误:%s", device.getName(),device.getAddress(), deviceCommand.getCode(), deviceCommand.getContent(), argStr,e.getMessage());
            command.sendSocket(CdataEnum.OTS,msg);
        }
    }

    @Override
    public void analysis(Command command) {
        byte[] data = command.getDataReceived();
        if(data == null || data.length == 0)
            return;

        DeviceInfc dev = command.getDeviceInfc();
        DeviceCommand deviceCommand = command.getDeviceCommand();
        Long commandId = deviceCommand.getId();
        boolean isHex = deviceCommand.getIsHex();

        String message = isHex ? command.byteToStr(data) : new String(data);

        String pattern = deviceCommand.getPattern();
        if(Strings.isNotBlank(pattern)){
            String verify = isVerify(message,pattern.replaceAll("@",command.getDevice().getAddress()));
            if(verify != null){
                command.sendSocket(CdataEnum.Pa,"warn["+commandId+"]"+verify);
                return;
            }
        }
        BaseService bs = ioc.get(BaseService.class);
        DeviceService ds = ioc.get(DeviceService.class);
        List<DeviceAnalysis> analysisList = bs.getTCache(DeviceAnalysis.class, v->commandId.equals(v.getCommandId()));

        if(analysisList == null || analysisList.size() == 0){
            command.sendSocket(CdataEnum.Pa,"warn 无对应解析项");
            return;
        }
        long t1 = System.currentTimeMillis();
        StringBuilder socketMsg = new StringBuilder();
        for(DeviceAnalysis analysis : analysisList){
            if(Strings.isNotBlank(analysis.getPattern()) && !message.matches(analysis.getPattern())){
                continue;//如果回复的消息与该解析规则要求的不匹配，则跳过
            }

            int start = analysis.getStart() == null ? 0 : analysis.getStart();
            Integer  end = analysis.getEnd();
            String calc = analysis.getCalc();
            String code = analysis.getCode();


            String value= "" ;
            if(end == null){
                value = message.substring(start);
            }else if(end <= message.length()){
                value = message.substring(start, end);
            }

            String numeric = analysis.getConversion();

            Object ov = value;
            if(Strings.isNotBlank(numeric)){
                Object D1 = dev.getDevData(code);
                ov = SysUtil.scriptByName(numeric,value,analysis,D1,dev,command);
            }

            //计算
            if(Strings.isNotBlank(calc) && ov != null){
                String ev = calc.replaceAll("@", ov.toString());
                try {
                    ov = SysUtil.jsEval(ev);
                } catch (Exception e) {
                    command.sendSocket(CdataEnum.Pa,"error["+code+"]"+e.getMessage());
                    continue;
                }
            }

            //小数位
            Integer correct = analysis.getCorrect();
            if(ov != null && correct != null && correct >=0){
                ov = SysUtil.jsEval("parseFloat(("+ov+").toFixed("+correct+"))");
            }

            DeviceProperty dp = ds.getProperty(command.getDevice(),code);
            socketMsg.append(dp != null ? dp.getName() : code).append(":").append(ov).append(" ");

            if(Strings.isBlank(analysis.getExpected()) || (Boolean) SysUtil.jsEval(analysis.getExpected().replaceAll("@",ov+""))){
                dev.putData(analysis.getCode(), ov);
            }else{
                socketMsg.append("[值不符合预期] ");
            }
        }
        command.sendSocket(CdataEnum.Pa, socketMsg + " ms:"+(System.currentTimeMillis() - t1));
    }

    public String isVerify(String m,String messagePattern){
        if(messagePattern.equals("$1")) {
            if(m.length() >= 12){
                int L = m.length() - 4;
                if(CalcUtil.crcModbus(m.substring(0,L)).toUpperCase().equals(m.substring(L))){
                    return null;
                }else {
                    return "校验位不匹配";
                }

            }else if(m.length() == 10){
                int errCode = Integer.parseInt(m.substring(5,6));
                if(errCode == 1)
                    return  "不支持该功能码";
                if(errCode == 2)
                    return  "超出寄存器地址范围";
                if(errCode == 3)
                    return  "超出寄存器最大数量";
                if(errCode == 4)
                    return  "请求的数据出错";
            }

        }else{
            if(m.matches(messagePattern))
                return null;
        }
        return "false";
    }
}
