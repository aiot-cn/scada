<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>

<!doctype html>
<html>
	<head>
	<%@include file="../common/page_head.jsp" %>
	<title>动作链</title>
		
<style type="text/css">
.layui-fluid{
	padding: 15px;
}
.temp{
	display: none;
}
#itablePosition tr td:LAST-CHILD span{
	cursor: pointer;
}
	.args em{
		font-size: 12px;
		font-style: normal;
		border: 1px solid;
		border-radius: 3px;
		padding: 0 5px;
		color: #666;
	}
.args span{
	margin-right: 5px;
	color: #20860F;
	display: inline-block;
}
	#itablePosition tr:nth-child(even){
		background-color: #F6F6F9;
	}
</style>
</head>
<body>

<div class="layui-fluid">
	<div class="layui-row layui-col-space15">
		<div class="layui-col-md12">
			<div class="layui-card">
				<div class="layui-card-header">
					动作链
					<div class="layui-input-inline" style="width: 200px;">
						<input data-search="itablePosition" data-field="id" placeholder="搜索" class="layui-input">
					</div>

                    <a class="layui-btn layui-btn-sm layui-btn-warm" style="position: absolute; right: 16px; top: 5px;" onclick="editRegion()">更新间隔</a>
				</div>
				<div class="layui-card-body">
					<table id="itablePosition" class="layui-table" >
						<thead>
						<tr>
							<th data-type="rownum" width="20">No.</th>
							<th data-field="isRemoved" data-type="switch" width="40" data-class="tac switch-contrary">状态</th>
							<th data-field="id" width="20" >ID</th>
							<th data-field="name">名称</th>
							<th data-field="deviceId"  data-translate="select">执行设备</th>
							<th data-field="method"  data-render="renderMethod">动作</th>
							<th data-field="args" data-render="renderArgs" data-class="args">参数</th>
							<th data-field="delay" width="50">延迟(S)</th>
							<th data-field="sequence" width="40">排序</th>
							<th data-type="edit"  data-render="renderPosition" width="140" data-class="tac">操作</th>
						</tr>
						</thead>
					</table>
				</div>
			</div>
		</div>
	</div>
</div>

<div class="lay-con">
	<table id="itableFunction" class="layui-table" style="table-layout: fixed;">
		<thead>
		<tr>
			<th data-field="id">id</th>
			<th data-field="region" data-translate="select">间隔</th>
			<th data-field="name">名称</th>
			<th data-field="func" data-translate="select">函数</th>
			<th data-field="calc" data-class="tal">公式</th>
			<th data-field="unit" width="30">单位</th>
			<th data-field="fromType" data-translate="select">识别来源</th>
			<th data-field="recType" data-translate="select">识别类型</th>
			<th data-field="devType" data-translate="select">识别设备</th>
			<th data-type="edit" width="120" data-render="renderAssign">操作</th>
		</tr>
		</thead>
	</table>
</div>

<form data-for="itablePosition" class="form-horizontal">
	<input name="id" type="hidden">
	<input class="layui-input" name="name">
	<input class="layui-input" name="args">
	<input class="layui-input" name="delay" type="number">
	<select class="layui-input" name="deviceId"></select>
	<select class="layui-input" name="method"></select>
    <input class="layui-input" name="sequence">
</form>

<form data-for="itableFunction" data-layer='{title : "解析",area : ["400px","auto"]}' style="padding-right: 40px">
	<input name="id" type="hidden">
	<input class="layui-input" name="name" required="required" data-clear="false">
	<input class="layui-input" name="calc" data-clear="false">
	<input class="layui-input" name="unit" data-clear="false">
	<select class="layui-input" name="fromType" data-clear="false"></select>
	<select class="layui-input" name="recType" data-clear="false"></select>
	<select class="layui-input" name="devType" required="required" data-clear="false"></select>
	<select class="layui-input" name="region" required="required" data-clear="false"></select>
	<select class="layui-input" name="func" required="required" data-clear="false"> </select>
</form>

</body>
<script type="text/javascript">

