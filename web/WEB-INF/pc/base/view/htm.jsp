<%@ page contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>

<!doctype html>
<html>
<head>
	<title>${SRes.name}</title>
	<meta http-equiv="Content-Type" content="text/html;charset=utf-8"/>
	<script src="${res}/layui/layui.all.js"></script>
	<link href="${res}/layui/css/layui.css" rel="stylesheet" >
	<script src="${res}/js/common.js"></script>
	<script type="text/javascript">
		var $ = layui.$;
		var base = "${base}";
		var baseURL = '${res}/plugin/ueditor-dev-1.5.0/';
		var paths  = [
			'editor.js',
			'core/browser.js',
			'core/utils.js',
			'core/EventBase.js',
			'core/dtd.js',
			'core/domUtils.js',
			'core/Range.js',
			'core/Selection.js',
			'core/Editor.js',
			'core/Editor.defaultoptions.js',
			'core/loadconfig.js',
			'core/ajax.js',
			'core/filterword.js',
			'core/node.js',
			'core/htmlparser.js',
			'core/filternode.js',
			'core/plugin.js',
			'core/keymap.js',
			'core/localstorage.js',
			'plugins/defaultfilter.js',
			'plugins/inserthtml.js',
			'plugins/autotypeset.js',
			'plugins/autosubmit.js',
			'plugins/background.js',
			'plugins/image.js',
			'plugins/justify.js',
			'plugins/font.js',
			'plugins/link.js',
			'plugins/iframe.js',
			'plugins/scrawl.js',
			'plugins/removeformat.js',
			'plugins/blockquote.js',
			'plugins/convertcase.js',
			'plugins/indent.js',
			'plugins/print.js',
			'plugins/preview.js',
			'plugins/selectall.js',
			'plugins/paragraph.js',
			'plugins/directionality.js',
			'plugins/horizontal.js',
			'plugins/time.js',
			'plugins/rowspacing.js',
			'plugins/lineheight.js',
			'plugins/insertcode.js',
			'plugins/cleardoc.js',
			'plugins/anchor.js',
			'plugins/wordcount.js',
			'plugins/pagebreak.js',
			'plugins/wordimage.js',
			'plugins/dragdrop.js',
			'plugins/undo.js',
			'plugins/copy.js',
			'plugins/paste.js',
			'plugins/puretxtpaste.js',
			'plugins/list.js',
			'plugins/source.js',
			'plugins/enterkey.js',
			'plugins/keystrokes.js',
			'plugins/fiximgclick.js',
			'plugins/autolink.js',
			'plugins/autoheight.js',
			'plugins/autofloat.js',
			'plugins/table.core.js',
			'plugins/table.cmds.js',
			'plugins/table.action.js',
			'plugins/table.sort.js',
			'plugins/contextmenu.js',
			'plugins/shortcutmenu.js',
			'plugins/basestyle.js',
			'plugins/elementpath.js',
			'plugins/formatmatch.js',
			'plugins/searchreplace.js',
			'plugins/customstyle.js',
			'plugins/catchremoteimage.js',
			'plugins/insertparagraph.js',
			'plugins/webapp.js',
			'plugins/template.js',
			'plugins/charts.js',
			'plugins/section.js',
			'plugins/simpleupload.js',
			'plugins/serverparam.js',
			'plugins/insertfile.js',
			'ui/ui.js',
			'ui/uiutils.js',
			'ui/uibase.js',
			'ui/separator.js',
			'ui/mask.js',
			'ui/popup.js',
			'ui/colorpicker.js',
			'ui/tablepicker.js',
			'ui/stateful.js',
			'ui/button.js',
			'ui/splitbutton.js',
			'ui/colorbutton.js',
			'ui/tablebutton.js',
			'ui/autotypesetpicker.js',
			'ui/autotypesetbutton.js',
			'ui/cellalignpicker.js',
			'ui/pastepicker.js',
			'ui/toolbar.js',
			'ui/menu.js',
			'ui/combox.js',
			'ui/dialog.js',
			'ui/menubutton.js',
			'ui/multiMenu.js',
			'ui/shortcutmenu.js',
			'ui/breakline.js',
			'ui/message.js',
			'adapter/editorui.js',
			'adapter/editor.js',
			'adapter/message.js'
		]
		for (var i=0,pi;pi = paths[i++];) {
			document.write('<script type="text/javascript" src="'+ baseURL + "_src/" + pi +'"><\/script>');
		}

	</script>

	<style type="text/css">
		.edui-editor-bottomContainer{
			display: none;
		}
	</style>
