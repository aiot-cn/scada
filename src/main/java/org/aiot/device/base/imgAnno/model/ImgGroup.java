
package org.aiot.device.base.imgAnno.model;

import org.aiot.model.table.TBase;
import org.nutz.dao.entity.annotation.Table;

@Table
public class ImgGroup extends TBase {

	private Long pid;
	private String name;

	public Long getPid() {
		return pid;
	}

	public void setPid(Long pid) {
		this.pid = pid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}