package org.aiot.device;

import com.alibaba.fastjson.JSONObject;
import org.aiot.communication.CommunicationInfc;
import org.aiot.infc.device.BaseExtend;
import org.aiot.infc.device.DevData;
import org.aiot.infc.device.DeviceInfc;
import org.aiot.lang.Command;
import org.aiot.lang.CommonAction;
import org.aiot.lang.CriQueue;
import org.aiot.lang.annotation.AoReflect;
import org.aiot.main.Constants;
import org.aiot.model.enums.*;
import org.aiot.model.lang.PointData;
import org.aiot.model.table.*;
import org.aiot.service.*;
import org.aiot.util.BaseUtils;
import org.aiot.util.CalcUtil;
import org.aiot.util.SysUtil;
import org.nutz.aop.InterceptorChain;
import org.nutz.aop.MethodInterceptor;
import org.nutz.castor.Castors;
import org.nutz.lang.Lang;
import org.nutz.lang.Mirror;
import org.nutz.lang.Strings;
import org.nutz.lang.util.MethodParamNamesScaner;
import org.nutz.log.Log;
import org.nutz.log.Logs;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;

import static org.aiot.main.Constants.ioc;

/**
 * 设备基类
 * implements Observer 实现观察者 Observable被观察者
 * bs.addObserver( this); 添加观察者
 */
@AoReflect("基础设备")
public class BaseDevice extends Observable implements DeviceInfc,MethodInterceptor {
	protected Log log = Logs.get();

	private   DeviceInfc target;
	private   Mirror<BaseDevice> mirror;

	protected BaseService bs;
	protected ConfigService cs;
	protected DeviceService ds;

	@AoReflect(value = "记录日志",type = AstEnum.param)
	protected boolean recordLog = false;

	@AoReflect(value = "调试模式",type = AstEnum.param)
	protected boolean isDebug = false;

	@AoReflect(value = "报警总计",getter = true)
	protected int alarmTotal;

	@AoReflect(value = "最近通讯时间")
	private long lastTime;

	@AoReflect
	protected TDevice device;

	protected DeviceType deviceType;

	private final Map<String, DevData> dataMap = new HashMap<>();//当前数据
	Map<String, CriQueue<Object>> queueMap = new HashMap<>();//数据队列

	protected String appPath = PathEnum.AppData.p2();

	public void setEnv(TDevice device,DeviceInfc target){
		this.bs = ioc.get(BaseService.class);
		this.cs = ioc.get(ConfigService.class);
		this.ds = ioc.get(DeviceService.class);

		this.device = device;
		this.deviceType = ds.getDeviceType(device.getDeviceType());

		this.target = target;
		this.appPath += device.getId();
		this.addObserver(cs);
		this.mirror = Mirror.me(this);
	}

	@AoReflect(value="巡检",type=AstEnum.command)
	public List<Command> comPoll(){
		return exec();
	}

	@AoReflect(value="执行",type=AstEnum.command)
	public List<Command> comType(String type,Object... format){
		List<Command> c = ds.buildCommands(device,type,null,format);
		execCommand(c,format);
		return c;
	}

	@AoReflect(value="设置",type=AstEnum.command)
	public List<Command> comSet(String analysis,Object... p){
		List<DeviceCommand> commands = bs.getTCache(DeviceCommand.class, v->
				device.getDeviceType().equals(v.getDeviceType()) && CommandTypeEnum.comSet.name().equals(v.getCode()) &&
				bs.getTCacheStream(DeviceAnalysis.class).anyMatch(a->v.getId().equals(a.getCommandId()) && Strings.equals(analysis, a.getCode()))
		);
		DeviceProperty a = getDeviceProperty(analysis);
		List<Command> c = ds.buildCommands(device,commands,"设置[" + (a != null ? a.getName() : analysis) + "]",p);
		execCommand(c,p);
		return c;
	}

	@AoReflect(value="接收",type=AstEnum.command)
	public void comRx(Command command){
		setLastTimeNow();
		/*command.analysiser(command.getRX());
		if(Strings.isNotBlank(command.getDeviceCommand().getContent()))
			command.sendCommand();*/
	}

