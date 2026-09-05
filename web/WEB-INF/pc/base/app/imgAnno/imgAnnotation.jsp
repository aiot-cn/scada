<%@ page import="java.util.Arrays" %>
<%@ page import="org.aiot.service.BaseService" %>
<%@ page import="org.aiot.main.Constants" %>
<%@ page import="org.aiot.device.base.imgAnno.ImgAnnoDevice" %>
<%@ page import="org.aiot.device.base.imgAnno.model.ImgProject" %>
<%@ page import="org.aiot.service.DeviceService" %>
<%@ page contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%
	BaseService bs = Constants.ioc.get(BaseService.class);
	DeviceService ds = Constants.ioc.get(DeviceService.class);
	Long id = Long.parseLong(request.getParameter("id"));
	Long devId = Long.parseLong(request.getParameter("d"));
	ImgAnnoDevice imageDevice = (ImgAnnoDevice) ds.getInstance(devId);
	request.setAttribute("bd",imageDevice);
	request.setAttribute("project",bs.query(ImgProject.class,id));
%>
<!doctype html>
<html>
	<head>
		<meta name="description" content="${project.path}">
		<%@include file="../../../common/page_head.jsp" %>
		<title>标注-${project.name} ${param.dir}</title>
		<link href="${res}/font/aiotfont/iconfont.css" rel="stylesheet" >
		<script src="${res}/plugin/iUI/iUI.js"></script>
		<script src="${res}/js/PinYin.js"></script>
		<meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=0">
		
<style type="text/css">
	html {
		touch-action: manipulation;
		background-color: #f2f4f7;
	}
	body{
		-background-color: #272b30;
	}
	.show{
		display: initial !important;
	}
	.itable tbody .itable-rownum {
		counter-increment: row-no 1;
	}
	.it-check{
		cursor: pointer;
	}


	.dl,.dc,.dr,#di{
		-border: 1px solid red;
		position: absolute;
		top : 0px;
		bottom: 0px;
	}
	.dl{
		width: 200px;
		background-color: #fff;
		padding: 10px;
	}

	.dc{
		left: 230px;
		right: 275px;
	}
	.dr{
		right: 10px;
		width: 250px;
	}
	.dr-con{
		overflow: auto;
		width: 100%;
		height: calc(100% - 72px);
	}
	.dr-tool{
		height: 32px;
		line-height: 30px;
		border-bottom: 1px solid #ccc;
		padding: 5px;
	}
	.dr-tool span{
		margin-right: 5px;
	}
	.dr-tool i{
		font-size: 16px;
		cursor: pointer;
	}
	.dr-tool i:hover{
		color: #0cad13;
	}
	.dr-foot{
		padding: 5px;
	}
	#di{
		bottom: 10px;
		top : 55px;
		width: 100%;
		box-sizing: border-box;
		overflow: hidden;
		background-image: radial-gradient(circle, #E2E4EC 1px, transparent 1px);
		background-size: 20px 20px;
	}

	#tImgTag{
		counter-reset:no-tag -1;
	}
	#tImgTag thead,.itable-edit{
		display: none;
	}
	#tImgTag tbody td{
		cursor: default !important;
	}
	#tImgTag tr td:last-child{
		width: 20px;
		padding: 5px;
		text-align: center;
	}

	#tImgTag .tag-color{
		counter-increment:no-tag 1;
		text-align: center;
		color: #fff;
		border-right: none;
		width: 10px;
		font-size: 12px;
		padding: 0;
	}
	#tImgTag .tag-color:before{
		content: counter(no-tag);
	}

	#tImgTag .tag-color .layui-input{
		border: none;
		padding: 0;
		width: 15px;
	}

	#tag-count{
		color: red;font-size: 12px;
		cursor:pointer;
	}

	.ul-img{
		overflow: auto;
		height: calc(100% - 30px);
		counter-reset:no-li 0;
	}

	.ul-img li{
		opacity: 0.7;
		position: relative;
	}
	.ul-img li:hover{
		opacity: 1;
	}
	.ul-img li.active{
		background-color: #009688;
		color: #fff;
		opacity: 1;
	}

	.ul-img li img{
		width: 100%;
		max-height: 200px;
	}
	.ul-img div{
		font-size: 12px;
	}
	.img-index{
		color: #E8902D;
		counter-increment:no-li 1;
		position:absolute;
		right: 2px;
		margin: 2px;
	}
	.img-index:before{
		content: counter(no-li);
	}
	.img-name{
		padding: 2px 15px 2px 2px;
		display: inline-block;
		word-break: break-all;
	}
	#img-name{
		line-height: 42px;
		color: #6b737a;
	}
	.tool{
		border-bottom: 1px solid #ccc;
		height: 42px;
		position: relative;
		z-index: 10000;
	}
	.tool li{
		float: left;
		width: 48px;
		height: 42px;
		line-height: 42px;
		font-size: 18px;
		text-align: center;
		cursor: pointer;
		color: #7e898f;
	}

	.tool li.active{
		color: #0a76be;
	}
	.tool li:hover{
		background-color: #ddd;
	}
	.tool li i{
		font-size: 20px;
	}

	#dm{
		user-select:none;
		-webkit-user-select:none;
		position: absolute;
	}
	#img{
		width: 100%;
	}
	[data-label]{
		position: absolute;
		border: 2px dashed #fff;
		box-sizing: border-box;
		cursor: default;
	}
	[data-label]:hover{
		box-shadow: 0px 0px 5px #fff;
		-z-index: 10000 !important;
	}
	[data-label]:hover .tag{
		display: inline;
	}

	.active[data-label]{
		border-style: solid;
		z-index: 10001 !important;
	}
	[data-label] .tag{
		color: #fff;
		position: absolute;
		padding: 0 5px;
		font-size: 12px;
		top: -18px;
		left: -2px;
		white-space: nowrap;
		-pointer-events: none;
	}
	[data-label] .tag:after{
		content:  attr(data-name) " " attr(data-px) " " attr(data-mm);
	}
	.show-group [data-label] .tag:before{
		content: attr(data-group) " ";
	}
	.hide-tag .tag{
		display: none;
	}

	.resize{
		display: inline-block;
		position: absolute;
		width: 8px;
		height: 8px;
	}
	.resize[data-type="1"]{
		bottom: -5px;
		right: -5px;
		cursor: se-resize;
	}
	[data-count]{
		color: #5A50CC;
		font-size: 12px !important;
		padding: 0 !important;
		text-align: right;
	}

	.d-help tr td:nth-child(even){
		color: #03a1ab;
	}

	.fa{
		cursor: pointer;
	}

	.scroll::-webkit-scrollbar{
		width: 5px;
		height: 2px;
	}
	.scroll::-webkit-scrollbar-thumb {
		border-radius: 10px;
		box-shadow: inset 0 0 5px rgba(0,0,0,0.2);
		background: rgba(0,0,0,0.2);
	}
	.scroll::-webkit-scrollbar-track {
		box-shadow: inset 0 0 5px rgba(0,0,0,0.2);
		border-radius: 0;
		background: rgba(0,0,0,0.1);
	}
	.iui-rmenu{
		z-index: 999999 !important;
	}
	.tr-between td{
		border-bottom: 1px solid #56d3cb;
	}
	.d-train .train-more-btn{
		text-align: center;
		margin: -5px 0 10px 0;
		cursor: pointer;
		color: #009688;
	}
	.train-more{
		display: none;
	}
	.d-filter ul{
		border: 1px solid #ddd;
	}
	.d-filter ul li{
		cursor: pointer;
		height: 30px;
		text-align: center;
		border-top: 1px solid #ddd;
		line-height: 30px;
	}
	.d-filter ul li[onclick]:hover{
		background-color: #009688;
		color: #fff;
	}
	.marker-y{
		border-top: 1px dashed rgba(0, 0, 0, 0);
		position: absolute;
		width: 100%;
		pointer-events:none;
	}
	.marker-x{
		border-left: 1px dashed rgba(0, 0, 0, 0);
		position: absolute;
		height: 100%;
		pointer-events:none;
	}
	.t-guides .layui-inline{
		position: absolute;
		margin-left: -10px;
	}
	.t-guides .layui-colorpicker{
		border: none;
	}
	.t-guides .layui-colorpicker-trigger-bgcolor{
		display:none;
	}
	#s-group xm-select{
		border-radius: 0;
		background-color: #eee;
		border: none;
	}
	xm-select > .xm-body{
		z-index: 10000;
	}
	.layui-form-checkbox{
		width: 100%;
	}
	#dir{
		height: 32px;
		float: left;
		border: none;
		padding: 0 10px;
		color: #0a76be;
		background-color: #eee;
	}
	#svg{
		position: absolute;
		top: 0;
		left: 0;
		width: 100%;
		height: 100%;
	}
	#svg polygon,#svg polyline{
		fill: rgba(0,0,0,0); /*填充颜色*/
		stroke: #e9bf76; 	/*边框颜色*/
		stroke-width:2px;
	}

	[point-index]{
		position: absolute;
		width: 12px;
		height: 12px;
		margin-left: -6px;
		margin-top: -6px;

		border-radius: 10px;
		text-align: center;
		z-index: 20000;
		color: #fff;
	}
	[point-visibility="2"]{
		background-color: red;
	}
	[point-visibility="1"]{
		background-color: blue;
	}
	[point-visibility="0"]{
		background-color: #999;
	}
	[point-index].active{
		color: #5A50CC;
	}
	[point-index]:before{
		content: attr(point-index);
		font-size: 12px;
		line-height: 12px;
		vertical-align: top;
	}
