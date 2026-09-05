<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>

<!doctype html>
<html>
	<head>
		<title>脚本编辑</title>
		<%@include file="../../common/page_head.jsp" %>
		<script>
			var require = {
				paths: { vs: '${res}/plugin/monaco-editor/0.45.0/min/vs' },
				'vs/nls': { availableLanguages: { '*': 'zh-cn' } }
			};
		</script>

		<script src="${res}/plugin/monaco-editor/0.45.0/min/vs/loader.js"></script>
		<script src="${res}/plugin/monaco-editor/0.45.0/min/vs/editor/editor.main.nls.zh-cn.js"></script>
		<script src="${res}/plugin/monaco-editor/0.45.0/min/vs/editor/editor.main.js"></script>
		<script src="${res}/plugin/monaco-editor/0.45.0/min/vs/language/typescript/tsWorker.js"></script>
		<script src="${res}/js/define/import.js"></script>
		<link rel="stylesheet" href="${res}/plugin/monaco-editor/0.45.0/min/vs/editor/editor.main.css">

		<style type="text/css">
			#editor{
				position: absolute;
				top:50px;
				left: 0;
				right: 0;
				bottom: 0;
			}
			.header{
				border-bottom: 1px solid #ccc;
				padding-bottom: 10px;
			}
			.monaco-editor .view-overlays .current-line{
				display: none;
			}


			.hide-tool .header{
				display: none;
			}
			.hide-tool #editor{
				top:0;
			}
			.hide-dor .decorationsOverviewRuler{
				display: none !important;
			}
		</style>
	</head>

	<body class="${param.style}">
	<TEXTAREA id="code" name="code" style="display: none">${param.value}</TEXTAREA>
	<div class="header">
		主题
		<select onchange="monaco.editor.setTheme(this.value)">
			<option value="vs">Visual Studio</option>
			<option value="vs-dark">Visual Studio Dark</option>
			<option value="hc-black">High Contrast Dark</option>
		</select>
		字体大小
		<select onchange="mo.updateOptions({'fontSize':this.value})">
			<option value="14">14px</option>
			<option value="16">16px</option>
			<option value="18">18px</option>
			<option value="20">20px</option>
			<option value="22">22px</option>
		</select>
		<span style="float: right">
			<button class="layui-btn layui-btn-sm layui-btn-normal layui-btn-info" onclick="saveScript()">
				<i class="layui-icon layui-icon-ok"></i> 保存
			</button>
			<button class="layui-btn layui-btn-sm layui-btn-warm" onclick="openArg()">
				<i class="layui-icon layui-icon-release"></i> 运行
			</button>
		</span>
	</div>
	<div id="editor"></div>
	<div class="lay-con form-arg">
		<form name="fm" class="layui-form layui-form-pane form-label-wider" action="">
			<div class="form-item"></div>
		</form>
	</div>
	</body>
	<script>
		//Monaco Editor 实例
		var mo;
		var sysScript,libExt;
		var $formItem = $(".form-item");
		var T1 = setInterval(regLib,5000);
		var options = {
			value: document.getElementById("code").value, // 设置编辑器初始内容
			language: 'javascript', // 设置编辑器语言
			automaticLayout: true, // 自动调整布局
			readOnly: false, // 是否只读
			//是否显示codeLens 是一种在代码中显示额外信息的功能。它通常用于显示与代码相关的元数据、测试覆盖率、Git提交信息等。每个CodeLens指示器通常位于代码行的顶部，并提供相关操作或指示。通过自定义CodeLens配置，您可以根据自己的需求显示或隐藏特定的指示器。
			codeLens: true,
			/**
			 * monaco.editor.setTheme(newTheme)
			 * 1.vs 白色主题（默认），全称是 Visual Studio
			 * 2.vs-dark 是黑色主题 全称 Visual Studio Dark
			 * 3.hc-black 高对比度的黑色主题。 全称 High Contrast Dark
			 */
			theme: 'vs',
			fontSize: 14, // 设置字体大小
			fontFamily: 'Courier New', // 设置字体
			wordWrap: 'on', // 设置自动换行

			minimap: {
				enabled: false // 是否显示迷你地图
			},

			scrollbar: {
				useShadows: false, // 是否使用阴影
				verticalHasArrows: true, // 垂直滚动条是否显示箭头
				horizontal: 'auto', // 设置水平滚动条
				vertical: 'auto' // 设置垂直滚动条
			},

			lineNumbers: 'on', // 是否显示行号
			lineNumbersMinChars: 3, // 设置行号区域最小字符数
			lineDecorationsWidth: 0, // 设置行号装饰宽度
			//glyphMargin: false, // 关闭字形边距以节省空间

			renderWhitespace: false, // 显示空格和制表符 'all'
			renderControlCharacters: true, // 显示控制字符

			//rulers: [80, 120], // 设置标尺

			lightbulb: {
				enabled: true // 是否显示灯泡建议
			},

			contextmenu: true, // 是否启用右键菜单

			folding: true, // 是否启用代码折叠

			suggest: {
				showIcons: true, // 是否显示建议的图标
				filteredTypes: { keyword: false, snippet: true, color: true, reference: true } // 过滤建议类型
			},

			quickSuggestions: {//快速建议
				other: true,
				comments: false,
				strings: true
			}
		};

		common.jsonCont("getAoMethods",{},function (json){
			for(var c in json){
				var m = "interface DT_"+c+" {\n";
				m += buildMethod(json[c]);
				m +="\n}\n";
				libSource += m;
			}
		});

		common.jsonModel("tDevice",{"isRemoved":0},function (json) {
			var m = "declare class dev {\n";
			$(json.list).each(function () {
				m += "\t/**\n";
				m += "\t* "+this.name+"\n";
				m += "\t*/\n";
				m += "\t static " + this.deviceType+"_"+this.id+":DT_"+this.deviceType+";\n";
			});
			m +="\n}\n";
			libSource += m;
		});

		var klass = ["org.aiot.service.BaseService","org.aiot.service.ConfigService","org.aiot.service.DeviceService","org.aiot.service.PointService",
			"org.aiot.lang.Cache","org.aiot.model.enums.PhaseEnum","org.aiot.model.enums.DictTypeEnum"];
		common.jsonCont("getClassBean",{"klass":klass.join(",")},function (json){
			for(var c in json){

				var m = "interface "+c.substring(c.lastIndexOf(".")+1)+" {\n";
				m += buildMethod(json[c]);
				m +="\n}\n";
				libSource += m;
			}
		});

		if(param.code){
			common.jsonModel("sysScript",{"code":param.code},function (json) {
				sysScript = json.list[0];
			});
		}

		common.ajaxStop(function () {
			mo = monaco.editor.create(document.getElementById('editor'), options);
			if(sysScript)
				loadScript(sysScript);
			addExtraLib(libSource,"station");
		});

		function getValue() {
			return mo.getValue();
		}

		/**
		 * https://github.com/DefinitelyTyped/DefinitelyTyped/blob/master/types/jquery/v2/index.d.ts
		 *
		 */