var pid = param.pid;
var klass = param.klass;
var deviceMap = {},deviceTypeMap = {},deviceMethod = {};
common.ajax("${base}/json/getAoMethods",{},function(ao){
	var devSel = itablePosition._form.deviceId;
	var methodSel = itablePosition._form.method;
	$(devSel).change(function(){
		var dm = deviceMethod[deviceMap[this.value].deviceType];
		$(methodSel).empty();
		for(var k in dm){
			$(methodSel).append("<option value='"+k+"'>"+dm[k]+"</option>");
		}
	});

	deviceMethod = ao;
	common.jsonModel("device",{},function(data){
		$(data.list).each(function(){
			deviceMap[this.id] = this;
			if(ao[this.deviceType])
				$(devSel).append("<option value='"+this.id+"'>"+this.name+"</option>");
		});
		$(devSel).change();
	});
	common.jsonModel("deviceType",{},function(data){
		$(data.list).each(function(){
			deviceTypeMap[this.code] = this;
		});
	});
});

common.selectFromDict("region","[name='region']");
common.selectFromDict("ocrFun","[name='func']");
common.selectFromDict("fromType","[name='fromType']");
common.selectFromDict("recType","[name='recType']");
common.selectFromDict("devType","[name='devType']");

common.ajaxStop(function(){
	itablePosition.load();
});

var itablePosition = new iTables("#itablePosition",{"pid":pid,"plass":klass},{
	baseOption : common.iTableModel("actionChain","sequence"),
	loadOnInit : false,
	onSelect:function(tr,data){
		$("#divImage").css({"right":"-1000PX"});
		$("#divPosition").css({"right":"0"});

	},
	callForm:function(params){
		params.pid = pid;
		params.plass = klass;
	},
	renderId : function(td,data){
		return $("<a target='_blank' href='${base}/common/img/position?name="+data.id+".jpg'>"+data.id+"</a>");
	},
	renderMethod : function(td,data){

		 var devType = deviceMap[data.deviceId].deviceType;

		 var name = deviceMethod[devType][data.method];
		return name.slice(0,name.lastIndexOf("("));

	},
	renderArgs : function(td,data){
		var name = deviceMethod[deviceMap[data.deviceId].deviceType][data.method];
		name = name.slice(name.lastIndexOf("(")+1,-1).split(",");
		var arg =  (data.args||"").split(",");
		for(var i = 0;i<name.length;i++){
			var n = name[i];
			var v = arg[i] || null;

			if(n == "action")
				v = "上个动作返回值";

			if(n == "pid"){
				v = $("<a>关联参数</a>");
				v.click(function(){
					layer.open({
						type: 1,
						title : "图像识别",
						content: $(".lay-con"), //捕获的元素
						area : ["90%","90%"]
					});
					itableFunction.load({"positionId":data.id});
				});
				common.jsonModel("plugin.device.ocr.model.ImgFunction",{"positionId":data.id},function(json){
					v.append("("+json.list.length+")");
				},{async:false});
			}

			var s = $("<span><em>"+n+"</em>：</span>");
			s.append(v);
			$(td).append(s);
		}
	},
	renderPosition : function(td,data){
		var span = $("<i class='layui-icon layui-icon-release' title='执行'></i>").click(function(){
			common.ajax("${base}/json/devMethod",{"id":data.id});
		});

		var span2 = $("<i class='layui-icon layui-icon-camera-fill' title='拍照'></i>").click(function(){
			common.ajax("${base}/robot/doCapturing",{"id":data.id},function(data){
				layer.alert(data.success ? "抓拍成功": data.message);
			});
		});

		var span3 = $("<i class='layui-icon layui-icon-templeate-1' title='复制行'></i>").click(function(){
			common.jsonModel("actionChain",{id:data.id},function(data){
				$(data.list).each(function(){
					//动作链复制
					this.id = null;
					common.jsonModel("actionChain",this,null,{"action":"save","async":false});
				});
				itablePosition.load();

			});
		});
		var span4 = $("<i class='layui-icon layui-icon-templeate-1' title='修改间隔'></i>").click(function(){

			layer.prompt({title:data.name+" "+data.id+" 修改间隔 →"},function (text, index) {
				common.jsonModel("plugin.device.ocr.model.ImgFunction",{positionId:data.id},function(data){
					$(data.list).each(function(){
						this.region = text;
						common.jsonModel("plugin.device.ocr.model.ImgFunction",this,null,{"action":"save","async":true});
						layer.closeAll();
					});
				});
			});
		});
		$(td).append(span).append(span2).append(span3).append(span4);

	},
	afterOrder : function (json) {
		itablePosition.load();
	}
});

