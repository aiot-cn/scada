package org.aiot.main;

import org.aiot.model.enums.VarRuntimeEnum;
import org.aiot.model.project.MethodBean;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.lang.segment.CharSegment;
import org.nutz.lang.segment.Segment;
import org.nutz.lang.util.Context;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * 全局运行时状态
 */
public class Constants {
	public static Ioc ioc; //推荐此做法 将Ioc容器放到一个public的静态属性
	public static ScriptEngineManager scriptManager = new ScriptEngineManager();
	public static ScriptEngine jse = new ScriptEngineManager().getEngineByName("nashorn");//脚本引擎管理共支持三种查找脚本引擎的方式，分别通过名称、文件扩展名和MIME类型来完成。

	public static String HOME_PATH;
	public static File homePropFile;
	public static PropertiesProxy prop;
	public static Properties homeProp = new Properties();

	public static boolean isAuthorized = false;//是否已授权

	public static Map<String, List<MethodBean>> methodMap = new HashMap<>();

	public static int diskCount;

	public static String format(String str){
		Segment seg = new CharSegment(str);
		Context context = VarRuntimeEnum.context.val();
		return seg.render(context).toString();
	}
}
