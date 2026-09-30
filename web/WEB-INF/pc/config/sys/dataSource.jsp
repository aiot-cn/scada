<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>

<!doctype html>
<html>
	<head>
	<%@include file="../../common/page_head.jsp" %>
	<title>数据源</title>
		
<style type="text/css">
	html,body,.layui-fluid,.layui-row,.layui-card{
		height: 100%;
		box-sizing: border-box;
	}
	.ch-2{
		height: calc(100% - 0px);
	}
	.ch-2 .layui-card-body{
		height: calc(100% - 75px);
		overflow: auto;
	}
	
</style>
</head>
<body>
<div class="layui-fluid">
	<div class="layui-row layui-col-space15">


  <div class="layui-col-md12 ch-2">    
		<div class="layui-card">
          <div class="layui-card-header">
		      	数据源
		  </div>
          <div class="layui-card-body itable-scroll">
            <div class="layui-row layui-col-space10">
 
	    <table id="itableDataSource" class="layui-table">
		<thead>
				<tr>
					<th data-field="isRemoved" data-type="switch" data-class="tac switch-contrary" width="40">启用</th>
					<th data-field="name">名称</th>
					<th data-field="url">URL</th>
					<th data-field="username">用户名</th>
					<th data-field="password">密码</th>
					<th data-field="maxWait">连接超时</th>
					<th data-field="maxActive">最大连接</th>
					<th data-field="defaultAutoCommit" data-translate="select" width="60">自动提交</th>
					<th data-field="validationQuery">测试SQL</th>
					<th data-type="edit" width="60" data-class="tac">操作</th>
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
	<form data-for="itableDataSource">
	    <input type="hidden" name="id">
	    
	    <input name="name" class="layui-input">
	    <input name="url" class="layui-input" list="url_list">
	    <input name="username" class="layui-input">
	    <input name="password" class="layui-input">
	    <input name="maxWait" class="layui-input" type="number">
	    <input name="maxActive" class="layui-input" type="number">
	    <select class="layui-input" name="defaultAutoCommit">
	    	<option value="0">否</option>
	    	<option value="1">是</option>
	    </select>
	    <input name="validationQuery" class="layui-input">
	</form>

	<datalist id="url_list">
		<option value="jdbc:mysql://localhost:3306/dbname">mysql</option>
	</datalist>
</div>
</body>
<script type="text/javascript">

var itableTemplate = new iTables("#itableDataSource",{},{
	baseOption : common.iTableModel("sysDataSource")
	
});

</script>
</html>