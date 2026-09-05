package org.aiot.model.enums;

/**
 * 通信数据类型
 * tx是发送(transport),rx是接收(receive)
 * @author TAOJIN
 *
 */
public enum CdataEnum {

	Tx ("发送","Tx >>> "),
	Rx ("接收","Rx <<< "),
	Pa ("解析","Pa --- "),
	Er ("错误","error  "),
	OTS("其它","OTS -- ")
	;


	private String name;
	private String label;

	private CdataEnum(String name,String label){
		this.name = name;
		this.label = label;
	}

	public String getName() {
		return name;
	}

	public String getLabel() {
		return label;
	}
}
