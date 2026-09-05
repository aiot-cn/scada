package org.aiot.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.aiot.device.BaseDevice;
import org.aiot.infc.device.DeviceInfc;
import org.aiot.infc.device.DevData;
import org.aiot.lang.Command;
import org.aiot.lang.NotifyEvent;
import org.aiot.lang.annotation.AoReflect;
import org.aiot.model.enums.ANSI;
import org.aiot.model.enums.AstEnum;
import org.aiot.model.enums.CommandTypeEnum;
import org.aiot.model.enums.EventEnum;
import org.aiot.model.project.MethodBean;
import org.aiot.model.table.*;
import org.aiot.util.SysUtil;
import org.nutz.aop.ClassAgent;
import org.nutz.aop.ClassDefiner;
import org.nutz.aop.DefaultClassDefiner;
import org.nutz.aop.MethodMatcher;
import org.nutz.aop.asm.AsmClassAgent;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Files;
import org.nutz.lang.Lang;
import org.nutz.lang.Mirror;
import org.nutz.lang.Strings;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.resource.Scans;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@IocBean(create="init")
public class DeviceService implements Observer {

	@Inject BaseService bs;
	@Inject ConfigService cs;
	
	private final Log log = Logs.get();
	private final Map<String,List<MethodBean>> dtMethodMap = new HashMap<>();
	private final Map<Long, DeviceInfc> instance  = new ConcurrentHashMap<>();
	private final Map<String,DeviceInfc> deviceMap = new HashMap<>();

	private List<Class<?>> devClassList;
	private Map<String,String> serviceMap = new HashMap<>();

	public void init(){
		bs.addObserver(this);
		loadDeviceConfig();
		new Thread(()->{
			try {
				initDevice();
			}catch (Throwable a){
				log.error("初始化设备异常:"+a.getMessage());
			}
		}).start();
	}
	
	
	public void loadDeviceConfig() {
		String str = Files.read("conf/device.json");
		JSONObject json = JSONObject.parseObject(str);
		
		for(String k : json.keySet()) {
			JSONObject typeJson = json.getJSONObject(k);
			JSONArray propertyJson = typeJson.getJSONArray("property");
			if(propertyJson != null){
				for(Object o : propertyJson){
					JSONObject j = (JSONObject) o;
					DeviceProperty dp = j.toJavaObject(DeviceProperty.class);
					dp.setDeviceType(k);
					if(bs.getTCacheStreamAll(DeviceProperty.class)
							.noneMatch(v->dp.getDeviceType().equals(v.getDeviceType()) && v.getCode().equals(dp.getCode()))){
						bs.daoSave(dp);
					}
				}
			}

			if(bs.getTCacheStreamAll(DeviceType.class).anyMatch(v -> k.equals(v.getCode()))){
				continue;
			}


			DeviceType dt = typeJson.toJavaObject(DeviceType.class);
			dt.setCode(k);
			bs.daoSave(dt);
			
			JSONArray commandJson = typeJson.getJSONArray("command");
			if(commandJson != null){
				for(Object o : commandJson) {
					JSONObject j = (JSONObject) o;
					DeviceCommand dc = j.toJavaObject(DeviceCommand.class);
					dc.setDeviceType(k);
					bs.daoSave(dc);
					
					List<DeviceAnalysis> analysisList =  JSON.parseArray(j.getString("analysis"), DeviceAnalysis.class);
					if(analysisList != null){
						for(DeviceAnalysis da : analysisList) {
							da.setCommandId(dc.getId());
							bs.daoSave(da);					
						}
					}
					
				}
			}

		}
	}

