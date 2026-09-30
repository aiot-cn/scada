package org.aiot.model.enums;

import org.aiot.model.table.*;
import org.aiot.model.table.user.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 系统权限行为动作
 */
public enum RoleActionEnum {
	FILE_UPLOAD("文件上传",new Class[]{TFile.class}),
	FILE_DELETE("文件删除",new Class[]{TFile.class}),

	SYS_SET("系统设置",new Class[]{
			SqlCode.class, SqlCondition.class, SysDataSource.class
	}),
	USER_ROLE("用户角色",new Class[]{
			SysUser.class, SysRole.class, SysMenu.class, MRoleMenuAction.class
	}),
	WORK_SCRIPT("工作脚本",new Class[]{
			SysScript.class,TWorkflow.class
	}),
	LOG_RECORD("日志记录",new Class[]{
			TLog.class,TRecord.class
	}),
	DEVICE_MANAGE("设备管理",new Class[]{
			DeviceType.class,TDevice.class,
			DeviceAnalysis.class, DeviceCommand.class,DeviceProperty.class
	}),
	BASIC("基础权限",new Class[]{
			SysDict.class,SysTrigger.class,SysUrl.class,
			TParam.class,
			TCommunication.class,TAiModel.class,TVideoSource.class,
			TPoint.class,TPointType.class,
			TDoc.class,TText.class,TTemplate.class,
			TCard.class, TPreset.class
	})
;
	
	private String name;
	private Class<?>[] tables;

	RoleActionEnum(String name,Class<?>[] tables){
		this.name = name;
		this.tables = tables;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Class<?>[] getTables() {
		return tables;
	}

	public void setTables(Class<?>[] tables) {
		this.tables = tables;
	}

	//根据表获取对应的权限动作 一个表可能关联多个动作(如TFile同时关联上传和删除)
	public static List<RoleActionEnum> getByTable(Class<?> table){
		List<RoleActionEnum> list = new ArrayList<>();
		for (RoleActionEnum action : values()) {
			if (Arrays.asList(action.tables).contains(table))
				list.add(action);
		}
		return list;
	}
}