</style>
</head>
<body>
	<div class="dl">
		<div style="border-bottom: 1px solid #ccc;height: 31px">
			<input id="imgName" class="layui-input" placeholder="图片" style="height: 30px;width: 140px;display: initial;font-size: 12px;color: #0a76be;border: none">
			<span title="筛选" class="t-filter-tag-img"><i class="fa fa-tags"></i></span>
			<span id="img-cont" style="color: red;font-size: 12px;"></span>
		</div>
		<ul class="ul-img scroll"></ul>
	</div>
	<div class="dc">
		<div class="tool">
			<ul>
				<%--<li title="选择"	class=""><i class="fa fa-mouse-pointer"></i></li>
				<li title="抓手"	class=""><i class="fa fa-allergies"></i></li>

				<li title="上一张"	class=""><i class="fa fa-arrow-circle-up"></i></li>
				<li title="下一张"	class=""><i class="fa fa-arrow-circle-down"></i></li>
				<li title="放大"	class=""><i class="fa fa-search-plus"></i></li>
				<li title="缩小"	class=""><i class="fa fa-search-minus"></i></li>

				<li title="复制"	class="t-copy"><i class="fa fa-copy"></i></li>
				<li title="粘贴"	class="t-paste"><i class="fa fa-paste"></i></li>--%>

				<li title="矩形"		class="t-rect g-model active" data-val="0"><i class="aiot-icon aiot-icon-rectangle"></i></li>
				<li title="多边形"	class="t-polygon g-model" data-val="1"><i class="aiot-icon aiot-icon-polygon"></i></li>
				<%--<li title="点"		class="t-point g-model" data-val="2"><i class="fa fa-share-alt"></i></li>--%>
				<li title="标签名称"	class="t-tag-name active"><i class="aiot-icon aiot-icon-label"></i></li>

				<%--<li title="自动标注"	class="t-tag-auto"><i class="fa fa-graduation-cap"></i></li>--%>
				<li title="图像处理"	class="t-crop"><i class="aiot-icon aiot-icon-crop-tool"></i></li>
				<li title="旋转"		class="t-rotate"><i class="aiot-icon aiot-icon-rotate"></i></li>


				<li title="导入导出"	class="t-import"><i class="aiot-icon aiot-icon-imp-exp"></i></li>
				<li title="训练"		class="t-train"><i class="aiot-icon aiot-icon-model-train"></i></li>
				<li title="模型"		class="t-detect"><i class="aiot-icon aiot-icon-openai"></i></li>
				<%--<li title="对比分组"	class="t-group-diff"><i class="aiot-icon aiot-icon-rectangle"></i></li>--%>
				<li title="计算大小"	class="t-pxmm"><i class="aiot-icon aiot-icon-scaleplate"></i></li>
				<li title="清理"		class="t-clean"><i class="layui-icon layui-icon-delete"></i></li>
				<li title="辅助线"	class="t-guides"><div></div><i class="aiot-icon aiot-icon-auxiliary-line"></i></li>
				<li title="帮助"		data-key="191"><i class="aiot-icon aiot-icon-help" style="color: #009688;"></i></li>
			</ul>

			<span id="img-name"></span>
			<div  style="float: right;margin-top: 7px;margin-right: 7px;">
				<input id="dir" value="${empty param.dir ? '/' : param.dir}"  readonly title="${project.path}" placeholder="目录">
				<div id="s-group" style="display: inline-block;width: 200px;"></div>
			</div>
		</div>
		<div id="di">
			<div id="dm">
				<img id="img" draggable="false">
				<svg id="svg" style="pointer-events: none"></svg>
			</div>
			<div class="marker-x"></div>
			<div class="marker-y"></div>
		</div>
	</div>
	<div class="dr">
		<div class="dr-tool">
			<input data-search="tImgTag" placeholder="标签" style="border: none;width: 120px;color: #0a76be;background: none;line-height: 38px">
			<p style="float: right;margin-right: 4px">
				<span title="总计" id="tag-count"></span>
				<span title="标签统计" class="t-statistics"><i class="fa fa-chart-bar"></i></span>
				<span title="移动到分组" class="t-move-group"><i class="fa fa-sign-out-alt"></i></span>
				<span title="全选"><input type="checkbox" id="checkTag" class="it-check"></span>
			</p>
		</div>
		<div class="dr-con scroll">
			<table id="tImgTag" class="" lay-skin="line" style="margin-top: 10px">
				<thead>
					<tr>
						<th data-field="color" data-edit="true" data-class="tag-color"></th>
						<th data-field="name" data-edit="true">名称</th>
						<th data-field="count" width="20">数量</th>
						<th data-type="checkbox" data-field="id" width="20"></th>
					</tr>
				</thead>
			</table>
		</div>
		<div class="dr-foot">
			<i class="layui-icon layui-icon-add-1" data-itable="create_tImgTag"></i>
		</div>
	</div>

	<form data-for="tImgTag" class="form-horizontal">
		<input type="hidden" name="id">
		<input class="layui-input" name="name" required="required">
		<input class="layui-input" name="color" type="color">
	</form>

	<div class="lay-con d-import">
		<form name="fimport" class="layui-form layui-form-pane">
			<div class="layui-form-item">
				<label class="layui-form-label">类型</label>
				<div class="layui-input-block">
					<select class="layui-input" name="type" lay-ignore>
						<option value="0">YOLO</option>
						<option value="1">VottCsv</option>
						<option value="2">MyCsv</option>
					</select>
				</div>
			</div>
			<div class="layui-form-item">
				<label class="layui-form-label">模式</label>
				<div class="layui-input-block">
					<select class="layui-input" name="model" lay-ignore="">
						<option value="0">目标检测</option>
						<option value="1">语义分割</option>
						<option value="2">分类</option>
						<option value="3">姿势估计</option>
						<option value="4">旋转框</option>
						<option value="5">OCR</option>
					</select>
				</div>
			</div>

			<div class="layui-form-item">
				<label class="layui-form-label">目录</label>
				<div class="layui-input-block">
					<input class="layui-input s-file" name="targetDir" data-suffix="csv" readonly>
				</div>
			</div>
		</form>

	</div>

	<div class="lay-con d-train">
		<form name="ftrain" class="layui-form layui-form-pane">
			<div class="layui-form-item">
				<label class="layui-form-label" style="padding:8px">训练类型</label>
				<div class="layui-input-block">
					<select class="layui-input" name="model" lay-ignore="">
						<option value="0">目标检测</option>
						<option value="1">语义分割</option>
						<option value="2">分类</option>
						<option value="3">姿势估计</option>
						<option value="4">旋转框</option>
						<option value="5">OCR</option>
					</select>
				</div>
			</div>
			<div class="layui-form-item">
				<label class="layui-form-label">训练名称</label>
				<div class="layui-input-block">
					<input class="layui-input" name="trainName" required lay-verify="required">
				</div>
			</div>
			<div class="layui-form-item">
				<label class="layui-form-label">微信推送</label>
				<div class="layui-input-block" id="s-person">

				</div>
			</div>
			<div class="train-more-btn">------------&nbsp;&nbsp;&nbsp;&nbsp;更多&nbsp;&nbsp;&nbsp;&nbsp;------------</div>
			<div class="train-more">
				<div class="layui-form-item">
					<label class="layui-form-label">验证集比例</label>
					<div class="layui-input-block">
						<input class="layui-input" name="valPercent" type="number" step="0.01">
					</div>
				</div>
				<div class="layui-form-item">
					<label class="layui-form-label">训练次数</label>
					<div class="layui-input-block">
						<input class="layui-input" name="epochs" type="number" placeholder="默认">
					</div>
				</div>
				<div class="layui-form-item">
					<label class="layui-form-label">图像大小</label>
					<div class="layui-input-block">
						<input class="layui-input" name="imgsz" type="number" placeholder="默认">
					</div>
				</div>
				<div class="layui-form-item">
					<label class="layui-form-label">batchSize</label>
					<div class="layui-input-block">
						<input class="layui-input" name="batchSize" type="number" placeholder="自动">
					</div>
				</div>
				<div class="layui-form-item">
					<label class="layui-form-label">多尺度训练</label>
					<div class="layui-input-block">
						<select class="layui-input" name="multiScale" lay-ignore="">
							<option value="0">否</option>
							<option value="1">是</option>
						</select>
					</div>
				</div>
				<div class="layui-form-item">
					<label class="layui-form-label">初始模型</label>
					<div class="layui-input-block">
						<input class="layui-input s-file" name="weights" data-path="train" data-suffix="pt" readonly>
					</div>
				</div>
				<div class="layui-form-item">
					<label class="layui-form-label" style="padding:8px">训练文件</label>
					<div class="layui-input-block">
						<input class="layui-input s-file" name="trainFile" readonly placeholder="yolo_yaml，my_zip" data-suffix="yaml,zip">
					</div>
				</div>
				<div class="layui-form-item">
					<label class="layui-form-label" style="padding:8px">分类目录</label>
					<div class="layui-input-block">
						<input class="layui-input s-file" name="classifyDir" readonly data-suffix="..">
					</div>
				</div>
				<div class="layui-form-item">
					<label class="layui-form-label" style="padding:8px">验证集</label>
					<div class="layui-input-block">
						<input class="layui-input s-file" name="valDir" data-path="" placeholder="20%" readonly data-suffix="..">
					</div>
				</div>
			</div>
		</form>

	</div>

	<div class="lay-con d-detect">
		<form name="fdetect" class="layui-form layui-form-pane">
			<div class="layui-form-item">
				<label class="layui-form-label">类型</label>
				<div class="layui-input-block">
					<select class="layui-input" name="type" lay-ignore="">
						<option value="0">目标检测</option>
						<option value="1">分类</option>
						<option value="2">语义分割</option>
						<option value="3">旋转框</option>
					</select>
				</div>
			</div>
			<div class="layui-form-item">
				<label class="layui-form-label">模型文件</label>
				<div class="layui-input-block">
					<input class="layui-input s-file" name="ptName" readonly data-path="" data-suffix="onnx,pt">
				</div>
			</div>
			<div class="layui-form-item">
				<label class="layui-form-label">验证目录</label>
				<div class="layui-input-block">
					<input class="layui-input s-file" name="source" placeholder="test" readonly data-path="test" data-suffix="..">
				</div>
			</div>
			<div class="layui-form-item">
				<label class="layui-form-label">图像大小</label>
				<div class="layui-input-block">
					<input class="layui-input" name="imgsz" type="number" placeholder="640">
				</div>
			</div>
		</form>
	</div>

	<div class="lay-con d-group-diff">
		<form name="fdetect" class="layui-form layui-form-pane">
			<div class="layui-form-item">
				<label class="layui-form-label">阀值</label>
				<div class="layui-input-block">
					<input class="layui-input" type="number" step="0.01" name="threshold">
				</div>
			</div>
			<div class="layui-form-item">
				<label class="layui-form-label">可信度</label>
				<div class="layui-input-block">
					<input class="layui-input" type="number" step="0.01" name="confidence">
				</div>
			</div>
		</form>
	</div>

	<div class="lay-con d-rotate">
		<form name="frotate" class="layui-form layui-form-pane">
			<div class="layui-form-item">
				<label class="layui-form-label">类型</label>
				<div class="layui-input-block">
					<select class="layui-input" name="type" lay-ignore>
						<option value="0">生成新图</option>
						<option value="2">180</option>
						<option value="1">90</option>
						<option value="3">-90</option>
					</select>
				</div>
			</div>
			<div class="layui-form-item">
				<label class="layui-form-label">起始角度</label>
				<div class="layui-input-block">
					<input class="layui-input" name="start" type="number" step="0.1">
				</div>
			</div>
			<div class="layui-form-item">
				<label class="layui-form-label">间隔</label>
				<div class="layui-input-block">
					<input class="layui-input" name="step" type="number" step="0.1" value="1">
				</div>
			</div>
			<div class="layui-form-item">
				<label class="layui-form-label">数量</label>
				<div class="layui-input-block">
					<input class="layui-input" name="size" type="number">
				</div>
			</div>
		</form>
	</div>

	<div class="lay-con d-crop">
		<form name="fcrop" class="layui-form layui-form-pane">
			<div>文件名以 标签编码- 开头的,将会标注<br></div>

			<div class="layui-form-item">
				<label class="layui-form-label">列</label>
				<div class="layui-input-block">
					<input class="layui-input" name="col" type="number">
				</div>
			</div>
			<div class="layui-form-item">
				<label class="layui-form-label">行</label>
				<div class="layui-input-block">
					<input class="layui-input" name="row" type="number">
				</div>
			</div>
			<div class="layui-form-item">
				<label class="layui-form-label">间隙</label>
				<div class="layui-input-block">
					<input class="layui-input" name="spacing" type="number">
				</div>
			</div>
			<div class="layui-form-item">
				<label class="layui-form-label">文件前缀</label>
				<div class="layui-input-block">
					<input class="layui-input" name="prefix">
				</div>
			</div>
			<div class="layui-form-item">
				<label class="layui-form-label">到 目录</label>
				<div class="layui-input-block">
					<input class="layui-input s-file" name="toDir" data-suffix="..">
				</div>
			</div>
		</form>
	</div>

	<div class="lay-con d-group">
		<form name="fgroup" class="layui-form layui-form-pane">
			<div class="layui-form-item">
				<code>分组</code> <span id="fgroupName"></span>
				<br><br><code>标签</code> <span id="fgroupTag"></span>
			</div>
			<div class="layui-form-item">
				<label class="layui-form-label">到 分组</label>
				<div class="layui-input-block">
					<select class="layui-input" name="toGroupId" lay-ignore>
						<option value="">--</option>
						<option value="0">数据集</option>
						<option value="-3">回收站</option>
					</select>
				</div>
			</div>
			<div class="layui-form-item">
				<label class="layui-form-label">到 标签</label>
				<div class="layui-input-block">
					<select class="layui-input" name="toTagId" lay-ignore></select>
				</div>
			</div>
			<div class="layui-form-item">
				<label class="layui-form-label">到 目录</label>
				<div class="layui-input-block">
					<input class="layui-input s-file" name="toDir" data-suffix="..">
				</div>
			</div>
		</form>
	</div>

	<div class="lay-con d-filter">
		<ul>
			<li onclick="filterImg(0)" style="border-top: none">全部</li>
			<li onclick="filterImg(2,4)">已标注</li>
			<li onclick="filterImg(2,5)">未标注</li>
			<li onclick="filterImg(3,1)">随机排序</li>
			<li onclick="filterImg(3,2)">名称排序</li>
			<li onclick="filterImg(3,3)">倒序</li>
			<li>
				<select name="nameType" style="width: 80px">
					<option value="1">名称包含</option>
					<option value="2">名称开头</option>
					<option value="3">序号前几</option>
				</select>
				<input name="nameCon" style="width: 80px;padding: 0 5px;">
				<button onclick="filterImg(1,1)">筛选</button>
				<button onclick="filterImg(1,2)">排除</button>
			</li>
			<li>
				标签数
				<select name="tagType">
					<option value=">">&gt;</option>
					<option value="<">&lt;</option>
					<option value="==">=</option>
				</select>
				<input name="tagCount" style="width: 80px;padding: 0 5px;" value="0" type="number">
				<button onclick="filterImg(2,1)">筛选</button>
				<button onclick="filterImg(2,2)">排除</button>
			</li>
		</ul>
	</div>

	<div class="lay-con d-pxmm">
		<form name="fpxmm" class="layui-form layui-form-pane">
			<div class="layui-form-item">
				<label class="layui-form-label">原图宽度</label>
				<div class="layui-input-block">
					<input class="layui-input" name="width" type="number">
				</div>
			</div>
			<div class="layui-form-item">
				<label class="layui-form-label">原图高度</label>
				<div class="layui-input-block">
					<input class="layui-input" name="height" type="number">
				</div>
			</div>
			<div class="layui-form-item">
				<label class="layui-form-label">mm/px</label>
				<div class="layui-input-block">
					<input class="layui-input" name="ratio" >
				</div>
			</div>
		</form>
	</div>

	<div class="lay-con d-clean">
		<form name="fclean" class="layui-form layui-form-pane" lay-filter="fclean">
			<div class="layui-form-item">
				<input type="checkbox" name="type" title="回收站"	value="0" lay-skin="primary" checked>
				<input type="checkbox" name="type" title="图像缓存"	value="1" lay-skin="primary">
				<input type="checkbox" name="type" title="所选图片"	value="2" lay-skin="primary">
				<input type="checkbox" name="type" title="图片不存在的标签"	value="3" lay-skin="primary">
				<input type="checkbox" name="type" title="更新模型列表"	value="4" lay-skin="primary">
			</div>
		</form>
	</div>

	<div class="lay-con d-cloud">
		<form name="fcloud" class="layui-form layui-form-pane">
			<div class="layui-form-item">
				<label class="layui-form-label">id</label>
				<div class="layui-input-block">
					<input class="layui-input" name="remoteId" type="number">
				</div>
			</div>
			<div class="layui-form-item">
				<label class="layui-form-label">dir</label>
				<div class="layui-input-block">
					<input class="layui-input" name="remoteDir">
				</div>
			</div>
			<div class="layui-form-item">
				<label class="layui-form-label">#</label>
				<div class="layui-input-block">
					<input class="layui-input" name="remoteGroup">
				</div>
			</div>
		</form>
	</div>

	<div class="lay-con d-help">
		<table class="layui-table" lay-size="sm">
			<tr>
				<td colspan="4"><b>图像</b></td>
			</tr>
			<tr>
				<td>移动</td>
				<td>右键</td>
				<td>全屏</td>
				<td>Space</td>
			</tr>
			<tr>
				<td>放大</td>
				<td>滚轮上 或 +</td>
				<td>缩小</td>
				<td>滚轮下 或 -</td>
			</tr>
			<tr>
				<td>上一张</td>
				<td>PgDn (shift 且复制)</td>
				<td>下一张</td>
				<td>PgUp (shift 且复制)</td>
			</tr>
			<tr class="tr-between">
				<td>居中</td>
				<td>Ctrl + 右键</td>
				<td></td>
				<td></td>
			</tr>
			<tr>
				<td colspan="4"><b>标签</b></td>
			</tr>
			<tr>
				<td>反选</td>
				<td>Ctrl + 单击</td>
				<td>多选</td>
				<td>Shift + 单击</td>

			</tr>
			<tr>
				<td>下一个</td>
				<td>Tab</td>
				<td>上一个</td>
				<td>Shift + Tab</td>
			</tr>
			<tr>
				<td>删除</td>
				<td>Del 或 双击  </td>
				<td>分组</td>
				<td>G 或 双击名称  </td>
			</tr>
			<tr>
				<td>移动</td>
				<td>方向键 (shift 10倍)</td>
				<td>大小</td>
				<td>Ctrl + 方向键</td>
			</tr>
			<tr>
				<td>旋转</td>
				<td>Alt + 方向键</td>
				<td>设置</td>
				<td>0 - 9</td>
			</tr>
			<tr>
				<td>复制</td>
				<td>C 或 Alt + 单击</td>
				<td>粘贴</td>
				<td>V</td>
			</tr>
			<tr>
				<td>全选</td>
				<td>A (Alt 且复制)</td>
				<td>剪贴</td>
				<td>X</td>
			</tr>
			<tr>
				<td>居中</td>
				<td>Q</td>
				<td>整标</td>
				<td>O</td>
			</tr>
			<tr class="tr-between">
				<td>遮盖</td>
				<td>[mask]</td>
				<td></td>
				<td></td>
			</tr>
			<tr>
				<td colspan="4"><b>系统</b></td>
			</tr>
			<tr>
				<td>取消</td>
				<td>ESC</td>
				<td>查看帮助</td>
				<td>?</td>
			</tr>
		</table>

	</div>


