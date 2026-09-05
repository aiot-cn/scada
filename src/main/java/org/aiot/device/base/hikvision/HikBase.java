package org.aiot.device.base.hikvision;

import com.sun.jna.Native;
import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.NativeLongByReference;
import org.aiot.device.base.CameraDevice;
import org.aiot.lang.annotation.AoReflect;
import org.aiot.lang.device.HCNetSDK;
import org.aiot.model.enums.ANSI;
import org.aiot.model.enums.AstEnum;
import org.aiot.model.enums.PathEnum;
import org.aiot.model.enums.VarRuntimeEnum;
import org.aiot.model.table.TDevice;
import org.aiot.service.BaseService;
import org.aiot.util.ImgUtil;
import org.aiot.util.SysUtil;
import org.nutz.lang.Lang;
import org.nutz.lang.Strings;
import org.nutz.lang.util.NutMap;
import org.nutz.log.Log;
import org.nutz.log.Logs;

import java.awt.image.BufferedImage;
import java.util.*;

public class HikBase extends CameraDevice {

	Log log = Logs.get();
	static HCNetSDK hCNetSDK;
	static String sdkVersion;
	NativeLong lUserID;

	@AoReflect(value ="端口",type = AstEnum.param)
	private Short port = 8000;

	@AoReflect(value ="布防",type = AstEnum.param)
	private boolean isListen;

	@AoReflect("UserID")
	private Long userID;

	private Long lAlarmHandle;


	private static HCNetSDK.FMSGCallBack fc;//报警回调函数实现
	private static HCNetSDK.FMSGCallBack_Linux fcLinux;

	private HCNetSDK.NET_DVR_DEVICEINFO_V30 m_strDeviceInfo;// 设备信息
	private HCNetSDK.NET_DVR_IPPARACFG_V40 m_strIpparaCfg;	// IP设备资源及IP通道资源配置结构体

	private List<NutMap> chanList;

	static {
		String libPath = PathEnum.lib + "HCNetSDK";
		try{
			SysUtil.addJnaPath(libPath);
			if(Lang.isWin()) {
				hCNetSDK = Native.load("HCNetSDK", HCNetSDK.class);
			}else {
				hCNetSDK = Native.load("hcnetsdk",HCNetSDK.class);
				HCNetSDK.NET_DVR_LOCAL_SDK_PATH struComPath = new HCNetSDK.NET_DVR_LOCAL_SDK_PATH();
				System.arraycopy(libPath.getBytes(), 0, struComPath.sPath, 0, libPath.length());
				struComPath.write();
				hCNetSDK.NET_DVR_SetSDKInitCfg(2, struComPath.getPointer());
			}
			Logs.get().info("hCNetSDK动态连接库加载成功");
			hCNetSDKInit();

			//开启日志
			//hCNetSDK.NET_DVR_SetLogToFile(3,"C:\\SdkLog\\",true);
			//hCNetSDK.NET_DVR_SetLogToFile(true, null, false);//Linux
			//设置连接时间与重连时间
			//hCNetSDK.NET_DVR_SetConnectTime(2000, 1);//对于Windows版本，SDK默认建立连接的超时时间为3000毫秒；对于Linux版本，V5.2.7.2及以上版本，连接超时时间为3500毫秒。
			//hCNetSDK.NET_DVR_SetReconnect(10000, true); //SDK默认启动预览、透明通道和布防的重连功能，重连时间间隔为5秒

			//释放SDK资源
			//hCNetSDK.NET_DVR_Cleanup();

		}catch (UnsatisfiedLinkError e){
			Logs.get().errorf(ANSI.COLOR_FORE.red.format("海康SDK在 " + libPath + " 中未找到"));
		}catch(Throwable e){
			Logs.get().errorf("hCNetSDK加载失败：%s",SysUtil.envInfo());
			e.printStackTrace();
		}
	}

	@Override
	public void init(){
		putDict("hikCode", "海康状态码");

		synchronized (BaseService.class){
			if(hCNetSDK == null)
				return;

			hCNetCallBack();//注册回调函数
			if(isListen){
				Lang.sleep(1000);//不延迟项目会挂
				alarmAction();
			}
		}

	}

