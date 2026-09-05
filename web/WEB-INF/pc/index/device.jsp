<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!doctype html>
<html>
	<head>
		<title>设备</title>
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
		margin: 0 50px;
		display: grid;
		grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
		gap: 15px;
	}
	.device-card li{
		background-color: #fff;
		border-radius: 10px;
		height: 120px;
		padding: 15px;

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
		height: 48px;
	}
	.card-top-icon{
		border: 1px solid #10182814;
		float: left;
		height: 38px;
		width: 38px;
		line-height: 38px;
		background-color: #e0f2fe;
		border-radius: 8px;

		text-align: center;
		color: #000;
		cursor: pointer;
	}
	.card-top-main{
		margin-left: 55px;
	}
	.card-name{
		font-weight: bold;
		color: #354052;
		cursor: pointer;
	}

	.card-commu{
		float: right;
		font-size: 13px;
		cursor: pointer;
	}

	.card-address{
		float: right;
		font-size: 13px;
	}
	.card-description{
		font-size: 13px;
		margin-top: 5PX;
	}
	.card-middle{
		height: 55px;
	}
	.card-data-t0,.card-data-t1,card-data-t2{
		line-height: 24px;
	}

	.card-bottom{
		text-align: right;
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
		font-size: 24px;
		color: rgba(0, 0, 0, 0.6);
	}
	.card-property [data-type='1'],
	.card-property [data-type='2']{
		display: inline-block;
		width: 14px;
		height: 14px;
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
			<li><i class="layui-icon layui-icon-water"></i> 传感器</li>
			<li><i class="layui-icon layui-icon-share"></i> 控制器</li>
			<li><i class="layui-icon layui-icon-video"></i> 视频</li>
			<li><i class="layui-icon layui-icon-picture"></i> 图像识别</li>
			<li><i class="layui-icon layui-icon-app"></i> 应用</li>
			<li><i class="layui-icon layui-icon-notice"></i> 通知</li>
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
			<a onclick="openDevice()"><i class="layui-icon layui-icon-add-1"></i> 添加设备</a>
			<a onclick="openDevType()"><i class="layui-icon layui-icon-template-1"></i> 设备类型</a>
			<a onclick="openCommu()"><i class="layui-icon layui-icon-bluetooth"></i> 通讯管理</a>
		</li>

		<li class="card-li" style="display: none">
			<div class="card-top">
				<div class="card-top-icon">
					<i class="card-dev-ico"></i>
				</div>
				<div class="card-top-main">
					<div>
						<span class="card-name">节点</span>
						<span class="card-commu">COM3</span>
						<span class="card-address" style="margin-right: 10px">#01</span>
					</div>
					<div class="card-description">

					</div>
				</div>
			</div>
			<div class="card-middle">
				<div class="card-menu"></div>
				<div class="card-data-t0"></div>
				<div class="card-data-t1"></div>
				<div class="card-data-t2"></div>
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
				<label class="layui-form-label">设备类型</label>
				<div class="layui-input-block">
					<select class="layui-input" name="deviceType" lay-ignore></select>
				</div>
			</div>

			<div class="layui-form-item">
				<label class="layui-form-label">名称</label>
				<div class="layui-input-block">
					<input class="layui-input" name="name">
				</div>
			</div>

			<div class="layui-form-item">
				<label class="layui-form-label">通信方式</label>
				<div class="layui-input-block">
					<select class="layui-input" name="communication" lay-ignore></select>
				</div>
			</div>
			<div class="layui-form-item">
				<label class="layui-form-label">地址</label>
				<div class="layui-input-block">
					<input class="layui-input" name="address">
				</div>
			</div>
		</form>
	</div>

<script>
	var deviceTypeList = [],deviceProperty = [],devList = [],commuList = [],menuList = [];
	var deviceTypeMap = {},commuMap = {},deviceMap = {};
	var card = $(".card-li").clone().removeAttr("style");
	common.jsonModel("deviceType",{isRemoved:0},function(json){
		deviceTypeList = json.list;
		common.renderSelect("[name='deviceType']",deviceTypeList,{value:"code"});
	});
	common.jsonModel("deviceProperty",{isRemoved:0},function (json){
		deviceProperty = json.list;
	});

	common.jsonModel("tDevice",{ASC:"sequence",isRemoved:0,pageSize:0},function(json){
		devList = json.list;
	});

	common.jsonModel("tCommunication",{isRemoved:0},function(json){
		commuList = json.list;
		common.renderSelect("[name='communication']",commuList,{dft:""});
	});

	common.jsonCont("getRMenu",{},function (json){
		menuList = json;
	});

	common.ajaxStop(function (){
		$(deviceTypeList).each(function (){
			deviceTypeMap[this.code] = this;
			this.property = [];
		});
		$(commuList).each(function (){
			commuMap[this.id] = this;
		});
		$(devList).each(function (){
			deviceMap[this.id] = this;
		});

		$(devList).each(function (){
			$(".device-card").append(buildCard(this));
		});

		setInterval(loadData,location.port == "8080" ? 2000 : 5000);
		loadData();
	});

	var xhr = new XMLHttpRequest();
	function loadData(){
		common.ajax2("${base}/json/getDeviceData",{"siteId":siteId,"isSimplify":true},function(json){
			renderData(json);
		},{"xhr" : xhr});
	}

	function buildCard(data){
		var property = [];
		$(deviceProperty).each(function (){
			if(this.deviceId == data.id){
				var dev = deviceMap[this.deviceId];
				if(dev)
					property.push(this);
			}else if(this.deviceType == data.deviceType){
				var dt = deviceTypeMap[this.deviceType];
				if(dt)
					property.push(this);
			}
		});

		var c = card.clone();
		c[0].data = data;
		var deviceType = deviceTypeMap[data.deviceType] || {};
		var devTypeProperty = deviceType.property;
		$(devTypeProperty).each(function (){
			var code = this.code;
			var a = property.filter(function (v){return code == this.code}).length;
			if(!a)
				property.push(this);
		});

		var devId = data.id;
		$(property).each(function (){
			var d = $("<span class='card-property'><span class='card-property-name'>"+this.name+"</span>" +
					" <span class='card-property-val' data-type='"+this.type+"' data-id='"+devId+"' data-code='"+this.code+"' data-val=''></span>" +
					"<span>"+(this.unit || '')+"</span>" +
					"<span>");
			d[0].data = this;
			c.find(".card-data-t"+this.type).append(d);
		});
		var cardMenu = c.find(".card-menu");
		$(menuList).each(function (){
			if(this.devId == devId && this.suffix == "DEV"){
				var menuName = this.name;
				var url = "${base}"+this.menu + (this.menu.indexOf("?") == -1 ? "?" : "&") + "d="+devId
				$("<a>"+menuName+"</a>").click(function (){
					location.href = url;
					parent.setNavSub(data.name + " - " +menuName);
				}).appendTo(cardMenu);
			}
		});
		c.find(".card-name").text(data.name);
		var commuName = (commuMap[data.communication] || {}).name || "";
		c.find(".card-commu").text(commuName);
		c.find(".card-address").text(data.address || "");
		c.find(".card-dev-ico").addClass(deviceType.icon || "aiot-icon aiot-icon-device");
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

	$(".device-card").on("click",".card-name",function (){
		var data = $(this).closest('li')[0].data;
		layer.open({
			type: 2,
			title: data.name,
			shadeClose: true,
			area: ['600px', '90%'],
			content: '${base}/lang/object?type=1&cid='+ data.id + '&deviceType='+data.deviceType
		});
	}).on("click",".card-top-icon",function (){
		var data = $(this).closest('li')[0].data;
		layer.open({
			type: 2,
			title : data.name + "["+data.deviceType+"] - 设备属性",
			content: "${base}/config/deviceProp?type="+data.deviceType+"&d="+data.id,
			area : ["800px","90%"]
		});
	}).on("click",".card-commu",function (){
		var data = $(this).closest('li')[0].data;
		layer.open({
			type: 2,
			title : "通讯 - " + this.innerText,
			content: "${base}/index/debug?room=COM-"+data.communication,
			area : ["80%","90%"]
		});
	}).on("click",".card-property-name",function (){
		var dev = $(this).closest('li')[0].data;
		var prop = this.parentNode.data
		layer.prompt({title: prop.name}, function(text, index){
			common.devExec(dev.id,"putData",{"code":prop.code,value:text},function (){
				layer.close(index);
			})
		});
	}).on("click",".layui-icon-edit",function (){
		var li = $(this).closest('li');
		openDevice(li[0]);
	}).on("click",".layui-icon-delete",function (){
		var li = $(this).closest('li');
		var data = li[0].data;
		layer.confirm("确定删除 "+ data.name +" ?", {icon: 3}, function(index){
			common.jsonModel("tDevice",{id:data.id},function (json){
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
			fdevice.deviceType.value = data.deviceType;
			fdevice.name.value = data.name || "";
			fdevice.communication.value = data.communication || "";
			fdevice.address.value = data.address ||  "";
		}
		layer.open({
			type: 1,
			title: "设备",
			btn: [card ? "修改" : "添加"],
			content:$(".d-device"), //捕获的元素
			area : ["350px","auto"],
			yes : function(index){
				var p = common.formJSON(".d-device",null,false,true) || {};
				common.jsonModel("tDevice",p,function (json){
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

	function openDevType(){
		layer.open({
			type: 2,
			title: "设备类型",
			btn: false,
			content:"${base}/index/deviceType",
			area : ["80%","80%"]
		})
	}

	function openCommu(){
		layer.open({
			type: 2,
			title: "通讯管理",
			btn: false,
			content:"${base}/config/communication",
			area : ["80%","80%"]
		})
	}

</script>
</body>
</html>