<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html>
<head>
	<title>模板维护</title>	
	<%@include file="../../common/page_head.jsp" %>
	<script src="${res}/plugin/editor/editor.js"></script>
	<script src="${res}/plugin/editor/plugins.js"></script>
	<script src="${res}/plugin/iUI/iUI.js"></script>
	<link href="${res}/plugin/editor/editor.css" rel="stylesheet" >
	
<style type="text/css">
	#edtior{
		margin-right: 300px;
	}
	.emr_editor_container{
		height: calc(100% - 35px);
	}
	.emr-footer{
		display: none;
	}
	.emr-attr{

	}
	.emr-attr-row{
		padding: 3px;
	}
	.emr-attr input,.emr-attr select{
		border: 1px solid #c3cadb;
		border-radius: 2px;
	}
	.emr-attr input:focus,.emr-attr select:focus{
		border-color: #6BB56E;
	}
	.emr-attr input{
		width: 190px;
		padding: 3px;
	}
	.emr-attr select{
		width: 198px;
		padding: 2px;
	}
	.emr-attr label{
		display: inline-block;
		width: 60px;
		text-align: right;
		margin-right: 10px;
	}
	[node-type]{
		border-top: 1px solid #ccc;
		margin-top: 10px;
		padding-top: 10px;
	}
	#nodePath{
		margin: 10px;
	}
	#nodePath li{
		display: inline-block;
	}
	#nodePath li.active{
		color: #a71d5d;
	}
	#nodePath li:hover{
		cursor: pointer;
		color: #0EA4D6;
	}
	#nodePath li:before{
		content: "/";
		margin: 0 3px;
	}
</style>
     
</head>
<body>
	<div class="emr-attr" style="float: right;width: 300px">
		<div>
			<ul id="nodePath">
				路径:<%--<li>div</li><li>table</li><li>tr</li><li>td</li>--%>
			</ul>
		</div>
		<div node-type="all">
			<div class="emr-attr-row">
				<label>ID</label><input name="id">
			</div>
			<div class="emr-attr-row">
				<label>标题</label><input name="title">
			</div>

			<%--<div class="emr-attr-row">
				<label>名称</label><input type="text" name="name">
			</div>
			<div class="emr-attr-row">
				<label>值</label><input type="text" name="value">
			</div>
			<div class="emr-attr-row">
				<label>公式</label><input name="emr-calc">
			</div>--%>

			<div class="emr-attr-row">
				<label>样式</label><input name="class">
			</div>
			<div class="emr-attr-row">
				<label>内边距</label><input name="style-padding">
			</div>
			<div class="emr-attr-row">
				<label>外边距</label><input name="style-margin">
			</div>
			<div class="emr-attr-row">
				<label>宽度</label><input name="style-width" style="width: 54px">
				<label>行高</label><input name="style-line-height" style="width: 54px">
			</div>
			<div class="emr-attr-row">
				<label>装饰线</label><select name="style-text-decoration-line" style="width: 62px">
					<option value="">--</option>
					<option value="underline">下划线</option>
					<option value="line-through">删除线</option>
					<option value="overline">上划线</option>
				</select>
				<label>线类型</label><select name="style-text-decoration-style" style="width: 62px">
				<option value="">--</option>
				<option value="double">双实线</option>
				<option value="dotted">点线</option>
				<option value="dashed">虚线</option>
				<option value="wavy">波浪线</option>
			</select>
			</div>
			<div class="emr-attr-row">
				<label>定位</label><select name="style-position" style="width: 62px">
					<option value="">--</option>
					<option value="absolute">绝对</option>
					<option value="relative">相对</option>
				</select>
				<label>显示</label><select name="style-display" style="width: 62px">
					<option value="">--</option>
					<option value="block">块</option>
					<option value="inline">内联</option>
					<option value="inline-block">内联块</option>
				</select>
			</div>
		</div>
		<div node-type="var">
			<div class="emr-attr-row">
				<label>标签类型</label><select name="data-type" lay-ignore>
					<option value="">--</option>
					<option value="html">html</option>
					<option value="attrVal">属性值</option>
					<option value="iframe">iframe</option>
					<option value="iTable">iTable</option>
					<option value="echarts">echarts</option>
					<option value="vue">vue</option>
				</select>
			</div>
			<div class="emr-attr-row">
				<label>数据来源</label><select name="data-source" lay-ignore>
					<option value="">--</option>
				</select>
			</div>
			<div class="emr-attr-row">
				<label>CODE</label><input name="data-code">
			</div>
		</div>
	</div>
	<div id="edtior"></div>

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
	<textarea id="content" style="display: none">${SRes.getContent()}</textarea>
