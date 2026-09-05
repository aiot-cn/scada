package org.aiot.controller;

import com.fazecast.jSerialComm.SerialPort;
import org.aiot.device.BaseDevice;
import org.aiot.infc.ProtocolInfc;
import org.aiot.communication.CommunicationInfc;
import org.aiot.infc.device.BaseExtend;
import org.aiot.infc.device.DevData;
import org.aiot.infc.device.DeviceInfc;
import org.aiot.lang.Command;
import org.aiot.lang.CommonAction;
import org.aiot.lang.annotation.AoReflect;
import org.aiot.lang.workflow.Workflow;
import org.aiot.main.Constants;
import org.aiot.model.DataRes;
import org.aiot.model.enums.DictTypeEnum;
import org.aiot.model.lang.SRes;
import org.aiot.model.lang.PointData;
import org.aiot.model.lang.RecognitionRes;
import org.aiot.model.project.ArgBean;
import org.aiot.model.project.MethodBean;
import org.aiot.model.table.*;
import org.aiot.service.*;
import org.aiot.util.*;
import org.nutz.lang.Files;
import org.nutz.lang.Lang;
import org.nutz.lang.Mirror;
import org.nutz.lang.Strings;
import org.nutz.lang.util.NutMap;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.By;
import org.nutz.mvc.annotation.Filters;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.filter.CrossOriginFilter;

import javax.script.Bindings;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.stream.Collectors;

import static org.aiot.main.Constants.ioc;

@At("/json")
public class JsonController {

	@At
	public @Ok("json") Object getEnum(String type) throws ClassNotFoundException, SecurityException, IllegalArgumentException {
		Map<String, Object> map = new HashMap<>();
		String[] t = type.split(",");
		for (String t1 : t){
			if(!t1.contains(".")){
				String t2 = Strings.upperFirst(t1);
				if(t1.contains("/")){
					String[] t3 = t1.split("/");
					t3[t3.length-1] = Strings.upperFirst(t3[t3.length-1]);
					t2 = String.join(".", t3);;
				}

				t1 = "org.aiot.model.enums."+ t2 + "Enum";
			}
			Class<?> c = Lang.loadClass(t1);
			List<Map<String, Object>> m =  CommonUtil.enumToListMap(c);
			if(t.length == 1)
				return m;
			String key = Strings.lowerFirst(c.getSimpleName()).replace("Enum", "");
			map.put(key,m);
		}
		return map;
	}

	//==================== 字典 =============================
	@At
	public @Ok("json") List<Map<String, Object>> getDictType(){
		List<Map<String, Object>> dict = CommonUtil.enumToListMap(DictTypeEnum.class);
		ioc.get(ConfigService.class).getDictType().forEach((k,v)->{
			Map<String, Object> map = new HashMap<>();
			map.put("code",k);
			map.put("name",v);
			dict.add(map);
		});
		return dict;
	}

	@At
	public @Ok("json") Object getDict(String[] type){
		Map<String, List<SysDict>> map = ioc.get(ConfigService.class).getDictMap();
		if(type.length == 1)
			return map.get(type[0]);
		Map<String, List<SysDict>> m2 = new HashMap<>();
		for(String t : type)
			m2.put(t,map.get(t));
		return m2;
	}

	/**
	 * 获取设备类型属性字段
	 * 设备、服务、定时任务、通讯都要用
	 */
	@At
	public @Ok("json") List<NutMap> getAoReflect(String klass,String deviceType) throws ClassNotFoundException{
		if(Strings.isBlank(klass)){
			BaseService bs = ioc.get(BaseService.class);
			DeviceType dt = bs.getTCacheAllFirst(DeviceType.class, v->Strings.equals(v.getCode(),deviceType));
			klass = dt.getKlass();
		}

		List<NutMap> r = new ArrayList<>();
		if(Strings.isNotBlank(klass)){
			Mirror<?> m = Mirror.me(Lang.loadClass(klass));
			Field[] fields = m.getFields(AoReflect.class);
			Arrays.sort(fields, Comparator.comparingInt(v->v.getAnnotation(AoReflect.class).sequence()));
			for(Field f : fields){
				AoReflect as = f.getAnnotation(AoReflect.class);
				NutMap nm = CommonUtil.aoToMap(as);
				nm.put("code", f.getName());
				nm.put("klass", f.getType());
				r.add(nm);
			}
		}
		return r;
	}

