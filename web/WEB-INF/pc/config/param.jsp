<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>

<!doctype html>
<html>
	<head>
	<%@include file="../common/page_head.jsp" %>
	<title>参数配置</title>

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
		  <div class="layui-form-item">
		    <div class="layui-input-block">
		      <button id="submit-btn" class="layui-btn" lay-submit lay-filter="formSubmit">确认</button>
		    </div>
		  </div>
	  </form>
	</div>

</div>

<div class="layui-form-item temp">
    <label class="layui-form-label">${v.name }</label>
    <div class="layui-input-block">

    </div>
</div>

</body>
<script type="text/javascript">
var type = param.type || 0;
var cid = param.cid;
var klass = param.klass;

var temp = $(".temp");
common.jsonCont("getAoReflect",{"klass" : klass,"deviceType":param.deviceType},function(json){
	$(json).each(function(){
		var data = this;
		if(this.type == "param"){
			var p = {};
			if(this.input){
				$(this.input.split(",")).each(function (){
					var a = this.split(":");
					p[a[0]] = a[1];
				});
			}
			var item = temp.clone().appendTo(".form-item").removeClass("temp");
			var label = this.name || this.code;
			item.find(".layui-form-label").text(label).attr("title",label);
			var block = item.find(".layui-input-block");
			var attr = 'class="layui-input" name="'+this.code+'"  placeholder="'+this.placeholder+'"';
			var input = null;

			if(this.klass == "boolean" || this.klass == "java.lang.Boolean"){
				block.append('<select lay-ignore '+attr+'><option value="false">否</option><option value="true">是</option></select>');
			}else if(this.select){
				input = $("<select lay-ignore "+attr+"></select>");
				block.append(input);
				$(this.select.split(",")).each(function () {
					var op = this.split(":");
					input.append("<option value='"+op[0]+"'>"+(op[1]||op[0])+"</option>");
				});
			}else if(p.type == "text"){
				input = $('<textarea '+attr+' style="height: 100px;"></textarea>').appendTo(block);
			}else{
				input = $('<input '+attr+'>').appendTo(block);
				if(this.klass == "int" || this.klass == "java.lang.Integer"){
					input.attr("type","number");
				}else if(this.klass == "float" || this.klass == "java.lang.Float"){
					input.attr("type","number").attr("step","0.01");
				}else if(this.klass.indexOf("java.io.File") != -1 || p.suffix){
					$(input).click(function (){
						top.common.openFile({"title":label,"input":this,"suffix":p.suffix},function(path){
							$(input).val(path);
						});
					});
				}
			}
		}
	});
	var t = {1:"device",2:"service",3:"cronParam",4:"commuParam"};
	common.ajax("${base}/json/"+t[type],{id:cid},function(json){
		$(fm).find(":input").each(function(){
			var v = json[this.name];
			this.value = v == undefined ? "" : (v.path || v);
		});
	});

});

layui.form.on('submit(formSubmit)', function(data){
	var json = [];
	for(var k in data.field){
		json.push({"type":type,"cid":cid,"code":k,"value":data.field[k]});
	}
	common.ajax("${base}/config/saveParams",json,null,{
		contentType : 'application/json'
	});
    return false;
});



</script>
</html>