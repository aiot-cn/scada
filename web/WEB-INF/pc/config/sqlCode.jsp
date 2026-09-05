<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>

<!doctype html>
<html>
	<head>
	<%@include file="../common/page_head.jsp" %>
	<script src="${res}/plugin/code-prettify/prettify.js"></script>
	<script src="${res}/plugin/code-prettify/lang-sql.js"></script>
	<link href="${res}/plugin/code-prettify/prettify.css" rel="stylesheet" >
	<title>sqlCode</title>
		
<style type="text/css">
	html,body{
		height: 100%;
	}
	.layui-icon-tips{
		margin-right: 15px;font-weight: bold;font-size: 20px;position: relative;top: 2px;
		color: #999;
	}

	pre.prettyprint{
		border: none;
	}
</style>
</head>
<body>
	<div class="layui-fluid sty-auto-h">
		<div class="layui-row layui-col-space15">

		  <div class="layui-col-md12">
				<div class="layui-card">
				  <div class="layui-card-header" style="line-height: 40px;">
					  <span class="title">SQLCode</span>
						<div class="layui-input-inline" style="width: 200px;">
						 <input data-search="itable1" data-field="sql" placeholder="搜索" class="layui-input">
						</div>
						<button data-itable="create_itable1" class="layui-btn layui-btn-sm layui-btn-normal">
							<i class="layui-icon layui-icon-addition"></i> 新建
						</button>
						<button class="layui-btn layui-btn-warm layui-btn-sm" onclick="execSql()" style="margin-left: 0;">
							<i class="layui-icon layui-icon-triangle-r"></i>执行SQL
						</button>

					  <div style="float: right">
						  <a onclick='common.ajax("${base}/table/initSqlCode")'  style="margin-right: 15px;color: #0a76be">初始化</a>
						  <a onclick='common.ajax("${base}/table/refreshSqlCode")' style="margin-right: 15px;color: #0a76be">刷新缓存</a>
						  <i class="layui-icon layui-icon-tips" title="SQL说明" onclick="openSqlExplain()"></i>
					  </div>


				  </div>
				  <div class="layui-card-body">

					<table id="itable1" class="layui-table">
						<thead>
							<tr>
								<th data-field="isRemoved" width="40" data-type="switch"  data-class="tac switch-contrary">启用</th>
								<th data-field="code" width="100">CODE</th>
								<th data-field="sqlStatement">SQL</th>
								<%--<th data-field="dataSource" width="80">DataSource</th>--%>
								<th data-field="remark" width="150">备注</th>
								<th data-type="edit" width="40" class="tac" data-class="tac">操作</th>
							</tr>
						</thead>
					</table>

				</div>
			  </div>
		  </div>

	  </div>
	</div>

	<div class="lay-con d-sql-explain" style="line-height: 30px;">
		参数(param)占位符 - 形式为  <code>@名称</code>；变量(var)占位符 - 形式为  <code>$名称</code>； 条件变量占位符  <code>$condition</code><br>
		@name 将会被替换成<code> ? </code>；$table 将会被直接替换成值，如 t_abc；$condition 保留字段，每个条件将以 and 添加
	</div>

	<div class="lay-con">
		<table id="itable2" class="layui-table">
			<thead>
				<tr>
					<th data-field="isRemoved"  data-type="switch" width="30" class="tac" data-class="tac switch-contrary"></th>
					<th data-field="gro" width="30">组</th>
					<th data-field="ao" width="60">关联</th>
					<th data-field="name">字段</th>
					<th data-field="op" width="40">条件</th>
					<th data-field="value" width="80">值</th>
					<th data-field="dbType" width="80">数据库类型</th>
					<th data-field="remark" width="80">备注</th>
					<th data-field="sqlSub">子查询</th>
					<th data-type="edit" width="70" class="tac" data-class="tac">操作</th>
				</tr>
			</thead>
		</table>
	</div>
	
	<form data-for="itable1" id="form_itable1" class="layui-form" data-layer='{title : "信息",area : ["800px","520px"]}' style="padding-right: 40px">
	    <input type="hidden" name="id">
	    <div class="layui-form-item">
			<div class="layui-inline">
			    <label class="layui-form-label">CODE</label>
			    <div class="layui-input-inline">
			       <input type="text" class="layui-input" name="code" required="required">
			    </div>
		    </div>
		    <div class="layui-inline">
			    <label class="layui-form-label">DataSource</label>
			    <div class="layui-input-inline">
			      <select name="dataSource" class="layui-input" lay-ignore></select>
			    </div>
		    </div>	    
		</div>

		<div class="layui-form-item">
		    <label class="layui-form-label">SQL</label>
		    <div class="layui-input-block">
		      <textarea class="layui-textarea" name="sqlStatement" required="required" rows="13"></textarea>
		    </div>
		</div>
		<div class="layui-form-item">
		    <label class="layui-form-label">备注</label>
		    <div class="layui-input-block">
		      <input type="text" class="layui-input" name="remark">
		    </div>
		</div>
	</form>
	
	<form data-for="itable2" id="form_itable2" class="form-horizontal">
	    <input type="hidden" name="id">
	    <input type="text" class="layui-input" name="name">
		<input type="text" class="layui-input" name="op">
		<input type="text" class="layui-input" name="value">
		<input type="text" class="layui-input" name="remark" >
		<input type="text" class="layui-input" name="sqlSub" >

		<select class="layui-input" name="status">
			<option value="1">正常</option>
			<option value="0">停用</option>		
		</select>
		<select class="layui-input" name="gro">
			<option value=""></option>
			<option value="┌">┌</option>
			<option value="├">├</option>	
			<option value="└">└</option>	
			<option value="┼">┼</option>		
		</select>
		<select class="layui-input" name="ao">
			<option value="AND">AND</option>
			<option value="OR">OR</option>	
			<option value="andNot">andNot</option>		
		</select>
		<select class="layui-input" name="dbType">
			<option></option>
			<option>SQLITE</option>
			<option>MYSQL</option>
			<option>ORACLE</option>
			<option>SQLSERVER</option>
			<option>PSQL</option>
		</select>
	</form>

