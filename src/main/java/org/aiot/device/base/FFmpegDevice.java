package org.aiot.device.base;

import org.aiot.device.BaseDevice;
import org.aiot.lang.annotation.AoReflect;
import org.aiot.lang.workflow.Workflow;
import org.aiot.model.enums.ANSI;
import org.aiot.model.enums.AstEnum;
import org.aiot.model.enums.ConfigEnum;
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
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.bytedeco.ffmpeg.global.avutil.AV_LOG_ERROR;

@AoReflect("FFmpeg")
public class FFmpegDevice extends BaseDevice {
	Log log = Logs.get();

	/**
	 * rtsp://admin:123456@192.168.1.64:554/h264/ch1/main/av_stream
	 */
	@AoReflect(value = "拉流地址",type = AstEnum.param)
	private String pullUrl;

	/**
	 * 如果推送协议为websocket则按帧推送
	 * websocket:video-1
	 * 其它则推流
	 * rtmp://127.0.0.1:1554/ffmpeg/test
	 */
	@AoReflect(value = "推送地址",type = AstEnum.param)
	private String pushUrl;

	/**
	 * Intel Quick Sync 难用，不推荐
	 * win10 D3D11VA
	 * win10之前 DXVA2
	 */

	@AoReflect(value = "硬件加速",type = AstEnum.param,select = "CUDA,D3D11VA,DXVA2,macOS,Linux")
	private String hwAccelType = ConfigEnum.hwAccelType.getValue();

	@AoReflect(value = "工作间隔",type = AstEnum.param)
	private int workInterval = 0;
	private long lastWorkTime;//上次工作时间


	@AoReflect("帧")
	private long count;//接收到的帧数

	Java2DFrameConverter converter = new Java2DFrameConverter();
	private FFmpegFrameGrabber grabber;
	private BufferedImage image;
	private final OpenCVFrameConverter.ToOrgOpenCvCoreMat matConverter = new OpenCVFrameConverter.ToOrgOpenCvCoreMat();
	private Workflow workflow;


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

			// NVIDIA CUDA 硬件解码
			if("CUDA".equals(hwAccelType)){
				grabber.setVideoCodecName("h264_cuvid");
				grabber.setOption("hwaccel", "cuda");
				grabber.setOption("hwaccel_output_format", "cuda");//显存直通，零拷贝
			}
			if("D3D11VA".equals(hwAccelType)){
				grabber.setVideoCodecName("h264_d3d11va");// 机制A：强制使用 D3D11,默认适配器通常是主显示适配器
				grabber.setOption("hwaccel", "d3d11va"); // 机制B：保持软解器 h264 不变，但让 libavformat 在打开码流时挂上 D3D11VA 加速
				/*
				  h264_d3d11va 解码后，帧默认停留在 GPU 显存（D3D11 纹理），JavaCV 的 matConverter 拿不到。
				  FFmpeg 把每帧从显存拷回系统内存（nv12 格式），JavaCV 再转成 BGR 的 Mat。
				 */
				grabber.setOption("hwaccel_output_format", "nv12");
			}
			if("DXVA2".equals(hwAccelType)){
				grabber.setVideoCodecName("h264_dxva2");
				grabber.setOption("hwaccel", "dxva2");
				grabber.setOption("hwaccel_output_format", "nv12");
			}
			if("macOS".equals(hwAccelType)){
				// VideoToolbox 解码器自带硬件加速，输出 CVPixelBuffer，JavaCV 已内置转换
				grabber.setVideoCodecName("h264_videotoolbox");
			}
			if("Linux".equals(hwAccelType)){
				grabber.setVideoCodecName("h264_vaapi");
				grabber.setOption("hwaccel", "vaapi");
				grabber.setOption("hwaccel_device", "/dev/dri/renderD128"); // 视机器可能为 renderD129
				grabber.setOption("hwaccel_output_format", "nv12"); // 转回系统内存，JavaCV 才能转 Mat
			}

