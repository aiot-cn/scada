package org.aiot.model.table;

import org.aiot.lang.annotation.AoTbase;
import org.nutz.dao.entity.annotation.Table;

@Table
@AoTbase
public class SqlCondition extends TBaseSeq{

	private Long codeId;
	private String name;
	private String op;
	private String value;
	private String gro;
	private String ao;
	private String sql;
	private String remark;
	
	
	public Long getCodeId() {
		return codeId;
	}
	public void setCodeId(Long codeId) {
		this.codeId = codeId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getOp() {
		return op;
	}
	public void setOp(String op) {
		this.op = op;
	}

	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	
	public String getGro() {
		return gro;
	}
	public void setGro(String gro) {
		this.gro = gro;
	}
	public String getAo() {
		return ao;
	}
	public void setAo(String ao) {
		this.ao = ao;
	}
	public String getSql() {
		return sql;
	}
	public void setSql(String sql) {
		this.sql = sql;
	}
	
	
	
	
	
	
}
