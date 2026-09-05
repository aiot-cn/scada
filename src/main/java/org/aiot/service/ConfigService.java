package org.aiot.service;

import org.aiot.lang.NotifyEvent;
import org.aiot.lang.annotation.AoReflect;
import org.aiot.main.Constants;
import org.aiot.model.enums.*;
import org.aiot.model.table.*;
import org.aiot.util.SysUtil;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Lang;
import org.nutz.lang.Mirror;
import org.nutz.lang.Strings;
import org.nutz.lang.inject.Injecting;
import org.nutz.lang.util.Context;
import org.nutz.log.Log;
import org.nutz.log.Logs;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 数据库系统参数
 */
@IocBean(create="init")
public class ConfigService implements Observer {
	Log log = Logs.get();

	@Inject BaseService bs;

	private Map<String,String> dictType = new HashMap<>(); //系统扩展字典

	public void init() {
		bs.addObserver(this);

		//系统配置（目前 风格样式配置在这里方便加载）
		Map<String,String> config = Stream.of(ConfigEnum.values()).collect(Collectors.toMap(Enum::name, ConfigEnum::getValue));
		ServletEnum.config.val(config);

		PathEnum.initPath();
		initDict();
		initContext();
		bs.getTCache(SysScript.class).forEach(v->{
			try {
				initScript(v);
			}catch (Exception e){
				log.error("脚本"+v.getFunction()+"编译错误 "+e.getMessage());
			}
		});

	}

	public void initDict(){
		Map<String,List<SysDict>> dict = Stream.of(DictTypeEnum.values()).collect(Collectors.toMap(Enum::name, DictTypeEnum::getList));
		ServletEnum.dict.val(dict);
	}

	public void initContext(){
		Context context = Lang.context();
		context.set("home", Constants.HOME_PATH);
		Properties prop = new Properties();
		try {
			prop.load(new ByteArrayInputStream(ConfigEnum.context.getValue().getBytes()));
			context.putAll(prop);
			VarRuntimeEnum.context.val(context);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void initScript(SysScript script){
		String var = "";
		String v1 = script.getType().getArgs();
		String v2 = script.getArgs();
		if(Strings.isNotBlank(v1))
			var = v1;
		else if(Strings.isNotBlank(v2)){
			for(String s1 : v2.split("\n")){
				var += "," + s1.split("\\|")[0];
			}
			var.substring(1);
		}
		SysUtil.jsEval(String.format("var script_%s = function(%s){%s\n}\n", script.getCode(),var,script.getFunction()));
	}

	/**
	 * 注册菜单
	 */
	public void regMenu(SysMenu menu){
		if(!bs.getTCacheStreamAll(SysMenu.class).findAny().isPresent())
			return;

		boolean m2 = bs.getTCacheStreamAll(SysMenu.class).anyMatch(v->v.getUrl().contains(menu.getUrl()));
		if(m2)
			return;

		TBase t = bs.daoSave(menu);
		//新注册的菜单默认对 角色 授权
		MRoleMenu m = new MRoleMenu();
		m.setRoleId(1L);
		m.setMenuId(t.getId());
		bs.daoSave(m);
	}

	public void injectParams(Object o,Map<String,String> vMap) {
		Mirror<?> m = Mirror.me(o.getClass());
		for(Field f : m.getFields(AoReflect.class)){
			AoReflect ao = f.getAnnotation(AoReflect.class);
			if(ao.type() == AstEnum.param){
				Injecting inject =  m.getInjecting(f.getName());
				String val = vMap.get(f.getName());
				if(Strings.isNotBlank(val)){
					inject.inject(o, val);
				}
			}

		}
	}

	public void injectParams(Object o,String field,String value) {
		Mirror<?> m = Mirror.me(o.getClass());
		m.getInjecting(field).inject(o,value);
	}

	public void injectParams(Object o,int type,Long cid) {
		injectParams(o,getConfigMap(type, cid));
	}

	public TParam getConfigParam(int type, Long cid, String code){
		return bs.getTCacheFirst(TParam.class, v->v.getType() == type && (cid == null || cid.equals(v.getCid())) && Strings.equals(code, v.getCode()));
	}

	public Map<String,String> getConfigMap(int type,Long cid){
		return bs.getTCacheMap(TParam.class, v->v.getType() == type && (cid == null || cid.equals(v.getCid()) && v.getValue() != null),
				TParam::getCode, TParam::getValue);
	}

	public void saveConfigParams(TParam v){
		TParam p = getConfigParam(v.getType(),v.getCid(),v.getCode());
		if(p == null){
			bs.daoSave(v);
		}else if(!Strings.equals(p.getValue(), v.getValue())){
			p.setValue(v.getValue());
			bs.daoSave(p);
		}
	}

	//-----------------------------------动作链--------------------------------------------
	public List<TAction> getAction(TBase tBase){
		return getAction(tBase.getClass().getName(),tBase.getId());
	}

	public List<TAction> getAction(String plass, Long pid){
		return bs.getTCache(TAction.class, v->
				(v.getParentId() == null || v.getParentId() == 0) && //只获取根节点的
				(v.getType() == 0 || v.getType() == 1) &&
				Strings.equals(plass,v.getPlass()) &&
				pid.equals(v.getPid())
		);
	}

	public List<TAction> getAction(Long parentId){
		return bs.getTCache(TAction.class, v->parentId.equals(v.getParentId()));
	}

	//-----------------------------------字典--------------------------------------------
	public SysDict getDict(String type,String value) {
		return bs.getTCacheFirst(SysDict.class,v->Strings.equals(v.getType(),type) && Strings.equals(v.getValue(),value));
	}

	public Map<String, List<SysDict>> getDictMap() {
		List<SysDict> dictList= bs.getTCache(SysDict.class);
		return dictList.stream().filter(v->v.getType() != null).collect(Collectors.groupingBy(SysDict::getType));
	}

	public Map<String, String> getDictType(){
		return dictType;
	}

	@Override
	public void update(Observable o, Object event) {
		if(!(event instanceof NotifyEvent))
			return;
		NotifyEvent ne = (NotifyEvent) event;
		Object arg = ne.getData();

		if(ne.getEventType() != EventEnum.SAVE_AFTER)
			return;
		if(arg instanceof TParam){
			TParam param = (TParam) arg;
			if (param.getType() == 0) {
				Map<String, String> c = ServletEnum.config.val();
				c.put(param.getCode(), param.getValue());
				if (ConfigEnum.context.name().equals(param.getCode()))
					initContext();
			}
		}else if(arg instanceof SysScript){
			//initScript((SysScript) arg);
		}else if(arg instanceof SysDict){
			initDict();
		}
	}
}
