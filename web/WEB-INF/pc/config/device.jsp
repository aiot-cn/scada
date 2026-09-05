<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!doctype html>
<html>
<head>
<title>设备设置</title>
<%@include file="../common/page_head.jsp" %>

<script src="${res}/plugin/iTrees/iTrees.js" type="text/javascript"></script>
<link href="${res}/plugin/iTrees/iTrees.css" rel="stylesheet" >
<style type="text/css">
	html,body{
		height: 100%;
	}
	.tree-search{
		width: 80px;
	}
	.itable td, .itable th{
		padding: 2px 5px;
	}
	.itable tfoot td{
		padding: 3px;
	}
</style>
</head>
<body class="page-device">
	<div class="layui-fluid sty-auto-h">
		<div class="layui-row layui-col-space15">
			<div class="layui-col-md2">
				<div class="layui-card">
					<div class="layui-card-header">
						<span class="title">区域</span>
						<span data-toolbar="iTree"></span>
					</div>
					<div class="layui-card-body">
						<ul id="iTree"></ul>
					</div>
				</div>
			</div>

			<div class="layui-col-md10">
				<div class="layui-card">
					<div class="layui-card-header">
						<span class="title">设备</span>
						<input data-search="itableDevice" placeholder="搜索">
						<a class="layui-btn layui-btn-normal layui-btn-sm" href="${base}/file/downDb">
							<i class="layui-icon layui-icon-download-circle"></i>
							备份
						</a>
                        <div data-itable="tool_itableDevice" class="itable-tool"></div>

                    </div>
					<div class="layui-card-body div-table">
						<table id="itableDevice" class="itable">
							<thead>
								<tr>
									<th data-field="isRemoved" data-type="switch" width="40" data-class="tac switch-contrary-no">状态</th>
									<th data-field="id" width="20" data-show="false">ID</th>
									<th data-field="groupId" data-translate="select" data-edit="true" data-show="false">组别</th>
									<th data-field="name" data-edit="true">名称</th>
									<th data-field="deviceType" data-edit="true">类型</th>
									<th data-field="communication" width="80" data-edit="true" data-translate="select">通信方式</th>
									<th data-field="address" data-edit="true">地址</th>
									<th data-field="dec" width="60">十进制</th>
									<%--<th data-field="exp1" data-edit="true">exp1</th>
									<th data-field="exp2" data-edit="true" data-show="false">exp2</th>--%>
									<th data-type="edit" width="70" class="tac">编辑</th>
								</tr>
							</thead>
						</table>
					</div>
				</div>
			</div>
		</div>
	</div>
		<form data-for="itableDevice" class="form-horizontal">
			<input type="hidden" name="id">
			<input type="hidden" name="parentId">
			<input name="siteId" required="required" data-default="1">
			<input type="hidden" name="isRemoved" data-default="0">
			<input class="layui-input" name="dec" type="number" data-clear="false">
			<input class="layui-input" name="address" data-clear="false">
			<input class="layui-input" name="name" required="required"  data-clear="false">
			<select class="layui-input" name="communication"  data-clear="false"></select>
			<select class="layui-input" name="deviceType" required="required"  data-clear="false"></select>
			<select class="layui-input" name="rate" data-clear="false"></select>
			<select class="layui-input" name="groupId" data-clear="false"></select>
			<input class="layui-input" name="exp1">
			<input class="layui-input" name="exp2">
		</form>
		<form data-for="iTree">
			<input type="hidden" name="id">
			<input type="hidden" name="parentId">
			<input type="hidden" name="type" value="devGroup" data-clear="false">
			<div class="layui-form-item">
				<div class="layui-inline">
					<label class="layui-form-label">名称</label>
					<div class="layui-input-inline">
						<input class="layui-input" name="name" required="required">
					</div>
				</div>
			</div>
		</form>
		<div id="softkey"></div>
</body>
<script type="text/javascript">
	var siteId = 1;
var devTypeMap = {};
common.jsonModel("deviceType",{"isRemoved":0},function(data){
	$(data.list).each(function (){
		devTypeMap[this.code] = this;
	});
	common.renderSelect("[name='deviceType']",data.list,{value:"code"});
});

common.jsonModel("tCommunication",{},function(data){
	common.renderSelect("[name='communication']",data.list,{dft:""});
});

common.ajaxStop(function(){
	itableDevice.load();
});

//common.selectFromDict("rate","[name='rate']",{dft:""});
//common.selectFromDict("devGroup","[name='groupId']",{dft:"",value:"id"});

var itableDevice = new iTables("#itableDevice",{"pageSize":0},{
	baseOption : common.iTableModel("tDevice","sequence"),
	//parentName : "parentId",
	loadOnInit: false,
	render : {
		name : function(td,data){
			var a = $("<a>"+data.name+"</a>");
			$(td).append(a);
			a.click(function(){
				if(data.isRemoved){
					return layer.msg("设备未启用");
				}
				layer.open({
					type: 2,
					title: data.name,
					shadeClose: true,
					area: ['600px', '90%'],
					content: '${base}/lang/object?type=1&cid='+ data.id + '&deviceType='+data.deviceType
				});
			});
		},
		deviceType : function (td,data){
			var a = $("<a>"+devTypeMap[data.deviceType].name+"</a>");
			a.click(function(){
				layer.open({
					type: 2,
					title : data.name + "["+data.deviceType+"] 设备属性",
					content: "${base}/config/deviceProp?type="+data.deviceType+"&d="+data.id,
					area : ["90%","90%"]
				});
			});
			a.appendTo(td);
		}
	},
	callData : function(data){
		if(data.address && data.address.length == 2){
			data.dec =  parseInt(data.address,16);
		}
	},
	callForm : function (params){
		if(iTree.data && !params.groupId)
			params.groupId = iTree.data.id;
		var decInput = itableDevice._form.dec;
		var nameInput = itableDevice._form.name;
		var dec = decInput.valueAsNumber;
		if(dec){
			$(decInput).val(dec + 1).blur();
		}
		nameInput.value = nameInput.value.replace(/\d+$/,function(a){return parseInt(a) + 1});
	}
});


$("[name='dec']").blur(function (){
	var hex = this.valueAsNumber.toString(16).toUpperCase();
	if(hex.length % 2 != 0)
		hex = "0"+hex;
	$('[name="address"]').val(hex);
});

var iTree = new iTrees("#iTree",{type:"devGroup"},{
	baseOption : common.iTableModel("sysDict"),
	callback : function(){

	},
	onSelect : function (data,li){
		itableDevice._form.groupId.value = data.id;
		var ids = data.id;
		$(li).find("li").each(function (){
			ids += ","+this.data.id;
		});
		itableDevice.load({"groupId_in":ids});
	}
});



//$(":input").not("select").virtualkeyboard();


</script>
</html>