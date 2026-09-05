<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>

<!doctype html>
<html>
	<head>
		<%@include file="../common/page_head.jsp" %>
		<title>设备类型</title>
		<script src="${res}/plugin/code-prettify/prettify.js"></script>
		<link href="${res}/plugin/code-prettify/prettify.css" rel="stylesheet" >
		<link href="${res}/font/aiotfont/iconfont.css" rel="stylesheet" >
		<style type="text/css">
			html,body{
				height: 100%;
			}
	.dev-icon{
		cursor: pointer;
	}
	.dev-icon i{
		font-size: 24px;
		vertical-align: middle;
	}
	#itableDeviceCommand td,#itableDeviceCommand th{
		padding: 5px;
	}
	.prettyprint{
		margin-top: 15px;
		font-family: "Courier New", monospace;
	}
	.sct-address{
		color: #06b24b;
	}
	.sct-pno{
		color: #EC6D13;
	}
	.sct-index{
		color: #077ee0;
	}
	.sct-script{
		color: #e40efc;
	}
</style>
</head>
<body>

	<div class="layui-fluid sty-auto-h">
		<div class="layui-row layui-col-space15">
			<div class="layui-col-md12">
				<div class="layui-card">
					<div class="layui-card-header">
						<span class="title">设备类型</span>
						<input data-search="tDeviceType" placeholder="搜索">

						<form lay-filter="f1" name="f1" class="layui-form" style="display: inline-block;margin-left: 20px">
							<input type="checkbox" name="isRemoved" title="启用" lay-skin="primary">
						</form>

						<div class="layui-inline" style="float: right">
							<span class="layui-badge layui-bg-blue layuiadmin-badge layui-icon layui-icon-close" style="cursor: pointer" onclick="cancelType()">
								取消未使用类型
							</span>
						</div>
					</div>
					<div class="layui-card-body">
						<div class="layui-row layui-col-space10">

							<table id="tDeviceType" class="">
								<thead>
									<tr>
										<th data-field="isRemoved" data-type="switch" width="40" data-class="tac switch-contrary">状态</th>
										<th data-field="role" data-type="select" width="100">类型</th>
										<th data-field="icon" width="60" data-class="tac">图标</th>
										<th data-field="code">型号</th>
										<th data-field="name">名称</th>
										<th data-field="protocol">协议</th>
										<th data-field="klass" data-translate="select">实现</th>
										<th data-type="edit" data-render="renderEdit" width="60" data-class="tac">操作</th>
									</tr>
								</thead>
							</table>

						</div>
					</div>
				</div>
			</div>

		</div>
	</div>

	<div class="lay-con d-command">
		<table id="itableDeviceCommand" class="layui-table" >
			<thead>
				<tr>
					<th data-field="isRemoved" data-type="switch" width="30" data-class="tac switch-contrary">状态</th>
					<th data-field="id" width="20">ID</th>
					<th data-field="code">指令</th>
					<th data-field="content">内容</th>
					<th data-field="isHex" data-type="switch" width="30px" data-class="tac">HEX</th>
					<th data-field="crc" data-translate="select">校验</th>
					<th data-field="cover" data-type="switch" width="30px" data-class="tac">覆盖</th>
					<th data-field="priority" width="50" title="值越小优先级越高">优先</th>
					<th data-field="delay" width="50">延迟</th>
					<th data-field="responseTime" width="50">响应</th>
					<th data-field="timeout" width="50">超时</th>
					<th data-field="invl" width="50">周期</th>
					<th data-field="pattern">数据验证</th>
					<th data-field="remark">备注</th>
					<th data-field="logRecord" data-type="switch" width="30px" data-class="tac">日志</th>
					<th data-type="edit" width="50px" class="tac" data-class="tac">操作</th>
				</tr>
			</thead>
		</table>
			
		<table id="itableAnalysis" class="layui-table" style="margin-top: 15px;">
			<thead>
				<tr>
					<th data-field="isRemoved" data-type="switch" width="30" data-class="tac switch-contrary">状态</th>
					<th data-field="id" width="20">ID</th>
					<th data-field="code" data-translate="select">解析值</th>
					<th data-field="start" width="60">起始位</th>
					<th data-field="end" width="60">结束位</th>
					<th data-field="conversion" data-translate="select">数值类型</th>
					<th data-field="calc">计算公式</th>
					<th data-field="correct">小数位</th>
					<th data-field="expected">预期值</th>
					<th data-field="pattern">数据验证</th>
					<th data-field="remark">备注</th>
					<%--<th data-field="sequence" width="40">排序</th>--%>
					<th data-type="edit" width="60" class="tac" data-class="tac">操作</th>
				</tr>
			</thead>
		</table>
	</div>
	<form data-for="tDeviceType">
	    <input type="hidden" name="id">
	    <input type="text" class="layui-input" name="code" required="required">
	    <input type="text" class="layui-input" name="name">
		<input type="text" class="layui-input" name="icon">
		<select class="layui-input" name="role"></select>
		<select class="layui-input" name="klass"></select>
		<select class="layui-input" name="protocol"></select>
	</form>
	
	<form data-for="itableDeviceCommand" class="form-horizontal">
		<input type="hidden" name="id">
		<input class="layui-input" id="commandCode" name="code" required="required" data-clear="false">
		<input class="layui-input" name="content" data-clear="false">
		<input class="layui-input" name="invl" type="number" data-clear="false">
		<input class="layui-input" name="delay" data-clear="false">
		<input class="layui-input" name="pattern" data-clear="false">
		<input class="layui-input" name="priority" type="number" data-clear="false">
		<input class="layui-input" name="responseTime" type="number" data-clear="false">				
		<input class="layui-input" name="timeout" data-clear="false">
		<input class="layui-input" name="remark" >
		<select  name="crc" class="layui-input">
			<option value="modbusCRC16">modbusCRC16</option>
		</select>
		<select  name="isHex" class="layui-input" data-clear="false">
			<option value="true">是</option>
			<option value="false">否</option>			    	
		</select>

	</form>
	
	<form data-for="itableAnalysis" class="form-horizontal">
		<input name="id" type="hidden">
		<input class="layui-input" name="remark">
		<input class="layui-input" name="unit">
		<input class="layui-input" name="sequence" type="number">
		<input class="layui-input" name="start" type="number" data-clear="false">
		<input class="layui-input" name="end" type="number" data-clear="false">
		<input class="layui-input" name="calc" data-clear="false" list="L1">
		<input class="layui-input" name="pattern" data-clear="false">
		<select class="layui-input" name="code" data-clear="false"></select>
		<input class="layui-input" name="correct" type="number">
		<input class="layui-input" name="expected">
		<select class="layui-input" name="conversion">
			<option value="hexToDec">16进制正整数</option>
			<option value="hexToFloat32">16进制浮点数</option>
		</select>
	</form>

