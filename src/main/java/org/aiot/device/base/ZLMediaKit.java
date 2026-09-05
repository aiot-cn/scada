package org.aiot.device.base;

import org.aiot.communication.mode.Console;
import org.aiot.device.BaseDevice;
import org.aiot.infc.device.BaseExtend;
import org.aiot.lang.NotifyEvent;
import org.aiot.lang.annotation.AoReflect;
import org.aiot.lang.workflow.Workflow;
import org.aiot.model.enums.ANSI;
import org.aiot.model.enums.EventEnum;
import org.aiot.model.enums.PathEnum;
import org.aiot.model.table.TCommunication;
import org.aiot.model.table.TVideoSource;
import org.aiot.model.table.TWorkflow;
import org.aiot.util.*;
import org.nutz.http.Http;
import org.nutz.http.HttpException;
import org.nutz.http.Response;
import org.nutz.img.Images;
import org.nutz.json.Json;
import org.nutz.lang.Files;
import org.nutz.lang.Lang;
import org.nutz.lang.util.NutMap;
import org.nutz.log.Log;
import org.nutz.log.Logs;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * https://github.com/ZLMediaKit/ZLMediaKit/wiki/MediaServer%E6%94%AF%E6%8C%81%E7%9A%84HTTP-API
 * config.ini 默认拉流超时是10s
 * [api]
 * apiDebug=0 默认1
 * secret=pUYVV4tmezaod6dvcIfTjSe6buZxAX0T 默认无，第一次运行时生成
 *
 * [http]
 * port=8079 默认80
 * sslport=0 默认443 0表示关闭
 * allow_cross_domains=1 默认允许跨域
 *
 * [rtsp]
 * port=554 Linux下1024端口以下权限不足
 * rtpTransportType=-1
 * sslport=0
 *
 * #ubuntu18.04下libstdc++6最高GLIBCXX_3.4.25，而需要3.4.26
 * 1.找到库文件位置 /sbin/ldconfig -p | grep stdc++
 * 2.查看已安装版本 strings /usr/lib/aarch64-linux-gnu/libstdc++.so.6 | grep GLIBCXX
 *
 * file libstdc++.so.6.0.26 一定要检查文件是否是arm的
 * export LD_LIBRARY_PATH=/opt/lib:$LD_LIBRARY_PATH 还未测试
 *
 * cp libstdc++.so.6.0.26 /usr/lib/aarch64-linux-gnu
 * cd /usr/lib/aarch64-linux-gnu
 * # 使用最新的库建立软连接
 * ln -s libstdc++.so.6.0.26 libstdc++.so.6 注意别搞反了 是 a(已存在) -> b(创建连接)
 */
public class ZLMediaKit extends BaseDevice implements Observer,BaseExtend.RMenu{
	Log log = Logs.get();

	private String secret;
	private Integer serverPort = 8079;//web,api,取流端口
	private Integer rtspPort = 1554;
	private Integer rtmpPort = 1935;

	File ZLMediaPath = PathEnum.lib.getFile("ZLMediaKit");
	private Map<Long,FFmpegDevice> ffmpegMap = new HashMap<>();

	private Console console;
	private final CountDownLatch countDownLatch = new CountDownLatch(1);

	private boolean hasLib;
	private boolean isStarted; //是否启动成功

