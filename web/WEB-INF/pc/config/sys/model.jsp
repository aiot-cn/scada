<%@ page contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>

<%@ page import="org.aiot.service.BaseService" %>
<%@ page import="org.aiot.main.Constants" %>
<%@ page import="org.aiot.lang.annotation.AoTbase" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.Collections" %>
<%@ page import="java.util.Comparator" %>
<%@ page import="java.util.List" %>

<%
	BaseService bs = Constants.ioc.get(BaseService.class);
	List<Class<?>> classes = new ArrayList<>(bs.getModelFields().keySet());
	Collections.sort(classes, new Comparator<Class<?>>() {
		public int compare(Class<?> o1, Class<?> o2) {
			return o1.getName().compareToIgnoreCase(o2.getName());
		}
	});
%>
<!doctype html>
<html>
	<head>
	<%@include file="../../common/page_head.jsp" %>
	<title>实体表</title>
		
<style type="text/css">
	html,body,.layui-fluid,.layui-row,.layui-card{
		height: 100%;
		box-sizing: border-box;
	}
	.scroll-wrapper {
		width: 100%;
		height: 100%;
		padding: 0;
		margin: 0;
	}
	.ch-2{
		height: calc(100% + 15px);
	}
	.ch-2 .layui-card-body{
		height: calc(100% - 75px);
		overflow: auto;
	}
	#ulTable li{
		border-bottom: 1px dashed #999;
		padding: 5px;
		color: #666;
	}
	#ulTable li:hover,#ulTable li.active{
		background-color: #F0F7F9;
		cursor: pointer;
	}
	.t-filed{
		-position: absolute;
		float: left;
		margin: 10px;
	}
	.t-filed thead{
		background-color: #dde5ff;
		color: #4d5059;
		font-size: 12px;
	}
	.t-filed thead th{
		border-radius: 5px 5px 0 0;
	}
	.t-filed tbody{
		border: 1px solid #f2f5f6;
	}
	.t-filed td{
		-border-top: 1px solid #f2f5f6;
		color: #585959;
		font-size: 10px;
		padding: 0px 8px;
	}
</style>
</head>
<body>
<div class="layui-fluid">
	<div class="layui-row layui-col-space15">
		<div class="layui-col-md2 ch-2">

		<div class="layui-card">
				<div class="layui-card-header">
					表
					<input data-search="modelTable" placeholder="搜索">
				</div>
				<div class="layui-card-body">
					<div class="layui-row layui-col-space10">

						<ul id="ulTable">
						<% for(Class<?> c : classes){
							String name = c.getSimpleName();
							AoTbase ao = c.getAnnotation(AoTbase.class);
							if(ao != null && !ao.value().isEmpty()){
								name += "["+ao.value()+"]";
							}
						%>
							<li title="<%= c.getName() %>" data-model="<%= c.getSimpleName() %>"><%= name %></li>
						<% } %>
						</ul>

					</div>
				</div>
		</div>
	</div>

		<div class="layui-col-md10 ch-2">
			<div class="layui-card">
				<div class="layui-card-header">
					详细
				</div>
				<div class="layui-card-body">
					<div id="er" class="layui-row layui-col-space10">
						<iframe class="scroll-wrapper" frameborder="0" name="fm"></iframe>
					</div>
				</div>
			</div>
		</div>

	</div>
</div>





</body>
<script type="text/javascript">

var modelTable;
var tFiled = $(".t-filed");

$("#ulTable").on("click","li",function () {
	$("#ulTable .active").removeClass("active");
	$(this).addClass("active");
	$('[name="fm"]').attr("src","${base}/config/sys/modelEditor?model="+$(this).data("model"));
});
$("#ulTable li").eq(0).click();


</script>
</html>