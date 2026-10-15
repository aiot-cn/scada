package org.aiot.model.enums;

import org.aiot.main.Constants;
import org.nutz.lang.Streams;

import java.io.FileOutputStream;
import java.io.Writer;

import static org.aiot.main.Constants.homeProp;

/**
 * 配置文件 home/aiot.properties
 */
public enum PropHomeEnum {
	license("授权", ""),//必须按文件授权，db中会导致数据库到另一台不能用
	DB_URL("主数据库","aiot.db"),
	RAM_PATH("缓存路径",null)
	;

	private String name;
	private String value;//默认值

	PropHomeEnum(String name, String value){
		this.name = name;
		this.value = value;
	}

	public String getName() {
		return name;
	}

	public String val() {
		return homeProp.getProperty(this.name(),this.value);
	}

	public void val(String value) {
		try {
			//还是原生的 Properties 好用，只是 =# 等字符会加斜杠转义符
			homeProp.put(name(),value);
			Writer writer = Streams.utf8w(new FileOutputStream(Constants.homePropFile));
			homeProp.store(writer, "aiot");
			Streams.safeClose(writer);
            /*MultiLineProperties prop = new MultiLineProperties(Streams.utf8r(new FileInputStream(Constants.propPath)));
            prop.put(key,value);
            prop.print(Streams.utf8w(new FileOutputStream(Constants.propPath)));*/

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
