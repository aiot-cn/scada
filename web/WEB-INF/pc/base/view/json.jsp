<%@ page contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>

<!doctype html>
<html>
<head>
	<%@include file="../../common/page_head.jsp" %>
	<link rel="stylesheet" type="text/css" href="${res}/font-station/iconfont.css">
	<link rel="stylesheet" href="${res}/plugin/codemirror-5.65.18/lib/codemirror.css">
	<link rel="stylesheet" href="${res}/plugin/codemirror-5.65.18/theme/eclipse.css">
	<script src="${res}/plugin/codemirror-5.65.18/lib/codemirror.js"></script>

	<script src="${res}/plugin/codemirror-5.65.18/mode/javascript/javascript.js"></script>
	<title>JSON</title>
	<meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=0">

	<style type="text/css">
		html,body{
			height: 100%;
		}
		.CodeMirror.cm-s-default{
			height: 100%;
			font-family: "Courier", monospace;
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

<textarea id="code">
${json}
</textarea>
</body>
<script type="text/javascript">

	var editor = CodeMirror.fromTextArea(document.getElementById("code"), {
		//mode :"javascript",
		//theme: "default",
		lineNumbers: true,
		fullScreen : true
	});

</script>
</html>