	@AoReflect("设备自检")
	public void selfTest(){

	}

	@AoReflect("获取值")
	public Object getDataVal(String key){
		if(Strings.isBlank(key))
			return null;
		if(key.contains(Constants.devFiled)){
			return getFieldVal(key.split("_")[1]);
		}else{
			DevData data = dataMap.get(key);
			if(data == null)
				return null;
			return data.getValue();
		}
	}

	@Override
	@AoReflect("获取值对象")
	public DevData getDevData(String key){
		return dataMap.get(key);
	}

	/**
	 * 设备初始化
	 * 需考虑二次执行的情况
	 */
	public void init(){
		//Logs.getLog(MainSetup.class).infof("设备 %s 初始化完成",device.getName());
	}


	public TDevice getDevice() {
		return device;
	}
	public DeviceType getDeviceType() {
		return deviceType;
	}

	public DeviceProperty getDeviceProperty(String code){
		return ds.getProperty(device,code);
	}


	public TCommunication getCommunModel(){
		return bs.getTCache(TCommunication.class,device.getCommunication());
	}

	public CommunicationInfc getCommunication(){
		CommuService commuService = ioc.get(CommuService.class);
		return commuService.getInstance(device.getCommunication());
	}



	//设置参数,并保存
	public void setParam(String field,String val){
		TParam c = new TParam();
		c.setType(1);
		c.setCid(device.getId());
		c.setCode(field);
		c.setValue(val);
		inject(field,val);
		cs.saveConfigParams(c);
	}

	public void initTable(Class<?> klass){
		initTable(klass.getPackage().getName());
	}

	public void initTable(String pack){
		ioc.get(BaseService.class).initTable(pack);
	}

	/**
	 * 根据名称执行设备方法
	 * @param method 方法名称
	 * @param arg	方法参数，逗号分隔
	 * @param action	对象参数，被执行方法参数名必须为action 参数名为 pid，值为空的将使用ActionChain的ID
	 * @return 所执行的方法返回值
	 */
	public Object invoke(String method, String arg, CommonAction action){
		Method m = getMethod(method);
		if(m == null)
			throw Lang.makeThrow("设备 %s 没有方法 %s",device.getName(),method);
		return invoke(m,arg,action);
	}


	public Object invoke(Method method,String arg,CommonAction action){
		List<String> pname = getParamNames(method);
		Map<String,Object> map = new HashMap<>();
		if(Strings.isNotBlank(arg)){
			String[] args = arg.split(",");
				for (int i=0;i<args.length && i < pname.size();i++){
					String a = args[i];
					map.put(pname.get(i),a);
					if(a.indexOf("#") == 0){
						map.put(pname.get(i),action.evalEl(Strings.removeFirst(a)));
					}
				}
		}
		map.put(CommonAction.class.getName(),action);
		Object pid2 = map.get("pid");
		if(pid2 == null || Strings.isBlank(pid2.toString()))
			map.put("pid",action.getChain().getId());
		return invoke(method.getName(),map);
	}

	public Object invoke(String method,Map<String,Object> arg){
		Method m = getMethod(method);
		if(m == null)
			throw Lang.makeThrow("设备:%d[%s] 没有找到方法:%s",device.getId(),device.getName(),method);

		return invoke(m,arg);
	}

	@Override
	public void filter(InterceptorChain chain) throws Throwable {
		BaseDevice callDev = (BaseDevice) chain.getCallingObj();
		Long devId = callDev.device.getId();
		Method method = chain.getCallingMethod();

		SysTrigger do1 = bs.getTCacheFirst(SysTrigger.class,v->
				devId.equals(v.getDeviceId()) &&
				("M-" + method.getName()).equals(v.getMember()) &&
				v.getPhase() == PhaseEnum.BEFORE);
		Map<String,Object> param = new HashMap<>();
		param.put("arg",chain.getArgs());
		param.put("bd",callDev);
		if(do1 != null){
			Object r = BaseUtils.runWorkflow(do1,param);
			//r.getClass().isAssignableFrom(method.getReturnType())
			if(r != null){
				if(r != PhaseEnum.SKIP)
					chain.setReturnValue(r);
				return;
			}
		}

		chain.doChain();

		SysTrigger do2 = bs.getTCacheFirst(SysTrigger.class,v->
				devId.equals(v.getDeviceId()) &&
				("M-" + method.getName()).equals(v.getMember()) &&
				v.getPhase() == PhaseEnum.AFTER);
		if(do2 != null){
			param.put("re",chain.getReturn());
			BaseUtils.runWorkflow(do2,param);
		}
	}

