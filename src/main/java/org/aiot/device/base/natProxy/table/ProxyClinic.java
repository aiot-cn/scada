package org.aiot.device.base.natProxy.table;

import org.aiot.lang.annotation.AoTbase;
import org.aiot.model.table.TBase;
import org.nutz.dao.entity.annotation.Table;

@Table
@AoTbase
public class ProxyClinic extends TBase {

	private String name;
	private String key;

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
