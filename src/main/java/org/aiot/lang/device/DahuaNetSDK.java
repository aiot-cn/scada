package org.aiot.lang.device;

import com.sun.jna.*;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.win32.StdCallLibrary;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

public interface DahuaNetSDK extends Library {
    //NetSDKLib NETSDK_INSTANCE = Native.load("dhnetsdk",NetSDKLib.class);
    //NetSDKLib CONFIG_INSTANCE = Native.load("dhconfigsdk", NetSDKLib.class);

    class LLong extends IntegerType {
        private static final long serialVersionUID = 1L;

        /** Size of a native long, in bytes. */
        public static int size;
        static {
            size = 8;
        }

        /** Create a zero-valued LLong. */
        public LLong() {
            this(0);
        }

        /** Create a LLong with the given value. */
        public LLong(long value) {
            super(size, value);
        }
    }
    public static class SdkStructure extends Structure {
        @Override
        protected List<String> getFieldOrder(){
            List<String> fieldOrderList = new ArrayList<String>();
            for (Class<?> cls = getClass();
                 !cls.equals(SdkStructure.class);
                 cls = cls.getSuperclass()) {
                Field[] fields = cls.getDeclaredFields();
                int modifiers;
                for (Field field : fields) {
                    modifiers = field.getModifiers();
                    if (Modifier.isStatic(modifiers) || !Modifier.isPublic(modifiers)) {
                        continue;
                    }
                    fieldOrderList.add(field.getName());
                }
            }
            //            System.out.println(fieldOrderList);

            return fieldOrderList;
        }

        @Override
        public int fieldOffset(String name){
            return super.fieldOffset(name);
        }
    }

    /************************************************************************
     ** 常量定义
     ***********************************************************************/
    public static final int NET_SERIALNO_LEN                      = 48;             // 设备序列号字符长度
    public static final int NET_QUERY_DEV_RADIOMETRY_POINT_TEMPER       = 0x0c;                // 查询测温点的参数值, pInBuf= NET_IN_RADIOMETRY_GETPOINTTEMPER*, pOutBuf= NET_OUT_RADIOMETRY_GETPOINTTEMPER *
    public static final int NET_QUERY_DEV_RADIOMETRY_TEMPER             = 0x0d;                // 查询测温项的参数值, pInBuf= NET_IN_RADIOMETRY_GETTEMPER*, pOutBuf= NET_OUT_RADIOMETRY_GETTEMPER *
    public static final int MAX_LOG_PATH_LEN                    = 260;  // 日志路径名最大长度


    // 查询类型,对应CLIENT_QueryDevState接口
    public static final int NET_DEVSTATE_COMM_ALARM            = 0x0001;           // 查询普通报警状态(包括外部报警,视频丢失,动态检测)
    public static final int NET_DEVSTATE_SHELTER_ALARM         = 0x0002;           // 查询遮挡报警状态
    public static final int NET_DEVSTATE_RECORDING             = 0x0003;           // 查询录象状态
    public static final int NET_DEVSTATE_DISK                  = 0x0004;           // 查询硬盘信息
    public static final int NET_DEVSTATE_RESOURCE              = 0x0005;           // 查询系统资源状态
    public static final int NET_DEVSTATE_BITRATE               = 0x0006;           // 查询通道码流
    public static final int NET_DEVSTATE_CONN                  = 0x0007;           // 查询设备连接状态
    public static final int NET_DEVSTATE_PROTOCAL_VER          = 0x0008;           // 查询网络协议版本号,pBuf = int*
    public static final int NET_DEVSTATE_TALK_ECTYPE           = 0x0009;           // 查询设备支持的语音对讲格式列表,见结构体NETDEV_TALKFORMAT_LIST
    public static final int NET_DEVSTATE_SD_CARD               = 0x000A;           // 查询SD卡信息(IPC类产品)
    public static final int NET_DEVSTATE_BURNING_DEV           = 0x000B;           // 查询刻录机信息,见结构体NET_BURNING_DEVINFO
    public static final int NET_DEVSTATE_BURNING_PROGRESS      = 0x000C;           // 查询刻录进度
    public static final int NET_DEVSTATE_PLATFORM              = 0x000D;           // 查询设备支持的接入平台
    public static final int NET_DEVSTATE_CAMERA                = 0x000E;           // 查询摄像头属性信息(IPC类产品),pBuf = NETDEV_CAMERA_INFO *,可以有多个结构体
    public static final int NET_DEVSTATE_SOFTWARE              = 0x000F;           // 查询设备软件版本信息  NETDEV_VERSION_INFO
    public static final int NET_DEVSTATE_LANGUAGE              = 0x0010;           // 查询设备支持的语音种类
    public static final int NET_DEVSTATE_DSP                   = 0x0011;           // 查询DSP能力描述(对应结构体NET_DEV_DSP_ENCODECAP)
    public static final int NET_DEVSTATE_OEM                   = 0x0012;           // 查询OEM信息
    public static final int NET_DEVSTATE_NET                   = 0x0013;           // 查询网络运行状态信息
    public static final int NET_DEVSTATE_TYPE                  = 0x0014;           // 查询设备类型
    public static final int NET_DEVSTATE_SNAP                  = 0x0015;           // 查询功能属性(IPC类产品)
    public static final int NET_DEVSTATE_RECORD_TIME           = 0x0016;           // 查询最早录像时间和最近录像时间
    public static final int NET_DEVSTATE_NET_RSSI              = 0x0017;           // 查询无线网络信号强度,见结构体NETDEV_WIRELESS_RSS_INFO
    public static final int NET_DEVSTATE_BURNING_ATTACH        = 0x0018;           // 查询附件刻录选项
    public static final int NET_DEVSTATE_BACKUP_DEV            = 0x0019;           // 查询备份设备列表
    public static final int NET_DEVSTATE_BACKUP_DEV_INFO       = 0x001a;           // 查询备份设备详细信息 NETDEV_BACKUP_INFO
    public static final int NET_DEVSTATE_BACKUP_FEEDBACK       = 0x001b;           // 备份进度反馈
    public static final int NET_DEVSTATE_ATM_QUERY_TRADE       = 0x001c;           // 查询ATM交易类型
    public static final int NET_DEVSTATE_SIP                   = 0x001d;           // 查询sip状态
    public static final int NET_DEVSTATE_VICHILE_STATE         = 0x001e;           // 查询车载wifi状态
    public static final int NET_DEVSTATE_TEST_EMAIL            = 0x001f;           // 查询邮件配置是否成功
    public static final int NET_DEVSTATE_SMART_HARD_DISK       = 0x0020;           // 查询硬盘smart信息
    public static final int NET_DEVSTATE_TEST_SNAPPICTURE      = 0x0021;           // 查询抓图设置是否成功
    public static final int NET_DEVSTATE_STATIC_ALARM          = 0x0022;           // 查询静态报警状态
    public static final int NET_DEVSTATE_SUBMODULE_INFO        = 0x0023;           // 查询设备子模块信息
    public static final int NET_DEVSTATE_DISKDAMAGE            = 0x0024;           // 查询硬盘坏道能力
    public static final int NET_DEVSTATE_IPC                   = 0x0025;           // 查询设备支持的IPC能力, 见结构体NET_DEV_IPC_INFO
    public static final int NET_DEVSTATE_ALARM_ARM_DISARM      = 0x0026;           // 查询报警布撤防状态
    public static final int NET_DEVSTATE_ACC_POWEROFF_ALARM    = 0x0027;           // 查询ACC断电报警状态(返回一个DWORD, 1表示断电,0表示通电)
    public static final int NET_DEVSTATE_TEST_FTP_SERVER       = 0x0028;           // 测试FTP服务器连接
    public static final int NET_DEVSTATE_3GFLOW_EXCEED         = 0x0029;           // 查询3G流量超出阈值状态,(见结构体 NETDEV_3GFLOW_EXCEED_STATE_INFO)
    public static final int NET_DEVSTATE_3GFLOW_INFO           = 0x002a;           // 查询3G网络流量信息,见结构体 NET_DEV_3GFLOW_INFO
    public static final int NET_DEVSTATE_VIHICLE_INFO_UPLOAD   = 0x002b;           // 车载自定义信息上传(见结构体 ALARM_VEHICLE_INFO_UPLOAD)
    public static final int NET_DEVSTATE_SPEED_LIMIT           = 0x002c;           // 查询限速报警状态(见结构体ALARM_SPEED_LIMIT)
    public static final int NET_DEVSTATE_DSP_EX                = 0x002d;           // 查询DSP扩展能力描述(对应结构体 NET_DEV_DSP_ENCODECAP_EX)
    public static final int NET_DEVSTATE_3GMODULE_INFO         = 0x002e;           // 查询3G模块信息(对应结构体NET_DEV_3GMODULE_INFO)
    public static final int NET_DEVSTATE_MULTI_DDNS            = 0x002f;           // 查询多DDNS状态信息(对应结构体NET_DEV_MULTI_DDNS_INFO)
    public static final int NET_DEVSTATE_CONFIG_URL            = 0x0030;           // 查询设备配置URL信息(对应结构体NET_DEV_URL_INFO)
    public static final int NET_DEVSTATE_HARDKEY               = 0x0031;           // 查询HardKey状态（对应结构体NETDEV_HARDKEY_STATE)
    public static final int NET_DEVSTATE_ISCSI_PATH            = 0x0032;           // 查询ISCSI路径列表(对应结构体NETDEV_ISCSI_PATHLIST)
    public static final int NET_DEVSTATE_DLPREVIEW_SLIPT_CAP   = 0x0033;           // 查询设备本地预览支持的分割模式(对应结构体DEVICE_LOCALPREVIEW_SLIPT_CAP)
    public static final int NET_DEVSTATE_WIFI_ROUTE_CAP        = 0x0034;           // 查询无线路由能力信息(对应结构体NETDEV_WIFI_ROUTE_CAP)
    public static final int NET_DEVSTATE_ONLINE                = 0x0035;           // 查询设备的在线状态(返回一个DWORD, 1表示在线, 0表示断线)
    public static final int NET_DEVSTATE_PTZ_LOCATION          = 0x0036;           // 查询云台状态信息(对应结构体 NET_PTZ_LOCATION_INFO)

    /************************************************************************
     ** 接口
     ***********************************************************************/
    //  JNA直接调用方法定义，cbDisConnect 实际情况并不回调Java代码，仅为定义可以使用如下方式进行定义。 fDisConnect 回调
    public boolean CLIENT_Init(Callback cbDisConnect, Pointer dwUser);

    //  JNA直接调用方法定义，SDK退出清理
    public void CLIENT_Cleanup();

    //  JNA直接调用方法定义，设置断线重连成功回调函数，设置后SDK内部断线自动重连, fHaveReConnect 回调
    public void CLIENT_SetAutoReconnect(Callback cbAutoConnect, Pointer dwUser);

    // 返回函数执行失败代码
    public int CLIENT_GetLastError();

    // 设置连接设备超时时间和尝试次数
    public void CLIENT_SetConnectTime(int nWaitTime, int nTryTimes);

    // 设置登陆网络环境
    public void CLIENT_SetNetworkParam(NET_PARAM pNetParam);

