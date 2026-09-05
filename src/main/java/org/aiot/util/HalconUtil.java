package org.aiot.util;

import org.aiot.lang.halcon.HObject;
import org.aiot.lang.halcon.HTuple;
import org.aiot.lang.halcon.HalconJavaNative;
import org.aiot.model.enums.ConfigEnum;
import org.aiot.model.enums.PathEnum;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class HalconUtil {

    //点集拟合直线 [size,2]
    public static float[] pointsToLine(int[] pointsXY,File imgFile) {
        if(imgFile == null){
            imgFile = PathEnum.temp.getFile("HalconInit.jpg");
            if(!imgFile.isFile()){
                try {
                    ImageIO.write(new BufferedImage(2560,1440,BufferedImage.TYPE_INT_RGB), "jpg", imgFile);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
		HalconJavaNative halcon = HalconJavaNative.getInstance();
        if(halcon == null)
            return null;

        float[] line;
        int size = pointsXY.length/2;
        int[][] points = new int[size][2];
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < size; i++) {
            points[i][0] = pointsXY[i * 2];     // x 坐标
            points[i][1] = pointsXY[i * 2 + 1]; // y 坐标
            if(sb.length() == 0){
            	sb.append(points[i][0]+"," + points[i][1]);
            }else{
            	sb.append(";"+points[i][0]+"," + points[i][1]);
            }
        }
        if(ConfigEnum.debug.getBoolean()){
        	System.out.println("点集：" + sb);
        }
        try(HObject contourHobject = new HObject();
            HObject pointsRegionHobject = new HObject();
            HTuple rowBeginHtuple = new HTuple();
            HTuple colBeginHtuple = new HTuple();
            HTuple rowEndHtuple  = new HTuple();
            HTuple colEndHtuple = new HTuple();
            HObject imageHobject = new HObject()){

        	halcon.readImage(imgFile.getAbsolutePath(), imageHobject.getNativeHandle());
            //点集转轮廓
            halcon.pointArrayToContour(points, contourHobject.getNativeHandle(),pointsRegionHobject.getNativeHandle());
            halcon.fitContourToLine(contourHobject.getNativeHandle(), pointsRegionHobject.getNativeHandle(), rowBeginHtuple.getNativeHandle(), colBeginHtuple.getNativeHandle(), rowEndHtuple.getNativeHandle(), colEndHtuple.getNativeHandle());

            double x1 = halcon.htupleToDouble(colBeginHtuple.getNativeHandle());
            double y1 = halcon.htupleToDouble(rowBeginHtuple.getNativeHandle());
            double x2 = halcon.htupleToDouble(colEndHtuple.getNativeHandle());
            double y2 = halcon.htupleToDouble(rowEndHtuple.getNativeHandle());
            line = new float[]{(float) x1, (float) y1, (float) x2,(float) y2};
        }

        return line;
    }
}
