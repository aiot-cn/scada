<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>

<!doctype html>
<html>
	<head>
	<%@include file="../common/page_head.jsp" %>
		<script type="text/javascript">
			var jQuery = $;
		</script>
		<script type="text/javascript" src="${res}/plugin/JSONView/jquery.jsonview.min.js"></script>
		<link href="${res}/plugin/JSONView/jquery.jsonview.css" rel="stylesheet">
		<script src="${res}/js/PinYin.js"></script>
	<title>动作链</title>
		
<style type="text/css">
	.layui-fluid{
		padding: 15px;
	}
	.temp{
		display: none;
	}
	#tActionChain td{
		overflow: hidden;
		white-space: nowrap;
	}
	#tActionChain tr:hover td,#tActionChain tfoot td{
		overflow: initial;
		white-space: initial;
		word-break: break-all;
	}

	#tActionChain tbody td,#tActionChain thead th{
		padding: 0 5px;
	}
	#tActionChain tbody tr td:nth-child(2){
		padding-left: 0;
	}
	#tActionChain tr td:LAST-CHILD span{
		cursor: pointer;
	}

	#tActionChain .switch-contrary-no{
		padding: 2px 5px;
	}
	.has-arg{
		cursor: pointer;
		color: #009688;
	}
	.is-action-group{
		color:#095ecb;
	}
	.is-deprecated{
		color: #999;
		text-decoration: line-through;
	}
	a.var-void{
		color: #999;
	}
	a.var-return{
		color: #CC3434;
	}
	a.var-void.var-return{
		text-decoration: line-through;
	}
	.args{
		font-size: 12px;
		font-style: normal;
		margin-right: 10px;
	}
	.args label{
		background: #37b36a;
		color: #fff;
		padding: 2px 3px;
		cursor: help;
		line-height: 23px;
	}
	.args label.a-url{
		background: #364ae7;
		cursor: pointer;
	}
	.args label.a-object{
		background: #c418c5;
	}
	.args span{
		padding: 1px 5px;
		border: 1px solid #999;
		border-left: none;
		color: #333;
		cursor: text;
	}
	.args span.a-tran{
		color: #c50822;
	}
	.args span.a-pid{
		color: #d109e9;
		font-weight: bold;
	}
	.args span.a-var{
		color: red;
	}
	.args span.a-sel{
		color: blue;
	}

	table tr:nth-child(even){
		background-color: #F6F6F9;
	}

	.hide-set tr th:last-child,.hide-set tr td:last-child{
		display: none;
	}

	.ita-nodata td{
		display: table-cell !important;
	}

	.rs1{
		color: #666;
		font-weight: bold;
	}
	.rs2{
		color: blue;
	}
	.rs3{
		color: #009688;
	}
	.rs4{
		color: red;
	}
	.in-layui-card .layui-fluid{
		padding: 5px 0;
	}
	a.layui-icon-reply-fill{
		-color: #CB51D4;
	}
	.explain{
		font-size: 12px;line-height: 20px;color: #666
	}
	.explain:hover .explain-con{
		display: block;
	}
	.explain-con{
		display: none;
	}
	.var-res{
		font-size: 12px;
	}
	.var-res:before{
		content: " ：";
	}
	.var-res:empty:before{
		content: "";
	}
