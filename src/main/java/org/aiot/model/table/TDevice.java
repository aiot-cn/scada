package org.aiot.model.table;

import org.aiot.lang.annotation.AoTbase;
import org.nutz.dao.entity.annotation.Table;


@Table
@AoTbase("设备")
public class TDevice extends TBaseSeq{

	private String name;//名称
	@AoTbase(from = DeviceType.class,field = "code")
    private String deviceType;//类型
	@AoTbase("通讯")
    private Long communication;//通讯
    private String address;//地址

	@AoTbase(from = TDevice.class)
    private Long parentId;//上级
    private String exp1;//扩展1
    private String exp2;//扩展2
	@AoTbase("坐标")
    private String point;//坐标
	@AoTbase(from = SysSite.class)
	private Long siteId;//站点ID
	@AoTbase("分组")
	private Long groupId;

	private String remark;

	public TDevice(){

	}

	public TDevice(Long id,String name){
		setId(id);
		this.name = name;
	}

	public String getPicture() {
		return picture;
	}

	public void setPicture(String picture) {
		this.picture = picture;
	}

	private String picture;

	@Override
	public String toString(){
		return String.format("%s ID:%d 类型:%s 地址：%s 通讯：%d \n", name,this.getId(),deviceType,address,communication);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	

	public String getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(String deviceType) {
		this.deviceType = deviceType;
	}

	public Long getCommunication() {
		return communication;
	}

	public void setCommunication(Long communication) {
		this.communication = communication;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public Long getParentId() {
		return parentId;
	}

	public void setParentId(Long parentId) {
		this.parentId = parentId;
	}

	public String getExp1() {
		return exp1;
	}

	public void setExp1(String exp1) {
		this.exp1 = exp1;
	}

	public String getExp2() {
		return exp2;
	}

	public void setExp2(String exp2) {
		this.exp2 = exp2;
	}

	public String getPoint() {
		return point;
	}

	public void setPoint(String point) {
		this.point = point;
	}


	public Long getSiteId() {
		return siteId;
	}

	public void setSiteId(Long siteId) {
		this.siteId = siteId;
	}

	public Long getGroupId() {
		return groupId;
	}

	public void setGroupId(Long groupId) {
		this.groupId = groupId;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}
}