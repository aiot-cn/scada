<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!doctype html>
<html>
	<head>
		<title>模型</title>
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
		height: 150px;
		padding: 15px;
		position: relative;
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
		-border: 1px solid #10182814;
		-background-color: #f5f6fa;
		height: 130px;
		border-radius: 8px;

		text-align: center;
		color: #000;
		cursor: pointer;

		display: flex;
		align-items: center;
		justify-content: center;
	}
	.card-top-main{
		padding: 5px;
	}
	.card-name{
		color: #354052;
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
	.by-server .itable-switch,
	.by-server .itable-edit,
	.by-server .itable-delete {
		display: none;
	}
	.btn-install-model{
		color: #009688;
		display: inline-block;
		padding: 2px 5px;
		border: 1px solid #009688;
		line-height: 15px;
		border-radius: 3px;
		font-size: 12px;
		margin-left: 5px;
		cursor: pointer;
	}

</style>
</head>
<body>
	<div class="header">
		<span style="color: #333;font-weight: bold">模型应用场景</span>
		<ul class="header-tag">
			<li onclick="openDevice()"><i class="layui-icon layui-icon-add-1"></i> 添加</li>
		</ul>
		<div style="float:right">
			<div class="header-search">
				<svg viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg" width="24" height="24" fill="currentColor" class=""><path d="M18.031 16.6168L22.3137 20.8995L20.8995 22.3137L16.6168 18.031C15.0769 19.263 13.124 20 11 20C6.032 20 2 15.968 2 11C2 6.032 6.032 2 11 2C15.968 2 20 6.032 20 11C20 13.124 19.263 15.0769 18.031 16.6168ZM16.0247 15.8748C17.2475 14.6146 18 12.8956 18 11C18 7.1325 14.8675 4 11 4C7.1325 4 4 7.1325 4 11C4 14.8675 7.1325 18 11 18C12.8956 18 14.6146 17.2475 15.8748 16.0247L16.0247 15.8748Z"></path></svg>
				<input type="text" placeholder="搜索">
			</div>
		</div>
	</div>
	<ul class="device-card">
		<li class="card-li" style="display: none">
			<div class="card-top">
				<div class="card-top-icon">
					<image class="card-dev-ico" />
				</div>
				<div class="card-top-main">
					<div>
						<span class="card-name">名称</span>
						<span class="card-type"></span>
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
				<label class="layui-form-label">名称</label>
				<div class="layui-input-block">
					<input class="layui-input" name="name" required>
				</div>
			</div>

			<div class="layui-form-item">
				<label class="layui-form-label">图片</label>
				<div class="layui-input-block">
					<input class="layui-input" name="icon" onclick="common.openFile(this)">
				</div>
			</div>
		</form>
	</div>

	<div class="lay-con d-model">
		<blockquote class="aiot-info-error emoji-font" style="display: none">
			⚠ 未连接到 <a href="http://www.ai-ot.cn" target="_blank">ai-ot.cn</a> 服务器（<span></span>），可手动添加模型
		</blockquote>
		<table id="tAiModel" style="table-layout: fixed">
			<thead>
				<tr>
					<th data-field="isRemoved"  data-type="switch" width="30" class="tac" data-class="tac switch-contrary">加载</th>
					<th data-field="modelPath">文件</th>
					<th data-field="algorithm" width="80">框架</th>
					<th data-field="taskType" data-type="select" width="80">任务类型</th>
					<th data-field="classNames" data-class="to" width="200">标签</th>
					<th data-field="fileSize" width="60">大小Mb</th>
					<th data-type="edit" data-render="renderEdit" width="70" class="tac" data-class="tac">操作</th>
				</tr>
			</thead>
		</table>
	</div>

	<form name="fmodel" data-for="tAiModel" class="layui-form layui-form-pane">
		<input type="hidden" name="id">
		<input type="hidden" name="fileSize">
		<input type="hidden" name="md5">
		<input class="layui-input" name="modelPath" onclick="common.openFile(this)" required data-suffix="onnx" data-path="/lib/aiModel">
		<select class="layui-input" name="algorithm"></select>
		<select class="layui-input" name="taskType"></select>
		<input class="layui-input" name="inputShape" placeholder="1,3,640,640">
		<input class="layui-input" name="classNames" placeholder="person,car,bicycle">
	</form>

<script>
	var scenarioCode;//模型应用场景code
	var modelTypeEnum = []
	var modelTypeDict=[];
	var serverModel = [];
	var card = $(".card-li").clone().removeAttr("style");
	var tAiModel = new iTables("#tAiModel",{},{
		baseOption : common.iTableModel("tAiModel"),
		loadOnInit : false,
		render : {
			"modelPath" : function(td,data){
				td.title = data.modelPath;
				var n = data.modelPath.split("/");
				return n[n.length - 1];
			},
			fileSize : function(td,data){
				td.title = data.md5;
				return data.fileSize;
			}
		},
		renderEdit : function(td,data){
			if(data.id<0){
				var $tr = $(td.parentNode);
				$tr.addClass("by-server");
				var btn = $("<span class='btn-install-model layui-icon layui-icon-add-1'> 安装</span>");
				btn.appendTo(td).click(function (){
					common.devExec("AIMODEL","downloadModel",data,function(json){
						$tr.before(tAiModel.insertRecord(json.data));
						$tr.remove();
					},{"maskType":1})
				});
			}

		},
		callForm:function(params){
			params.scenarioCode = scenarioCode;
		}
	});

	common.jsonEnum("aimodel/scenario,aimodel/algorithm,aimodel/task",function(map){
		modelTypeEnum = map.scenario;
		common.renderSelect(fmodel.algorithm,map.algorithm,{dft:"",value:"code",name:"code"});
		common.renderSelect(fmodel.taskType,map.task,{dft:"",value:"code"});
	});

	common.jsonDict("modelScenario",function(arr){
		modelTypeDict = arr;
	});

	common.ajaxStop(function (){
		$(modelTypeEnum).each(function (){
			$(".device-card").append(buildCard(this));
		});
		$(modelTypeDict).each(function (){
			this.code = this.id;
			$(".device-card").append(buildCard(this));
		});
		common.ajax("http://www.ai-ot.cn/json/getAiModel",{},function(list){
			$(list).each(function (){
				this.id = -this.id;
				if(this.md5)
					serverModel.push(this);
			});
		},{
			errorCallback : function (jqXHR,textStatus,errorThrown){
				$(".aiot-info-error").show().find("span").text(textStatus);
			}
		});
	});

	function buildCard(data){
		var c = card.clone();
		c[0].data = data;
		var imgPath = "${res}/images/noSet.png";
		if(data.icon)
			imgPath = "${base}/image" + data.icon;
		if(!data.id){
			imgPath = "${res}/images/aiModel/scenario/" + data.code + ".jpg";
			c.find(".card-bottom").hide();
		}
		c.find(".card-top-icon").click(function(){
			layer.open({
				type: 1,
				title : data.name + " - 模型",
				content: $(".d-model"),
				area : ["80%","80%"]
			});
			loadModel(data.code);
		});
		c.find(".card-dev-ico").attr("src",imgPath);
		c.find(".card-name").text(data.name);

		return c;
	}

	function loadModel(code){
		scenarioCode = code;
		common.jsonModel("tAiModel",{"scenarioCode":scenarioCode},function(json){
			var m1 = json.list;
			var m2 = [];
			$(serverModel).each(function (){
				if(this.scenarioCode != code)
					return;
				var md5 = this.md5;
				var m3 = m1.filter(function (v,i){
					return md5 == v.md5;
				});
				if(m3.length == 0){
					m2.push(this);
				}
			});
			tAiModel.clear();
			tAiModel._onLoaded(m1.concat(m2));
		});
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
			common.jsonModel("sysDict",{id:data.id},function (json){
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
			fdevice.name.value = data.name || "";
			fdevice.code.value = data.code || "";
			fdevice.icon.value = data.icon || "";
		}
		layer.open({
			type: 1,
			title: "模型应用场景",
			btn: [card ? "修改" : "添加"],
			content:$(".d-device"),
			area : ["350px","auto"],
			yes : function(index){
				var p = common.formJSON(".d-device",null,false,true) || {};
				p.type = "modelScenario";
				common.jsonModel("sysDict",p,function (json){
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