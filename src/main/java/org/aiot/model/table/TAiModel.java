package org.aiot.model.table;

import org.aiot.lang.annotation.AoTbase;
import org.aiot.model.enums.aimodel.AlgorithmEnum;
import org.aiot.model.enums.aimodel.TaskEnum;
import org.nutz.dao.entity.annotation.Table;
import org.nutz.lang.Strings;

@Table
@AoTbase
public class TAiModel extends TBase {

	/**
	 * 模型应用场景编码 ModelScenarioEnum
	 * 自定义的取dict modelScenario 的id
	 */
	private String scenarioCode;

	private String name;
	private String modelPath; //模型权重文件路径
	private Double fileSize; //文件大小 单位：Mb
	private String md5;

	private AlgorithmEnum algorithm; //算法框架: yolov5, yolov8, damo
	private TaskEnum taskType;//任务类型: detection, segmentation, obb, classification, pose
	//private String modelScale;//模型量级/规模 n, s, m, l, x 文件名中包含
	//private String inputShape;//输入形状, 如 1,3,640,640
	private String classNames;//person,car,bicycle

	public String getScenarioCode() {
		return scenarioCode;
	}

	public void setScenarioCode(String scenarioCode) {
		this.scenarioCode = scenarioCode;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getModelPath() {
		return modelPath;
	}

	public void setModelPath(String modelPath) {
		this.modelPath = modelPath;
	}

	public Double getFileSize() {
		return fileSize;
	}

	public void setFileSize(Double fileSize) {
		if(fileSize != null)
			this.fileSize = fileSize;
	}

	public String getMd5() {
		return md5;
	}

	public void setMd5(String md5){
		if(Strings.isNotBlank(md5))
			this.md5 = md5;
	}

	public AlgorithmEnum getAlgorithm() {
		return algorithm;
	}

	public void setAlgorithm(AlgorithmEnum algorithm) {
		this.algorithm = algorithm;
	}

	public TaskEnum getTaskType() {
		return taskType;
	}

	public void setTaskType(TaskEnum taskType) {
		this.taskType = taskType;
	}

	public String getClassNames() {
		return classNames;
	}

	public void setClassNames(String classNames) {
		this.classNames = classNames;
	}
}
