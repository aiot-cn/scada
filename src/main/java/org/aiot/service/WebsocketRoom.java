package org.aiot.service;


import org.aiot.lang.WebsocketHandler;
import org.aiot.main.Constants;
import org.aiot.model.table.TBase;
import org.aiot.model.table.TCommunication;
import org.aiot.model.table.TDevice;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.plugins.mvc.websocket.AbstractWsEndpoint;
import org.nutz.plugins.mvc.websocket.NutWsConfigurator;
import org.nutz.plugins.mvc.websocket.WsHandler;

import javax.websocket.EndpointConfig;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.util.Date;
import static org.aiot.main.Constants.ioc;

//ServerEndpoint是websocket的必备注解, value是映射路径, configurator是配置类.
@ServerEndpoint(value = "/websocket", configurator=NutWsConfigurator.class)
@IocBean // 使用NutWsConfigurator的必备条件
public class WebsocketRoom extends AbstractWsEndpoint {
 // 并不需要你马上实现任何方法,它也马上能工作

	/**
	 * 根据WebSocket会话创建一个WsHandler. 注意,
	 * 该实例还得实现MessageHandler.Whole或MessageHandler.Partial接口!!!
	 */
	@Override
	public WsHandler createHandler(Session session, EndpointConfig config) {
		return new WebsocketHandler();
	}


	Log log = Logs.get();
	
	/**
	 * 
	 * @param room	房间
	 * @param msg	消息
	 * @param isSync 是否同步阻塞
	 */
	public void sendMsg(String room,Object msg,boolean isSync) {
	    try {
	    	each(room, (index, ele, length) -> {
				if(isSync){
					if(msg instanceof String)
						sendTextSync(ele.getId(), (CharSequence) msg);
					else
						sendJsonSync(ele.getId(), msg);
				}else{
					if(msg instanceof String)
						sendText(ele.getId(), (CharSequence) msg);
					else
						sendJson(ele.getId(), msg);
				}
			});
		} catch (Exception e) {
			log.debug(e.getMessage());
		}	
	}

	public static void sendRoot(String msg,Object... args){
		ioc.get(WebsocketRoom.class).sendMsg("ROOT", Constants.hmsSFormat.format(new Date()) + " " + String.format(msg,args),true);
	}

	public static void sendMsg(TBase t, String msg){
		if(t instanceof TDevice)
			//sendDevice((TDevice) t,msg);
			System.out.println("1111");
		else
			ioc.get(WebsocketRoom.class).sendMsg("wsroom:"+t.getClass().getSimpleName(), Constants.hmsSFormat.format(new Date()) + " " + msg,true);
	}

	/**
	 * 发往通讯线程
	 */
	public static void sendCommu(TCommunication commu, String msg){
		ioc.get(WebsocketRoom.class).sendMsg("COM-"+commu.getId(), Constants.hmsSFormat.format(new Date()) + " " + msg,true);
	}

	/**
	 * 发往设备
	 */
	public static void sendDevice(TDevice device, String msg){
		Long devId = device == null ? null : device.getId();
		ioc.get(WebsocketRoom.class).sendMsg("DEV-"+devId,Constants.hmsSFormat.format(new Date()) + " " +msg,true);
	}
	
}
