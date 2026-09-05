package org.aiot.device.base.imgAnno.model;

import org.aiot.model.table.TBase;
import org.nutz.dao.entity.annotation.Table;

@Table

public class ImgAnnotation extends TBase {

	private Long pid;
	private Long tagId;
	private long groupId;
	private String dir;
	private String name; //文件名
	private String position;//定位 left,top,width,height,rotate
	private int type;//-1 矩形遮盖 0 物体检测 1语义分割
	private Float confidence;//可信度

	public ImgAnnotation() {

	}

	public ImgAnnotation(Long pid,String dir, String name, Long tagId, long groupId) {
		this.pid = pid;
		this.dir = dir;
		this.name = name;
		this.tagId = tagId;
		this.groupId = groupId;
	}

	public String getPosYolo(){
		if(type == 1){
			return position.replaceAll(","," ");
		}
		String[] b = position.split(",");
		float x = Math.max(Float.parseFloat(b[0]),0);
		float y = Math.max(Float.parseFloat(b[1]),0);
		float w = Math.min(Float.parseFloat(b[2]),1-x);
		float h = Math.min(Float.parseFloat(b[3]),1-y);
		return (x + w/2) + " " + (y + h/2) + " " + w + " " + h;
	}

	public float[] getPosFloat(){
		String[] b = position.split(",");
		float[] f = new float[b.length];
		for(int i=0;i<b.length;i++){
			f[i] = Float.parseFloat(b[i]);
		}
		return f;
	}

	public void setPosMinMax(String xMin,String yMin,String xMax,String yMax){
		float x1 = Float.parseFloat(xMin);
		float y1 = Float.parseFloat(yMin);
		float x2 = Float.parseFloat(xMax);
		float y2 = Float.parseFloat(yMax);
		this.position =  x1 + "," + y1 + "," + (x2-x1) + "," + (y2-y1);
	}


	public Long getPid() {
		return pid;
	}

	public void setPid(Long pid) {
		this.pid = pid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPosition() {
		return position;
	}

	public void setPosition(String position) {
		this.position = position;
	}

	public Long getTagId() {
		return tagId;
	}

	public void setTagId(Long tagId) {
		this.tagId = tagId;
	}

	/**
	 * -1 矩形遮盖 0 物体检测 1语义分割
	 */
	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public long getGroupId() {
		return groupId;
	}

	public void setGroupId(long groupId) {
		this.groupId = groupId;
	}

	public Float getConfidence() {
		return confidence;
	}

	public void setConfidence(Float confidence) {
		this.confidence = confidence;
	}

	public String getDir() {
		return dir;
	}

	public void setDir(String dir) {
		this.dir = dir;
	}

	@Override
	public boolean equals(Object o){
		if(o instanceof ImgAnnotation){
			ImgAnnotation t2 = (ImgAnnotation) o;
			return (this.name + this.position + this.tagId).equals(t2.getName() + t2.getPosition()+t2.getTagId());
		}
		return false;
	}

	public static class Pos{
		public float x;
		public float y;
		public float w;
		public float h;
		public float r; //角度

		Pos(String pos){
			String[] s = pos.split(",");
			x = Float.parseFloat(s[0]);
			y = Float.parseFloat(s[1]);
			w = Float.parseFloat(s[2]);
			h = Float.parseFloat(s[3]);
			if(s.length > 4)
				r = Float.parseFloat(s[4]);
		}
	}

	public Pos getPos(){
		return new Pos(position);
	}
}