	public static boolean hCNetSDKInit(){
		boolean s = hCNetSDK.NET_DVR_Init();
		if(s) {
			int buildVersion  = hCNetSDK.NET_DVR_GetSDKBuildVersion();
			sdkVersion = Strings.toHex(buildVersion, 8);
			Logs.get().info(ANSI.COLOR_FORE.blue.format("hCNetSDK初始化成功，版本:"+sdkVersion));
		}else {
			Logs.get().error("hCNetSDK初始化失败，"+getErrorMsg());
		}
		return s;
	}

	@Override
	public void selfTest(){
		if(login()){
			setLastTimeNow();
			if(isListen)
				alarmAction();
		}

		/*DATA.getDataMap().forEach((k,v)->{
			if(k.indexOf("M") == 0){
				Long t = v.getTime();
				if(t != null && System.currentTimeMillis() - t > 15 * 1000){
					put(k,"0");
				}
			}
		});*/

	}

	public boolean hCNetCallBack(){
		if(fc != null || fcLinux != null)
			return true;

		boolean f;
		if(Lang.isWin()){
			fc = this::FMSGCallBack;
			f = hCNetSDK.NET_DVR_SetDVRMessageCallBack_V30(fc, null);
		}else{
			fcLinux = this::FMSGCallBack;
			f = hCNetSDK.NET_DVR_SetDVRMessageCallBack_V30(fcLinux, null);
		}
		log.info("hCNetSDK回调设置 "+(f ? "成功" : "失败"));
		return f;
	}

	public String getDevInfo(){
		return device.getName() + "["+getHost()+"] ";
	}

	public static String getErrorMsg(){
		int code = hCNetSDK.NET_DVR_GetLastError();
		if(code == 3){
			return "hCNetSDK还未初始化";
		}
		return "error ["+ code + "]：" + hCNetSDK.NET_DVR_GetErrorMsg(new NativeLongByReference(new NativeLong(code)));
	}

	public boolean isLogin(){
		return hCNetSDK != null && lUserID != null && lUserID.longValue() > -1 && lUserID.longValue() < 64;
	}

	@AoReflect("SDK释放")
	public boolean cleanup(){
		boolean b = hCNetSDK.NET_DVR_Cleanup();
		if(b){
			log.info("hCNetSDK已释放");
			sdkVersion = null;
			lUserID = null;
		}else{
			log.errorf("%s %s:%d hCNetSDK释放失败(%s) ",device.getName(),getHost(),port,getErrorMsg());
		}
		return b;
	}

	/*************************************************
	 * 函数描述: 注册登录设备
	 *************************************************/
	public boolean login() {
		if(hCNetSDK == null){
			log.error("HikSDK未加载");
			return false;
		}
		if(sdkVersion == null){
			if(!hCNetSDKInit()){
				return false;
			}

		}
		if(System.currentTimeMillis() < (long) VarRuntimeEnum.debugTime.val()){//如果是调试模式，直接失败
			return false;
		}
		//已登录 登录失败lUserID有可能值为 4294967295
		if (isLogin() || loginV30()) {
			setLastTimeNow();
			return true;
		}

		return false;

	}

	@AoReflect("注销")
	public void logout(){
		if(isLogin()){
			if(hCNetSDK.NET_DVR_Logout_V30(lUserID)){
				lUserID = null;
			}else{
				log.errorf("%s 注销失败(%s) login:%s:%d",device.getName(),getErrorMsg(),getHost(),port);
			}

		}
	}

	@AoReflect(value="登录")
	public boolean loginV30(){
		// 注册
		m_strDeviceInfo = new HCNetSDK.NET_DVR_DEVICEINFO_V30();
		lUserID = hCNetSDK.NET_DVR_Login_V30(getHost(), port, user, password, m_strDeviceInfo);
		userID = lUserID.longValue();

		//ALARM = new long[m_strDeviceInfo.byAlarmInPortNum];
		if(isLogin()){
			log.infof("%s 登录成功 login:%s lUserID:%s",device.getName(),getHost(),lUserID);
			loginCallBack();
		}else{
			log.errorf("%s 登录失败(%s) login:%s:%d %s %s",device.getName(),getErrorMsg(),getHost(),port,user,password);
		}

		return isLogin();

	}

	@AoReflect("报警布防")
	public boolean alarmAction(){
		if(!login()){
			return false;
		}
		if(lAlarmHandle != null && lAlarmHandle.intValue() > -1){
			return true;
		}

		lAlarmHandle = hCNetSDK.NET_DVR_SetupAlarmChan_V30(lUserID).longValue();//建立报警上传通道，获取报警等信息。
		if(lAlarmHandle < 0){
			log.errorf("%s 布防失败 %s",getHost(), getErrorMsg());
			return false;
		}else{
			putData("DO1", "1");
			log.infof( "%s[%s]报警上传通道:%d",device.getName(),getHost(),lAlarmHandle);
			return true;
		}

	}