    //
    public boolean CLIENT_SetDeviceSearchParam(NET_DEVICE_SEARCH_PARAM pstParam);

    // 获取SDK的版本信息
    public int CLIENT_GetSDKVersion();

    //  JNA直接调用方法定义，登陆接口
    public LLong CLIENT_LoginEx(String pchDVRIP, int wDVRPort, String pchUserName, String pchPassword, int nSpecCap, Pointer pCapParam, NET_DEVICEINFO lpDeviceInfo, IntByReference error/*= 0*/);

    //  JNA直接调用方法定义，登陆扩展接口///////////////////////////////////////////////////
    //  nSpecCap 对应  EM_LOGIN_SPAC_CAP_TYPE 登陆类型
    public LLong CLIENT_LoginEx2(String pchDVRIP, int wDVRPort, String pchUserName, String pchPassword, int nSpecCap, Pointer pCapParam, NET_DEVICEINFO_Ex lpDeviceInfo, IntByReference error/*= 0*/);

    //  JNA直接调用方法定义，向设备注销
    public boolean CLIENT_Logout(LLong lLoginID);

    // 获取配置
    // error 为设备返回的错误码： 0-成功 1-失败 2-数据不合法 3-暂时无法设置 4-没有权限
    public boolean CLIENT_GetNewDevConfig(LLong lLoginID,String szCommand,int nChannelID,byte[] szOutBuffer,int dwOutBufferSize,IntByReference error,int waiitime,Pointer pReserved);

    // 设置配置
    public boolean CLIENT_SetNewDevConfig(LLong lLoginID, String szCommand, int nChannelID, byte[] szInBuffer, int dwInBufferSize, IntByReference error, IntByReference restart, int waittime);

    // 解析查询到的配置信息
    public boolean CLIENT_ParseData(String szCommand,byte[] szInBuffer,Pointer lpOutBuffer,int dwOutBufferSize,Pointer pReserved);

    // 组成要设置的配置信息
    public boolean CLIENT_PacketData(String szCommand, Pointer lpInBuffer, int dwInBufferSize, byte[] szOutBuffer, int dwOutBufferSize);

    // 设置报警回调函数, fMessCallBack 回调
    public void  CLIENT_SetDVRMessCallBack(Callback cbMessage, Pointer dwUser);

    // 设置报警回调函数, fMessCallBackEx1 回调
    public void  CLIENT_SetDVRMessCallBackEx1(fMessCallBackEx1 cbMessage, Pointer dwUser);

    // 向设备订阅报警--扩展
    public boolean  CLIENT_StartListenEx(LLong lLoginID);

    // 停止订阅报警
    public boolean CLIENT_StopListen(LLong lLoginID);


    //结束查询
    public boolean  CLIENT_StopFindFaceRecognition(LLong lFindHandle);

    // 高安全级别登陆
    public LLong CLIENT_LoginWithHighLevelSecurity(NET_IN_LOGIN_WITH_HIGHLEVEL_SECURITY pstInParam, NET_OUT_LOGIN_WITH_HIGHLEVEL_SECURITY pstOutParam);

    // 打开日志功能
    // pstLogPrintInfo指向LOG_SET_PRINT_INFO的指针
    public boolean CLIENT_LogOpen(LOG_SET_PRINT_INFO pstLogPrintInfo);

    // 关闭日志功能
    public boolean CLIENT_LogClose();

    // 抓图, hPlayHandle为监视或回放句柄
    public boolean CLIENT_CapturePictureEx(LLong hPlayHandle, String pchPicFileName, int eFormat);

    // 抓图请求扩展接口
    public boolean CLIENT_SnapPictureEx(LLong lLoginID, SNAP_PARAMS stParam, IntByReference reserved);

    // 设置抓图回调函数, fSnapRev回调
    public void CLIENT_SetSnapRevCallBack(Callback OnSnapRevMessage, Pointer dwUser);

    // 私有云台控制扩展接口,支持三维快速定位
    public boolean CLIENT_DHPTZControlEx(LLong lLoginID, int nChannelID, int dwPTZCommand, int lParam1, int lParam2, int lParam3, int dwStop);


    // 云台控制扩展接口,支持三维快速定位,鱼眼
    // dwStop类型为BOOL, 取值0或者1
    // dwPTZCommand取值为NET_EXTPTZ_ControlType中的值或者是NET_PTZ_ControlType中的值
    // NET_IN_PTZBASE_MOVEABSOLUTELY_INFO
    // 精准绝对移动控制命令, param4对应结构 NET_IN_PTZBASE_MOVEABSOLUTELY_INFO（通过 CFG_CAP_CMD_PTZ 命令获取云台能力集( CFG_PTZ_PROTOCOL_CAPS_INFO )，若bSupportReal为TRUE则设备支持该操作）
    public boolean CLIENT_DHPTZControlEx2(LLong lLoginID,int nChannelID,int dwPTZCommand,int lParam1,int lParam2,int lParam3,int dwStop,Pointer param4);


    // 查询设备状态(pBuf内存由用户申请释放)
    // pBuf指向char *,输出参数
    // pRetLen指向int *;输出参数，实际返回的数据长度，单位字节
    public boolean CLIENT_QueryDevState(LLong lLoginID, int nType, Pointer pBuf, int nBufLen, IntByReference pRetLen, int waittime);


    // 获取设备能力接口
    // pInBuf指向void*，输入参数结构体指针       pOutBuf指向void*，输出参数结构体指针
    public boolean CLIENT_GetDevCaps(LLong lLoginID,int nType,Pointer pInBuf,Pointer pOutBuf,int nWaitTime);


    // 获取配置信息(szOutBuffer内存由用户申请释放, 具体见枚举类型 NET_EM_CFG_OPERATE_TYPE 说明)
    public boolean CLIENT_GetConfig(LLong lLoginID,int emCfgOpType,int nChannelID,Pointer szOutBuffer,int dwOutBufferSize,int waittime,Pointer reserve);

    // 设置配置信息(szInBuffer内存由用户申请释放, 具体见枚举类型 NET_EM_CFG_OPERATE_TYPE 说明)
    public boolean CLIENT_SetConfig(LLong lLoginID,int emCfgOpType,int nChannelID,Pointer szInBuffer,int dwInBufferSize,int waittime,IntByReference restart,Pointer reserve);
    /***********************************************************************
     ** 回调
     ***********************************************************************/
    //JNA Callback方法定义,断线回调
    public interface fDisConnect extends StdCallLibrary.StdCallCallback {
        public void invoke(LLong lLoginID, String pchDVRIP, int nDVRPort, Pointer dwUser);
    }
    public interface fDisConnectLinux extends Callback{
        public void invoke(LLong lLoginID, String pchDVRIP, int nDVRPort, Pointer dwUser);
    }

    // 网络连接恢复回调函数原形
    public interface fHaveReConnect extends StdCallLibrary.StdCallCallback {
        public void invoke(LLong lLoginID, String pchDVRIP, int nDVRPort, Pointer dwUser);
    }
    public interface fHaveReConnectLinux extends Callback{
        public void invoke(LLong lLoginID, String pchDVRIP, int nDVRPort, Pointer dwUser);
    }

    // 消息回调函数原形(pBuf内存由SDK内部申请释放)
    // 新增参数说明
    // bAlarmAckFlag : TRUE,该事件为可以进行确认的事件；FALSE,该事件无法进行确认
    // nEventID 用于对 CLIENT_AlarmAck 接口的入参进行赋值,当 bAlarmAckFlag 为 TRUE 时,该数据有效
    // pBuf内存由SDK内部申请释放
    public interface fMessCallBackEx1 extends StdCallLibrary.StdCallCallback {
        public boolean invoke(int lCommand, LLong lLoginID, Pointer pStuEvent, int dwBufLen, String strDeviceIP, NativeLong nDevicePort, int bAlarmAckFlag, NativeLong nEventID, Pointer dwUser);
    }
    public interface fMessCallBackEx1Linux extends Callback{
        public boolean invoke(int lCommand, LLong lLoginID, Pointer pStuEvent, int dwBufLen, String strDeviceIP, NativeLong nDevicePort, int bAlarmAckFlag, NativeLong nEventID, Pointer dwUser);
    }

    // 抓图回调函数原形(pBuf内存由SDK内部申请释放)
    // EncodeType 编码类型，10：表示jpeg图片      0：mpeg4    CmdSerial : 操作流水号，同步抓图的情况下用不上
    public interface fSnapRev extends StdCallLibrary.StdCallCallback {
        public void invoke(LLong lLoginID, Pointer pBuf, int RevLen, int EncodeType, int CmdSerial, Pointer dwUser);
    }
    public interface fSnapRevLinux extends Callback{
        public void invoke(LLong lLoginID, Pointer pBuf, int RevLen, int EncodeType, int CmdSerial, Pointer dwUser);
    }


    //----------------------语音对讲--------------------------
    // 向设备发起语音对讲请求          pfcb是用户自定义的数据回调接口, pfAudioDataCallBack 回调
    public LLong CLIENT_StartTalkEx(LLong lLoginID, Callback pfcb, Pointer dwUser);

    // 停止语音对讲        lTalkHandle语音对讲句柄，是CLIENT_StartTalkEx的返回 值
    public boolean CLIENT_StopTalkEx(LLong lTalkHandle);

    // 启动本地录音功能(只在Windows平台下有效)，录音采集出来的音频数据通过CLIENT_StartTalkEx的回调函数回调给用户，对应操作是CLIENT_RecordStopEx
    // lLoginID是CLIENT_Login的返回值
    public boolean CLIENT_RecordStartEx(LLong lLoginID);

    // 开始PC端录音
    public boolean CLIENT_RecordStart();

    // 结束PC端录音
    public boolean CLIENT_RecordStop();

    // 停止本地录音(只在Windows平台下有效)，对应操作是CLIENT_RecordStartEx。
    public boolean CLIENT_RecordStopEx(LLong lLoginID);

    // 向设备发送用户的音频数据，这里的数据可以是从CLIENT_StartTalkEx的回调接口中回调出来的数据
    public LLong CLIENT_TalkSendData(LLong lTalkHandle, Pointer pSendBuf, int dwBufSize);
    /**
     * 打开语音对讲，这个接口可以从回调中得到音频裸数据，而CLIENT_StartTalkEx只能得到带音频头的数据
     * @param pInParam -> NET_IN_START_TALK_INFO
     * @param pOutParam -> NET_OUT_START_TALK_INFO
     */
    public LLong CLIENT_StartTalkByDataType(LLong lLoginID, Pointer pInParam, Pointer pOutParam,int nWaittime);
    /**
     * 发送语音数据到设备 返回值为发送给设备的音频流长度，-1表示接口调用失败
     * @param pInParam -> NET_IN_TALK_SEND_DATA_STREAM
     * @param pOutParam -> NET_OUT_TALK_SEND_DATA_STREAM
     * @return LLong 返回值为发送给设备的音频流长度，-1表示接口调用失败
     */
    public LLong CLIENT_TalkSendDataByStream(LLong lTalkHandle, Pointer pInParam, Pointer pOutParam);