<datalist id="L1">
	<option value="hexBit('@',0)">16进制转2进制第0位</option>
	<option value="&">发送的数据(全部)</option>
	<option value="$">接设备属性</option>
	<option value="_">接设备字段</option>
</datalist>

<div class="lay-con d-exp" style="height: calc(100% - 30px)">
	<input class="layui-input d-exp-code">
	<pre class="prettyprint" style="height: calc(100% - 50px);overflow: auto;border: 1px solid #ddd;" contenteditable="true" spellcheck="false"></pre>
</div>
	

</body>
<script type="text/javascript">
	var propList = [];
	var context = {};
	var comType = [],comTypeMap = {},protocolMap = {};

	layui.use('form', function(){
		var form = layui.form;
		form.render();
		form.on('checkbox', function(data){
			tDeviceType.load();
		});
	});

	layui.use("autocomplete",function(){
		var cInput = $('#commandCode')[0];
		layui.autocomplete.render({
			elem: cInput,
			//name : "name",
			//code : "method",
			//term : "name",
			data : [],
			loadData : function (json) {
				return comType;
			},

			onselect: function (resp) {
				cInput.value = resp.code;
			}

		})
	});

	common.jsonEnum("varRuntime,deviceRole",function (map){
		$(map.varRuntime).each(function (){
			if(this.code == "context"){
				context = this.value.map || {};
			}
		});
		common.renderSelect("[name='role']",map.deviceRole,{value:"code"});
	});

	common.jsonCont("getCommunicationProtocol",{},function(json){
		$(json.data).each(function (){
			protocolMap[this.class] = this;
		});
		common.renderSelect("[name='protocol']",json.data,{dft:"",value:"class"});
	});
	common.jsonDevice("getTypeImplement",{},function(json){
		var list = json.data.sort(function(a,b){return a.name.localeCompare(b.name)});
		common.renderSelect("[name='klass']",list,{value:"class",dft:""});
	});
	common.jsonModel("deviceProperty",{},function(json){
		propList = json.list;
	});
	common.jsonModel("sysScript",{},function(json){
		common.renderSelect("[name='crc']",    json.list,{dft:"",value:"code",filter:function () {return this.type == "crc";} });
		common.renderSelect("[name='conversion']",json.list,{dft:"",value:"code",filter:function () {return this.type == "numeric";} });
	});
	common.ajaxStop(function(){
		tDeviceType.load();
	});



