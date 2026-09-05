package org.aiot.device.detector.onnx;

import ai.onnxruntime.OnnxTensor;
import ai.onnxruntime.OnnxValue;
import ai.onnxruntime.OrtSession;

import org.aiot.lang.annotation.AoReflect;
import org.aiot.model.enums.AstEnum;
import org.aiot.model.enums.DeviceRoleEnum;
import org.aiot.model.lang.RecognitionRes;
import org.aiot.util.FileUtil;
import org.nutz.lang.Files;
import org.nutz.lang.Lang;
import org.nutz.lang.Strings;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@AoReflect(value = "DAMO_OCR",deviceRole = DeviceRoleEnum.AI_MODEL)
public class OnnxDamoOcrDevice extends OnnxBase{

	private final Log log = Logs.get();

	@AoReflect(type = AstEnum.param)
	private File vocabFile;

	@AoReflect(value = "指定字符",type = AstEnum.param)
	private String specifyChar;


	long[] shape = {3, 3, 32, 300};

	private String[] labels;

	@Override
	public void init() {

	}

	public void setVocabFile(File vocabFile) {
		this.vocabFile = vocabFile;
		if(vocabFile != null){
			int start = 2;
			List<String> list = Files.readLines(vocabFile);
			labels = new String[list.size() + start];
			for(int i=0;i<list.size();i++){
				labels[i+start] = list.get(i).trim();
			}
		}
	}

	@Override
	public void setLabels(String labels){
		File vocabFile = FileUtil.toFile(labels);
		setVocabFile(vocabFile);
	}


	/**
	 * @param type 暂无用
	 * @return
	 */
	@Override
	public RecognitionRes recognizeMat(Mat image, String type) {

		if(labels == null)
			throw Lang.makeThrow("还未加载词汇表");

		RecognitionRes targets = new RecognitionRes(null, image.width(), image.height());
		long t1 = System.currentTimeMillis();
		// 1. 加载图像并进行预处理
		Mat img = image;
		long t2 = System.currentTimeMillis();

		// 保持宽高比的resize
		Mat resizedImg = keepRatioResize(img);
		long t3 = System.currentTimeMillis();

		// 转换为FloatTensor并分割图像
		List<Mat> chunkImgs = splitImage(resizedImg);

		// 创建输入数据
		float[][][][] inputData = prepareInputData(chunkImgs);

		img.release();
		resizedImg.release();
		chunkImgs.forEach(Mat::release);

		float[] inputData1 = flattenArray(inputData);
		long t4 = System.currentTimeMillis();
		long t5 = t4,t6 = t4;
		String remark = "";
		try(OrtSession.Result results = onnxList.get(0).run(inputData1,shape)){
			t5 = System.currentTimeMillis();
			// 获取输出张量
			OnnxValue outputValue = results.get(0);
			if (outputValue instanceof OnnxTensor) {
				OnnxTensor outputTensor = (OnnxTensor) outputValue;
				float[][][] outputData = (float[][][]) outputTensor.getValue();

				// 对每个批次的结果进行CTC解码
				String finalStr = "";

				float minConfidence = 1;
				for (int i = 0; i < outputData.length; i++) {
					// 获取当前批次的预测结果 [sequence_length, num_classes]
					float[][] batchOutput = outputData[i];

					// 应用softmax并获取预测索引
					int[][] preds = applySoftmaxAndArgmax(batchOutput);

					// CTC解码
					List<Integer> decoded = ctcDecode(preds);
					for(Integer j :decoded){
						String c = labels[preds[j][0]];
						float confidence = preds[j][1] / 100f;
						finalStr += c;
						remark += "["+c+"]"+confidence + " ";
						minConfidence = Math.min(confidence, minConfidence);
					}

				}
				targets.addTarget(finalStr,minConfidence,0,0,1f,1f);
			}
			t6 = System.currentTimeMillis();
		}catch (Exception e) {
			e.printStackTrace();
		};

		String time = String.format("加载%dms 缩放%dms 预处理%dms 推理%dms 解析%dms 共计%dms",t2-t1,t3-t2,t4-t3,t5-t4,t6-t5,t6-t1);
		targets.setRemark(remark +" " + time);
		return targets;
	}

