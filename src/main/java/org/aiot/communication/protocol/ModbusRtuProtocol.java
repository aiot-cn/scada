package org.aiot.communication.protocol;

import org.aiot.communication.CommunicationInfc;
import org.aiot.infc.ProtocolInfc;
import org.aiot.infc.device.DeviceInfc;
import org.aiot.lang.Command;
import org.aiot.lang.annotation.AoReflect;
import org.aiot.model.enums.CdataEnum;
import org.aiot.model.table.DeviceCommand;
import org.aiot.model.table.DeviceProperty;
import org.aiot.model.table.TDevice;
import org.aiot.service.BaseService;
import org.aiot.service.CommuService;
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
 * 自动分组规则：遥测(type 0)用03读保持寄存器，遥信(type 1)用02读离散输入，遥控(type 2)不巡检；
 * 同类属性按地址升序，地址跨度超过8拆分为多帧分开发送(每组一次请求)。
 * 设备无需配置 DeviceCommand，配置 comPoll 指令行时仅作为调度参数来源(延迟/超时等)，
 * 巡检节奏由通讯线程控制：无指令构建时队列空闲会自动等待
 */
@AoReflect("ModbusRTU")
public class ModbusRtuProtocol implements ProtocolInfc {

    /**
     * 直接由协议构建巡检指令，帧内容由 DeviceProperty 地址配置生成。
     * comPoll 的 DeviceCommand 配置行仅作为调度参数来源(延迟/响应/超时)，无配置时使用默认值
     */
    @Override
    public List<Command> buildCommands(TDevice device, String commandType, String remark, Object... format) {
        if(!"comPoll".equals(commandType))
            return null;
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
        Command c = new Command(device, dc, Strings.sBlank(remark, "巡检"));
        List<Command> list = new ArrayList<>();
        list.add(c);
        return list;
    }

    @Override
    public void build(Command command) {
        TDevice device = command.getDevice();
        try {
            List<List<DeviceProperty>> groups = groupProperties(device);
            if (groups.isEmpty())
                throw new RuntimeException("无可读取的属性配置(需配置属性地址)");

            //最后一组由当前指令承载(通讯线程发送)，之前的组在此同步发送并解析，保证线上按组顺序
            for (int i = 0; i < groups.size() - 1; i++) {
                Command cmd = new Command(device, command.getDeviceCommand(), "分组" + (i + 1));
                setFrame(cmd, groups.get(i));
                CommunicationInfc ci = ioc.get(CommuService.class).getInstance(command.getCommunication().getId());
                byte[] b = cmd.sendCommand(ci);
                cmd.setRX(b);
                analysis(cmd);
            }
            setFrame(command, groups.get(groups.size() - 1));

        } catch (Exception e) {
            command.sendSocket(CdataEnum.OTS, String.format("设备%s#%s 指令生成错误:%s",
                    device.getName(), device.getAddress(), e.getMessage()));
        }
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

            //起始地址取构建时记录的值，丢失时重算最小地址
            Object startObj = command.getReceive();
            int type = func <= 2 ? 1 : 0;
            List<DeviceProperty> propList = getProperties(command.getDevice()).stream()
                    .filter(v -> (v.getType() == null ? 0 : v.getType()) == type)
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
        int func = (group.get(0).getType() != null && group.get(0).getType() == 1) ? 2 : 3;
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
     * 属性分组：遥测(0)与遥信(1)分开，同类内按地址升序、跨度超过8再拆分，每组一次读取
     */
    private List<List<DeviceProperty>> groupProperties(TDevice device) {
        List<DeviceProperty> propList = getProperties(device);
        List<List<DeviceProperty>> groups = new ArrayList<>();
        for (int type = 0; type <= 1; type++) {
            int t = type;
            List<DeviceProperty> sorted = propList.stream()
                    .filter(p -> (p.getType() == null ? 0 : p.getType()) == t)
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
     * 设备的可读取属性，按地址(16进制)升序。
     * 设备专属属性(deviceId)按code覆盖设备类型默认属性
     */
    private List<DeviceProperty> getProperties(TDevice device) {
        BaseService bs = ioc.get(BaseService.class);
        Map<String, DeviceProperty> map = new HashMap<>();
        bs.getTCache(DeviceProperty.class, p -> Strings.equals(device.getDeviceType(), p.getDeviceType()) && p.getDeviceId() == null)
                .forEach(p -> map.put(p.getCode(), p));
        bs.getTCache(DeviceProperty.class, p -> Strings.equals(device.getDeviceType(), p.getDeviceType()) && device.getId().equals(p.getDeviceId()))
                .forEach(p -> map.put(p.getCode(), p));

        return map.values().stream().filter(p -> Strings.isNotBlank(p.getAddress()))
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
