package org.aiot.device.base.imgAnno.model;

import org.aiot.model.table.TBase;
import org.nutz.dao.entity.annotation.Table;

//验证结果
@Table
public class ImgDetect extends TBase {

	private Long ptId;//模型文件ID
	private String directory;//目录
	private String name;//文件名
	private String tag;
	private Integer tagIndex;
	private String position;//定位
	private Float confidence;//可信度
	private Integer type;//1误识别 2漏识别

	public void setPosMinMax(String xMin,String yMin,String xMax,String yMax){
		float x1 = Float.parseFloat(xMin);
		float y1 = Float.parseFloat(yMin);
		float x2 = Float.parseFloat(xMax);
		float y2 = Float.parseFloat(yMax);
		this.position =  x1 + "," + y1 + "," + (x2-x1) + "," + (y2-y1);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Long getPtId() {
		return ptId;
	}

	public void setPtId(Long ptId) {
		this.ptId = ptId;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public Integer getTagIndex() {
		return tagIndex;
	}

	public void setTagIndex(Integer tagIndex) {
		this.tagIndex = tagIndex;
	}

	public String getPosition() {
		return position;
	}

	public void setPosition(String position) {
		this.position = position;
	}

	public Float getConfidence() {
		return confidence;
	}

	public void setConfidence(Float confidence) {
		this.confidence = confidence;
	}

	public String getDirectory() {
		return directory;
	}

	public void setDirectory(String directory) {
		this.directory = directory;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}
}