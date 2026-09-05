package org.aiot.device.base.dahuatech;

import com.sun.jna.Callback;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.ptr.IntByReference;
import org.aiot.device.base.CameraDevice;
import org.aiot.lang.annotation.AoReflect;
import org.aiot.lang.device.DahuaNetSDK;
import org.aiot.model.enums.AstEnum;
import org.aiot.model.enums.PathEnum;
import org.aiot.util.SysUtil;
import org.nutz.lang.Lang;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.aiot.lang.device.DahuaNetSDK.*;

import java.util.concurrent.CountDownLatch;


public class DahuaBase extends CameraDevice {
    Log log = Logs.get();

    static DahuaNetSDK dhSdk;
    static DahuaNetSDK dhConfigSdk;

    //static NetSDKLib dhconfigsdk = NetSDKLib.CONFIG_INSTANCE;

    @AoReflect(value = "端口", type = AstEnum.param)
    private int port = 37777;

    @AoReflect(value = "实时图片接收缓冲(k)", type = AstEnum.param)
    private int picBufSize = 2*1024;


    private  boolean bLogopen = false;

    // 语音对讲句柄
    public static LLong m_hTalkHandle = new LLong(0);

    // 设备信息
    private DahuaNetSDK.NET_DEVICEINFO_Ex m_stDeviceInfo = new DahuaNetSDK.NET_DEVICEINFO_Ex();

    // 登陆句柄
    LLong m_hLoginHandle = new LLong(0);

    // 设备断线通知回调
    private DahuaNetSDK.fDisConnect disConnect;
    private DahuaNetSDK.fDisConnectLinux disConnectLinux;

    // 网络连接恢复
    private DahuaNetSDK.fHaveReConnect haveReConnect;
    private DahuaNetSDK.fHaveReConnectLinux haveReConnectLinux;

    //抓图回调
    private DahuaNetSDK.fSnapRev snapRev;
    private DahuaNetSDK.fSnapRevLinux snapRevLinux;

    //语音回调
    protected DahuaNetSDK.pfAudioDataCallBack audioDataCallBack;
    protected DahuaNetSDK.pfAudioDataCallBackLinux audioDataCallBackLinux;

    protected DahuaNetSDK.fAudioDataCallBackEx audioDataCallBackEx;
    protected DahuaNetSDK.fAudioDataCallBackExLinux audioDataCallBackExLinux;

    //音频发送回调
    protected DahuaNetSDK.fTalkSendPosCallBack talkSendPosCallBack;
    protected DahuaNetSDK.fTalkSendPosCallBackLinux talkSendPosCallBackLinux;


    protected static byte[] imgBytes;
    protected static CountDownLatch countDownLatch = new CountDownLatch(1);

    protected boolean isSendStream;//正在发送音频

    protected String[] errCode = new String[]{
            "0:没有错误",
            "-1:未知错误",
            "1:Windows系统出错",
            "2:网络错误",
            "3:设备协议不匹配",
            "4:句柄无效",
            "7:参数错误",
            "21:对返回数据的校验出错"
    };

    @Override
    public void init() {
        synchronized (bs) {
            loadSdk();
        }
    }

