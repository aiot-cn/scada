package org.aiot.model.enums;

/**
 * 摄像头品牌
 * @author TAOJIN
 * rtsp://admin:123456@192.168.1.64:554
 */
public enum CameraBrandEnum {
    HIKVISION   ("海康威视", "{rtsp}/h264/ch{ch}/[main,sub]/av_stream"),
    HIKVISION2  ("海康新版", "{rtsp}/Streaming/Channels/{ch}[01,02,03]"),
    DAHUA       ("大华",    "{rtsp}/cam/realmonitor?channel={ch}&subtype=[0,1]"),
    UNIVIEW     ("宇视",    "{rtsp}/unicast/c{ch}/s[0,1]/live"),
    TIANDY      ("天地伟业", "{rtsp}/{ch}/[1,2]"),
    HUAWEI      ("华为",    "{rtsp}/LiveMedia/ch{ch}/Media[1,2]"),
    TPLINK      ("TP-LINK","{rtsp}/stream[1,2]"),
    TOPSEE      ("天视通",  "{rtsp}/[mpeg4,mpeg4cif]"),
    ;

    private String name;
    private String template;

    CameraBrandEnum(String name, String template) {
        this.name = name;
        this.template = template;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTemplate() {
        return template;
    }

    /**
     * 根据结构化参数生成RTSP地址
     * @param host      IP:端口
     * @param account   账号
     * @param password  密码
     * @param channel   通道号
     * @param streamType 码流类型 (main/sub)
     */
    public String buildRtsp(String host, String account, String password, String channel, String streamType) {
        String auth = account + ":" + password;
        boolean isSub = "sub".equals(streamType);
        return template
                .replace("{auth}", auth)
                .replace("{host}", host)
                .replace("{ch}", channel)
                .replace("{stream}", isSub ? "sub" : "main")
                .replace("{subtype}", isSub ? "1" : "0");
    }
}