</style>
</head>
<body class="">
<div class="layui-fluid">

	<%--<div>
		<button class="layui-btn layui-btn-warm layui-btn-sm" onclick="execAction()"><i class="layui-icon layui-icon-release"></i> 执行</button>
	</div>--%>
	<div id="t1">
		<table id="tActionChain" class="layui-table" style="table-layout:fixed;margin-top: 0">
			<thead>
			<tr>
				<th data-field="isRemoved" data-type="switch" width="24" data-class="tac switch-contrary-no" onclick="execAction(findArg())" title="执行" style="background-color: #FFB800;color:#fff">
					<i class="layui-icon layui-icon-release"></i>
				</th>
				<%--<th data-field="sequence" width="40">No.</th>
                <th data-field="id" width="20" >ID</th>
                <th data-field="groups"  data-translate="select" width="40">组</th>--%>
				<th data-field="deviceName"  data-translate="select" data-type="level" width="150">执行设备</th>
				<th data-field="methodName"   width="120">动作</th>
				<th data-field="args" data-class="td-arg">参数</th>
				<th data-field="variable" width="80"><a onclick="getRes()">变量</a></th>
				<th data-type="edit"  data-render="renderEdit" width="55" data-class="tac">操作</th>
			</tr>
			</thead>
		</table>
	</div>
	<div class="explain">
		<span>...</span>
		<div class="explain-con">
			1. <span class="rs1">逻辑</span> 返回值<span class="rs4"> 不为空 </span>，数值类型<span class="rs4"> >0 </span>时执行<u>下层</u>动作链<br>
			2. <span class="rs1">运算</span> 调用<span class="rs2"> 动作链 </span>的 <span class="rs3">EL表达式</span>、<span class="rs3">执行脚本</span><br>
			3. <span class="rs1">参数</span>
			<a class="args"><label title="model.table.Test" class="a-object">json</label><span>{name:'张三'~score:7.5}</span></a>
			<a class="args"><label title="java.lang.Object">el</label><span class="a-var">#t.score+1&gt;2?3:'b'</span></a>
			<span class="rs4">#</span>变量或表达式<br>
			4.Ctrl反选 Ctrl+A全选 Ctrl+C复制 Ctrl+V粘贴 DEL删除
		</div>

	</div>
</div>

<div class="lay-con action-res"></div>
<form name="f1" data-for="tActionChain" class="form-horizontal">
	<input type="hidden" name="id">
	<input type="hidden" name="parentId">
	<input type="hidden" name="deviceId" required>
	<input type="hidden" name="method">
	<input class="layui-input" name="args">
	<input class="layui-input" name="variable">
	<input class="layui-input" name="deviceName" required>
	<input class="layui-input" name="methodName" required>
    <input class="layui-input" name="sequence">
</form>

<div id="layParam" class="lay-con">
	<form name="f2" class="layui-form layui-form-pane form-label-wider" action="">
		<%--<div class="form-item">
			<div class="layui-form-item">
				<label class="layui-form-label" title="">参数</label>
				<div class="layui-input-block">
					<input class="layui-input" name="text" placeholder="">
				</div>
			</div>
		</div>--%>
	</form>
</div>

<div style="position: absolute;left: -1000px">
	<input id="text1">
</div>

</body>
<script type="text/javascript">

var pid = param.pid;
var klass = param.klass || "model.table.ActionChainGroup";
var action={},selectMap={};
var tActionChain,resWin;
var temp = $(".temp");

var devSel = f1.deviceId;
var devList = [
	//{name:"❏ 脚本",id:-1,deviceType:"script"},
	{name:"❏ 工作流",id:-2,deviceType:"workflow"}
];
var deviceMethod = {
	workflow : []
};

$(document.body).addClass(param.style);

$(document).on("keydown",function (e){
	if(e.target.nodeName == "INPUT")
		return;
	if(e.keyCode == 46){//DEL
		var arr = [];
		tActionChain.table.find(".row_selected").each(function (){
			arr.push({"IID":this.data.IID,"id":this.data.id});
		});
		tActionChain._onRemove(arr);
	}
	if(e.ctrlKey){
		e.preventDefault();
		if(e.keyCode == 65){//A
			$(tActionChain._tbody).find("tr").addClass("row_selected");
		}
		if(e.keyCode == 67){//C
			var arr = [];
			tActionChain.table.find(".row_selected").each(function (){
				arr.push({
					"deviceId":this.data.deviceId,
					"method":this.data.method,
					"args":this.data.args,
					"variable":this.data.variable
				});
			});
			$("#text1").val(JSON.stringify(arr)).select();
			document.execCommand("copy");
			layer.msg("已复制"+arr.length+"条");
		}
		if(e.keyCode == 86){

		}
	}
}).on("paste",function (e){
	var clipboardData = e.originalEvent.clipboardData || window.clipboardData;
	var pastedData = clipboardData.getData('text');
	var arr = JSON.parse(pastedData);
	$(arr).each(function (){
		tActionChain.saveData(this);
	});
});

