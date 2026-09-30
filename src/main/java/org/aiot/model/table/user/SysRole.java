
package org.aiot.model.table.user;

import org.aiot.lang.annotation.AoTbase;
import org.aiot.model.table.TBase;
import org.nutz.dao.entity.annotation.Table;

@Table
@AoTbase("角色")
public class SysRole extends TBase {

	private String name;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
}