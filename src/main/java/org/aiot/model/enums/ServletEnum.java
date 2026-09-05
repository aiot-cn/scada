package org.aiot.model.enums;

import org.nutz.mvc.Mvcs;

/**
 * ServletContext application
 * @author TAOJIN
 *
 */
public enum ServletEnum {
	/** 静态文件根路径 */
	res,
	/** 静态文件缓存标识 */
	resCache,
	/** 系统配置 Map<String,Object> */
	config,
	/** 字典 */
	dict,
	/** 序列号 */
	serialNo
;
	

	public void val(Object obj) {
		Mvcs.getServletContext().setAttribute(this.name(), obj);
	}
	
	@SuppressWarnings("unchecked")
	public <T> T val() {
		Object o = Mvcs.getServletContext().getAttribute(this.name());
		return (T) o;
	}
 
}