    /**
     * 发送语音文件中的音频数据到设备 成功返回 lTalkHandle， 失败返回 0
     * @param pInParam -> NET_IN_TALK_SEND_DATA_FILE
     * @param pOutParam -> NET_OUT_TALK_SEND_DATA_FILE
     * @return LLong 成功返回 lTalkHandle， 失败返回 0
     */
    public LLong CLIENT_TalkSendDataByFile(LLong lTalkHandle, Pointer pInParam, Pointer pOutParam);


    /**
     * 停止发送音频文件
     */
    public boolean CLIENT_StopTalkSendDataByFile(LLong lTalkHandle);


    // 解码音频数据扩展接口(只在Windows平台下有效)    pAudioDataBuf是要求解码的音频数据内容
    public void CLIENT_AudioDec(Pointer pAudioDataBuf, int dwBufSize);
    public boolean CLIENT_AudioDecEx(LLong lTalkHandle, Pointer pAudioDataBuf, int dwBufSize);


    // 设置语音对讲模式,客户端方式还是服务器方式
    // emType : 方式类型 参照 EM_USEDEV_MODE
    public boolean CLIENT_SetDeviceMode(LLong lLoginID, int emType, Pointer pValue);


    // 用户自定义的数据回调   lTalkHandle是CLIENT_StartTalkEx的返回值
    // byAudioFlag：   0表示是本地录音库采集的音频数据 ，  1表示收到的设备发过来的音频数据
    public interface pfAudioDataCallBack extends StdCallLibrary.StdCallCallback {
        public void invoke(LLong lTalkHandle, Pointer pDataBuf, int dwBufSize, byte byAudioFlag, Pointer dwUser);
    }

    public interface pfAudioDataCallBackLinux extends Callback{
        public void invoke(LLong lTalkHandle, Pointer pDataBuf, int dwBufSize, byte byAudioFlag, Pointer dwUser);
    }

    //音频文件发送进度回调函数
    public interface fTalkSendPosCallBack extends StdCallLibrary.StdCallCallback {
        public void invoke(LLong lTalkHandle, int dwTotalSize, int dwSendSize,Pointer dwUser);
    }

    public interface fTalkSendPosCallBackLinux extends Callback {
        public void invoke(LLong lTalkHandle, int dwTotalSize, int dwSendSize,Pointer dwUser);
    }

    public interface fAudioDataCallBackEx extends StdCallLibrary.StdCallCallback {
        public void invoke(LLong lTalkHandle, NET_AUDIO_DATA_CB_INFO stAudioInfo, int emAudioFlag,Pointer dwUser);
    }

    public interface fAudioDataCallBackExLinux extends Callback {
        public void invoke(LLong lTalkHandle, NET_AUDIO_DATA_CB_INFO stAudioInfo, int emAudioFlag,Pointer dwUser);
    }



    /************************************************************************
     ** 结构体
     ***********************************************************************/
    // 设置登入时的相关参数
    public static class NET_PARAM  extends SdkStructure
    {
        public int                    nWaittime;                // 等待超时时间(毫秒为单位)，为0默认5000ms
        public int                    nConnectTime;            	// 连接超时时间(毫秒为单位)，为0默认1500ms
        public int                    nConnectTryNum;           // 连接尝试次数，为0默认1次
        public int                    nSubConnectSpaceTime;    	// 子连接之间的等待时间(毫秒为单位)，为0默认10ms
        public int                    nGetDevInfoTime;        	// 获取设备信息超时时间，为0默认1000ms
        public int                    nConnectBufSize;        	// 每个连接接收数据缓冲大小(字节为单位)，为0默认250*1024
        public int                    nGetConnInfoTime;         // 获取子连接信息超时时间(毫秒为单位)，为0默认1000ms
        public int                    nSearchRecordTime;      	// 按时间查询录像文件的超时时间(毫秒为单位),为0默认为3000ms
        public int                    nsubDisconnetTime;      	// 检测子链接断线等待时间(毫秒为单位)，为0默认为60000ms
        public byte                   byNetType;                // 网络类型, 0-LAN, 1-WAN
        public byte                   byPlaybackBufSize;      	// 回放数据接收缓冲大小（M为单位），为0默认为4M
        public byte                   bDetectDisconnTime;       // 心跳检测断线时间(单位为秒),为0默认为60s,最小时间为2s
        public byte                   bKeepLifeInterval;        // 心跳包发送间隔(单位为秒),为0默认为10s,最小间隔为2s
        public int                    nPicBufSize;              // 实时图片接收缓冲大小（字节为单位），为0默认为2*1024*1024
        public byte[]                 bReserved = new byte[4];  // 保留字段字段
    }

    // 测温信息
    public static class NET_RADIOMETRYINFO extends SdkStructure
    {
        public int                 nMeterType;                         // 返回测温类型,见NET_RADIOMETRY_METERTYPE
        public int                 nTemperUnit;                        // 温度单位(当前配置的温度单位),见 NET_TEMPERATURE_UNIT
        public float               fTemperAver;                        // 点的温度或者平均温度   点的时候 只返回此字段
        public float               fTemperMax;                         // 最高温度
        public float               fTemperMin;                         // 最低温度
        public float               fTemperMid;                         // 中间温度值
        public float               fTemperStd;                         // 标准方差值
        public byte[]              reserved = new byte[64];
    }

    // CLIENT_QueryDevInfo 接口 NET_QUERY_DEV_RADIOMETRY_TEMPER 命令入参
    public static class NET_IN_RADIOMETRY_GETTEMPER extends SdkStructure
    {
        public int                         dwSize;
        public NET_RADIOMETRY_CONDITION    stCondition;                // 获取测温项温度的条件

        public NET_IN_RADIOMETRY_GETTEMPER() {
            this.dwSize = this.size();
        }
    }

    // 获取测温项温度的条件
    public static class NET_RADIOMETRY_CONDITION extends SdkStructure
    {
        public int                 nPresetId;                          // 预置点编号
        public int                 nRuleId;                            // 规则编号
        public int                 nMeterType;                         // 测温项类别,见NET_RADIOMETRY_METERTYPE
        public byte[]              szName = new byte[64];              // 测温项的名称,从测温配置规则名字中选取
        public int                 nChannel;                           // 通道号
        public byte[]              reserved = new byte[256];
    }

    // CLIENT_QueryDevInfo 接口 NET_QUERY_DEV_RADIOMETRY_POINT_TEMPER 命令入参
    public static class NET_IN_RADIOMETRY_GETPOINTTEMPER extends SdkStructure {
        public int               dwSize;
        public int               nChannel;                           // 通道号
        public NET_POINT         stCoordinate;                       // 测温点的坐标,坐标值 0~8192

        public NET_IN_RADIOMETRY_GETPOINTTEMPER() {
            this.dwSize = this.size();
        }
    }

    //二维空间点
    public static class NET_POINT extends SdkStructure
    {
        public short nx;
        public short ny;

        @Override
        public String toString() {
            return "NET_POINT{" +
                    "nx=" + nx +
                    ", ny=" + ny +
                    '}';
        }

        public NET_POINT() {
        }
    }

    // CLIENT_QueryDevInfo 接口 NET_QUERY_DEV_RADIOMETRY_POINT_TEMPER 命令出参
    public static class NET_OUT_RADIOMETRY_GETPOINTTEMPER extends SdkStructure
    {
        public int               	dwSize;
        public NET_RADIOMETRYINFO   stPointTempInfo;                    // 获取测温点的参数值

        public NET_OUT_RADIOMETRY_GETPOINTTEMPER() {
            this.dwSize = this.size();
        }
    }

    // 查询设备信息
    public boolean CLIENT_QueryDevInfo(LLong lLoginID, int nQueryType, Pointer pInBuf, Pointer pOutBuf, Pointer pReservedL, int nWaitTime);

    // CLIENT_QueryDevInfo 接口 NET_QUERY_DEV_RADIOMETRY_TEMPER 命令出参
    public static class NET_OUT_RADIOMETRY_GETTEMPER extends SdkStructure
    {
        public int               	dwSize;
        public NET_RADIOMETRYINFO   stTempInfo;                         // 获取测温参数值

        public NET_OUT_RADIOMETRY_GETTEMPER() {
            this.dwSize = this.size();
        }
    }

    // CLIENT_LoginWithHighLevelSecurity 输入参数
    public static class NET_IN_LOGIN_WITH_HIGHLEVEL_SECURITY extends SdkStructure
    {
        public int						    dwSize;				            // 结构体大小
        public byte[]						szIP=new byte[64];	            // IP
        public int							nPort;				            // 端口
        public byte[]						szUserName=new byte[64];		// 用户名
        public byte[]						szPassword=new byte[64];		// 密码
        public int		                    emSpecCap;			            // 登录模式
        public byte[]						byReserved=new byte[4];		    // 字节对齐
        public Pointer pCapParam;			            // 见 CLIENT_LoginEx 接口 pCapParam 与 nSpecCap 关系

        public NET_IN_LOGIN_WITH_HIGHLEVEL_SECURITY()
        {
            this.dwSize = this.size();
        }// 此结构体大小
    };

    // CLIENT_LoginWithHighLevelSecurity 输出参数
    public static class NET_OUT_LOGIN_WITH_HIGHLEVEL_SECURITY extends SdkStructure
    {
        public int						    dwSize;				            // 结构体大小
        public NET_DEVICEINFO_Ex			stuDeviceInfo;		            // 设备信息
        public int							nError;				            // 错误码，见 CLIENT_Login 接口错误码
        public byte[]						byReserved=new byte[132];	    // 预留字段

        public NET_OUT_LOGIN_WITH_HIGHLEVEL_SECURITY()
        {
            this.dwSize = this.size();
        }// 此结构体大小
    };

    // 语音编码信息
    public static class NETDEV_TALKDECODE_INFO extends SdkStructure
    {
        public int                 encodeType;                       // 编码类型, encodeType对应NET_TALK_CODING_TYPE
        public int                 nAudioBit;                        // 位数,如8或16等
        public int                 dwSampleRate;                     // 采样率,如8000或16000等
        public int                 nPacketPeriod;                    // 打包周期, 单位ms, 目前只能是25
        public byte[]    		   reserved = new byte[60];
    }


    // 语音编码类型
    public static class NET_TALK_CODING_TYPE extends SdkStructure
    {
        public static final int NET_TALK_DEFAULT = 0;            // 无头PCM
        public static final int NET_TALK_PCM = 1;                // 带头PCM
        public static final int	NET_TALK_G711a = 2;              // G711a
        public static final int NET_TALK_AMR = 3;                // AMR
        public static final int	NET_TALK_G711u = 4;              // G711u
        public static final int	NET_TALK_G726 = 5;               // G726
        public static final int	NET_TALK_G723_53 = 6;            // G723_53
        public static final int NET_TALK_G723_63 = 7;            // G723_63
        public static final int	NET_TALK_AAC = 8;                // AAC
        public static final int	NET_TALK_OGG = 9;                // OGG
        public static final int	NET_TALK_G729 = 10;              // G729
        public static final int	NET_TALK_MPEG2 = 11;             // MPEG2
        public static final int	NET_TALK_MPEG2_Layer2 = 12;      // MPEG2-Layer2
        public static final int	NET_TALK_G722_1 = 13;            // G.722.1
        public static final int	NET_TALK_ADPCM = 21;             // ADPCM
        public static final int	NET_TALK_MP3   = 22;             // MP3
    }


