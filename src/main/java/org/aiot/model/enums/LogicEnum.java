package org.aiot.model.enums;

/**
 *  逻辑符
 * @author TAOJIN
 *
 */
public enum LogicEnum {

	AND("并且","&&"),
	OR("或者","||"),
	AND_NOT("且不","&& !"),
	OR_NOT("或不","|| !")
	;
	
	private String name;
	private String symbol;
	
	private LogicEnum(String name,String symbol){
		this.name = name;
		this.symbol = symbol;
	}

	public String getName(){
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}
}
