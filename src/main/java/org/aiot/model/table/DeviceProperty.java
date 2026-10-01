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

	private String name;
   	private String code;
	//modbus 104为hex
	private String address;
	/**
	 * 二次计算脚本(js)，原始值用@符号代替
	 * 如 @/100
	 */
	private String calcScript;

	//0数值（遥测） 1状态(遥信) 2开关（遥控）
	private Integer type;

	private String unit;
	private String remark;

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

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getCalcScript() {
		return calcScript;
	}

	public void setCalcScript(String calcScript) {
		this.calcScript = calcScript;
	}
}