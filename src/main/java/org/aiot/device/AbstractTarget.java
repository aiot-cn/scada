package org.aiot.device;

import org.aiot.device.detector.onnx.OnnxWrapper;
import org.aiot.lang.annotation.AoReflect;
import org.aiot.model.lang.RecognitionRes;
import org.aiot.util.ImgUtil;
import org.nutz.lang.Strings;
import org.nutz.log.Logs;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;

public abstract class AbstractTarget extends BaseDevice {

	// 设置模型文件
	public abstract List<OnnxWrapper> setModelFile(File modelFile);

	/**
	 * 设置标签
	 * 1 逗号分隔
	 * 2 map简写 如COCO80、COCO1000
	 * 3 文件路径 /yolov5.txt
	 */

	public void setLabels(String labels){

	}


	/**
	 * @param obj
	 * @param type 用于区分调用，标签筛选可以 RecognitionRes.filter
	 */
	@AoReflect("识别")
	public abstract RecognitionRes recognize(Object obj, String type);

	@AoReflect("识别目标")
	public RecognitionRes recognizeTargets(RecognitionRes targets,String type){
		RecognitionRes t = new RecognitionRes(targets.getImg(),targets.getWidth(),targets.getHeight());
		BufferedImage bi = t.getBufferedImage();
		targets.getTargets().forEach(v->{
			try {
				BufferedImage bi2 = ImgUtil.crop(bi, v);
				RecognitionRes t2 = recognize(bi2,type);

				t2.getTargets().forEach(v2->{
					float confidence = Math.min(v.getConfidence(),v2.getConfidence());
					String label = Strings.sBlank(v2.getLabel(),v.getLabel());
					float[][] points = v2.getPoints();
					if(points == null){
						float left = v.getLeft() + v2.getLeft() * v.getWidth();
						float top = v.getTop() + v2.getTop() * v.getHeight();
						float width = v.getWidth() * v2.getWidth();
						float height = v.getHeight() * v2.getHeight();
						t.addTarget(label, confidence,left,top,width,height);
					}else{
						for(int i=0;i<points.length;i++){
							points[i][0] = v.getLeft() + v.getWidth() * points[i][0];
							points[i][1] = v.getTop() + v.getHeight() * points[i][1];
						}
						t.addTarget(label, confidence,points);
					}

				});
			}catch (Exception e){
				Logs.get().error(e.getMessage());
			}
		});
		return t;
	}


}