	//------------  获取所有设备类型的方法描述  -------------------
	@At
	public @Ok("json") Map<String,List<MethodBean>> getAoMethods(){
		BaseService bs = ioc.get(BaseService.class);
		Map<String,List<MethodBean>> m = new HashMap<>();
		bs.getTCache(DeviceType.class,v->Strings.isNotBlank(v.getKlass())).forEach(dt->{
			m.put(dt.getCode(),getDevTypeMethods(dt.getCode()));
		});
		return m;
	}

	@At
	public @Ok("json") List<MethodBean> getDevTypeMethods(String deviceType){
		BaseService bs = ioc.get(BaseService.class);
		DeviceService ds = ioc.get(DeviceService.class);
		DeviceType dt = bs.getTCacheAllFirst(DeviceType.class,v->Strings.equals(deviceType,v.getCode()));
		List<MethodBean> list = new ArrayList<>();
		List<MethodBean> list2 = ds.methodsDetail(Strings.sBlank(dt.getKlass(), BaseDevice.class.getName()));
		if(list2 != null)
			list.addAll(list2);
		return list;
	}

	@At
	public @Ok("json") MethodBean getDevTypeMethod(String deviceType,String method,Long devId) throws ClassNotFoundException{
		BaseService bs = ioc.get(BaseService.class);
		if("workflow".equals(deviceType)){
			TWorkflow script = bs.getTCacheFirst(TWorkflow.class,v->method.equals(v.getCode()));
			MethodBean mb = new MethodBean(method,script.getName(),Object.class);
			mb.setArg(StrUtil.String2List(script.getArgs(),"\n", ArgBean.class));
			return mb;
		}
		if(devId != null){
			deviceType = bs.getTCache(TDevice.class,devId).getDeviceType();
		}
		String finalDeviceType = deviceType;
		DeviceType dt = bs.getTCacheFirst(DeviceType.class, v->Strings.equals(finalDeviceType,v.getCode()));
		Class<?> c = Strings.isBlank(dt.getKlass()) ? BaseDevice.class : Lang.loadClass(dt.getKlass());
		return ioc.get(DeviceService.class).methodDetail(c,method);
	}


	//------------   获取设备属性 param 页面用 -------------------
	@At
	public @Ok("json") Object device(String id){
		DeviceService ds = ioc.get(DeviceService.class);
		TDevice d = ioc.get(DeviceService.class).getDeviceFirst(id);
		if(d == null)
			return DataRes.error("没有找到标识为["+id+"]的设备");
		DeviceInfc bd = ds.getInstance(d.getId());
		return CommonUtil.aoFieldMap(bd);
	}

	@At
	public @Ok("json") Map<Long, NutMap> getDeviceData(Long siteId, boolean isSimplify, Long[] deviceId){
		BaseService bs = ioc.get(BaseService.class);
		DeviceService ds = ioc.get(DeviceService.class);
		Map<Long,TBase> deviceMap = bs.getTCacheMap(TDevice.class);
		List<TDevice> devices = new ArrayList<>();
		if(deviceId != null){
			for(Long did : deviceId){
				TDevice d = (TDevice) deviceMap.get(did);
				if(d != null && (siteId == null || siteId.equals(d.getSiteId())))
					devices.add(d);
			}
		}else{
			devices = bs.getTCache(TDevice.class,v->siteId == null || siteId.equals(v.getSiteId()));
		}

		Map<Long, NutMap> map = new HashMap<>();
		for(TDevice v : devices){
			DeviceInfc bd = ds.getInstance(v.getId());
			if(bd == null)
				continue;

			NutMap m = new NutMap();
			Map<String, DevData> dataMap = bd.getDataMap();
			if(isSimplify){
				NutMap dataMap2 = new NutMap();
				dataMap.forEach((k,d)->{
					dataMap2.put(k,new NutMap("value",d.getValue()).setv("state",d.getState()));
				});
				m.put("dataMap", dataMap2);
			}else{
				m.put("dataMap", dataMap);
			}
			map.put(v.getId(), m);
		}
		return map;
	}

