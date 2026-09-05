<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!doctype html>
<html>
	<head>
		<title>模板</title>
		<%@include file="../common/page_head.jsp" %>
		<link href="${res}/font/aiotfont/iconfont.css" rel="stylesheet" >
<style type="text/css">
	html,body{
		height: 100%;
		color: #676f83;
	}
	a{
		color: #676f83;
	}
	.header{
		padding: 30px 50px;
	}
	.header-tag{
		display: inline-block;
	}
	.header-tag li{
		display: inline-block;
		padding: 5px 12px;
		border-radius: 8px;
	}
	.header-tag li.active{
		color: #155aef;
		background-color: #c8ceda33;
		border:1px solid #fffffff2;
	}
	.header-tag li:hover{
		cursor: pointer;
		background-color: #c8ceda33;
	}
	.header-tag i{
		font-size: 14px;
	}
	.device-card{
		margin-left: 50px;
	}
	.device-card li{
		position: relative;
		background-color: #fff;
		border-radius: 10px;
		padding: 15px;
		float: left;
		margin-right: 30px;
		margin-bottom: 30px;
		height: 250px;
		width: 258px;
	}
	.header-search{
		border-radius: 8px;
		position: relative;
	}
	.header-search svg{
		transform: scale(0.7);
		position: absolute;
		left: 5px;
		top: 4px;
	}
	.header-search input{
		border:1px solid rgba(0,0,0,0);
		background-color: #c8ceda40;
		padding: 6px 10px 6px 30px;
		border-radius: 7px;
		font-size: 13px;
	}
	.header-search input:hover{
		border:1px solid #d0d5dc;
	}
	.header-search input:focus{
		background: #fff;
		border:1px solid #d0d5dc;
	}
	.card-li{
		box-shadow:0px 1px 2px 0px #1018280f,0px 1px 3px 0px #1018281a;
	}
	.card-li:hover{
		box-shadow: 0px 4px 6px -2px #10182808,0px 12px 16px -4px #10182814;;
	}
	.card-top{

	}
	.card-top-icon{
		border: 1px solid #10182814;
		height: 200px;
		background-color: #e0f2fe;
		border-radius: 8px;

		text-align: center;
		color: #000;
		cursor: pointer;
	}
	.card-top-main{
		padding: 5px;
	}
	.card-name{
		font-weight: bold;
		color: #354052;
		cursor: pointer;
	}

	.card-type{
		float: right;
		font-size: 13px;
	}

	.card-address{
		float: right;
		font-size: 13px;
	}
	.card-description{
		font-size: 13px;
		margin-top: 5PX;
	}

	.card-bottom{
		position: absolute;
		bottom: 10px;
		right: 15px;
		display: none;
	}
	.card-li:hover .card-bottom{
		display: block;
	}
	.card-bottom i{
		cursor: pointer;
	}
	.card-first a{
		display: block;
		padding: 5px 15px;
		border-radius: 8px;
	}
	.card-first a:hover{
		cursor: pointer;
		background-color: #c8ceda33;
	}
	.card-property{
		margin-right: 10px;
	}
	.card-dev-ico{
		max-width: 100%;
		max-height: 100%;
	}
	.card-property [data-type='1'],
	.card-property [data-type='2']{
		display: inline-block;
		width: 16px;
		height: 16px;
		border-radius: 14px;
		background-color: #ccc;
		vertical-align: middle;
	}
	.card-property [data-type='1'][data-val='0'],
	.card-property [data-type='2'][data-val='0']{
		background-color: #52555c;
	}
	.card-property [data-type='1'][data-val='1'],
	.card-property [data-type='2'][data-val='1']{
		background-color: #2075d7;
	}
	.card-property [data-type='0'][data-state='1']{
		color: #d7860c;
	}
	.card-property [data-type='0'][data-state='2']{
		color: red;
	}
	.card-property [data-type='0']:before{
		content: attr(data-val);
	}
	.card-menu a{
		margin-right: 10px;
		cursor: pointer;
		color: #5a88bd;
	}
