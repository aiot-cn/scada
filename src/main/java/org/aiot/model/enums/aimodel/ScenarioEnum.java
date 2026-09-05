package org.aiot.model.enums.aimodel;

/**
 * 模型应用场景
 * @author dtj
 *
 */
public enum ScenarioEnum {
	cocoData("通用检测（coco数据集）"),
	ocr("文字识别"),
	face("人脸"),
	licensePlate("车牌"),

	firework("烟火"),
	water("水"),
	safetyHelmet("安全帽"),
	reflectiveVest("反光衣"),
	faceMask("口罩"),

	smoking("吸烟"),
	phoneCall("打电话"),
	sleepingOnDuty("睡岗"),
	fallDown("跌倒"),

	digitalDisplay("数显表"),
	dialGauge("指针表"),
	pressurePlate("压板"),
	indicatorLight("指示灯"),

	;
	
	private String name;
	
	
	ScenarioEnum(String name){
		this.name = name;
	}

	public String getName() {
		return name;
	}
	
}
