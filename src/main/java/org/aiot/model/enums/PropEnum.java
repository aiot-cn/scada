package org.aiot.model.enums;

import org.aiot.main.Constants;

/**
 * system.properties 等
 */
public enum PropEnum {
	AIOT_HOME("资源目录",""),
	DES_KEY("3des密钥","AIOTAIOTAIOTAIOTAIOTAIOT"),//3des必须是24个密钥字符串
	;

	private String name;
	private String value;//默认值

	PropEnum(String name, String value){
		this.name = name;
		this.value = value;
	}

	public String getName() {
		return name;
	}

	public String val() {
		return Constants.prop.get(this.name(),this.value);
	}

}