    // 对讲方式
    public static class EM_USEDEV_MODE extends SdkStructure
    {
        public static final int NET_TALK_CLIENT_MODE 	  = 0;   // 设置客户端方式进行语音对讲
        public static final int NET_TALK_SERVER_MODE 	  = 1;   // 设置服务器方式进行语音对讲
        public static final int NET_TALK_ENCODE_TYPE 	  = 2;   // 设置语音对讲编码格式(对应 NETDEV_TALKDECODE_INFO)
        public static final int NET_ALARM_LISTEN_MODE 	  = 3;   // 设置报警订阅方式
        public static final int NET_CONFIG_AUTHORITY_MODE = 4;   // 设置通过权限进行配置管理
        public static final int NET_TALK_TALK_CHANNEL 	  = 5;   // 设置对讲通道(0~MaxChannel-1)
        public static final int NET_RECORD_STREAM_TYPE	  = 6;   // 设置待查询及按时间回放的录像码流类型(0-主辅码流,1-主码流,2-辅码流)
        public static final int NET_TALK_SPEAK_PARAM      = 7;   // 设置语音对讲喊话参数,对应结构体 NET_SPEAK_PARAM
        public static final int NET_RECORD_TYPE           = 8;   // 设置按时间录像回放及下载的录像文件类型(详见  NET_RECORD_TYPE)
        public static final int NET_TALK_MODE3            = 9;   // 设置三代设备的语音对讲参数, 对应结构体 NET_TALK_EX
        public static final int NET_PLAYBACK_REALTIME_MODE = 10; // 设置实时回放功能(0-关闭,1开启)
        public static final int NET_TALK_TRANSFER_MODE    = 11;  // 设置语音对讲是否为转发模式, 对应结构体 NET_TALK_TRANSFER_PARAM
        public static final int NET_TALK_VT_PARAM         = 12;  // 设置VT对讲参数, 对应结构体 NET_VT_TALK_PARAM
        public static final int NET_TARGET_DEV_ID         = 13;  // 设置目标设备标示符, 用以查询新系统能力(非0-转发系统能力消息)
    }

    // 语音对讲喊话参数
    public static class NET_SPEAK_PARAM extends SdkStructure
    {
        public int 				  dwSize;                     		// 结构体大小
        public int 				  nMode;                      		// 0：对讲（默认模式）,1：喊话；从喊话切换到对讲要重新设置
        public int 				  nSpeakerChannel;           		// 扬声器通道号,喊话时有效
        public boolean 			  bEnableWait;            			// 开启对讲时是否等待设备的响应,默认不等待.TRUE:等待;FALSE:不等待
        // 超时时间由CLIENT_SetNetworkParam设置,对应NET_PARAM的nWaittime字段
        public NET_SPEAK_PARAM()
        {
            this.dwSize = this.size();
        }
    }

    // 是否开启语音对讲的转发模式
    public static class NET_TALK_TRANSFER_PARAM extends SdkStructure
    {
        public int 				 dwSize;
        public int 			 	 bTransfer;                 	   // 是否开启语音对讲转发模式, TRUE: 开启转发

        public NET_TALK_TRANSFER_PARAM()
        {
            this.dwSize = this.size();
        }
    }

    public static class NET_IN_TALK_SEND_DATA_FILE extends SdkStructure{

        public int dwSize;//结构体大小
        public Pointer pFilePath;//音频文件全路径
        public fTalkSendPosCallBack cbSendPos;//音频文件发送进度
        public Pointer dwUser;//用户参数
        public int dwSendInterval;//发送间隔百分比，0和100表示使用sdk默认计算的间隔，于100发送更快，大于100，发送更慢

        /**
         *  是否需要加音频头。
         *  TRUE，表示需要SDK根据下面的音频信息加音频头；
         *  FALSE，表示不需要SDK根据音频信息加音频头，直接发送pFilePath路径指向的数据给设备。
         */
        public boolean bNeedHead;
        public int emEncodeType;//音频编码格式,参考枚举{ @link NET_TALK_CODING_TYPE }
        public int nAudioBit;//位数,如8或16等
        public int dwSampleRate;//采样率,如8000或16000等

        public NET_IN_TALK_SEND_DATA_FILE(){
            this.dwSize = this.size();
        }

    }

    public static class NET_OUT_TALK_SEND_DATA_FILE extends SdkStructure{
        public int dwSize;

        public NET_OUT_TALK_SEND_DATA_FILE(){
            this.dwSize = this.size();
        }
    }

    public static class NET_IN_TALK_SEND_DATA_STREAM extends SdkStructure{

        public int dwSize;
        public Pointer pBuf;//音频流缓冲
        public int dwBufSize;//音频流缓冲大小

        /**
         *  是否需要加音频头。
         *  TRUE，表示需要SDK根据下面的音频信息加音频头；
         *  FALSE，表示不需要SDK根据音频信息加音频头，直接发送pBuf指向的数据给设备。
         */
        public boolean bNeedHead;
        public int emEncodeType;//音频编码格式,参考枚举{ @link NET_TALK_CODING_TYPE }
        public int nAudioBit;//位数,如8或16等
        public int dwSampleRate;//采样率,如8000或16000等

        public NET_IN_TALK_SEND_DATA_STREAM(){
            this.dwSize = this.size();
        }
    }

    //fAudioDataCallBackEx 回调音频信息
    public static class NET_AUDIO_DATA_CB_INFO extends SdkStructure{
        public Pointer pBuf;//带音频头的音频数据
        public int dwBufSize;//带音频头的音频数据长度
        public int emAudioCode;//音频编码格式,参考枚举{ @link NET_TALK_CODING_TYPE }
        public Pointer pRawBuf;//不带音频头的音频裸数据
        public int dwRawBufSize;//不带音频头的音频数据长度
        public int nAudioBit;//位数,如8或16等
        public int dwSampleRate;//采样率,如8000或16000等
        public byte[] bReserved = new byte[256];
    }

    //CLIENT_TalkSendDataByStream接口 出参
    public static class NET_OUT_TALK_SEND_DATA_STREAM extends SdkStructure{
        public int dwSize;
        public NET_OUT_TALK_SEND_DATA_STREAM(){this.dwSize = this.size();}
    }

    //CLIENT_StartTalkByDataType接口 入参
    public static class NET_IN_START_TALK_INFO extends SdkStructure{
        /**
         *  结构体大小
         */
        public int dwSize;

        /**
         *  音频数据回调函数,实现时使用{ @link fAudioDataCallBackEx }
         */
        public fAudioDataCallBackEx pfAudioDataCallBackEx;

        /**
         *  pfAudioDataCallBackEx回调对应的用户指针
         */
        public Pointer dwUser;

        public NET_IN_START_TALK_INFO(){
            this.dwSize = this.size();
        }
    }
    //CLIENT_StartTalkByDataType接口 出参
    public static class NET_OUT_START_TALK_INFO extends SdkStructure{
        public int dwSize;
        public NET_OUT_START_TALK_INFO(){
            this.dwSize = this.size();
        }
    }

    // 设备设备参数
    public static class NET_DEVICE_SEARCH_PARAM extends SdkStructure {

        public int       	dwSize;						// 结构体大小
        /**
         * 是否使用默认配置,默认为TRUE
         */
        public int        	bUseDefault;
        /**
         * 广播本地端口, 默认5050, 值为0时使用最近一次配置
         */
        public short        wBroadcastLocalPort;
        /**
         * 广播远程端口, 默认5050, 值为0时使用最近一次配置
         */
        public short        wBroadcastRemotePort;
        /**
         * 组播远程端口, 默认37810, 值为0时使用最近一次配置
         */
        public short        wMulticastRemotePort;
        /**
         * 组播修改设备时是否只支持组播回复,默认FALSE表示单播或组播回复
         */
        public int        	bMulticastModifyRespond;
        /**
         * 组播本地端口, 默认37810, 值为0时使用最近一次配置
         */
        public short        wMulticastLocalPort;
        /**
         * 端口不可用时自动更新端口次数,默认50次，范围[0-65534]
         */
        public int			iAutoUpdatePortTimes;
        /**
         * AOL 组播远程端口, 默认8087, 值为0时使用最近一次配置
         */
        public short        wAOLMulticastRemotePort;
        /**
         * AOL 组播本地端口, 默认37811, 值为0时使用最近一次配置
         */
        public short        wAOLMulticastLocalPort;

        public NET_DEVICE_SEARCH_PARAM() {
            this.dwSize = this.size();
        }
    }

    // 设备信息
    public static class NET_DEVICEINFO extends SdkStructure {
        public byte[]              sSerialNumber = new byte[NET_SERIALNO_LEN];    // 序列号
        public byte                byAlarmInPortNum;         // DVR报警输入个数
        public byte                byAlarmOutPortNum;        // DVR报警输出个数
        public byte                byDiskNum;                // DVR硬盘个数
        public byte                byDVRType;                // DVR类型, 见枚举NET_DEV_DEVICE_TYPE
        public union 			   union = new union();
        public static class union extends Union {
            public byte                byChanNum;                // DVR通道个数
            public byte                byLeftLogTimes;           // 当登陆失败原因为密码错误时,通过此参数通知用户,剩余登陆次数,为0时表示此参数无效
        }
    }

    // 设备信息扩展///////////////////////////////////////////////////
    public static class NET_DEVICEINFO_Ex extends SdkStructure {
        public byte[]     sSerialNumber = new byte[NET_SERIALNO_LEN];    // 序列号
        public int        byAlarmInPortNum;                              // DVR报警输入个数
        public int        byAlarmOutPortNum;                             // DVR报警输出个数
        public int        byDiskNum;                                     // DVR硬盘个数
        public int        byDVRType;                                     // DVR类型,见枚举NET_DEVICE_TYPE
        public int        byChanNum;                                     // DVR通道个数
        public byte       byLimitLoginTime;                              // 在线超时时间,为0表示不限制登陆,非0表示限制的分钟数
        public byte       byLeftLogTimes;                                // 当登陆失败原因为密码错误时,通过此参数通知用户,剩余登陆次数,为0时表示此参数无效
        public byte[]     bReserved = new byte[2];                       // 保留字节,字节对齐
        public int        byLockLeftTime;                                // 当登陆失败,用户解锁剩余时间（秒数）, -1表示设备未设置该参数
        public byte[]     Reserved = new byte[24];                       // 保留
    }

