package org.aiot.communication.protocol;

import org.aiot.infc.ProtocolInfc;
import org.aiot.infc.device.DeviceInfc;
import org.aiot.lang.Command;
import org.aiot.lang.annotation.AoReflect;
import org.aiot.model.enums.CdataEnum;
import org.aiot.model.enums.CommandTypeEnum;
import org.aiot.model.table.DeviceCommand;
import org.aiot.model.table.DeviceProperty;
import org.aiot.model.table.TDevice;
import org.aiot.service.BaseService;
import org.aiot.util.CalcUtil;
import org.aiot.util.SysUtil;
import org.nutz.lang.Strings;

import java.util.*;
import java.util.stream.Collectors;

import static org.aiot.main.Constants.ioc;

/**
 * Modbus RTU 协议，不依赖 DeviceAnalysis，巡检帧完全由 DeviceProperty 的地址配置自动生成：
 * 从机地址取 TDevice.address，寄存器地址取 DeviceProperty.address(按16进制解析，如 104 表示 0x104)，
 * 二次计算脚本取 DeviceProperty.calcScript(原始值用@符号代替，如 @/100)。
 *
 * 自动分组规则(type与modbus功能码一致，未配置type的属性不参与通讯)：遥控(1)用01读线圈，遥信(2)用02读离散输入，
 * 遥测(3)用03读保持寄存器，输入寄存器(4)用04读取.
 * 设置：遥控属性用05写单线圈(on→ff00/off→0000)，其余可写属性用06写单寄存器(原始值，支持0x前缀16进制).
 * 同类属性按地址升序，地址跨度超过8拆分为多帧分开发送(每组一次请求)。
 * 设备无需配置 DeviceCommand，配置 comPoll 指令行时仅作为调度参数来源(延迟/超时等)，
 * 巡检节奏由通讯线程控制：无指令构建时队列空闲会自动等待
 */
@AoReflect("ModbusRTU")
public class ModbusRtuProtocol implements ProtocolInfc {

    /**
     * 直接由协议构建巡检指令：按 DeviceProperty 地址配置分组，每组生成一条指令且帧在此构建完成，
     * 全部入队后由通讯线程逐条发送。
     * comPoll 的 DeviceCommand 配置行仅作为调度参数来源(延迟/响应/超时)，无配置时使用默认值
     */
    @Override
    public List<Command> buildType(String commandType, TDevice device, String remark, Object... format) {
        List<Command> list = new ArrayList<>();
        if(!CommandTypeEnum.comPoll.name().equals(commandType))
            return list;

        BaseService bs = ioc.get(BaseService.class);
        DeviceCommand dc = bs.getTCacheFirst(DeviceCommand.class, v->
                Strings.equals(device.getDeviceType(), v.getDeviceType()) && Strings.equals(commandType, v.getCode()));
        if(dc == null){
            dc = new DeviceCommand();
            dc.setId(0L);
            dc.setDeviceType(device.getDeviceType());
            dc.setCode(commandType);
        }
        dc.setIsHex(true);

        List<List<DeviceProperty>> groups = groupProperties(device);
        if(groups.isEmpty())
            throw new RuntimeException("无可读取的属性配置(需配置属性地址)");

        String r = Strings.sBlank(remark, commandType);

        for (int i = 0; i < groups.size(); i++) {
            Command c = new Command(device, dc, groups.size() == 1 ? r : r + " 分组" + (i + 1));
            setFrame(c, groups.get(i));
            list.add(c);
        }
        return list;
    }

