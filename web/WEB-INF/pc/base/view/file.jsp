<%@ page contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ page import="org.aiot.model.table.TFile" %>
<%@ page import="org.nutz.lang.util.NutMap" %>
<%@ page import="org.nutz.lang.Times" %>
<%@ page import="org.aiot.main.Constants" %>
<%@ page import="java.io.File" %>
<%@ page import="org.aiot.model.lang.SRes" %>
<%@ page import="org.aiot.util.FileUtil" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%
	SRes sRes = (SRes) request.getAttribute("SRes");
	NutMap nm = new NutMap();
	File file = FileUtil.toFile("",sRes.getPathName());

	if(file.isFile()){
		TFile tFile = FileUtil.fileInfo(file);
		request.setAttribute("obj",tFile);

		nm.put("time", Times.sDT(tFile.getCreateDate()));
		if(tFile.getSize() > 1024)
			nm.put("size",String.format("%.1f Mb",tFile.getSize()/1024));
		else
			nm.put("size",String.format("%.1f kb",tFile.getSize()));
		String title = tFile.getPathName();
		if(title.contains("/weights/best.pt"))
			title = title.replace("/ai/img","").replace("/train","").replace("/weights/best","");
		nm.put("showName",title.substring(1));
		nm.put("title", title.substring(Math.max(0,title.length()-25)));
		nm.put("pathName",tFile.getPathName());
		nm.put("ico",Constants.prop.get("ico."+tFile.getType(),"normal")+".png");
		nm.put("type",tFile.getType());
	}else{
		nm.put("size","<span style='color:red'>文件不存在</span>");
	}

	request.setAttribute("info",nm);

%>
<!doctype html>
<html>
	<head>
		<title>${info.showName}</title>
		<link rel="icon" href="${res}/images/favicon.ico" type="image/x-icon">
		<meta name="description" content="${obj.description} ${info.size} ${info.time}">
		<meta property="og:image" content="${res}/images/ico/${info.ico}">
		<meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">

		<script src="${res}/layui/layui.all.js"></script>
		<script src="${res}/js/common.js"></script>

		<style type="text/css">
			html,body {
				height: 100%;
				margin: 0;
				font-size:  14px;
			}

			.container {
				height: 100%;
				background-image: linear-gradient(to right,#fbc2eb,#a6c1ee);
			}

			.login-wrapper {
				background-color: #fff;
				width: 500px;
				min-height: 400px;
				padding: 20px 50px;
				position: relative;
				left: 50%;
				border-radius: 15px;
				top:50%;
				transform: translate(-50%,-50%);
			}

			.t1{
				line-height: 22px;
				width: 100%;
				table-layout: fixed;
			}

			.login-wrapper .header {
				font-weight: bold;
				text-align: center;
				margin-bottom: 40px;
			}
			.t1 tr td:nth-child(1){
				padding-right: 10px;
			}
			.t1 tr td:nth-child(2){
				color: gray;
			}

			#d-onnx{
				display: none;
				margin-top: 10px;
			}

			@media screen and (max-width: 600px){
				.login-wrapper{
					width: calc(80% - 20px);
					padding: 20px;
				}
			}

		</style>
	</head>

	<body>
		<div class="container">
			<div class="login-wrapper">
				<div class="header">
					<div style="padding: 20px">
						<img src="${res}/images/ico/${info.ico}" style="height: 60px;">
					</div>
					<div>
						<a href="${base}/file/download/${info.pathName}">${info.showName}</a>
					</div>
					<div id="d-onnx">
						<img src="${res}/images/ico/onnx.png" style="height: 20px;vertical-align: middle;">
						<a style="font-weight: normal">onnx</a>
					</div>
				</div>
				<div class="form-wrapper">
					<table class="t1">
						<tr>
							<td width="30">大小</td>
							<td>${info.size}</td>
						</tr>
						<tr>
							<td>时间</td>
							<td>${info.time}</td>
						</tr>
						<tr>
							<td>MD5</td>
							<td style="text-transform: uppercase;overflow: hidden;">${obj.md5}</td>
						</tr>
						<tr>
							<td style="vertical-align: top;">描述</td>
							<td style="word-break: break-all;white-space: pre-line;">${obj.description}</td>
						</tr>
					</table>
				</div>

			</div>
		</div>
	</body>

	<script>
		var $ = layui.$;
		var base = '${base}';
		var type = '${info.type}';
		if(type == "pt"){
			var dOnnx = $("#d-onnx").show();
			common.jsonDevExec("ImageDevice","ptToOnnx",{pt:"${info.pathName}"},function (json){
				if(json.data)
					dOnnx.find("a").attr("href",base + "/json/file?name="+json.data.path);
			});
		}
	</script>
</html>