package org.aiot.model.enums;

import org.aiot.main.Constants;
import org.aiot.util.FileUtil;

/**
 * 配置文件
 *
 */
public enum PropEnum {
	DB_URL("数据库","aiot.db"),
	license("许可证",""),
	RAM_PATH("缓存路径",null)
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
		return Constants.propStation.getProperty(this.name(),this.value);
	}

	public void val(String value) {
		FileUtil.setStationProp(this.name(),value);
	}

}
