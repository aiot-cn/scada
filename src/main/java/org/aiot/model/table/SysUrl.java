
package org.aiot.model.table;

import org.aiot.lang.annotation.AoTbase;
import org.aiot.model.enums.RoleActionEnum;
import org.nutz.dao.entity.annotation.Table;

@Table
@AoTbase
public class SysUrl extends TBase {

	/**
	 * URL 类型
	 * <li>0 页面</li>
	 * <li>1 JSON</li>
	 * <li>2 中转</li>
	 * <li>3 重定向</li>
	 * <li>4 格式、二进制</li>
	 * <li>5 代理</li>
	 * <li>6 SQL</li>
	 */
	private int type;//类型

	private String url;
	private String name;

	private String resParam;//请求参数
	private String script;

	private RoleActionEnum actionCode;//需要的权限

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getResParam() {
		return resParam;
	}

	public void setResParam(String resParam) {
		this.resParam = resParam;
	}

	public String getScript() {
		return script;
	}

	public void setScript(String script) {
		this.script = script;
	}

	public RoleActionEnum getActionCode() {
		return actionCode;
	}

	public void setActionCode(RoleActionEnum actionCode) {
		this.actionCode = actionCode;
	}
}