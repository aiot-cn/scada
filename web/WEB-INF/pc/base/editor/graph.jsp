<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!--[if IE]><meta http-equiv="X-UA-Compatible" content="IE=5,IE=9" ><![endif]-->
<%--https://jgraph.github.io/mxgraph/javascript/examples/grapheditor/www/index.html--%>
<!doctype html>
<html>
<head>
	<%@include file="../../common/page_head.jsp" %>
	<title>画板-${SRes.getTitle()}</title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
	<meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">
	<link rel="stylesheet" type="text/css" href="${res}/plugin/mxgraph-4.2.2/grapheditor/styles/grapheditor.css">
	<link href="${res}/font/aiotfont/iconfont.css" rel="stylesheet" >

	<style>
		html td.mxPopupMenuItem {
			padding: 2px 30px;
			font-size: 14px;
		}
		.geFormatSection input{
			vertical-align: middle;
		}
		.geSprite.aiot-icon{
			background: initial;
			font-size: 18px;
			text-align: center;
			line-height: 22px;
		}
		.geButton[title="保存"]{
			opacity: 1;
		}
		table.mxPopupMenu td{
			padding: 8px;
		}

		.layui-layer-content input{
			border-width:1px;
		}
		.v-method{
			color: dodgerblue;
		}
		.v-method:before{
			content: ".";
		}
		.v-args{
			margin-top: 3px;
		}
		.v-args-name{
			background-color: #0F93BF;
			color: #fff;
			padding: 1px 3px;
		}
		.v-args-val{
			border: 1px solid #0F93BF;
			padding: 0px 3px;
			color: #666;
		}
		.v-args-val.t-el{
			color: red;
		}
		.v-condition{
			color: #D67B10;
		}
		.v-condition-exc{
			color: red;
		}

		.btn-run{
			color: #06A536;
		}

		.geEditor [data-code]:before {
			content: attr(data-code);
		}
		.data-show-single.geEditor [data-code]:before {
			content : "0";
		}
	</style>

	<script type="text/javascript">
		// Parses URL parameters. Supported parameters are:
		// - lang=xy: Specifies the language of the user interface.
		// - touch=1: Enables a touch-style user interface.
		// - storage=local: Enables HTML5 local storage.
		// - chrome=0: Chromeless mode.
		var urlParams = param;

		// Default resources are included in grapheditor resources
		mxLoadResources = false;

		// Public global variables
		window.MAX_REQUEST_SIZE = window.MAX_REQUEST_SIZE  || 10485760;
		window.MAX_AREA = window.MAX_AREA || 15000 * 15000;

		// URLs for save and export
		window.EXPORT_URL =  '/export';
		window.SAVE_URL =  '/save';
		window.OPEN_URL =  '/open';
		window.OPEN_FORM = 'open.html';

		// Sets the base path, the UI language via URL param and configures the
		// supported languages to avoid 404s. The loading of all core language
		// resources is disabled as all required resources are in grapheditor.
		// properties. Note that in this example the loading of two resource
		// files (the special bundle and the default bundle) is disabled to
		// save a GET request. This requires that all resources be present in
		// each properties file since only one file is loaded.

		window.mxLanguage = window.mxLanguage || urlParams['lang'];
		window.mxLanguages = window.mxLanguages || ['de', 'se'];

		window.mxBasePath = '${res}/plugin/mxgraph-4.2.2';
		window.STYLE_PATH = mxBasePath + '/grapheditor/styles';
		window.RESOURCE_BASE = mxBasePath + '/grapheditor/resources/grapheditor';
		window.RESOURCES_PATH = mxBasePath + '/resources';
		window.STENCIL_PATH = mxBasePath + '/grapheditor/stencils';
		window.IMAGE_PATH = mxBasePath+ '/grapheditor/images';
		window.CSS_PATH = mxBasePath + '/styles';

		var jQuery = $;
	</script>
	<script type="text/javascript" src="${res}/plugin/mxgraph-4.2.2/grapheditor/deflate/pako.min.js"></script>
	<script type="text/javascript" src="${res}/plugin/mxgraph-4.2.2/grapheditor/deflate/base64.js"></script>
	<script type="text/javascript" src="${res}/plugin/mxgraph-4.2.2/grapheditor/jscolor/jscolor.js"></script>
	<script type="text/javascript" src="${res}/plugin/mxgraph-4.2.2/grapheditor/sanitizer/sanitizer.min.js"></script>
	<script type="text/javascript" src="${res}/plugin/mxgraph-4.2.2/mxClient.min.js"></script>
	<script type="text/javascript" src="${res}/plugin/mxgraph-4.2.2/grapheditor/js/EditorUi.js"></script>
	<script type="text/javascript" src="${res}/plugin/mxgraph-4.2.2/grapheditor/js/Editor.js"></script>
	<script type="text/javascript" src="${res}/plugin/mxgraph-4.2.2/grapheditor/js/Sidebar.js"></script>
	<script type="text/javascript" src="${res}/plugin/mxgraph-4.2.2/grapheditor/js/Graph.js"></script>
	<script type="text/javascript" src="${res}/plugin/mxgraph-4.2.2/grapheditor/js/Format.js"></script>
	<script type="text/javascript" src="${res}/plugin/mxgraph-4.2.2/grapheditor/js/Shapes.js"></script>
	<script type="text/javascript" src="${res}/plugin/mxgraph-4.2.2/grapheditor/js/Actions.js"></script>
	<script type="text/javascript" src="${res}/plugin/mxgraph-4.2.2/grapheditor/js/Menus.js"></script>
	<script type="text/javascript" src="${res}/plugin/mxgraph-4.2.2/grapheditor/js/Toolbar.js"></script>
	<script type="text/javascript" src="${res}/plugin/mxgraph-4.2.2/grapheditor/js/Dialogs.js"></script>
	<script type="text/javascript" src="${res}/plugin/mxgraph-4.2.2/iGraph.js"></script>
	<script type="text/javascript" src="${res}/plugin/JSONView/jquery.jsonview.min.js"></script>
	<link href="${res}/plugin/JSONView/jquery.jsonview.css" rel="stylesheet">
	<script src="${res}/js/PinYin.js"></script>

