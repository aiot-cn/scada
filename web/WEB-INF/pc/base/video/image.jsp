<%@ page  contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!doctype html>
<html>
<head>
	<title>视频图片</title>
	<%@include file="../../common/page_head.jsp" %>
	<script src="${res}/js/websocket.js"></script>
	<style type="text/css">
		html,body{
			margin: 0;
			padding: 0;
			height: 100%;
		}

	</style>
</head>
<body>
	<img id="img" src="" width="100%" height="100%" />
</body>

<script>
	var roomId = param.socket;
	var img = document.getElementById("img");
	getSocket();

	function getSocket(){
		ws.onopen = function(ev) {
			console.info("连接成功："+wsServer);
			ws.joinRoom(roomId);
		};

		ws.onclose = function () {
			layer.msg('连接断开，开始重连...');
			var t = setTimeout(function () {
				clearTimeout(t);
				ws = new WebSocket(wsServer);
				getSocket();
				//socketIsCon = false;
			},3000);
		};

		ws.onmessage = function(evt){
			if (evt.data instanceof ArrayBuffer) {
				var blob = new Blob([evt.data], {type: 'image/jpeg'});
				var url = URL.createObjectURL(blob);
				img.src = url;
				URL.revokeObjectURL(img.previousSrc || '');
				img.previousSrc = url;

			}
		}
	}
</script>


</html>