</body>
	<script src="${res}/plugin/xm-select.js"></script>
<script type="text/javascript">
	var svgNS = "http://www.w3.org/2000/svg";

	layui.form.render();
	var proPath = "${bd.getImgPath()}${project.path}";
	var ids = [param.id];

	var DEV = {
		id : param.id,
		cid : "${bd.device.communication}",
	};

	var model = 0;//0目标检测 1语义分割
	var t0 = new Date().getTime();
	var imgSuff = ["jpg","jpeg","bmp","png","tif","tiff","webp","dng","mpo","pfm"];
	var data,$imgs,$activeLi,$copyTag;
	var tagMap = {},tagCodeMap = {},groupMap = {},
		imgAll = [], //所有图片
		//imgTag = [], //标签图片
		imgArr = [], //使用的图片
        imgShow = []; //当前显示
	var groupSelect,personSelect;
	var $dl = $(".dl");
	var img = $("#img")[0];
	var di = $("#di")[0];
	var dm = $("#dm");
	var svg = $("#svg")[0];
	var imgPage = 0;

	var showMarker = false;
	var markerX = $(".marker-x")[0],markerY = $(".marker-y")[0];
	var $ulImg = $(".ul-img").scroll(function (e){
		if(e.target.scrollHeight - e.target.scrollTop < 1000){
			loadImg(++imgPage)
		}
	});

	getImgs();

	common.jsonModel("imgGroup",{pid_in:ids.join(",")},function (json) {
		common.renderSelect('[name="toGroupId"]',json.list,{"empty":false});
		var groups = [{"id":0,name:"数据集"},{"id":-2,name:"自动标注"},{"id":-3,name:"回收站"}];
		groups = groups.concat(json.list);
		$(groups).each(function (){
			groupMap[this.id] = this;
		});
		groupSelect = xmSelect.render({
			el: '#s-group',
			data: groups,
			empty: '组别',
			size: 'small',
			initValue: [0],
			height: '500px',
			prop:{"value":"id"},
			filterable: true,
			toolbar: {
				show: true,
				list: [ 'ALL', 'CLEAR', 'REVERSE' ]
			},
			theme: {
				color: '#55a4f1'
			},
			on: function(d){
				var ids = [];
				$(d.arr).each(function(){
					ids.push(this.id);
				});
				location.hash = ids.join(",");
				if(d.arr.length != 1)
					dm.addClass("show-group");
				else
					dm.removeClass("show-group");

				setTimeout(function (){
					loadLabel(data.name);
				},100);
			}
		});
	});

	common.jsonModel("tPerson",{wechatId_isNot:"null"},function (json) {
		personSelect = xmSelect.render({
			el: '#s-person',
			data: json.list,
			height: '500px',
			prop:{"value":"id"},
			filterable: true,
			toolbar: {
				show: true,
				list: [ 'ALL', 'CLEAR', 'REVERSE' ]
			}
		});
	});

	var tImgTag = new iTables("#tImgTag",{"pid_in":ids.join(","),"dir_in":"/,"+$("#dir").val()},{
		baseOption : common.iTableModel("ImgTag","sequence"),
		inline_edit :false,
		callForm : function(params){
			params.pid = param.id;
		},
		loadAfter:function(list){
			$(list).each(function (){
				tagMap[this.id] = this;
				tagCodeMap[this.code] = this;
			});
			this.selectIndex(0);
			common.renderSelect('[name="toTagId"]',list,{"dft":""});
		},
		render : {
			name : function (td,data){
				$(td).click(function (){
					getActiveLabel().setTag(data).save();
				}).addClass("tag-code");
				td.title = data.code;
				td.parentNode.searchIndex = data.code + (data.name || "") + PinYin.get(data.name || "");
				if(!data.name)
					$(td).css("color","#009688");
				return data.name || data.code;
			},
			color : function (td,data) {
				td.style["background-color"] = data.color;
				//return "<i style='background-color: "+data.color+"'></i>";
			},
			count : function (td,data){
				$(td).attr("data-count",data.id);
				return data.count;
			}
		}

	});

	function getImgs(callback){
		var path = proPath + $("#dir").val();

		common.ajax("${base}/file/getList",{"path":path},function (arr) {
			if(arr == null){
				layer.msg($("#dir").val() + " 目录不存在");
				return;
			}

			var t1 = new Date().getTime();
			imgShow = imgArr = imgAll = arr.filter(function (v) {
				var v2 = v.slice(v.lastIndexOf(".")+1).toLowerCase()
				for(var i=0;i<imgSuff.length;i++){
					if(imgSuff[i] == v2){
						return true;
					}

				}
				return false;
			});
			/*if(imgAll.length == 0){
				$("#dir").click();
			}*/
			callback && callback(arr);
		});
	}

	common.ajaxStop(function () {
		$("#img-cont").text(imgAll.length);
		loadImg(0);
		layui.use('flow', function(){
			layui.flow.lazyimg({
				elem:"img",
				scrollElem : ".ul-img"
			});

		});

	});

	function loadImg(page){
		var size = 50;
		if(page*size >= imgShow.length)
			return;

		console.log("加载第"+(page+1)+"页");
		imgPage = page;
		var path = proPath + $("#dir").val();
		var df2 = document.createDocumentFragment();
		for(var i= page*size;i<imgShow.length && i < page*size + size;i++){
			var name = imgShow[i];
			var data = {
				src : "${base}/image"+path + "/" + encodeURIComponent(name),
				name : name,
				no : i + 1
			};
			var li = buildLi(data);
			var imgSrc = data.src + "?width=200";
			li.img.setAttribute("lay-src",imgSrc);
			df2.appendChild(li);
		}
		$ulImg[0].appendChild(df2);
	}


	function buildLi(data){
		var li = document.createElement("li");
		var img = document.createElement("img");
		li.appendChild(img);
		img.onload = function (){
			var data = this.parentNode.data;
			data.ratio = this.naturalHeight / this.naturalWidth;
			if(data.no == 1){
				console.debug((new Date().getTime() - t0)+"ms首图加载");
				this.parentNode.click();
				imgFull();
			}
		};
		var div = document.createElement("div");
		var s1 = document.createElement("span");
		var s2 = document.createElement("span");
		s1.classList.add("img-index");
		s2.classList.add("img-name");
		s2.innerText = data.name;
		div.appendChild(s1);
		div.appendChild(s2);
		li.appendChild(div);
		li.data = data;
		li.img = img;
		return li;
	}


	$dl.on("click","li",function () {
		$ulImg.find(".active").removeClass("active");
		$activeLi = $(this).addClass("active");
		data = this.data;
		$("#img-name").text(data.no+"/"+$dl.find("li").length+" "+data.name);
		img.src = data.src;
		loadLabel(data.name);
	}).on("mousedown","li",function (e){
		if(e.which == 3)
			$(this).click();
	});

	function imgFull(){
		if(di.offsetHeight > di.offsetWidth*data.ratio){
			dm[0].style.width = "100%";
			dm[0].style.left = "0px";
			dm[0].style.top = (di.offsetHeight - di.offsetWidth*data.ratio)/2+"px";
		}else{
			dm[0].style.width = di.offsetHeight/data.ratio+"px";
			dm[0].style.left = (di.offsetWidth - dm[0].offsetWidth)/2+"px";
			dm[0].style.top = "0px";
		}
	}

	//相对源事件节点位置
	function delegatePos(e){
		var target = e.target;
		var x = e.offsetX;
		var y = e.offsetY;
		while (target != e.delegateTarget){
			x += target.offsetLeft;
			y += target.offsetTop;
			target = target.parentNode;
		}
		return {x:x,y:y};
	}

	var target,eClinet,$activeLabel;
	var polygon;
	dm.mousedown(function (e) {
		eClinet = {x:e.clientX,y:e.clientY};
		target = e.target;
		dm.pos();

		if(e.which == 1){
			if(model === 0)
				return M0down(e);

			if(polygon == null){
				if(target.nodeName == "polygon" || target.nodeName == "polyline")
					return;
				polygon = document.createElementNS(svgNS, model == 1 ? 'polygon' : "polyline");
				polygon.setAttribute('points',e.offsetX + "," + e.offsetY+","+e.offsetX + "," + e.offsetY);
				$(polygon).setTag(tImgTag._data);
				svg.appendChild(polygon);
			}else{
				var pt = svg.createSVGPoint();
				pt.x = e.offsetX;
				pt.y = e.offsetY;
				polygon.points.appendItem(pt);
			}
		}else if(e.which == 2){
			//$activeLi.click();
		}else if(e.which == 3){
			if(e.ctrlKey){
				var tp = getOffset(e.target);
				pointCenter(tp.left + e.offsetX,tp.top+e.offsetY);
			}
		}

	}).mousemove(function (e){
		if(eClinet == null)
			return;
		var diffX = e.clientX - eClinet.x;
		var diffY = e.clientY - eClinet.y;
		if(e.which == 3){
			//dm.move(diffX,diffY);
			setPos(dm[0],dm[0].pos.left + diffX,dm[0].pos.top + diffY);
		}else{
			if(model === 0){
				if($activeLabel){
					if(target == img || $(target).hasClass("resize")){
						$activeLabel.sizeRe(diffX,diffY);
					}else{
						$activeLabel.move(diffX,diffY);
					}
				}
			}else if(model == 1 || model == 2){
				if(polygon == null)
					return;
				var p = polygon.points[polygon.points.length -1];
				p.x = e.offsetX;
				p.y = e.offsetY;
			}
		}

	}).mouseup(function (e){
		if(model === 0)
			return M0up(e);

	}).on("dblclick","[data-label],polygon",function (e){
		if(e.ctrlKey)
			return;
		if($(e.target).hasClass("tag")){
			$(this).toggleGroup();
		}else{
			$(this).del();
		}


	}).on("mousewheel DOMMouseScroll", function (e) {
		e.preventDefault();
		var delta = e.originalEvent.wheelDelta;  // chrome & ie
		//e.originalEvent.detail             // firefox
		var o = getOffset(e.target);
		imgZoom(delta > 0 ? 0.2 : -0.2,e.offsetX + o.left,e.offsetY + o.top);
	});

	di.onmousemove = function (e){
		if(showMarker){
			markerX.style.left = (e.clientX - 230)+"px";
			markerY.style.top = (e.clientY - 55)+"px";
		}
	};

	function M0down(e){
		var $t = $(target);
		if($t.hasClass("resize")){
			$(target.parentNode).actived();
		}
		if($t.hasClass("tag")){
			target = target.parentNode;
		}

		if(target == img){//新建标签
			$(addLabel()).actived().css({top:e.offsetY,left:e.offsetX}).setTag(tImgTag._data);
		}else if($t.attr("data-label") != undefined){
			if(e.ctrlKey){//多选标签
				if($t.hasClass("active")){
					$t[0].activePos = $t[0].offsetLeft + "_"+$t[0].offsetTop;
				}else{
					$t.addClass("active");
				}

			}else if(e.shiftKey){//关键点
				var dmp = delegatePos(e);
				$t.addPoint(dmp.x + 3,dmp.y + 3,2);//.actived()
				$t.save();
			}else{//选中标签
				$t.actived();
			}
			if(e.altKey){//复制标签
				getActiveLabel().clone().appendTo(dm)
						.attr("data-id","").pos().move(10,10).actived()
						.each(function (){
							this.point = [];
						});
			}
		}else if($t.attr("point-index") != undefined){
			e.stopPropagation();
			$t.actived();
		}
		$activeLabel = getActiveLabel().pos();
	}

	function M0up(e){
		if(!$activeLabel || $activeLabel.length == 0)
			return;
		if(e.which == 1){
			var activePos = e.target.activePos;
			//取消ctrl选中
			if(activePos && activePos == e.target.offsetLeft + "_"+e.target.offsetTop){
				e.target.activePos = null;
				$(e.target).removeClass("active");
			}
			var hasMove = e.clientX != eClinet.x || e.clientY != eClinet.y;
			//关键点
			if($activeLabel.attr("point-index")){
				if(hasMove)
					$activeLabel.save();
			}else if(!$activeLabel.attr("data-id")){
				//结束新建label
				if($activeLabel[0].clientWidth > 3 && $activeLabel[0].clientHeight >3){
					$activeLabel.save();
				}else{
					$activeLabel.remove();
				}
			}else if(Math.abs(e.clientX - eClinet.x) + Math.abs(e.clientY - eClinet.y) > 3){
				$activeLabel.save();
			}
		}
		$activeLabel = null;
		eClinet= null;
	}

	function imgZoom(z,x,y){
		var rw = x * z;
		var rh = y * z;
		var p = getPos(dm[0]);
		if(z < 0 && p.w < 10)
			return;
		getLabel().pos();
		setPos(dm[0],p.left - rw,p.top - rh,p.w * (1+z));
		getLabel().each(function () {
			setPosStr(this,this.pos.str);
		});
		$(svg.getElementsByTagName("polygon")).each(function (){
			$(this.points).each(function (){
				this.x = this.x + this.x *z;
				this.y = this.y + this.y *z
			});
		});
		dm.find("[point-index]").zoomXY(z);
		if(img.src.indexOf("width=")>-1)
			img.src = data.src;
	}

	$(img).load(function () {
		getLabel().each(function () {
			rePos(this);
		});
	});

	$("[data-key]").click(function (e){
		var fun = keyFun[this.dataset.key];
		if(fun)
			fun.call(e.target,e);
	});

	function getLabel(){
		return dm.find("[data-label]");
	}
	function getActiveLabel(){
		return dm.find(".active");
	}

	function loadLabel(name){
		dm.find("[point-index]").remove();
		getLabel().remove();
		$(svg).empty();
		polygon = null;
		var countMap = {};
		exec("getAnno",{"names":name},function (json){
			var annos = json.data;
			$(annos).each(function (){
				var c = countMap[this.tagId];
				countMap[this.tagId] = (c || 0) + 1;
				$(addLabel(this)).setTag(tagMap[this.tagId]).setGroup(groupMap[this.groupId]);
			});
			getLabel().eq(0).actived();
			$("[data-count]").each(function (){
				var i = $(this).data("count");
				$(this).text(countMap[i] || "");
			});
			if(annos)
				$("#tag-count").text(annos.length);
			getLabel().pxmm(fpxmm.width.value,fpxmm.height.value,fpxmm.ratio.value);
		});
	}

	function activeLabel(saveMil){
		var labels =  getActiveLabel().pos();
		if(saveMil)
			labels.save(saveMil);
		return labels;
	}

	function addLabel(data){
		var dp = getPos(dm[0]);

		if(data && data.type == 1){
			var polygon = document.createElementNS(svgNS, 'polygon');
			polygon.data = data;
			var points = [];
			$(data.position.split(",")).each(function(i){
				points.push(this * (i%2==0 ? dp.w : dp.h))
			});
			polygon.setAttribute('points',points.join(","));
			svg.appendChild(polygon);
			return polygon;
		}

		var $label =  $("<div data-id data-label><span class='tag'></span><i class='resize' data-type='1'></i></div>").appendTo(dm);
		var label = $label[0];
		label.data = data || {};
		label.point = [];
		if(data){
			var p = (data.position || "").split(",");
			if(p[4])
				$label.setRotate(p[4]);
			if(p.length > 5){
				for(var i=0;i<(p.length-5)/3;i++){
					var j = 5 + i * 3;
					$label.addPoint(p[j]*dp.w,p[j+1]*dp.h,p[j+2]);
				}
			}
			if(data.confidence)
				$label.attr("title",data.confidence.toFixed(2));
		}
		rePos(label);
		return label;
	}

	function saveLabel(node,data){
		if(!node.data)
			node.data = {};

		var d = {
			id : $(node).attr("data-id"),
			dir : $("#dir").val(),
			pid : param.id,
			tagId : $(node).attr("data-label"),
			groupId: node.data.groupId || groupSelect.getValue('value')[0] || 0,
		}
		var d2 = $.extend(d,data);
		common.jsonModel("imgAnnotation",d2,function (json){
			node.data = json.data;
			$(node).attr("data-id",json.data.id);
		},{"action":"save"});
	}

	function setPos(node,left,top,width,height){
		if(left !== undefined)
			node.style.left = left + "px";
		if(top !== undefined)
			node.style.top = top + "px";
		if(width){
			node.style.width = width+"px";
			if(node.id != "dm")
				node.style["z-index"] = 10000 - Math.round((width));
		}
		if(height)
			node.style.height = height+"px";
	}

	function getPos(node) {
		var s = node.style;
		var p = {
			left : parseFloat(s.left.slice(0,-2)),
			top : parseFloat(s.top.slice(0,-2)),
			w : s.width.indexOf("px") > -1 ? parseFloat(s.width.slice(0,-2)) : node.clientWidth,
			h : s.height.indexOf("px") > -1 ? parseFloat(s.height.slice(0,-2)) : node.clientHeight,
			rotate : s.transform ? s.transform.slice(7,-4)%360 : 0
		};
		return p;
	}

	function rePos(node) {
		var data = node.data;
		if(data && data.position){
			$(node).attr("data-id",data.id);
			setPosStr(node,data.position);
		}
	}

	function setPosStr(node,posStr) {
		var p = getPos(dm[0]);
		var p2 = posStr.split(",");
		setPos(node,p2[0]*p.w,p2[1]*p.h,p2[2]*p.w,p2[3]*p.h);
	}

	function getTags(filed){
		var tid = [];
		tImgTag.table.find(".it-check:checked").each(function (){
			var v = this.value;
			if(filed)
				v = tagMap[v][filed];
			tid.push(v);
		});
		return tid.join(",");
	}

	function pointCenter(px,py){
		var cx = di.offsetWidth / 2;
		var cy = di.offsetHeight / 2;
		dm.animate({left:cx - px,top:cy-py});
	}

	function getOffset(node){
		var o = {left : 0,top : 0};
		while (node.parentNode && node.parentNode != di){
			o.left += node.offsetLeft;
			o.top += node.offsetTop;
			node = node.parentNode;
		}
		return o;
	}

	$("#tImgTag").on("click",".it-check",function (){
		loadLabel(data.name);
	});

	$(".g-model").click(function (){
		$(".g-model").removeClass("active");
		model = $(this).addClass("active").data("val");
		svg.style.pointerEvents = (model == 0 ? "none" : "auto");
	});


	$(".t-tag-name").click(function (){
		$(this).toggleClass("active");
		$("body").toggleClass("hide-tag");
	});

	$(".s-file").click(function (){
		var _this = this;
		var param = {
			base:proPath,
			suffix : $(this).data("suffix"),
			path : $(this).data("path")
		};
		common.openFile(param,function(path){
			$(_this).val(path);
		});
	});

	$("#dir").click(function (){
		var _this = this;
		var param = {base:proPath,suffix:".."};

		common.openFile(param,function(path){
			location.href = location.pathname+"?d=${param.d}&id=${param.id}&dir="+path
			/*$(_this).val(path || "/");
			getImgs(function (arr){
				reLoadImg();
			});*/
		});
	});

	$(".t-import").click(function (){
		layer.open({
			type: 1,
			title: "导入/导出",
			btn: ["导入","导出"],
			content:$(".d-import"), //捕获的元素
			area : ["400px","auto"],
			btn1 : function(index){
				var p = common.formJSON(".d-import");
				p.names = "";
				exec("tagImport",p,function (){
					layer.close(index);
					$activeLi.click();
				});
			},
			btn2 : function(index){
				var p = common.formJSON(".d-import");
				p.names = "";
				exec("tagExport",p,function (){
					layer.close(index);
					layer.msg("导出完成");
				});
			}
		});
	});

	$(".t-train").click(function (){
		ftrain.trainName.value = new Date().format("yyMMdd_hhmm");
		ftrain.model.value = model;
		layer.open({
			type: 1,
			title: "训练模型",
			btn: ['确定'],
			content:$(".d-train"),
			area : ["400px","auto"],
			yes : function(index){
				common.submit(ftrain,function (d){
					layer.close(index);
					openInfo();
					d.weChatPush = personSelect.getValue("valueStr");
					exec("trainPro",d);
				});

			}
		});

	});

	$(".train-more-btn").click(function (){
		$(".train-more").toggleClass("show");
	});


	$(".t-detect").click(function (){
		layer.open({
			type: 1,
			title: "选择模型文件",
			btn: ['开始验证...',"查看验证结果","自动标注"],
			content:$(".d-detect"), //捕获的元素
			area : ["400px","auto"],
			btn1 : function(index){
				openInfo(DEV.cid2);
				var p = common.formJSON(".d-detect");
				p.devId = DEV.id2;
				exec("detect",p,function (){});
			},
			btn2 : function (){
				var p = common.formJSON(".d-detect");
				window.open("imgDetectPt?d="+param.d+"&pro="+param.id+"&path="+proPath+"/"+(p.source || "test"));
			},
            btn3 : function(index){
				//openInfo(DEV.cid2);
				var loadIndex = layer.load();
                var p = common.formJSON(".d-detect") || {};
				p.devId = DEV.id2;
                exec("autoLabel",p,function (json){
					layer.close(loadIndex);
					layer.msg("自动标注 "+json.data);
				});
            }
		});
	});

	$(".t-group-diff").click(function (){
		layer.open({
			type: 1,
			title: "组别差异",
			btn: ['对比',"取消"],
			content:$(".d-group-diff"), //捕获的元素
			area : ["400px","auto"],
			yes : function(index){
				exec("getAnno",{},function (json) {
					var p = common.formJSON(".d-group-diff");
					var threshold = p.threshold || 0;
					var confidence = p.confidence;
					imgMap = {};
					var imgGroup = {},imgNameMap = {};
					$(json.data).each(function () {
						if(confidence && this.confidence && this.confidence < confidence)
							return true;
						imgNameMap[this.name] = 1;
						imgGroup[this.name] = imgGroup[this.name] || {};
						imgGroup[this.name][this.groupId] = imgGroup[this.name][this.groupId] || [];
						imgGroup[this.name][this.groupId].push(this);
					});
					var g = groupSelect.getValue("value");
					for(var k in imgNameMap){
						var s1 = imgGroup[k][g[0]];
						var s2 = imgGroup[k][g[1]];
						if(!s1 || !s2 || s1.length != s2.length){
							imgMap[k] = 1;
						}else if(threshold){
							var d1 = s1[0].position.split(",");
							var d2 = s2[0].position.split(",");
							for(var j = 0;j<4;j++){
								if(Math.abs(d1[j] - d2[j]) > threshold){
									imgMap[k] = 1;
									break;
								}
							}
						}

					}
					filterLi();
				});
			}
		});
	});



	$(".t-rotate").click(function (){
		layer.open({
			type: 1,
			title: "旋转",
			btn: ['确定 '+imgShow.length+" 张"],
			content:$(".d-rotate"), //捕获的元素
			area : ["300px","auto"],
			yes : function(index){
				var p = common.formJSON(".d-rotate") || {};
				exec("rotate",p,function (){
					layer.close(index);
					if(p.type == 0)
						layer.msg("旋转后的图片已生成在项目generate目录，本次生成"+(imgShow.length * p.size)+"张");
					$activeLi.click();
					var t = new Date().getTime();
					$activeLi.find("img").attr("src",data.src+"&width=200&v="+t);
					img.src = data.src + "&width=1000&v="+t;
				},{"maskType":1});
			}
		});
	});

	$(".t-crop").click(function (){
		layer.open({
			type: 1,
			title: "图像处理 "+imgShow.length+" 张",
			btn: ["切图","合并","整标"],
			content:$(".d-crop"), //捕获的元素
			area : ["300px","auto"],
			btn1 : function(index){
				var p = common.formJSON(".d-crop") || {};
				exec("crop",p,null,{"maskType":1})
			},
			btn2 : function(index){
				var p = common.formJSON(".d-crop") || {};
				exec("stitchedImg",p,function (json){
					layer.msg("合并完成");
				},{"maskType":1})
			},
			btn3 : function(index){
				exec("nameToTag",{},function (json){
					layer.msg("整标完成");
				},{"maskType":1})
			}
		});

	})

	$(".t-pxmm").click(function (){
		img.src = data.src;
		layer.open({
			type: 1,
			title: "计算大小",
			btn: ['获取宽高','确定'],
			content:$(".d-pxmm"),
			area : ["400px","300px"],
			btn1 : function(){
				fpxmm.width.value = img.naturalWidth;
				fpxmm.height.value = img.naturalHeight;
			},
			btn2 : function (index){
				layer.close(index);
				$("[data-label]").pxmm(fpxmm.width.value,fpxmm.height.value,fpxmm.ratio.value);
			}
		});

	});

	$("#tag-count").click(function (){
		this.order = !this.order;
		this.order ? tImgTag.sort("count","DESC") : tImgTag.sort("sequence","ASC");
	});

	$(".t-statistics").click(function (){
		exec("getAnnoStatistics",{names:""},function (json){
			var c = 0;
			var tagCout = {};
			$(json.data).each(function (){
				c +=  this.count;
				tagCout[this.tagId] = this.count;
			});
			$("[data-count]").each(function (){
				var d = this.parentNode.data;
				this.innerText = d.count = tagCout[d.id] || "";
			});
			$("#tag-count").text(c);
		});
	});

	$(".t-move-group").click(function (){
		$("#fgroupName").text(groupSelect.getValue("nameStr"));
		$("#fgroupTag").text(getTags("code"));

		layer.open({
			type: 1,
			title: "移动/复制 标签",
			btn: ['移动','复制'],
			content:$(".d-group"),
			area : ["400px","auto"],
			btn1 : function(index){
				layer.close(index);
				moveTag(0);
			},
			btn2 : function(index){
				layer.close(index);
				moveTag(1);
			}
		});
	});

	function moveTag(type){
		var p = common.formJSON(".d-group");
		p.type = type;
		exec("moveGroup",p,function (){
			$activeLi.click();
		});
	}

	$(".t-clean").click(function (){
		layer.open({
			type: 1,
			title: "清理",
			btn: ['确定'],
			content:$(".d-clean"),
			area : ["250px","auto"],
			yes : function(index){
				layer.close(index);
				var p = common.formJSON(fclean);
				if(p.type instanceof Array)
					p.type = p.type.join(",");
				exec("clean",p,function (json){
					$activeLi.click();
				});
			}
		});
	});

	layui.colorpicker.render({
		elem: $(".t-guides").find("div"),
		color: 'rgba(7, 155, 140, 1)',
		format: 'rgb',
		predefine: true, // 开启预定义颜色
		colors: ['#eee','#ddd','#ccc','#bbb','#aaa','#999','rgba(238, 238, 238, 0.5)','rgba(221, 221, 221, 0.5)','rgba(204, 204, 204, 0.5)','rgba(187, 187, 187, 0.5)','rgba(170, 170, 170, 0.5)'], //自定义预定义颜色项
		alpha: true,//开启透明度
		done: setGuides,
		change: setGuides
	});

	$(".t-cloud").click(function (){
		layer.open({
			type: 1,
			title: "云服务",
			btn: ['拉取模型','拉取数据集'],
			content:$(".d-cloud"),
			area : ["400px","auto"],
			btn1 : function(index){
				layer.close(index);
				openInfo();
				exec("remotePt",{},function(json){

				});
			},
			btn2 : function (index){
				common.submit(fcloud,function (d){
					layer.close(index);
					openInfo();
					exec("remoteProject",d);
				});
			}
		});

	});

	function setGuides(color){
		showMarker = !!color;
		$(markerX).css("border-color",color);
		$(markerY).css("border-color",color);
	}


	$("#checkTag").change(function (){
		tImgTag.table.find(".it-check:visible").prop("checked",this.checked);
		loadLabel(data.name);
	});

	$(".t-filter-tag-img").mouseup(function (e){
		layer.open({
			type: 1,
			title: "筛选图片",
			shade:0,
			skin: 'layui-layer-molv',
			content:$(".d-filter"),
			area : ["350px","auto"]
		});
	});

	$("#imgName").keyup(function () {
		clearTimeout(this.timeout);
		var _this = this;
		var con = this.value;
		if(con == this.beforeCon)
			return;
		this.timeout = setTimeout(function (){
			var n = con;
			_this.beforeCon = con;
			var not = false;
			if(n.indexOf("!") == 0){
				not = true;
				n = n.slice(1);
			}
			var regExp = new RegExp(n);
			imgShow = imgArr.filter(function (a){
				if(n === "")
					return true;
				var b = regExp.test(a);
				return not ? !b : b;
			});
			reLoadImg();
		},500);
	});

	function exec(method,params,callback,options){
		var p = {
			"id" : ${project.id},
			"proId" : ${project.id},
			"dir" : $("#dir").val() || "/",
			"groupId" : groupSelect.getValue("valueStr"),
			"tagId" : getTags(),
			"names" : imgShow.join("/")
		};
		common.devExec(params.devId || param.d,method,$.extend(p,params),function (json){
			if(json.message)
				layer.msg(json.message);
			if(callback)
				callback(json);
		},options);
	}

	/**
	 * 0全部
	 * 1名称过滤		1.1名称包含 1.2名称开头 1.3序号前几
	 * 2标签过滤 	2.1已标注 2.2未标注
	 * 3排序 		3.1随机 3.2名称 3.3倒序
	 * action 1筛选 2排除 3添加
	 */

	function filterImg(type,action){
		var p = common.formJSON(".d-filter");
		if(type == 0){
			imgShow = imgArr = imgAll;
		}else if(type == 1) {

			if(p.nameType == 1 || p.nameType == 2){ //名称包含 名称开头
				var r = (p.nameType == 2 ? "^" : "") + p.nameCon;
				var regExp = new RegExp(r);
				imgShow = imgShow.filter(function (a){
					var b = regExp.test(a);
					return action == 1 ? b : !b;
				});
			}else if(p.nameType == 3){ //序号前几
				imgShow = action == 1 ?  imgShow.slice(0,p.nameCon) : imgShow.slice(p.nameCon);
			}
		}else if(type == 2){
			exec("getAnnoImg",{names:""},function (json){
				var m = json.data;
				imgShow = imgArr = imgShow.filter(function (a){
					if(action == 4) //已标注
						return m[a] > 0;
					if(action == 5) //未标注
						return m[a] == undefined;
					if(action == 1)
						return eval(m[a] + p.tagType + p.tagCount);
					if(action == 2)
						return !eval(m[a] + p.tagType + p.tagCount);
				});
				reLoadImg();
			});
			return;
		}else if(type == 3){
			if(action == 3){
				imgShow.reverse();
			}else{
				imgShow.sort(function (a,b){
					if(action == 1)
						return Math.random() - 0.5;
					if(action == 2)
						return a.localeCompare(b);

				});
			}

		}
		reLoadImg();

	}

	function reLoadImg(){
		imgPage = 0;
		$("#img-cont").text(imgShow.length);
		$ulImg.empty();
		loadImg(imgPage);
		$ulImg.scrollTop(0).scroll();
	}

	function liNext($n,isPrev){
		do{
			$n = isPrev ? $n.prev() : $n.next();
		}while ($n.is(":hidden"));
		return $n.length > 0 ? $n : $n.prevObject;
	}

	function openInfo(roomId){
		layer.open({
			type: 2,
			title: "信息",
			area : ["90%","90%"],
			content:"${base}/config/debug?room="+(roomId || DEV.cid),
		});
	}

	function callBackMsg(msg){
		if(msg.indexOf("训练目标已输出") > -1){
			var url1 = "${base}/plugin/image/imgDetect?d="+param.d+"&path="+proPath+msg.match(/\/train\/\d{6}_\d{4}/)[0];
			return "<a target='_blank' href='"+url1+"'>"+msg+"</a>";
		}else if(msg.indexOf("saved") > -1){
			var url2 = "${base}/plugin/image/imgDetect?d="+param.d+"&path="+proPath+msg.match(/\/train\/[\s\S]+\/exp\d*/)[0];
			return "<a target='_blank' href='"+url2+"'>"+msg+"</a>";
		}
	}

	var numKey = "";
	$(document).keydown(function (e){
		if(e.target.nodeName == "INPUT")
			return;
		if(e.keyCode == 33 || e.keyCode == 34)
			e.preventDefault();
		//数字键 0-9[48-57]
		if(e.keyCode >= 48 && e.keyCode <= 57){
			numKey = (numKey || 0) * 10 + e.keyCode - 48;
		}else if(e.keyCode >= 96 && e.keyCode <= 105){
			numKey = (numKey || 0) * 10 + e.keyCode - 96;
		}else {
			var fun = keyFun[e.keyCode];
			if(fun)
				fun.call(e.target,e);
		}
	}).keyup(function (e){
		if(numKey !== ""){
			tImgTag.table.find(".tag-code:visible").eq(numKey).click();
			numKey = "";
		}else{
			var fun = keyFun["up_"+e.keyCode];
			if(fun)
				fun.call(e.target,e);
		}

	}).contextmenu(function (e){
		e.preventDefault();
	});

	function copyAll(){
		$copyTag = getLabel();
		layer.msg("已复制"+$copyTag.length+"个标签");
	}

	function reName(){
		layer.prompt({"formType":0,title:"重命名","value":data.name,offset:['50%', '215px']}, function (value, index, elem) {
			layer.close(index);
			exec("reName",{"names":data.name,"newName":value},function (){
				data.name = value;
				$activeLi.find(".img-name").text(value);
			});
		});
	}

	var keyFun = {
		8 : function (e){
			if(polygon){
				var psize = polygon.points.length;
				polygon.points.removeItem(psize-2);
			}
		},
		9 : function (e){//Tab alt切换系统窗口 ctrl切换浏览器窗口
			e.preventDefault();
			//切换关键点可见性
			var at = getActiveLabel()[0];
			if(at.label){
				var vis = parseInt(at.getAttribute("point-visibility"))-1;
				if(vis < 0)
					vis = 2;
				at.setAttribute("point-visibility",vis);
				$(at).save();
				return;
			}
			var a = getLabel().sort(function (a,b){
				var pa = getPos(a);
				var pb = getPos(b);
				return pa.left - pb.left;
			});
			for(var i=0;i<a.length;i++){
				if($(a[i]).hasClass("active")){
					if(e.shiftKey)
						a = a[i == 0 ? a.length - 1 : i-1];
					else
						a = a[i == a.length - 1 ? 0 : i+1];
					break;
				}
			}

			var b = $(a).actived();

			//在不可见区域移到居中
			var pos = dm.position();
			var p2 = b.position();
			var left = pos.left + p2.left;
			var top = pos.top + p2.top;
			var right = left + b.width();
			var bottom = top + b.height();
			var isExceed = left < 0 || top < 0 || right > di.offsetWidth || bottom > di.offsetHeight

			if(isExceed){ //ctrl是切浏览器窗口，alt是切系统窗口
				b.center();
			}
		},
		27 : function (){// ESC
			layer.closeAll();
			if(polygon != null){
				var psize = polygon.points.length;
				if(psize > 3){
					polygon.points.removeItem(psize-1);
					$(polygon).save();
				}else{
					$(polygon).remove();
				}
				polygon = null;
			}
		},
		32 : function (e) { // Space ctrl是切输入法
			if(e.shiftKey){
				getActiveLabel().center();
			}else{
				imgFull();
				getLabel().posRe();
			}

		},
		33 : function (e) { // pgUp
			e.shiftKey && copyAll(); //ctrl 是切浏览器窗口
			$activeLi = liNext($activeLi.removeClass("active"),1).addClass("active");
			$ulImg.scrollTop($activeLi[0].offsetTop - $ulImg.height() /2 + $activeLi.height()/2);
		},
		up_33 : function (){
			$activeLi.click();
			$ulImg.scroll();
		},
		34 : function (e) { // pgDn
			e.shiftKey && copyAll(); //ctrl 是切浏览器窗口
			$activeLi = liNext($activeLi.removeClass("active")).addClass("active");
			$ulImg.scrollTop($activeLi[0].offsetTop - $ulImg.height() /2 + $activeLi.height()/2);
		},
		up_34 : function (){
			$activeLi.click();
			$ulImg.scroll();
		},
		arrow : function (e,a,b){
			if(e.shiftKey){
				a = a * 10;
				b = b * 10
			}
			if(e.ctrlKey && e.altKey){
				//旋转角度
				activeLabel(1000).rotateRe(a/2);
			}else if(e.ctrlKey){
				activeLabel(1000).sizeRe(a,b);
			}else if(e.altKey){
				var p = dm.position();
				dm.css({left:p.left+a*20,top:p.top+b*20});
			}else{
				activeLabel(1000).move(a,b);
			}
		},
		37 : function (e) { // left
			keyFun.arrow(e,-1,0);
		},
		38 : function (e) { // up
			keyFun.arrow(e,0,-1);
		},
		39 : function (e) { // right
			keyFun.arrow(e,1,0);
		},
		40 : function (e) { // down
			keyFun.arrow(e,0,1);
		},
		46 : function (e) { // Del
			getActiveLabel().del();
		},
		65 : function (e) { // A
			dm.find("[data-label]").addClass("active");
			e.altKey && copyAll(); //ctrl 是全选
		},
		67 : function (e) { // C
			if(e.ctrlKey)
				return;
			$copyTag = getActiveLabel();
			if($copyTag.length == 0)
				$copyTag = getLabel();
			layer.msg("已复制"+$copyTag.length+"个标签");
		},
		71 : function (e){ // G
			getActiveLabel().toggleGroup();
		},

		79 : function (e){ // O
			var data = {};
			$(addLabel()).actived()
					.css({top:0,left:0,width:dm.width(),height:dm.height()})
					.setTag(tImgTag._data).save();
		},
		81 : function (e) { // Q
			getActiveLabel().center();
		},
		86 : function (e) { // v
			$copyTag.each(function (){
				delete this.data.id;
				$(addLabel(this.data)).addClass("active").setTag(tagMap[this.data.tagId]).save();
			});
		},
		88 : function (e) { // X
			keyFun["67"](e);
			$copyTag.del();
		},
		96 : function (e){ // num 0-9

		},
		107 : function (e){ // num +
			getActiveLabel().toggleGroup();
		},
		113 : function (e){ // F2
			reName();
		},
		187 : function (e){ // +
			imgZoom(0.2,img.clientWidth/2,img.clientHeight/2);
		},
		189 : function (e){ // -
			imgZoom(-0.2,img.clientWidth/2,img.clientHeight/2);
		},
		191 : function (e){ // ?
			layer.open({
				type: 1,
				skin:"layui-layer-molv",
				title: "帮助信息",
				shadeClose : true,
				content:$(".d-help"),
				area : ["450px","auto"]
			});
		}

	};

	$.fn.extend({
		actived : function(){
			if(this.length > 0)
				getActiveLabel().removeClass("active");
			this.addClass("active");
			return this;
		},
		pos : function (){
			var pd = getPos(dm[0]);
			this.each(function () {
				var p = this.pos = getPos(this);
				var p2 = {
					left : p.left / pd.w,
					top : p.top / pd.h,
					width : p.w / pd.w,
					height : p.h / pd.h
				};
				this.pos.normalize = p2;
				this.pos.str = p2.left.toFixed(5) + "," + p2.top.toFixed(5) + "," + p2.width.toFixed(5) + "," + p2.height.toFixed(5);
			});
			return this;
		},
		posRe : function (){
			this.each(function () {
				rePos(this);
			});
			return this;
		},
		move : function (left,top) {
			this.each(function () {
				var p = this.pos;
				var l = Math.min(Math.max(p.left + left,0),dm[0].offsetWidth - p.w);
				var t = Math.min(Math.max(p.top + top,0),dm[0].offsetHeight - p.h);
				setPos(this,l,t);
			});
			return this;
		},
		
		sizeRe : function (width,height) {
			this.each(function () {
				var p = this.pos;
				var w = Math.min(p.w + width,dm[0].offsetWidth-p.left);
				var h = Math.min(p.h + height,dm[0].offsetHeight-p.top);
				setPos(this,null,null,w,h);
			});
			return this;
		},
		rotateRe : function (r) {
			this.each(function () {
				var p = getPos(this);
				$(this).setRotate(p.rotate + r);
			});
			return this;
		},

		setRotate : function (r){
			this.each(function () {
				$(this).css("transform","rotate("+r+"deg)");
			});
			return this;
		},
		center : function (){
			var p = getPos(this[0]);
			pointCenter(p.left+p.w/2,p.top+p.h/2);
		},
		setTag : function (tag){
			tag = tag || {id:-1,"name":"标签已删除","color":"#000"};
			this.each(function (){
				var $this = $(this);
				$this.attr("data-label",tag.id);
				if(this.nodeName == "polygon"){
					$this.css({"stroke":tag.color});
					var title = document.createElementNS(svgNS, "title");
					title.textContent = tag.name || tag.code;
					this.appendChild(title);
				}else{
					$this.css({"border-color":tag.color});
					$this.find(".tag").attr("data-name", tag.name || tag.code).css({"background-color":tag.color});
					this.style.backgroundColor = tag.code == "[mask]" ? "rgba("+hexToRgb(tag.color || "#000000")+",0.9)" : "";
				}
			});
			return this;
		},
		setGroup : function (group){
			this.each(function (){
				this.data.groupId = group.id;
				$(this).find(".tag").attr({
					"data-group" : group.name || group
				});
			});
			return this;
		},
		addPoint : function (x,y,v){
			var label = this[0];
			var point = $("<div point-index='"+(label.point.length+1)+"' point-visibility='"+v+"'></div>")
					.css({left:x,top:y}).appendTo(dm);
			label.point.push(point[0]);
			point[0].label = label;
			return point;
		},
		zoomXY : function (z){
			this.each(function (){
				var p = getPos(this);
				$(this).css({
					"left":p.left + p.left * z,
					"top" : p.top + p.top * z
				});
			});
		},
		toggleGroup : function (){
			var a = groupSelect.getValue('value');
			if(a.length < 2)
				return this;
			this.each(function (){
				var b = a.indexOf(this.data.groupId);
				var c  = b+1;
				if(c >= a.length || c < 0)
					c = 0;

				var group = groupMap[a[c]];
				$(this).setGroup(group);
				saveLabel(this,{"groupId":group.id});
			});
			return this;
		},
		pxmm : function(width,height,ratio){
			if(width && height)
				this.pos().each(function (){
					var n = this.pos.normalize;
					var w = Math.round(n.width * width);
					var h = Math.round(n.height * height);
					var t = $(this).find(".tag").attr("data-px",w + "×"+h+"px");
					if(ratio){
						var wm = Math.round(w * ratio);
						var hm = Math.round(h * ratio);
						t.attr("data-mm",wm + "×"+hm+"mm");
					}

				});
			return this;
		},
		save : function (millisec) {
			this.each(function () {
				var node = this;
				if(this.label)
					node = this.label;
				if(node.timer)
					clearTimeout(node.timer);

				var data = {name : window.data.name};
				var p2 = getPos(dm[0]);
				if(this.nodeName == "polygon"){
					data.type = 1;
					var p = [];
					$(this.points).each(function (){
						p.push(this.x/p2.w);
						p.push(this.y/p2.h);
					});
					data.position = p.join(",");
				}else{
					var p = getPos(node);
					var left = p.left / p2.w;
					var top = p.top / p2.h;
					var width = p.w / p2.w;
					var height = p.h / p2.h;
					/*if(left > 1 || left < 0 || top > 1 || top < 0 || (width+left) > 1 || (height+top) > 1)
                        layer.tips("标注越界",node,{tips: [2, '#FF5722'],tipsMore: true});*/
					data.position = left + "," + top + "," + width + "," + height+","+p.rotate;
					if(node.point.length > 0){
						$(node.point).each(function (){
							var pp = getPos(this);
							data.position += "," + pp.left /p2.w + "," + pp.top/p2.h + "," + this.getAttribute("point-visibility");
						});
					}
				}
				this.timer = setTimeout(function(){
					saveLabel(node,data);
				},millisec || 0);
			});
			return this;
		},
		del : function(){
			var _this = this;
			//关键点
			var label = this[0].label;
			if(label){
				label.point.remove(this[0]);
				$(this[0]).remove();
				$(label.point).each(function (index){
					this.setAttribute("point-index",index + 1);
				});
				$(label).save();
				return this;
			}
			var ids = [];
			this.each(function (){
				var annoId = $(this).data("id") || this.data.id;
				if(annoId)
					ids.push(annoId);
			});
			if(ids.length > 0)
				common.jsonModel("imgAnnotation",{id:ids.join(",")},function (json){
					_this.each(function (){
						$(this.point).remove();
					});
					_this.remove();
				},{"action":"del"});
			else
				this.remove();
			return this;
		}
	});



	var m1 = [{
		title : "重命名",
		check : function(node){
			return true;
		},
		click : reName
	},{
		title : "删除",
		click : function(){
			layer.confirm(data.name, {icon: 3, title:'删除'}, function(index){
				layer.close(index);
				exec("delImg",{"names":data.name},function (){
					var n = $activeLi.next();
					$activeLi.remove();
					n.click();
				});
			});
		}
	}];

	common.ajax("${base}/device/getDevice",{"klass":"org.aiot.device.base.AbstractTarget"},function (list){
		var m = {title:"识别",sub:[]};
		if(list.length > 0)
			m1.push(m);
		$(list).each(function (){
			var _this = this;
			m.sub.push({
				title : this.name,
				check : function(node){
					return true;
				},
				click : function(){
					var fileName = proPath + $("#dir").val() + "/" + data.name;
					common.devExec(_this.id,"recognize",{"file":fileName},function (json){
						var t = json.data.targets;
						layer.msg("标签数："+t.length + " " +(json.data.remark || ""));
						$(t).each(function (){
							var tag = tagCodeMap[this.label];
							if(tag){
								this.position = this.left + "," + this.top + "," + this.width + "," + this.height;
								$(addLabel(this)).setTag(tag).save();
							}

						});
					},{maskType:1})
				}
			});
		});
		iUI.RMenu(".ul-img",m1);
	});



	function hexToRgb(hex) {
		hex = hex.replace('#', '');

		// 如果颜色是3位数，需要转换为6位数
		if (hex.length === 3) {
			hex = hex[0] + hex[0] + hex[1] + hex[1] + hex[2] + hex[2];
		}

		// 拆分颜色为RGB
		var r = parseInt(hex.substring(0, 2), 16);
		var g = parseInt(hex.substring(2, 4), 16);
		var b = parseInt(hex.substring(4, 6), 16);

		// 返回RGB字符串
		return r+","+g+","+b;
	}


</script>
</html>