var libSource = `
var BD:BaseDevice;

var BS:BaseService;
var CS:ConfigService;
var DS:DeviceService;
var PS:PointService;

var CACHE:Cache;
var DICT:DictTypeEnum;
var PHASE:PhaseEnum;

function print(...str);

class Java{
	/**
	* @deprecated 建议使用declare来导入类，以确定导入类型
	*/
	static type(c:string):class;
}

/**
* @deprecated 建议使用declare来导入类，以确定导入类型
*/
class Packages{

}

interface BaseDevice{
	/**
    * 获取设备实例
    * @deprecated 建议使用dev来获取，以确定设备类型
    */
    static getInstance(devID:number):BD;
}
`;

		//JavaScript的基本数据类型有数值型Number、字符串型String、布尔型Boolean以及两个特殊的数据类型undefined和null。
		var castor= {
			"[B" : "byte[]",
			"List" : "Object[]",
			"int" : "number",
			"Integer" : "number",
			"float" : "number",
			"Float" : "number",
			"double" : "number",
			"Double" : "number"
		};
		function reReturn(a) {
			var b = a.lastIndexOf(".");
			if(b > 0){
				a = a.substring(b+1);
			}
			return castor[a] || a;
		}

		// When resolving definitions and references, the editor will try to use created models.
		// Creating a model for the library allows "peek definition/references" commands to work with the library.
		//monaco.editor.createModel(libSource, "typescript", monaco.Uri.parse(libUri));

		monaco.languages.registerCompletionItemProvider('javascript', {
			provideCompletionItems: function (model, position, context, token) {
				var word = model.getWordUntilPosition(position);
				var range = {
					startLineNumber: position.lineNumber,
					endLineNumber: position.lineNumber,
					startColumn: word.startColumn,
					endColumn: word.endColumn,
					lineContent : model.getLineContent(position.lineNumber)
				};
				return {
					suggestions: completionItems(range)
				};
			}
		});

		function completionItems(range) {
			var suggestions = [
				{
					label: '设备实例(dev)',
					insertText: 'dev',
					detail: '选择设备',
					filterText: 'dev sb bd',
					documentation: '设备详请请到设备设置页面',//选项的详细描述，一个字符串或markdown
					preselect: true,//预选中，布尔值，当设置为true时，该选项出现后，就会被默认选中。一个选项列表只会有一个预选中。如果有多个，则选中最匹配的。
					sortText: 'A',//选项列表将会使用该属性来进行排序。
					/**
					 * 选项的类型，是一个枚举值。用于区分不同类型，如函数的类型，变量，不同的类型有不用的icon显示，用于区分可以定义选项的图标。枚举类型为 ： CompletionItemKind
					 一共有26种。Class，Color，Constant，Constructor，Customcolor，Enum，EnumMember，Event，Field，File，Folder，Function，Interface，
					 Issue，Keyword，Method，Module，Operator，Property，Reference，Snippet，Struct，Text，TypeParameter，Unit，User，Value，Variable，
					 */
					kind: monaco.languages.CompletionItemKind.Variable,

					//commitCharacters: ['a', 'b', 'c'], //默认enter选中，增加选中键
					/**
					 * 插入的规则，枚举值CompletionItemInsertTextRule，两个值，
					 *  InsertAsSnippet: 4 将insertText当作一个片段进行插入。
					 *  KeepWhitespace: 1 自动调整多行的插入文本的空格和换行缩进。
					 */
					//insertTextRules: '',

					//tags: [monaco.languages.CompletionItemTag.Deprecated],// 枚举值，一个数组，枚举值只有一个选项 Deprecated: 1 设置后，选项会显示横线，表示不推荐，或废弃。

					/**
					 * 利用command属性可以在作者确定一个选项后，执行一系列的action或command操作，比如配置一个CompletionItem，让其在选中后，执行搜索操作或者执行格式化操作。
					 * 这里的操作只能有一个。而command中最重要的id，我们在 codelend篇已经详细介绍了。内置的154个command id。大部分都可以用。
					 * 如果你要在选择一个选项后，执行格式操作，editor.action.formatDocument
					 */
					/*command: {
						id: 'editor.action.organizeImports',
						title: '选中这个建议选项后，操作'
					},*/
					//用户选中它时，在第一行自动引入某个包，并且在第二行，添加备注。这些东西都可以利用 additionalTextEdits来操作
					/*additionalTextEdits : [
						{
							range: {
								endColumn: '1',
								endLineNumber: '1',
								startColumn: '1',
								startLineNumber: '1',
							},
							text: `我要引入一个包`,
							forceMoveMarkers: true,
						}
					],*/

					range: range //文本要替换的区间，默认是从单词的开始位置（TextDocument.getWordRangeAtPosition）到当前光标的位置。
				},{
					label: '导入类(import)',
					insertText: 'import "\${0}";',
					insertTextRules: monaco.languages.CompletionItemInsertTextRule.InsertAsSnippet,
					filterText: 'import',
					detail: 'import',
					preselect: true
				}
			];

			if(range.lineContent.indexOf("import") > -1){
				for(var i=0;i<javaClasses.length;i++){
					var className = javaClasses[i];
					var shortName = className.substring(className.lastIndexOf(".") + 1);
					suggestions.push({
						label: className,
						insertText: className,
						kind: monaco.languages.CompletionItemKind.Class
					});
				}
			}


			return suggestions;
		}

		function addExtraLib(content,path){
			monaco.languages.typescript.javascriptDefaults.
			addExtraLib(content, "lib."+path+".d.ts");
		}

		function regLib(){
			var pack = [];
			var a = mo.getValue().match(/import\s+['"]?(\w+\.)+\w+['"]?/g);
			for(var i=0;a != null && i < a.length;i++)
				pack.push(a[i].match(/(\w+\.)+\w+/g)[0]);
			var b = pack.join(",");
			if(b && b != libExt){
				libExt = b;
				var source = "";
				common.jsonCont("getClassBean",{"klass":b},function (json){
					for(var c in json){
						var m = "class "+c.substring(c.lastIndexOf(".")+1)+" {\n";
						m += buildMethod(json[c]);
						m +="\n}\n";
						source += m;
					}
					window.t2 = source;
					addExtraLib(source,"aiot.declare");
				});

			}
		}

		function buildMethod(arr){
			var m="";
			for(var i = 0;i<arr.length;i++){
				var j = arr[i];
				m += "\t/**\n";
				m += "\t* "+j.name+"\n";
				var ma = [];
				$(j.arg).each(function () {
					m += "\t* @param "+this.code + " "+ (this.name || "") +"\n";
					ma.push(this.code+":"+reReturn(this.type));
				});
				if(j.deprecated)
					m += "\t* @deprecated\n";
				m += "\t* @returns "+reReturn(j.returnType)+"\n";
				m += "\t*/\n";
				m += "\t" + (j.isStatic ? "static " : "") +
						j.code+"("+ma.join(',')+"):"+reReturn(j.returnType)+";\n";
			}
			return m;
		}

		function loadScript(sc) {
			sysScript = sc;
			mo.setValue(sysScript.function || ("//Hello " + sysScript.code));
		}

		function saveScript() {
			var con = mo.getValue();
			common.jsonCont("execScript",{"id":sysScript.id,"text":con},function () {
				sysScript.function = con;
				layer.msg("保存成功");
			})
		}

		function runScript(args) {
			var con = mo.getValue();
			common.jsonCont("execScript",{"id":sysScript.id,"run":true,"text":con,"args":args},function(json) {
				sysScript.function = con;
				return false;
			})
		}

		function openArg() {
			$formItem.empty();
			var a = sysScript.args;
			if(!a){
				runScript();
				return;
			}

			$(a.split("\n")).each(function () {
				var b = this.split("\|");
				$formItem.append(createInput({
					code : b[0],
					name : b[1]
				}));
			});
			layer.open({
				type: 1,
				title: "参数",
				btn: ['确定','取消'],
				shade: 0,
				content:$(".form-arg"), //捕获的元素
				area : ["500px","auto"],
				yes : function(index){
					var p = [];
					$formItem.find(":input").each(function () {
						p.push(this.value);
					});
					runScript(p.join(","));
				}
			});
		}

		function createInput(argBean) {
			var a = $('<div class="layui-form-item temp">\n' +
					'    <label class="layui-form-label">'+(argBean.code || argBean.name)+'</label>\n' +
					'    <div class="layui-input-block">' +
					'    	<input class="layui-input" name="'+argBean.code+'">' +
					'	</div>\n' +
					'</div>');
			return a;
		}

		function temp(){
			// 获取和设置文本内容：使用 editor.getValue() 方法可以获取编辑器中的文本内容，使用 editor.setValue(newValue) 方法可以设置编辑器的文本内容。
			var currentText = editor.getValue();
			editor.setValue("function goodbye() {\n\tconsole.log('Goodbye, world!');\n}");

			// 语法高亮：可以通过 editor.deltaDecorations 方法添加或移除行内装饰，以实现自定义的语法高亮。
			var decorations = editor.deltaDecorations([], [
				{ range: new monaco.Range(1, 1, 1, 20), options: { isWholeLine: true, linesDecorationsClassName: 'myLineDecoration' } }
			]);

			// 设置语言：使用 editor.setModelLanguage 方法可以设置编辑器的语言，以便进行相应语法高亮和代码提示。
			monaco.editor.setModelLanguage(editor.getModel(), 'typescript');

			// 注册事件监听器：可以通过 editor.onDidChangeModelContent 方法注册事件监听器，以便在内容变化时执行相应的操作。
			editor.onDidChangeModelContent(function(event) {
				console.log("Editor content changed: " + editor.getValue());
			});

			// 代码折叠：使用 editor.setFoldingStrategy 方法可以设置代码折叠策略，实现代码折叠功能。
			editor.setFoldingStrategy(function(context, firstLineNumber, line, lastLineNumber) {
				// Custom folding logic here
			});

			// 自动完成和智能提示：可以通过 editor.createContextKey 方法创建上下文键，以便根据特定的上下文显示自动完成和智能提示。
			var myContextKey = editor.createContextKey('isReadOnly', false);
			myContextKey.set(true);

			// 设置主题：使用 monaco.editor.defineTheme 方法可以定义新的编辑器主题，以及 editor.setTheme 方法可以设置编辑器的主题。
			monaco.editor.defineTheme('myCustomTheme', {
				base: 'vs',
				inherit: true,
				rules: [{ background: 'EDF2F7' }],
				colors: { 'editor.foreground': '#000000' }
			});
			editor.setTheme('myCustomTheme');

			// 布局控制：可以使用 editor.layout 方法手动触发编辑器的重新布局。
			editor.layout();

			// 获取光标位置和选中文本：通过 editor.getPosition 和 editor.getSelection 方法可以获取光标位置和选中的文本内容。
			var currentPosition = editor.getPosition();
			var selectedText = editor.getModel().getValueInRange(editor.getSelection());

			// 代码格式化：可以使用 editor.getAction 方法获取格式化代码的动作，以便执行代码格式化操作。
			var formatAction = editor.getAction('editor.action.formatDocument');
			formatAction.run();

			// 获取编辑器配置：可以通过 editor.getRawOptions 方法获取编辑器的原始配置信息。
			var editorOptions = editor.getRawOptions();

			// 设置编辑器配置：可以使用 editor.updateOptions 方法动态更新编辑器的配置选项。
			editor.updateOptions({ tabSize: 4 });

			// 执行命令：可以通过 editor.executeEdits 方法执行编辑器的编辑操作，比如插入文本、替换文本等。
			editor.executeEdits("my-source", [{ range: new monaco.Range(1, 1, 1, 1), text: "Hello, World!" }]);

			// 获取模型：可以通过 editor.getModel 方法获取编辑器当前的模型（包含文本内容、语言等信息）。
			var model = editor.getModel();

			// 设置模型：可以使用 editor.setModel 方法设置编辑器的模型，以显示不同的文本内容或切换语言。
			var newModel = monaco.editor.createModel("console.log('Hello, Monaco Editor!')", "javascript");
			editor.setModel(newModel);

			// 撤销和重做操作：可以通过 editor.trigger 方法执行编辑器的撤销和重做操作。
			editor.trigger('keyboard', 'redo', null);

			// 获取编辑器实例：可以通过 monaco.editor.getEditor 方法获取现有的编辑器实例，以便对其进行操作。
			var editor = monaco.editor.getEditor('container-id');

			// 设置编辑器的尺寸：可以使用 editor.layout 方法手动触发编辑器的重新布局，以适应容器的尺寸变化。
			editor.layout();

			// 获取光标位置和选中文本：可以通过 editor.getPosition 和 editor.getSelection 方法获取光标位置和选中的文本内容。
			var currentPosition = editor.getPosition();
			var selectedText = editor.getModel().getValueInRange(editor.getSelection());

			// 获取编辑器内容范围：可以使用 editor.getModel().getFullModelRange() 获取编辑器内容的完整范围。
			var fullRange = editor.getModel().getFullModelRange();

			// 格式化选定的文本：可以使用 editor.getAction 方法获取格式化选定文本的动作，以便执行选定文本的格式化操作。
			var formatSelectionAction = editor.getAction('editor.action.formatSelection');
			formatSelectionAction.run();

			// 设置编辑器的语言：可以使用 editor.setModelLanguage 方法设置编辑器的语言。
			monaco.editor.setModelLanguage(editor.getModel(), 'html');

			// 获取编辑器的所有装饰器：可以使用 editor.getAllDecorations 方法获取编辑器的所有装饰器。
			var allDecorations = editor.getAllDecorations();

			// 设置编辑器的装饰器：可以使用 editor.deltaDecorations 方法添加或移除装饰器。
			var newDecorations = editor.deltaDecorations([], [
				{ range: new monaco.Range(2, 1, 2, 20), options: { isWholeLine: true, linesDecorationsClassName: 'myLineDecoration' } }
			]);

			// 获取编辑器的所有动作：可以使用 editor.getActions 方法获取编辑器的所有动作。
			var allActions = editor.getActions();

			// 设置编辑器的内容状态：可以使用 editor.changeDecorations 方法改变编辑器的内容状态。
			editor.changeDecorations(callback);

			// 设置编辑器的焦点：可以使用 editor.focus 方法设置编辑器的焦点。
			editor.focus();

			// 获取编辑器的所有模型：可以使用 monaco.editor.getModels 方法获取所有当前存在的编辑器模型。
			var allModels = monaco.editor.getModels();

			// 注销模型：可以使用 model.dispose 方法注销不再需要的编辑器模型。
			model.dispose();

			// 获取编辑器的所有语言：可以使用 monaco.languages.getLanguages 方法获取所有支持的语言。
			var allLanguages = monaco.languages.getLanguages();

			// 注册新的语言：可以使用 monaco.languages.register 方法注册新的语言支持。
			monaco.languages.register({ id: 'myCustomLanguage' });

			// 获取编辑器的所有主题：可以使用 monaco.editor.getThemes 方法获取所有可用的主题。
			var allThemes = monaco.editor.getThemes();

			// 注册新的主题：可以使用 monaco.editor.defineTheme 方法注册新的编辑器主题。
			monaco.editor.defineTheme('myCustomTheme', {
				base: 'vs-dark',
				inherit: true,
				rules: [{ background: 'EDF2F7' }],
				colors: { 'editor.foreground': '#000000' }
			});

			// 获取编辑器的所有扩展：可以使用 monaco.extensions.getExtensions 方法获取所有可用的扩展。
			var allExtensions = monaco.extensions.getExtensions();

			// 加载扩展：可以使用 monaco.extensions.loadExtension 方法加载新的扩展。
			monaco.extensions.loadExtension('myExtension');

			// 获取编辑器的所有快捷键：可以使用 editor.getActions 方法获取编辑器的所有快捷键动作。
			var allKeybindings = editor.getActions();

			// 设置编辑器的快捷键：可以使用 editor.addCommand 方法添加新的快捷键命令。
			editor.addCommand(monaco.KeyMod.CtrlCmd | monaco.KeyCode.KEY_S, function() {
				console.log('Save command triggered');
			});

			// 获取编辑器的所有语言配置：可以使用 monaco.languages.getLanguageConfiguration 方法获取所有语言的配置信息。
			var allLanguageConfigurations = monaco.languages.getLanguageConfiguration();

			// 设置语言配置：可以使用 monaco.languages.setLanguageConfiguration 方法设置特定语言的配置信息。
			monaco.languages.setLanguageConfiguration('javascript', {
				comments: {
					lineComment: '//',
					blockComment: ['/*', '*/']
				}
			});
		}
	</script>
</html>