    // SDK全局日志打印信息
    public static class LOG_SET_PRINT_INFO extends SdkStructure
    {
        public int 		dwSize;
        public int 		bSetFilePath;//是否重设日志路径, BOOL类型，取值0或1
        public byte[] 	szLogFilePath = new byte[MAX_LOG_PATH_LEN];//日志路径(默认"./sdk_log/sdk_log.log")
        public int 		bSetFileSize;//是否重设日志文件大小, BOOL类型，取值0或1
        public int 		nFileSize;//每个日志文件的大小(默认大小10240),单位:比特, 类型为unsigned int
        public int 		bSetFileNum;//是否重设日志文件个数, BOOL类型，取值0或1
        public int 		nFileNum;//绕接日志文件个数(默认大小10), 类型为unsigned int
        public int 		bSetPrintStrategy;//是否重设日志打印输出策略, BOOL类型，取值0或1
        public int 		nPrintStrategy;//日志输出策略,0:输出到文件(默认); 1:输出到窗口, 类型为unsigned int
        public byte[]	byReserved=new byte[4];							// 字节对齐
        public Pointer	cbSDKLogCallBack;						// 日志回调，需要将sdk日志回调出来时设置，默认为NULL
        public Pointer	dwUser;									// 用户数据
        public LOG_SET_PRINT_INFO()
        {
            this.dwSize = this.size();
        }
    }

    // 抓图类型
    public static class NET_CAPTURE_FORMATS extends SdkStructure
    {
        public static final int    NET_CAPTURE_BMP = 0;
        public static final int    NET_CAPTURE_JPEG  = 1;           // 100%质量的JPEG
        public static final int    NET_CAPTURE_JPEG_70 = 2;         // 70%质量的JPEG
        public static final int    NET_CAPTURE_JPEG_50 = 3;
        public static final int    NET_CAPTURE_JPEG_30 = 4;
    }

    // 抓图参数结构体
    public static class SNAP_PARAMS extends SdkStructure
    {
        public int     Channel;                       // 抓图的通道
        public int     Quality;                       // 画质；1~6
        public int     ImageSize;                     // 画面大小；0：QCIF,1：CIF,2：D1
        public int     mode;                          // 抓图模式；-1:表示停止抓图, 0：表示请求一帧, 1：表示定时发送请求, 2：表示连续请求
        public int     InterSnap;                     // 时间单位秒；若mode=1表示定时发送请求时
        // 只有部分特殊设备(如：车载设备)支持通过该字段实现定时抓图时间间隔的配置
        // 建议通过 CFG_CMD_ENCODE 配置的stuSnapFormat[nSnapMode].stuVideoFormat.nFrameRate字段实现相关功能
        public int     CmdSerial;                     // 请求序列号，有效值范围 0~65535，超过范围会被截断为 unsigned short
        public int[]   Reserved = new int[4];
    }

    // 串口基本属性
    public static class CFG_COMM_PROP extends SdkStructure
    {
        public byte                           byDataBit;                            // 数据位；0：5，1：6，2：7，3：8
        public byte                           byStopBit;                            // 停止位；0：1位，1：1.5位，2：2位
        public byte                           byParity;                             // 校验位；0：无校验，1：奇校验；2：偶校验
        public byte                           byBaudRate;                           // 波特率；0：300，1：600，2：1200，3：2400，4：4800，
        // 5：9600，6：19200，7：38400，8：57600，9：115200
    }

    // 归位预置点配置
    public static class CFG_PRESET_HOMING extends SdkStructure
    {
        public int                            nPtzPresetId;                         // 云台预置点编号	0~65535
        //-1表示无效
        public int                            nFreeSec;                             // 空闲的时间，单位为秒
    }

    //云台控制坐标单元
    public static class PTZ_SPACE_UNIT extends SdkStructure
    {
        public int nPositionX;  // 云台水平运动位置,有效范围：0,3600]
        public int nPositionY;  // 云台垂直运动位置,有效范围：-1800,1800]
        public int nZoom;       // 云台光圈变动位置,有效范围：0,128]
        public byte[] szReserve = new byte[32];//预留32字节
    }


    // 云台控制坐标单元
    public static class CFG_PTZ_SPACE_UNIT extends SdkStructure
    {
        public int                 nPositionX;           //云台水平运动位置，有效范围：[0,3600]
        public int                 nPositionY;           //云台垂直运动位置，有效范围：[-1800,1800]
        public int                 nZoom;                //云台光圈变动位置，有效范围：[0,128]
    }

    // 云台控制预置点结构
    public static class PTZ_PRESET extends SdkStructure
    {
        public int                 bEnable;                             			// 该预置点是否生效
        public byte[]              szName = new byte[64];      // 预置点名称
        public CFG_PTZ_SPACE_UNIT  stPosition;                          		    // 预置点的坐标和放大倍数
    }

    // 云台预置点配置对应结构
    public static class PTZ_PRESET_INFO extends SdkStructure
    {
        public int              dwMaxPtzPresetNum;                     // 最大预置点个数
        public int              dwRetPtzPresetNum;                     // 实际使用预置点个数
        public Pointer          pstPtzPreset;                          // 预置点信息(根据最大个数申请内存，大小sizeof(PTZ_PRESET)*dwMaxPtzPresetNum)
    }

    // 云台配置
    public static class CFG_PTZ_INFO extends SdkStructure
    {
        // 能力
        public byte                           abMartixID;
        public byte                           abCamID;
        public byte                           abPTZType;

        // 信息
        public int                            nChannelID;                           // 通道号(0开始)
        public int                            bEnable;                              // 使能开关
        public byte[]                         szProName = new byte[128];   // 协议名称
        public int                            nDecoderAddress;                      // 解码器地址；0 - 255
        public CFG_COMM_PROP                  struComm;
        public int                            nMartixID;                            // 矩阵号
        public int                            nPTZType;                             // 云台类型0-兼容，本地云台 1-远程网络云台
        public int                            nCamID;                               // 摄像头ID
        public int                            nPort;                                // 使用的串口端口号
        public CFG_PRESET_HOMING              stuPresetHoming;                      // 一段时间不操作云台，自动归位到某个预置点
        public int                            nControlMode;                         // 控制模式, 0:"RS485"串口控制(默认);1:"Coaxial" 同轴口控制
    }

    // 通道名称配置
    public static class AV_CFG_ChannelName extends SdkStructure
    {
        public int			nStructSize;
        public int			nSerial;						// 摄像头唯一编号
        public byte[]		szName = new byte[256];// 通道名

        public AV_CFG_ChannelName() {
            this.nStructSize = this.size();
        }
    }

    // 云台定位信息
    public static class NET_PTZ_LOCATION_INFO extends SdkStructure
    {
        public int     			nChannelID;                 		// 通道号
        public int     			nPTZPan;                    		// 云台水平运动位置,有效范围：[0,3600]
        public int     			nPTZTilt;                   		// 云台垂直运动位置,有效范围：[-1800,1800]
        public int    			nPTZZoom;                   		// 云台光圈变动位置,有效范围：[0,128]
        public byte    			bState;                     		// 云台运动状态, 0-未知 1-运动 2-空闲
        public byte   		 	bAction;                    		// 云台动作,
        // 255-未知,0-预置点,1-线扫,2-巡航,3-巡迹,4-水平旋转,5-普通移动,6-巡迹录制,
        // 7-全景云台扫描,8-热度图,9-精确定位,10-设备校正,11-智能配置，12-云台重启
        public byte    			bFocusState;                		// 云台聚焦状态, 0-未知, 1-运动状态, 2-空闲
        public byte    			bEffectiveInTimeSection;   			// 在时间段内预置点状态是否有效
        // 如果当前上报的预置点是时间段内的预置点,则为1,其他情况为0
        public int     			nPtzActionID;               		// 巡航ID号
        public int   			dwPresetID;                 		// 云台所在预置点编号
        public float   			fFocusPosition;             		// 聚焦位置
        public byte    			bZoomState;                 		// 云台ZOOM状态,0-未知,1-ZOOM,2-空闲
        public byte[]    		bReserved = new byte[3];            // 对齐
        public int   			dwSequence;                 		// 包序号,用于校验是否丢包
        public int   			dwUTC;                      		// 对应的UTC(1970-1-1 00:00:00)秒数。
//        public EM_DH_PTZ_PRESET_STATUS emPresetStatus;
//        public int[]    reserved = new int[248];                  // 保留字段

        public int              emPresetStatus;                       // 预置点位置,参考 EM_DH_PTZ_PRESET_STATUS
        public int              nZoomValue;                           // 真实变倍值 当前倍率（扩大100倍表示）
        public NET_PTZSPACE_UNNORMALIZED stuAbsPosition;              // 云台方向与放大倍数（扩大100倍表示）
        // 第一个元素为水平角度，0-36000；
        // 第二个元素为垂直角度，（-18000）-（18000）；
        // 第三个元素为显示放大倍数，0-MaxZoom*100
        public int              nFocusMapValue;                       // 聚焦映射值
        public int              nZoomMapValue;                        // 变倍映射值
        public int              emPanTiltStatus;                      //云台P/T运动状态
        public NET_EVENT_INFO_EXTEND stuEventInfoEx = new NET_EVENT_INFO_EXTEND(); // 事件公共扩展字段结构体
        public byte[]           reserved = new byte[696];             // 保留字段
    }



    // 云台定位中非归一化坐标和变倍
    public static class NET_PTZSPACE_UNNORMALIZED extends SdkStructure
    {
        public int                    nPosX;           // x坐标
        public int                    nPosY;           // y坐标
        public int                    nZoom;           // 放大倍率
        public byte[]                 byReserved = new byte[52];   // 预留字节
    }

    // 预置点状态枚举
    public static class EM_DH_PTZ_PRESET_STATUS extends SdkStructure
    {
        public static final int EM_DH_PTZ_PRESET_STATUS_UNKNOWN = 0;        // 未知
        public static final int EM_DH_PTZ_PRESET_STATUS_REACH = 1;          // 预置点到达
        public static final int EM_DH_PTZ_PRESET_STATUS_UNREACH = 2;        // 预置点未到达
    }

    // 通用云台控制命令
    public static class NET_PTZ_ControlType extends SdkStructure
    {
        public static final int NET_PTZ_UP_CONTROL = 0;//上
        public static final int NET_PTZ_DOWN_CONTROL = NET_PTZ_UP_CONTROL+1; //下
        public static final int NET_PTZ_LEFT_CONTROL = NET_PTZ_DOWN_CONTROL+1; //左
        public static final int NET_PTZ_RIGHT_CONTROL = NET_PTZ_LEFT_CONTROL+1; //右
        public static final int NET_PTZ_ZOOM_ADD_CONTROL = NET_PTZ_RIGHT_CONTROL+1; //变倍+
        public static final int NET_PTZ_ZOOM_DEC_CONTROL = NET_PTZ_ZOOM_ADD_CONTROL+1; //变倍-
        public static final int NET_PTZ_FOCUS_ADD_CONTROL = NET_PTZ_ZOOM_DEC_CONTROL+1; //调焦+
        public static final int NET_PTZ_FOCUS_DEC_CONTROL = NET_PTZ_FOCUS_ADD_CONTROL+1; //调焦-
        public static final int NET_PTZ_APERTURE_ADD_CONTROL = NET_PTZ_FOCUS_DEC_CONTROL+1; //光圈+
        public static final int NET_PTZ_APERTURE_DEC_CONTROL = NET_PTZ_APERTURE_ADD_CONTROL+1; //光圈-
        public static final int NET_PTZ_POINT_MOVE_CONTROL = NET_PTZ_APERTURE_DEC_CONTROL+1; //转至预置点
        public static final int NET_PTZ_POINT_SET_CONTROL = NET_PTZ_POINT_MOVE_CONTROL+1; //设置
        public static final int NET_PTZ_POINT_DEL_CONTROL = NET_PTZ_POINT_SET_CONTROL+1; //删除
        public static final int NET_PTZ_POINT_LOOP_CONTROL = NET_PTZ_POINT_DEL_CONTROL+1; //点间巡航
        public static final int NET_PTZ_LAMP_CONTROL = NET_PTZ_POINT_LOOP_CONTROL+1; //灯光雨刷
    }

