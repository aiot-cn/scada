<%@page contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>

<!doctype html>
<html>
	<head>
	<%@include file="../../common/page_head.jsp" %>
	<title>角色设置</title>
		
<style type="text/css">
	html,body{
		height: 100%;
	}

	.layui-icon-circle{
		margin-right: 8px;
	    font-size: 22px;
	    color: #c2c2c2;
	    vertical-align: middle;
	    cursor: pointer;
    }
    
	.layui-icon-circle[v="0"]{
		content: "\e643";
		color: #5FB878;
	}
	.layui-icon-circle[v="0"]:before{
		content: "\e643";
	}

	
</style>
</head>
<body>
<div class="layui-fluid sty-auto-h">

<div class="layui-row layui-col-space15">
  <div class="layui-col-md6">
		<div class="layui-card">
          <div class="layui-card-header">
		      	角色
		  </div>
          <div class="layui-card-body">
            <div class="layui-row layui-col-space10">
 
	    <table id="itableRole" class="layui-table">
		<thead>
				<tr>
					<th data-type="rownum" width="20">No</th>
					<th data-field="name">名称</th>
					<th data-type="edit" width="80" class="tac" data-class="tac">操作</th>
				</tr>
			</thead>
		</table>
	
            </div>
        </div>
      </div>    
  </div>
  
    <div class="layui-col-md4" style="display: none">
		<div class="layui-card">
          <div class="layui-card-header">
		      	菜单
		  </div>
          <div class="layui-card-body">
            <div class="layui-row layui-col-space10">
 
	    <table id="tRoleMenu" class="layui-table">
		<thead>
				<tr>
					<th data-type="rownum" width="20">No</th>
					<th data-type="checkDel" data-join="mRoleMenu"  data-filed="menuId" width="40" data-class="tac" class="tac">选择</th>
					<th data-field="name" data-type="level">菜单</th>
				</tr>
			</thead>
		</table>

            </div>
        </div>
      </div>
  </div>

  <div class="layui-col-md6">
		<div class="layui-card">
          <div class="layui-card-header">
		      	行为
		  </div>
          <div class="layui-card-body">
            <div class="layui-row layui-col-space10">

	    <table id="tRoleAction" class="layui-table">
			<thead>
				<tr>
					<th data-type="rownum" width="20">No</th>
					<th data-type="checkDel" data-join="mRoleAction" data-filed="actionCode" width="40" data-class="tac">选择</th>
					<th data-field="name">行为</th>
					<th data-field="code">CODE</th>
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
	<form data-for="itableRole">
	    <input type="hidden" name="id">
	    <input type="text" class="layui-input" name="name">
	</form>
	<form data-for="tRoleAction">
	    <input type="hidden" name="id">
	    <select class="layui-input" name="actionCode" id="actionCode"></select>
	    <select class="layui-input" name="scope" id="scope"></select>
	    <select class="layui-input" name="emrStatus" id="emrStatus"></select>
	</form>
	<form data-for="tRoleMenu">
	    <input type="hidden" name="id">
	    <select class="layui-input" name="menuId" id="menuId"></select>
	</form>

</div>
</body>
<script type="text/javascript">
common.ajaxStop(function(){
	itableRole.load();
});

var itableRole = new iTables("#itableRole",{},{
	baseOption : common.iTableModel("sysRole"),
	loadOnInit : false,
	onSelect:function(tr,data){
		common.jsonModel("mRoleMenuAction",{"roleId" : data.id,"menuId_isNot":"null"},function(json){
			tRoleMenu.setJoinList("mRoleMenu",json.list);
		});
		common.jsonModel("mRoleMenuAction",{"roleId" : data.id,"actionCode_isNot":"null"},function(json){
			tRoleAction.setJoinList("mRoleAction",json.list);
		});
	},
	beforeStats:function(){
		this.selectIndex(0);
	}
});

var tRoleMenu = new iTables("#tRoleMenu",{},{
	getController  : "${base}/table/getList?tableName=sysMenu",
	ASC : "sequence",
	parentName : "parentId",
	mRoleMenu : {
		saveController : "${base}/table/doSave?tableName=mRoleMenuAction",
		delController : "${base}/table/doDel?tableName=mRoleMenuAction",
		callForm : function (params){
			params.menuId = itableRole._data.id;
		},
		joinField : "menuId"
	}
});

var tRoleAction = new iTables("#tRoleAction",{},{
	getController  : "${base}/json/getEnum?type=RoleAction",
	primaryKey : "code",
	mRoleAction : {
		saveController : "${base}/table/doSave?tableName=mRoleMenuAction",
		delController : "${base}/table/doDel?tableName=mRoleMenuAction",
		callForm : function (params){
			params.roleId = itableRole._data.id;
		},
		joinField : "actionCode"
	}
});


</script>
</html>