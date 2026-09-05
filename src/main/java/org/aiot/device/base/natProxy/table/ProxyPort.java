package org.aiot.device.base.natProxy.table;

import org.aiot.lang.annotation.AoTbase;
import org.aiot.model.table.TBase;
import org.nutz.dao.entity.annotation.Table;

@Table
@AoTbase
public class ProxyPort extends TBase {

	private Long clinicId;
	private String name;
	/** 代理服务器端口 */
	private Integer netPort;
	/** 需要代理的网络信息（代理客户端能够访问），格式 192.168.1.99:80 (必须带端口) */
	private String lan;

	public Long getClinicId() {
		return clinicId;
	}

	public void setClinicId(Long clinicId) {
		this.clinicId = clinicId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getNetPort() {
		return netPort;
	}

	public void setNetPort(Integer netPort) {
		this.netPort = netPort;
	}

	public String getLan() {
		return lan;
	}

	public void setLan(String lan) {
		this.lan = lan;
	}
}
