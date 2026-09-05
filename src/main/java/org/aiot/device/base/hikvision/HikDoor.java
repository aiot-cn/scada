package org.aiot.device.base.hikvision;

import com.alibaba.fastjson.JSONObject;
import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.ptr.IntByReference;

import org.aiot.lang.annotation.AoReflect;
import org.aiot.lang.device.HCNetSDK;
import org.aiot.model.table.DeviceProperty;
import org.aiot.model.table.SysDict;
import org.aiot.model.table.TCard;
import org.aiot.model.table.TPerson;
import org.aiot.util.CalcUtil;
import org.aiot.util.CommonUtil;
import org.nutz.json.Json;
import org.nutz.lang.Lang;
import org.nutz.lang.Strings;
import org.nutz.lang.util.NutMap;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@AoReflect("海康门禁")
public class HikDoor extends HikBase {

	//卡参数配置条件结构体
	public static class NET_DVR_CARD_CFG_COND extends Structure {
		public int dwSize;
		public int dwCardNum;//设置或获取卡数量，获取时置为0xffffffff表示获取所有卡信息
		public byte byCheckCardNo;//设备是否进行卡号校验：0- 不校验，1- 校验
		public byte[] byRes1 = new byte[3];
		public short wLocalControllerID;//就地控制器序号，表示往就地控制器下发离线卡参数，0代表是门禁主机
		public byte[] byRes2 = new byte[2];
		public int dwLockID;//锁ID
		public byte[] byRes3 = new byte[20];
		@Override
		protected List getFieldOrder() {
			return Arrays.asList("dwSize","dwCardNum","byCheckCardNo","byRes1","wLocalControllerID","byRes2","dwLockID","byRes3");
		}
	}
	//卡密码开门使能配置结构体
	public static class NET_DVR_CARD_PASSWD_CFG extends Structure{
		public int      dwSize;//结构体大小
		public byte[]   byCardNo= new byte[32];//卡号
		public byte[]   byCardPassword= new byte[8];//卡密码
		public int      dwErrorCode;//获取卡密码开门使能配置返回的错误码，0表示配置成功，其他值表示失败的错误码
		public byte     byCardValid;//卡是否有效（用于删除卡，设置时置为0进行删除，获取时此字段始终为1）：0- 无效，1- 有效
		public byte[]   byRes2= new byte[23];
		@Override
		protected List getFieldOrder() {
			return Arrays.asList("dwSize","byCardNo","byCardPassword","dwErrorCode","byCardValid","byRes2");
		}
	}

	public static class CARD_RIGHT_PLAN_WORD extends Structure{
		public short[]	planNum = new short[4];
		@Override
		protected List getFieldOrder() {
			return Arrays.asList("planNum");
		}
	}

	//卡参数配置结构体
	public static class NET_DVR_CARD_CFG_V50 extends Structure{
		public int	dwSize;
		/**
		 * 需要修改的卡参数（设置卡参数时有效），按位表示，每位代表一种参数，值：0- 不修改，1- 需要修改<br>
		 * 1卡是否有效参数<br>2有效期参数<br>3卡类型参数<br>4门权限参数<br>5首卡参数<br>6最大刷卡次数参数<br>7所属群组参数<br>8卡密码参数<br>9卡权限计划参数<br>10已刷卡次数<br>
		 * 11工号<br>12姓名<br>13部门编号<br>14排班计划编号<br>15排班计划类型<br>16房间号<br>17SIM卡号（手机号）<br>18楼层号<br>19用户类型
		 */
		public int	dwModifyParamType;
		/**
		 * 卡号，特殊卡号定义如下：<br>
		 * 0xFFFFFFFFFFFFFFFF：非法卡号<br>
		 * 0xFFFFFFFFFFFFFFFE：胁迫码<br>
		 * 0xFFFFFFFFFFFFFFFD：超级码<br>
		 * 0xFFFFFFFFFFFFFFFC~0xFFFFFFFFFFFFFFF1：预留的特殊卡<br>
		 * 0xFFFFFFFFFFFFFFF0：最大合法卡号
		 */
		public byte[]	byCardNo =new byte[32];
		/**
		 * 卡是否有效：0- 无效，1- 有效（用于删除卡，设置时置为0进行删除，获取时此字段始终为1）
		 */
		public byte	byCardValid;
		/**
		 * 卡类型：1- 普通卡（默认），2- 残疾人卡，3- 黑名单卡，4- 巡更卡，5- 胁迫卡，6- 超级卡，7- 来宾卡，8- 解除卡，9- 员工卡，10- 应急卡，11- 应急管理卡
		 */
		public byte	byCardType;
		/**
		 * 是否为首卡：1- 是，0- 否
		 */
		public byte	byLeaderCard;
		public byte	byRes1;
		/**
		 * 门权限（梯控的楼层权限），按字节表示，1-为有权限，0-为无权限，从低位到高位依次表示对门（或者梯控楼层）1-N是否有权限
		 */
		public byte[]	byDoorRight=new byte[256];
		/**
		 * 有效期参数
		 */
		public NET_DVR_VALID_PERIOD_CFG	struValid = new NET_DVR_VALID_PERIOD_CFG();
		public byte[]	byBelongGroup=new byte[128];//所属群组，按字节表示，1-属于，0-不属于，从低位到高位表示是否从属群组1~N
		/**
		 * 卡密码
		 */
		public byte[]	byCardPassword=new byte[8];
		/**
		 * 卡权限计划，取值为计划模板编号，同个门不同计划模板采用权限或的方式处理
		 * 4个为一组
		 */
		public short[]	wCardRightPlan = new short[1024];
		/**
		 * 最大刷卡次数，0为无次数限制
		 */
		public int	dwMaxSwipeTime;
		/**
		 * 已刷卡次数
		 */
		public int	dwSwipeTime;
		public short	wRoomNumber;//房间号