    //加载SDk
    public void loadSdk() {
        try {
            if (dhSdk != null) {
                return;
            }
            SysUtil.addJnaPath(PathEnum.lib + "dhNetSDK");
            dhSdk = Native.load("dhnetsdk", DahuaNetSDK.class);
            log.info("dhnetsdk加载成功");
            dhConfigSdk = Native.load("dhconfigsdk", DahuaNetSDK.class);
            log.info("dhconfigsdk加载成功");

            if (Lang.isWin()) {
                disConnect = this::fDisConnect;
                haveReConnect = this::fHaveReConnect;
                snapRev = this::fSnapRev;
                audioDataCallBack = this::pfAudioDataCallBack;
                talkSendPosCallBack = this::fTalkSendPosCallBack;
                audioDataCallBackEx = this::fAudioDataCallBackEx;
                //初始化
                if(initSdk(disConnect, haveReConnect)){
                    dhSdk.CLIENT_SetSnapRevCallBack(snapRev,null);
                    log.info("dhnetsdk抓拍回调已设置");
                }
            }else{
                disConnectLinux = this::fDisConnect;
                haveReConnectLinux = this::fHaveReConnect;
                snapRevLinux = this::fSnapRev;
                audioDataCallBackLinux = this::pfAudioDataCallBack;
                talkSendPosCallBackLinux = this::fTalkSendPosCallBack;
                audioDataCallBackExLinux = this::fAudioDataCallBackEx;
                //初始化
                if(initSdk(disConnectLinux, haveReConnectLinux)){
                    dhSdk.CLIENT_SetSnapRevCallBack(snapRevLinux,null);
                    log.info("dhnetsdk抓拍回调已设置");
                }
            }



        } catch (Throwable throwable) {
            Logs.get().errorf("dhnetsdk加载失败：%s", SysUtil.envInfo());
            throwable.printStackTrace();
        }
    }

    /**
     * 初始化
     */
    public  boolean initSdk(Callback disConnect, Callback haveReConnect) {

        if (dhSdk.CLIENT_Init(disConnect, null)) {
            Logs.get().info("dhnetsdk初始化成功");
        }else{
            Logs.get().info("dhnetsdk初始化失败");
            return false;
        }

        // 设置断线重连回调接口，设置过断线重连成功回调函数后，当设备出现断线情况，SDK内部会自动进行重连操作
        // 此操作为可选操作，但建议用户进行设置
        dhSdk.CLIENT_SetAutoReconnect(haveReConnect, null);

        //设置登录超时时间和尝试次数，可选
        int waitTime = 5000; //登录请求响应超时时间设置为5S
        int tryTimes = 1;    //登录时尝试建立链接1次
        dhSdk.CLIENT_SetConnectTime(waitTime, tryTimes);


        // 设置更多网络参数，NET_PARAM的nWaittime，nConnectTryNum成员与CLIENT_SetConnectTime
        // 接口设置的登录设备超时时间和尝试次数意义相同,可选
        DahuaNetSDK.NET_PARAM netParam = new DahuaNetSDK.NET_PARAM();
        netParam.nConnectTime = 10000;      // 登录时尝试建立链接的超时时间
        netParam.nGetConnInfoTime = 3000;   // 设置子连接的超时时间
        netParam.nGetDevInfoTime = 3000;//获取设备信息超时时间，为0默认1000ms
        netParam.nPicBufSize = picBufSize * 1024;
        dhSdk.CLIENT_SetNetworkParam(netParam);
        return true;
    }

    public int getLastError() {
        return dhSdk.CLIENT_GetLastError() & 0x7fffffff;
    }


    @Override
    public void selfTest() {
        if (login()) {
            setLastTimeNow();
        }

    }

    //登录设备
    public synchronized boolean login() {
        if (dhSdk == null) {
            sendSocket("sdk未加载");
            return false;
        }

        if(m_hLoginHandle.longValue() > 0)
            return true;

        if (loginDev(getHost(), port, user, password)) {
            setLastTimeNow();
            return true;
        }
        return false;
    }

    //断线回调
    public void fDisConnect(LLong lLoginID, String pchDVRIP, int nDVRPort, Pointer dwUser) {
        System.out.printf("Device[%s] Port[%d] DisConnect!\n", pchDVRIP, nDVRPort);
    }


    // 网络连接恢复，设备重连成功回调
    // 通过 CLIENT_SetAutoReconnect 设置该回调函数，当已断线的设备重连成功时，SDK会调用该函数
    public void fHaveReConnect(LLong m_hLoginHandle, String pchDVRIP, int nDVRPort, Pointer dwUser) {
        System.out.printf("ReConnect Device[%s] Port[%d]\n", pchDVRIP, nDVRPort);
    }

