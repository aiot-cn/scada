<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>

<!doctype html>
<html>
	<head>
	<%@include file="../../../common/page_head.jsp" %>
	<title>内网穿透服务</title>
		
<style type="text/css">
    html,body{
		height: 100%;
    }
	[data-clinic]{
		background-color: #ddd;
		padding: 1px 7px;
		border-radius: 10px;
	}
	[data-clinic][data-status="1"]{
		background-color: #009688;
		color: #fff;
	}
	[data-port] span{
		color: #fff;
		background-color: #89807d;
		padding: 2px 7px;
		border-radius: 4px;
		font-size: 12px;
		margin-right: 10px;
	}
	.p-read{
		background-color: #009688 !important;
	}
	.p-write{
		background-color: #2291dd !important;
	}
	.p-link{
		margin-right: 0 !important;
	}
</style>
</head>
<body>
<div class="layui-fluid sty-auto-h">
	<div class="layui-row layui-col-space15">
		<div class="layui-col-md5">
			<div class="layui-card">
				<div class="layui-card-header">
					<span class="title">客户端</span>
					<input data-search="tProxyClinic" placeholder="搜索">
				</div>
				<div class="layui-card-body">
					<table id="tProxyClinic" class="">
						<thead>
						<tr>
							<th data-type="rownum" width="30">No</th>
							<%--<th data-field="createDate" width="140">时间</th>--%>
							<th data-field="name">名称</th>
							<th data-field="key">密钥</th>
							<th data-render="renderStatus" width="60">状态</th>
							<th data-type="edit" width="40" class="tac" data-class="tac">操作</th>
						</tr>
						</thead>
					</table>
				</div>
			</div>
		</div>

	  <div class="layui-col-md7">
		<div class="layui-card">
	          <div class="layui-card-header">
				  <span class="title">代理配置</span>
				  <input data-search="tProxyPort" placeholder="搜索">
			  </div>
	          <div class="layui-card-body d-table">
	            <div class="layui-row ">
				    <table id="tProxyPort" class="">
						<thead>
							<tr>
								<th data-type="rownum" width="30">No</th>
								<%--<th data-field="createDate" width="140">时间</th>--%>
								<th data-field="name">名称</th>
								<th data-field="netPort" width="60">公网端口</th>
								<th data-field="lan">后端IP端口</th>
								<th data-render="renderMetrics">流量</th>
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

	<form data-for="tProxyClinic" class="layui-form layui-form-pane" style="padding:15px 15px 0 15px" data-layer='{title : "客户端",area : ["350px","auto"]}'>
		<input type="hidden" name="id">
		<div class="layui-form-item">
			<label class="layui-form-label">名称</label>
			<div class="layui-input-block">
				<input type="text" class="layui-input" name="name" required="required">
			</div>
		</div>
		<div class="layui-form-item">
			<label class="layui-form-label">密钥</label>
			<div class="layui-input-block">
				<input type="text" class="layui-input" name="key" required="required">
			</div>
		</div>
	</form>

	<form data-for="tProxyPort" class="layui-form layui-form-pane">
		<input type="hidden" name="id">
		<div class="layui-form-item">
			<label class="layui-form-label">名称</label>
			<div class="layui-input-block">
				<input type="text" class="layui-input" name="name" required="required">
			</div>
		</div>
		<div class="layui-form-item">
			<label class="layui-form-label">公网端口</label>
			<div class="layui-input-block">
				<input type="text" class="layui-input" name="netPort" required="required">
			</div>
		</div>
		<div class="layui-form-item">
			<label class="layui-form-label">后端IP端口</label>
			<div class="layui-input-block">
				<input type="text" class="layui-input" name="lan" placeholder="127.0.0.1:22" required="required">
			</div>
		</div>
	</form>

</div>
</body>
<script type="text/javascript">

	var tProxyClinic = new iTables("#tProxyClinic",{pageSize:0},{
		baseOption : common.iTableModel("proxyClinic"),
		renderStatus : function(tr,data){
			return '<span data-clinic="'+data.id+'" data-status="0"></span>';
		},
		onSelect:function(tr,data){
			tProxyPort.load({"clinicId":data.id});
		},
		beforeStats:function(){
			//this.selectIndex(0);
			getClientStatus();
		}
	});

	//先全部显示，为了查看流量
	var tProxyPort = new iTables("#tProxyPort",{pageSize:0},{
		baseOption : common.iTableModel("proxyPort"),
		renderMetrics : function(td,data){
			$(td).attr("data-port",data.netPort);
		},
		callForm:function(params){
			if(!tProxyClinic._data)
				layer.msg("请选择客户端");
			params.clinicId = tProxyClinic._data.id;
		},
		beforeStats:function(){
			getMetrics();
		}
	});

	function getClientStatus(){
		common.devExec2("getClientStatus",{}, function(json){
			$("[data-clinic]").each(function(){
				var status = json.data[this.dataset.clinic];
				$(this).attr("data-status",status ? 1 : 0).text(status ? "在线" : "离线");
			});
		});
	}

	function getMetrics(){
		common.devExec2("getMetrics",{}, function(json){
			var m = {};
			$(json.data).each(function (){
				m[this.port] = this;
			});
			$("[data-port]").each(function(){
				var d = m[this.dataset.port];
				if(d){
					this.innerHTML  = '<span class="p-read">↑ '+calcCapacity(d.readBytes)+'</span><span class="p-write">↓ '
							+calcCapacity(d.wroteBytes)+'</span><span class="p-link">连接数 '+d.channels+'</span>';
				}else{
					this.innerHTML = "";
				}
			});
		});
	}

	function calcCapacity(bytes){
		if(bytes < 1024)
			return "0 KB";
		if(bytes < 1024 * 1024)
			return parseInt((bytes / 1024)+"") + " KB";
		if (bytes < 1024 * 1024 * 1024)
			return parseInt((bytes / (1024 * 1024))+"") + "MB";
		return (bytes / (1024 * 1024 * 1024)).toFixed(1) + "GB";
	}

	setInterval(getClientStatus,5000);
	setInterval(getMetrics,10000);

</script>
</html>