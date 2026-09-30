
package org.aiot.model.table.user;

import org.aiot.lang.annotation.AoTbase;
import org.aiot.model.enums.RoleActionEnum;
import org.aiot.model.table.TBase;
import org.nutz.dao.entity.annotation.Table;

@Table
@AoTbase
public class MRoleMenuAction extends TBase {

	@AoTbase(from = SysRole.class)
	private Long roleId;

	@AoTbase(from = SysMenu.class)
	private Long menuId;

	private RoleActionEnum actionCode;
	
	public Long getRoleId() {
		return roleId;
	}
	public void setRoleId(Long roleId) {
		this.roleId = roleId;
	}
	
	public RoleActionEnum getActionCode() {
		return actionCode;
	}
	public void setActionCode(RoleActionEnum actionCode) {
		this.actionCode = actionCode;
	}

	public Long getMenuId() {
		return menuId;
	}

	public void setMenuId(Long menuId) {
		this.menuId = menuId;
	}
}