if(!pid){
	common.jsonModel(klass,{"code":param.code},function (json){
		var data = json.list[0] || {};
		pid = data.id;
	},{async:false});
	if(!pid)
		throw(layer.alert("没有"+param.code+"动作组",{icon:2}));
}

common.jsonModel("tDevice",{"isRemoved":0,"siteId":siteId},function(data){
	$(data.list).each(function (){
		this.JP = PinYin.get(this.name);
		devList.push(this);
	});
});


common.ajax("${base}/json/getAoMethods",{},function(json){
	for(var k in json){
		deviceMethod[k] = json[k];
		json[k].sort(function (a, b) {
			a.JP = PinYin.get(a.name);
			return a.code.localeCompare(b.code)
		});
	}
});

common.jsonModel("tWorkflow",{pid:"null"},function (json){
	$(json.list).each(function (){
		var m = {"code":this.code,"name":this.name || this.code,"returnType":this.returnType,"arg":[]};
		if(this.args)
			$(this.args.split("\n")).each(function (){
				var a = this.split("|");
				m.arg.push({"code":a[0],"name":a[1],"select":a[2],"type":(a[3]|| "?")});
			});
		deviceMethod.workflow.push(m);
	});
});

var tActionChain = new iTables("#tActionChain",{},{
	baseOption : common.iTableModel("tAction","sequence"),
	parentName : "parentId",
	loadOnInit : false,
	hasSort : false,
	callForm:function(params){
		params.pid = pid;
		params.plass = klass;
	},
	callData : function (data,tr){
		try{
			var dev = getDevice(data.deviceId);
			data.deviceName = dev.name || data.deviceId;
			data.deviceType = dev.deviceType;
			data.meth = getMethod(dev.deviceType,data.method);
		}catch(e){
			console.error(data);
		}

	},
	render : {
		deviceId : function (td,data){
			return data.deviceName;
		},
		methodName : function(td,data){
			var m = data.meth;
			var t = $("<span>"+m.name+"</span>");
			t[0].title = data.method;
			data.methodName = m.name;
			if(data.method.indexOf("#") == 0){
				t.addClass("is-action-group");
			}
			if(m.deprecated)
				t.addClass("is-deprecated");
			if(m.arg.length > 0){
				t.addClass("has-arg").click(function () {
					layer.open({
						type: 2,
						title: data.method + ' - 参数 ['+data.id+"]",
						btn : ["确定"],
						area: ['500px', '320px'],
						content:"${base}/lang/methodArg?deviceType="+data.deviceType+"&method="+encodeURIComponent(data.method)+"&args="+encodeURIComponent(data.args|| ''),
						yes: function(index,layero){
							var body = layer.getChildFrame('body', index);
							var iframeWin = window[layero.find('iframe')[0]['name']];
							var arg = "";
							$(iframeWin.fm).find("input,select,textarea").each(function() {
								if(this.name && !this.disabled) {
									arg += ","+this.value;
								}
							});
							data.args = arg.slice(1);
							tActionChain.saveData(data);
							layer.close(index);
						}
					});
				});
			}


			return t;

		},
		args : function(td,data){
			var args = data.meth.arg;
			var argVal =  (data.args||"").split(",");


			for(var i = 0;i<args.length;i++){
				var arg = args[i];

				var s = $("<a class='args'></a>").attr("title",arg.code + "("+arg.type.replace("java.lang.","")+")");
				var lable = $("<label></label>").appendTo(s);
				lable.text(arg.name || arg.code);
				if(arg.type.indexOf(".") > 0 && arg.type.indexOf("java.lang") != 0)
					lable.addClass("a-object");
				if(arg.url){
					lable.addClass("a-url");
					var pid = argVal[i] || data.id;
					var url = base + arg.url + (arg.url.indexOf("?")>-1 ? "&":"?")+arg.code+"="+pid;
					lable.click(function () {
						layer.open({
							type: 2,
							title: '参数',
							shadeClose: true,
							area: ['90%', '500px'],
							content: url
						});
					});
				}
				var v = argVal[i] || "";
				var value = $("<span>"+v+"</span>").appendTo(s);

				/*var k = data.deviceType.klass.split(".");
                k = k[k.length-1];
                var v2 = selectMap[k+"."+data.method+"."+arg.code+"."+v];
                if(v2 != undefined){
                    value.text(v2).css("color","blue")[0].title = v;
                }*/

				if(arg.type == "common.CommonAction"){
					s.hide();
					//value.text("上下文");
				}

				if(arg.select){
					var sel = arg.select.split(",");
					for(var j=0;j<sel.length;j++){
						var s3 = sel[j].split(":");
						if(s3[0] == v){
							value.text(s3[1]);
							value.addClass("a-sel");
							break;
						}
					}
				}

				//上层id标识
				if(arg.code == "pid" && !v){
					value.text(data.id);
					value.addClass("a-pid");
				}
				//变量
				if(v.indexOf("#")==0){
					value.addClass("a-var");
				}
				//方法翻译
				if(arg.translate){
					value.addClass("a-tran");
					var tranParam = {};
					tranParam[args[i]] = v;
					common.jsonDevExec(data.deviceId,arg.translate,tranParam,function (json) {
						value.text(json.data);
					},{async:false})
				}

				$(td).append(s);
			}
		},
		variable : function (td,data){
			td.title = data.meth.returnType;
			var a = $("<a>"+(data.variable || '')+"</a>").appendTo(td);
			$(td).append("<span class='var-res'></span>")
			if(data.meth.returnType == "void"){
				a.addClass("var-void");
			}
			if(!data.variable){
				a.addClass("layui-icon layui-icon-reply-fill");
			}
			if(data.variable == "return"){
				a.addClass("var-return");
			}
			a.click(function (e){
				if(e.ctrlKey){
					common.jsonCont("getActionState",{"id":data.id},function(json){
						if(json){
							$(".action-res").JSONView(json,{

							});
							layer.open({
								type: 1,
								content:$(".action-res"),
								area:["80%","80%"]
							});
						}else{
							layer.msg("还未执行过");
						}

					});
				}else{
					var path = data.id + ".chain";
					layer.open({type : 2,btn : false,shade : 0,title: path,
						content : "${base}/view/" + path,
						area : ["80%","80%"],scrollbar: false,maxmin: true,
						success: function(layero, index){
							resWin = window[layero.find('iframe')[0].name];
						}
					});
				}
			});
		}
	},

	renderEdit : function(td,data){
		var span = $("<i class='itable-font itable-run' title='执行'></i>").click(function(){
			common.ajax("${base}/json/devMethod",{"id":data.id});
		});

		var span2 = $("<i class='itable-font itable-copy' title='复制'></i>").click(function(){
			var d = $.extend({},data,{id:null,"sequence":tActionChain.json.length+1});
			tActionChain.saveData(d);
		});

		var more = $("<i class='itable-font itable-more'></i>");
		var ul = $("<ul class='ita-more-ul'></ul>").appendTo(more);
		$("<li></li>").appendTo(ul).append(span).append(span2)
				.append($(td).find(".itable-edit"))
				.append($(td).find(".itable-delete"))
				.append($(td).find(".itable-tree-sub"));
		$(td).append(more);
	},
	callback : getRes
});

