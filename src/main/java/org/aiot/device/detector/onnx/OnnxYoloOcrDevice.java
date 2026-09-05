package org.aiot.device.detector.onnx;

import ai.onnxruntime.OnnxValue;
import ai.onnxruntime.OrtSession;
import org.aiot.lang.annotation.AoReflect;
import org.aiot.model.enums.DeviceRoleEnum;
import org.aiot.model.lang.RecognitionRes;
import org.aiot.model.lang.Target;
import org.aiot.util.MathUtil;
import org.aiot.util.OpenCVUtil;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.opencv.core.CvType;
import org.opencv.core.Mat;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;


@AoReflect(value = "ONNX_YOLO_OCR",deviceRole = DeviceRoleEnum.AI_MODEL)
public class OnnxYoloOcrDevice extends OnnxBase{
	private final Log log = Logs.get();

	/*
	 * YOLO5 shape [1,25200,9]
	 * 位置4+可信度1+分类4个的可信度 = 9
	 * 由于YOLOv5是anchor-based模型，每个网格预测3个边界框，因此总的检测框数量为：80x80 + 1600 + 400 = 8400 * 3 = 25200个单元格
	 *
	 */
	@Override
	public RecognitionRes recognizeMat(Mat image, String type) {

		long t1 = System.currentTimeMillis();

		long t2 = System.currentTimeMillis();
		RecognitionRes targets = new RecognitionRes(null,image.width(),image.height());


		long t3 = 0, t4 = 0;
		for(OnnxWrapper ow : onnxList){

			Mat mat2 = OpenCVUtil.resizeToTargetHeight(image,64);
			Mat mat3 = OpenCVUtil.fillToSquare(mat2,640);

			//归一化并转换为浮点型
			Mat floatMat = new Mat();
			mat3.convertTo(floatMat, CvType.CV_32FC3, 1.0 / 255.0);
			float[] data =  OpenCVUtil.normalizeCHW(floatMat);
			OpenCVUtil.release(mat2,mat3,floatMat);

			t3 = System.currentTimeMillis();
			long[] shape = ow.getInputShape();
			try(OrtSession.Result result = ow.run(data,shape)){
				t4 = System.currentTimeMillis();

				String[] labels = ow.getLabels();
				OnnxValue onnxValue = result.get(0);//output0
				//log.info(onnxValue.getInfo());

				//*--------------- 目标检测 ---------------
				float[][] output = ((float[][][]) onnxValue.getValue())[0];
				int start = 5;
				List<float[]> des = new ArrayList<>(Arrays.asList(output));
				List<float[]> fs = MathUtil.nms(des,0.3f,0.3f);
				float height = 64f;
				float width = image.width() * (height / image.height());

				List<Target> targetList = new ArrayList<>();
				for (float[] detection : fs) {
					float xCenter = detection[0]; // 中心点 x
					float yCenter = detection[1]; // 中心点 y
					float w = detection[2];  // 宽度
					float h = detection[3];  // 高度
					float confidence = detection[4]; // 置信度

					int maxIndex = maxIndex(detection,start,labels.length);
					int classId = maxIndex - start;
					String label = labels[classId];

					// 将相对坐标转换为绝对坐标
					float x = Math.max(0,xCenter - w / 2);
					float y = Math.max(0,yCenter - h / 2);
					float left = x/width;
					float top = y/height;

					Target t = new Target(label,confidence,left,top,w/width,h/height);
					//msg += String.format("%s,%.2f,%f,%f,%f,%f;",labels[classId], confidence, x/640,y/640,w/640,h/640);
					targetList.add(t);
				}
				targetList.sort(Comparator.comparing(Target::getLeft));

				List<Float> widthArr = new ArrayList<>();
				List<Target> tList2 = new ArrayList<>();

				float maxDecimal = (float) targetList.stream().filter(v->".".equals(v.getLabel()))
						.mapToDouble(Target::getConfidence)
						.max().orElse(0.5);

				for(int i=0;i<targetList.size();i++){
					Target v = targetList.get(i);
					if(v.getConfidence() < 0.5)
						continue;

					if(".".equals(v.getLabel())){
						if(i == 0 || i == targetList.size() - 1 || v.getTop() < 0.5 || v.getConfidence() < maxDecimal)
							continue;
					}else if(v.getWidth() > v.getHeight()){
						continue;
					}

					if(!".".equals(v.getLabel()) && !"1".equals(v.getLabel()))
						widthArr.add(v.getWidth());

					tList2.add(v);
				}

				/*if(widthArr.size()>2){
					float[] widthArr2 = Castors.me().castTo(widthArr,float[].class);
					// 1. 计算中位数
					float median = MathUtil.getMedian(widthArr2);
					// 2. 计算绝对偏差
					float[] absoluteDeviations = new float[widthArr2.length];
					for (int i = 0; i < widthArr2.length; i++) {
						absoluteDeviations[i] = Math.abs(widthArr2[i] - median);
					}
					// 3. 计算绝对偏差的中位数(MAD)
					float mad = MathUtil.getMedian(absoluteDeviations);

					tList2 = targetList.stream().filter(v->{
						if(".".equals(v.getLabel()) || "1".equals(v.getLabel()))
							return true;
						float zScore = Math.abs(v.getWidth() - median) / (mad == 0 ? 0.0001f : mad);
						return zScore < 20;
					}).collect(Collectors.toList());
				}*/


				float minC = 1, minX = 1 , minY = 1 ,maxX = 0,maxY = 0;
				String label = "";
				for(Target t : tList2){
					label += t.getLabel();
					minC = Math.min(minC,t.getConfidence());
					minX = Math.min(minX,t.getLeft());
					minY = Math.min(minY,t.getTop());
					maxX = Math.max(maxX,t.getLeft() + t.getWidth());
					maxY = Math.max(maxY,t.getTop() + t.getHeight());
				}
				targets.addTarget(label,minC,minX,minY,maxX-minX,maxY-minY);
				if(isDebug)
					targets.getTargets().addAll(targetList);


			} catch (Exception e) {
				e.printStackTrace();
			}

		}
		long t5 = System.currentTimeMillis();
		String time = String.format("加载%dms 预处理%dms 推理%dms 解析%dms 共计%dms",t2-t1,t3-t2,t4-t3,t5-t4,t5-t1);
		targets.setRemark(time);
		return targets;
	}


	public int maxIndex(float[] array,int start,int length){
		int maxIndex = start;         // 初始化最大值索引为0
		// 遍历数组（从索引1开始）
		for (int i = start; i < start + length; i++) {
			// 如果当前元素大于已知最大值，更新索引
			if (array[i] > array[maxIndex]) {
				maxIndex = i;
			}
		}
		return maxIndex;

	}

}