    //抓图回调
    public void fSnapRev(LLong lLoginID, Pointer pBuf, int RevLen, int EncodeType, int CmdSerial, Pointer dwUser) {
        if (pBuf == null || RevLen <= 0)
            return;

        imgBytes = new byte[RevLen];
        System.arraycopy(pBuf.getByteArray(0, RevLen),0,imgBytes,0,RevLen);
        countDownLatch.countDown();
    }

    /**
     * 语音对讲的数据回调
     */
    public void pfAudioDataCallBack(LLong lTalkHandle, Pointer pDataBuf, int dwBufSize, byte byAudioFlag, Pointer dwUser){

        if(isSendStream || lTalkHandle.longValue() != m_hTalkHandle.longValue()) {
            return;
        }

        if (byAudioFlag == 0) { // 将收到的本地PC端检测到的声卡数据发送给设备端
            LLong lSendSize = dhSdk.CLIENT_TalkSendData(m_hTalkHandle, pDataBuf, dwBufSize);
            if(lSendSize.longValue() != (long)dwBufSize) {
                System.err.println("send incomplete" + lSendSize.longValue() + ":" + dwBufSize);
            }
        }else if (byAudioFlag == 1) { // 将收到的设备端发送过来的语音数据传给SDK解码播放
            dhSdk.CLIENT_AudioDecEx(m_hTalkHandle, pDataBuf, dwBufSize);
        }
    }

    public void fAudioDataCallBackEx(LLong lTalkHandle, NET_AUDIO_DATA_CB_INFO stAudioInfo, int emAudioFlag, Pointer dwUser) {

    }

    //音频发送回调
    public void fTalkSendPosCallBack(LLong lTalkHandle, int dwTotalSize, int dwSendSize,Pointer dwUser){
        System.out.println(lTalkHandle+":"+dwSendSize);
    };



    /**
     * 查询测温点
     */
    public NET_RADIOMETRYINFO queryPointTemper(int nChannel, short x, short y) {
        int nQueryType = DahuaNetSDK.NET_QUERY_DEV_RADIOMETRY_POINT_TEMPER;
        // 入参
        NET_IN_RADIOMETRY_GETPOINTTEMPER stIn = new NET_IN_RADIOMETRY_GETPOINTTEMPER();
        stIn.nChannel = nChannel;
        stIn.stCoordinate.nx = x;
        stIn.stCoordinate.ny = y;

        // 出参
        NET_OUT_RADIOMETRY_GETPOINTTEMPER stOut = new NET_OUT_RADIOMETRY_GETPOINTTEMPER();

        stIn.write();
        stOut.write();
        boolean bRet = dhSdk.CLIENT_QueryDevInfo(m_hLoginHandle, nQueryType, stIn.getPointer(), stOut.getPointer(), null, 3000);
        if (!bRet) {
            System.err.printf("QueryPointTemper Failed!" + "ToolKits.getErrorCodePrint()");
            return null;
        }

        stOut.read();
        return stOut.stPointTempInfo;
    }

    /**
     * 查询测温项-点、线、区域
     */
    /**
     *
     * @param nChannel 通道号
     * @param nPresetId 预置点
     * @param nRuleId 规则编号
     * @param nMeterType  测温类型：1-点 2-线 3-区域
     * @return
     */
    public NET_RADIOMETRYINFO queryItemTemper(int nChannel, int nPresetId, int nRuleId, int nMeterType) {
        int nQueryType = DahuaNetSDK.NET_QUERY_DEV_RADIOMETRY_TEMPER;

        // 入参
        NET_IN_RADIOMETRY_GETTEMPER stIn = new NET_IN_RADIOMETRY_GETTEMPER();
        stIn.stCondition.nPresetId = nPresetId;
        stIn.stCondition.nRuleId = nRuleId;
        stIn.stCondition.nMeterType = nMeterType;    // eg: NET_RADIOMETRY_METERTYPE.NET_RADIOMETRY_METERTYPE_AREA;
        stIn.stCondition.nChannel = nChannel;

        // 出参
        NET_OUT_RADIOMETRY_GETTEMPER stOut = new NET_OUT_RADIOMETRY_GETTEMPER();

        stIn.write();
        stOut.write();
        boolean bRet = dhSdk.CLIENT_QueryDevInfo(m_hLoginHandle, nQueryType, stIn.getPointer(), stOut.getPointer(), null, 3000);

        if (!bRet) {
            log.errorf("获取温度失败 code:"+getLastError());
            return null;
        }

        stOut.read();
        NET_RADIOMETRYINFO t =  stOut.stTempInfo;
        sendSocket("获取到温度 最高温："+t.fTemperMax);
        return t;
    }

