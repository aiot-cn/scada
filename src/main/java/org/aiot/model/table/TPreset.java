package org.aiot.model.table;

import org.aiot.lang.annotation.AoTbase;
import org.nutz.dao.entity.annotation.Table;


@Table
@AoTbase("预置位")
public class TPreset extends TBase{
	private  Long deviceId;
	private String name;
	private	String code;

	private Float  pan;//P参数（水平参数），精确到小数点后3位，范围：0 – 36.000
	private Float  tilt;//T参数（垂直参数），精确到小数点后3位，范围：-90.000 – 270.000
	private Float  zoom;//Z参数（变倍参数），精确到小数点后3位，范围：0-100000
	private Integer  focus;//聚焦参数，聚焦范围：归一化0-100000

	public TPreset(){

	}

	public TPreset(Float pan, Float tilt, Float zoom, Integer focus){
		this.pan = pan;
		this.tilt = tilt;
		this.zoom = zoom;
		this.focus = focus;
	}

	public Long getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(Long deviceId) {
		this.deviceId = deviceId;
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

	public Float getPan() {
		return pan;
	}

	public void setPan(Float pan) {
		this.pan = pan;
	}

	public Float getTilt() {
		return tilt;
	}

	public void setTilt(Float tilt) {
		this.tilt = tilt;
	}

	public Float getZoom() {
		return zoom;
	}

	public void setZoom(Float zoom) {
		this.zoom = zoom;
	}

	public Integer getFocus() {
		return focus;
	}

	public void setFocus(Integer focus) {
		this.focus = focus;
	}

	@Override
	public String toString() {
		return "P:"+pan+" T:"+tilt+" Z:"+zoom+" F:"+focus;
	}

	@Override
	public boolean equals(Object obj){
		if(obj instanceof TPreset){
			TPreset t = (TPreset) obj;
			return t.pan.equals(pan) && t.tilt.equals(tilt) && t.zoom.equals(zoom) && t.focus.equals(focus);
		}
		return false;
	}
}