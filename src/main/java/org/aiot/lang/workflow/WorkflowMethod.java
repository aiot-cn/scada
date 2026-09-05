
package org.aiot.lang.workflow;

import java.util.Map;

public class WorkflowMethod{
	private int type; //0设备方法 1脚本
	private Integer index;
	private Long deviceId;
	private String deviceType;
	private String method;
	private Map<String,Object> arg;
	private String variable;//返回值变量名

	private boolean isStart;
	private boolean isReturn;
	private boolean ignoreException;


	public Integer getIndex() {
		return index;
	}

	public void setIndex(Integer index) {
		this.index = index;
	}

	public Long getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(Long deviceId) {
		this.deviceId = deviceId;
	}

	public String getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(String deviceType) {
		this.deviceType = deviceType;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public Map<String, Object> getArg() {
		return arg;
	}

	public void setArg(Map<String, Object> arg) {
		this.arg = arg;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public boolean isStart() {
		return isStart;
	}

	public void setStart(boolean start) {
		isStart = start;
	}

	public boolean isReturn() {
		return isReturn;
	}

	public void setReturn(boolean aReturn) {
		isReturn = aReturn;
	}

	public String getVariable() {
		return variable;
	}

	public void setVariable(String variable) {
		this.variable = variable;
	}

	public boolean isIgnoreException() {
		return ignoreException;
	}

	public void setIgnoreException(boolean ignoreException) {
		this.ignoreException = ignoreException;
	}
}