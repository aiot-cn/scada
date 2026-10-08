package org.aiot.controller;

import com.fazecast.jSerialComm.SerialPort;
import org.aiot.communication.CommunicationInfc;
import org.aiot.device.BaseDevice;
import org.aiot.device.base.AiModelDevice;
import org.aiot.device.base.ZLMediaKit;
import org.aiot.infc.ProtocolInfc;
import org.aiot.infc.device.BaseExtend;
import org.aiot.infc.device.DevData;
import org.aiot.infc.device.DeviceInfc;
import org.aiot.lang.Command;
import org.aiot.lang.annotation.AoReflect;
import org.aiot.lang.workflow.Workflow;
import org.aiot.main.Constants;
import org.aiot.model.DataRes;
import org.aiot.model.enums.DictTypeEnum;
import org.aiot.model.enums.PathEnum;
import org.aiot.model.lang.PointData;
import org.aiot.model.lang.RecognitionRes;
import org.aiot.model.lang.SRes;
import org.aiot.model.project.ArgBean;
import org.aiot.model.project.MethodBean;
import org.aiot.model.table.*;
import org.aiot.model.table.user.SysUser;
import org.aiot.mvc.RoleActionFilter;
import org.aiot.service.*;
import org.aiot.util.*;
import org.nutz.dao.Cnd;
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
import java.net.NetworkInterface;
import java.net.SocketException;
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
		Class<?> c = BaseDevice.class;
		if(Strings.isNotBlank(dt.getKlass())){
			try {
				c = Lang.loadClass(dt.getKlass());
			} catch (ClassNotFoundException ignored) {

			}
		}
		List<MethodBean> list = ds.methodsDetail(c);
		if(list == null)
			return new ArrayList<>();
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
				if(d != null && (siteId == null))
					devices.add(d);
			}
		}else{
			devices = bs.getTCache(TDevice.class,v->siteId == null);
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

	/**
	 * 批量修改点位设置
	 * 按编号包含、设备类型、所属区域筛选点位，多条件同时满足（交集），区域为空时不限制
	 */
	@At
	public @Ok("json") DataRes batchSetPoint(TPoint  point,String deviceType,Long[] areaIds){
		BaseService bs = ioc.get(BaseService.class);
		List<TPoint> points = bs.getTCache(TPoint.class, v-> {
			if(v.getCode() == null || !v.getCode().contains(point.getCode()))
				return false;

			if(areaIds != null && areaIds.length > 0 && !Arrays.asList(areaIds).contains(v.getAreaId()))
				return false;

			if(Strings.isBlank(deviceType))
				return true;
			
			Long deviceId = v.getDeviceId();
			if(deviceId ==  null)
				return false;

			TDevice d = bs.getTCache(TDevice.class,deviceId);
			return deviceType.equals(d.getDeviceType());
		});
		if(points.isEmpty())
			return DataRes.error("没有匹配到点位");
		for(TPoint p : points){
			if(Strings.isNotBlank(point.getUnit()))
				p.setUnit("null".equals(point.getUnit())? null : point.getUnit());

			if(Strings.isNotBlank(point.getAlarmRule()))
				p.setAlarmRule("null".equals(point.getAlarmRule())? null : point.getAlarmRule());

			if(point.getRecOnValue() != null)
				p.setRecOnValue(point.getRecOnValue() > 0 ? point.getRecOnValue() : null);

			if(point.getRecOnTime() != null)
				p.setRecOnTime(point.getRecOnTime());
			if(point.getRecOnState() != null)
				p.setRecOnState(point.getRecOnState());
			if(point.getRecOnEvery() != null)
				p.setRecOnEvery(point.getRecOnEvery());
			bs.daoSave(p,"unit|alarmRule|recOnValue|recOnTime|recOnState|recOnEvery");
		}

		return DataRes.success("共更新"+points.size()+"个点位");
	}

	//由点位编号解析所属设备类型：dev-{deviceId}-{属性code}
	private String pointDeviceType(TPoint point, Map<Long,TBase> deviceMap){
		String[] s = Strings.sNull(point.getCode()).split("-",3);
		if(s.length < 3 || !"dev".equals(s[0]))
			return null;
		try{
			TDevice d = (TDevice) deviceMap.get(Long.parseLong(s[1]));
			return d == null ? null : d.getDeviceType();
		}catch (NumberFormatException e){
			return null;
		}
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
	@Filters(@By(type= RoleActionFilter.class, args="WORK_SCRIPT"))
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
		AiotService as = ioc.get(AiotService.class);
		Map<String,Object> m = HttpUtil.reqToMap(req);
		return as.execWorkflow(id,m);
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

	//====================   总览 dashboard   =============================
	//总览信息：概况、磁盘、网络、插件、流媒体、模型、设备、点位、记录 一次返回
	@At
	public @Ok("json") NutMap getDashboardInfo(){
		BaseService bs = ioc.get(BaseService.class);
		DeviceService ds = ioc.get(DeviceService.class);
		PointService ps = ioc.get(PointService.class);
		NutMap nm = NutMap.NEW();

		//概况
		NutMap overview = NutMap.NEW();
		overview.put("userCount", bs.getTCache(SysUser.class).size());
		nm.put("overview", overview);

		//磁盘
		List<NutMap> disks = new ArrayList<>();
		for(File root : SystemInfo.getDiskRoots()){
			long total = root.getTotalSpace();
			if(total <= 0)
				continue;
			NutMap disk = NutMap.NEW();
			disk.put("path", root.getPath());
			disk.put("total", total);
			disk.put("free", root.getFreeSpace());
			disk.put("usage", SystemInfo.getDiskUsage(root));
			disks.add(disk);
		}
		nm.put("disks", disks);

		//网络：网口、wifi、蓝牙
		List<NutMap> networks = new ArrayList<>();
		try {
			Enumeration<NetworkInterface> nis = NetworkInterface.getNetworkInterfaces();
			while (nis.hasMoreElements()){
				NetworkInterface ni = nis.nextElement();
				if(ni.isLoopback())
					continue;

				NutMap net = NutMap.NEW();
				net.put("name", ni.getName());
				net.put("displayName", ni.getDisplayName());
				net.put("up", ni.isUp());
				net.put("type", netType(ni));
				List<String> addrs = new ArrayList<>();
				ni.getInterfaceAddresses().forEach(a->{
					if(a.getAddress() != null)
						addrs.add(a.getAddress().getHostAddress());
				});
				if(addrs.isEmpty())
					continue;
				net.put("addrs", addrs);
				networks.add(net);
			}
		} catch (SocketException ignored) {
		}
		nm.put("networks", networks);

		//插件：资源目录 lib 下存在对应文件夹即视为已安装
		String[][] plugins = {
				{"HCNetSDK","海康"},
				{"dhNetSDK","大华"},
				{"nginx","代理"},
				{"ZLMediaKit","流媒体"},
				{"wkhtmltox","HTML转pdf或图像"}
		};
		List<NutMap> pluginList = new ArrayList<>();
		for(String[] p : plugins){
			NutMap plugin = NutMap.NEW();
			plugin.put("name", p[0]);
			plugin.put("text", p[1]);
			plugin.put("installed", PathEnum.lib.getFile(p[0]).isDirectory());
			pluginList.add(plugin);
		}
		nm.put("plugins", pluginList);

		//流媒体：视频源总数及拉流在线数
		NutMap video = NutMap.NEW();
		video.put("total", bs.getTCache(TVideoSource.class).size());
		int online = 0;
		ZLMediaKit zlm = ds.getDevice(ZLMediaKit.class);
		if(zlm != null){
			NutMap res = zlm.getMediaList();
			if(res != null && res.get("data") instanceof List)
				online = ((List<?>) res.get("data")).size();
		}
		video.put("online", online);
		nm.put("video", video);

		//模型：总数及已加载数
		NutMap model = NutMap.NEW();
		model.put("total", bs.getTCache(TAiModel.class).size());
		AiModelDevice amd = ds.getDevice(AiModelDevice.class);
		model.put("loaded", amd == null ? 0 : amd.getLoadedCount());
		nm.put("model", model);

		//设备：总数及各类型数量
		Map<String, List<TDevice>> devMap = bs.getTCacheMap(TDevice.class, v->true, TDevice::getDeviceType);
		List<NutMap> types = new ArrayList<>();
		devMap.forEach((k,v)->{
			DeviceType dt = bs.getTCacheAllFirst(DeviceType.class, t->Strings.equals(k,t.getCode()));
			NutMap type = NutMap.NEW();
			type.put("type", k);
			type.put("name", dt == null ? k : dt.getName());
			type.put("count", v.size());
			types.add(type);
		});
		types.sort((a,b)-> b.getInt("count") - a.getInt("count"));
		NutMap device = NutMap.NEW();
		device.put("total", bs.getTCache(TDevice.class).size());
		device.put("types", types);
		nm.put("device", device);

		//点位：1分钟内没有数据的即为离线，在线中按报警状态再细分，三类互斥
		List<TPoint> points = bs.getTCache(TPoint.class);
		long now = System.currentTimeMillis();
		int ptOnline = 0, alarm = 0;
		for(TPoint p : points){
			PointData pd = ps.getPointData(p.getId());
			if(pd == null || pd.getTime() == null || now - pd.getTime() >= 60*1000L)
				continue;
			if(pd.getState() != null && pd.getState() > 1)
				alarm++;
			else
				ptOnline++;
		}
		NutMap point = NutMap.NEW();
		point.put("total", points.size());
		point.put("online", ptOnline);
		point.put("offline", points.size() - ptOnline - alarm);
		point.put("alarm", alarm);
		nm.put("point", point);

		//记录：按当日、本周、本月统计
		Date date = new Date();
		NutMap record = NutMap.NEW();
		record.put("today", recordStat(bs, dayStart(date)));
		record.put("week", recordStat(bs, weekStart(date)));
		record.put("month", recordStat(bs, monthStart(date)));
		nm.put("record", record);

		return nm;
	}

	//CPU、内存，页面轮询累加显示
	@At
	public @Ok("json") NutMap getDashboardPerf(){
		NutMap nm = NutMap.NEW();
		nm.put("time", System.currentTimeMillis());
		nm.put("cpuSystem", SystemInfo.getSystemCpuUsage());
		nm.put("cpuProcess", SystemInfo.getProcessCpuUsage());
		nm.put("memPhysical", SystemInfo.getSystemMemoryUsage());
		nm.put("physicalTotal", SystemInfo.getTotalPhysicalMemory());
		nm.put("physicalUsed", SystemInfo.getTotalPhysicalMemory() - SystemInfo.getFreePhysicalMemory());
		nm.put("memHeap", SystemInfo.getHeapMemoryUsage());
		nm.put("heapUsed", SystemInfo.getHeapMemoryUsed());
		nm.put("heapMax", SystemInfo.getHeapMemoryMax());
		return nm;
	}

	//按名称粗略区分网卡类型：wifi、bluetooth、ethernet
	private String netType(NetworkInterface ni){
		String n = (ni.getName() + " " + Strings.sNull(ni.getDisplayName())).toLowerCase();
		if(n.contains("bluetooth") || n.contains("蓝牙") || n.startsWith("bt"))
			return "bluetooth";
		if(n.contains("wifi") || n.contains("wi-fi") || n.contains("802.11") || n.contains("wireless") || n.contains("wlan") || n.contains("无线"))
			return "wifi";
		return "ethernet";
	}

	/**
	 * 待复核：state>0 且 reviewState 为空
	 * 待处理：reviewState>0 且 reviewOpinion 为空
	 * 已处理：reviewState>0 且 reviewOpinion 不为空
	 */
	private NutMap recordStat(BaseService bs,Date start){
		NutMap nm = NutMap.NEW();
		Cnd base = Cnd.where("createDate", ">=", start).and("isRemoved", "=", 0);
		nm.put("pendingReview", bs.count(TRecord.class, base.clone().and("state", ">", 0).and("reviewState", "is", null)));
		nm.put("pendingProcess", bs.count(TRecord.class, base.clone().and("reviewState", ">", 0).and("reviewOpinion", "is", null)));
		nm.put("processed", bs.count(TRecord.class, base.clone().and("reviewState", ">", 0).and("reviewOpinion", "is not", null)));
		return nm;
	}

	//当天 00:00
	private static Date dayStart(Date date){
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		return c.getTime();
	}

	//本周一 00:00
	private static Date weekStart(Date date){
		Calendar c = Calendar.getInstance();
		c.setTime(dayStart(date));
		int dow = c.get(Calendar.DAY_OF_WEEK);//1=周日 ... 7=周六
		c.add(Calendar.DAY_OF_MONTH, dow == Calendar.SUNDAY ? -6 : Calendar.MONDAY - dow);
		return c.getTime();
	}

	//本月1号 00:00
	private static Date monthStart(Date date){
		Calendar c = Calendar.getInstance();
		c.setTime(dayStart(date));
		c.set(Calendar.DAY_OF_MONTH, 1);
		return c.getTime();
	}

	//====================   服务提供   =============================
	@At
	@Filters(@By(type= CrossOriginFilter.class))
	public @Ok("json") List<TAiModel> getAiModel(){
		BaseService bs = ioc.get(BaseService.class);
		return bs.getTCacheStreamAll(TAiModel.class).collect(Collectors.toList());
	}

}
