package org.aiot.model.project;

import org.nutz.lang.Strings;

public class ArgBean {

	private String code;
	private Class<?> type;//参数类型

	private String name;
	private String url;

	private String input;
	private String select;//0:否,1:是
	private String selectMethod; //通过类方法获取
	private String placeholder;

	public ArgBean(String code, Class<?> type){
		this.code = code;
		this.type = type;
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

	public Class<?> getType() {
		return type;
	}

	public void setType(Class<?> type) {
		this.type = type;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getSelect() {
		return select;
	}

	public void setSelect(String select) {
		if(Strings.endsWithChar(select,')')){
			this.selectMethod = select.substring(0,select.indexOf('('));
		}else{
			this.select = select;
		}

	}

	public String getPlaceholder() {
		return placeholder;
	}

	public void setPlaceholder(String placeholder) {
		this.placeholder = placeholder;
	}

	public String getInput() {
		return input;
	}

	public void setInput(String input) {
		this.input = input;
	}

	public String getSelectMethod() {
		return selectMethod;
	}

	public void setSelectMethod(String selectMethod) {
		this.selectMethod = selectMethod;
	}
}
