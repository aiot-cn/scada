package org.aiot.model.table;

import org.aiot.lang.annotation.AoTbase;
import org.aiot.model.enums.CompareEnum;
import org.aiot.model.enums.LogicEnum;
import org.nutz.dao.entity.annotation.Table;

@Table
@AoTbase
public class DeviceAction extends TBaseSeq {

	//分组 0-- 1 分组开始 ┌ 2分组中├ 3 分组结束 └ 4 ┌┌ 分组加括号
	private int gro;
    private LogicEnum ao;//关联
	@AoTbase(from = DeviceType.class,field = "code")
    private String deviceType;
	@AoTbase(from = TDevice.class)
    private Long deviceId;
    private String analysis;//属性
    private CompareEnum compare;//比较
    private Float value;
    private Float hysteresis;//回差
    private Integer times;//连续次数

    private int alarm;//报警 0无 1自动 2预警 3报警
	private int trigger;//联动 0到符合时 1到不符时 2状态变化 3每次符合 4每次不符 5每次
	private Long pid;//上层关联表ID，如 风机、警灯
	
	
	public LogicEnum getAo() {
		return ao;
	}
	public void setAo(LogicEnum ao) {
		this.ao = ao;
	}
	public String getAnalysis() {
		return analysis;
	}
	public void setAnalysis(String analysis) {
		this.analysis = analysis;
	}
	
	public CompareEnum getCompare() {
		return compare;
	}
	public void setCompare(CompareEnum compare) {
		this.compare = compare;
	}
	public Float getValue() {
		return value;
	}
	public void setValue(Float value) {
		this.value = value;
	}
	
	/**
	 * 回差
	 * @return
	 */
	public Float getHysteresis() {
		return hysteresis;
	}
	public void setHysteresis(Float hysteresis) {
		this.hysteresis = hysteresis;
	}
	public Integer getTimes() {
		return times;
	}
	public void setTimes(Integer times) {
		this.times = times;
	}


	public int getAlarm() {
		return alarm;
	}

	public void setAlarm(int alarm) {
		this.alarm = alarm;
	}

	public String getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(String deviceType) {
		this.deviceType = deviceType;
	}

	public Long getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(Long deviceId) {
		this.deviceId = deviceId;
	}

	/**
	 * 分组 0独立-- <br>
	 * 1分组开始	┌ <br>
	 * 2分组中	├ <br>
	 * 3分组结束	└ <br>
	 * 4分组加括号┌┌
	 */
	public int getGro() {
		return gro;
	}

	public void setGro(int gro) {
		this.gro = gro;
	}

	public Long getPid() {
		return pid;
	}

	public void setPid(Long pid) {
		this.pid = pid;
	}

	public int getTrigger() {
		return trigger;
	}

	public void setTrigger(int trigger) {
		this.trigger = trigger;
	}
}