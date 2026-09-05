package org.aiot.model.lang;

import org.aiot.infc.ImgAbstract;
import org.aiot.infc.ValInfc;
import org.aiot.model.table.TRecord;
import org.aiot.util.FileUtil;
import org.aiot.util.ImgUtil;
import org.aiot.util.OpenCVUtil;
import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.nutz.lang.Strings;
import org.nutz.mvc.View;
import org.nutz.mvc.view.JspView;
import org.opencv.core.Mat;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class RecognitionRes extends ImgAbstract implements ValInfc,View {

	List<Target> targets = new ArrayList<>();
	Object value;
	String remark;

	//JSON转换时会调用无参构造函数
	public RecognitionRes(){

	}

	public RecognitionRes(Object img){
		this.img = img;
	}

	public RecognitionRes(Object img, int width, int height){
		this.img = img;
		this.width = width;
		this.height = height;
	}

	public RecognitionRes(Object img, Target target){
		this.img = img;
		targets.add(target);
	}

	public RecognitionRes clone(List<Target> targets){
		RecognitionRes t = new RecognitionRes(img,width,height);
		t.targets = targets == null ? new ArrayList<>() : targets;
		return t;
	}

	public void pullWH(){
		if(width != 0)
			return;

		BufferedImage bi = getBufferedImage();
		if(bi != null){
			this.width = bi.getWidth();
			this.height = bi.getHeight();
		}
	}

	public Target addTarget(Target t){
		targets.add(t);
		return t;
	}

	/**
	 * @param detType
	 * 0.left top w h confidence <br>
	 * 1.xc yc w h confidence <br>
	 * 2.xMin yMin xMax yMax confidence <br>
	 */
	public Target addTarget(String label,float[] detection,int detType,int w2,int h2,float x0,float y0){
		float x = detection[0];
		float y = detection[1];
		float w = detection[2];
		float h = detection[3];
		if(detType == 1){
			x = x - w / 2;
			y = y - h / 2;
		}else if(detType == 2){
			w = w - x;
			h = h - y;
		}
		float confidence = detection[4]; // 置信度

		Target t =  new Target(label,confidence,
				Math.max(0, (x-x0)/(w2-x0*2)),Math.max(0, (y-y0)/(h2-y0*2)),
				w/(w2-x0*2),h/(h2-y0*2));
		targets.add(t);
		return t;
	}

	public Target addTargetMinMax(String label,String confidence,String xMin,String yMin,String xMax,String yMax){
		float left = Float.parseFloat(xMin);
		float top = Float.parseFloat(yMin);
		return addTarget(label, Float.parseFloat(confidence), left, top, Float.parseFloat(xMax)-left, Float.parseFloat(yMax)-top);
	}

	public Target addTargetMinMax(String label,float confidence,int xMin,int yMin,int xMax,int yMax){
		float left = xMin*1f/width;
		float top = yMin*1f/height;
		return addTarget(label, confidence, left, top, (xMax-xMin)*1f/width, (yMax-yMin)*1f/height);
	}


	public Target addTarget(String label,float confidence,float left,float top,float width,float height){
		if(width + left > 1f)
			width = 1f - left;
		if(height + top > 1f)
			height = 1f - top;
		Target t = new Target(label,confidence,left,top,width,height);
		targets.add(t);
		return t;
	}

	public Target addTarget(String label,float confidence,int left,int top,int width,int height){
		return addTarget(label,confidence,left*1f/this.width,top*1f/this.height,width*1f/this.width,height*1f/this.height);
	}

	public Target addTarget(String label,float confidence,String points){
		String[] s = points.split(" ");
		float[][] f = new float[s.length/2][2];
		for(int i=0;i<f.length;i++){
			f[i][0] = Float.parseFloat(s[i*2]);
			f[i][1] = Float.parseFloat(s[i*2+1]);
		}
		return addTarget(label,confidence,f);
	}

	public Target addTarget(String label,float confidence,float[][] points){
		Target t = new Target();
		t.setLabel(label);
		t.setConfidence(confidence);
		t.setPoints(points);

		Rectangle2D r = t.getPointsBounds2D();
		t.setLeft((float) r.getX());
		t.setTop((float) r.getY());
		t.setWidth((float) r.getWidth());
		t.setHeight((float) r.getHeight());

		targets.add(t);
		return t;
	}

	public void addTargets(RecognitionRes t){
		if(this.width == 0 && t.getWidth() != 0){
			this.width = t.getWidth();
			this.height = t.getHeight();
		}
		this.targets.addAll(t.getTargets());
	}

	public void addTargets(List<Target> targets){
		this.targets.addAll(targets);
	}

	public BufferedImage getBufferedImage(){
		return ImgUtil.read(img);
	}

	public void setBufferedImage(BufferedImage bufferedImage) {
		this.img = bufferedImage;
		this.width = bufferedImage.getWidth();
		this.height = bufferedImage.getHeight();
	}

	public File getFile(){
		if(img == null || !(img instanceof File))
			return null;
		return (File) img;
	}

	public BufferedImage getTargetImage(Target target){
		Mat mat = OpenCVUtil.crop(img,target);
		BufferedImage bi = ImgUtil.read(mat);
		OpenCVUtil.release(mat);
		return bi;
	}

	//过滤
	public RecognitionRes filter(Predicate<Target> predicate){
		return clone(targets.stream().filter(predicate).collect(Collectors.toList()));
	}

	//排序
	public RecognitionRes sort(Comparator<Target> comparator){
		return clone(targets.stream()
				.sorted(comparator) // 降序排序
				.collect(Collectors.toList())
		);
	}

	public RecognitionRes find(Float confidence, String code){
		return filter(v-> (confidence == null || v.getConfidence() > confidence) && (Strings.isBlank(code) || code.equals(v.getLabel())));
	}

	public void nms(float iouThreshold){
		targets.sort((t1, t2) -> Float.compare(t2.confidence, t1.confidence));
		List<Target> result = new ArrayList<>();
		while (!targets.isEmpty()) {
			// 取出置信度最高的检测框
			Target current = targets.get(0);
			result.add(current);

			// 移除当前检测框
			targets.remove(0);

			// 遍历剩余的检测框，移除与当前框 IoU 超过阈值的框
			Iterator<Target> iterator = targets.iterator();
			while (iterator.hasNext()) {
				Target detection = iterator.next();
				float iou = current.getIoU(detection);
				if (iou > iouThreshold) {
					iterator.remove();
				}
			}
		}
		targets.addAll(result);
	}

	/**
	 * 模糊匹配
	 * @param label
	 * @return
	 */
	public RecognitionRes findLabel(String... label){
		if(label == null || label.length == 0)
			return this;
		return filter(v-> {
			for(String s : label){
				if(v.getLabel().contains(s))
					return true;
			}
			return false;
		});
	}

	/**
	 * 保留可信度以上的目标
	 */
	public RecognitionRes minConfidence(float confidence){
		return filter(v-> v.getConfidence() >= confidence);
	}

	//获取最高可信度的目标
	public RecognitionRes filterMaxTotalTargets(int total){
		return clone(targets.stream()
				.sorted((t1, t2) -> Float.compare(t2.confidence, t1.confidence)) // 降序排序
				.limit(total)
				.collect(Collectors.toList())
		);
	}

	/**
	 * 在参数区域内
	 */
	public RecognitionRes inRegion(Target... regions){
		return filter(v->{
			for(Target t:regions){
				if(t.contains(v.getBounds2D()))
					return true;
			}
			return false;
		});
	}

	/**
	 * 在参数区域内含相交
	 */
	public RecognitionRes intersects(Target... regions){
		return filter(v->{
			for(Target t:regions){
				if(v.intersects(t.getBounds2D()))
					return true;
			}
			return false;
		});
	}

	public List<List<Target>> getRow(Predicate<Target> predicate){
		List<Target> ts = targets;
		if(predicate != null)
			ts=targets.stream().filter(predicate).collect(Collectors.toList());
		return toRow(ts);
	}

	//转行
	public static List<List<Target>> toRow(List<Target> targets){
		// 计算每个矩形的中心y，并创建包含中心y和矩形的列表
		List<Target> pairs = new ArrayList<>();
		float totalHeight = 0;
		for (Target t : targets) {
			totalHeight += t.getHeight();
			pairs.add(t);
		}
		float avgHeight = totalHeight / targets.size();
		float threshold = avgHeight * 0.5f; // 阈值设为平均高度的一半

		// 按中心y排序
		pairs.sort(Comparator.comparingDouble(p -> p.getCenter()[1]));

		List<List<Target>> rows = new ArrayList<>();
		List<Target> currentRow = new ArrayList<>();
		currentRow.add(pairs.get(0));

		for (int i = 1; i < pairs.size(); i++) {
			float currentCenterY = pairs.get(i).getCenter()[1];
			float prevCenterY = pairs.get(i - 1).getCenter()[1];
			float diff = currentCenterY - prevCenterY;
			if (diff <= threshold) {
				currentRow.add(pairs.get(i));
			} else {
				rows.add(currentRow);
				currentRow = new ArrayList<>();
				currentRow.add(pairs.get(i));
			}
		}
		rows.add(currentRow); // 添加最后一行

		// 对每行按x坐标排序
		for (List<Target> row : rows) {
			row.sort(Comparator.comparingDouble(Target::getLeft));
		}
		return rows;
	}

	public List<List<Target>> getLineWithMinHeight(Predicate<Target> predicate,int lineCount,float minLineHeight,boolean leftToRight){
		List<Target> ts = targets;
		if(predicate != null)
			ts=targets.stream().filter(predicate).collect(Collectors.toList());
		return resToLineWithMinHeight(ts,lineCount,minLineHeight,leftToRight);
	}

	public static List<List<Target>> resToLineWithMinHeight(List<Target> resList,int lineCount,float minLineHeight,boolean leftToRight) {

		List<List<Target>> result = new ArrayList<>();
		if(resList.size() == 0)
			return result;

		//OptionalDouble average = resList.stream().mapToDouble(Target::getHeight).average();
		// resList长度小于等于lineCount，那就是每行一个
		if (resList.size() <= lineCount) {
			//每个元素按中心点y方向排序
			resList.sort(Comparator.comparing(v->v.getCenter()[1]));
			for(Target target : resList) {
				result.add(Collections.singletonList(target));
			}
			return result;
		}

		if(lineCount == 1){
			//按目标左侧点从左到右排序
			resList.sort(Comparator.comparing(Target::getLeft));
			result.add(resList);
			return result;
		}


		//lineCount至少为2
		// 将resList每个元素按中心点y方向排序
		resList.sort(Comparator.comparing(v->v.getCenter()[1]));

		//首次将resList分成2份并加入result
		List<List<Target>> tmp = new ArrayList<>();
		float d = getMaxDelta(resList, tmp);
		if (d <= minLineHeight) {//说明只能分一行
			result.add(resList);
		}else{
			result.addAll(tmp);
			while(result.size() < lineCount){
				int maxIndex = 0;
				float maxDelta = Integer.MIN_VALUE;
				List<List<Target>> maxOutput = null;
				for (int i = 0; i < result.size(); i++) {
					tmp = new ArrayList<>();
					d = getMaxDelta(result.get(i), tmp);
					if(d > maxDelta){
						maxDelta = d;
						maxIndex = i;
						maxOutput = tmp;
					}
				}
				if(maxDelta <= minLineHeight){//本次遍历，最大分行高度已经小于minLineHeight，不能再分行了
					break;
				}else{
					result.remove(maxIndex);
					result.addAll(maxIndex, maxOutput);
				}
			}
		}

		result.forEach(v->{
			v.sort(Comparator.comparing(Target::getLeft));
			if(!leftToRight)
				Collections.reverse(v);
		});

		return result;

	}

	/**
	 * 获取有序resList的最大差值,并把最大差值前后的元素分成两组，output返回
	 */
	public static float getMaxDelta(List<Target> resList,List<List<Target>> output) {
		if(output == null){
			output = new ArrayList<>();
		}else{
			output.clear();
		}
		float result = 0;
		if (resList.size() == 0) {

		} else if (resList.size() == 1) {
			output.add(Collections.singletonList(resList.get(0)));
		} else {
			Target topRes = resList.get(0);
			int maxIndex = 0;
			result = Integer.MIN_VALUE;
			for (int i = 0; i < resList.size() - 1; i++) {
				float me = resList.get(i).getCenter()[1] - topRes.getCenter()[1];
				float after = resList.get(i+1).getCenter()[1] - topRes.getCenter()[1];
				float delta = after - me;
				if (delta > result) {
					result = delta;
					maxIndex = i;
				}
			}
			output.add(resList.subList(0, maxIndex + 1));
			output.add(resList.subList(maxIndex + 1,resList.size()));
		}
		return result;
	}

	@Override
	public void render(HttpServletRequest req, HttpServletResponse resp, Object obj) throws Throwable {
		req.setAttribute("tagArr", Json.toJson(targets, JsonFormat.compact()));
		if(req.getAttribute("SRes") == null){
			String path = "";
			if(img instanceof String)
				path = (String) img;
			if(img instanceof File)
				path = FileUtil.toPath((File)img);
			SRes sRes = new SRes(path);
			req.setAttribute("SRes", sRes);
		}
		new JspView( "pc/base/view/img").render(req,resp,null);
	}

	@Override
	public String toString(){
		if(targets == null || targets.size() == 0)
			return "count:0";
		Map<String,Long> labelCount = targets.stream().collect(Collectors.groupingBy(Target::getLabel, Collectors.counting()));
		return labelCount.toString();
	}

	public TRecord toRecord(){
		TRecord rh = new TRecord();
		if(img instanceof String){
			rh.setFile((String) img);
		}else if(img instanceof File){
			rh.setFile((File) img);
		}
		rh.setRemark(getRemark());

		List<String> p = new ArrayList<>();
		for(Target t:getTargets()){
			p.add(t.toString());
		}
		rh.setTargets(String.join(";",p));
		rh.setVal(getValue());
		return rh;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public List<Target> getTargets() {
		return targets;
	}
	public List<Target> getTargets(Predicate<Target> predicate) {
		return targets.stream().filter(predicate).collect(Collectors.toList());
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}
}
