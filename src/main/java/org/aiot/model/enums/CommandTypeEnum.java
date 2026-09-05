package org.aiot.model.enums;

/**
 * 指令类型
 *
 */
public enum CommandTypeEnum {
	comRx("接收"),
	comSet("设置")
	;
	
	private String text;
	CommandTypeEnum(String text){
		this.text = text;
	}
	
	
	public String getText() {
		return text;
	}


	public void setText(String text) {
		this.text = text;
	}

}