	public Object invoke(Method m,Map<String,Object> arg){
		Parameter[] p = m.getParameters();
		Object[] o = new Object[p.length];
		List<String> pname = getParamNames(m);
		for(int i=0;i<p.length;i++){
			String param = pname.get(i);
			//Lang.getTypeClass(p[i].getParameterizedType());
			Class<?> paramClass = p[i].getType();
			if(paramClass.equals(CommonAction.class)){
				o[i] = arg.get(CommonAction.class.getName());
			}else{//有参数的
				Object argVal = arg.get(param);
				if(argVal == null || Strings.isBlank(argVal.toString())){
					AoReflect ao = p[i].getAnnotation(AoReflect.class);
					if(ao != null && ao.type() == AstEnum.param){
						o[i] = Castors.me().castTo(arg,paramClass);
					}else if(paramClass.equals(int.class))
						o[i] = 0;
					else if(paramClass.equals(boolean.class))
						o[i] = false;
				}else if(argVal instanceof String){
					String argStr = (String) argVal;
					argStr = argStr.replaceAll("~",",");
					if(paramClass.equals(String.class)){
						o[i] = argStr;
					}else if(Strings.isQuoteBy(argStr,'{','}')){
						o[i] = JSONObject.parseObject(argStr,paramClass);
					}else{
						o[i] = Castors.me().castTo(argStr,paramClass);
					}
				}else{
					o[i] = Castors.me().castTo(argVal,paramClass);
				}

			}

		}

		try {
			m.setAccessible(true);
			return m.invoke(this, o);
		} catch (IllegalAccessException e) {
			throw Lang.makeThrow("设备[%s]方法(%s)无执行权限",device.getName(),m.getName());
		}catch (InvocationTargetException e) {//调用方法内部错误
			Throwable te = e.getTargetException();
			if(te instanceof RuntimeException){
				throw (RuntimeException)te;
			}
			e.printStackTrace();
			String paramStr = Arrays.toString(o);
			throw Lang.makeThrow("设备[%s] 执行%s(%s) 异常:%s",device.getName(),m.getName(),paramStr.substring(1,paramStr.length()-1),te.getMessage());
		}catch (IllegalArgumentException e){
			e.printStackTrace();
			throw Lang.makeThrow("设备[%s] 方法(%s) 参数%s 错误:%s",device.getName(),m.getName(),Arrays.toString(o),e.getMessage());
		}

	}

	public Method getMethod(String method){
		for (Method m : this.getClass().getMethods()) {
			if (m.getName().equals(method)) {
				return m;
			}
		}
		return null;
	}

	public List<String> getParamNames(Method method){
		return MethodParamNamesScaner.getParamNames(this.getClass().getName().contains("$$") ? target.getMethod(method.getName()) : method);
	}

	/**
	 * 注入一个字段
	 */
	public void inject(String field,Object val){
		try {
			Field f = mirror.getField(field);
			if(f == null)
				return;
			if(f.getType().isAssignableFrom(DevData.class)){
				mirror.setValue(this,f,val);
			}else if(val instanceof DevData){
				DevData d = (DevData) val;
				mirror.setValue(this,f,d.getValue());
			}else{
				mirror.setValue(this,f,val);
			}

		}catch (Exception e) {
			log.errorf("设备 %s 注入字段 %s[%s] 出错:%s",device.getName(),field,val,e.getMessage());
		}
	}

	public Object getFieldVal(String field){
		return mirror.getValue(this, field);
	}

