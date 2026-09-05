package org.aiot.device.base;

import org.aiot.device.BaseDevice;
import org.aiot.lang.annotation.AoReflect;
import org.aiot.lang.workflow.Workflow;
import org.aiot.model.enums.AstEnum;
import org.aiot.model.enums.PathEnum;
import org.aiot.model.lang.RecognitionRes;
import org.aiot.util.OpenCVUtil;
import org.bytedeco.ffmpeg.global.avcodec;
import org.bytedeco.ffmpeg.global.avutil;
import org.bytedeco.javacv.*;
import org.nutz.lang.Strings;
import org.nutz.lang.util.NutMap;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.opencv.core.Mat;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.bytedeco.ffmpeg.global.avutil.AV_LOG_ERROR;

@AoReflect("FFmpeg")
public class FFmpegDevice extends BaseDevice {
	Log log = Logs.get();

	@AoReflect(value = "拉流地址",type = AstEnum.param)
	private String pullUrl = "rtsp://admin:123456@192.168.1.64:554/h264/ch1/main/av_stream";

	@AoReflect(value = "实时取流",type = AstEnum.param)
	private boolean streamOpen = true;

	@AoReflect(value = "超时",type = AstEnum.param)
	private Integer timeout;


	@AoReflect("帧")
	private long count;

	Java2DFrameConverter converter = new Java2DFrameConverter();
	private FFmpegFrameGrabber grabber;
	private BufferedImage image;
	private final OpenCVFrameConverter.ToOrgOpenCvCoreMat matConverter = new OpenCVFrameConverter.ToOrgOpenCvCoreMat();
	private Workflow workflow;


	@AoReflect(value = "推流地址",type = AstEnum.param)
	private String pushUrl; // rtmp://127.0.0.1:1554/ffmpeg/test

	@AoReflect(value = "GPU加速",type = AstEnum.param)
	private boolean useGpu = true;

	private FFmpegFrameRecorder recorder;
	private Integer imageWidth;
	private Integer imageHeight;
	private long frameIndex;

	private volatile boolean isStreamPushing = false;
	private final AtomicBoolean encoding = new AtomicBoolean(false);
	private final ExecutorService encoder = Executors.newSingleThreadExecutor(r -> {
		Thread t = new Thread(r, "FFmpeg-enc");
		t.setDaemon(true);
		return t;
	});
	private long skipCount;

