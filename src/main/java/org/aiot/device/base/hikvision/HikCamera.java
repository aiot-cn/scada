package org.aiot.device.base.hikvision;

import com.alibaba.fastjson.JSONObject;
import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;
import org.aiot.infc.device.BaseExtend;
import org.aiot.infc.device.CameraInfc;
import org.aiot.lang.Command;
import org.aiot.lang.annotation.AoReflect;
import org.aiot.lang.device.HCNetSDK;
import org.aiot.model.enums.AstEnum;
import org.aiot.model.enums.DeviceRoleEnum;
import org.aiot.model.enums.PathEnum;
import org.aiot.model.table.TPreset;
import org.aiot.util.FileUtil;
import org.aiot.util.ImgUtil;
import org.nutz.lang.Files;
import org.nutz.lang.Lang;
import org.nutz.lang.Strings;
import org.nutz.lang.util.NutMap;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@AoReflect(value = "海康摄像头",deviceRole = DeviceRoleEnum.VIDEO)
public class HikCamera extends HikBase implements CameraInfc,BaseExtend.RMenu{
	
	@AoReflect(value ="热成像通道",type = AstEnum.param)
	private int thermalStart = 2;

	@AoReflect(value ="云台通道",type = AstEnum.param)
	private int pztChannel = 1;

	@AoReflect(value ="云台类型",select = "0:默认,1:重载",type = AstEnum.param)
	private int pztType;

	@AoReflect(value ="web地址",type = AstEnum.param)
	private String web;

	@AoReflect(value ="外网地址",type = AstEnum.param)
	private String net;

	@AoReflect(value ="外网限时(S)",type = AstEnum.param)
	private Integer netTimeOut = 60;

	@AoReflect(value ="图像宽度",type = AstEnum.param)
	private Integer imgWidth = 1920;
	@AoReflect(value ="图像高度",type = AstEnum.param)
	private Integer imgHeight = 1080;

	@AoReflect(value ="测温规则",type = AstEnum.param)
	private Integer tempRuleId;

	//---------------------------------------------------------------------------
	private HCNetSDK.NET_DVR_IPPARACFG_V40 m_strIpparaCfg;	// IP设备资源及IP通道资源配置结构体

	private Map<Integer, Long> channelMap = new HashMap<>();//通道预置点时间
	private NativeLong handleThermometry = new NativeLong(0);



	@Override
	public void init(){
		super.init();
		addMenu(device.getName(),"/plugin/hikvision/camera");
	}

	@Override
	public void selfTest(){
		super.selfTest();
		channelMap.forEach((k,v)->{
			if(System.currentTimeMillis() > v){
				PTZPresetGo(1, k,null);
				channelMap.remove(k);
			}
		});
	}


	@Override
	public void msgCallBack(int type,HCNetSDK.RECV_ALARM pAlarmInfo){
		String hex = Integer.toHexString(type);
		putData("M"+hex,"1");
		//移动侦测、视频丢失、遮挡、IO信号量等报警信息(V3.0以上版本支持的设备)
		if(type == HCNetSDK.COMM_ALARM_V30){
			String sAlarmType;

			HCNetSDK.NET_DVR_ALARMINFO_V30 strAlarmInfoV30 = new HCNetSDK.NET_DVR_ALARMINFO_V30();
			strAlarmInfoV30.write();
			Pointer pInfoV30 = strAlarmInfoV30.getPointer();
			pInfoV30.write(0, pAlarmInfo.RecvBuffer, 0, strAlarmInfoV30.size());
			strAlarmInfoV30.read();
			sendSocket("报警类型：%d 报警输入端口：%d",strAlarmInfoV30.dwAlarmType,strAlarmInfoV30.dwAlarmInputNumber);
			switch (strAlarmInfoV30.dwAlarmType) {
				case 0:
					int AIN = strAlarmInfoV30.dwAlarmInputNumber;
					putData("M"+AIN,"1");
					break;
				case 1:
					sAlarmType = new String("硬盘满");
					break;
				case 2:
					sAlarmType = new String("信号丢失");
					break;
				case 3: //移动侦测
					break;
				case 4:
					sAlarmType = new String("硬盘未格式化");
					break;
				case 5:
					sAlarmType = new String("读写硬盘出错");
					break;
				case 6:
					sAlarmType = new String("遮挡报警");
					break;
				case 7:
					sAlarmType = new String("制式不匹配");
					break;
				case 8:
					sAlarmType = new String("非法访问");
					break;
			}
		}
	}

	@Override
	public void loginCallBack(){
		if(tempRuleId != null)
			startThermometry(tempRuleId);
	}

	/**
	 * 抓图保存到文件
	 * 双通道摄像头通常 1可见光 2红外
	 * 路径如果为空，则以抓拍路径，时间戳保存
	 * @return
	 */

	@Override
	@AoReflect(value="抓拍")
	public synchronized  File getPicture(Integer lChannel,File file){
		if(!login()){
			return null;
		}
		if(file == null)
			file = PathEnum.image.getFile(getHost().replaceAll("\\.","_")+"_"+lChannel+".jpg");
		Files.createDirIfNoExists(file.getParent());

		HCNetSDK.NET_DVR_JPEGPARA lpJpegPara = new HCNetSDK.NET_DVR_JPEGPARA();//JPEG图像参数
		lpJpegPara.wPicQuality = 0;
		lpJpegPara.wPicSize = 0xff;
		boolean s =  hCNetSDK.NET_DVR_CaptureJPEGPicture(lUserID, new NativeLong(lChannel), lpJpegPara, file.getAbsolutePath());
		String pathName = FileUtil.toPath(file);
		if(s){
			sendSocket(getDevInfo()+"抓拍[通道"+lChannel+"] "+pathName,true);
			return file;
		}else{
			sendSocket(getDevInfo()+"抓拍[通道"+lChannel+"] "+pathName +" 失败:"+getErrorMsg(),true);
		}
		return null;
	}