	protected List<Command> exec(Object... format){
		StackTraceElement[] ss = Thread.currentThread().getStackTrace();
		String methodName = ss[2].getMethodName();
		Method method = getMethod(methodName);
		DeviceService ds =  ioc.get(DeviceService.class);
		AoReflect ao = method.getAnnotation(AoReflect.class);
		String remark = ao != null ? ao.value() : methodName;
		List<Command> c = ds.buildCommands(device,methodName,remark,format);
		/*if(c.size() == 0)
			throw Lang.makeThrow("设备类型【%s】还未配置 %s 指令",deviceType.getName(),remark);*/
		execCommand(c,format);
		return  c;
	}

	private void execCommand(List<Command> commands,Object... format){
		CommunicationInfc ci = getCommunication();
		if(ci == null){
			StackTraceElement[] ss = Thread.currentThread().getStackTrace();
			log.error(ANSI.COLOR_FORE.red.format("设备["+device.getName()+"]未正确加载通讯，不能执行"+ss[3].getMethodName()+"指令"));
			return;
		}
		TCommunication cu = ci.getCommun();
		if(cu.isListen()){
			commands.forEach(v->v.sendCommand(format));
		}else {
			ioc.get(CommuService.class).addCommand(commands,format);
		}
	}
	/**
	 *	发送消息
	 * @param msg 消息内容
	 * @param isLog 是否记录日志
	 */
	public void sendSocket(String msg,boolean isLog){
		WebsocketRoom.sendDevice(device,msg);
		String classInfo = "";
		if(isLog){
			StackTraceElement[] sts = Thread.currentThread().getStackTrace();
			String className = sts[1].getClassName();
			for(int i = 2;i<sts.length;i++){
				if(!sts[i].getClassName().equals(className)){
					className = sts[i].getClassName();
					classInfo = String.format("%s.%s(%s.java:%d) - ",className,sts[i].getMethodName(),className.substring(className.lastIndexOf(".")+1),sts[i].getLineNumber());
					break;
				}
			}
			Logs.getLog(BaseDevice.class).info(classInfo + msg);
		}

		/*if(device.getParentId()!=null){
			BaseDevice p = ds.getInstance(device.getParentId());
			if(p!=null)
				p.sendSocket(msg,false);
		}*/

	}
	
	public String sendSocket(String msg,Object... o){
		String s = String.format(msg, o);
		sendSocket(s,recordLog);
		return s;
	}
	/*public void sendSocket(MsgData md){
		ioc.get(WebsocketRoom.class).sendMsg("wsroom:d_"+device.getId(),md,true);
	}*/


	/**
	 * 添加字典类型
	 */
	public void putDict(String type,String name){
		if(ioc != null)
			ioc.get(ConfigService.class).getDictType().put(type,name);
	}

	/**
	 * 添加菜单
	 */
	//TODO 未用
	public void addMenu(String name,String url){
		/*SysMenu m = new SysMenu();
		m.setName(name);
		m.setUrl(url);
		m.setDeviceId(device.getId());
		m.setIsPc(1);
		cs.regMenu(m);*/
	}

	/**
	 * 构建菜单项
	 * @param name 菜单名称
	 * @param menu 菜单标识 设备菜单URL以/开始
	 * @param suffix 右键菜单时文件后缀 DEV是设备菜单
	 */
	public BaseExtend.RMenu.RMenuOption buildMenu(String name,String menu,String suffix){
		return new BaseExtend.RMenu.RMenuOption(name,menu,device.getId(),suffix);
	}

	/**
	 * 注册目录
	 */
	public String regDir(String name){
		PathEnum.AppData.addDir(name);
		return "/AppData/"+name;
	}

	/**
	 * 注册服务 ,class必须实现SyserviceInfc
	 */
	@Deprecated
	public void regService(Class<?>... klass) {
		for(Class<?> c : klass){
			AoReflect ao = c.getAnnotation(AoReflect.class);
			ds.getServiceMap().put(c.getName(), ao.value());
		}
	}

	public void notify(Object o){
		try {
			setChanged();
			notifyObservers(o);
		}catch (Throwable e){
			e.printStackTrace();
		}
	}

