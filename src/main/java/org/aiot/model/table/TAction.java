package org.aiot.model.table;

import org.aiot.lang.annotation.AoTbase;
import org.nutz.dao.entity.annotation.Table;

@Table
@AoTbase
public class TAction extends TBaseSeq {

	@AoTbase(from = TDevice.class)
	private Long deviceId;//执行设备
    private String method;//执行方法
    private String args;//参数
	private String variable;//返回值变量

	private Long pid;//上层ID
	private String plass;//上层class
	private int type;//类型 01动作链（默认） 2脚本 3流程

	@AoTbase(from = TAction.class)
	private Long parentId;

	public String toString(){
		return 	deviceId + "." +method + "("+args+")";
	}


	public Long getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(Long deviceId) {
		this.deviceId = deviceId;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public String getArgs() {
		return args;
	}

	public void setArgs(String args) {
		this.args = args;
	}

	public Long getPid() {
		return pid;
	}

	public void setPid(Long pid) {
		this.pid = pid;
	}

	public Long getParentId() {
		return parentId;
	}

	public void setParentId(Long parentId) {
		this.parentId = parentId;
	}

	public String getPlass() {
		return plass;
	}

	public void setPlass(String plass) {
		this.plass = plass;
	}

	public String getVariable() {
		return variable;
	}

	public void setVariable(String variable) {
		this.variable = variable;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}
}