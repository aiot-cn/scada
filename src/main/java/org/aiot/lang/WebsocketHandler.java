package org.aiot.lang;

import org.aiot.infc.device.DeviceInfc;
import org.aiot.main.Constants;
import org.aiot.service.BaseService;
import org.aiot.service.DeviceService;
import org.nutz.lang.util.NutMap;
import org.nutz.plugins.mvc.websocket.handler.SimpleWsHandler;

public class WebsocketHandler extends SimpleWsHandler {
    public WebsocketHandler(){
        super(""); // 覆盖默认前缀
    }

    public void sayhi(NutMap req) { // 对应js端的action名称,方法参数必须是NutMap哦
        String name = req.getString("name");// 可以拿到页面发过来的任意内容
        NutMap resp = new NutMap("action", "notify"); // 响应的内容完全由你决定,推荐用{action:"xxx", ....}
        resp.setv("msg", "hi, " + name);
        endpoint.sendJson(session.getId(), resp); // 通过endpoint可以发生给任何你想发生的对象, session就是当前WebSocket的会话.
    }

    public void devExec(NutMap req){
        Long devId = req.getLong("devId");
        DeviceInfc d = Constants.ioc.get(DeviceService.class).getInstance(devId);
        d.invoke(req.getString("method"), req);
    }

}
