<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>

<!doctype html>
<html>
<head>
	<%@include file="../common/page_head.jsp" %>

	<title>脚本代码</title>
	<script src="${res}/plugin/code-prettify/prettify.js"></script>
	<link href="${res}/plugin/code-prettify/prettify.css" rel="stylesheet" >
	<style type="text/css">
		html,body{
			height: 100%
		}
		.scroll-wrapper {
			width: 100%;
			height: 100%;
			padding: 0;
			margin: 0;
		}

		.scriptText{
			height: 100%;width: 100%;box-sizing: border-box;font-family:"Courier";
			color: #046F65;
			overflow: auto;
			border: none !important;
			line-height: 18px;
			tab-size: 4;
		}
		pre.scriptText .com{
			font-size: 12px;
		}
		textarea.scriptText{
			height:calc(100% - 30px);
			padding-left: 20px;
		}
		.script-arg{
			width: 20%;
			float: left;
			box-sizing: border-box;
			padding: 2px 5px;
		}
		#spanArgs{
			color: #14a951;
			font-size: 12px;
		}
		ol.linenums{
			padding-left: 45px;
			color: #ccc;
		}
		pre.prettyprint ol.linenums li {
			list-style-type: decimal-leading-zero;
		}
	</style>
</head>
<body>
<div class="layui-fluid sty-auto-h">

	<div class="layui-row layui-col-space15">
		<div class="layui-col-md3">
			<div class="layui-card">
				<div class="layui-card-header">
					<span class="title">脚本</span>
					<select id="scriptType" onchange="tScript.load({'type':this.value})" style="width: calc(100% - 45px);"></select>
				</div>
				<div class="layui-card-body">
					<div class="layui-row layui-col-space10">

						<table id="tScript" class="layui-table">
							<thead>
							<tr>
								<th data-field="name">名称</th>
								<th data-field="code">CODE</th>
								<th data-type="edit" data-render="renderEdit" data-class="tac" width="40">操作</th>
							</tr>
							</thead>
						</table>

					</div>
				</div>
			</div>
		</div>
		<div class="layui-col-md9">
			<div class="layui-card">
				<div class="layui-card-header">
					<span class="title">代码</span>
					<a style="color: red;font-size: 12px" onclick='getScriptBase()'>常量</a>
					参数 - <span id="spanArgs"></span>
				</div>
				<div class="layui-card-body">
					<div class="layui-row layui-col-space10">
						<pre class="scriptText prettyprint linenums"></pre>
						<iframe class="scroll-wrapper" src="${base}/base/editor/script?code=initCall" frameborder="0" name="fm" style="display: none"></iframe>
					</div>
				</div>
			</div>
		</div>
	</div>

</div>
<div class="lay-con" style="height: calc(100% - 4px);margin: 0;">
	<textarea class="scriptText" spellcheck="false"></textarea>
	<div>
		<input class="script-arg" placeholder="arg0">
		<input class="script-arg" placeholder="arg1">
		<input class="script-arg" placeholder="arg2">
		<input class="script-arg" placeholder="arg3">
		<input class="script-arg" placeholder="arg4">
	</div>
</div>
<div>
	<form data-for="tScript">
		<input type="text" class="layui-input" name="name">
		<input type="text" class="layui-input" name="code">
	</form>
</div>
</body>
<script type="text/javascript">

	var scriptBase;
	var scriptTypeArg = {};
	var canEditor = supportEditor();
	getScriptBase();
	common.jsonEnum("ScriptType",function(list){
		common.renderSelect("#scriptType",list,{value:"code",callOption:function (data){
				$(this).text(data.name+"("+data.args+")");
				scriptTypeArg[data.code] = data.args;
			}
		});
	});

	common.ajaxStop(function(){
		tScript.load({"type":$("#scriptType").val()});
	});
	var tScript = new iTables("#tScript",{},{
		baseOption : common.iTableModel("sysScript"),
		loadOnInit : false,
		callForm:function(params){
			params.type = $("#scriptType").val();
		},
		onSelect : function(tr,data){
			$("#spanArgs").text(data.args || "无");
			showEditor(canEditor);
			if(canEditor){
				fm.loadScript(data);
			}else{
				loadScript(data.function);
			}

		},
		render : {
			"name" : function (td,data){
				if(scriptTypeArg[data.type])
					return data.name;
				var a = $("<a>"+data.name+"</a>").appendTo(td);
				a.click(function (){
					common.editArgDefine(data.args,function (value){
						tScript.saveData({"id":data.id,"args":value});
					})
				});
			}
		},
		renderEdit : function (td,data) {
			if(canEditor)
				return ;
			var i = $('<i class="layui-icon layui-icon-release"></i>').click(function () {
				tScript.select(data);
				layer.open({
					type: 1,
					title : data.name + " - "+(data.args || ""),
					content: $(".lay-con"),
					area : ["90%","90%"],
					btn : ["保存","运行"],
					yes: function(index, layero){
						execScript(); //保存可能失败，不能关闭
					},
					btn2: function(index, layero){
						execScript(true);
						return false;
					}
				});
			});
			return i;
		}
	});

	function execScript(run){
		var args = [];
		$(".script-arg").each(function () {
			args.push(this.value);
		});
		var param = {
			id : tScript._data.id,
			text : $("textarea.scriptText").val(),
			run : run,
			args : args.join(",")
		};
		common.jsonCont("execScript",param,function (json){
			tScript._data.code = param.text;
			return false;
		});
	}

	function getScriptBase() {
		showEditor(false);

		if(scriptBase){
			loadScript(scriptBase);
			return;
		}
		common.jsonCont("getScriptText",{},function (json) {
			scriptBase = json.data;
			loadScript(scriptBase);
		})
	}

	function showEditor(show) {
		if(show){
			$(".scroll-wrapper").show();
			$(".prettyprint").hide();
		}else{
			$(".scroll-wrapper").hide();
			$(".prettyprint").show();
		}
	}

	function loadScript(text) {
		$(".prettyprinted").removeClass("prettyprinted");
		$("pre.scriptText").text(text || "");
		$("textarea.scriptText").val(text || "");
		prettyPrint();
	}

	function supportEditor(){
		return !!Array.from;
	}

	$(".scriptText").keydown(function (e){
		if(e.keyCode == 9){
			e.preventDefault();
			this.setRangeText("\t");
			this.selectionStart += 1;
		}
	});


</script>
</html>