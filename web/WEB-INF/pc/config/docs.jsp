<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>

<!doctype html>
<html>
<head>
	<%@include file="../common/page_head.jsp" %>

	<title>文档配置</title>
	<style type="text/css">
		html,body{
			height: 100%
		}
		.scroll-wrapper {
			width: 100%;
			height: 100%;
			padding: 0;
			margin: 0;
		}
	</style>
</head>
<body>
<div class="layui-fluid sty-auto-h">

	<div class="layui-row layui-col-space15">
		<div class="layui-col-md3">
			<div class="layui-card">
				<div class="layui-card-header">
					<span class="title">文档</span>
					<select id="docProject" onchange="tScript.load({'proCode':this.value})"></select>
				</div>
				<div class="layui-card-body">
					<div class="layui-row layui-col-space10">

						<table id="tDoc" class="layui-table">
							<thead>
							<tr>
								<th data-field="name" data-type="level">名称</th>
								<th data-field="path">路径</th>
								<th data-type="edit" data-class="tac" width="40">操作</th>
							</tr>
							</thead>
						</table>

					</div>
				</div>
			</div>
		</div>
		<div class="layui-col-md9">
			<div class="layui-card">
				<div class="layui-card-header">
					<span class="title">内容</span>
				</div>
				<div class="layui-card-body">
					<div class="layui-row layui-col-space10">
						<iframe class="scroll-wrapper" src="${base}/base/view/markdown?PROTOCOL=TText-doc-0" frameborder="0" name="fm"></iframe>
					</div>
				</div>
			</div>
		</div>
	</div>

</div>

<div>
	<form data-for="tDoc">
		<input type="text" class="layui-input" name="name">
		<input type="text" class="layui-input" name="path">
	</form>
</div>
</body>
<script type="text/javascript">

	common.jsonDict("docProject",function(list){
		common.renderSelect("#docProject",list,{value:"code",dft:"aiot"});
	});

	common.ajaxStop(function(){
		tDoc.load({"proCode":$("#docProject").val()});
	});

	var tDoc = new iTables("#tDoc",{},{
		baseOption : common.iTableModel("tDoc","sequence"),
		parentName : "parentId",
		loadOnInit : false,
		callForm:function(params){
			params.proCode = $("#docProject").val();
		},
		onSelect : function(tr,data){
			$(".scroll-wrapper").attr("src","${base}/base/view/markdown?PROTOCOL=TText-doc-"+data.id)
		},
		render : {
			"path" : function(tr,data){
				if(data.path){
					var path = data.proCode+data.path;
					return "<a href='${base}/docs/"+path+"' target='_blank'>"+data.path+"</a>";
				}

			}
		}
	});


</script>
</html>