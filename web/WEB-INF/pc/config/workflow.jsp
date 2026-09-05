<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!doctype html>
<html>
	<head>
<title>工作流</title>
<c:import url="../common/page_head.jsp"></c:import>
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

	.break-all{
		word-break: break-all;
	}
	.layui-btn-xs{
		height: 25px;
		line-height: 25px;
		padding: 0 2px;
	}
	.layui-btn-xs i{
		font-size: 14px !important;
	}
	#tWorkflow img{
		max-width: 100%;
	}
	.itable tbody tr:first-child td{
		border-top:none;
	}
	.itable td{
		color: #666;
		padding: 2px 3px;
	}
	.layui-btn-group button,.layui-btn-group .layui-btn-primary:first-child{
		border: none;
	}
	.filter-img.active{
		color: #0d7cdf;
	}
</style>
</head>
	<body>
	<div class="layui-fluid sty-auto-h">

		<div class="layui-row layui-col-space15">
			<div class="layui-col-md3" style="width: 250px">
				<div class="layui-card">
					<div class="layui-card-header">
						<div class="layui-btn-group">
							<button class="filter-img layui-btn layui-btn-primary layui-btn-xs"><i class="layui-icon layui-icon-picture"></i></button>
							<button data-itable="create_tWorkflow" type="button" class="layui-btn layui-btn-primary layui-btn-xs"><i class="layui-icon"></i></button>
							<button onclick="tWorkflow.edit()" type="button" class="layui-btn layui-btn-primary layui-btn-xs"><i class="layui-icon"></i></button>
							<button onclick="tWorkflow.delete()" type="button" class="layui-btn layui-btn-primary layui-btn-xs"><i class="layui-icon"></i></button>
						</div>
						<input data-search="tWorkflow" placeholder="搜索" style="width: 100px;font-size: 12px;height: 25px;">
					</div>
					<div class="layui-card-body">
						<div class="layui-row layui-col-space10">

							<table id="tWorkflow" class="">
								<thead style="display: none">
									<tr>
										<th data-field="code">code</th>
									</tr>
								</thead>
							</table>

						</div>
					</div>
				</div>
			</div>

			<div class="layui-col-md9" style="height: 100%;width: calc(100% - 250px);">
				<div class="layui-card">
					<div class="layui-card-header">
						<span class="title">工作流</span>
					</div>
					<div class="layui-card-body">
						<div class="layui-row layui-col-space10">
							<iframe class="scroll-wrapper" frameborder="0" name="fm"></iframe>
						</div>
					</div>
				</div>
			</div>

		</div>

	</div>

	<div>
		<form data-for="tWorkflow" class="layui-form layui-form-pane" style="padding:15px 15px 0 15px" data-layer='{title : "工作流",offset:["30px","90px"],area : ["350px","auto"]}'>
			<input type="hidden" name="id">
			<div class="layui-form-item">
				<label class="layui-form-label">CODE</label>
				<div class="layui-input-block">
					<input type="text" class="layui-input" name="code" required="required">
				</div>
			</div>
			<div class="layui-form-item">
				<label class="layui-form-label">名称</label>
				<div class="layui-input-block">
					<input type="text" class="layui-input" name="name">
				</div>
			</div>
			<div class="layui-form-item">
				<label class="layui-form-label">图片</label>
				<div class="layui-input-block">
					<input type="text" class="layui-input" name="img">
				</div>
			</div>
			<div class="layui-form-item">
				<label class="layui-form-label">返回类型</label>
				<div class="layui-input-block">
					<input type="text" class="layui-input" name="returnType">
				</div>
			</div>
		</form>

	</div>
	</body>

<script type="text/javascript">

	var tWorkflow = new iTables("#tWorkflow",{img:"null"},{
		baseOption : common.iTableModel("tWorkflow"),
		inline_edit : false,
		onSelect:function(tr,data){
			//pid 访问速度快，code 较友好
			$('[name="fm"]').attr("src","${base}/base/editor/workflow?PROTOCOL=tWorkflow-"+data.id);
		},
		render : {
			code : function (td,data){
				var d = $("<div title='"+(data.returnType || "")+"'><span class='itable-rownum'> "+(data.code || "")+"</span><span style='float: right'>"+(data.name || "")+"</span></div>").appendTo(td);
				if(data.img)
					$("<img src='${base}/image/"+data.img+"?width=250'>").appendTo(td).click(function (){
						layer.open({
							type : 2,
							btn : false,
							shade : 0,
							title: data.name || data.code,
							content : "${base}/view/" + data.img,
							area : ["80%","80%"],
							scrollbar: false,
							maxmin: true
						});
					});
			}
		},
		loadAfter : function (){
			this.selectIndex(0);
		},
		callLoadParams : function(params){
			params.pid = "null";
		}
	});

	$(tWorkflow._form.img).click(function (){
		common.openFile({suffix:"jpg,jpeg,png,gif,bmp",input:this});
	});

	$(".filter-img").click(function (){
		if($(this).toggleClass("active").hasClass("active")){
			tWorkflow.load({img_isNot:"null"});
		}else{
			tWorkflow.load({img:"null"});
		}
	});

</script>

</html>