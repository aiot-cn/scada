<%@ page import="org.aiot.util.HttpUtil" %>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%
	Object obj = request.getAttribute("obj");
	if(HttpUtil.isAjax(request)){
		response.setContentType("text/json");
		com.alibaba.fastjson.JSONObject json = new com.alibaba.fastjson.JSONObject();
		json.put("success", false);
		//json.put("obj", obj);
		json.put("message", ((Exception)obj).getMessage());
		out.print(json);
		out.flush();
	}else{
%>		
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>出错啦！</title>
<style type="text/css">
* { padding:0; margin:0;}
li { list-style:none;}
img { border:none;}
.clear { zoom:1;}
.clear:after { content:'\20'; clear:both; display:block;}

.error-page { width:940px; margin:0 auto; padding-top:110px;}
.error-page-left { width:440px; float:left; background:url(${res}/images/error.gif) no-repeat 22px 0; height:478px;}
.error-page-right { width:500px; float:left;}
.error-page-right h3 { line-height:114px; font-size:22px; color:#333; font-weight:600; padding-top:10px;}
.error-page-title { line-height:24px; font-size:14px; color:#333;}
.error-page-title a { color:#0066cc; text-decoration:underline;}
.error-page-txt { line-height:24px; padding-left:40px; font-size:14px; color:#333; padding-bottom:9px;}
.error-page-txt a { color:#0066cc; text-decoration:underline;}
</style>
</head>

<body style="background-color: #fff">
<div class="error-page">
	<div class="error-page-left">&nbsp;</div>
    <div class="error-page-right">
    	<h3>抱歉！可能由于以下原因执行错误！</h3>
    	<p class="error-page-title">>>${obj}</p>
    </div>
</div>
</body>
</html>
<%}%>