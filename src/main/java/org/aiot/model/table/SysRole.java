
package org.aiot.model.table;

import org.nutz.dao.entity.annotation.Table;

@Table
public class SysRole extends TBase{

	private static final long serialVersionUID = -6543932864385292551L;

	private String name;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	
	
}