	@AoReflect("取消布防")
	public void closeAlarmChan(){
		if(!login()){
			log.error("布防失败，登录异常");
			return;
		}
		if(lAlarmHandle != null){
			boolean b = hCNetSDK.NET_DVR_CloseAlarmChan_V30(new NativeLong(lAlarmHandle));
			if(b){
				log.info(getHost() + " 已撤销布防通道:"+lAlarmHandle);
			}else{
				log.errorf("%s 撤销布防失败 %s",getHost(), getErrorMsg());
			}
		}
	}

	@AoReflect(value="通道信息")
	public List<NutMap> getChannelInfo(){
		if(chanList != null)
			return chanList;

		if(!login())
			return null;


		//IP设备资源及IP通道资源配置结构体。
		m_strIpparaCfg = new HCNetSDK.NET_DVR_IPPARACFG_V40();
		boolean bRet = getConfig(HCNetSDK.NET_DVR_GET_IPPARACFG_V40,0,m_strIpparaCfg);
		if(!bRet)
			return null;

		chanList = new ArrayList<>();
		//模拟通道
		for (int iChannum = 0; iChannum < m_strDeviceInfo.byChanNum; iChannum++) {
			if (!bRet || m_strIpparaCfg.byAnalogChanEnable[iChannum] == 1) {
				int c = iChannum + m_strDeviceInfo.byStartChan;
				NutMap m = new NutMap();
				m.put("name", "Camera" + c);
				m.put("no", c);
				m.put("type",1);
				chanList.add(m);
			}
		}

		//支持IP通道
		if (bRet){
			for (int iChannum = 0; iChannum < m_strDeviceInfo.byIPChanNum; iChannum++) {

				HCNetSDK.NET_DVR_STREAM_MODE streamMode = m_strIpparaCfg.struStreamMode[iChannum];//取流模式
				HCNetSDK.NET_DVR_GET_STREAM_UNION streamUnion = streamMode.uGetStream;//取流模式联合体
				int c = iChannum + m_strIpparaCfg.dwStartDChan;
				if (streamMode.byGetStreamType == 0) {
					streamUnion.setType(HCNetSDK.NET_DVR_IPCHANINFO.class);
					streamUnion.read();
					if (streamUnion.struChanInfo.byEnable == 1) {
						HCNetSDK.NET_DVR_PICCFG_V30 dvrPiccfg = new HCNetSDK.NET_DVR_PICCFG_V30();
						getConfig(1002, c, dvrPiccfg);
						NutMap m = new NutMap();
						m.put("no", iChannum+1);
						m.put("type",2);
						m.put("name", new String(dvrPiccfg.sChanName).trim());
						chanList.add(m);
					}
				} else if (streamMode.byGetStreamType == 6) {
					streamUnion.setType(HCNetSDK.NET_DVR_IPCHANINFO_V40.class);
					streamUnion.read();
					if (streamUnion.struIPChan.byEnable == 1) {

					}
				}

			}

		}

		return chanList;
	}

	public boolean getConfig(int cmd,int channel,Structure structure){
		structure.write();
		boolean b =  hCNetSDK.NET_DVR_GetDVRConfig(lUserID, cmd, new NativeLong(channel),structure.getPointer(), structure.size(), new IntByReference());
		if(b){
			structure.read();
		}else{
			log.errorf("%s 通道[%d] 执行 %d 失败  %s",getHost(),channel,cmd,getErrorMsg());
		}
		return b;
	}

	@AoReflect("校时")
	public void setTimecfg(){
		if(!login())
			return;
		HCNetSDK.NET_DVR_TIME dvrTime = date2hkTime(new Date());
		dvrTime.write();
		boolean b = hCNetSDK.NET_DVR_SetDVRConfig(lUserID, 119, new NativeLong(0), dvrTime.getPointer(), dvrTime.size());
		if(!b)
			log.errorf("%s 校时失败 %s",getHost(), getErrorMsg());
	}

	public void getff(){
		//hCNetSDK.NET_dVR_G
	}

