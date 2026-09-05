<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!doctype html>
<html>
<head>
	<title>视频监控</title>
	<%@include file="../common/page_head.jsp" %>
	<c:if test="${empty param.v}">
	<script src="${res}/device/hikvision/encryption/AES.js"></script>
	<script src="${res}/device/hikvision/encryption/cryptico.min.js"></script>
	<script src="${res}/device/hikvision/encryption/crypto-3.1.2.min.js"></script>
	</c:if>

	<style type="text/css">

		html,body,.divMain,.divLeft,#divPlugin{
			height: 100%;
		}
		#divPlugin {
			background-color: #343434;
			text-align: center;
		}

		.plugin-info{
			line-height: 50;
			display: inline-block;
			font-size: 16px;
			color: #fff;
		}

		.divRight {
			width: 200px;
			position: absolute;
			overflow: hidden;
			right: 0;
			height: 100%;
			padding-left: 20px;
		}

		.opinfo {
			font-size: 10px;
			clear: both;
		}
		.ptz-option{
			margin: 10px 0;
		}
		.ptz-option select{
			width: 130px;
			margin-bottom: 6px;
		}
		#shrinkage{
			position: absolute;
			left: -20px;
			top: 50%;
			font-size: 16px;
			color: #ccc;
		}

		#shrinkage:HOVER {
			cursor: pointer;
			color: #58b530;
		}
		.hide-right .layui-icon-next:BEFORE{
			content: "\e65a";
		}
		.hide-right .right{
			display: none;
		}
		.hide-right .divRight {
			width: 0px;
			display: none;
		}
		.hide-right .divLeft{
			margin-right: 0;
		}
		.divLeft{
			margin-right: 220px;
		}

		.ptz-ctrl-l {
			float: left;
			margin-bottom: 10px;
			width: 115px;
		}

		.ptz-ctrl-r {
			float: left;
			margin-bottom: 10px;
			text-align: right;
			width: 75px;
		}

		.ptz-ctrl-l .direction {
			float: left;
			width: 32px;
			height: 32px;
			margin: 0 4px 4px 0;
			cursor: pointer;
			background-image: url(${res}/device/hikvision/image/icons.png);
			background-color: #FFFFFF;
			background-position: 0 -90px;
		}

		.ptz-ctrl-l i.icon-ptz-left-up {
			background-position: 0 0;
		}

		.ptz-ctrl-l i.icon-ptz-up {
			background-position: -30px 0px;
		}

		.ptz-ctrl-l i.icon-ptz-right-up {
			background-position: -60px 0px;
		}

		.ptz-ctrl-l i.icon-ptz-left {
			background-position: 0 -30px;
		}

		.ptz-ctrl-l i.icon-ptz-auto {
			background-position: -30px -30px;
		}

		.ptz-ctrl-l i.icon-ptz-auto-sel {
			background-position: -126px -90px;
		}

		.ptz-ctrl-l i.icon-ptz-right {
			background-position: -60px -30px;
		}

		.ptz-ctrl-l i.icon-ptz-left-down {
			background-position: 0px -60px;
		}

		.ptz-ctrl-l i.icon-ptz-down {
			background-position: -30px -60px;
		}

		.ptz-ctrl-l i.icon-ptz-right-down {
			background-position: -60px -60px;
		}

		.ptz-ctrl-r i {
			float: left;
			width: 36px;
			height: 30px;
			margin: 1px 0 1px 1px;
			background: url(${res}/device/hikvision/image/icons.png) no-repeat;
		}

		.ptz-ctrl-r .operation {
			float: left;
			width: 75px;
			height: 32px;
			margin-bottom: 4px;
			cursor: pointer;
			background-image: url(${res}/device/hikvision/image/icons.png);
			background-color: #FFFFFF;
			background-position: -32px -90px;
		}

		.ptz-ctrl-r i.icon-ptz-zoomout {
			background-position: -90px 0;
		}

		.ptz-ctrl-r i.icon-ptz-zoomin {
			background-position: -126px 0;
		}

		.ptz-ctrl-r i.icon-ptz-focusout {
			background-position: -90px -30px;
		}

		.ptz-ctrl-r i.icon-ptz-focusin {
			background-position: -126px -30px;
		}

		.ptz-ctrl-r i.icon-ptz-irisout {
			background-position: -90px -60px;
		}

		.ptz-ctrl-r i.icon-ptz-irisin {
			background-position: -126px -60px;
		}

		.ptz-ctrl-l i {
			display: inline-block;
			width: 30px;
			height: 30px;
			margin: 1px 1px;
			background: url(${res}/device/hikvision/image/icons.png) no-repeat;
		}
		.ptz-other{
			margin-bottom: 10px;
		}
		.audio-div{
			clear: both;
			margin-bottom: 10px;
		}
		#setPreset{
			margin-left: 8px;
			display:none;
		}
	</style>