    /**
     * 登录设备
     */
    public  boolean loginDev(String m_strIp, int m_nPort, String m_strUser, String m_strPassword) {

        NET_IN_LOGIN_WITH_HIGHLEVEL_SECURITY pstInParam = new NET_IN_LOGIN_WITH_HIGHLEVEL_SECURITY();
        pstInParam.nPort = m_nPort;
        pstInParam.szIP = m_strIp.getBytes();
        pstInParam.szPassword = m_strPassword.getBytes();
        pstInParam.szUserName = m_strUser.getBytes();
        //出参
        NET_OUT_LOGIN_WITH_HIGHLEVEL_SECURITY pstOutParam = new NET_OUT_LOGIN_WITH_HIGHLEVEL_SECURITY();
        pstOutParam.stuDeviceInfo = m_stDeviceInfo;

        IntByReference nError = new IntByReference(0);
        m_hLoginHandle = dhSdk.CLIENT_LoginEx2(m_strIp, m_nPort, m_strUser, m_strPassword, 0, null, m_stDeviceInfo, nError);
        //高安全级别登录产生句柄
        //m_hLoginHandle = dhSdk.CLIENT_LoginWithHighLevelSecurity(pstInParam, pstOutParam);
        if (m_hLoginHandle.longValue() == 0) {
            log.errorf("大华[%s:%d] Login %s %s error:%d", m_strIp, m_nPort,m_strUser,m_strPassword,getLastError());
        } else {
            log.infof("大华[%s:%d] Login Success.Handle:%d",m_strIp,m_nPort,m_hLoginHandle.longValue());
        }

        return m_hLoginHandle.longValue() != 0;
    }

    /**
     * 登出设备
     */
    public boolean logoutDev() {
        if (m_hLoginHandle.longValue() == 0) {
            return false;
        }

        boolean bRet = dhSdk.CLIENT_Logout(m_hLoginHandle);
        if (bRet) {
            m_hLoginHandle.setValue(0);
        }

        return bRet;
    }

    /**
     * 清除环境
     */
    public void cleanup() {
        if (bLogopen) {
            dhSdk.CLIENT_LogClose();
            dhSdk.CLIENT_Cleanup();
        }
    }

    /**
     * 远程抓图
     */
    public boolean remoteCapturePicture(int chn) {
        return snapPicture(chn, 0, 0);
    }

    /**
     * 定时抓图
     */
    public boolean timerCapturePicture(int chn) {
        return snapPicture(chn, 1, 2);
    }

    /**
     * 停止定时抓图
     */
    public boolean stopCapturePicture(int chn) {
        return snapPicture(chn, -1, 0);
    }