</head>

<body class="geEditor">

<div class="lay-con d-call-data">
	<form name="f1" class="layui-form layui-form-pane" action="">
		<div class="form-item">
			<div class="layui-form-item p-vertex">
				<label class="layui-form-label">类型</label>
				<div class="layui-input-block">
					<select class="layui-input" name="type" lay-ignore="" >
						<option value="0">数值</option>
						<option value="1">状态</option>
						<option value="2">控制</option>
						<option value="3">路径</option>
					</select>
				</div>
			</div>
			<div class="layui-form-item p-vertex">
				<label class="layui-form-label">数据源</label>
				<div class="layui-input-block">
					<select class="layui-input" name="dataSource" lay-ignore="" ></select>
				</div>
			</div>
			<div class="layui-form-item p-vertex">
				<label class="layui-form-label">CODE</label>
				<div class="layui-input-block">
					<input class="layui-input" name="code">
				</div>
			</div>

		</div>
		<datalist id="L1">
			<option value="Exception">异常</option>
		</datalist>
	</form>
</div>
<textarea id="content">${SRes.getContent()}</textarea>
<div class="lay-con d-data-source">
	<table id="tDataSouce" class="layui-table" >
		<thead>
		<tr>
			<th data-type="rownum" width="20">No</th>
			<th data-field="code" width="70">CODE</th>
			<th data-field="url">URL</th>
			<th data-field="period" width="50">周期</th>
			<th data-type="edit" class="tac" data-class="tac" width="40">操作</th>
		</tr>
		</thead>
	</table>
</div>
<form data-for="tDataSouce">
	<input type="text" class="layui-input" name="code" required="required">
	<input type="text" class="layui-input" name="url" required="required">
	<input type="number" class="layui-input" name="period">
