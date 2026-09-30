<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ page import="java.lang.reflect.Field" %>
<%@ page import="java.util.Arrays" %>
<%@ page import="org.nutz.lang.Strings" %>
<%@ page import="java.util.Date" %>
<%@ page import="org.aiot.service.BaseService" %>
<%@ page import="org.aiot.main.Constants" %>
<%@ page import="org.aiot.lang.annotation.AoTbase" %>

<%
	BaseService bs = Constants.ioc.get(BaseService.class);
	String modelName = request.getParameter("model");
	Class<?> c = bs.getModelClass(modelName);
	Field[] fields = bs.getModelFields().get(c);
	fields = Arrays.copyOf(fields, fields.length - 6);
%>

<!doctype html>
<html>
	<head>
	<%@include file="../../common/page_head.jsp" %>
	<title>model</title>
		
<style type="text/css">

</style>
</head>
<body>
	<div style="margin: 10px">
		<table id="tModel" class="layui-table" style="margin: 0">
			<thead>
			<tr>
				<th data-field="isRemoved" data-type="switch" width="20" data-class="tac switch-contrary">R</th>
				<th data-field="id">ID</th>
				<% for(Field f : fields){
					String name = f.getName();
					AoTbase ao = f.getAnnotation(AoTbase.class);
					if(ao != null && Strings.isNotBlank(ao.value()))
						name = ao.value();
					out.println("<th data-field='"+f.getName()+"'>"+name+"</th>");
				}%>
				<th data-type="edit" width="60" data-class="tac">编辑</th>
			</tr>
			</thead>
		</table>
	</div>
	<form data-for="tModel">
	    <input type="hidden" name="id">
		<% for(Field f : fields){
			String name = f.getName();
			AoTbase ao = f.getAnnotation(AoTbase.class);
			if(ao != null && Strings.isNotBlank(ao.value()))
				name = ao.value();
			if(f.getType() == boolean.class || f.getType() == Boolean.class){
				out.println("<select class='layui-input' name='"+f.getName()+"' lay-ignore='' form='itable1_form'>\n" +
						"<option value='true'>是</option>" +
						"<option value='false'>否</option>" +
						"</select>");
			}else if(f.getType() == float.class || f.getType() == Float.class ||
					f.getType() == double.class || f.getType() == Double.class){
				out.println("<input class='layui-input' type='number' step='0.000001' name='"+f.getName()+"'>");
			}else{
				String type = "text";
				if(Date.class == f.getType()){
					type = "datetime-local";
				}else if(f.getType() == int.class || f.getType() == Integer.class ||
						f.getType() == short.class || f.getType() == Short.class ||
						f.getType() == long.class || f.getType() == Long.class){
					type = "number";
				}
				out.println("<input class='layui-input' type='"+type+"' name='"+f.getName()+"'>");
			}

		}%>
	</form>
</body>
<script type="text/javascript">
var model = param.model;
var tModel = new iTables("#tModel",{},{
	baseOption : common.iTableModel(model),
	inline_edit : true
	//scrollLoad : document
});

</script>
</html>