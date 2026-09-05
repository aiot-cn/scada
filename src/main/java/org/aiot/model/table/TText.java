package org.aiot.model.table;

import org.nutz.dao.entity.annotation.Table;

@Table
public class TText extends TClass{

	private String content;

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
}