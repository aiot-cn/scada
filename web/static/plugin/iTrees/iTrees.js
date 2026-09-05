var iTrees = function(olul, param, options) {
	this.param = param || {};
	this.options = options || {};
	if(this.options.baseOption){
		this.options = $.extend(options.baseOption,options);
	}
	this.olul = $(olul).addClass("tree");
	this._olul = this.olul.get(0);
	this.form = $('form[data-for="' + this._olul.id + '"]');
	this._form = this.form[0];
	this.submitButton = this.form.find("[type='submit']");
	this.layerOption = $(this.form).data("layer") ? eval('(' + $(this.form).data("layer") + ')') : {};

	this.labelFeild = options.label || "name";
	this.idFeild = options.id || "id";
	this.parentIdFeild = options.parentId || "parentId";
	this.expendLevel = options.expendLevel || 2;//默认展开层级

	this.selected = null;//当前选中的节点
	this.data = null;//当前选中的数据

	this.hasCheck = options.hasCheck;//是否有复选框

	this.prepareEvent();

	this.isFiltered = "isFiltered";
	this.map = {};

	this.json = null;
	this.number = 0;

	//load data;
	if (this.options.getController && this.options.loadOnInit !== false) {
		this.load(param);
	}
};

//参数方法的统一调用
iTrees.prototype.callback = function (fName,p1,p2,p3) {
	var f = this.options[fName];
	if(f){
		return f.call(this,p1,p2,p3);
	}
};

//绑定相关操作
iTrees.prototype.prepareEvent = function() {
	var _this = this;
	if (this.submitButton.length == 0){
		this.submitButton = $("<button type='submit' style='display:none'>提交</button>").appendTo(this.form);
	}
	var toolbar = $("[data-toolbar='"+this._olul.id+"']").addClass("tree-toolbar");
	var create = $("<i class='layui-icon layui-icon-add-1' title='添加'></i>").click(function (){
		_this.openDialog();
	});
	var edit = $("<i class='layui-icon layui-icon-edit' title='修改'></i>").click(function (){
		if(_this.data)
			_this.openDialog(1);
		else
			layer.msg("请先选择要编辑的项目");
	});
	var remove = $("<i class='layui-icon layui-icon-delete' title='删除'></i>").click(function (){
		var d = _this.data;
		if(!d){
			layer.msg("请先选择要删除的项目");
			return;
		}
		layer.confirm('确定删除 ' + d[_this.labelFeild], {icon: 3}, function(index){
			layer.close(index);
			if(_this.callback("beforeDelete",d)){
				return;
			}
			_this.ajax(_this.options.delController,d,function(json){
				_this.removeRecord(d);
			});

		});
	});

	if(this.options.create)
		toolbar.append(create);
	if(this.options.edit)
		toolbar.append(edit);
	if(this.options.remove)
		toolbar.append(remove);

	var refresh = $("<i class='layui-icon layui-icon-refresh' title='刷新'></i>").appendTo(toolbar).click(function () {
		_this.load(_this.param);
	});
	var search = $("<input class='tree-search' placeholder='搜索'>").appendTo(toolbar).keyup(function () {
		_this.filter(this.value);
	});

	$('[data-event="'+this._olul.id+'-search"]').keyup(function () {
		_this.filter(this.value);
	});
	if(this._form)
		this._form.onsubmit = function(event){
			event.preventDefault();

			var params = _this.formJSON();

			if(params){
				_this.saveData(params);
			}else{
				toastr.error("","参数不合法");
			}

		}
};

iTrees.prototype.ajax = function(url,param,callBack,option) {
	var _this = this;
	$.ajax({
		url : url,
		data : param,
		contentType : _this.options.contentType || "application/x-www-form-urlencoded",
		success : function(json) {
			if(json.success !== false){
				if (callBack) {
					callBack(json);
				}
			}else{
				_this.toastr.error(json.message);
			}
		},
		error : function(jqXHR,textStatus,errorThrown) {
			_this.toastr.error(errorThrown.message,"加载数据失败:"+textStatus);
		},
		complete : function (XHR, TS) {
			XHR = null;
		}
	});
};

//加载数据
iTrees.prototype.load = function(param) {
	var _self = this;
	this.data = null;
	this.selected = null;
	this.ajax(this.options.getController,param,function (json) {
		_self._onLoaded(json);
		_self.callback("callback",json);
	});
};

iTrees.prototype.saveData = function(data) {
	var _self = this;
	this.ajax(this.options.saveController,data,function (json){
		_self.updateRecord(json.data);
	});
};

iTrees.prototype.delData = function(data) {
	var _self = this;
	this.ajax(this.options.delController,data,function (json){
		_self.removeRecord(data);
	});
};

