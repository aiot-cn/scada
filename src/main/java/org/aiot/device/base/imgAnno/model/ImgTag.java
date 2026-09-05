
package org.aiot.device.base.imgAnno.model;

import org.aiot.model.table.TBaseSeq;
import org.nutz.dao.entity.annotation.Table;

@Table
public class ImgTag extends TBaseSeq {

	private Long pid; //项目ID
	private String name;
	private String code;
	private String color;

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

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	@Override
	public boolean equals(Object o){
		if(o instanceof ImgTag){
			ImgTag t2 = (ImgTag) o;
			return (this.name + this.color).equals(t2.getName() + t2.getColor());
		}
		return false;
	}
}