    /**
     * 设置下发：遥控(type 1)属性用05写单线圈，其余可写属性用06写单寄存器。
     * format[0]为要写的值：线圈支持 on/off/开/关/true/false/1/0(非零即合)，
     * 寄存器支持十进制(含负数)或0x前缀16进制，写入原始值(不做calcScript反算)
     */
    @Override
    public List<Command> buildSet(String code, TDevice device, String remark, Object... format) {
        if(format == null || format.length == 0 || format[0] == null || Strings.isBlank(format[0].toString()))
            throw new RuntimeException("缺少设置值");

        DeviceProperty p = getProperties(device).stream()
                .filter(v -> Strings.equals(code, v.getCode()))
                .findFirst().orElse(null);
        if(p == null)
            throw new RuntimeException("属性" + code + "未配置类型或地址,不能设置");
        int type = p.getType();
        if(type == 2 || type == 4)
            throw new RuntimeException("遥信/输入寄存器属性" + code + "为只读,不能设置");

        String val;
        if(type == 1) { //遥控 05写单线圈
            val = parseOn(format[0]) ? "ff00" : "0000";
        } else { //遥测 06写单寄存器
            int n = parseVal(format[0].toString());
            if(n < -32768 || n > 0xFFFF)
                throw new RuntimeException("寄存器值超出范围(-32768~65535):" + n);
            val = String.format("%04x", n & 0xFFFF);
        }
        int func = type == 1 ? 5 : 6;
        String frame = String.format("%02x%02x%04x%s", parseAddr(device.getAddress()), func, parseAddr(p.getAddress()), val);
        String hex = frame + CalcUtil.crcModbus(frame).toLowerCase();

        BaseService bs = ioc.get(BaseService.class);
        DeviceCommand dc = bs.getTCacheFirst(DeviceCommand.class, v->
                Strings.equals(device.getDeviceType(), v.getDeviceType()) && Strings.equals(CommandTypeEnum.comSet.name(), v.getCode()));
        if(dc == null){
            dc = new DeviceCommand();
            dc.setId(0L);
            dc.setDeviceType(device.getDeviceType());
            dc.setCode(CommandTypeEnum.comSet.name());
        }
        dc.setIsHex(true);

        Command c = new Command(device, dc, Strings.sBlank(remark, "设置" + code));
        c.setHex(true);
        c.setContent(hex);
        c.setDataToSend(CalcUtil.hexToByte(hex));
        List<Command> list = new ArrayList<>();
        list.add(c);
        return list;
    }

    /**
     * 帧已在 buildType 中按属性分组构建完成，此处无需处理，发送与解析由通讯线程完成
     */
    @Override
    public void build(Command command) {

    }

    @Override
    public void analysis(Command command) {
        byte[] data = command.getDataReceived();
        if (data == null || data.length == 0)
            return;
        try {
            if (data.length < 5 || !CalcUtil.isModbusCRC16(data)) {
                command.sendSocket(CdataEnum.Pa, "warn 校验位不匹配:" + CalcUtil.byteToHex(data));
                return;
            }

            int func = data[1] & 0xFF;
            if ((func & 0x80) != 0) {
                command.sendSocket(CdataEnum.Pa, "warn " + errMsg(data[2] & 0xFF));
                return;
            }

            //05/06写响应为请求帧原样回显，比对一致即成功，实际状态由后续巡检回读
            if (func == 5 || func == 6) {
                String echo = CalcUtil.byteToHex(data);
                if (echo.equalsIgnoreCase(command.getContent()))
                    command.sendSocket(CdataEnum.Pa, "设置成功 " + echo);
                else
                    command.sendSocket(CdataEnum.Pa, "warn 设置回显与请求不一致:" + echo);
                return;
            }

            //起始地址取构建时记录的值，丢失时重算最小地址
            Object startObj = command.getReceive();
            int type = func;//功能码与属性type一致：01线圈→遥控 02离散→遥信 03/04寄存器→遥测/输入寄存器
            List<DeviceProperty> propList = getProperties(command.getDevice()).stream()
                    .filter(v -> v.getType() == type)
                    .collect(Collectors.toList());
            int start;
            if (startObj instanceof Integer) {
                start = (Integer) startObj;
            } else {
                if (propList.isEmpty())
                    return;
                start = parseAddr(propList.get(0).getAddress());
            }

            long t1 = System.currentTimeMillis();
            DeviceInfc dev = command.getDeviceInfc();
            StringBuilder socketMsg = new StringBuilder();
            for (DeviceProperty p : propList) {
                int idx = parseAddr(p.getAddress()) - start;
                Object ov;
                if (func <= 2) { //位：第n个位在data[3+n/8]的第n%8位
                    int i = 3 + idx / 8;
                    if (idx < 0 || i >= data.length)
                        continue;
                    ov = (data[i] >> (idx % 8)) & 1;
                } else { //寄存器：16位，按有符号数处理
                    int i = 3 + idx * 2;
                    if (idx < 0 || i + 1 >= data.length)
                        continue;
                    ov = (short) (((data[i] & 0xFF) << 8) | (data[i + 1] & 0xFF));
                }

                //二次计算，原始值用@代替
                if (Strings.isNotBlank(p.getCalcScript()))
                    ov = SysUtil.jsCalc(p.getCalcScript(), ov);

                dev.putData(p.getCode(), ov);
                socketMsg.append(Strings.sBlank(p.getName(), p.getCode())).append(":").append(ov).append(" ");
            }
            command.sendSocket(CdataEnum.Pa, socketMsg + " ms:" + (System.currentTimeMillis() - t1));
        } catch (Exception e) {
            command.sendSocket(CdataEnum.Pa, "error " + e.getMessage());
        }
    }

