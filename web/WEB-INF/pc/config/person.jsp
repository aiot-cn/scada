<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>

<!doctype html>
<html>
	<head>
	<%@include file="../common/page_head.jsp" %>
	<title>人员</title>
		
<style type="text/css">
	html,body,.layui-fluid,.layui-row,.layui-card{
		height: 100%;
	}
	.ch-1,.ch-2{
		height: 100%;
	}
	
	.layui-card-body{
		height: calc(100% - 75px);
		overflow: auto;
	}
	
	.layui-fluid{
		box-sizing: border-box;
    	padding-bottom: 0;
	}
</style>
</head>
<body>
<div class="layui-fluid">
	<div class="layui-row layui-col-space15">

	  <div class="layui-col-md12 ch-1">    
		<div class="layui-card">
	          <div class="layui-card-header">
			      	人员
				  <%--<span class="layui-badge layui-bg-blue layuiadmin-badge layui-icon layui-icon-add-1" onclick="common.personEdit('')">
		    		添加
		    	  </span>--%>
			  </div>
	          <div class="layui-card-body">
	            <div class="layui-row layui-col-space10">
				    <table id="tPerson" class="layui-table">
						<thead>
							<tr>
								<th data-field="name">姓名</th>
								<th data-field="sex" data-translate="select">性别</th>
								<th data-field="birthday">生日</th>
								<th data-field="deptId" data-translate="select">部门</th>
								<th data-field="employeeNo">工号</th>
								<th data-field="cardNo">卡号</th>
								<th data-field="mobile">手机号</th>
								<th data-field="wechatId">微信ID</th>
								<th data-field="carNumber">车牌</th>
								<th data-field="remark">备注</th>
								<th data-type="edit" width="80" class="tac" data-class="tac">操作</th>
							</tr>
						</thead>
					</table>
	            </div>
	        </div>
	      </div>    
	  </div>
  
	</div>
</div>

<div>
	<form data-for="tPerson" class="form-horizontal">
		<input type="hidden" name="id">
	    <input class="layui-input" name="name" required="required">
		<select class="layui-input" name="sex"></select>
	    <input class="layui-input" name="birthday" type="datetime-local">
		<select class="layui-input" name="deptId"></select>
		<input class="layui-input" name="cardNo">
		<input class="layui-input" name="carNumber">
		<input class="layui-input" name="mobile">
		<input class="layui-input" name="wechatId">
		<input class="layui-input" name="employeeNo">
		<input class="layui-input" name="remark">
	</form>
</div>
</body>
<script type="text/javascript">
common.selectFromDict("sex","[name='sex']");
common.selectFromDict("dept","[name='deptId']",{"value":"id"});
common.ajaxStop(function () {
	tSite.load();
});
var tSite = new iTables("#tPerson",{},{
	baseOption : common.iTableModel("tPerson"),
	loadOnInit:false
});


</script>
</html>