		public short	wFloorNumber;//层号
		public int	dwEmployeeNo;//工号
		public byte[]	byName=new byte[32];//姓名
		public byte	wDepartmentNo;//部门编号
		public byte	wSchedulePlanNo;//排班计划编号

		public byte	bySchedulePlanType;//排班计划类型：0- 无意义，1- 个人，2- 部门
		public byte[]	byRes2=new byte[3];
		public int	dwLockID;//锁ID
		public byte[]	byLockCode=new byte[8];//锁代码
		/**
		 * 房间代码
		 * 按位表示，0-无权限，1-有权限
		 * 第0位表示：弱电报警
		 * 第1位表示：开门提示音
		 * 第2位表示：限制客卡
		 * 第3位表示：通道
		 * 第4位表示：反锁开门
		 * 第5位表示：巡更功能
		 */
		public byte[]	byRoomCode=new byte[8];

		public int	dwCardRight;//卡权限
		public int	dwPlanTemplate;//计划模板(每天)各时间段是否启用，按位表示，0--不启用，1-启用
		public int	dwCardUserId;//持卡人ID
		public byte	byCardModelType;//0-空，1- MIFARE S50，2- MIFARE S70，3- FM1208 CPU卡，4- FM1216 CPU卡，5-国密CPU卡，6-身份证，7- NFC
		public byte[]	byRes3=new byte[83];
		@Override
		protected List getFieldOrder() {
			return Arrays.asList("dwSize","dwModifyParamType","byCardNo","byCardValid","byCardType",
					"byLeaderCard","byRes1","byDoorRight","struValid","byBelongGroup",
					"byCardPassword","wCardRightPlan","dwMaxSwipeTime","dwSwipeTime","wRoomNumber",
					"wFloorNumber","dwEmployeeNo","byName","wDepartmentNo","wSchedulePlanNo",
					"bySchedulePlanType","byRes2","dwLockID","byLockCode","byRoomCode",
					"dwCardRight","dwPlanTemplate","dwCardUserId","byCardModelType","byRes3");
		}
	}

	//有效期参数结构体
	public static class NET_DVR_VALID_PERIOD_CFG  extends Structure{
		public byte               byEnable;//是否启用该有效期：0- 不启用，1- 启用
		public byte               byBeginTimeFlag;//是否限制起始时间的标志，0-不限制，1-限制
		public byte               byEnableTimeFlag;//是否限制终止时间的标志，0-不限制，1-限制
		public byte               byTimeDurationNo;//有效期索引,从0开始（时间段通过SDK设置给锁，后续在制卡时，只需要传递有效期索引即可，以减少数据量
		public HCNetSDK.NET_DVR_TIME_EX    struBeginTime = new HCNetSDK.NET_DVR_TIME_EX();//有效期起始时间
		public HCNetSDK.NET_DVR_TIME_EX    struEndTime = new HCNetSDK.NET_DVR_TIME_EX();//有效期结束时间
		public byte[] byRes2= new byte[32];