iTrees.prototype._onLoaded = function(json) {

	this.clear();
	var t1 = new Date().getTime();
	var _self = this;
	_self.json = json.list || json;

	//load json
	if (!_self.json) {
		console.warn("iTrees无加载数据");
		return false;
	}

	$(_self.json).each(function() {
		_self.insertRecord(this);
	});

	$(_self.json).each(function (){
		_self.transform(this);
	});

	this.expendToLevel(this.expendLevel);
	var t2 = new Date().getTime();
	console.debug(_self._olul.id+"树本次加载"+_self.json.length+"条数据，共耗时"+(t2 - t1)+"毫秒");

	this.callback("loadAfter",_self.json);
};

iTrees.prototype.transform = function (data){

	var $li = this.map[data[this.idFeild]];
	var parentId = data[this.parentIdFeild];

	if (parentId != null && this.map[parentId] != null) {

		var parentUl = this.map[parentId].find("ul:first");
		if (parentUl.length > 0) {
			try {
				$li.appendTo(parentUl);
				this.map[parentId].addClass("folder");
			} catch (e) {
			}
		}
	} else {
		$li.appendTo(this.olul);
	}
}

iTrees.prototype._render = function($span, data) {
	if (this.param._render) { //customed render
		this.param._render($span, data);
	} else { //default render

		var label = data[this.labelFeild];

		if (label == null) {
			label = "";
		}

		var id = data[this.idFeild];

		$span.parent().attr("title", label);

		$span.html(label);
		//$label = $(label).appendTo(span);

	}

};

//remove a record from table.
iTrees.prototype.removeRecord = function(data) {
	var id = data[this.idFeild];
	var $li = this.map[id];
	this.map[id] = null;
	$li.remove();

	var parentId = data[this.parentIdFeild];
	this.caluStatus(this.map[parentId]);
};

//update a record in table.
iTrees.prototype.updateRecord = function(data) {
	var li = this.map[data[this.idFeild]];
	var li2 = this.insertRecord(data);
	if(li){
		li.replaceWith(li2);
	}else{
		this.transform(data);
	}
};

//insert a record in table.
iTrees.prototype.insertRecord = function(data) {
	var _self = this;

	var $li = $('<li><i data-id="'+data[this.idFeild]+'"></i>'
		+(this.hasCheck?'<input type="checkbox">':'')
		+'<span></span><ul></ul>' +
		'</li>');
	var li = $li[0];
	li.data = data;
	var span = $li.find("span:first");
	try {
		this._render(span, data);
	} catch (e) {
		console.error(e);
	}

	this.refreshIcon(li);

	//$li.appendTo(this.olul);

	$li.children('i:first').click(function() {
		$(this).parent().toggleClass("opened");
	});

	span.mousedown(function(event) {
		_self.olul.find("li span").removeClass("selected");
		$(this).addClass("selected");
		_self.selected = li;
		_self.data = data;
		_self.callback("onSelect",data,li);
	});

	if (this.options.dblclick) {
		span.dblclick(function(){
			_self.options.dblclick(li,data);
		});
	}

	this.map[data[this.idFeild]] = $li;

	return li;
};

iTrees.prototype.getChecked = function(){
	var c = [];
	this.olul.find("input:checked").each(function(){
		c.push(this.parentNode.data);
	});
	return c;
};

iTrees.prototype.getNode = function(id) {
	if (this.map[id] != null && this.map[id].size() > 0) {
		return this.map[id].get(0);
	}
};

iTrees.prototype.get$li = function(id) {
	if (this.map[id] != null && this.map[id].size() > 0) {
		return this.map[id];
	}
	return null;
};



iTrees.prototype.refreshIcon = function(li) {
	if (this.options.icon) {
		try {
			var icon = this.options.icon(li.data);
			if (icon != null && icon.length > 0) {
				$(li).children("i:first").css("background-image",
					"url(\"" + icon + "\")");
			}
		} catch (e) {
		}
	}
};

iTrees.prototype.caluStatus = function(li) {
	var $li = $(li);
	if ($li.find("ul li").length == 0) {
		$li.removeClass("folder");
	} else {
		$li.addClass("folder");
	}

};




//重置Tree状态，默认全部节点关闭，只保留选中的节点展开
iTrees.prototype.reset = function() {
	this.olul.find("li").show().removeClass("opened");
	this.expendToLevel(this.expendLevel);
	if (this.selected) {
		this.expendTo(this.selected.data);
	}
};

iTrees.prototype.filter = function(serstring) {
	var _self = this;
	if (serstring.length == 0) {
		this.reset();
		return;
	}
	var sers = serstring.toUpperCase().split(" ");
	//this.olul.find("li").hide();

	this.olul.find("li").each(function(){
		var show = true;
		for(var i = 0;i<sers.length;i++){
			show = this.innerText.toUpperCase().indexOf(sers[i]) > -1 && show
		}
		if(show){
			$(this).show().addClass("opened");
		}else{
			$(this).hide();
		}
	});



};

