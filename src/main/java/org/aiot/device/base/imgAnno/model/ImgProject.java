package org.aiot.device.base.imgAnno.model;

import org.aiot.model.table.TBase;
import org.nutz.dao.entity.annotation.Table;


@Table
public class ImgProject extends TBase {

	private String name;
	private String path;
	private String remark;

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}
}