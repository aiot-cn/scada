package org.aiot.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class MathUtil {

	public static int maxIndex(float[] array,int start){
		return maxIndex(array, start, array.length);
	}
	public static int maxIndex(float[] array,int start,int end){
		if(end <= 0)
			end = array.length + end;
		int maxIndex = start;
		for (int i = start; i < end; i++) {
			if (array[i] > array[maxIndex]) {
				maxIndex = i;
			}
		}
		return maxIndex - start;
	}

	/**
	 * 计算数组的中位数
	 * @param arr 输入数组
	 * @return 中位数
	 */
	public static float getMedian(float[] arr) {
		float[] sortedArr = arr.clone();
		Arrays.sort(sortedArr);

		int n = sortedArr.length;
		if (n % 2 == 0) {
			// 偶数个元素，取中间两个的平均值
			return (sortedArr[n/2 - 1] + sortedArr[n/2]) / 2.0f;
		} else {
			// 奇数个元素，取中间值
			return sortedArr[n/2];
		}
	}

	/**
	 * 对 logits 应用 Softmax，返回每个类别的可信度（概率），和为 1。
	 * 数值稳定：减去最大值防止 exp 溢出。
	 *
	 * @param logits 未归一化的 logits，如 [-1.2f, 3.4f, 0.5f]
	 * @return 概率数组，长度相同，每个元素 ∈ [0,1]，总和 ≈ 1.0
	 */
	public static float[] softmax(float[] logits) {
		if (logits == null || logits.length == 0) {
			return new float[0];
		}

		// Step 1: 找最大值（用于数值稳定）
		float maxLogit = logits[0];
		for (int i = 1; i < logits.length; i++) {
			if (logits[i] > maxLogit) {
				maxLogit = logits[i];
			}
		}

		// Step 2: 计算 exp(logits[i] - maxLogit)
		float[] exps = new float[logits.length];
		float sumExp = 0.0f;
		for (int i = 0; i < logits.length; i++) {
			exps[i] = (float) Math.exp(logits[i] - maxLogit);
			sumExp += exps[i];
		}

		// Step 3: 归一化：exp_i / sum(exp)
		float[] probs = new float[logits.length];
		for (int i = 0; i < logits.length; i++) {
			probs[i] = exps[i] / sumExp;
		}

		return probs;
	}

	/**
	 * 非极大值抑制（NMS）
	 *
	 * @param detections 检测框列表，每个检测框格式为 [x, y, width, height, confidence, classId]
	 * @param iouThreshold IoU 阈值
	 * @param confidenceThreshold 置信度阈值
	 * @return 过滤后的检测框列表
	 */
	public static List<float[]> nms(List<float[]> detections, float iouThreshold, float confidenceThreshold) {
		// 1. 过滤低置信度的检测框
		detections.removeIf(detection -> detection[4] < confidenceThreshold);

		// 2. 按置信度从高到低排序
		detections.sort((a, b) -> Float.compare(b[4], a[4]));

		// 3. 初始化结果列表
		List<float[]> result = new ArrayList<>();

		// 4. 遍历检测框
		while (!detections.isEmpty()) {
			// 取出置信度最高的检测框
			float[] current = detections.get(0);
			result.add(current);

			// 移除当前检测框
			detections.remove(0);

			// 遍历剩余的检测框，移除与当前框 IoU 超过阈值的框
			Iterator<float[]> iterator = detections.iterator();
			while (iterator.hasNext()) {
				float[] detection = iterator.next();
				float iou = calculateIoU(current, detection);
				if (iou > iouThreshold) {
					iterator.remove();
				}
			}
		}

		return result;
	}

	/**
	 * 计算两个检测框的 IoU（交并比）
	 *
	 * @param box1 检测框 1，格式为 [x, y, width, height, ...]
	 * @param box2 检测框 2，格式为 [x, y, width, height, ...]
	 * @return IoU 值
	 */
	public static float calculateIoU(float[] box1, float[] box2) {
		// 计算两个框的相交区域
		float x1 = Math.max(box1[0], box2[0]);
		float y1 = Math.max(box1[1], box2[1]);
		float x2 = Math.min(box1[0] + box1[2], box2[0] + box2[2]);
		float y2 = Math.min(box1[1] + box1[3], box2[1] + box2[3]);

		// 计算相交区域面积
		float intersectionArea = Math.max(0, x2 - x1) * Math.max(0, y2 - y1);

		// 计算两个框的面积
		float box1Area = box1[2] * box1[3];
		float box2Area = box2[2] * box2[3];

		// 计算 IoU
		return intersectionArea / (box1Area + box2Area - intersectionArea);
	}

	//旋转点
	public static float[][] pointRotate(float[][] points, float[] center,float angleDeg) {
		// 将角度转换为弧度（顺时针旋转）
		double angleRad = Math.toRadians(angleDeg);
		double cosTheta = Math.cos(angleRad);
		double sinTheta = Math.sin(angleRad);

		// 旋转后的点
		float[][] rotatedPoints = new float[points.length][2];

		// 应用旋转公式（图像坐标系调整）
		for (int i = 0; i < points.length; i++) {
			float x = points[i][0];
			float y = points[i][1];
			float dx = x - center[0];
			float dy = y - center[1];
			// 图像坐标系下的顺时针旋转公式
			rotatedPoints[i][0] = (float) (center[0] + dx * cosTheta - dy * sinTheta);
			rotatedPoints[i][1] = (float) (center[1] + dx * sinTheta + dy * cosTheta);
		}

		return rotatedPoints;
	}

	/**
	 * 将二维数组进行行列转换（转置）
	 * @param matrix 原始二维数组
	 * @return 转置后的二维数组
	 */
	public static float[][] transpose(float[][] matrix) {
		if (matrix == null || matrix.length == 0) {
			return matrix;
		}

		int rows = matrix.length;
		int cols = matrix[0].length;

		// 创建新的转置矩阵
		float[][] transposed = new float[cols][rows];

		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				transposed[j][i] = matrix[i][j];
			}
		}

		return transposed;
	}

}
