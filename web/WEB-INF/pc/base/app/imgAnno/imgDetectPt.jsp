<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>

<!doctype html>
<html>
	<head>
		<%@include file="../../common/page_head.jsp" %>
		<link href="${res}/font-station/fa/fa-solid.css" rel="stylesheet">
		<%--<link href="${res}/font-station/iconfont.css" rel="stylesheet">--%>
		<title>图像验证</title>
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
		right: 280px;
	}
	.dr{
		right: 10px;
		width: 250px;
		border: 1px solid #ccc;
	}
	.dr-con{
		overflow: auto;
		height: calc(100% - 30px);
	}
	.dr-tool{
		height: 30px;
		line-height: 30px;
		border-bottom: 1px solid #ccc;
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
		max-height: 200px;
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
		box-sizing: border-box;
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

	#tPt{
		counter-reset:no-tag 0;
	}

	#tPt thead{
		display: none;
	}
	#tPt tr td:first-child{
		width: 20px;
		padding: 5px;
		text-align: center;
	}

	#tPt .tag-color{
		counter-increment:no-tag 1;
		text-align: center;
		color: #fff;
		border-right: none;
		width: 10px;
		font-size: 12px;
		padding: 0;
	}
	#tPt .tag-color:before{
		content: counter(no-tag);
	}

	#tPt .tag-color .layui-input{
		border: none;
		padding: 0;
		width: 15px;
	}
	.pt-des{
		font-size: 12px;
		color: #999;
		overflow: hidden;
		text-overflow: ellipsis;
		white-space: nowrap;
		width: 160px;
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

	#svg{
		position: absolute;
		top: 0;
		left: 0;
		width: 100%;
		height: 100%;
		--pointer-events: none;
	}
	#svg polygon{
		fill: rgba(0,0,0,0); /*填充颜色*/
		stroke: #e9bf76; 	/*边框颜色*/
		stroke-width:0.002;
	}
	polygon:hover{
		filter : url(#shadow-white)
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
				<input id="confidence" type="range" value="0.2" min="0.2" max="1" step="0.1" style="margin-top: 12px">
				<span id="confidenceVal" style="vertical-align: super;padding: 0 10px;">0.2</span>
			</div>
		</div>

		<div id="di">
			<div id="dm">
				<img id="img" draggable="false">
				<svg id="svg" viewBox="0 0 1 1" preserveAspectRatio="none">
					<defs>
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
					</defs>
				</svg>
			</div>
		</div>
	</div>

	<div class="dr">
		<div class="dr-tool">
			<input data-search="tPt" placeholder="模型" style="border: none;width: 120px;margin-left: 7px;font-size: 12px;color: #009688;">
		</div>
		<div class="dr-con scroll">
			<table id="tPt" class="layui-table" lay-skin="line" style="margin: 0;border: none;">
				<thead>
				<tr>
					<th data-field="color" data-edit="true" data-class="tag-color" width="20"></th>
					<th data-field="title" data-edit="true">名称</th>
					<th data-field="count" width="20">数量</th>
					<th data-type="checkbox" data-field="id" width="20"></th>
				</tr>
				</thead>
			</table>
		</div>
	</div>

	<form data-for="tPt" class="form-horizontal">
		<input type="hidden" name="id">
		<input class="layui-input" name="title" required="required">
		<input class="layui-input" name="color" type="color">
	</form>

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

	var ptMap = {},tagMap = {};

    common.devExec("proIds",{"id":param.pro},function (json){
       common.jsonModel("imgTag",{"pid_in":json.data.join(",")},function (j){
			$(j.list).each(function (){
				tagMap[this.code] = this;
			});
       })
    });

	var tPt = new iTables("#tPt",{"plass":"IMGTAG"+param.pro},{
		baseOption : common.iTableModel("tFile"),
		DESC : "id",
		inline_edit :false,
		callForm : function(params){

		},
		loadAfter:function(list){
			this.selectIndex(0);
		},
		render : {
			title : function (td,data){
				var t1 = data.pathName.split("/");
				td.title = data.description;
				var t0 = data.title || t1[t1.length-3];
				if(t0.match(/^\d{6}_\d{4}/))
					t0 = data.createDate.slice(0,16);
				$(td).append(t0);
				$(td).append("<div class='pt-des'>"+data.description+"</div>");
			},
			color : function (td,data) {
				var c = "000000"+Math.floor(Math.random() * 0xFFFFFF).toString(16);
				var c2 = "#"+c.slice(-6);
				data.color = c2;
				ptMap[data.id] = data;
				td.style["background-color"] = c2;
				td.title = data.createDate + "\n" + data.pathName;
			},
			count : function (td,data){
				$(td).attr("data-count",data.id);
			}
		}

	});

	$("#tPt").on("click",".it-check",function (){
		$activeLi.click();
	});

	function loadLabel(){
		var p = {"name":data.name,"confidence_gt":$("#confidence").val()};
		p.directory = path.slice(path.lastIndexOf("/")+1);
		if(getPts())
			p["ptId_in"] = getPts();

		common.jsonModel("ImgDetect",p,function (json) {
			var pos = {};
			$(json.list).each(function () {
				var label = $(addLabel(this));
				if(label.is("div")){
					var lt = label.position();
					var k = (lt.left/10).toFixed(0)+"_"+(lt.top/10).toFixed(0);
					var i = pos[k] = pos[k] ? pos[k] + 1 : 1;
					if(i > 1)
						label.find(".tag").css("top","-"+i*18+"px");
				}
			});
		});
	}

	function labelCount(){
		var p = {"pageSize":1000000,"confidence_gt":$("#confidence").val(),"directory":path.slice(path.lastIndexOf("/")+1)};
		var c = {};
		common.jsonModel("ImgDetect",p,function (json) {
			$(json.list).each(function () {
				c[this.ptId] = (c[this.ptId] || 0) + 1;
			});
			$("[data-count]").each(function (){
				var cid = $(this).data("count");
				$(this).text(c[cid] || "");
			});
		});
	}

	$(function (){
		getImgList(path);
		labelCount();
	});

	$dl.on("click","li",function () {
		activeLabel = null;imgLoad = false;
		$(".ul-img .active").removeClass("active");
		$activeLi = $(this).addClass("active");
		data = this.data;
		data.width = di.offsetWidth;
		$("#img-name").text("["+data.no+"/"+$dl.find("li").length+"] "+data.name);
		img.src = data.src.replace(/#/g,"%23");
		$("[data-label],polygon").remove();
		loadLabel();

	}).scroll(function () {
		loadImgList();
		if(this.scrollHeight - this.scrollTop == this.offsetHeight){
			$(this).unbind("scroll");
		}
	});

	function imgFull(){
		if(di.offsetHeight > di.offsetWidth*data.ratio){
			dm.style.width = "100%";
			dm.style.left = "0px";
			dm.style.top = (di.offsetHeight - di.offsetWidth*data.ratio)/2+"px";
		}else{
			dm.style.width = di.offsetHeight/data.ratio+"px";
			dm.style.left = (di.offsetWidth - dm.offsetWidth)/2+"px";
			dm.style.top = "0px";
		}
	}

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
			if(p.w < 5)
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
		labelCount();
	});

	function getImgList(path){
		this.path = path;
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
					imgFull();
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

	function rePos(node) {
		var data = node.data;
		if(!data)
			return;
		$(node).attr("data-id",data.id);
		setPosStr(node,data.position);
	}

	function setPosStr(node,posStr) {
		var p = getPos(dm);
		var p2 = posStr.split(",");
		setPos(node,p2[0]*p.w,p2[1]*p.h,p2[2]*p.w,p2[3]*p.h);
	}

	function createSvgNS(name){
		return document.createElementNS('http://www.w3.org/2000/svg', name)
	}

	function addLabel(data){
		var tag = tagMap[data.tag] || {}
		var pt = ptMap[data.ptId] || {};
		var color = tag.color || pt.color;
		var text = data.tag + " " + data.confidence.toFixed(2);
		if(data.position.split(",").length > 5){
			var polygon = createSvgNS('polygon');
			polygon.setAttribute('points',data.position);
			var title = createSvgNS("title");
			title.textContent = text;
			polygon.appendChild(title);
			$(polygon).css("stroke",color);
			$("#svg").append(polygon);
			return polygon;
		}

		var label =  $("<div data-label=''><span class='tag'></span></div>").appendTo(dm)[0];
		label.title = tag.name || "";
		label.data = data;
		rePos(label);
		labelStyle(label,text,color);
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

	function getPts(){
		var tid = [];
		tPt.table.find(".it-check:checked").each(function (){
			var v = this.parentNode.parentNode.data.id;
			tid.push(v);
		});
		return tid.join(",");
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