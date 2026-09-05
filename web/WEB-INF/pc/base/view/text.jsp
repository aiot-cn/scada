<%@ page contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>

<!doctype html>
<html>
<head>
	<%@include file="../../common/page_head.jsp" %>
	<link rel="stylesheet" type="text/css" href="${res}/font/aiotfont/iconfont.css">
	<title>${SRes.name}</title>
	<meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=0">

	<style type="text/css">
		html,body{
			height: 100%;
		}
		.aiot-icon-save{
			font-size: 18px;
			position: absolute;
			right: 20px;
			top:10px;
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
<textarea>${SRes.getContent()}</textarea>
</body>
<script type="text/javascript">
	function save(){
		common.jsonCont("saveRes",{"url":"${SRes.url}","content":$("textarea").val()});
	}
	$("textarea").keydown(function (e){
		if(e.keyCode == 9){
			e.preventDefault();
			this.setRangeText("\t");
			this.selectionStart += 1;
		}else if(e.keyCode == 83 && e.ctrlKey){
			e.preventDefault();
			save();
		}
	});

</script>
</html>