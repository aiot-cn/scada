<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>

<!doctype html>
<html>
<head>
	<%@include file="../../common/page_head.jsp" %>
	<link rel="stylesheet" href="${res}/font/imgfont/iconfont.css">
	<script src="${res}/plugin/iUI/iUI.js"></script>
	<title>${SRes.name}</title>
	<meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=0">

	<style type="text/css">
		html {
			touch-action: manipulation;
			background-color: #666;
		}
		html,body{
			height: 100%;
		}
		.layui-form-pane .layui-form-label{
			width: 110px;
			padding: 8px 10px;
		}
		#di{
			position: absolute;
			top : 0;
			bottom: 0;
			background-color: #666;
			left:0;
			right: 0;
			overflow: hidden;
		}
		#dm{
			user-select:none;
			-webkit-user-select:none;
			position: absolute;
		}
		[data-label]{
			position: absolute;
			border: 2px solid #fff;
			box-sizing: border-box;
		}
		#dm .active,[data-label]:hover{
			-z-index: 100000 !important; /*会导致遮盖内部标签*/
			border-color: #e707b1 !important;
			box-shadow: 0px 0px 5px #fff;
		}
		[data-label]:hover .tag{
			display: inline;
		}

		.tag{
			color: #fff;
			position: absolute;
			padding: 0 5px;
			font-size: 12px;
			top: -18px;
			left: -2px;
			white-space: nowrap;
		}
		.tag:hover{
			background-color: #e707b1 !important;
		}
		.tag:hover:after{
			content: attr(data-wh);
			margin-left: 5px;
		}

		.hide-tag .tag{
			display: none;
		}

		.div-info{
			color: #bbb;
			margin: 5px 10px;
			z-index: 10;
			position: absolute;
		}
		#img-name:hover{
			cursor: pointer;
			color: #fff;
		}

		#svg,#svg2{
			position: absolute;
			top: 0;
			left: 0;
			width: 100%;
			height: 100%;
		}
		#svg polygon{
			fill: rgba(238, 138, 247, 0); /*填充透明*/
			stroke: #f3e292; /*线条颜色*/
			stroke-width: 1.5px;
		}

		#svg polyline{
			fill: rgba(238, 138, 247, 0);
			stroke: aqua;
		}
		polygon:hover{
			filter : url(#shadow-white)
		}
		.svg-label{
			position: absolute;
			padding: 3px;
			line-height: 12px;
			font-size: 12px;
			white-space: nowrap;
			background: rgba(30,30,30,0.7);
			color: #fff;
		}

		.tool-left{
			-border: 1px solid #fff;
			position: absolute;
			width: 200px;
			top: 30px;
			left: 10px;
		}
		#textPoint{
			font-size: 12px;width:calc(100% - 6px);background-color: rgba(255, 255, 255, 0.1);color: #ccc;
			padding: 3px;
		}
		.btn-add-point{
			color: #bbb;
			cursor: pointer;
			position: absolute;
			bottom: 4px;
			right: -2px;
		}

		[point-index]{
			position: absolute;
			width: 12px;
			height: 12px;
			margin-left: -6px;
			margin-top: -6px;
			background-color: red;
			border-radius: 10px;
			text-align: center;
			z-index: 20000;
			color: #fff;
		}
		[point-index]:before{
			content: attr(point-index);
			font-size: 12px;
			line-height: 12px;
			vertical-align: top;
		}

		.ul-tool{
			position: fixed;
			bottom: 0;
			left: 50%;
			transform: translateX(-50%);
			background-color: #333;
			border-radius: 3px;
			z-index: 10000;
		}
		.ul-tool li{
			float: left;
			padding: 5px;
		}
		.ul-tool li:hover{
			cursor: pointer;
		}
		.ul-tool li.active{
			background-color: #181818;
		}
		.ul-tool li.active i{
			color: #ccc;
		}
		.ul-tool li:hover i{
			color: #ccc;
		}
		.ul-tool i{
			font-size: 24px;
			color: #888;
		}
		.t-img-next{
			display: none;
		}
		[data-model="hand"] #dm{
			cursor: pointer;
		}
		.event-none{
			pointer-events: none;
			border-color: #fff !important;
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
		#slideTest1{
			display: none;
		}
	</style>
