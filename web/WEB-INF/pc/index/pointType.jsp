<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>

<!doctype html>
<html>
	<head>
	<%@include file="../common/page_head.jsp" %>
	<title>点位类型</title>
		
<style type="text/css">
    html,body{
        background-color: #F2F2F2;
    }
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

	  <div class="layui-col-md12 ch-2">
		<div class="layui-card">
	          <div class="layui-card-header">
				  <span class="title">点位类型</span>
				  <input data-search="tPoint" placeholder="搜索">
			  </div>
	          <div class="layui-card-body">
	            <div class="layui-row layui-col-space10">
				    <table id="tPointType">
						<thead>
							<tr>
								<th data-field="id" width="20">ID</th>
								<th data-field="name" data-edit="true">名称</th>
								<th data-field="unit" data-edit="true">单位</th>
								<th data-field="recOnEvery" data-type="switch" width="40" data-class="tac">每次保存</th>
								<th data-field="recOnTime" data-type="switch" width="40" data-class="tac">定时保存</th>
								<th data-field="recOnState" data-type="switch" width="40" data-class="tac">状态保存</th>
								<th data-field="recOnValue" width="60">差异保存</th>
								<th data-field="alarmRule" data-edit="true">报警规则</th>
								<th data-type="edit" width="40" class="tac" data-class="tac">操作</th>
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

	<form data-for="tPointType" class="form-horizontal">
		<input type="hidden" name="id">
	    <input class="layui-input" name="name" required="required">
		<input class="layui-input" name="unit">
		<input class="layui-input" name="alarmRule">
		<input class="layui-input" name="recOnValue" type="number" step="0.001">
	</form>

</div>
</body>
<script type="text/javascript">

	var tPointType = new iTables("#tPointType",{pageSize:0},{
		baseOption : common.iTableModel("tPointType")
	});

</script>
</html>