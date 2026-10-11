package org.aiot.model.enums;

/**
 * 日志分类
 */
public enum LogCategoryEnum {
	SYSTEM("系统"),
	LOGIN("登录"),
	CONFIG("配置"),
	DEVICE("设备")
	;

	private String name;

	LogCategoryEnum(String name){
		this.name = name;
	}

	public String getName() {
		return name;
	}

}
