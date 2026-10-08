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
	<title>后台总览</title>

<style type="text/css">
html,body{
	height: 100%;
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
	width: 320px;
	height: 200px;
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
/*磁盘、设备 类型条*/
.bar-line{
	display: flex;
	align-items: center;
	margin-bottom: 12px;
}
.bar-line .name{
	width: 80px;
	flex: none;
	color: #555;
	overflow: hidden;
	text-overflow: ellipsis;
	white-space: nowrap;
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
.rec-grid{
	display: flex;
	background: #f8f8f8;
	border-radius: 5px;
	padding: 10px 0;
}
.rec-cell{
	flex: 1;
	text-align: center;
}
.rec-cell b{
	display: block;
	font-size: 20px;
	color: #333;
}
.rec-cell span{
	color: #888;
	font-size: 12px;
}
.card-tips{
	float: right;
	color: #999;
	font-size: 12px;
	font-weight: normal;
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
					<span class="card-tips" id="ovStartTime"></span>
				</div>
				<div class="layui-card-body">
					<div class="ov-flex">
						<div class="stat-box">
							<div class="stat-item">
								<div class="num c-blue" id="ovRunTime">-</div>
								<div class="txt">已运行时间</div>
							</div>
							<div class="stat-item">
								<div class="num" id="ovUser">-</div>
								<div class="txt">用户数量</div>
							</div>
							<div class="stat-item">
								<div class="num"><span id="vsTotal">-</span> / <span id="vsOnline" class="c-green">-</span></div>
								<div class="txt">视频源/在线</div>
							</div>
							<div class="stat-item">
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
				<div class="layui-card-header">CPU<span class="card-tips" id="cpuNow"></span></div>
				<div class="layui-card-body">
					<div id="chartCpu" class="chart"></div>
				</div>
			</div>
		</div>

		<%--==================== 内存 ====================--%>
		<div class="layui-col-md4">
			<div class="layui-card">
				<div class="layui-card-header">内存<span class="card-tips" id="memNow"></span></div>
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

		<%--==================== 串口 ====================--%>
		<div class="layui-col-md6">
			<div class="layui-card">
				<div class="layui-card-header">串口</div>
				<div class="layui-card-body fix-body" style="overflow-x: auto;">
					<table class="layui-table" lay-skin="line" style="margin: 0;">
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
				<div class="layui-tab layui-tab-brief">
					<ul class="layui-tab-title">
						<li class="layui-this">当日</li>
						<li>本周</li>
						<li>本月</li>
					</ul>
					<div class="layui-tab-content">
						<div class="layui-tab-item layui-show" id="recToday"><div class="empty">加载中...</div></div>
						<div class="layui-tab-item" id="recWeek"><div class="empty">加载中...</div></div>
						<div class="layui-tab-item" id="recMonth"><div class="empty">加载中...</div></div>
					</div>
				</div>
			</div>
		</div>

	</div>
</div>

</body>

<script type="text/javascript">
var MAX_POINT = 60;//图表最多保留的采样点数
var startTime = new Date(${startTime});
$("#ovStartTime").text("启动："+startTime.format("yyyy-MM-dd hh:mm:ss"));
tickRun();
setInterval(tickRun, 1000);

function pad(n){
	return n < 10 ? "0" + n : "" + n;
}
function fmtHMS(ms){
	var d = new Date(ms);
	return pad(d.getHours()) + ":" + pad(d.getMinutes()) + ":" + pad(d.getSeconds());
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
	$li.addClass("layui-this").siblings().removeClass("layui-this");
	$li.parents(".layui-tab").first().find(".layui-tab-content > .layui-tab-item")
		.eq($li.index()).addClass("layui-show").siblings().removeClass("layui-show");
});

//==================== 总览信息（概况、磁盘、网络、插件、流媒体、模型、设备、点位、记录）一次请求 ====================
common.ajax(base + "/json/getDashboardInfo", {}, function(d){
	//概况
	$("#ovUser").text(d.overview.userCount);

	//流媒体、模型 显示在概况
	$("#vsTotal").text(d.video.total);
	$("#vsOnline").text(d.video.online);
	$("#amTotal").text(d.model.total);
	$("#amLoaded").text(d.model.loaded);

	renderDisk(d.disks);
	renderNetwork(d.networks);
	renderPlugin(d.plugins);
	renderDevice(d.device);
	renderPoint(d.point);
	renderRecord(d.record);
});
function tickRun(){
	var s = Math.floor((Date.now() - startTime.getTime()) / 1000);
	if(s < 0)
		s = 0;
	var day = Math.floor(s / 86400);
	$("#ovRunTime").text(day + " 天 " + pad(Math.floor(s % 86400 / 3600)) + ":" + pad(Math.floor(s % 3600 / 60)) + ":" + pad(s % 60));
}

//==================== CPU、内存 轮询累加 ====================
var chartCpu = echarts.init(document.getElementById("chartCpu"));
var chartMem = echarts.init(document.getElementById("chartMem"));
chartCpu.setOption(lineOption(["整体CPU", "进程CPU"]));
chartMem.setOption(lineOption(["物理内存", "JVM堆内存"]));

function lineOption(names){
	return {
		tooltip : {
			trigger : "axis",
			valueFormatter : function(v){
				return v == null ? "-" : v + "%";
			}
		},
		legend : {data : names, top : 0},
		grid : {left : 45, right : 15, top : 35, bottom : 25},
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

var perfTime = [], cpuSysData = [], cpuProcData = [], memPhyData = [], memHeapData = [];
function pollPerf(){
	common.ajax(base + "/json/getDashboardPerf", {}, function(d){
		perfTime.push(fmtHMS(d.time));
		cpuSysData.push(pct(d.cpuSystem));
		cpuProcData.push(pct(d.cpuProcess));
		memPhyData.push(pct(d.memPhysical));
		memHeapData.push(pct(d.memHeap));
		if(perfTime.length > MAX_POINT){
			perfTime.shift();
			cpuSysData.shift();
			cpuProcData.shift();
			memPhyData.shift();
			memHeapData.shift();
		}
		chartCpu.setOption({xAxis : {data : perfTime}, series : [{data : cpuSysData}, {data : cpuProcData}]});
		chartMem.setOption({xAxis : {data : perfTime}, series : [{data : memPhyData}, {data : memHeapData}]});

		var cs = pct(d.cpuSystem), cp = pct(d.cpuProcess);
		$("#cpuNow").text("整体 " + (cs == null ? "-" : cs + "%") + " / 进程 " + (cp == null ? "-" : cp + "%"));
		$("#memNow").text("物理 " + fmtSize(d.physicalUsed) + "/" + fmtSize(d.physicalTotal)
			+ "，JVM堆 " + fmtSize(d.heapUsed) + "/" + fmtSize(d.heapMax));
	});
}
pollPerf();
setInterval(pollPerf, 2000);

//==================== 点位：概况中的环形饼图 ====================
var chartPoint = echarts.init(document.getElementById("chartPoint"));
function renderPoint(d){
	chartPoint.setOption({
		title : {text : String(d.total), subtext : "点位总数", left : "center", top : "32%"},
		tooltip : {trigger : "item", formatter : "{b}: {c} ({d}%)"},
		legend : {bottom : 0, itemWidth : 10, itemHeight : 10, textStyle : {fontSize : 12}},
		series : [{
			type : "pie",
			radius : ["42%", "62%"],
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
			+ '<span class="name" title="' + esc(this.type) + '">' + esc(this.name || this.type) + '</span>'
			+ '<div class="bar"><i style="width:' + w + '%"></i></div>'
			+ '<span class="val val-s">' + this.count + '</span>'
			+ '</div>');
	});
}

//==================== 记录：tab 显示 当日、本周、本月 ====================
function renderRecord(d){
	[["today", "#recToday"], ["week", "#recWeek"], ["month", "#recMonth"]].forEach(function(p){
		var s = d[p[0]] || {};
		$(p[1]).html('<div class="rec-grid">'
			+ '<div class="rec-cell"><b class="c-blue">' + (s.pendingReview || 0) + '</b><span>待复核</span></div>'
			+ '<div class="rec-cell"><b class="c-red">' + (s.pendingProcess || 0) + '</b><span>待处理</span></div>'
			+ '<div class="rec-cell"><b class="c-green">' + (s.processed || 0) + '</b><span>已处理</span></div>'
			+ '</div>');
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
