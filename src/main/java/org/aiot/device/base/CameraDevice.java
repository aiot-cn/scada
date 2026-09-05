package org.aiot.device.base;

import org.aiot.device.BaseDevice;
import org.aiot.lang.annotation.AoReflect;
import org.aiot.model.enums.AstEnum;

public class CameraDevice extends BaseDevice {
    @AoReflect(value = "地址",getter = true)
    protected String host;

    @AoReflect(value ="用户名",type = AstEnum.param)
    protected String user = "admin";

    @AoReflect(value ="密码",type = AstEnum.param)
    protected String password = "123456";

    @AoReflect(value = "RTSP地址",getter = true)
    String rtspUrl;

    @AoReflect(value = "RTSP格式", type = AstEnum.param)
    //大华 rtsp://[user]:[password]@[host]:[port]/cam/realmonitor?channel=1&subtype=0
    private String rtspFormat = "rtsp://[user]:[password]@[host]:[port]/h264/ch1/main/av_stream";
    @AoReflect(value = "RTSP局域网", type = AstEnum.param)
    private String rtspLan = "";
    @AoReflect(value = "RTSP因特网", type = AstEnum.param)
    private String rtspInternet = "";

    public String getHost(){
        return device.getAddress();
    }

    public String getRtspUrl() {
        String a = rtspFormat.replace("[user]",user).replace("[password]",password);
        String url = a.replace("[host]",getHost()).replace("[port]","554");
        url += ";" + a.replace("[host]:[port]",rtspLan);
        url += ";" + a.replace("[host]:[port]",rtspInternet);
        return url;
    }

}