    // 云台控制扩展命令
    public static class NET_EXTPTZ_ControlType extends SdkStructure
    {
        public static final int NET_EXTPTZ_LEFTTOP = 0x20;//左上
        public static final int NET_EXTPTZ_RIGHTTOP = NET_EXTPTZ_LEFTTOP+1; //右上
        public static final int NET_EXTPTZ_LEFTDOWN = NET_EXTPTZ_RIGHTTOP+1; //左下
        public static final int NET_EXTPTZ_RIGHTDOWN = NET_EXTPTZ_LEFTDOWN+1; //右下
        public static final int NET_EXTPTZ_ADDTOLOOP = NET_EXTPTZ_RIGHTDOWN+1; //加入预置点到巡航巡航线路预置点值
        public static final int NET_EXTPTZ_DELFROMLOOP = NET_EXTPTZ_ADDTOLOOP+1; //删除巡航中预置点巡航线路预置点值
        public static final int NET_EXTPTZ_CLOSELOOP = NET_EXTPTZ_DELFROMLOOP+1; //清除巡航巡航线路
        public static final int NET_EXTPTZ_STARTPANCRUISE = NET_EXTPTZ_CLOSELOOP+1; //开始水平旋转
        public static final int NET_EXTPTZ_STOPPANCRUISE = NET_EXTPTZ_STARTPANCRUISE+1; //停止水平旋转
        public static final int NET_EXTPTZ_SETLEFTBORDER = NET_EXTPTZ_STOPPANCRUISE+1; //设置左边界
        public static final int NET_EXTPTZ_SETRIGHTBORDER = NET_EXTPTZ_SETLEFTBORDER+1; //设置右边界
        public static final int NET_EXTPTZ_STARTLINESCAN = NET_EXTPTZ_SETRIGHTBORDER+1; //开始线扫
        public static final int NET_EXTPTZ_CLOSELINESCAN = NET_EXTPTZ_STARTLINESCAN+1; //停止线扫
        public static final int NET_EXTPTZ_SETMODESTART = NET_EXTPTZ_CLOSELINESCAN+1; //设置模式开始模式线路
        public static final int NET_EXTPTZ_SETMODESTOP = NET_EXTPTZ_SETMODESTART+1; //设置模式结束模式线路
        public static final int NET_EXTPTZ_RUNMODE = NET_EXTPTZ_SETMODESTOP+1; //运行模式模式线路
        public static final int NET_EXTPTZ_STOPMODE = NET_EXTPTZ_RUNMODE+1; //停止模式模式线路
        public static final int NET_EXTPTZ_DELETEMODE = NET_EXTPTZ_STOPMODE+1; //清除模式模式线路
        public static final int NET_EXTPTZ_REVERSECOMM = NET_EXTPTZ_DELETEMODE+1; //翻转命令
        public static final int NET_EXTPTZ_FASTGOTO = NET_EXTPTZ_REVERSECOMM+1; //快速定位水平坐标(8192)垂直坐标(8192)变倍(4)
        public static final int NET_EXTPTZ_AUXIOPEN = NET_EXTPTZ_FASTGOTO+1; //辅助开关开辅助点
        public static final int NET_EXTPTZ_AUXICLOSE = NET_EXTPTZ_AUXIOPEN+1; //辅助开关关辅助点
        public static final int NET_EXTPTZ_OPENMENU = 0x36;//打开球机菜单
        public static final int NET_EXTPTZ_CLOSEMENU = NET_EXTPTZ_OPENMENU+1; //关闭菜单
        public static final int NET_EXTPTZ_MENUOK = NET_EXTPTZ_CLOSEMENU+1; //菜单确定
        public static final int NET_EXTPTZ_MENUCANCEL = NET_EXTPTZ_MENUOK+1; //菜单取消
        public static final int NET_EXTPTZ_MENUUP = NET_EXTPTZ_MENUCANCEL+1; //菜单上
        public static final int NET_EXTPTZ_MENUDOWN = NET_EXTPTZ_MENUUP+1; //菜单下
        public static final int NET_EXTPTZ_MENULEFT = NET_EXTPTZ_MENUDOWN+1; //菜单左
        public static final int NET_EXTPTZ_MENURIGHT = NET_EXTPTZ_MENULEFT+1; //菜单右
        public static final int NET_EXTPTZ_ALARMHANDLE = 0x40;//报警联动云台parm1：报警输入通道；parm2：报警联动类型1-预置点2-线扫3-巡航；parm3：联动值，如预置点号
        public static final int NET_EXTPTZ_MATRIXSWITCH = 0x41;//矩阵切换parm1：监视器号(视频输出号)；parm2：视频输入号；parm3：矩阵号
        public static final int NET_EXTPTZ_LIGHTCONTROL= NET_EXTPTZ_MATRIXSWITCH+1; //灯光控制器
        public static final int NET_EXTPTZ_EXACTGOTO = NET_EXTPTZ_LIGHTCONTROL+1; //三维精确定位parm1：水平角度(0~3600)；parm2：垂直坐标(0~900)；parm3：变倍(1~128)
        public static final int NET_EXTPTZ_RESETZERO = NET_EXTPTZ_EXACTGOTO+1; //三维定位重设零位
        public static final int NET_EXTPTZ_MOVE_ABSOLUTELY = NET_EXTPTZ_RESETZERO+1; //绝对移动控制命令，param4对应结构PTZ_CONTROL_ABSOLUTELY
        public static final int NET_EXTPTZ_MOVE_CONTINUOUSLY = NET_EXTPTZ_MOVE_ABSOLUTELY+1; //持续移动控制命令，param4对应结构PTZ_CONTROL_CONTINUOUSLY
        public static final int NET_EXTPTZ_GOTOPRESET = NET_EXTPTZ_MOVE_CONTINUOUSLY+1; //云台控制命令，以一定速度转到预置位点，parm4对应结构PTZ_CONTROL_GOTOPRESET
        public static final int NET_EXTPTZ_SET_VIEW_RANGE = 0x49;//设置可视域(param4对应结构PTZ_VIEW_RANGE_INFO)
        public static final int NET_EXTPTZ_FOCUS_ABSOLUTELY = 0x4A;//绝对聚焦(param4对应结构PTZ_FOCUS_ABSOLUTELY)
        public static final int NET_EXTPTZ_HORSECTORSCAN = 0x4B;//水平扇扫(param4对应PTZ_CONTROL_SECTORSCAN,param1、param2、param3无效)
        public static final int NET_EXTPTZ_VERSECTORSCAN = 0x4C;//垂直扇扫(param4对应PTZ_CONTROL_SECTORSCAN,param1、param2、param3无效)
        public static final int NET_EXTPTZ_SET_ABS_ZOOMFOCUS = 0x4D;//设定绝对焦距、聚焦值,param1为焦距,范围:0,255],param2为聚焦,范围:[0,255],param3、param4无效
        public static final int NET_EXTPTZ_SET_FISHEYE_EPTZ = 0x4E;//控制鱼眼电子云台，param4对应结构PTZ_CONTROL_SET_FISHEYE_EPTZ
        public static final int NET_EXTPTZ_UP_TELE = 0x70;    //上 + TELE param1=速度(1-8)，下同
        public static final int NET_EXTPTZ_DOWN_TELE = NET_EXTPTZ_UP_TELE+1; //下 + TELE
        public static final int NET_EXTPTZ_LEFT_TELE = NET_EXTPTZ_DOWN_TELE+1; //左 + TELE
        public static final int NET_EXTPTZ_RIGHT_TELE = NET_EXTPTZ_LEFT_TELE+1; //右 + TELE
        public static final int NET_EXTPTZ_LEFTUP_TELE = NET_EXTPTZ_RIGHT_TELE+1; //左上 + TELE
        public static final int NET_EXTPTZ_LEFTDOWN_TELE = NET_EXTPTZ_LEFTUP_TELE+1; //左下 + TELE
        public static final int NET_EXTPTZ_TIGHTUP_TELE = NET_EXTPTZ_LEFTDOWN_TELE+1; //右上 + TELE
        public static final int NET_EXTPTZ_RIGHTDOWN_TELE = NET_EXTPTZ_TIGHTUP_TELE+1; //右下 + TELE
        public static final int NET_EXTPTZ_UP_WIDE = NET_EXTPTZ_RIGHTDOWN_TELE+1; // 上 + WIDEparam1=速度(1-8)，下同
        public static final int NET_EXTPTZ_DOWN_WIDE = NET_EXTPTZ_UP_WIDE+1; //下 + WIDE
        public static final int NET_EXTPTZ_LEFT_WIDE = NET_EXTPTZ_DOWN_WIDE+1; //左 + WIDE
        public static final int NET_EXTPTZ_RIGHT_WIDE = NET_EXTPTZ_LEFT_WIDE+1; //右 + WIDE
        public static final int NET_EXTPTZ_LEFTUP_WIDE = NET_EXTPTZ_RIGHT_WIDE+1; //左上 + WIDENET_IN_PTZBASE_SET_FOCUS_MAP_VALUE_INFO
        public static final int NET_EXTPTZ_LEFTDOWN_WIDE = NET_EXTPTZ_LEFTUP_WIDE+1; //左下 + WIDE
        public static final int NET_EXTPTZ_TIGHTUP_WIDE = NET_EXTPTZ_LEFTDOWN_WIDE+1; //右上 + WIDE
        public static final int NET_EXTPTZ_RIGHTDOWN_WIDE = NET_EXTPTZ_TIGHTUP_WIDE+1; //右下 + WIDE
        public static final int NET_EXTPTZ_GOTOPRESETSNAP = 0x80;                   // 转至预置点并抓图
        public static final int NET_EXTPTZ_DIRECTIONCALIBRATION = 0x82;             // 校准云台方向（双方向校准）
        public static final int NET_EXTPTZ_SINGLEDIRECTIONCALIBRATION = 0x83;       // 校准云台方向（单防线校准）,param4对应结构 NET_IN_CALIBRATE_SINGLEDIRECTION
        public static final int NET_EXTPTZ_MOVE_RELATIVELY = 0x84;			        // 云台相对定位,param4对应结构 NET_IN_MOVERELATIVELY_INFO
        public static final int NET_EXTPTZ_SET_DIRECTION = 0x85;				    // 设置云台方向, param4对应结构 NET_IN_SET_DIRECTION_INFO
        public static final int NET_EXTPTZ_BASE_MOVE_ABSOLUTELY = 0x86;		        // 精准绝对移动控制命令, param4对应结构 NET_IN_PTZBASE_MOVEABSOLUTELY_INFO（通过 CFG_CAP_CMD_PTZ 命令获取云台能力集( CFG_PTZ_PROTOCOL_CAPS_INFO )，若bSupportReal为TRUE则设备支持该操作）
        public static final int NET_EXTPTZ_BASE_MOVE_CONTINUOUSLY = NET_EXTPTZ_BASE_MOVE_ABSOLUTELY+1;	// 云台连续移动控制命令, param4 对应结构 NET_IN_PTZBASE_MOVE_CONTINUOUSLY_INFO.  通过 CFG_CAP_CMD_PTZ 命令获取云台能力集
        // 若 CFG_PTZ_PROTOCOL_CAPS_INFO 中 stuMoveContinuously 字段的 stuType.bSupportExtra 为 TRUE, 表示设备支持该操作
        public static final int NET_EXTPTZ_BASE_SET_FOCUS_MAP_VALUE = NET_EXTPTZ_BASE_MOVE_CONTINUOUSLY+1;	// 设置当前位置聚焦值, param4对应结构体 NET_IN_PTZBASE_SET_FOCUS_MAP_VALUE_INFO
        public static final int NET_EXTPTZ_TOTAL = NET_EXTPTZ_BASE_SET_FOCUS_MAP_VALUE+1;                   //最大命令值
    }

