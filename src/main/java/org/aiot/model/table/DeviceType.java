package org.aiot.model.table;

import org.aiot.lang.annotation.AoTbase;
import org.aiot.model.enums.DeviceRoleEnum;
import org.nutz.dao.entity.annotation.Table;

/**
 * 设备类型
 */
@Table
@AoTbase
public class DeviceType extends TBaseSeq{
	private DeviceRoleEnum role;
	private String code;
	private String name;
	private String icon;
	private String klass;
	private String protocol;

	public DeviceRoleEnum getRole() {
		return role;
	}

	public void setRole(DeviceRoleEnum role) {
		this.role = role;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getIcon() {
		return icon;
	}
	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getKlass() {
		return klass;
	}
	public void setKlass(String klass) {
		this.klass = klass;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}

	public String getProtocol() {
		return protocol;
	}

	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

	@Override
	public String toString(){
		return name + "("+code+")"+" "+klass;
	}
	
	
}
