//所有支持的公式
var emr_editor_expressions = [];



/**
 * EMR Editor plug-in可以使用到的UI控件，在这里定义好控件以及控件的行为，在plug-in中通过ui:..直接引用。
 */
var emr_editor_plugin_ui = {
	euiTable : function(ei) {
		var d = $("<div class='ei-table-div'><div class='ei-table-tool'><span class='ei-table-info'>0行 ✕ 0列</span> <span class='layui-icon layui-icon-close'></span></div></div>").appendTo(document.body);
		for(var i=0;i<10;i++){
			for(var j=0;j<10;j++){
				d.append("<i data-c='"+j+"' data-r='"+i+"'></i>");
			}
		}
		d.find(".layui-icon-close").click(function () {
			d.hide();
		});
		d.mousemove(function (e) {
			var t = e.target;
			if(t.nodeName == "I"){
				var c = $(t).data("c");
				var r = $(t).data("r");
				d.find(".ei-table-info").text((r+1) + "行 ✕ " + (c+1)+"列");
				d.find("i").each(function () {
					if($(this).data("c") <= c && $(this).data("r") <= r){
						$(this).addClass("highlight");
					}else{
						$(this).removeClass("highlight");
					}
				});
			}else{
				d.find(".ei-table-info").text("0行 ✕ 0列");
				d.find(".highlight").removeClass("highlight");
			}
		});
		d.click(function (e) {
			var t = e.target;
			if(t.nodeName == "I"){
				var c = $(t).data("c") + 1;
				var r = $(t).data("r") + 1;
				d.hide();
				var tr = "";
				for(var i=0;i<r;i++){
					var td = "";
					for(var j=0;j<c;j++){
						td += "<td>&#xFEFF;</td>";
					}
					tr += "<tr>" + td + "</tr>";
				}
				ei.insertHTML("<table>"+tr+"</table>");
			}
		})
	},

	//特殊符号
	specialChar : function(ei) {
		var html = "<div class='ei-special-char-div'>" +
						"<div class='ei-special-char-tool'><span class='layui-icon layui-icon-close'></span></div>" +
						"<div class='ei-special-char-body'></div> "+
					"</div>";
		var div = $(html).appendTo(document.body);

		div.find(".layui-icon-close").click(function () {
			div.hide();
		});

		var body = div.find(".ei-special-char-body");
		var str = '①②③④⑤⑥⑦⑧⑨⑩⑴⑵⑶⑷⑸⑹⑺⑻⑼⑽㈠㈡㈢㈣㈤㈥㈦㈧㈨㈩ⅠⅡⅢⅣⅤⅥⅦⅧⅨⅩ℃％‰￠¤∏∑※§αβγ×÷±√∽≌∠⌒∥⊥⊙∴∵∈∩∪∨∧∝∮∫≯≮＞＜≥≤≠≡≈∷→←↑↓№';
		for (var i = 0; i < str.length; i++) {
			body.append("<i>"+str[i]+"</i>");
		}

		div.click(function (e) {
			var t = e.target;
			if (t.nodeName == "I") {
				ei.insertText(t.innerText);
			}
		});

	},
	
	html: function (sCommand, win, editor) { //查看HTML代码。
		var node = emr_editor_plugin_ui.iue(sCommand, win, false);
		node.onmousedown = function(event){
			if (editor.type != "iframe") {
				common.openErrorMessage("不支持的操作");
				return;
			}
			if (this.flag) {
				var ta = this.textarea;
				var html = this.textarea.value;
				win.document.body.innerHTML = html;
				this.flag = false;
				this.textarea = null;
				this.style.border = "0px solid #CCC";
				return false;
				
			} else {
				var html = win.document.body.innerHTML;
				win.document.body.innerHTML = "";
				var ta = document.createElement("textarea");
				ta.style.width = "100%";
				ta.style.minHeight = "300px";
				ta.value = html;
				win.document.body.appendChild(ta);
				this.flag = true;
				this.textarea = ta;
				this.style.border = "1px solid #CCC";
				return false;
			}
		};
		return node;
	},
	
	//公式
	expressions : function(sCommand, win, editor) {
		sCommand.iclass = 'edui-icon edui-icon-formula';
		var node = emr_editor_plugin_ui.iue(sCommand, win, false);
		node.onmousedown = function(event) {
			var div = document.createElement("div");
			div.setAttribute("class","edit_select"); 
			div.style.left = (event.clientX - 10) + "px";
			div.style.top = (event.clientY - 2) + "px";
			div.style.display = "block";
			div.style.maxWidth = "200px";
			div.style.maxHeight = "400px";
			div.style.overflow = "auto";

			

			//信息显示区；
			var bt = document.createElement("i");
			bt.setAttribute("class","icon  icon-remove");
			bt.innerHTML = "";
			bt.style.float = "right";
			bt.style.fontSize = "20px";
			bt.style.cursor = "pointer";
			div.appendChild(bt);
			
			
			div.onclick = function () {
				document.body.removeChild(div);
			};
			
			bt.onclick = function () {
				document.body.removeChild(div);
			};
			
			for (var i = 0; i < emr_editor_expressions.length; i++) {
				var container = document.createElement("div");
				container.setAttribute("class", "expressions_container");
				var exp = emr_editor_expressions[i].build();
				container.innerHTML = exp;
				div.appendChild(container);		
				
				container.onclick = function (event) {
					editor.insertHTML(this.innerHTML);
				};
			}
			
			
			
			document.body.appendChild(div);
		};
		return node;
	},
	
	//续打
	continuePrint: function (sCommand, win ,editor) {
		var div = document.createElement("div");
		div.setAttribute("class", "edui-icon-con");
		var div_ico = document.createElement("div");
		div_ico.setAttribute("class", sCommand.iclass);
		div.title = (sCommand.title || sCommand.command);
		div.appendChild(div_ico);
		
		div.onmousedown = function(event){
			var printTag = editor.window.document.getElementById('continuePrint');
			if(printTag){				
				$(editor.window.document.body).find('var,span,hr').each(function(){
					if($(this).offset().top+$(this).height()<$(printTag).offset().top+$(printTag).height()){
						this.style.visibility = "hidden";	
					}
					
				});
				var text = printTag.parentNode.innerHTML.split('<span id="continuePrint"></span>');
				printTag.parentNode.innerHTML = '<span style="visibility: hidden;">' + text[0] + '</span>' + text[1];
				
				
			}else{
				alert('未找到上次打印标记');
				return false;
			}
			
			var pwin = window.open(null, "Print Window", '');
			pwin.document.write('<link href="' + ctx + '/js/emr/editor/iframe.css" rel="stylesheet" type="text/css">');
			pwin.document.write($.trim(editor.getEditArea().innerHTML));
			pwin.document.body.setAttribute("class", "print");
			pwin.print();
			pwin.close();
			return false;			
		};
		return div;
	}
	
};

