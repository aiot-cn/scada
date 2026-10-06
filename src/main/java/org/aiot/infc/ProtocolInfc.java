package org.aiot.infc;


import org.aiot.lang.Command;
import org.aiot.model.table.TDevice;

import java.util.List;

public interface ProtocolInfc {
    //执行 包含巡检
    List<Command> buildType(String type,TDevice device,String remark,Object... format);

    //设置
    List<Command> buildSet(String code,TDevice device,String remark,Object... format);

    /**
     * 构建 具体指令
     * 在发送前一刻，比如1分钟巡检一次还未到时间
     */

    void build(Command command);

    //解析
    void analysis(Command command);

}