</head>
<body>
<div>
	<script id="editor" type="text/plain" style="width:100%;height:500px;">

	</script>
</div>
<div id="btns" style="display: none">
	<div>
		<button onclick="getAllHtml()">获得整个html的内容</button>
		<button onclick="getContent()">获得内容</button>
		<button onclick="setContent()">写入内容</button>
		<button onclick="setContent(true)">追加内容</button>
		<button onclick="getContentTxt()">获得纯文本</button>
		<button onclick="getPlainTxt()">获得带格式的纯文本</button>
		<button onclick="hasContent()">判断是否有内容</button>
		<button onclick="setFocus()">使编辑器获得焦点</button>
		<button onmousedown="isFocus(event)">编辑器是否获得焦点</button>
		<button onmousedown="setblur(event)" >编辑器失去焦点</button>

	</div>
	<div>
		<button onclick="getText()">获得当前选中的文本</button>
		<button onclick="insertHtml()">插入给定的内容</button>
		<button id="enable" onclick="setEnabled()">可以编辑</button>
		<button onclick="setDisabled()">不可编辑</button>
		<button onclick=" UE.getEditor('editor').setHide()">隐藏编辑器</button>
		<button onclick=" UE.getEditor('editor').setShow()">显示编辑器</button>
		<button onclick=" UE.getEditor('editor').setHeight(300)">设置高度为300默认关闭了自动长高</button>
	</div>

	<div>
		<button onclick="getLocalData()" >获取草稿箱内容</button>
		<button onclick="clearLocalData()" >清空草稿箱</button>
	</div>

</div>