	public byte[] send(byte[] b){
		if(b == null)
			return null;
		return send(new Command(device,b,""));
	}

	public byte[] send(Command command){
		CommunicationInfc commuInfc = getCommunication();
		commuInfc.sendSocket(CdataEnum.Tx,command.toString());
		TCommunication communication = commuInfc.getCommun();
		byte[] b = commuInfc.send(command);
		String s = b == null ? "null" : (communication.isHex() ? CalcUtil.byteToHex(b).replaceAll("(.{2})", "$1 ") : new String(b));
		commuInfc.sendSocket(CdataEnum.Rx,s);
		return b;
	}

	@Override
	public Map<String,DevData> getDataMap(){
		return dataMap;
	}


	/**
	 * 注意：避免字段的set方法调用到自身
	 * 如果仅到变量不需要注入到字段应该使用DATA.setVal
	 */
	@Override
	@AoReflect("设置值")
	public DevData putData(String code, Object value){
		if(Strings.isBlank(code) || value == null)
			return null;

		//设置值 没有会创建
		DevData data = setVal(code,value);
		Integer stateNow = data.getState();//当前状态

		String pointCode = "dev-"+device.getId()+"-"+code;
		PointService ps = ioc.get(PointService.class);
		PointData pointData = ps.put(pointCode,value);
		if(pointData != null){
			data.setState(pointData.getState());
		}

		DeviceProperty dp = getDeviceProperty(code);
		if(dp != null){
			//属性
			if(Strings.isNotBlank(dp.getDevField())){
				inject(dp.getDevField(),data);
			}
		}



		/*List<DeviceAction> das = bs.getTCache(DeviceAction.class,v-> v.getAlarm() > 0 &&
				Strings.equals(code,v.getAnalysis()) && Strings.equals(v.getDeviceType(),device.getDeviceType()) &&
				(v.getDeviceId() == null || (v.getDeviceId().equals(device.getId()) || v.getDeviceId().equals(device.getParentId())))
		);
		int state = 0;
		for(DeviceAction v : das){
			state = Math.max(state,ds.conditionHolds(v,device));
		}
		data.setState(state);



		boolean isRec = dp.isRecOnEvery();//是否保存历史记录

		//状态改变
		if(data.changedState()){
			if(dp.isRecOnState())
				isRec = true;
			if(state == 2){//报警触发
				//notify(new AlarmData(value+"", "", dp.getName()));
			}
		}
		//数值差异保存
		if(data.changedVal(dp.getRecOnValue())){
			isRec = true;
		}

		//数值改变触发
		if(data.changedVal(dp.getNotifyOnValue())){
			notify(dp);
		}


		if(isRec){
			//saveHistory(attr, value, state);
		}

		//符合条件就联动，和状态没有关系
		linkAction(code);*/
		return data;
	}


