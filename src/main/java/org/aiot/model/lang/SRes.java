package org.aiot.model.lang;

import org.aiot.infc.SResProtocol;
import org.aiot.util.UrlParser;
import org.nutz.lang.Lang;
import org.nutz.lang.Mirror;
import org.nutz.lang.Strings;

public class SRes extends UrlParser{

	private SResProtocol protocol;

	public SRes(SResProtocol protocol){
		super(null);
		this.protocol = protocol;
	}

	public SRes(String url){
		super(url);
		try {
			Class<?> clazz = Lang.loadClass("org.aiot.handler.protocol."+getProtocolName()+"Protocol");
			this.protocol = (SResProtocol) Mirror.me(clazz).born(getPathName());
		} catch (ClassNotFoundException e) {
			throw Lang.makeThrow("不支持协议："+getProtocolName());
		}
	}

	public String getTitle() {
		return protocol.getTitle();
	}

	public String getContent() {
		if(protocol.getContent() == null)
			return "";

		return protocol.getContent()
				.replaceAll("&lt;","&amp;lt;")
				.replaceAll("&gt;","&amp;gt;")
				.replaceAll("&quot;","&amp;quot;");
	}

	public byte[] getBytes(){
		return protocol.getBytes();
	}

	public void saveContent(String content){
		protocol.saveContent(content);
	}


	public SResProtocol getProtocol() {
		return protocol;
	}

	public void setProtocol(SResProtocol protocol) {
		this.protocol = protocol;
	}
}
