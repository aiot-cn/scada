
package org.aiot.model.table;

import org.nutz.dao.entity.annotation.Table;

@Table
public class TLog extends TText{

	private int type;//0 信息 1警告 2错误

	public TLog(){

	}

	public TLog(String content){
		setContent(content);
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

}