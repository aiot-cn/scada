package org.aiot.model.table;


import org.aiot.lang.annotation.AoTbase;
import org.aiot.model.enums.ScriptTypeEnum;
import org.nutz.dao.entity.annotation.Table;

@Table
@AoTbase
public class SysScript extends TBase {

	private ScriptTypeEnum type;
	private String name;
	private String code;//方法名
	private String function;//内容
	private String args;//参数


	public ScriptTypeEnum getType() {
		return type;
	}

	public void setType(ScriptTypeEnum type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getFunction() {
		return function;
	}

	public void setFunction(String function) {
		this.function = function;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getArgs() {
		return args;
	}

	public void setArgs(String args) {
		this.args = args;
	}
}