var editorBar = {

	fontSize : {
		title	: '字号',
		command : 'fontSize',
		ui		: function(ei){
				var command = this.command;
				var select = document.createElement("select");
				select.title = this.title;
				select.setAttribute("class", "tool-font-size");
				select.setAttribute("placehoder", '字号');
				select.onchange = function(event){
					ei.document.execCommand(command, false, this.value);			
					this.selectedIndex = 0;
				};
				select.innerHTML = '<option value="0" selected>字号</option><option value="1">1号</option><option value="2">2号</option><option value="3">3号</option>'
								 + '<option value="4">4号</option><option value="5">5号</option><option value="6">6号</option><option value="7">7号</option>';
				return select;
		}
	},
	fontName : {
		command : 'fontName',
		title	: '字体',
		ui		: function (ei) {
			var command = this.command;
			var select = document.createElement("select");
			select.title = this.title;
			select.setAttribute("class", "tool-font-name");
			select.onchange = function(event){
				ei.document.execCommand(command, false, this.value);
				this.selectedIndex = 0;
			};
			select.innerHTML = '<option>字体</option><option>宋体</option><option>黑体</option><option>楷体</option><option>幼圆</option><option>隶书</option><option>仿宋</option>'
				+ '<option>微软雅黑</option><option>Arial</option><option>Verdana</option><option>Tahoma</option><option>Times New Roman</option><option>Tempus Sans ITC</option>';
			return select;
		}
	},
	formatBlock : {
		command : "formatBlock",
		title	: "格式",
		ui		: function (ei) {
			var command = this.command;
			var select = document.createElement("select");
			select.title = this.title;
			select.setAttribute("class", "tool-h");
			select.onchange = function(event){
				ei.document.execCommand(command, false, this.value);
			};
			select.innerHTML = '<option>&lt;H1&gt;</option><option>&lt;H2&gt;</option><option>&lt;H3&gt;</option><option>&lt;H4&gt;</option><option>&lt;H5&gt;</option><option>&lt;H6&gt;</option>'
				+ '<option>&lt;Div&gt;</option><option>&lt;Address&gt;</option>';
			return select;
		}
	},
	foreColor : {
		command : "foreColor",
		title : '前景色',
		iclass : "edui-icon edui-icon-forecolor",
		ui : function (ei) {
			var _this = this;
			var node = ei.toolbar_iue(this);
			$(node).attr("lay-ignore","true");
			layui.colorpicker.render({
			    elem: $(node).find("div"),
			    predefine: true,
			    change: function(color){
			      ei.execCommand(_this.command,color);
			    }
			});
			return node;
		}
		
	},
	backColor : {
		command : "backColor",
		title : '背景色',
		iclass : "edui-icon edui-icon-backcolor",
		ui : function(ei){
			return editorBar.foreColor.ui.call(this,ei);
		}
		
	},
	createLink : {//插入link
		command : "createLink",
		title 	: '链接',
		iclass	: 'edui-icon edui-icon-link',
		click	: function(ei){
			var url = window.prompt("URL:", "");
			if (url) {
				ei.document.execCommand(this.command, false, url);
			}
		}
	},
	unlink : {//清除link
		command : "unlink",
		title 	: '清除链接',
		iclass	: 'edui-icon edui-icon-unlink'
	},

	removeFormatWord: {//清除word格式
		title 	: '清除word格式',
		iclass	: 'edui-icon edui-icon-word2',
		click : function(ei){
			$(ei.paper).find("table,tr,td").removeAttr("style class width valign border cellspacing");
			$(ei.paper).find("font,p,span").each(function () {
				$(this).after(this.innerHTML).remove();
			});
			var phtml = $(ei.paper).html().replace(/<o:p><\/o:p>/g,"");
			$(ei.paper).html(phtml);
		}
	},
	
	html: {
		title : 'html',
		iclass : "edui-icon edui-icon-html",
		click : function(ei){
			layer.open({
				title: '源代码',
				area: ['90%', '90%'],
				content : '<textarea class="layui-layer-input" style="width: 100%; height: 95%"></textarea>',
				btn: ['确定', '取消'],
				success: function(layero, index){
					layero.find("textarea").val(ei.paper.html());
				},
				yes: function(index, layero){		
					ei.paper.html(layero.find("textarea").val());
					layer.close(index);
				}
			});
		}
		
	},
	insertHR:{
		title 	: '插入横线',
		iclass	: "edui-icon edui-icon-horizontal",
		click	: function(ei){
			ei.insertHTML("<hr>");
		}
	},

	insertText  : {
		command : "insertText",
		title 	: '插入文本',
		iclass	: 'edui-icon edui-icon-pasteplain',
		click	: function(ei){
			layer.prompt({
			  formType: 2,
			  title: '插入文本',
			  area: ['500px', '150px']
			}, function(value, index, elem){
			  layer.close(index);
			  ei.execCommand("insertText",value);
			});

		}
	},
	
	insertPage : {
		title 	: '手动分页',
		iclass  : 'edui-icon edui-icon-pagebreak',
		click	: function(ei){		
			ei.insertHTML("<div class='div_page_break'></div>");
		}
	},
	
	varxml : {//显示
		title 	: 'XML结构',
		iclass	: "edui-icon edui-icon-code",
		click	: function(ei,toolBar){
			$(toolBar).toggleClass("active");
			if($(toolBar).hasClass("active")){
				$(ei.body).addClass("showtag");
			}else{
				$(ei.body).removeClass("showtag");
			}
			
		}
	},
	landscape : {
		title 	: '横向/竖向',
		iclass	: "edui-icon edui-icon-diamond",
		click	: function(ei,toolBar){
			$(toolBar).toggleClass("active");
			if($(toolBar).hasClass("active")){
				$(ei.paper).addClass("landscape");
			}else{
				$(ei.paper).removeClass("landscape");
			}
			
		}
	},

	print : {//打印
		title 	: '打印',
		iclass  : 'edui-icon edui-icon-print2',
		shortcutKey: 'p',
		click : function(ei){
			ei.window.print();
        	//common.open(base+"/common/print",{model : "medicalPrint", content : ei.getContent()});
		}
	},

	cancelPage : {//取消分页
		title 	: '取消分页',
		iclass  : 'edui-icon edui-icon-clearpage',
		click	: function () {
        	emrNode.cancelPage();
        	$.call(this,window.parent.refreshTree);//重新绑定病历树结构
        }
	},
	
	fullscreen  : {//全屏
		title 	: '全屏',
		iclass  : 'edui-icon edui-icon-fullscreen',
		click	: function(ei){
			$(ei.container).toggleClass("fullscreen_emr");
		}
	},
	specialchar : {
		title 	: '插入特殊字符',
		shortcutKey: 'i',
		iclass	: "edui-icon edui-icon-spechars",
		ui : function (ei){
			emr_editor_plugin_ui.specialChar(ei);
			return ei.toolbar_iue(this);
		},
		click	: function (ei,toolBar) {
			var p = $(toolBar).position();
			$(".ei-special-char-div").show().css({top:p.top + 25,left:p.left+3});
		}
	},
	_expressions : {
		title 	: '插入公式',
		iclass	: "edui-icon edui-icon-formula",
		click	: emr_editor_plugin_ui.expressions
	},
	insertImage : {
		title 	: '插入图片',
		iclass	: 'edui-icon edui-icon-insertimage',
		click	: function (ei,toolBar) {
			layer.open({
				type : 2,
				title:"选择文件",
				shadeClose:true,
				content:base+"/config/explorer",
				area : ["900px","500px"],
				btn: ['确定'],
				yes : function (index, layero) {
					var iframeWin = window[layero.find('iframe')[0].name];
					explorerCallback(iframeWin.fileName);
					layer.close(index);
				}
			});
		}
	},
	insertTable : {
		title 	: '插入表格',
		iclass  : "edui-icon edui-icon-inserttable",
		ui : function (ei){
			emr_editor_plugin_ui.euiTable(ei);
			return ei.toolbar_iue(this);
		},
		click	: function (ei,toolBar) {
			var p = $(toolBar).position();
			$(".ei-table-div").show().css({top:p.top + 25,left:p.left+3});
		}
	},

	save : {
		title 	: '保存',
		iclass	: 'edui-icon edui-icon-save'
	}
	
};

