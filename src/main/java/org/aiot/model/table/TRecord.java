package org.aiot.model.table;

import org.aiot.lang.annotation.AoTbase;
import org.aiot.util.FileUtil;
import org.nutz.dao.entity.annotation.Table;
import org.nutz.lang.Strings;

import java.io.File;
import java.util.Date;

@Table
@AoTbase(cache = false)
public class TRecord extends TBase{

	private Long pid;//关联ID,目前仅 pointId
	//private Integer type;

	//Float在数据库会有精度损失
	private Double value;
	private String valStr;

	//0正常 1预警 2报警
	private Integer state;
	//复核之后的状态
	private Integer reviewState;
	private Date reviewDate; //复核时间
	private String reviewOpinion;//复核意见

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

	public Integer getReviewState() {
		return reviewState;
	}

	public void setReviewState(Integer reviewState){
		this.reviewState = reviewState;
		if(reviewState != null)
			reviewDate = new Date();
	}

	public Date getReviewDate() {
		return reviewDate;
	}

	public void setReviewDate(Date reviewDate) {
		this.reviewDate = reviewDate;
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

	public String getReviewOpinion() {
		return reviewOpinion;
	}

	public void setReviewOpinion(String reviewOpinion) {
		this.reviewOpinion = Strings.isBlank(reviewOpinion) ? null : reviewOpinion;
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
