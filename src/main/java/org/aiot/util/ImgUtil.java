package org.aiot.util;

import jdk.nashorn.internal.runtime.Undefined;
import org.aiot.infc.CallBack;
import org.aiot.infc.ImgAbstract;
import org.aiot.model.lang.Target;
import org.bytedeco.ffmpeg.global.avutil;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.nutz.castor.Castors;
import org.nutz.img.Colors;
import org.nutz.img.Fonts;
import org.nutz.img.Images;
import org.nutz.lang.Strings;
import org.opencv.core.Mat;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.bytedeco.ffmpeg.global.avutil.AV_LOG_ERROR;

public class ImgUtil {
	
	public static BufferedImage read(Object img){
		if(img instanceof ImgAbstract)
			img = ((ImgAbstract)img).getImg();

		if(img instanceof BufferedImage)
			return (BufferedImage) img;

		try {
			if(img instanceof File)
				return ImageIO.read((File) img);
			if(img instanceof String) {
				File file = Castors.me().castTo(img, File.class);
				return ImageIO.read(file);
			}
			if(img instanceof byte[]){
				try(ByteArrayInputStream bis = new ByteArrayInputStream((byte[]) img)) {
					return ImageIO.read(bis);
				}
			}
			if(img instanceof Mat){
				byte[] bytes = OpenCVUtil.toBytes((Mat) img);
				try(ByteArrayInputStream bis = new ByteArrayInputStream(bytes)) {
					return ImageIO.read(bis);
				}
			}

		}catch (IOException e){
			System.out.println("读取图片"+img+"失败："+e.getMessage());
		}

		return null;
	}

	public static void write(BufferedImage img,File file){
		Images.write(img,file);
	}

	/**
	 *	复制 效率略低，可以处理不同图像类型之间的转换
	 */
	public static BufferedImage copy(BufferedImage img){
		BufferedImage clonedImage = new BufferedImage(img.getWidth(), img.getHeight(), img.getType());
		Graphics2D g2d = clonedImage.createGraphics();
		g2d.drawImage(img, 0, 0, null);
		g2d.dispose();
		return clonedImage;
	}

	/**
	 * 克隆 效率更高，但只能与原图像一致
	 */
	public static BufferedImage clone(BufferedImage source) {
		ColorModel cm = source.getColorModel();
		boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
		WritableRaster raster = source.copyData(null);
		return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
	}

	public static byte[] toBytes(BufferedImage img){
		return toBytes(img,"jpg");
	}

