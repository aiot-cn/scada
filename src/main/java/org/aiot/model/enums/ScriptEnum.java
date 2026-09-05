package org.aiot.model.enums;

import org.aiot.util.SysUtil;

/**
 * 脚本
 * @author TAOJIN
 *
 */
public enum ScriptEnum {

	initCall("初始化"),
	observer("观察者")
	;


	private String name;

	private ScriptEnum(String name){
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void exec(Object... arg){
		SysUtil.scriptByName(name(),arg);
	}

    
}
