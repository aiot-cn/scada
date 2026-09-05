package org.aiot.communication;

import org.aiot.lang.Command;
import org.aiot.model.enums.CdataEnum;
import org.aiot.model.table.TCommunication;
import org.aiot.service.WebsocketRoom;
import org.aiot.util.CalcUtil;
import org.nutz.lang.Strings;
import org.nutz.log.Logs;


import java.util.Observable;


public abstract class CommunicationInfc extends Observable {

	protected TCommunication commun;

	/**
	 * 打开
	 */
	public abstract  boolean open();

	/**
	 * 关闭
	 */
	public abstract  boolean close();


	/**
	 * 发送
	 */
	public abstract byte[] send(Command command);

	/**
	 * 接收
	 */
	public void receive(){};

	/**
	 * 自检
	 */
	public boolean selfTest() {return true;};


	public void sendSocket(CdataEnum type, String m, Object... f){
		if(Strings.isBlank(m))
			return;
		String msg = f.length > 0 ? String.format(m,f) : m;
		int length = msg.length();
		if(length > 300){
			msg = msg.substring(0,150) + " ..."+ (length-300) +"... " + msg.substring(length-150);
		}
		if(commun.isLogRecord())
			Logs.get().info(commun.getName() + " " + msg);
		WebsocketRoom.sendCommu(commun, "[U:"+commun.getId()+"] "+type.getLabel()+msg);
	}

	public String sendListen(byte[] bytes,String topic,String s){
		if(s == null)
			s =  commun.isHex() ? CalcUtil.byteToHex(bytes) : new String(bytes);
		sendSocket(CdataEnum.Rx,Strings.sBlank(topic,"") + " " + ( commun.isHex() ?  s.replaceAll("(.{2})", "$1 ") : s));
		return s;
	}

	public TCommunication getCommun() {
		return commun;
	}
	public void setCommun(TCommunication commun) {
		this.commun = commun;
	}
}
