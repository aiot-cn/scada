<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>

<!doctype html>
<html>
	<head>
	<%@include file="../common/page_head.jsp" %>
	<script src="${res}/js/cronstrue-i18n.min.js"></script>
	<title>定时任务</title>
		
<style type="text/css">
	html,body{
		height: 100%;
	}
	.scroll-wrapper{
		width: 100%;
		height: 100%;
		padding: 0;
		margin: 0;
	}
	.layui-icon-run{
		font-size: 20px;
	}

	.layui-icon-run[data-v='NORMAL']:before,
	.layui-icon-run[data-v='BLOCKED']:before,
	.layui-icon-run[data-v='RUNNING']:before{
		content: "\e651";
	}
	.layui-icon-run:before{
		content: "\e652";
	}
	.message{
		white-space: pre-line;
		word-break: break-word;
	}
	.f-cron div{
		margin-bottom: 10px;
		color: #333;
	}
	.f-cron div:last-child{
		margin-bottom: 0;
	}
	.f-cron [type="radio"]{
		vertical-align: middle;
		zoom: 130%;
		cursor: pointer;
	}
	.bootstrap-input{
		font-size: 12px;
		color: #049A64;
	}
	[data-state='NORMAL']{
		color: #16971b;
	}

</style>
</head>
<body>

	<div class="layui-fluid sty-auto-h">
		<div class="layui-row layui-col-space15">
			<div class="layui-col-md4">
				<div class="layui-card">
					<div class="layui-card-header">定时任务</div>
					<div class="layui-card-body">
						<div class="layui-row layui-col-space10">

							<table id="tCrontab" class="layui-table">
								<thead>
									<tr>
										<th data-field="isRemoved" data-type="switch" width="30" data-class="tac switch-contrary"></th>
										<th data-field="cron">周期</th>
										<th data-field="remark">备注</th>
										<th data-render="renderState" width="30">状态</th>
										<th data-type="edit" data-render="renderEdit" class="tac" data-class="tac" width="70">编辑</th>
									</tr>
								</thead>
							</table>

						</div>
					</div>
				</div>
			</div>

			<div class="layui-col-md8">
				<div class="layui-card">
					<div class="layui-card-header">
						执行
					</div>
					<div class="layui-card-body">
						<div class="layui-row layui-col-space10">
							<iframe class="scroll-wrapper" src="${base}/config/action?style=in-layui-card&klass=org.aiot.model.table.SysCrontab&pid=0" frameborder="0" name="fm"></iframe>
						</div>
					</div>
				</div>
			</div>

		</div>
	</div>

	<form data-for="tCrontab">
	    <input type="hidden" name="id">
	    <input class="layui-input" name="cron" autocomplete="off">
	    <input class="layui-input" name="remark">
<%--	<input class="layui-input" name="args">
		<select class="layui-input" name="klass">
			<option value="plugin.crontab.CronAction">动作链</option>
		</select>--%>
		<datalist id="crons">
			<option value="0 */1 * * * ?">每1分钟</option>
			<option value="0 0 */1 * * ?">每1小时</option>
		</datalist>
	</form>

	<div class="lay-con" style="margin-bottom: 5px">
		<form name="f1" class="f-cron">
			<div>
				<input class="bootstrap-input" name="type" type="radio" value="1" checked>
				每<span style="visibility: hidden">占</span> <input class="bootstrap-input" name="a1" style="width: 100px">
				<select class="bootstrap-input"  name="a2">
					<option value="1">秒</option>
					<option value="2">分钟</option>
					<option value="3">小时</option>
					<option value="4">天</option>
				</select>
			</div>
			<div>
				<input class="bootstrap-input" name="type" type="radio" value="2">
				每<span style="color: #0A882B">天</span> <input class="bootstrap-input" name="b1" style="width: 100px"> 点
			</div>
			<div>
				<input class="bootstrap-input" name="type" type="radio" value="3">
				每<span style="color: #0a63b2">周</span> <input class="bootstrap-input" name="c1" style="width: 100px"> 的 <input class="bootstrap-input" name="c2"  style="width: 100px"> 点
			</div>
			<div>
				<input name="type" type="radio" value="4">
				每<span style="color: #a71d5d">月</span> <input class="bootstrap-input" name="d1" style="width: 100px"> 的 <input class="bootstrap-input" name="d2"  style="width: 100px"> 点
			</div>

		</form>
	</div>

