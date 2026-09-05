package org.aiot.infc;

public interface SResProtocol{
	String getTitle();
	String getContent();
	void saveContent(String content);
	byte[] getBytes();
	//页面参数
	String getParam();
}
