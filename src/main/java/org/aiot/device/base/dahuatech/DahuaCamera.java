package org.aiot.device.base.dahuatech;

import com.sun.jna.Memory;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;

import org.aiot.infc.device.CameraInfc;
import org.aiot.lang.annotation.AoReflect;
import org.aiot.lang.device.DahuaNetSDK;
import org.aiot.model.enums.AstEnum;
import org.aiot.model.enums.DeviceRoleEnum;
import org.aiot.model.enums.PathEnum;
import org.aiot.model.table.TPreset;
import org.aiot.util.ImgUtil;
import org.nutz.lang.Files;
import org.nutz.lang.Lang;
import org.nutz.lang.util.NutMap;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.aiot.lang.device.DahuaNetSDK.*;

import static org.aiot.lang.device.DahuaNetSDK.NET_DEVSTATE_PTZ_LOCATION;
import static org.aiot.lang.device.DahuaNetSDK.NET_EXTPTZ_ControlType.NET_EXTPTZ_EXACTGOTO;
import static org.aiot.lang.device.DahuaNetSDK.NET_EXTPTZ_ControlType.NET_EXTPTZ_RESETZERO;


@AoReflect(value = "大华摄像头",deviceRole = DeviceRoleEnum.VIDEO)
public class DahuaCamera extends DahuaBase implements CameraInfc {

    @AoReflect(value = "起始通道", type = AstEnum.param)
    private int channelStart = 0;

    @AoReflect(value = "当前云台通道", type = AstEnum.param)
    private int pztChannel = 0;
    
    @AoReflect(value ="可见光通道",type = AstEnum.param)
	private int kjgChannel  = 0;
	
	@AoReflect(value ="热成像通道",type = AstEnum.param)
	private int thermalStart = 0;

    @AoReflect(value = "web地址", type = AstEnum.param)
    private String web;

    @AoReflect(value = "外网地址", type = AstEnum.param)
    private String net;

    @AoReflect(value = "外网限时(S)", type = AstEnum.param)
    private Integer netTimeOut = 60;

	@AoReflect(value ="图像宽度",type = AstEnum.param)
	private Integer imgWidth = 1920;
	@AoReflect(value ="图像高度",type = AstEnum.param)
	private Integer imgHeight = 1080;

    // 是否正在录音
    private static boolean m_bRecordStatus  = false;

    @Override
    @AoReflect("抓拍")
    public File getPicture(Integer channel,File file){
        byte[] bytes = captureBytes(channel);
        if(bytes == null)
            return null;
        if(file == null)
            file = PathEnum.image.getFile(getHost().replaceAll("\\.","_")+"_"+channel+".jpg");
        Files.createDirIfNoExists(file.getParent());
        Files.write(file, bytes);
        return null;
    };

