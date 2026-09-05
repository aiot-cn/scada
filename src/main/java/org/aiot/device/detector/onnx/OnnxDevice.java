package org.aiot.device.detector.onnx;

import ai.onnxruntime.OnnxValue;
import ai.onnxruntime.OrtException;
import ai.onnxruntime.OrtSession;
import org.aiot.lang.annotation.AoReflect;
import org.aiot.model.enums.DeviceRoleEnum;
import org.aiot.model.enums.PathEnum;
import org.aiot.model.enums.aimodel.AlgorithmEnum;
import org.aiot.model.enums.aimodel.TaskEnum;
import org.aiot.model.lang.RecognitionRes;
import org.aiot.model.lang.Target;
import org.aiot.util.HalconUtil;
import org.aiot.util.MathUtil;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.nutz.lang.Strings;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.opencv.core.Point;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.*;


@AoReflect(value = "ONNX",deviceRole = DeviceRoleEnum.AI_MODEL)
public class OnnxDevice extends OnnxBase{
	private final Log log = Logs.get();

	/*
	 * YOLO5 shape [1,25200,9]
	 * 位置4+目标框可信度1+分类4个的可信度 = 9
	 * 由于YOLOv5是anchor-based模型，每个网格预测3个边界框，因此总的检测框数量为：80x80 + 40x40 + 20x20 = 8400 * 3 = 25200个单元格
	 *
	 * 语义分割 output0 [1, 25200, 39] output1 [1, 32, 160, 160]
	 * 位置4+目标框可信度1+分类2个的可信度+掩码32=39
	 *
	 * YOLO11 [1,84,8400]
	 * 输出形状(1, 84, 8400)中的“1”表示批次大小（batch size），
	 * x中心, y中心, width, height +80个分类的置信度 = 84
	 * 80x80 + 40x40 + 20x20 = 8400个网格‌
	 *
	 * @param type 用于区分调用,标签模糊匹配
	 *             标签筛选可以 Targets.filter
	 */