	//------------   获取点位数据 -------------------
	@At
	public @Ok("json") List<NutMap> getPointData(){
		List<NutMap> nutMaps = new ArrayList<>();
		PointService ps = ioc.get(PointService.class);
		Map<Long, PointData> dataMap = ps.getPointDataMap();
		dataMap.forEach((k,data)->{
			if(data == null)
				return;

			Object v = data.getValue();
			NutMap nm = new NutMap();
			nm.put("id",k);
			nm.put("state",data.getState());
			nm.put("time",data.getTime());

			if(v instanceof RecognitionRes){
				RecognitionRes rec = (RecognitionRes) v;
				nm.put("value",rec.getValue());
				File imgFile = rec.getFile();
				if(imgFile != null && imgFile.isFile())
					nm.put("image", FileUtil.toPath(imgFile));
			}else{
				nm.put("value",v);
			}
			nutMaps.add(nm);
		});
		return nutMaps;
	}
	//====================  通讯  =============================
	@At
	public @Ok("json") DataRes getCommunicationMode(){
		return new DataRes(CommonUtil.getAoReflect("org.aiot.communication.mode", CommunicationInfc.class));
	}

	@At
	public @Ok("json") DataRes getCommunicationProtocol(){
		return new DataRes(CommonUtil.getAoReflect("org.aiot.communication.protocol", ProtocolInfc.class));
	}

	@At
	public @Ok("json") SerialPort[] getSerialPort(){
		return SerialPort.getCommPorts();
	}

	@At
	public @Ok("json") DataRes commuSend(Long id,String data,boolean isHex){
		CommunicationInfc commu = ioc.get(CommuService.class).getInstance(id);
		Command c = new Command(data,isHex,"");
		c.sendCommand(commu);
		//通信发送内容没有调试信息
		//commu.send(c);
		return new DataRes();
	}

	//==================== 脚本  =============================
	//脚本编辑所需
	@At
	public @Ok("json") NutMap getClassBean(String[] klass){
		NutMap nm = NutMap.NEW();
		for(String c : klass){
			List<MethodBean> mbs = Constants.methodMap.computeIfAbsent(c, v->{
				List<MethodBean> list = new ArrayList<>();
				try {
					Method[] ms = Mirror.me(Lang.loadClass(c)).getMethods();
					for(Method m:ms){
						if(!Modifier.isPublic(m.getModifiers()))
							continue;
						MethodBean t = CommonUtil.methodDetail(m);
						if(t!= null)
							list.add(t);
					}
				} catch (ClassNotFoundException e) {
					//e.printStackTrace();
				}

				return list;
			});

			nm.put(c,mbs);
		}
		return nm;
	}

	@At
	public @Ok("json") DataRes getScriptText(){
		String s = Files.read("script/baseJava.js")+ "\r\n";
		s += Files.read("script/analysis.js");
		return DataRes.success(null,s);
	}

	//执行脚本,用方法体包裹
	@At
	public @Ok("json") DataRes execScript(Long id,String text,String args,boolean run){
		BaseService bs = ioc.get(BaseService.class);
		ConfigService cs = ioc.get(ConfigService.class);
		SysScript ss = bs.getTCache(SysScript.class,id);
		String con = ss.getCode();
		ss.setFunction(text);
		try {
			cs.initScript(ss);
		}catch (Exception e){
			ss.setFunction(con);
			throw Lang.makeThrow(e.getMessage());
		}

		bs.daoSave(ss);

		if(run){
			Object[] a = Strings.sBlank(args,"").split(",");
			Object r = SysUtil.scriptByName(ss.getCode(),a);
			return DataRes.success("返回：" + r);
		}else{
			return DataRes.success(null);
		}

	}

	@At("/script/?")
	public @Ok("json") DataRes execScript2(String func, HttpServletRequest req){
		Map<String,Object> m = new HashMap<>();
		req.getParameterMap().forEach((k,v)-> m.put(k,v[0]));
		Object o = SysUtil.scriptByName(func,m);
		return new DataRes(o);
	}

