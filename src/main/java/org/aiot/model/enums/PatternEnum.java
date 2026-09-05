package org.aiot.model.enums;

import java.util.regex.Pattern;

/**
 * 正则表达式
 * @author TAOJIN
 *
 */
public enum PatternEnum {
	E_MAIL("^\\s*\\w+(?:\\.{0,1}[\\w-]+)*@[a-zA-Z0-9]+(?:[-.][a-zA-Z0-9]+)*\\.[a-zA-Z]+\\s*$"),
	yyyy_MM_ddTHH_mm("\\d{4}-\\d{2}-\\d{2}[T|\\s]\\d{2}:\\d{2}(:\\d{2})*"),
	yyyy_MM_dd("\\d{4}-\\d{2}-\\d{2}"),
	P_TIME("^((\\d{2,4})([/\\\\-])(\\d{1,2})([/\\\\-])(\\d{1,2}))"
            + "(([ T])?"
            + "(\\d{1,2})(:)(\\d{1,2})((:)(\\d{1,2}))?"
            + "(([.])"
            + "(\\d{1,}))?)?"
            + "(([+-])(\\d{1,2})(:\\d{1,2})?)?"
            + "$"),
	HEX("([0-9A-Fa-f]{2})+"),
	NUMBER("^(\\-|\\+)?\\d+(\\.\\d+)?$")
;
	
	private String pattern;
	
	private PatternEnum(String pattern){
		this.pattern = pattern;
	}
	
	public boolean matches(String content) {
		if(content == null)
			return false;
		return Pattern.matches(pattern, content);
	}

	public String getPattern() {
		return pattern;
	}

	public void setPattern(String pattern) {
		this.pattern = pattern;
	}
	
	
 
}
