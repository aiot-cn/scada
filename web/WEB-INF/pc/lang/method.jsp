<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!doctype html>
<html>
	<head>
		<title>方法</title>
		<c:import url="../common/page_head.jsp"></c:import>
		<script type="text/javascript">
			var jQuery = $;
		</script>
		<script type="text/javascript" src="${res}/plugin/JSONView/jquery.jsonview.min.js"></script>
		<link href="${res}/plugin/JSONView/jquery.jsonview.css" rel="stylesheet">
		<script src="${res}/js/PinYin.js"></script>

		<style type="text/css">
			a.var-zero{
				color: #3B5EE0;;
			}
			[data-type='command']{
				color: #db6813;
			}
			[data-type='workflow']{
				color: #a40cb5;
				cursor: pointer;
			}
		</style>
	</head>
	
	<body>
		<div class="layui-fluid">
			<table id="tMethod" class="layui-table" style="table-layout:fixed;margin: 0">
				<thead>
				<tr>
					<th data-type="rownum" width="20">No</th>
					<th data-field="name">名称</th>
					<th data-field="code" style="padding: 0 20px 0 0;">
						<input data-search="tMethod" placeholder="搜索" class="layui-input" style="height: 32px;" onclick="event.stopPropagation();">
					</th>
					<th data-field="returnType" width="100" data-class="to">返回</th>
				</tr>
				</thead>
			</table>
		</div>

		<div class="lay-con pre-code"></div>
	</body>


<script type="text/javascript">
var cid = param.cid;
var deviceType = param.deviceType;
var $view = $(".pre-code");

var tMethod = new iTables("#tMethod",{"deviceType":deviceType},{
	getController  : "${base}/json/getDevTypeMethods",
	getDataCallback : function(json){
		json.sort(function (a,b){
			var c = b.type.localeCompare(a.type);
			return c == 0 ? a.name.localeCompare(b.name) : c;
		});
	},
	render : {
		name : function(td,data,icolumn,text){
			var s = $("<span data-type='"+data.type+"'>"+text+"</span>");
			if(data.type == "workflow"){
				s.click(function (){
					top.layer.open({
						type: 2,
						title: "工作流-"+text + "：" + data.returnType,
						area: ['80%', '80%'],
						content : "${base}/base/workflowEditor?plass=model.table.Device&pid="+cid+"&code="+data.code
					});
				});
			}
			td.parentNode.searchIndex = PinYin.get(text) + text + data.code + data.returnType;
			return s;
		},
		code : function (td,data,icolumn,text) {
			var n = $("<a>"+text+"</a>").appendTo(td);
			if(data.arg.length == 0){
				n.addClass("var-zero");
				n.click(function () {
					msg(data.code);
				});
			}else{
				n.click(function () {
					layer.open({
						type: 2,
						title: data.name + " - 参数",
						btn : ["执行"],
						content:"${base}/lang/methodArg?deviceType="+deviceType+"&method="+encodeURIComponent(data.code),
						area : ["400px","auto"],
						yes : function(index,layero){
							var body = layer.getChildFrame('body', index);
							var iframeWin = window[layero.find('iframe')[0]['name']];
							msg(data.code,common.formJSON(iframeWin.fm,null,false,true));
						}
					});
				});
			}


			return n;
		},
		returnType : function (td,data,icolumn,text) {
			td.title = text;
			var t = text.split(".");
			return t[t.length-1];
		}
	}
});


function msg(method,data){
	common.devExec(cid,method,data,function(json){
		if(json.data){
			try {
				$view.JSONView(json.data,{});
			}catch (e){
				$view.empty().text(json.data);
			}
			layer.open({
				type: 1,
				content:$view,
				area:["80%","80%"]
			});
		}else{
			layer.msg("返回值：null");
		}

	});
}


</script>

</html>