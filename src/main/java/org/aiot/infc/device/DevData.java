package org.aiot.infc.device;

public class DevData {
	private Object value;//值
	private Integer type;//0遥测 1遥信 2遥控 3遥调
	private Integer state;//状态 -1挂牌 0正常 1预警 2报警
	private Long time;

	private Object prevVal;
	private Integer prevState;
	private Long prevTime;

	private Integer toVal;//强制/手动控制
	private long toTime;

	public  boolean isNumber() {
		return value != null && value instanceof Number;
	}

	public boolean changedVal(Float diff){
		if(prevVal == null || value == null || diff == null)
			return false;
		try {
			return Math.abs((Float) prevVal) - ((Float)value) >= diff;
		}catch (Exception e){
			e.printStackTrace();
		}

		return false;
	}

	public boolean changedState(){
		return prevState != null && !prevState.equals(state);
	}

	public boolean changedTime(int second){
		if(prevTime == null)
			return true;
		return time - prevTime > second * 1000L;
	}
	
	public Object getValue() {
		return value;
	}

	public String getString() {
		if(value == null)
			return null;
		return value.toString();
	}

	public Integer getInt(){
		if(value == null)
			return null;
		if(isNumber())
			return ((Number) value).intValue();
		return Integer.parseInt(value.toString());
	}

	public Long getLong(){
		if(value == null)
			return null;
		if(isNumber())
			return ((Number) value).longValue();
		return Long.parseLong(value.toString());
	}

	public Float getFloat(){
		if(value == null)
			return null;
		if(isNumber())
			return ((Number) value).floatValue();
		return Float.parseFloat(value.toString());
	}

	public void setValue(Object value) {
		this.prevVal = this.value;
		this.value = value;
	}

	public Long getTime() {
		return time;
	}

	public void setTime(Long time) {
		this.prevTime = this.time;
		this.time = time;
	}

	public Integer getState() {
		return state;
	}

	public void setState(Integer state) {
		this.prevState = this.state;
		this.state = state;
	}


	public Integer getToVal() {
		return toVal;
	}

	public void setToVal(Integer toVal) {
		this.toVal = toVal;
	}

	public Object getPrevVal() {
		return prevVal;
	}

	public void setPrevVal(String prevVal) {
		this.prevVal = prevVal;
	}

	public Integer getPrevState() {
		return prevState;
	}

	public void setPrevState(Integer prevState) {
		this.prevState = prevState;
	}

	public Long getPrevTime() {
		return prevTime;
	}

	public void setPrevTime(Long prevTime) {
		this.prevTime = prevTime;
	}


	public long getToTime() {
		return toTime;
	}

	public void setToTime(long toTime) {
		this.toTime = toTime;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}
}
