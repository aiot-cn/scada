package org.aiot.model.enums;

/**
 * 设备职能
 * @author TAOJIN
 *
 */
public enum DeviceRoleEnum {
	SENSOR("传感器"),
	CONTROLLER("控制器"),
	VIDEO("视频"),
	AI_MODEL("AI模型"),
	APP("应用"),
	NOTIFY("通知")
	;


	private String name;

	DeviceRoleEnum(String name){
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
