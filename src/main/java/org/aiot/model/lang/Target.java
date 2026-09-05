package org.aiot.model.lang;

import org.nutz.lang.Lang;
import org.nutz.lang.Strings;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.NoSuchElementException;

/**
 * 统一为归一化点坐标
 */
public class Target implements Shape{
	String label;
	float confidence;
	float left; //宽度为0时,表示为相对偏移量
	float top;
	float width;
	float height;
	float angel; //角度
	//List有类型擦除问题，对数据自动转换不友好
	float[][] points = new float[0][];
	float[] line;

	float value;
	Object remark;

	public Target(){

	}

	public Target(String str){
		String[] s = str.split("[,\\s]");
		if(s.length > 1){
			if(Strings.isNotBlank(s[0]))
				label = s[0];
			confidence = Float.parseFloat(s[1]);
		}
		if(s.length > 7){
			s = str.substring(s[0].length() + s[1].length()+2).split(",");
			points = new float[s.length][];
			for(int i=0;i<s.length;i++){
				String[] s1 = s[i].split("\\s");
				points[i] = new float[]{
						Float.parseFloat(s1[0]),
						Float.parseFloat(s1[1])
				};
			}
		}else if(s.length > 5){
			left = Float.parseFloat(s[2]);
			top = Float.parseFloat(s[3]);
			width = Float.parseFloat(s[4]);
			height = Float.parseFloat(s[5]);
			if(s.length > 6)
				angel = Float.parseFloat(s[6]);
		}
	}

	public Target(String str,int width,int height){
		String[] s = str.split("[,\\s]");
		float[] f = new float[s.length];
		for(int i=1;i<s.length;i++){
			f[i] = Float.parseFloat(s[i]);
		}
		this.label = s[0];
		this.confidence = f[1];
		this.left 	= f[2]/width;
		this.top 	= f[3]/height;
		this.width 	= f[4]/width;
		this.height = f[5]/height;

		if(s.length > 6)
			this.angel = f[6];
	}



	public Target(String label,float confidence){
		this.label = label;
		this.confidence = confidence;
	}

	public Target(float left,float top,float width,float height){
		this.left = left;
		this.top = top;
		this.width = width;
		this.height = height;
	}

	public Target(String label,float confidence,float left,float top,float width,float height){
		this.label = label;
		this.confidence = confidence;
		this.left = left;
		this.top = top;
		this.width = width;
		this.height = height;
	}

	public void addPoint(float[] point){
		// 扩容数组
		float[][] newPoints = new float[points.length + 1][];

		// 复制原有数据
		System.arraycopy(points, 0, newPoints, 0, points.length);

		// 添加新点（克隆以防外部修改）
		newPoints[points.length] = point.clone();

		// 替换引用
		points = newPoints;
	}

	//越界
	public boolean isOutside(){
		if(width > 0)
			return left < 0 || left > 1 || top < 0 || top > 1 || (left + width) > 1 || (top + height) > 1;
		for(float[] f : points){
			if(f[0] < 0 || f[0] > 1 || f[1] < 0 || f[1] > 1)
				return true;
		}
		return false;
	}

	//归一化
	public void normalize(int width,int height){
		this.left = this.left / width;
		this.top = this.top / height;
		this.width = this.width / width;
		this.height = this.height / height;
	}

	/**
	 * 展开点到基本矩形
	 */
	public Target expandPoints(){
		Rectangle2D rec = getPointsBounds2D();
		this.left   = (float) rec.getMinX();
		this.top    = (float) rec.getMinY();
		this.width  = (float) rec.getWidth();
		this.height = (float) rec.getHeight();
		return this;
	}

	/**
	 * 强制矩形在区域内
	 */
	public void clampRect(){
		if(left < 0)
			left = 0;
		if(top < 0)
			top = 0;
		if(left + width > 1)
			width = 1 - left;
		if(top + height > 1)
			height = 1 - top;
	}

	@Override
	public String toString() {
		String s = label+","+confidence;
		if(width == 0 && points.length > 0){
			for(int i=0;i<points.length;i++){
				float[] p = points[i];
				s += "," + p[0] + " " + p[1];
			}
		}else{
			s += ","+left+","+top+","+width+","+height;
			if(angel > 0)
				s += ","+angel;
		}
		return s;
	}

	@Override
	public Rectangle getBounds() {
		return new Rectangle(
				(int)(left * 10000),
				(int)(top * 10000),
				(int)(width * 10000),
				(int)(height * 10000)
		);
	}

	@Override
	public Rectangle2D getBounds2D() {
		return new Rectangle2D.Float(left,top,width,height);
	}

	public Rectangle2D getPointsBounds2D(){
		float minX = Float.MAX_VALUE;
		float maxX = Float.MIN_VALUE;
		float minY = Float.MAX_VALUE;
		float maxY = Float.MIN_VALUE;

		for (float[] point : points) {
			if (point[0] < minX) minX = point[0];
			if (point[0] > maxX) maxX = point[0];
			if (point[1] < minY) minY = point[1];
			if (point[1] > maxY) maxY = point[1];
		}

		return new Rectangle2D.Float(minX,minY,maxX-minX,maxY-minY);
	}