</body>
<script type="text/javascript">
var triggerState = {
	NONE:	 {name:"无",detail:"未注册或已被删除"},
	NORMAL:	 {name:"正常",detail:"等待按计划触发任务"},
	PAUSED:	 {name:"暂停",detail:"手动暂停，暂时不会触发任务"},
	COMPLETE:{name:"完成",detail:"已执行完所有调度计划，不再触发任务"},
	ERROR:	 {name:"错误",detail:"配置错误或任务执行过程中抛出未处理的异常"},
	BLOCKED: {name:"阻塞",detail:"线程池资源不足或任务冲突被阻塞"}
}

common.ajaxStop(function(){
	tCrontab.load();
});

setInterval(quary,1000);
var tCrontab = new iTables("#tCrontab",{},{
	baseOption : common.iTableModel("sysCrontab"),
	loadOnInit : false,
	render : {
		cron : function(td,data){
			$(td).append(cronstrue.toString(data.cron, {
				locale: "zh_CN",
				use24HourTimeFormat: true
			})).attr("title",data.klass || "CronAction");
		}
	},
	renderState : function(td,data){
		$(td.parentNode).attr("data-cron",data.id);
		var s = $("<span class='state'></span>");
		$(td).append(s);
	},
	renderEdit : function(td,data){
		var i=$("<i class='layui-icon layui-icon-release' title='立即执行'></i>");
		i.click(function(){
			common.ajax("${base}/json/execCron",{"id":data.id},function(json){
				layer.alert(json.data ? json.data.replace(/\r\n/g,"<br>") : "null");
			});
		});
		var i2=$("<i class='layui-icon layui-icon-run' title='运行' data-v='0'></i>");
		i2.click(function(){
			var dv = $(this).attr("data-v");
			var u = dv == "NORMAL" || dv == "BLOCKED" ? "delCron" : "addCron";
			common.ajax("${base}/json/"+u,{"id":data.id});
		});
		$(td).append(i).append(i2);

	},
	onSelect:function(tr,data){
		fm.window.reload(data.id);
	}
	//,callBack : quaryJob
});

function quary(){
	
	common.ajax("${base}/json/queryCron",{},function(json){
		$("[data-cron]").each(function(){
			var c = $(this).attr("data-cron");
			var v = json.data[c] || "";
			var state = triggerState[v] || {};
			$(this).find(".state").text(state.name || "").attr({
				"title" : state.detail || "",
				"data-state" : v
			});
			$(this).find(".layui-icon-run").attr("data-v",v); 
		});
	});
}

//秒，分，小时，日，月，周几
$("[name='cron']").click(function (){
	var o = $(this).offset();
	layer.open({
		type: 1,
		title : "任务时间",
		content: $(".lay-con"),
		offset: [(o.top+30)+'px', o.left+'px'],
		//area : ["auto","300px"],
		btn : ["确定"],
		yes: function(index, layero){
			var type = f1.type.value;
			var cron = "";
			if(type == 1){
				var v1 = f1.a1.value;
				var v2 = f1.a2.value;
				if(v2 == 1){
					cron = (v1 ? ("0/"+v1) : "*")+" * * * * ?"
				}else if(v2 == 2){
					cron = "0 "+(v1 ? ("0/"+v1) : "*")+" * * * ?"
				}else if(v2 == 3){
					cron = "0 0 "+(v1 ? ("0/"+v1) : "*")+" * * ?"
				}else if(v2 == 4){
					cron = "0 0 0 "+(v1 ? ("1/"+v1) : "*")+" * ?"
				}
			}else if(type == 2){
				var v1 = f1.b1.value;
				cron = "0 0 "+v1+" * * ?"
			}else if(type == 3){
				var v1 = f1.c1.value;
				var v2 = f1.c2.value;
				cron = "0 0 "+(v2 || 0)+" * * "+v1
			}else if(type == 4){
				var v1 = f1.d1.value;
				var v2 = f1.d2.value;
				cron = "0 0 "+(v2 || 0)+" "+v1+" * ?"
			}
			$("[name='cron']").val(cron).focus();
			layer.close(index);
		}
	});

});

</script>
</html>