		@Override
		protected List getFieldOrder() {
			return Arrays.asList("byEnable","byBeginTimeFlag","byEnableTimeFlag","byTimeDurationNo","struBeginTime","struEndTime","byRes2");
		}

	}

	public static TCard dvrCard2Tcard(NET_DVR_CARD_CFG_V50 dvrCard){
		TCard card = new TCard();
		card.setNo(new String(dvrCard.byCardNo).trim());
		card.setType(dvrCard.byCardType);
		card.setPassword(new String(dvrCard.byCardPassword).trim());
		if(dvrCard.struValid.byEnable == 1){
			//if(dvrCard.struValid.byBeginTimeFlag == 1)
				card.setBeginTime(HikBase.hkTimeEx2Date(dvrCard.struValid.struBeginTime));
			//if(dvrCard.struValid.byEnableTimeFlag == 1)
				card.setEndTime(HikBase.hkTimeEx2Date(dvrCard.struValid.struEndTime));
		}

		card.setLeader(dvrCard.byLeaderCard);
		card.setMaxSwipe(dvrCard.dwMaxSwipeTime);

		String a = "";
		for (int i = 0; i < 4; i++) {
			a+=dvrCard.byDoorRight[i]+"[";
			for(int j = 0; j < 4; j++){
				a+=dvrCard.wCardRightPlan[i*4+j]+",";
			}
			a+="] ";

		}
		card.setOther(a);

		return card;
	}

	public static NET_DVR_CARD_CFG_V50 tcard2DvrCard(TCard card){
		NET_DVR_CARD_CFG_V50 dvrCard = new NET_DVR_CARD_CFG_V50();
		byte[] no = card.getNo().getBytes();
		System.arraycopy(no, 0, dvrCard.byCardNo, 0,no.length);
		dvrCard.byCardType = (byte) card.getType();

		if(card.getPassword() != null){
			byte[] password = card.getPassword().getBytes();
			System.arraycopy(password, 0, dvrCard.byCardPassword, 0,password.length);
		}
		if(card.getBeginTime() != null || card.getEndTime() != null){
			dvrCard.struValid.byEnable = 1;
		}

		if(card.getBeginTime() != null){
			dvrCard.struValid.byBeginTimeFlag = 1;
			dvrCard.struValid.struBeginTime = HikBase.date2hkTimeEx(card.getBeginTime());
		}

		if(card.getEndTime() != null){
			dvrCard.struValid.byEnableTimeFlag = 1;
			dvrCard.struValid.struEndTime = HikBase.date2hkTimeEx(card.getEndTime());
		}

		dvrCard.byLeaderCard = (byte)card.getLeader();
		dvrCard.dwMaxSwipeTime = card.getMaxSwipe();
		dvrCard.byCardValid = (byte)card.getIsRemoved();
		return dvrCard;
	}


	@Override
	public void init(){
		super.init();
		addMenu(device.getName(),"/plugin/hikvision/card");
	}

