package org.aiot.device.detector.onnx;

import ai.onnxruntime.*;
import org.aiot.device.AbstractTarget;
import org.aiot.infc.ImgAbstract;
import org.aiot.infc.device.BaseExtend;
import org.aiot.lang.annotation.AoReflect;
import org.aiot.model.DataRes;
import org.aiot.model.enums.ANSI;
import org.aiot.model.enums.AstEnum;
import org.aiot.model.lang.RecognitionRes;
import org.aiot.util.*;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;


import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

/**
 * https://runtime.onnx.org.cn/docs/get-started/with-java.html
 * https://central.sonatype.com/artifact/com.microsoft.onnxruntime/onnxruntime?smo=true
 */
public class OnnxBase extends AbstractTarget implements BaseExtend.RMenu{
	private final Log log = Logs.get();

	@AoReflect(type = AstEnum.param,select = "CPU,CUDA,TensorRT,RK_NPU",sequence = 1)
	private String providerType = "CPU";

	@AoReflect(type = AstEnum.param,input = "suffix:onnx",sequence = 2)
	private File modelFile;

	@AoReflect(value = "模型标签",getter = true)
	private String modelLabel;

	private OrtSession.SessionOptions options;

	protected final List<OnnxWrapper> onnxList = new ArrayList<>();

	@Override
	public void setLabels(String labels){
		OnnxWrapper ow = onnxList.get(0);
		ow.setLabels(labels.split( ","));
	}

	public void setProviderType(String providerType) {
		this.providerType = providerType;
		try {
			options = new OrtSession.SessionOptions();
			if("CUDA".equals(providerType)){
				options.addCUDA(0);
			}else if("TensorRT".equals(providerType)){
				options.addTensorrt(0); // 0表示使用默认的GPU设备
			}else if("RK_NPU".equals(providerType)){
				//options.addExecutionProvider(OrtProvider.RK_NPU,OrtProviderOptions);
			}
		}catch (OrtException e) {
			log.warn(ANSI.COLOR_FORE.yellow.format(providerType + "添加失败：" + e.getMessage() + " 可用提供者："+ OrtEnvironment.getAvailableProviders()));
		}catch (UnsatisfiedLinkError e){
			log.error(ANSI.COLOR_FORE.red.format( "ONNX无法链接到所需的本地库：" + e.getMessage()));
		}
	}

	public List<OnnxWrapper> setModelFile(File modelFile) {
		this.modelFile = modelFile;
		if(options == null){
			setProviderType(null);
		}
		if(modelFile == null || options == null)
			return null;
		if(!modelFile.exists()){
			log.warn(ANSI.COLOR_FORE.yellow.format("设备["+device.getName() +"]加载模型："+modelFile.getName()+" 文件不存在"));
			return null;
		}

		onnxList.forEach(OnnxWrapper::close);
		onnxList.clear();

		File[] files = new File[]{modelFile};
		if(modelFile.isDirectory())
			files = FileUtil.getFiles(modelFile, v->v.getName().endsWith(".onnx")).toArray(new File[0]);

		try {
			//操作间线程数
			//options.setInterOpNumThreads(1);
			//操作内线程数
			//options.setIntraOpNumThreads(1);
			//顺序执行以减少内存峰值
			//options.setExecutionMode(OrtSession.SessionOptions.ExecutionMode.SEQUENTIAL);
			// 禁用内存模式优化
			//options.setMemoryPatternOptimization(false);
			options.setSessionLogLevel(OrtLoggingLevel.ORT_LOGGING_LEVEL_WARNING);
			//禁用CPU 内存分配器 (可以显著减少内存)
			//options.setCPUArenaAllocator(false);

		} catch (OrtException e) {
			e.printStackTrace();
		}
		for(File f : files){
			OnnxWrapper wrapper = new OnnxWrapper(f,options);
			onnxList.add(wrapper);
		}

		return onnxList;

	}

	/*
	 * YOLO5 shape [1,25200,9]
	 * 位置4+可信度1+分类4个的可信度 = 9
	 * 由于YOLOv5是anchor-based模型，每个网格预测3个边界框，因此总的检测框数量为：80x80 + 1600 + 400 = 8400 * 3 = 25200个单元格
	 *
	 */
	@Override
	@AoReflect(value="识别",type= AstEnum.command)
	public RecognitionRes recognize(Object file,String type) {
		long t1 = System.currentTimeMillis();
		String m = file + "";
		if(file instanceof File)
			m = ((File) file).getAbsolutePath();
		sendSocket("onnx 识别[%s]:"+ m,type);

		Object obj = file;
		if(obj instanceof String)
			obj = CommonUtil.getUri((String)obj);

		if(obj instanceof File){
			RecognitionRes t =  recognizeFile((File)obj,type);
			t.setRemark(t.getRemark() + " 共计"+(System.currentTimeMillis() - t1)+"ms");
			return t;
		}


		if(obj instanceof ImgAbstract)
			obj = ((ImgAbstract)obj).getImg();

		Mat img = OpenCVUtil.read(obj);
		long t2 = System.currentTimeMillis();
		RecognitionRes t =  recognizeMat(img,type);
		img.release();
		t.setImg(ImgUtil.read(obj));
		t.setRemark("读取"+(t2 - t1)+"ms "+t.getRemark() + " 共计"+(System.currentTimeMillis() - t1)+"ms");
		return t;
	}