	public RecognitionRes recognizeMat(Mat image,String type) {
		if(image == null || image.empty() || image.width() <=0 || image.height() <= 0)
			throw new IllegalArgumentException("图像数据错误");


		RecognitionRes res = new RecognitionRes(null,image.width(),image.height());

		String[] types = Strings.isBlank(type) ? null : type.split(",");
		Map<String,float[]> dataMap = new HashMap<>();
		long T1 = 0;//预处理
		long T2 = 0;//识别
		long T3 = 0;//解析
		for(OnnxWrapper ow : onnxList){
			if(types != null && !ow.startModelName(types) && !ow.containsLabels(types))
				continue;

			long[] shape = ow.getInputShape();
			int width = (int) shape[2],height = (int) shape[3];
			if(width < 1 || height <1)
				continue;
			long t1 = System.currentTimeMillis();
			double scale = Math.min((double) width / image.cols(), (double) height / image.rows());
			int newWidth = (int) (image.cols() * scale);
			int newHeight = (int) (image.rows() * scale);

			// 计算填充位置
			int x0 = (width - newWidth) / 2;
			int y0 = (height - newHeight) / 2;

			float[] data;
			boolean isClassify = ow.isTask(TaskEnum.classify);
			if(isClassify){
				data = dataMap.computeIfAbsent("A"+width+height,v->squareCropAndResize(image,width));
			}else if(ow.getAlgorithm() == AlgorithmEnum.DAMO){
				data = dataMap.computeIfAbsent("B"+width+height,v->matUndistortedFloat(image,width,height,1,new int[]{0}));
			}else{
				data = dataMap.computeIfAbsent("C"+width+height,v->matUndistortedFloat(image,width,height,1.0/255.0,new int[]{114}));
			}

			RecognitionRes targets = new RecognitionRes(null,image.width(),image.height());
			long t2 = System.currentTimeMillis();
			T1 += (t2 - t1);
			try(OrtSession.Result result = ow.run(data,shape)){
				long t3 = System.currentTimeMillis();
				T2 += (t3 - t2);

				String[] labels = ow.getLabels();
				OnnxValue onnxValue = result.get(0);//output0
				//log.info(onnxValue.getInfo());
				//--------------- 分类 ---------------
				if(isClassify){
					float[] output = ((float[][]) onnxValue.getValue())[0];

					// 应用Softmax并获取Top-K类别
					int topK = 5;
					List<Prediction> predictions = getTopKPredictions(output, topK);
					for(int i=0;i<predictions.size();i++){
						Prediction p = predictions.get(i);
						if(p.probability > 0.01)
							targets.addTarget(labels[p.classIndex],p.probability,0,0,1f,1f);
					}
					T3 += (System.currentTimeMillis() - t3);
					continue;
				}

				float[][] output = ((float[][][]) onnxValue.getValue())[0];

				//*--------------- 旋转框 ---------------
				if(ow.isTask(TaskEnum.obb)){
					if(!ow.isNms()){
						output = MathUtil.transpose(output);
						for (float[] detection : output) {
							int classId = MathUtil.maxIndex(detection, 4, -1);
							detection[4] = detection[classId + 4];//可信度
							detection[5] = classId;
							detection[6] = detection[detection.length - 1];
						}
					}

					for(float[] detection : output) {
						if(detection[4] < 0.3)
							continue;
						float cx = detection[0];  //中心点
						float cy = detection[1];
						float w = detection[2];  // 宽度
						float h = detection[3];  // 高度
						float confidence = detection[4]; // 置信度
						int classId = (int) detection[5];
						float angel = (float) Math.toDegrees(detection[6]);

						if(angel > 60){
							angel = angel - 90;
							float tem = w;
							w = h;
							h = tem;
						}

						float x = cx - w / 2;
						float y = cy - h / 2;
						float left = (x-x0)/(width-x0*2);
						float top = (y-y0)/(height-y0*2);
						float width2 = w/(width-x0*2);
						float heigth2 = h/(height-y0*2);

						String label = labels[classId];
						Target t = new Target(label,confidence,left,top,width2,heigth2);
						t.setAngel(angel);
						targets.addTarget(t);
					}
					if(!ow.isNms())
						targets.nms(0.5f);
					T3 += (System.currentTimeMillis() - t3);
					continue;
				}

				//*--------------- 姿势估计 ---------------
				if(ow.isTask(TaskEnum.pose)){
					for(float[] detection : output) {
						if(detection[4] < 0.1)
							continue;
						Target t = targets.addTarget(ow.getLabel((int) detection[5]),detection,2,width,height,x0,y0);
						int i0 = (detection.length - 6)/3;
						for(int i = 0;i < i0;i++){
							int j = 6 + 3 * i;
							t.addPoint(new float[]{
									(detection[j]  -x0) / (width -x0*2),
									(detection[j+1]-y0) / (height-y0*2),
									detection[j+2]
							});
						}
					}
					T3 += (System.currentTimeMillis() - t3);
					continue;
				}
				//*--------------- DAMO-YOLO---------------
				if(ow.getAlgorithm() == AlgorithmEnum.DAMO){
					List<float[]> des = resDamoYolo(result);
					List<float[]> fs = MathUtil.nms(des,0.7f,0.5f);
					for (float[] detection : fs) {
						targets.addTarget(ow.getLabel((int) detection[5]),detection,0,width,height,x0,y0);
					}
					T3 += (System.currentTimeMillis() - t3);
					continue;
				}

				//*--------------- 目标检测 ---------------
				if(ow.isNms()){
					for (float[] detection : output){
						targets.addTarget(ow.getLabel((int) detection[5]),detection,2,width,height,x0,y0);
					}
					T3 += (System.currentTimeMillis() - t3);
					continue;
				}

				int start = 5;
				//YOLO8 YOLO11
				if(output.length<8400){
					output = transposeMatrix(output);
					for (int i = 0; i < output.length; i++) {
						float[] detection = output[i];
						float[] newDetection = new float[detection.length + 1];
						System.arraycopy(detection, 0, newDetection, 0, 4);
						newDetection[4] = detection[maxIndex(detection,4,labels.length)];
						System.arraycopy(detection, 4, newDetection, 5, detection.length - 4);
						output[i] = newDetection;
					}
				}
				List<float[]> des = new ArrayList<>(Arrays.asList(output));
				List<float[]> fs = MathUtil.nms(des,0.5f,0.3f);
				for (float[] detection : fs) {
					int index = maxIndex(detection,start,labels.length) - start;
					Target t = targets.addTarget(ow.getLabel(index),detection,1,width,height,x0,y0);
					//msg += String.format("%s,%.2f,%f,%f,%f,%f;",labels[classId], confidence, x/640,y/640,w/640,h/640);
					float w = detection[2];  // 宽度
					float h = detection[3];  // 高度
					float x = detection[0] - w / 2;
					float y = detection[1] - h / 2;
					//语义分割
					if(ow.getOutputName().length > 1){
						OnnxValue output1 = result.get(1);
						float[][][][] maskPrototypes = (float[][][][]) output1.getValue(); //原型掩码 [1, 32, 160, 160]
						MatOfPoint matPoint = generateMaskInfo(detection,labels.length,maskPrototypes);
						int[] points = new int[matPoint.rows() * 2];
						matPoint.get(0,0,points);

						for (int i = 0; i < points.length/2; i++) {
							t.addPoint(new float[]{
									(points[i*2]   + x + 1 -x0)/(width -x0 * 2),
									(points[i*2+1] + y + 1 -y0)/(height-y0 * 2)
							});
						}

						//拟合直线 优先使用halcon
						float[] linePoints = HalconUtil.pointsToLine(points,null);
						if(linePoints == null)
							linePoints = fitLine(matPoint,(int)w,(int)h);
						t.setLine(pointsToArray(linePoints,x,y,x0,y0,width,height));
						matPoint.release();
					}
				}
				T3 += (System.currentTimeMillis() - t3);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if(types == null || ow.startModelName(types)){
					res.getTargets().addAll(targets.getTargets());
				}else{
					res.getTargets().addAll(targets.findLabel(types).getTargets());
				}
				//sendSocket(String.format("onnx 模型[%s] 结果:%s",ow.getModelName(), targets),true);
			}
		}

		res.setRemark(String.format("预处理%dms 推理%dms 解析%dms",T1,T2,T3));
		return res;
	}