var tDeviceType = new iTables("#tDeviceType",{},{
	baseOption : common.iTableModel("deviceType","sequence"),
	loadOnInit : false,
	onAfterEdit : function(){
		$('textarea').each(function () {
			  this.setAttribute('style', 'height:' + (this.scrollHeight) + 'px;overflow-y:hidden;');
		})
	},
	callLoadParams: function(param){
		param.isRemoved = f1.isRemoved.checked ? 0 : null;
	},
	render: {
		icon : function(td,data){
			$(td).addClass("dev-icon");
			return "<i class='"+data.icon+"'></i>"
		},
		code : function(td,data){
			var a = $("<a>"+data.code+"</a>");
			a.click(function(){
				layer.open({
					type: 2,
					title : data.name + "["+data.code+"] 类型属性",
					content: "${base}/config/deviceProp?type="+data.code,
					area : ["800px","80%"]
				});
			});
			a.appendTo(td);
		},
		protocol : function(td,data){
			var pro = protocolMap[data.protocol];
			if(!pro){
				return null;
			}
			var a = $("<a>"+pro.name+"</a>");
			a.click(function(){

				common.renderSelect("[name='code']",propList,{value:"code",dft:"",filter:function(){
						this.name = this.name || this.code;
						return this.deviceType == data.code;
					}
				});
				common.jsonCont("getDevTypeMethods",{deviceType : data.code},function(list){
					comType = list.filter(function (v){
						return v.type == "command";
					});
					comType.sort(function (a, b) { return a.code.localeCompare(b.code)});
					comTypeMap = {};
					$(comType).each(function (){
						comTypeMap[this.code] = this.name;
					});
					itableDeviceCommand.load({"deviceType":data.code});
				});

				layer.open({
					type: 1,
					title : data.name + " 指令解析",
					content: $(".d-command"), //捕获的元素
					area : ["90%","90%"]
				});
			});
			a.appendTo(td);
		}
	},

	renderEdit : function (td,data){
		var i = $("<i class='layui-icon layui-icon-export' title='导入/导出' style='color: #517BC3'></i>");

		i.click(function(){
			$(".d-exp-code").val(data.code);
			var t = $(".prettyprint").removeClass("prettyprinted").empty();
			common.ajax("${base}/device/deviceTypeJson",{code:data.code},function (json){
				var f = ["id","createBy","createDate","updateBy","updateDate","isRemoved","deviceType","commandId"];
				jsonFilter(json);
				t.text(JSON.stringify(json,null,2));
				prettyPrint();
			});
			layer.open({
				type: 1,
				title : "导入/导出",
				btn : ["导入"],
				content: $(".d-exp"),
				area : ["500px","80%"],
				yes : function (index){
					layer.close(index);
					common.ajax("${base}/device/deviceTypeImp",{code:$(".d-exp-code").val(),json:$(".prettyprint").text()},function (json){
						tDeviceType.updateRecord(json);
					});
				}
			});
		});
		i.appendTo(td);

	}
});

$("#tDeviceType").on("click",".dev-icon",function(){
	var data = tDeviceType._data;
	layer.open({
		type: 2,
		title : "图标",
		btn: "确定",
		content: "${base}/lang/icon",
		area : ["690px","80%"],
		yes: function(index, layero){
			var iframeWin = window[layero.find('iframe')[0].name];
			data.icon = iframeWin.getValue();
			tDeviceType.saveData(data);
			layer.close(index);
		}
	});
})

function jsonFilter(json){
	var f = ["id","createBy","createDate","updateBy","updateDate","isRemoved","deviceType","commandId"];
	for(var k in json){
		var v = json[k];
		if(v instanceof Array){
			if(v.length == 0)
				delete json[k];
			else
				for(var i=0;i<v.length;i++)
					jsonFilter(v[i]);
		}else if(typeof(v) == "object"){
			jsonFilter(v);
		}else{
			if(f.indexOf(k) > -1 || v === false || v === ""){
				delete json[k];
			}
		}
	}
}

var itableDeviceCommand = new iTables("#itableDeviceCommand",{},{
	baseOption : common.iTableModel("deviceCommand"),
	loadOnInit : false,
	onSelect:function(tr,data){
		itableAnalysis.load({"commandId":data.id});
	},
	callForm:function(params){
		params.deviceType = tDeviceType._data.code;
	},
	loadAfter:function(json){
		this.selectIndex(0);
	},
	render : {
		code : function (td,data){
			return comTypeMap[data.code] || data.code;
		},
		content : function (td,data){
			return  common.strContext(data.content,context).replace(/{\d+}/g,function (p){
				return "<span class='sct-pno' title='参数序号'>"+p+"</span>"
			}).replace(/\s?(\w+\|)+\w+\s?/g,function (p){
				return "<span class='sct-index' title='索引值'>"+p+"</span>"
			}).replace(/@/g,function (p){
				return "<span class='sct-address' title='地址'>"+p+"</span>"
			}).replace(/ \\$\w+/g,function (p){
				return "<span class='sct-script' title='脚本'>"+p+"</span>"
			});
		}
	}
});

var itableAnalysis = new iTables("#itableAnalysis",{},{
	baseOption : common.iTableModel("deviceAnalysis"),
	loadOnInit : false,
	callForm:function(params){
		params.commandId = itableDeviceCommand._data.id;
	}
});

$('textarea').on('input', function () {
	  this.style.height = 'auto';
	  this.style.height = (this.scrollHeight) + 'px';
});

function cancelType() {
	common.jsonModel("device",{},function (json) {
		var devType = {};
		$(json.list).each(function () {
			devType[this.deviceType] = this;
		});
		$(tDeviceType.json).each(function () {
			if(this.isRemoved == 0 && !devType[this.code]){
				common.jsonModel("deviceType",{id:this.id},null,{action:"remove"});
			}
		});
	})
}

</script>
</html>