</head>
<body data-model="hand">
<div id="di">
	<span class="div-info">
		<span class="img-info"></span>
		<span id="img-index"></span><span class="t-img-next">/</span><span id="img-count"></span>
		<span id="img-name" onclick="common.copy(this.innerText);"></span>
		<div id="slideTest1" class="demo-slider" style="margin-top: 20px"></div>
	</span>
	<div class="tool-left" style="z-index: 100;display: none">
		<div class="tool-left-1">
			<div style="position: relative">
				<textarea id="textPoint" rows="3"></textarea>
				<i class="layui-icon layui-icon-add-1 btn-add-point" title="添加"></i>
			</div>

		</div>
		<div class="tool-left-2">

		</div>
	</div>

	<div id="dm">
		<img id="img" draggable="false"  style="width: 100%">
		<svg id="svg">
			<filter id="shadow-white">
				<feOffset  dx="0" dy="0" in="SourceAlpha" result="offset"></feOffset>
				<feFlood flood-color="white" result="color"></feFlood>
				<feComposite in="color" in2="offset" operator="in" result="shadow"></feComposite>
				<feGaussianBlur in="shadow" stdDeviation="0.001" result="blurredShadow"></feGaussianBlur>
				<feMerge>
					<feMergeNode in="blurredShadow"></feMergeNode>
					<feMergeNode in="SourceGraphic"></feMergeNode>
				</feMerge>
			</filter>
		</svg>
		<%--<svg id="svg" style="" viewBox="0 0 1 1" preserveAspectRatio="none">
			<defs>

			</defs>
		</svg>--%>
	</div>
</div>

<ul class="ul-tool">
	<li title="上一张 pageUp"	onclick="imgNext(-1)" class="t-img-next"><i class="layui-icon layui-icon-left"></i></li>
	<li title="抓手 H"		onclick="selectModel(this,'hand')" class="active"><i class="imgfont imgf-hand"></i></li>
	<li title="直线 L"		onclick="selectModel(this,'line')"><i class="imgfont imgf-line"></i></li>
	<li title="矩形 R"		onclick="selectModel(this,'rect')"><i class="imgfont imgf-rect"></i></li>
	<li title="多边形 P"	onclick="selectModel(this,'polygon')"><i class="imgfont imgf-polygon"></i></li>
	<li title="下一张 pageDown"	onclick="imgNext(1)" class="t-img-next"><i class="layui-icon layui-icon-right"></i></li>
</ul>

<div class="lay-con d-label">
	<form name="flabel" class="layui-form layui-form-pane">
		<div class="layui-form-item">
			<label class="layui-form-label">标签名</label>
			<div class="layui-input-block">
				<input class="layui-input" name="name">
			</div>
		</div>
		<div class="layui-form-item">
			<label class="layui-form-label">备注</label>
			<div class="layui-input-block">
				<input class="layui-input" name="remark">
			</div>
		</div>
	</form>
</div>

<div class="lay-con d-sift">
	<form name="fsift" class="layui-form layui-form-pane">
		<div class="layui-form-item">
			<label class="layui-form-label">目标图片</label>
			<div class="layui-input-block">
				<input class="layui-input" name="uri2" onclick="common.openFile(this)">
			</div>
		</div>
		<div class="layui-form-item">
			<label class="layui-form-label">图片高度</label>
			<div class="layui-input-block">
				<input class="layui-input" name="imgHeight" type="number" placeholder="320">
			</div>
		</div>
		<div class="layui-form-item">
			<label class="layui-form-label">匹配算法</label>
			<div class="layui-input-block">
				<select class="layui-input" name="desMatcherType">
					<option value="1">FLANNBASED</option>
					<option value="2">BRUTEFORCE</option>
					<%--<option value="3">BRUTEFORCE_L1</option>
					<option value="4">BRUTEFORCE_HAMMING</option>
					<option value="5">BRUTEFORCE_HAMMINGLUT</option>
					<option value="6">BRUTEFORCE_SL2</option>--%>
				</select>
			</div>
		</div>
		<div class="layui-form-item">
			<label class="layui-form-label">边缘响应阈值</label>
			<div class="layui-input-block">
				<input class="layui-input" name="contrastThreshold" type="number" step="0.01" placeholder="0.04">
			</div>
		</div>
		<div class="layui-form-item">
			<label class="layui-form-label">边缘消除阈值</label>
			<div class="layui-input-block">
				<input class="layui-input" name="edgeThreshold" type="number" step="0.1" placeholder="10">
			</div>
		</div>
		<div class="layui-form-item">
			<label class="layui-form-label">洛韦比率</label>
			<div class="layui-input-block">
				<input class="layui-input" name="loweRatioThresh" type="number" step="0.01" placeholder="0.75" title="0.6更严格 0.8更宽松">
			</div>
		</div>
		<div class="layui-form-item">
			<label class="layui-form-label">垂直比率</label>
			<div class="layui-input-block">
				<input class="layui-input" name="yRatioThresh" type="number" step="0.01">
			</div>
		</div>
		<div class="layui-form-item">
			<label class="layui-form-label">水平比率</label>
			<div class="layui-input-block">
				<input class="layui-input" name="xRatioThresh" type="number" step="0.01">
			</div>
		</div>
	</form>
