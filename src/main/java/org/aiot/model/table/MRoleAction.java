
package org.aiot.model.table;

import org.aiot.lang.annotation.AoTbase;
import org.aiot.model.enums.ActionEnum;
import org.nutz.dao.entity.annotation.Table;

@Table
@AoTbase
public class MRoleAction extends TBase {
	
	private static final long serialVersionUID = 2817741558408694028L;
	@AoTbase(from = SysRole.class)
	private Long roleId; 
	private ActionEnum actionCode;
	
	public Long getRoleId() {
		return roleId;
	}
	public void setRoleId(Long roleId) {
		this.roleId = roleId;
	}
	
	public ActionEnum getActionCode() {
		return actionCode;
	}
	public void setActionCode(ActionEnum actionCode) {
		this.actionCode = actionCode;
	}
	
	
}