	public List<Class<?>> getDevClassList(){
		if(devClassList == null){
			synchronized(this) {
				devClassList = new ArrayList<>();
				for (Class<?> classZ : Scans.me().scanPackage(BaseDevice.class)) {
					AoReflect ao = classZ.getAnnotation(AoReflect.class);
					String klass = classZ.getName();
					if (ao != null && !klass.contains("$") && DeviceInfc.class.isAssignableFrom(classZ)) {
						devClassList.add(classZ);
						if (bs.getTCacheStreamAll(DeviceType.class).noneMatch(t -> klass.equals(t.getKlass()))) {
							DeviceType dt = new DeviceType();
							String code = ao.code();
							if (Strings.isBlank(code))
								code = klass.substring(klass.lastIndexOf(".") + 1);
							dt.setCode(code);
							dt.setName(ao.value());
							dt.setRole(ao.deviceRole());
							dt.setKlass(klass);
							bs.daoSave(dt);
						}
					}
				}
			}
		}
		return devClassList;
	}

	public void initDevice(){
		for(TDevice dev : getDeviceList()){
			loadDev(dev);
		}
	}

	public  DeviceInfc getInstance(TDevice device){
		Long deviceId = device.getId();
		DeviceInfc i = instance.get(deviceId);
		if(i == null){
			try {
				DeviceType dt = this.getDeviceType(device.getDeviceType());
				Class<?> c = (dt == null || Strings.isBlank(dt.getKlass())) ? BaseDevice.class : Lang.loadClass(dt.getKlass());
				//c.newInstance() java9已弃用，只能调用无参构造函数
				Constructor<?> constructor = c.getDeclaredConstructors()[0];
				Object[] arg = new Object[constructor.getParameterCount()];

				i = (DeviceInfc) constructor.newInstance(arg);
				i.setEnv(device,null);
				log.debugf("%d.%s[%s] 设备已实例化",device.getId(),device.getName(),c.getName());

				ClassDefiner cd = DefaultClassDefiner.defaultOne();
				ClassAgent agent = new AsmClassAgent();
				agent.addInterceptor(new DeviceMethodMatcher(), i);
				Class<?> invocation = agent.define(cd, c);
				DeviceInfc inv = (DeviceInfc) invocation.getDeclaredConstructors()[0].newInstance(arg);
				inv.setEnv(device,i);
				i = inv;

				instance.put(deviceId, i);
				deviceMap.put(device.getDeviceType()+"_"+device.getId(),i);

			} catch(ClassNotFoundException e){
				log.error("设备["+ ANSI.COLOR_FORE.red.format(deviceId+"."+device.getName())+"]未找到实现类:"+e.getMessage());
			} catch(Throwable e) {
				log.error("设备["+ANSI.COLOR_FORE.red.format(deviceId+"."+device.getName())+"]实例化异常:"+e.getMessage());
				e.printStackTrace();
			}
		}
		return i;
	}

	public DeviceInfc getInstance(Long id){
		if(id == null)
			throw Lang.makeThrow("获取设备时ID不能为空");
		return instance.get(id);
	}

	public DeviceInfc removeInstance(Long id){
		return instance.remove(id);
	}

