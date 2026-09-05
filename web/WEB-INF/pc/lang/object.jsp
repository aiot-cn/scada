<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!doctype html>
<html>
	<head>
		<title>对象</title>
		<c:import url="../common/page_head.jsp"></c:import>
	
		<style type="text/css">
			.layui-tab-item{
				position: absolute;
			    right: 0;
			    left: 0;
			    bottom: 0;
			    top: 40px;
			}
			.layui-tab-item iframe{
				width: 100%;
				height: calc(100% - 4px);;
			}
		</style>
	</head>
	
	<body>

		<div class="layui-tab layui-tab-brief" style="margin-top: 0">
		  <ul class="layui-tab-title">
		    <li class="layui-this">参数</li>
		    <li>属性</li>
		    <li>方法</li>
			  <c:if test="${param.type == 1}">
				  <li>数据</li>
			  </c:if>
		  </ul>
		  <div class="layui-tab-content">
		    <div class="layui-tab-item layui-show">
		    	<iframe vp-src="${base}/config/param?type=param.type&cid=param.cid&deviceType=param.deviceType" frameborder="0"></iframe>
		    </div>
		    <div class="layui-tab-item">
		    	<iframe vp-src="${base}/lang/field?type=param.type&cid=param.cid&deviceType=param.deviceType" frameborder="0"></iframe>
		    </div>
		    <div class="layui-tab-item">
		    	<iframe vp-src="${base}/lang/method?type=param.type&cid=param.cid&deviceType=param.deviceType" frameborder="0"></iframe>
		    </div>
			  <c:if test="${param.type == 1}">
				  <div class="layui-tab-item">
					  <iframe vp-src="${base}/lang/devData?type=param.type&cid=param.cid&deviceType=param.deviceType" frameborder="0"></iframe>
				  </div>
			  </c:if>
		  </div>
		</div>
	</body>
<script type="text/javascript">
	common.templateAttr("vp-src");
</script>

</html>