<%@ page contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ page import="org.nutz.lang.util.NutMap" %>
<%@ page import="org.aiot.model.lang.SRes" %>

<!doctype html>
<html>
<head>
	<%@include file="../../common/page_head.jsp" %>
	<link rel="stylesheet" type="text/css" href="${res}/font/aiotfont/iconfont.css">
	<link rel="stylesheet" href="${res}/plugin/codemirror-5.65.18/lib/codemirror.css">
	<link rel="stylesheet" href="${res}/plugin/codemirror-5.65.18/theme/eclipse.css">
	<script src="${res}/plugin/codemirror-5.65.18/lib/codemirror.js"></script>
	<%
		SRes sRes = (SRes) request.getAttribute("SRes");
		String suffix = sRes.getSuffix();
		NutMap m = new NutMap();
		m.setv("js","javascript").setv("json","javascript");
		m.setv("ini","properties").setv("conf","properties").setv("properties","properties");
		m.setv("java","clike");
		m.setv("py","python");
		m.setv("sh","shell");
		m.setv("css","css");
		m.setv("yaml","yaml");
		request.setAttribute("mode",m.getOrDefault(suffix,"xml"));
	%>
	<script src="${res}/plugin/codemirror-5.65.18/mode/${mode}/${mode}.js"></script>
	<title>${SRes.name}</title>
	<meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=0">

	<style type="text/css">
		html,body{
			height: 100%;
		}
		.CodeMirror.cm-s-default{
			height: 100%;
			font-family: "Courier", monospace;
		}
		.CodeMirror pre.CodeMirror-line, .CodeMirror pre.CodeMirror-line-like{
			-margin-left: 5px;
		}
		.aiot-icon-save{
			font-size: 18px;
			position: absolute;
			right: 20px;
			top:10px;
			z-index: 10;
		}
		.aiot-icon-save:hover{
			cursor: pointer;
			color: #0a63b2;
		}
		textarea{
			height: calc(100% - 4px);
			width: 100%;
			border: none;
			box-sizing: border-box;
			padding: 10px;
		}
	</style>
</head>
<body>
<i class="aiot-icon aiot-icon-save" onclick="save()"></i>
<textarea id="code">${SRes.getContent()}</textarea>
</body>
<script type="text/javascript">
	var type = "${SRes.suffix}";
	function save(){
		common.jsonCont("saveRes",{"url":"${SRes.url}","content":editor.getValue()});
	}
	var options = {
		//mode :"javascript",
		//theme: "default",
		lineNumbers: true,
		fullScreen : true,
		extraKeys: { // 配置额外快捷键
			"Ctrl-S": function() {
				save();
			}
		}
	}
	if(type == "java")
		options.mode = "text/x-java";
	var editor = CodeMirror.fromTextArea(document.getElementById("code"), options);

</script>
</html>