package org.aiot.model.lang;

import com.alibaba.fastjson.util.TypeUtils;
import org.aiot.infc.ImgInfc;
import org.aiot.infc.ValInfc;
import org.nutz.mvc.View;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class PointData implements ValInfc, ImgInfc, View {

	private Object value;
	private Integer state;//状态 0正常 1预警 2报警
	private Long time;

	private Object prevVal;
	private Integer prevState;
	private Long prevTime;

	private Integer toVal;//强制/手动控制
	private Long toTime;

	public void setValue(Object value,Integer state){
		this.prevVal = this.value;
		this.value = value;

		this.prevState = this.state;
		this.state = state;

		this.prevTime = this.time;
		this.time = System.currentTimeMillis();
	}

	public void setValue(Object value) {
		setValue(value,null);
	}

	@Override
	public String getRemark() {
		return null;
	}

	@Override
	public void setRemark(String remark) {

	}

	public boolean changedVal(Double diff){
		if(prevVal == null || value == null || diff == null)
			return false;
		try {
			double f = TypeUtils.castToDouble(prevVal) - TypeUtils.castToDouble(value);
			return Math.abs(f) >= diff;
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

	public  boolean isNumber() {
		return value != null && value instanceof Number;
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

	public Integer getState() {
		return state;
	}

	public void setState(Integer state) {
		this.state = state;
	}

	public Long getTime() {
		return time;
	}

	public void setTime(Long time) {
		this.time = time;
	}

	public Object getPrevVal() {
		return prevVal;
	}

	public void setPrevVal(Object prevVal) {
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

	public Integer getToVal() {
		return toVal;
	}

	public void setToVal(Integer toVal) {
		this.toVal = toVal;
	}

	public Long getToTime() {
		return toTime;
	}

	public void setToTime(Long toTime) {
		this.toTime = toTime;
	}

	@Override
	public void render(HttpServletRequest req, HttpServletResponse resp, Object obj) throws Throwable {
		if(value instanceof View)
			((View)value).render(req,resp,obj);
	}

	@Override
	public byte[] getImgBytes() {
		if(value instanceof ImgInfc)
			return ((ImgInfc)value).getImgBytes();
		return null;
	}
}
