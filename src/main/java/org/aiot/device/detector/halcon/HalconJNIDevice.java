package org.aiot.device.detector.halcon;

import com.beust.jcommander.internal.Lists;
import org.aiot.device.BaseDevice;
import org.aiot.lang.annotation.AoReflect;
import org.aiot.lang.halcon.HObject;
import org.aiot.lang.halcon.HTuple;
import org.aiot.lang.halcon.HalconJavaNative;

import java.awt.*;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@AoReflect("HalconJNI设备")
public class HalconJNIDevice extends BaseDevice {
	HalconJavaNative halconJavaNative;
	
	@Override
	public void init(){
		halconJavaNative = HalconJavaNative.getInstance();
	}
	@AoReflect(value="sayHello")
	public String sayHello() {
		return halconJavaNative.getHelloMessage();
	}
	@AoReflect(value="tranIntArray")
	public void tranIntArray(int[][] array) {
		halconJavaNative.tranIntArray(array);
	}
	@AoReflect(value="获取int数组")
	public int[] getIntArray(String str) {
		return Arrays.stream(str.split("-"))
        .map(String::trim)
        .mapToInt(s -> {
        	return Integer.parseInt(s);
        })
        .toArray();
	}
	@AoReflect(value="图像分割和获取")
	public int[][][] zoomImageAndGetGrayValues(File imgFile,int zoomWidth) {
		long l0 = System.currentTimeMillis();
		HObject imageHobject = createHObject();
		HObject imageRHobject = createHObject();
		HObject imageGHobject = createHObject();
		HObject imageBHobject = createHObject();		
		zoomAndDecompose3Image(imgFile, imageHobject, zoomWidth, imageRHobject, imageGHobject, imageBHobject);
		HTuple rowsHtuple = createHTuple();
		HTuple colsHtuple = createHTuple();
		getImageRegionPoints(imageRHobject, rowsHtuple, colsHtuple);
		HTuple rValuesHtuple = createHTuple();
		getImageGrayValue(imageRHobject, rowsHtuple, colsHtuple, rValuesHtuple);
		HTuple gValuesHtuple = createHTuple();
		getImageGrayValue(imageGHobject, rowsHtuple, colsHtuple, gValuesHtuple);
		HTuple bValuesHtuple = createHTuple();
		getImageGrayValue(imageBHobject, rowsHtuple, colsHtuple, bValuesHtuple);
		System.out.println("耗时1：" + (System.currentTimeMillis() - l0) + "ms");
		int[] rValues = htupleToIntArray(rValuesHtuple);
		int[] gValues = htupleToIntArray(gValuesHtuple);
		int[] bValues = htupleToIntArray(bValuesHtuple);
		int lie = zoomWidth;
		int hang = rValues.length/zoomWidth;
		
		int[][] r = convertTo2D(rValues, hang, lie);
		int[][] g = convertTo2D(gValues, hang, lie);
		int[][] b = convertTo2D(bValues, hang, lie);
		clearObject(imageHobject,imageRHobject,imageGHobject,imageBHobject,rowsHtuple,colsHtuple,rValuesHtuple,gValuesHtuple,bValuesHtuple);
		System.out.println("耗时2：" + (System.currentTimeMillis() - l0) + "ms");
		return new int[][][]{r, g, b};
		
	}
	
