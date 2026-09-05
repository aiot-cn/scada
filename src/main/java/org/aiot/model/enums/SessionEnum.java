package org.aiot.model.enums;

import org.nutz.mvc.Mvcs;

import javax.servlet.http.HttpSession;

/**
 * Session
 * @author TAOJIN
 *
 */
public enum SessionEnum {
	/** SysUser对象 */
	user,

	person,

	/** Session描述 */
	principal,

	/** SysSite站点 */
	site,

	/** 所有上级站点包含自身，以逗号分隔*/
	siteIds,

	/** 权限 */
	role;
	

	public void val(Object obj) {
		Mvcs.getHttpSession().setAttribute(this.name(), obj);
	}
	
	@SuppressWarnings("unchecked")
	public <T> T val() {
		HttpSession httpSession = Mvcs.getHttpSession();
		if(httpSession == null) {
			return null; //还没有Session环境
		}
		Object o = httpSession.getAttribute(this.name());
		return (T) o;
	}
 
}