	public List<float[]> resDamoYolo(OrtSession.Result result) throws OrtException{
		List<float[]> list = new ArrayList<>();

		OnnxValue onnxValue0 = result.get(0);//output0
		float[][] scores = ((float[][][]) onnxValue0.getValue())[0];
		//DAMO
		if(result.size() > 1){
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
				if(f[4] > 0.1){
					System.arraycopy(bboxes[i],0,f,0,4);
					f[2] = f[2] - f[0];
					f[3] = f[3] - f[1];
					list.add(f);
				}
			}
		}else {
			for (float[] f : scores) {
				int index = 4;
				for (int j = index; j < f.length; j++) {
					if (f[j] > f[4]) {
						f[4] = f[j];
						index = j;
					}
				}
				if (f[4] > 0.5) {
					if(f.length == 5){
						float[] c = new float[6];
						System.arraycopy(f,0,c,0,5);
						f = c;
					}
					f[2] = f[2] - f[0];
					f[3] = f[3] - f[1];
					f[5] = index - 4;
					list.add(f);
				}

			}
		}
		return list;
	}

	public float[] pointsToArray(float[] points,float x,float y,float x0,float y0,float width,float height){
		float[] p = new float[points.length];
		for (int i = 0; i < points.length; i++) {
			if(i % 2 == 0)
				p[i] = (points[i] + x + 1 -x0)/(width -x0 * 2);
			else
				p[i] = (points[i] + y + 1 -y0)/(height-y0 * 2);
		}
		return p;
	}

	public boolean labelContains(String lable,String[] arr){
		if(arr == null || arr.length == 0)
			return true;
		if(arr[0].endsWith(".onnx"))
			return true;

		for(String L : arr)
			if (lable.contains(L))
				return true;
		return false;
	}

	//取中间一段，前面有位置，后面有掩码等情况
	public int maxIndex(float[] array,int start,int length){
		int maxIndex = start;         // 初始化最大值索引为0
		// 遍历数组（从索引1开始）
		for (int i = start; i < array.length && i < start + length; i++) {
			// 如果当前元素大于已知最大值，更新索引
			if (array[i] > array[maxIndex]) {
				maxIndex = i;
			}
		}
		return maxIndex;

	}

	/**缩放
	 * 线性插值	Imgproc.INTER_LINEAR	平衡速度和质量 yoloV5默认
	 * 最近邻	Imgproc.INTER_NEAREST	最快但质量差
	 * 区域插值	Imgproc.INTER_AREA		缩小图像时减少锯齿
	 * 立方插值	Imgproc.INTER_CUBIC		高质量但较慢
	 * Lanczos	Imgproc.INTER_LANCZOS4	最高质量但最慢
	 */
	public BufferedImage scaledUndistorted(BufferedImage image,int targetWidth,int targetHeight){
		// 计算缩放比例
		double scale = Math.min((double) targetWidth / image.getWidth(), (double) targetHeight / image.getHeight());
		int scaledWidth = (int) (image.getWidth() * scale);
		int scaledHeight = (int) (image.getHeight() * scale);

		// 计算填充位置
		int x0 = (targetWidth - scaledWidth) / 2;
		int y0 = (targetHeight - scaledHeight) / 2;

		BufferedImage resizedImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);
		Graphics2D g2d = resizedImage.createGraphics();

		// 填充灰色背景
		g2d.setColor(new Color(114, 114, 114));
		g2d.fillRect(0, 0, targetWidth, targetHeight);

		// 缩放并绘制 平滑缩放
		g2d.drawImage(image.getScaledInstance(scaledWidth, scaledHeight, Image.SCALE_SMOOTH), x0, y0, null);
		g2d.dispose();
		return resizedImage;
	}

	/**
	 * 预处理图片：归一化、转换为 ONNX 输入格式
	 *
	 * @param image 原始图片
	 * @return 预处理后的数据（形状shape [1, 3, 640, 640]）
	 */
	public static float[] preprocessImage(BufferedImage image) {
		//将图片数据转换为 float[]，并归一化到 [0, 1]
		int width = image.getWidth();
		int height = image.getHeight();
		float[] inputData = new float[3 * width * height]; // 3 通道，640x640

		int index = 0;
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				// 获取像素的 RGB 值
				int rgb = image.getRGB(x, y);
				int r = (rgb >> 16) & 0xFF; // 红色通道
				int g = (rgb >> 8) & 0xFF;  // 绿色通道
				int b = rgb & 0xFF;        // 蓝色通道

				// 归一化并存储到 inputData（按 CHW 顺序）
				inputData[index] 						= r / 255f; // 红色通道
				inputData[index + width * height] 		= g / 255f; // 绿色通道
				inputData[index + 2 * width * height] 	= b / 255f; // 蓝色通道
				index++;
			}
		}

		return inputData;
	}


	//语义分割
	public static MatOfPoint generateMaskInfo(float[] detection, int typeCount, float[][][][] proto){
		// 32 * 160 * 160 这个是mask原型 c h w
		float[][][] maskSrc = proto[0];
		// 转为二维矩阵也就是 32 * 25600,也就是 32 行 25600 列,相当于把 160*160展平
		Mat m1 = flattenMaskProto(maskSrc);
		float[][] flattenedData = floatArray2floatArray(maskSrc);

		// 每个目标框
		// 32 这个是mask 掩膜系数,也就是权重,转为矩阵
		float[] maskWeight = Arrays.copyOfRange(detection, 5+typeCount, 5+typeCount+32);
		// 创建1x32矩阵
		Mat m2 = new Mat(1, maskWeight.length, CvType.CV_32F);
		// 填充数据
		for (int i = 0; i < maskWeight.length; i++) {
			m2.put(0, i, maskWeight[i]);
		}
		// 矩阵乘法 1*32 乘 32*25600 得到 1*25600
		Mat m3 = new Mat();
		Core.gemm(m2, m1, 1, new Mat(), 0, m3);

		// 再将 1*25600 转回 160*160 也就是一个缩小的掩膜图
		Mat m4 = m3.reshape(1, 160); // 1 channel, 160 rows;

		// 对每个元素求sigmod限制到0~1,后续根据阈值进行二值化
		Mat m5 = applySigmoid(m4);

		// 将160*160上采样到图片原始尺寸
		Mat m6 = new Mat();
		Imgproc.resize(m5, m6, new Size(640, 640), 0, 0, Imgproc.INTER_LINEAR_EXACT); //0、3、6不行

		//高斯模糊
		//Imgproc.GaussianBlur(m6, m6, new Size(3, 3), 0);

		int x = (int) (detection[0] - detection[2]/2);
		int y = (int) (detection[1] - detection[3]/2);
		int width = (int) detection[2];
		int height = (int) detection[3];


		//使用 submat 裁剪
		Mat m7 = m6.submat(new Rect(x, y, width, height)); //sigmoidMask
		//saveSigmoidMask(m7);

		//转换为 8UC1 (0~255) 并二值化（阈值可调）
		Mat binaryMask = new Mat();
		double threshold = 0.5; // sigmoid 阈值 (默认 0.5)
		m7.convertTo(binaryMask, CvType.CV_8UC1, 255.0);
		Imgproc.threshold(binaryMask, binaryMask, threshold * 255, 255, Imgproc.THRESH_BINARY);


		//提取轮廓
		List<MatOfPoint> contours = new ArrayList<>();
		Mat hierarchy = new Mat();
		Imgproc.findContours(binaryMask, contours, hierarchy,
				Imgproc.RETR_EXTERNAL, // 只检测外部轮廓
				Imgproc.CHAIN_APPROX_NONE //存储轮廓上的所有点
		);

		//找到面积最大的轮廓
		double maxArea = 0;
		MatOfPoint largestContour = null;

		for (MatOfPoint contour : contours) {
			double area = Imgproc.contourArea(contour);
			if (area > maxArea) {
				maxArea = area;
				largestContour = contour;
			}
		}

		//List<Point> p2 = largestContour.toList();
		/*for(Point p : p2){
			p.x += x + 1;
			p.y += y + 1;
		}*/
		return largestContour;

	}


	//拟合直线
	private static float[] fitLine(MatOfPoint points, int width, int height) {
		// 1. 转换轮廓为MatOfPoint2f
		MatOfPoint2f contour2f = new MatOfPoint2f(points.toArray());

		// 2. 拟合直线
		Mat line = new Mat();
		Imgproc.fitLine(contour2f, line, Imgproc.DIST_L2, 0, 0.01, 0.01);

		// 3. 解析直线参数
		float[] lineParams = new float[4];
		line.get(0, 0, lineParams);
		float vx = lineParams[0]; // 方向向量x分量
		float vy = lineParams[1]; // 方向向量y分量
		float x0 = lineParams[2]; // 直线上一点的x坐标
		float y0 = lineParams[3]; // 直线上一点的y坐标

		// 4. 计算直线端点（假设图像尺寸为width x height）


		// 计算直线与图像边界的交点
		Point pt1 = new Point();
		Point pt2 = new Point();

		// 计算直线方程： (x - x0)/vx = (y - y0)/vy → y = (vy/vx)(x - x0) + y0
		// 寻找x=0和x=width时的y值
		float y1 = ((vy / vx) * (0 - x0) + y0);
		float y2 = ((vy / vx) * (width - x0) + y0);
		pt1.x = 0;
		pt1.y = y1;
		pt2.x = width;
		pt2.y = y2;

		// 如果直线接近垂直，使用y=0和y=height计算x值
		if (Math.abs(vx) < 0.1) { // 防止除以零
			float x1 = ((vx / vy) * (0 - y0) + x0);
			float x2 = ((vx / vy) * (height - y0) + x0);
			pt1.x = x1;
			pt1.y = 0;
			pt2.x = x2;
			pt2.y = height;
		}

		// 确保点在图像范围内
		pt1 = clipPoint(pt1, width, height);
		pt2 = clipPoint(pt2, width, height);

		float[] f = new float[]{(float) pt1.x, (float) pt1.y, (float) pt2.x, (float) pt2.y};
		return f;
	}

	public int[][] matPointToArray(MatOfPoint matOfPoint) {
		int size = matOfPoint.rows();
		int[][] result = new int[size][2];

		// 一次性获取所有数据
		int[] data = new int[size * 2];
		matOfPoint.get(0, 0, data);

		// 填充二维数组
		for (int i = 0; i < size; i++) {
			result[i][0] = data[i * 2];     // x 坐标
			result[i][1] = data[i * 2 + 1]; // y 坐标
		}

		return result;
	}

	private static Point clipPoint(Point pt, int width, int height) {
		pt.x = Math.max(0, Math.min(width, pt.x));
		pt.y = Math.max(0, Math.min(height, pt.y));
		return pt;
	}

	private static Mat flattenMaskProto(float[][][] proto) {
		// 将32*160*160转换为32*25600矩阵
		int channels = proto.length; // 32
		int height = proto[0].length; // 160
		int width = proto[0][0].length; // 160

		// 创建32x25600矩阵
		Mat m1 = new Mat(channels, height * width, CvType.CV_32F);

		// 填充数据
		for (int c = 0; c < channels; c++) {
			for (int h = 0; h < height; h++) {
				for (int w = 0; w < width; w++) {
					float value = proto[c][h][w];
					m1.put(c, h * width + w, value);
				}
			}
		}

		return m1;
	}

	private static Mat applySigmoid(Mat m4) {
		Mat sigmoid = new Mat();
		Core.multiply(m4, new Scalar(-1), sigmoid);  // -x
		Core.exp(sigmoid, sigmoid);                  // exp(-x)
		Core.add(sigmoid, new Scalar(1), sigmoid);  // 1 + exp(-x)
		Mat ones = Mat.ones(sigmoid.size(), sigmoid.type());
		Core.divide(ones,sigmoid, sigmoid); // 1 / (1 + exp(-x))
		return sigmoid;
	}

	public static void saveSigmoidMask(Mat sigmoidMask) {
		// 1. 将0-1的浮点矩阵转换为0-255的8UC1图像
		Mat uint8Mask = new Mat();
		sigmoidMask.convertTo(uint8Mask, CvType.CV_8UC1, 255.0);

		// 2. 保存图像（PNG格式保留精度）
		Imgcodecs.imwrite(PathEnum.AppData.getFile(System.currentTimeMillis()+".png").getAbsolutePath(), uint8Mask);

		// 可选：可视化阈值处理
		//Mat binaryMask = new Mat();
		//Imgproc.threshold(uint8Mask, binaryMask, 128, 255, Imgproc.THRESH_BINARY);
		//Imgcodecs.imwrite(outputPath.replace(".png", "_binary.png"), binaryMask);
	}

	private static MatOfPoint computeConvexHull(MatOfPoint points) {
		MatOfInt hull = new MatOfInt();
		Imgproc.convexHull(points, hull);

		Point[] pointsArray = points.toArray();
		Point[] hullPoints = new Point[hull.rows()];

		for (int i = 0; i < hull.rows(); i++) {
			int index = (int) hull.get(i, 0)[0];
			hullPoints[i] = pointsArray[index];
		}

		return new MatOfPoint(hullPoints);
	}


	public static BufferedImage showMatrixWithBox(RealMatrix matrix) {
		// 转换 RealMatrix to BufferedImage
		int numRows = matrix.getRowDimension();
		int numCols = matrix.getColumnDimension();
		BufferedImage image = new BufferedImage(numCols, numRows, BufferedImage.TYPE_INT_RGB);
		for (int i = 0; i < numRows; i++) {
			for (int j = 0; j < numCols; j++) {
				double value = matrix.getEntry(i, j);
				int grayValue = (int) Math.round(value * 255.0);
				grayValue = Math.min(grayValue, 255);
				grayValue = Math.max(grayValue, 0);
				int pixelValue = (grayValue << 16) | (grayValue << 8) | grayValue;
				image.setRGB(j, i, pixelValue);
			}
		}
		return image;
		//Images.write(image, PathEnum.image.getFile(System.currentTimeMillis()+".jpg"));
	}

	public static RealMatrix applySigmoid(RealMatrix matrix) {
		int rows = matrix.getRowDimension();
		int cols = matrix.getColumnDimension();
		RealMatrix result = MatrixUtils.createRealMatrix(rows, cols);
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				double value = matrix.getEntry(i, j);
				double sigmoid = 1.0 / (1.0 + Math.exp(-value));
				result.setEntry(i, j, sigmoid);
			}
		}
		return result;
	}



	public static float[][] floatArray2floatArray(float[][][] data){
		float[][] flattenedData = new float[data.length][data[0].length * data[0][0].length];
		for (int i = 0; i < data.length; i++) {
			float[][] slice = data[i];
			for (int j = 0; j < slice.length; j++) {
				System.arraycopy(slice[j], 0, flattenedData[i], j * slice[j].length, slice[j].length);
			}
		}
		return flattenedData;
	}
	public static double[] floatArray2doubleArray(float[] data){
		double[] maskDouble = new double[data.length];
		for (int j = 0; j < data.length; j++) {
			maskDouble[j] = data[j];
		}
		return maskDouble;
	}
	public static double[][] floatArray2doubleArray(float[][] data){
		double[][] maskDouble = new double[data.length][data[0].length];
		for (int i = 0; i < data.length; i++) {
			for(int j=0; j<data[0].length;j++){
				maskDouble[i][j] = data[i][j];
			}
		}
		return maskDouble;
	}
	// 再将 1*25600 转回 160*160
	public static RealMatrix transfer_25600_To_160_160(RealMatrix data){
		RealMatrix res = new Array2DRowRealMatrix(160, 160);
		for (int i = 0; i < 160; i++) {
			for (int j = 0; j < 160; j++) {
				int index = i * 160 + j;
				double value = data.getEntry(0, index);
				res.setEntry(i, j, value);
			}
		}
		return res;
	}

	public static RealMatrix resizeRealMatrix(RealMatrix matrix, int newRows, int newCols) {
		int rows = matrix.getRowDimension();
		int cols = matrix.getColumnDimension();
		RealMatrix resizedMatrix = MatrixUtils.createRealMatrix(newRows, newCols);
		for (int i = 0; i < newRows; i++) {
			for (int j = 0; j < newCols; j++) {
				int origI = (int) Math.floor(i * rows / newRows);
				int origJ = (int) Math.floor(j * cols / newCols);
				double d = matrix.getEntry(origI, origJ);
//                if(d>=maskThreshold){
//                    d = 1;
//                }else{
//                    d = 0;
//                }
				resizedMatrix.setEntry(i, j, d);
			}
		}
		return resizedMatrix;
	}

	/** ------------------------- 分类 ------------------------------------ */
	/**
	 * 预处理：
	 * 1. 以最小边为基准裁剪成正方形
	 * 2. 缩放到224x224
	 * 3. 转换为0-1范围并转为CHW格式
	 * 4. 应用标准化
	 */
	private float[] squareCropAndResize(Mat image,int sz) {
		// Step 1: 以最小边为基准裁剪正方形
		int minSide = Math.min(image.rows(), image.cols());
		int startX = (image.cols() - minSide) / 2;
		int startY = (image.rows() - minSide) / 2;
		Mat squareCrop = new Mat(image, new Rect(startX, startY, minSide, minSide));

		// Step 2: 缩放到224x224
		Mat resized = new Mat();
		Imgproc.resize(squareCrop, resized, new Size(sz, sz));

		// Step 3: 转换为0-1范围的浮点数
		Mat floatMat = new Mat();
		resized.convertTo(floatMat, CvType.CV_32FC3, 1.0 / 255.0);

		// Step 4: 应用标准化并转换为CHW格式
		float[] d =  normalizeCHW(floatMat);
		squareCrop.release();
		resized.release();
		floatMat.release();

		return d;
	}

	/**
	 * 将OpenCV Mat转换为CHW格式的float数组，并应用标准化
	 */
	private float[] normalizeCHW(Mat mat) {
		// PyTorch风格的标准化参数
		float[] MEAN = {0.485f, 0.456f, 0.406f};//RGB 减去均值
		float[] STD = {0.229f, 0.224f, 0.225f};//RGB 除以标准差
		int height = mat.rows();
		int width = mat.cols();
		int channels = mat.channels();
		float[] data = new float[channels * height * width];

		for (int h = 0; h < height; h++) {
			for (int w = 0; w < width; w++) {
				float[] pixel = new float[channels];
				mat.get(h, w, pixel);

				// OpenCV是BGR顺序，转换为RGB
				float b = pixel[0];
				float g = pixel[1];
				float r = pixel[2];

				// 应用标准化：(pixel - mean) / std
				float rNormalized = (r - MEAN[0]) / STD[0];
				float gNormalized = (g - MEAN[1]) / STD[1];
				float bNormalized = (b - MEAN[2]) / STD[2];

				// CHW布局: [通道][高度][宽度]
				data[0 * height * width + h * width + w] = rNormalized; // 红色通道
				data[1 * height * width + h * width + w] = gNormalized; // 绿色通道
				data[2 * height * width + h * width + w] = bNormalized; // 蓝色通道
			}
		}
		return data;
	}



}