   //获取补光灯能力入参(对应 : NET_LIGHTINGCONTROL_CAPS)

    public static class NET_IN_LIGHTINGCONTROL_CAPS extends SdkStructure {
        /**
         * /结构体大小
         */
        public int              dwSize;
        /**
         * / 通道号
         */
        public int              nChannel;

        public NET_IN_LIGHTINGCONTROL_CAPS() {
            this.dwSize = this.size();
        }
    }


     //获取补光灯能力出参(对应 : NET_LIGHTINGCONTROL_CAPS)
    public static class NET_OUT_LIGHTINGCONTROL_CAPS extends SdkStructure {
        /**
         * /结构体大小
         */
        public int              dwSize;
        /**
         * /是否支持灯光控制
         */
        public int              bSupport;
        /**
         * /支持的灯光配置版本
         */
        public int              emConfigVersion;
        /**
         * /灯光类型
         */
        public int              emLightType;
        /**
         * /复合灯光类型
         */
        public int[]            anLightTypeComplex = new int[3];
        /**
         * /复合灯光类型数量
         */
        public int              nLightTypeComplexLen;
        /**
         * /近光灯组数量
         */
        public int              nNearLightNumber;
        /**
         * /中光灯组数量
         */
        public int              nMiddleLightNumber;
        /**
         * /远光灯组数量
         */
        public int              nFarLightNumber;
        /**
         * /默认支持的模式
         */
        public int              emDefaultMode;
        /**
         * /支持的模式类型
         */
        public int[]            anModes = new int[20];
        /**
         * /支持的模式数量
         */
        public int              nModesLen;
        /**
         * /复合灯模式信息
         */
        public NET_MODES_COMPLEX_LIGHT stuModesComplex = new NET_MODES_COMPLEX_LIGHT();
        /**
         * /灯光组信息
         */
        public NET_LIGHT_TYPE_COMPLEX_DETAIL stuLightTypeComplexDetail = new NET_LIGHT_TYPE_COMPLEX_DETAIL();
        /**
         * /云台联动灯光信息
         */
        public NET_LINKING_DETAIL stuLinkingDetail = new NET_LINKING_DETAIL();
        /**
         * /灯光组功率控制掩码
         */
        public int[]            anPower = new int[3];
        /**
         * /灯光组激光角度控制掩码
         */
        public int[]            anAngleControl = new int[3];
        /**
         * /灯光补偿信息
         */
        public NET_CORRECTION   stuCorrection = new NET_CORRECTION();
        /**
         * /灯光灵敏度信息
         */
        public NET_SENSITIVITY  stuSensitivity = new NET_SENSITIVITY();
        /**
         * /是否支持激光灯光轴调节
         */
        public int              bSupportLaserLightMove;
        /**
         * /定时模式支持的时间段数量
         */
        public int              nLightingTimeSectionNum;
        /**
         * /是否支持分时配置
         */
        public int              bSupportByTime;
        /**
         * /是否支持复合灯模式信息
         */
        public int              bSupportModesComplex;

        public NET_OUT_LIGHTINGCONTROL_CAPS() {
            this.dwSize = this.size();
        }
    }


    /**
     * @description 灯光支持的模式信息
     */
    public static class NET_MODES_COMPLEX_LIGHT extends SdkStructure {
        /**
         * /红外灯支持的模式
         */
        public int[] anInfraredLight = new int[3];
        /**
         * /红外灯支持的模式数量
         */
        public int nInfraredLightLen;
        /**
         * /白光灯支持的模式
         */
        public int[] anWhiteLight = new int[3];
        /**
         * /白光灯支持的模式数量
         */
        public int nWhiteLightLen;
        /**
         * /激光灯支持的模式
         */
        public int[] anLaserLight = new int[3];
        /**
         * /激光灯支持的模式数量
         */
        public int nLaserLightLen;
        /**
         * /智能混光灯支持的模式
         */
        public int[] emAIMixLight = new int[8];
        /**
         * /智能混光灯支持的模式数量
         */
        public int nAIMixLight;
        /**
         * /保留字节
         */
        public byte[] byReserved = new byte[92];

        public NET_MODES_COMPLEX_LIGHT() {
        }
    }


    /**
     * @description 灯光组信息
     */
    public static class NET_LIGHT_TYPE_COMPLEX_DETAIL extends SdkStructure {
        /**
         * /近光灯组灯类型
         */
        public int[]            anNearLight = new int[4];
        /**
         * /近光灯组灯数量
         */
        public int              nNearLightLen;
        /**
         * /中光灯组灯类型
         */
        public int[]            anMiddleLight = new int[4];
        /**
         * /中光灯组灯数量
         */
        public int              nMiddleLightLen;
        /**
         * /远光灯组灯类型
         */
        public int[]            anFarLight = new int[4];
        /**
         * /远光灯组灯数量
         */
        public int              nFarLightLen;
        /**
         * / 保留字节
         */
        public byte[]           byReserved = new byte[128];

        public NET_LIGHT_TYPE_COMPLEX_DETAIL() {
        }
    }

    /**
     * @description 云台联动灯光类型
     */
    public  static class NET_LINKING_DETAIL extends SdkStructure {
        /**
         * /闪烁灯光信息
         */
        public NET_FILCKER_LIGHTING stuFilckerLighting = new NET_FILCKER_LIGHTING();
        /**
         * /常亮灯光信息
         */
        public NET_KEEP_LIGHTING stuKeepLighting = new NET_KEEP_LIGHTING();
        /**
         * / 保留字节
         */
        public byte[]           byReserved = new byte[128];

        public NET_LINKING_DETAIL() {
        }
    }


    /**
     * @description 灯光闪烁相关信息
     */
    public  static class NET_FILCKER_LIGHTING extends SdkStructure {
        /**
         * /是否支持灯光闪烁
         */
        public int              bSupported;
        /**
         * /支持灯光联动能力集
         */
        public NET_LINKING_ABILITY stuAbility = new NET_LINKING_ABILITY();
        /**
         * /闪烁灯光类型
         */
        public int[]            anLightType = new int[3];
        /**
         * /闪烁灯光数量
         */
        public int              nLightTypeLen;
        /**
         * /闪烁间隔时间范围
         */
        public int[]            anFilckerIntevalTime = new int[2];
        /**
         * /闪烁可配置次数
         */
        public int[]            anFilckerTimes = new int[2];
        /**
         * /保留字节
         */
        public byte[]           byReserved = new byte[128];

        public NET_FILCKER_LIGHTING() {
        }
    }

    /**
     * @description 支持灯光联动的能力集
     */
    public static class NET_LINKING_ABILITY extends SdkStructure {
        /**
         * /支持的非智能事件
         */
        public int[]            emAnSupportEvents = new int[10];
        /**
         * /支持的非智能事件数量
         */
        public int              nSupportEventsLen;
        /**
         * /支持的智能规则
         */
        public int[]            emAnSupportIntelliScence = new int[40];
        /**
         * /支持的智能规则数量
         */
        public int              nSupportIntelliScenceLen;
        /**
         * / 保留字节
         */
        public byte[]           byReserved = new byte[128];

        public NET_LINKING_ABILITY() {
        }
    }

    /**
     * @description 灯光常亮信息
     */
    public static class NET_KEEP_LIGHTING extends SdkStructure {
        /**
         * /是否支持灯光常亮
         */
        public int              bSupported;
        /**
         * /支持灯光联动能力集
         */
        public NET_LINKING_ABILITY stuAbility = new NET_LINKING_ABILITY();
        /**
         * /常亮灯光类型
         */
        public int[]            anLightType = new int[3];
        /**
         * /常亮灯光数量
         */
        public int              nLightTypeLen;
        /**
         * / 保留字节
         */
        public byte[]           byReserved = new byte[128];

        public NET_KEEP_LIGHTING() {
        }
    }

    /**
     * @description 灯光补偿信息
     */
    public static class NET_CORRECTION extends SdkStructure {
        /**
         * /是否支持灯光补偿
         */
        public int              bSupported;
        /**
         * /补偿范围最大值
         */
        public int              nRange;
        /**
         * / 保留字节
         */
        public byte[]           byReserved = new byte[128];

        public NET_CORRECTION() {
        }
    }

    /**
     * @description 灯光灵敏度信息
     */
    public static class NET_SENSITIVITY extends SdkStructure {
        /**
         * /是否支持灯光灵敏度
         */
        public int              bSupported;
        /**
         * /灯光灵敏度最大值
         */
        public int              nRange;
        /**
         * / 保留字节
         */
        public byte[]           byReserved = new byte[128];

        public NET_SENSITIVITY() {
        }
    }


    //补光灯配置
    public static class NET_VIDEOIN_LIGHTING_INFO extends SdkStructure {
        public int              dwSize;
        /**
         * / 灯光模式
         */
        public int              emLightMode;
        /**
         * / 灯光补偿值，倍率优先时有效0-4
         */
        public int              nCorrection;
        /**
         * / 灯光灵敏度，倍率优先时有效，0-5，默认为3
         */
        public int              nSensitive;
        /**
         * / 近光灯亮度0-100
         */
        public int              nNearLight;
        /**
         * / 远光灯亮度0-100
         */
        public int              nFarLight;

        public NET_VIDEOIN_LIGHTING_INFO() {
            this.dwSize = this.size();
        }
    }

    /**
     * @description 补光灯灵敏度配置
     */
    public class CFG_LIGHTING_V2_INFO extends SdkStructure {
        /**
         * 通道
         */
        public int              nChannel;
        /**
         * 白天黑夜对应灯光配置数量
         */
        public int              nDNLightInfoNum;
        /**
         * 白天黑夜对应灯光配置 从元素0开始分别表示 白天、夜晚、普通、顺光、一般逆光、强逆光、低照度、自定义
         */
        public CFG_LIGHTING_V2_DAYNIGHT[] anDNLightInfo = new CFG_LIGHTING_V2_DAYNIGHT[8];

