<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<meta charset="UTF-8" />
<meta name="robots" content="noindex,nofollow">		
<meta name="renderer" content="webkit|ie-comp|ie-stand">
<meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
<meta name="author" content="<%=application.getRealPath(request.getRequestURI())%>">

<link rel="icon" href="${res}/images/favicon.ico" type="image/x-icon">

<%-- <script src="${res}/layui-src/layui.js"></script> --%>
<script src="${res}/layui/layui.js"></script>
<link href="${res}/layui/css/layui.css" rel="stylesheet" > 

<script src="${res}/js/common.js?v=${resCache}"></script>
<script src="${res}/plugin/itable/iTables.js?v=${resCache}"></script>
<link href="${res}/plugin/itable/itable.css?v=${resCache}" rel="stylesheet" >
<link href="${res}/plugin/itable/icon/iconfont.css?v=${resCache}" rel="stylesheet" >
<link href="${res}/css/style.css?v=${resCache}" rel="stylesheet" > 
<style type="text/css">
    ${config.textCss}
</style>

<script type="text/javascript">
var base = '${base}';
var siteId = '${site.id}';
var userId = "${user.id}";
var login_user = "${user.loginName}";
var resCache = '${resCache}';
var $ = layui.$;
//layui.use('layer');
var param = common.urlParams();
var roleAction = JSON.parse('${roleJson}' || '{}');
layui.config({
	version: false,
	debug: false,
	base: '${res}/layui/plugins/'
});
</script>