common.ajaxStop(function(){
	tActionChain.load({"pid":pid,"plass":klass,"type_in":"0,1"});
	var af = tActionChain._form;
	layui.use("autocomplete",function(){
		layui.autocomplete.render({
			elem: af.deviceName,
			data : devList,
			onselect: function (resp,elem) {
				f1.deviceId.value= resp.id;
				f1.methodName.value = "";
				f1.args.placeholder = "参数";
			}
		});

		layui.autocomplete.render({
			elem: af.methodName,
			//name : "name",
			//code : "method",
			//term : "name",
			//url: '${base}/table/getList?tableName=device',
			data : deviceMethod,
			loadData : function (json) {
				var type = getDevice(af.deviceId.value).deviceType;
				var arr = json[type];
				arr.sort(function (a, b) { return a.code.localeCompare(b.code)});
				return arr;
			},

			onselect: function (resp) {
				af.method.value = resp.code;
				af.args.placeholder = argStr(resp.arg);
			}

		})
	});
});

function getMethod(deviceType,method){
	var a = deviceMethod[deviceType];
	if(!a){
		layer.alert("类型:"+deviceType+"未匹配",{icon : 2});
		return;
	}
	for(var i=0;i<a.length;i++){
		if(a[i].code == method)
			return a[i];
	}
}

