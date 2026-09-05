package org.aiot.model;

public class DataRes {
	
	private boolean success = true;
	private String message;
	private Long time;
	private Object data;

	public DataRes() {
	}

	public DataRes(String message) {
		this.message = message;
		this.success = message == null;
	}

	public DataRes(Object data){
		this.data = data;
	}

	public static DataRes success(String msg,Object data){
		DataRes r = new DataRes();
		r.setMessage(msg);
		r.setData(data);
		return r;
	}

	public static DataRes success(String msg){
		return success(msg,null);
	}

	public static DataRes error(String msg){
		DataRes r = new DataRes();
		r.setMessage(msg);
		r.setSuccess(false);
		return r;
	}

	public boolean isSuccess() {
		return success;
	}
	public void setSuccess(boolean success) {
		this.success = success;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public Object getData() {
		return data;
	}
	public void setData(Object data) {
		this.data = data;
	}

	public Long getTime() {
		return System.currentTimeMillis();
	}

	public void setTime(Long time) {
		this.time = time;
	}
}
