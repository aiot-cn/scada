package org.aiot.model.table;

import org.aiot.lang.annotation.AoTbase;
import org.nutz.dao.entity.annotation.Table;

@Table
@AoTbase
public class DeviceAnalysis extends TBaseSeq{
	@AoTbase(from = DeviceCommand.class)
	private Long commandId;
    private String code;
    private Integer start;
    private Integer end;
    private String calc;
	private String pattern;
    private String remark;
	private Integer correct;//小数精度
	private String conversion;//数值转换方法
	private String expected;//期望值[公式]

	public Long getCommandId() {
		return commandId;
	}
	public void setCommandId(Long commandId) {
		this.commandId = commandId;
	}
	public Integer getStart() {
		return start;
	}
	public void setStart(Integer start) {
		this.start = start;
	}
	public Integer getEnd() {
		return end;
	}
	public void setEnd(Integer end) {
		this.end = end;
	}
	public String getCalc() {
		return calc;
	}
	public void setCalc(String calc) {
		this.calc = calc;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public String getPattern() {
		return pattern;
	}
	public void setPattern(String pattern) {
		this.pattern = pattern;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}

	public Integer getCorrect() {
		return correct;
	}

	public void setCorrect(Integer correct) {
		this.correct = correct;
	}

	public String getConversion() {
		return conversion;
	}

	public void setConversion(String conversion) {
		this.conversion = conversion;
	}

	public String getExpected() {
		return expected;
	}

	public void setExpected(String expected) {
		this.expected = expected;
	}
}