	public void loadDev(TDevice dev){
		log.infof( ANSI.COLOR_FORE.purple.format("设备加载")+" - %03d.%s",dev.getId(), dev.getName());
		DeviceInfc bd = getInstance(dev);
		if(bd == null)
			return;
		try {
			Mirror<? extends DeviceInfc> m = Mirror.me(bd.getClass());
			for(Field f : m.getFields(AoReflect.class)){
				AoReflect ao = f.getAnnotation(AoReflect.class);
				if(ao.type() == AstEnum.param){
					TParam p = getDeviceParam(dev.getId(), f.getName());
					if(p != null && Strings.isNotBlank(p.getValue())){
						m.getInjecting(f.getName()).inject(bd, p.getValue());
					}
				}else if(ao.type() == AstEnum.device || DeviceInfc.class.isAssignableFrom(f.getType())){
					TDevice d = getDeviceChild(dev,f.getType(),f.getName());
					if(ao.required() && d == null){
						DeviceType dt = bs.getTCacheStreamAll(DeviceType.class).filter(v->Strings.equals(v.getKlass(), f.getType().getName())).findFirst().orElse(null);
						if(dt != null){
							dt.setIsRemoved(0);
							bs.daoSave(dt);

							d = new TDevice();
							d.setParentId(dev.getId());
							d.setSiteId(dev.getSiteId());
							d.setName(ao.value());
							d.setDeviceType(dt.getCode());
							bs.daoSave(d);
						}
					}
					if(d != null){
						m.getInjecting(f.getName()).inject(bd,getInstance(d));
						log.infof("%-4s设备 [%d.%s] --> [%d.%s.%s]%s","",d.getId(),d.getName(),dev.getId(), dev.getName(),f.getName(),ao.value());
					}/*else{
						log.warnf("%-4s设备 [%d.%s] 的子设备 [%s]：%s 没有找到","",dev.getId(), dev.getName(),ao.value(),f.getName());
					}*/
				}/*else if(ao.type() == 3) {
					Device d = bs.getTCache(Device.class,dev.getParentId());
					if(d != null){
						DeviceInfc pd = ds.getInstance(d);
						if(pd.getClass().equals(f.getType())) {
							m.getInjecting(f.getName()).inject(bd,ds.getInstance(d));
						}else{
							log.errorf("设备 [%s] 的父设备 [%s]类型（%s）与注入的类型（%s）不匹配", dev.getName(),ao.value(),pd.getClass(),f.getType());
						}

					}else{
						log.errorf("没有找到设备 [%s] 的父设备 [%s]：%s", dev.getName(),ao.value(),f.getName());
					}
				}*/

			}
			//log.infof("设备 [%d.%s] 参数已注入，开始初始化...",dev.getId(),dev.getName());
			new Thread(() -> {
				try {
					bd.init();
					//log.infof("设备 [%d.%s] 初始化完成",dev.getId(),dev.getName());
				} catch (Exception e) {
					log.errorf("设备 [%d.%s]%s 初始化异常：%s",dev.getId(),dev.getName(),bd.getClass().getSimpleName(),e.getMessage());
					e.printStackTrace();
				}
			}).start();

		} catch (Exception e2) {
			e2.printStackTrace();
		}

	}
	//------------------------------------------devInfc 设备--------------------------------------------------------------
	public <T> T getDevice(Class<T> classOfT){
		return instance.values().stream()
				.filter(v -> classOfT.isAssignableFrom(v.getClass()))
				.map(classOfT::cast)
				.findFirst()
				.orElse(null);
	}
	public <T> List<T> getDevByClass(Class<T> classOfT){
		List<T> list = new ArrayList<>();
		instance.forEach((k,v)->{
			if(classOfT.isAssignableFrom(v.getClass())){
				list.add(classOfT.cast(v));
			}
		});
		return list;
	}
	
	//------------------------------------------tDevice --------------------------------------------------------------
	public List<TDevice> getDeviceList() {
		return bs.getTCache(TDevice.class);
	}

	public List<TDevice> getDeviceBySite(Long siteId){
		return bs.getTCache(TDevice.class,v->siteId.equals(v.getSiteId()));
	}

	public List<TDevice> getDeviceByCommu(Long cid) {
		return bs.getTCache(TDevice.class,v->cid.equals(v.getCommunication()));
	}
	
	public List<TDevice> getDeviceByType(String type){
		return bs.getTCache(TDevice.class,v->type.equals(v.getDeviceType()));
	}

	public TDevice getDeviceByAddress(String address){
		return bs.getTCacheFirst(TDevice.class,v->address.equals(v.getAddress()));
	}

	public TDevice getDeviceFirst(String tag){
		return bs.getTCacheFirst(TDevice.class,v->
				v.getId().toString().equals(tag) ||
				Strings.equals(tag,v.getDeviceType())
				);
	}

