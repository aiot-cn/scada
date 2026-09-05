<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>

<!doctype html>
<html>
	<head>
	<%@include file="../common/page_head.jsp" %>
	<title>设备数据</title>
		
<style type="text/css">
.layui-fluid{
	padding: 15px;
}
.temp{
	display: none;
}
</style>
</head>
<body>

<div class="layui-fluid">

	<table id="tDevData" class="layui-table" style="margin: 0">
		<thead>
		<tr>
			<th data-field="code">键</th>
			<th data-field="type" data-dict="dataType">类型</th>
			<th data-field="value">值</th>
			<th data-field="state" data-dict="hisStatus">状态</th>
			<th data-field="time" data-render="renderTime">时间</th>
			<th data-field="prevVal">上次值</th>
			<th data-field="prevState" data-dict="hisStatus">上次状态</th>
			<th data-field="prevTime" data-render="renderTime">上次时间</th>
			<th data-field="toVal" data-render="renderTime">到值</th>
			<th data-field="toTime">到时间</th>
		</tr>
		</thead>
	</table>

</div>

</body>
<script type="text/javascript">
var type = param.type || 0;
var cid = param.cid;
var klass = param.klass;
var list = [];

var tDevData = new iTables("#tDevData",{},{
	loadOnInit : false,
	renderTime : function(td,data,icolumn,text){
		return new Date(text).format("yy-MM-dd hh:mm:ss");
	}
});

common.jsonDict("dataType",function (list,map){
	tDevData.dict.type = map;
});
common.jsonDict("hisStatus",function (list,map){
	tDevData.dict.state = tDevData.dict.prevState = map;
});

common.devExec(cid,"getDataMap",{},function (json){
	for(var k in json.data){
		var data = json.data[k];
		data.code = k;
		list.push(data);
	}
});

common.ajaxStop(function (){
	tDevData._onLoaded(list);
});



</script>
</html>