			startPull();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void startPull(){
		new Thread(this::pullStream,"FFmpeg").start();
	}
	//拉流
	private void pullStream() {
		String msg = "FFmpeg["+grabber.getVideoCodecName()+"] pull start... <- " + pullUrl;
		log.info(ANSI.COLOR_FORE.green.format(msg));
		try {
			grabber.start();
			msg = "FFmpeg pull success,开始读帧 <- " + pullUrl;
			log.info(ANSI.COLOR_FORE.green.format(msg));
			while (grabber != null) {
				Mat mat = null;
				try {
					Frame frame = grabber.grab();
					if(frame == null)
						continue;
					//BufferedImage bi = converter.getBufferedImage(frame);

					mat = matConverter.convert(frame);
					if(mat == null || mat.empty())
						continue;

					count ++;
					if(imageWidth == null || imageHeight == null){
						imageWidth = mat.cols();
						imageHeight = mat.rows();
						log.info("视频宽高: " + imageWidth + "x" + imageHeight);
						if(Strings.isNotBlank(pushUrl) && !pushUrl.startsWith("websocket")){
							initRecorder();
						}
					}
					pushFrame(mat);
				} catch (FFmpegFrameGrabber.Exception e) {
					e.printStackTrace();
				}finally {
					if(mat != null)
						mat.release();
				}
			}

		}catch(FFmpegFrameGrabber.Exception e){
			log.error("FFmpeg pull error <- " + pullUrl);
			e.printStackTrace();
		}
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

			if ("CUDA".equals(hwAccelType)) {
				// NVIDIA 硬件编码
				recorder.setVideoCodecName("h264_nvenc");
				recorder.setVideoOption("preset", "p1");
				recorder.setVideoOption("tune", "ll");
				recorder.setVideoOption("rc", "vbr");
				recorder.setVideoOption("cq", "28");
			} else if ("D3D11VA".equals(hwAccelType) || "DXVA2".equals(hwAccelType)) {
				// Windows Media Foundation 硬件编码（跨厂商，Intel/AMD/NVIDIA 通用）
				recorder.setVideoCodecName("h264_mf");
			} else if ("macOS".equals(hwAccelType)){
				// Apple VideoToolbox 硬件编码
				recorder.setVideoCodecName("h264_videotoolbox");
			} else {
				// 软件编码。注：Linux VAAPI(h264_vaapi) 编码要求输入帧为 VA 表面，
				// JavaCV 的 FFmpegFrameRecorder 未内置 hwframe 上传，故回退到 libx264。
				recorder.setVideoCodec(avcodec.AV_CODEC_ID_H264);
				recorder.setVideoOption("tune", "zerolatency");
				recorder.setVideoOption("preset", "ultrafast");
				recorder.setVideoOption("crf", "28");
			}

			recorder.start();
			frameIndex = 0;
			isStreamPushing = true;
			log.info("开始推流 -> " + pushUrl);

		} catch (FrameRecorder.Exception e) {
			log.error("推流失败 -> " + pushUrl);
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
	public BufferedImage getImage(Integer timeout){
		if(timeout != null && System.currentTimeMillis() - getLastTime() > timeout)
			return null;
		return image;
	}

	public void pushFrame(Mat mat){
		long now = System.currentTimeMillis();
		//正在执行
		if (now - lastWorkTime < workInterval || !encoding.compareAndSet(false, true)) {
			skipCount++;
			return;
		}
		lastWorkTime = now;

		//异步执行，需要先克隆一份
		Mat cloned = mat.clone();
		encoder.submit(() -> {
			Object obj = null;
			try {
				if(workflow != null){
					obj = workflow.run(new NutMap("image", mat));
				}
				if(isStreamPushing){
					pushStream(cloned,obj);
				}else if(Strings.isNotBlank(pushUrl) && pushUrl.startsWith("websocket")){

				}

			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				cloned.release();
				encoding.set(false);
			}
		});
	}

	private void pushStream(Mat mat,Object obj) throws FFmpegFrameRecorder.Exception {
		boolean first = true;
		do {
			if (first) {
				if(obj instanceof RecognitionRes){
					RecognitionRes res = (RecognitionRes) obj;
					OpenCVUtil.drawRecognitionRes(mat,res);
				}
				first = false;
			}
			Frame frame = matConverter.convert(mat);
			frame.timestamp = frameIndex * (1000000L / 25);
			frameIndex++;
			recorder.record(frame);
		} while (fillGap());
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

	/**
	 * 获取硬件解码器
	 */
	public String getHwDecoder() {

		try {

			// 根据编码格式获取候选硬件解码器列表 这个仅仅是编译支持
			List<String> hwDecoders = Arrays.asList(
					"h264_cuvid",      // NVIDIA CUDA
					"h264_qsv",        // Intel Quick Sync  -- windows Linux
					"h264_mediacodec", // Android MediaCodec
					"h264_videotoolbox", // macOS VideoToolbox
					"h264_d3d11va",    // Windows D3D11VA --win10推荐
					"h264_dxva2",      // Windows DXVA2 --win10之前
					"h264_vaapi"       // Linux VAAPI -- 跨厂商
			);

			for (String decoderName : hwDecoders) {
				if (avcodec.avcodec_find_decoder_by_name(decoderName) != null) {
					return decoderName;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
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
