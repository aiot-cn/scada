var iframeCss = document.currentScript.src.replace("editor.js","iframe.css");
/**
 * 构造函数，创建一个emr_editor编辑区。
 */
var emr_editor = function(eId, params) {
	this.version = "1.0";
	this.container = document.getElementById(eId);
	$(this.container).addClass("emr_editor_container");	
	this.params = params || {};
	this.toolbar_customize = this.params.customizeTool || {};//自定义工具
	this.toolbars = {};
	this.iframe = null;
	this.window = null;
	this.toolbar = null;
	this.body = null;
	this.paper = null;
	
	this.clipboard = null;
	this.clipboardHTML = null;
	
	//可以覆盖的方法
	this.callSave = null;
	this.shortcutKeys = {};
		
	this.createToolbar();
	this.createIframeEditor();	
	this.createFooter();
	
	//加载工具栏插件
	var toolbars = this.params.toolbars.split(",");
	for (var i = 0; i < toolbars.length; i++) {
		var name = toolbars[i];
		var sCommand = this.toolbar_customize[name] || this.toolbar_actions[name];
		this.addToolbar(name,sCommand);
	}	
	
	this.inputListen();
};

emr_editor.prototype.toolbar_actions = {
	"|":{
		iclass	: 'edui-icon edui-icon-separator'
	},
	bold:{
		command : "bold",
		title 	: '粗体\n(快捷键：Ctrl+b)',
		iclass	: 'edui-icon edui-icon-bold'
	},
	italic:{//斜体
		command : "italic",
		title 	: '斜体\n(快捷键：Ctrl+i)',
		iclass	: 'edui-icon edui-icon-italic'
	},
	underline:{//下划线，默认使用char类型的控件。
		command : "underline",
		title : '下划线\n(快捷键：Ctrl+u)',
		iclass	: 'edui-icon edui-icon-underline'
	},
	strikeThrough:{
		command : "strikeThrough",
		title   : '删除线',
		iclass	: 'edui-icon edui-icon-strikethrough'
	},
	justifyLeft:{//左对齐
		command : "justifyLeft",
		title 	: '左对齐',
		iclass	: 'edui-icon edui-icon-justifyleft'
	},
	justifyCenter:{//剧中对齐
		command : "justifyCenter",
		title 	: '居中对齐',
		iclass	: 'edui-icon edui-icon-justifycenter'
	},
	justifyRight:{//右对齐
		command : "justifyRight",
		title 	: '右对齐',
		iclass	: 'edui-icon edui-icon-justifyright'
	},
	
	justifyFull:{//两端对齐
		command : "justifyFull",
		title 	: '两端对齐',
		iclass	: 'edui-icon edui-icon-justifyjustify'
	},
	superscript:{//上标
		command : "superscript",
		title 	: '上标',
		iclass	: 'edui-icon edui-icon-superscript'
	},
	subscript:{//下标
		command : "subscript",
		title 	: '下标',
		iclass	: 'edui-icon edui-icon-subscript'
	},
	removeFormat:{
		command : 'removeFormat',
		title	: '清除格式',
		iclass	: 'edui-icon edui-icon-removeformat'
	},
	insertOrderedList:{
		command : "insertOrderedList",
		title 	: '有序列表',
		iclass	: 'edui-icon edui-icon-insertorderedlist'
	},
	insertUnorderedList:{
		command : "insertUnorderedList",
		title 	: '无序列表',
		iclass	: 'edui-icon edui-icon-insertunorderedlist'
	},
	indent:{
		command : "indent",
		title 	: '缩进',
		iclass	: 'edui-icon edui-icon-indent'
	},
	outdent:{
		command : "outdent",
		title 	: '取消缩进',
		iclass	: 'edui-icon edui-icon-outdent'
	}
};

/**
 * 创建工具栏
 */
emr_editor.prototype.createToolbar = function() {
	this.toolbar = document.createElement('div');
	this.toolbar.setAttribute("id", "emr_toolbar");
	this.container.appendChild(this.toolbar);
	
};


/**
 * 创建iFrame类型的编辑器区域。
 */
emr_editor.prototype.createIframeEditor = function() {	
	var iframeElement = document.createElement('iframe');
	this.container.appendChild(iframeElement);
	this.iframe = iframeElement;
	this.window = this.iframe.contentWindow;
	this.document = this.window.document;
	var docHTML = '<!DOCTYPE html><html lang=""><head><meta charset="UTF-8">'
	+ '<link rel="stylesheet" type="text/css" href="' + iframeCss + '?v='+ window.resCache +'">'
	+ '<title></title></head><body></body></html>';
	this.document.write(docHTML);
	this.document.close();
	
	this.body = this.document.body;
	this.paper = $('<div id="paper"></div>').appendTo(this.document.body);
	this.paper.attr("contentEditable",true);
};