	@Override
	@AoReflect(value="通道图像")
	public BufferedImage getImage(Integer channel) {
		File file = getPicture(channel,null);
		return ImgUtil.read(file);
	}


	@Override
	@AoReflect(value = "录制视频",type = AstEnum.command)
	public File recordVideo(Integer lChannel,int second,File file){
		if(file == null)
			file = PathEnum.video.getFile(System.currentTimeMillis()+".mp4");
		Command command = exec(getRtspUrl().split(";")[0],second,file.getAbsolutePath()).get(0);
		return file;
	}

	@Override
	@AoReflect(value=" 获取倍数")
	public Float getMultiple(Integer channel){
		HCNetSDK.NET_DVR_PTZPOS p = getPTZinfo(channel);
		if(p == null){
			return null;
		}
		return p.wZoomPos / 10f;
	}

	@Override
	@AoReflect(value="设置倍数")
	public boolean setMultiple(Integer channel,float multiple){
		return setPTZ(channel, (short)4, (short) 0, (short) 0 , (short)(multiple * 10));
	}

	@Override
	@AoReflect("获取PTZF")
	public TPreset getPTZF(Integer channel) {
		if(channel == null)
			channel = pztChannel;
		if(pztType == 1){
			HCNetSDK.NET_PTZ_INFO info = ptzExinfo(channel);
			return new TPreset(info.fPan,info.fTilt,info.fZoom,info.dwFocus);
		}
		HCNetSDK.NET_DVR_PTZPOS ptz = getPTZinfo(pztChannel);
		HCNetSDK.NET_DVR_FOCUSMODE_CFG focusmodeCfg = getFocusModeCfg(pztChannel);
		if(ptz == null || focusmodeCfg == null)
			return null;
		return new TPreset(ptz.wPanPos/10f,ptz.wTiltPos/10f,ptz.wZoomPos/10f,focusmodeCfg.dwFocusPos);
	}

	@Override
	@AoReflect("到PTZF")
	public boolean toPTZF(Float pan, Float tilt,Float zoom,Integer focus,Integer channel){
		if(channel == null)
			channel = pztChannel;

		if(pztType == 1){
			HCNetSDK.NET_PTZ_INFO info = ptzExinfo(channel);
			if(pan == null)
				pan = info.fPan;
			if(tilt == null)
				tilt = info.fTilt;
			if(zoom == null)
				zoom = info.fZoom;
			if(focus == null)
				focus = info.dwFocus;

			boolean b =  ptzEx(pan,tilt,zoom,focus,channel);
			if(!b)
				return false;
			for(int i = 0;i < 30;i ++){
				Lang.sleep(1000);
				info = ptzExinfo(channel);
				if(Math.abs(info.fPan  - pan ) <= 1 && Math.abs(info.fTilt   - tilt ) <= 1 &&
				   Math.abs(info.fZoom - zoom) <= 1 && Math.abs(info.dwFocus - focus) <= 1){
					return true;
				}
			}
			return false;
		}

		boolean b = true;
		if(pan != null || tilt != null || zoom != null){
			b = false;
			HCNetSDK.NET_DVR_PTZPOS ptz = getPTZinfo(pztChannel);
			short p = pan  == null ? ptz.wPanPos  : (short) (pan  * 10);
			short t = tilt == null ? ptz.wTiltPos : (short) (tilt * 10);
			short z = zoom == null ? ptz.wZoomPos : (short) (zoom * 10);
			if(!setPTZ(channel, (short) 1, p, t ,z))
				return false;

			for(int i=0;i<30;i++){
				Lang.sleep(1000);
				ptz = getPTZinfo(pztChannel);
				if(Math.abs(ptz.wPanPos-p) <= 1 && Math.abs(ptz.wTiltPos- t) <= 1 && Math.abs(ptz.wZoomPos - z) <=1){
					b = true;
					break;
				}
			}
		}

		if(!b)
			return false;

		if(focus == null)
			return true;

		HCNetSDK.NET_DVR_FOCUSMODE_CFG cfg = getFocusModeCfg(channel);
		if(cfg == null)
			return false;
		cfg.byFocusMode = (byte) 1;
		cfg.byFocusDefinitionDisplay = 1;
		cfg.dwFocusPos = focus;
		cfg.write();
		if(!setFocusModeCfg(channel, cfg))
			return false;
		for(int i = 0; i<10;i++){
			Lang.sleep(1000);
			cfg = getFocusModeCfg(pztChannel);
			if(cfg == null)
				return false;
			if(Math.abs(cfg.dwFocusPos - focus) <= 15) {
				Lang.sleep(500);
				return true;
			}
				
		}
		return false;
	}

	public void waitStopPTZF(Integer channel){
		TPreset t = getPTZF(channel);
		for(int i = 0;i<60;i++){
			Lang.sleep(500);
			TPreset t2 = getPTZF(channel);
			if(t== null || t2 == null || t2.equals(t))
				return;
			t = t2;
		}
	}

