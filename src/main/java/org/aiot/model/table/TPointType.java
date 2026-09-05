package org.aiot.model.table;

import org.aiot.lang.annotation.AoTbase;
import org.nutz.dao.entity.annotation.Table;

@Table
@AoTbase
public class TPointType extends TBase {

	private String name;
	private String unit;
	private String alarmRule;

	private Boolean recOnEvery;//每次保存
	private Boolean recOnTime;//定时保存
	private Boolean recOnState;//状态变化保存
	private Double recOnValue;//数值变化保存

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public String getAlarmRule() {
		return alarmRule;
	}

	public void setAlarmRule(String alarmRule) {
		this.alarmRule = alarmRule;
	}

	public Boolean getRecOnEvery() {
		return recOnEvery;
	}

	public void setRecOnEvery(Boolean recOnEvery) {
		this.recOnEvery = recOnEvery;
	}

	public Boolean getRecOnTime() {
		return recOnTime;
	}

	public void setRecOnTime(Boolean recOnTime) {
		this.recOnTime = recOnTime;
	}

	public Boolean getRecOnState() {
		return recOnState;
	}

	public void setRecOnState(Boolean recOnState) {
		this.recOnState = recOnState;
	}

	public Double getRecOnValue() {
		return recOnValue;
	}

	public void setRecOnValue(Double recOnValue) {
		this.recOnValue = recOnValue;
	}
}
