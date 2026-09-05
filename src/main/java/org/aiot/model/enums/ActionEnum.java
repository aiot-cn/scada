package org.aiot.model.enums;

/**
 * 行为动作
 * @author TAOJIN
 *
 */
public enum ActionEnum {
	SYS_SET("系统设置"),
	DEV_SET("设备设置"),
	DEV_ADD("设备新增"),
	DEV_EDIT("设备修改"),
	DEV_DEL("设备删除"),
	PRESET_SET("预置点设置"),
	ACTION_SET("联动设置"),
	ACTION_ADD("联动新增"),
	ACTION_EDIT("联动修改"),
	ACTION_DEL("联动删除"),
;
	
	private String name;
	
	private ActionEnum(String name){
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	
    
}