	/******************************************************************************
	 * FMSGCallBack 报警信息回调函数 //门禁客户端布防只支持一路
	 * 主类型 dwMajor 1报警 2异常 3操作 5事件
	 * 次类型 dwMinor
	 * --------1报警--------			--------2异常--------				--------3操作--------			--------5事件--------
	 * 0x400 防区短路报警				0x27 网络断开						0x50 本地登陆					0x01 合法卡认证通过				0x3c 人脸加刷卡认证通过		0x89 通行超时
	 * 0x401 防区断路报警				0x3a RS485连接状态异常				0x51 本地注销登陆					0x02 刷卡加密码认证通过			0x3d 人脸加刷卡认证失败		0x8a 误闯报警
	 * 0x402 防区异常报警				0x3b RS485连接状态异常恢复				0x5a 本地升级					0x03 刷卡加密码认证失败			0x3e 人脸加刷卡认证超时		0x8b 闸机自由通行时未认证通过
	 * 0x403 防区报警恢复				0x400 设备上电启动					0x70 远程登录					0x04 数卡加密码认证超时			0x3f 人脸加密码加指纹认证通过	0x8c 摆臂被阻挡
	 * 0x404 设备防拆报警				0x401 设备掉电关闭					0x71 远程注销登陆					0x05 刷卡加密码超次				0x40 人脸加密码加指纹认证失败	0x8d 摆臂阻挡消除
	 * 0x405 设备防拆恢复				0x402 看门狗复位						0x79 远程布防					0x06 未分配权限					0x41 人脸加密码加指纹认证超时	0x8e 设备升级本地人脸建模失败
	 * 0x406 读卡器防拆报警			0x403 蓄电池电压低					0x7a 远程撤防					0x07 无效时段					0x42 人脸加刷卡加指纹认证通过	0x8f 逗留事件
	 * 0x407 读卡器防拆恢复			0x404 蓄电池电压恢复正常				0x7b 远程重启					0x08 卡号过期					0x43 人脸加刷卡加指纹认证失败	0x97 密码不匹配
	 * 0x408 事件输入报警				0x405 交流电断电						0x7e 远程升级					0x09 无此卡号					0x44 人脸加刷卡加指纹认证超时	0x98 工号不存在
	 * 0x409 事件输入恢复				0x406 交流电恢复						0x86 远程导出配置文件				0x0a 反潜回认证失败				0x45 工号加指纹认证通过		0x99 组合认证通过
	 * 0x40a 胁迫报警				0x407 网络恢复						0x87 远程导入配置文件				0x0b 互锁门未关闭					0x46 工号加指纹认证失败		0x9a 组合认证超时
	 * 0x40b 离线事件满90%报警			0x408 FLASH读写异常					0xd6 远程手动开启报警输出			0x0c 卡不属于多重认证群组			0x47 工号加指纹认证超时		0x9b 认证方式不匹配
	 * 0x40c 卡号认证失败超次报警		0x409 读卡器掉线						0xd7 远程手动关闭报警输出			0x0d 卡不在多重认证时间段内			0x48 工号加指纹加密码认证通过
	 * 0x40d SD卡存储满报警			0x40a 读卡器掉线恢复					0x400 远程开门					0x0e 多重认证模式超级权限认证失败	0x49 工号加指纹加密码认证失败
	 * 0x40e 联动抓拍事件报警			0x40b 指示灯关闭						0x401 远程关门（对于梯控，表示受控）	0x0f 多重认证模式远程认证失败		0x4a 工号加指纹加密码认证超时
	 * 0x40f 门控安全模块防拆报警		0x40c 指示灯恢复						0x402 远程常开（对于梯控，表示自由）	0x10 多重认证成功					0x4b 人脸认证通过
	 * 0x410 门控安全模块防拆恢复		0x40d 通道控制器掉线					0x403 远程常关（对于梯控，表示禁用）	0x11 首卡开门开始					0x4c 人脸认证失败
	 * 0x411 POS开启					0x40e 通道控制器恢复					0x404 远程手动校时				0x12 首卡开门结束					0x4d 工号加人脸认证通过
	 * 0x412 POS结束					0x40f 门控安全模块掉线					0x405 NTP自动校时				0x13 常开状态开始					0x4e 工号加人脸认证失败
	 * 0x413 人脸图像画质低			0x410 门控安全模块掉线恢复				0x406 远程清空卡号				0x14 常开状态结束					0x4f 工号加人脸认证超时
	 * 0x414 指纹图像画质低			0x413 就地控制器网络断开				0x407 远程恢复默认参数				0x15 门锁打开					0x50 人脸识别失败
	 * 0x415 消防输入短路报警			0x414 就地控制器网络恢复				0x408 防区布防					0x16 门锁关闭					0x51 首卡授权开始
	 * 0x416 消防输入断路报警			0x415 主控RS485环路节点断开			0x409 防区撤防					0x17 开门按钮打开					0x52 首卡授权结束
	 * 0x417 消防输入恢复				0x416 主控RS485环路节点恢复			0x40a 本地恢复默认参数				0x18 开门按钮放开					0x53 门锁输入短路报警
	 * 0x418 消防按钮触发				0x417 就地控制器掉线					0x40b 远程抓拍					0x19 正常开门（门磁）				0x54 门锁输入断路报警
	 * 0x419 消防按钮恢复				0x418 就地控制器掉线恢复				0x40c 修改网络中心参数配置			0x1a 正常关门（门磁）				0x55 门锁输入异常报警
	 * 0x41a 维护按钮触发				0x419 就地下行RS485环路断开			0x40d 修改GPRS中心参数配置			0x1b 门异常打开（门磁）			0x56 门磁输入短路报警
	 * 0x41b 维护按钮恢复				0x41a 就地下行RS485环路恢复			0x40e 修改中心组参数配置			0x1c 门打开超时（门磁）			0x57 门磁输入断路报警
	 * 0x41c 紧急按钮触发				0x41b 分控器在线						0x40f 解除码输入					0x1d 报警输出打开					0x58 门磁输入异常报警
	 * 0x41d 紧急按钮恢复				0x41c 分控器离线						0x410 自动重新编号				0x1e 报警输出关闭					0x59 开门按钮输入短路报警
	 * 0x41e 分控器防拆报警			0x41d 身份证阅读器未连接（智能专用）		0x411 自动补充编号				0x1f 常关状态开始					0x5a 开门按钮输入断路报警
	 * 0x41f 分控器防拆报警恢复		0x41e 身份证阅读器连接恢复（智能专用）	0x412 导入普通配置文件				0x20 常关状态结束					0x5b 开门按钮输入异常报警
	 * 0x422 通道控制器防拆报警		0x41f 指纹模组未连接（智能专用）			0x413 导出普通配置文件				0x21 多重多重认证需要远程开门		0x5c 门锁异常打开
	 * 0x423 通道控制器防拆报警恢复		0x420 指纹模组连接恢复（智能专用）		0x414 导入卡权限参数				0x22 多重认证超级密码认证成功事件	0x5d 门锁打开超时
	 * 0x424 通道控制器消防输入报警		0x421 摄像头未连接					0x415 导出卡权限参数				0x23 多重认证重复认证事件			0x5e 首卡未授权开门失败
	 * 0x425 通道控制器消防输入报警恢复	0x422 摄像头连接恢复					0x416 本地U盘升级					0x24 多重认证重复认证事件			0x5f 呼梯继电器断开
	 * 0x442 合法事件满90%报警			0x423 COM口未连接					0x417 访客呼梯					0x25 门铃响						0x60 呼梯继电器闭合
	 *								0x424 COM口连接恢复					0x418 住户呼梯					0x26 指纹比对通过					0x61 自动按键继电器断开
	 *								0x425 设备未授权						0x419 远程实时布防				0x27 指纹比对失败					0x62 自动按键继电器闭合
	 *								0x426 人证设备在线					0x41a 远程实时撤防				0x28 刷卡加指纹认证通过			0x63 按键梯控继电器断开
	 *								0x427 人证设备离线					0x41b 遥控器未对码操作失败			0x29 刷卡加指纹认证失败			0x64 按键梯控继电器闭合
	 *								0x428 本地登录锁定					0x41c 遥控器关门					0x2a 刷卡加指纹认证超时			0x65 工号加密码认证通过
	 *								0x428 本地登录解锁					0x41d 遥控器开门					0x2b 刷卡加指纹加密码认证通过		0x66 工号加密码认证失败
	 *								0x411 电池电压低（仅人脸设备使用）		0x41e 遥控器常开门				0x2c 刷卡加指纹加密码认证失败		0x67 工号加密码认证超时
	 *								0x412 电池电压恢复正常（仅人脸设备使用）									0x2d 刷卡加指纹加密码认证超时		0x68 真人检测失败
	 *								0x428 本地登录锁定													0x2e 指纹加密码认证通过			0x69 人证比对通过
	 *								0x429 本地登录解锁													0x2f 指纹加密码认证失败			0x70 人证比对失败
	 *								0x42a 与反潜回服务器通信断开											0x30 指纹加密码认证超时			0x71 黑名单事件
	 *								0x42b  与反潜回服务器通信恢复											0x31 指纹不存在					0x72 合法短信
	 *								0x42c  电机或传感器异常												0x32 刷卡平台认证					0x73 非法短信
	 *								0x42d  CAN总线异常													0x33 呼叫中心事件					0x74 MAC侦测
	 *								0x42e  CAN总线恢复													0x34 消防继电器导通触发门常开		0x75 门状态常闭或休眠状态认证失败
	 *								0x42f  闸机腔体温度超限												0x35 消防继电器恢复门恢复正常		0x76 认证计划休眠模式认证失败
	 *								0x430  红外对射异常													0x36 人脸加指纹认证通过			0x77 卡加密校验失败
	 *								0x431  红外对射恢复													0x37 人脸加指纹认证失败			0x78 反潜回服务器应答失败
	 *								0x432  灯板通信异常													0x38 人脸加指纹认证超时			0x85 尾随通行
	 *								0x433  灯板通信恢复													0x39 人脸加密码认证通过			0x86 反向闯入
	 *								0x434  红外转接板通信异常												0x3a 人脸加密码认证失败			0x87 外力冲撞
	 *								0x435  红外转接板通信恢复												0x3b 人脸加密码认证超时			0x88 翻越
	 *
	 * lCommand 上传的消息类型，不同的报警信息对应不同的类型，通过类型区分是什么报警信息
	 * pAlarmer 报警设备信息，包括设备序列号、IP地址、登录IUserID句柄等
	 * pAlarmInfo 报警信息，通过lCommand值判断pAlarmer对应的结构体
	 ******************************************************************************/
	@Override
	public void msgCallBack(int type,HCNetSDK.RECV_ALARM pAlarmInfo){
		Map<String,String> codeMap = bs.getTCacheMap(SysDict.class, v->v.getType().equals("hikCode"), SysDict::getValue,SysDict::getName);
		if(type == 0x5002) {//门禁主机报警信息
			HCNetSDK.NET_DVR_ACS_ALARM_INFO alarmInfo = new HCNetSDK.NET_DVR_ACS_ALARM_INFO();
			alarmInfo.write();
			Pointer pInfoV30 = alarmInfo.getPointer();
			pInfoV30.write(0, pAlarmInfo.RecvBuffer, 0, alarmInfo.size());
			alarmInfo.read();
			HCNetSDK.NET_DVR_ACS_EVENT_INFO eventInfo = alarmInfo.struAcsEventInfo;
			String dwMajorHex = Integer.toHexString(alarmInfo.dwMajor);//消息主类型
			String dwMinorHex = Integer.toHexString(alarmInfo.dwMinor);//消息次类型
			//alarmInfo.wInductiveEventType 归纳类型
			String prop = dwMajorHex+":"+dwMinorHex;
			DeviceProperty dp = getDeviceProperty(prop);
			String cardNo = new String(eventInfo.byCardNo).trim();
			int employeeNo = eventInfo.dwEmployeeNo;
			String employee = null;
			if(employeeNo != 0){
				if(Strings.isin(new String[]{"5:1","5:26","5:4b"},prop))
					putData("employee",employeeNo);
				TPerson tPerson = bs.getTCacheFirst(TPerson.class, v->(employeeNo+"").equals(v.getEmployeeNo()));
				employee = tPerson != null ?  tPerson.getName() : employeeNo + "";
			}
			sendSocket((dp != null ? dp.getName() : prop) + " 门编号:"+eventInfo.dwDoorNo+
					" 事件触发器编号:"+eventInfo.dwCaseSensorNo+
					" 卡号:"+cardNo+" 卡类型:"+eventInfo.byCardType+
					" 人员:"+employee);

			putData(prop, CommonUtil.getNonNull(employee,cardNo,eventInfo.dwDoorNo));
			//门磁状态
			switch (alarmInfo.dwMinor){
				case 0x408 :
					putData("DI"+eventInfo.dwCaseSensorNo, "1");
					break;
				case 0x409 :
					putData("DI"+eventInfo.dwCaseSensorNo, "0");
					break;
			}
		}
	}

