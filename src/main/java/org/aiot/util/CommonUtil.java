package org.aiot.util;

import org.aiot.infc.device.DeviceInfc;
import org.aiot.lang.Cache;
import org.aiot.lang.CommonAction;
import org.aiot.lang.annotation.AoReflect;
import org.aiot.lang.workflow.Workflow;
import org.aiot.main.Constants;
import org.aiot.model.enums.AstEnum;
import org.aiot.model.project.ArgBean;
import org.aiot.model.project.MethodBean;
import org.aiot.service.PointService;
import org.nutz.castor.Castors;
import org.nutz.http.Http;
import org.nutz.http.Response;
import org.nutz.lang.Files;
import org.nutz.lang.Mirror;
import org.nutz.lang.Strings;
import org.nutz.lang.util.MethodParamNamesScaner;
import org.nutz.lang.util.NutMap;
import org.nutz.resource.Scans;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommonUtil {
	
	/**
	 * 获取系统任务列表<br>
	 * plugin.quartzJob 	定时任务<br>
	 * plugin.syservice 	系统服务<br>
	 * plugin.communication	通讯类型<br>
	 * plugin.device		设备类型
	 * @return
	 */
	public static List<NutMap> getAoReflect(String packageZ,Class<?> instance){
		List<NutMap> l = new ArrayList<>();
		for (Class<?> classZ : Scans.me().scanPackage(packageZ)){
			AoReflect ao = classZ.getAnnotation(AoReflect.class);
			//排除多线程生成的类
			if(ao != null && !classZ.getName().contains("$") && (instance == null || instance.isAssignableFrom(classZ))){
				l.add(aoClassStrMap(classZ));
			}
		}
		return l;
	}

	public static NutMap aoClassStrMap(Class<?> classZ){
		AoReflect ao = classZ.getAnnotation(AoReflect.class);
		String klass = classZ.getName();
		NutMap nm = new NutMap();
		nm.put("class", klass);
		nm.put("name", Strings.sBlank(ao.value(),classZ.getSimpleName()));

		String code = ao.code();
		if(Strings.isBlank(code))
			code = klass.substring(klass.lastIndexOf(".")+1);
		nm.put("code",code);
		return nm;
	}

	public static MethodBean methodDetail(Method met){

		/*boolean isAbs = Modifier.isAbstract(met.getDeclaringClass().getModifiers()) ;
		if(isAbs){
			return null;
		}*/
		List<String> pname = MethodParamNamesScaner.getParamNames(met);
		if(pname == null)
			return null;
		AoReflect ao = met.getAnnotation(AoReflect.class);
		MethodBean mb = new MethodBean(met.getName(),ao == null ? "" : ao.value(),met.getReturnType());
		mb.setType(ao == null ? AstEnum.auto : ao.type());
		mb.setDeprecated(met.getAnnotation(Deprecated.class) != null);
		mb.setStatic(Modifier.isStatic(met.getModifiers()));

		Parameter[] parameters = met.getParameters();
		for(int i=0;i< parameters.length;i++){
			Parameter p = parameters[i];
			ArgBean ab = new ArgBean(pname.get(i),p.getType());
			mb.getArg().add(ab);
			AoReflect ap = p.getAnnotation(AoReflect.class);
			if(ap == null)
				continue;
			ab.setName(ap.value());
			ab.setUrl(ap.url());
			ab.setSelect(ap.select());
			ab.setInput(ap.input());
			ab.setPlaceholder(ap.placeholder());
		}
		return mb;
	}

	public static NutMap aoToMap(AoReflect ao){
		NutMap nm = new NutMap();
		nm.put("type", ao.type());
		nm.put("name", ao.value());
		nm.put("code", ao.code());
		nm.put("required", ao.required());
		nm.put("placeholder", ao.placeholder());
		nm.put("select", ao.select());
		nm.put("input",ao.input());
		nm.put("url",ao.url());
		return nm;
	}
	/**
	 * 枚举转ListMap
	 * @param clazz
	 * @return
	 */
	public static List<Map<String, Object>> enumToListMap(Class<?> clazz){
		List<Map<String, Object>> resultList = new ArrayList<>();

		for(Object o : clazz.getEnumConstants()){
			Enum e = (Enum) o;
			Mirror<?> m = Mirror.me(o.getClass());
			Map<String, Object> map = new HashMap<>();
			map.put("code",e.name());
			for(Field field : m.getFields()){
				map.put(field.getName(), m.getValue(o,field.getName()));
			}
			resultList.add(map);
		}

		return resultList;
	}

	/**
	 * 将含有 AoReflect 注解的类转换为 map
	 * @param map
	 * @param o
	 */
	public static void aoFieldMap(NutMap map,Object o){
		Mirror<?> m = Mirror.me(o);
		for(Field f : m.getFields(AoReflect.class)){
			AoReflect as = f.getAnnotation(AoReflect.class);
			Object v = as.getter() ? m.getValue(o, f.getName()) : m.getValue(o,f);
			if(DeviceInfc.class.isAssignableFrom(f.getType()) || as.type() == AstEnum.device){
				NutMap nm = new NutMap();
				map.put(f.getName(), nm);
				if(v != null){
					aoFieldMap(nm,v);
				}
			}else{
				map.put(f.getName(), v);
			}

		}
	}
	public static NutMap aoFieldMap(Object o){
		NutMap map = new NutMap();
		if(o != null){
			aoFieldMap(map,o);
		}
		return map;
	}


	/**
	 * 将对象字段类型与值的类型一致的注入
	 * @param o
	 * @param v
	 */
	public static void aoFieldVal(Object o,Object v){
		if(o == null || v == null)
			return;
		Mirror<?> m = Mirror.me(o);
		for(Field f : m.getFields()){
			if(f.getType().isAssignableFrom(v.getClass()) && !f.getType().equals(Object.class)){
				m.setValue(o, f, v);
				break;
			}
		}
	}

	public static int[] timeDiff(Date d1,Date d2){
		int[] i = new int[5];
		if(d1 == null || d2 == null)
			return i;

		Calendar c1 = Calendar.getInstance();
		c1.setTime(d1);
		Calendar c2 = Calendar.getInstance();
		c2.setTime(d2);

		int minute1 = c1.get(Calendar.MINUTE);
		int minute2 = c2.get(Calendar.MINUTE);
		if(minute1 > minute2){
			i[4] = minute2 + 60 - minute1;
			c2.add(Calendar.HOUR_OF_DAY,-1);
		}else{
			i[4] = minute2 - minute1;
		}
		int hour1 = c1.get(Calendar.HOUR_OF_DAY);
		int hour2 = c2.get(Calendar.HOUR_OF_DAY);
		if(hour1 > hour2){
			i[3] = hour2 + 24 - hour1;
			c2.add(Calendar.DAY_OF_MONTH,-1);
		}else{
			i[3] = hour2 - hour1;
		}
		int day1 = c1.get(Calendar.DAY_OF_MONTH);
		int day2 = c2.get(Calendar.DAY_OF_MONTH);
		int[] days = new int[]{31,c2.get(Calendar.YEAR) % 4 == 0 ? 29 : 28,31,30,31,30,31,31,30,31,30,31};
		if(day1 > day2){
			c2.add(Calendar.MONTH,-1);
			i[2] = day2 + days[c2.get(Calendar.MONTH)] - day1;
		}else{
			i[2] = day2 - day1;
		}
		int month1 = c1.get(Calendar.MONTH);
		int month2 = c2.get(Calendar.MONTH);
		if(month1 > month2){
			c2.add(Calendar.YEAR,-1);
			i[1] = month2 + 12 - month1;
		}else{
			i[1] = month2 - month1;
		}
		i[0] = c2.get(Calendar.YEAR) - c1.get(Calendar.YEAR);

		//System.out.printf("%d年%d月%d日%d时%d分",i[0],i[1],i[2],i[3],i[4]);
		return i;
	}

	public static <T> T getNonNull(Object... o){
		for(Object v:o){
			if(v != null){
				if(v instanceof String){
					String s = (String) v;
					if(Strings.isNotBlank(s)){
						return (T)v;
					}
				}else{
					return (T)v;
				}
			}
		}
		return null;
	}

	public static Object getUri(String uri){
		if(uri.startsWith("/"))
			uri = uri.substring(1);

		if(uri.endsWith(".workflow"))
			return Workflow.getGlobal(uri);

		if(uri.endsWith(".cache"))
			return Cache.getFromUri(uri);

		if(uri.endsWith(".chain")){
			String[] s= uri.split("\\.");
			return CommonAction.getActionState(Long.parseLong(s[0])).getResult();
		}

		if(uri.endsWith(".point")){
			String[] s= uri.split("\\.");
			return Constants.ioc.get(PointService.class).getPointDataMap().get(Long.parseLong(s[0]));
		}

		return Castors.me().castTo(uri, File.class);
	}

	private static final Set<Class<?>> DISPLAYABLE_TYPES = new HashSet<>(Arrays.asList(
			Byte.class, Short.class, Integer.class, Long.class,
			Float.class, Double.class,Number.class,
			Character.class,String.class,
			Boolean.class,Void.class
	));

	public static boolean isBasicType(Object o){
		if(o == null)
			return false;
		return isBasicType(o.getClass());
	}
	public static boolean isBasicType(Class<?> clazz) {
		if (clazz == null) return false;
		return clazz.isPrimitive()
				|| clazz == Byte.class
				|| clazz == Short.class
				|| clazz == Integer.class
				|| clazz == Long.class
				|| clazz == Float.class
				|| clazz == Double.class
				|| clazz == Number.class
				|| clazz == Boolean.class
				|| clazz == Character.class
				|| clazz == String.class
				|| clazz == Void.class;
	}

	public static boolean isDirectlyDisplayable(Class<?> clazz) {
		// 检查基本类型
		if (clazz.isPrimitive()) {
			return true;
		}

		// 检查包装类和String
		if (DISPLAYABLE_TYPES.contains(clazz)) {
			return true;
		}

		// 检查数组类型（基本类型数组和String数组）
		if (clazz.isArray()) {
			Class<?> componentType = clazz.getComponentType();
			return componentType.isPrimitive() || componentType.equals(String.class);
		}

		return false;
	}
}
