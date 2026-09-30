<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>

<!doctype html>
<html>
	<head>
	<%@include file="../../common/page_head.jsp" %>
	<title>用户</title>
		
<style type="text/css">
	html,body{
		height: 100%
	}
</style>
</head>
<body>
<div class="layui-fluid sty-auto-h">

<div class="layui-row layui-col-space15">
  <div class="layui-col-md12">
		<div class="layui-card">
          <div class="layui-card-header">
		      	用户
		      	<input data-search="tDictType" placeholder="搜索">
		  </div>
          <div class="layui-card-body">
            <div class="layui-row layui-col-space10">
 
	    <table id="tUser" class="layui-table">
			<thead>
				<tr>
					<th data-field="login">账号</th>
					<th data-field="name" data-translate="select">名称</th>
					<%--<th data-field="password">密码</th>--%>
					<th data-field="employeeNo">工号</th>
					<th data-field="mobile">电话</th>
					<th data-field="remark">备注</th>
					<th data-field="roleId" data-type="select">角色</th>
					<th data-field="isDefault" data-type="switch" width="40" data-class="tac">默认</th>
					<th data-type="edit" data-render="renderEdit" width="60" data-class="tac">操作</th>
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
	<form data-for="tUser">
	    <input class="layui-input" name="login">
		<input class="layui-input" name="remark">
		<input class="layui-input" name="name">
		<input class="layui-input" name="employeeNo">
		<input class="layui-input" name="mobile">
		<select class="layui-input" name="roleId"></select>
		<select class="layui-input" name="isDefault">
			<option value="0">否</option>
			<option value="1">是</option>
		</select>
	</form>

</div>
</body>
<script type="text/javascript">
	var sysSite=[],sysRole=[],sysSite1=[];


	common.jsonModel("sysRole",{"isRemoved":0},function(json){
		common.renderSelect(tUser._form.roleId,json.list,{dft:""});
	});

	common.ajaxStop(function () {
		tUser.load();
	});


	var tUser = new iTables("#tUser",{},{
		baseOption : common.iTableModel("sysUser"),
		loadOnInit: false,
		renderEdit : function (td,data){

			var n = $("<i class='layui-icon layui-icon-password' title='重置密码'></i>").appendTo(td);
			n.click(function (){
				layer.prompt({
					formType: 0, //输入框类型，支持0（文本）默认1（密码）2（多行文本）
					value : "123456",
					title : "重置密码"
				},function(value, index, elem){
					common.ajax("${base}/user/resetPassword",{"userId":data.id,"password":value});
					layer.close(index);
				});
			});

			if(data.id == 0){
				$(td).find(".itable-delete").hide();
				//n.hide();
			}
		}
	});

</script>
</html>