//展开到某一个节点
iTrees.prototype.expendTo = function(data) {
	var a = this.getLiPath(data);
	if (a.length > 1) {
		a.pop();
	}
	$(a).addClass("opened");
	return a;
};

//展开所有节点
iTrees.prototype.expendAll = function() {
	this.olul.find("li").addClass("opened");
};

//闭合所有节点
iTrees.prototype.collapseAll = function() {
	this.olul.find("li").removeClass("opened");
};

//展开到某一层
iTrees.prototype.expendToLevel = function(level) {
	if(!level){
		return;
	}
	var str = "#" + this._olul.id + " > li";
	for (; level > 1; level--) {
		$(str).addClass("opened");
		str += " > ul > li";
	}
};

//选中一个节点
iTrees.prototype.select = function(data) {

	if (data.nodeName != null) {
		if (data.nodeName != "LI") {
			data = $(data).parentsUntil("ul", "li");
		}
		data = data.prop("data");
	}

	var $li = this.map[data[this.idFeild]];
	$li.find("span").eq(0).mousedown();
};

//清除树上的所有数据和内容
iTrees.prototype.clear = function() {
	this.olul.empty();
	this.json = null;
	this.isFiltered = "isFiltered";
	this.selected = null;
	this.map = {};

	this.json = null;
	this.number = 0;
};

/**
 * 重置表单，把表单内的所有值都设置为缺省值。
 */
iTrees.prototype.resetForm = function () {

	this.form.find(":input").each(function () {
		if (this.getAttribute("data-clear") == "false") {
			return;
		}

		var def = this.getAttribute("data-default");
		if(def == "DATA"){
			def = new Date().format("yyyy-MM-dd");
		}
		if(def == "DATATIME"){
			def = new Date().format("yyyy-MM-ddThh:mm");
		}
		if(def && def[0] == "("){
			def = eval(def);
		}
		if(this.tagName == "SELECT"){
			if(def){
				this.value = def;
			}else{
				this.selectedIndex = 0;
			}
			$(this).change();
		}else if(this.type == "file"){
			$(this).val("");
			$(this).change();
		}else if(this.type == "color"){
			this.value = "#000000";
		}else{
			this.value = def || "";
		}
	});

	this.callback("afterResetForm");
};

iTrees.prototype.submitForm = function () {
	this.form.find("[type='submit']")[0].click();
};

iTrees.prototype.openDialog = function(type){
	var _self = this;
	_self.resetForm();

	if(type){
		this.setForm(this.data);
	}else if(this.data){
		this._form[this.parentIdFeild].value = this.data[this.idFeild];
	}


	layer.open({
		type: 1,
		title: (_self.layerOption.title || "") + " " +  (type ? "修改" :"添加"),
		btn: ['确定','取消'],
		content:_self.form, //捕获的元素
		area : _self.layerOption.area || ["auto","auto"],
		cancel: function(index){
			layer.close(index);
		},
		yes : function(index){
			layer.close(index);
			_self.submitForm();
		}
	});

};

iTrees.prototype.formJSON = function() {
	var json = {};
	this.form.find(":input").each(function() {
		if (this.name && !this.disabled && !$(this).attr("data-ignore")) {
			var val = (this.value+"").trim();

			if (this.validity.valid) {
				//this.placeholder = "";
			} else {
				json = null;
				//this.placeholder = this.validationMessage;
				console.error(this);
				if($(this).is(":visible"))
					this.focus();
				else
					toastr.error(this.validationMessage);
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
			}else if (this.type == "datetime-local"){
				if(this.dataset.type == "long"){
					json[this.name] = val ? (new Date(val).getTime()/1000 - 8*60*60) : "";
				}else{
					json[this.name] = val;
				}
			}else{
				json[this.name] = val;
				if(this.dataset.type=="like"){
					json[this.name] = "%" + val + "%";
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

	for(k in json){
		return json;
	}
	return false;
};

iTrees.prototype.setForm = function (data) {
	if (!data)
		return;

	this.form.find(":input").each(function (){
		var value =  data[this.name];
		if(value == undefined)
			return true;

		if (this.type == "datetime-local") {
			if(typeof(value) == "number"){
				value = new Date(value*1000).format("yyyy-MM-ddThh:mm");
			}else{
				value = value.replace(" ","T");
			}
		}

		if (this.type == "date"){
			value = /[\d]{4}-[\d]{2}-[\d]{2}/.exec(value);
		}

		if (this.type != "file"){
			this.value = value;
		}

		if(this.tagName == "SELECT"){
			$(this).change();
		}

		if (this.control) {
			this.control();
		}
	});

};

//提示消息
iTrees.prototype.toastr = {
	success : function(msg,title){
		layer.msg("["+title+"]"+msg, {icon: 1});
	},
	error : function(msg,title){
		layer.alert("["+title+"]"+msg, {icon: 2});
	}
};