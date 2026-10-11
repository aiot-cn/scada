
package org.aiot.model.table;

import org.aiot.lang.annotation.AoTbase;
import org.aiot.model.enums.LogEventEnum;
import org.nutz.dao.entity.annotation.Table;

@Table
@AoTbase(cache = false)
public class TLog extends TBase{

	private LogEventEnum eventType;
	private int logLevel; //0信息 1警告 2错误
	private String content;
	private String source;//来源

	public LogEventEnum getEventType() {
		return eventType;
	}

	public void setEventType(LogEventEnum eventType) {
		this.eventType = eventType;
	}

	public int getLogLevel() {
		return logLevel;
	}

	public void setLogLevel(int logLevel) {
		this.logLevel = logLevel;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}
}
