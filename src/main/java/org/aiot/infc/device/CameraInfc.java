package org.aiot.infc.device;

import org.aiot.lang.annotation.AoReflect;

import java.awt.image.BufferedImage;
import java.io.File;

@AoReflect("摄像头")
public interface CameraInfc extends PtzInfc{
	//channel:通道号 为空时采用默认通道
	@AoReflect("抓拍")
	File getPicture(Integer channel,File file);

	@AoReflect(value="图像")
	BufferedImage getImage(Integer channel);

	@AoReflect(value = "录制视频")
	File recordVideo(Integer channel,int second,File file);

	@AoReflect("RTSP地址")
	String getRtspUrl();

	@AoReflect(value = "获取倍数")
	Float getMultiple(Integer channel);

	@AoReflect("设置倍数")
	boolean setMultiple(Integer channel,float multiple);

	@AoReflect("放大")
	boolean zoomIn(Integer channel);

	@AoReflect("缩小")
	boolean zoomOut(Integer channel);



}
