package org.aiot.lang;

import org.aiot.infc.ProtocolInfc;
import org.aiot.communication.CommunicationInfc;
import org.aiot.infc.device.DeviceInfc;
import org.aiot.model.enums.CdataEnum;
import org.aiot.model.enums.VarRuntimeEnum;
import org.aiot.model.table.DeviceCommand;
import org.aiot.model.table.DeviceType;
import org.aiot.model.table.TCommunication;
import org.aiot.model.table.TDevice;
import org.aiot.service.BaseService;
import org.aiot.service.CommuService;
import org.aiot.service.DeviceService;
import org.aiot.service.WebsocketRoom;
import org.aiot.util.CalcUtil;
import org.aiot.util.SysUtil;
import org.nutz.lang.Lang;
import org.nutz.lang.Strings;
import org.nutz.log.Log;
import org.nutz.log.Logs;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import static org.aiot.main.Constants.ioc;

public class Command implements Comparable<Command> {
	Log log = Logs.get();
	private final static AtomicLong commandNumber = new AtomicLong();
	private final CountDownLatch countDownLatch = new CountDownLatch(1);
	private final Long number;//顺序
	private final Long[] ts = new Long[3];//[创建时间,发送时间,接收时间]

	private TCommunication communication; //通讯,直发
	private TDevice device;

	private byte[] dataToSend; //准备发送的数据
	private Object[] args; //构建指令时的参数
	private String content; //准备发送的内容，字符串，避免来回转换

	private byte[] dataReceived; //接收到的数据

	//TODO 其它的协议应该不走这里
	private DeviceCommand deviceCommand; // 指令
	//private BaseDevice bd;//JSON会死循环



	private boolean isHex;

	private Integer responseTime; //响应时间
	private Integer outTime; //接收超时



	private String remark;

	private Object receive;//发送后接收到的数据
	
	public Command(){
		ts[0] = System.currentTimeMillis();
		this.number = commandNumber.incrementAndGet();
	}
	public Command(String remark, Object[] format){
		this();
		this.remark = remark;
		this.args = format;
	}

	//最基本的发送
	public Command(String s, boolean isHex, String remark, Object... format){
		this(remark,format);
		this.content = s;
		this.isHex = isHex;
		this.dataToSend = isHex ? CalcUtil.hexToByte(s) : s.getBytes();
		//render 后 bs 不为空
	}

	public Command(TDevice device, byte[] b, String remark, Object... format){
		this(remark,format);
		this.dataToSend = b;
		this.device = device;
		BaseService bs = ioc.get(BaseService.class);
		this.communication = bs.getTCache(TCommunication.class,device.getCommunication());
	}
	
	public Command(TDevice device, DeviceCommand tCommand, String remark, Object... format){
		this(device, (byte[]) null,remark,format);
		this.device = device;
		this.deviceCommand = tCommand;
		this.isHex = tCommand.getIsHex();
		this.responseTime = tCommand.getResponseTime();
		this.outTime = tCommand.getTimeout();
	}

	public DeviceInfc getDeviceInfc(){
		DeviceService ds = ioc.get(DeviceService.class);
		return ds.getInstance(device.getId());
	}

	public DeviceType getDeviceType(){
		BaseService bs = ioc.get(BaseService.class);
		return bs.getTCacheFirst(DeviceType.class,v->Strings.equals(device.getDeviceType(),v.getCode()));
	}

	public ProtocolInfc getProtocolInfc(){
		String protocolClass = getDeviceType().getProtocol();
		if(Strings.isBlank(protocolClass))
			return null;
		CommuService commuService = ioc.get(CommuService.class);
		return commuService.getProtocol(protocolClass);
	}

	public byte[] getDataToSend() {
		return dataToSend;
	}

	public void setDataToSend(byte[] dataToSend) {
		this.dataToSend = dataToSend;
	}

	public byte[] getDataReceived() {
		return dataReceived;
	}

	public void setDataReceived(byte[] dataReceived) {
		this.dataReceived = dataReceived;
	}


	public byte[] sendCommand(Object... args){
		if(args != null && args.length != 0)
			this.args = args;
		CommunicationInfc commuInfc = ioc.get(CommuService.class).getInstance(communication.getId());
		return sendCommand(commuInfc);
	}

	/**
	 * 由通讯实现决定发送的内容
	 * 默认返回byte，
	 */
	public byte[] sendCommand(CommunicationInfc sender){

		this.communication = sender.getCommun();

		String socketMsg = Strings.sBlank(communication.getUri(),communication.getName()) + " " + getStrCon() + " " + (isHex ? "HEX" : "ASCII");

		if(!sender.open())
			return errorSend(socketMsg + " 通讯打开失败:" + communication.getKlass());

		if(device != null){
			socketMsg += " " + device.getName()+"("+device.getAddress()+")";

			long dataTime = 0;
			Integer invl = deviceCommand.getInvl();
			long nowTime = System.currentTimeMillis();
			long afterTime = dataTime + (invl == null ? 0 : invl * 1000) - nowTime;
			long debugTime = VarRuntimeEnum.debugTime.val();
			if(afterTime > 0 &&  debugTime < nowTime ){
				return errorSend(socketMsg + " 下次查询 " + (afterTime/1000) + "S 后");
			}

			Integer delay = deviceCommand.getDelay();//发送前延迟
			Lang.sleep(delay == null ? 10 : delay);
		}

		ts[1] = System.currentTimeMillis();
		if(Strings.isNotBlank(remark)){
			socketMsg += " " + remark;
		}else if(deviceCommand != null){
			socketMsg += " [" + deviceCommand.getCode() + "]" + Strings.sBlank(deviceCommand.getRemark(),"");
		}
		sendSocket(CdataEnum.Tx,socketMsg.replaceAll("null", "") + " ms:" +(ts[1] - ts[0]));
		byte[] b =  sender.send(this);
		if(b != null && b.length > 0){
			dataReceived = b;
		}
		return dataReceived;
	}

