/**
 * 公共JS文件
 */
var common = {
	ajax : function(url, data, callback, options) {
		options = options || {};
		if(options.maskType !== undefined){
			layer.load(options.maskType, {shade: [0.5, '#393D49']});
		}
		var p = {
			type : options.type || 'POST',
			url : url,
			timeout: options.timeout,
			async : options.async !== false,// 默认为异步ajax
			traditional : true,
			data : options.contentType == "application/json" ? JSON.stringify(data) : data,
			beforeSend : options.beforeSend || null,
			jsonp : options.jsonp,
			//dataType : options.dataType || 'json',//预期服务器返回的数据类型
			contentType : options.contentType || "application/x-www-form-urlencoded",
			headers : options.headers,
			success : function(json) {
				var c = false;
				var success = null;
				if(json instanceof Object)
					success = json.success;

				//返回错误时不执行（或者允许错误执行）
				if (callback && (success !== false || options.callError)) {
					try{
						c = callback(json,json != null ? json.data : null);
					}catch (e) {
						console.error(e);
						layer.alert("回调错误 "+e.name + "：" + e.message,{icon : 2});
						return;
					}
				}

				//返回错误时弹出错误，除非回调返回true
				if(success === false && !c){
					if(options.errorCallback){
						options.errorCallback();
					}else{
						var msg = json.message;
						if(json.obj && json.obj.cause){
							msg = json.obj.cause.message;
						}
						layer.alert("执行失败 "+msg,{icon : 2});
					}
				}

				//没有回调，或者回调返回false时执行
				if(success === true && c === false){
					layer.alert("执行成功 "+(json.message || ""),{icon : 1});
				}

			},
			error : function(jqXHR,textStatus,errorThrown ){
				if(options.errorCallback){
					options.errorCallback(jqXHR,textStatus,errorThrown);
				}else {
					console.error(errorThrown);
				}
			},
			complete: function (XHR, TS) {
				XHR = null;
				if(options.maskType !== undefined){
					layer.closeAll('loading');
				}
			}
		};

		if(data instanceof FormData){
			p.processData = false;
			p.contentType = false;
		}
		$.ajax(p);
	},
	isAjaxStop : false,
	ajaxStop : function(fun){
		$(document).ajaxStop(function(){
			if(!common.isAjaxStop){
				common.isAjaxStop = true;
				fun();
			}
		});
	},
	whenNumber : 1,
	whenCount : function (fun,count){
		count = count || 2;
		if(common.whenNumber == count)
			fun();
		common.whenNumber ++;
	},
	ajaxConfirm:function(url,msg,callBack){
		layer.confirm(msg,{
			title:"确认",
			icon : 3,
			yes : function(index){
				common.ajax(url,{},function(data){
					callBack();
					layer.close(index);
					return false;
				});
			}
		});
	},
	//原生ajax
	ajax2 : function(url, data, callback, options){
		var options = options || {};
		var xhr = options.xhr || new XMLHttpRequest();
		xhr.open(options.type || "post",url,true);
		xhr.timeout = options.timeout || 2000;// 超时时间，单位是毫秒。默认0无超时，同步线程无效
		xhr.setRequestHeader("Content-Type",options.contentType || "application/x-www-form-urlencoded");
		xhr.send($.param(data));// Blob, BufferSource, FormData, URLSearchParams,USVString.也支持Document
		xhr.onreadystatechange = function(){
		    if(xhr.readyState==4){//0请求未初始化 1正在加载 2已加载 3交互中 4完成
		        if(xhr.status>=200&&xhr.status<=300||xhr.status==304){
		        	if(callback){
		        		callback(JSON.parse(xhr.responseText));
		        	}
		        }
		    }
		}
	},
	//如果使用了cookie 不建议使用  sessionStorage，因为两者不同步
	sessionBase : function(key,value){
		if(value){
			return layui.sessionData("base",{key:key,value:value});
		}else if(key){
			return layui.sessionData("base")[key];
		}else{
			return layui.sessionData("base");
		}
	},

	jsonCont : function(cont,data, callback, option){
		common.ajax(base+"/json/"+cont,data, callback, option);
	},

	jsonModel : function(modelName,data, callback, options){
		var am = {query : "getList",save : "doSave", del : "doDel",remove : "doSave"};
		var option = options || {};
		var action = option.action || "query";
		var url = base + "/table/"+am[action]+"?tableName="+modelName;
		if(option.action == "remove"){
			data.isRemoved = 1;
		}
		common.ajax(url,data, callback, option);
	},

	jsonSqlCode : function(code,data, callback, options){
		var url = base + "/table/sqlCode/"+code;
		var p = data || {};
		if(p.isRemoved == undefined){
			p.isRemoved = 0;
		}
		common.ajax(url,data, callback, options);
	},

	jsonEnum : function(type,callback){
		common.ajax(base+"/json/getEnum",{"type":type},function(list){
			callback(list);
		},{type : "GET"});
	},

	jsonDevice : function(cont,data,callback,options){
		common.ajax(base+"/device/"+cont,data,callback,options);
	},

	jsonDict : function(dictType,callback,key){
		common.ajax(base+"/json/getDict",{"type":dictType},function(list){
			var k = key || "value";
			var map = {};
			$(list).each(function(){
				map[this[k]] = this;
			});
			callback(list,map);
		},{type : "GET"});
	},

	jsonScript : function(code,data,callback,options){
		var url = base + "/json/script/"+code;
		common.ajax(url,data, callback, options);
	},

	devExec : function(id,method,data,callback,options){
		common.ajax(base+"/device/exec/"+id+"/"+method,data,callback,options);
	},

	devExec2 : function(method,data,callback,options){
		common.devExec(param.d,method,data,callback,options);
	},

	/**
	 * @author taojin
	 * str   筛选器	  表单筛选(不会取disabled)
	 * not	 筛选器	  排除不需要取值的	默认 null
	 * empty boolean  是否清空		默认 false
	 * nul 	 boolean  是否取空值 	默认 false
	 * other : 支持 data-type="like",data-text="[name]",datetime-local自动去T,checkbox自动数组
	 */
	formJSON : function(str,not,empty,nul) {
		var json = {};
		var fromlist = str.selector ? str : $(str).find("input,select,textarea").not(not);
		fromlist.each(function() {
			if (this.name && !this.disabled) {
				var val = this.value.trim();

				if (this.validity.valid) {
					//this.placeholder = "";
				} else {
					json = null;
					//this.placeholder = this.validationMessage;
					//val = "";
					this.focus();
					return;
				}

				if (this.type == "radio") {
					if(this.checked)
						json[this.name] = val;
				}else if (this.type == "checkbox") {
					if(this.checked){
						if(json[this.name]){
							if(json[this.name] instanceof Array){
								json[this.name].push(val);
							}else{
								json[this.name] = [json[this.name]];
								json[this.name].push(val);
							}
						}else{
							json[this.name] = val;
						}

					}

				}else if(this.type == "file"){
					json[this.name] = new FormData(this);
				}else if (nul || val) {

					if (this.type == "datetime-local"){
						json[this.name] = val ? val.substring(0, 16).replace("T", " ")+":00.0" : "";
					}else{
						json[this.name] = this.dataset.type=="like" ? ("%" + val + "%") : val;
					}
					if(this.dataset.text){
						json[this.dataset.text] = this.selectedOptions[0].text;
					}
					if(this.dataset.value){
						json[this.dataset.value] = val;
					}
				}
			}
		});

		if (empty) {
			fromlist.each(function() {
				if (this.type == "radio" || this.type == "checkbox") {
					this.checked = false;
				}else{
					this.value="";
				}
			});
		}

		for(k in json){
			return json;
		}
		return false;
	},

	submit : function(str,callback,option) {
		var json = {},option = option || {};
		var fromlist = str.selector ? str : $(str).find(":input");
		if(option.not)
			fromlist = fromlist.not(option.not);

		fromlist.each(function() {
			if (this.disabled || !this.name)
				return true;
			var val = this.value.trim();

			if (this.validity.valid) {
				//this.placeholder = "";
			} else {
				layer.tips(this.validationMessage,this,{tips:[1,'#FB6D01']});
				this.focus();
				json = false;
				return false;
			}

			if (this.type == "radio") {
				if(this.checked)
					json[this.name] = val;
			}else if (this.type == "checkbox") {
				if(this.checked){
					if(json[this.name]){
						if(json[this.name] instanceof Array){
							json[this.name].push(val);
						}else{
							json[this.name] = [json[this.name]];
							json[this.name].push(val);
						}
					}else{
						json[this.name] = val;
					}

				}

			}else if(this.type == "file"){
				json[this.name] = new FormData(this);
			}else if (option.blank || val) {

				if (this.type == "datetime-local"){
					json[this.name] = val ? val.substring(0, 16).replace("T", " ")+":00.0" : "";
				}else{
					json[this.name] = this.dataset.type=="like" ? ("%" + val + "%") : val;
				}
				if(this.dataset.text){
					json[this.dataset.text] = this.selectedOptions[0].text;
				}
				if(this.dataset.value){
					json[this.dataset.value] = val;
				}
			}
		});

		if(json){
			callback(json);
		}
		return fromlist;
	},

	uploadFile : function(files,options,callback){
		$(files).each(function (){
			//检测文件是不是图片
			/*if (this.type.indexOf('image') === -1) {
                alert("您拖的不是图片！");
                return false;
            }*/
			//拖拉图片到浏览器，可以实现预览功能
			//var img = window.webkitURL.createObjectURL(this);
			var filename = this.name; //图片名称
			var filesize = Math.floor((this.size) / 1024);
			if (filesize > 100*1000) {
				layer.msg(filename + " 超过100Mb.");
				return true;
			}

			var formData = new FormData();
			formData.append("path",options.path);
			formData.append("name",options.name || this.name);
			formData.append("file",this);
			common.ajax(base+"/file/upload",formData,function (json) {
				callback(json);
			},{maskType:1});
		});
	},

	topWin : function (){
		var w = window;
		while (w !== w.parent){
			try {
				w.parent.document;
				w = w.parent;
			}catch (e){
				break
			}
		}
		return w;
	},

	/*
	 * 获取URL参数
	 */
	urlParams : function(query){
		var params = {};
		query = query || window.location.search.substring(1);
        var vars = query.split("&");
        for (var i=0;i<vars.length;i++) {
             var pair = vars[i].split("=");
             params[pair[0]] = decodeURI(pair[1]);
        }
        return params;
	},

	getUrlVal : function (callBack){
		var key = location.pathname.replace(base,"");
		common.jsonModel("configParam",{"type":5,"key":key},function (json){
			var m = {};
			$(json.list).each(function (){
				m[this.code] = this.value;
				if(callBack)
					callBack(m);
				else
					window.urlVal = m;
			});
		});
	},

	setUrlVal : function (code,value){
		var key = location.pathname.replace(base,"");
		common.jsonModel("configParam",{"type":5,"key":key,"code":code,"value":value},function (json){

		},{action:"save",headers: {UK:"type,key,code",UKOver:true}});
	},

	getUrlDict : function (code,callBack){
		var key = location.pathname.replace(base,"")+"/"+code;
		common.jsonModel("sysDict",{"type":"beUrl","name":key},function (json){
			callBack(json.list);
		});
	},

	/*
	 * 模板功能，属性含变量值
	 * 例 vp-href="/a/b/param.d"
	 */
	templateAttr : function(attr){
		var attr2 = attr.split('-')[1];
		$("["+attr+"]").each(function (){
			var v = $(this).attr(attr);
			v =  v.replace(/param\.\w+/g,function(c){return eval(c)});
			$(this).remove(attr).attr(attr2,v);
		});
	},

	renderSelect : function(sq,list,option){
		var p = option || {};
		if(p.empty !== false){
			$(sq).empty();
		}

		if(p.dft != undefined){
			var dft = p.dft;
			if(dft instanceof Array){
				$(dft).each(function(){
					$(sq).append("<option value='"+this.value+"'>"+this.name+"</option>");
				});
			}else{
				$(sq).append("<option value='"+dft+"'>--</option>");
			}
		}

		if(!(list instanceof Array)){
			var arr = [];
			for(var k in list){
				if(list[k] instanceof Object)
					arr.push(list[k]);
				else
					arr.push({"id":k,"name":list[k]});
			}
			arr.sort(function (a,b) {
				if(typeof(a.id) == "number")
					return a.id - b.id;
				else
					return a.id.localeCompare(b.id);
			});
			list = arr;
		}

		$(list).each(function(){
			if(!p.filter || p.filter.call(this)){
				//p.value 值可能为0
				var option = $("<option value='"+ this[p.value || "id"] +"'>"+(this[p.name || "name"] || this)+"</option>");
				$(sq).append(option);
				if(p.callOption){
					p.callOption.call(option,this);
				}
			}
		});

		if(p.selected){
			$(sq).val(p.selected);
		}
	},
	//100 就是默认缩放级别，大于 100 则是放大了，小于 100 则是缩小了。
	detectZoom :function(){
	    var ratio = 0,
	    screen = window.screen,
	    ua = navigator.userAgent.toLowerCase();

	   if (window.devicePixelRatio !== undefined) {
	      ratio = window.devicePixelRatio;
	   }else if (~ua.indexOf('msie')) {
	    if (screen.deviceXDPI && screen.logicalXDPI) {
	      ratio = screen.deviceXDPI / screen.logicalXDPI;
	    }
	  }else if (window.outerWidth !== undefined && window.innerWidth !== undefined) {
	    ratio = window.outerWidth / window.innerWidth;
	  }

	   if (ratio){
	    ratio = Math.round(ratio * 100);
	  }

	   return ratio;
	},
	buildFormItem : function(data){
		var type = data.klass || data.type || "";
		var value = data.value || "";
		var attr = 'class="layui-input" autocomplete="off" name="'+data.code+'"  placeholder="'+(data.placeholder || "")+'"';
		var input = '<input '+attr+'>';
		if(type.indexOf("text") == 0){
			input = '<textarea '+attr+'></textarea>';
		}else if(type.indexOf("Boolean") >= 0){
			input = '<select lay-ignore '+attr+'><option value="false">否</option><option value="true">是</option></select>';
		}else if(type.indexOf("Integer") >= 0){
			input = '<input '+attr+' type="number">';
		}else if(type.indexOf("Float") >= 0){
			input = '<input '+attr+' type="number" step="0.01">';
		}else if(data.select) {
			input = '<select lay-ignore '+attr+'>';
			$(data.select.split(",")).each(function () {
				var op = this.split(":");
				input += "<option value='" + op[0] + "'>" + op[1] + "</option>";
			});
			input += '</select>'
		}
		var node =  '<div class="layui-form-item">'+
					'<label class="layui-form-label">'+data.name+'</label>' +
					'<div class="layui-input-block">'+ input +
					'</div></div>';
		var $n = $(node);
		$n.find(":input").val(data.value);
		return $n;
	},
	strContext : function (str,context){
		return (str || "").replace(/ -+\S+/g,function (p){
			return " <span class='sct-pname'>"+p.slice(1)+"</span>";
		}).replace(/\${.*?}/g,function(p){
			var v = p.slice(2,-1);
			var c = context[v] ? "sct-var" : "sct-var-un";
			return "<span class='"+c+"' title='"+context[v]+"'>"+v+"</span>";
		})
	},
	buildDict : function(dictMap,value){
		var data = dictMap[value];
		if(data){
			var node = $("<span>"+data.name+"</span>");
			node.css({"color":data.color});
			return node[0];
		}
	},
	selectFromDict : function(dictType,sq,p){
		p = p || {};
		common.ajax(base+"/json/getDict",{"type":dictType},function(list){
			common.renderSelect(sq,list,$.extend({name:"name",value:"value"},p));
		},{
			async : p.async,
			type : p.type || "GET"
		});
	},
	selectFromEnum : function(type,sq,p){
		p = p || {};
		common.ajax(base+"/json/getEnum",{"type":type},function(list){
			common.renderSelect(sq,list,$.extend({name:"name",value:"code"},p));
		},{
			async : p.async,
			type : p.type || "GET"
		});
	},
	selectToDict : function(sq){
		var dict = {};
		$(sq).find("option").each(function(){
			dict[this.value] = this.label;
		});
		return dict;
	},
	iTableModel : function(modelName,orderField){
		var tp = {
				getController  : base + "/table/getList?tableName=" + modelName,
				saveController : base + "/table/doSave?tableName=" + modelName,
				delController  : base + "/table/doDel?tableName=" + modelName,
				create : true,
				edit : true,
				remove : true,
				inline_edit : true
		};

		if(orderField){
			tp.orderController = base + "/table/doOrder?tableName=" + modelName;
			tp.order = orderField;
			tp.ASC = orderField;
		}

		return tp;
	},

	open : function(URL, PARAMTERS,target,method) {
        //创建form表单
        var temp_form = document.createElement("form");
        $(temp_form).css("display","none");
        $(document.body).append(temp_form);
        temp_form.action = URL;
        //打开新窗口_blank 本窗口_self
        temp_form.target = target || "_blank";
        temp_form.method = method || "post";
        temp_form.style.display = "none";
        //添加参数  //此为提交多个
        for (var item in PARAMTERS) {
            var opt = document.createElement("input");
            //设置 name 参数
            opt.name = item;
            opt.value = PARAMTERS[item];
            temp_form.appendChild(opt);
        }
        //提交数据
        temp_form.submit();
        $(temp_form).remove();
    },

    downloadFile : function(fileName, content) {
        content = "\ufeff" + content;
        var blob = new Blob([content],{type:"text/plain;charset=UTF-8"});
        var aTag = document.createElement('a');
        aTag.download = fileName;
        aTag.href = URL.createObjectURL(blob);
        aTag.click();
        URL.revokeObjectURL(blob);
    },

	/**
	 * 文件选择
	 * @param param.base 基础路径
	 * @param param.suffix 后缀
	 * @param param.path 默认路径，可向上
	 *  回调
	 */
	openFile : function(param,callback) {
		var path = "";
		if(typeof param == "string"){
			path = param;
		}else if(param.nodeName== "INPUT"){
			var p = {};
			var m = param.dataset;
			for(var k in m){
				p[k] = m[k];
			}
			p.input = param;
			param = p;
		}

		var input = param.input;
		if(input){
			delete param.input;
			if(input.value){
				path = input.value.substring(0,input.value.lastIndexOf("/"));
			}else if(param.path){
				path = param.path;
				delete param.path;
			}
		}
		var url = base+"/config/explorer?"+$.param(param);
		if(path)
			url += "#"+path;
		layer.open({
			type : 2,
			title: param.title || "选择文件",
			shadeClose:true,
			content:url,
			area : param.area || ["900px","500px"],
			btn: ['确定'],
			yes : function (index, layero) {
				var iframeWin = window[layero.find('iframe')[0].name];
				var path = iframeWin.callBackPath();
				input && (input.value = path);
				callback && callback(path.replace("//","/").replace(/#/g,"%23"));
				layer.close(index);
			}
		});
	},

	openFrame : function(title,url,option) {
		var op = option || {};
		layer.open({
			type : 2,
			title: title,
			content:url,
			shade : 0,
			maxmin:true,
			area : op.area || ["80%","80%"]
		});
	},
	msgIndex : 0,
	openMsg : function (msg){
		layer.close(common.msgIndex);
		if(msg.indexOf("{") == -1)
			msg = "{"+msg+"}";
		var json = eval("("+msg+")");

		if(json.type == 2){
			json.content = base + json.content;
		}else if(json.type == 4){
			json.content = json.content.split("\n");
		}if(json.type == 6){
			return eval("("+json.content+")");
		}

		if(!json.area){
			if(json.type == 2)
				json.area = ["80%","80%"];
			else
				json.area = ["auto","auto"]
		}

		if(json.type == 5){
			var url = base + json.content;
			delete json.content;
			common.msgIndex = layer.prompt(json, function(value, index, elem){
				common.ajax(url,{"value":value});
				layer.close(index);
			});
		}else{
			common.msgIndex = layer.open(json);
		}
	},
    D_YMD : function(){
		return new Date().format("yyyy-MM-dd");
	},
	D_YMDHM : function(){
		return new Date().format("yyyy-MM-dd hh:mm");
	},
	tempTree : [],
	arrayToTree : function(list,pidName){
		pidName = pidName || "parentId";
		var m = {};

		$(list).each(function(){
			m[this.id] = this;
		});

		//根节点
		$(list).each(function(){
			if(!m[this[pidName]]){
				this[pidName] = 0;
			}
		});
		return common.arrayIterator(list, 0,pidName,0);
	},
	arrayIterator : function(list,pid,pidName,level){
		var childs = [];
		$(list).each(function(){
			if(this[pidName] == pid){
				var cl = common.arrayIterator(list, this.id,pidName,level);
				if (cl.length > 0) {
					this.children = cl;
				}
				this.level = level;
				childs.push(this);
			}
		});
		return childs;
	},
	//全屏
    fullscreen: function(othis){
      var SCREEN_FULL = 'layui-icon-screen-full'
      ,SCREEN_REST = 'layui-icon-screen-restore'
      ,iconElem = othis.children("i");

      if(iconElem.hasClass(SCREEN_FULL)){
        var elem = document.documentElement;
        if(elem.webkitRequestFullScreen){
          elem.webkitRequestFullScreen();
        } else if(elem.mozRequestFullScreen) {
          elem.mozRequestFullScreen();
        } else if(elem.requestFullScreen) {
          elem.requestFullscreen();
        }

        iconElem.addClass(SCREEN_REST).removeClass(SCREEN_FULL);
      } else {
        var elem = document;
        if(elem.webkitCancelFullScreen){
          elem.webkitCancelFullScreen();
        } else if(elem.mozCancelFullScreen) {
          elem.mozCancelFullScreen();
        } else if(elem.cancelFullScreen) {
          elem.cancelFullScreen();
        } else if(elem.exitFullscreen) {
          elem.exitFullscreen();
        }

        iconElem.addClass(SCREEN_FULL).removeClass(SCREEN_REST);
      }
    },
	copy : function(text){
		if(window.clipboardData){
			window.clipboardData.setData("Text",text);
		}else{
			var aux = document.createElement("input");
			aux.setAttribute("value", text);
			document.body.appendChild(aux);
			aux.select();
			document.execCommand("copy");
			document.body.removeChild(aux);
		}
		layer.msg("已复制");
	},
	selectSite : function(){
		layer.open({
			type: 2,
			title: '进入',
			skin: 'layui-layer-molv',
			//shadeClose: true,
			area: ['400px', '180px'],
			content: base + '/user/selectSite'
		});
	},
	editArgDefine : function(args,callBack){
		var t = "";
		$((args || "").split("\n")).each(function (){
			var t1 = this.split("|");
			t += "<tr><td>"+t1[0]+"</td><td>"+(t1[1]||"")+"</td><td>"+(t1[2]||"")+"</td><td>"+(t1[3]||"")+"</td></tr>";
		});
		layer.open({
			title : "参数设置",
			area : ["600px","auto"],
			btn : ["确定","添加"],
			content : "<table class='ta-args layui-table' lay-size='sm'>" + //contenteditable='true'
				"<thead><th>参数</th><th>名称</th><th>选项</th><th>类型</th></thead><tbody>"+t+"</tbody></table>",
			yes : function(index, layero) {
				var t = "";
				$(".ta-args tbody tr").each(function (){
					var c = this.childNodes;
					if(!c[0].innerText.trim())
						return true;

					for(var i=0;i<c.length;i++){
						t += c[i].innerText + "|"
					}
					t += "\n";
				});
				callBack(t.slice(0,-1));
			},
			btn2 : function(index, layero) {
				$(".ta-args tbody").append("<tr><td></td><td></td><td></td><td></td></tr>");
				return false;
			}

		});
	},
	execArg : function(str,callback,data){
		var d = data || {};
		var html = '<form name="fArgs" class="layui-form layui-form-pane layer-from">';
		var e = true;
		if(str){
			$(str.split("\n")).each(function () {
				var b = this.split("\|");
				var argBean = {code : b[0],name : b[1]};
				if(d[argBean.code] == undefined){
					e = false;
					html += '<div class="layui-form-item">\n' +
						'    <label class="layui-form-label" title="'+argBean.code+'">'+(argBean.code || argBean.name)+'</label>\n' +
						'    <div class="layui-input-block">' +
						'    	<input class="layui-input" name="'+argBean.code+'">' +
						'	</div>\n' +
						'</div>';
				}
			});
			html += "</form>";
		}

		if(e){
			callback(d);
		}else{
			layer.open({
				type: 1,
				title: "参数",
				btn: ['确定','取消'],
				content:html,
				area : ["400px","auto"],
				yes : function(index,lay){
					layer.close(index);
					callback($.extend(d,common.formJSON(lay.find(":input"))));
				}
			});
		}

	},
	fileView : function (){
		$(document.body).on("click","[data-view],[data-pos]",function (){
			var path = this.getAttribute("data-view");
			if(this.nodeName == "IMG"){
				var src = this.getAttribute("src");
				var u = src.substring(0,src.indexOf("?")).split("/").filter(Boolean).slice(2);
				path = u.join("/");
			}
			var name = path.substring(path.lastIndexOf("/")+1);
			var suffix = name.substring(name.lastIndexOf(".")+1).toLowerCase();
			var area = ["80%","80%"];
			if(suffix == "mp3")
				area = ["400px","100px"];

			var tagPos = this.getAttribute("data-pos");
			if(tagPos)
				path += "?tagPos="+tagPos;
			layer.open({
				type : 2,
				btn : false,
				shade : 0,
				title: name,
				content : base + "/view/" + path,
				area : area,
				scrollbar: false,
				maxmin: true
			});
		});
	},
	imgZoom : function (base64,width,callback) {
		var image = new Image();
		image.src = "data:image/jpeg;base64,"+base64;
		image.onload = function () {
			if(width){
				height = image.height * (width / image.width);
			}
			if(height){
				width = image.width * (height / image.height);
			}

			var canvas = document.createElement('canvas');
			var context = canvas.getContext('2d');
			canvas.width = width;
			canvas.height = height;
			context.drawImage(image, 0, 0, width, height);
			var data = canvas.toDataURL('image/jpeg').replace("data:image/jpeg;base64,","");
			callback(data);
		};
	},
	/**
	 * JS年龄计算函数，返回数组
	 * t1 string 起始时间
	 * t2 string 截止时间 默认为系统当前时间
	 * return [] 按精确度计算多个
	 */

	getAge:function(t1,t2) {
		var ages = [];
		if(!t1)
			return "未知";

		if(typeof(t1) == "string")
			t1 = t1.replace(/-/g, "/");

		var s1 = new Date(t1);
		var s2 = new Date();
		if(t2){
			if(typeof(t2) == "string")
				t2 = t2.replace(/-/g, "/");
			s2 = new Date(t2);
		}
		var sec = parseInt(s2.getTime()/1000 - s1.getTime()/1000);
		if(sec < 60){
			return sec + "秒";
		}
		if(sec < 60 * 60){
			var min = parseInt(sec / 60);
			return min + "分" + (sec - min * 60) + "秒";
		}
		if(sec < 24 * 60 * 60){
			var hour = parseInt(sec / 3600);
			return hour + "时" + parseInt((sec - hour * 3600)/60) + "分";
		}

		var getDays = function(year,month){
			var days2 = year % 4 == 0 ? 29 : 28;
			var days = [31,days2,31,30,31,30,31,31,30,31,30,31];
			return days[month];
		};

		var minutes,hours,days,months;
		if(s1.getMinutes() > s2.getMinutes()){
			minutes = s2.getMinutes() - s1.getMinutes() + 60;
			s2.setHours(s2.getHours() - 1 );
		}else{
			minutes = s2.getMinutes() - s1.getMinutes()
		}

		if(s1.getHours() > s2.getHours()){
			hours = s2.getHours() - s1.getHours() + 24;
			s2.setDate(s2.getDate() - 1);
		}else{
			hours = s2.getHours() - s1.getHours();
		}

		if(s1.getDate() > s2.getDate()){
			s2.setMonth(s2.getMonth() - 1);
			days = s2.getDate() - s1.getDate() + getDays(s2.getFullYear(),s2.getMonth());
		}else{
			days = s2.getDate() - s1.getDate();
		}

		if(s1.getMonth() > s2.getMonth()){
			months = s2.getMonth() - s1.getMonth() + 12;
			s2.setFullYear(s2.getFullYear() - 1);
		}else{
			months = s2.getMonth() - s1.getMonth();
		}

		var years = s2.getFullYear() - s1.getFullYear();


		if(years > 0){
			ages.push(years+"岁");
			if(years <= 3){
				ages.push(years+"岁"+months+"月");
			}
		}
		if(years == 0 && months > 0){
			ages.push(months+"月");
			if(months <= 6){
				ages.push(months+"月"+days+"天");
			}
		}

		if(years == 0 && months == 0 && days > 0){
			ages.push((days + 1)+"天");
			if(days <= 7){
				ages.push(days+"天"+hours+"小时");
			}
			if(days <= 3){
				ages.push(days+"天"+hours+"小时"+minutes+"分钟");
			}

		}
		if(years == 0 && months == 0 && days == 0 && hours > 0){
			ages.push(hours+"小时"+minutes+"分钟");
		}
		if(years == 0 && months == 0 && days == 0 && hours == 0){
			ages.push(minutes+"分钟");
		}

		return ages[ages.length-1];

	},
	netInfo : function(localIP){
		var p = {};
		var h = location.hostname;
		var f1 = function (){
			if(!localIP)
				return false;
			var p1 = localIP.substring(0,localIP.lastIndexOf("."));
			var p2 = h.substring(0,h.lastIndexOf("."));
			return p1 == p2;
		}
		if(h.indexOf("127.0.0") == 0 || h.indexOf("localhost") == 0 || f1()){
			p.type = "local";
		}else if(/^(\d{1,3}\.){3}\d{1,3}/.test(h)){
			p.type = "lan";
		}else{
			p.type = "internet";
		}
		return p;
	},
	imgSuffix : ["jpg","jpeg","bmp","png","tif","tiff","webp","dng","mpo","pfm"],
	isImg : function (name){
		if(!name)
			return false;
		if(typeof name == "object")
			name = name.name;
		if(typeof name != "string")
			return false;
		var v2 = name.slice(name.lastIndexOf(".")+1).toLowerCase();
		return common.imgSuffix.indexOf(v2) > -1;
	},

	addDevExtend : function (m1,op){
		common.jsonCont("getRMenu",{},function (list){
			$(list).each(function (){
				var devMenu = this;
				var arr = m1;
				var nameArr = devMenu.name.split("/");
				$(nameArr).each(function (index,val){
					var menu = arr.filter(function(a){return a.title==val})[0];
					if(!menu){
						menu = {
							title: val
						}
						if(index + 1 == nameArr.length){
							menu.click = function (){
								common.devExec(devMenu.devId,"menuClick",
									{"menu":devMenu.menu,"val":op.getVal(this)},op.callback,{maskType:1});
							};
							menu.check = function(node){
								var type = op.getType(node);
								if(!type)
									return false;
								if(!devMenu.suffix)
									return true;
								return devMenu.suffix.split(",").indexOf(type) > -1;
							}
						}
						arr.push(menu);
					}
					if(!menu.sub)
						menu.sub = [];
					arr = menu.sub;
				});

			});
		});
	},

	isWindowsBrowser : function (){
		var platform = navigator.platform || '';
		var userAgent = navigator.userAgent || '';
		return platform.indexOf('Win') !== -1 ||
			userAgent.indexOf('Windows') !== -1 ||
			userAgent.indexOf('Win32') !== -1 ||
			userAgent.indexOf('Win64') !== -1;
	}

};

// 扩展Date的format方法
Date.prototype.format = function(format) {
	if(isNaN(this.getTime()))
		return "";
	var week = "日一二三四五六";
	var o = {
		"M+" : this.getMonth() + 1,
		"d+" : this.getDate(),
		"h+" : this.getHours(),
		"m+" : this.getMinutes(),
		"s+" : this.getSeconds(),
		'W+': week[this.getDay()],
		"q+" : Math.floor((this.getMonth() + 3) / 3),//季度
		"S" : this.getMilliseconds()
	};
	if (/(y+)/.test(format)) {
		format = format.replace(RegExp.$1, (this.getFullYear() + "")
				.substr(4 - RegExp.$1.length));
	}
	for ( var k in o) {
		if (new RegExp("(" + k + ")").test(format)) {
			format = format.replace(RegExp.$1, RegExp.$1.length == 1 ? o[k]
					: ("00" + o[k]).substr(("" + o[k]).length));
		}
	}
	return format;
};

//在需要的地方进行hack，否则会导致支持的浏览器bug
var hack  = {
	ArrayFrom : function () {
		if(Array.from)
			return;
		Array.from = function (obj){
			var a = [];
			for (var k in obj){
				a.push(obj[k]);
			}
			return a;
		};
	}
};