	/**
		 *
		 * @param channel 通道号
		 * @param dwPTZPresetCmd 8设置预置点 9清除预置点 39转到预置点
		 * @param dwPresetIndex 预置点编号
		 */
		@AoReflect("预置点操作")
		public synchronized  boolean PTZPreset(@AoReflect("通道")int channel,
								 @AoReflect(value="操作",select = "8:设置,9:清除,39:调用")int dwPTZPresetCmd,
								 @AoReflect("编号")int dwPresetIndex){
			if(!login())
			 return false;

			boolean r =  hCNetSDK.NET_DVR_PTZPreset_Other(lUserID, new NativeLong(channel),dwPTZPresetCmd,dwPresetIndex);
			if(r){
				if(dwPTZPresetCmd == 39){
					putData("preset-"+channel,dwPresetIndex+"");
					log.info(getDevInfo()+"调用预置点："+dwPresetIndex);
					waitStopPTZF(channel);
				}
			}else{
				log.error(getDevInfo()+ "预置点cmd:"+dwPTZPresetCmd+" channel:"+channel+" index"+dwPresetIndex + "Error:"+getErrorMsg());
			}
			return r;
		}

		@AoReflect(value="调用预置点")
		public boolean PTZPresetGo(int index, Integer channel, Integer duration){
			if(channel == null)
				channel = 1;
			if(duration != null)
				channelMap.put(channel,System.currentTimeMillis()+duration*1000);
			return PTZPreset(channel,39,index);
		}




		//获取云台信息
		@AoReflect(value="云台信息")
		public HCNetSDK.NET_DVR_PTZPOS getPTZinfo(int lChannel){
			if(!login()){
				return null;
			}

			HCNetSDK.NET_DVR_PTZPOS ptz = new HCNetSDK.NET_DVR_PTZPOS();
			ptz.write();
			IntByReference ibrBytesReturned = new IntByReference();
			boolean s = hCNetSDK.NET_DVR_GetDVRConfig(lUserID, HCNetSDK.NET_DVR_GET_PTZPOS, new NativeLong(lChannel), ptz.getPointer(), ptz.size(), ibrBytesReturned);

			if(s){
				ptz.read();
				ptz.wPanPos = Short.parseShort(Strings.num2hex(ptz.wPanPos));
				ptz.wTiltPos = Short.parseShort(Strings.num2hex(ptz.wTiltPos));
				if(ptz.wTiltPos > 3000){
					ptz.wTiltPos = (short) (ptz.wTiltPos - 3600);
				}
				ptz.wZoomPos = Short.parseShort(Strings.num2hex(ptz.wZoomPos));
			}else {
				log.errorf("获取 %s[%d] PTZ信息失败  %s",getHost(),lChannel,getErrorMsg());
				return null;
			}

			return ptz;
		}

		/**
		 * 设置IP快球PTZ参数
		 * @param lChannel 通道号
		 * @param wAction 操作类型，仅在设置时有效。1-定位PTZ参数，2-定位P参数，3-定位T参数，4-定位Z参数，5-定位PT参数
		 * @param wPanPos P参数（水平参数）
		 * @param wTiltPos T参数（垂直参数）
		 * @param wZoomPos Z参数（变倍参数）
		 * @return
		 */
		@AoReflect(value="云台设置")
		public synchronized  boolean setPTZ(int lChannel,short wAction,short wPanPos,short wTiltPos,short wZoomPos){
			HCNetSDK.NET_DVR_PTZPOS ptz = new HCNetSDK.NET_DVR_PTZPOS();
			ptz.wAction = wAction;
			ptz.wPanPos = Short.parseShort(wPanPos+"", 16);
			if(wTiltPos < 0)
				wTiltPos += 3600;
			ptz.wTiltPos = Short.parseShort(wTiltPos+"", 16);
			ptz.wZoomPos = Short.parseShort(wZoomPos+"", 16);
			ptz.write();
			return setPTZ(lChannel,ptz);
		}

		public boolean setPTZ(int lChannel, HCNetSDK.NET_DVR_PTZPOS ptz){
			if(!login()){
				return false;
			}
			boolean s = hCNetSDK.NET_DVR_SetDVRConfig(lUserID, HCNetSDK.NET_DVR_SET_PTZPOS, new NativeLong(lChannel), ptz.getPointer(), ptz.size());
			ptz.read();
			if(s){
				log.infof(getDevInfo()+"设置PTZ(%d) P:%d T:%d Z:%d",ptz.wAction,ptz.wPanPos,ptz.wTiltPos,ptz.wZoomPos);
			}else {
				log.infof(getDevInfo()+"设置PTZ(%d) P:%d T:%d Z:%d 失败：%d",ptz.wAction,ptz.wPanPos,ptz.wTiltPos,ptz.wZoomPos,getErrorMsg());
			}
			return s;
		}

		@AoReflect(value="设置水平角度")
		public boolean setPosPan(float pan){
			return setPTZ(1, (short)2, (short) (pan * 10), (short) 0 , (short)0);
		}

		@AoReflect(value="设置垂直角度")
		public boolean setPosTilt(float tilt){
			return setPTZ(1, (short)3, (short) 0, (short)(tilt * 10), (short)0);
		}



