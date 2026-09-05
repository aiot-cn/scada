package org.aiot.model.project;

import org.aiot.model.enums.AstEnum;

import java.util.ArrayList;
import java.util.List;

public class MethodBean {
	private String code;
	private String name;
	private AstEnum type;//类型
	private List<ArgBean> arg;
	private Class<?> returnType;

	private boolean isStatic;//静态的
	private boolean deprecated;//弃用的

	public MethodBean(String code, String name, Class<?> returnType){
		this.code = code;
		this.name = name;
		this.returnType = returnType;
		this.arg = new ArrayList<>();
		this.type = AstEnum.auto;
	}

	public void addArg(ArgBean argBean){
		arg.add(argBean);
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public AstEnum getType() {
		return type;
	}

	public void setType(AstEnum type) {
		this.type = type;
	}

	public List<ArgBean> getArg() {
		return arg;
	}

	public void setArg(List<ArgBean> arg) {
		this.arg = arg;
	}

	public Class<?> getReturnType() {
		return returnType;
	}

	public void setReturnType(Class<?> returnType) {
		this.returnType = returnType;
	}

	public boolean isDeprecated() {
		return deprecated;
	}

	public void setDeprecated(boolean deprecated) {
		this.deprecated = deprecated;
	}

	public boolean isStatic() {
		return isStatic;
	}

	public void setStatic(boolean aStatic) {
		isStatic = aStatic;
	}
}
