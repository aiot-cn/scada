
package org.aiot.model.table;

import org.aiot.lang.annotation.AoTbase;
import org.nutz.dao.entity.annotation.Table;

@Table
@AoTbase
public class MUserSiteRole extends TBase {

	private static final long serialVersionUID = 7280905054210124650L;
	@AoTbase(from = SysRole.class)
	private Long roleId;
	@AoTbase(from = SysUser.class)
	private Long userId;
	@AoTbase(from = SysSite.class)
	private Long siteId;
	private int isDefault;
	
	public Long getRoleId() {
		return roleId;
	}
	public void setRoleId(Long roleId) {
		this.roleId = roleId;
	}
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}


	public Long getSiteId() {
		return siteId;
	}

	public void setSiteId(Long siteId) {
		this.siteId = siteId;
	}

	public int getIsDefault() {
		return isDefault;
	}

	public void setIsDefault(int isDefault) {
		this.isDefault = isDefault;
	}
}