var itableFunction = new iTables("#itableFunction",{},{
	baseOption : common.iTableModel("plugin.device.ocr.model.ImgFunction","sequence"),
	loadOnInit : false,
	callForm:function(params){
		params.positionId = itablePosition._data.id;
	},
	onSelect:function(tr,data){
		$("#divImage").css({"right":"0","background-image":"url(${base}/common/img/position?size=2&name="+data.positionid+".jpg)"});
		$("#divPosition").css({"right":"-1000PX"});
		$("#imgArea").css({"top":(data.top/yScale)+"px","left":(data.lef/xScale)+"px","width":(data.width/xScale)+"px","height":(data.height/yScale)+"px"});
	},
	renderAssign :function(td,data){
		var span = $("<i class='layui-icon layui-icon-survey' title='测试1'></i>").click(function(){
			common.ajax("${base}/robot/testAnalysis",{"imgfunctionId":data.id},function(json){
				if(json.success){
					layer.alert("ocr："+json.data.ocr +"<br>val："+json.data.val);
				}else{
					layer.alert("测试失败！",{"icon":2});
				}

			});
		});
		var span2 = $("<i class='layui-icon layui-icon-templeate-1' title='复制2'></i>").click(function(){
			itableFunction._onEdit(data);
			itableFunction.form[0].id.value="";
			itableFunction.form[0].IID.value="";
		});

		var span3 = $("<i class='layui-icon layui-icon-spread-left' title='更新位置'></i>").click(function(e){
			e.stopPropagation();
			data.top = y1*yScale;
			data.lef = x1*xScale;
			data.width = (x2-x1)*xScale;
			data.height = (y2-y1)*yScale;
			itableFunction.saveController(data);
		});



		var span4 = $("<i class='layui-icon layui-icon-note' title='显示图框'></i>").click(function(e){
			$(".imgArea").hide();
			$("#divImage").css({"right":"0","background-image":"url(${res}/camera/position/"+data.positionid+".jpg)"});
			$("#divPosition").css({"right":"-1000PX"});
			//通过百度识别返回的数据画框
			common.ajax("${base}/robot/ocrImgByBaiDu",{"id":data.id, flag: 1},function(json){
				if(json.success){
					for(var i=0; i<json.data.length; i++){
						var posData = json.data[i];
						if(!posData){
							return;
						}
						var divRec = $(".imgArea").clone();
						divRec.css({"top":(posData.targettop/yScale)+"px","left":(posData.targetleft/xScale)+"px","width":(posData.targetWidth/xScale)+"px","height":(posData.targetHeight/yScale)+"px"}).show();
						$("#divImage").append(divRec);
					}
				}else{
					layer.alert("识别失败",{"icon":2});
				}

			});
		});

		$(td).append(span);
		$(td).append(span4);
		//$(td).append(span2);
		//$(td).append(span3);
	}
});

function editRegion(){
	layer.prompt({title: " 修改间隔 →"},function (text, index) {
		common.jsonModel("actionChain",{pid:pid},function(data){
			$(data.list).each(function(){
				common.jsonModel("plugin.device.ocr.model.ImgFunction",{positionId:this.id},function(data){
					$(data.list).each(function(){
						this.region = text;
						common.jsonModel("plugin.device.ocr.model.ImgFunction",this,null,{"action":"save","async":true});
					});
				});
			});
		});
	});
}

</script>
</html>