package org.aiot.model.table;

import org.aiot.lang.annotation.AoTbase;
import org.nutz.dao.entity.annotation.Table;
import org.nutz.lang.Strings;


@Table
@AoTbase
public class TWorkflow extends TText{
	private String name;//名称
	private String code;
	private String img;

	private String args;//参数
	private String returnType;

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

	public String getImg() {
		return img;
	}

	public void setImg(String img) {
		this.img = Strings.sBlank(img,null);
	}

	public String getArgs() {
		return args;
	}

	public void setArgs(String args) {
		this.args = args;
	}

	public String getReturnType() {
		return returnType;
	}

	public void setReturnType(String returnType) {
		this.returnType = returnType;
	}

}