</head>
<body>
<div class="divMain">
	<div class="divRight">
		<div id="shrinkage">
			<i class="layui-icon layui-icon-next" style="font-size: 18px;position: absolute;left: 19px;"></i>
		</div>
		<div class="right">
			<%-- <div><a href="${app }/index/toCamera">拍照记录</a>
			<a href="http://station.iteasy.com/resources/download/WebComponentsKit.exe" target="_blank">插件下载</a>
			</div>	 --%>
			<div class="ptz-option">
				<div class="c01">
					画面数量
					<select class="sel2" onchange="changeWndNum(this.value);">
						<option value="1">1</option>
						<option value="2">4</option>
						<option value="3">9</option>
						<option value="4">16</option>
					</select>
				</div>
				<div class="c02">
					登录设备  <select id="ip" class="sel" onchange="changeIp(this)"></select>
				</div>
				<div class="c03">
					视频通道  <select id="channels" class="sel"></select>
				</div>
				<div class="c04">
				码流类型 <select id="streamtype" class="sel">
							<option value="2">标清</option>
							<option value="1">高清</option>
							<%--<option value="3">第三码流</option>
							<option value="4">转码码流</option>--%>
						</select>
				</div>
				<div class="c05">
					<input id="startTime" type="datetime-local" step="1" style="margin-bottom: 6px">
					<input id="endTime" type="datetime-local" step="1" style="margin-bottom: 6px">
				</div>
				<div class="c06">
					<input type="button"  value="开始预览" onclick="selRealPlay();" style="width: 90px;"/>
					<input type="button"  value="停止预览" onclick="clickStopRealPlay();" style="width: 90px;margin-left: 5px;"/>
				</div>
				<div class="c07">
					<input type="button"  value="全部预览" onclick="autoRealPlay();" style="width: 90px;margin-top: 5px"/>
					<input type="button"  value="全部停止" onclick="stopAll();" style="width: 90px;margin-left: 5px"/>
				</div>
				<div class="c08">
					<input type="button"  value="拍照" onclick="clickCapturePic();" style="width: 90px;margin-top: 5px;"/>
					<input type="button"  value="录像" onclick="videoRecord(this);" style="width: 90px;margin-left: 5px;"/>
				</div>
				<div class="c09">
					<input type="button"  value="回放" onclick="clickStartPlayback2();" style="width: 90px;margin-top: 5px;"/>
					<input type="button"  value="倒放" onclick="clickReversePlayback();" style="width: 90px;margin-left: 5px;"/>
				</div>
				<div class="c10">
					<input type="button"  value="快放" onclick="clickPlayFast();" style="width: 90px;margin-top: 5px;"/>
					<input type="button"  value="慢放" onclick="clickPlaySlow();" style="width: 90px;margin-left: 5px;"/>
				</div>
				<div class="c11">
					<input type="button"  value="暂停" onclick="clickPause();" style="width: 90px;margin-top: 5px;"/>
					<input type="button"  value="恢复" onclick="clickResume();" style="width: 90px;margin-left: 5px;"/>
				</div>
				<div class="c12">
					<input type="button"  value="3D放大" onclick="clickEnable3DZoom();" style="width: 90px;margin-top: 5px;"/>
					<input type="button"  value="停用3D" onclick="clickDisable3DZoom();" style="width: 90px;margin-left: 5px;"/>
				</div>
			</div>
			<div class="ptz-ctrl">
				<div class="ptz-ctrl-l">
					<span class="direction" onmousedown="PTZControl(5);" onmouseup="PTZControl(1,true);"><i class="icon-ptz-left-up"></i></span>
					<span class="direction" onmousedown="PTZControl(1);" onmouseup="PTZControl(1,true);"><i class="icon-ptz-up"></i></span>
					<span class="direction" onmousedown="PTZControl(7);" onmouseup="PTZControl(1,true);"><i class="icon-ptz-right-up"></i></span>
					<span class="direction" onmousedown="PTZControl(3);" onmouseup="PTZControl(1,true);"><i class="icon-ptz-left"></i></span>
					<span class="direction" onclick="PTZControl(9);"><i ng-class="{true:'icon-ptz-auto-sel', false:'icon-ptz-auto'}[oParams.bAuto]" class="icon-ptz-auto"></i></span>
					<span class="direction" onmousedown="PTZControl(4);" onmouseup="PTZControl(1,true);"><i class="icon-ptz-right"></i></span>
					<span class="direction" onmousedown="PTZControl(6);" onmouseup="PTZControl(1,true);"><i class="icon-ptz-left-down"></i></span>
					<span class="direction" onmousedown="PTZControl(2);" onmouseup="PTZControl(1,true);"><i class="icon-ptz-down"></i></span>
					<span class="direction" onmousedown="PTZControl(8);" onmouseup="PTZControl(1,true);"><i class="icon-ptz-right-down"></i></span>
				</div>
				<div class="ptz-ctrl-r">
					<span class="operation">
					    <i class="icon-ptz-zoomout" title="调焦 -" onmousedown="PTZControl(11)" onmouseup="PTZControl(1,true)"></i>
					    <i class="icon-ptz-zoomin" title="调焦 +" onmousedown="PTZControl(10)" onmouseup="PTZControl(1,true)"></i>
					</span>
					<span class="operation">
			            <i class="icon-ptz-focusout" title="聚焦 -" onmousedown="PTZControl(13)" onmouseup="PTZControl(1,true)"></i>
			            <i class="icon-ptz-focusin" title="聚焦 +" onmousedown="PTZFocusIn(12)" onmouseup="PTZControl(1,true)"></i>
			        </span>
					<span class="operation">
			            <i class="icon-ptz-irisout" title="光圈 -" onmousedown="PTZControl(15)" onmouseup="PTZControl(1,true)"></i>
			            <i class="icon-ptz-irisin" title="光圈 +" onmousedown="PTZControl(14)" onmouseup="PTZControl(1,true)"></i>
			        </span>
				</div>
			</div>
			<div class="ptz-other">
				<select id="preset" style="width: 105px" placeholder="预置点">
					<c:forEach var="i" begin="1" end="32">
						<option value="${i}">预置点 ${i}</option>
					</c:forEach>
					<option value="33">自动翻转</option>
					<option value="34">回到零点</option>
					<option value="35">巡航扫描1</option>
					<option value="36">巡航扫描2</option>
					<option value="37">巡航扫描3</option>
					<option value="38">巡航扫描4</option>
					<option value="39">白天模式</option>
					<option value="40">黑夜模式</option>
					<option value="41">花样扫描1</option>
					<option value="42">花样扫描2</option>
					<option value="43">花样扫描3</option>
					<option value="44">花样扫描4</option>
					<option value="45">一键巡航</option>
					<option value="46">日夜自动模式</option>
					<option value="92">设置手动限位</option>
					<option value="93">确认手动限位</option>
					<option value="94">远程重启</option>
					<option value="95">主菜单</option>
					<option value="96">停止扫描</option>
					<option value="97">随机扫描</option>
					<option value="98">帧扫描</option>
					<option value="99">自动扫描</option>
					<option value="100">垂直扫描</option>
					<option value="101">全景扫描</option>
				</select>
				<span onclick="cSetPreset()" id="setPreset">设置</span>
				<span onclick="cGoPreset()" style="border-left: 1px dashed #ccc;padding-left: 5px;margin-left: 3px">调用</span>
			</div>
			<div class="audio-div">
				<select id="audiochannels" style="width: 105px"></select>
				<span  value="" onclick="clickStartVoiceTalk()" style="margin-left: 8px;">对讲</span>
				<span  value="" onclick="clickStopVoiceTalk()" style="border-left: 1px dashed #ccc;padding-left: 5px;margin-left: 3px">停止</span>
			</div>

			<div class="video-page-div" style="margin-bottom: 5px;">
				<select id="videoPage" onchange="playPage(this.value)" style="width: 105px;"></select>
				<input id="carouselStep" type="number" min="0" value="10" step="5" style="width: 32px;margin-left: 5px;"><span  onclick="carouselPlay()" style="margin-left: 8px;">轮播</span>
			</div>

			<div id="opinfo" class="opinfo"></div>
		</div>
	</div>
	<div class="divLeft">
		<div id="divPlugin" class="plugin">
			<span class="plugin-info">...</span>
		</div>
	</div>

