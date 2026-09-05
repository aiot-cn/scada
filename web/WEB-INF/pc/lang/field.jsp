<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>

<!doctype html>
<html>
	<head>
	<%@include file="../common/page_head.jsp" %>
	<title>字段值</title>
		
<style type="text/css">
	td[data-code]{
		word-break: break-all;
	}
</style>
</head>
<body>

<div class="layui-fluid">
	<table class="layui-table" style="margin: 0">
		<colgroup>
			<col width="140">
		</colgroup>
		<tbody id="tbody">
			<%--<tr><td>a</td><td>b</td></tr>--%>
		</tbody>
	</table>

</div>

</body>
<script type="text/javascript">
var type = param.type || 0;
var cid = param.cid;
var klass = param.klass;

var temp = $(".temp");
common.jsonCont("getAoReflect",{"klass" : klass,"deviceType":param.deviceType},function(json){
	$(json).each(function(){
		if((this.type == "auto" || this.type == "analysis")  && !this.param && (this.klass.indexOf("java.lang")==0 || this.klass.indexOf(".") == -1)){
			$("#tbody").append("<tr>" +
					"<td title='"+this.code+"'>"+(this.name||this.code)+"</td>" +
					"<td title='"+this.placeholder+"' data-code='"+this.code+"'></td>" +
					"</tr>")
		}
	});
	load();
	setInterval(load,2000);
});

function load(){
	var t = {1:"device",2:"service",3:"cronParam"};
	common.ajax("${base}/json/"+t[type],{id:cid},function(json){
		$("[data-code]").each(function(){
			var v = json[$(this).data("code")];
			this.innerText = v == undefined ? "" : v;
		});
	});
}


</script>
</html>