		@AoReflect(value="PTZ设置")
		public synchronized  boolean ptzEx(float pan,float tilt,float zoom,int focus,Integer channel){
			if(!login())
				return false;
			if(channel == null)
				channel = pztChannel;

			HCNetSDK.NET_PTZ_INFO ptz = new HCNetSDK.NET_PTZ_INFO();
			ptz.fPan = pan;
			ptz.fTilt = tilt;
			ptz.fZoom = zoom;
			ptz.dwFocus = focus;
			ptz.write();

			HCNetSDK.NET_DVR_PTZABSOLUTEEX_CFG ptzCfg = new HCNetSDK.NET_DVR_PTZABSOLUTEEX_CFG();
			ptzCfg.dwFocalLen = 100000;
			ptzCfg.fHorizontalSpeed = 1000;
			ptzCfg.fVerticalSpeed = 1000;
			ptzCfg.byZoomType = 0;
			ptzCfg.struPTZCtrl = ptz;
			ptzCfg.dwSize = ptzCfg.size();
			ptzCfg.write();

			HCNetSDK.NET_DVR_COND_INT netDvrCondInt = new HCNetSDK.NET_DVR_COND_INT();
			netDvrCondInt.dword = channel;
			netDvrCondInt.write();

			HCNetSDK.NET_DVR_STD_CONFIG stdConfig = new HCNetSDK.NET_DVR_STD_CONFIG();
			stdConfig.lpCondBuffer = netDvrCondInt.getPointer();
			stdConfig.dwCondSize = netDvrCondInt.size();
			stdConfig.lpInBuffer = ptzCfg.getPointer();
			stdConfig.dwInSize = ptzCfg.size();
			stdConfig.write();
			boolean s =  hCNetSDK.NET_DVR_SetSTDConfig(lUserID,6697,stdConfig);
			if(s){
				log.errorf(getDevInfo()+"设置PTZ_EX P:%s T:%s Z: %s F:%s",pan+"",tilt+"",zoom+"",focus+"");
			}else{
				log.errorf(getDevInfo()+"设置PTZ_EX P:%s T:%s Z: %s F:%s 失败:%s",pan+"",tilt+"",zoom+"",focus+"",getErrorMsg());
			}
			return s;
		}

	@AoReflect(value="PTZ信息Ex")
	public HCNetSDK.NET_PTZ_INFO ptzExinfo(Integer channel){
		if(!login()){
			return null;
		}
		if(channel == null)
			channel = pztChannel;

		HCNetSDK.NET_PTZ_INFO ptz = new HCNetSDK.NET_PTZ_INFO();
		ptz.write();

		HCNetSDK.NET_DVR_PTZABSOLUTEEX_CFG ptzCfg = new HCNetSDK.NET_DVR_PTZABSOLUTEEX_CFG();
		ptzCfg.dwSize = ptzCfg.size();
		ptzCfg.struPTZCtrl = ptz;
		ptzCfg.write();

		HCNetSDK.NET_DVR_COND_INT netDvrCondInt = new HCNetSDK.NET_DVR_COND_INT();
		netDvrCondInt.dword = channel;
		netDvrCondInt.write();

		HCNetSDK.NET_DVR_STD_CONFIG stdConfig = new HCNetSDK.NET_DVR_STD_CONFIG();
		stdConfig.lpCondBuffer = netDvrCondInt.getPointer();
		stdConfig.dwCondSize = netDvrCondInt.size();
		stdConfig.lpOutBuffer  = ptzCfg.getPointer();
		stdConfig.dwOutSize  = ptzCfg.size();
		stdConfig.write();

		boolean s =  hCNetSDK.NET_DVR_GetSTDConfig(lUserID,6696,stdConfig);
		if(s){
			stdConfig.read();
			ptzCfg.read();
			ptz.read();
		}else{
			log.errorf("获取 %s PTZ_EX失败:%s",getHost(),getErrorMsg());
			return null;
		}

		return ptz;
	}

		/**
		 * 云台图像区域选择放大或缩小
		 */
		@AoReflect(value="3D定位-区域")
		public synchronized  boolean NET_DVR_PTZSelZoomIn_EX(int xTop,int yTop ,int xBottom,int yBottom ){
			xTop = Math.min(xTop, imgWidth);
			yTop = Math.min(yTop, imgHeight);
			xBottom = Math.min(xBottom, imgWidth);
			yBottom = Math.min(yBottom, imgHeight);
			if(!login()){
				return false;
			}
			HCNetSDK.NET_DVR_POINT_FRAME pf = new HCNetSDK.NET_DVR_POINT_FRAME();
			pf.xTop = xTop * 255 / imgWidth;
			pf.yTop = yTop * 255 / imgHeight;
			pf.xBottom = xBottom * 255 / imgWidth;
			pf.yBottom = yBottom * 255 / imgHeight;
			boolean s = hCNetSDK.NET_DVR_PTZSelZoomIn_EX(lUserID, new NativeLong(1), pf);
			if(s){
				log.infof(getDevInfo()+"3D定位 xT:%d yT:%d xB:%d yB:%d",xTop,yTop,xBottom,yBottom);
			}else{
				log.errorf(getDevInfo()+"3D定位失败 xT:%d yT:%d xB:%d yB:%d %s",xTop,yTop,xBottom,yBottom,getErrorMsg());
			}
			return s;
		}