	public static HCNetSDK.NET_DVR_TIME date2hkTime(Date time) {
		HCNetSDK.NET_DVR_TIME dvrTime = new HCNetSDK.NET_DVR_TIME();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(time);
		dvrTime.dwYear = calendar.get(Calendar.YEAR);
		dvrTime.dwMonth = calendar.get(Calendar.MONTH)+1;
		dvrTime.dwDay = calendar.get(Calendar.DAY_OF_MONTH);
		dvrTime.dwHour = calendar.get(Calendar.HOUR_OF_DAY);
		dvrTime.dwMinute = calendar.get(Calendar.MINUTE);
		dvrTime.dwSecond = calendar.get(Calendar.SECOND);
		return dvrTime;
	}

	public static Date hkTime2Date(HCNetSDK.NET_DVR_TIME dvrTime) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR,dvrTime.dwYear);
		calendar.set(Calendar.MONTH,dvrTime.dwMonth - 1);
		calendar.set(Calendar.DAY_OF_MONTH,dvrTime.dwDay);
		calendar.set(Calendar.HOUR_OF_DAY,dvrTime.dwHour);
		calendar.set(Calendar.MINUTE,dvrTime.dwMinute);
		calendar.set(Calendar.SECOND,dvrTime.dwSecond);
		return calendar.getTime();
	}

	public static HCNetSDK.NET_DVR_TIME_EX date2hkTimeEx(Date time) {
		HCNetSDK.NET_DVR_TIME_EX dvrTime = new HCNetSDK.NET_DVR_TIME_EX();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(time);
		dvrTime.wYear = (short) calendar.get(Calendar.YEAR);
		dvrTime.byMonth = (byte) (calendar.get(Calendar.MONTH)+1);
		dvrTime.byDay = (byte) calendar.get(Calendar.DAY_OF_MONTH);
		dvrTime.byHour = (byte) calendar.get(Calendar.HOUR_OF_DAY);
		dvrTime.byMinute = (byte) calendar.get(Calendar.MINUTE);
		dvrTime.bySecond = (byte) calendar.get(Calendar.SECOND);
		return dvrTime;
	}

	public static Date hkTimeEx2Date(HCNetSDK.NET_DVR_TIME_EX dvrTime) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR,dvrTime.wYear);
		calendar.set(Calendar.MONTH,dvrTime.byMonth - 1);
		calendar.set(Calendar.DAY_OF_MONTH,dvrTime.byDay);
		calendar.set(Calendar.HOUR_OF_DAY,dvrTime.byHour);
		calendar.set(Calendar.MINUTE,dvrTime.byMinute);
		calendar.set(Calendar.SECOND,dvrTime.bySecond);
		return calendar.getTime();
	}


	/******************************************************************************
	 * FMSGCallBack 报警信息回调函数
	 * 0x4000移动侦测、视频丢失、遮挡、IO信号量等报警信息 0x4991火点检测报警信息 0x1150声音报警信息 0x1102行为分析信息 0x5212温度报警信息 0x4012GIS信息 0x5002门禁主机报警信息
	 ******************************************************************************/

	public void FMSGCallBack(NativeLong lCommand, HCNetSDK.NET_DVR_ALARMER pAlarmer, HCNetSDK.RECV_ALARM pAlarmInfo,int dwBufLen, Pointer pUser){
		String ip = new String(pAlarmer.sDeviceIP).trim();
		TDevice dev = bs.getTCacheFirst(TDevice.class, v->ip.equals(v.getAddress()));
		HikBase baseDev = (HikBase) ds.getInstance(dev.getId());
		int cNo = lCommand.intValue();//报警类型
		String cNoHex = Integer.toHexString(cNo);
		//Map<String,String> codeMap = bs.getTCacheMap(SysDict.class, v->v.getType().equals("hikCode"), SysDict::getValue,SysDict::getName);
		//sendSocket("收到 %s[%s] -> 0x%s %s",dev.getName(),ip,cNoHex,codeMap.computeIfAbsent(cNoHex,v->""));
		baseDev.msgCallBack(cNo,pAlarmInfo);
	}

	public void msgCallBack(int type,HCNetSDK.RECV_ALARM pAlarmInfo){

	}

	public void loginCallBack(){

	}

	public byte[] errImgByte(){
		String msg = getErrorMsg();
		BufferedImage buf = ImgUtil.createText(msg);
		ImgUtil.addText(buf,getHost() + " " + device.getName());
		return ImgUtil.toBytes(buf,"");
	}


}