    /**
     * 生成一组属性的读取帧
     */
    private void setFrame(Command command, List<DeviceProperty> group) {
        TDevice device = command.getDevice();
        int slave = parseAddr(device.getAddress());
        int func = group.get(0).getType();//type与功能码一致：01线圈 02离散输入 03保持寄存器 04输入寄存器
        int start = parseAddr(group.get(0).getAddress());
        int count = parseAddr(group.get(group.size() - 1).getAddress()) - start + 1;

        String frame = String.format("%02x%02x%04x%04x", slave, func, start, count);
        String hex = frame + CalcUtil.crcModbus(frame).toLowerCase();

        command.setReceive(start);//起始地址，解析时定位用
        command.setHex(true);
        command.setContent(hex);
        command.setDataToSend(CalcUtil.hexToByte(hex));
    }

    /**
     * 属性分组：遥控(1)、遥信(2)、遥测(3)、输入寄存器(4)分开，同类内按地址升序、跨度超过8再拆分，每组一次读取
     */
    private List<List<DeviceProperty>> groupProperties(TDevice device) {
        List<DeviceProperty> propList = getProperties(device);
        List<List<DeviceProperty>> groups = new ArrayList<>();
        for (int type = 1; type <= 4; type++) {
            int t = type;
            List<DeviceProperty> sorted = propList.stream()
                    .filter(p -> p.getType() == t)
                    .collect(Collectors.toList());

            List<DeviceProperty> cur = new ArrayList<>();
            int start = -1;
            for (DeviceProperty p : sorted) {
                int addr = parseAddr(p.getAddress());
                if (start < 0 || addr - start > 8) {
                    if (!cur.isEmpty())
                        groups.add(cur);
                    cur = new ArrayList<>();
                    start = addr;
                }
                cur.add(p);
            }
            if (!cur.isEmpty())
                groups.add(cur);
        }
        return groups;
    }

    /**
     * 设备的可读取属性(需配置type与地址)，按地址(16进制)升序。
     * 设备专属属性(deviceId)按code覆盖设备类型默认属性
     */
    private List<DeviceProperty> getProperties(TDevice device) {
        BaseService bs = ioc.get(BaseService.class);
        Map<String, DeviceProperty> map = new HashMap<>();
        bs.getTCache(DeviceProperty.class, p -> Strings.equals(device.getDeviceType(), p.getDeviceType()) && p.getDeviceId() == null)
                .forEach(p -> map.put(p.getCode(), p));
        bs.getTCache(DeviceProperty.class, p -> Strings.equals(device.getDeviceType(), p.getDeviceType()) && device.getId().equals(p.getDeviceId()))
                .forEach(p -> map.put(p.getCode(), p));

        return map.values().stream().filter(p -> p.getType() != null && Strings.isNotBlank(p.getAddress()))
                .sorted(Comparator.comparingInt(p -> parseAddr(p.getAddress())))
                .collect(Collectors.toList());
    }

    /**
     * 地址按16进制解析，如 104 表示 0x104，支持0x前缀
     */
    private int parseAddr(String s) {
        if (Strings.isBlank(s))
            throw new RuntimeException("地址不能为空");
        s = s.trim().replace(" ", "");
        if (s.toLowerCase().startsWith("0x"))
            s = s.substring(2);
        return Integer.parseInt(s, 16);
    }

    /**
     * 线圈值解析：true/on/开/非零 为合，false/off/关/0 为断
     */
    private boolean parseOn(Object v) {
        if (v instanceof Boolean)
            return (Boolean) v;
        if (v instanceof Number)
            return ((Number) v).doubleValue() != 0;
        String s = v.toString().trim();
        if ("on".equalsIgnoreCase(s) || "true".equalsIgnoreCase(s) || "开".equals(s))
            return true;
        if ("off".equalsIgnoreCase(s) || "false".equalsIgnoreCase(s) || "关".equals(s))
            return false;
        return parseVal(s) != 0;
    }

    /**
     * 数值解析：十进制(含负数)或0x前缀16进制
     */
    private int parseVal(String s) {
        s = s.trim().replace(" ", "");
        try {
            if (s.toLowerCase().startsWith("0x"))
                return Integer.parseInt(s.substring(2), 16);
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            throw new RuntimeException("无法解析数值:" + s);
        }
    }

    private String errMsg(int code) {
        switch (code) {
            case 1: return "不支持该功能码";
            case 2: return "超出寄存器地址范围";
            case 3: return "超出寄存器最大数量";
            case 4: return "请求的数据出错";
            default: return "异常码" + code;
        }
    }
}
