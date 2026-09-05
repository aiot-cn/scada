package org.aiot.model.table;


import org.aiot.lang.annotation.AoTbase;
import org.nutz.dao.entity.annotation.Table;

import java.util.List;

@Table
@AoTbase
public class DeviceCommand extends TBase{

	@AoTbase(from = DeviceType.class,field = "code")
	private String deviceType;//设备类型
    private String code;//指令类型
    private String content;//内容
    private boolean isHex;   
    private String pattern;//数据验证
    private Integer priority;//执行优先级
    private Integer delay;//发送前延迟
    private Integer responseTime;//响应时间
    private Integer timeout;//数据超时
    private Integer invl;//周期
	private boolean cover;//覆盖（后面的指令会覆盖前面未执行的指令）
    private boolean logRecord;//记录日志
    private String remark;
	private String crc;//循环冗余码校验

	private List<DeviceAnalysis> analysis;

	public String toString(){
		return String.format("%s:%s %s\n",deviceType, code,content);
	}

	public String getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(String deviceType) {
		this.deviceType = deviceType;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public boolean getIsHex() {
		return isHex;
	}

	public void setIsHex(boolean isHex) {
		this.isHex = isHex;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getPattern() {
		return pattern;
	}

	public void setPattern(String pattern) {
		this.pattern = pattern;
	}

	public Integer getPriority() {
		return priority;
	}

	public void setPriority(Integer priority) {
		this.priority = priority;
	}

	public Integer getDelay() {
		return delay;
	}

	public void setDelay(Integer delay) {
		this.delay = delay;
	}

	public Integer getResponseTime() {
		return responseTime;
	}

	public void setResponseTime(Integer responseTime) {
		this.responseTime = responseTime;
	}

	public Integer getTimeout() {
		return timeout;
	}

	public void setTimeout(Integer timeout) {
		this.timeout = timeout;
	}

	public Integer getInvl() {
		return invl;
	}

	public void setInvl(Integer invl) {
		this.invl = invl;
	}


	public boolean isLogRecord() {
		return logRecord;
	}

	public void setLogRecord(boolean logRecord) {
		this.logRecord = logRecord;
	}

	public String getCrc() {
		return crc;
	}

	public void setCrc(String crc) {
		this.crc = crc;
	}

	public boolean isCover() {
		return cover;
	}

	public void setCover(boolean cover) {
		this.cover = cover;
	}

	public List<DeviceAnalysis> getAnalysis() {
		return analysis;
	}

	public void setAnalysis(List<DeviceAnalysis> analysis) {
		this.analysis = analysis;
	}
}