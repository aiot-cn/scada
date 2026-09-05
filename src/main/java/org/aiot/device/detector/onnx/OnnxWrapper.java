package org.aiot.device.detector.onnx;

import ai.onnxruntime.*;
import org.aiot.model.enums.ANSI;
import org.aiot.model.enums.aimodel.AlgorithmEnum;
import org.aiot.model.enums.aimodel.TaskEnum;
import org.nutz.lang.util.NutMap;
import org.nutz.log.Log;
import org.nutz.log.Logs;

import java.io.File;
import java.nio.FloatBuffer;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;


public class OnnxWrapper {
    private final Log log = Logs.get();
    private static final OrtEnvironment env = OrtEnvironment.getEnvironment();
    private OrtSession session;

    private AlgorithmEnum algorithm = AlgorithmEnum.YOLOv5;
    private TaskEnum task = TaskEnum.detect;
    private boolean nms = false;
    private String[] labels = new String[0];
    private String modelName;

    private String[] inputName;
    private long[] inputShape;
    private String[] outputName;
    private long[] outputShape;

    static {
        Logs.get().info(ANSI.COLOR_FORE.blue.format("ONNX Runtime Java API version:")+ env.getVersion());//1.17.3
    }


    public OnnxWrapper(File modelFile,OrtSession.SessionOptions options){
        modelName = modelFile.getName();
        try {

            session = env.createSession(modelFile.getAbsolutePath(), options);

            NodeInfo inputNodeInfo = session.getInputInfo().values().iterator().next();
            inputName = session.getInputNames().toArray(new String[0]);
            TensorInfo inputTensorInfo = (TensorInfo) inputNodeInfo.getInfo();
            inputShape = inputTensorInfo.getShape();

            NodeInfo outputNodeInfo = session.getOutputInfo().values().iterator().next();
            outputName = session.getOutputNames().toArray(new String[0]);
            TensorInfo outputTensorInfo = (TensorInfo) outputNodeInfo.getInfo();
            outputShape = outputTensorInfo.getShape();
            if(outputShape.length == 2)
                task = TaskEnum.classify;

            OnnxModelMetadata metadata = session.getMetadata();
            log.info("\n--------- 模型加载:"+ ANSI.COLOR_FORE.purple.format(modelFile.getName())+ " ---------" +
                    "\ninput:"+session.getInputNames()       + " shape:" + Arrays.toString(inputShape) +
                    "\noutput:"+session.getOutputNames()     + " shape:" + Arrays.toString(outputShape) +
                    "\nproducer:"+metadata.getProducerName() + " graph:" + metadata.getGraphName());

            /**
             * damoyolo_tinynasL25_S_456 [output, 848]
             * damoyolo 其它 [pred]
             * yolov5 yolo11 [output0]
             */
            if(!outputName[0].equals("output0"))
                this.algorithm = AlgorithmEnum.DAMO;
            Map<String, String> customMetadata = metadata.getCustomMetadata();
            customMetadata.forEach((k,v)-> {
                System.out.println(k+":"+v);
                if("task".equals(k))
                    setTask(v);
                if(v.contains("YOLO11"))
                    this.algorithm = AlgorithmEnum.YOLO11;
                if(v.contains("'nms': True"))
                    this.nms = true;
            });
            String names = customMetadata.get("names");
            if(names != null){
                Object[] labels = new NutMap(names).values().toArray(new Object[0]);
                this.labels = new String[labels.length];
                for (int i=0;i<labels.length;i++)
                    this.labels[i] = labels[i]+"";
            }

        } catch (OrtException e) {
            e.printStackTrace();
        }
    }

    public OrtSession.Result run(float[] chwData,long[] shape){
        //new long[]{1, 3, 640, 640}
        if(shape == null)
            shape = inputShape;

        synchronized (OnnxWrapper.class){
            try(OnnxTensor inputTensor = OnnxTensor.createTensor(env, FloatBuffer.wrap(chwData), shape)){
                return session.run(Collections.singletonMap(inputName[0], inputTensor));
            } catch (OrtException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public TaskEnum getTask() {
        return task;
    }

    public boolean isTask(TaskEnum task){
        return this.task == task;
    }

    public void setTask(String task) {
        try {
            this.task = TaskEnum.valueOf(task);
        }catch (IllegalArgumentException e){
            log.warn("未知任务类型："+task);
        }
    }

    public void close(){
        try {
            if(session != null)
                session.close();
        } catch (Exception e) {
            e.printStackTrace();
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

    public void setLabels(String[] labels) {
        this.labels = labels;
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

    public long[] getOutputShape() {
        return outputShape;
    }

    public AlgorithmEnum getAlgorithm() {
        return algorithm;
    }

    public void setAlgorithm(AlgorithmEnum algorithm) {
        this.algorithm = algorithm;
    }

    public boolean isNms() {
        return nms;
    }

    public void setNms(boolean nms) {
        this.nms = nms;
    }
}


