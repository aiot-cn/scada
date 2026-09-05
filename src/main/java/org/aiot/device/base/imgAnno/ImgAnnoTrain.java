package org.aiot.device.base.imgAnno;

import org.aiot.device.base.imgAnno.model.ImgTag;
import org.aiot.model.enums.PathEnum;
import org.nutz.lang.Strings;


import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


public class ImgAnnoTrain {

	private int produce;//0 YOLO5 1 YOLO11
	private int model;//0目标检测 1语义分割 2分类 3姿势估计 4obb 5目标OCR
	private Long proId; //项目ID
	private String dir;
	private Long[] groupId;
	private Long[] tagId;
	private String[] tagCode;

	private String valDir; //
	private Float valPercent;//验证集比例
	private String testDir;

	private String yaml; //yaml文件
	private String weights; //初始模型
	private Integer epochs; //训练轮次 目标检测300 分类10
	private Integer imgsz; //图像大小 目标检测640 分类224
	private Integer batchSize; //批次大小 默认-1 根据GPU自动
	private boolean multiScale;

	private String classifyDir;//分类训练目录

	private String trainName; //训练名称
	private Long startTime;//训练开始时间
	private String trainProgress; //训练进度

	private List<ImgTag> tags;
	private String tagsStr;
	private int imgCount;
	private int labelCount;

	private Long[] weChatPush;

	public ImgAnnoTrain(){}
	public ImgAnnoTrain(Long proId){
		this.proId = proId;
	}
	public ImgAnnoTrain(Long proId, String dir, Integer epochs, Integer imgsz, Integer batchSize){
		this.proId = proId;
		this.dir = dir;
		this.epochs = epochs;
		this.imgsz = imgsz;
		this.batchSize = batchSize;
	}

	public void setProInfo(Long[] groupId,Long[] tagId){
		this.groupId = groupId;
		this.tagId = tagId;
	}

	//https://docs.ultralytics.com/zh/modes/train
	public String renderYoloArg(){
		String param = "";
		param += " --project " +(PathEnum.image.p2() + "yolo/train"); //训练结果目录
		if(imgsz != null)
			param += " --imgsz "+this.imgsz;

		if(epochs != null)
			param += " --epochs "+epochs;
		if(batchSize != null)
			param += " --batch-size "+batchSize;
		if(weights != null)
			param += " --weights "+"/"+weights;
		if(multiScale)
			param += " --multi-scale "; //多尺度训练
		/*if(savePeriod != null) //模型保存周期
			param += " --save-period "+savePeriod;*/
		return param;
	}

	public String getTagsStr() {
		if(tags != null){
			String tagCode = tags.stream().map(ImgTag::getCode).collect(Collectors.joining(","));
			String tagName = tags.stream().map(ImgTag::getName).collect(Collectors.joining(","));
			return String.format("[标签] %s\n[别名] %s", tagCode, tagName);
		}
		return tagsStr;
	}

	public void setTagsStr(String tagsStr) {
		this.tagsStr = tagsStr;
	}


	public void imgCountInc(){
		imgCount ++;
	}
	public void labelCountInc(){
		labelCount ++;
	}

	@Override
	public boolean equals(Object o){
		if(o instanceof ImgAnnoTrain){
			ImgAnnoTrain t = (ImgAnnoTrain)o;
			if(yaml != null)
				return yaml.equals(t.getYaml());

			String s1 = proId + dir + Arrays.toString(groupId)+Arrays.toString(tagId);
			String s2 = t.getProId() + t.getDir() + Arrays.toString(t.getGroupId())+Arrays.toString(t.getTagId());
			return s1.equals(s2);
		}

		return false;
	}

	public boolean equals(Long proId,String dir){
		return this.proId.equals(proId) && Strings.equals(this.dir,dir);
	}

	/**
	 * 0目标检测 1语义分割 2分类 3姿势估计 4obb 5目标OCR
	 */
	public int getModel() {
		return model;
	}

	public void setModel(int model) {
		this.model = model;
	}

	public Long getProId() {
		return proId;
	}

	public void setProId(Long proId) {
		this.proId = proId;
	}

	public String getDir() {
		return dir;
	}

	public void setDir(String dir) {
		this.dir = dir;
	}

	public Long[] getGroupId() {
		return groupId;
	}

	public void setGroupId(Long[] groupId) {
		this.groupId = groupId;
	}

	public Long[] getTagId() {
		return tagId;
	}

	public void setTagId(Long[] tagId) {
		this.tagId = tagId;
	}

	public String getYaml() {
		return yaml;
	}

	public void setYaml(String yaml) {
		this.yaml = yaml;
	}


	public String getWeights() {
		return weights;
	}

	public void setWeights(String weights) {
		this.weights = weights;
	}

	public Integer getEpochs() {
		return epochs;
	}

	public void setEpochs(Integer epochs) {
		this.epochs = epochs;
	}

	public Integer getImgsz() {
		return imgsz;
	}

	public Integer getBatchSize() {
		return batchSize;
	}

	public void setBatchSize(Integer batchSize) {
		this.batchSize = batchSize;
	}

	public void setImgsz(Integer imgsz) {
		this.imgsz = imgsz;
	}

	public String getTrainName() {
		return trainName;
	}

	public void setTrainName(String trainName) {
		this.trainName = trainName;
	}

	public Long getStartTime() {
		return startTime;
	}

	public void setStartTime(Long startTime) {
		this.startTime = startTime;
	}

	public List<ImgTag> getTags() {
		return tags;
	}

	public void setTags(List<ImgTag> tags) {
		this.tags = tags;
	}

	public int getImgCount() {
		return imgCount;
	}

	public void setImgCount(int imgCount) {
		this.imgCount = imgCount;
	}

	public int getLabelCount() {
		return labelCount;
	}

	public void setLabelCount(int labelCount) {
		this.labelCount = labelCount;
	}

	public String getClassifyDir() {
		return classifyDir;
	}

	public void setClassifyDir(String classifyDir) {
		this.classifyDir = classifyDir;
	}

	public String getValDir() {
		return valDir;
	}

	public void setValDir(String valDir) {
		this.valDir = valDir;
	}

	public String getTestDir() {
		return testDir;
	}

	public void setTestDir(String testDir) {
		this.testDir = testDir;
	}

	public boolean isMultiScale() {
		return multiScale;
	}

	public void setMultiScale(boolean multiScale) {
		this.multiScale = multiScale;
	}

	public String[] getTagCode() {
		return tagCode;
	}

	public void setTagCode(String[] tagCode) {
		this.tagCode = tagCode;
	}

	public String getTrainProgress() {
		return trainProgress;
	}

	public void setTrainProgress(String trainProgress) {
		this.trainProgress = trainProgress;
	}

	public Long[] getWeChatPush() {
		return weChatPush;
	}

	public void setWeChatPush(Long[] weChatPush) {
		this.weChatPush = weChatPush;
	}

	public Float getValPercent() {
		return valPercent;
	}

	public void setValPercent(Float valPercent) {
		this.valPercent = valPercent;
	}
}