</div>

</body>
<script type="text/javascript">
	var svgNS = "http://www.w3.org/2000/svg";
	var pathName = '${SRes.pathName}'.replace(/#/g,"%23");

	var lastIndex = pathName.lastIndexOf("/")
	var imgPath = pathName.substring(0,lastIndex);
	var imgName = pathName.substring(lastIndex+1);
	var tagPos = '${param.tagPos}';
	var tagArr = null;
	<c:if test="${tagArr != null}">
	tagArr = ${tagArr};
	</c:if>
	var img = $("#img")[0];
	var di = $("#di")[0];
	var dm = $("#dm")[0];
	var svg = $("#svg")[0];
	var W,H; //图像实际宽高
	var scale = 1; //当前缩放比列

	var imgArr = [];
	var imgIndex = 0;
	var loadTimes = 0;
	var model = "hand";
	var minConfidence = 0.1;

	layui.use('slider', function() {
		var slider = layui.slider;
		slider.render({
			elem: '#slideTest1',
			value: 10,
			setTips: function(value){ //自定义提示文本
				return "可信度 " + (value/100);
			},
			change: function(value){
				minConfidence = value / 100;
				$("[data-label]").each(function (){
					var conf = this.data.confidence;
					if(conf)
						this.style.display = conf >= minConfidence ? "" : "none";
				});
			}
		});
	});

	function formatName(name){
		return  name.split(/\/+/g).filter(function(v){return v}).join("/");
	}
	pathName = formatName(pathName);
	loadImg(pathName);

	function loadImg(pn){
		pathName = pn;
		img.src = "${base}/image/"+encodeURI(pathName);
		$("#img-name").text(pathName);
		fsift.uri2.value = pathName;
	}

	//设置文件列表
	function setFileList(arr){
		$(".t-img-next").show();
		imgArr = arr.filter(function (v){
			v.name = formatName(v.name);
			var v2 = v.name.slice(v.name.lastIndexOf(".")+1).toLowerCase();
			return common.imgSuffix.indexOf(v2) > -1;
		});
		$("#img-count").text(imgArr.length);
		imgIndex = findIndex(pathName);
		$("#img-index").text(imgIndex+1);
	}

	function findIndex(name){
		for(var i=0;i<imgArr.length;i++){
			if (typeof imgArr[i] === 'object' && imgArr[i].name === name) {
				return i;
			} else if (typeof imgArr[i] === 'string' && imgArr[i] === name) {
				return i;
			}
		}
	}

	var listener = {
		load : function(e){
		},
		label : {
			dblClick : function(e,target){
				$(target).del();
			},
			changed : function(data,label){

			},
			deleted : function(data,label){

			}
		}
	}

	var m1 = [
		{
			title : "选择图片",
			icon : "imgfont imgf-open",
			check : function(node){
				return true;
			},
			click : function(){
				openFileImg(function (path){
					location.href = base+"/view/"+path;
				});
			}
		},{
			title : "下载",
			icon : "imgfont imgf-download",
			check : function(node,li){
				var name = img.src.substring(img.src.lastIndexOf("/")+1);
				if(!common.isImg(name))
					name = name.split(".")[0] + ".jpg";
				$(li).find("a").attr({href:img.src,download:name});
				return node.getAttribute("data-label") == undefined;
			},
			click : function(){
				//window.open("${base}/json/file?name="+pathName);
			}
		},{
			title : "识别",
			icon : "imgfont imgf-img-rec",
			check : function(node,li){
				return true;
			}
		},{
			title : "裁剪",
			icon : "imgfont imgf-crop",
			check : function(node){
				return node.dataset.label || node.nodeName == "polygon";
			},
			click : function (){
				var p = {
					uri  : pathName,
					target : JSON.stringify($(this.target).getTarget()[0])
				};

				var newWin=window.open('about:blank');
				common.ajax("${base}/image/crop",p,function (json){
					newWin.location = "${base}/view/"+json;
				});
			}
		},{
			title : "模板匹配",
			icon : "imgfont imgf-matching",
			click : function (){
				layer.open({
					type: 1,
					title: "匹配参数",
					btn: ['确定','水平预览','垂直预览'],
					content:$(".d-sift"),
					shade: 0,
					area : ["380px","auto"],
					btn1 : function(index){
						sift();
					},
					btn2 : function(index){
						sift(1);
						return false;
					},
					btn3 : function(index){
						sift(2);
						return false;
						/*common.ajax("${base}/util/featureTarget",p,function (json){
					});*/
					}
				});
				/*var targets = $("[data-label],polygon").getTarget();
                targets.push({label:"",left : 0,top : 0,width : 1,height : 1});
                var p = {
                    rec : JSON.stringify({"img" : pathName,"targets" : targets}),
                    hint : JSON.stringify({"drawMatches":true})
                }

                openFileImg(function (path){
                    p.uri2 = path;
                    var newWin = window.open(base+"/view/"+path);
                    newWin.onload = function (){
                        newWin.layer.load(1, {shade: [0.5, '#393D49'],content: '<div style="color: #fff;width: 100px;position: absolute;top: 40px;">匹配中...</div>'});
                        common.ajax("${base}/util/featureTarget",p,function (list){
						newWin.layer.closeAll();
						$(list).each(function (){
							newWin.addLabel(this);
						});
					});
				}
			});*/
			}
		},{
			title : "形状信息",
			//icon : "imgfont imgf-crop",
			check : function(node){
				return node.dataset.label || node.nodeName == "polygon";
			},
			click : function (){
				var labelStr = JSON.stringify($(this.target).getData()[0]) +
						JSON.stringify($(this.target).getTarget()[0]);
				layer.alert(labelStr);
			}
		},{
			title : "清理",
			icon : "imgfont imgf-clean",
			check : function(node){
				return true;
			},
			click : function(){
				cleanLabel();
			}
		}];

	common.addDevExtend(m1,{
		getVal : function (note){
			return pathName;
		},
		getType : function (node){
			return "img";
		},
		callback : function (json,data){
			cleanLabel();
			var t = json.data.targets;
			layer.msg("标签数："+t.length + " " + (json.data.remark || ""),{offset: '50px'});
			$(t).each(function (){
				addLabel(this);
			});
		}
	});

	common.jsonModel("sysScript",{"function":"%imgView%"},function (json){
		var m = {title:"处理","sub":[]};
		if(json.list.length > 0)
			m1.push(m);
		$(json.list).each(function (){
			var _this = this;
			m.sub.push({
				title : this.name,
				check : function(node){
					return node.getAttribute("data-label") != 2;
				},
				click : function(){
					var target = this.target;
					var d = $.extend({"file":pathName,"imgWidth":W,"imgHeight":H},target.data);
					common.execArg(_this.args,function (data){

						common.jsonScript(_this.function,data,function (json){
							if(typeof(json.data) == "string"){
								top.layer.open({
									type : 2,
									btn : false,
									shade : 0,
									title: _this.name,
									content : "${base}/view/"+json.data,
									area : ["80%","80%"],
									scrollbar: false,
									maxmin: true
								});
							}else{
								var t = json.data.targets;
								if(t){
									layer.msg("标签数："+t.length + " " + (json.data.remark || ""));
									$(t).each(function (){
										addLabel(this);
									});
								}else{
									addLabel(json.data);
								}
							}
						});

					},d)
				}
			});
		});
	});

	common.ajaxStop(function (){
		iUI.RMenu("body",m1,function (node) {
			//return node.data || node.parentNode.data;
			return true;
		});
	});

	img.onload = function (){
		var _this = this;
		W = this.naturalWidth;
		H = this.naturalHeight;
		$(".img-info").text(W + " × " + H + " " + Math.round(W*H/10000)+"W");
		if(loadTimes == 0){
			loadTimes ++;
			imgFull();
		}

		scale = W / dm.offsetWidth;
		if(tagPos){
			var tagList = tagPos.split(/[;\n]/);
			for(var i=0;i<tagList.length;i++){
				var a = tagList[i].split(/[,\s]/);
				if(a.length > 7){
					addLabel(textToTarget(a.slice(2).join(",")));
				}else{
					var b = {
						label		: a[0],
						confidence	: a[1],
						left		: a[2],
						top			: a[3],
						width		: a[4],
						height		: a[5]
					}
					if(a.length > 6)
						b.angle = a[6];
					if(b.width * 1 > 1){
						b.left  = a[2] / W;
						b.top   = a[3] / H;
						b.width = a[4] / W;
						b.height= a[5] / H;
					}
					addLabel(b);
				}
			}
		}
		$(tagArr).each(function (){
			addLabel(this);
		});
		listener.load(imgArr[imgIndex]);
	};

	function imgFull(){
		var ratio = H / W;
		if(W < di.offsetWidth && di.offsetHeight){
			dm.style.width = "";
			dm.style.left = (di.offsetWidth - W)/2 + "px";
			dm.style.top = (di.offsetHeight - H)/2 + "px";
		}else if(di.offsetHeight > di.offsetWidth*ratio){
			dm.style.width = "100%";
			dm.style.left = "0px";
			dm.style.top = (di.offsetHeight - di.offsetWidth*ratio)/2+"px";
		}else{
			dm.style.width = di.offsetHeight/ratio+"px";
			dm.style.left = (di.offsetWidth - dm.offsetWidth)/2+"px";
			dm.style.top = "0px";
		}
	}

	function mousePos(e){
		var p = $(dm).position();
		return {
			x: e.clientX - p.left,
			y: e.clientY - p.top
		}
	}


	var target,eClinet,polygon;
	$(dm).mousedown(function (e) {
		if(e.which != 1)
			return;
		if($(e.target).hasClass("tag"))
			return;
		target = e.target;
		eClinet = {x:e.clientX,y:e.clientY};
		imgPos = $(this).position();
		if(model == "rect"){
			if($(target).hasClass("resize")){
				target.parentNode.pos = getPos(target.parentNode);
				polygon = $(target.parentNode);
			}else if(target.dataset.label){
				target.pos = getPos(target);
				polygon = $(target);
			}else{
				polygon = $("<div data-label='0'><span class='tag'>null</span><i class='resize' data-type='1'></i></div>")
						.css({top:e.offsetY,left:e.offsetX}).addClass("active")
						.appendTo(dm);
			}
		}else if(model == "line" || model == "polygon"){
			if(polygon == null){
				polygon = createSvgNS(model == "line" ? "polyline" : "polygon");
				var p = mousePos(e);
				polygon.setAttribute('points',p.x + "," + p.y+","+p.x + "," + p.y);
				if(model == "line")
					polygon.label = $("<span class='svg-label'></span>").css({"left":p.x,"top":p.y-20}).appendTo(dm);
			}else{
				if(model == "line"){
					polygon = null;
				}else{
					var pt = svg.createSVGPoint();
					pt.x = e.offsetX;
					pt.y = e.offsetY;
					polygon.points.appendItem(pt);
				}

			}
		}
		$(polygon).actived();
	}).mousemove(function (e){
		if(eClinet == null)
			return;
		var diffX = e.clientX - eClinet.x;
		var diffY = e.clientY - eClinet.y;
		if(model == "hand"){
			setPos(this,imgPos.left + diffX,imgPos.top + diffY);
		}
		if(polygon == null)
			return;

		if(model == "rect"){
			if($(target).hasClass("resize")){
				polygon.sizeRe(diffX,diffY);
			}else if(target.dataset.label){
				polygon.move(diffX,diffY);
			}else{
				polygon.css({width:diffX,height:diffY});
			}
		}else if(model == "line" || model == "polygon"){
			var p = polygon.points[polygon.points.length -1];
			var p2 = mousePos(e);
			p.x = p2.x;
			p.y = p2.y;
			if(model == "line"){
				var x1 = polygon.points[0].x;
				var y1 = polygon.points[0].y;
				var angle = calcAngle(x1,y1,p2.x,p2.y);
				var px = x1 - p2.x;
				var py = y1 - p2.y;
				var dis = (Math.sqrt(px * px + py * py)*scale).toFixed(1);
				polygon.label.css({"left":p2.x,"top":p2.y-30})
						.html(parseInt(p2.x * scale)+","+parseInt(p2.y*scale) + " <br>D "+dis+ " "+angle+"°");
			}
		}

	}).mouseup(function (e){
		if(e.which != 1)
			return;

		if(model == "line"){

		}else if(model == "polygon"){

		}else if(eClinet){
			if(e.clientX == eClinet.x && e.clientY == eClinet.y){
				if($(polygon).width() < 3 || $(polygon).height() < 3){
					$(polygon).remove();
				}
			}else{
				$(polygon).changed();
			}

			eClinet= null;
			polygon = null;
			imgPos = null;
		}


	}).on("dblclick","[data-label],.tag,.svg-label,polygon,polyline",function (e){
		listener.label.dblClick(e,this);
	}).on("click",".tag",function (e){
		e.stopPropagation();
		var tagNode = this;
		flabel.name.value = tagNode.innerText;
		flabel.remark.value = this.parentNode.title;
		layer.open({
			type: 1,
			title: "标签",
			btn: ['确定'],
			content:$(".d-label"),
			area : ["350px","auto"],
			yes : function(index){
				tagNode.innerText = flabel.name.value;
				tagNode.parentNode.title = flabel.remark.value;
				$(tagNode.parentNode).changed(1);
				layer.close(index);
			}
		});
	}).on("mousewheel DOMMouseScroll", function (e) {
		e.preventDefault();
		var delta = e.originalEvent.wheelDelta;  // chrome & ie
		//e.originalEvent.detail             // firefox
		var z = delta > 0 ? 0.2 : -0.2;
		var p = getPos(dm);
		var ex = e.clientX - p.left;
		var ey = e.clientY - p.top;
		var rw = ex / p.width * z * p.width;
		var rh = ey / p.height * z * p.height;
		var w2 = p.width + p.width * z;
		if(w2 < 100 && delta < 0)
			return;

		setPos(dm,p.left - rw,p.top - rh,w2);
		scale = W / dm.offsetWidth;
		$("[data-label],polyline,polygon").zoom(z);
	});


	$(document).keydown(function (e){
		var fun = keyFun[e.keyCode];
		if(fun)
			fun.call(e.target,e);
	});


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
			width : s.width.indexOf("px") > -1 ? parseFloat(s.width.slice(0,-2)) : node.clientWidth,
			height : s.height.indexOf("px") > -1 ? parseFloat(s.height.slice(0,-2)) : node.clientHeight,
			rotate : s.transform ? s.transform.slice(7,-4)%360 : 0
		};
		return p;
	}

	function renderLabel(data,type){
		var $label =  $("<div data-label='"+(type || 1)+"'><span class='tag'></span><i class='resize' data-type='1'></i></div>");
		var label = $label[0];
		label.data = data;
		$label.find(".tag").attr("data-wh","W:" + Math.round(data.width*W) +" H:" + Math.round(data.height*H));
		label.style["z-index"] = Math.round(10000 - (data.width > 1 ? data.width : data.width * 10000));
		setPos(label,data.left * dm.offsetWidth,data.top * dm.offsetHeight,data.width * dm.offsetWidth,data.height * dm.offsetHeight);
		if(data.angel)
			label.style.transform = "rotate("+(data.angel)+"deg)";
		if(data.remark)
			label.title = data.remark;
		var a = data.label || "";
		//0不显示可信度
		if(data.confidence && data.confidence > 0){
			var d = parseFloat(data.confidence).toFixed(2);
			label.style.display = data.confidence > minConfidence ? "" : "none";
			labelStyle(label,a + " " + d,confColor(data.confidence));
		}else
			labelStyle(label,a,"#f77e4a");
		return label;
	}

	function createSvgNS(name){
		var s =  document.createElementNS('http://www.w3.org/2000/svg', name);
		$(svg).append(s);
		return s;
	}

	function renderPolygon(arr){
		var polygon = createSvgNS('polygon');
		polygon.setAttribute('points',arr.join(","));

		var p = Array.isArray(arr[0]) ? arr[0] : arr;
		var circle = createSvgNS('circle');
		circle.setAttribute("cx", p[0]); // 圆心x坐标
		circle.setAttribute("cy", p[1]); // 圆心y坐标
		circle.setAttribute("r", 0.005);   // 圆的半径
		circle.setAttribute("fill", "blue"); // 填充颜色


		return polygon;
	}

	function addLabelByStr(str){
		var a = str.trim().split(/[,\s]+/);
		var b = {
			label		: a[0],
			confidence	: a[1],
			left		: a[2],
			top			: a[3],
			width		: a[4],
			height		: a[5]
		}
		if(a.length > 6)
			b.angle = a[6];
		return addLabel(b);
	}

	function addLabel(data){
		var label;
		if(data.width){
			if(data.width > 1){
				data.left	= data.left  / W;
				data.top 	= data.top 	 / H;
				data.width	= data.width / W;
				data.height	= data.height/ H;
			}
			label = renderLabel(data);
			$(dm).append(label);
		}

		if(data.points && data.points.length > 0){
			//点 + 可信度
			if(data.points[0].length == 3){
				$(data.points).each(function (index){
					var $label =  $("<div point-index='"+(index+1)+"'></div>");
					$label[0].title = this[2].toFixed(2);
					$label.css({
						left:this[0]*100+"%",
						top:this[1]*100+"%",
						"background-color":confColor(this[2])
					});
					$(dm).append($label);
				});

			}else{
				var polygon = createSvgNS('polygon');
				$(polygon).setPoints(data.points);
				var title = document.createElementNS(svgNS, "title");
				title.textContent = data.label || "null";
				polygon.appendChild(title);
			}

			//$(p).css("stroke",confColor(data.confidence));
			//var title = createSvgNS("title");
			//title.textContent = data.label + " " + data.confidence.toFixed(2);
			//p.appendChild(title);
		}

		if(data.line){
			var points = [[data.line[0],data.line[1]],[data.line[2],data.line[3]]];
			$(createSvgNS('polygon')).setPoints(points).css("stroke","#38e3b2");
			//var title = createSvgNS("title");
			//title.textContent = data.label + " " + data.confidence.toFixed(2);
			//p.appendChild(title);
		}

		return label;
	}

	function cleanLabel(){
		$("[data-label],[point-index].tag,.svg-label,polygon,polyline,circle").remove();
	}

	function confColor(confidence){
		var colorIndex = ["#f77e4a","#f7a04a","#f7c34a","#debd15","#e3ef31","#d3e112","#25c4eb","#099ec4","#448af4","#0d61e0","#094bae"];
		return colorIndex[Math.trunc(confidence*10)];
	}

	function labelStyle(node,name,color){
		$(node).css({"border-color":color});
		$(node).find(".tag").text(name).css({"background-color":color});
	}

	function setLabel(node,tag){
		if(node == null)
			return false;
		$(node).attr("data-label",tag.id).css({"border-color":tag.color});
		$(node).find(".tag").text(tag.name || tag.code).css({"background-color":tag.color});
	}

	function getLabel(){
		return $(dm).find("[data-label]");
	}
	function getActiveLabel(){
		return $(dm).find(".active");
	}

	// 计算角度（0°~360°，左侧为0°，顺时针增加）
	function calcAngle(x1, y1, x2, y2) {
		var angleRadians = Math.atan2(y1-y2, x1-x2);
		var angleDegrees = angleRadians * (180 / Math.PI);
		angleDegrees = (angleDegrees + 360) % 360; // 确保非负数

		return  angleDegrees.toFixed(1);
	}

	function selectModel(li,m){
		model = m;
		$(document.body).attr("data-model",m)
		$(".ul-tool").find(".active").removeClass("active");
		$(li).addClass("active");
	}

	function imgNext(i){
		if(imgIndex + i < 0)
			return layer.msg("已到第一张");
		if(imgIndex + i >= imgArr.length)
			return layer.msg("已到最后一张");

		$("#svg").empty();
		$(".svg-label,[data-label]").remove();
		imgIndex += i;
		$("#img-index").text(imgIndex+1);
		imgName = imgArr[imgIndex];
		if(typeof imgName == "object"){
			tagArr = imgName.targets;
			imgName = imgName.name;
		}
		loadImg(imgName);
	}

	function openFileImg(callback){
		var param = {
			suffix : common.imgSuffix.join(","),
			path : imgPath
		};
		common.openFile(param,callback);
	}

	function textToTarget(text){
		var a = text.trim().split(/[,\s]+/);
		var b = {};
		if(a.length == 2){
			//点
		}else if(a.length == 4){
			//矩形框
			b = {
				left		: a[0],
				top			: a[1],
				width		: a[2],
				height		: a[3]
			}
			if(b.width * 1 > 1){
				b.left  = a[0] / W;
				b.top   = a[1] / H;
				b.width = a[2] / W;
				b.height= a[3] / H;
			}
		}else if(a.length == 5){
			//旋转框
			b = {
				left		: a[2],
				top			: a[3],
				width		: a[4],
				height		: a[5]
			}
			if(b.width * 1 > 1){
				b.left  = a[2] / W;
				b.top   = a[3] / H;
				b.width = a[4] / W;
				b.height= a[5] / H;
			}
		}else{
			//多边形
			b.points = [];
			var isAb = false; //非百分比
			for(var i =0;i<a.length;i++){
				if(a[i]*1 > 1.1)
					isAb = true;
			}
			for(var i =0;i<a.length/2;i++){
				var x = a[i*2+0];
				var y = a[i*2+1];
				if(isAb){
					x = x / W;
					y = y / H;
				}
				b.points.push([x,y]);
			}
		}
		return b;
	}

	function getActive(setPos){
		return $(dm).find(".active");
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

			var a = getLabel().sort(function (a,b){
				var pa = getPos(a);
				var pb = getPos(b);
				var rowA = Math.floor(pa.top / dm.offsetHeight * 10);
				var rowB = Math.floor(pb.top /dm.offsetHeight * 10);
				if (rowA !== rowB) {
					return rowA - rowB;
				}
				return pa.left - pb.left;
			});
			var b = a[0];
			for(var i=0;i<a.length;i++){
				if($(a[i]).hasClass("active")){
					if(e.shiftKey)
						b = a[i == 0 ? a.length - 1 : i-1];
					else
						b = a[i == a.length - 1 ? 0 : i+1];
					break;
				}
			}

			b = $(b).actived();

			//在不可见区域移到居中
			var pos = $(dm).position();
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
				}else{
					$(polygon).remove();
				}
				polygon = null;
				eClinet = null;

			}
		},
		33 : function (e) { // pgUp
			imgNext(-1);
		},

		34 : function (e) { // pgDn
			imgNext(1);
		},
		arrow : function (e,a,b){
			if(e.shiftKey){
				a = a * 10;
				b = b * 10
			}
			if(e.ctrlKey && e.altKey){
				//旋转角度
				getActive().setPos().rotateRe(a/2);
			}else if(e.ctrlKey){
				getActive().setPos().sizeRe(a,b);
			}else if(e.altKey){
				var p = dm.position();
				dm.css({left:p.left+a*20,top:p.top+b*20});
			}else{
				getActive().setPos().move(a,b);
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
			getActive().del();
		}

	};

	$.fn.extend({
		actived : function(){
			if(this.length > 0)
				getActive().removeClass("active");
			this.addClass("active");
			return this;
		},
		setPos : function (){
			this.each(function () {
				this.pos = getPos(this);
			});
			return this;
		},

		zoom : function (z){
			this.each(function (){
				if(this.nodeName == "DIV"){
					var p = getPos(this);
					$(this).css({
						"left"	: p.left  + p.left  * z,
						"top" 	: p.top   + p.top   * z,
						"width"	: p.width + p.width * z,
						"height": p.height + p.height * z
					});
				}else if(this.nodeName == "polyline" || this.nodeName == "polygon"){
					$(this.points).each(function (){
						this.x = this.x + this.x * z;
						this.y = this.y + this.y * z;
					});
					if(this.label){
						var p0 = this.points[1];
						$(this.label).css({left:p0.x,top:p0.y-20});
					}
				}

			});
		},
		move : function (left,top) {
			this.each(function () {
				var p = this.pos;
				var l = p.left + left;
				var t = p.top + top;
				setPos(this,l,t);
			});
			this.changed();
			return this;
		},
		sizeRe : function (width,height) {
			this.each(function () {
				var p = this.pos;
				var w = Math.min(p.width + width,dm.offsetWidth-p.left);
				var h = Math.min(p.height + height,dm.offsetHeight-p.top);
				setPos(this,null,null,w,h);
			});
			this.changed();
			return this;
		},
		rotateRe : function (r) {
			this.each(function () {
				var p = getPos(this);
				$(this).setRotate(p.rotate + r);
			});
			this.changed();
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
			var px = p.left+p.width/2
			var py = p.top+p.height/2;
			var cx = di.offsetWidth / 2;
			var cy = di.offsetHeight / 2;
			$(dm).animate({left:cx - px,top:cy-py});
		},
		setPoints: function (arr) {
			this.each(function (){
				for(var i=0;i<arr.length;i++){
					var pt = svg.createSVGPoint();
					var x = arr[i][0] * dm.offsetWidth;
					var y = arr[i][1] * dm.offsetHeight;
					pt.x = x;
					pt.y = y;
					this.points.appendItem(pt);
				}
			});
			return this;
		},
		getTarget : function (){
			var arr = [];
			var target = {};
			this.each(function (){
				var label = this;
				if(label.dataset.label){
					var transform = label.style.transform;
					target = {
						"label" : $(label).find(".tag").text(),
						"left"	: label.offsetLeft 	 / dm.offsetWidth,
						"top"	: label.offsetTop 	 / dm.offsetHeight,
						"width"	: label.offsetWidth  / dm.offsetWidth,
						"height": label.offsetHeight / dm.offsetHeight,
						"angel" : transform ? transform.slice(7,-4) % 360 : 0
					};
				}else{
					target = {
						"points":[]
					};
					$(label.points).each(function (){
						target.points.push([this.x / dm.offsetWidth,this.y / dm.offsetHeight]);
					});
				}
				arr.push(target);
			});
			return arr;
		},
		getData : function (){
			var arr = [];

			this.each(function () {
				var node = this;
				var data    = getPos(node);
				data.top    = parseInt(data.top    * scale);
				data.left   = parseInt(data.left   * scale);
				data.width  = parseInt(data.width  * scale);
				data.height = parseInt(data.height * scale);

				data.label = $(node).find(".tag").text();
				data.remark = node.title;
				if(node.data && node.data.id)
					data.id = node.data.id;
				arr.push(data);
			});
			return arr;
		},
		changed : function (millisec) {
			this.each(function () {
				var node = this;
				var data = $(this).getTarget()[0];

				//新建，立即通知
				if(!this.data){
					this.data = {};
					listener.label.changed(data,node);
					return true;
				}

				if(node.timer)
					clearTimeout(node.timer);

				this.timer = setTimeout(function(){
					listener.label.changed(data,node);
				},millisec || 500);
			});
			return this;
		},
		del : function (){
			this.each(function (){
				listener.label.deleted($(this).getData()[0],this);
				$(this).remove();
			});
		}
	});


	$(".btn-add-point").click(function (){
		addLabel(textToTarget($("#textPoint").val()));
	});

	function sift(drawMatches){
		var param = common.formJSON(".d-sift");
		param.drawMatches = drawMatches;
		var targets = $("[data-label],polygon").getTarget();
		targets.push({label:"",left : 0,top : 0,width : 1,height : 1});
		var p = {
			rec : JSON.stringify({"img" : pathName,"targets" : targets}),
			hint : JSON.stringify(param),
			uri2 : param.uri2
		}
		common.open("${base}/image/featureTarget",p);
	}

</script>
</html>