function getDevice(deviceId){
	for(var i=0;i<devList.length;i++){
		var d = devList[i];
		if(d.id == deviceId)
			return d;
	}
	return {};
}

function argStr(arr) {
	var a = [];
	$(arr).each(function () {
		a.push(this.code);
	});
	return a.join(",");
}


function reload(pid) {
	this.pid = pid;
	tActionChain.load({"pid":pid,"plass":klass});
}

//[{name:参数名,code:arg1}]
function execAction(param){
	if(!param || param.length==0){
		common.jsonCont("action",{"klass":klass,"id":pid},function (){
            layer.alert("执行成功",{icon : 1});
            getRes();
        });
		return;
	}

	var f2 = $("[name='f2']").empty();
	$(param).each(function (){
		$('<div class="form-item"><div class="layui-form-item">' +
				'<label class="layui-form-label" title="'+this.code+'">'+(this.name || this.code)+'</label>' +
				'<div class="layui-input-block"> <input class="layui-input" name="'+this.code+'" placeholder="'+this.code+'"> ' +
				'</div> </div> </div>').appendTo(f2);
	});
	layer.open({
		type : 1,
		title : "执行参数",
		area : ["500px","auto"],
		btn : ["确定","取消"],
		content : $("#layParam"),
		yes : function (index){
			var p = common.formJSON("[name='f2']") || {};
			common.jsonCont("action",$.extend(p,{"klass":klass,"id":pid}),function (){
                layer.alert("执行成功",{icon : 1});
                getRes();
            });
		}
	});

}


function findArg(){
	var a = {}, b = {},arg = [];
	$(tActionChain._tbody.rows).each(function (){
		var data = this.data;
		if(data.isRemoved)
			return;
		var c = (data.args || "").split(",").filter(function(v){return v.indexOf("#") == 0});
		$(c).each(function (){
			var d = this.split(/[\s\[\]\-()*/+><=#|?:!&]/);
			$(d).each(function () {
				var e = this.split(".")[0];
				if(/^[a-z]/.test(e) && !b[e])
					a[e] = 1;
			});
		});
		b[data.variable] = 1;
	});

	for(var k in a){
		arg.push({code:k});
	}
	return arg;
}

function getRes(){
	var ids = [];
	$(tActionChain.json).each(function (){
		if(this.isRemoved === 0)
			ids.push(this.id);
	});
	common.jsonCont("getActionState",{id:ids.join(",")},function (json){
		$(".var-res").empty();
		for(var k in json){
			$(tActionChain.rows[k]).find(".var-res").text(json[k]);
		}
	});
}

</script>
</html>