package org.aiot.device.detector.onnx;

import org.aiot.lang.annotation.AoReflect;
import org.aiot.model.DataRes;
import org.aiot.model.enums.ANSI;
import org.aiot.model.enums.AstEnum;
import org.aiot.model.enums.DeviceRoleEnum;
import org.aiot.model.enums.PathEnum;
import org.aiot.model.enums.aimodel.AlgorithmEnum;
import org.aiot.model.enums.aimodel.TaskEnum;
import org.aiot.model.lang.RecognitionRes;
import org.aiot.model.lang.Target;
import org.aiot.util.FileUtil;
import org.aiot.util.MathUtil;
import org.aiot.util.OpenCVUtil;
import org.nutz.lang.Strings;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.opencv.core.Mat;
import org.opencv.dnn.Dnn;
import org.opencv.dnn.Net;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@AoReflect(value = "CvOnnx",deviceRole = DeviceRoleEnum.AI_MODEL)
public class CvOnnx extends OnnxBase {
	private final Log log = Logs.get();

	@AoReflect(type = AstEnum.param,input = "suffix:onnx")
	private File modelFile;

	protected final List<InfModel> modelList = new ArrayList<>();

	public List<OnnxWrapper> setModelFile(File modelFile){
		OpenCVUtil.release();
		this.modelFile = modelFile;
		if(modelFile == null)
            return null;
		if(!modelFile.exists()){
			log.warn(ANSI.COLOR_FORE.yellow.format("设备["+device.getName() +"]加载模型："+modelFile.getName()+" 文件不存在"));
            return null;
		}
		for (InfModel model : modelList){
			Net net = model.getPredictor();
			net.empty();
		}
		modelList.clear();

		File[] files = new File[]{modelFile};
		if(modelFile.isDirectory())
			files = FileUtil.getFiles(modelFile, v->v.getName().endsWith(".onnx")).toArray(new File[0]);

		for(File f : files){
			File modelTemp = PathEnum.temp.getRam("cv_temp_"+device.getId()+".onnx");
			try {
				Files.copy(f.toPath(), modelTemp.toPath(), StandardCopyOption.REPLACE_EXISTING);
			} catch (IOException e) {
				e.printStackTrace();
			}
			Net net = Dnn.readNetFromONNX(modelTemp.getAbsolutePath());
			List<String> layers = net.getLayerNames();
			//System.out.println(net.getLayerNames().size());
			// 设置计算后端（可选）
			//net.setPreferableBackend(Dnn.DNN_BACKEND_OPENCV);
			//net.setPreferableTarget(Dnn.DNN_TARGET_CPU);
			InfModel infModel = new InfModel(modelFile.getName(),net);
			infModel.setOutputName(net.getUnconnectedOutLayersNames());
			if(layers.size() == 112 || layers.size() == 161){
				infModel.task = TaskEnum.classify;
				infModel.inputShape = new long[]{1,3,224,224};
				infModel.preprocess = InfModel.Preprocess.CENTER_CROP;
			}else if(layers.size() == 255){
				infModel.task = TaskEnum.detect;
			}else if(layers.size() == 272){
				infModel.task = TaskEnum.segment;
			}else if(layers.size() == 361){
				infModel.modelType = AlgorithmEnum.YOLO11;
				infModel.task = TaskEnum.pose;
			}else if(layers.size() == 363){
				infModel.modelType = AlgorithmEnum.YOLO11;
				infModel.task = TaskEnum.obb;
				infModel.inputShape = new long[]{1,3,1024,1024};
			}

			String des = FileUtil.fileInfo(f).getDescription();
			if(Strings.isNotBlank(des)){
				Matcher matcher = Pattern.compile("model:(\\S+)").matcher(des);
				if(matcher.find()) {
					String model = matcher.group(1);
					if(model.equals("ocr")){
						infModel.task = TaskEnum.ocr;
						infModel.preprocess = InfModel.Preprocess.FIXED_HEIGHT;
					}
				}
				matcher = Pattern.compile("\\[标签\\] (\\S+)").matcher(des);
				if(matcher.find()) {
					String labelStr = matcher.group(1);
					infModel.labels = labelStr.split(",");
				}
			}

			modelList.add(infModel);
			log.info("\n--------- 模型加载:"+ ANSI.COLOR_FORE.purple.format(modelFile.getName())+ "  " + infModel.modelType+"-"+ infModel.task +" ---------");
		}

        return null;
    }

