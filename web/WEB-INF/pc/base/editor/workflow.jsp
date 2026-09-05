<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<!doctype html>
<html>
<head>
	<meta charset="UTF-8" />
	<title>工作流</title>
	<c:import url="../../common/page_head.jsp"></c:import>
	<script src="${res}/plugin/jsplumb.min-2.15.6.js"></script>
	<script src="${res}/js/PinYin.js"></script>
	<link href="${res}/font/aiotfont/iconfont.css" rel="stylesheet">
	<link rel="stylesheet" href="${res}/plugin/codemirror-5.65.18/lib/codemirror.css">
	<link rel="stylesheet" href="${res}/plugin/codemirror-5.65.18/theme/eclipse.css">
	<script src="${res}/plugin/codemirror-5.65.18/lib/codemirror.js"></script>
	<script src="${res}/plugin/codemirror-5.65.18/mode/css/css.js"></script>
	<script src="${res}/plugin/codemirror-5.65.18/mode/javascript/javascript.js"></script>
	<script src="${res}/plugin/codemirror-5.65.18/mode/xml/xml.js"></script>

	<link rel="stylesheet" href="${res}/plugin/codemirror-5.65.18/addon/hint/show-hint.css">
	<script src="${res}/plugin/codemirror-5.65.18/addon/hint/show-hint.js"></script>
	<script src="${res}/plugin/codemirror-5.65.18/addon/hint/javascript-hint.js"></script>

	<script type="text/javascript">
		var jQuery = $;
	</script>
	<script type="text/javascript" src="${res}/plugin/JSONView/jquery.jsonview.min.js"></script>
	<link href="${res}/plugin/JSONView/jquery.jsonview.css" rel="stylesheet">
	<style>
		html{
			background-color: #f2f4f7;
		}
		body{
			font-size: 12px;
			user-select: none;
		}
		input::placeholder{
			color: #b6b7b4;
		}
		#d-tool{
			position: absolute;
			padding: 10px;
			z-index: 1000;
			background: #f2f4f7;
		}
		.d-bindings{
			position: absolute;
			top: 30px;
			bottom: 0;
			width: 460px;
			z-index: 100;
			overflow: auto;
			background: rgba(255,255,255,0.8);
		}
		#d-tool span{
			font-size: 12px;
		}

		#diagramContainer{
			position: absolute;
			overflow: auto;
			top: 0;
			left: 0;
			right: 0;
			bottom: 0;
			background-image: repeating-linear-gradient(to right,  #ecedf3 0px, #ecedf3 1px, transparent 1px, transparent 60px),
							  repeating-linear-gradient(to bottom, #ecedf3 0px, #ecedf3 1px, transparent 1px, transparent 60px);
		}
		#selectBox{
			position: absolute;
			display: none;
			background-color: rgb(193,186,249,0.5);
			border: 1px solid #a7a0f9;
			pointer-events: none;
			z-index: 10;
		}
		.item{
			min-width: 200px;
			border: 1px solid #c7d2d9;
			position: absolute;
			border-radius: 5px;
			cursor: default;
		}

		.item[data-width] .t-dev{
			table-layout: fixed;
			width: 100%;
		}
		.t-dev{

		}
		.t-dev thead{
			background: linear-gradient(to bottom, #ffffff 0%, #e7f0f5 100%);
		}
		.t-dev th{
			font-weight: normal;
			text-align: left;
		}
		.t-dev th input{
			color: #333;
			border: none;
			background-color: rgba(0,0,0,0);
			padding: 6px 10px;
			width: 100%;
			font-weight: bold;
		}
		.t-dev tbody input,
		.t-dev tfoot input{
			color: #333;
			border: none;
			width: 100%;
			background-color: rgba(0,0,0,0);
		}

		.t-dev td{
			overflow: hidden;
		}

		.t-dev tbody td{
			padding:2px 10px;
			color: #444;
			font-family: "Courier New", serif;
			background-color: #fff;
		}
		.t-dev tbody tr:first-child td{
			padding-top: 4px;
		}
		.t-dev tbody tr:last-child td{
			padding-bottom: 4px;
		}

		.t-dev tfoot{
			background: #dce7f1;
		}
		.t-dev tfoot td{
			padding: 4px 6px;
		}
		.t-dev tfoot i{
			cursor: pointer;
			color: #9dabb5;
			font-size: 14px;
			margin-right: 5px;
		}
		td > textarea{
			display: none;
		}
		.text-con{
			padding: 0 !important;
		}

		.item[data-is-start='1']{
			border: 1px solid #6fb3e3;
		}
		[data-is-start='1'] .t-dev thead input{
			color: #fff;
		}
		[data-is-start='1'] .t-dev thead {
			background: linear-gradient(to bottom, #96d2f7 0%, #3888c7 100%);
		}
		.item[data-is-start='1'] .layui-icon-flag{
			color: #0a76be;
			font-weight: bold;
		}

		.layui-form-autocomplete dl dd{
			line-height: 24px !important;
		}
		.conn-label{
			cursor: pointer;
		}
		.conn-label:before{
			content: attr(data-priority);
			border-radius: 5px;
			background: #d7773f;
			color: #fff;
			padding: 0 3px;
			margin-right: 2px;
		}
		.conn-label[data-priority='0']:before{
			content: '';
			padding:0;
		}

		.executed path{
			stroke: #3890e5;
		}

		.active.item{
			border : 1px solid #468ee3;
		}
		.active path{
			stroke:#468ee3;
		}
		.CodeMirror{
			font-size: 14px;
			height: 100%;
			font-family: "Courier New", serif;
		}
		[data-width]{
			width: 300px;
		}
		[data-height] > table{
			height: 100%;
		}
		.rb-resize{
			position: absolute;
			right: -5px;
			bottom: -5px;
			cursor: se-resize;
			width: 10px;
			height: 10px;
		}
		.item-move:hover{
			cursor: move;
		}
		#tWorkflowBindings th{
			border-top:none;
		}
		#tWorkflowBindings tr td:nth-child(2){
			word-break: break-all;
		}
		.bind-val{
			color: #555;
		}
		.bind-obj{
			color: #007acc;
			cursor: pointer;
		}
		.monitor-span.active{
			border-color: #4e7edd;
		}
		.t-dev .is-string{
			color: #07a90e;
		}
		.t-dev .is-return{
			color: #a208bb;
		}
		#boundRightBottom{
			position: absolute;
		}
		.val-more{
			float: right;
			cursor: pointer;
		}
		.val-more:hover{
			color: #0d7cdf;
		}
		.scroll-wrapper { -webkit-overflow-scrolling: touch;overflow-y: scroll;width: 100%; height: 100%;}
	</style>