	private int[][] convertTo2D(int[] array, int rows, int cols) {
        // 处理无效输入
        if (array == null || rows < 0 || cols < 0 || rows * cols != array.length) {
            return null;
        }
        
        int[][] result = new int[rows][cols];
        for (int i = 0; i < rows; i++) {
            // 计算当前行在一维数组中的起始位置
            int rowStart = i * cols;
            for (int j = 0; j < cols; j++) {
                result[i][j] = array[rowStart + j];
            }
        }
        return result;
    }
	//x1-y1;x2-y2;x3-y3
	@AoReflect(value="字符串转PointList")
	public List<Point> stringToPointList(String string) {
		String[] array = string.split(";");
		List<Point> result = Lists.newArrayList();
		for (int i = 0; i < array.length; i++) {
			String s = array[i];
			String[] item = s.split("-");
			String x = item[0];
			String y = item[1];
			result.add(new Point(Integer.parseInt(x), Integer.parseInt(y)));
		}
		return result;
	}
	@AoReflect(value="paintRegion")
	public boolean paintRegion(HObject srcImageHobject,HObject destImageHobject,HObject regionHobject,int r, int g, int b) {
		return halconJavaNative.paintRegion(srcImageHobject.getNativeHandle(), destImageHobject.getNativeHandle(), regionHobject.getNativeHandle(), r, g, b);
	}
	@AoReflect(value="区域膨胀")
	public boolean dilationRegion(HObject srcRegionHobject,HObject destRegionHobject,int radius) {
		return halconJavaNative.dilationRegion(srcRegionHobject.getNativeHandle(), destRegionHobject.getNativeHandle(), radius);
	}
	@AoReflect(value="获取区域的面积和中心")
	public boolean areaCenter(HObject regionHobject,HTuple areaHtuple,HTuple rowHtuple,HTuple columnHtuple) {
		return halconJavaNative.areaCenter(regionHobject.getNativeHandle(), areaHtuple.getNativeHandle(), rowHtuple.getNativeHandle(), columnHtuple.getNativeHandle());
	}
	@AoReflect(value="获取图像有效区域")
	public boolean getImageRegionPoints(HObject imageHobject,HTuple rowsHtuple,HTuple colsHtuple) {
		return halconJavaNative.getImageRegionPoints(imageHobject.getNativeHandle(), rowsHtuple.getNativeHandle(), colsHtuple.getNativeHandle());
	}
	@AoReflect(value="获取图像灰度值")
	public boolean getImageGrayValue(HObject imageHobject,HTuple rowsHtuple,HTuple colsHtuple,HTuple valuesHtuple) {
		return halconJavaNative.getImageGrayValue(imageHobject.getNativeHandle(), rowsHtuple.getNativeHandle(), colsHtuple.getNativeHandle(), valuesHtuple.getNativeHandle());
	}
	@AoReflect(value="获取区域平均灰度值")
	public boolean getImageRegionGrayValue(HObject imageHobject,HObject regionHobject,HTuple meanValueHtuple) {
		return halconJavaNative.getImageRegionMeanGrayValue(imageHobject.getNativeHandle(), regionHobject.getNativeHandle(), meanValueHtuple.getNativeHandle());
	}
	@AoReflect(value="设置图像灰度值")
	public boolean setImageGrayValue(HObject imageHobject,HTuple rowsHtuple,HTuple colsHtuple,HTuple valuesHtuple) {
		return halconJavaNative.setImageGrayValue(imageHobject.getNativeHandle(), rowsHtuple.getNativeHandle(), colsHtuple.getNativeHandle(), valuesHtuple.getNativeHandle());
	}
	@AoReflect(value="点集转轮廓")
	public boolean pointArrayToRegion(List<Point> pointList,HObject contourHobject,HObject pointsRegionHobject) {
		int[][] array = new int[pointList.size()][2];
		for (int i = 0; i < pointList.size(); i++) {
			array[i][0] = (int)pointList.get(i).getX();
			array[i][1] = (int)pointList.get(i).getY();			
		}
		System.out.println("点集：" + pointList.stream().map(v->v.getX()+","+v.getY()).collect(Collectors.joining(";")));
		return halconJavaNative.pointArrayToContour(array, contourHobject.getNativeHandle(),pointsRegionHobject.getNativeHandle());
	}
	@AoReflect(value="创建矩形区域")
	public boolean createRectangleRegion(HObject rectangleRegionHobject,int x1,int y1,int x2,int y2) {
		return halconJavaNative.genRectangleByTwoPoints(rectangleRegionHobject.getNativeHandle(), x1, y1, x2, y2);
	}
	@AoReflect(value="创建圆形区域")
	public boolean createCircleRegion(HObject circleRegionHobject,int x,int y,int radius) {
		return halconJavaNative.genCircleRegion(circleRegionHobject.getNativeHandle(), x, y, radius);
	}
	@AoReflect(value="轮廓拟合直线")
	public boolean fitRegionToLine(HObject contourHobject,HObject regionLineHobject,HTuple rowBeginHtuple,HTuple colBeginHtuple,HTuple rowEndHtuple,HTuple colEndHtuple) {
		return halconJavaNative.fitContourToLine(contourHobject.getNativeHandle(), regionLineHobject.getNativeHandle(), rowBeginHtuple.getNativeHandle(), colBeginHtuple.getNativeHandle(), rowEndHtuple.getNativeHandle(), colEndHtuple.getNativeHandle());
	}
	@AoReflect(value="通过圆心计算直线角度(-180~180)")
	public double calcLineDegByCenter(HTuple row1Htuple, HTuple col1Htuple, HTuple row2Htuple, HTuple col2Htuple, HTuple rowCHtuple, HTuple colCHtuple) {
		return halconJavaNative.calcLineDegByCenter(row1Htuple.getNativeHandle(), col1Htuple.getNativeHandle(), row2Htuple.getNativeHandle(), col2Htuple.getNativeHandle(), rowCHtuple.getNativeHandle(), colCHtuple.getNativeHandle());
	}
	@AoReflect(value="计算直线角度(-180~180)")
	public double calcLineDeg(HTuple row1Htuple, HTuple col1Htuple, HTuple row2Htuple, HTuple col2Htuple) {
		return halconJavaNative.calcLineDeg(row1Htuple.getNativeHandle(), col1Htuple.getNativeHandle(), row2Htuple.getNativeHandle(), col2Htuple.getNativeHandle());
	}
	@AoReflect(value="图片读取")
	public boolean readImage(File imgFile,HObject imageHobject) {
		return halconJavaNative.readImage(imgFile.getAbsolutePath(),imageHobject.getNativeHandle());
	}
	@AoReflect(value="缩放图像(RGB)")
	public boolean zoomAndDecompose3Image(File imgFile,HObject imageHobject,int zoomWidth,HObject imageRHobject,HObject imageGHobject,HObject imageBHobject) {
		return halconJavaNative.zoomAndDecompose3Image(imgFile.getAbsolutePath(),imageHobject.getNativeHandle(), zoomWidth, imageRHobject.getNativeHandle(), imageGHobject.getNativeHandle(), imageBHobject.getNativeHandle());
	}
	@AoReflect(value="图像写入(jpg)")
	public boolean writeImage(HObject imageHobject,File file) {
		String folderPath = file.getParent();
		String name = file.getName().substring(0,file.getName().lastIndexOf("."));//名字最后一个点之前的加jpg为最终文件名
		return halconJavaNative.writeImage(imageHobject.getNativeHandle(), folderPath, name);
	}
	@AoReflect(value="图像增强")
	public boolean scaleImage(HObject srcImageHobject,HObject destImageHobject,int minGrayValue,int maxGrayValue) {
		return halconJavaNative.scaleImage(srcImageHobject.getNativeHandle(), destImageHobject.getNativeHandle(), minGrayValue, maxGrayValue);
	}
	@AoReflect(value="图像最大值叠加")
	public boolean maxImage(HObject srcImageHobject,HObject destImageHobject) {
		return halconJavaNative.maxImage(srcImageHobject.getNativeHandle(), destImageHobject.getNativeHandle());
	}
	@AoReflect(value="创建二维码识别模型")
	public boolean createQRCodeModel(HTuple modelHtuple) {
		return halconJavaNative.createQRCodeModel(modelHtuple.getNativeHandle());
	}
	@AoReflect(value="释放二维码识别模型")
	public boolean clearQRCodeModel(HTuple modelHtuple) {
		return halconJavaNative.clearQRCodeModel(modelHtuple.getNativeHandle());
	}
	@AoReflect(value="设置二维码参数(int)")
	public boolean setQrCodeModelIntParam(HTuple modelHtuple,String key,int param) {
		return halconJavaNative.setQrCodeModelIntParam(modelHtuple.getNativeHandle(), key, param);
	}
	@AoReflect(value="设置二维码参数(string)")
	public boolean setQrCodeModelStringParam(HTuple modelHtuple,String key,String param) {
		return halconJavaNative.setQrCodeModelStringParam(modelHtuple.getNativeHandle(), key, param);
	}
	@AoReflect(value="二维码识别")
	public boolean findQrCode(HObject imageHobject, HTuple modelHtuple, HObject qrcodeXLDHobject,HTuple qrcodeStringHtuple) {
		return halconJavaNative.findQrCode(imageHobject.getNativeHandle(), modelHtuple.getNativeHandle(), qrcodeXLDHobject.getNativeHandle(), qrcodeStringHtuple.getNativeHandle());
	}
	@AoReflect(value="获取图片尺寸")
	public boolean getImageSize(HObject imageHobject, HTuple widthHtuple, HTuple heightHtuple) {
		return halconJavaNative.getImageSize(imageHobject.getNativeHandle(), widthHtuple.getNativeHandle(), heightHtuple.getNativeHandle());
	}
	@AoReflect(value="HTuple转Int")
	public int htupleToInt(HTuple htuple) {
		return halconJavaNative.htupleToInt(htuple.getNativeHandle());
	}
	//该方法动作组不要调，会导致浏览器崩溃
	@AoReflect(value="HTuple转Int数组")
	public int[] htupleToIntArray(HTuple htuple) {
		return halconJavaNative.htupleToIntArray(htuple.getNativeHandle());
	}
	@AoReflect(value="int转HTuple")
	public boolean intToHtuple(HTuple htuple,int intValue) {
		return halconJavaNative.intToHtuple(htuple.getNativeHandle(), intValue);
	}
	@AoReflect(value="int数组转HTuple")
	public boolean intArrayToHtuple(HTuple htuple,int[] intArray) {
		return halconJavaNative.intArrayToHtuple(htuple.getNativeHandle(), intArray);
	}
	@AoReflect(value="HTuple转Double")
	public double htupleToDouble(HTuple htuple) {
		return halconJavaNative.htupleToDouble(htuple.getNativeHandle());
	}
	@AoReflect(value="double转HTuple")
	public boolean doubleToHtuple(HTuple htuple,double doubleValue) {
		return halconJavaNative.doubleToHtuple(htuple.getNativeHandle(), doubleValue);
	}
	@AoReflect(value="HTuple转String")
	public String htupleToString(HTuple htuple) {
		return halconJavaNative.htupleToString(htuple.getNativeHandle());
	}
	@AoReflect(value="创建HObject")
	public HObject createHObject() {
		long l = HalconJavaNative.createHObject();
		return new HObject(l);
	}	
	@AoReflect(value="创建HTuple")
	public HTuple createHTuple() {
		long l = HalconJavaNative.createHTuple();
		return new HTuple(l);
	}	
	@AoReflect(value="释放对象")
	public void clearObject(Object... objs) {
		for(Object object : objs){
			if(object instanceof HObject){
				((HObject)object).close();
			}else if(object instanceof HTuple){
				((HTuple)object).close();
			}else{
				sendSocket("非预期：未知的对象类型：" + object, true);
			}
		}
	}
}