	@Override
	public void init() {
		OpenCVUtil.load(); // 确保 OpenCV native 库已加载
		try{
			grabber = new FFmpegFrameGrabber(pullUrl);
			//grabber.getImageWidth(); 这里还获取不了图像的宽高
			//System.out.println("实际像素格式: " + grabber.getPixelFormat());
			// 缓冲区大小（避免卡顿）
			//grabber.setNumBuffers(1024 * 1024);
			// 设置RTSP传输协议（tcp/udp）
			grabber.setOption("rtsp_transport", "tcp"); // 更稳定
			avutil.av_log_set_level(AV_LOG_ERROR);
			grabber.setOption("stimeout", "9000000");
			// GPU 加速配置
			if(useGpu) {
				// NVIDIA CUDA 硬件解码
				grabber.setVideoCodecName("h264_cuvid"); // H.264 CUDA 解码
				grabber.setOption("hwaccel", "cuda");
				grabber.setOption("hwaccel_output_format", "cuda");
			}
			if(streamOpen)
				start();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void start(){
		new Thread(this::pullStream,"FFmpeg").start();
	}

	private void pullStream() {
		try {
			log.info("FFmpeg pull... <- " + pullUrl);
			grabber.start();
			log.info("FFmpeg 连接成功,开始读帧");
			while (grabber != null) {
				Mat mat = null;
				try {
					Frame frame = grabber.grab();
					if(frame == null)
						continue;
					//BufferedImage bi = converter.getBufferedImage(frame);

					mat = matConverter.convert(frame);
					if(mat == null)
						continue;

					count ++;
					if(imageWidth == null || imageHeight == null){
						imageWidth = mat.cols();
						imageHeight = mat.rows();
						log.info("视频宽高: " + imageWidth + "x" + imageHeight);
						if(Strings.isNotBlank(pushUrl)){
							initRecorder();
						}
					}
					pushFrame(mat);
					setLastTimeNow();
				} catch (FFmpegFrameGrabber.Exception e) {
					e.printStackTrace();
				}finally {
					if(mat != null)
						mat.release();
				}
			}

		}catch(FFmpegFrameGrabber.Exception e){
			e.printStackTrace();
		}
	}

	@AoReflect(value = "保存视频",type = AstEnum.command)
	public File saveVideo(int second,File file){
		if(file == null)
			file = PathEnum.video.getFile(System.currentTimeMillis()+".mp4");
		exec(pullUrl,second,file.getAbsolutePath());
		return file;
	}

	@Override
	public void selfTest() {

	}

	@AoReflect("获取图像")
	public BufferedImage getImage(){
		if(timeout != null && System.currentTimeMillis() - getLastTime() > timeout)
			return null;
		return image;
	}

	public void initRecorder(){
		log.info("准备推流... ->" + pushUrl);
		try{
			recorder = new FFmpegFrameRecorder(pushUrl, imageWidth, imageHeight);
			recorder.setFormat("flv");
			recorder.setFrameRate(25);
			recorder.setGopSize(50);
			recorder.setVideoBitrate(2000000);
			recorder.setAudioChannels(0);

			if (useGpu) {
				recorder.setVideoCodecName("h264_nvenc");
				recorder.setVideoOption("preset", "p1");
				recorder.setVideoOption("tune", "ll");
				recorder.setVideoOption("rc", "vbr");
				recorder.setVideoOption("cq", "28");
			} else {
				recorder.setVideoCodec(avcodec.AV_CODEC_ID_H264);
				recorder.setVideoOption("tune", "zerolatency");
				recorder.setVideoOption("preset", "ultrafast");
				recorder.setVideoOption("crf", "28");
			}

			recorder.start();
			frameIndex = 0;
			isStreamPushing = true;
			log.info("开始推流 -> " + pushUrl + (useGpu ? " [GPU/NVENC]" : " [CPU]"));

		} catch (FrameRecorder.Exception e) {
			if (useGpu) {
				log.info("NVENC 初始化失败，回退到软件编码");
				useGpu = false;
				initRecorder();
				return;
			}
			e.printStackTrace();
		}
	}

	public void pushFrame(Mat mat){
		if (!isStreamPushing || mat == null){
			return;
		}
		if (!encoding.compareAndSet(false, true)) {
			skipCount++;
			return;
		}

		Mat cloned = mat.clone();
		encoder.submit(() -> {
			try {
				boolean first = true;
				do {
					if (first) {
						if(workflow != null){
							Object obj = workflow.run(new NutMap("image", cloned));
							if(obj instanceof RecognitionRes){
								RecognitionRes res = (RecognitionRes) obj;
								OpenCVUtil.drawRecognitionRes(cloned,res);
							}
						}
						first = false;
					}
					Frame frame = matConverter.convert(cloned);
					frame.timestamp = frameIndex * (1000000L / 25);
					frameIndex++;
					recorder.record(frame);
				} while (fillGap());
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				cloned.release();
				encoding.set(false);
			}
		});
	}

	private synchronized boolean fillGap() {
		if (skipCount > 0) {
			skipCount--;
			return true;
		}
		return false;
	}

	@Override
	public void destroy(){
		encoder.shutdownNow();
		if(grabber != null) {
			try {
				grabber.close();
				grabber.release();
			} catch (FrameGrabber.Exception e) {
				e.printStackTrace();
			}finally {
				grabber = null;
			}
		}
		if (recorder != null){
			try {
				recorder.close();
				recorder.release();
			} catch (FrameRecorder.Exception e) {
				e.printStackTrace();
			}finally {
				recorder = null;
			}
		}

		super.destroy();
	}

	public String getPullUrl() {
		return pullUrl;
	}

	public void setPullUrl(String pullUrl) {
		this.pullUrl = pullUrl;
	}

	public void setImage(BufferedImage image) {
		this.image = image;
	}

	public String getPushUrl() {
		return pushUrl;
	}

	public void setPushUrl(String pushUrl) {
		this.pushUrl = pushUrl;
	}

	public Workflow getWorkflow() {
		return workflow;
	}

	public void setWorkflow(Workflow workflow) {
		this.workflow = workflow;
	}
}