	HCNetSDK.CardCfgCondCallback fc; //长连接回调
	NativeLong handle;// 长连接句柄 获取卡参数2178 设置卡参数2179
	CountDownLatch countDownLatch = new CountDownLatch(1);
	String errMsg = null;
	List<TCard> cardList = new ArrayList<>();

	public NativeLong getHandleGetCard(){
		if(!login())
			return null;
		//if(handleGetCard != null && handleGetCard.intValue() > -1)
		//	return handleGetCard;

		fc = this::cardCfgCondInvoke;
		NET_DVR_CARD_CFG_COND cardCfgCond = new NET_DVR_CARD_CFG_COND();
		cardCfgCond.dwSize = cardCfgCond.size();
		cardCfgCond.dwCardNum = 0xFFFFFFFF;
		cardCfgCond.write();
		handle = hCNetSDK.NET_DVR_StartRemoteConfig(lUserID,2178,cardCfgCond.getPointer(),cardCfgCond.size(),fc,null);
		if(handle.intValue()<0){
			throw Lang.makeThrow("获取卡信息建立长连接失败:"+getErrorMsg());
		}else{
			log.info("获取卡信息长连接句柄:"+ handle);
			return handle;
		}
	}

	public NativeLong getHandleSetCard(){
		if(!login())
			return null;

		fc = this::cardCfgCondInvoke;
		NET_DVR_CARD_CFG_COND cardCfgCond = new NET_DVR_CARD_CFG_COND();
		cardCfgCond.dwSize = cardCfgCond.size();
		cardCfgCond.dwCardNum = 0x1;
		cardCfgCond.byCheckCardNo = 1;
		cardCfgCond.write();
		handle = hCNetSDK.NET_DVR_StartRemoteConfig(lUserID,2179,cardCfgCond.getPointer(),cardCfgCond.size(),fc,null);
		if(handle.intValue()<0){
			throw Lang.makeThrow("设置卡信息建立长连接失败:"+getErrorMsg());
		}else{
			log.info("设置卡信息长连接句柄:"+ handle);
			return handle;
		}
	}

