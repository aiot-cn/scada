package org.aiot.model.table;

import org.aiot.lang.annotation.AoTbase;
import org.nutz.dao.entity.annotation.Table;

@Table
@AoTbase
public class TPointType extends TBase {

	private String name;
	private String unit;//单位
	/**
	 * 报警规则，换行分隔、从重到轻：第1行=报警(2)、第2行=预警(1)，未命中=正常(0) <br>
	 * 每行为裸比较的组合，如 <5||>10 <br>
	 * 注意：值在阈值附近波动会反复切换状态，必要时增加"连续 N 次命中才切换"的防抖。<br>
	 * 两种高级写法 <br>
	 *   <li>值打头的完整 JS 表达式  `*2>10` → `6*2>10` → true</li>
	 *   <li>三元式，直接返回状态值   `>10?2:>5?1:0` → 1</li>
	 */
	private String alarmRule;
	private boolean recOnEvery;//每次保存
	private boolean recOnTime;//周期保存
	private boolean recOnState;//状态变化保存
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

	public boolean isRecOnEvery() {
		return recOnEvery;
	}

	public void setRecOnEvery(boolean recOnEvery) {
		this.recOnEvery = recOnEvery;
	}

	public boolean isRecOnTime() {
		return recOnTime;
	}

	public void setRecOnTime(boolean recOnTime) {
		this.recOnTime = recOnTime;
	}

	public boolean isRecOnState() {
		return recOnState;
	}

	public void setRecOnState(boolean recOnState) {
		this.recOnState = recOnState;
	}

	public Double getRecOnValue() {
		return recOnValue;
	}

	public void setRecOnValue(Double recOnValue) {
		this.recOnValue = recOnValue;
	}
}
