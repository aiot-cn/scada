package org.aiot.model.table;

import org.aiot.lang.annotation.AoTbase;
import org.nutz.dao.entity.annotation.Table;

@Table
@AoTbase
public class TPoint extends TPointType {
	/**
	 * 点位编码规则 dev-{deviceId}-{属性code}
	 * 默认设备id为负
	 */
	private String code;
	/**
	 * 地址，用于modbus、iec104 等上传
	 */
	private String address;

	private Long typeId;//暂无用

	private Long areaId;//sysDict

	private String image;
	private String target;//label,confidence,left,top,width,height,rotate

	/**
	 * 形状，用于显示、描述 比如原始框
	 * left,top,width,height,rotate
	 */
	private String shape;

	public TPoint() {
	}

	public TPoint(String code, String image, String target) {
		this.code = code;
		this.image = image;
		this.target = target;
	}

	public Long getDeviceId(){
		if(code.startsWith("dev-")) {
			try {
				return Long.parseLong(code.substring(4, code.indexOf("-")));
			} catch (Exception e) {
				return null;
			}
		}
		return null;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public Long getTypeId() {
		return typeId;
	}

	public void setTypeId(Long typeId) {
		this.typeId = typeId;
	}

	public Long getAreaId() {
		return areaId;
	}

	public void setAreaId(Long areaId) {
		this.areaId = areaId;
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

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}
}
