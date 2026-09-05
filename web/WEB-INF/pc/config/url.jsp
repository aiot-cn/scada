<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>

<!doctype html>
<html>
	<head>
	<%@include file="../common/page_head.jsp" %>
	<title>URL映射</title>
		
<style type="text/css">
	html,body{
		height: 100%;
	}

</style>
</head>
<body>

	<div class="layui-fluid sty-auto-h">
		<div class="layui-row layui-col-space15">
			<div class="layui-col-md12">
				<div class="layui-card">
					<div class="layui-card-header">
						<span class="title">URL</span>
					</div>
					<div class="layui-card-body">
						<div class="layui-row layui-col-space10">

							<table id="tUrl" class="layui-table">
								<thead>
									<tr>
										<th data-field="isRemoved" data-type="switch" width="40" data-class="tac switch-contrary">状态</th>
										<th data-field="name">名称</th>
										<th data-field="type" data-translate="select">类型</th>
										<th data-field="url">URL</th>
										<th data-field="resParam">参数</th>
										<th data-field="script" data-translate="select">脚本</th>
										<th data-field="role" data-translate="select">权限</th>
										<th data-type="edit" class="tac" data-class="tac" width="70">编辑</th>
									</tr>
								</thead>
							</table>

						</div>
					</div>
				</div>
			</div>
		</div>
	</div>

	<div class="lay-con">
		<table id="tUrlParam" class="layui-table">
			<thead>
			<tr>
				<th data-field="value">值</th>
				<th data-type="edit" class="tac" data-class="tac" width="70">编辑</th>
			</tr>
			</thead>
		</table>
	</div>

	<form data-for="tUrl">
	    <input type="hidden" name="id">
	    <input class="layui-input" name="url" required>
		<input class="layui-input" name="name">
	    <input class="layui-input" name="resParam">
		<select class="layui-input" name="script"></select>
		<select class="layui-input" name="role">
			<option value="0">游客</option>
			<option value="1">用户</option>
			<option value="2">站点</option>
		</select>
		<select class="layui-input" name="type">
			<option value="0">页面</option>
			<option value="1">JSON</option>
			<option value="2">中转</option>
			<option value="3">重定向</option>
			<option value="4">格式</option>
			<option value="5">代理</option>
			<option value="6">SQL</option>
		</select>
	</form>

	<form data-for="tUrlParam">
		<input type="hidden" name="id">
		<input class="layui-input" name="value" required>
	</form>

</body>
<script type="text/javascript">
	var url = "/";
	common.jsonModel("sysScript",{type:"url"},function(json){
		common.renderSelect("[name='script']",json.list,{value:"code",dft:""});
		tUrl.load();
	});

	var tUrl = new iTables("#tUrl",{},{
		baseOption : common.iTableModel("sysUrl"),
		loadOnInit : false,
		render : {
			name : function (td,data){
				return "<a href='${base}"+data.url+"' target='_blank'>"+(data.name || '--')+"</a>";
			},
			url : function(td,data){
				var a  = $("<a>"+data.url+"</a>");
				a.click(function (){
					layer.open({
						type : 1,
						title : "参数",
						area : ["80%","400px"],
						content : $(".lay-con"),
					});
					url = data.url.split("?")[0];
					tUrlParam.load({"type":5,"code":url});
				});
				return a;
			}
		}
	});

	var tUrlParam = new iTables("#tUrlParam",{},{
		baseOption : common.iTableModel("configParam"),
		loadOnInit : false,
		callForm : function(params){
			params.type = 5;
			params.code = url;
		}
	});


</script>
</html>