</div>
</body>
<c:if test="${empty param.v}">
<script id="videonode" src="${res}/device/hikvision/webVideoCtrl.js"></script>
<script src="${res}/device/hikvision/hkws.js"></script>
</c:if>
<c:if test="${3.0 == param.v}">
	<script id="videonode" src="${res}/device/hikvision/3.0/webVideoCtrl.js"></script>
	<script src="${res}/device/hikvision/3.0/hkws.js"></script>
</c:if>
<script type="text/javascript">
	/**
	 * 网址传参 ?d=72,73&channel=1-1,2-2;4,3,2 （通道-码流类型）
	 * 方法 mouseDownPTZControl
	 * 参数 1上 2下 3左 4右 5左上 6左下 7右上 8右下 9旋转
	 */
	var d = param.d;
	var channel = param.channel || "";//通道 1,2,3|2,3,4
	var stream = param.stream || 2;
	var backTime = param.backTime || param.recTime;
	var backTimeEnd = param.backTimeEnd;//回放截止时间
	var backTimeMin = param.backTimeMin;//回放时长分钟

	var gd=[],gc=[],gip=[],channelList = [];
	var winCount = 1;//当前窗口总数
	var pageNum = 1;//当前播放第几页
	var devicemode = 1;//模式 1、IPServer 2、HiDDNS

	var date = new Date();
	var szRealTime = date.getTime();
	var timeZone = date.getTimezoneOffset();

	$(".divMain").addClass(param.style);

	var $videoPage = $("#videoPage");

	/*common.ajax("${base}/user/getAction",{},function (data) {
		if(data.PRESET_SET)
			$("#setPreset").show();
	});*/

	function winNum(count){
		if(count > 9)
			return 4;
		if(count > 4)
			return 3;
		if(count > 1)
			return 2;
		return 1;
	}

	$(function () {
		gd = d.split(",");
		gc = channel.split(";");
		$("#streamtype").val(stream);

		if(backTime){
			$("#startTime").val(backTime.replace(" ","T"));
			$("#streamtype").val(1);//有的回放没有子码流，所以默认回放为主码流
			if(backTimeMin){
				var backDate = new Date(backTime).getTime();
				var backEndDate = new Date(backDate + backTimeMin*60*1000);//+ timeZone*60*1000
				$("#endTime").val(backEndDate.format("yyyy-MM-ddThh:mm:ss"));
			}
		}else{
			$("#startTime").val(new Date().format("yyyy-MM-ddThh:mm:ss"));
		}
		$(d.split(",")).each(function (index) {
			load(this,index);
		});
	});

	function load(deviceId,index) {
		common.ajax("${base}/json/device",{id:deviceId},function(data){
			var szIP = data.host;
			var szPort = 80;
			var szNet = data.net;//外网
			var szWeb = data.web;//内网不同网段
			var szNetTimeOut = data.netTimeOut || 60;

			if(szWeb && location.host.indexOf("808") == -1){
				szNets = szWeb.split(":");
				szIP = szNets[0];
				szPort = szNets[1];
			}
			if(szNet && location.host.indexOf("nat") > -1){
				szNets = szNet.split(":");
				szIP = szNets[0];
				szPort = szNets[1];
				setInterval(autoStopPlay,1000);//外网访问 自动停止
			}

			gip[index] = szIP;

			login(szIP,szPort,data.user,data.password,data.device.name,index);
		});
	}

	var loginCount = 0;
	function login(szIP,szPort,szUsername,szPassword,name,index){

		WebVideoCtrl.I_Login(szIP, 1, szPort, szUsername, szPassword, {
			success: function (xmlDoc) {

				showOPInfo("登录成功 "+szIP+":"+szPort);
				$("#ip").append("<option value='" + szIP + "'>" + name + "</option>");
				if(channel) {
					var cs = channel.split(/,|;/);
					changeWndNum(winNum(cs.length));
				}
				setTimeout(function () {
					loginCount ++;
					getChannel(szIP);//获取视频通道
					$("#channels option").not('[ip="'+$("#ip").val()+'"]').hide();
					if(loginCount == gd.length){
						loginStop();
					}

				}, 20);
			},
			error: function () {
				loginCount++;
				showOPInfo("登录失败"+szIP+":"+szPort);
			}
		});
	}

	function loginStop() {
		if(channel){
			channelList = chanelsRender(channel);
			var page = parseInt((channelList.length-1) / 16);
			if(page > 0){
				$(".video-page-div").show();
				for(var i=0;i<page+1;i++){
					$videoPage.append("<option value='"+i+"'>第 "+(i+1)+"/"+(page+1)+" 页</option>");
				}
			}
			var szEndTime = $("#endTime").val().replace("T"," ");
			chanelPlay(channel,backTime,szEndTime);
		}else{
			var chs = $("#channels")[0].childElementCount;
			changeWndNum(winNum(chs));
			autoRealPlay(backTime);
		}
		videoSelWnd();
	}

	function changeIp(input) {
		$("#channels option").show().not('[ip="'+input.value+'"]').hide();
		$("#channels option[ip='"+input.value+"']").eq(0).attr("selected","selected");
		clickGetAudioInfo();//获取对讲通道
	}

	function getWinIndex(cindex) {
		var i = 0;
		for(var j=0;j<cindex;j++){
			i += gc[j].split(",").length;
		}
		return i;
	}

	function getChannel(ip){
		var oSel = $("#channels");
		var szDeviceIdentify = ip;

		if (null == szDeviceIdentify) {
			return;
		}

		// 模拟通道 （单摄像头一般是模拟通道）
		WebVideoCtrl.I_GetAnalogChannelInfo(szDeviceIdentify, {
			async: false,
			success: function (xmlDoc) {
				var oChannels = $(xmlDoc).find("VideoInputChannel");

				$.each(oChannels, function (i) {
					var id = $(this).find("id").eq(0).text(),
							name = $(this).find("name").eq(0).text();
					if ("" == name) {
						name = "Camera " + (i < 9 ? "0" + (i + 1) : (i + 1));
					}
					oSel.append("<option value='" + id + "' bZero='false' ip='"+ ip +"'>" + name + "</option>");
				});
				showOPInfo(szDeviceIdentify + " 获取模拟通道成功！");
			},
			error: function (status, xmlDoc) {
				showOPInfo(szDeviceIdentify + " 获取模拟通道失败！", status, xmlDoc);
			}
		});
		// 数字通道
		WebVideoCtrl.I_GetDigitalChannelInfo(szDeviceIdentify, {
			async: false,
			success: function (xmlDoc) {
				var oChannels = $(xmlDoc).find("InputProxyChannelStatus");

				$.each(oChannels, function (i) {
					var id = $(this).find("id").eq(0).text(),
							name = $(this).find("name").eq(0).text(),
							online = $(this).find("online").eq(0).text();
					if ("false" == online) {// 过滤禁用的数字通道
						return true;
					}
					if ("" == name) {
						name = "IPCamera " + (i < 9 ? "0" + (i + 1) : (i + 1));
					}
					oSel.append("<option value='" + id + "' bZero='false' ip='"+ ip +"'>" + name + "</option>");
				});
				showOPInfo(szDeviceIdentify + " 获取数字通道成功！");
			},
			error: function (status, xmlDoc) {
				showOPInfo(szDeviceIdentify + " 获取数字通道失败！", status, xmlDoc);
			}
		});
		// 零通道
		/*WebVideoCtrl.I_GetZeroChannelInfo(szDeviceIdentify, {
			async: false,
			success: function (xmlDoc) {
				var oChannels = $(xmlDoc).find("ZeroVideoChannel");

				$.each(oChannels, function (i) {
					var id = $(this).find("id").eq(0).text(),
							name = $(this).find("name").eq(0).text();
					if ("" == name) {
						name = "Zero Channel " + (i < 9 ? "0" + (i + 1) : (i + 1));
					}
					if ("true" == $(this).find("enabled").eq(0).text()) {// 过滤禁用的零通道
						oSel.append("<option value='" + id + "' bZero='true' ip='"+ ip +"'>" + name + "</option>");
					}
				});
				showOPInfo(szDeviceIdentify + " 获取零通道成功！");
			},
			error: function (status, xmlDoc) {
				showOPInfo(szDeviceIdentify + " 获取零通道失败！", status, xmlDoc);
			}
		});*/
	}

	// 窗口分割
	function changeWndNum(iType) {
		$(".sel2").val(iType);
		iType = parseInt(iType);
		for(var i = iType * iType;i< winCount;i++){
			WebVideoCtrl.I_Stop({"iWndIndex" : i});
		}
		WebVideoCtrl.I_ChangeWndNum(iType);
		winCount = iType * iType;

		var chs = $("#channels")[0].childElementCount;
		$videoPage.empty();
		for(var i=0;i<chs/winCount;i++){
			$videoPage.append("<option value='"+i+"'>第 "+(i+1)+" 页</option>");
		}
		if ($videoPage[0].children.length > 1){
			$(".video-page-div").show();
		}else{
			$(".video-page-div").hide();
		}
	}

	//窗口选择
	function videoSelWnd(xmlDoc) {
		var json = WebVideoCtrl.I_GetWindowStatus(g_iWndIndex);
		if(!json)
			return;
		var channel = json.iChannelID;
		if(channel > 32)
			channel = channel -32;
		$("#ip [value='"+json.szIP+"']").attr("selected","selected");
		$("#ip").change();
		var sel = $("#channels [ip='"+json.szIP+"'][value='"+channel+"']");
		sel.attr("selected","selected");

		var channelName = sel.text();
		//console.info(sel);
		if(channelName && channelName.indexOf("@")==0){
			$(".ptz-ctrl").hide();
		}else{
			$(".ptz-ctrl").show();
		}
	}

	//选择预览
	function selRealPlay(){
		clickStartRealPlay();
		szRealTime = new Date().getTime();
	}

	function stopAll(){
		for(var i=0;i<16;i++){
			WebVideoCtrl.I_Stop({iWndIndex : i});
		}
	}

	//全部预览
	function autoRealPlay(bTime) {
		var chanelOpts = $("#channels")[0].children;
		for(var i = 0;i < Math.min(chanelOpts.length,winCount);i++){
			var oWndInfo = WebVideoCtrl.I_GetWindowStatus(i);
			if(oWndInfo != null){
				WebVideoCtrl.I_Stop({"iWndIndex" : i,success: function () {
						realPlay($(chanelOpts[i]).attr("ip"),i,chanelOpts[i].value,bTime,null,$("#streamtype").val());
					}
				});
			}else{
				realPlay($(chanelOpts[i]).attr("ip"),i,chanelOpts[i].value,bTime,null,$("#streamtype").val());
			}

		}
	}

	//按通道播放
	function chanelPlay(chanels,szStartTime,szEndTime) {
		var chanelArr = chanelsRender(chanels);
		changeWndNum(winNum(chanelArr.length));
		for(var i=0;i<16;i++){
			var c = chanelArr[i];
			if(c){
				var ch = c.chanel.split("-");
				var streamType = ch[1];
				var oWndInfo = WebVideoCtrl.I_GetWindowStatus(i);
				if(oWndInfo != null){
					WebVideoCtrl.I_Stop({iWndIndex : i,success: function () {
							if(c)
								realPlay(c.ip,i,ch[0],szStartTime,szEndTime,streamType);
						}
					});
				}else if(c){
					realPlay(c.ip,i,ch[0],szStartTime,szEndTime,streamType);
				}
			}else{
				WebVideoCtrl.I_Stop({iWndIndex : i});
			}
		}

	}

	//按页播放
	function playPage(page) {
		var chanelOpts = $("#channels")[0].children;
		for(var i=0;i<winCount;i++){
			var c = chanelOpts[page * winCount + i];
			if(c){
				//var ch = c.chanel.split("-");
				var ch = c.value;
				var streamType = $("#streamtype").val();
				var oWndInfo = WebVideoCtrl.I_GetWindowStatus(i);
				if(oWndInfo != null){
					WebVideoCtrl.I_Stop({iWndIndex : i,success: function () {
							if(c)
								realPlay(c.getAttribute("ip"),i,ch,null,null,streamType);
						}
					});
				}else if(c){
					realPlay(c.getAttribute("ip"),i,ch,null,null,streamType);
				}
			}else{
				WebVideoCtrl.I_Stop({iWndIndex : i});
			}
		}
	}

	//播放下一页
	function playPageNext(){
		var count = $videoPage[0].children.length;
		var p = $videoPage.val();//当前页
		p = parseInt(p)+1;
		if(p >= count)
			p = 0;
		$videoPage.val(p);
		playPage(p);
	}

	//轮播
	var si;
	function carouselPlay(){
		var s =  parseInt($("#carouselStep").val());
		clearInterval(si);
		if(s){
			si = setInterval(playPageNext,s*1000);
			showOPInfo("开始轮播，间隔"+s+"s");
		}else{
			showOPInfo("结束轮播");
		}
	}

	//字符串通道转换
	function chanelsRender(chanels) {
		var chanelArr = [];
		var szi = chanels.split(";");
		for(var i=0;i<szi.length;i++){
			var ch = szi[i].split(",");
			for(var j=0;j<ch.length;j++){
				if(ch[j])
					chanelArr.push({
						"ip":gip[i],
						"chanel":ch[j]
					});
			}
		}
		return chanelArr;
	}

	//播放
	function realPlay(szIp,wndIndex,channel,szStartTime,szEndTime,streamType) {
		var oWndInfo = WebVideoCtrl.I_GetWindowStatus(wndIndex);
		var startPlayback = function (){
			//回放
			if(szStartTime){
				var szDeviceIdentify = szIp;//3.1版本 +"_"+80 ?
				WebVideoCtrl.I_StartPlayback(szDeviceIdentify, {
					//iRtspPort: 554,
					iWndIndex : wndIndex,//不传为当前窗口
					iStreamType: 1, //有的刻录机不支持子码流回放
					iChannelID: parseInt(channel),
					szStartTime: szStartTime,//开始时间，默认为当天 00:00:00 格式如 2012-12-12 12:12:12
					szEndTime: szEndTime || undefined,//空或null会导致不能播放
					success: function () {

					},
					error: function (status, xmlDoc) {

					}
				});
			}else{
				WebVideoCtrl.I_StartRealPlay(szIp, {
					iWndIndex : wndIndex,//窗口
					iChannelID: parseInt(channel) ,//通道
					iStreamType: parseInt(streamType) || 2,//码流类型 1、主码流 2子码流 红外一般不支持子码流
					bZeroChannel: false,
					success: function () {
						showOPInfo("通道"+channel+" 码流"+streamType + " 播放成功");
					},
					error: function (status, xmlDoc) {
						showOPInfo("通道"+channel+" 码流"+streamType + " 播放失败",status, xmlDoc);
					}
				});
			}
			szRealTime = new Date().getTime();
		};
		if (oWndInfo != null) {// 已经在播放了，先停止
			WebVideoCtrl.I_Stop({
				success: function () {
					startPlayback();
				}
			});
		} else {
			startPlayback();
		}
	}

	//回放
	function clickStartPlayback2() {
		var szIp = $("#ip").val();
		var channel = $("#channels").val();
		var startTime = ($("#startTime").val()||"").replace("T"," ");
		var endTime = ($("#endTime").val()||"").replace("T"," ");
		realPlay(szIp,g_iWndIndex,channel,startTime,endTime);
	}

	//设置预置点
	function cSetPreset() {
		var oWndInfo = WebVideoCtrl.I_GetWindowStatus(g_iWndIndex),
				iPresetID = parseInt($("#preset").val(), 10);

		if (oWndInfo != null) {
			WebVideoCtrl.I_SetPreset(iPresetID, {
				success: function (xmlDoc) {
					showOPInfo(oWndInfo.szIP + " 设置预置点成功！");
				},
				error: function () {
					showOPInfo(oWndInfo.szIP + " 设置预置点失败！");
				}
			});
		}
	}

	// 调用预置点
	function cGoPreset() {
		var iPresetID = parseInt($("#preset").val(), 10);
		clickGoPreset(iPresetID);
	}

	//录像
	function videoRecord(btn) {
		var oWndInfo = WebVideoCtrl.I_GetWindowStatus(g_iWndIndex);
		if(oWndInfo != null){
			if(btn.isRecord){
				WebVideoCtrl.I_StopRecord({
					success: function () {
						btn.isRecord = 0;
						showOPInfo(oWndInfo.szDeviceIdentify + " 已停止录像");
					},
					error: function () {
						showOPInfo(oWndInfo.szDeviceIdentify + " 停止录像失败");
					}
				});
			}else{
				var szChannelID = $("#channels").val(),
						szFileName = oWndInfo.szDeviceIdentify + "_" + szChannelID + "_" + new Date().getTime();

				WebVideoCtrl.I_StartRecord(szFileName, {
					bDateDir: true, //是否生成日期文件
					success: function () {
						btn.isRecord = 1;
						showOPInfo(oWndInfo.szDeviceIdentify + " 开始录像成功！");
					},
					error: function () {
						showOPInfo(oWndInfo.szDeviceIdentify + " 开始录像失败！");
					}
				});
			}

		}
	}

	function autoStopPlay(){
		if(new Date().getTime() - szRealTime > szNetTimeOut*1000){
			var iWndNum = $(".sel2").val();
			var s = false;
			for(var i = 0;i < iWndNum * iWndNum;i++){
				var oWndInfo = WebVideoCtrl.I_GetWindowStatus(i);
				if (oWndInfo != null) {
					WebVideoCtrl.I_Stop(i);
					s = true;
				}
			}
			if(s){
				setTimeout(function () {
					if(window.confirm("外网访问流量限制，"+szNetTimeOut+"秒自动停止，点击确定继续预览")){
						autoRealPlay()
					}
				},2000);

			}
		}
	}

	$("#shrinkage").click(function(){
		if($(".divMain").hasClass("hide-right")){
			$(".divMain").removeClass("hide-right");
			$(this).addClass("glyphicon-chevron-right");
		}else{
			$(".divMain").addClass("hide-right")
			$(this).removeClass("glyphicon-chevron-right")
		}
	});

	/**
	 * 云台方向控制
	 * iPTZIndex 操作类型（1-上，2-下，3-左，4-右，5-左上，6-左下，7-右上，8-右下，9-自转，10-调焦+， 11-调焦-, 12-F 聚焦+, 13-聚焦-, 14-光圈+, 15-光圈-）
	 * bStop 是否停止iPTZIndex 指定的操作，true|false
	 * options 可选参数对象
	 iWndIndex 窗口号，默认为当前选中窗口
	 iPTZSpeed 云台速度，默认为4
	 */
	var g_bPTZAuto = false;
	function PTZControl(iPTZIndex,bStop,options){
		var options = options || {};
		var oWndInfo = WebVideoCtrl.I_GetWindowStatus(g_iWndIndex),
				bZeroChannel = $("#channels option").eq($("#channels").get(0).selectedIndex).attr("bZero") == "true" ? true : false;

		if (bZeroChannel) {// 零通道不支持云台
			return;
		}

		if (oWndInfo != null) {
			if (9 == iPTZIndex && g_bPTZAuto) {
				iPTZSpeed = 0;// 自动开启后，速度置为0可以关闭自动
			} else {
				g_bPTZAuto = false;// 点击其他方向，自动肯定会被关闭
			}

			WebVideoCtrl.I_PTZControl(iPTZIndex, bStop, {
				iPTZSpeed: options.iPTZSpeed || 4,
				success: function (xmlDoc) {
					if (9 == iPTZIndex) {
						g_bPTZAuto = !g_bPTZAuto;
					}
					showOPInfo(oWndInfo.szIP + " 云台操作成功！");
				},
				error: function () {
					showOPInfo(oWndInfo.szIP + " 云台操作失败！");
				}
			});
		}
	}
</script>


</html>