	@AoReflect("开门")
	public boolean openDoor(int index){
		boolean b =  hCNetSDK.NET_DVR_ControlGateway(lUserID,new NativeLong(index),1);
		if(!b){
			throw Lang.makeThrow("开门失败："+getErrorMsg());
		}
		return b;
	}


	@AoReflect("获取卡信息")
	public List<TCard> getCard(){
		NativeLong handle = getHandleGetCard();
		cardList = new ArrayList<>();
		if(handle != null){
			createDown();
		}
		return cardList;
	}

	@AoReflect("设置卡信息")
	public TCard setCard(TCard card){
		NET_DVR_CARD_CFG_V50 cfg = tcard2DvrCard(card);
		NativeLong handle = getHandleSetCard();

		cfg.dwSize = cfg.size();
		cfg.dwModifyParamType = 511;//1023已刷卡次数

		cfg.byDoorRight[0] = 1;
		cfg.byDoorRight[1] = 1;
		cfg.wCardRightPlan[0] = 1;
		cfg.wCardRightPlan[4] = 1;
		//cfg.dwSwipeTime = 99;
		cfg.write();
		boolean b = hCNetSDK.NET_DVR_SendRemoteConfig(handle,3,cfg.getPointer(),cfg.size());
		if(b){
			createDown();
			if(errMsg != null)
				throw Lang.makeThrow("设置卡失败:"+errMsg);
		}
		return card;
	}

