<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>

<!doctype html>
<html>
	<head>
	<%@include file="../common/page_head.jsp" %>
	<title>历史记录</title>
		
<style type="text/css">
    html,body{
		height: 100%;
    }
	.fm-search .layui-form-label{
		width: 60px;
		padding: 9px 0px;
	}
	.fm-search .layui-input-block{
		margin-left: 75px;
	}
	.fm-search .layui-form-item {
		margin-bottom: 10px;
	}
	td.p-img{
		padding: 0 !important;
	}
</style>
</head>
<body>
<div class="layui-fluid sty-auto-h">
	<div class="layui-row layui-col-space15">
		<div class="layui-col-md2" style="width: 250px;">
			<div class="layui-card">
				<div class="layui-card-header">
					<span class="title">查询条件：</span>
				</div>
				<div class="layui-card-body">
					<form name="fm" class="layui-form fm-search" action="">
						<div class="layui-form-item">
							<label class="layui-form-label">开始时间</label>
							<div class="layui-input-block">
								<input type="date" name="beginTime" autocomplete="off" class="layui-input">
							</div>
						</div>
						<div class="layui-form-item">
							<label class="layui-form-label">结束时间</label>
							<div class="layui-input-block">
								<input type="date" name="endTime" autocomplete="off" class="layui-input">
							</div>
						</div>
						<div class="layui-form-item">
							<label class="layui-form-label">点位名称</label>
							<div class="layui-input-block">
								<input class="layui-input" name="name" data-type="like">
							</div>
						</div>
						<div class="layui-form-item">
							<label class="layui-form-label">点位编号</label>
							<div class="layui-input-block">
								<input name="pCode" class="layui-input" autocomplete="off">
							</div>
						</div>
						<div class="layui-form-item">
							<label class="layui-form-label">值大于</label>
							<div class="layui-input-block">
								<input class="layui-input" type="number" name="vGt" step="0.01">
							</div>
						</div>
						<div class="layui-form-item">
							<label class="layui-form-label">值小于</label>
							<div class="layui-input-block">
								<input class="layui-input" type="number" name="vLt" step="0.01">
							</div>
						</div>
						<div class="layui-form-item">
							<label class="layui-form-label">状态</label>
							<div class="layui-input-block">
								<select class="layui-input" name="state" lay-ignore>
									<option value="">--</option>
									<option value="0">正常</option>
									<option value="1">预警</option>
									<option value="2">报警</option>
								</select>
							</div>
						</div>
						<div class="layui-form-item">
							<label class="layui-form-label">备注</label>
							<div class="layui-input-block">
								<input class="layui-input" name="remark" data-type="like">
							</div>
						</div>
						<div class="layui-form-item">
							<div class="layui-input-block">
								<button type="button" class="layui-btn layui-btn-normal layui-btn-sm" onclick="tRecord.load()">
									<i class="layui-icon layui-icon-search"></i>
									查询
								</button>
							</div>
						</div>
					</form>
				</div>
			</div>
		</div>

	  <div class="layui-col-md12" style="width: calc(100% - 250px);">
		<div class="layui-card">
	          <div class="layui-card-header">
				  <span class="title">历史记录</span>
				  <button onclick="downFile()" class="layui-btn layui-btn-normal layui-btn-sm" style="vertical-align: initial">
					  <i class="layui-icon layui-icon-download-circle"></i>
					  下载文件
				  </button>
				  <div data-itable="tool_tRecord" class="itable-tool"></div>
			  </div>
	          <div class="layui-card-body d-table">
	            <div class="layui-row layui-col-space10">
				    <table id="tRecord" class="">
						<thead>
							<tr>
								<th data-type="rownum" width="30">No</th>
								<th data-field="createDate" width="140">时间</th>
								<th data-field="name">名称</th>
								<th data-field="code">编号</th>
								<th data-field="value">值</th>
								<th data-field="unit" data-edit="true">单位</th>
								<th data-field="state" data-type="select">状态</th>
								<th data-field="file" data-class="p-img" width="80">文件</th>
								<th data-field="remark">备注</th>
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

	<form data-for="tRecord" class="layui-form layui-form-pane" style="padding:15px 15px 0 15px" data-layer='{title : "记录",area : ["350px","auto"]}'>
		<input type="hidden" name="id">
		<div class="layui-form-item">
			<label class="layui-form-label">值</label>
			<div class="layui-input-block">
				<input type="text" class="layui-input" name="value" required="required">
			</div>
		</div>
		<div class="layui-form-item">
			<label class="layui-form-label">状态</label>
			<div class="layui-input-block">
				<select class="layui-input" lay-ignore="" name="state">
					<option value="0">正常</option>
					<option value="1" style="color: #ef8a0c">预警</option>
					<option value="2" style="color: red">报警</option>
				</select>
			</div>
		</div>
		<div class="layui-form-item">
			<label class="layui-form-label">备注</label>
			<div class="layui-input-block">
				<input type="text" class="layui-input" name="remark">
			</div>
		</div>
	</form>

</div>
</body>
<script type="text/javascript">
	common.fileView();
	var tRecord = new iTables("#tRecord",{pageSize:100},{
		baseOption : common.iTableModel("tRecord"),
		getController  : "${base}/table/sqlCode/record",
		DESC: param.desc || "id",
		saveReformatGet : true,
		hasSort : false,
		scrollLoad : ".d-table",
		render : {
			name : function (td,data){
				var title = "点位："+data.pid;
				if(data.code)
					title += "\n编号："+data.code;
				if(data.remark)
					title += "\n备注："+data.remark;
				td.title = title;
				return data.name || "";
			},
			value : function (td,data){
				return data.value == undefined ? data.valStr : data.value;
			},
			file : function (td,data,col,val){
				if(!val)
					return;
				var val = val.replace(/\\/g,"/");
				var suffix = val.slice(val.lastIndexOf(".")+1).toLowerCase();
				var imgType = ["jpg","jpeg","png","gif","bmp","mp4"];
				if(imgType.indexOf(suffix) > -1)
					return "<img data-pos='"+(data.targets)+"' lay-src='${base}/image"+val+"?width=100'>";
				return "<span data-view='"+val+"'>" +val.slice(val.lastIndexOf("/")+1)+ "</span>";
			}
		},
		callLoadParams : function(params,options){
			var p = common.formJSON(fm) || {};
			return p;
		},
		callback : function (){
			layui.use('flow', function(){
				layui.flow.lazyimg({
					elem:"img",
					scrollElem : ".d-table"
				});
			});
		}
	});

	function downFile(){
		var name = [];
		$(tRecord.json).each(function (){
			if(this.file)
				name.push(this.file);
		});
		if(!name.length){
			layer.msg("没有可下载的文件");
			return;
		}
		window.open("${base}/file/downloads?names="+name.join(","));
	}
</script>
</html>