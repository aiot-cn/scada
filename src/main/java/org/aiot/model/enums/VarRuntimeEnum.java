package org.aiot.model.enums;

import org.nutz.castor.Castors;
import org.nutz.lang.Lang;

/**
 * 运行时参数
 * @author dtj
 *
 */
public enum VarRuntimeEnum {
	dbUrl("数据源","aiot.db"),
	context("环境变量", Lang.context()),
	debugTime("调试截止时间",0L)

	;

	private String name;
	private Object value;//默认值


	VarRuntimeEnum(String name, Object value){
		this.name = name;
		this.value = value;
	}

	public String getName() {
		return name;
	}

	public <T> T val(){
		return (T)value;
	}

	public Object val(Object value){
		if(this == debugTime){
			this.value = Castors.me().castTo(value,long.class);
		}else{
			this.value = value;
		}
		return this.value;
	}

}