		@AoReflect(value="3D定位-点")
		public boolean NET_DVR_PTZSelZoomIn_Point(int x,int y){
			return NET_DVR_PTZSelZoomIn_EX(x,y,x,y);
		}
		/**
		 * 带速度的云台控制操作(不用启动图象预览)
		 * @param channel 通道
		 * @param command 2接通灯光电源 3接通雨刷开关4接通风扇开关5接通加热器开关6接通辅助设备开关7接通辅助设备开关
		 * 11焦距变大(倍率变大) 12焦距变小(倍率变小) 13焦点前调 14焦点后调 15光圈扩大 16光圈缩小
		 * 21云台上仰 22云台下俯 23云台左转 24云台右转 25云台上仰和左转 26云台上仰和右转 27 云台下俯和左转 28云台下俯和右转
		 * 29云台左右自动扫描58云台下俯和焦距变大(倍率变大)59云台下俯和焦距变小(倍率变小)60云台左转和焦距变大(倍率变大)61云台左转和焦距变小(倍率变小)
		 * 62云台右转和焦距变大(倍率变大)63云台右转和焦距变小(倍率变小)64云台上仰和左转和焦距变大(倍率变大)65云台上仰和左转和焦距变小(倍率变小)66云台上仰和右转和焦距变大(倍率变大)
		 * 67云台上仰和右转和焦距变小(倍率变小)68云台下俯和左转和焦距变大(倍率变大)69云台下俯和左转和焦距变小(倍率变小)70云台下俯和右转和焦距变大(倍率变大)
		 * 71云台下俯和右转和焦距变小(倍率变小)72云台上仰和焦距变大(倍率变大)73云台上仰和焦距变小(倍率变小)
		 * @param stop	0－开始；1－停止
		 * @param speed 云台控制的速度取值范围[1,7]
		 * @return
		 */
		public boolean NET_DVR_PTZControlWithSpeed(int channel,int command,int stop,int speed){
			if(!login()){
				return false;
			}
			return hCNetSDK.NET_DVR_PTZControlWithSpeed_Other(lUserID,new NativeLong(channel),command,stop,speed);
		}

		/**
		 * 获取快球聚焦模式信息
		 * @param lChannel
		 * @return
		 */
		@AoReflect(value="获取聚焦参数")
		public HCNetSDK.NET_DVR_FOCUSMODE_CFG getFocusModeCfg(int lChannel){
			if(!login()){
				return null;
			}
			HCNetSDK.NET_DVR_FOCUSMODE_CFG ptz = new HCNetSDK.NET_DVR_FOCUSMODE_CFG();
			ptz.write();
			IntByReference ibrBytesReturned = new IntByReference();
			boolean s = hCNetSDK.NET_DVR_GetDVRConfig(lUserID, HCNetSDK.NET_DVR_GET_FOCUSMODECFG, new NativeLong(lChannel), ptz.getPointer(), ptz.size(), ibrBytesReturned);
			ptz.read();
			if(!s){
				log.error("获取["+lChannel+"]聚焦参数失败  " + getErrorMsg());
				return null;
			}
			return ptz;
		}

		public boolean setFocusModeCfg(int lChannel, HCNetSDK.NET_DVR_FOCUSMODE_CFG cfg){
			boolean b = hCNetSDK.NET_DVR_SetDVRConfig(lUserID, 3306,new NativeLong(lChannel),cfg.getPointer(),cfg.size());
			if(!b){
				log.error("设置["+lChannel+"]聚焦参数失败  " + getErrorMsg());
			}
			return b;
		}

		@AoReflect(value="设置聚焦模式")
		public boolean setFocusMode(
				@AoReflect(value = "通道",placeholder = "默认1")Integer lChannel,
				@AoReflect(select = "0:自动,1:手动,2:半自动")int model){
			HCNetSDK.NET_DVR_FOCUSMODE_CFG cfg = getFocusModeCfg(lChannel);
			if(cfg == null)
				return false;
			cfg.byFocusMode = (byte) model;
			cfg.write();
			return setFocusModeCfg(lChannel, cfg);
		}

	@AoReflect(value="设置聚焦值")
	public boolean setFocusPos(int lChannel,int pos){
		return toPTZF(null,null,null,pos,lChannel);
	}


	@AoReflect(value="调多预置点")
	public void pushPreset(String channelPre){

		List<String> chanel = new ArrayList<>();
		if(Strings.isNotBlank(channelPre)){
			try{
				String[] ch = channelPre.split("_");
				for(String c : ch){
					String[] p = c.split(":");
					chanel.add(p[0]);
					if(p.length > 1)
						PTZPreset(Integer.parseInt(p[0]),39,Integer.parseInt(p[1]));
				}
			}catch (Exception e){
				e.printStackTrace();
			}
		}
	}

	@AoReflect(value="推送页面")
	public void openCamera(String title,boolean hideRight,String version,String dev,String channel){
		if(Strings.isBlank(dev))
			dev = device.getId()+"";

		String url = "/plugin/hikvision/camera?d="+dev;
		url += "&channel="+channel;
		url += "&v="+version;
		if(hideRight){
			url += "&style=hide-right";
		}
		JSONObject json = new JSONObject();
		json.put("title",title);
		json.put("content",url);
		//ioc.get(WebsocketRoom.class).sendMsg("wsroom:index",json,true);
	}

	/**
	 * 获得云台水平相对零点角度（精度0.1）
	 * @param
	 * @return
	 */
	@AoReflect(value="获取水平角度")
	public float getRelativeHorAngle(){
		return getPTZinfo(1).wPanPos/10f;
	}