emr_editor.prototype.createFooter = function() {
	this.footer = $("<div class='emr-footer'><span class='ef-title'>路径：</span><span class='ef-path'></span></div>")
				  .appendTo(this.container);
};

/**
 * 加载插件[{},{}]
 */
emr_editor.prototype.loadToolbar = function(kCommand) {	
	for(var k in kCommand){
		this.addToolbar(k,kCommand[k]);
	}
};

emr_editor.prototype.addToolbar = function(name,sCommand) {	
	if(!sCommand){
		return;
	}
	try {
			var ui = sCommand.ui ? sCommand.ui(this) : this.toolbar_iue(sCommand);
			this.toolbar.appendChild(ui);
			this.toolbars[name] = ui;
			if (sCommand.shortcutKey) {
				//注册事件
				this.registShortcutKey(sCommand.shortcutKey,ui.onmousedown || ui.onclick || ui.onmouseup);
				
				//完善tootip
				ui.title = (ui.title || sCommand.title || sCommand.command) + "\n(" + "快捷键：Ctrl+" + sCommand.shortcutKey + ")";
			}
			
	} catch(e){
			console.error(e);
	}	
};

/*×
禁用按钮
*/
emr_editor.prototype.disableToolbar = function(name) {
	var tool = this.toolbars[name];
	if(tool){
		tool.classList.add("emr-tool-disable");
	}
};

/**
 * 基于ue 的icon class的控件
 * @param sCommand
 */
emr_editor.prototype.toolbar_iue =  function (sCommand) {
	var _this = this;
	var title = sCommand.title;
	var div = document.createElement("div");
	div.setAttribute("class", "edui-icon-con");
	if(title){		
		div.title = title;
	}	
	var div_ico = document.createElement("div");
	div_ico.setAttribute("class", sCommand.iclass);	
	div.appendChild(div_ico);
	
	div.onclick = function(){
		if(div.className.indexOf("emr-tool-disable") > -1)
			return false;

		if(sCommand.click){
			sCommand.click(_this,this);
		}else if(sCommand.command){
			_this.document.execCommand(sCommand.command, false, '');
		}
	};
	return div;
};

/**
 * 重载工具的回调函数
 * @param toolName
 * @param fun
 */
emr_editor.prototype.toolClick = function(toolName,fun){
	var t = this.toolbars[toolName];
	if(t){
		t.onclick = fun;
	}else{
		console.error("IEMR编辑器没有 " + toolName + " 工具");
	}
	
};

/**
 * 加载一个url作为编辑器的内容
 */
emr_editor.prototype.loadUrl = function(url,callback) {
	if (url) {
		$(this.body).load(url, null, callback);
	}
};



/**
 * 加载一段HTML代码作为编辑器的内容。
 * @param html
 */
emr_editor.prototype.setContent = function(html) {
	if(html.indexOf('id="paper"') > 0){
		this.body.innerHTML = html;
		this.paper = $(this.body).find("#paper");
	}else{
		this.paper.html(html);
	}
	
};

/**
 * 得到当前编辑器内容的HTML代码
 */
