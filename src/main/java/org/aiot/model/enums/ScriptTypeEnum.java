package org.aiot.model.enums;

/**
 * 脚本类型
 * @author TAOJIN
 *
 */
public enum ScriptTypeEnum {

	normal("通用",""),
	command("指令","content,device"),
	numeric("数值","val,analysis,data,bd,command"),
	crc("校验","content,command"),
	reflect("映射","reflect,data,bd,val"),
	observer("订阅","arg,bd"),
	url("URL","req,resp,url"),
	action("动作链",""),
	aop("AOP","arg,bd,re")
	;


	private String name;
	private String args;

	private ScriptTypeEnum(String name, String args){
		this.name = name;
		this.args = args;

	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getArgs() {
		return args;
	}

	public void setArgs(String args) {
		this.args = args;
	}
}