	@AoReflect(value="识别文件",type= AstEnum.command)
	public RecognitionRes recognizeFile(File file,String type) {
		long t1 = System.currentTimeMillis();
		Mat image = OpenCVUtil.readImg(file);
		long T1 = System.currentTimeMillis() - t1;
		RecognitionRes t = recognizeMat(image,type);
		t.setRemark("读取"+T1+"mm " + t.getRemark());
		t.setImg(file);
		image.release();
		return t;
	}

	public RecognitionRes recognizeMat(Mat image, String type) {
		return null;
	}


	@Override
	public List<RMenuOption> menuList() {
		List<RMenuOption> list = new ArrayList<>();
		list.add(new RMenuOption("加载到/"+device.getName(),"load",device.getId(),"onnx"));
		if(onnxList.size() == 1){
			String menuName = device.getName()+"-"+onnxList.get(0).getModelName().replace(".onnx","");
			list.add(new RMenuOption("识别/"+menuName,"rec",device.getId(),"img"));
		}else{
			list.add(new RMenuOption("识别/"+device.getName(),"rec",device.getId(),"img"));
			for(OnnxWrapper ow : onnxList){
				String menuName = device.getName()+"/"+ow.getModelName().replace(".onnx","");
				list.add(new RMenuOption("识别/"+menuName,"rec:"+ow.getModelName(),device.getId(),"img"));
			}
		}
		return list;
	}

	@Override
	public Object menuClick(String menu,String val){
		if("load".equals(menu)){
			setParam("modelFile", val);
			return DataRes.success("加载成功");
		}else if(menu.startsWith("rec")){
			String[] a = menu.split(":");
			return recognize(val,a.length > 1 ? a[1] : "");
		}
		return null;
	}

	@Override
	public void destroy(){
		onnxList.forEach(OnnxWrapper::close);
		super.destroy();
	}

	public String getModelLabel() {
		StringBuilder sb = new StringBuilder();
		onnxList.forEach(v->{
			sb.append(v.getModelName()).append("：")
					.append(String.join(",",v.getLabels()))
					.append("\n");
		});
		return sb.toString();
	}

	/**
	 *
	 * @param img
	 * @param w 目标宽度
	 * @param h 目标高度
	 * @param alpha 归一化系数
	 * @param fillColor 填充颜色
	 */
	public float[] matUndistortedFloat(Mat img,int w,int h,double alpha,int[] fillColor){
		Scalar scalar = new Scalar(114, 114, 114);
		if(fillColor.length == 1)
			scalar = new Scalar(fillColor[0], fillColor[0], fillColor[0]);
		if(fillColor.length == 3)
			scalar = new Scalar(fillColor[0], fillColor[1], fillColor[2]);

		Mat dst = new Mat(h, w, CvType.CV_8UC3, scalar);

		double scale = Math.min((double) w / img.width(), (double) h / img.height());
		int newWidth = (int) (img.width() * scale);
		int newHeight = (int) (img.height() * scale);
		//等比例缩放图像
		Mat resized = new Mat();
		Imgproc.resize(img, resized, new Size(newWidth, newHeight), 0, 0, Imgproc.INTER_LINEAR); //Imgproc.INTER_AREA 这个在识别形状时最好 5在识别语义分割时最好

		int x0 = (w - newWidth) / 2;
		int y0 = (h - newHeight) / 2;
		//将缩放后的图像放入画布中心
		Mat roi = dst.submat(new Rect(x0, y0, newWidth, newHeight));
		resized.copyTo(roi);
		//Imgcodecs.imwrite(file.getParent() + "/a3.jpg", dst);
		//归一化并转换为浮点型
		Mat floatMat = new Mat();
		dst.convertTo(floatMat, CvType.CV_32FC3, alpha);

		float[] d =  OpenCVUtil.normalizeCHW(floatMat);
		OpenCVUtil.release(dst,resized,roi,floatMat);
		return d;
	}

	// 转置函数
	public float[][] transposeMatrix(float[][] matrix) {

		int originalRows = matrix.length;         // 原行数 (84)
		int originalCols = matrix[0].length;      // 原列数 (8400)

		// 创建转置后的矩阵: 行数=原列数，列数=原行数
		float[][] transposed = new float[originalCols][originalRows];

		// 遍历原矩阵，交换行列索引
		for (int i = 0; i < originalRows; i++) {
			for (int j = 0; j < originalCols; j++) {
				transposed[j][i] = matrix[i][j];
			}
		}

		return transposed;
	}

	// 获取Top-K预测结果
	public List<Prediction> getTopKPredictions(float[] logits, int topK) {
		float[] probs = MathUtil.softmax(logits);
		PriorityQueue<Prediction> pq = new PriorityQueue<>(Comparator.comparingDouble(p -> p.probability));
		for (int i = 0; i < probs.length; i++) {
			pq.add(new Prediction(i, probs[i]));
			if (pq.size() > topK) pq.poll();
		}
		List<Prediction> result = new ArrayList<>(pq);
		result.sort((a, b) -> Float.compare(b.probability, a.probability));

		return result;
	}

	// 预测结果封装类
	static class Prediction {
		int classIndex;
		float probability;

		Prediction(int classIndex, float probability) {
			this.classIndex = classIndex;
			this.probability = probability;
		}
	}
}


