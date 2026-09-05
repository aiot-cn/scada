
var wsServer = 'ws://'+location.host+base+"/websocket";
var ws = new WebSocket(wsServer);

ws.onopen = function(ev) {
	console.info("连接成功："+wsServer);
};

ws.onclose = function () {
	console.log('链接关闭');
};

ws.onerror = function(ev) {	   
	console.error(ev);
};

//进入房间
WebSocket.prototype.joinRoom = function(room){
	if (this.readyState == WebSocket.OPEN) {
		this.send(JSON.stringify({action:"join",room:room}));
		console.info("已进入房间："+room);
	} else {
		layer.alert("进入房间["+room+"]失败",{icon:2});
	}
};

//离开房间
WebSocket.prototype.leftRoom = function(room){
	this.send(JSON.stringify({action:"left",room:room}));
	console.info("已退出房间："+room);
};

//执行设备方法
WebSocket.prototype.devExec = function(devNo,method,params){
	var p = {action:"devExec",devNo:devNo,method:method};
	this.send(JSON.stringify($.extend(p,params)));
};

//执行脚本
WebSocket.prototype.scriptExec = function(code,params){
	var p = {action:"scriptExec",scriptCode:code};
	this.send(JSON.stringify($.extend(p,params)));
};

