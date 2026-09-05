package org.aiot.model.table;


public class TBaseAction extends TBaseSeq{

	private Integer actionType;//1动作组 2脚本 3流程
	private String actionCode;

	public Integer getActionType() {
		return actionType;
	}

	public void setActionType(Integer actionType) {
		this.actionType = actionType;
	}

	public String getActionCode() {
		return actionCode;
	}

	public void setActionCode(String actionCode) {
		this.actionCode = actionCode;
	}
}