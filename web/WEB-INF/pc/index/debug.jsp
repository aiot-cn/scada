<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!doctype html>
<html lang="zh-CN">
<head>
	<title>调试</title>
	<%@include file="../common/page_head.jsp" %>
	<script src="${res}/js/websocket.js"></script>

	<style type="text/css">
		#msg{
			border: none;
			font-family: "宋体", serif;
			width: 100%;
			height:100%;
			position: absolute;
			top:0;
			bottom: 0;
			overflow: auto;
			tab-size: 4;
			white-space: pre;
		}
		.li-tx{
			color: #0d61e3;
		}
		.li-rx{
			color: #6991b5;
		}
		.li-pa{
			color: #bb7224;
		}
		.li-error{
			color: red;
		}

		.con-right{
			position: absolute;
			right: 0;
			left:425px;
			top:0;
			bottom: 0;
		}
		.con-left{
			position: absolute;
			left:0;
			top:0;
			bottom: 0;
			width: 415px;
			border-right: 1px solid #ccc;
			box-sizing: border-box;
			overflow: auto;
		}
		.hide-left .con-left{
			display: none;
		}
		.hide-left .con-right{
			left: 0;
		}
		.layui-form-pane{
			margin-top: 0;
			padding: 10px;
		}
		.layui-form-pane .layui-form-checkbox{
			margin: 4px 0 4px 6px;
		}
		.btn-send{
			background-color: #348de5;
			color: #fff;
			cursor: pointer;
			padding: 2px 5px;
			border-radius: 3px;
		}
		#msg a{
			color: #DC13BD;
			line-height: 26px;
			padding-bottom: 2px;
			border-bottom: 1px solid;
		}
		.u-1m{
			font-weight: bold;
		}
		.u-4m{
			text-decoration: underline;
		}
		.u-5m{
			/*闪烁*/
		}
		.u-7m{
			/*反显颜色*/
		}
		.u-8m{
			/*消隐*/
		}

		/* 前景色 30:黑 31:红 32:绿 33:黄 34:蓝色 35:紫色 36:深绿 37:白色 背景色 40-47*/
		.u-30m{
			color: black;
		}
		.u-31m{
			color: red;
		}
		.u-32m{
			color: limegreen;
		}
		.u-33m{
			color: yellow;
		}
		.u-34m{
			color: blue;
		}
		.u-35m{
			color: purple;
		}
		.u-36m{
			color: green;
		}
	</style>
</head>
<body class="${param.style}" style="background-color: #fff;">
<form name="f1" class="con-left layui-form layui-form-pane">
	<div class="layui-form-item">
		<label class="layui-form-label">通讯</label>
		<div class="layui-input-block">
			<%--<select name="communication" lay-filter="communication"></select>--%>
			<div id="s-type" class=""></div>
		</div>
	</div>

	<div class="layui-form-item" pane>
		<label class="layui-form-label">接收</label>
		<div class="layui-input-block">
			<input type="checkbox" name="receive" lay-skin="switch" lay-text=" ON | OFF" checked="checked">
			<a class="layui-btn layui-btn-xs layui-btn-primary layui-border-blue" style="margin-top: 6px;margin-left: 10px;line-height: 20px;" onclick="msgClear()">
				<i class="layui-icon layui-icon-fonts-clear"></i>
				清空
			</a>
			<a style="margin-top: 5px;margin-left: 10px;vertical-align: middle;display: inline-block">
				帧数 <span id="frames">0</span>
			</a>
		</div>
	</div>

	<div class="layui-form-item" pane>
		<label class="layui-form-label">显示</label>
		<div class="layui-input-block">
			<input type="checkbox" name="show" value="Tx" title="发送">
			<input type="checkbox" name="show" value="Rx" title="接收">
			<input type="checkbox" name="show" value="Pa" title="解析">
		</div>
	</div>

	<div class="layui-form-item">
		<label class="layui-form-label">包含</label>
		<div class="layui-input-block">
			<input name="msgContain" class="layui-input">
		</div>
	</div>

	<div class="layui-form-item">
		<label class="layui-form-label">排除</label>
		<div class="layui-input-block">
			<input name="msgExclude" class="layui-input">
		</div>
	</div>

	<c:forEach begin="0" end="7" var="v">
	<div class="layui-form-item d-send">
		<span class="layui-form-label" style="padding: 8px;">
			<span class="btn-send">发送${v+1}</span>
			<input name="sendHex" type="checkbox" lay-ignore>HEX
		</span>
		<div class="layui-input-block">
			<input name="sendData" class="layui-input">
		</div>
	</div>
	</c:forEach>


</form>

<div class="con-right">
	<ul id="msg" spellcheck="false"></ul>
