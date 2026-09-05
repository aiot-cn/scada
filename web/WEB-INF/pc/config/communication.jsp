<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>

<!doctype html>
<html>
	<head>
	<%@include file="../common/page_head.jsp" %>
	<title>通讯模式</title>
		
<style type="text/css">
	html,body{
		height: 100%;
	}

</style>
</head>
<body>
	<div class="layui-fluid sty-auto-h">
		<div class="layui-row layui-col-space15">
			<div class="layui-col-md12">
				<div class="layui-card">
					<div class="layui-card-header">
						<span class="title">通讯模式</span>
						<a onclick="openPorts()" class="layui-a-tips">本地串口</a>
					</div>
					<div class="layui-card-body">
						<div class="layui-row layui-col-space10">

							<table id="tCommunication">
								<thead>
									<tr>	
										<th data-field="id" width="20px">ID</th>
										<th data-field="isRemoved" data-type="switch" width="40px" data-class="tac switch-contrary">状态</th>
										<th data-field="name">名称</th>
										<th data-field="klass" data-translate="select">通讯</th>				
										<th data-field="uri">参数</th>
										<th data-field="hex" data-type="switch" width="40px" data-class="tac">HEX</th>
										<th data-field="listen" data-type="switch" width="40px" data-class="tac">监听</th>
										<th data-field="logRecord" data-type="switch" width="40px" data-class="tac">日志</th>
										<th data-type="edit" width="60" data-class="tac">操作</th>
									</tr>
								</thead>
							</table>

						</div>
					</div>
				</div>
			</div>

		</div>
	</div>

	<div class="lay-con">

		<table id="tPorts" class="layui-table">
			<thead>
				<tr>
					<th data-type="rownum">No.</th>
					<th data-field="friendlyName">friendlyName</th>
					<th data-field="comPort">comPort</th>
					<th data-field="portDescription">portDescription</th>
				</tr>
			</thead>
		</table>

	</div>
	
	<form data-for="tCommunication" id="form_itable1">
	    <input type="hidden" name="id">
	    <input type="text" class="layui-input" name="name" data-clear="false">
		<input type="text" class="layui-input" name="uri" data-clear="false">
		<select class="layui-input" name="klass" data-clear="false"></select>
		<select  name="listen" class="layui-input" data-clear="false">
			<option value="false">否</option>
			<option value="true">是</option>
		</select>
		<select  name="hex" class="layui-input" data-clear="false">
			<option value="true">是</option>
			<option value="false">否</option>
		</select>
	</form>
</body>
<script type="text/javascript">
var context = {}; //环境变量

common.jsonCont("getCommunicationMode",{},function(json){
	common.renderSelect("[name='klass']",json.data,{value:"class"});
});

common.jsonEnum("varRuntime",function (list){
	$(list).each(function (){
		if(this.code == "context"){
			context = this.value.map || {};
		}
	});
});

common.ajaxStop(function(){
	itables1.load();
});

var itables1 = new iTables("#tCommunication",{},{
	baseOption : common.iTableModel("tCommunication"),
	loadOnInit : false,
	render : {
		name : function(td,data){
			var a = $("<a>"+data.name+"</a>");
			$(td).append(a);
			a.click(function(){
				layer.open({
					type: 2,
					title: data.name + ' - 参数',
					shadeClose: true,
					area: ['400px', '500px'],
					content: '${base}/config/param?type=4&cid='+ data.id + '&klass='+data.klass
				});
			});
		},
		uri : function (td,data){
			return common.strContext(data.uri,context);
		}
	}
});

var tPorts = new iTables("#tPorts",{},{
	getController  : "${base}/json/getSerialPort",
	loadOnInit: false
});

function openPorts(){
	tPorts.load();
	layer.open({
		type: 1,
		title: '本地串口',
		shadeClose: true,
		area: ['800px', '400px'],
		content:$(".lay-con")
	});
}

</script>
</html>