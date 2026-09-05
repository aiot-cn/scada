package org.aiot.model.table;

import org.aiot.lang.annotation.AoTbase;
import org.nutz.dao.entity.annotation.Table;

/**
 * 参数配置
 * 
 */
@Table
@AoTbase
public class TParam extends TBase{

	/**
	 * 0系统  1设备  2系统服务  4通讯 5URL 6环境变量
	 * 3计划任务 废弃
	 */
	private int type;
	private Long cid;
	private String code;
	private String value;


	public TParam(){

	}

	public TParam(int type, Long cid, String code, String value){
		this.cid = cid;
		this.code = code;
		this.value = value;
		this.type = type;
	}

	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public Long getCid() {
		return cid;
	}
	public void setCid(Long cid) {
		this.cid = cid;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	
	/**
	 * 0系统  1设备  2系统服务 3计划任务 4通讯 5URL 6环境变量
	 */
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}

}
