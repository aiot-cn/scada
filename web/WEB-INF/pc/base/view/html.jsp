<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!doctype html>
<html>
	<head>
		<title>${SRes.getTitle()}</title>
		<meta charset="UTF-8" />

		<script src="${res}/layui/layui.js"></script>
		<script src="${res}/js/common.js"></script>
		<script src="${res}/plugin/echarts.min.js"></script>

		<script src="${res}/plugin/itable/iTables.js"></script>
		<link href="${res}/plugin/itable/itable.css" rel="stylesheet" >

		<link href="${res}/plugin/editor/iframe.css" rel="stylesheet">
		<script src="${res}/js/iTemplate.js"></script>

		<script type="text/javascript">
			var base = '${base}';
			var $ = layui.$;
			layui.config({
				version: false,
				debug: false,
				base: '${res}/layui/plugins/'
			});
		</script>

		<style type="text/css">
			html,body{
				padding: 0;
				margin: 0;
			}

		</style>
	</head>

	<body>
		${SRes.getContent()}
	</body>

	<script type="text/javascript">
		//Vue.prototype.$base = base;
		$("#paper").removeAttr("contenteditable");
		var params = common.urlParams();
		var codeNode = $("code[data-var='dataSource']");
		var itable = {},charts = {};
		var tableOption = {
			loadOnInit : false,
			renderImg : function (td,data,col,val){
				var width = col.th.width || 100;
				return $("<img style='width:100%' src='${base}/image/"+val+"?width="+width+">");
			}
		}

		$('[data-type="iframe"]').each(function (){

		});

		$('table[data-type="iTable"]').each(function (){
			itable[this.id] = new iTables(this,{},tableOption);
		});

		$('[data-type="echarts"]').each(function (){
			charts[this.id] = echarts.init(this);
		});

		$(JSON.parse(codeNode.html() || "[]")).each(function (){
			var _this = this;
			sourceFun(this);
			if(this.period > 0){
				setInterval(function (){
					sourceFun(_this);
				},this.period)
			}
		});

		function sourceFun(sour){
			common.ajax("${base}/"+sour.url,params,function (json){
				var d = json.data || json;
				$("[data-source='"+sour.code+"']").each(function (){
					var v = d[$(this).data("code")];
					if(v == undefined)
						v = "";
					var t = $(this).data("type") || "html";
					if(t == "iframe"){
						var url = v + (v.indexOf("?") ? "&" : "?") + location.search.substring(1);
						$(this).append('<iframe id="iframe2" name="ifrmname" src="${base}/'+url+'" frameborder="0" style="width: 100%; height: 100%;"></iframe>');
					}else if(t == "html")
						this.innerHTML = v;
					else if(t == "attrVal")
						$(this).attr("data-val",v);
					else if(t == "iTable"){
						var it = itable[this.id];
						it._onLoaded(v || d);
					}else if(t == "echarts"){
						charts[this.id].setOption(d);
					}else if(t == "vue"){
						new Vue({
							el: this,
							data:d
						});
					}

				});
			});
		}

	</script>

</html>