</form>
</body>
<script type="text/javascript">
	var tTemplate;
	// Extends EditorUi to update I/O action states based on availability of backend
	var mxEditor,editorUi,graph;
	var editorUiInit = EditorUi.prototype.init;
	EditorUi.prototype.init = function() {
		editorUiInit.apply(this, arguments);
		this.actions.get('export').setEnabled(false);
	};


	Graph.prototype.convertValueToString = function (cell) {
		var value = this.model.getValue(cell);

		if (value == null || typeof (value) != 'object')
			return mxGraph.prototype.convertValueToString.apply(this, arguments);

		var result = null;

		if (this.isReplacePlaceholders(cell) && cell.getAttribute('placeholder') != null) {
			var name = cell.getAttribute('placeholder');
			var current = cell;

			while (result == null && current != null) {
				if (current.value != null && typeof (current.value) == 'object') {
					result = current.getAttribute(name);
				}

				current = this.model.getParent(current);
			}
		} else {

			if (Graph.translateDiagram && Graph.diagramLanguage != null) {
				result = value.getAttribute('label_' + Graph.diagramLanguage);
			}

			if (result == null) {
				result = value.getAttribute('label') || '';
			}
		}

		var data = getCellData(cell);

		if(data.code){
			return dataToSpan(data);
		}

		return result || '';
	};

	function dataToSpan(data){
		return "<span data-type='"+data.type+"' data-source='"+(data.dataSource || '')+"' data-code='"+data.code+"'></span>";
	}

	//悬停标题
	Graph.prototype.getTooltipForCell = function(cell) {
		return "";
	}

	//右面板添加设置项目
	DiagramFormatPanel.prototype.addStation = function (div) {
		$("<input type='checkbox'>").appendTo(div).change(function (){
			$(document.body).toggleClass("data-show-single");
		});
		$(div).append("<span> 单字符显示</span>")
		return div;
	};

	StyleFormatPanel.prototype.addStation = function (div) {
		var b = graph.getSelectionCells()[0];
		if(!b)
			return;

		var a = $("<div><input type='checkbox'><span> 吸附</span></div>").appendTo(div).find("input")
				.change(function (){
					graph.setCellStyles("connectable",this.checked?1:0, graph.getSelectionCells());
				}).attr("checked",b.style.indexOf("connectable=0") <= -1);

		return div;
	};

	// Adds required resources (disables loading of fallback properties, this can only
	// be used if we know that all keys are defined in the language specific file)
	var bundle = mxResources.getDefaultBundle(RESOURCE_BASE, mxLanguage) || mxResources.getSpecialBundle(RESOURCE_BASE, mxLanguage);
	mxUtils.getAll([bundle, STYLE_PATH + '/default.xml'], function(xhr) {
		// Adds bundle text to resources
		mxResources.parse(xhr[0].getText());

		// Configures the default graph theme
		var themes = {};
		themes[Graph.prototype.defaultThemeName] = xhr[1].getDocumentElement();

		// Main
		mxEditor = new Editor(urlParams['chrome'] == '0', themes);
		graph = mxEditor.graph;
		editorUi = new EditorUi(mxEditor);
		//排除要移除的属性
		html4.ATTRIBS['span::data-type'] = 0;
		html4.ATTRIBS['span::data-source'] = 0;
		html4.ATTRIBS['span::data-code'] = 0;

		editorUi.actions.addAction("runArgs",function (){

		});
		editorUi.toolbar.addButton("btn-save aiot-icon aiot-icon-data-source","数据源",function (){
			layer.open({
				type: 1,
				title : "数据源",
				btn : ["确定"],
				content: $(".d-data-source"),
				area : ["700px","400px"],
				yes : function (index){
					var j = JSON.stringify(tDataSouce.getDataList());
					console.log(j);
					layer.close(index);
				}
			});
		});
		editorUi.toolbar.addButton("btn-save aiot-icon aiot-icon-save","保存",function (){
			//mxEditor.getGraphXml().outerHTML 这个值不对;
			var graphXml = mxEditor.getGraphXml();
			var datasource = graphXml.ownerDocument.createElement("datasource");
			datasource.textContent = JSON.stringify(tDataSouce.getDataList());
			graphXml.appendChild(datasource);
			common.jsonCont("saveRes",{"url":"${SRes.url}","content":mxUtils.getPrettyXml(graphXml)});
		});
		mxEditor.graph.addListener(mxEvent.CLICK, function(sender, evt) {
			var e = evt.properties.event;
			if(e.altKey){
				var cell = evt.properties.cell;
				editorUi.showDataDialog(cell);
			}
		});

		var content = document.getElementById("content").value;
		//content += "<dataSource>fdnifdfd</dataSource>"
		if(content){
			var doc = mxUtils.parseXml(content).documentElement;
			var dataSource = doc.getElementsByTagName("datasource")[0];
			if(dataSource){
				tDataSouce._onLoaded(JSON.parse(dataSource.textContent));
				doc.removeChild(dataSource);
			}
			mxEditor.setGraphXml(doc);
		}
		initEditData();
	}, function() {

	});

	var tDataSouce = new iTables("#tDataSouce",{},{
		primaryKey : "code",
		create : true,
		edit : true,
		remove : true,
		inline_edit : true,
		modeCommit : true,
		beforeStats : function (json){
			common.renderSelect('[name="dataSource"]',this.getDataList(),{dft:"",value:"code",name:"code"});
		}
	});



	/*common.jsonModel("tTemplate",{"id":param.id},function (json){
		tTemplate = json.list[0];
		var ds = JSON.parse(tTemplate.dataSource || "[]");
		common.renderSelect(f1.dataSource,ds,{dft:"",value:"code"})
	});*/

	//当前选中
	//var cell = graph.getSelectionCell() || graph.getModel().getRoot();
	//var objNode = graph.getModel().getValue(cell);
	//var pathId = objNode.getAttribute("pathId");

	/**
	 * var parent = graph.getDefaultParent();
	 graph.getModel().beginUpdate();
	 try {
  var v1 = graph.insertVertex(parent, null, 'Node1', 20, 20, 80, 30, 'blink'); // 设置节点样式为"blink"
} finally {
  graph.getModel().endUpdate();
}
	 mxEditor.graph.view.getState(a);//得到位置信息
	 */

	var EditDataDialog = function (ui, cell) {
		f1.reset();
		var value = cell.value;
		if (!mxUtils.isNode(value)) {
			var doc = mxUtils.createXmlDocument();
			value = doc.createElement('object');
		}

		$(f1).find(":input").each(function (){
			this.placeholder = "";
			this.value = value.getAttribute(this.name) || "";
			$(this).change();
		});
		/*if (cell.edge){
			$(".p-edge").show();
			$(".p-vertex").hide();
		}else{
			$(".p-edge").hide();
			$(".p-vertex").show();
		}*/

		layer.open({
			type : 1,
			title : "编辑数据 - "+cell.id,
			btn : ["确定","取消"],
			area : ["450px","auto"],
			content : $(".d-call-data"),
			yes : function (index){
				$(f1).find(":input").each(function (){
					if(this.value)
						value.setAttribute(this.name,this.value);
					else
						value.removeAttribute(this.name);
				});
				//value.setAttribute('label', dataToSpan(getCellData(cell)));
				var graph = ui.editor.graph;
				graph.model.setValue(cell,value);
				layer.close(index);
			}
		});
	}

	function initEditData(){

		$(f1.deviceId).change(function(){

		});
	}

	function test2(color){
		//获取cell
		graph.model.cells;
		//设置颜色
		graph.setCellStyles(mxConstants.STYLE_STROKECOLOR,color, graph.getSelectionCells());

		//graph.setCellStyle('shape=cloud;fillColor=Gray', [v1,v2]);
		//mxConstants.STYLE_STROKECOLOR
	}




</script>
</html>