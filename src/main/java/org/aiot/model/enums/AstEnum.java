package org.aiot.model.enums;

/**
 * AoStation注解类型
 * @author TAOJIN
 *
 */
public enum AstEnum {
	device("设备"),
	/**
	 * 保存在数据库
	 */
	param("参数"),
	command("指令"),
	analysis("解析"),
	auto("自动");

	private String typeName;
	private AstEnum(String typeName){
		this.typeName = typeName;
	}

	public String getTypeName() {
		return typeName;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}
    
    
}