    /**
     * 抓图 (除本地抓图外, 其他全部调用此接口)
     */
    private boolean snapPicture(int chn, int mode, int interval) {
        // send caputre picture command to device
        DahuaNetSDK.SNAP_PARAMS stuSnapParams = new DahuaNetSDK.SNAP_PARAMS();
        stuSnapParams.Channel = chn;            // channel
        stuSnapParams.mode = mode;              // 抓图模式；-1:表示停止抓图, 0：表示请求一帧, 1：表示定时发送请求, 2：表示连续请求
        stuSnapParams.Quality = 3;              // picture quality
        stuSnapParams.InterSnap = interval;     // timer capture picture time interval
        stuSnapParams.CmdSerial = 0;            // request serial

        IntByReference reserved = new IntByReference(0);
        if (!dhSdk.CLIENT_SnapPictureEx(m_hLoginHandle, stuSnapParams, reserved)) {
            System.err.printf("CLIENT_SnapPictureEx Failed!");
            return false;
        }
        return true;
    }

    public  void SetStructDataToPointer(Structure pJavaStu, Pointer pNativeData, long OffsetOfpNativeData) {
        pJavaStu.write();
        Pointer pJavaMem = pJavaStu.getPointer();
        pNativeData.write(OffsetOfpNativeData, pJavaMem.getByteArray(0, pJavaStu.size()), 0, pJavaStu.size());
    }

    public  void GetPointerDataToStruct(Pointer pNativeData, long OffsetOfpNativeData, Structure pJavaStu) {
        pJavaStu.write();
        Pointer pJavaMem = pJavaStu.getPointer();
        pJavaMem.write(0, pNativeData.getByteArray(OffsetOfpNativeData, pJavaStu.size()), 0,
                pJavaStu.size());
        pJavaStu.read();
    }
    public  void GetPointerData(Pointer pNativeData, Structure pJavaStu) {
        GetPointerDataToStruct(pNativeData, 0, pJavaStu);
    }
    /**
     * 获取单个配置
     *
     * @param hLoginHandle 登陆句柄
     * @param nChn         通道号，-1 表示全通道
     * @param strCmd       配置名称
     * @param cmdObject    配置对应的结构体对象
     * @return 成功返回 true
     */
    public  boolean GetDevConfig(LLong hLoginHandle, int nChn, String strCmd, Structure cmdObject) {
        IntByReference error = new IntByReference(0);
        IntByReference retLen = new IntByReference(0);
        int nBufferLen = 2 * 1024 * 1024;
        byte[] strBuffer = new byte[nBufferLen];
        if (!dhSdk.CLIENT_GetNewDevConfig(hLoginHandle, strCmd, nChn, strBuffer, nBufferLen, error, 5000,null)) {
            System.err.printf("Get Config Failed!");
            return false;
        }
        cmdObject.write();
        if (!dhConfigSdk.CLIENT_ParseData(strCmd, strBuffer, cmdObject.getPointer(), cmdObject.size(), retLen.getPointer())) {
            System.err.println("Parse Config Failed!");
            return false;
        }
        cmdObject.read();
        return true;
    }

    /**
     * 设置单个配置
     *
     * @param hLoginHandle 登陆句柄
     * @param nChn         通道号，-1 表示全通道
     * @param strCmd       配置名称
     * @param cmdObject    配置对应的结构体对象
     * @return 成功返回 true
     */
    public  boolean SetDevConfig(LLong hLoginHandle, int nChn, String strCmd, Structure cmdObject) {
        boolean result = false;
        int nBufferLen = 2 * 1024 * 1024;
        byte szBuffer[] = new byte[nBufferLen];
        for (int i = 0; i < nBufferLen; i++) szBuffer[i] = 0;
        IntByReference error = new IntByReference(0);
        IntByReference restart = new IntByReference(0);

        cmdObject.write();
        if (dhConfigSdk.CLIENT_PacketData(strCmd, cmdObject.getPointer(), cmdObject.size(),
                szBuffer, nBufferLen)) {
            cmdObject.read();
            if (dhSdk.CLIENT_SetNewDevConfig(hLoginHandle, strCmd, nChn, szBuffer, nBufferLen, error, restart, 5000)) {
                result = true;
            } else {
                System.err.printf("Set Config Failed!");
                result = false;
            }
        } else {
            System.err.println("Packet " + strCmd + " Config Failed!");
            result = false;
        }

        return result;
    }
}