	@Override
	public boolean contains(double x, double y) {
		return contains(new Point2D.Double(x,y));
	}

	@Override
	public boolean contains(double x, double y, double w, double h) {
		return contains(new Rectangle2D.Double(x,y,w,h));
	}

	@Override
	public boolean contains(Point2D p) {
		return getBounds2D().contains(p);
	}

	@Override
	public boolean contains(Rectangle2D r) {
		return getBounds2D().contains(r);
	}

	@Override
	public boolean intersects(double x, double y, double w, double h) {
		return intersects(new Rectangle2D.Double(x,y,w,h));
	}

	/**
	 * 当前对象（this）是否与参数矩形 r 相交
	 */
	@Override
	public boolean intersects(Rectangle2D r) {
		return getBounds2D().intersects(r);
	}


	@Override
	public PathIterator getPathIterator(AffineTransform at) {
		return new RectPathIterator(this, at);
	}

	@Override
	public PathIterator getPathIterator(AffineTransform at, double flatness) {
		// 对于矩形，平坦度参数可以忽略，因为矩形已经是平坦的
		return getPathIterator(at);
	}

	//获取形状中心点
	public float[] getCenter(){
		return new float[]{
				left + width  / 2,
				top  + height / 2
		};
	}

	//获取点集中心点
	public float[] getPointsCenter(){
		float x1 = points[0][0];
		float y1 = points[0][1];
		float x2 = points[1][0];
		float y2 = points[1][1];
		float x3 = points[2][0];
		float y3 = points[2][1];
		// 检查三点是否共线
		float area = x1 * (y2 - y3) + x2 * (y3 - y1) + x3 * (y1 - y2);
		if (Math.abs(area) < 1e-10) {
			return null;
		}

		// 使用几何公式计算圆心
		float D = 2 * area;

		float Ux = ((x1*x1 + y1*y1) * (y2 - y3) +
				(x2*x2 + y2*y2) * (y3 - y1) +
				(x3*x3 + y3*y3) * (y1 - y2)) / D;

		float Uy = ((x1*x1 + y1*y1) * (x3 - x2) +
				(x2*x2 + y2*y2) * (x1 - x3) +
				(x3*x3 + y3*y3) * (x2 - x1)) / D;

		return new float[]{Ux,Uy};
	}

	/**
	 * 获取顶点
	 */
	public float[][] getVertex(){
		float[][] f = new float[4][2];
		f[0] = new float[]{left,top};
		f[1] = new float[]{left + width,top};
		f[2] = new float[]{left + width,top + height};
		f[3] = new float[]{left,top + height};
		return f;
	}

	public float[][] getVertex(int width,int height){
		float[][] vertex = getVertex();
		for (float[] v : vertex) {
			v[0] = v[0] * width;
			v[1] = v[1] * height;
		}
		return vertex;
	}

	/**
	 * 获取相交面积
	 */
	public float getIntersectArea(Target target){
		Rectangle2D _this = getBounds2D();
		Rectangle2D t2 = target.getBounds2D();
		if(!_this.intersects(t2))
			return 0;

		// 计算相交区域
		Rectangle2D intersection = _this.createIntersection(t2);
		return (float) (intersection.getWidth() * intersection.getHeight());
	}

	/**
	 * 获取相交面积占比
	 */
	public float getIoU(Target target){
		return getIntersectArea(target) / (width * height);
	}

	/**
	 * 判断两个Target（目标检测）是否相同
	 */
	public boolean targetEqual(Target another) {
		return Lang.equals(this.label, another.label) && Lang.equals(this.confidence, another.confidence)
				&& Lang.equals(this.left, another.left) && Lang.equals(this.top, another.top)
				&& Lang.equals(this.width, another.width) && Lang.equals(this.height, another.height)
				&& Lang.equals(this.angel, another.angel);
	}

	public Point2D getPoint2D(int index){
		return new Point2D.Float(points[index][0],points[index][1]);
	}

	//两点间的距离
	public float pointDistance(int p1,int p2){
		return (float) getPoint2D(p1).distance(getPoint2D(p2));
	}

	//两点间的中点
	public Point2D getPointMid(int p1,int p2){
		float cx = (points[p1][0] + points[p2][0]) / 2;
		float cy = (points[p1][1] + points[p2][1]) / 2;
		return new Point2D.Float(cx,cy);
	}

	//获取P1-P2与水平线的夹角
	public float getPointAngle(int p1,int p2){
		return calcAngle(points[p1][0],points[p1][1],points[p1][0],points[p2][1]);
	}

	//获取线与水平线的夹角
	public float getLineAngle(){
		return calcAngle(line[0],line[1],line[2],line[3]);
	}

