package org.aiot.model.table;

import org.aiot.lang.annotation.AoTbase;
import org.aiot.model.enums.PhaseEnum;
import org.nutz.dao.entity.annotation.Table;


@Table
@AoTbase
public class SysTrigger extends TBase{
	private String name;//名称
	private String icon;

	/**
	 * -1 脚本
	 * -2 工作流
	 * -3 定时任务（触发执行，无拦截回调）
	 * -4 URL（触发执行，无拦截回调）
	 * -5 图像标签 （PointService）
	 */
	private Long deviceId;
	/**
	 * D- 设备数据
	 * M- 设备方法
	 */
	private String member;
	private PhaseEnum phase;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public Long getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(Long deviceId) {
		this.deviceId = deviceId;
	}

	public String getMember() {
		return member;
	}

	public void setMember(String member) {
		this.member = member;
	}

	public PhaseEnum getPhase() {
		return phase;
	}

	public void setPhase(PhaseEnum phase) {
		this.phase = phase;
	}
}