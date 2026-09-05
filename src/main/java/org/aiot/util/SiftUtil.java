package org.aiot.util;

import org.aiot.model.enums.PathEnum;
import org.aiot.model.lang.RecognitionRes;
import org.aiot.model.lang.Target;
import org.opencv.calib3d.Calib3d;
import org.opencv.core.*;
import org.opencv.features2d.DescriptorMatcher;
import org.opencv.features2d.Features2d;
import org.opencv.features2d.SIFT;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class SiftUtil {

    public static RecognitionRes featureTarget(RecognitionRes templateRes, Object targetImg){
        return featureTarget(templateRes, targetImg, new SiftHint());
    }
    public static RecognitionRes featureTarget(RecognitionRes templateRes, Object targetImg, SiftHint hint){
        RecognitionRes res  = new RecognitionRes(targetImg);
        Mat img1 = OpenCVUtil.read(templateRes.getImg(),Imgcodecs.IMREAD_GRAYSCALE);
        Mat img2 = OpenCVUtil.read(targetImg,Imgcodecs.IMREAD_GRAYSCALE);

        Mat gray1 = OpenCVUtil.resizeToTargetHeight(img1, hint.imgHeight);
        Mat gray2 = OpenCVUtil.resizeToTargetHeight(img2, hint.imgHeight);

        int width = gray1.width(),height = gray1.height();

        SIFT sift = SIFT.create(0, 3,
                    hint.contrastThreshold , hint.edgeThreshold,
                    1.6);

        // 检测关键点和计算描述子
        MatOfKeyPoint keypoints1 = new MatOfKeyPoint();
        MatOfKeyPoint keypoints2 = new MatOfKeyPoint();
        Mat descriptors1 = new Mat();
        Mat descriptors2 = new Mat();

        List<Target> maskTargets = templateRes.getTargets(v -> "mask".equals(v.getLabel()));
        Mat mask1 = maskTargets.size() > 0 ? Mat.zeros(gray1.size(), CvType.CV_8UC1) : new Mat();
        maskTargets.forEach(target -> {
            Imgproc.rectangle(mask1,
                    new Point(target.getLeft() * width, target.getTop() * height),
                    new Point(target.getX2() * width, target.getY2() * height),
                    new Scalar(255), -1);
        });

        Mat mask2 = new Mat();
        sift.detectAndCompute(gray1, mask1, keypoints1, descriptors1);
        sift.detectAndCompute(gray2, mask2, keypoints2, descriptors2);

        KeyPoint[] kp1 = keypoints1.toArray();
        KeyPoint[] kp2 = keypoints2.toArray();

        DescriptorMatcher matcher = DescriptorMatcher.create(hint.desMatcherType);

        List<MatOfDMatch> knnMatches = new ArrayList<>();
        matcher.knnMatch(descriptors1, descriptors2, knnMatches, 2); // k=2

        //筛选优质匹配
        List<DMatch> goodMatches = new ArrayList<>();
        List<Double> dxList = new ArrayList<>();
        List<Double> dyList = new ArrayList<>();
        for (MatOfDMatch knnMatch : knnMatches) {
            DMatch[] matches = knnMatch.toArray();
            if (matches.length < 2)
                continue;
            DMatch m0 = matches[0];
            //Lowe's Ratio Test  最近邻（最佳）匹配明显优于次近邻（第二佳）匹配
            if (m0.distance < hint.loweRatioThresh * matches[1].distance) {
                Point p1 = kp1[m0.queryIdx].pt;
                Point p2 = kp2[m0.trainIdx].pt;
                double dx = (p1.x - p2.x)/gray1.width();
                double dy = (p1.y - p2.y)/gray1.height();
                if(Math.abs(dx) <= hint.xRatioThresh && Math.abs(dy) <= hint.yRatioThresh){
                    dxList.add(dx);
                    dyList.add(dy);
                    goodMatches.add(m0);
                }
            }
        }
        if(hint.xRatioThresh < 1 || hint.yRatioThresh < 1){
            Collections.sort(dxList);
            Collections.sort(dyList);
            double dxc = dxList.get(dxList.size()/2);
            double dyc = dyList.get(dyList.size()/2);
            goodMatches = goodMatches.stream().filter(v->{
                Point p1 = kp1[v.queryIdx].pt;
                Point p2 = kp2[v.trainIdx].pt;
                double dx = (p1.x - p2.x)/gray1.width() - dxc;
                double dy = (p1.y - p2.y)/gray1.height() - dyc;
                return (hint.xRatioThresh >= 1 || Math.abs(dx) < 0.1) && (hint.yRatioThresh >= 1 || Math.abs(dy) < 0.1);
            }).collect(Collectors.toList());
        }
        res.setRemark("模板:" + kp1.length +
                      " 目标:" + kp2.length +
                      " 匹配:" + goodMatches.size());
        try {
            // 绘制匹配结果
            if(hint.drawMatches > 0){
                Mat matchOutput = new Mat();
                MatOfDMatch goodMatchesMat = new MatOfDMatch();
                goodMatchesMat.fromList(goodMatches);
                if(hint.drawMatches == 1){
                    Features2d.drawMatches(gray1, keypoints1, gray2, keypoints2,
                            goodMatchesMat, matchOutput,
                            Scalar.all(-1), Scalar.all(-1),
                            new MatOfByte(), Features2d.DrawMatchesFlags_NOT_DRAW_SINGLE_POINTS);
                }else{
                    drawMatchesVertical(gray1, keypoints1, gray2, keypoints2,
                            goodMatches, matchOutput);
                }

                File siftMatches = PathEnum.temp.getFile("siftMatches.jpg");
                res.setImg(siftMatches);
                Imgcodecs.imwrite(siftMatches.getAbsolutePath(),matchOutput);
                OpenCVUtil.release(matchOutput);
                return res;
            }

            // 估计单应性矩阵并绘制模板在场景中的位置
            if (goodMatches.size() < 4) {
                return res;
            }

            List<Point> objPoints = new ArrayList<>();
            List<Point> scenePoints = new ArrayList<>();

            for (DMatch match : goodMatches) {
                objPoints.add(kp1[match.queryIdx].pt);
                scenePoints.add(kp2[match.trainIdx].pt);
            }

            MatOfPoint2f objMat = new MatOfPoint2f();
            MatOfPoint2f sceneMat = new MatOfPoint2f();
            objMat.fromList(objPoints);
            sceneMat.fromList(scenePoints);
            List<Target> targets = buildTarget(gray1,gray2,objMat,sceneMat,templateRes.getTargets(v -> !"mask".equals(v.getLabel())));
            OpenCVUtil.release(objMat,sceneMat);
            res.addTargets(targets);
        }finally {
            OpenCVUtil.release(img1,img2,gray1,gray2,
                    keypoints1,keypoints2,descriptors1,descriptors2,
                    mask1,mask2
            );
        }
        return res;
    }

    public static List<Target> buildTarget(Mat gray1,Mat gray2,
                                           MatOfPoint2f objMat, MatOfPoint2f sceneMat,
                                           List<Target> templateRegions){

        List<Target> targetList = new ArrayList<>();
        //使用RANSAC计算单应性矩阵
        Mat H = Calib3d.findHomography(objMat, sceneMat, Calib3d.RANSAC, 3.0);
        if(H.empty()) {
            return targetList;
        }
        int temWidth = gray1.width(), temHeight = gray1.height();
        int sceneWidth = gray2.width(), sceneHeight = gray2.height();

        for (Target tr : templateRegions) {
            float left = tr.getLeft();
            float top = tr.getTop();

            //计算模板局部区域 (50,50)-(80,80) 在场景图中的对应四角点
            Mat objLocalCorners;
            if (tr.getWidth() != 0) {
                float[][] vertex = tr.getVertex(temWidth, temHeight);
                objLocalCorners = new Mat(4, 1, CvType.CV_32FC2);
                objLocalCorners.put(0, 0,
                        vertex[0][0], vertex[0][1],
                        vertex[1][0], vertex[1][1],
                        vertex[2][0], vertex[2][1],
                        vertex[3][0], vertex[3][1]
                );
            } else {
                float[][] list = tr.getPoints();
                if(list.length < 4) continue;
                left = list[0][0];
                top  = list[0][1];
                float[] data = new float[list.length * 2];
                for (int i = 0; i < list.length; i++) {
                    data[i * 2 + 0] = list[i][0] * temWidth;
                    data[i * 2 + 1] = list[i][1] * temHeight;
                }
                objLocalCorners = new Mat(list.length, 1, CvType.CV_32FC2);
                objLocalCorners.put(0, 0,data);
            }

            MatOfPoint2f sceneLocalCorners = new MatOfPoint2f();
            // 变换角点到场景图像
            Core.perspectiveTransform(objLocalCorners, sceneLocalCorners, H);
            Target target = new Target();
            target.setLabel(tr.getLabel());
            for (Point p : sceneLocalCorners.toArray()) {
                target.addPoint(new float[]{(float) p.x / sceneWidth, (float) p.y / sceneHeight});
            }
            float[] targetPoint = target.getPoints()[0];
            target.setLeft(targetPoint[0] - left);
            target.setTop(targetPoint[1] - top);
            target.setRemark("");
            //保持输入输出对应
            targetList.add(target);
            OpenCVUtil.release(objLocalCorners, sceneLocalCorners);
        }
        OpenCVUtil.release(H);
        //watch.print();
        return targetList;
    }

    /**
     * 垂直显示匹配结果
     */
    private static void drawMatchesVertical(Mat img1, MatOfKeyPoint keypoints1,
                                            Mat img2, MatOfKeyPoint keypoints2,
                                            List<DMatch> goodMatches, Mat output) {
        KeyPoint[] kp1 = keypoints1.toArray();
        KeyPoint[] kp2 = keypoints2.toArray();

        // 创建垂直排列的画布
        int totalHeight = img1.height() + img2.height();
        int maxWidth = Math.max(img1.width(), img2.width());
        output.create(totalHeight, maxWidth, CvType.CV_8UC3);

        // 复制第一张图片到上方
        Mat roi1 = output.rowRange(0, img1.height()).colRange(0, img1.width());
        if (img1.channels() == 1) {
            Imgproc.cvtColor(img1, roi1, Imgproc.COLOR_GRAY2BGR);
        } else {
            img1.copyTo(roi1);
        }

        // 复制第二张图片到下方
        Mat roi2 = output.rowRange(img1.height(), totalHeight).colRange(0, img2.width());
        if (img2.channels() == 1) {
            Imgproc.cvtColor(img2, roi2, Imgproc.COLOR_GRAY2BGR);
        } else {
            img2.copyTo(roi2);
        }

        // 绘制匹配线
        Random rng = new Random(12345);
        for (DMatch match : goodMatches) {
            Point pt1 = kp1[match.queryIdx].pt;
            Point pt2 = kp2[match.trainIdx].pt;

            // 为第二张图片的点坐标加上偏移量
            Point pt2Offset = new Point(pt2.x, pt2.y + img1.height());

            // 生成随机颜色
            Scalar color = new Scalar(rng.nextInt(256), rng.nextInt(256), rng.nextInt(256));

            // 绘制关键点
            Imgproc.circle(output, pt1, 4, color, -1, Imgproc.LINE_AA);
            Imgproc.circle(output, pt2Offset, 4, color, -1, Imgproc.LINE_AA);

            // 绘制连接线
            Imgproc.line(output, pt1, pt2Offset, color, 1, Imgproc.LINE_AA);
        }
    }



    /**nfeatures: 要保留的最佳特征点数，0表示不限制数量<br/>
     * nOctaveLayers: 高斯金字塔每组中的层数，默认为3<br/>
     * contrastThreshold: 对边缘响应的阈值，越小则检测到的特征点越多（噪声也可能增加），默认0.04<br/>
     * edgeThreshold: 边缘消除阈值，用于消除边缘效应，数值越大保留的边缘特征越多，默认10<br/>
     * sigma: 高斯模糊的标准差，影响初始图像模糊程度，默认1.6
     */
    public static class SiftHint{
        int imgHeight = 320;

        double contrastThreshold = 0.04;
        double edgeThreshold = 10;

        int desMatcherType = DescriptorMatcher.FLANNBASED;
        float loweRatioThresh = 0.75f;
        //水平方向相差百分比阈值
        float xRatioThresh = 0.4f;
        float yRatioThresh = 0.4f;

        //绘制匹配结果
        int drawMatches = 0;//1 横向 2 纵向

        public double getContrastThreshold() {
            return contrastThreshold;
        }

        public void setContrastThreshold(double contrastThreshold) {
            this.contrastThreshold = contrastThreshold;
        }

        public double getEdgeThreshold() {
            return edgeThreshold;
        }

        public void setEdgeThreshold(double edgeThreshold) {
            this.edgeThreshold = edgeThreshold;
        }

        public int getDesMatcherType() {
            return desMatcherType;
        }

        public void setDesMatcherType(int desMatcherType) {
            this.desMatcherType = desMatcherType;
        }

        public int getImgHeight() {
            return imgHeight;
        }

        public void setImgHeight(int imgHeight) {
            this.imgHeight = imgHeight;
        }

        public float getLoweRatioThresh() {
            return loweRatioThresh;
        }

        public void setLoweRatioThresh(float loweRatioThresh) {
            this.loweRatioThresh = loweRatioThresh;
        }

        public float getxRatioThresh() {
            return xRatioThresh;
        }

        public void setxRatioThresh(float xRatioThresh) {
            this.xRatioThresh = xRatioThresh;
        }

        public float getyRatioThresh() {
            return yRatioThresh;
        }

        public void setyRatioThresh(float yRatioThresh) {
            this.yRatioThresh = yRatioThresh;
        }

        public int getDrawMatches() {
            return drawMatches;
        }

        public void setDrawMatches(int drawMatches) {
            this.drawMatches = drawMatches;
        }
    }

}
