<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!--[if IE]><meta http-equiv="X-UA-Compatible" content="IE=5,IE=9" ><![endif]-->
<!doctype html>
<html>
<head>
	<title>${SRes.title}</title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
	<meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">

	<c:import url="../../common/page_head.jsp"></c:import>
	<script type="text/javascript">
		var urlParams = param;
		window.mxBasePath = '${res}/plugin/mxgraph-4.2.2';
		window.STENCIL_PATH = mxBasePath + '/grapheditor/stencils';
	</script>

	<script type="text/javascript" src="${res}/plugin/mxgraph-4.2.2/mxClient.min.js"></script>
	<script type="text/javascript" src="${res}/plugin/mxgraph-4.2.2/grapheditor/js/Graph.js"></script>
	<script type="text/javascript" src="${res}/plugin/mxgraph-4.2.2/grapheditor/js/Shapes.js"></script>
	<script type="text/javascript" src="${res}/plugin/mxgraph-4.2.2/iGraph.js"></script>

	<style>
		.mxgraph{
			overflow:hidden;
			position:absolute;top:0;left: 0;bottom: 0;right: 0;
		}
		.msg{
			padding: 10px;
			font-size: 20px;
		}
	</style>
</head>

<body class="geEditor">
<div class="msg"></div>
<div class="mxgraph"></div>
<div class="lay-con d-remote-control">
	<form name="f1" class="layui-form layui-form-pane form-label-wider" action="">
		<div class="form-item">
			<div class="layui-input-block" style="margin-left: 5px">
				<select lay-ignore="" class="layui-input" name="remoteControl">
					<option value="">自动</option>
					<option value="1">ON</option>
					<option value="0">OFF</option>
				</select>
			</div>
		</div>

	</form>