	@AoReflect(value="获取垂直角度")
	public float getTiltAngle(){
		return getPTZinfo(1).wTiltPos/10f;
	}
	/**
	 * 控制顶部云台左右
	 * @param isLeft 是否左转
	 * @param speed	速度 [1-7]
	 * @param isStop 是否停止
	 * @return
	 */
	public boolean setTopLR(boolean isLeft,int speed,boolean isStop){
		int command = 24;
		int stop = 0;
		if(isLeft){
			command = 23;
		}
		if(isStop){
			stop = 1;
		}
		return NET_DVR_PTZControlWithSpeed(1, command, stop, speed);
	}



	/*************************************************
	* 函数: "热成像测温" [刻录机不支持，必须直连红外摄像头]
	*************************************************/
	HCNetSDK.NET_DVR_THERMOMETRY_UPLOAD dvrThermometry;
	HCNetSDK.FRemoteConfigCallback fcf;
	HCNetSDK.FRemoteConfigCallback_Linux fcfLinux;

	public HCNetSDK.NET_DVR_THERMOMETRY_UPLOAD getDvrThermometry(){
		return dvrThermometry;
	}

	@AoReflect(value="红外测温")
	public HCNetSDK.NET_DVR_THERMOMETRY_UPLOAD getThermometry(int ruleId, int presetId, Integer channel){
		dvrThermometry = null;

		if (startThermometry(ruleId)){
			int i = 0;
			//等待一段时间，接收实时测温结果
			while (dvrThermometry == null && i++ < 100) {
				Lang.sleep(100);
			}
			stopThermometry();
		}
		return dvrThermometry;
	}

	@AoReflect("开始实时测温")
	public boolean startThermometry(int ruleId){
		if(!login())
			return false;
		HCNetSDK.NET_DVR_REALTIME_THERMOMETRY_COND trm = new HCNetSDK.NET_DVR_REALTIME_THERMOMETRY_COND();
		trm.dwSize = trm.size();
		trm.byRuleID = (byte) ruleId;//0 普通模式 专家模式下预置点对应的规则ID
		trm.dwChan = thermalStart;//热成像通道
		trm.byMode = 1;

		//建立长连接
		if(Lang.isWin()){
			fcf = this::FRemoteConfigInvoke;
			handleThermometry = hCNetSDK.NET_DVR_StartRemoteConfig(lUserID, HCNetSDK.NET_DVR_GET_REALTIME_THERMOMETRY, trm, trm.size(), fcf, null);
		}else{
			fcfLinux = this::FRemoteConfigInvoke;
			handleThermometry = hCNetSDK.NET_DVR_StartRemoteConfig(lUserID, HCNetSDK.NET_DVR_GET_REALTIME_THERMOMETRY, trm, trm.size(), fcfLinux, null);
		}
		if (handleThermometry.longValue() >= 0){
			return true;
		}
		log.error("开始实时测温 error:"+getErrorMsg());
		return false;
	}

	@AoReflect("结束实时测温")
	public void stopThermometry(){
		//关闭长连接配置接口所创建的句柄，释放资源
		if(hCNetSDK.NET_DVR_StopRemoteConfig(handleThermometry)){
			handleThermometry = new NativeLong(0);
		}else{
			log.error("结束实时测温 error:"+getErrorMsg());
		}
	}

	@AoReflect(value="手动测温")
	public Float manualTherm(int ruleId){
		if(!login()){
			return null;
		}

		HCNetSDK.NET_DVR_REALTIME_THERMOMETRY_COND trm = new HCNetSDK.NET_DVR_REALTIME_THERMOMETRY_COND();
		trm.dwSize = trm.size();
		trm.byRuleID = (byte) ruleId;//0 普通模式 专家模式下预置点对应的规则ID
		trm.dwChan = thermalStart;//热成像通道
		trm.byMode = 1;

		//建立长连接
		HCNetSDK.FRemoteConfigCallback2 f2 = this::manualThermInfoCallback;
		NativeLong lHandle = hCNetSDK.NET_DVR_StartRemoteConfig(lUserID, HCNetSDK.NET_DVR_GET_MANUALTHERM_INFO, trm, trm.size(), f2, null);

		if (lHandle.longValue() >= 0){
			int i = 0;
			//等待一段时间，接收实时测温结果
			while (dvrThermometry == null && i++ < 100) {
				Lang.sleep(100);
			}
			//关闭长连接配置接口所创建的句柄，释放资源
			if(!hCNetSDK.NET_DVR_StopRemoteConfig(lHandle)){
				System.out.println("NET_DVR_StopRemoteConfig failed, error "+getErrorMsg());
			}
		}else{
			System.out.println("NET_DVR_GET_REALTIME_THERMOMETRY failed, error "+getErrorMsg());
		}
		return null;

	}

