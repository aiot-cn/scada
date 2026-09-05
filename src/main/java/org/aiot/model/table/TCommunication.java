package org.aiot.model.table;

import org.aiot.lang.annotation.AoTbase;
import org.nutz.dao.entity.annotation.Table;

/**
 * 通讯
 */
@Table
@AoTbase
public class TCommunication extends TBase{

	private String name;
	private boolean listen;	 //监听
	private String klass;	//通讯实现类
	private String uri;		//统一资源标识
	private boolean logRecord;//记录日志
	private boolean hex;

	public TCommunication(){

	}

	public TCommunication(int id,Class<?> klass,String uri,boolean listen,boolean hex){
		setId((long) id);
		this.klass = klass.getName();
		this.uri = uri;
		this.listen = listen;
		this.hex = hex;
	}
	

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getKlass() {
		return klass;
	}
	public void setKlass(String klass) {
		this.klass = klass;
	}
	public String getUri() {
		return uri;
	}
	public void setUri(String uri) {
		this.uri = uri;
	}

	public boolean isLogRecord() {
		return logRecord;
	}

	public void setLogRecord(boolean logRecord) {
		this.logRecord = logRecord;
	}

	public boolean isListen() {
		return listen;
	}

	public void setListen(boolean listen) {
		this.listen = listen;
	}

	public boolean isHex() {
		return hex;
	}

	public void setHex(boolean hex) {
		this.hex = hex;
	}
}