	/**
	 * 联动触发执行，注意这不是一个安全的执行操作
	 */
	public void linkAction(String attr){

		List<DeviceAction> daList = bs.getTCache(DeviceAction.class);
		for(int i = 0;i< daList.size();i++){
			DeviceAction v = daList.get(i);
			//System.out.println(v.getSequence()+"---1---"+v.getDeviceType()+v.getValue());
			if(Strings.equals(v.getAnalysis(),attr) && (
					(v.getDeviceId() == null && Strings.equals(device.getDeviceType(), v.getDeviceType())) ||
					(v.getDeviceId() != null && device.getId().equals(v.getDeviceId()))
			)){
				//System.out.println(v.getSequence()+"  └---2---"+v.getDeviceType()+v.getValue());
				Object val = getDataVal(attr);
				DevData data = dataMap.get(attr);
				int state = -2;
				List<TAction> chain = new ArrayList<>();
				if(v.getPid() == null && (v.getGro() == 1 || v.getGro() == 4)){ //上层联动在上层执行
					chain = new ArrayList<>();
					int start = 0;
					int end = 0;
					String evl = null;
					for(int j = i;j<daList.size();j++){
						i++;
						DeviceAction da = daList.get(j);
						chain.addAll(cs.getAction(da));

						boolean b = false;
						List<TDevice> devList = new ArrayList<>();
						if(da.getDeviceId() != null) {
							TDevice dev = bs.getTCache(TDevice.class,da.getDeviceId());
							devList.add(dev);
						}else{
							devList = ds.getDeviceByType(da.getDeviceType());
						}

						for(TDevice dev : devList) {
							int s2 = ds.conditionHolds(da, dev);
							//回差为0表示不计入总状态
							if(da.getHysteresis() == null || da.getHysteresis() != 0)
								state = Math.max(state,s2);
							if(s2 >= 0)
								b = true;
						}

						String c = b+"";
						String symbol = da.getAo() == null ? "||" : da.getAo().getSymbol();

						if(da.getGro() == 1){
							c = "( " + c;
							start ++;
						}else if(da.getGro() == 4){
							c = "(( " + c;
							start += 2;
						}else if(da.getGro() == 3){
							c = c + " )";
							end++;
						}else if(da.getGro() == 5){
							c = c + " ))";
							end += 2;
						}

						if(evl == null){
							evl = c;
						}else{
							evl += " "+ symbol + " " + c;
						}
						//System.out.println(da.getSequence()+"    └---3---"+da.getDeviceType()+da.getValue() + "   "+evl);
						if((da.getGro() == 3 || da.getGro() == 5) && end >= start){
							boolean s = (boolean) SysUtil.jsEval(evl);
							if(!s)
								state = -1;
							break;
						}
					}
					i--;
				}else{
					state = ds.conditionHolds(v,device);
				}

				int stateBefore = Constants.condGroup.computeIfAbsent(v.getId(), d->0);
				if(	  (v.getTrigger() == 0 && state > 0 && stateBefore <= 0)//到符合时
					||(v.getTrigger() == 1 && state <=0 && stateBefore >  0)//到不符合
					||(v.getTrigger() == 2 && state != stateBefore) 		//状态变化
					||(v.getTrigger() == 3 && state > 0) 					//每次符合
					||(v.getTrigger() == 4 && state <=0) 					//每次不符
					|| v.getTrigger() == 5									//每次
				){
					DeviceProperty dp = getDeviceProperty(attr);
					String title = device.getName() + ":" + (dp == null ? attr : dp.getName()) + val + v.getCompare().getName() +v.getValue();
					CommonAction ca = new CommonAction();
					ca.setArg("bd",this);
					ca.setArg("title",title);
					ca.setArg("state",state);
					ca.setArg("value",val);
					ca.setArg("valBefore", data.getPrevVal());
					ca.setArg("stateBefore",stateBefore);
					if(v.getGro() == 0){
						//sendSocket("触发联动->"+title);
						ca.chainRun(v);
					}else{
						ca.chainRun(chain);
					}
				}
				Constants.condGroup.put(v.getId(),state);
			}
		};
	}

	//数据超时
	public boolean isTimeOut(){
		return System.currentTimeMillis() - lastTime > 30 * 1000;
	}
	
	public long getLastTime(){
		return lastTime;
	}

	public void setLastTimeNow(){
		this.lastTime = System.currentTimeMillis();
	}

	public int getAlarmTotal() {
		int i = 0;
		for(DevData d : dataMap.values()){
			if(d.getState() != null && d.getState() > 1)
				i++;
		}
		alarmTotal = i;
		return alarmTotal;
	}

	public CriQueue<Object> getQueue(String key){
		return queueMap.computeIfAbsent(key,v->new CriQueue<>(10));
	}

	public DevData setVal(String key,Object value){
		DevData d = dataMap.computeIfAbsent(key, v->new DevData());
		d.setValue(value);
		d.setTime(System.currentTimeMillis());
		getQueue(key).push(value);
		return d;
	}

	public String toJson(){
		return "\""+ this +"\"";
	}

	/**
	 * 销毁
	 */
	public void destroy(){
		if(ds == null || device == null)
			return;
		ds.removeInstance(device.getId());
		log.info("设备["+device.getId()+"] " + device.getName()+" 已销毁");
	}

}
