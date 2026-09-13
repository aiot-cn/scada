<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!doctype html>
<html>
<head>
	<meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
	<meta charset="UTF-8" />
	<link rel="icon" href="${res}/images/favicon.ico" type="image/x-icon">
	<link href="${res}/plugin/xgplayer/3.0.10/xgplayer.min.css" rel="stylesheet">
	<script type="text/javascript" src="${res}/plugin/ckplayer-x2/ckplayer.js" charset="utf-8" data-name="ckplayer"></script>
	<script src="${res}/plugin/xgplayer/3.0.10/xgplayer.min.js" charset="utf-8"></script>
	<script src="${res}/plugin/xgplayer/3.0.10/xgplayer-flv.min.js" charset="utf-8"></script>
	<style type="text/css">
		html, body {width:100%;height:100%;margin:auto;overflow: hidden;}
		body {display:flex;}
		#video {flex:auto;}
		.xgplayer-cssfullscreen,.xgplayer-volume,.xgplayer-playbackrate{
			display: none !important;
		}
	</style>
	<script type="text/javascript">
		//window.addEventListener('resize',function(){document.getElementById('video').style.height=window.innerHeight+'px';});

	</script>
</head>
<body>

	<div id="video" style="height: 100%;width: 100%"></div>

	<script type="text/javascript">
		function isIOS() {
			return /iPad|iPhone|iPod/.test(navigator.userAgent);
		}

		var vurl = '${param.url}';
		if(!vurl){
			vurl = isIOS()? '/live/${param.ch}/hls.m3u8' : '/live/${param.ch}.live.flv';
		}

		var netTimeout = '${param.netTimeout}' || 60;
		if(Object.assign){
			var player = new Player({
				id: 'video',
				url: vurl,
				isLive: true,
				playsinline: true,
				autoplay: true,
				volume:0,
				height: window.innerHeight,
				width: window.innerWidth,
				plugins: [window.FlvPlayer]
			});

		}else{
			var videoObject = {
				container: '#video', //“#”代表容器的ID，“.”或“”代表容器的class
				variable: 'player', //播放函数名称，该属性必需设置，值等于下面的new ckplayer()的对象
				//flashplayer:true,
				video: vurl,//视频地址
				live:true,
				overspread:true,//视频铺满
				autoplay:true,
				volume:0,
				//debug:true,
				loaded : "loadedHandler"
			};
			var player = new ckplayer(videoObject);//初始化播放器
		}

		if(netType() == "internet"){
			setInterval(function (){
				if(Object.assign){
					player.pause();
				}else{
					player.videoClear();
				}
			},netTimeout * 1000);
		}

		function loadedHandler(){
			player.addListener('clickEvent', durationHandler);
			player.addListener('error', errorHandler);
			//player.addListener('play', durationHandler);
		}

		function durationHandler(action,name){
			if(action == "actionScript->videoPlay"){
				player.videoClear();
				player.videoPlay();
			}
		}

		function errorHandler(con,name){
			console.error(con + ":"+name);
			if(name == "player"){

			}
		}

		function netType(){
			var h = location.hostname;
			if(h.indexOf("127.0.0") == 0 || h.indexOf("localhost") == 0){
				return "local";
			}else if(/^(\d{1,3}\.){3}\d{1,3}/.test(h)){
				return "lan";
			}else{
				return "internet";
			}
		}

	</script>
</body>

</html>
