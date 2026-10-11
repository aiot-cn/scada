package org.aiot.model.enums;

/**
 * 日志事件类型
 */
public enum LogEventEnum {

	SYSTEM_BOOTING("系统启动", LogCategoryEnum.SYSTEM),
	LOGIN_SUCCESS("登录成功", LogCategoryEnum.LOGIN),
	LOGIN_FAILURE("登录失败", LogCategoryEnum.LOGIN),
	DEVICE_OPERATION("设备操作", LogCategoryEnum.DEVICE),
	;

	private String name;
	private LogCategoryEnum category;

	LogEventEnum(String name, LogCategoryEnum category){
		this.name = name;
		this.category = category;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public LogCategoryEnum getCategory() {
		return category;
	}

	public void setCategory(LogCategoryEnum category) {
		this.category = category;
	}
}
