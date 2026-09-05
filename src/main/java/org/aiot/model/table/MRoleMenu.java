
package org.aiot.model.table;

import org.aiot.lang.annotation.AoTbase;
import org.nutz.dao.entity.annotation.Table;

@Table
@AoTbase
public class MRoleMenu extends TBase {

	private static final long serialVersionUID = 2105494863534089363L;
	@AoTbase(from = SysRole.class)
	private Long roleId;
	@AoTbase(from = SysMenu.class)
	private Long menuId;

	public Long getRoleId() {
		return roleId;
	}
	public void setRoleId(Long roleId) {
		this.roleId = roleId;
	}
	public Long getMenuId() {
		return menuId;
	}
	public void setMenuId(Long menuId) {
		this.menuId = menuId;
	}
	
	
	
}