	public byte[] errorSend(String msg){
		sendSocket(CdataEnum.Tx,"error "+msg);
		Lang.sleep(1000);
		return null;
	}

	public String byteToStr(byte[] b){
		if(b == null)
			return null;
		String m = deviceCommand.getIsHex() ? CalcUtil.byteToHex(b) : new String(b);
		return m.trim();
	}

	public boolean crcVerify(){
		Object o = SysUtil.scriptByName(deviceCommand.getCrc(), content,this);
		if(o == null)
			return false;
		return (boolean) o;
	}

	//TODO 待移除
	public String isVerify(String m){
		String messagePattern = deviceCommand.getPattern();
		if(Strings.isBlank(messagePattern)) {
			return  null;
		}

		if(messagePattern.equals("$1")) {
			if(m.length() >= 12){
				int L = m.length() - 4;
				if(CalcUtil.crcModbus(m.substring(0,L)).toUpperCase().equals(m.substring(L))){
					return null;
				}else {
					return "校验位不匹配";
				}

			}else if(m.length() == 10){
				int errCode = Integer.parseInt(m.substring(5,6));
				if(errCode == 1)
					return  "不支持该功能码";
				if(errCode == 2)
					return  "超出寄存器地址范围";
				if(errCode == 3)
					return  "超出寄存器最大数量";
				if(errCode == 4)
					return  "请求的数据出错";
			}

		}else{
			String pattern = messagePattern.replaceAll("@",device.getAddress()).replaceAll("\\$exp2",device.getExp2()).replaceAll(" ","");
			if(m.matches(pattern))
				return null;
		}
		return "false";
	}
	

	
	public void sendSocket(CdataEnum type,String msg){
		msg = type.getLabel()+msg;
		if(device != null){
			msg = String.format("[C:%-3dD:%-2d] ",deviceCommand.getId(),device.getId()) + msg;
			if(deviceCommand.isLogRecord() || communication.isLogRecord()){
				log.info(msg);
			}
		}
		WebsocketRoom.sendCommu(communication,msg);
	}
	
	
	public String getStrCon(){
		String  c = content.replaceAll("\r\n", "\\\\r\\\\n");
		if(isHex){
			c = c.replaceAll("(.{2})", "$1 ");
		}
		return c;
	}
	
	@Override
	public int compareTo(Command o2) {
		int p1 = 0,p2 = 0;
		DeviceCommand c1 = this.getDeviceCommand(),c2 = o2.getDeviceCommand();
		if(c1 != null && c1.getPriority() != null)
			p1 =c1.getPriority();

		if(c2 != null && c2.getPriority() != null)
			p2 =c2.getPriority();
		return p1 != p2 ? p1 - p2 : this.number.compareTo(o2.number);
	}

	/**
	 * 等待接收 在延迟与响应时间的基础上再等待
	 * @param timeout 单位:秒
	 */
	public byte[] getRxAwait(int timeout){
		long T = timeout*1000L;
		if(deviceCommand != null){
			Integer t2 = deviceCommand.getDelay();
			Integer t3 = deviceCommand.getResponseTime();
			if(t2 != null)
				T += t2;
			if(t3 != null)
				T += t3;
		}

		try {
			countDownLatch.await(T, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return dataReceived;
	}


	/**
	 * tx是发送(transport),rx是接收(receive)
	 */
	public void setRX(byte[] rX) {
		ts[2] = System.currentTimeMillis();
		dataReceived = rX;
		countDownLatch.countDown();
		String message = "NULL ";
		if(rX != null){
			message = byteToStr(rX);
			if(deviceCommand.getIsHex())
				message =  message.replaceAll("(.{2})", "$1 ");
		}

		if(rX != null)
			message += " byte:"+rX.length;
		if(ts[1] != null)
			message += " ms:"+(ts[2]-ts[1]);
		sendSocket(CdataEnum.Rx,message);
	}


	@Override
	public String toString(){
		String s = remark + " - " + content;
		if(isHex)
			s += "(HEX)";
		if(device != null)
			s += " 设备:" + device.getName();
		if(communication != null)
			s += " " + communication.getKlass() + ":" + communication.getUri();
		return s;
	}


	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}
	
	public TCommunication getCommunication() {
		return communication;
	}

	public void setCommunication(TCommunication communication) {
		this.communication = communication;
	}

	public DeviceCommand getDeviceCommand() {
		return deviceCommand;
	}

	public TDevice getDevice() {
		return device;
	}

	public void setDevice(TDevice device) {
		this.device = device;
	}


	public boolean isHex() {
		return isHex;
	}

	public void setHex(boolean isHex) {
		this.isHex = isHex;
	}

	public Object[] getArgs() {
		return args;
	}

	public void setArgs(Object[] args) {
		this.args = args;
	}

	public <T> T getReceive() {
		return (T)receive;
	}

	public void setReceive(Object receive) {
		this.receive = receive;
	}

	public void setDeviceCommand(DeviceCommand deviceCommand) {
		this.deviceCommand = deviceCommand;
	}

	public Integer getResponseTime() {
		return responseTime;
	}

	public void setResponseTime(Integer responseTime) {
		this.responseTime = responseTime;
	}

	public Integer getOutTime() {
		return outTime;
	}

	public void setOutTime(Integer outTime) {
		this.outTime = outTime;
	}

	public long getCreateTime(){
		return ts[0];
	}
}
