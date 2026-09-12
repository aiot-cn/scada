<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<!doctype html>
<html>
	<head>
	<%@include file="../common/page_head.jsp" %>
	<link rel="stylesheet" href="${res}/plugin/codemirror-5.65.18/lib/codemirror.css">
	<link rel="stylesheet" href="${res}/plugin/codemirror-5.65.18/theme/eclipse.css">
	<script src="${res}/plugin/codemirror-5.65.18/lib/codemirror.js"></script>
	<script src="${res}/plugin/codemirror-5.65.18/mode/properties/properties.js"></script>
	<script src="${res}/plugin/codemirror-5.65.18/mode/css/css.js"></script>

	<title>系统参数</title>
		
<style type="text/css">
	html,body{
		height: 100%;
	}
	textarea.layui-input{
		height: 200px;
	}
	.text-code label{

	}
	.text-code .layui-input-block{
		margin-left: 0;
	}
	.CodeMirror{
		border: 1px solid #ddd;
		font-family: "Courier New","Courier", monospace;
		line-height: 20px;
	}
</style>
</head>
<body>
<div class="layui-fluid sty-auto-h">

	<div class="layui-row layui-col-space15">

		<div class="layui-col-md6">
			<div class="layui-card">
				<div class="layui-card-header">
					信息
				</div>
				<div class="layui-card-body">
					<form name="f2" class="layui-form layui-form-pane">
						<%--<div class="layui-form-item">
							<label class="layui-form-label">序列号</label>
							<div class="layui-input-block">
								<div class="layui-input" style="line-height: 38px;font-weight: bold;color: green;cursor: not-allowed;">${serialNo}</div>
							</div>
						</div>--%>
						<div class="layui-form-item">
							<label class="layui-form-label">系统启动</label>
							<div class="layui-input-block">
								<div class="layui-input" style="line-height: 38px;font-weight: bold;color: green;cursor: not-allowed;">${resCache}</div>
							</div>
						</div>
					<div class="i2"></div>
					</form>
				</div>
			</div>
		</div>

		<div class="layui-col-md6">
			<div class="layui-card">
				<div class="layui-card-header">
					参数
					<button onclick="clearCatch();" class="layui-btn layui-btn-primary layui-btn-xs layui-border-red">
						<i class="layui-icon layui-icon-fonts-clear "></i> 清理缓存
					</button>
				</div>
				<div class="layui-card-body">
					<form name="f1" class="layui-form layui-form-pane">

						<div class="i1"></div>
						<div style="text-align: center;">
							<button type="button" class="layui-btn" onclick="saveConfig()"><i class="layui-icon layui-icon-ok"></i> 保存</button>
						</div>
					</form>
				</div>
			</div>
		</div>

</div>
</div>

</body>
<script type="text/javascript">

common.ajax("${base}/json/getEnum",{type:"config"},function(json){
	$(json).each(function(){
		var $d = buildFormItem(this).appendTo(".i1");
		var i = this.type.indexOf("text-");
		if(i == 0){
			$d.addClass("text-code").find(".layui-form-label").removeClass("layui-form-label");
			var textarea = $d.find("textarea")[0];
			textarea.editor = CodeMirror.fromTextArea(textarea, {
				mode:this.type.slice(5),
				lineNumbers: true
			});
		}
	});
})

common.ajax("${base}/json/getEnum",{type:"varRuntime"},function(json){
	$(json).each(function(){
		if(typeof(this.value) != "object")
			$('.i2').append(buildFormItem(this));
	});
    $("[name='dbUrl']").click(function () {
        common.openFile({"suffix":"db"},function(fileName){
			layer.load(2, {shade: [0.7,'#000'] });
           common.ajax("${base}/config/setDataSource",{"fileName":fileName},function (){
			   parent.location.href = "${base}";
		   });
        });
    });

})

function buildFormItem(data){
    var input  = common.buildInput(data);
    var node =  '<div class="layui-form-item">'+
        '<label class="layui-form-label">'+data.name+'</label>' +
        '<div class="layui-input-block">'+ input +
        '</div></div>';
    var $n = $(node);
    $n.find(":input").val(data.value).addClass("layui-input");
    return $n;
}

function saveConfig(){
	var json = [];
	$('[name="f1"] :input').each(function(){
		json.push({"type":0,"code":this.name,"value":this.editor ? this.editor.getValue() : this.value});
	});
	common.ajax("${base}/config/saveParams",json,null,{
		contentType : 'application/json'
	});
}

function clearCatch(){

}

</script>
</html>