        public CFG_LIGHTING_V2_INFO() {
            for (int i = 0; i < anDNLightInfo.length; i++) {
                anDNLightInfo[i] = new CFG_LIGHTING_V2_DAYNIGHT();
            }
        }
    }

    /**
     * @description 白天黑夜补光灯灵敏度配置
     */
    public class CFG_LIGHTING_V2_DAYNIGHT extends SdkStructure {
        /**
         * 各类型灯光信息
         */
        public CFG_LIGHTING_V2_UNIT[] anLightInfo = new CFG_LIGHTING_V2_UNIT[3];
        /**
         * 灯光类型数量
         */
        public int              nLightInfoLen;

        public CFG_LIGHTING_V2_DAYNIGHT() {
            for (int i = 0; i < anLightInfo.length; i++) {
                anLightInfo[i] = new CFG_LIGHTING_V2_UNIT();
            }
        }
    }

    /**
     * @description 补光灯灵敏度配置信息单元
     */
    public class CFG_LIGHTING_V2_UNIT extends SdkStructure {
        /**
         * 灯光类型
         */
        public int              emLightType;
        /**
         * 灯光模式
         */
        public int              emMode;
        /**
         * 灯光补偿
         */
        public int              nCorrection;
        /**
         * 灯光灵敏度
         */
        public int              nSensitive;
        /**
         * 补光灯开关延时
         */
        public int              nLightSwitchDelay;
        /**
         * 近光灯组信息
         */
        public NET_LIGHT_INFO[] anNearLight = new NET_LIGHT_INFO[4];
        /**
         * 近光灯组数量
         */
        public int              nNearLightLen;
        /**
         * 中光灯组信息
         */
        public NET_LIGHT_INFO[] anMiddleLight = new NET_LIGHT_INFO[4];
        /**
         * 中光灯组数量
         */
        public int              nMiddleLightLen;
        /**
         * 远光灯组信息
         */
        public NET_LIGHT_INFO[] anFarLight = new NET_LIGHT_INFO[4];
        /**
         * 远光灯组数量
         */
        public int              nFarLightLen;
        /**
         * 当前白光灯上限亮度相对于白光灯最大亮度的百分比0~100
         */
        public int              nPercentOfMaxBrightness;
        /**
         * 智能补光方案下生效，表示红外白光切换延时，防止来回切换影响寿命和体验单位秒 范围0-300 默认30s
         */
        public int              nAIMixLightSwitchDelay;
        /**
         * 预留字节
         */
        public byte[]           byReserved = new byte[120];

        public CFG_LIGHTING_V2_UNIT() {
            for (int i = 0; i < anNearLight.length; i++) {
                anNearLight[i] = new NET_LIGHT_INFO();
            }
            for (int i = 0; i < anMiddleLight.length; i++) {
                anMiddleLight[i] = new NET_LIGHT_INFO();
            }
            for (int i = 0; i < anFarLight.length; i++) {
                anFarLight[i] = new NET_LIGHT_INFO();
            }
        }
    }

    /**
     * @description 灯光信息
     */
    public class NET_LIGHT_INFO extends SdkStructure {
        /**
         * 亮度百分比
         */
        public int              nLight;
        /**
         * 激光灯角度归一化值
         */
        public int              nAngle;

        public NET_LIGHT_INFO() {
        }
    }

    /**
     * @description 灯光类型
     */
    public enum EM_CFG_LC_LIGHT_TYPE {
        /**
         * 未知
         */
        EM_CFG_LC_LIGHT_TYPEUNKNOWN(0, "未知"),
        /**
         * 红外灯
         */
        EM_CFG_LC_LIGHT_TYPE_INFRAREDLIGHT(1, "红外灯"),
        /**
         * 白光灯
         */
        EM_CFG_LC_LIGHT_TYPE_WIHTELIGHT(2, "白光灯"),
        /**
         * 激光灯
         */
        EM_CFG_LC_LIGHT_TYPE_LASERLIGHT(3, "激光灯"),
        /**
         * 智能混光灯(根据智能ID切换红外和白光灯)
         */
        EM_CFG_LC_LIGHT_TYPE_AIMIXLIGHT(4, "智能混光灯(根据智能ID切换红外和白光灯)"),
        /**
         * 指示灯
         */
        EM_CFG_LC_LIGHT_TYPE_PILOTLIGHT(5, "指示灯");

        private int value;
        private String note;

        public String getNote() {
            return note;
        }

        public int getValue() {
            return value;
        }

        EM_CFG_LC_LIGHT_TYPE(int givenValue, String note) {
            this.value = givenValue;
            this.note = note;
        }

        public static String getNoteByValue(int givenValue) {
            for (EM_CFG_LC_LIGHT_TYPE enumType : EM_CFG_LC_LIGHT_TYPE.values()) {
                if (givenValue == enumType.getValue()) {
                    return enumType.getNote();
                }
            }
            return null;
        }

        public static int getValueByNote(String givenNote) {
            for (EM_CFG_LC_LIGHT_TYPE enumType : EM_CFG_LC_LIGHT_TYPE.values()) {
                if (givenNote.equals(enumType.getNote())) {
                    return enumType.getValue();
                }
            }
            return -1;
        }
    }

    /**
     * @description 灯光模式
     */
    public enum EM_CFG_LC_MODE {
        /**
         * 未知
         */
        EM_CFG_LC_MODE_UNKNOWN(0, "未知"),
        /**
         * 手动
         */
        EM_CFG_LC_MODE_MANUAL(1, "手动"),
        /**
         * 倍率优先
         */
        EM_CFG_LC_MODE_ZOOMPRIO(2, "倍率优先"),
        /**
         * 定时(废弃)
         */
        EM_CFG_LC_MODE_TIMING(3, "定时(废弃)"),
        /**
         * 自动
         */
        EM_CFG_LC_MODE_AUTO(4, "自动"),
        /**
         * 关闭灯光
         */
        EM_CFG_LC_MODE_OFF(5, "关闭灯光"),
        /**
         * 支持多种灯光(废弃)
         */
        EM_CFG_LC_MODE_EXCLUSIVEMANUAL(6, "支持多种灯光(废弃)"),
        /**
         * 智能灯光(废弃)
         */
        EM_CFG_LC_MODE_SMARTLIGHT(7, "智能灯光(废弃)"),
        /**
         * 事件联动(废弃)
         */
        EM_CFG_LC_MODE_LINKING(8, "事件联动(废弃)"),
        /**
         * 光敏
         */
        EM_CFG_LC_MODE_DUSKTODAWN(9, "光敏"),
        /**
         * 强制打开灯光
         */
        EM_CFG_LC_MODE_FORCEON(10, "强制打开灯光");

        private int value;
        private String note;

        public String getNote() {
            return note;
        }

        public int getValue() {
            return value;
        }

        EM_CFG_LC_MODE(int givenValue, String note) {
            this.value = givenValue;
            this.note = note;
        }

        public static String getNoteByValue(int givenValue) {
            for (EM_CFG_LC_MODE enumType : EM_CFG_LC_MODE.values()) {
                if (givenValue == enumType.getValue()) {
                    return enumType.getNote();
                }
            }
            return null;
        }

        public static int getValueByNote(String givenNote) {
            for (EM_CFG_LC_MODE enumType : EM_CFG_LC_MODE.values()) {
                if (givenNote.equals(enumType.getNote())) {
                    return enumType.getValue();
                }
            }
            return -1;
        }
    }

    public class NET_EVENT_INFO_EXTEND extends SdkStructure {
        /**
         * RealUTC 是否有效，bRealUTC 为 TRUE 时，用 stuRealUTC，否则 stuRealUTC 字段无效(用原事件结构体中的 事件发生时间/事件触发时间(UTC) 字段)
         */
        public int bRealUTC;
        /**
         * 仅用于字节对齐
         */
        public byte[] byReserved = new byte[4];
        /**
         * 事件发生的时间(标准UTC时间(不带时区夏令时偏差)), 由于事件的UTC时间在产品线之间使用的差异性, 故增加RealUTC作为标准UTC时间, 平台在收到事件解析首优先级是RealUTC, 其次是UTC.
         */
        public NET_TIME_EX stuRealUTC = new NET_TIME_EX();
        /**
         * 事件类型是否有效
         */
        public int bIsEventsTypeValid;
        /**
         * 事件类型, bIsEventsTypeValid为TRUE时有效, 0:正常抓图事件, 1:邮件联动抓图事件(图片通过第二路抓图码流上来，和正常抓图的图片可以不一样)
         */
        public int szEventsType;
        /**
         * 保留字节
         */
        public byte[] szReserved = new byte[1012];

        public NET_EVENT_INFO_EXTEND() {
        }

        @Override
        public String toString() {
            return "NET_EVENT_INFO_EXTEND{" +
                    "bRealUTC=" + bRealUTC +
                    ", stuRealUTC=" + stuRealUTC +
                    ", bIsEventsTypeValid=" + bIsEventsTypeValid +
                    ", szEventsType=" + szEventsType +
                    '}';
        }
    }

    public class NET_TIME_EX extends SdkStructure {
        /**
         * 年
         */
        public int dwYear;
        /**
         * 月
         */
        public int dwMonth;
        /**
         * 日
         */
        public int dwDay;
        /**
         * 时
         */
        public int dwHour;
        /**
         * 分
         */
        public int dwMinute;
        /**
         * 秒
         */
        public int dwSecond;
        /**
         * 毫秒
         */
        public int dwMillisecond;
        /**
         * utc时间(获取时0表示无效，非0有效 下发无效)
         */
        public int dwUTC;
        /**
         * 保留字段
         */
        public int[] dwReserved = new int[1];

        public void setTime(int year, int month, int day, int hour, int minute, int second) {
            this.dwYear = year;
            this.dwMonth = month;
            this.dwDay = day;
            this.dwHour = hour;
            this.dwMinute = minute;
            this.dwSecond = second;
            this.dwMillisecond = 0;
        }

        public String toString() {
            return dwYear + "/" + dwMonth + "/" + dwDay + " " + dwHour + ":" + dwMinute + ":" + dwSecond;
        }

        public String toStringTime() {
            return String.format(
                    "%02d/%02d/%02d %02d:%02d:%02d", dwYear, dwMonth, dwDay, dwHour, dwMinute, dwSecond);
        }

        public String toStringTitle() {
            return String.format(
                    "Time_%02d%02d%02d_%02d%02d%02d", dwYear, dwMonth, dwDay, dwHour, dwMinute, dwSecond);
        }
    }

    /**
     * 设置当前位置聚焦值
     * 对应接口 {@link DahuaNetSDK CLIENT_DHPTZControlEx2}
     * 对应枚举 {@link NET_EXTPTZ_ControlType#NET_EXTPTZ_BASE_SET_FOCUS_MAP_VALUE}
     */
    public class NET_IN_PTZBASE_SET_FOCUS_MAP_VALUE_INFO extends SdkStructure {
        /**
         * 结构体大小
         */
        public int              dwSize;
        /**
         * 聚焦映射值, 取值范围 [0, 28672]
         */
        public int              nfocusMapValue;

        public NET_IN_PTZBASE_SET_FOCUS_MAP_VALUE_INFO() {
            this.dwSize = this.size();
        }
    }

}