	// 保持宽高比的resize
	private static Mat keepRatioResize(Mat img) {
		double curRatio = (double) img.cols() / img.rows();
		int maskHeight = 32;
		int maskWidth = 804;

		int targetWidth, targetHeight;
		if (curRatio > (double) maskWidth / maskHeight) {
			targetHeight = maskHeight;
			targetWidth = maskWidth;
		} else {
			targetHeight = maskHeight;
			targetWidth = (int) (maskHeight * curRatio);
		}

		// 调整图像尺寸
		Mat resized = new Mat();
		Imgproc.resize(img, resized, new Size(targetWidth, targetHeight));

		// 创建掩码并复制图像
		Mat mask = Mat.zeros(maskHeight, maskWidth, CvType.CV_8UC3);
		Mat roi = mask.submat(0, resized.rows(), 0, resized.cols());
		resized.copyTo(roi);

		resized.release();
		roi.release();

		return mask;
	}

	// 分割图像为3个块
	private static List<Mat> splitImage(Mat img) {
		List<Mat> chunks = new ArrayList<>();
		int chunkWidth = 300;
		int overlap = 48;  // 重叠部分

		for (int i = 0; i < 3; i++) {
			int left = (chunkWidth - overlap) * i;
			int right = Math.min(left + chunkWidth, img.cols());

			if (left >= img.cols()) break;

			Mat chunk = new Mat(img, new Rect(left, 0, right - left, img.rows()));
			chunks.add(chunk);
		}
		return chunks;
	}

	// 准备输入数据 [batch, channels, height, width]
	private static float[][][][] prepareInputData(List<Mat> chunks) {
		int batchSize = chunks.size();
		float[][][][] inputData = new float[batchSize][3][32][300];

		for (int b = 0; b < batchSize; b++) {
			Mat chunk = chunks.get(b);
			// 确保所有块都是300x32
			Mat resized = null;
			if (chunk.cols() != 300 || chunk.rows() != 32) {
				resized = new Mat();
				Imgproc.resize(chunk, resized, new Size(300, 32));
				chunk = resized;
			}

			// 归一化并转换维度顺序 HWC -> CHW
			for (int y = 0; y < 32; y++) {
				for (int x = 0; x < 300; x++) {
					double[] pixel = chunk.get(y, x);
					// BGR通道 (OpenCV默认顺序)
					inputData[b][0][y][x] = (float) (pixel[0] / 255.0); // B
					inputData[b][1][y][x] = (float) (pixel[1] / 255.0); // G
					inputData[b][2][y][x] = (float) (pixel[2] / 255.0); // R
				}
			}
			if(resized != null)
				resized.release();
		}
		return inputData;
	}

	// 将4D数组展平为1D
	private static float[] flattenArray(float[][][][] array) {
		int batch = array.length;
		int channels = array[0].length;
		int height = array[0][0].length;
		int width = array[0][0][0].length;

		float[] flat = new float[batch * channels * height * width];
		int idx = 0;

		for (int b = 0; b < batch; b++) {
			for (int c = 0; c < channels; c++) {
				for (int y = 0; y < height; y++) {
					for (int x = 0; x < width; x++) {
						flat[idx++] = array[b][c][y][x];
					}
				}
			}
		}
		return flat;
	}

	// 应用softmax并获取argmax
	private int[][] applySoftmaxAndArgmax(float[][] logits) {
		int seqLength = logits.length;
		int numClasses = logits[0].length;
		int[][] preds = new int[seqLength][2];

		for (int t = 0; t < seqLength; t++) {
			// 计算softmax
			float max = Float.NEGATIVE_INFINITY;
			for (float value : logits[t]) {
				if (value > max) max = value;
			}

			float sum = 0.0f;
			float[] probs = new float[numClasses];
			for (int c = 0; c < numClasses; c++) {
				probs[c] = (float) Math.exp(logits[t][c] - max);
				sum += probs[c];
			}

			// 获取argmax
			int maxIdx = 0;
			float maxProb = 0;
			for (int c = 0; c < numClasses; c++) {
				probs[c] /= sum;
				if (probs[c] > maxProb && (Strings.isBlank(specifyChar) || c < 2 || specifyChar.contains(labels[c]) )) {
					maxProb = probs[c];
					maxIdx = c;
				}
			}
			preds[t][0] = maxIdx;
			preds[t][1] = (int) (probs[maxIdx] * 100);
			//System.out.println(labels[maxIdx] + " " + probs[maxIdx]);
		}
		return preds;
	}

	// CTC解码（简单版：去除重复和空白符）
	private List<Integer> ctcDecode(int[][] preds) {
		List<Integer> ctc = new ArrayList<>();
		int last = 0;

		for (int i = 0; i < preds.length; i++) {
			int p = preds[i][0];
			// 0通常是空白符，跳过
			if (p != 0) {
				// 跳过连续重复
				if (p != last) {
					String charStr = labels[p];
					if (charStr != null) {
						ctc.add(i);
					}
					last = p;
				}
			} else {
				last = 0; // 重置最后一个非空白符
			}
		}
		return ctc;
	}

}