</div>
<textarea id="content" style="display: none">${SRes.getContent()}</textarea>
</body>
<script type="text/javascript">
	//var graph = new mxGraph(container);
	var editor = new mxEditor();
	var graph = editor.graph;
	var posCell;//定位

	var styleXmlNode,xmlStr;
	var container = $(".mxgraph")[0];
	editor.setGraphContainer(container);
	var pathCell = [],devIds =[];
	var dataSourceList = [];

	initGraph();
	common.ajax('${res}/plugin/mxgraph-4.2.2/grapheditor/styles/default.xml',{},function (xml){
		styleXmlNode = xml.documentElement;
	});

	//mxStencilRegistry.loadStencilSet(STENCIL_PATH + '/flowchart.xml', null);


	common.ajaxStop(function (){
		var dec = new mxCodec(styleXmlNode.ownerDocument);
		dec.decode(styleXmlNode, graph.getStylesheet());
		var content = document.getElementById("content").value;
		loadXmlStr(content);
		pathCell = findByObj(function (obj){
			var deviceId = obj.getAttribute("deviceId");
			if(deviceId != null)
				devIds.push(deviceId);
			return obj.getAttribute("pathId") != null;
		});
		$(dataSourceList).each(function (){
			var _this = this;
			sourceFun(this);
			if(this.period > 0){
				setInterval(function (){
					sourceFun(_this);
				},this.period)
			}
		});
		if(parent.fmCallBack)
			parent.fmCallBack();
	});

	function loadXmlStr(xml) {
		var xmlDocument = mxUtils.parseXml(xml);
		var doc = xmlDocument.documentElement;
		if (doc == null || doc.nodeName != 'mxGraphModel')
			return;
		var dataSource = doc.getElementsByTagName("datasource")[0];
		if(dataSource){
			dataSourceList = JSON.parse(dataSource.textContent);
			doc.removeChild(dataSource);
		}
		var decoder = new mxCodec(xmlDocument);
		decoder.decode(doc, graph.getModel());
		posCell = findCell("variable","pathPoint");
		fitPage();
	}



	function fitPage(){
		//var fmt = graph.pageFormat;
		//var ps = graph.pageScale;
		//var cw = graph.container.clientWidth;
		if(param.fitCenter != 0){
			graph.fit();//自适应
			graph.center(true,true,0.5,0.5);//将画布放到容器中间
		}

		if(param.zoomi != undefined){
			var z = parseInt(param.zoomi);
			for(var i = 0;i<Math.abs(z);i++)
				z > 0 ? graph.zoomIn() : graph.zoomOut();
		}
		//graph.zoomTo(1);
		//var sc = graph.getView().getScale();//获取当前的缩放比例
	}

	function sourceFun(sour){
		common.ajax("${base}"+sour.url,param,function (json){
			var d = json.data || json;
			$("[data-source='"+sour.code+"']").each(function (){
				var v = d[this.dataset.code];
				if(v == undefined)
					v = "";
				var t = this.dataset.type;
				if(t == 0){
					this.innerHTML = v;
				}
				if(t == 1)
					$(this).attr("data-val",v);

			});
		});
	}

	/**
	 * 创建连接线
	 * mxGraph.insertEdge(parent, id, value, source, target, style);
	 *
	 */

	function addPos(pathId,percent){
		if(posCell)
			graph.removeCells([posCell]);
		if(pathId == undefined)
			return;
		var cell = findCell("pathId",pathId);
		if(!cell)
			return;
		var sc = graph.getView().getScale();//当前缩放
		var tr = graph.view.getTranslate();
		var mxCellState = graph.view.getState(cell);
		var points = mxCellState.absolutePoints;
		var x = (points[1].x/sc - points[0].x/sc) * percent + points[0].x/sc;
		var y = (points[1].y/sc - points[0].y/sc) * percent + points[0].y/sc;
		/**
		 * add(parent, child, index)
		 remove(cell)
		 setCollapsed(cell, collapsed)
		 setGeometry(cell, geometry)
		 setRoot(root)
		 setStyle(cell, style)
		 setTerminal(cell, terminal, isSource)
		 setTerminals(edge,source,target)
		 setValue(cell, value)
		 setVisible(cell, visible)
		 var bound = new mxRectangle(x,y,w,h);
		 graph.resizeCell(cell,bound);
		 */
		var parent = graph.getDefaultParent();
		graph.getModel().beginUpdate();
		try {
			/**
			 * parent，id，value，x，y，width，height，style
			 * id 标识为空，则由模型管理维护
			 * style - 要应用于此顶点的样式描述。在简单的级别，该参数是遵循特定格式的字符串。在字符串中显示零个或多个样式名称和一些覆盖全局样式或设置新样式的键/值对。在我们创建自定义样式之前，我们将使用当前可用的样式。
			 * 使用 mxGraph.insertVertex 会创建一个 relative 为 false 的节点。如果你要将一个节点添加到另一个节点中需要在该方法调用的第9个参数传入 true，将 relative 设置为 true。这时子节点使用相对坐标系，以父节点左上角作为基点，x、y 取值范围都是 [-1,1]。如 C节点 相对 B节点定位。
			 * graph.setCellStyles(mxConstants.STYLE_FILLCOLOR, "#CCCCCC", [cell]);
			 * graph.insertVertex(cell, null, '', 20, 20, 10, 10, 'stylename;image=data:image/svg+xml;base64,');
			 */
			posCell = graph.insertVertex(parent, null, '', x-tr.x-5, y-tr.y-5, 10, 10, 'ellipse;whiteSpace=wrap;html=1;aspect=fixed;shadow=0;sketch=0;fillStyle=auto;gradientDirection=east;strokeColor=none;fillColor=#FFE6D9;gradientColor=#FF0000;');
			//posCell = graph.insertVertex(cell, null, '',(percent*2)-1,(percent*2)-1, 10, 10, 'fillColor=red;strokeColor=red;rounded=1',true);
			/*var geo = graph.getCellGeometry(posCell);
			geo = geo.clone();
			graph.getModel().setGeometry(mxCell, geo);*/
			//geo.width = activeStep.activity.name.length * 20 + 50 + 20;
			//graph.getModel().setGeometry(mxCell, geo);

			//var p = graph.view.getState(cell);
			//p.absolutePoints[0].x = p.absolutePoints[0].x -5;
			//p.absolutePoints[0].y = p.absolutePoints[0].y -5;
			//graph.view.updateCellState(p);
			//graph.updateCellSize(posCell,true);只改变大小

		} finally {
			graph.getModel().endUpdate();
		}

	}

	function initGraph(){
		graph.setHtmlLabels(true);
		//graph.centerZoom = false;
		graph.setTooltips(false);
		graph.setEnabled(false);

		graph.convertValueToString = function (cell) {

			//mxUtils.isNode(cell.value)
			if (typeof(cell.value) == 'object') {
				var type = cell.getAttribute('type', '');
				var dataSource = cell.getAttribute('dataSource', '');
				var code = cell.getAttribute('code', '');
				if(code){
					return "<span data-type='"+type+"' data-source='"+(dataSource || '')+"' data-code='"+code+"'></span>";
				}else{
					return cell.value.getAttribute('label');
				}

			}
			return cell.value || "";

		};

		// Enables panning with left mouse button
		graph.panningHandler.useLeftButtonForPanning = true;
		graph.panningHandler.ignoreCell = true;
		//graph.container.style.cursor = 'move';
		graph.setPanning(true);
		//graph.resizeContainer = true;//自动大小
		//graph.border = 20;

		graph.resizeContainer = false;
		graph.getBorderSizes();

		graph.addListener(mxEvent.DOUBLE_CLICK, function(sender, evt) {
			var cell = evt.getProperty('cell');
			var d = getCellData(cell);
			if(d.field){
				var devType = devMap[d.deviceId].deviceType;
				var prop = devPropMap[devType][d.field];
				if(prop.type == 2){
					layer.open({
						type: 1,
						title : "遥控",
						content : $(".d-remote-control"),
						area : ["300px","auto"],
						btn : ["确定"],
						yes : function (index){
							layer.close(index);
							common.jsonCont("setDevToVal",{"deviceId":d.deviceId,"analysis":d.field,"value":f1.remoteControl.value})
						}
					});
				}
			}

		});

	}

	/*graph.container.addEventListener('wheel', function(event) {

		event.preventDefault();
		// 获取鼠标位置
		var rect = container.getBoundingClientRect();
		var mouseX = event.clientX - rect.left;
		var mouseY = event.clientY - rect.top;

		// 将鼠标位置转换为图形坐标
		var graphPoint = graph.convertPoint(new mxPoint(mouseX, mouseY));

		// 计算缩放比例
		var scale = graph.getView().getScale();
		var zoomFactor = event.deltaY < 0 ? 1.1 : 0.9;

		// 设置缩放中心
		graph.getView().setTranslate(new mxPoint(graphPoint.x / scale - graph.getView().getTranslate().x * (1 / scale - zoomFactor),
				graphPoint.y / scale - graph.getView().getTranslate().y * (1 / scale - zoomFactor)));

		// 应用缩放
		graph.getView().setScale(scale * zoomFactor);
	});*/

	$(document).on("mousewheel DOMMouseScroll", function (e) {
		e.preventDefault();
		var delta = e.originalEvent.wheelDelta;  // chrome & ie
		//e.originalEvent.detail             // firefox
		delta > 0 ? graph.zoomIn() : graph.zoomOut();
	});

	function findCell(attr,val){
		var cells = graph.getModel().cells;
		for(var k in cells){
			var cell = cells[k];
			var v = cell.value;
			if(v && v.nodeName == "object" && v.getAttribute(attr) == val)
				return cell;
		}
	}

	function findByObj(fun){
		var a = [];
		var cells = graph.getModel().cells;
		for(var k in cells){
			var cell = cells[k];
			var v = cell.value;
			if(v && v.nodeName == "object"){
				if(fun(v))
					a.push(cell);
			}
		}
		return a;
	}

	//var style = graph.stylesheet.getDefaultVertexStyle(); 得到默认样式 https://blog.csdn.net/u012124304/article/details/106857815
	//style[mxConstants.STYLE_SHAPE] = mxConstants.SHAPE_ELLIPSE; // 将顶点的默认形状改为椭圆形
	//graph.setCellStyle('shape=cloud;fillColor=Gray', [v1,v2])//更新一组节点的样式
	//graph.setCellStyles(mxConstants.STYLE_FONTCOLOR,'#FF0000');
	var devData = {};
	function updateValue(){
		graph.getModel().beginUpdate();
		try {
			//var cell = graph.getSelectionCell();
			var cells = graph.getModel().cells;
			for(var k in cells){
				var cell = cells[k];
				var v = cell.value;
				if(v && v.nodeName == "object"){
					var field = v.getAttribute("field");
					if(field){
						var devId = v.getAttribute("deviceId");
						/*var devType = devMap[d.deviceId].deviceType;
						var prop = devPropMap[devType][d.field];*/
						var d0 = devData[devId];
						if(d0){
							var d = d0.dataMap[field];
							if(d){
								v.setAttribute("label",d.value || "");
								var c = ["none","#F5D808","red"];
								setCellStyle(cell,{"fontColor":c[d.state] || "none"});
							}

						}

					}

				}

			}
			graph.refresh();

		} finally {
			graph.getModel().endUpdate();
		}
	}

	function setCellStyle(cell,style){
		var s = cell.style;
		for(var k in style){
			var s2 = k + "="+style[k]+";"
			if(s.indexOf(k) == -1){
				s += s2;
			}else {
				s = s.replace(new RegExp(k+"=\\S+;"),s2);
			}
		}
		graph.getModel().setStyle(cell,s);
	}

	//在路径上以百分比添加点
	function addPathPoint(pathId,targetUn){
		var cell = findCell("pathId",pathId);
		if(!cell)
			return ;
		var g = cell.geometry;
		var points =  [g.sourcePoint];
		if(g.points)
			points = points.concat(g.points);
		points.push(g.targetPoint);
		var length = 0;
		var lengthArr = [];
		for(var i=0;i<points.length-1;i++){
			var dx = points[i + 1].x - points[i].x;
			var dy = points[i + 1].y - points[i].y;
			var xy = Math.sqrt(dx * dx + dy * dy);
			lengthArr.push(xy);
			length += xy;
		}
		targetUn = targetUn > 0.999 ? 0.999 : targetUn;
		for(var i=0;i<lengthArr.length;i++){
			var a = lengthArr[i]/length;
			if(targetUn > a){
				targetUn -= a;
			}else{
				var b = targetUn / a; //偏移百分比
				var px = points[i].x;
				var py = points[i].y;
				var tx = px + (points[i + 1].x - px) * b;
				var ty = py + (points[i + 1].y - py) * b;
				if(posCell){
					var g = posCell.geometry;
					g.x = tx - g.width/2;
					g.y = ty - g.height/2;
					return graph.addCell(posCell);
				}
				return graph.insertVertex(graph.getDefaultParent(), null, '', tx-5, ty-5, 10, 10, 'ellipse;whiteSpace=wrap;html=1;aspect=fixed;shadow=0;sketch=0;fillStyle=auto;gradientDirection=east;strokeColor=none;fillColor=#FFE6D9;gradientColor=#FF0000;');
			}
		}
	}

	// 二次贝塞尔曲线点的计算函数
	function quadraticBezierPoint(x0, y0, cx, cy, x2, y2, t) {
		return {
			x: Math.pow(1 - t, 2) * x0 + 2 * (1 - t) * t * cx + Math.pow(t, 2) * x2,
			y: Math.pow(1 - t, 2) * y0 + 2 * (1 - t) * t * cy + Math.pow(t, 2) * y2
		};
	}

	// 二次贝塞尔曲线长度估算函数
	function bezierLength(x0, y0, x1, y1, x2, y2, segments) {
		segments = segments || 1000; // 默认将曲线分成1000段
		var length = 0;
		for (var i = 0; i < segments - 1; i++) {
			var t0 = i / segments;
			var t1 = (i + 1) / segments;
			var p0 = quadraticBezierPoint(x0, y0, x1, y1, x2, y2,t0);
			var p1 = quadraticBezierPoint(x0, y0, x1, y1, x2, y2,t1);
			// 使用欧几里得距离公式计算两点间的距离
			var dx = p1.x - p0.x;
			var dy = p1.y - p0.y;
			length += Math.sqrt(dx * dx + dy * dy);
		}
		return length;
	}



</script>
</html>