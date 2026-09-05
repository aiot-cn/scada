package org.aiot.lang.halcon;

public class HalconJavaNative {
    private static boolean load;
    private static final HalconJavaNative instance = new HalconJavaNative();

	static{
        try {
            System.loadLibrary("aiotHalconJavaNative"); // 加载JNI动态库
            load = true;
            System.out.println("aiotHalconJavaNative加载成功");
        }catch (UnsatisfiedLinkError e){
            System.out.println("aiotHalconJavaNative加载失败："+e.getMessage());
        }
    }

    public static HalconJavaNative getInstance(){
        if(!load)
            return null;
        return instance;
    }
	// 声明本地方法
    public native String getHelloMessage();
    public native void tranIntArray(int[][] array);
    
    public native boolean readImage(String path, long imageHobject);
    public native boolean getImageSize(long imageHobject, long widthHtuple, long heightHtuple);
    public native boolean createQRCodeModel(long modelHtuple);
    public native boolean clearQRCodeModel(long modelHtuple);
    public native boolean setQrCodeModelIntParam(long modelHtuple,String key,int param);
    public native boolean setQrCodeModelStringParam(long modelHtuple,String key,String param);
    public native boolean findQrCode(long imageHobject, long modelHtuple, long qrcodeXLDHobject, long qrcodeStringHtuple);
    public native boolean scaleImage(long srcImageHobject,long destImageHobject, int min, int max);
    public native boolean maxImage(long srcImageHobject,long destImageHobject);
    public native boolean zoomAndDecompose3Image(String path,long imageHobject,int zoomWidth,long imageRHobject,long imageGHobject,long imageBHobject);
    public native boolean writeImage(long imageHobject, String folder, String name);
    
    public native boolean pointArrayToContour(int[][] array,long contourHobject,long pointsRegionHobject);
    public native boolean fitContourToLine(long contourHobject,long regionLineHobject,long rowBeginHtuple,long colBeginHtuple,long rowEndHtuple,long colEndHtuple);
    public native boolean genRectangleByTwoPoints(long rectangleRegionHobject,int x1,int y1,int x2,int y2);
    public native boolean genCircleRegion(long circleRegionHobject,int x,int y,int radius);
    public native boolean dilationRegion(long srcRegionHobject,long destRegionHobject,int radius);
    public native boolean paintRegion(long srcImageHobject,long destImageHobject,long regionHobject,int r,int g,int b);
    public native boolean areaCenter(long regionHobject, long areaHtuple, long rowHtuple, long columnHtuple);
    public native boolean getImageRegionPoints(long imageHobject, long rowsHtuple, long colsHtuple);
    public native boolean getImageGrayValue(long imageHobject, long rowsHtuple, long colsHtuple, long valuesHtuple);
    public native boolean getImageRegionMeanGrayValue(long imageHobject,long regionHobject, long meanValueHtuple);
    public native boolean setImageGrayValue(long imageHobject, long rowsHtuple, long colsHtuple, long valuesHtuple);
    public native double calcLineDegByCenter(long row1Htuple,long col1Htuple,long row2Htuple,long col2Htuple,long rowCHtuple,long colCHtuple);
    public native double calcLineDeg(long row1Htuple,long col1Htuple,long row2Htuple,long col2Htuple);
    
    public native int htupleToInt(long htuple);
    public native int[] htupleToIntArray(long htuple);
    public native boolean intToHtuple(long htuple,int intValue);
    public native boolean intArrayToHtuple(long htuple,int[] intArray);
    public native double htupleToDouble(long htuple);
    public native boolean doubleToHtuple(long htuple,double doubleValue);
    public native String htupleToString(long htuple); 
    public static native long createHObject();    
    public static native long createHTuple();  
    public static native void clearHObject(long hobject);  
    public static native void clearHTuple(long htuple);  
    
}