		 //手动测温回调
		 public void manualThermInfoCallback(int dwType, HCNetSDK.NET_SDK_MANUAL_THERMOMETRY tm, int dwBufLen, Pointer pUserData){
			 /**
			  *  NET_SDK_CALLBACK_TYPE_STATUS   = 0,
			  *   NET_SDK_CALLBACK_TYPE_PROGRESS = 1,
			  *   NET_SDK_CALLBACK_TYPE_DATA = 2
			  */
			 if(dwType == 2){
				 HCNetSDK.NET_SDK_MANUALTHERM_RULE rule = tm.struRuleInfo;
				 String[] ut = {"摄氏度℃","华氏度℉","开尔文K"};
				 String[] rt = {"点","框","线"};
				 sendSocket("手动测温["+dwType+"] 规则ID:"+rule.byRuleID +" 温度单位:" + tm.byThermometryUnit +" 类型:"+rule.byRuleCalibType);
				 if(rule.byRuleCalibType == 0){
					 HCNetSDK.NET_SDK_POINT_THERMOMETRY pointThermometry = rule.struPointTherm;
					 sendSocket("点温:"+pointThermometry.fPointTemperature + " 坐标:"+pointThermometry.struPoint.fX+","+pointThermometry.struPoint.fY);
				 }else{
					 HCNetSDK.NET_SDK_REGION_THERMOMETRY regionThermometry = rule.struRegionTherm;
					 sendSocket("框线 最高温:"+regionThermometry.fMaxTemperature);
				 }
			 }

		 }