emr_editor.prototype.getContent = function() {	
	var con = this.paper[0].outerHTML;
	//去除特殊字符
	return con.replace(/(&#8203;|​)/g, "");
};



/**
 * 执行定制的sCommand命令。
 * @param sCommand
 * @param value
 * https://developer.mozilla.org/en-US/docs/Web/API/Document/execCommand
 */
emr_editor.prototype.execCommand = function(sCommand, value) {
	this.window.document.execCommand(sCommand, false, value);
};


/**
 * 插入HTML代码到当前的光标插入点。
 * @param html 字符串
 */
emr_editor.prototype.insertHTML = function(html) {
	//this.execCommand("insertHTML",value);
	var _this = this;
	$(html).each(function (){
		_this.insertNode(this);
	});
};


/**
 * 插入HTML代码到当前的光标插入点。
 * @param value html字符串
 */
emr_editor.prototype.insertText = function(value) {
	this.execCommand("insertText",value);
};

/**
 * 得当当前选区
 */
emr_editor.prototype.getSelect = function() {
	return this.document.getSelection().getRangeAt(0);
};

/**
 * 得到当前选区的文本
 */
emr_editor.prototype.getSelectText = function() {
	return this.getSelect().toString();
};

emr_editor.prototype.selectNode = function(node) {
	var range = this.document.createRange();
	range.selectNodeContents(node);
	this.selectRange(range);
	return range;
};

/**
 * 选中一个range
 */
emr_editor.prototype.selectRange = function(range) {
	var selection = this.windows.getSelection();
    if(selection.rangeCount > 0) {
        selection.removeAllRanges();
    }
    selection.addRange(range);
};


/**
 * 拷贝选择文字
 */
emr_editor.prototype.copy = function() {
		this.clipboard = this.getSelectText();
		var div = document.createElement("div");
		div.appendChild(this.getSelect().cloneContents());
		this.clipboardHTML = div.innerHTML;	
};

/**
 * 剪切选择文字
 */
emr_editor.prototype.cut = function() {
		this.clipboard = this.getSelectText();
		var div = document.createElement("div");
		div.appendChild(this.getSelect().extractContents());
		this.clipboardHTML = div.innerHTML;
};

/**
 * 粘贴拷贝的文字 paste
 */
emr_editor.prototype.paste = function(text) {
	this.insertText(text || this.clipboard);	
};

/**
 * 粘贴拷贝的文字 paste
 */
emr_editor.prototype.pasteHTML = function() {
	this.insertHTML(this.clipboardHTML);
};

/**
 * 用替换的方法插入HTML元素，不支持撤销，但是不会断开标签
 * @param node 要插入的html对象。
 */
emr_editor.prototype.insertDom = function(node) {
	var aid = "id_" + new Date().getTime();
	this.insertHTML("<span id=" + aid + " class=\"temp_span\"></span>");
	var textNode = $(this.body).find("#" + aid).get(0);
	var txtNodePar = textNode.parentNode;//获得当前操作元素的父节点
	txtNodePar.replaceChild(node,textNode);
};

/**
 * 光标处插入节点
 * @param node 要插入的html对象。
 */
emr_editor.prototype.insertNode = function(node) {
	 var range = this.window.getSelection().getRangeAt(0);
     range.deleteContents();
     range.insertNode(node);

	// 将光标移动到插入节点的后面
	range.setStartAfter(node);
	range.setEndAfter(node);
	this.window.getSelection().removeAllRanges();
	this.window.getSelection().addRange(range);
};

/**
 * 注册键盘事件
 * @param keyChar 注册的快捷键。
 * @param func 响应key的事件。
 */
emr_editor.prototype.registShortcutKey = function(keyChar, func) {
	if(this.shortcutKeys[keyChar] != null) {
		throw "重复注册快捷键" + keyChar;
	}
	if (keyChar.length != 1) {
		throw "快捷键字符长度不等于\"1\"" + keyChar;		
	}

	this.shortcutKeys[keyChar.toLocaleLowerCase()] = func;
	this.shortcutKeys[keyChar.toUpperCase()] = func;
};

//判断节点是否可编辑
emr_editor.prototype.isEditable = function(node){
	return node.isContentEditable || getComputedStyle(node).webkitUserModify == "read-write";
};

/**
 * 输入监听
 */
emr_editor.prototype.inputListen = function() {
	var _this = this;
	
	this.body.onkeydown = function(event) {
		if(event.keyCode == 8){ //回退
			/*var br=_this.window.getSelection().anchorNode.innerHTML;
			if(br){
				while(br.trim().slice(-4)=="<br>"){
					br=br.trim().slice(0,-4);
				}
				_this.window.getSelection().anchorNode.innerHTML = br;

			}*/
		}else if (event.keyCode == 9) { //TAB
			/*
			event.preventDefault();
			var old = common.pxToNumber(event.target.style.marginLeft);
			if (old == null && old.length == 0) {
				old = 0;
			}
			var step = 32;
			if (event.shiftKey) {
				step = -32;
			}
			old = old + step;
			if (old < 0) {
				old = 0;
			}
			event.target.style.marginLeft = old + "px";
			//*/
		}else if (event.keyCode == 13) { //回车
			if(_this.isEditable(event.target)){
				//编辑区浏览器行为是增加div
				event.preventDefault();
				var br = _this.document.createElement("br");
				_this.insertNode(br);
			}
		}
		
		if (event.ctrlKey && event.keyCode != 17) {
			if(!_this.params.sysCXV){
				_this.eiCXV(event);
			}
			//保存
			if ('s'.charCodeAt(0) == event.keyCode || 'S'.charCodeAt(0) == event.keyCode) {
				event.preventDefault();
				if (_this.callSave) {
					_this.callSave();
				}
				return false;
			}
			
			for (var key in _this.shortcutKeys) {
				if (key.charCodeAt(0) == event.keyCode) {
					event.preventDefault();
					_this.shortcutKeys[key](event);
					return false;
				}
			}
			
		}
	};

	emr_editor.prototype.eiCXV = function(event) {
		//内部复制
		if ('c'.charCodeAt(0) == event.keyCode || 'C'.charCodeAt(0) == event.keyCode) {
			if (event.altKey) {
				return;
			} else {
				_this.copy();
			}
			event.preventDefault();
			return false;
		}


		//内部剪切
		if ('x'.charCodeAt(0) == event.keyCode || 'X'.charCodeAt(0) == event.keyCode) {
			if (event.altKey) {
				return;
			} else {
				_this.cut();
			}
			event.preventDefault();
			return false;
		}

		//内部粘贴
		if ('v'.charCodeAt(0) == event.keyCode || 'V'.charCodeAt(0) == event.keyCode) {
			if (event.shiftKey) {
				return;
			} else {
				_this.paste();
			}
			event.preventDefault();
			return false;
		}
	}
};


