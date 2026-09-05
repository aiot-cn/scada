package org.aiot.infc;


import org.aiot.lang.Command;

public interface ProtocolInfc {
    //构建
    void build(Command command);

    //解析
    void analysis(Command command);
}