	 	//实时测温回调
		public void FRemoteConfigInvoke(int dwType, HCNetSDK.NET_DVR_THERMOMETRY_UPLOAD tm, int dwBufLen, Pointer pUserData) {
			dvrThermometry = tm;

			String b = "";
			try {
				b = new String(tm.szRuleName,"GBK").trim();
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			String[] ut = {"摄氏度℃","华氏度℉","开尔文K"};
			String[] rt = {"点","框","线"};
			sendSocket("实时测温 规则ID:"+tm.byRuleID +"("+b+") 温度单位:" + tm.byThermometryUnit +" 类型:"+tm.byRuleCalibType + " 预置点:"+tm.wPresetNo);

			//点测温
			HCNetSDK.NET_VCA_POINT sp = tm.struPointThermCfg.struPoint;
			String temp;
			if(tm.byRuleCalibType == 0){
				temp = String.format("%.1f",tm.struPointThermCfg.fTemperature);
				sendSocket("点温:"+temp + " 坐标:"+sp.fX+","+sp.fY);
				putData("temperature",temp);
			}
			//框/线测温
			HCNetSDK.NET_VCA_POLYGON sr = tm.struLinePolygonThermCfg.struRegion;
			String area = "";
			for(int i=0;i<sr.dwPointNum;i++){
				area += sr.struPos[i].fX+","+sr.struPos[i].fY+";";
			}
			if(tm.byRuleCalibType == 1 || tm.byRuleCalibType == 2){
				temp = String.format("%.1f",tm.struLinePolygonThermCfg.fMaxTemperature);
				String tempMin = String.format("%.1f",tm.struLinePolygonThermCfg.fMinTemperature);
				sendSocket("框/线测温信息 最高温:"+ temp
						+ " 最低温:"+tempMin
						+ " 平均:"+tm.struLinePolygonThermCfg.fAverageTemperature
						+ " 温差:"+tm.struLinePolygonThermCfg.fTemperatureDiff
						+ " 区域:"+area
						+ " 最高温坐标:" + tm.struHighestPoint.fX+","+tm.struHighestPoint.fY
						+ " 最低温坐标:" + tm.struLowestPoint.fX+","+tm.struLowestPoint.fY);
				putData("temperature", temp);
				putData("temperatureMin", tempMin);
			}


		}

		//测温基本参数配置
		public HCNetSDK.NET_DVR_THERMOMETRY_BASICPARAM getThermometryConfig(){
			HCNetSDK.NET_DVR_THERMOMETRY_BASICPARAM ndtb = new HCNetSDK.NET_DVR_THERMOMETRY_BASICPARAM();
			hCNetSDK.NET_DVR_GetSTDConfig(lUserID,3622,ndtb);
			return ndtb;
		}



	/**
	 * 传入区域返回当前倍数
	 *
	 * @param xTop
	 * @param yTop
	 * @param width
	 * @param height
	 * @return
	 */
	public int zoomToArea(int xTop, int yTop, int width, int height) {
		NET_DVR_PTZSelZoomIn_EX(xTop, yTop, xTop + width, yTop + height);
		Lang.sleep(5000);
		HCNetSDK.NET_DVR_PTZPOS p = getPTZinfo(1);
		if(p == null)
			return -1;
		return p.wZoomPos;
	}

	/**
	 * 传入xy,居中
	 *
	 * @param x
	 * @param y
	 * @return
	 */
	public int pointToCenter(int x, int y) {
		NET_DVR_PTZSelZoomIn_Point(x, y);
		Lang.sleep(5000);
		return getPTZinfo(1).wZoomPos;
	}

	public Integer getImgWidth() {
		return imgWidth;
	}

	public Integer getImgHeight() {
		return imgHeight;
	}


    //------------------------------------------- 云台 ------------------------------------------------


	@Override
	public void setZero() {

	}

	@Override
	public void toZero() {

	}

	@AoReflect("到水平角")
	@Override
	public void toPan(float pan) {
		setPTZ(pztChannel, (short)2, (short) (pan * 10), (short) 0 , (short)0);
	}

	@Override
	public void toTilt(float tilt) {
		setPTZ(pztChannel, (short)3, (short) 0, (short)(tilt * 10), (short)0);
	}

	@Override
	public void toPanTilt(float pan, float tilt) {
		setPTZ(pztChannel, (short)5, (short) (pan * 10), (short)(tilt * 10), (short)0);
	}

	@Override
	public void toPTZ(Float pan, Float tilt, Float zoom, Integer focus) {
		toPTZF(pan,tilt,zoom,focus,null);
	}

	@Override
	public void setPreset(int id) {
		PTZPreset(pztChannel,8,id);
	}

	@Override
	public boolean toPreset(int id) {
		return PTZPreset(pztChannel,39,id);
	}

	@Override
	public boolean toPresetSys(long id) {
		TPreset preset = bs.getTCache(TPreset.class,id);
		return toPTZF(preset.getPan(),preset.getTilt(),preset.getZoom(),preset.getFocus(),null);
	}

	@Override
	public void moveUp() {

	}

	@Override
	public void moveDown() {

	}

	@Override
	public void moveLeft() {

	}

	@Override
	public void moveRight() {

	}

	@Override
	public void stop() {

	}

	@AoReflect("云台控制")
	@Override
	public void controlPTZ(@AoReflect(value = "指令") String command, int stop, int speed,Integer channel) {
		NutMap m = new NutMap("up:21,down:22,left:23,right:24,leftUp:25,rightUp:26,leftDown:27,rightDown:28," +
							"zoomAdd:11,zoomDec:12,focusAdd:13,focusDec:14,apertureAdd:15,apertureDec:16,light:2");
		int c = channel == null ? pztChannel : channel;
		int ci = m.containsKey(command) ? m.getInt(command) : Integer.parseInt(command);
		NET_DVR_PTZControlWithSpeed(c,ci,stop,speed);
	}



	@Override
	public boolean zoomIn(Integer channel) {
		return false;
	}

	@Override
	public boolean zoomOut(Integer channel) {
		return false;
	}


	private int lVoiceComHandle = -1; //语音对讲句柄
	//设置语音PCM回调函数
	private static HCNetSDK.FVoiceDataCallback_MR_V30 cbVoicePcmDataCallBack_mr_v30 = new cbVoicePcmDataCallBack_MR_V30();
	static class cbVoicePcmDataCallBack_MR_V30 implements HCNetSDK.FVoiceDataCallback_MR_V30 {
		public void invoke(int lVoiceComHandle, Pointer pRecvDataBuffer, int dwBufSize, byte byAudioFlag, Pointer pUser) {
			//System.out.println("-----=cbVoiceDataCallBack_MR_V30 enter---------");
		}
	}

	@AoReflect("播放PCM")
	public void startTransPCMData(File pcmFile) {
		int PCM_SEND = 1920;
		if(!login()){
			return;
		}
		if(lVoiceComHandle < 0){
			lVoiceComHandle = hCNetSDK.NET_DVR_StartVoiceCom_MR_V30(lUserID.intValue(), 1, cbVoicePcmDataCallBack_mr_v30, null);
			if(lVoiceComHandle < 0){
				log.error("NET_DVR_StartVoiceCom_MR_V30 error：" + getErrorMsg());
				return;
			}
			log.info("NET_DVR_StartVoiceCom_MR_V30 success VoiceComHandle:"+lVoiceComHandle);
		}
		log.info("开始播放PCM:"+pcmFile.getName());
		try {
			// 获取文件的输入流
			FileInputStream inputStream = new FileInputStream(pcmFile);
			int PCMdataLength = 0;
			try {
				//返回文件的总字节数
				PCMdataLength = inputStream.available();
			} catch (IOException e1) {
				e1.printStackTrace();

			}

			HCNetSDK.BYTE_ARRAY ptrVoicePcmByte = new HCNetSDK.BYTE_ARRAY(PCMdataLength);
			try {
				inputStream.read(ptrVoicePcmByte.byValue);
			} catch (IOException e2) {
				e2.printStackTrace();
			}
			ptrVoicePcmByte.write();
			for (int i = 0; i < PCMdataLength / PCM_SEND; i++) {
				HCNetSDK.BYTE_ARRAY ptrPcmSend = new HCNetSDK.BYTE_ARRAY(PCM_SEND);
				System.arraycopy(ptrVoicePcmByte.byValue, i * PCM_SEND, ptrPcmSend.byValue, 0, PCM_SEND);
				ptrPcmSend.write();
				if (!hCNetSDK.NET_DVR_VoiceComSendData(lVoiceComHandle, ptrPcmSend.byValue, PCM_SEND)) {
					//log.info("NET_DVR_VoiceComSendData"+PCM_SEND+"succeed ,data is " + i * PCM_SEND);
					log.error("NET_DVR_VoiceComSendData failed, error code:" + getErrorMsg());
				}
				//需要实时速率发送数据
				try {
					Thread.sleep(60);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
			stopVoiceCom();
		}
	}

	@AoReflect("停止语音")
	public boolean stopVoiceCom(){
		if(lVoiceComHandle > -1){
			boolean b = hCNetSDK.NET_DVR_StopVoiceCom(new NativeLong(lVoiceComHandle));
			if(b){
				lVoiceComHandle = -1;
			}else{
				log.error("停止语音失败:"+getErrorMsg());
			}
			return b;
		}
		return true;
	}

	@Override
	public List<RMenuOption> menuList(){
		List<RMenuOption> list = new ArrayList<>();
		list.add(buildMenu("视频","/control/hikCamera","DEV"));
		return list;
	}

	@Override
	public Object menuClick(String menu, String val) {
		return null;
	}
}