	//两点间的角度 水平左侧为0
	public static float calcAngle(float x1,float y1,float x2,float y2) {
		float angleRadians = (float) Math.atan2(y1-y2, x1-x2);
		float angleDegrees = (float) (angleRadians * (180 / Math.PI));
		return (angleDegrees + 360) % 360; // 确保非负数
	}

	//缩放形状
	public void zoomRect(float multiple){
		float[] c = getCenter();
		width = width * multiple;
		height = height * multiple;
		left = c[0] - width / 2;
		top = c[1] - height / 2;
	}

	public float getX2(){
		return left + width;
	}

	public float getY2(){
		return top + height;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public float getConfidence() {
		return confidence;
	}

	public void setConfidence(float confidence) {
		this.confidence = confidence;
	}

	public float getLeft() {
		return left;
	}

	public void setLeft(float left) {
		this.left = left;
	}

	public float getTop() {
		return top;
	}

	public void setTop(float top) {
		this.top = top;
	}

	public float getWidth() {
		return width;
	}

	public void setWidth(float width) {
		this.width = width;
	}

	public float getHeight() {
		return height;
	}

	public void setHeight(float height) {
		this.height = height;
	}

	public float[][] getPoints() {
		return points;
	}

	public void setPoints(float[][] points) {
		this.points = points;
	}

	public float getValue() {
		return value;
	}

	public void setValue(float value) {
		this.value = value;
	}

	public Object getRemark() {
		return remark;
	}

	public void setRemark(Object remark) {
		this.remark = remark;
	}

	public float[] getLine() {
		return line;
	}

	public void setLine(float[] line) {
		this.line = line;
	}

	public float getAngel() {
		return angel;
	}

	public void setAngel(float angel) {
		this.angel = angel;
	}

	// 矩形路径迭代器实现
	private static class RectPathIterator implements PathIterator {
		private final Target rectangle;
		private final AffineTransform transform;
		private int index;

		// 矩形路径的线段类型序列
		private static final int[] SEGMENT_TYPES = {
				PathIterator.SEG_MOVETO,
				PathIterator.SEG_LINETO,
				PathIterator.SEG_LINETO,
				PathIterator.SEG_LINETO,
				PathIterator.SEG_CLOSE
		};

		public RectPathIterator(Target rect, AffineTransform at) {
			this.rectangle = rect;
			this.transform = at;
			this.index = 0;
		}

		@Override
		public int getWindingRule() {
			return PathIterator.WIND_NON_ZERO;
		}

		@Override
		public boolean isDone() {
			return index >= SEGMENT_TYPES.length;
		}

		@Override
		public void next() {
			if (!isDone()) {
				index++;
			}
		}

		@Override
		public int currentSegment(float[] coords) {
			if (isDone()) {
				throw new NoSuchElementException("rect iterator out of bounds");
			}

			if (index == 4) { // CLOSE 段
				return PathIterator.SEG_CLOSE;
			}

			// 计算当前点的坐标
			double x = 0, y = 0;
			switch (index) {
				case 0: // 左上角 (MOVETO)
					x = rectangle.left;
					y = rectangle.top;
					break;
				case 1: // 右上角 (LINETO)
					x = rectangle.left + rectangle.width;
					y = rectangle.top;
					break;
				case 2: // 右下角 (LINETO)
					x = rectangle.left + rectangle.width;
					y = rectangle.top + rectangle.height;
					break;
				case 3: // 左下角 (LINETO)
					x = rectangle.left;
					y = rectangle.top + rectangle.height;
					break;
			}

			// 应用变换（如果有）
			if (transform != null) {
				double[] pt = new double[]{x, y};
				transform.transform(pt, 0, pt, 0, 1);
				coords[0] = (float) pt[0];
				coords[1] = (float) pt[1];
			} else {
				coords[0] = (float) x;
				coords[1] = (float) y;
			}

			return SEGMENT_TYPES[index];
		}

		@Override
		public int currentSegment(double[] coords) {
			if (isDone()) {
				throw new NoSuchElementException("rect iterator out of bounds");
			}

			if (index == 4) { // CLOSE 段
				return PathIterator.SEG_CLOSE;
			}

			// 计算当前点的坐标
			double x = 0, y = 0;
			switch (index) {
				case 0: // 左上角 (MOVETO)
					x = rectangle.left;
					y = rectangle.top;
					break;
				case 1: // 右上角 (LINETO)
					x = rectangle.left + rectangle.width;
					y = rectangle.top;
					break;
				case 2: // 右下角 (LINETO)
					x = rectangle.left + rectangle.width;
					y = rectangle.top + rectangle.height;
					break;
				case 3: // 左下角 (LINETO)
					x = rectangle.left;
					y = rectangle.top + rectangle.height;
					break;
			}

			// 应用变换（如果有）
			if (transform != null) {
				transform.transform(new double[]{x, y}, 0, coords, 0, 1);
			} else {
				coords[0] = x;
				coords[1] = y;
			}

			return SEGMENT_TYPES[index];
		}
	}
}