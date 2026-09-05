package org.aiot.model.table;

import org.aiot.lang.annotation.AoTbase;
import org.nutz.dao.entity.annotation.Table;

@Table
@AoTbase
public class TPoint extends TBase {
	private String name;
	/**
	 * 设备属性编码规则 dev-id-attr
	 */
	private String code;
	//private Long typeId;
	private Long placeId;

	private String image;
	private String target;//label,confidence,left,top,width,height,rotate

	private String unit;//单位
	private String alarmRule;//报警规则
	private boolean recOnEvery;//每次保存
	private boolean recOnTime;//周期保存
	private boolean recOnState;//状态变化保存
	private Double recOnValue;//数值变化保存

	//left,top,width,height,rotate
	private String shape; //形状，用于显示、描述

	public TPoint() {
	}

	public TPoint(String code, String image, String target) {
		this.code = code;
		this.image = image;
		this.target = target;
	}

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

	public Long getPlaceId() {
		return placeId;
	}

	public void setPlaceId(Long placeId) {
		this.placeId = placeId;
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

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}

	public String getShape() {
		return shape;
	}

	public void setShape(String shape) {
		this.shape = shape;
	}
}