</head>
<body>
<div id="d-tool">
	<span class="layui-btn  layui-btn-xs layui-btn-primary layui-icon layui-icon-add-1" onclick="addItem()" title="快捷键（I）"> 添加</span>
	<span class="layui-btn  layui-btn-xs layui-btn-primary layui-icon layui-icon-fonts-code"> 代码</span>
	<span class="layui-btn  layui-btn-xs layui-btn-primary layui-icon layui-icon-senior"> 参数</span>
	<span class="layui-btn  layui-btn-xs layui-btn-primary aiot-icon aiot-icon-save" onclick="save()" title="快捷键（S）"> 保存</span>

	<span class="layui-btn  layui-btn-xs layui-btn-primary aiot-icon aiot-icon-run" onclick="run()"> 运行</span>
	<span class="layui-btn  layui-btn-xs layui-btn-primary monitor-span"><input type="checkbox" style="height: 11px"> 监控</span>
	<span class="layui-btn  layui-btn-xs layui-btn-primary layui-icon layui-icon-screen-full" onclick="common.fullscreen($(this))"> 全屏</span>
</div>

<div class="d-bindings" style="display: none">
	<table id="tWorkflowBindings" class="">
		<thead>
		<tr>
			<th data-field="id" width="70"></th>
			<th data-field="value"></th>
		</tr>
		</thead>
	</table>
</div>

<div id="diagramContainer">
	<div id="selectBox"></div>
	<div id="boundRightBottom"></div>
</div>

<div class="lay-con conn-param">
	<form name="f1" class="layui-form layui-form-pane" action="">
		<div class="form-item">
			<div class="layui-form-item p-edge">
				<label class="layui-form-label">顺序</label>
				<div class="layui-input-block">
					<input class="layui-input" name="priority" type="number">
				</div>
			</div>
			<div class="layui-form-item p-edge">
				<label class="layui-form-label">条件</label>
				<div class="layui-input-block">
					<input class="layui-input" name="condition" list="L1">
				</div>
			</div>
			<div class="layui-form-item p-edge">
				<label class="layui-form-label">类型</label>
				<div class="layui-input-block">
					<select class="layui-input" name="type" lay-ignore>
						<option value="Bezier">曲线</option>
						<option value="Straight">直线</option>
						<option value="Flowchart">折线</option>
						<option value="StateMachine">弧线</option>
					</select>
				</div>
			</div>
			<datalist id="L1">
				<option value="Exception">异常</option>
			</datalist>
		</div>
	</form>
