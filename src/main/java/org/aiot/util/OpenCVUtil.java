package org.aiot.util;

import nu.pattern.OpenCV;
import org.aiot.infc.ImgAbstract;
import org.aiot.infc.ImgInfc;
import org.aiot.model.lang.RecognitionRes;
import org.aiot.model.lang.Target;
import org.nutz.castor.Castors;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class OpenCVUtil {
	static {
		OpenCV.loadLocally();
		// 手动加载
		//System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
	}
	public static void load(){
	}

	public static Mat read(Object img){
		return read(img,Imgcodecs.IMREAD_COLOR);
	}

	public static Mat read(Object img,int flags){
		if(img instanceof ImgAbstract){
			ImgAbstract ab = (ImgAbstract) img;
			img = ab.getImg();
		}
		if(img instanceof ImgInfc)
			img = ((ImgInfc)img).getImgBytes();

		if(img instanceof Mat)
			return ((Mat) img).clone();

		if(img instanceof File)
			return readImg((File)img,flags);

		if(img instanceof String){
			File file = Castors.me().castTo(img,File.class);
			return readImg(file,flags);
		}
		if(img instanceof byte[])
			return readImg((byte[])img,flags);
		if(img instanceof BufferedImage){
			BufferedImage image = (BufferedImage) img;
			//子图像
			if(image.getRaster().getParent() != null)
				image = ImgUtil.copy(image);
			// 处理图像类型：转换为3字节BGR格式（移除Alpha通道）
			if (image.getType() != BufferedImage.TYPE_3BYTE_BGR) {
				BufferedImage newImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
				newImage.getGraphics().drawImage(image, 0, 0, null);
				image = newImage;
			}

			// 获取图像的字节数组
			byte[] pixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
			Mat mat = new Mat(image.getHeight(), image.getWidth(), CvType.CV_8UC3);
			mat.put(0, 0, pixels);
			return mat;
		}
		return null;
	}

	public static Mat readImg(byte[] bytes,int flags){
		MatOfByte matOfByte = new MatOfByte(bytes);
		Mat mat = Imgcodecs.imdecode(matOfByte,flags);
		matOfByte.release();
		return mat;
	}

	public static Mat readImg(File file){
		return readImg(file,Imgcodecs.IMREAD_COLOR);
	}

	public static Mat readImg(File file,int flags){
		//Imgcodecs.imread(file.getAbsolutePath())
		Mat mat = null;
		try {
			byte[] bytes = Files.readAllBytes(file.toPath());
			mat = readImg(bytes,flags);
		} catch (IOException e){
			e.printStackTrace();
		}
		return mat;
	}

	public static List<byte[]> readVideo(File file){
		VideoCapture cap = new VideoCapture(file.getAbsolutePath());

		// 检查视频是否成功打开
		if (!cap.isOpened()) {
			System.out.println("无法打开视频文件");
			return null;
		}

		Mat frame = new Mat();
		List<byte[]> list = new ArrayList<>();

		// 逐帧读取视频
		while (cap.read(frame)) {
			if (!frame.empty()) {
				list.add(toBytes(frame));
			}
		}
		// 释放资源
		cap.release();
		frame.release();

		return  list;
	}

	public static boolean writeImg(Mat mat,File file){
		//Imgcodecs.imwrite(file.getAbsolutePath(), mat);
		try {
			String fileName = file.getName();
			byte[] bytes = toBytes(mat,fileName.substring(fileName.lastIndexOf(".")));
			Files.write(file.toPath(), bytes);
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public static byte[] toBytes(Mat mat){
		return toBytes(mat,".jpg");
	}

	public static byte[] toBytes(Mat mat,String ext){
		MatOfByte matOfByte = new MatOfByte();
		Imgcodecs.imencode(ext, mat, matOfByte); // 指定格式
		byte[] bytes = matOfByte.toArray();
		matOfByte.release(); // 或 close();
		return bytes;
	}

	public static void release(Mat... mats){
		for(Mat m : mats){
			if(m != null)
				m.release();
		}
	}

	public static float[] normalizeCHW(Mat mat) {
		// PyTorch风格的标准化参数
		int height = mat.rows();
		int width = mat.cols();
		int channels = mat.channels();
		int totalPixels = height * width;
		float[] data = new float[channels * height * width];

		float[] matData = new float[totalPixels * channels];
		mat.get(0, 0, matData);
		for (int i = 0; i < totalPixels; i++) {
			int baseIndex = i * channels;
			// 获取BGR值
			float b = matData[baseIndex];
			float g = matData[baseIndex + 1];
			float r = matData[baseIndex + 2];
			// 计算在CHW布局中的位置
			int h = i / width;
			int w = i % width;

			data[0 * totalPixels + h * width + w] = r; // 红色通道
			data[1 * totalPixels + h * width + w] = g; // 绿色通道
			data[2 * totalPixels + h * width + w] = b; // 蓝色通道
		}
		return data;
	}

	/**
	 * 将图像等比缩放到指定高度
	 *
	 * @param srcImage     原始图像
	 * @param targetHeight 目标高度（像素）
	 * @return 缩放后的图像
	 */
	public static Mat resizeToTargetHeight(Mat srcImage,int targetHeight) {
		// 获取原始尺寸
		int originalHeight = srcImage.rows();
		int originalWidth = srcImage.cols();

		// 计算缩放比例
		double scaleFactor = (double) targetHeight / originalHeight;

		// 计算目标宽度（保持宽高比）
		int targetWidth = (int) Math.round(originalWidth * scaleFactor);

		// 创建目标Mat
		Mat dstImage = new Mat();

		// 选择插值方法：
		// INTER_AREA - 适合缩小
		// INTER_LINEAR - 适合放大
		int interpolation = scaleFactor < 1 ? Imgproc.INTER_AREA : Imgproc.INTER_LINEAR;

		// 执行缩放
		Imgproc.resize(
				srcImage,
				dstImage,
				new Size(targetWidth, targetHeight),
				0, 0,
				interpolation
		);

		return dstImage;
	}

	/**
	 * 不变形填充到正方形
	 * @param mat
	 * @param side 边长
	 */
	public static Mat fillToSquare(Mat mat,int side){
		Mat finalImage = new Mat(side, side, mat.type());
		finalImage.setTo(new Scalar(114, 114, 114));
		mat.copyTo(finalImage.submat(0, mat.rows(), 0,mat.cols()));
		return finalImage;
	}

	//绘制矩形
	public static void rectangle(Mat mat,double x1,double y1,double x2,double y2,int[] RGB,int thickness){
		Point topLeft = new Point(x1, y1);       // 左上角坐标 (x, y)
		Point bottomRight = new Point(x2, y2);   // 右下角坐标 (x, y)
		Scalar  color = new Scalar(0, 0, 255);  // BGR颜色
		if(RGB != null)
			color = new Scalar(RGB[2], RGB[1], RGB[0]);

		// 绘制矩形
		Imgproc.rectangle(
				mat,             	// 目标图像
				topLeft,           // 矩形左上角点
				bottomRight,        // 矩形右下角点
				color,              // 边框颜色
				thickness           // 线宽
		);
	}

	/**
	 * 在 Mat 上绘制 RecognitionRes 中所有 Target 的检测框和标签
	 */
	public static void drawRecognitionRes(Mat mat, RecognitionRes res) {
		if (mat == null || mat.empty() || res == null) {
			return;
		}
		List<Target> targets = res.getTargets();
		if (targets == null || targets.isEmpty()) {
			return;
		}

		int imgWidth = mat.cols();
		int imgHeight = mat.rows();

		for (Target t : targets) {
			Scalar color = labelColor(t.getLabel());
			int thickness = 2;

			float[][] points = t.getPoints();
			if (points != null && points.length > 0) {
				Point[] pts = new Point[points.length];
				for (int i = 0; i < points.length; i++) {
					pts[i] = new Point(points[i][0] * imgWidth, points[i][1] * imgHeight);
				}
				MatOfPoint mop = new MatOfPoint(pts);
				List<MatOfPoint> list = new ArrayList<>();
				list.add(mop);
				Imgproc.polylines(mat, list, true, color, thickness);
				release(mop);
			} else if (t.getWidth() > 0 || t.getHeight() > 0) {
				int x = (int) (t.getLeft() * imgWidth);
				int y = (int) (t.getTop() * imgHeight);
				int w = (int) (t.getWidth() * imgWidth);
				int h = (int) (t.getHeight() * imgHeight);
				Imgproc.rectangle(mat, new Point(x, y), new Point(x + w, y + h), color, thickness);
			}

			StringBuilder sb = new StringBuilder(t.getLabel());
			if (t.getConfidence() > 0) {
				sb.append(String.format(" %.2f", t.getConfidence()));
			}
			int textX = (int) (t.getLeft() * imgWidth);
			int textY = Math.max((int) (t.getTop() * imgHeight) - 5, 15);
			Imgproc.putText(mat, sb.toString(), new Point(textX, textY),
					Imgproc.FONT_HERSHEY_SIMPLEX, 0.5, color, 1);
		}
	}

	private static Scalar labelColor(String label) {
		if (label == null) {
			return new Scalar(0, 255, 0);
		}
		int hash = label.hashCode();
		return new Scalar(Math.max(hash & 0xFF, 60), Math.max((hash >> 8) & 0xFF, 60), Math.max((hash >> 16) & 0xFF, 60));
	}

	//旋转
	public static Mat rotate(Mat src,double angle,boolean bounding) {
		// 计算旋转中心
		Point center = new Point(src.cols() / 2.0, src.rows() / 2.0);

		// 获取旋转矩阵
		Mat rotationMatrix = Imgproc.getRotationMatrix2D(center, angle, 1.0);

		Size toSize = src.size();
		if(bounding){
			// 计算旋转后的边界
			Rect bbox = new RotatedRect(center, src.size(), angle).boundingRect();

			// 调整旋转矩阵的平移部分
			rotationMatrix.put(0, 2, rotationMatrix.get(0, 2)[0] + bbox.width/2 - center.x);
			rotationMatrix.put(1, 2, rotationMatrix.get(1, 2)[0] + bbox.height/2 - center.y);
			toSize = bbox.size();
		}

		// 应用旋转
		Mat rotated = new Mat();
		Imgproc.warpAffine(
				src,
				rotated,
				rotationMatrix,
				toSize,
				Imgproc.INTER_LINEAR
				//Core.BORDER_CONSTANT,
				//new Scalar(0, 0, 0)
		);
		rotationMatrix.release();

		return rotated;
	}

	public static Mat crop(Object image, Target target) {
		Mat src = read(image);
		try {
			if(target.getWidth() > 1){
				target.normalize(src.width(), src.height());
			}
			if(target.isOutside()){
				return null;
			}
			if(target.getWidth() == 0 && target.getPoints().length > 0){
				return perspectiveTransform(image,target.getPoints());
			}

			if(target.getAngel() == 0){
				Rect rect = targetToRect(src,target);
				return new Mat(src, rect);
			}

			RotatedRect rr = targetToRotatedRect(src,target);
			return rotateCrop(src,rr);

		}catch (Exception e){
			e.printStackTrace();
		}finally {
			release(src);
		}
		return null;
	}

	public static Mat rotateCrop(Mat image,RotatedRect rect) {
		// 获取旋转矩形的四个顶点
		Point[] vertices = new Point[4];
		rect.points(vertices);

		// 确定目标矩形的宽度和高度
		int width = (int)rect.size.width;
		int height = (int)rect.size.height;

		// 定义目标点（水平矩形）
		MatOfPoint2f dst = new MatOfPoint2f(
				new Point(0, height - 1),
				new Point(0, 0),
				new Point(width - 1, 0),
				new Point(width - 1, height - 1)
		);

		// 创建源点矩阵
		MatOfPoint2f src = new MatOfPoint2f(vertices);

		// 计算透视变换矩阵
		Mat transform = Imgproc.getPerspectiveTransform(src, dst);

		// 应用透视变换
		Mat cropped = new Mat();
		Imgproc.warpPerspective(
				image,
				cropped,
				transform,
				new Size(width, height)
		);
		release(dst,src,transform);
		return cropped;
	}

	public static Mat cropBlack(Mat src) {
		int y1 = 0,y2 = 0,x1 = 0,x2 = 0;
		int rowLast = src.rows() - 1;
		int colLast = src.cols() - 1;
		for (int i = 0; i < src.rows(); i++) {
			double[] pixel = src.get(i, 0);
			// 允许5的容差，处理可能的压缩伪影
			if (!isBlackPixel(pixel))
				break;
			y1 = i;
		}
		for (int i = 0; i < src.rows(); i++) {
			double[] pixel = src.get(i,colLast);
			if (!isBlackPixel(pixel))
				break;
			y2 = i;
		}
		for (int i = 0; i < src.cols(); i++) {
			double[] pixel = src.get(0,i);
			if (!isBlackPixel(pixel))
				break;
			x1 = i;
		}
		for (int i = 0; i < src.cols(); i++) {
			double[] pixel = src.get(rowLast,i);
			if (!isBlackPixel(pixel))
				break;
			x2 = i;
		}
		//上下边裁剪
		int top = Math.min(y1,y2);
		//左右边裁剪
		int left = Math.min(x1,x2);
		Rect rect = new Rect(left, top, src.width() - left * 2, src.height() - top * 2);
		return new Mat(src,rect);
	}

	/**
	 * 判断像素是否为黑色（RGB接近0,0,0）
	 */
	private static boolean isBlackPixel(double[] pixel) {
		// 允许5的容差，处理可能的压缩伪影
		return pixel[0] <= 5 && pixel[1] <= 5 && pixel[2] <= 5;
	}

	/**
	 * 对图像进行透视变换和裁剪
	 * @param image 原始图像
	 * @param pointsf 四个顶点坐标，顺序为：左上、右上、右下、左下
	 * @return 变换后的图像
	 */
	public static Mat perspectiveTransform(Object image,float[][] pointsf) {
		final int POINT_COUNT = 4;
		// 参数验证
		if (pointsf == null || pointsf.length != POINT_COUNT) {
			throw new IllegalArgumentException("pointsf must be a 4-element array");
		}
		Mat srcImage = null,transformMatrix = null;
		Mat dstImage;

		try {
			srcImage = read(image);
			Point[] points = new Point[POINT_COUNT];
			for(int i = 0; i < POINT_COUNT; i++){
				if (pointsf[i] == null || pointsf[i].length < 2) {
					throw new IllegalArgumentException("Each point must be a 2-element array");
				}
				points[i] = new Point(pointsf[i][0] * srcImage.width(), pointsf[i][1] * srcImage.height());
			}

			// 计算宽度
			double widthA = Math.sqrt(Math.pow(points[2].x - points[3].x, 2) +
					Math.pow(points[2].y - points[3].y, 2));
			double widthB = Math.sqrt(Math.pow(points[1].x - points[0].x, 2) +
					Math.pow(points[1].y - points[0].y, 2));
			double maxWidth = Math.max(widthA, widthB);

			// 计算高度
			double heightA = Math.sqrt(Math.pow(points[1].x - points[2].x, 2) +
					Math.pow(points[1].y - points[2].y, 2));
			double heightB = Math.sqrt(Math.pow(points[0].x - points[3].x, 2) +
					Math.pow(points[0].y - points[3].y, 2));
			double maxHeight = Math.max(heightA, heightB);

			// 3. 定义目标点
			Point[] dstPoints = new Point[POINT_COUNT];
			dstPoints[0] = new Point(0, 0);                    // 左上
			dstPoints[1] = new Point(maxWidth - 1, 0);         // 右上
			dstPoints[2] = new Point(maxWidth - 1, maxHeight - 1); // 右下
			dstPoints[3] = new Point(0, maxHeight - 1);        // 左下

			// 4. 计算透视变换矩阵
			transformMatrix = Imgproc.getPerspectiveTransform(
					new MatOfPoint2f(points),
					new MatOfPoint2f(dstPoints)
			);

			// 5. 应用透视变换
			dstImage = new Mat();
			Imgproc.warpPerspective(
					srcImage,
					dstImage,
					transformMatrix,
					new Size(maxWidth, maxHeight)
			);

			return dstImage;
		} finally {
			release(srcImage,transformMatrix);
		}
	}


	/**
	 * 执行透视变换
	 *
	 * @param src 原始图像
	 * @param srcPoints 原始图像中的4个角点
	 * @param dstPoints 目标位置点
	 * @return 校正后的图像
	 */
	public static Mat correctPerspective(Mat src,Point[] srcPoints, Point[] dstPoints) {
		// 创建源点和目标点的矩阵
		Mat srcMat = new MatOfPoint2f(srcPoints);
		Mat dstMat = new MatOfPoint2f(dstPoints);

		// 计算透视变换矩阵
		Mat perspectiveMatrix = Imgproc.getPerspectiveTransform(srcMat, dstMat);

		// 应用透视变换，保持原始图像大小
		Mat corrected = new Mat();
		Imgproc.warpPerspective(
				src,
				corrected,
				perspectiveMatrix,
				new Size(src.width(), src.height()),
				Imgproc.INTER_LINEAR,
				Core.BORDER_CONSTANT,
				new Scalar(0, 0, 0)  // 背景填充为黑色
		);

		return corrected;
	}

	public static Rect targetToRect(Mat mat,Target target){
		return new Rect((int) (target.getLeft() * mat.width()),
				(int) (target.getTop() * mat.height()),
				(int) (target.getWidth() * mat.width()),
				(int) (target.getHeight() * mat.height()));
	}

	public static RotatedRect targetToRotatedRect(Mat mat,Target target){
		int width = mat.width();
		int height = mat.height();
		float w = target.getWidth() * width;
		float h = target.getHeight() * height;
		return new RotatedRect(
				new Point(target.getLeft() * width + w/2, target.getTop() * height + h/2),// 中心点
				new Size(w, h),target.getAngel() // 旋转角度（顺时针）
		);
	}

	//图片对比
	public static float diff(Object img1,Object img2,File result){
		Mat image1 = read(img1);
		Mat image2 = read(img2);

		// 转换为灰度图
		Mat gray1 = new Mat();
		Mat gray2 = new Mat();
		Imgproc.cvtColor(image1, gray1, Imgproc.COLOR_BGR2GRAY);
		Imgproc.cvtColor(image2, gray2, Imgproc.COLOR_BGR2GRAY);

		// 计算绝对差异图
		Mat diff = new Mat();
		Core.absdiff(gray1, gray2, diff);

		// 二值化差异图
		Mat thresh = new Mat();
		Imgproc.threshold(diff, thresh, 0, 255, Imgproc.THRESH_BINARY | Imgproc.THRESH_OTSU);

		// 形态学操作去除噪声
		Mat kernel = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(3, 3));
		Imgproc.morphologyEx(thresh, thresh, Imgproc.MORPH_OPEN, kernel);
		Imgproc.morphologyEx(thresh, thresh, Imgproc.MORPH_CLOSE, kernel);

		// 查找轮廓
		List<MatOfPoint> contours = new ArrayList<>();
		Mat hierarchy = new Mat();
		Imgproc.findContours(thresh, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

		// 计算差异区域面积
		double diffArea = 0;
		double minContourArea = Math.max(gray1.rows() * gray1.cols() * 0.0005, 50);

		for (MatOfPoint contour : contours) {
			double area = Imgproc.contourArea(contour);
			if (area > minContourArea) {
				diffArea += area;
			}
		}

		// 计算总像素数
		double totalPixels = gray1.rows() * gray1.cols();

		// 计算差异百分比
		double diffPercentage = diffArea / totalPixels;

		// 紧凑型可视化
		if (result != null) {
			// 调整图像大小以适应屏幕
			int maxHeight = 400;
			double scale = (double) maxHeight / image1.rows();
			int newWidth = (int) (image1.cols() * scale);

			// 调整所有图像大小
			Mat image1Resized = new Mat();
			Mat image2Resized = new Mat();
			Mat diffResized = new Mat();
			Mat threshResized = new Mat();

			Imgproc.resize(image1, image1Resized, new Size(newWidth, maxHeight));
			Imgproc.resize(image2, image2Resized, new Size(newWidth, maxHeight));
			Imgproc.resize(diff, diffResized, new Size(newWidth, maxHeight));
			Imgproc.resize(thresh, threshResized, new Size(newWidth, maxHeight));

			// 在图像1上绘制轮廓
			Mat image1Contours = image1Resized.clone();
			List<MatOfPoint> contoursResized = new ArrayList<>();

			for (MatOfPoint contour : contours) {
				MatOfPoint2f contour2f = new MatOfPoint2f(contour.toArray());
				MatOfPoint2f contourResized2f = new MatOfPoint2f();
				Core.multiply(contour2f, new Scalar(scale, scale), contourResized2f);

				MatOfPoint contourResized = new MatOfPoint();
				contourResized2f.convertTo(contourResized, CvType.CV_32S);
				contoursResized.add(contourResized);
			}

			Imgproc.drawContours(image1Contours, contoursResized, -1, new Scalar(0, 0, 255), 2);

			// 创建结果图像
			Mat resultImg = Mat.zeros(maxHeight * 2, newWidth * 2, CvType.CV_8UC3);

			// 放置图像
			Mat roi1 = resultImg.submat(0, maxHeight, 0, newWidth);
			image1Resized.copyTo(roi1);

			Mat roi2 = resultImg.submat(0, maxHeight, newWidth, newWidth * 2);
			image2Resized.copyTo(roi2);

			Mat roi3 = resultImg.submat(maxHeight, maxHeight * 2, 0, newWidth);
			Mat diffColor = new Mat();
			Imgproc.cvtColor(diffResized, diffColor, Imgproc.COLOR_GRAY2BGR);
			diffColor.copyTo(roi3);

			Mat roi4 = resultImg.submat(maxHeight, maxHeight * 2, newWidth, newWidth * 2);
			Mat threshColor = new Mat();
			Imgproc.cvtColor(threshResized, threshColor, Imgproc.COLOR_GRAY2BGR);
			threshColor.copyTo(roi4);

			// 添加文本
			Imgproc.putText(resultImg, "Result: ", new Point(10, maxHeight * 2 - 30),
					Imgproc.FONT_HERSHEY_SIMPLEX, 0.7, new Scalar(0, 255, 0), 2);

			Imgproc.putText(resultImg, String.format("Diff: %.4f", diffPercentage),
					new Point(10, maxHeight * 2 - 10), Imgproc.FONT_HERSHEY_SIMPLEX,
					0.7, new Scalar(0, 255, 0), 2);

			writeImg(resultImg,result);
			release(image1Resized,image2Resized,diffResized,threshResized,image1Contours,resultImg,
					roi1,roi2,roi3,diffColor,roi4,threshColor);
		}

		release(image1,image2,gray1,gray2,diff,thresh,kernel,hierarchy);
		return (float) diffPercentage;
	}

}