</body>


<script type="text/javascript">
	var tTemplate = ${SRes.protocol.getParam()};

	var editorVar = null;
	var editorDia = null;

	var eActive,copyNode;

	var toolbars = "html,|,bold,italic,underline,strikeThrough,|,justifyLeft,justifyCenter,justifyRight,justifyFull,|,fontSize,fontName,formatBlock,|," +
	   "foreColor,backColor,|,superscript,subscript,insertOrderedList,insertUnorderedList,indent,outdent,removeFormat,removeFormatWord,|,createLink,unlink,|,"+
	   "insertHR,insertText,specialchar,expressions,insertImage,insertTable,|,dataSource,save";

	editorBar.dataSource = {
		title 	: '数据源',
		iclass  : "edui-icon edui-icon-3-22",
		click	: function (ei,toolBar) {
			layer.open({
				type: 1,
				title : "数据源",
				btn : ["确定"],
				content: $(".d-data-source"),
				area : ["700px","400px"],
				yes : function (index){
					layer.close(index);
				}
			});
		}
	}

	function getDataSourceNode(){
		var codeNode = editor.paper.find("code[data-var='dataSource']");
		if(codeNode.length == 0){
			codeNode = $('<code data-var="dataSource"></code>').appendTo(editor.paper);
		}
		return codeNode;
	}

	var editor = new emr_editor("edtior",{
		"toolbars" : toolbars,
		"customizeTool" : editorBar,
		"sysCXV" : true
	});

	$(editor.body).on("mouseup",editorClick);
	
	editor.toolClick("save",function(){
		getDataSourceNode().text(JSON.stringify(tDataSouce.getDataList()));
		common.jsonCont("saveRes",{"url":"${SRes.url}","content":editor.getContent()});
	});

	$(function (){
		editor.setContent($("#content").val());
		tDataSouce._onLoaded(JSON.parse(getDataSourceNode().html() || "[]"));
		document.title = "模板-"+tTemplate.title;
		$(editor.body).addClass("template");
		iUI.RMenu(editor.window,m1);
	});

	var m1 = [
		{
			title : "复制",
			click : function(){
				copyNode = this.target.cloneNode();
			}
		},{
			title : "粘贴",
			check : function(){
				return copyNode;
			},
			click : function(){
				editor.insertNode(copyNode.cloneNode());
			}
		},{
			title : "删除",
			check : function(target,li){
				var name = target.localName;
				if(target.id)
					name += "#"+target.id;
				if(target.className)
					name += "."+target.id;
				$(li).find("span").text("删除 "+name);
				return true;
			},
			click : function(){
				$(this.target).remove();
			}
		},{
			title : "仅保留文本",
			click : function(){
				this.target.innerHTML = this.target.innerText
			}
		},{
			title : "插入标签",
			click : function(){
				editor.insertHTML("<var data-type='html' title='标签'>&#8203;</var>");
			}
		},{
			title : "插入HTML",
			click : function(){
				layer.prompt({"title":"HTML","formType":2},function (value,index){
					editor.insertHTML(value);
					layer.close(index);
				});
			}
		},{
			title : "表格",
			check : function(target){
				return $(target).parents("table").length > 0;
			},
			sub : [
				{
					title : "增加一列",
					click : function(){
						var i = $(this.target).prevAll().length;
						$(this.target).parents("table").find("tr").each(function (){
							$(this).find("td").eq(i).after("<td> </td>");
						});
					}
				}, {
					title : "添加行",
					click : function(){
						var tr = $(this.target).parents("tr");
						var tr2 = tr.clone();
						tr2.find("td").html("&nbsp;");
						tr.after(tr2);
					}
				},{
					title : "添加列",
					click : function(){
						$(this.target).after("<td>&nbsp;</td>")
					}
				},{
					title : "刪除行",
					click : function(){
						$(this.target.parentNode).remove();
					}
				},{
					title : "刪除列",
					click : function(){
						$(this.target).remove();
					}
				},{
					title : "向右合并",
					click : function(){
						var td = $(this.target);
						if(!td.is("td"))
							td = td.parents("td");
						td[0].colSpan += 1;
						td.next().remove();
					}
				}, {
					title : "取消右合并",
					check : function (target){
						return target.colSpan > 1;
					},
					click : function(){
						this.target.colSpan -= 1;
						$(this.target).after("<td></td>")
					}
				}, {
					title : "向下合并",
					click : function(){
						var td = $(this.target).closest("td");
						td[0].rowSpan += 1;
						td.belowTd().remove();
					}
				}, {
					title : "取消下合并",
					check : function (target){
						return target.rowSpan > 1;
					},
					click : function(){
						var td = $(this.target).closest("td");
						td[0].rowSpan -= 1;
						td.belowTd().after("<td></td>")
					}
				}
			]
		}
	];

	function explorerCallback(fileName) {
		editor.insertHTML("<img style='max-width: 680px' src='${base}/image"+fileName+"'>");
	}

	var tDataSouce = new iTables("#tDataSouce",{},{
		primaryKey : "code",
		create : true,
		edit : true,
		remove : true,
		inline_edit : true,
		modeCommit : true,
		beforeStats : function (json){
			common.renderSelect('[name="data-source"]',this.getDataList(),{dft:"",value:"code",name:"code"});
		}
	});

	function editorClick(e){
		$(eActive).removeAttr("active");
		eActive = e.target;
		$(eActive).attr("active","");
		emrAttr(e.target);
		var arr = [];
		var c0 = eActive.children[0];
		if(c0)
			arr.push(c0);
		arr.push(eActive);
		var pNode = eActive.parentNode;
		while (pNode.nodeName != "BODY" && pNode.nodeName != "HTML"){
			arr.push(pNode);
			pNode = pNode.parentNode;
		}
		var p = $("#nodePath").empty();
		$(arr.reverse()).each(function (){
			var li = document.createElement("li");
			li.innerHTML = this.localName;
			li.data = this;
			if(this == eActive)
				$(li).addClass("active");
			p.append(li);
		});
	}

	function emrAttr(v){
		$(".emr-attr :input").each(function(){
			if(this.name.indexOf("style-") === 0){
				this.value = v.style[this.name.substring(6)] || "";
			}else{
				this.value = v.getAttribute(this.name) || "";
			}

		});
	}

	$("#nodePath").on("click","li",function (){
		$("#nodePath").find(".active").removeClass("active");
		$(this).addClass("active");
		eActive = this.data;
		emrAttr(eActive);
	});

	$(".emr-attr").on("focus",":input",function (e){
		this.bVal = this.value;
	}).on("blur",":input",function (e){
		if(this.bVal == this.value)
			return;
		if(this.name.indexOf("style-") === 0){
			eActive.style[this.name.substring(6)] = this.value || "";
		}else{
			if(this.value)
				eActive.setAttribute(this.name,this.value)
			else
				eActive.removeAttribute(this.name);
		}
	});

</script>
</html>