</style>
</head>
<body>
	<div class="header">
		<ul class="header-tag">
			<li class="active"><i class="layui-icon layui-icon-cols"></i> 全部</li>
			<li><i class="layui-icon layui-icon-layouts"></i> 页面</li>
			<li><i class="layui-icon layui-icon-template"></i> 平面图</li>
			<li><i class="layui-icon layui-icon-component"></i> 3D</li>
		</ul>
		<div style="float:right">
			<div class="header-search">
				<svg viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg" width="24" height="24" fill="currentColor" class=""><path d="M18.031 16.6168L22.3137 20.8995L20.8995 22.3137L16.6168 18.031C15.0769 19.263 13.124 20 11 20C6.032 20 2 15.968 2 11C2 6.032 6.032 2 11 2C15.968 2 20 6.032 20 11C20 13.124 19.263 15.0769 18.031 16.6168ZM16.0247 15.8748C17.2475 14.6146 18 12.8956 18 11C18 7.1325 14.8675 4 11 4C7.1325 4 4 7.1325 4 11C4 14.8675 7.1325 18 11 18C12.8956 18 14.6146 17.2475 15.8748 16.0247L16.0247 15.8748Z"></path></svg>
				<input type="text" placeholder="搜索">
			</div>
		</div>
	</div>
	<ul class="device-card">
		<li class="card-first">
			<div style="font-size: 12px;padding: 5px 15px;"> 开始</div>
			<a onclick="openDevice()"><i class="layui-icon layui-icon-add-1"></i> 添加页面</a>
			<a><i class="layui-icon layui-icon-add-1"></i> 添加平面图</a>
			<a><i class="layui-icon layui-icon-add-1"></i> 添加3D</a>
		</li>

		<li class="card-li" style="display: none">
			<div class="card-top">
				<div class="card-top-icon">
					<image class="card-dev-ico" />
				</div>
				<div class="card-top-main">
					<div>
						<span class="card-name">名称</span>
						<span class="card-type">类型</span>
					</div>
					<div class="card-description">

					</div>
				</div>
			</div>
			<div class="card-bottom">
				<i class="layui-icon layui-icon-edit"></i>
				<i class="layui-icon layui-icon-delete"></i>
			</div>
		</li>
	</ul>

	<div class="lay-con d-device">
		<form name="fdevice" class="layui-form layui-form-pane">
			<input type="hidden" name="id">
			<div class="layui-form-item">
				<label class="layui-form-label">类型</label>
				<div class="layui-input-block">
					<select class="layui-input" name="type" lay-ignore>
						<option value="0">页面</option>
						<option value="1">平面图</option>
						<option value="2">3D</option>
					</select>
				</div>
			</div>

			<div class="layui-form-item">
				<label class="layui-form-label">标题</label>
				<div class="layui-input-block">
					<input class="layui-input" name="title">
				</div>
			</div>

			<div class="layui-form-item">
				<label class="layui-form-label">地址</label>
				<div class="layui-input-block">
					<input class="layui-input" name="path">
				</div>
			</div>
			<div class="layui-form-item">
				<label class="layui-form-label">图片</label>
				<div class="layui-input-block">
					<input class="layui-input" name="image" onclick="common.openFile(this)">
				</div>
			</div>
		</form>
	</div>

<script>
	var templateList = [];
	var cardType = {
		0:{name:"页面",editor:"html"},
		1:{name:"平面图",editor: "graph"},
		2:{name:"3D",editor: "three"}
	};
	var card = $(".card-li").clone().removeAttr("style");

	common.jsonModel("tTemplate",{isRemoved:0,pageSize:0},function(json){
		templateList = json.list;
	});

	common.ajaxStop(function (){
		$(templateList).each(function (){
			$(".device-card").append(buildCard(this));
		});
	});

	function buildCard(data){
		var c = card.clone();
		c[0].data = data;
		var cardMenu = c.find(".card-menu");
		var imgPath = data.image ? ("${base}/image" + data.image) : "${res}/images/noSet.png";
		c.find(".card-dev-ico").attr("src",imgPath).click(function (){
			var editUrl = "${base}/base/editor/"+cardType[data.type].editor+"?PROTOCOL=tTemplate-"+data.id;
			//window.open(editUrl);
			location.href = editUrl;
			parent.setNavSub(data.title);
		});

		c.find(".card-name").text(data.title).click(function (){
			window.open("${base}/template/"+data.id);
		});
		c.find(".card-type").text(cardType[data.type].name);
		c.find(".card-description").html("<a target='_blank' href='${base}/template"+data.path+"'>"+(data.path || "")+"</a>");

		return c;
	}

	function renderData(json){
		$(".card-property-val").each(function (){
			var dJson = json[this.dataset.id] || {};
			var dMap = dJson.dataMap || {};
			var data = dMap[this.dataset.code] || {};
			this.dataset.val = data.value || "";
			this.dataset.state = data.state || "";
		});
	}

	$(".device-card").on("click",".layui-icon-edit",function (){
		var li = $(this).closest('li');
		openDevice(li[0]);
	}).on("click",".layui-icon-delete",function (){
		var li = $(this).closest('li');
		var data = li[0].data;
		layer.confirm("确定删除 "+ data.name +" ?", {icon: 3}, function(index){
			common.jsonModel("tTemplate",{id:data.id},function (json){
				layer.close(index);
				$(li).remove();
			},{action:"del"});
		});
	});

	function openDevice(card){
		fdevice.reset();
		if(card){
			var data = card.data;
			fdevice.id.value = data.id;
			fdevice.type.value = data.type;
			fdevice.title.value = data.title || "";
			fdevice.path.value = data.path || "";
			fdevice.image.value = data.image ||  "";
		}
		layer.open({
			type: 1,
			title: "视图",
			btn: [card ? "修改" : "添加"],
			content:$(".d-device"),
			area : ["350px","auto"],
			yes : function(index){
				var p = common.formJSON(".d-device",null,false,true) || {};
				common.jsonModel("tTemplate",p,function (json){
					layer.close(index);
					var card2 = buildCard(json.data);
					if(p.id){
						$(card).before(card2).remove();
					}else{
						$(".device-card").append(card2);
					}
				},{action:"save"});
			}
		});
	}

</script>
</body>
</html>