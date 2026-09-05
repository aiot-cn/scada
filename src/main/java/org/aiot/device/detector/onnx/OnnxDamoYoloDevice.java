package org.aiot.device.detector.onnx;

import ai.onnxruntime.OnnxValue;
import ai.onnxruntime.OrtSession;
import org.aiot.lang.annotation.AoReflect;
import org.aiot.model.enums.DeviceRoleEnum;
import org.aiot.model.lang.RecognitionRes;
import org.aiot.model.lang.Target;
import org.aiot.util.MathUtil;
import org.aiot.util.OpenCVUtil;
import org.nutz.lang.Strings;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

@AoReflect(value = "ONNX_DAMO_YOLO",deviceRole = DeviceRoleEnum.AI_MODEL)
public class OnnxDamoYoloDevice extends OnnxBase{
	private final Log log = Logs.get();

	/*
	 * DAMO-YOLO shape [1,8400,80] [1,8400,4]
	 *
	 */
	@Override
	public RecognitionRes recognizeMat(Mat image, String type) {

		long t1 = System.currentTimeMillis();

		RecognitionRes targets = new RecognitionRes(null,image.width(),image.height());
		String[] types = Strings.isBlank(type) ? null : type.split(",");

		long t2 = 0, t3 = 0;
		for(OnnxWrapper ow : onnxList){
			if(types != null && !ow.startModelName(types) && !ow.containsLabels(types))
				continue;

			long[] shape = ow.getInputShape();
			int width = (int) shape[2],height = (int) shape[3];
			if(width < 1 || height <1)
				continue;

			double scale = Math.min((double) width / image.cols(), (double) height / image.rows());
			int newWidth = (int) (image.cols() * scale);
			int newHeight = (int) (image.rows() * scale);

			// 计算填充位置
			int x0 = (width - newWidth) / 2;
			int y0 = (height - newHeight) / 2;

			Mat dst = new Mat(height, width, CvType.CV_8UC3);

			//等比例缩放图像
			Mat resized = new Mat();
			Imgproc.resize(image, resized, new Size(newWidth, newHeight), 0, 0, Imgproc.INTER_LINEAR);

			//将缩放后的图像放入画布中心
			Mat roi = dst.submat(new Rect(x0, y0, newWidth, newHeight));
			resized.copyTo(roi);
			//Imgcodecs.imwrite(file.getParent() + "/a3.jpg", dst);


			//归一化并转换为浮点型
			Mat floatMat = new Mat();
			dst.convertTo(floatMat, CvType.CV_32FC3, 1.0);

			float[] data=  OpenCVUtil.normalizeCHW(floatMat);
			OpenCVUtil.release(dst,resized,roi,floatMat);

			t2 = System.currentTimeMillis();
			try(OrtSession.Result result = ow.run(data,shape)){
				t3 = System.currentTimeMillis();
				List<float[]> list = new ArrayList<>();

				OnnxValue onnxValue0 = result.get(0);//output0
				float[][] scores = ((float[][][]) onnxValue0.getValue())[0];
				//DAMO
				if(ow.getOutputName().length > 1){
					OnnxValue onnxValue1 = result.get(1);
					float[][] bboxes = ((float[][][]) onnxValue1.getValue())[0];

					for(int i = 0;i<scores.length;i++){
						float[] ss = scores[i];
						float[] f = new float[6];
						for(int j=0;j < ss.length;j++){
							if(ss[j] > f[4]){
								f[4] = ss[j];
								f[5] = j;
							}
						}
						if(f[4] > 0.05){
							System.arraycopy(bboxes[i],0,f,0,4);
							list.add(f);
						}
					}
				}else {
					for (float[] b : scores) {
						int index = 4;
						for (int j = index; j < b.length; j++) {
							if (b[j] > b[4]) {
								b[4] = b[j];
								index = j;
							}
						}
						if (b[4] > 0.5) {
							if(b.length == 5){
								float[] c = new float[6];
								System.arraycopy(b,0,c,0,5);
								b = c;
							}
							b[5] = index - 4;
							list.add(b);
						}

					}
				}
				String[] labels = ow.getLabels();
				List<float[]> fs = MathUtil.nms(list,0.8f,0.5f);
				for (float[] detection : fs) {
					float x = detection[0]; // x
					float y = detection[1]; // y
					float w = detection[2] - x;  // 宽度
					float h = detection[3] - y;  // 高度
					float confidence = detection[4]; // 置信度
					int classId = (int) detection[5];
					String label = labels.length > classId ? labels[classId] : classId+"";
					/*if(!labelContains(label,types))
						continue;*/
					// 将相对坐标转换为绝对坐标

					Target t = new Target(label,confidence,
							Math.max(0, (x-x0)/(width-x0*2)),Math.max(0, (y-y0)/(height-y0*2)),
							w/(width-x0*2),h/(height-y0*2));
					//msg += String.format("%s,%.2f,%f,%f,%f,%f;",labels[classId], confidence, x/640,y/640,w/640,h/640);

					targets.addTarget(t);
				}


			} catch (Exception e) {
				e.printStackTrace();
			}

		}
		long t4 = System.currentTimeMillis();
		String time = String.format("预处理%dms 推理%dms 解析%dms 共计%dms",t2-t1,t3-t2,t4-t3,t4-t1);
		targets.setRemark(time);
		return targets;
	}


}


