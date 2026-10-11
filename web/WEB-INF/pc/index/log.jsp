<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!doctype html>
<html>
	<head>
		<c:import url="../common/page_head.jsp"></c:import>
<title>系统日志</title>
<style type="text/css">
	html,body{
		height: 100%
	}

</style>
</head>
<body>

<div class="layui-fluid sty-auto-h">

	<div class="layui-row layui-col-space15">
	  
	  <div class="layui-col-md9">    
			<div class="layui-card">
	          <div class="layui-card-header">
			      	日志
			  </div>
	          <div class="layui-card-body table-history">
	            <div class="layui-row layui-col-space10">
	 
				    <table id="itable1" class="itable">
						<thead>
							<tr>
								<th data-type="rownum" width="40">No</th>
								<th data-field="createDate" width="150">时间</th>
								<th data-field="eventType" data-type="dict">事件</th>
								<th data-field="content">内容</th>
								<th data-field="createBy" data-type="dict">操作人</th>
								<th data-field="logLevel" data-translate="{0:'信息',1:'警告',2:'错误'}" width="30">级别</th>
							</tr>
						</thead>
					</table>
		
	            </div>
	        </div>
	      </div>    
	  </div>

			<div class="layui-col-md3">
				<div class="layui-card">
					<div class="layui-card-header">查询</div>
					<div class="layui-card-body">
						<div class="layui-row layui-col-space10">
							<form name="fm" class="layui-form" action="">
								<div class="layui-form-item">
									<label class="layui-form-label">事件</label>
									<div class="layui-input-block">
										<select class="layui-input" name="eventType" lay-ignore=""></select>
									</div>
								</div>
								<div class="layui-form-item">
									<label class="layui-form-label">内容</label>
									<div class="layui-input-block">
										<input class="layui-input" name="content" data-type="like">
									</div>
								</div>
								<div class="layui-form-item">
									<label class="layui-form-label">级别</label>
									<div class="layui-input-block">
										<select class="layui-input" name="logLevel">
											<option value="">--</option>
											<option value="0">信息</option>
											<option value="1">警告</option>
											<option value="2">错误</option>
										</select>
									</div>
								</div>
								<div class="layui-form-item">
									<label class="layui-form-label">开始时间</label>
									<div class="layui-input-block">
										<input class="layui-input" type="date" name="createDate_lt">
									</div>
								</div>
								<div class="layui-form-item">
									<label class="layui-form-label">结束时间</label>
									<div class="layui-input-block">
										<input class="layui-input" type="date" name="createDate_gt">
									</div>
								</div>

								<div class="layui-form-item">
								    <div class="layui-input-block">
								      <a class="layui-btn" href="javascript:itables1.load();" style="padding: 0 13px">
								      	<i class="layui-icon layui-icon-search"></i> 查询
								      </a>
										<button type="reset" class="layui-btn layui-btn-primary">重置</button>
								    </div>
								</div>

							</form>
						</div>
					</div>
				</div>
			</div>

		</div>

</div>



</body>
<script type="text/javascript">
	var eventTypeDict =  {},userDict = {};
	common.jsonEnum("LogEvent",function (list){
		common.renderSelect('[name="eventType"]',list,{value:"code",dft:""});
		$(list).each(function (){
			eventTypeDict[this.code] = this;
		});
	});
	common.jsonModel("SysUser",{},function (json){
		$(json.list).each(function (){
			userDict[this.id] = this;
			this.name = this.name || this.login;
		});
	});

common.ajaxStop(function (){
	itables1.load();
});
var itables1 = new iTables("#itable1",{pageSize:100},{
	getController  : "${base}/table/getList?tableName=tLog",
	loadOnInit:false,
	DESC:"id",
	scrollLoad : ".table-history",
	dict : {
		eventType : eventTypeDict,
		createBy : userDict
	},
	callLoadParams : function(params){
		var p = common.formJSON(fm) || {};
		//p.type_lt = 10;
		return p;
	}
});


</script>
</html>