	@AoReflect("下载人员到系统")
	public void downPerson(){
		String strInBuffer = "POST /ISAPI/AccessControl/UserInfo/Search?format=json";
		HCNetSDK.BYTE_ARRAY ptrByteArray = new HCNetSDK.BYTE_ARRAY(1024,strInBuffer);    //数组
		ptrByteArray.write();

		NativeLong lHandler = hCNetSDK.NET_DVR_StartRemoteConfig(lUserID, 2550, ptrByteArray.getPointer(), strInBuffer.length(), null, null);
		if (lHandler.longValue() < 0) {
			log.error(getErrorMsg());
			return;
		} else {

			//输入参数，XML或者JSON数据,查询多条人员信息json报文
			NutMap parameter = new NutMap();
			parameter.put("searchID", System.currentTimeMillis()+""); // 查询id
			parameter.put("maxResults", 100); // 最大查询数量
			parameter.put("searchResultPosition",0);
			String strInbuff = "{\"UserInfoSearchCond\":"+ Json.toJson(parameter)+"}";

			//System.out.println("查询的json报文:" + strInbuff);

			//把string传递到Byte数组中，后续用.getPointer()方法传入指针地址中。
			HCNetSDK.BYTE_ARRAY ptrInbuff = new HCNetSDK.BYTE_ARRAY(strInbuff.length());
			System.arraycopy(strInbuff.getBytes(), 0, ptrInbuff.byValue, 0, strInbuff.length());
			ptrInbuff.write();

			//定义接收结果的结构体
			HCNetSDK.BYTE_ARRAY ptrOutuff = new HCNetSDK.BYTE_ARRAY(100 * 1024);

			IntByReference pInt = new IntByReference(0);

			while (true) {
                /*
                dwOutBuffSize是输出缓冲区大小，需要自定义指定大小，如果接口报错错误码43.说明接收设备数据的缓冲区或存放图片缓冲区不足，应扩大缓冲区大小
                 */
				int dwState = hCNetSDK.NET_DVR_SendWithRecvRemoteConfig(lHandler.intValue(), ptrInbuff.getPointer(), strInbuff.length(), ptrOutuff.getPointer(), 100 * 1024, pInt);
				if (dwState == -1) {
					log.error(getErrorMsg());
					break;
				} else if (dwState == 1001) {
					log.warn("配置等待");
					try {
						Thread.sleep(30);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else if (dwState == 1003) {
					log.error("查询人员失败");
					break;
				} else if (dwState == 1004) {
					log.error("查询人员异常");
					break;
				} else if (dwState == 1000) {
					ptrOutuff.read();
					String json = new String(ptrOutuff.byValue, StandardCharsets.UTF_8).trim();

					JSONObject jsonObject = JSONObject.parseObject(json).getJSONObject("UserInfoSearch");
					List<TPerson> list = Json.fromJsonAsList(TPerson.class,jsonObject.getString("UserInfo"));
					for(TPerson person : list){
						if(Strings.isBlank(person.getEmployeeNo()))
							continue;
						TPerson p0 = bs.getTCacheFirst(TPerson.class,v->person.getEmployeeNo().equals(v.getEmployeeNo()));
						if(p0 == null){
							bs.daoSave(person);
							continue;
						}
						if(!Strings.equals(person.getName(),p0.getName())){
							p0.setName(person.getName());
							bs.daoSave(p0);
						}
					}
					break;
				} else if (dwState == 1002) {
					System.out.println("获取人员完成");
					break;
				}
			}

			if (!hCNetSDK.NET_DVR_StopRemoteConfig(lHandler)) {
				log.error(getErrorMsg());
			}
		}
	}

	public void createDown(){
		errMsg = null;
		countDownLatch = new CountDownLatch(1);
		try {
			countDownLatch.await(10, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param dwType 0 回调状态值；1回调进度值 2回调数据内容
	 * @param lpBuffer 存放数据的缓冲区指针，具体内容跟dwType相关
	 */
	public void cardCfgCondInvoke(int dwType, Pointer lpBuffer, int dwBufLen, Pointer pUserData){
		if(dwType == 0){
			/**
			 * 1000，表示获取和配置成功并且结束；
			 * 1001，lpBuffer：4字节状态 + 32字节卡号；
			 * 1002，lpBuffer：4字节状态 + 4字节错误码 + 32字节卡号；
			 * 1003，表示长连接配置异常
			 * 1004, 表示（IPC配置文件导入）语言不匹配
			 * 1005, 表示（IPC配置文件导入）设备类型不匹配
			 * 1006，表示需要等待一段时间再发送
			 */
			byte[] dwStatus = lpBuffer.getByteArray(0,4);
			int iStatus = CalcUtil.byteToInt2(dwStatus);

			switch (iStatus){
				case 1000:
					countDownLatch.countDown();
					log.infof("长连接 %s 已完成",handle);
					break;
				case 1001:
					String card1 = new String(lpBuffer.getByteArray(4,32));
					log.infof("卡号:",card1);
					break;
				case 1002:
					byte[] err = lpBuffer.getByteArray(4,4);
					String card = new String(lpBuffer.getByteArray(8,32));
					errMsg = CalcUtil.byteToInt2(err)+":"+CalcUtil.byteToHex(err);
					break;
				/*case 1003:
					break;
				case 1004:
					break;
				case 1005:
					break;
				case 1006:
					break;*/
				default:
					log.warnf("长连接 %s 状态：%d",handle,iStatus);
					break;
			}

		}else if(dwType == 2){
			NET_DVR_CARD_CFG_V50 cfg = new NET_DVR_CARD_CFG_V50();
			cfg.write();
			cfg.getPointer().write(0,lpBuffer.getByteArray(0,cfg.size()),0,cfg.size());
			cfg.read();
			cardList.add(dvrCard2Tcard(cfg));
		}
	}

	/**
	 * 只适用中途调用，已完成的长连接不能调
	 */
	public void stopRemoteConfig(){
		boolean b2 = hCNetSDK.NET_DVR_StopRemoteConfig(handle);
		System.out.println("关闭长连接 "+handle+(b2?" 成功":" 失败"));
	}





}
