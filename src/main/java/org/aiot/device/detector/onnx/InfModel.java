package org.aiot.device.detector.onnx;

import org.aiot.model.enums.aimodel.AlgorithmEnum;
import org.aiot.model.enums.aimodel.TaskEnum;
import org.aiot.util.MathUtil;
import org.aiot.util.OpenCVUtil;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.opencv.core.*;
import org.opencv.dnn.Dnn;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;


public class InfModel {
    private final Log log = Logs.get();

    AlgorithmEnum modelType = AlgorithmEnum.YOLOv5;
    TaskEnum task;
    boolean nms = false;
    String[] labels = new String[0];

    String[] inputName;
    long[] inputShape = new long[]{1,3,640,640};
    String[] outputName;
    long[] outputShape;

    String modelName;
    Object predictor; //执行者
    double scaleFactor= 1.0/255.0;
    int FILL_COLOR = 114;

    Preprocess preprocess = Preprocess.UNDISTORTED_FILL;

    public InfModel(String modelName, Object predictor){
        this.modelName = modelName;
        this.predictor = predictor;
    }

    public Mat preprocess(Mat input){
        return preprocess.build(input,inputShape,scaleFactor,FILL_COLOR);
    }


    public TaskEnum getTask() {
        return task;
    }

    public void setTask(String task) {
        try {
            this.task = TaskEnum.valueOf(task);
        }catch (IllegalArgumentException e){
            log.warn("未知任务类型："+task);
        }
    }


    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public String[] getLabels() {
        return labels;
    }

    public String getLabel(int index){
        return  index < labels.length ? labels[index] : (index + "");
    }

    public boolean containsLabels(String[] labels){
       for(String L1 : this.labels){
           for(String L2 : labels){
               if(L1.contains(L2))
                   return true;
           }
       }
       return false;
    }

    public boolean startModelName(String[] names){
        for(String name : names)
            if(modelName.startsWith(name))
                return true;
        return false;
    }

    public String[] getInputName() {
        return inputName;
    }

    public long[] getInputShape() {
        return inputShape;
    }

    public String[] getOutputName() {
        return outputName;
    }

    public void setOutputName(List<String> outputName) {
        this.outputName = outputName.toArray(new String[0]);
    }

    public long[] getOutputShape() {
        return outputShape;
    }

    public AlgorithmEnum getModelType() {
        return modelType;
    }

    public void setModelType(AlgorithmEnum modelType) {
        this.modelType = modelType;
    }

    public boolean isNms() {
        return nms;
    }

    public void setNms(boolean nms) {
        this.nms = nms;
    }

    public <T> T getPredictor() {
        return (T) predictor;
    }

    //预处理接口
    public static interface Preprocess{
        UndistortedFill UNDISTORTED_FILL = new UndistortedFill();
        CenterCrop CENTER_CROP = new CenterCrop();
        FixedHeight FIXED_HEIGHT = new FixedHeight();
        Mat build(Mat input,long[] shape,double scaleFactor,int fillColor);
    }

    //推理接口
    public static interface Inference{
        Object run(Mat data);
    }

    //后处理接口
    public static interface Postprocess{
        List<Object> parse(Mat output);
    }

    /**
     * 不变形填充
     */
    public static class UndistortedFill implements Preprocess{
        @Override
        public Mat build(Mat input,long[] shape,double scaleFactor, int fillColor) {
            int ws = (int) shape[2];
            int hs = (int) shape[3];

            // 调整大小并保持宽高比
            Mat resized = new Mat();
            // 计算保持宽高比的新尺寸
            double h = input.rows();
            double w = input.cols();
            double scale = Math.min(hs / h, ws / w); // 计算缩放比例
            int newH = (int) (h * scale);
            int newW = (int) (w * scale);

            Size newSize = new Size(newW, newH);
            Imgproc.resize(input, resized, newSize, 0, 0, Imgproc.INTER_LINEAR);

            // 创建目标尺寸的图像，并用灰色(114,114,114)填充
            Mat padded = new Mat(hs, ws, input.type());
            Scalar grayColor = new Scalar(fillColor, fillColor, fillColor);
            padded.setTo(grayColor);

            // 计算居中位置
            int top = (hs - newH) / 2;
            int left = (ws - newW) / 2;

            // 将调整大小后的图像复制到填充图像的中心
            Rect roi = new Rect(left, top, newW, newH);
            resized.copyTo(padded.submat(roi));

            //OpenCVUtil.writeImg(padded, PathEnum.temp.getFile("resized.jpg"));

            // 归一化到0-1范围
            Mat blob = Dnn.blobFromImage(padded, scaleFactor,
                    new Size(ws, hs), new Scalar(0, 0, 0), true, false);

            OpenCVUtil.release(resized,padded);

            return blob;
        }
    }

    /**
     * 中心裁剪
     */
    public static class CenterCrop implements Preprocess{
        @Override
        public Mat build(Mat input, long[] shape,double scaleFactor, int fillColor) {
            // Step 1: 以最小边为基准裁剪正方形
            int minSide = Math.min(input.rows(), input.cols());
            int startX = (input.cols() - minSide) / 2;
            int startY = (input.rows() - minSide) / 2;
            Mat squareCrop = new Mat(input, new Rect(startX, startY, minSide, minSide));

            // Step 2: 缩放到224x224
            Mat resized = new Mat();
            Imgproc.resize(squareCrop, resized, new Size(shape[2], shape[3]));
            //OpenCVUtil.writeImg(resized, PathEnum.temp.getFile("resized.jpg"));
            Mat blob = Dnn.blobFromImage(resized, scaleFactor,
                    new Size(shape[2], shape[3]), new Scalar(0.485 * 255.0, 0.456 * 255.0, 0.406 * 255.0), true, false);
            //new Scalar(0.485, 0.456, 0.406)
            Core.divide(blob, new Scalar(0.229, 0.224, 0.225), blob);

            OpenCVUtil.release(squareCrop,resized);
            return blob;
        }
    }

    //固定高度
    public static class FixedHeight implements Preprocess {
        @Override
        public Mat build(Mat input, long[] shape, double scaleFactor, int fillColor) {
            Mat mat2 = OpenCVUtil.resizeToTargetHeight(input,64);
            Mat mat3 = OpenCVUtil.fillToSquare(mat2, (int) shape[2]);
            Mat blob = Dnn.blobFromImage(mat3, scaleFactor,
                    new Size(shape[2], shape[3]), new Scalar(0, 0, 0), true, false);
            OpenCVUtil.release(mat2, mat3);
            return blob;
        }
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