</div>
<div class="lay-con d-json"></div>
<textarea id="content" style="display: none">${SRes.getContent()}</textarea>
</body>
<script type="text/javascript">
	var tWorkflow = ${SRes.protocol.getParam()};
	tWorkflow.content = $("#content").val();
	var devList = [
		{name:"❏ 脚本",id:-1,deviceType:"script"},
		{name:"❏ 工作流",id:-2,deviceType:"workflow"}
	];
	var deviceMethod = {
		script : [
			{name:"JS",code:"js",returnType:"java.lang.Object",arg:[{code: "content",type: "text.js"}]},
			{name:"XML",code:"xml",returnType:"java.lang.String",arg:[{code: "content",type: "text.xml"}]},
			{name:"JSON",code:"json",returnType:"java.lang.String",arg:[{code: "content",type: "text.json"}]}
		],
		workflow : []
	};
	var itemIndex = 0;
	var dc = $("#diagramContainer")[0];
	var selectBox = $("#selectBox")[0];
	var boundrb = $("#boundRightBottom")[0]

	jsPlumbInstance.prototype.getOffset = function (a, b, c){
		var con = this.getContainer();
		var d =  $(a).offset();
		d.top += con.scrollTop - con.offsetTop;
		d.left += con.scrollLeft - con.offsetLeft;
		return d;
	}
	var jsPlumbInstance = jsPlumb.getInstance({Container: "diagramContainer"});
	jsPlumbInstance.reset();//避坑

	jsPlumb.Connection.prototype.serialize = function() {
		var data = this.getData();
		return {
			source:this.sourceId,target:this.targetId,
			sourcePosition:this.endpoints[0].anchor.type,
			targetPosition:this.endpoints[1].anchor.type,
			priority:data.priority || 0,
			condition:data.condition || "",
			type:this.connector.type
		};
	}

	/**
	 * ---------- connector 连接线 -----------
	 * Bezier: 贝塞尔曲线 默认
	 * Flowchart: 具有90度转折点的流程线
	 * StateMachine: 状态机
	 * Straight: 直线
	 * ---------- anchors 锚点类型 -----------
	 * 静态锚点
	 * 动态锚点
	 * 边缘锚点
	 * 固定锚点
	 * --------- endpoint 端点类型 -----------
	 * Dot 圆点
	 * Rectangle 矩形
	 * Image 图像
	 * Blank 空白
	 * ---------- overlays 遮罩 箭头 -----------
	 * Arrow 一个可配置的箭头
	 * Label 标签，可以在连接上显示文字信息
	 * PlainArrow 原始类型的箭头
	 * Diamond 菱形箭头
	 * Custom 自定义类型
	 */
	var plumbOption = {
		isSource: true,
		isTarget: true,
		connector: "Bezier",
		connectorStyle: {stroke: '#3890e5'},
		//connectorHoverStyle: {strokeWidth: 2},
		//endpointStyle: { fill: '#3890e5',radius:5},
		//paintStyle: { stroke: 'lightgray', strokeWidth: 3 },
		//hoverPaintStyle: {outlineStroke: 'lightblue'}
	}
	var connEvents = {
		click: function (labelOverlay, originalEvent) {
			var conn = labelOverlay.component || labelOverlay.currentTarget.farthestViewportElement._jsPlumb;
			var data = conn.serialize();
			var connType = data.type;
			$(f1).find(":input").each(function (){
				var v = data[this.name];
				this.value = v != undefined ? v : "";
			});
			layer.open({
				type : 1,
				title : "条件",
				btn : ["确定","取消"],
				area : ["400px","auto"],
				content : $(".conn-param"),
				yes : function (index){
					$(f1).find(":input").each(function (){
						data[this.name] = this.value;
					});
					if(connType != data.type){
						jsPlumbInstance.deleteConnection(conn);
						createConn(data);
					}else{
						setConnLabel(conn,data);
					}
					layer.close(index);
				}
			});
		}
	};

	jsPlumbInstance.importDefaults({
		//Connector: ["Bezier", { curviness: 50 }], // 连接线类型
		PaintStyle: {
			stroke: "#999"
		},
		EndpointStyle: {
			fill: '#c1dff1',
			radius: 5           // 端点半径
		},
		// Overlays 是所有
		ConnectionOverlays:[
			["Arrow",{
				location: 0.98,
				width: 8,
				length: 10,
				events: connEvents
			}],
			['Label',{
				label: '<span class="conn-label" data-priority="0"></span>',
				events: connEvents
			}]
		]
	});


	var DM = function (data){
		var data = data || {};
		this.index = data.index || ++itemIndex;
		this.tbody = $("<tbody></tbody>");
		var item = $("<div class='item' style='top:45px;left: 15px'><table class='t-dev'>" +
				"<thead><tr><th style='width: 80px'><input class='i-dev' placeholder='设备'></th>" +
				"<th><input class='i-method' placeholder='方法'></th></tr></thead> " +
				"<tfoot><tr><td>" +
				"<i class='layui-icon layui-icon-layer item-move' title='移动'></i>" +
				"<i class='is-start layui-icon layui-icon-flag' title='起点'></i>" +
				"</td><td><input class='i-variable'></td></tr></tfoot> </table> </div>");
		this.div = item;
		item[0].dm = this;
		item.css({top:data.top || 40,left:data.left||15});
		if(data.width)
			item.css({width:data.width});
		if(data.height)
			item.css({height:data.height});
		item.attr("data-index",this.index);
		item.attr("data-is-start",data.isStart);

		this.thead = item.find("thead").attr("id",this.index+"-INPUT").after(this.tbody);
		this.tfoot = item.find("tfoot").attr("id",this.index+"-OUTPUT");
		item.find(".i-variable").val(data.variable || "").addClass(data.variable=="return"? "is-return" : "");
		item.find(".is-start").click(function (){
			$(".item").removeAttr("data-is-start");
			item.attr("data-is-start",1);
		});
		$(dc).append(item);
		autocomplete(item);
		//jsPlumbInstance.draggable(item);
		jsPlumbInstance.addEndpoint(this.thead, {anchors: 'Left',  isTarget: true, maxConnections: -1});
		jsPlumbInstance.addEndpoint(this.thead, {anchors: 'Top',   isTarget: true, maxConnections: -1});
		var pointR = jsPlumbInstance.addEndpoint(this.tfoot, {anchors: 'Right', isSource: true, maxConnections: -1});
		var pointB = jsPlumbInstance.addEndpoint(this.tfoot, {anchors: 'Bottom',isSource: true, maxConnections: -1});
		$(pointR.canvas).css("z-index",10);
		$(pointB.canvas).css("z-index",10);
	}

	DM.prototype.serialize = function (){
		var a = {};
		if(this.device){
			a.deviceId = this.device.id;
			a.deviceType = this.device.deviceType;
		}
		if(this.method)
			a.method = this.method.code

		var variable = this.tfoot.find(".i-variable").val();
		if(variable)
			a.variable = variable
		var div = this.div[0];
		var b = $.extend({"left":div.offsetLeft,"top":div.offsetTop},div.dataset,a);
		b.arg = {};
		$(this.tbody).find(":input").each(function (){
			b.arg[this.name] = this.getValue ? this.getValue() : this.value;
		});
		return b;
	}

	DM.prototype.setDevice = function(dev){
		this.device = dev;
		this.thead.find(".i-dev").val(dev.name);
	}

	DM.prototype.setMethod = function(method,param){
		var _this = this;
		var param = param || {};
		var method = method || {};
		this.method = method;
		this.thead.find(".i-method").val(method.name);
		jsPlumbInstance.empty(this.tbody);
		$(method.arg).each(function (){
			_this.addArg(this,param[this.code]);
		});
		if(this.div.find(".rb-resize").length > 0){
			var height = this.div.height();
			this.div.attr("data-width",this.div.width());
			this.div.attr("data-height",height);
			this.div.find("iframe").css({"height":height - 57});
		}else{
			this.div.removeAttr("data-width").css("width","");
			this.div.removeAttr("data-height").css("height","");
		}
		var rt = method.returnType || "";
		this.tfoot.find(".i-variable").attr("title",rt).attr("placeholder",rt.substring(rt.lastIndexOf(".")+1));
		jsPlumbInstance.setSuspendDrawing(false,true);
		//jsPlumbInstance.repaint(this.tfoot);//刷新样式
		//jsPlumbInstance.revalidate(this.tfoot);//重绘某个元素
	}

	DM.prototype.addArg = function(arg,val){
		var a = (arg.type || "").split(".");
		var lastType = a[a.length-1];
		var v = val || "";
		var input = "<input name='"+arg.code+"' value='"+v+"' placeholder='"+lastType+"' title='"+arg.type+"'>";
		var tr = $("<tr><td title='"+arg.code+"'>"+(arg.name || arg.code)+"</td><td>"+input+"</td></tr>");
		if(v.indexOf('"') == 0)
			tr.find("input").addClass("is-string");

		if(a[0] == "text"){
			tr = $("<tr><td colspan='2' class='text-con'><textarea name='"+arg.code+"'>"+(val || "")+"</textarea><div class='rb-resize'></div></td></tr>");
		}
		tr.attr("id",this.index+"-"+arg.code);
		this.tbody.append(tr);
		//不能合并到上面
		if(a[0] == "text"){
			var textArea = tr.find("textarea")[0];
			if(a[1]=="js"){
				var src = "${base}/base/editor/script?style=hide-tool hide-dor&value="+encodeURIComponent(val||'');
				var iframe = $('<iframe class="scroll-wrapper" frameborder="0"></iframe>');
				iframe.attr("src",src);
				tr.find("td").append(iframe);
				textArea.getValue = function (){
					return iframe[0].contentWindow.getValue();
				};
			}else{
				var editor = CodeMirror.fromTextArea(textArea, {
					mode :{name: "javascript", globalVars: true},
					fullScreen : true,
					hintOptions: {schemaInfo: tags},
					extraKeys: {
						"Ctrl-Space": "autocomplete",
						"Alt-/": "autocomplete",
						//"'/'": completeIfAfterLt,
						//"'='": completeIfInTag,
						//"' '": completeAfter,
						"'.'": completeAfter
					}
				});
				textArea.getValue = function (){
					return editor.getValue();
				};
			}
		}
		tr[0].data = arg;
		jsPlumbInstance.addEndpoint(tr, {
			anchors: 'Left',
			isTarget: true,
			maxConnections: -1
		})
	};

	DM.prototype.active = function (){
		this.div.addClass("active");
		var index = this.index+"-";
		$(jsPlumbInstance.getConnections()).each(function (){
			if(this.sourceId.indexOf(index) == 0 || this.targetId.indexOf(index) == 0){
				$(this.canvas).addClass("active");
			}else{
				$(this.canvas).removeClass("active");
			}
		});
	}

	common.ajax("${base}/device/getDevice",{"siteId":siteId},function(list){
		$(list).each(function (){
			this.JP = PinYin.get(this.name);
			devList.push(this);
		});
	});

	common.jsonCont("getAoMethods",{},function(json){
		for(var k in json){
			deviceMethod[k] = json[k];
			json[k].sort(function (a, b) {
				a.JP = PinYin.get(a.name);
				return a.code.localeCompare(b.code)
			});
		}
	});

	common.jsonModel("tWorkflow",{pid:"null"},function (json){
		$(json.list).each(function (){
			var m = {"code":this.code,"name":this.name || this.code,"returnType":this.returnType,"arg":[]};
			if(this.args)
				$(this.args.split("\n")).each(function (){
					var a = this.split("|");
					m.arg.push({"code":a[0],"name":a[1],"select":a[2],"type":(a[3]|| "?")});
				});
			deviceMethod.workflow.push(m);
		});
	});

	common.ajaxStop(function(){
		layui.use("autocomplete",function (){
			loadJson(eval("("+tWorkflow.content+")") || {method:[{index:1,deviceId:0,isStart:1,top:178,left:324,arg:{}}]});
		});
	});

	function loadJson(json){
		itemIndex = 0;
		jsPlumbInstance.reset();
		$(".item").remove();
		$(json.method).each(function (){
			itemIndex = Math.max(itemIndex,this.index);
			addItem(this);
		});
		$(json.connection).each(function (){
			createConn(this);
		});
		jsPlumbInstance.repaintEverything();
	}

	function getDevice(deviceId,deviceType){
		var dev;
		for(var i=0;i<devList.length;i++){
			var d = devList[i];
			if(d.deviceType == deviceType){
				if(deviceId == d.id)
					return d;
				dev = d;
			}
		}
		if(dev)
			return dev;
		layer.alert("类型:"+deviceType+"未匹配",{icon : 2});
	}

	function getMethod(deviceType,method){
		var a = deviceMethod[deviceType];
		if(!a){
			layer.alert("类型:"+deviceType+"未匹配",{icon : 2});
			return;
		}
		for(var i=0;i<a.length;i++){
			if(a[i].code == method)
				return a[i];
		}
	}

	function addItem(data){
		var dm =  new DM(data);
		if(data.deviceType){
			dm.setDevice(getDevice(data.deviceId,data.deviceType) || {});
			dm.setMethod(getMethod(data.deviceType,data.method),data.arg);
		}
		return dm;
	}

	function createConn(data){
		var c = jsPlumbInstance.connect({
			connector : data.type || "Bezier",
			source: data.source,
			target: data.target,
			anchors: [data.sourcePosition || "Right",data.targetPosition || "Left"]
		});
		/*c.bind("click", function(conn) {
			console.log("you clicked on ", conn);
		});*/
		setConnLabel(c,data);
	}

	function setConnLabel(conn,data){
		conn.setData(data);
		var labelMap = conn.getOverlays();
		var label;
		for (var k in labelMap){
			if(labelMap[k].type=="Label")
				label = labelMap[k];
		}
		var span = label.getElement().children[0];
		$(span).attr("data-priority",data.priority || 0).text(data.condition || "");
	}

	function getDm(node){
		return $(node).closest(".item")[0].dm;
	}

	function autocomplete(item){
		layui.autocomplete.render({
			elem: item.find(".i-dev"),
			data : devList,
			onselect: function (resp,elem) {
				getDm(elem).setDevice(resp);
			}
		});

		layui.autocomplete.render({
			elem: item.find(".i-method"),
			data : deviceMethod,
			loadData : function (json,elem) {
				var type = getDm(elem).device.deviceType;
				return deviceMethod[type];
			},
			onselect: function (resp,elem) {
				getDm(elem).setMethod(resp);
			}
		});
	}

	function getSource(){
		var a = [],b=[];
		$(".item").each(function (){
			a.push('\t\t'+JSON.stringify(this.dm.serialize()).replace('"arg":','\n\t\t"arg":'));
		});
		$(jsPlumbInstance.getConnections()).each(function (){
			b.push('\t\t'+JSON.stringify(this.serialize()));
		});
		return "{\n\tmethod:[\n"+a.join(",\n")+"\n\t]," +
				"\n\tconnection:[\n"+b.join(",\n")+"\n\t]" +
				"\n}"
	}

	$(".layui-icon-fonts-code").click(function (){
		layer.prompt({
			formType: 2,
			value: getSource(),
			title: '代码',

			area: ['800px', '300px'],
			maxlength: 100000,
			resize : true
		}, function(value, index, elem){
			var json = eval("("+value+")");
			loadJson(json);
			layer.close(index);
		});
	});

	$(".layui-icon-senior").click(function (){
		if(!tWorkflow.id){
			layer.msg("不支持参数");
			return;
		}
		common.editArgDefine(tWorkflow.args,function (value){
			common.jsonModel("tWorkflow", {id:tWorkflow.id,args:value},function(){
				tWorkflow.args = value;
				layer.msg("已保存");
			},{action:"save"});
		});
	});

	function save(){
		common.jsonCont("saveRes",{"url":"${SRes.url}","content":getSource()});
	}

	$(".monitor-span").click(function (){
		$(this).toggleClass("active");
		$(".d-bindings").css("display",$(this).hasClass("active") ? "block" : "none")
	});

	var resTimeStamp;
	$(".monitor-span input").click(function (e){
		e.stopPropagation();
		if(this.checked){
			$(".d-bindings").show();
			$(".monitor-span").addClass("active");
			this.interval = setInterval(loadRes,2000);
		}else{
			clearInterval(this.interval);
		}
	});

	function run(){
		$(".d-bindings").show();
		$(".monitor-span").addClass("active");
		common.jsonCont("workflow/"+tWorkflow.id,{},function (json){
			loadRes();
			if(!json || json.success !== false)
				layer.alert(JSON.stringify(json));
		},{"callError":true});
	}

	function loadBindings(json){
		var data = json.binding;
		var klass = json.klass;
		var list = [];
		for(var k in data){
			var d = data[k];
			try{
				if(k == "_this" || (d != null && d.clazz))
					continue;
				list.push({"id":k,"value":data[k],"klass":klass[k]});
			}catch(e){
				console.warn(e);
			}
		}
		tWorkflowBindings.clear();
		tWorkflowBindings._onLoaded(list);
	}

	function executedConn(conns){
		var m = {};
		$(conns).each(function (){
			m[this.source+this.target]=true;
		});
		$(jsPlumbInstance.getConnections()).each(function (){
			$(this.canvas).removeClass("executed");
			if(m[this.sourceId + this.targetId]){
				$(this.canvas).addClass("executed");
			}
		});
	}

	function loadRes(){
		if(!tWorkflow.id)
			return;
		common.jsonCont("getWorkflowRes",{"id":tWorkflow.id,"timeStamp":resTimeStamp},function (json){
			if(!json) return;
			var binding = json.binding;
			if(!binding) return;
			resTimeStamp = binding["RUN_TIME"];
			loadBindings(json);
			executedConn(json.connection);
		});
	}

	var imgWin;
	var tWorkflowBindings = new iTables("#tWorkflowBindings",{},{
		render : {
			//这里永远返回默认视图
			id : function (td,data){
				if(data.value === null || data.klass.indexOf("java.lang") == 0 || data.klass == "java.util.Date"){
					return data.id;
				}
				if(data.klass == "java.io.File"){
					$(td).append("<a>"+data.id+"</a>").click(function (){
						openView(data.value.path);
					});
				}else{
					var url = tWorkflow.id+"/"+data.id+".workflow";
					$(td).append("<a>"+data.id+"</a>").click(function (){
						openView(url);
					});
				}
			},
			value : function (td,data){
				var val = data.value;
				var s = $("<span class='bind-val'></span>").appendTo(td);
				s.text(val);
				if(common.isImg(val)){
					s.addClass("bind-obj").click(function (){
						openView(val);
					});
				}
				if(!(val instanceof Object))
					return;
				var jsonStr = JSON.stringify(data.value).slice(1,-1).replace(/"/g,"");
				s.text(jsonStr.substring(0,100));
				if(jsonStr.length > 100){
					var s2 = $("<span class='val-more'>更多...</span>").appendTo(td);
					s2.click(function (){
						showJson(data.value);
					});
				}
				if(data.klass == "org.aiot.model.lang.RecognitionRes"){
					s.addClass("bind-obj").click(function (){
						if(imgWin && imgWin.document && imgWin.document.body){
							$(val.targets).each(function (){
								imgWin.addLabel(this);
							});
							return;
						}

						if(val.img){
							var path = val.img.path;
							openView(path);
						}
					});
				}else if(Array.isArray(val)){
					var v0 = val[0];
					if(Array.isArray(v0)){
						if(isTarget(v0[0])){
							var text = val.length + "行 ";
							$(val).each(function(i){
								text += this.length + ",";
								$(this).each(function (j){
									this.label = this.label + " " + (i+1) + "-" + (j+1);
								});
							});
							s.text(text.slice(0,-1));
							s.addClass("bind-obj").click(function (){
								$(val).each(function(){
									$(this).each(function (){
										imgWin.addLabel(this);
									});
								});
							});
						}
					}else if(isTarget(v0)){
						s.addClass("bind-obj").click(function (){
							$(val).each(function (){
								imgWin.addLabel(this);
							});
						})
					}
				}else if(isTarget(val)){
					s.addClass("bind-obj").click(function (){
						imgWin.addLabel(val);
					})
				}
			}
		}
	});

	function openView(path){
		layer.open({type : 2,btn : false,shade : 0,title: path,
			content : "${base}/view/" + path,
			area : ["80%","80%"],scrollbar: false,maxmin: true,
			success: function(layero, index){
				imgWin = window[layero.find('iframe')[0].name];
			}
		});
	}

	function isTarget(t){
		return t.left != undefined && t.top != undefined && t.width != undefined && t.height != undefined;
	}

	var eClinet,selBoxPos,curClient={},hasMove=false;
	var reSizeEvent;
	var moveTime = new Date().getTime();
	$(dc).on("mousedown",".item",function (e){
		if($(e.target).is("td") || $(e.target).hasClass("item-move"))
			eClinet = {x:e.clientX,y:e.clientY};
		if($(e.target).hasClass("rb-resize")){
			reSizeEvent = {x:e.clientX,y:e.clientY};
			var dm = getDm(e.target);
			reSizeEvent.div = dm.div;
			reSizeEvent.width = dm.div.width();
			reSizeEvent.height = dm.div.height();
			return;
		}
		if(!e.ctrlKey && !$(this).hasClass("active"))
			$(".item.active").removeClass("active");
		this.dm.active();
		$(".item.active").each(function (){
			this.pos = {"left":this.offsetLeft,"top":this.offsetTop};
		});
	}).on("mouseup",".item",function (e){
		if(!e.ctrlKey && !hasMove)
			$(".item.active").not(this).removeClass("active");
	}).on("mouseup","input",function (e){
		var _this = this;
		if(this.placeholder=='File' || (e.ctrlKey && this.placeholder=='Object')){
			var p = this.value.substring(1,this.value.lastIndexOf("/"));
			common.openFile({"path":p},function(path){
				$(_this).val('"'+path+'"').addClass("is-string");
			});
		}
	}).mousedown(function (e){
		if(e.target == this){
			selBoxPos = {x:e.clientX,y:e.clientY};
			selectBox.style.left = e.clientX + dc.scrollLeft + "px";
			selectBox.style.top = e.clientY + dc.scrollTop + "px";
		}
	}).mousemove(function (e) {
		curClient.x = e.clientX;
		curClient.y = e.clientY;
		if (eClinet != null){
			hasMove=true;

			var diffX = e.clientX - eClinet.x;
			var diffY = e.clientY - eClinet.y;
			$(".item.active").diff(diffX,diffY).fitArea();
			jsPlumbInstance.setSuspendDrawing(false,true);

		}else if(selBoxPos != null){
			if(e.clientX - selBoxPos.x > 3 && e.clientY - selBoxPos.y > 3){
				selectBox.style.display = "block";
				selectBox.style.width = (e.clientX - selBoxPos.x) + "px";
				selectBox.style.height = (e.clientY - selBoxPos.y) + "px";
			}
		}else if(reSizeEvent != null){
			var diffX = e.clientX - reSizeEvent.x;
			var w = e.clientX - reSizeEvent.x + reSizeEvent.width;
			var h = e.clientY - reSizeEvent.y + reSizeEvent.height;
			reSizeEvent.div.css({"width":w}).attr("data-width",w);
			if(h > 80){
				reSizeEvent.div.css({"height":h}).attr("data-height",h);
				reSizeEvent.div.find("iframe").css({"height":h - 57});
			}

		}

	}).mouseup(function (e){
		hasMove = false;
		eClinet = null;
		reSizeEvent = null;
		if(e.target == this){
			$(".item.active").removeClass("active");
			$(jsPlumbInstance.getConnections()).each(function (){
				$(this.canvas).removeClass("active");
			});
		}

		if(selBoxPos != null && selectBox.style.display == "block"){
			var p = $(selectBox).position();
			p.width = $(selectBox).width();
			p.height = $(selectBox).height();
			$(".item").each(function (){
				var pos = $(this).position();
				if(pos.left > p.left && pos.left < p.left + p.width &&
						pos.top > p.top && pos.top < p.top + p.height
				){
					$(this).addClass("active");
				}
			});
		}
		selBoxPos = null;
		selectBox.style.display = "none";
	});

	$(document).keydown(function (e){
		if($(e.target).is(":input"))
			return;
		var fun = keyFun[e.keyCode];
		if(fun)
			fun.call(e.target,e);
	}).keyup(function (e){
		var t = $(e.target);
		if(!t.is(":input"))
			return;
		var v = e.target.value;
		if(v.indexOf('"') == 0){
			t.addClass("is-string");
		}else if(v == "return"){
			t.addClass("is-return");
		}else{
			t.removeClass("is-string is-return");
		}
	});

	var copyItem = [];
	var keyFun = {

		27 : function (){// ESC

		},
		46 : function (e) { // Del
			$(".item.active").each(function (){
				jsPlumbInstance.remove(this);
			});
		},
		65 : function (e) { // A
			if(e.ctrlKey)
				$(".item").addClass("active");
		},
		67 : function (e) { // C
			if(!e.ctrlKey)
				return;
			copyItem = [];
			$(".item.active").each(function (){
				copyItem.push(this.dm.serialize());
			});
			if(copyItem.length > 0)
				layer.msg("已复制"+copyItem.length+"条");
		},
		73 : function (e) { // I
			if(e.target == document.body)
				addItem({top:curClient.y + dc.scrollTop,left:curClient.x + dc.scrollLeft});
		},
		83 : function (e) { // S
			if(e.ctrlKey){
				e.preventDefault();
				save();
			}
		},
		86 : function (e) { // V
			if(e.ctrlKey)
				$(copyItem).each(function (){
					this.top = curClient.y + dc.scrollTop;
					this.left = curClient.x + dc.scrollLeft;
					delete this.index;
					addItem(this);
				});
		}

	};

	$.fn.extend({
		diff : function (x,y){
			this.each(function () {
				this.style.left = (this.pos.left + x)+"px";
				this.style.top = (this.pos.top + y)+"px";
			});
			return this;
		},
		fitArea : function (){
			var r = 0,b = 0;
			this.each(function () {
				r = Math.max(r,this.offsetLeft + this.offsetWidth);
				b = Math.max(r,this.offsetTop + this.offsetHeight);
			});
			if(r > boundrb.offsetWidth - 100)
				boundrb.style.width = (r + 100) + "px";
			if(b > boundrb.offsetHeight - 100)
				boundrb.style.height = (b + 100) + "px";
			return this;
		}
	});

	var dummy = {
		attrs: {
			color: ["red", "green", "blue", "purple", "white", "black", "yellow"],
			size: ["large", "medium", "small"],
			description: null
		},
		children: []
	};

	var tags = {
		"!topwww": ["topwww"],
		"!attrs": {
			id: null,
			class: ["A", "B", "C"]
		},
		topwww: {
			attrs: {
				lang: ["en", "de", "fr", "nl"],
				freeform: null
			},
			children: ["animal", "plant"]
		},
		animal: {
			attrs: {
				name: null,
				isduck: ["yes", "no"]
			},
			children: ["wings", "feet", "body", "head", "tail"]
		},
		plant: {
			attrs: {name: null},
			children: ["leaves", "stem", "flowers"]
		},
		wings: dummy, feet: dummy, body: dummy, head: dummy, tail: dummy,
		leaves: dummy, stem: dummy, flowers: dummy
	};

	function completeAfter(cm, pred) {
		var cur = cm.getCursor();
		if (!pred || pred()) setTimeout(function() {
			if (!cm.state.completionActive)
				cm.showHint({completeSingle: false});
		}, 100);
		return CodeMirror.Pass;
	}

	function completeIfAfterLt(cm) {
		return completeAfter(cm, function() {
			var cur = cm.getCursor();
			return cm.getRange(CodeMirror.Pos(cur.line, cur.ch - 1), cur) == "<";
		});
	}

	function completeIfInTag(cm) {
		return completeAfter(cm, function() {
			var tok = cm.getTokenAt(cm.getCursor());
			if (tok.type == "string" && (!/['"]/.test(tok.string.charAt(tok.string.length - 1)) || tok.string.length == 1)) return false;
			var inner = CodeMirror.innerMode(cm.getMode(), tok.state).state;
			return inner.tagName;
		});
	}

	var dJson = $(".d-json");
	function showJson(json){
		dJson.JSONView(json,{

		});
		layer.open({
			title:"数据预览",
			type: 1,
			content:dJson,
			area:["500px","80%"]
		});
	}

</script>

</html>
