<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>

<!doctype html>
<html>
	<head>
	<%@include file="../common/page_head.jsp" %>
	<title>方法参数</title>
		
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

	<div class="layui-row layui-col-space15">
	<form name="fm" class="layui-form layui-form-pane form-label-wider" action="">
		  <div class="form-item"></div>
	</form>
	</div>

</div>

<div class="layui-form-item temp">
    <label class="layui-form-label"></label>
    <div class="layui-input-block"></div>
</div>

</body>
<script type="text/javascript">

var deviceType = param.deviceType ;
var method = decodeURIComponent(param.method);
var devId = param.d || null;
var args = decodeURIComponent(param.args || "").split(",");
var argMap = {}; //参数值

var temp = $(".temp");
var argList = [];
var selectMap = {};

common.ajax("${base}/json/getDevTypeMethod",{"deviceType" : deviceType,"method":method,"devId":devId},function(json){
	argList = json.arg;
});

common.ajaxStop(function (){
	$(argList).each(function (index) {
		if(this.type == "javax.servlet.http.HttpServletRequest" || this.type == "javax.servlet.http.HttpServletResponse")
			return true;
		var arg = this;
		var argType = arg.type;//参数类型
		var inputAttr = {};
		if(this.input){
			$(this.input.split(",")).each(function (){
				var a = this.split(":");
				inputAttr[a[0]] = a[1];
			});
		}
		var item = temp.clone().appendTo(".form-item").removeClass("temp");
		item.find(".layui-form-label").text(arg.name || arg.code).attr("title",arg.code);
		var block = item.find(".layui-input-block");
		var attr = 'class="layui-input" name="'+arg.code+'"  placeholder="'+(arg.placeholder || '')+'"';
		var dictSelect = selectMap[deviceType+"."+method+"."+arg.code];
		var input;
		if(argType == "boolean" || argType == "java.lang.Boolean"){
			input = $('<select lay-ignore '+attr+'><option value="false">否</option><option value="true">是</option></select>').appendTo(block);
		}else if(arg.select){
			input = $("<select lay-ignore "+attr+"></select>");
			block.append(input);
			$(arg.select.split(",")).each(function () {
				var op = this.split(":");
				input.append("<option value='"+op[0]+"'>"+op[1]+"</option>");
			});
		}else if(dictSelect){
			input = $("<select lay-ignore "+attr+"></select>");
			block.append(input);
			$(dictSelect).each(function () {
				input.append("<option value='"+this.v2+"'>"+this.name+"</option>");
			});
		}else{
			if(inputAttr.type == "text"){
				input = $('<textarea '+attr+' style="height: 100px;"></textarea>').appendTo(block);
			}else{
				input = $('<input '+attr+'>').appendTo(block);
			}

			if (argType.indexOf('CommonAction')>-1){
				input.val("上下文").attr("disabled","true");
			}
			//动作链参数可能有#号
			if(param.args == undefined){
				if(argType == "int" || argType == "java.lang.Integer" || argType == "java.lang.Long"){
					input.attr("type","number");
				}else if(argType == "java.util.Date"){
					input.attr("type","datetime-local");
				}
			}
		}

		$(input).click(function(e){
			if(argType == "java.io.File" || (e.ctrlKey && argType == "java.lang.Object")){
				common.topWin().common.openFile({},function(path){
					$(input).val(path);
				});
			}
		});


		if(args[index] != undefined && args[index] !== ""){
			input.val(args[index]);
		}else if(argMap[this.code] != undefined){
			input.val(argMap[this.code]);
		}

	});
});



</script>
</html>