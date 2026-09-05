package org.aiot.model.enums;

import org.aiot.main.Constants;
import org.aiot.model.table.TParam;
import org.aiot.service.ConfigService;
import org.nutz.lang.Lang;
import org.nutz.lang.Strings;
/**
 * 系统参数
 * @author dtj
 *
 */
public enum ConfigEnum {
	domain(			"域名","",		"input",""),
	passwordType(	"密码类型","md5","select","md5:MD5,sha1:SHA1,sha256:SHA256"),
	debug(			"调试","0",		"Boolean",""),
	favicon(		"图标","",		"input",""),
	textCss(		"自定义样式","",	"text-css",""),
	context(		"环境变量","",	"text-properties","")
	;
	
	private String name;
	private String value;//默认值
	private String type;
	private String select;
	
	ConfigEnum(String name,String value,String type,String select){
		this.name = name;
		this.value = value;
		this.type = type;
		this.select = select;
	}

	public String getName() {
		return name;
	}

	public String getValue() {
		String val = this.value;
		TParam conf = Constants.ioc.get(ConfigService.class).getConfigParam(0,null,name());
		if(conf != null && Strings.isNotBlank(conf.getValue())){
			val = conf.getValue();
		}
		return val.trim();
	}

	public boolean getBoolean() {
		return Lang.parseBoolean(getValue());
	}

	public void setValue(String value) {
		ConfigService cs = Constants.ioc.get(ConfigService.class);
		TParam p = new TParam();
		p.setType(0);
		p.setCode(name());
		p.setValue(value);
		cs.saveConfigParams(p);
	}

}
