
package org.aiot.lang.workflow;

import org.nutz.lang.Strings;

public class WorkflowConnection implements Comparable<WorkflowConnection>{
	private String source;//1-OUTPUT
	private String target;//2-INPUT
	private Integer priority;
	private String condition;

	private Integer sourceIndex;
	private Integer targetIndex;
	private String targetField;

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
		String[] s = source.split("-");
		sourceIndex = Integer.parseInt(s[0]);
	}

	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
		String[] s = target.split("-");
		targetIndex = Integer.parseInt(s[0]);
		targetField = s[1];
	}

	public Integer getPriority() {
		return priority;
	}

	public void setPriority(Integer priority) {
		this.priority = priority;
	}

	public String getCondition() {
		return condition;
	}

	public void setCondition(String condition) {
		this.condition = condition;
	}

	public Integer getSourceIndex() {
		return sourceIndex;
	}

	public void setSourceIndex(Integer sourceIndex) {
		this.sourceIndex = sourceIndex;
	}

	public Integer getTargetIndex() {
		return targetIndex;
	}

	public void setTargetIndex(Integer targetIndex) {
		this.targetIndex = targetIndex;
	}

	public String getTargetField() {
		return targetField;
	}

	public void setTargetField(String targetField) {
		this.targetField = targetField;
	}

	@Override
	public int compareTo(WorkflowConnection t) {
		int a = (	t.priority == null || 	 t.priority == 0) ? 99 : 	t.priority;
		int b = (this.priority == null || this.priority == 0) ? 99 : this.priority;
		int c = b - a;
		if(c != 0 )
			return c;
		a = Strings.isBlank(   t.condition) ? 1 : 0;
		b = Strings.isBlank(this.condition) ? 1 : 0;
		return b - a;
	}
}