package org.aiot.infc.device;

import org.aiot.lang.Command;
import org.aiot.lang.CommonAction;
import org.aiot.model.table.TDevice;
import org.nutz.aop.MethodInterceptor;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

public interface DeviceInfc extends MethodInterceptor {
    void init(); //初始化
    void selfTest();//自检
    void destroy();//销毁

    DevData putData(String code,Object value);
    DevData getDevData(String key);
    //查看当前设备数据
    Map<String,DevData> getDataMap();

    Method getMethod(String name);

    List<Command> comPoll();
    List<Command> comSet(String key,Object... p);
    void comRx(Command command);

    void setEnv(TDevice device, DeviceInfc target);

    Object invoke(String method,Map<String,Object> args);
    Object invoke(String method, String args, CommonAction commonAction);

}
