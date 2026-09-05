<%@ page contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>

<!doctype html>
<html>
	<head>
	<%@include file="../common/page_head.jsp" %>
	<title>设备属性</title>
		
<style type="text/css">
	td[data-code]:before{
		content: attr(data-val);
		color: #0a76be;
		margin-right: 5px;
	}
</style>
</head>
<body>
	<div style="margin: 15px">
		<table id="itableProp">
			<thead>
			<tr>
				<th data-field="isRemoved" data-type="switch" width="40" data-class="tac switch-contrary">状态</th>
				<th data-field="name">名称</th>
				<th data-field="code">CODE</th>
				<th data-field="unit" width="45">单位</th>
				<th data-field="type" data-translate="select" width="45">类型</th>
				<%--<th data-field="valType" data-translate="select" width="90">值类型</th>
				<th data-field="scale" width="40">精度</th>
				<th data-field="deviation" width="45">偏差</th>--%>
				<th data-field="devField" data-translate="select" style="min-width: 60px">设备属性</th>
				<%--<th data-field="pointTypeId" data-translate="select" style="min-width: 60px">点位类型</th>
				<th data-field="alarmRule" data-translate="select" style="min-width: 60px">报警规则</th>--%>

				<th data-type="edit" width="60" data-class="tac">操作</th>
			</tr>
			</thead>
		</table>
	</div>
	<form data-for="itableProp">
	    <input type="hidden" name="id">
	    <input type="text" class="layui-input" name="name">
		<input type="text" class="layui-input" name="code" list="prop" required>
		<input type="text" class="layui-input" name="alarmRule">
		<%--<select name="pointTypeId" class="layui-input"></select>--%>
		<select name="type" class="layui-input" data-clear="false">
			<option value="">--</option>
			<option value="0">数值</option>
			<option value="1">状态</option>
			<option value="2">开关</option>
		</select>

		<select name="valType" class="layui-input"></select>
		<input type="text" class="layui-input" name="unit">
		<input type="number" class="layui-input" name="recOnValue" step="0.1">
		<input type="number" class="layui-input" name="notifyOnValue" step="0.1">
		<select name="devField" class="layui-input"></select>
		<%--<select name="classify" class="layui-input"></select>
		<input type="text" class="layui-input" name="ext">
		<input type="number" class="layui-input" name="deviation" step="0.1">
		<input type="number" class="layui-input" name="scale" step="0.000000001">--%>
	</form>

	<datalist id="prop">
		<option value="temperature">温度 ℃</option>
		<option value="humidity">湿度 %RH</option>
		<option value="sf6">六氟化硫 ppmv</option>
		<option value="o2">氧气 %</option>
		<option value="voltage">电压 V</option>
		<option value="current">电流 A</option>
		<option value="resistor">电阻 Ω</option>
		<option value="density">密度</option>
		<option value="pressure">压力 Pa</option>
		<option value="volume">音量 db</option>
		<option value="voc">挥发性有机化合物</option>
		<option value="depth">水深 cm</option>
		<option value="power">功率 W</option>
		<option value="electricEnergy">电能 Wh</option>
		<option value="powerFactor">功率因数</option>
		<option value="dayTotal">日累计</option>
		<option value="monthTotal">月累计</option>
		<option value="arming">布防</option>

		<option value="AI1">数据1</option>
		<option value="AI2">数据2</option>
		<option value="AI3">数据3</option>
		<option value="AI4">数据4</option>

		<option value="DO1">开关1</option>
		<option value="DO2">开关2</option>
		<option value="DO3">开关3</option>
		<option value="DO4">开关4</option>

		<option value="DI1">输入1</option>
		<option value="DI2">输入2</option>
		<option value="DI3">输入3</option>
		<option value="DI4">输入4</option>

		<option value="S1">状态1</option>
		<option value="S2">状态2</option>
		<option value="S3">状态3</option>
		<option value="S4">状态4</option>

		<option value="M1">报警1</option>
		<option value="M2">报警2</option>
		<option value="M3">报警3</option>
		<option value="M4">报警4</option>

	</datalist>
</body>
<script type="text/javascript">

var type = param.type;
var did  = param.d;
var deviceType = {};

if(did){
	//setInterval(loadVal,3000)
}


/*common.selectFromDict("dataType","[name='type']",{dft:""});
common.selectFromDict("dataClassify","[name='classify']",{dft:""});
common.jsonEnum("valType",function(json){
	common.renderSelect("[name='valType']",json,{value:"code",dft:""});
});*/

if(!type && did){
	common.jsonModel("tDevice",{"id":did},function (json) {
		type = json.list[0].deviceType;
	},{"async":false});
}

common.jsonModel("deviceType",{"code":type},function (json) {
	deviceType = json.list[0];
	$("#typeName").text(deviceType.name);
});

/*common.jsonModel("tPointType",{},function (json) {
	common.renderSelect("[name=pointTypeId]",json.list,{dft:""});
});*/

common.jsonCont("getAoReflect",{"deviceType":type},function (list) {
	common.renderSelect("[name=devField]",list,{dft:"",value:"code",filter:function () {
			return this.type == "analysis";
		}
	});
});

common.ajaxStop(function (){
	if(did)
		itableProp.load({"deviceId":did});
	else
		itableProp.load({"deviceType":type,"deviceId":"NULL"});
});

var itableProp = new iTables("#itableProp",{},{
	baseOption : common.iTableModel("deviceProperty","sequence"),
	loadOnInit : false,
	//callback : loadVal,
	render : {
		unit : function (td,data){
			$(td).attr("data-code",data.code);
			return data.unit;
		}
	},
	callForm:function(params){
		params.deviceType = type;
		params.deviceId = did;
	}
});

function loadVal(){
    if(did)
        common.devExec("getDataMap",{},function (json){
            var data = json.data;
            $("td[data-code]").each(function (){
                $(this).attr("data-val",(data[this.dataset.code] || {}).value);
            });
        });
}
</script>
</html>