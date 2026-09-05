<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!doctype html>
<html>
	<head>
		<title>工作</title>
		<%@include file="../common/page_head.jsp" %>
		<script src="${res}/js/cronstrue-i18n.min.js"></script>
		<script src="${res}/js/PinYin.js"></script>
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
		height: 50px;
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
		position: relative;
	}

	.card-dev-ico{
		font-size: 24px;
		color: #1f99e3;
	}

	/*禁用样式*/
	.card-disable{
		position: absolute;
		top: -12px;
		right: -0px;
		color: #f17676;;
		display: none;
		font-size: 12px;
		font-weight: bold;
	}

	[data-removed='1'] .card-disable{
		display: block;
	}
	[data-removed='1'] .card-top-icon{
		background-color: #f5f5f5;
	}
	[data-removed='1'] .card-dev-ico{
		color: rgba(0, 0, 0, 0.3);
		font-size: 20px;
	}

	.card-top-main{
		margin-left: 55px;
	}
	.card-name{
		font-weight: bold;
		color: #354052;
		cursor: pointer;
	}
	.card-type{
		float: right;
	}
	.card-description{
		font-size: 13px;
		margin-top: 5PX;
	}
	.card-description a{
		cursor: pointer;
		color: #249af1;
	}
	.card-middle{
		height: 55px;
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
	.layui-layer-page .layui-layer-content{
		overflow: initial;
	}
	.d-form-cron{
		display: block;
		position: relative;
		overflow: visible;
		height: 0;
		margin: 0;
		z-index: 10;
	}
	.f-cron{
		background-color: #fff;
		border: 1px solid #d2d2d2;
		box-shadow: 0px 3px 10px rgba(0, 0, 0, .12);
		display: inline-block;
		padding: 15px;
		border-radius: 3px;
		top: -1px;
		position: absolute;
	}
	.f-cron div{
		color: #333;
		margin-bottom: 10px;
	}
	.f-cron div:last-child{
		margin-bottom: 0;
	}
	.f-cron [type="radio"]{
		vertical-align: middle;
		zoom: 130%;
		cursor: pointer;
	}
	.f-cron .layui-form-radio{
		margin: 0;
		padding: 0;
	}
	.f-cron .layui-form-radio > i{
		margin-right: 0;
	}
</style>
</head>
<body>
	<div class="header">
		<ul class="header-tag">
			<li class="active"><i class="layui-icon layui-icon-cols"></i> 全部</li>
			<li><i class="aiot-icon aiot-icon-device"></i> 设备</li>
			<li><i class="aiot-icon aiot-icon-clock"></i> 定时任务</li>
			<li><i class="aiot-icon aiot-icon-url"></i> URL</li>
			<li><i class="aiot-icon aiot-icon-picture"></i> 图像标签</li>
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
			<a onclick="openTrigger({data:{name:'定时任务',deviceId:'-3'}})"><i class="layui-icon layui-icon-add-1"></i> 定时任务</a>
			<a onclick="openTrigger({data:{name:'拦截回调',phase:'BEFORE'}})"><i class="layui-icon layui-icon-add-1"></i> 拦截回调</a>
			<a onclick="openTrigger({data:{name:'URL',deviceId:'-4'}})"><i class="layui-icon layui-icon-add-1"></i> http接口</a>
		</li>

		<li class="card-li" data-removed='0' style="display: none">
			<div class="card-top">
				<div class="card-top-icon">
					<i class="card-dev-ico"></i>
					<i class="card-disable layui-icon layui-icon-password"></i>
				</div>
				<div class="card-top-main">
					<div>
						<span class="card-name">节点</span>
						<span class="card-type">拦截</span>
					</div>
					<div class="card-description">

					</div>
				</div>
			</div>
			<div class="card-middle">
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

	<div class="lay-con d-trigger">
		<form name="fTrigger" class="layui-form layui-form-pane">
			<div class="layui-form-item">
				<label class="layui-form-label">名称</label>
				<div class="layui-input-block">
					<input class="layui-input" name="name">
				</div>
			</div>

			<div class="layui-form-item">
				<label class="layui-form-label">触发于</label>
				<div class="layui-input-block">
					<input class="layui-input" name="deviceId">
				</div>
			</div>

			<div class="layui-form-item">
				<label class="layui-form-label">的</label>
				<div class="layui-input-block">
					<input class="layui-input" name="member">
				</div>
				<div class="d-form-cron" style="display: none">
					<div class="f-cron">
						<div>
							<input class="bootstrap-input" name="type" type="radio" value="1" checked>
							每<span style="visibility: hidden">占</span> <input class="bootstrap-input" autocomplete='off' name="a1" style="width: 100px">
							<select class="bootstrap-input"  name="a2" lay-ignore="">
								<option value="1">秒</option>
								<option value="2">分钟</option>
								<option value="3">小时</option>
								<option value="4">天</option>
							</select>
							<a class="f-cron-ok layui-btn layui-btn-normal layui-btn-sm" style="float: right">
								<i class="layui-icon layui-icon-ok"></i>
								确定
							</a>
						</div>
						<div>
							<input class="bootstrap-input" name="type" type="radio" value="2">
							每<span style="color: #0A882B">天</span> <input class="bootstrap-input" name="b1" autocomplete='off' style="width: 100px"> 点 <input class="bootstrap-input" name="b2" autocomplete='off'  style="width: 100px"> 分
						</div>
						<div>
							<input class="bootstrap-input" name="type" type="radio" value="3">
							每<span style="color: #0a63b2">周</span> <input class="bootstrap-input" name="c1" list="c1" autocomplete='off' style="width: 100px"> 的 <input class="bootstrap-input" name="c2" autocomplete='off'  style="width: 100px"> 点
							<datalist id="c1">
								<option value="MON-FRI">周一到周五</option>
								<option value="SAT,SUN">周六和周日</option>
								<option value="MON">周一</option>
								<option value="TUE">周二</option>
								<option value="WED">周三</option>
								<option value="THU">周四</option>
								<option value="FRI">周五</option>
								<option value="SAT">周六</option>
								<option value="SUN">周日</option>
							</datalist>
						</div>
						<div>
							<input name="type" type="radio" value="4">
							每<span style="color: #a71d5d">月</span> <input class="bootstrap-input" name="d1" autocomplete='off' style="width: 100px"> 号 <input class="bootstrap-input" name="d2" autocomplete='off'  style="width: 100px"> 点
						</div>

					</div>
				</div>
			</div>
			<div class="layui-form-item">
				<label class="layui-form-label">类型</label>
				<div class="layui-input-block">
					<select class="layui-input" name="phase" lay-ignore="">
						<option value="">--</option>
						<option value="BEFORE">拦截</option>
						<option value="AFTER">回调</option>
					</select>
				</div>
			</div>
		</form>
	</div>

<script>
	var triggerState = {
		NONE:	 {name:"无",detail:"未注册或已被删除"},
		NORMAL:	 {name:"正常",detail:"等待按计划触发任务"},
		PAUSED:	 {name:"暂停",detail:"手动暂停，暂时不会触发任务"},
		COMPLETE:{name:"完成",detail:"已执行完所有调度计划，不再触发任务"},
		ERROR:	 {name:"错误",detail:"配置错误或任务执行过程中抛出未处理的异常"},
		BLOCKED: {name:"阻塞",detail:"线程池资源不足或任务冲突被阻塞"}
	}

	var triggerList = [],deviceProperty = [];
	var deviceTypeMap = {};//TODO 待删
	var deviceMap = {},deviceMethod = {};
	var card = $(".card-li").clone().removeAttr("style");

	var devList = [
		//{name:"❏ 脚本",		id:-1, deviceType:"SCRIPT",		JP:"jb"},
		//{name:"❏ 工作流",	id:-2, deviceType:"WORKFLOW",	JP:"gzl"},
		{name:"❏ 定时任务",	id:-3, deviceType:"CRONTAB",	JP:"dsrw"},
		{name:"❏ URL",		id:-4, deviceType:"URL"},
		{name:"❏ 图像标签",	id:-5, deviceType:"IMG_LABEL",JP:"txbq"}
	];
	var deviceMethod = {
		SCRIPT : [
			//{name:"JS",code:"js",returnType:"java.lang.Object",arg:[{code: "content",type: "text.js"}]}
		],
		WORKFLOW : []
	};

	common.jsonModel("tDevice",{isRemoved:0,pageSize:0},function(json){
		$(json.list).each(function (){
			this.JP = PinYin.get(this.name);
			devList.push(this);
		});
		$(devList).each(function (){
			deviceMap[this.id] = this;
		});
	});

	common.jsonModel("deviceProperty",{isRemoved:0},function (json){
		deviceProperty = json.list;
	});

	common.jsonCont("getAoMethods",{},function(json){
		for(var k in json){
			deviceMethod[k] = json[k];
			$(json[k]).each(function (){
				this.JP = PinYin.get(this.name);
				//code作为提示显示
				this.code2 = "M-"+this.code;
			});
			json[k].sort(function (a, b) {
				return a.code2.localeCompare(b.code2)
			});
		}
	});

	common.jsonModel("sysTrigger",{},function (json){
		triggerList = json.list;
	});

	common.ajaxStop(function (){

		$(triggerList).each(function (){
			$(".device-card").append(buildCard(this));
		});

		/*setInterval(loadData,location.port == "8080" ? 2000 : 5000);
		loadData();*/
	});

	var phaseName = {
		BEFORE : "拦截",
		AFTER : "回调"
	};
	function buildCard(data){
		var c = card.clone();
		c[0].data = data;
		c[0].dataset.removed = data.isRemoved;

		c.find(".card-name").text(data.name);

		var ico = "device"
		var typeName = phaseName[data.phase] || "";
		if(data.deviceId == -3){
			typeName = "定时";
			ico = "clock";
		}else if(data.deviceId == -4){
			typeName = "URL";
			ico = "url";
		}else if(data.deviceId == -5){
			typeName = "图像标签";
			ico = "picture";
		}

		c.find(".card-type").text(typeName);
		c.find(".card-dev-ico").addClass("aiot-icon aiot-icon-"+ico);

		try {
			var desDiv = c.find(".card-description");
			if(data.deviceId == -3){
				desDiv.text(renderCron(data.member));
			}else if(data.deviceId == -4){
				var a = $("<a>"+data.member+"</a>").click(function (){
					layer.open({
						type: 2,
						title: data.member,
						area: ["95%","90%"],
						content: "${base}"+data.member
					});
				});
				desDiv.append(a);
			}else if(data.deviceId == -5){
				desDiv.append("<a>"+data.member+"</a>");
			}else{
				var dev = deviceMap[data.deviceId];
				var method = getMethod(dev.deviceType,data.member);
				var devName = dev.name;
				desDiv.text(devName + " → "+method.name);
			}
		}catch (e){
			console.error(e);
		}

		return c;
	}

	layui.use("autocomplete",function (){
		layui.autocomplete.render({
			elem: fTrigger.deviceId,
			data : devList,
			onselect: function (data,elem) {

			}
		});
		layui.autocomplete.render({
			elem: fTrigger.member,
			data : deviceMethod,
			loadData : function (json,elem) {
				var dev = fTrigger.deviceId.data;
				if(!dev)
					return [];
				return deviceMethod[dev.deviceType];
			},
			onselect: function (resp,elem) {

			}
		});
	});

	function renderCron(cron){
		if(!cron)
			return "";
		return cronstrue.toString(cron, {
			locale: "zh_CN",
			use24HourTimeFormat: true
		});
	}

	$(fTrigger.member).click(function (){
		var dev = fTrigger.deviceId.data;
		if(!dev || dev.deviceType != "CRONTAB")
			return;
		$(".d-form-cron").show();
	});

	//秒，分，小时，日，月，周几
	$(".f-cron-ok").click(function (){
		var f1 = fTrigger;
		var type = f1.type.value;
		var cron = "";
		if(type == 1){
			var v1 = f1.a1.value;
			var v2 = f1.a2.value;
			if(v2 == 1){
				cron = (v1 ? ("0/"+v1) : "*")+" * * * * ?"
			}else if(v2 == 2){
				cron = "0 "+(v1 ? ("0/"+v1) : "*")+" * * * ?"
			}else if(v2 == 3){
				cron = "0 0 "+(v1 ? ("0/"+v1) : "*")+" * * ?"
			}else if(v2 == 4){
				cron = "0 0 0 "+(v1 ? ("1/"+v1) : "*")+" * ?"
			}
		}else if(type == 2){
			var v1 = f1.b1.value;
			var v2 = f1.b2.value;
			cron = "0 "+(v2 || 0)+" "+v1+" * * ?"
		}else if(type == 3){
			var v1 = f1.c1.value;
			var v2 = f1.c2.value;
			cron = "0 0 "+(v2 || 0)+" ? * "+v1
		}else if(type == 4){
			var v1 = f1.d1.value;
			var v2 = f1.d2.value;
			cron = "0 0 "+(v2 || 0)+" "+v1+" * ?"
		}
		var data = {
			name : renderCron(cron),
			code2 : cron
		};
		fTrigger.member.data = data;
		$(fTrigger.member).val(data.name).focus();
		$(".d-form-cron").hide();
	});


	var xhr = new XMLHttpRequest();
	/*function loadData(){
		common.ajax2("${base}/json/getDeviceData",{"siteId":siteId,"isSimplify":true},function(json){
			renderData(json);
		},{"xhr" : xhr});
	}*/

	function renderData(json){
		$(".card-property-val").each(function (){
			var dJson = json[this.dataset.id] || {};
			var dMap = dJson.dataMap || {};
			var data = dMap[this.dataset.code] || {};
			this.dataset.val = data.value || "";
			this.dataset.state = data.state || "";
		});
	}

	$(".device-card").on("click",".card-top-icon",function (){
		var li = $(this).closest('li')[0];
		var data = li.data;
		common.jsonModel("sysTrigger",{"id":data.id,"isRemoved":data.isRemoved ? 0 : 1},function (json){
			data.isRemoved = json.data.isRemoved;
			li.dataset.removed = data.isRemoved;
			layer.msg(data.isRemoved ? "已禁用" : "已启用");
		},{action:"save"});
	}).on("click",".card-name",function (){
		var data = $(this).closest('li')[0].data;
		var editUrl = "${base}/base/editor/workflow?PROTOCOL=tWorkflow-SysTrigger/"+data.id;
		location.href = editUrl;
		parent.setNavSub(data.name);
		/*layer.open({
			type: 2,
			title: data.name,
			area: ['100%', '100%'],
			content: editUrl
		});*/
	}).on("click",".layui-icon-edit",function (){
		var li = $(this).closest('li');
		openTrigger(li[0]);

	}).on("click",".layui-icon-delete",function (){
		var li = $(this).closest('li');
		var data = li[0].data;
		layer.confirm("确定删除 "+ data.name +" ?", {icon: 3}, function(index){
			common.jsonModel("sysTrigger",{id:data.id},function (json){
				layer.close(index);
				$(li).remove();
			},{action:"del"});
		});
	});

	function getMethod(type,code2){
		if(type == "CRONTAB")
			return {"name":renderCron(code2),"code2":code2};
		if(type == "URL" || type == "IMG_LABEL")
			return {"name":code2 || "","code2":code2 || ""};
		var list = deviceMethod[type];
		for(var i=0;i<list.length;i++){
			if(list[i].code2 == code2){
				return list[i];
			}
		}
	}

	function openTrigger(card){
		fTrigger.reset();
		$(".d-form-cron").hide();
		fTrigger.deviceId.data = null;
		fTrigger.member.data = null;
		var data = (card || {}).data;
		if(data){
			fTrigger.name.value = data.name;
			fTrigger.phase.value = data.phase;
			var dev = deviceMap[data.deviceId];
			if(dev){
				fTrigger.deviceId.data = dev;
				fTrigger.deviceId.value = dev.name;
				var method = getMethod(dev.deviceType,data.member);
				fTrigger.member.data = method;
				fTrigger.member.value = method.name;
			}
		}

		var did = (data || {}).id;
		layer.open({
			type: 1,
			title: "添加",
			btn: [did ? "修改" : "添加"],
			content:$(".d-trigger"), //捕获的元素
			area : ["390px","auto"],
			yes : function(index){
				var p = {
					id : did,
					name : fTrigger.name.value,
					deviceId : fTrigger.deviceId.data.id,
					member : fTrigger.member.data.code2,
					phase : fTrigger.phase.value
				};
				if(p.deviceId == -4 || p.deviceId == -5)
					p.member = fTrigger.member.value;

				if(!p.name || !p.deviceId || !p.member){
					layer.msg("请填写完整");
					return;
				}

				common.jsonModel("sysTrigger",p,function (json){
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