<script type="text/javascript">
	UE.registerUI('save',function(editor,uiName){
		var btn = new UE.ui.Button({
			name:"save",
			title:"保存",
			cssRules :'background-position: -480px -20px;',
			onclick:function () {
				save();
			}
		});
		return btn;
	});
	UE.registerUI('image',function(editor,uiName){
		//创建一个button
		var btn = new UE.ui.Button({
			name:uiName,
			title:"图片",
			cssRules :'background-position:-380px 0px;',
			onclick:function () {
				common.openFile({suffix:"jpg,jpeg,png,gif"},function (name){
					editor.execCommand("insertHtml","<img style='width:400px' src='${base}/json/img?name="+name+"' />");
				});
			}
		});

		return btn;
	});

	window.UEDITOR_CONFIG = {
		//为编辑器实例添加一个路径，这个不能被注释
		UEDITOR_HOME_URL: baseURL,
		// 服务器统一请求接口路径
		//serverUrl: URL + "php/controller.php",
		//工具栏上的所有的功能按钮和下拉框，可以在new编辑器的实例时选择自己需要的重新定义
		toolbars: [
			[
				"undo",
				"redo",
				"|",
				"bold",
				"italic",
				"underline",
				"fontborder",
				"strikethrough",
				"superscript",
				"subscript",
				"removeformat",
				"formatmatch",
				"autotypeset",
				"blockquote",
				"pasteplain",
				"|",
				"forecolor",
				"backcolor",
				"insertorderedlist",
				"insertunorderedlist",
				"selectall",
				"cleardoc",
				"|",
				"rowspacingtop",
				"rowspacingbottom",
				"lineheight",
				"|",
				"customstyle",
				"paragraph",
				"fontfamily",
				"fontsize",
				"|",
				"directionalityltr",
				"directionalityrtl",
				"indent",
				"|",
				"justifyleft",
				"justifycenter",
				"justifyright",
				"justifyjustify",
				"|",
				"touppercase",
				"tolowercase",
				"|",
				"imagenone",
				"imageleft",
				"imageright",
				"imagecenter",
				"|",
				"image",
				"horizontal",
				"date",
				"time",
				"spechars",
				"|",
				"inserttable",
				"deletetable",
				"insertparagraphbeforetable",
				"insertrow",
				"deleterow",
				"insertcol",
				"deletecol",
				"mergecells",
				"mergeright",
				"mergedown",
				"splittocells",
				"splittorows",
				"splittocols",
				"charts",
				"|",
				"print","save"
			]
		]
	};
	//实例化编辑器
	//建议使用工厂方法getEditor创建和引用编辑器实例，如果在某个闭包下引用该编辑器，直接调用UE.getEditor('editor')就能拿到相关的实例

	var ue = UE.getEditor('editor',{
		fullscreen : true,
		elementPathEnabled : false,
		wordCount:false,
		onready:function(){
			common.jsonCont("resContent",{"url":"${SRes.url}"},function (txt){
				ue.setContent(txt);
			});
			ue.addListener("keydown", function(type, e) {
				if(e.ctrlKey && e.keyCode == 83){
					e.preventDefault();
					save();
				}
			});
		}}
	);

	function save(){
		common.jsonCont("saveRes",{"url":"${SRes.url}","content":ue.getContent()},function (){
			layer.msg("保存成功");
		});
	}

	function isFocus(e){
		alert(UE.getEditor('editor').isFocus());
		UE.dom.domUtils.preventDefault(e)
	}
	function setblur(e){
		UE.getEditor('editor').blur();
		UE.dom.domUtils.preventDefault(e)
	}
	function insertHtml() {
		var value = prompt('插入html代码', '');
		UE.getEditor('editor').execCommand('insertHtml', value)
	}
	function createEditor() {
		enableBtn();
		UE.getEditor('editor');
	}
	function getAllHtml() {
		alert(UE.getEditor('editor').getAllHtml())
	}
	function getContent() {
		var arr = [];
		arr.push("使用editor.getContent()方法可以获得编辑器的内容");
		arr.push("内容为：");
		arr.push(UE.getEditor('editor').getContent());
		alert(arr.join("\n"));
	}
	function getPlainTxt() {
		var arr = [];
		arr.push("使用editor.getPlainTxt()方法可以获得编辑器的带格式的纯文本内容");
		arr.push("内容为：");
		arr.push(UE.getEditor('editor').getPlainTxt());
		alert(arr.join('\n'))
	}
	function setContent(isAppendTo) {
		var arr = [];
		arr.push("使用editor.setContent('欢迎使用ueditor')方法可以设置编辑器的内容");
		UE.getEditor('editor').setContent('欢迎使用ueditor', isAppendTo);
		alert(arr.join("\n"));
	}
	function setDisabled() {
		UE.getEditor('editor').setDisabled('fullscreen');
		disableBtn("enable");
	}

	function setEnabled() {
		UE.getEditor('editor').setEnabled();
		enableBtn();
	}

	function getText() {
		//当你点击按钮时编辑区域已经失去了焦点，如果直接用getText将不会得到内容，所以要在选回来，然后取得内容
		var range = UE.getEditor('editor').selection.getRange();
		range.select();
		var txt = UE.getEditor('editor').selection.getText();
		alert(txt)
	}

	function getContentTxt() {
		var arr = [];
		arr.push("使用editor.getContentTxt()方法可以获得编辑器的纯文本内容");
		arr.push("编辑器的纯文本内容为：");
		arr.push(UE.getEditor('editor').getContentTxt());
		alert(arr.join("\n"));
	}
	function hasContent() {
		var arr = [];
		arr.push("使用editor.hasContents()方法判断编辑器里是否有内容");
		arr.push("判断结果为：");
		arr.push(UE.getEditor('editor').hasContents());
		alert(arr.join("\n"));
	}
	function setFocus() {
		UE.getEditor('editor').focus();
	}
	function deleteEditor() {
		disableBtn();
		UE.getEditor('editor').destroy();
	}
	function disableBtn(str) {
		var div = document.getElementById('btns');
		var btns = UE.dom.domUtils.getElementsByTagName(div, "button");
		for (var i = 0, btn; btn = btns[i++];) {
			if (btn.id == str) {
				UE.dom.domUtils.removeAttributes(btn, ["disabled"]);
			} else {
				btn.setAttribute("disabled", "true");
			}
		}
	}
	function enableBtn() {
		var div = document.getElementById('btns');
		var btns = UE.dom.domUtils.getElementsByTagName(div, "button");
		for (var i = 0, btn; btn = btns[i++];) {
			UE.dom.domUtils.removeAttributes(btn, ["disabled"]);
		}
	}

	function getLocalData () {
		alert(UE.getEditor('editor').execCommand( "getlocaldata" ));
	}

	function clearLocalData () {
		UE.getEditor('editor').execCommand( "clearlocaldata" );
		alert("已清空草稿箱")
	}
</script>
</body>
</html>