    @AoReflect(value = "抓拍字节")
    public byte[] captureBytes(Integer lChannel) {
        if(!login())
            return null;
        countDownLatch = new CountDownLatch(1);
        //Long t1 = System.currentTimeMillis();
        if(remoteCapturePicture(lChannel)){
            try {
                countDownLatch.await(2, TimeUnit.SECONDS);
                //System.out.println("耗时："+(System.currentTimeMillis() - t1)+"ms");
                return imgBytes;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    @AoReflect(value="通道图像")
    public BufferedImage getImage(Integer channel){
        byte[] bytes = captureBytes(channel);
        return ImgUtil.read(bytes);
    }

    @Override
    @AoReflect(value = "录制视频")
    public File recordVideo(Integer channel,int second,File file){
        return null;
    }

    @Override
    @AoReflect(value="获取倍数")
    public Float getMultiple(Integer channel){
        DahuaNetSDK.NET_PTZ_LOCATION_INFO p = getPTZInfo();
        int i = 0;
        while (p == null) {
            i++;
            if (i>=3) {
                throw new RuntimeException();
            }else{
                Lang.sleep(500);
                p = getPTZInfo();
            }
        }
        return p.nPTZZoom - 4f;
    }

    @Override
    @AoReflect(value="设置倍数")
    public boolean setMultiple(Integer channel,
                               @AoReflect(value = "倍数", placeholder = "0~124")float multiple) {
        NET_PTZ_LOCATION_INFO ptz = getPTZInfo();
        return setPtz(ptz.nPTZPan*1f,ptz.nPTZTilt*1f,multiple + 4);
    }


    @AoReflect("放大")
    @Override
    public boolean zoomIn(Integer channel) {
        int speed = 5;
        return ptzControl(4,0,speed);
    }

    @AoReflect("缩小")
    @Override
    public boolean zoomOut(Integer channel) {
        int speed = 5;
        return ptzControl(5,0,speed);
    }

    @AoReflect("查询测温点")
    public float queryPointInfo(short x, short y) {
        if (!login()) {
            return 0;
        }
        try {
            DahuaNetSDK.NET_RADIOMETRYINFO pointInfo = queryPointTemper(channelStart, x, y);
            if (pointInfo == null) {
                log.info("查询测温点失败！");
                return 0;
            }
            return pointInfo.fTemperAver;
        } catch (NumberFormatException e) {
            log.error(e.getMessage());
        }
        return 0;
    }

    /**
     *
     * @param presetId 预置点编号
     * @param ruleId 规则编号
     * @return
     */
    @AoReflect("测温")
    public DahuaNetSDK.NET_RADIOMETRYINFO queryItemInfo(int presetId, int ruleId) {
        if (!login()) {
            return null;
        }
        return queryItemTemper(thermalStart, presetId, ruleId, 3); //nMeterType  测温类型：1-点 2-线 3-区域;
    }


    @AoReflect("查询测温项-温差")
    public String queryItemInfoDiff(int presetId) {
        if (!login()) {
            return "";
        }
        try {
            DahuaNetSDK.NET_RADIOMETRYINFO stItemInfo =
                    queryItemTemper(channelStart, presetId, 1, 3);
            if (stItemInfo == null) {
                log.info("查询测温项失败！");
                return "";
            }
           /* return "最高温度:" + stItemInfo.fTemperMax + "最低温度:" + stItemInfo.fTemperMin;*/
            return stItemInfo.fTemperMax - stItemInfo.fTemperMin +"";
        } catch (NumberFormatException e) {
            log.error(e.getMessage());
        }
        return "";
    }
    //=================================云台操作========================================================

    @AoReflect(value = "快速定位")
    public boolean NET_DVR_PTZSelZoomIn_EX(int xTop, int yTop, int xBottom, int yBottom) {
        xTop = Math.min(xTop, imgWidth);
        yTop = Math.min(yTop, imgHeight);
        xBottom = Math.min(xBottom, imgWidth);
        yBottom = Math.min(yBottom, imgHeight);

        int dx = (xTop + xBottom) / 2;

        int dy = (yTop + yBottom) / 2;

        int frameX = xBottom - xTop;

        int frameY = yBottom - yTop;

        if (!login()) {
            return false;
        }
        boolean s = dhSdk.CLIENT_DHPTZControlEx(m_hLoginHandle, pztChannel,
                DahuaNetSDK.NET_EXTPTZ_ControlType.NET_EXTPTZ_FASTGOTO,
                ((dx - imgWidth / 2) * 8192 * 2 / imgWidth), ((dy - imgHeight / 2) * 8192 * 2 / imgHeight), (imgWidth * imgHeight) / (frameX * frameY), 1);

        if (s) {
            log.infof("快速定位成功 xT:%d yT:%d xB:%d yB:%d", xTop, yTop, xBottom, yBottom);
        } else {
            log.errorf("快速定位失败 xT:%d yT:%d xB:%d yB:%d %s", xTop, yTop, xBottom, yBottom);
        }
        return s;
    }

    @AoReflect("标定零位")
    @Override
    public void setZero() {
        dhSdk.CLIENT_DHPTZControlEx(m_hLoginHandle, pztChannel, NET_EXTPTZ_RESETZERO, 0, 0, 0, 0);
    }

    @AoReflect("到零位")
    @Override
    public void toZero() {
        setPtz(0f, 0f, 1f);
    }

    @AoReflect("到水平角")
    @Override
    public void toPan(float pan) {
        setPtz(pan*10,0f,1f);
    }

    @AoReflect("到俯仰角")
    @Override
    public void toTilt(float tilt) {
        setPtz(0f,tilt*10,1f);
    }

    @AoReflect("到水平俯仰")
    @Override
    public void toPanTilt(float pan, float tilt) {
        setPtz(pan*10f,tilt*10f,1f);
    }

    @AoReflect("设置预置位")
    @Override
    public void setPreset(int id) {
        pointControl(11, id);
    }

    @AoReflect("到预置位")
    @Override
    public boolean toPreset(int id) {
        return pointControl(10, id);
    }

    @AoReflect("到系统预置位")
    @Override
    public boolean toPresetSys(long id) {
        TPreset preset = bs.getTCache(TPreset.class,id);
        return setPtz((preset.getPan()*10f),  (preset.getTilt()*10f), (preset.getZoom()*10f));
    }

    @AoReflect("上")
    @Override
    public void moveUp() {
        int speed = 5;
        ptzControl(0, 0, speed);
    }

    @AoReflect("下")
    @Override
    public void moveDown() {
        int speed = 5;
        ptzControl(1, 0, speed);
    }

    @AoReflect("左")
    @Override
    public void moveLeft() {
        int speed = 5;
        ptzControl(2, 0, speed);
    }

    @AoReflect("右")
    @Override
    public void moveRight() {
        int speed = 5;
        ptzControl(3, 0, speed);
    }

    @AoReflect("调焦+")
    public boolean ptzFocusAddStart(@AoReflect(value = "倍速",placeholder = "默认5")Integer speed){
        if (speed == null){
            speed = 5;
        }
        return ptzControl(6,0,speed);
    }

    @AoReflect("调焦-")
    public boolean ptzFocusDecStart(@AoReflect(value = "倍速",placeholder = "默认5")Integer speed){
        if (speed == null){
            speed = 5;
        }
        return ptzControl(7,0,speed);
    }

    @AoReflect("光圈+")
    public boolean ptzApertureAddStart(@AoReflect(value = "倍速",placeholder = "默认5")Integer speed){
        if (speed == null){
            speed = 5;
        }
        return ptzControl(8,0,speed);
    }

    @AoReflect("光圈-")
    public boolean ptzApertureDecStart(@AoReflect(value = "倍速",placeholder = "默认5")Integer speed){
        if (speed == null){
            speed = 5;
        }
        return ptzControl(9,0,speed);
    }

    @AoReflect("左上")
    public boolean ptzLUStart(@AoReflect(value = "速度",placeholder = "默认5")Integer speed){
        if (speed == null){
            speed = 5;
        }
        return ptzControl(32,speed,speed);
    }

    @AoReflect("右上")
    public boolean ptzRUStart(@AoReflect(value = "速度",placeholder = "默认5")Integer speed){
        if (speed == null){
            speed = 5;
        }
        return ptzControl(33,speed,speed);
    }

    @AoReflect("左下")
    public boolean ptzLDStart(@AoReflect(value = "速度",placeholder = "默认5")Integer speed){
        if (speed == null){
            speed = 5;
        }
        return ptzControl(34,speed,speed);
    }

    @AoReflect("右下")
    public boolean ptzRDStart(@AoReflect(value = "速度",placeholder = "默认5")Integer speed){
        if (speed == null){
            speed = 5;
        }
        return ptzControl(35,speed,speed);
    }


    /**
     * 预置点操作
     *
     * @param dwPTZCommand 转置预置点：10、设置：11、删除、12
     * @param index        预置点编号
     * @return
     */
    public boolean pointControl(int dwPTZCommand, Integer index) {
        if (!login()) {
            return false;
        }
        return dhSdk.CLIENT_DHPTZControlEx(m_hLoginHandle, pztChannel,
                dwPTZCommand, 0, index, 0, 0);
    }


    @AoReflect("云台信息")
    public DahuaNetSDK.NET_PTZ_LOCATION_INFO getPTZInfo() {
        if (!login()) {
            return null;
        }
        DahuaNetSDK.NET_PTZ_LOCATION_INFO ptzInfo = new DahuaNetSDK.NET_PTZ_LOCATION_INFO();
        ptzInfo.nChannelID = pztChannel;
        ptzInfo.write();
        boolean t = dhSdk.CLIENT_QueryDevState(m_hLoginHandle, NET_DEVSTATE_PTZ_LOCATION, ptzInfo.getPointer(), ptzInfo.size(), new IntByReference(), 3000);
        if (!t) {
            log.errorf("云台信息获取失败,error:%d",getLastError());
            return null;
        }
        ptzInfo.read();
        return ptzInfo;
    }

    /**
     *
     * @param lParam1:水平角度(0~3600)
     * @param lParam2:垂直坐标(0~900)
     * @param lParam3:变倍(1~128)
     * @return
     */
    @AoReflect("云台设置")
    public boolean setPtz(@AoReflect(value = "水平角度", placeholder = "0~3600") Float lParam1,
                          @AoReflect(value = "垂直坐标", placeholder = "-900~900") Float lParam2,
                          @AoReflect(value = "变倍", placeholder = "0~124") Float lParam3) {
    	if (!login()) {
            return false;
        }
        return dhSdk.CLIENT_DHPTZControlEx(m_hLoginHandle, pztChannel, NET_EXTPTZ_EXACTGOTO, Math.round(lParam1), Math.round(lParam2), Math.round(lParam3), 0);
    }

    @Override
    public void toPTZ(Float pan, Float tilt, Float zoom,Integer focus) {
    	setPtz(pan*10, tilt*10, zoom);
    }

    /**
     *  云台：上、下、左、右、变倍+、变倍-、调焦+、调焦-、光圈+、光圈-
     * @param dwPTZCommand 控制命令类型 上：0 下：1 左：2 右：3
     *                     变倍+：4变倍-：5调焦+：6调焦-：7光圈+：8光圈-：9
     *                     左上：32 右上：33 左下：34 右下：35、
     * @param lParam2 速度（1-8）
     * 是否停止，对云台八方向操作及镜头操作命令有效，进行其他操作时，本参数应填充FALSE
     */
    public boolean ptzControl(int dwPTZCommand, int lParam1, int lParam2) {
        if (!login()) {
            return false;
        }
        switch (dwPTZCommand) {
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
            case 8:
            case 9:
                dhSdk.CLIENT_DHPTZControlEx(m_hLoginHandle, pztChannel, dwPTZCommand, 0, lParam2, 0, 0);
                break;
            case 32:
            case 33:
            case 34:
            case 35:
                dhSdk.CLIENT_DHPTZControlEx(m_hLoginHandle, pztChannel, dwPTZCommand, lParam1, lParam2, 0, 0);
                break;
            default:
                break;
        }
        return true;
    }

    @AoReflect("停止")
    @Override
    public void stop() {
        dhSdk.CLIENT_DHPTZControlEx(m_hLoginHandle, pztChannel,
                0, 0, 0, 0, 1);
        dhSdk.CLIENT_DHPTZControlEx(m_hLoginHandle, pztChannel,
                1, 0, 0, 0, 1);
        dhSdk.CLIENT_DHPTZControlEx(m_hLoginHandle, pztChannel,
                2, 0, 0, 0, 1);
        dhSdk.CLIENT_DHPTZControlEx(m_hLoginHandle, pztChannel,
                3, 0, 0, 0, 1);
        dhSdk.CLIENT_DHPTZControlEx(m_hLoginHandle, pztChannel,
                4, 0, 0, 0, 1);
        dhSdk.CLIENT_DHPTZControlEx(m_hLoginHandle, pztChannel,
                5, 0, 0, 0, 1);
        dhSdk.CLIENT_DHPTZControlEx(m_hLoginHandle, pztChannel,
                6, 0, 0, 0, 1);
        dhSdk.CLIENT_DHPTZControlEx(m_hLoginHandle, pztChannel,
                7, 0, 0, 0, 1);
        dhSdk.CLIENT_DHPTZControlEx(m_hLoginHandle, pztChannel,
                8, 0, 0, 0, 1);
        dhSdk.CLIENT_DHPTZControlEx(m_hLoginHandle, pztChannel,
                9, 0, 0, 0, 1);
        dhSdk.CLIENT_DHPTZControlEx(m_hLoginHandle, pztChannel,
                32, 0, 0, 0, 1);
        dhSdk.CLIENT_DHPTZControlEx(m_hLoginHandle, pztChannel,
                33, 0, 0, 0, 1);
        dhSdk.CLIENT_DHPTZControlEx(m_hLoginHandle, pztChannel,
                34, 0, 0, 0, 1);
        dhSdk.CLIENT_DHPTZControlEx(m_hLoginHandle, pztChannel,
                35, 0, 0, 0, 1);

    }

    @Override
    @AoReflect("获取云台位置")
    public TPreset getPTZF(Integer channel) {
        DahuaNetSDK.NET_PTZ_LOCATION_INFO info = getPTZInfo();
        TPreset tPreset = new TPreset();
        tPreset.setPan(info.nPTZPan/10f);
        tPreset.setTilt(info.nPTZTilt/10f);
        tPreset.setZoom(info.nPTZZoom/1f - 4);
        tPreset.setFocus(info.nFocusMapValue - 4096);

        return tPreset;
    }

    @Override
    public boolean toPTZF(Float pan, Float tilt, Float zoom, Integer focus, Integer channel) {
        return false;
    }

    @Override
    public boolean setFocusMode(Integer channel, int model) {
        return false;
    }

    @AoReflect("云台控制")
    @Override
    public void controlPTZ(String command, int stop, int speed,Integer channel) {
        NutMap m = new NutMap("up:0,down:1,left:2,right:3,leftUp:32,rightUp:33,leftDown:34,rightDown:35," +
                "zoomAdd:4,zoomDec:5,focusAdd:6,focusDec:7,apertureAdd:8,apertureDec:9,light:66");
        int c = channel == null ? pztChannel : channel;
        switch (command){
            case "light":
                dhSdk.CLIENT_DHPTZControlEx(m_hLoginHandle, c, m.getInt(command), stop, 0, 0, 0);
                break;
            default:
                dhSdk.CLIENT_DHPTZControlEx(m_hLoginHandle, c, m.getInt(command), speed, speed, 0, stop);
        }
    }

    @AoReflect(value="设置垂直角度")
    public boolean setPosTilt(float tilt){//TODO
        int t = getPTZInfo().nPTZPan;
        int z= getPTZInfo().nPTZZoom;
        return setPtz(t*1.0f,(tilt*10),z*1.0f);
    }


	public Integer getImgWidth() {
		return imgWidth;
	}


	public void setImgWidth(Integer imgWidth) {
		this.imgWidth = imgWidth;
	}


	public Integer getImgHeight() {
		return imgHeight;
	}


	public void setImgHeight(Integer imgHeight) {
		this.imgHeight = imgHeight;
	}



	@AoReflect(value="区域放大")
	public int zoomToArea(int xTop, int yTop, int width, int height) {
		return NET_DVR_PTZSelZoomIn_EX(xTop, yTop, xTop+width, yTop+height)?1:0;
	}



	@AoReflect(value="点位居中")
	public int pointToCenter(int x, int y) {
		return NET_DVR_PTZSelZoomIn_EX(x, y, x, y)?1:0;
	}

	/**
	 * 控制顶部云台左右
	 * @param isLeft 是否左转
	 * @param speed	速度 [1-7]
	 * @param isStop 是否停止*/
	@AoReflect(value="控制顶部云台左右")
	public boolean setTopLR(boolean isLeft, int speed, boolean isStop) {
		if(isStop){
			stop();
		}else{
			if(isLeft){
				 ptzControl(2, 0, speed);
			}else{
				ptzControl(3, 0, speed);
			}
		}
		return true;
	}
	@AoReflect(value="调用预置点")
	public boolean PTZPresetGo(int index, Integer channel, Integer duration) {
		return pointControl(10, index);
	}


	//旋转云台角度
    public boolean turnAngle(int speed,float angle) {
        float a = getRelativeHorAngle();
        a += angle;
        if(a < 0)
            a += 360;
        if(a > 360)
            a -= 360;
        return setPosPan(a);
    }

   @AoReflect("设置聚焦值")
    public boolean setFocusPos(int lChannel, int pos) {
       if (!login()) {
           return false;
       }
        DahuaNetSDK.NET_IN_PTZBASE_SET_FOCUS_MAP_VALUE_INFO focusMapInfo = new DahuaNetSDK.NET_IN_PTZBASE_SET_FOCUS_MAP_VALUE_INFO();
        focusMapInfo.nfocusMapValue = pos;

        focusMapInfo.write();
        if (!dhSdk.CLIENT_DHPTZControlEx2(m_hLoginHandle, lChannel==0 ? 0 :lChannel-1,
                DahuaNetSDK.NET_EXTPTZ_ControlType.NET_EXTPTZ_BASE_SET_FOCUS_MAP_VALUE,
                0, 0, 0, 0, focusMapInfo.getPointer())) {
            System.err.println("Set Focus Map Failed!!!" + getLastError());
            return false;
        } else {
            System.out.println("Set Focus Map Succeed!!!");
            return true;
        }
    }

    @AoReflect(value="获取水平角度")
	public float getRelativeHorAngle() {
        DahuaNetSDK.NET_PTZ_LOCATION_INFO p = getPTZInfo();
        int i = 0;
        while (p == null) {
            i++;
            if (i>=3) {
                throw new RuntimeException();
            }else{
                Lang.sleep(500);
                p = getPTZInfo();
            }
        }
        return  p.nPTZPan/10f;
	}
    @AoReflect(value="获取垂直角度")
    public float getTiltAngle() {//TODO 待实现
        DahuaNetSDK.NET_PTZ_LOCATION_INFO p = getPTZInfo();
        int i = 0;
        while (p == null) {
            i++;
            if (i>=3) {
                throw new RuntimeException();
            }else{
                Lang.sleep(500);
                p = getPTZInfo();
            }
        }
        return  p.nPTZTilt/10f;
    }


	@AoReflect(value="设置水平角度")
	public boolean setPosPan(float pan) {
        int t = getPTZInfo().nPTZTilt;
        int z= getPTZInfo().nPTZZoom;
        return setPtz((pan*10),t*1f,z*1f);
	}

	/*@AoReflect(value="红外测温")
    @Override
	public NET_DVR_THERMOMETRY_UPLOAD getThermometry(int ruleId,int presetId,Integer channel) {
        if (hw != null) {
            return hw.getThermometry(ruleId,presetId,channel);
        }
        NetSDKLib.NET_RADIOMETRYINFO t =  queryItemInfo(ruleId,presetId);//qcj modify(杜淘金说大华反着的)
        if(t == null)
            return null;

        NET_DVR_THERMOMETRY_UPLOAD t2 = new NET_DVR_THERMOMETRY_UPLOAD();
        t2.byRuleCalibType = 1;
        HCNetSDK.NET_DVR_LINEPOLYGON_THERM_CFG t3 = new HCNetSDK.NET_DVR_LINEPOLYGON_THERM_CFG();
        t2.struLinePolygonThermCfg = t3;
        t3.fMaxTemperature = t.fTemperMax;
        t3.fMinTemperature = t.fTemperMin;
        t3.fAverageTemperature = t.fTemperAver;
        t3.fTemperatureDiff = t.fTemperStd;
        t3.struRegion = new HCNetSDK.NET_VCA_POLYGON();

		return t2;
	}*/



	

    /**
     * @param chn 转发通道 设置语音对讲是否为转发模式(场景：1.直接登录设备相机,不需要设置转发模式;2.经过了NVR中转,需要设置 )
     */
	@AoReflect("打开语音")
    public boolean startTalk(@AoReflect(value = "模式",select = "0:对讲,1:喊话") int type,@AoReflect(value = "通道")Integer chn) {

        if (!login()) {
            return false;
        }

        if(m_hTalkHandle.longValue() != 0) {
            return true;
        }

        // 设置语音对讲编码格式
        DahuaNetSDK.NETDEV_TALKDECODE_INFO talkEncode = new DahuaNetSDK.NETDEV_TALKDECODE_INFO();
        talkEncode.encodeType = DahuaNetSDK.NET_TALK_CODING_TYPE.NET_TALK_DEFAULT;
        talkEncode.dwSampleRate = 8000;
        talkEncode.nAudioBit = 16;
        talkEncode.nPacketPeriod = 25;
        talkEncode.write();
        if(!dhSdk.CLIENT_SetDeviceMode(m_hLoginHandle, DahuaNetSDK.EM_USEDEV_MODE.NET_TALK_ENCODE_TYPE, talkEncode.getPointer())) {
            log.error("语音-设置对讲编码格式:失败");
            return false;
        }

        // 设置语音对讲喊话参数
        DahuaNetSDK.NET_SPEAK_PARAM speak = new DahuaNetSDK.NET_SPEAK_PARAM();
        speak.nMode = type; //0：对讲（默认模式）,1：喊话；从喊话切换到对讲要重新设置
        speak.nSpeakerChannel = 0; //扬声器通道号,喊话时有效
        speak.write();
        boolean b1 = dhSdk.CLIENT_SetDeviceMode(m_hLoginHandle, DahuaNetSDK.EM_USEDEV_MODE.NET_TALK_SPEAK_PARAM, speak.getPointer());
        if (!b1) {
            log.error("语音-设置对讲喊话参数:失败"+ getLastError());
            return false;
        }

        // 设置语音对讲是否为转发模式
        DahuaNetSDK.NET_TALK_TRANSFER_PARAM talkTransfer = new DahuaNetSDK.NET_TALK_TRANSFER_PARAM();
        talkTransfer.bTransfer = chn == null ? 0 : 1; // 是否开启语音对讲转发模式
        talkTransfer.write();
        if(!dhSdk.CLIENT_SetDeviceMode(m_hLoginHandle, DahuaNetSDK.EM_USEDEV_MODE.NET_TALK_TRANSFER_MODE, talkTransfer.getPointer())) {
            log.error("语音-设置转发模式:失败"+ getLastError());
            return false;
        }
        // 转发模式设置转发通道
        if (chn != null) {
            IntByReference nChn = new IntByReference(chn);
            if(!dhSdk.CLIENT_SetDeviceMode(m_hLoginHandle, DahuaNetSDK.EM_USEDEV_MODE.NET_TALK_TALK_CHANNEL, nChn.getPointer())) {
                log.error("语音-设置转发通道:失败"+ getLastError());
                return false;
            }
        }

        m_hTalkHandle = dhSdk.CLIENT_StartTalkEx(m_hLoginHandle, audioDataCallBack, null);
        if(m_hTalkHandle.longValue() == 0) {
            log.error("语音-启动失败：" + getLastError());
            return false;
        }else{
            log.info("大华语音-启动成功");
        }
        if(dhSdk.CLIENT_RecordStart()){
            m_bRecordStatus = true;
        } else {
            log.error("语音-打开麦克风失败"+ getLastError());
        }
        return true;
    }



    @AoReflect("关闭语音")
    public  void stopTalk() {
        if(m_hTalkHandle.longValue() == 0) {
            return;
        }

        if (m_bRecordStatus){
            dhSdk.CLIENT_RecordStop();
            m_bRecordStatus = false;
        }

        if(dhSdk.CLIENT_StopTalkEx(m_hTalkHandle)) {
            m_hTalkHandle.setValue(0);
        }else {
            log.errorf("Stop Talk Failed!" + getLastError());
        }
    }

    /**
     * 功能：发送语音文件中的音频数据到设备
     * 描述：1.必须开始对讲，在调用播放功能
     * 	   2.该接口只支持不带音频头的音频裸数据。
     *     3.emEncodeType参数只支持PCM G711a G711u
     *     4.注意封装参数参数设定，demo中以以16位8K的PCM数据为例;
     */
    //@AoReflect("发送音频-文件")
    public void talkSendFile() {
        DahuaNetSDK.NET_IN_TALK_SEND_DATA_FILE stIn = new DahuaNetSDK.NET_IN_TALK_SEND_DATA_FILE();
        /**
         *  是否需要加音频头。
         *  TRUE，表示需要SDK根据下面的音频信息加音频头；
         *  FALSE，表示不需要SDK根据音频信息加音频头，直接发送pFilePath路径指向的数据给设备。
         */
        stIn.bNeedHead = true;
        stIn.cbSendPos = talkSendPosCallBack;
        stIn.dwSampleRate = 8000;
        stIn.nAudioBit = 16;
        stIn.emEncodeType = DahuaNetSDK.NET_TALK_CODING_TYPE.NET_TALK_PCM;
        stIn.dwSendInterval = 100;
        byte[] pFilePathByteArr = "E:\\1.wav".getBytes();

        Pointer pFilePath = new Memory(pFilePathByteArr.length);
        pFilePath.clear(pFilePathByteArr.length);
        pFilePath.write(0, pFilePathByteArr, 0, pFilePathByteArr.length);
        stIn.pFilePath = pFilePath;
        stIn.write();

        DahuaNetSDK.NET_OUT_TALK_SEND_DATA_FILE stOut = new DahuaNetSDK.NET_OUT_TALK_SEND_DATA_FILE();
        stOut.write();
        LLong flg = dhSdk.CLIENT_TalkSendDataByFile(m_hTalkHandle, stIn.getPointer(), stOut.getPointer());
        System.out.println(flg+":"+m_hTalkHandle);
        if(flg.longValue() == 0){
            System.out.println(getLastError());
        }
    }

    @AoReflect("发送音频")
    public void talkSendStream(String pcmPath){
        if(isSendStream){
            sendSocket("音频正在播放中...");
            return;
        }
        if(!startTalk(0,null)){
            sendSocket("语音打开失败，不能播放音频");
            return;
        }
        isSendStream = true;
        try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File(pcmPath));
            AudioFormat audioFormat = audioInputStream.getFormat();
            int rate = (int) audioFormat.getSampleRate();
            int bits = audioFormat.getSampleSizeInBits();
            int channels = audioFormat.getChannels();

            InputStream in =  new FileInputStream(pcmPath);
            int times = in.available()/(rate * bits / 8);
            sendSocket("%s 开始播放 时长:%dS 采样率：%d,位数：%d,声道数：%d",pcmPath,times,rate,bits,channels);

            byte[] tempbytes = new byte[1280];
            int numberOfByteRead = 0; // 从文件中读取字节数
            //部分设备对讲时候只能保存1秒左右的数据，所以发送音频文件的时候，必须要控制发送速度，以免发送过快导致设备丢数据。
            //以16位8K的PCM数据为例，1秒钟的数据量为16*8000 bit即16*8000/8 byte(16000字节)
            //设置音频采集率为8000 ，位数为16 ，每秒读取byte[] tempbytes = new byte[1280]  1280字节
            //计算1280/16000 * 1000 = 64ms
            int t = 1280*1000/(rate * bits / 8);
            while (true) {
                // 读取文件流
                numberOfByteRead = in.read(tempbytes);
                if (numberOfByteRead > 0) {
                    // 发送语音数据到设备
                    Pointer pDataBuf = new Memory(numberOfByteRead);
                    pDataBuf.write(0, tempbytes, 0, numberOfByteRead);

                    DahuaNetSDK.NET_IN_TALK_SEND_DATA_STREAM stIn = new DahuaNetSDK.NET_IN_TALK_SEND_DATA_STREAM();
                    stIn.bNeedHead = true;
                    stIn.emEncodeType = DahuaNetSDK.NET_TALK_CODING_TYPE.NET_TALK_DEFAULT;
                    stIn.nAudioBit = bits;
                    stIn.dwSampleRate = rate;
                    stIn.pBuf = pDataBuf;
                    stIn.dwBufSize = numberOfByteRead;
                    stIn.write();
                    DahuaNetSDK.NET_OUT_TALK_SEND_DATA_STREAM stOut = new DahuaNetSDK.NET_OUT_TALK_SEND_DATA_STREAM();
                    stOut.write();
                    dhSdk.CLIENT_TalkSendDataByStream(m_hTalkHandle, stIn.getPointer(), stOut.getPointer());
                    Lang.sleep(t);
                }else {
                    break;
                }
            }
        } catch (UnsupportedAudioFileException e) {
            throw Lang.makeThrow("音频格式错误："+e.getMessage());
        } catch (IOException e) {
            throw Lang.makeThrow("文件读取错误："+e.getMessage());
        }finally {
            isSendStream = false;
        }


    }

    //补光灯设置
    //单目云台，需要提前设置补光方案为白光模式
    @AoReflect("补光灯")
    public boolean lightFill(@AoReflect(select = "1:打开,0:关闭") int model) {
        if (model == 0) {
            model = 4;
        }

        if (!login()) {
            return false;
        }
        DahuaNetSDK.NET_IN_LIGHTINGCONTROL_CAPS stIn = new DahuaNetSDK.NET_IN_LIGHTINGCONTROL_CAPS();
        DahuaNetSDK.NET_OUT_LIGHTINGCONTROL_CAPS stOut = new DahuaNetSDK.NET_OUT_LIGHTINGCONTROL_CAPS();
        stIn.nChannel = kjgChannel;
        stIn.write();
        stOut.write();

        boolean bRet = dhSdk.CLIENT_GetDevCaps(m_hLoginHandle, 0x22, stIn.getPointer(), stOut.getPointer(), 10000);

        stIn.read();
        stOut.read();
        if (!bRet) {
            log.info("获取能力错误");
            return false;
        } else {
            log.info("获取能力成功");
        }

        if (stOut.emConfigVersion == 2) {
            DahuaNetSDK.CFG_LIGHTING_V2_INFO config = new DahuaNetSDK.CFG_LIGHTING_V2_INFO();
            int channel = kjgChannel;// 通道号
            Pointer pointer = new Memory(config.size());
            pointer.clear(config.size());
            SetStructDataToPointer(config, pointer, 0);

            if (GetDevConfig(m_hLoginHandle, channel, "Lighting_V2", config)) {
                DahuaNetSDK.CFG_LIGHTING_V2_DAYNIGHT[] anDNLightInfo = config.anDNLightInfo;// 白天黑夜对应灯光配置 从元素0开始分别表示 白天、夜晚、普通、顺光、一般逆光、强逆光、低照度、自定义
                /**--------白天-----------*/
                int nLightInfoLen = anDNLightInfo[0].nLightInfoLen;
                DahuaNetSDK.CFG_LIGHTING_V2_UNIT[] anLightInfo = anDNLightInfo[0].anLightInfo;// 获取白天下的补光灯配置信息
                // 设置，在获取配置的基础上设置
                // 配置文件 -白天    配置文件 - 白光模式   模式-手动   模式- 0-100
                config.anDNLightInfo[0].anLightInfo[1].emMode = model;//模式1：手动  4：自动
                config.anDNLightInfo[0].anLightInfo[1].anNearLight[0].nLight = 50;//近光灯
                config.anDNLightInfo[0].anLightInfo[1].anFarLight[0].nLight = 50;//近光灯

                boolean a = SetDevConfig(m_hLoginHandle, channel, "Lighting_V2", config);
                if (a) {
                    log.info("补光灯参数设置成功！");
                } else {
                    return false;
                }
            }
        }

        return true;
    }
}
