package org.aiot.util;

import org.nutz.castor.Castors;
import org.nutz.lang.Strings;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 叫StringUtil的太多了
 */
public class StrUtil {

	/**
	 * 获取正则匹配的字符串
	 */
	public static String getPatternStr(String text,String regex,int group){
		Matcher matcher = Pattern.compile(regex).matcher(text);
		if (matcher.find())
			return matcher.group(group);
		return null;
	}

	public static String replace(String str, String regex, Function<String,String> callBack){
		if(Strings.isBlank(str))
			return null;
		Matcher m = Pattern.compile(regex).matcher(str);
		StringBuffer sb = new StringBuffer();
		while (m.find()) {
			m.appendReplacement(sb, callBack.apply(m.group()));
		}
		m.appendTail(sb);
		return sb.toString();
	}

	public static <T> List<T> String2List(String str, String separator, Class<T> type){
		List<T> list = new ArrayList<>();
		if(Strings.isNotBlank(str)){
			String[] a1 = str.split(separator);
			for (String s : a1) {
				list.add(Castors.me().castTo(s, type));
			}
		}
		return  list;
	}
}