	/**
	 * 获取设备某一类型的子设备
	 */
	public TDevice getDeviceChild(TDevice dev,Class<?> classOfT,String exp){
		return bs.getTCacheFirst(TDevice.class,v->{
					DeviceType dt = getDeviceType(v.getDeviceType());
			try {
				return dt != null && Strings.isNotBlank(dt.getKlass()) && dev.getId().equals(v.getParentId()) &&
						(Strings.isBlank(v.getExp1()) ? classOfT.isAssignableFrom(Lang.loadClass(dt.getKlass())) : Strings.equals(exp,v.getExp1()) );
			} catch (Exception e) {
				log.error(dev.getName()+" 的子设备 "+classOfT+"查找出错："+e.getMessage());
				return false;
			}

		});
	}
	
	/**
	 * 获取设备下的子设备
	 */
	public List<TDevice> getChildDevice(Long deviceId){
		return bs.getTCache(TDevice.class, v-> deviceId.equals(v.getParentId()));
	}

	//---------------------------------------------    设备参数    ----------------------------------------------------------
	public TParam getDeviceParam(Long cid, String code){
		return bs.getTCacheFirst(TParam.class, v->v.getType() == 1 && (cid == null || cid.equals(v.getCid())) && Strings.equals(code, v.getCode()));
	}
	
	//-------------------------------------------deviceType 设备类型----------------------------------------------------------
	
	public DeviceType getDeviceType(String type){
		return bs.getTCacheFirst(DeviceType.class,v->v.getCode().equals(type));
	}

	//-------------------------------------------command 设备指令-------------------------------------------------------------

	public List<Command> buildCommands(TDevice device, List<DeviceCommand> deviceCommands, String remark, Object... format){
		List<Command> commandList = new ArrayList<>();
		for(DeviceCommand devCom:deviceCommands){
			commandList.add(new Command(device,devCom,remark, format));
		}
		return commandList;
	}

	/**
	 * 根据设备与指令类型生成 指令
	 * 此方法不允许在其它地方调用
	 * @param device
	 * @param commandType
	 * @param remark
	 * @param format
	 * @return
	 */
	public List<Command> buildCommands(TDevice device,String commandType,String remark,Object... format){
		List<DeviceCommand> l = bs.getTCache(DeviceCommand.class, v-> device.getDeviceType().equals(v.getDeviceType()) &&
				Strings.equals(commandType,v.getCode())
		);
		List<Command> commandList = buildCommands(device,l,remark,format);
		return commandList;
	}

	/**
	 * 根据设备与指令类型生成
	 * @param device
	 * @param commandType
	 * @return
	 */
	public List<Command> buildCommands(TDevice device, CommandTypeEnum commandType, Object... format){
		return buildCommands(device,commandType.name(),null,commandType.getText(),format);
	}

	public DeviceProperty getProperty(TDevice device,String code){
		DeviceProperty dp = bs.getTCacheFirst(DeviceProperty.class,v->
				Strings.equals(device.getDeviceType(),v.getDeviceType())
				&& Strings.equals(code,v.getCode())
				&& device.getId().equals(v.getDeviceId())
		);
		if(dp == null){
			dp = bs.getTCacheFirst(DeviceProperty.class,v->
					Strings.equals(device.getDeviceType(),v.getDeviceType())
					&& Strings.equals(code,v.getCode())
			);
		}
		return dp;
	}

	//-------------------------------------------报警联动-------------------------------------------------------------
	/**
	 * 判断是否符合报警联动规则
	 * @param deviceAction 联动规则
	 * @param device 设备
	 * @return -2故障 -1 挂牌 0 不符合 1、预警 2、报警
	 */

	public int conditionHolds(DeviceAction deviceAction,TDevice device) {
		if(deviceAction == null)
			return 0;
		DeviceInfc bd = getInstance(device.getId());
		if(bd == null)
			return 0;

		Object value = bd.getDevData(deviceAction.getAnalysis()).getValue();
		return deviceAction.getCompare().eval(value, deviceAction.getValue(), deviceAction.getHysteresis());
	}