	//==================== 工作流 =============================
	@At("/workflow/?")
	public @Ok("json") Object workflow(Long id,HttpServletRequest req){
		Map<String,Object> m = HttpUtil.reqToMap(req);
		TWorkflow tWorkflow = ioc.get(BaseService.class).getTCache(TWorkflow.class,id);
		Workflow workflow = new Workflow(tWorkflow);
		return workflow.run(m);
	}

	@At
	public @Ok("json:full") NutMap getWorkflowRes(Long id,Date timeStamp){
		Bindings b =  Workflow.bindingsMap.get(id);
		if(b == null)
			return null;

		Object rt = b.get("RUN_TIME");
		if(timeStamp != null && rt != null){
			if(((Date)rt).getTime()/1000 <= timeStamp.getTime()/1000)
				return null;
		}
		Map<String,Class<?>> klass = new HashMap<>();
		b.forEach((k,v)->klass.put(k,v == null ? null : v.getClass()));
		NutMap nm = new NutMap();
		nm.put("binding",b);
		nm.put("klass",klass);
		nm.put("connection",Workflow.connectionsMap.get(id));
		return nm;
	}

	//==================== 动作链 =============================
	//执行动作连
	@At
	public @Ok("json") DataRes action(String klass,Long id,HttpServletRequest r) {
		CommonAction ca = new CommonAction();

		Enumeration<String> paramNames = r.getParameterNames();
		while (paramNames.hasMoreElements()) {
			String paramName = paramNames.nextElement();
			String paramValue = r.getParameter(paramName);
			ca.setArg(paramName,paramValue);
		}
		Object o = ca.chainRun(klass, id);

		return new DataRes(o);
	}
	//执行单条动作链
	@At
	public @Ok("json") DataRes devMethod(Long id){
		TAction ac = ioc.get(BaseService.class).getTCache(TAction.class,id);
		CommonAction action = new CommonAction();
		action.setIgnoreException(false);
		return new DataRes(action.chainRun(ac));
	}
	//获取动作链状态
	@At
	public @Ok("json") Object getActionState(Long[] id){
		if(id.length == 1)
			return CommonAction.getActionState(id[0]);
		Map<Long,Object> res = new HashMap<>();
		for(Long i : id){
			CommonAction.ActionState actionState = CommonAction.getActionState(i);
			if(actionState == null || actionState.getResult() == null)
				continue;
			Object o = actionState.getResult();
			if(CommonUtil.isDirectlyDisplayable(o.getClass()))
				res.put(i,o);
		}
		return res;
	}

	//==================== 定时任务 =============================
	@At
	public @Ok("json") DataRes queryCron(){
		return new DataRes(ioc.get(CronService.class).query());
	}

	@At
	public @Ok("json") DataRes execCron(Long id){
		return new DataRes(ioc.get(CronService.class).exec(id));
	}

	//====================   资源   =============================
	@At
	public @Ok("raw") Object resContent(String url){
		return new SRes(url).getContent();
	}

	@At
	public @Ok("json") DataRes saveRes(String url,String content){
		new SRes(url).saveContent(content);
		return DataRes.success("");
	}

	//右键菜单
	@At
	public @Ok("json") List<Object> getRMenu(HttpServletRequest req){
		String name = "menuList";
		Map<String,Object> m = new HashMap<>();
		req.getParameterMap().forEach((k,v)-> m.put(k,v[0]));

		DeviceService ds = ioc.get(DeviceService.class);
		Class<?> c = BaseExtend.RMenu.class;
		List<Object> r = new ArrayList<>();
		ds.getDeviceMap().forEach((k,v)->{
			if(c.isAssignableFrom(v.getClass())){
				Object o = v.invoke(name,m);
				if(o instanceof List){
					r.addAll((List<?>)o);
				}else{
					r.add(o);
				}
			}
		});
		return r;
	}

	//====================   服务提供   =============================
	@At
	@Filters(@By(type= CrossOriginFilter.class))
	public @Ok("json") List<TAiModel> getAiModel(){
		BaseService bs = ioc.get(BaseService.class);
		return bs.getTCacheStreamAll(TAiModel.class).collect(Collectors.toList());
	}

}