</body>
<script type="text/javascript">
/* common.jsonModelName("sysDataSource",{"isRemoved":0},function(json){
	common.renderSelect("[name='dataSource']",json.list,{name:"name",value:"name",dft:[{"name":"默认","value":""}]});
}); */



var itables1 = new iTables("#itable1",{},{
	baseOption : common.iTableModel("sqlCode"),
	ASC : "code",
	inline_edit : false,
	loadAfter : prettyPrint,
	render : {
		code : function(td,data){
			var a = $("<a>"+data.code+"</a>");
			a.click(function(){
				layer.open({
					type: 1,
					title : "条件信息",
					content: $(".lay-con"), //捕获的元素
					area : ["80%","400px"]
				});
				itables2.load({"codeId":data.id});
			});
			return a;
		},
		sqlStatement : function(td,data){
			var code = $(td).append('<pre class="prettyprint lang-sql">'+data.sqlStatement+'</pre>').find("code")[0];
		}
	}
});

var itables2 = new iTables("#itable2",{},{
	baseOption : common.iTableModel("sqlCondition","sequence"),
	loadOnInit : false,
	callForm:function(params){
		params.codeId = itables1._data.id;
	}
});

function execSql(){
	layer.prompt({
		title: 'SQL',
		formType: 2,
		area : ["800px","200px"]
	}, function(text, index){
		common.ajax("${base}/table/execSql",{sql:text});
	});
}

function openSqlExplain(){
	layer.open({
		type: 1,
		title: "SQL说明",
		content:$(".d-sql-explain"),
		area : ["800px","auto"]
	});
}

</script>
</html>