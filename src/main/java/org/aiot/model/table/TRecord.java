package org.aiot.model.table;

import org.aiot.util.FileUtil;
import org.nutz.dao.entity.annotation.Table;

import java.io.File;

@Table
public class TRecord extends TBase{

	private Long pid;//关联ID,目前仅 pointId
	//private Integer type;

	//Float在数据库会有精度损失
	private Double value;
	private String valStr;

	private Integer state; //0正常 1预警 2报警
	private String remark;
	private String file;
	private String targets;//name,confidence,left,top,width,height

	public TRecord(){}

	public Double getValue() {
		return value;
	}
	public void setValue(Double value) {
		this.value = value;
	}

	public Integer getState() {
		return state;
	}

	public void setState(Integer state) {
		this.state = state;
	}

	public Long getPid() {
		return pid;
	}

	public void setPid(Long pid) {
		this.pid = pid;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getFile() {
		return file;
	}

	public void setFile(String file) {
		this.file = file;
	}

	public String getTargets() {
		return targets;
	}

	public void setTargets(String targets) {
		this.targets = targets;
	}

	public String getValStr() {
		return valStr;
	}

	public void setValStr(String valStr) {
		this.valStr = valStr;
	}

	public void setFile(File file) {
		this.file = FileUtil.toPath(file);
	}

	public void setVal(Object val){
		if(val == null)
			return;

		if(val instanceof Number)
			this.value = ((Number) val).doubleValue();
		else if(val instanceof String){
			try {
				this.value = Double.parseDouble(val.toString());
			}catch (Exception e){
				this.valStr = val.toString();
			}
		}
		else
			this.valStr = val.toString();
	}
}
