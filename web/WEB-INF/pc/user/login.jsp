<%@ page import="org.aiot.util.HttpUtil" %>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%
	HttpUtil httpUtil = new HttpUtil();
	if(httpUtil.isAjax(request)){
		Object obj = request.getAttribute("obj");
		response.setContentType("text/json");
		com.alibaba.fastjson.JSONObject json = new com.alibaba.fastjson.JSONObject();
		json.put("success", false);
		json.put("code", 1);
		json.put("message", "回话超时，请重新登录");
		//out.print(json);
		//out.flush();
	}else{
%>	
<!DOCTYPE html>

<html lang="cn">
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
  <meta charset="utf-8">
  <title>系统登录</title>
  <meta name="author" content="taojin">
  <meta name="viewport" content="width=device-width, initial-scale=1.0, minimum-scale=1.0, maximum-scale=1.0, user-scalable=no">
  <%@include file="../common/page_head.jsp" %>
  <style type="text/css">
  
  	html,body{
		width:100%;
		height:100%;
	}

	html{
		background:#020c1c;
		background:linear-gradient(160deg,#031633 0%,#052a55 45%,#021024 100%);
	}

	canvas{
	  display:block;
	  vertical-align:bottom;
	}

#particles-js{
	width: 100%;
	height: 100%;
	position: relative;
	background-position: 50% 50%;
	background-size: cover;
	background-repeat: no-repeat;
	margin-left: auto;
	margin-right: auto;
}

/* 科技网格线 + 节点光点 */
#particles-js::before{
	content:"";
	position:absolute;
	top:0;
	left:0;
	width:100%;
	height:100%;
	z-index:0;
	background-image:
		radial-gradient(circle at 0 0,rgba(0,195,255,.35) 1.5px,transparent 2px),
		linear-gradient(rgba(0,195,255,.08) 1px,transparent 1px),
		linear-gradient(90deg,rgba(0,195,255,.08) 1px,transparent 1px);
	background-size:45px 45px;
	-webkit-mask-image:radial-gradient(ellipse at 50% 45%,#000 20%,transparent 75%);
	mask-image:radial-gradient(ellipse at 50% 45%,#000 20%,transparent 75%);
}

/* 漂浮光晕 */
body::before,body::after{
	content:"";
	position:fixed;
	border-radius:50%;
	z-index:-1;
	pointer-events:none;
}
body::before{
	width:520px;
	height:520px;
	top:-160px;
	left:-140px;
	background:radial-gradient(circle,rgba(0,140,255,.28) 0%,transparent 70%);
	filter:blur(30px);
	animation:iot-float 13s ease-in-out infinite alternate;
}
body::after{
	width:640px;
	height:640px;
	right:-200px;
	bottom:-220px;
	background:radial-gradient(circle,rgba(81,0,255,.27) 0%,transparent 70%);
	filter:blur(40px);
	animation:iot-float 16s ease-in-out infinite alternate-reverse;
}

@keyframes iot-float{
	0%{transform:translate(0,0);}
	100%{transform:translate(70px,45px);}
}


.sk-rotating-plane {
	display: none;
    width: 80px;
    height: 80px;
    margin: auto;
    background-color: white;
    -webkit-animation: sk-rotating-plane 1.2s infinite ease-in-out;
    animation: sk-rotating-plane 1.2s infinite ease-in-out;
    z-index: 1;
    position: absolute;
    top: 50%;
    left: 50%;
    margin-left: -40px;
    margin-top: -80px;
}
.sk-rotating-plane.active{display: block;}

@keyframes sk-rotating-plane{
	0% {
	    -webkit-transform: perspective(120px) rotateX(0deg) rotateY(0deg);
	    transform: perspective(120px) rotateX(0deg) rotateY(0deg);
	}
	50% {
	    -webkit-transform: perspective(120px) rotateX(-180.1deg) rotateY(0deg);
	    transform: perspective(120px) rotateX(-180.1deg) rotateY(0deg);
	}
	100% {
	    -webkit-transform: perspective(120px) rotateX(-180deg) rotateY(-179.9deg);
	    transform: perspective(120px) rotateX(-180deg) rotateY(-179.9deg);
	}
}

@keyframes login-small{
	0%{
		transform: scale(1);-moz-transform: scale(1);	/* Firefox 4 */-webkit-transform: scale(1);	/* Safari 和 Chrome */-o-transform: scale(1);	/* Opera */-ms-transform:scale(1); 	/* IE 9 */
	}
	100%{
		transform: scale(0.2);-moz-transform: scale(0.1);	/* Firefox 4 */-webkit-transform: scale(0.2);	/* Safari 和 Chrome */-o-transform: scale(0.1);	/* Opera */-ms-transform:scale(0.1); 	/* IE 9 */
	}
}

.login{z-index: 2;position:absolute;width: 350px;border-radius: 5px;height: 500px;background: white;box-shadow: 0px 0px 5px #333333, 0 0 40px rgba(0,195,255,.35);top: 50%;left: 50%;margin-top: -250px;margin-left: -175px;transition: all 1s;-moz-transition: all 1s;	/* Firefox 4 */-webkit-transition: all 1s;	/* Safari 和 Chrome */-o-transition: all 1s;	/* Opera */}
.login-top{font-size: 24px;margin-top: 50px;padding-left: 40px;box-sizing: border-box;color: #333333;margin-bottom: 50px;}
.login-center{width: 100%;box-sizing: border-box;padding: 0 40px;margin-bottom: 30px;}
.login-center-img{width: 20px;height: 20px;float: left;margin-top: 5px;}
.login-center-img>img{width: 100%;}
.login-center-input{float: left;width: 230px;margin-left: 15px;height: 30px;position: relative;}
.login-center-input input{z-index: 2;transition: all 0.5s;padding-left: 10px;color: #333333;width: 100%;height: 30px;border: 0;border-bottom: 1px solid #cccccc;border-top: 1px solid #ffffff;border-left: 1px solid #ffffff;border-right: 1px solid #ffffff;box-sizing: border-box;outline: none;position: relative;}
.login-center-input input:focus{border: 1px solid dodgerblue;}
.login-center-input-text{background: white;padding: 0 5px;position: absolute;z-index: 0;opacity: 0;height: 20px;top: 50%;margin-top: -10px;font-size: 14px;left: 5px;color: dodgerblue;line-height: 20px;transition: all 0.5s;-moz-transition: all 0.5s;	/* Firefox 4 */-webkit-transition: all 0.5s;	/* Safari 和 Chrome */-o-transition: all 0.5s;	/* Opera */}
.login-center-input input:focus~.login-center-input-text{top: 0;z-index: 3;opacity: 1;margin-top: -15px;}
.login.active{-webkit-animation: login-small 0.8s ; animation: login-small 0.8s ;animation-fill-mode:forwards;-webkit-animation-fill-mode:forwards}
.login-button{cursor: pointer;width: 250px;text-align: center;height: 40px;line-height: 40px;background-color: dodgerblue;border-radius: 5px;margin: 0 auto;margin-top: 50px;color: white;}

*{box-sizing:content-box;}
a:hover, a:focus{text-decoration:none;}
body,div,dl,dt,dd,ul,ol,li,h1,h2,h3,h4,h5,h6,pre,form,fieldset,input,textarea,p,blockquote,th,td{margin:0;padding:0;}
table{border-collapse:collapse;border-spacing:0;}
body{-webkit-text-size-adjust:none;}
fieldset,img{border:0;}
img{ vertical-align: top; max-width: 100%; }
address,caption,cite,code,dfn,em,th,var{font-style:normal;font-weight:normal;}
ol,ul{list-style:none;}
caption,th{text-align:left;}
h1,h2,h3,h4,h5,h6{font-size:100%;font-weight:normal;}
q:before,q:after{content:'';}
abbr,acronym {border:0;}
.clearfix:after{visibility:hidden;display: block;font-size:0;content:" ";clear:both;height:0;}
* html .clearfix{ zoom: 1; } /* IE6 */
*:first-child+html .clearfix { zoom: 1; } /* IE7 */
.cli{ clear:both; font-size:0; height:0; overflow:hidden;display:block;}
.lclear{clear:left;font-size:0;height:0;overflow:hidden;}	
.fl{float:left;}
.fr{float:right;}

body{font-size:12px;font-family:'微软雅黑',"宋体","Arial Narrow",Helvetica,sans-serif;color:#000;line-height:1.2;text-align:left;}
a{color:#333;text-decoration:none;}
.layui-icon{
	font-size: 24px;
	color: #727272;
}
.logo{
	text-align: center;
    margin-top: 40px;
}

</style>
</head>
<body>

<form name='loginForm' id="particles-js">
<input type="hidden" name="url" value="${obj.url }">
		<div class="login">
			<div class="logo">
				<img src="${res}/images/logo.png" style="height: 48px">
			</div>
			<div class="login-top">
				登录
			</div>
			<div class="login-center clearfix">
				<div class="login-center-img"><i class="layui-icon layui-icon-username"></i></div>
				<div class="login-center-input">
					<input type="text" name="loginName" value="${cookie.loginName.value}" placeholder="请输入您的用户名" onfocus="this.placeholder=''" onblur="this.placeholder='请输入您的用户名'">
					<div class="login-center-input-text">用户名</div>
				</div>
			</div>
			<div class="login-center clearfix">
				<div class="login-center-img"><i class="layui-icon layui-icon-password"></i></div>
				<div class="login-center-input">
					<input type="password" name="password" value="${cookie.password.value}" placeholder="请输入您的密码" onfocus="this.placeholder=''" onblur="this.placeholder='请输入您的密码'">
					<div class="login-center-input-text">密码</div>
				</div>
			</div>
			 <div class="login-center clearfix">
	            <input type="checkbox" name="remember" checked="checked" style="margin-left: 5px;vertical-align: middle;">
	            <span>记住密码</span>
	            <span class="tg fr" style="cursor:pointer;color: #77778b;" id="forget">忘记密码？</span>	           
	          </div>
			<div class="login-button">
				登陆
			</div>
		</div>
		<div class="sk-rotating-plane"></div>
<canvas class="particles-js-canvas-el" width="1920" height="935" style="width: 100%; height: 100%;"></canvas>
</form>


<script type="text/javascript">
$(function(){
	document.onkeydown = function(e){ 
	    var ev = document.all ? window.event : e;
	    if(ev.keyCode==13) {
	    	$(".login-button").click();
	    }
	}

/* 	if(loginForm.loginName.value){
		loginForm.onsubmit();
	} */	
});

loginForm.onsubmit = function(e){
	if(e){
		e.preventDefault();
	}
	
	common.ajax("${base}/user/doLogin",common.formJSON("#particles-js"),function(json){
		if(json.success){
			if(location.pathname.indexOf("/user/login") > -1)
				location.href = "${base}";
			else
				location.reload();
		}else{
			layer.alert(json.message,{icon:2});
			loginForm.password.value = "";
		}
		$(".login,.sk-rotating-plane").removeClass("active");
		$(".login").show();
	},{callError:"true"});
};

	$(".login-button").click(function(){
		layer.closeAll();
		if(!loginForm.loginName.value){
			loginForm.loginName.focus();
			return;
		}
		if(!loginForm.password.value){
			loginForm.password.focus();
			return;
		}
		
		$(".login").addClass("active");
		setTimeout(function(){
			$(".sk-rotating-plane").addClass('active');
			$(".login").hide();
			loginForm.onsubmit();
		},800)
		
	});

</script>

</body>
</html>

<%}%>