	/**
	 *  根据设备及解析 获取对应的报警联动规则
	 */
	public DeviceAction getDeviceAction(TDevice device, String analysis) {
		return bs.getTCacheFirst(DeviceAction.class,v->v.getAlarm() > 0 && Strings.equals(analysis,v.getAnalysis()) && (
				v.getDeviceId() == null ? Strings.equals(v.getDeviceType(),device.getDeviceType()) : (v.getDeviceId().equals(device.getId()) || v.getDeviceId().equals(device.getParentId()))
		));
	}
	
	//----------------------------------------方法详情-----------------------------------------------------------------
	public List<MethodBean> methodsDetail(String klass){
		return dtMethodMap.computeIfAbsent(klass,v-> {
			try {
				return methodsDetail(Lang.loadClass(klass));
			} catch (ClassNotFoundException | NoClassDefFoundError e) {
				e.printStackTrace();
			}
			return null;
		});
	}

	public List<MethodBean> methodsDetail(Class<?> klass){
		return dtMethodMap.computeIfAbsent(klass.getName(),v-> {
			List<MethodBean> m2 = new ArrayList<>();
			Method[] ms = Mirror.me(klass).getMethods();
			//klass.getMethods() 这个只能获取当前类的方法
			for(Method met : ms){
				if(met.getAnnotation(AoReflect.class) != null){
					MethodBean b = SysUtil.methodDetail(met);
					if(b != null)
						m2.add(b);
				}

			}
			return m2;
		});

	}

	public MethodBean methodDetail(Class<?> klass,String name){

		for(MethodBean met: methodsDetail(klass)){
			if(Strings.equals(met.getCode(), name))
				return met;
		}
		return null;
	}


	
	public Map<String, String> getServiceMap() {
		return serviceMap;
	}

	public void setServiceMap(Map<String, String> serviceMap) {
		this.serviceMap = serviceMap;
	}

	public Map<String, DeviceInfc> getDeviceMap() {
		return deviceMap;
	}

	public void destroy(){
		for(DeviceInfc deviceInfc : deviceMap.values()){
			try {
				deviceInfc.destroy();
			}catch (Exception ignored){

			}
		}
	}

	@Override
	public void update(Observable o, Object event) {
		if(!(event instanceof NotifyEvent))
			return;
		NotifyEvent ne = (NotifyEvent) event;
		if(ne.getEventType() != EventEnum.SAVE_AFTER)
			return;
		Object arg = ne.getData();

		if(arg instanceof TDevice){
			TDevice device = (TDevice) arg;
			if(device.getIsRemoved() == 0){
				loadDev((TDevice) arg);
			}else{
				DeviceInfc deviceInfc = getInstance(device);
				if(deviceInfc != null)
					deviceInfc.destroy();
			}

		}else if(arg instanceof DeviceAction){//重新刷新设备报警状态
			DeviceAction da = (DeviceAction) arg;
			if(da.getAlarm() == 0)
				return;
			List<TDevice> deviceList = bs.getTCache(TDevice.class, v->
					(da.getDeviceId() == null && Strings.equals(v.getDeviceType(),da.getDeviceType()) ) ||
							(da.getDeviceId() != null && da.getDeviceId().equals(v.getId()) )
			);
			for(TDevice d : deviceList){
				int state = Math.max(0,conditionHolds(da,d));
				DeviceInfc bd = getInstance(d);
				DevData data = bd.getDevData(da.getAnalysis());
				if(data != null)
					data.setState(state);
			}
		}else if(arg instanceof TParam){
			TParam param = (TParam) arg;
			if(param.getType() == 1){
				cs.injectParams(getInstance(param.getCid()),param.getCode(),param.getValue());
			}
		}
	}

	public class DeviceMethodMatcher implements MethodMatcher {
		//TODO 这里过滤的不精准
		@Override
		public boolean match(Method method) {
			SysTrigger d = bs.getTCacheFirst(SysTrigger.class,v->v.getDeviceId() > 0 &&
					("M-"+method.getName()).equals(v.getMember()));
			return d != null;
		}
	}


}
