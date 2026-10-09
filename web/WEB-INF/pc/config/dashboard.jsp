<%@ page import="org.aiot.util.SystemInfo" %>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%
	request.setAttribute("startTime",SystemInfo.getStartTime());
%>
<!doctype html>
<html>
	<head>
	<%@include file="../common/page_head.jsp" %>
	<script src="${res}/plugin/echarts/5.4.3/echarts.min.js"></script>
	<link href="${res}/font/aiotfont/iconfont.css" rel="stylesheet" >
	<title>后台总览</title>

<style type="text/css">
html,body{
	height: 100%;
	background-color: #f5f6fa;
}
.chart{
	height: 250px;
}
/*固定高度可滚动的卡片内容区*/
.fix-body{
	height: 250px;
	overflow-y: auto;
}
/*tab 卡片（网络、记录）：tab 标题作为卡片 header*/
.layui-card > .layui-tab{
	margin: 0;
}
.layui-card > .layui-tab > .layui-tab-title{
	margin-bottom: 0;
}
.layui-card > .layui-tab > .layui-tab-content{
	height: 250px;
	overflow-y: auto;
	padding: 11px 15px;
}
.layui-tab-title .layui-badge{
	margin-left: 4px;
	padding: 0 5px;
	height: 16px;
	line-height: 16px;
}
/*记录：条目行间距稍大*/
.tab-rec .bar-line{
	margin-bottom: 18px;
}
/*记录：tab 左侧标题*/
.layui-tab-title .tab-title-txt{
	float: left;
	height: 40px;
	line-height: 40px;
	margin:0 15px;
	font-size: 14px;
	color: #333;
}
/*记录状态名称前的颜色点*/
.bar-line .name .dot{
	display: inline-block;
	width: 8px;
	height: 8px;
	margin-right: 4px;
	border-radius: 50%;
	vertical-align: middle;
}
.empty{
	color: #999;
	padding: 15px 0;
	text-align: center;
}
/*概况、点位 数字块 + 饼图*/
.ov-flex{
	display: flex;
	flex-wrap: wrap;
	align-items: center;
}
.ov-flex .stat-box{
	flex: 1;
	min-width: 400px;
}
.chart-point{
	width: 250px;
	height: 140px;
	margin-top: -10px;
	flex: none;
}
.stat-box{
	display: flex;
	flex-wrap: wrap;
}
.stat-item{
	flex: 1;
	min-width: 130px;
	text-align: center;
	padding: 12px 10px;
}
.stat-item .ioc image{
	height: 48px;
}
.stat-item .num{
	font-size: 26px;
	font-weight: 600;
	line-height: 1.4;
	color: #333;
	white-space: nowrap;
}
.stat-item .txt{
	color: #888;
	font-size: 13px;
}
.c-green{color: #16b777;}
.c-gray{color: #999;}
.c-red{color: #FF5722;}
.c-blue{color: #1E9FFF;}
/*CPU、内存曲线颜色，与 echarts LINE_COLORS 一致*/
.c-line1{color: #5470c6;}
.c-line2{color: #91cc75;}
.c-line3{color: #FFB800;}
/*磁盘、设备 类型条*/
.bar-line{
	display: flex;
	align-items: center;
	margin-bottom: 12px;
}
.bar-line .name{
	width: 120px;
	flex: none;
	color: #555;
	overflow: hidden;
	text-overflow: ellipsis;
	white-space: nowrap;
}
/*设备类型图标*/
.bar-line .name i{
	font-size: 20px;
	line-height: 20px;
	vertical-align: middle;
	margin-right: 5px;
	color: #666;
}
.bar-line .bar{
	flex: 1;
	height: 10px;
	margin: 0 10px;
	background: #f2f2f2;
	border-radius: 5px;
	overflow: hidden;
}
.bar-line .bar i{
	display: block;
	height: 100%;
	border-radius: 5px;
	background: #1E9FFF;
}
.bar-line .val{
	width: 150px;
	flex: none;
	color: #666;
	font-size: 12px;
	text-align: right;
	white-space: nowrap;
}
.bar-line .val-s{
	width: 46px;
}
/*网络*/
.net-item{
	display: flex;
	align-items: center;
	padding: 5px 0;
	color: #666;
}
.net-item .dot{
	width: 8px;
	height: 8px;
	flex: none;
	margin-right: 8px;
	border-radius: 50%;
	background: #ccc;
}
.net-item .dot.on{
	background: #16b777;
}
.net-item .net-name{
	max-width: 45%;
	overflow: hidden;
	text-overflow: ellipsis;
	white-space: nowrap;
}
.net-item .net-ip{
	flex: 1;
	margin-left: 10px;
	color: #999;
	font-size: 12px;
	overflow: hidden;
	text-overflow: ellipsis;
	white-space: nowrap;
	text-align: right;
}
/*插件*/
.plugin-item{
	display: flex;
	align-items: center;
	padding: 7px 0;
	border-bottom: 1px dashed #f2f2f2;
}
.plugin-item:last-child{
	border-bottom: none;
}
.plugin-item .plugin-name{
	flex: 1;
	margin-left: 10px;
	color: #999;
	font-size: 12px;
	overflow: hidden;
	text-overflow: ellipsis;
	white-space: nowrap;
}
/*记录*/
.card-tips{
	float: right;
	color: #999;
	font-size: 12px;
	font-weight: normal;
}
/*card-tips 中 CPU 数值固定宽度，避免数值变化时跳动*/
.tip-num{
	display: inline-block;
	min-width: 36px;
	text-align: right;
}
</style>

</head>
<body>
<div class="layui-fluid">
	<div class="layui-row layui-col-space15">

		<%--==================== 概况 ====================--%>
		<div class="layui-col-md12">
			<div class="layui-card">
				<div class="layui-card-header">概况
					<span class="card-tips" id="ovStartTime" style="float: initial;"></span>
				</div>
				<div class="layui-card-body">
					<div class="ov-flex">
						<div class="stat-box">
							<div class="stat-item">
								<div class="ico"><img src="${res}/images/ico/as.png"></div>
								<div class="num c-blue" id="ovRunTime">-</div>
								<div class="txt">已运行时间</div>
							</div>
							<div class="stat-item">
								<div class="ico"><img src="${res}/images/ico/crt.png"></div>
								<div class="num" id="ovUser">-</div>
								<div class="txt">用户数量</div>
							</div>
							<div class="stat-item">
								<div class="ico"><img src="${res}/images/ico/video.png"></div>
								<div class="num"><span id="vsTotal">-</span> / <span id="vsOnline" class="c-green">-</span></div>
								<div class="txt">视频源/在线</div>
							</div>
							<div class="stat-item">
								<div class="ico"><img src="${res}/images/ico/onnx.png"></div>
								<div class="num"><span id="amTotal">-</span> / <span id="amLoaded" class="c-green">-</span></div>
								<div class="txt">模型数/加载数</div>
							</div>
						</div>
						<div id="chartPoint" class="chart-point"></div>
					</div>
				</div>
			</div>
		</div>

		<%--==================== CPU ====================--%>
		<div class="layui-col-md4">
			<div class="layui-card">
				<div class="layui-card-header">CPU<span class="card-tips"><span class="c-line1">物理</span> <span id="cpuSys" class="tip-num">-</span> / <span class="c-line2">程序</span> <span id="cpuProc" class="tip-num">-</span></span></div>
				<div class="layui-card-body">
					<div id="chartCpu" class="chart"></div>
				</div>
			</div>
		</div>

		<%--==================== 内存 ====================--%>
		<div class="layui-col-md4">
			<div class="layui-card">
				<div class="layui-card-header">内存<span class="card-tips"><span class="c-line1">物理</span> <span id="memPhy">-</span>，<span class="c-line2">程序</span> <span id="memProc">-</span>，<span class="c-line3">堆</span> <span id="memHeap">-</span></span></div>
				<div class="layui-card-body">
					<div id="chartMem" class="chart"></div>
				</div>
			</div>
		</div>

		<%--==================== 磁盘 ====================--%>
		<div class="layui-col-md4">
			<div class="layui-card">
				<div class="layui-card-header">磁盘</div>
				<div class="layui-card-body fix-body" id="diskList">
					<div class="empty">加载中...</div>
				</div>
			</div>
		</div>

		<%--==================== 插件 ====================--%>
		<div class="layui-col-md4">
			<div class="layui-card">
				<div class="layui-card-header">插件</div>
				<div class="layui-card-body fix-body" id="pluginList">
					<div class="empty">加载中...</div>
				</div>
			</div>
		</div>

		<%--==================== 设备 ====================--%>
		<div class="layui-col-md4">
			<div class="layui-card">
				<div class="layui-card-header">设备<span class="card-tips">总数 <b id="devTotal" class="c-blue">-</b></span></div>
				<div class="layui-card-body fix-body" id="devTypes">
					<div class="empty">加载中...</div>
				</div>
			</div>
		</div>

		<%--==================== 记录：tab 当日、本周、本月 ====================--%>
		<div class="layui-col-md4">
			<div class="layui-card">
				<div class="layui-tab layui-tab-brief tab-rec">
					<ul class="layui-tab-title">
						<span class="tab-title-txt">记录</span>
						<li class="layui-this">当日 <span class="layui-badge layui-bg-gray" id="recCountToday">0</span></li>
						<li>本周 <span class="layui-badge layui-bg-gray" id="recCountWeek">0</span></li>
						<li>本月 <span class="layui-badge layui-bg-gray" id="recCountMonth">0</span></li>
					</ul>
					<div class="layui-tab-content">
						<div class="layui-tab-item layui-show" id="recToday"><div class="empty">加载中...</div></div>
						<div class="layui-tab-item" id="recWeek"><div class="empty">加载中...</div></div>
						<div class="layui-tab-item" id="recMonth"><div class="empty">加载中...</div></div>
					</div>
				</div>
			</div>
		</div>

		<%--==================== 串口 ====================--%>
		<div class="layui-col-md6">
			<div class="layui-card">
				<div class="layui-card-header">串口</div>
				<div class="layui-card-body fix-body" style="overflow-x: auto;">
					<table class="layui-table" lay-skin="line" style="margin: 0;border: none">
						<tbody id="serialBody">
						<tr><td colspan="3" class="empty">加载中...</td></tr>
						</tbody>
					</table>
				</div>
			</div>
		</div>

		<%--==================== 网络：tab 网口、wifi、蓝牙 ====================--%>
		<div class="layui-col-md6">
			<div class="layui-card">
				<div class="layui-tab layui-tab-brief">
					<ul class="layui-tab-title">
						<li class="layui-this">网口 <span class="layui-badge layui-bg-gray" id="netCountEth">0</span></li>
						<li>WiFi <span class="layui-badge layui-bg-gray" id="netCountWifi">0</span></li>
						<li>蓝牙 <span class="layui-badge layui-bg-gray" id="netCountBt">0</span></li>
					</ul>
					<div class="layui-tab-content">
						<div class="layui-tab-item layui-show" id="netEth"><div class="empty">加载中...</div></div>
						<div class="layui-tab-item" id="netWifi"><div class="empty">加载中...</div></div>
						<div class="layui-tab-item" id="netBt"><div class="empty">加载中...</div></div>
					</div>
				</div>
			</div>
		</div>

	</div>
</div>

</body>

<script type="text/javascript">
var MAX_POINT = 200;//图表最多保留的采样点数
var startTime = new Date(${startTime});
$("#ovStartTime").text("启动："+startTime.format("yyyy-MM-dd hh:mm:ss"));
tickRun();
setInterval(tickRun, 1000);

function pad(n){
	return n < 10 ? "0" + n : "" + n;
}
function fmtMS(ms){
	var d = new Date(ms);
	return pad(d.getMinutes()) + ":" + pad(d.getSeconds());
}

//字节转可读大小
function fmtSize(b){
	if(b == null)
		return "-";
	if(b < 1024 * 1024 * 1024)
		return (b / 1024 / 1024).toFixed(0) + " MB";
	return (b / 1024 / 1024 / 1024).toFixed(1) + " GB";
}
function esc(s){
	return $("<div>").text(s == null ? "" : s).html();
}
function usageColor(u){
	return u >= 0.9 ? "#FF5722" : (u >= 0.7 ? "#FFB800" : "#16baaa");
}

//==================== tab 切换（网络、记录）====================
$(document).on("click", ".layui-tab-title li", function(){
	var $li = $(this);
	$li.addClass("layui-this").siblings("li").removeClass("layui-this");
	//标题栏里可能有非 li 的标题文本，索引只按 li 计
	var idx = $li.parent().children("li").index($li);
	$li.parents(".layui-tab").first().find(".layui-tab-content > .layui-tab-item")
		.eq(idx).addClass("layui-show").siblings().removeClass("layui-show");
});

//==================== 总览信息（概况、磁盘、插件、模型、设备、点位）====================
common.ajax(base + "/json/getDashboardInfo", {}, function(d){
	//概况
	$("#ovUser").text(d.overview.userCount);

	//模型 显示在概况
	$("#amTotal").text(d.model.total);
	$("#amLoaded").text(d.model.loaded);

	renderDisk(d.disks);
	renderPlugin(d.plugins);
	renderDevice(d.device);
	renderPoint(d.point);
});

//网络：Windows 下网卡(含虚拟网卡)较多时枚举很慢，独立请求不阻塞首屏
common.ajax(base + "/json/getDashboardNetwork", {}, function(list){
	renderNetwork(list);
});

//流媒体：ZLMediaKit HTTP 调用较慢，独立请求不阻塞首屏
common.ajax(base + "/json/getDashboardVideo", {}, function(d){
	$("#vsTotal").text(d.total);
	$("#vsOnline").text(d.online);
});

//记录：多次数据库统计较慢，独立请求不阻塞首屏
common.ajax(base + "/json/getDashboardRecord", {}, function(d){
	renderRecord(d);
});
function tickRun(){
	var s = Math.floor((Date.now() - startTime.getTime()) / 1000);
	if(s < 0)
		s = 0;
	var txt = pad(Math.floor(s % 86400 / 3600)) + ":" + pad(Math.floor(s % 3600 / 60)) + ":" + pad(s % 60);
	var day = Math.floor(s / 86400);
	if(day > 0)
		txt = day + " 天 " + txt;
	$("#ovRunTime").text(txt);
}

//==================== CPU、内存 轮询累加 ====================
//曲线颜色，与 css 中 .c-line1/.c-line2/.c-line3 一致，card-tips 中名称用该颜色区分
var LINE_COLORS = ["#5470c6", "#91cc75", "#FFB800"];
var chartCpu = echarts.init(document.getElementById("chartCpu"));
var chartMem = echarts.init(document.getElementById("chartMem"));
chartCpu.setOption(lineOption(["物理CPU", "程序CPU"]));
chartMem.setOption(lineOption(["物理内存", "程序内存", "堆内存"]));

function lineOption(names){
	return {
		color : LINE_COLORS,
		tooltip : {
			trigger : "axis",
			valueFormatter : function(v){
				return v == null ? "-" : v + "%";
			}
		},
		grid : {left : 45, right : 15, top : 15, bottom : 25},
		xAxis : {type : "category", boundaryGap : false, data : []},
		yAxis : {type : "value", min : 0, max : 100, axisLabel : {formatter : "{value}%"}},
		series : names.map(function(n){
			return {name : n, type : "line", smooth : true, showSymbol : false, data : []};
		})
	};
}
//使用率 0~1 转百分比，不可用(-1)的采样点记为 null 断开曲线
function pct(v){
	return (v == null || v < 0) ? null : Math.round(v * 1000) / 10;
}

var perfTime = [], cpuSysData = [], cpuProcData = [], memPhyData = [], memProcData = [], heapData = [];

function applyPerfData(){
	chartCpu.setOption({xAxis : {data : perfTime}, series : [{data : cpuSysData}, {data : cpuProcData}]});
	chartMem.setOption({xAxis : {data : perfTime}, series : [{data : memPhyData}, {data : memProcData}, {data : heapData}]});
}

//==================== 采样数据 localStorage 缓存，页面打开时恢复历史曲线 ====================
var PERF_CACHE_KEY = "dashboardPerfCache";
function loadPerfCache(){
	try{
		var c = JSON.parse(localStorage.getItem(PERF_CACHE_KEY));
		if(c && c.v == 1 && $.isArray(c.t) && $.isArray(c.cs) && $.isArray(c.cp)
			&& $.isArray(c.mp) && $.isArray(c.mr) && $.isArray(c.h)){
			perfTime = c.t.slice(-MAX_POINT);
			cpuSysData = c.cs.slice(-MAX_POINT);
			cpuProcData = c.cp.slice(-MAX_POINT);
			memPhyData = c.mp.slice(-MAX_POINT);
			memProcData = c.mr.slice(-MAX_POINT);
			heapData = c.h.slice(-MAX_POINT);
			applyPerfData();
		}
	}catch(e){
		//localStorage 不可用或数据损坏时忽略，从空数据开始
	}
}
function savePerfCache(){
	try{
		localStorage.setItem(PERF_CACHE_KEY, JSON.stringify(
			{v : 1, t : perfTime, cs : cpuSysData, cp : cpuProcData, mp : memPhyData, mr : memProcData, h : heapData}));
	}catch(e){
		//写入失败（隐私模式/超出配额等）忽略
	}
}
loadPerfCache();

var pollCount = 0;
function pollPerf(){
	common.ajax(base + "/json/getDashboardPerf", {}, function(d){
		perfTime.push(fmtMS(d.time));
		cpuSysData.push(pct(d.cpuSystem));
		cpuProcData.push(pct(d.cpuProcess));
		memPhyData.push(pct(d.memPhysical));
		memProcData.push(pct(d.memProcess));
		heapData.push(pct(d.memHeap));
		if(perfTime.length > MAX_POINT){
			perfTime.shift();
			cpuSysData.shift();
			cpuProcData.shift();
			memPhyData.shift();
			memProcData.shift();
			heapData.shift();
		}
		applyPerfData();

		var cs = pct(d.cpuSystem), cp = pct(d.cpuProcess);
		$("#cpuSys").text(cs == null ? "-" : cs + "%");
		$("#cpuProc").text(cp == null ? "-" : cp + "%");
		$("#memPhy").text(fmtSize(d.physicalUsed) + "/" + fmtSize(d.physicalTotal));
		$("#memProc").text(d.processUsed == null || d.processUsed < 0 ? "-" : fmtSize(d.processUsed));
		$("#memHeap").text(fmtSize(d.heapUsed) + "/" + (d.heapMax > 0 ? fmtSize(d.heapMax) : "-"));

		//每5次采样缓存一次，作为下次打开页面时的初始数据
		if(++pollCount % 5 == 0)
			savePerfCache();
	});
}
pollPerf();
setInterval(pollPerf, 3000);

//==================== 点位：概况中的环形饼图 ====================
var chartPoint = echarts.init(document.getElementById("chartPoint"));
function renderPoint(d){
	chartPoint.setOption({
		title : {text : String(d.total), subtext : "点位总数", left : "center", top : "32%"},
		tooltip : {trigger : "item", formatter : "{b}: {c} ({d}%)"},
		legend : {bottom : 0, itemWidth : 10, itemHeight : 10, textStyle : {fontSize : 12}},
		series : [{
			type : "pie",
			radius : ["70%", "50%"],
			center : ["50%", "44%"],
			label : {show : false},
			data : [
				{name : "在线", value : d.online, itemStyle : {color : "#16b777"}},
				{name : "离线", value : d.offline, itemStyle : {color : "#999999"}},
				{name : "报警", value : d.alarm, itemStyle : {color : "#FF5722"}}
			]
		}]
	});
}

//==================== 磁盘 ====================
function renderDisk(list){
	var $d = $("#diskList").empty();
	if(!list || list.length == 0){
		$d.append('<div class="empty">未获取到磁盘信息</div>');
		return;
	}
	$(list).each(function(){
		var u = Math.round(this.usage * 1000) / 10;
		$d.append('<div class="bar-line">'
			+ '<span class="name">' + esc(this.path) + '</span>'
			+ '<div class="bar"><i style="width:' + u + '%;background:' + usageColor(this.usage) + '"></i></div>'
			+ '<span class="val">' + u + "%　" + fmtSize(this.total - this.free) + "/" + fmtSize(this.total) + '</span>'
			+ '</div>');
	});
}

//==================== 串口 ====================
common.ajax(base + "/json/getSerialPort", {}, function(list){
	var $t = $("#serialBody").empty();
	if(!list || list.length == 0){
		$t.append('<tr><td colspan="3" class="empty">未检测到串口</td></tr>');
		return;
	}
	$(list).each(function(){
		$t.append('<tr><td>' + esc(this.comPort) + '</td><td>' + esc(this.friendlyName) + '</td><td>' + esc(this.portDescription) + '</td></tr>');
	});
});

//==================== 网络：tab 显示 网口、wifi、蓝牙 ====================
function renderNetwork(list){
	var groups = {ethernet : [], wifi : [], bluetooth : []};
	$(list).each(function(){
		(groups[this.type] || groups.ethernet).push(this);
	});
	var ids = {ethernet : ["#netEth", "#netCountEth"], wifi : ["#netWifi", "#netCountWifi"], bluetooth : ["#netBt", "#netCountBt"]};
	$.each(groups, function(type, arr){
		$(ids[type][1]).text(arr.length);
		var $c = $(ids[type][0]).empty();
		if(arr.length == 0){
			$c.append('<div class="empty">无</div>');
			return;
		}
		$(arr).each(function(){
			$c.append('<div class="net-item"><i class="dot ' + (this.up ? "on" : "off") + '"></i>'
				+ '<span class="net-name" title="' + esc(this.displayName || this.name) + '">' + esc(this.displayName || this.name) + '</span>'
				+ '<span class="net-ip">' + esc((this.addrs || []).join("、")) + '</span></div>');
		});
	});
}

//==================== 插件 ====================
function renderPlugin(list){
	var $p = $("#pluginList").empty();
	$(list).each(function(){
		$p.append('<div class="plugin-item">' + esc(this.text)
			+ '<span class="plugin-name">' + esc(this.name) + '</span>'
			+ (this.installed ? '<span class="layui-badge layui-bg-green">已安装</span>' : '<span class="layui-badge layui-bg-gray">未安装</span>')
			+ '</div>');
	});
}

//==================== 设备 ====================
function renderDevice(d){
	$("#devTotal").text(d.total);
	var $t = $("#devTypes").empty();
	if(!d.types || d.types.length == 0){
		$t.append('<div class="empty">暂无设备</div>');
		return;
	}
	$(d.types).each(function(){
		var w = d.total > 0 ? Math.round(this.count / d.total * 100) : 0;
		$t.append('<div class="bar-line">'
			+ '<span class="name" title="' + esc(this.type) + '">'
			+ (this.icon ? '<i class="' + esc(this.icon) + '"></i>' : '') + esc(this.name || this.type) + '</span>'
			+ '<div class="bar"><i style="width:' + w + '%"></i></div>'
			+ '<span class="val val-s">' + this.count + '</span>'
			+ '</div>');
	});
}

//==================== 记录：tab 显示 当日、本周、本月，样式同设备卡 ====================
function renderRecord(d){
	[["today", "#recToday", "#recCountToday"], ["week", "#recWeek", "#recCountWeek"], ["month", "#recMonth", "#recCountMonth"]].forEach(function(p){
		var s = d[p[0]] || {};
		var total = s.total || 0;
		$(p[2]).text(total);
		var $c = $(p[1]).empty();
		if(total == 0){
			$c.append('<div class="empty">无记录</div>');
			return;
		}
		//前三个为记录状态（名称前带颜色点），后三个为复核流程状态，条宽为占总数的比例
		[["正常", s.normal, "#16b777", "#16b777"], ["预警", s.warning, "#FFB800", "#FFB800"], ["报警", s.alarm, "#FF5722", "#FF5722"],
			["待复核", s.pendingReview, "#1E9FFF", null], ["待处理", s.pendingProcess, "#1E9FFF", null], ["已处理", s.processed, "#1E9FFF", null]].forEach(function(r){
			var w = Math.round((r[1] || 0) / total * 100);
			$c.append('<div class="bar-line">'
				+ '<span class="name">' + (r[3] ? '<span class="dot" style="background:' + r[3] + '"></span>' : '') + r[0] + '</span>'
				+ '<div class="bar"><i style="width:' + w + '%;background:' + r[2] + '"></i></div>'
				+ '<span class="val val-s">' + (r[1] || 0) + '</span>'
				+ '</div>');
		});
	});
}

//==================== 图表自适应 ====================
window.onresize = function(){
	chartCpu.resize();
	chartMem.resize();
	chartPoint.resize();
};
</script>
</html>