</div>
</body>
<script src="${res}/plugin/xm-select.js"></script>
<script type="text/javascript">
	var roomId = param.room;
	var m = 0,devTypeSelect;
	var form = layui.form;
	var sendArr = layui.data("debug").send || [];
	var frames = $("#frames");
	var room = [
		{"name":"系统","id":"ROOT"},
		{"name":"通讯","id":"-1","children":[]},
		{"name":"设备","id":"-2","children":[]}
	];
	getSocket();

	form.render();

	common.jsonModel("tCommunication",{"isRemoved":0},function(data){
		$(data.list).each(function(){
			this.id = "COM-"+this.id;
		});
		room[1].children = data.list;
	});
	common.jsonModel("tDevice",{"isRemoved":0},function(data){
		$(data.list).each(function(){
			this.id = "DEV-"+this.id;
		});
		room[2].children = data.list;
	});

	common.ajaxStop(function () {
		devTypeSelect = xmSelect.render({
			el: '#s-type',
			theme: {
				color: '#0081ff',
			},
			data: room,
			prop:{"value":"id"},
			initValue: [roomId || "ROOT"],
			filterable: true,
			on: function(data){
				if(data.isAdd)
					ws.joinRoom(data.change[0].id);
				else
					ws.leftRoom(data.change[0].id);
			}
		});
	});

	var msgUl = document.getElementById("msg");
	function addMessage(msg){
		var li = document.createElement("li");
		msgUl.appendChild(li);
		if(msg.indexOf("Tx")>-1)
			$(li).addClass("li-tx");
		if(msg.indexOf("Rx")>-1)
			$(li).addClass("li-rx");
		if(msg.indexOf("Pa")>-1)
			$(li).addClass("li-pa");
		if(msg.indexOf("error")>-1)
			$(li).addClass("li-error");
		if(msg.indexOf("\033") > -1){
			msg = msg.replace(/\033\[\d+m/g,function (s) {
				if(s.indexOf("[0") > -1){
					return "</span>"
				}else{
					var style = s.replace("\033[","u-");
					return "<span class='"+style+"'>";
				}
			});
			li.innerHTML = msg;
		}else{
			li.innerText = msg;
		}

		msgUl.scrollTop = msgUl.scrollHeight;
		m++;
		frames.text(m);
		if(m > 1000)
			msgClear();
	}

	function msgClear(){
		$("#msg").empty();
		m=0;
	}

	$(".btn-send").click(function (){
		var d = $(this).parents("div");
		var p = {
			"id":devTypeSelect.getValue('value')[0].split("-")[1],
			"data":d.find("[name='sendData']").val(),
			"isHex":d.find("[name='sendHex']")[0].checked
		}
		var localData = [];
		$(".d-send").each(function (){
			localData.push({"data":$(this).find("[name='sendData']").val(),"isHex":$(this).find("[name='sendHex']")[0].checked})
		});
		layui.data("debug",{"key":"send",value:localData});
		common.jsonCont("commuSend",p,function (json){});

	});
	$(".d-send").each(function (i){
		var d = sendArr[i];
		if(d){
			$(this).find("[name='sendData']").val(d.data);
			$(this).find("[name='sendHex']")[0].checked = d.isHex;
		}
	});

	function getSocket(){
		ws.onopen = function(ev) {
			console.info("连接成功："+wsServer);
			if(devTypeSelect)
				$(devTypeSelect.getValue('value')).each(function (){
					ws.joinRoom(this);
				});
			else
				ws.joinRoom(roomId || "ROOT");
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
			var msg = evt.data;
			var receive = f1.receive.checked;
			var msgContain = f1.msgContain.value;
			var msgExclude = f1.msgExclude.value;
			var show = false;

			if(parent.callBackMsg){
				try {
					msg = parent.callBackMsg(msg) || msg;
				}catch (e){
					console.error(e);
				}
			}

			//接收
			if(!receive){
				return;
			}

			//包含
			if(msgContain && !isInclude(msg,msgContain)){
				return;
			}

			//排除
			if(msgExclude && isInclude(msg,msgExclude)){
				return;
			}

			var checked = $("input[name='show']:checked");
			if(checked.length == 0){
				show = true;
			}else{
				checked.each(function(){
					if(msg.indexOf(this.value) > -1){
						show = true;
					}
				});
			}

			if(show){
				addMessage(msg);
			}

		};
	}

	function isInclude(search,str){
		var sers = str.trim().split(" ");
		for (var i=0; i< sers.length;i++) {
			if (search.indexOf(sers[i]) > -1)
				return true
		}
	}

	//压力测试
	function stress(j){
		var t1 = Date.now();
		for(var i = 0;i<j;i++){
			addMessage("13:17:52.885 [C:43 D:9] SEND: 07#除湿器2 :07RA00000\\r\\n ASCII 巡检 COM8[9600]");
		}
		console.info("显示"+j+"条数据共耗时"+(Date.now()-t1)+"毫秒");
	}

</script>


</html>