	public static byte[] toBytes(BufferedImage bufImg,String format){
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			boolean success = ImageIO.write(bufImg, Strings.sBlank(format,"jpg"), baos);
			if (success) {
				return baos.toByteArray();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	//裁剪
	public static BufferedImage crop(Object img, Target target){
		BufferedImage bi = read(img);
		if(bi == null)
			return null;

		int width = bi.getWidth();
		int height = bi.getHeight();

		float x = target.getLeft() * width;
		float y = target.getTop() * height;
		float w = target.getWidth() * width;
		float h = target.getHeight() * height;

		if(target.getAngel() == 0){
			return bi.getSubimage((int) x,(int)y,(int)w,(int)h);
		}

		BufferedImage rotatedImage = new BufferedImage(width, height, bi.getType());
		Graphics2D g2d = rotatedImage.createGraphics();
		// 设置抗锯齿
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		// 设置插值方式为双线性
		g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		AffineTransform at = AffineTransform.getRotateInstance(Math.toRadians(-target.getAngel()), x + w/2, y + h/2);
		g2d.drawRenderedImage(bi, at);
		g2d.dispose();
		return rotatedImage.getSubimage((int) x,(int)y, (int) w, (int) h);
	}

	public static BufferedImage createText(String text){
		return createText(text,640,360,"#111","#fff","",16,0);
	}

	public static BufferedImage createText(String content,
										   int width,
										   int height,
										   String bgColor,
										   String fontColor,
										   String fontName,
										   int fontSize,
										   int fontStyle) {
		// 处理下参数
		if (Strings.isBlank(content)) {
			return null;
		}
		if (width <= 0) {
			width = 256;
		}
		if (height <= 0) {
			height = 256;
		}
		if (Strings.isBlank(fontColor)) {
			fontColor = "#FFF";
		}
		if (Strings.isBlank(bgColor)) {
			bgColor = "#000";
		}
		if (fontSize <= 0) {
			fontSize = height / 2;
		}
		if (fontStyle < 0 || fontStyle > 2) {
			fontStyle = Font.BOLD;
		}
		// 准备
		BufferedImage im;
		Graphics2D gc;
		Color colorFont = Colors.as(fontColor);
		Color colorBg = Colors.as(bgColor);
		// 判断图片格式
		int imageType = BufferedImage.TYPE_INT_RGB;
		if (colorFont.getAlpha() < 255 || colorBg.getAlpha() < 255) {
			imageType = BufferedImage.TYPE_INT_ARGB;
		}
		// 生成背景
		im = new BufferedImage(width, height, imageType);
		gc = im.createGraphics();

		//设置渲染 VALUE_ANTIALIAS_ON 抗锯齿
		gc.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		//高质量 VALUE_RENDER_SPEED 快速渲染
		gc.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

		gc.setBackground(colorBg);
		gc.clearRect(0, 0, width, height);

		// 写入文字
		Font cFont = Fonts.get(fontName, fontStyle, fontSize);
		gc.setFont(cFont);
		gc.setColor(colorFont);

		FontMetrics cFontM = gc.getFontMetrics();
		int cW = cFontM.stringWidth(content);
		int w2 = width-40*2;
		if(cW < w2){
			gc.drawString(content, (width  - cW) / 2, (height - fontSize) / 2);
			return im;
		}

		String[] line = textLine(content,cFontM,w2);
		int rows = line.length;
		int x = 40;
		int y = (height - (rows * 2 -1) * fontSize)/2 + fontSize;
		for(int i=0;i<rows;i++){
			gc.drawString(line[i], x, y + (i * 2 * fontSize));
		}
		return im;
	}



	public static BufferedImage addText(BufferedImage bufImg,String text) {
		return addText(bufImg,text,40,40,"#fff",16);
	}

	public static BufferedImage addText(BufferedImage bufImg,String text,int x,int y,String color,int fontSize){
		return addText(bufImg,text,x,y,color,fontSize,0,null);
	}

	public static BufferedImage addText(BufferedImage bufImg,String text,int x,int y,String color,int fontSize,int fontStyle,String fontName) {
		Graphics2D gc = bufImg.createGraphics();
		gc.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		Font cFont = Fonts.get(fontName, fontStyle, fontSize);
		gc.setFont(cFont);
		gc.setColor(Colors.as(color));
		drawText(gc,text,bufImg.getWidth()-2*x,x,y);
		return bufImg;
	}

	public static void drawText(Graphics2D gc,String text,int width,int x,int y){
		FontMetrics cFontM = gc.getFontMetrics();
		String[] line = textLine(text,cFontM,width);
		for(int i=0;i<line.length;i++){
			gc.drawString(line[i], x, y + (i * 2 * cFontM.getAscent())+cFontM.getAscent()); //Y是基线位置，不是顶点位置
		}
	}

	public static String[] textLine(String text,FontMetrics cFontM,int width){
		List<String> line = new ArrayList<>();
		for(int i=0;i<text.length();i++){
			String s = text.substring(0,i);
			int w3 = cFontM.stringWidth(s);
			if(w3 > width){
				line.add(text.substring(0,i-1));
				text = text.substring(i-1);
				i = 0;
			}
		}
		if(text.length() > 0)
			line.add(text);
		return line.toArray(new String[0]);
	}
	/**
     * 在图片上绘制指定颜色的线段
     * 
     */
    public static BufferedImage drawLine(BufferedImage image,int x1, int y1, int x2, int y2, float lineWidth, Color color) {        
        // 创建Graphics2D对象进行绘制
        Graphics2D g2d = image.createGraphics();
  
        // 设置绘图参数
        g2d.setColor(color);
        g2d.setStroke(new BasicStroke(lineWidth)); // 设置线宽
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
                            RenderingHints.VALUE_ANTIALIAS_ON); // 抗锯齿
        
        // 绘制线段
        g2d.drawLine(x1, y1, x2, y2);
        // 释放资源
        g2d.dispose();
        
        return image;
    }
    /**
     * 在图片上绘制指定颜色的矩形框
     * 
     */
    public static BufferedImage drawRect(BufferedImage image,int x, int y, int width, int height, float lineWidth, Color color) {        
        // 创建Graphics2D对象进行绘制
        Graphics2D g2d = image.createGraphics();
        
        // 设置绘图参数
        g2d.setColor(color);
        g2d.setStroke(new BasicStroke(lineWidth)); // 设置线宽
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
                            RenderingHints.VALUE_ANTIALIAS_ON); // 抗锯齿
        
        // 绘制矩形框
        g2d.drawRect(x, y, width, height);
        // 释放资源
        g2d.dispose();
        
        return image;
    }

	/**
	 * 折线图
	 */
	public static BufferedImage lineChart(int width,int height,List<float[]> data) {
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		Graphics2D g2 = image.createGraphics();
		Color lineColor = new Color(255, 255, 255);
		g2.setColor(lineColor);
		g2.setStroke(new BasicStroke(1f));
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		for(int i=0;i<data.size()-1;i++){
			float[] d1 = data.get(i);
			float[] d2 = data.get(i+1);
			int x1 = (int) (d1[0] * width);
			int y1 = (int) ((1-d1[1]) * height);
			int x2 = (int) (d2[0] * width);
			int y2 = (int) ((1-d2[1]) * height);
			g2.drawLine(x1, y1, x2, y2);
		}
		g2.dispose();

		return image;
	}


	public static BufferedImage fileToBufImg(File file, int w, int h){
		BufferedImage bi = null;
		try {
			bi = file.isFile() ?  ImageIO.read(file) : Images.read("../../resources/images/noImage.jpg");
		} catch (IOException e) {
			bi = Images.read("../../resources/images/noImage.jpg");
			e.printStackTrace();
		}finally {
			if((w != 0 || h != 0) && bi != null && w < bi.getWidth())
				bi = Images.zoomScale(bi, w == 0 ? -1 : w, h == 0 ? -1 : h);
		}
		return bi;
	}

	public static void readVideo(File file, CallBack<BufferedImage,Integer,Object> callback) {
		avutil.av_log_set_level(AV_LOG_ERROR);
		FFmpegFrameGrabber grabber = null;
		Java2DFrameConverter converter = new Java2DFrameConverter();
		try {
			// 初始化帧抓取器
			grabber = new FFmpegFrameGrabber(file.getAbsolutePath());
			grabber.start();

			// 获取视频信息
			//int lengthInFrames = grabber.getLengthInFrames();

			// 逐帧处理
			Frame frame;
			int i = 0;
			while ((frame = grabber.grab()) != null) {
				BufferedImage image = converter.getBufferedImage(frame);
				if (image != null){
					Object a = callback.apply(image,i);
					if(a != null && !(a instanceof Undefined) && (Boolean) a)
						break;
					i++;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (grabber != null) {
					grabber.stop();
					grabber.release();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

}