	@Override
	public void init() {

	}

	@Override
	public RecognitionRes recognizeMat(Mat image, String type) {
		int srcWidth = image.width();
		int srcHeight = image.height();
		RecognitionRes res = new RecognitionRes(null,srcWidth,srcHeight);

		String[] types = Strings.isBlank(type) ? null : type.split(",");
		try {
			for (InfModel model : modelList){
				if(types != null && !model.startModelName(types) && !model.containsLabels(types))
					continue;

				RecognitionRes targets = new RecognitionRes(null,srcWidth,srcHeight);
				Net net = model.getPredictor();
				int inputWidth = (int) model.inputShape[2];
				int inputHeight = (int) model.inputShape[3];
				double scale = Math.min((double) inputWidth / srcWidth, (double) inputHeight / srcHeight);
				int newWidth = (int) (image.cols() * scale);
				int newHeight = (int) (image.rows() * scale);
				int x0 = (inputWidth - newWidth) / 2;
				int y0 = (inputHeight - newHeight) / 2;

				Mat blob = model.preprocess(image);
				net.setInput(blob);

				List<Mat> outputs = new ArrayList<>();
				net.forward(outputs, net.getUnconnectedOutLayersNames());
				Mat output0 = outputs.get(0);
				//0目标检测 1语义分割 2分类 3姿势估计 4旋转框 5OCR

				Mat reshaped = output0.reshape(1,output0.size(1));
				float[][] detections = new float[reshaped.rows()][];
				for (int i = 0; i < reshaped.rows(); i++) {
					float[] detection = new float[reshaped.cols()];
					reshaped.get(i, 0, detection);
					detections[i] = detection;
				}
				OpenCVUtil.release(blob,reshaped);
				outputs.forEach(Mat::release);

				if(model.modelType == AlgorithmEnum.YOLO11){
					detections = MathUtil.transpose(detections);
				}

				if(model.task == TaskEnum.classify){
					float[] output = new float[detections.length];
					for (int i = 0; i < output.length; i++){
						output[i] = detections[i][0];
					}
					List<InfModel.Prediction> predictions = model.getTopKPredictions(output, 5);
					for(int i=0;i<predictions.size();i++){
						InfModel.Prediction p = predictions.get(i);
						if(p.probability > 0.01)
							targets.addTarget(model.getLabel(p.classIndex),p.probability,0,0,1f,1f);
					}
				}
				else if(model.task == TaskEnum.obb){
					for (float[] detection : detections) {
						int classId = MathUtil.maxIndex(detection,4,-1);

						detection[4] = detection[classId + 4];
						if(detection[4] < 0.3)
							continue;

						float angel = (float) Math.toDegrees(detection[detection.length - 1]);
						if(angel > 60){
							angel = angel - 90;
							float tem = detection[2];
							detection[2] = detection[3];
							detection[3] = tem;
						}

						Target target = targets.addTarget(model.getLabel(classId), detection, 1, inputWidth, inputHeight, x0, y0);
						target.setAngel(angel);
					}
					targets.nms(0.5f);
				}
				else if(model.task == TaskEnum.pose){
					List<float[]> f0 = new ArrayList<>(Arrays.asList(detections));
					List<float[]> fs = MathUtil.nms(f0,0.5f,0.3f);
					for(float[] detection : fs) {
						Target t = targets.addTarget(model.getLabel(0),detection,1,inputWidth,inputHeight,x0,y0);
						int i0 = (detection.length - 5)/3;
						for(int i = 0;i < i0;i++){
							int j = 5 + 3 * i;
							t.addPoint(new float[]{
									(detection[j]  -x0) / (inputWidth -x0*2),
									(detection[j+1]-y0) / (inputHeight-y0*2),
									detection[j+2]
							});
						}
					}
				}
				else if(model.task == TaskEnum.ocr){
					List<float[]> f0 = new ArrayList<>(Arrays.asList(detections));
					List<float[]> fs = MathUtil.nms(f0,0.3f,0.2f);
					float height = 64f;
					float width = image.width() * (height / image.height());

					List<Target> dot = new ArrayList<>();
					for (float[] detection : fs) {
						float xCenter = detection[0]; // 中心点 x
						float yCenter = detection[1]; // 中心点 y
						float w = detection[2];  // 宽度
						float h = detection[3];  // 高度
						float confidence = detection[4]; // 置信度

						int classId = MathUtil.maxIndex(detection, 5);

						// 将相对坐标转换为绝对坐标
						float x = Math.max(0,xCenter - w / 2);
						float y = Math.max(0,yCenter - h / 2);
						float left = x/width;
						float top = y/height;

						String label = model.getLabel(classId);
						Target t = new Target(label,confidence,left,top,w/width,h/height);
						if(".".equals(label)){
							dot.add(t);
						}else if(confidence > 0.5){
							targets.addTarget(t);
						}
					}

					List<Target> sourceTarget = new ArrayList<>();
					sourceTarget.addAll(targets.getTargets());
					sourceTarget.addAll(dot);

					targets.nms(0.1f);
					if(dot.size() > 0){
						dot.sort(Comparator.comparing(Target::getConfidence));
						targets.addTarget(dot.get(dot.size()-1));
					}
					targets.getTargets().sort(Comparator.comparing(Target::getLeft));

					float minC = 1, minX = 1 , minY = 1 ,maxX = 0,maxY = 0;
					String label = "";
					for(Target t : targets.getTargets()){
						label += t.getLabel();
						minC = Math.min(minC,t.getConfidence());
						minX = Math.min(minX,t.getLeft());
						minY = Math.min(minY,t.getTop());
						maxX = Math.max(maxX,t.getLeft() + t.getWidth());
						maxY = Math.max(maxY,t.getTop() + t.getHeight());
					}
					targets.getTargets().clear();
					targets.addTarget(label,minC,minX,minY,maxX-minX,maxY-minY);
					if(isDebug)
						targets.addTargets(sourceTarget);

				}
				else{
					//目标检测 reshape to (row 25200, col 9)
					List<float[]> f0 = new ArrayList<>(Arrays.asList(detections));
					List<float[]> fs = MathUtil.nms(f0,0.5f,0.3f);
					for (float[] detection : fs) {
						int classId = MathUtil.maxIndex(detection, model.modelType == AlgorithmEnum.YOLO11 ? 4 : 5);
						targets.addTarget(model.getLabel(classId), detection, 1, inputWidth, inputHeight, x0, y0);
					}
				}

				if(types == null || model.startModelName(types)){
					res.addTargets(targets.getTargets());
				}else{
					res.addTargets(targets.findLabel(types));
				}
			}
		}finally {
			OpenCVUtil.release(image);
		}
		return res;
	}

	public String getModelLabel() {
		StringBuilder sb = new StringBuilder();
		modelList.forEach(v->{
			sb.append(v.getModelName()).append("：")
					.append(String.join(",",v.getLabels()))
					.append("\n");
		});
		return sb.toString();
	}


	@Override
	public List<RMenuOption> menuList() {
		List<RMenuOption> list = new ArrayList<>();
		list.add(new RMenuOption("加载到/"+device.getName(),"load",device.getId(),"onnx"));
		if(modelList.size() == 1){
			String menuName = device.getName()+"-"+modelList.get(0).getModelName().replace(".onnx","");
			list.add(new RMenuOption("识别/"+menuName,"rec",device.getId(),"img"));
		}else{
			list.add(new RMenuOption("识别/"+device.getName(),"rec",device.getId(),"img"));
			for(InfModel ow : modelList){
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
			return new DataRes(recognize(val,a.length > 1 ? a[1] : ""));
		}
		return null;
	}

}


