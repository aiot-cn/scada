
package org.aiot.model.table;

import org.aiot.lang.annotation.AoTbase;
import org.nutz.dao.entity.annotation.Table;

@Table
@AoTbase
public class SysMenu extends TBaseSeq {

	private static final long serialVersionUID = 3785990682825335022L;
	private int type;//0菜单 1按钮
	private String name;
	private String url;
	private String icon;
	private Long parentId;
	@AoTbase(from = TDevice.class)
	private Long deviceId;

	private Integer isPc;
	private Integer isMobile;
	private Integer isConsole;
	private Integer isExternal;

	private String exp;//扩展

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getIcon() {
		return icon;
	}
	public void setIcon(String icon) {
		this.icon = icon;
	}
	public Long getParentId() {
		return parentId;
	}
	public void setParentId(Long parentId) {
		this.parentId = parentId;
	}

	public Long getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(Long deviceId) {
		this.deviceId = deviceId;
	}

	public Integer getIsPc() {
		return isPc;
	}

	public void setIsPc(Integer isPc) {
		this.isPc = isPc;
	}

	public Integer getIsMobile() {
		return isMobile;
	}

	public void setIsMobile(Integer isMobile) {
		this.isMobile = isMobile;
	}

	public Integer getIsConsole() {
		return isConsole;
	}

	public void setIsConsole(Integer isConsole) {
		this.isConsole = isConsole;
	}

	public Integer getIsExternal() {
		return isExternal;
	}

	public void setIsExternal(Integer isExternal) {
		this.isExternal = isExternal;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getExp() {
		return exp;
	}

	public void setExp(String exp) {
		this.exp = exp;
	}
}