	@Override
	public void init(){
		bs.addObserver(this);

		if(ZLMediaPath.isDirectory()){
			hasLib = true;
		}else{
			log.warn(ANSI.COLOR_FORE.yellow.format("还未安装流媒体服务"));
			return;
		}
		File ZLMediaConf = new File(ZLMediaPath,"config.ini");
		if(ZLMediaConf.isFile()){
			//获取ini配置
			IniParser iniConfig = new IniParser(ZLMediaConf);
			serverPort = iniConfig.getInteger("http", "port",8079);
			rtspPort = iniConfig.getInteger("rtsp", "port",1554);
			rtmpPort = iniConfig.getInteger("rtmp", "port",1935);
			secret = iniConfig.get("api", "secret");
		}
		String uri = "${home}/lib/ZLMediaKit/MediaServer";

		TCommunication tc = new TCommunication(-1,Console.class,uri,true,false);
		console = new Console();
		console.setCharsetName("UTF-8");
		console.setCommun(tc);
		console.addObserver( this);
		console.open();
		try {
			countDownLatch.await(2000, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		selfTest();
	}

	@Override
	public void selfTest(){
		NutMap nm = getMediaList();
		if(nm == null)
			return;

		List<MediaInfo> list =  nm.getAsList("data",MediaInfo.class);
		if(list == null)
			list = new ArrayList<>();

		NutMap m = new NutMap();
		for(MediaInfo medial : list){
			m.put(medial.originUrl,medial);
		}

		List<TVideoSource> videoSources = bs.getTCache(TVideoSource.class);
		for(TVideoSource vs : videoSources){
			if(!m.containsKey(vs.getUrl()))
				addStreamProxy(vs);
		}
	}

	public void installLib() throws Exception{
		if(isStarted)
			throw Lang.makeThrow("流媒体服务正在运行中");

		if(ZLMediaPath.isDirectory())
			Files.deleteDir(ZLMediaPath);

		String server = "http://www.ai-ot.cn/file/download/release/"+ SystemInfo.getOsType()+"/"+SystemInfo.getOsArch()+"/ZLMediaKit.zip";
		File zipLib = HttpUtil.downloadFile(server,ZLMediaPath.getParentFile(),null);
		ZipUtil.unzip(zipLib);
		//如果网络不好，拉流需要很长时间
		new Thread(this::init).start();
	}

	/**
	 * 添加流代理 服务超时时间默认10s
	 * {code=0, data={key=__defaultVhost__/live/1}}
	 */

	public void addStreamProxy(TVideoSource vs){
		new Thread(()->{
			NutMap nm = new NutMap();
			nm.put("vhost","__defaultVhost__");
			nm.put("app","live");
			nm.put("stream",vs.getId());
			nm.put("url",vs.getRtspUrl());
			NutMap s = get("addStreamProxy",nm,15*1000);
			System.out.println("添加流["+vs.getId()+"] "+vs.getRtspUrl()+" -> " + s);

			//0成功 -1 重复添加
			if(s != null && (s.getInt("code") == 0 || s.getInt("code") == -1)){
				FFmpegDevice ffmpeg = new FFmpegDevice();
				ffmpegMap.put(vs.getId(),ffmpeg);
				ffmpeg.setPullUrl("rtsp://127.0.0.1:"+rtspPort+"/live/"+vs.getId());
				ffmpeg.setPushUrl("rtmp://127.0.0.1:"+rtmpPort+"/ffmpeg/"+vs.getId());
				if(vs.getWorkId() != null){
					TWorkflow tWorkflow = bs.getTCache(TWorkflow.class,vs.getWorkId());
					ffmpeg.setWorkflow(new Workflow(tWorkflow));
				}
				ffmpeg.init();
			}
		}).start();
	}

	public NutMap delStreamProxy(TVideoSource vs){
		NutMap nm = new NutMap();
		nm.put("key","__defaultVhost__/live/"+vs.getId());
		NutMap s = get("delStreamProxy",nm);
		System.out.println("删除流["+vs.getId()+"] ：" + s);
		if(s != null && s.getInt("code") == 0){
			FFmpegDevice ffmpeg = ffmpegMap.get(vs.getId());
			if(ffmpeg != null)
				ffmpeg.destroy();
		}
		return s;
	}

	/**
	 * 成功
	 * {"code":0}
	 * ["code":0,"data":{"aliveSecond":8,"app":"live","bytesSpeed":30136,"createStamp":1784996555,"isRecordingHLS":true,"isRecordingMP4":false,"vhost":"__defaultVhost__",
	 * "originSock":{"identifier":"class mediakit::RtspPlayerImp-2","local_ip":"10.0.0.1","local_port":60905,"peer_ip":"223.111.180.97","peer_port":10642},
	 * "originType":4,"originTypeStr":"pull",
	 * "originUrl":"rtsp://admin:ynen234234@nat.iteasy.com:10642/h264/ch33/sub/av_stream","params":"","readerCount":0,
	 * "schema":"ts","stream":"1","totalReaderCount":0,
	 * "tracks":[
	 * 	{"codec_id":0,"codec_id_name":"H264","codec_type":0,"duration":8483,"fps":22.0,"frames":188,"gop_interval_ms":1002,"gop_size":22,"height":360,"key_frames":10,"loss":0.0,"ready":true,"width":640},
	 * 	{"channels":1,"codec_id":2,"codec_id_name":"mpeg4-generic","codec_type":1,"duration":8448,"frames":66,"loss":0.0,"ready":true,"sample_bit":16,"sample_rate":8000}
	 * ]}]
	 *
	 * 错误
	 * {
	 * 	"code" : -300,
	 * 	"msg" : "Required parameter missed: \"secret\""
	 * }
	 */
	@AoReflect("获取流列表")
	public NutMap getMediaList(){
		NutMap nm = get("getMediaList",new HashMap<>());
		return nm;
	}

	@AoReflect("抓拍")
	public BufferedImage capture(String url){
		Map<String,Object> param = new HashMap<>();
		param.put("secret",secret);
		param.put("url",url);
		param.put("timeout_sec",3);//截图失败超时时间，防止 FFmpeg 一直等待截图
		param.put("expire_sec",0);//截图的过期时间，该时间内产生的截图都会作为缓存返回
		Response r = Http.get("http://127.0.0.1:"+ serverPort +"/getSnap",param,5000);
		return Images.read(r.getStream());
	}

	@AoReflect("执行")
	public NutMap execApi(@AoReflect(value = "类型",
			select = "getApiList:获取API列表,getThreadsLoad:查看线程,getServerConfig:查看配置,restartServer:重启服务")
			String action){
		Map<String,Object> param = new HashMap<>();
		return get(action,param);
	}

	/**
	 * Exception = -400,//代码抛异常
	 * InvalidArgs = -300,//参数不合法
	 * SqlFailed = -200,//sql执行失败
	 * AuthFailed = -100,//鉴权失败
	 * Success = 0//执行成功
	 *
	 * OtherFailed = -1,//业务代码执行失败
	 * 	   code : -1, # 代表业务代码执行失败
	 *     msg : "can not find the stream", # 失败提示
	 *     result : -2 # 业务代码执行失败具体原因
	 *
	 * @return
	 */
	private NutMap get(String action,Map<String,Object> param,int timeout){
		String apiUrl = "http://127.0.0.1:"+ serverPort +"/index/api/"+ action;
		param.put("secret",secret);
		try {
			Response r = Http.get(apiUrl,param,timeout);
			if(r == null){
				log.warn(action + "失败");
				return null;
			}

			return (NutMap) Json.fromJson(r.getContent());
		}catch (HttpException e){
			log.error(ANSI.COLOR_FORE.red.format("网络错误:")+e.getMessage());
			return null;
		}
	}

	private NutMap get(String action,Map<String,Object> param){
		return get(action,param,2000);
	}

	@Override
	public List<RMenuOption> menuList(){
		List<RMenuOption> list = new ArrayList<>();
		String[] us = "streamUrl".split("\n");
		for(int i=0;i<us.length;i++){
			list.add(new RMenuOption("通道"+(i+1),"/control/video?ch="+i,device.getId(),"DEV"));
		}
		return list;
	}

	@Override
	public Object menuClick(String menu, String val) {
		return null;
	}

	/**
	 * main.cpp:271 start_main | ZLMediaKit(git hash:3f59233/2024-12-20T19:31:05+08:00,branch:master,build time:2024-12-27T14:34:16)
	 * util.cpp:391 operator() | Stamp thread started
	 */
	@Override
	public void update(Observable o, Object event) {
		if(o instanceof Console){
			String msg = (String) event;
			if(msg.contains(" W "))
				msg = ANSI.COLOR_FORE.yellow.format(msg);
			if(msg.contains(" D "))
				msg = ANSI.COLOR_FORE.blue.format(msg);
			if(msg.contains(" E "))
				msg = ANSI.COLOR_FORE.red.format(msg);

			System.out.println(msg);

			if(msg.contains("api.secret")){
				secret = StrUtil.getPatternStr(msg,"to: ([A-Za-z0-9]+)",1);
			}
			if(msg.contains("started")){
				isStarted = true;
				log.info("流媒体服务: http://127.0.0.1:"+ serverPort +"/webassist/?secret="+secret);
				Lang.sleep(1000);
				countDownLatch.countDown();
			}
		}

		if(!(event instanceof NotifyEvent))
			return;

		NotifyEvent ne = (NotifyEvent) event;
		Object arg = ne.getData();
		if(!(arg instanceof TVideoSource))
			return;
		TVideoSource vs = (TVideoSource) arg;
		if(ne.getEventType() == EventEnum.SAVE_AFTER){
			delStreamProxy(vs);
			if(vs.getIsRemoved() == 0)
				addStreamProxy(vs);
		}else if(ne.getEventType() == EventEnum.DELETE_AFTER){
			delStreamProxy(vs);
		}

	}

	public void destroy() {
		if(console != null)
			console.close();
		super.destroy();
	}

	public boolean getHasLib() {
		return hasLib;
	}

	public Integer getServerPort() {
		return serverPort;
	}

	public static class MediaInfo{
		String app;
		String stream;
		String originUrl;
		String vhost;
	}
}
