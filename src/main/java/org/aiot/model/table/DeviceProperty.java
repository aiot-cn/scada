package org.aiot.model.table;

import org.aiot.lang.annotation.AoTbase;
import org.nutz.dao.entity.annotation.Table;

@Table
@AoTbase
public class DeviceProperty extends TBaseSeq{

	@AoTbase(from = DeviceType.class,field = "code")
	private String deviceType;

	@AoTbase(from = TDevice.class)
	private Long deviceId;

	private String devField;

	//用于默认的报警、保存
	//private Long pointTypeId;

	private String name;
   	private String code;

	//报警规则
	//private String alarmRule;

	//0数值（遥测） 1状态(遥信) 2开关（遥控）
	private Integer type;

	//0环境 1消防 2安防
	//private Integer classify;


	private String unit;
	private String remark;

	/**
	 * 0 周期保存
	 */
	//private boolean recOnTime;
	/**
	 * 是否状态变化保存
	 */
	//private boolean recOnState;

	/**
	 * 是否每次保存
	 */
	//private boolean recOnEvery;
	/**
	 * 数值变化保存
	 */
	//private Float recOnValue;

	/**
	 * 数值变化触发
	 */
	//private Float notifyOnValue;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	/**
	 * 0遥测 1遥信 2遥控
	 */
	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public String getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(String deviceType) {
		this.deviceType = deviceType;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getDevField() {
		return devField;
	}

	public void setDevField(String devField) {
		this.devField = devField;
	}


	public Long getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(Long deviceId) {
		this.deviceId = deviceId;
	}

}