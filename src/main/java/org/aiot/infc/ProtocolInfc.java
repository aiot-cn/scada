package org.aiot.infc;


import org.aiot.lang.Command;
import org.aiot.model.table.TDevice;

import java.util.List;

public interface ProtocolInfc {
    //构建
    void build(Command command);

    //解析
    void analysis(Command command);

    /**
     * 构建指令列表，由 DeviceService 直接调用，指令构建的主体是协议本身。
     * 默认按 DeviceCommand 配置构建(GeneralProtocol 模式)；
     * DeviceCommand 只是协议可能会用到的配置，覆写此方法的协议可自行决定是否读取它
     */
    List<Command> buildCommands(TDevice device, String commandType, String remark, Object... format);
}
