<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>

<!doctype html>
<html>
	<head>
		<%@include file="../../common/page_head.jsp" %>
		<link href="${res}/font-station/fa/fa-solid.css" rel="stylesheet">
		<%--<link href="${res}/font-station/iconfont.css" rel="stylesheet">--%>
		<title>图像标签</title>
		<meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=0">
		
<style type="text/css">
	html {
		touch-action: manipulation;
	}
	body{
		-background-color: #272b30;
	}
	.dl,.dc,.dr,#di{
		-border: 1px solid red;
		position: absolute;
		top : 15px;
		bottom: 15px;
	}
	.dl{
		left: 15px;
		width: 200px;
		-border: 1px solid #ccc;
		overflow: auto;
	}
	.dc{
		left: 230px;
		right: 15px;
	}
	.dt{
		border: 1px solid #ccc;
		height: 48px;
	}

	#di{
		border-right: 1px solid #ccc;
		border-bottom: 1px solid #ccc;
		bottom: 0px;
		top : 65px;
		width: 100%;
		box-sizing: border-box;
		overflow: hidden;
		background-image: linear-gradient(#ccc 1px, transparent 1px),
		linear-gradient(90deg, #ccc 1px, transparent 1px),
		linear-gradient(#eee 1px, transparent 0px),
		linear-gradient(90deg, #eee 1px, transparent 1px);
		background-size: 50% 50%,50% 50%,15px 15px,15px 15px;
	}

	.tag-color{
		padding: 0 !important;
		border-right: none;
		width: 10px;
	}
	.tag-color i {
		display: inline-block;
		width: 15px;
		height: 31px;
		float: left;
		color: #fff;
		text-align: center;
		line-height: 31px;
		font-style: normal;
		font-size: 12px;
	}
	.tag-color .layui-input{
		border: none;
		padding: 0;
		width: 15px;
	}

	i.itable-delete{
		margin-left: 0;
	}
	.inline-edit-row td{
		border-bottom: none;
	}
	.ul-img li{
		min-height: 100px;
		-border: 1px solid red;
		margin-bottom: 10px;
		opacity: 0.7;
	}
	.ul-img li:hover,.ul-img li.active{
		border: 1px solid #f5ae8c;
		background-color: #444;
		color: #fff;
		opacity: 1;
	}

	.ul-img li img{
		width: 100%;
	}
	.ul-img div{
		font-size: 12px;
	}
	.img-index{
		color: #4EEF2D;
	}
	.img-name{
		float: right;
	}
	.tool{

	}
	.tool li{
		float: left;
		width: 48px;
		height: 48px;
		font-size: 18px;
		text-align: center;
		line-height: 48px;
		cursor: pointer;
	}

	.tool li.active{
		background-color: #ddd;
	}
	.tool li:hover{
		background-color: #aaa;
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
		border: 2px solid #fff;
	}
	.active[data-label]{
		border-style: solid;
	}
	[data-label] .tag{
		color: #fff;
		position: absolute;
		padding: 0 5px;
		font-size: 12px;
		top: -18px;
		left: -2px;
		white-space: nowrap;
	}
	.hide-tag .tag{
		display: none;
	}
	[data-label]:hover .tag{
		display: inline;
	}

	[data-count]{
		color: #5A50CC;
		font-size: 12px !important;
		padding: 0 !important;
		text-align: right;
	}

	div::-webkit-scrollbar {
		width: 5px;
	}
	div::-webkit-scrollbar-thumb {
		border-radius: 10px;
		box-shadow: inset 0 0 5px rgba(0,0,0,0.2);
		background: rgba(0,0,0,0.2);
	}
	div::-webkit-scrollbar-track {
		box-shadow: inset 0 0 5px rgba(0,0,0,0.2);
		border-radius: 0;
		background: rgba(0,0,0,0.1);
	}
</style>
</head>
<body>
	<div class="dl">
		<ul class="ul-img"></ul>
	</div>
	<div class="dc">
		<div class="dt">
			<ul class="tool">
				<li title="打开"		class="t-open"><i class="fa fa-folder-open"></i></li>
				<li title="参数"		class="t-param"><i class="fa fa-wrench"></i></li>

			</ul>
			<div style="float: right;">
				<input id="confidence" type="range" value="0.8" min="0" max="1" step="0.1" style="margin-top: 12px">
				<span id="confidenceVal" style="vertical-align: super;padding: 0 10px;">0.8</span>
			</div>
		</div>

		<div id="di">
			<div id="dm">
				<img id="img" draggable="false">
			</div>
		</div>
	</div>

</body>
<script type="text/javascript">
	var path = param.path;
	var method = param.method;
	var args = {};
	var targetUrl = decodeURIComponent(param.targetUrl);
	var imgLoad = false;

	var device,project,data,$ulImg,$activeLi;
	var $dl = $(".dl");
	var img = $("#img")[0];
	var di = $("#di")[0];
	var dm = $("#dm")[0];

	$(function (){
		getImgList(path);
	});

	$dl.on("click","li",function () {
		activeLabel = null;imgLoad = false;
		$(".ul-img .active").removeClass("active");
		$activeLi = $(this).addClass("active");
		data = this.data;
		data.width = di.offsetWidth;
		$("#img-name").text("["+data.no+"/"+$dl.find("li").length+"] "+data.name);
		img.src = data.src.replace(/#/g,"%23");
		$("[data-label]").remove();
		if(di.offsetHeight > di.offsetWidth*data.ratio){
			dm.style.width = "100%";
			dm.style.left = "0px";
			dm.style.top = (di.offsetHeight - di.offsetWidth*data.ratio)/2+"px";
		}else{
			dm.style.width = di.offsetHeight/data.ratio+"px";
			dm.style.left = (di.offsetWidth - dm.offsetWidth)/2+"px";
			dm.style.top = "0px";
		}
		if(method){
			//addLabel({"targetleft":50,"targettop":80,"targetWidth":100,"targetHeight":120,"targetNo":"label","confidence":0.5,});
			args.confidence = $("#confidence").val();
			args.relativePath = data.pathName;
			args.imgName = data.name;
			common.devExec(method,args,function (json){
				var list = json.list || json.data || json;
				layer.msg("标签数："+list.length);
				$(json.list || json.data || json).each(function (){
					addLabel(this);
				});
			});
		}

	}).scroll(function () {
		loadImgList();
		if(this.scrollHeight - this.scrollTop == this.offsetHeight){
			$(this).unbind("scroll");
		}
	});

	var imgPos,eClinet;

	$(dm).mousedown(function (e) {
		eClinet = {x:e.clientX,y:e.clientY};
		imgPos = $(this).position();

	}).mousemove(function (e){
		if(eClinet == null)
			return;
		var diffX = e.clientX - eClinet.x;
		var diffY = e.clientY - eClinet.y;
		setPos(this,imgPos.left + diffX,imgPos.top + diffY);
	}).mouseup(function (e){
		imgPos = null ; eClinet= null;

	}).on("mousewheel DOMMouseScroll", function (e) {
		e.preventDefault();
		var delta = e.originalEvent.wheelDelta;  // chrome & ie
		//e.originalEvent.detail             // firefox
		var p = getPos(dm);
		var ex = e.offsetX;
		var ey = e.offsetY;
		if(e.target != dm){
			var tp = getPos(e.target);
			ex += tp.left;
			ey += tp.top;
		}
		var rw = ex / p.w * 0.2 * p.w;
		var rh = ey / p.h * 0.2 * p.h;
		if(delta > 0){
			setPos(dm,p.left - rw,p.top - rh,p.w * 1.2);
			if(img.src.indexOf("width=")>-1)
				img.src = data.src;
		}else{
			if(p.w < 50)
				return;
			setPos(dm,p.left + rw,p.top + rh,p.w * 0.8);
		}
		var p2 = getPos(dm);
		$(di).find("[data-label]").each(function () {
			var p3 = getPos(this);
			setPos(this,p3.left / p.w * p2.w,
					p3.top / p.h * p2.h,
					p3.w / p.w * p2.w,
					p3.h / p.h * p2.h
			)
		});
	});

	$(img).load(function () {
		imgLoad = true;
		$(di).find("[data-label]").each(function () {
			rePos(this);
		});
	}).contextmenu(function (e){
		e.preventDefault();
	});


	$(document).keydown(function (e){
		var fun = keyFun[e.keyCode];
		if(fun)
			fun.call(e.target,e);
	});

	$(".t-open").click(function (){
		common.openFile({"path":path,"suffix":".."},function(fileName){
			getImgList(fileName);
		});
	});

	$(".t-param").click(function (){
		layer.open({
			type: 2,
			title: "参数",
			btn : ["确定"],
			content:"${base}/config/methodArg?d="+param.d+"&method="+method,
			area : ["400px","auto"],
			success: function(layero, index){
				var iframeWin = window[layero.find('iframe')[0]['name']];
				iframeWin.argMap = args;
			},
			yes : function(index,layero){
				var iframeWin = window[layero.find('iframe')[0]['name']];
				args = common.formJSON(iframeWin.fm,null,false,true);
				layer.close(index);
				$activeLi.click();
			}
		});
	});
	$("#confidence").change(function (){
		$("#confidenceVal").text(this.value);
		$activeLi.click();
	});

	function getImgList(path){
		$(".ul-img").empty();
		common.ajax("${base}/config/getFileList",{"path":path},function (arr) {
			arr = arr || [];
			arr = arr.sort().filter(function (v){
				var v2 = v.toUpperCase();
				return v2.indexOf(".JP") > -1 || v2.indexOf(".BMP") > -1 || v2.indexOf(".PNG") > -1 || v2.indexOf(".GIF") > -1;
			});
			$(arr).each(function (index) {
				var data = {
					name : this,
					pathName : path + "/" +this,
					src : "${base}/json/img?name="+path + "/" +this,
					no : index + 1
				};
				var li = $("<li><img><div><span class='img-index'>"+(index+1)+"</span><span class='img-name'>"+this+"</span></div></li>").appendTo(".ul-img")[0];
				li.data = data;
			});
			if(arr.length > 0){
				$ulImg = $(".ul-img").find("img").load(function () {
					this.parentNode.data.ratio = this.naturalHeight / this.naturalWidth;
				});
				$ulImg.eq(0).load(function () {
					$dl.find("li:eq(0)").click();
				});

				loadImgList();
			}else{
				$(".t-open").click();
			}

		});
	}


	function loadImgList(){
		var h = $dl[0].offsetHeight + $dl[0].scrollTop;
		$ulImg.each(function () {
			if(h > this.offsetTop - 400){
				var data = this.parentNode.data;
				if(!this.src)
					this.src = data.src.replace(/#/g,"%23") + "&width=200";
			}
		});
	}

	function setPos(node,left,top,width,height){
		//console.log(left+","+top+","+width+","+height);
		if(left !== undefined)
			node.style.left = left + "px";
		if(top !== undefined)
			node.style.top = top + "px";
		if(width)
			node.style.width = width+"px";
		if(height)
			node.style.height = height+"px";
	}

	function getPos(node) {
		var p = $(node).position();
		var w = node.style.width;
		p.w = w.indexOf("px") > -1 ? parseFloat(w.slice(0,-2)) : node.clientWidth;
		var h = node.style.height;
		p.h = h.indexOf("px") > -1 ? parseFloat(h.slice(0,-2)) : node.clientHeight;
		//console.log(p);
		return p;
	}

	var dataType = {
		targetRes : function(p,t){
			var nw = img.naturalWidth;
			var nh = img.naturalHeight;
			var d = {
				left : t.targetleft / nw * p.w,
				top : t.targettop /nh * p.h,
				width : t.targetWidth / nw * p.w,
				height : t.targetHeight /nh * p.h
			}
			t.tag = data.targetNo;
			return d;
		},
		imgDetect : function(p,t){
			var p2 = t.position.split(",");
			var d = {
				left : p2[0]*p.w,
				top : p2[1]*p.h,
				width : p2[2]*p.w,
				height : p2[3]*p.h
			}
			return d;
		}
	};

	function rePos(node) {
		if(!imgLoad)
			return;
		var p = getPos(dm);
		var p2 = dataType[param.dataType || "targetRes"](p,node.data);
		setPos(node,p2.left,p2.top,p2.width,p2.height);
	}


	function addLabel(data){
		var label =  $("<div data-label=''><span class='tag'></span></div>").appendTo(dm)[0];

		label.data = data;
		rePos(label);
		var c = ["#f77e4a","#f7a04a","#f7c34a","#debd15","#e3ef31","#d3e112","#25c4eb","#099ec4","#448af4","#0d61e0","#094bae"];
		if(data.confidence)
			labelStyle(label,data.tag + " " + data.confidence,c[Math.trunc(data.confidence*10)]);
		else
			labelStyle(label,data.tag,"#f77e4a");
		return label;
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

	var keyFun = {
		37 : function (e) { // left

		},
		38 : function (e) { // up
			$activeLi.prev().click();
			$dl.scrollTop($activeLi[0].offsetTop - $dl.height() /2 + $activeLi.height()/2);
		},
		39 : function (e) { // right

		},
		40 : function (e) { // down
			$activeLi.next().click();
			$dl.scrollTop($activeLi[0].offsetTop - $dl.height() /2 + $activeLi.height()/2);
		}

	};

	$.fn.extend({
		move : function (left,top) {
			this.each(function () {
				var p = getPos(this);
				setPos(this,p.left + left,p.top +top);
			});
		},
		sizeRe : function (width,height) {
			this.each(function () {
				var p = getPos(this);
				$(this).css({width: p.w + width,height:p.h + height});
			});
		},
		save : function (millisec) {
			this.each(function () {
				if(this.timer)
					clearTimeout(this.timer);
				var node = this;
				this.timer = setTimeout(function(){
					saveLabel(node);
				},millisec || 0);
			});
		}
	});


</script>
</html>