/**
 * iTables的构造函数
 * @param atable table html element object
 * @param params
 * @param options 扩展的参数。
 * shortDel 快捷键del删除
 */
var iTables = function(atable, params, options) {
//try {
	var _self = this;
	this.json = [];//表格中的数据
	this.verson = 20210812;//当前版本
	this.params = params || {};
	this.options = options || {};
	this.dict = options.dict || {};
	if(this.options.baseOption){
		this.options = $.extend(options.baseOption,options);
	}
	if(this.options.renderOptions){
		this.options = $.extend(options.renderOptions(),options);
	}
	this.beCreate = this.options.create;

	this.table = $(atable).addClass("itable");
	this._table = this.table.get(0);
	this.tid = this._table.id;
	//tbody
	if (this._table.tBodies.length == 0) {
		this._table.appendChild(document.createElement("tbody"));
	}
	this._tbody = this._table.tBodies[0];

	this.form = $('form[data-for="' + this.tid + '"]');
	this._form = this.form[0];
	this.inputs = null;
	this.searchInput = options.searchInput || $("[data-search='"+this.tid +"']")[0];
	this.buttonCreate = $("[data-itable='create_"+this.tid +"']");
	this.toolbar = $("[data-itable='tool_"+this.tid +"']");
	this.isServerPage = !!this.options.scrollLoad;//是否后台分页模式,目前仅后台分页，前台分页需再增加前台每页数量参数调用page方法
	this.isEditInLine = this.options.inline_edit && !this.options.scrollLoad;
	this.isDblEdit = this.options.dblEdit !== false;
	this.isShortDel = this.options.shortDel !== false;

	this.pageInfo = {
		pageNum : 1,
		pageSize : params.pageSize == undefined ? 500 : params.pageSize,//每页显示数量
		nextPage : null //是否有下一页
	};

	this.order = {
		by : this.options.DESC ? "DESC" : "ASC",
		field : this.options.ASC || this.options.DESC
	};

	this.layerOption = $(this.form).data("layer") ? eval('(' + $(this.form).data("layer") + ')') : {};
	this.layerOption.index = null;
	this.idIndex = 0;//数据没有主键ID时生成IID主键
	this._selectedTr = null;//当前选中的TR
	this._inlineTr = null;//行内编辑的TR
	this.checkedMap = {};//通过复选框选中的TR
	this._data = null;//当前选中的Date
	this.THs = this.table.find("thead tr th");
	this.dragNode = null;//当前拖拽的节点
	this.dragType = null;//当前拖拽的类型
	this.primaryKey = this.options.primaryKey || "id";//主键
	this.arrTemp = [];//临时数组，递归缓存等使用，使用前清空
	this.parentName = this.options.parentName;//上级名称
	this.render = this.options.render || {};//td生成方法
	this.rows = {};//注册 表的TH
	this.columns = [];
	this.colMap = {};
	this.colshow = {};

	this.initColumns();
	this.initTool();
	//this.initShortcutKey();
	this.initScroll();
	this._prepareForm();//初始化表单

	//排序
	if(this.options.hasSort !== false){
		this._prepareSort();
	}

	//prepare search
	this._prepareSearch();

	//行内编辑 下拉加载时没法用行编辑新建
	if (this.isEditInLine) {
		this.prepare_inlineEdit();
	}else{
		this.beCreate ? this.buttonCreate.click(function(){
			_self.createDialog();
		}) : this.buttonCreate.hide();
	}

	//load data;
	if (this.options.getController && this.options.loadOnInit !== false) {
		this.load();
	}

	window.currentItable = null;
	$(document).on("keydown",function (e) {
		if (e.target.nodeName == "INPUT" || window.currentItable != _self)
			return;

		if (_self.isShortDel && e.keyCode == 46) {//DEL
			var arr = [];
			_self.table.find(".row_selected").each(function () {
				arr.push({"IID":this.data.IID,"id": this.data[_self.primaryKey]});
			});
			_self._onRemove(arr);
		}
	});

	this.table.on("dblclick","tr",function (){
		if(_self.options.edit && _self.isDblEdit) {
			if(this.data){
				_self._onEdit(this.data);
			}
		}
	});

	/*} catch(e) {
         toastr.error(e.toLocaleString,"iTables异常");
     }*/
};

//参数方法的统一调用
iTables.prototype.callback = function(fName,p1,p2,p3,p4,p5){
	return this.callFun(this.options[fName],p1,p2,p3,p4,p5);
};

iTables.prototype.callFun = function(fun,p1,p2,p3,p4,p5){
	if(fun)
		return fun.call(this,p1,p2,p3,p4,p5);
};

iTables.prototype.getPrimaryValue = function(data){
	return data[this.primaryKey];
};

iTables.prototype.getChecked = function (){
	var _self = this;
	var list = [];
	for(var k in _self.checkedMap){
		var d = _self.checkedMap[k];
		if(d)
			list.push(_self.checkedMap[k]);
	}
	return list;
}

iTables.prototype.getDataList = function () {
	var arr = [];
	$(this._tbody.children).each(function (){
		arr.push(this.data);
	});
	return arr;
};

iTables.prototype.initColumns = function () {
	var _self = this;
	this.colshow = {};
	try {
		this.colshow = layui.data('itable_'+this.tid).colshow || {};
	}catch (e){
		console.warn("不支持本地缓存");
	}
	this.THs.each(function (index, th){
		var d = $(th).data();
		var field = d.field;
		var show = _self.colshow[field] || _self.colshow[field] == undefined;
		if(!show){
			$(th).hide();
		}
		var c = {
			index		: index,
			th			: th,
			show		: show,					//该列是否显示
			field		: field,
			name		: $(th).text(),
			join		: d["join"],

			class		: d.class,
			format		: d.format,
			translate	: d.translate,
			edit		: d.edit,	//单元格是否可编辑
			tfcol		: d.tfcol,	//行内编辑跨多列的情况
			input		: d.input	//显示的字段和录入的字段不一致的情况
		};
		$(th).addClass(c.class);
		if(d.dict)
			d.type = "dict";

		if(d.type){
			var dt = _self.tdType[d.type];
			if(dt)
				c.type = dt;
			else
				console.error(_self._table.id + " 列类型[type]:"+d.type+" 未定义");
			if(d.type == "checkbox"){
				var checkbox = $("<input type='checkbox' class='it-check'>").click(function (e){
					e.stopPropagation();
					var isChecked = this.checked;
					$(_self._tbody).find(".it-check").each(function (){
						this.checked = isChecked;
						var data = this.parentNode.parentNode.data;
						var pValue = _self.getPrimaryValue(data);
						_self.checkedMap[pValue] = isChecked ?  data : false;
					});

				});
				$(th).append(checkbox);
			}
		}
		if(d.render){
			var dr = _self.options[d.render];
			if(dr)
				c.render = dr;
			else
				console.error(_self._table.id + "列生成[render]:"+d.render+" 未定义");
		}else if(d.field){
			c.render = _self.render[field];
		}
		_self.colMap[field] = c;
		_self.columns.push(c);
	});
	$(this._table.tFoot).find("tr").each(function (){
		$(this.children).each(function (index){
			if(!_self.columns[index].show)
				this.style.display = "none";
		});
	});
};

iTables.prototype.initTool = function () {
	var _self = this;
	var cols = $('<div class="itable-tool-cols"><i class="layui-icon layui-icon-cols"></i></div>').appendTo(this.toolbar);
	var colsPan = $('<ul class="itable-tool-panel"></ul>').appendTo(cols);
	$(_self.columns).each(function(){
		var col = this;
		if(col.field){
			var li = $('<li><i class="layui-icon layui-icon-ok" v="'+(col.show ? 1 : 0)+'"></i> '+this.name+'</li>').appendTo(colsPan);
			li.click(function(){
				var i = $(this).find("i");
				var v = i.attr("v");
				var c = _self.table.find("tr").find('td:eq('+col.th.cellIndex+'),th:eq('+col.th.cellIndex+')');
				if(v != "0"){
					i.attr("v","0");
					_self.colshow[col.field] = col.show = false;
					c.hide();
					//
				}else{
					i.attr("v","1");
					_self.colshow[col.field] = col.show = true;
					c.show();
				}
				layui.data('itable_'+_self.tid, {key: 'colshow',value : _self.colshow});
			});
		}

	});
};

iTables.prototype.initShortcutKey = function () {
	var _this = this;
	_this.table.keyup(function(e){
		if(e.keyCode == 27){//ESC
			_this.table.find(".itable-cancel").click();
		}else if(e.keyCode == 13){//回车
			if(e.target.nodeName == "TEXTAREA"){
				return false;
			}
			_this.submitForm();
			e.preventDefault();
			return false;
		}
	});
	$(this._table.tHead).find("[data-type='edit']").css("cursor","help").attr("title","Enter 提交\nESC 取消");
	//选中行后table会失去焦点，因此只能注册在body上。避免污染事件
	/*$(document.body).keyup(function(e){
		if (e.keyCode == 46){//del
			_this._onRemove(_this._data);
		}
	});*/
};

iTables.prototype.initScroll = function () {
	var _self = this;
	//下拉加载事件
	var scrollLoad = this.options.scrollLoad;
	if(!scrollLoad){
		return;
	}
	//IOS浏览器的iframe大小会随着内容变化而变化，因此iframe及其文档对象永远不会出现滚动条
	if(scrollLoad == document || scrollLoad == document.body){
		console.warn("下拉容器建议采用body子元素，以获取更好的浏览器兼容性");
	}
	$(scrollLoad).scroll(function(){
		var t = this;
		var clientHeight = t.clientHeight || t.documentElement.clientHeight;
		var scrollTop = (t.scrollTop == 0 ? 1 : t.scrollTop) || t.documentElement.scrollTop || window.pageYOffset || t.body.scrollTop;
		var scrollHeight = t.scrollHeight || t.documentElement.scrollHeight || t.body.scrollHeight;

		//到底触发
		if(clientHeight + Math.ceil(scrollTop) >= scrollHeight){
			//如果有下一页
			if(_self.pageInfo.nextPage){
				_self.load(null, {"noClear":true,"isNext":true});
			}else if(_self.pageInfo.nextPage === false){
				layer.msg('我是有底线的~');
			}
		}

	});
};




/**
 * 准备列排序
 */
iTables.prototype._prepareSort = function() {
	var _self = this;
	$(this.columns).each(function(index, column){
		//fill cell depends the TH definition
		var d = column.th.dataset;
		var field = d.field;
		var sort = d.sort;
		if (sort != "false" && (field || sort)) {
			column.th.onclick = function(event) {
				var cellIndex = this.cellIndex;

				var dir = column.th.getAttribute("sorted");
				dir = dir != "ASC" ? "ASC" : "DESC";
				_self.THs.removeAttr("sorted");
				column.th.setAttribute("sorted",dir);

				if(_self.isServerPage){
					_self.order.by = dir;
					_self.order.field = sort || field;
					_self.load();
				}else if(_self.json != null && _self.json.length != 0){
					_self.sort(sort || field, dir);
				}

				/*$(_self._table).find('td').each(function() {
					if(this.cellIndex == cellIndex){
						$(this).addClass("td-checked");
					}
				});	*/
			};
			$(column.th).addClass("itable-sort");
		}
	});
};

/**
 * 打开新建对话框
 */
iTables.prototype.createDialog = function(){
	var _self = this;
	_self.resetForm();

	_self.layerOption.index = layer.open({
		type: 1,
		title: _self.layerOption.title ? _self.layerOption.title + "添加" : false,
		skin: 'ita-layer ita-add ita-'+_self.tid,
		btn: _self.layerOption.btn || ['确定','取消'],
		content:_self.form, //捕获的元素
		area : _self.layerOption.area || ["auto","auto"],
		cancel: function(index){
			layer.close(index);
		},
		yes : function(index){
			_self.submitForm();
		}
	});

};

iTables.prototype.edit = function(){
	this._onEdit(this._data);
};

iTables.prototype.delete = function(){
	this._onRemove(this._data);
};

/**
 * 准备搜索框
 */
iTables.prototype._prepareSearch = function() {
	var _self = this;
	$(this.searchInput).keyup(function () {
		if(_self.isServerPage){
			clearTimeout(_self.filterTime);
			_self.filterTime = setTimeout(function(){
				_self.load();
			},400);
		}else{
			_self.filter(this.value);
		}
	});

};

/**
 * 准备行内编辑功能
 */
iTables.prototype.prepare_inlineEdit = function() {
	var _self = this;

	var tfoot = this._table.tFoot;
	if(!tfoot){
		tfoot = this._table.createTFoot()
	}
	var tr = tfoot.insertRow(-1);
	$(tr).addClass("inline-edit-row");
	this._inlineTr = tr;

	if (!this.beCreate){
		$(tr).hide();
	}

	$(this.columns).each(function(){
		var col = this;
		if(this.tfcol == 0){
			return;
		}
		var th = this.th;
		//new cell
		var td = tr.insertCell(-1);
		if(this.tfcol > 1){
			td.colSpan = this.tfcol;
		}
		td.title = th.innerHTML;
		var field = col.field;
		$(td).addClass(col.class);
		col.show ? $(td).show() : $(td).hide();

		var inputf = col.input || field;
		if (inputf && _self.inputs != null) {
			var $input = _self.inputs.filter("[name='"+inputf+"']");

			if($input.length <= 0) {
				if(_self.options.inputAuto == true){
					$input = $('<input type="text" name="' + field + '"  value=""/>');
				}else{
					$input = $('<span name="' + field + '"></span>');
				}
			}
			//赋值操作在resetForm方法，这里只处理属性
			if($input.attr("type") == "file"){
				$(td).addClass("d-file");
				$(td).append("<span>上传</span>");
				$input.change(function(){
					var arr=this.value.split('\\');
					var fileName=arr[arr.length-1];
					$(td).find("span").html(fileName || "上传");
				});
			}

			$input.attr({"placeholder":$input[0].placeholder || th.innerText || th.title,"form":$input.attr("form") || _self.form.attr("id")});

			$input.appendTo(td);
		} else if ($(th).data("type") == "edit") {

			var b1 = $("<i class='itable-font itable-complete' title='确定' id='"+_self._table.id+"_submit2'></i>");
			b1.click(function(e){
				_self.inputs.filter("[name='"+_self.primaryKey+"']").removeAttr("readonly");
				var firstInput = $(tr).find("input,select").not(":hidden").eq(0);

				//键盘触发
				if(e.clientX == 0 && e.clientY == 0){
					//如果是自动完成
					if(firstInput.hasClass("ui-autocomplete-input")){
						firstInput.val("");
					}
					firstInput.focus();
				}

				_self.submitForm();
			});

			//reset button
			var b2 = $("<i class='itable-font itable-cancel' title='撤销' id='"+_self._table.id+"_cancel2'></i>");
			b2.click(function() {
				_self.inputs.filter("[name='"+_self.primaryKey+"']").removeAttr("readonly");
				var datatr = tr.datatr;

				//如果还未存到数据库，从表里删除
				if(datatr && !datatr.data.id){
					_self.removeRecord(data);
				}

				_self.restoreInline();
			});

			$(td).append(b1);
			$(td).append(b2);
			$(td).addClass("ita-td-edit");
		}
		//_self.inputs = _self.form.find(':input');
	});
};


/**
 * 提交form,自动判断是新建还是更新.
 */
iTables.prototype.submitForm = function() {
	var _self = this;
	var form = this.form;

	//check
	if (!form[0]) {
		toastr.error("没有对应的表单，无法提交");
		return
	}
	try {
		form.find("[type='submit']")[0].click();
	}catch (e) {
		toastr.error(e,"提交错误");
	}


};


/**insert a record in table.
 * 在iTables中插入一条数据，数据在表会新增一行以展现传入的数据。<p>
 @param data 参数是要插入的数据的json对象。
 @param type 1 保存数据
 */
iTables.prototype.insertRecord = function(data,type) {

	var _self = this;
	_self.callback("beforeInsertRecord",data);
	var PKV = data[_self.primaryKey];
	if(PKV == undefined || PKV == null || PKV == ""){
		data['IID'] = data['IID'] || "IID_" + this.idIndex++;
	}else{
		data['IID'] =  PKV;
	}

	var tr = this.rows[data.IID];
	//更新tr
	if (tr) {
		$(tr).empty();
		//$.extend(tr.data,data); 与之前的数据合并会导致数据刷新问题
		if(this._data){
			data.TREE_INDEX = this._data.TREE_INDEX;
			data.children = this._data.children;
		}

		this._buildTr(tr,data);

		//new row
	}else{
		tr = document.createElement("tr");
		if(this.options.edit)
			$(tr).dblclick(function () {
				_self._onEdit(this.data);
			});
		var parentName = _self.options.parentName;
		//保存时新增一条数据
		if(type == 1 && parentName){
			var ptr = this.rows[data[parentName]];
			if(ptr){
				var pdata = ptr.data;
				var pTree = pdata.TREE_INDEX;
				if(!pdata.children){
					pdata.children = [];
				}
				data.TREE_INDEX = [pTree[0]+1,1,pdata.children.length+1];
				pdata.children.push(data);
				$(ptr).after(tr);
				this._buildTr(ptr, pdata);
			}else{
				data.TREE_INDEX = [1,1,1];
				data[parentName] = 0;
				this._tbody.appendChild(tr);
			}

		}else if(type == 1 && !this.isEditInLine) {
			$(this._tbody).prepend(tr);
		}else{
			//插入到最后
			this._tbody.appendChild(tr);
		}
		this._buildTr(tr, data);

		//注册
		this.rows[data['IID']] = tr;
		this.json.push(data);
	}

	return tr;
};


/**update a record in table.
 * 在iTables中更新一条数据，数据在表中对应的行会被更新为最新的数据。如果没有该行，则新插入一行。<p>
 参数是要更新的数据的json对象。根据json的id属性找到对应的行进行更新。
 */
iTables.prototype.updateRecord = function(data) {
	if(!data){
		return;
	}
	var _self = this;

	//在注册表查找tr,保证每个tr有被注册
	var tr = this.rows[data.IID];
	//更新tr
	if (tr != null) {
		//重新创建tr的内容完成更新
		$(tr).empty();
		//更新data
		//$.extend(tr.data,data) 合并会造成设置为空值的列状态不能刷新
		if(_self.options.parentName){
			data.TREE_INDEX = tr.data.TREE_INDEX;
			data.children = tr.data.children
		}
		this._buildTr(tr,data);
		//直接返回
	} else {
		//新纪录，直接插入。
		tr = this.insertRecord(data);
	}
	return tr;
};

/**
 * remove a record from table.
 * 从iTables中删除一条数据，数据在表中对应的行会被移除。
 */
iTables.prototype.removeRecord = function(data) {

	//从注册表里获取tr
	var tr = this.rows[data.IID];


	//直接去表里查找
	if (tr == null) {
		for (var index = 0; index < this._tbody.rows.length; index++) {
			if (this._tbody.rows[index].data == data) {
				tr = this._tbody.rows[index];
			}
		}
	}

	//执行删除
	if (tr) {
		this._tbody.removeChild(tr);
		delete this.rows[data.IID];
		this.json.splice(this.json.indexOf(data), 1);
	}

	this.statistics();
};



iTables.prototype._buildTr = function (tr, data) {
	var _self = this;

	//对单条数据处理
	_self.callback("callData",data,tr);

	tr.data = data;
	$(tr).empty();
	$(this.columns).each(function(){
		try{
			var td = tr.insertCell(-1);//new cell
			_self._buildTd(td,data,this);
		}catch(e){
			console.error(e);
		}
	});

	$(tr).click(function(e) {
		window.currentItable = _self;
		if(e.ctrlKey){
			$(this).toggleClass("row_selected");
		}else if(this != _self._selectedTr)
			_self._selectTr(this);
	});

	_self.callback("afterBuildTr",tr,data);
};

iTables.prototype._buildTd = function (td, data,icolumn){
	var _self = this;
	$(td).empty();
	_self.callback("beforeBuildTd",td,data,icolumn);
	if(!icolumn.show) //$.hide() 性能很差
		td.style.display="none";
	$(td).addClass(icolumn.class);
	//has field definition
	var text = _self.translateText(icolumn,data[icolumn.field]);

	if (text != null && !icolumn.render && !icolumn.type) {
		td.innerText = text; //避免内容包含 < 等特殊符号
	}
	$(td).append(this.callFun(icolumn.type,td,data,icolumn,text));

	//单元格编辑
	if(this.options.edit && icolumn.edit){
		$(td).css("cursor","text");
		td.ondblclick = function(e){
			e.stopPropagation();
			var text = data[icolumn.field];
			if(text == undefined || text == null)
				text = td.innerText;
			var input = $(_self._form[icolumn.field]).clone();
			input.addClass("itable-ed-edit").val(text).appendTo(td).focus();
			input.on({
				"dblclick":function (e) {
					e.stopPropagation();
				},
				"blur" : function(e){
					if(text != this.value){
						_self.updateTd(td,this.value,icolumn);
					}else{
						$(this).remove();
					}
				},
				"keydown" : function(e){
					if (e.keyCode == 13) {
						if(text != this.value){
							_self.updateTd(td,this.value,icolumn);
						}else{
							$(this).remove();
						}
						return false;
					}
				}
			});
		};
	}

	$(td).append(this.callFun(icolumn.render,td,data,icolumn,text));

};

iTables.prototype.setTd = function(field,data){
	var col = this.colMap[field];
	var tr = this.rows[data[this.primaryKey]];
	if(tr){
		td = tr.childNodes[col.index];
		this._buildTd(td,data,col);
	}

}

iTables.prototype.updateTd = function(td,val,icolumn){
	var _self = this;
	var data = td.parentNode.data;
	var field = icolumn.field;
	var p = {};
	p[_self.primaryKey] = data[_self.primaryKey];
	p[field] = val;

	if(this.callback("callForm",p,data)){
		return;
	}

	if(p[_self.primaryKey] == undefined){
		layer.alert("没有主键，无法修改",{icon:2});
		return;
	}

	_self.ajax(_self.options.saveController,p,function(json){
		data[field] = json.data[field];
		_self._buildTd(td,data,icolumn);
	});
};

/**
 * 利用Select或者json翻译数据中的外键id到名字
 *
 */
iTables.prototype.translateText = function (icolumn,text) {
	//translate
	if (!icolumn.translate || !icolumn.field) {
		return text;
	}

	if (icolumn.translate == "select") {
		if(this.inputs){
			var option = this.inputs.filter("[name='"+icolumn.field+"']").find('option[value="' + text + '"]');
			return option.text() || text;
		}
		return text;
	} else if(icolumn.translate){
		var json = eval("("+icolumn.translate+")");
		return json[text] || text;
	}
	return text;
};

iTables.prototype.tdType = {
	rownum : function (td,data) {
		td.title = data[this.primaryKey];
		$(td).addClass("itable-rownum");
	},
	edit : function(td,data) {
		var _self = this;
		var tr = td.parentNode;
		$(td).addClass("ita-td-edit");
		$(td).click(function (event) {
			event.stopPropagation();
		});
		//edit button
		var beEdit = _self.options.edit;
		if (beEdit && (beEdit == true || beEdit(data))) {
			var buttonEdit = $("<i class='itable-font itable-edit' title='编辑'></i>");
			buttonEdit.click(function (event) {
				_self.inputs.filter("[name='" + _self.primaryKey + "']").attr("readonly", "readonly");
				if (_self.options.callUpdate && _self.options.callUpdate(data)) {
					return;
				}
				_self._onEdit(data);
			});
			$(td).append(buttonEdit);

		}

		//remove button
		var beRemove = _self.options.remove;
		if (beRemove && (beRemove == true || beRemove(data))) {
			var buttonRemove = $("<i class='itable-font itable-delete' title='删除'></i>");
			buttonRemove.click(function (event) {
				if (_self.callback("callDelete", data)) {
					return;
				}
				_self._onRemove(data);

			});
			$(td).append(buttonRemove);
		}

		if (_self.options.order) {

			var iorder = $("<i class='itable-font itable-order' title='排序' draggable='true'></i>")[0];
			iorder.ondragstart = function (e) {
				_self.dragNode = tr;
				_self.dragType = "order";
			};
			$(iorder).dblclick(function () {
				var r1 = td.parentNode;
				layer.prompt({title: '行 ' + r1.rowIndex + ' 移动到 ➝'}, function (text, index) {
					layer.close(index);
					var r2 = _self._tbody.rows[text - 1];
					$(r2).before(r1);
					_self.updateOrder();
				});
			});

			$(td).append(iorder);
		}

		var parentName = _self.options.parentName;
		if (parentName) {
			var addSub = $("<i class='itable-font itable-tree-sub' title='添加子级' draggable='true'></i>");
			$(td).append(addSub);
			addSub.click(function () {
				if(_self.isEditInLine)
					$(tr).after(_self._inlineTr);
				else
					_self.createDialog();
				_self._form[parentName].value = data[_self.primaryKey];

			});
			addSub[0].ondragstart = function (e) {
				_self.dragNode = tr;
				_self.dragType = "treeSub";
			}
		}

		if (_self.options.order || parentName) {

			tr.ondragover = function (e) {
				e.preventDefault();//不阻止ondragover默认事件就无法触发ondrop函数
			};

			tr.ondrop = function (e) {
				e.preventDefault();
				if (tr == _self.dragNode) {
					return;
				} else if (_self.dragType == "order") {
					$(tr).after(_self.dragNode);
					_self.updateOrder();
				} else if (_self.dragType == "treeSub") {
					layer.confirm('确定将： ' + _self.dragNode.innerText + '<br>移动到：' + tr.innerText, {icon: 3}, function (index) {
						layer.close(index);
						var p = {};
						p[_self.primaryKey] = _self.dragNode.data[_self.primaryKey];
						p[_self.parentName] = data[_self.primaryKey];
						_self.ajax(_self.options.saveController, p, function () {
							_self.load()
						});
					});
				}

			}
		}
	},
	/**
	 * 多选checkbox渲染器，用来在某一个td中插入多选checkbox. 供内部调用
	 */
	checkbox : function(td,data,icolumn){
		var _self = this;
		var checkbox = document.createElement("input");
		checkbox.type = "checkbox";
		checkbox.name = icolumn.field;
		checkbox.value = data[icolumn.field];
		var pValue = this.getPrimaryValue(data);
		if(_self.checkedMap[pValue])
			checkbox.checked = true;
		$(checkbox).addClass("it-check");
		$(checkbox).change(function(){
			_self.checkedMap[pValue] = this.checked ? data : false;
		});
		$(td).empty();
		td.appendChild(checkbox);
	},
	dateTime : function(td,data,icolumn,text){
		if(text){
			var date;
			if(text && (text+"").length == 10){
				date = new Date(text*1000);
			}else{
				date = new Date(text.replace(/-/g, "/").replace("T"," "));//兼容safari
			}
			td.innerHTML = date.format((icolumn.format || "yyyy-MM-dd hh:mm"));
		}

	},
	switch : function(td,data,icolumn,text){
		var _self = this;
		var tr = td.parentNode;
		if(typeof(text) == "boolean"){
			text = text ? 1 : 0;
		}
		$(td).empty();
		var i = $("<i class='itable-font itable-switch' v='"+text+"'></i>").appendTo(td);
		i[0].title = data[this.primaryKey];
		i.dblclick(function(e){
			_self.updateTd(td,text == 1 ? 0 : 1,icolumn);
			e.stopPropagation();
		}).click(function(e){
			e.stopPropagation();
		});

	},
	state : function(td,data,icolumn,text){
		$(td).empty();
		$("<i class='i-"+icolumn.field+"' v='"+text+"'></i>").appendTo(td);
	},
	dict : function(td,data,icolumn,text){
		var dict = this.dict[icolumn.field][text] || {};
		if(dict){
			if(dict.icon)
				$(td).append("<i class='ita-icon "+dict.icon+"'></i>");
			$(td).append(dict.name || text).css({"color":dict.color,"background-color":dict.backColor});
		}else{
			$(td).append(text)
		}

	},
	select :  function(td,data,icolumn,text){
		var option = this.inputs.filter("[name='"+icolumn.field+"']").find('option[value="' + text + '"]');

		var $td = $(td).text(option.text()).addClass(option.attr("class"));
		var style = option.attr("style");
		if(style){
			$(style.split(";")).each(function (){
				var a = this.split(":")
				$td.css(a[0],a[1]);
			});
		}
	},
	checkDel : function(td,data,icolumn){
		var _self = this;
		var op = this.options[icolumn.join];
		op.map = op.map || {};
		var pid = data[this.primaryKey];
		var d2 = op.map[pid] || {};
		var i = $('<i class="layui-icon layui-icon-ok" v="'+( d2.id || 0)+'"></i>');
		i.dblclick(function(e){
			e.stopPropagation();
			if(op.map[pid]){
				_self.ajax(op.delController,{"id" : op.map[pid].id},function(json){
					i.attr("v","0");
					delete op.map[pid];
				});
			}else{
				var p = {};
				p[op.joinField] = data[_self.primaryKey];
				if(op["callForm"].call(_self,p,data))
					return;
				_self.ajax(op.saveController,p,function(json){
					op.map[pid] = json.data;
					i.attr("v",json.data.id);
				});
			}
		});
		$(td).append(i);
	},
	level : function(td,data,icolumn,text){
		var _self = this;
		if(!icolumn.render)
			td.innerHTML = text;
		var leveli = $("<i class='ita-tree'></i>");
		$(td).prepend(leveli);
		for(var i =1;i<data.TREE_INDEX[0];i++){
			$(td).prepend("<i class='ita-tree tree-tab'></i>");
		}
		if(data.TREE_INDEX[1] == 1)
			leveli.attr("seq","first");
		if(data.TREE_INDEX[1] == data.TREE_INDEX[2])
			leveli.attr("seq","last");
		if(data.children){
			leveli.attr("children","1");
			leveli.attr('opened',"1");
			$(leveli).click(function(e){
				e.stopPropagation();
				var isExpand = $(this).attr("opened") == 1;
				$(this).attr("opened",isExpand ? 0 : 1)
				if(isExpand){
					_self.fold(data);
				}else{
					_self.expand(data);
				}
			});
		}

	}
};


/**
 * 展开
 */
iTables.prototype.expand = function(data){
	this.arrTemp = [];
	this.treeToArray(data.children);
	var _this = this;
	$(this.arrTemp).each(function(){
		var ptr = _this.rows[this[_this.parentName]];
		if($(ptr).find("[opened]").attr("opened") == "1")
			$(_this.getTr(this)).show();
	});
};

/**
 * 展开层级
 */
iTables.prototype.expandLevel = function(level){
	var _this = this;
	$(this.json).each(function(){
		var $tr = $(_this.getTr(this));
		this.TREE_INDEX[0] > level ? $tr.hide() : $tr.show();
		if(this.TREE_INDEX[0] >= level){
			$tr.find(".ita-tree[opened]").attr("opened",0);
		}else{
			$tr.find(".ita-tree[opened]").attr("opened",1);
		}
	});

};

/**
 * 折叠
 */
iTables.prototype.fold = function(data){
	var _this = this;
	this.arrTemp = [];
	this.treeToArray(data.children);

	$(this.arrTemp).each(function(){
		var $tr = $(_this.getTr(this));
		$tr.hide();
	});
};

/**
 * 远程加载数据
 * load(null,{noClear:true,loadingType:false}) 刷新数据
 * @param params 请求数据参数
 * @param option 加载选项
 * 				noClear 加载之前是否清除
 * 				loadingType 加载时的样式
 * 				isNext 是否是加载的下一页
 *
 */
iTables.prototype.load = function(params,option){
	var _self = this;
	var loadOptions = option || {};
	this.restoreInline();//重新加载前需要重置
	var loadParams = loadOptions.paramExtend ? $.extend(_self.params,params) : (params || _self.params);
	_self.params = loadParams;

	if(!loadOptions.noClear){
		_self.clear();
	}

	loadParams = _self.callback("callLoadParams",loadParams,loadOptions) || loadParams;

	//page
	var pi = this.pageInfo;
	loadParams.pageSize =  pi.pageSize;
	loadParams.pageNum  =  loadOptions.isNext ? pi.nextPage : (loadParams.pageNum || 1);
	if(this.order.field){
		delete loadParams["ASC"];
		delete loadParams["DESC"];
		loadParams[this.order.by] = this.order.field;
	}

	if(this.searchInput && this.isServerPage){
		//检索参数默认为 term 为了与jQuery保持兼容
		var searchField = this.searchInput.dataset.field || "term";
		loadParams[searchField] = "%" + this.searchInput.value.replace(" ","%") + "%";
	}

	// load data

	var loadingType = loadOptions.loadingType;
	if(loadingType != false){
		_self.layerOption.index = layer.load(loadingType || 1, {
			shade: [0.1,'#fff'] //0.1透明度的白色背景
		});
	}

	this.ajax(_self.options.getController,loadParams,function(json){
		layer.close(_self.layerOption.index);
		if(_self.callback("getDataCallback",json)){
			return;
		}
		var pager = json.pager;
		if(json.pager && json.pager.recordCount != undefined){//nutz Page原生支持
			var pageNumber = json.pager.pageNumber;
			pi.pageNum = pageNumber;
			pi.nextPage = json.list.length >= pager.pageSize ?  pageNumber + 1 : false;
		}else if(this.options.scrollLoad){
			var pn = _self.pageInfo.pageNum;
			pi.nextPage = _self.pageInfo.pageSize ? pn++ : false;
		}
		//pageInfo对象 > list属性 > json
		var list = json.list || json;

		if (!list || !list.length) {
			_self.pageInfo.nextPage = false;
			if($(_self._tbody).find(".ita-nodata").length == 0)//避免异步多次加载生成多条
				$(_self._tbody).append("<tr class='ita-nodata'><td colspan='"+ _self.columns.length +"' >暂时还没有数据...</td></tr>");
		}

		var list2 = _self.getChecked().concat(list);
		_self._onLoaded(list2);

		_self.callback("callback",list2);
	});
};


/**
 * 加载给定的数据。
 */
iTables.prototype._onLoaded = function(list){
	if (!list){return;}

	var _self = this;
	_self.restoreInline();

	if(this.options.parentName){
		list = _self.renderTree(list);
	}

	this.version = new Date().getTime();
	var length = list.length;

	for (var i = 0; i < length; i ++) {
		this.insertRecord(list[i]);
	}

	if(this.options.scrollLoad && this.pageInfo.nextPage){
		var t = $(this.options.scrollLoad)[0];
		var clientHeight = t.clientHeight || t.documentElement.clientHeight;
		var scrollHeight = t.scrollHeight || t.documentElement.scrollHeight || t.body.scrollHeight;

		//如果未出现滚动条
		if(scrollHeight <= clientHeight){
			//alert(clientHeight + "-" + scrollHeight);
			this.load(null,{"noClear":true,"isNext":true});
		}
	}

	this.statistics();

	var time2 = new Date().getTime();
	console.debug(this._table.id+"表格本次加载"+_self.json.length+"条数据，共耗时"+(time2-this.version)+"毫秒");
	this.callback("loadAfter",list);
};

/**更新数据库中的排序字段*/
iTables.prototype.updateOrder = function(){
	var _self = this;
	var order = _self.options.order;
	var orderArr = [];
	$(_self.table).find("tbody tr").each(function(index){
		if(this.data){
			var orderJson = {};
			orderJson[_self.primaryKey] = this.data[_self.primaryKey];
			orderJson[order] = index+1;
			this.data[order] = index+1;
			orderArr.push(orderJson);
		}
	});
	if(_self.options.orderController){
		_self.ajax( _self.options.orderController + "&order=" + order,orderArr,function(ajson){
			if(ajson.success){
				toastr.success("","排序成功");
			}else{
				toastr.error(ajson.obj['@type'],"排序异常");
				_self.load();
			}
			this.callback("afterOrder",ajson);
		},{contentType : 'application/json'});
	}

};

iTables.prototype.setJoinList = function (join,list){
	var _this = this;
	var op = this.options[join];
	var joinField = op.joinField;
	op.list = list;
	op.map = {};
	$(list).each(function (){
		var mainId = this[joinField];
		op.map[mainId] = this;
	});
	$(this.json).each(function (){
		_this.updateRecord(this);
	});
}

/**
 * 清除所有表中的数据
 */
iTables.prototype.clear = function () {
	this.json = [];
	this.rows = {};
	//this.statistics();
	$(this._tbody).empty();
	this.pageInfo.pageNum = 1;
	this.pageInfo.nextPage = null;
};


/**
 * 过滤处理。根据传入的数值，过滤表中的数据，隐藏无关的行。显示有关的行。该过滤支持多列多关键字模糊过滤。
 * @param serstring 用来过滤数据的条件字符串。
 */
iTables.prototype.filter = function(serstring) {
	var tbody = this._table.tBodies[0];
	//check
	if (!serstring) {
		$(tbody.rows).css("display","");
		return;
	}
	$(tbody.rows).css("display","none");
	var sers = serstring.toUpperCase().split(" ");
	var t1 = [];
	//var time = new Date().getTime();

	for (var index = 0; index < tbody.rows.length; index++) {
		var row = tbody.rows[index];
		var searchIndex = (row.searchIndex || row.innerText).toUpperCase();

		//多关键字和检索索引相匹配。

		for (var i=0; i< sers.length;i++) {
			var ser = sers[i];
			if (searchIndex.indexOf(ser) > -1) {
				t1.push(row);
				break;
			}
		}
	}
	$(t1).css("display","");

	//console.log("检索耗时:"+(new Date().getTime() - time) + "ms");

};


/**
 * 开始编辑某一条数据，会调用编辑的dialog或者是行内编辑。
 * @param data 要编辑的数据。
 */
iTables.prototype._onEdit = function(data) {
	var _self = this;
	if(this._data != data)
		this.select(data);
	if (this.isEditInLine) {
		this.editInLine(data);
	} else 	{
		this.autoInput(data);
		this.form.show();
		if (_self.options.beforeUpdate && _self.options.beforeUpdate(data)){
			return;
		}
		_self.layerOption.index = layer.open({
			type: 1,
			title : _self.layerOption.title ? _self.layerOption.title + " 修改" : false,
			skin: 'ita-layer ita-edit ita-'+_self.tid,
			btn: ['确定','取消'],
			content: _self.form, //捕获的元素
			area : _self.layerOption.area || ["auto","auto"],
			cancel: function(index){
				layer.close(index);
			},
			yes : function(index){
				_self.submitForm();
			}

		});
	}
	this.callback("onAfterEdit",data);
};

/**
 * 删除某一条数据 或者按 checked 列名选中删除，会调用编辑的确认dialog进行删除。
 * @param data 要编辑的数据
 */
iTables.prototype._onRemove = function(data) {
	var _self = this;
	var checks = [];
	var params = {"primaryKey":_self.primaryKey,"IID":[]};
	params[_self.primaryKey] = [];
	//批量删除(data : 字段名)
	if(Object.prototype.toString.call(data) === "[object String]"){
		$(this._table).find("[name='"+data+"']:checked").each(function(){
			var d = this.parentNode.parentNode.data;
			checks.push(d);
			params["IID"].push(d.IID);
			params[_self.primaryKey].push(d[_self.primaryKey]);
		});
	}else if(data){
		$(data).each(function (){
			params["IID"].push(this.IID);
			params[_self.primaryKey].push(this[_self.primaryKey]);
		});
	}

	if(!params[_self.primaryKey][0] && !params["IID"][0]){
		toastr.error("请指定需要删除的项目","删除错误");
		return;
	}

	if(_self.options.modeCommit){
		if(checks.length > 0){
			$(checks).each(function(){
				_self.removeRecord(this);
			});
		}else{
			$(data).each(function(){
				_self.removeRecord(this);
			});
		}
		return;
	}

	layer.confirm('确定删除该数据?', {icon: 3}, function(index){
		if(_self.callback("beforeDelete",data,params)){
			return;
		}
		_self.ajax(_self.options.delController,$.param(params,true),function(json){
			layer.close(index);
			if (json.success) {
				toastr.success("","删除成功");

				if(checks.length > 0){
					$(checks).each(function(){
						_self.removeRecord(this);
					});
				}else{
					$(data).each(function(){
						_self.removeRecord(this);
					});
				}
				_self.callback("afterDelete",data,json);

			} else {
				toastr.error(json.message,"删除失败");
			}
		});

	});

};

/**
 * 回复行内编辑到初始的新建行状态。
 */
iTables.prototype.restoreInline = function () {
	var _self = this;
	if (_self._inlineTr) {
		_self._table.tFoot.appendChild(_self._inlineTr);
		$(_self._inlineTr.dtr).show();
		if(!_self.beCreate){
			$(_self._inlineTr).hide();
		}
	}

	_self.resetForm();

};


/**
 * 初始化表单，如果表单不存在，自动生成表单。
 */
iTables.prototype._prepareForm = function () {
	var _self = this;
	var _form = this.form.get(0);
	if (!_form) {
		return;
	}
	var formId = this.form.attr("id");
	if (!formId) {
		formId = this._table.id + "_form";
		this.form.attr("id", formId);
	}
	if (!this.form.find("[type='submit']")[0]){
		this.form.append('<button type="submit" style="display:none">提交</button>');
	}
	if(!this.form.find("[name='"+this.primaryKey+"']")[0]){
		this.form.append('<input type="hidden" name="'+this.primaryKey+'" form="'+formId+'">');
	}
	if(!this.form.find("[name='IID']")[0]){
		this.form.append('<input type="hidden" name="IID" form="'+formId+'">');
	}
	this.inputs = this.form.find(':input');
	this.inputs.each(function (index, input) {
		if(!input.getAttribute("form")){
			input.setAttribute("form", _self.form.attr("id"));
		}
	});
	//TODO dd
	_form.onsubmit = function(event){
		event.preventDefault();
		$("#"+_self._table.id + "_submit2").attr("disabled",true);

		var params = _self.formJSON(_self.inputs,null,false,true);

		if(params){
			layer.close( _self.layerOption.index);
			_self.saveData(params);
		}else{
			toastr.error("","参数不合法");
		}

	}

};


iTables.prototype.saveData = function(data,option){
	var _self = this;
	data.primaryKey = _self.primaryKey;

	var orderField = _self.options.order;
	if(orderField && !data.IID && !data[orderField]){
		var order = 0;
		$(_self.json).each(function(){
			order = Math.max(order,this[orderField] || 0);
		});
		data[orderField] = order + 1;
	}

	if (_self.callback("callForm",data)) {
		$("#"+_self._table.id + "_submit2").removeAttr("disabled");
		return;
	}
	//客户端提交模式
	if(_self.options.modeCommit){
		$("#"+_self._table.id + "_submit2").removeAttr("disabled");
		var ajson = {"data":data,"success":true};
		_self.submitResponse(ajson,data);
		return;
	}

	if(this.enctype == "multipart/form-data"){
		//TODO 如果有文件
		return;
	}

	_self.ajax(_self.options.saveController,data,_self.submitResponse,option);
};

iTables.prototype.updateByPrimary = function(primaryVal){
	var _self = this;
	var p = {};
	p[this.primaryKey] = primaryVal;
	_self.ajax(_self.options.getController,p,function(json){
		data = (json.list || json)[0];
		_self.updateRecord(data);
	},{async : false});
};

//刷新选中行
iTables.prototype.refreshSelected = function(){
	this.updateByPrimary(this._data[this.primaryKey]);
}

iTables.prototype.submitResponse = function(ajson,params){
	var _self = this;
	var tr = null;
	var data = ajson.data;
	if(data){
		this.table.find(".ita-nodata").remove();
	}
	if(_self.options.saveReformatGet){
		_self.updateByPrimary(data[this.primaryKey] || params[this.primaryKey]) ;
	}else{
		_self.insertRecord(data,1);
	}

	if(!this.options.modeCommit){
		toastr.success("","保存成功");
	}

	_self.restoreInline();
	_self.statistics();

	this.callback("afterSubmit",ajson);
	$("#"+_self._table.id + "_submit2").removeAttr("disabled");
}

/**
 * 重置表单，把表单内的所有值都设置为缺省值。
 */
iTables.prototype.resetForm = function () {
	if (!this.inputs) {
		return;
	};

	this.inputs.each(function (index, input) {
		var def = input.getAttribute("data-default");
		if(def == "DATA"){
			def = new Date().format("yyyy-MM-dd");
		}
		if(def == "DATATIME"){
			def = new Date().format("yyyy-MM-ddThh:mm");
		}
		if(def && def[0] == "("){
			def = eval(def);
		}
		if (input.getAttribute("data-clear") == "false") {
			return;
		}else if(input.tagName == "SELECT"){
			if(def){
				input.value = def;
			}else{
				input.selectedIndex = 0;
			}
			$(input).change();
		}else if(input.type == "file"){
			$(input).val("");
			$(input).change();
		}else if(input.type == "color"){
			input.value = def || "#000000";
		}else{
			input.value = def || "";
		}
	});

	this.callback("afterResetForm");
};


/**
 * 用行内编辑的方式，编辑一行数据。该方法会把该行变为可编辑的。
 * @param data  要放在行内编辑的数据json对象.
 */
iTables.prototype.editInLine = function (data) {
	var _self = this;
	this.autoInput(data);

	$(_self._inlineTr).show();//无create时 编辑行默认不显示
	$(_self._inlineTr.dtr).show();//显示之前隐藏的TR

	var dtr = this.getTr(data);
	$(dtr).after(_self._inlineTr);
	$(dtr).hide();

	_self._inlineTr.dtr = dtr;
};


/**
 * 把对象的数据内容复制到Form内。
 * @param data 要赋值给form的json数据。
 */
iTables.prototype.autoInput = function (data) {

	if (!data) {
		return;
	}
	this.resetForm();

	for(var i = 0;i<this.inputs.length;i++){
		var input = this.inputs[i];
		var tagName =input.tagName;
		var value =  data[input.name];
		if((value || value == 0) && (tagName == "INPUT" || tagName == "SELECT" || tagName == "TEXTAREA")){
			if (input.type == "datetime-local") {//
				if(typeof(value) == "number"){
					value = new Date(value*1000).format("yyyy-MM-ddThh:mm");
				}else{
					value = value.replace(" ","T");
				}

			}

			if (input.type == "date"){
				value = /[\d]{4}-[\d]{2}-[\d]{2}/.exec(value);
			}



			if (input.type != "file"){
				input.value = value;
			}

			if(tagName == "SELECT"){
				$(input).change();
			}

			if (input.control) {
				input.control();
			}
		}
	}


};

/**
 * 默认提供的统计函数
 */
iTables.prototype.statisticsType = {
	'count':function (base, text) {//总数
		if(base == null) {
			return 1;
		}
		return ++base;
	},
	'average':function (base,text,index,length) {//平均
		var num = parseFloat(text) || 0;
		if(base == null) {
			return num;
		}
		if(index+1 == length){
			base += num;
			return (base/length).toFixed(1);
		}
		return base += num;
	},
	'sum':function (base, text,index,length) {//合计
		var num = parseFloat(text) || 0;
		return base += num;
	},
	'min':function (base, text) {//最小
		var num = new Number(text);
		if(base == null) {
			return num;
		}
		if (base < num) {
			return base;
		} else {
			return num;
		}
	},
	'max':function (base, text) {//最大
		var num = new Number(text);
		if(base == null) {
			return num;
		}
		if (base < num) {
			return num;
		} else {
			return base;
		}
	},
	'earliest':function (base, text,index,length) {//最早
		var d = new Date(text);
		d = (base == null || base > d) ? d : base;
		return 	index+1 == length ? new Date(d).format("yyyy-MM-dd hh:mm") : d;
	},
	'latest':function (base, text,index,length) {//最晚
		var d = new Date(text);
		d = (base == null || base < d) ? d : base;
		return 	index+1 == length ? new Date(d).format("yyyy-MM-dd hh:mm") : d;
	}
};

/**
 * statistics.执行统计操作。根据定义的统计算法，对当前数据进行统计。
 * iTable 变动即调用
 */
iTables.prototype.statistics = function () {
	var _self = this;
	if (this.callback("beforeStats")){
		return;
	}

	var btr = this.table.find("tbody tr");
	var sth = this.table.find("tfoot [data-statistics]");
	sth.each(function(){
		var sFun = _self.statisticsType[this.dataset["statistics"]];
		var field = this.dataset["field"];
		var result = null;

		//"tbody tr:visible"
		btr.each(function(index){
			try{
				if(this.data)
					result = sFun(result,this.data[field],index,btr.length);
			} catch (e){
				toastr.error(e.message,"iTables异常");
			}
		});

		if(result != null){
			try {
				this.innerHTML = parseFloat(result.toFixed(3));
			}catch (e){
				this.innerHTML = result;
			}
		}

	});

};



//
/**
 * 排序功能，执行排序操作。根据定义的字段，对当前数据进行排序。
 * @param feild 排序的字段.
 * @param dir 排序的方向. "ASC"正序，"DESC"倒序。
 */
iTables.prototype.sort = function (feild, dir) {
	var data= this.json;
	data.sort(function(a,b){
		if(!a[feild]){
			return -1;
		}else if(!b[feild]){
			return 1;
		}else if(typeof a[feild] == "string"){
			return a[feild].localeCompare(b[feild]);
		}else{
			return a[feild]- b[feild];
		}

	});
	if (dir == "DESC") {
		data.reverse();
	}

	this.clear();
	this._onLoaded(data);

	//重新选中之前选中的行
	if(this._selectedTr){
		this.select(this._selectedTr.data);
	}


};

/**
 * 选中指定的行<p>
 参数是一个table行的HTML tr对象。
 */
iTables.prototype._selectTr = function(tr)  {
	this.table.find(".row_selected").removeClass("row_selected");
	$(tr).addClass("row_selected");
	this._selectedTr = tr;
	this._data = tr.data;
	this.callback("onSelect",tr,tr.data);
};

iTables.prototype.getTr = function(data){
	return this.rows[data[this.primaryKey]];
}

/**
 * 选中指定的数据所在的行<p>
 参数是一个数据的json对象，根据对象的id来找到对应的行，并选中。
 */
iTables.prototype.select = function(data){
	var tr = this.getTr(data);
	if(tr){
		this._selectTr(tr);
	}
};

iTables.prototype.hideEdit = function(b){
	if(b)
		this.table.addClass("itable-hide-edit");
	else
		this.table.removeClass("itable-hide-edit");
};

/**
 * 将iTables的数据导出为csv文件。
 */
iTables.prototype.expCsv = function(filename) {
	var loadParams = {};
	if(this.options.callLoadParams){
		loadParams = this.options.callLoadParams(loadParams) || loadParams;
	}
	loadParams.fileName = filename || "Excel";
	loadParams.columns = JSON.stringify(this.columns);

	var a = $("<a target='_blank'>EXP</a>");
	a.attr("href",this.options.expController+"&"+$.param(loadParams));
	var e = document.createEvent('MouseEvents');
	e.initEvent( 'click', true, true );
	a.get(0).dispatchEvent(e);
};

iTables.prototype.selectIndex = function(index){
	var tr = this.table.find("tbody tr")[index];
	if(tr && tr.data){
		this._selectTr(tr);
	}
};

iTables.prototype.formJSON = function(str) {
	var json = {};
	var fromlist = $(str).filter("input,select,textarea");
	fromlist.each(function() {
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

iTables.prototype.ajax = function(url,data,callBack,option){
	var _self = this;
	var option = option || {};
	if(!url){
		layer.alert("无有效请求路径",{icon : 2});
		layer.close(_self.layerOption.index);
		return;
	}
	$.ajax({
		url : url,
		//dataType : 'json',
		contentType : option.contentType || "application/x-www-form-urlencoded",
		type : "POST",
		async : option.async !== false,
		traditional : true,//避免数组参数转成 a[]=1&a[]=2带中括号的形式
		data : option.contentType == "application/json" ? JSON.stringify(data) : data,
		success : function(json) {
			if(json.success === false){
				var msg = json.message;
				if(json.obj && json.obj.cause){
					msg = json.obj.cause.message;
				}
				layer.close(_self.layerOption.index);
				layer.alert("执行失败："+msg,{icon : 2});
			}else{
				callBack.call(_self,json,data);
			}
		},
		error : function(jqXHR,textStatus,errorThrown) {
			layer.close(_self.layerOption.index);
			toastr.error(textStatus + "," + errorThrown.message,"数据请求错误：");
		},
		complete : function (XHR, TS) {
			XHR = null;
		}
	});
};

iTables.prototype.renderTree = function(list){

	var _this = this;
	var m = {};

	$(list).each(function(){
		m[this.id] = this;
	});
	var parentName = this.options.parentName;
	//根节点
	$(list).each(function(){
		if(!m[this[parentName]]){
			this[parentName] = 0;
		}
	});
	var a = this.arrayToTree(list, 0,parentName,0);
	this.arrTemp = [];
	this.treeToArray(a);
	return this.arrTemp;
};

iTables.prototype.treeToArray = function(list){
	var _this = this;
	$(list).each(function(){
		_this.arrTemp.push(this);
		if(this.children){
			_this.treeToArray(this.children);
		}
	});
};

iTables.prototype.arrayToTree = function(list,pid,pidName,level){
	var _this = this;
	var childs = [],i=0;
	level++;
	$(list).each(function(index){
		if(this[pidName] == pid){

			var cl = _this.arrayToTree(list, this.id,pidName,level);
			if (cl.length > 0) {
				this.children =cl;
			}
			this.TREE_INDEX = [level,++i,0];
			childs.push(this);
		}
	});
	$(childs).each(function (){
		this.TREE_INDEX[2] = childs.length;
	});
	return childs;
};

Array.prototype.indexOf = function(val) {
	for (var i = 0; i < this.length; i++) {
		if (this[i] == val) return i;
	}
	return -1;
};
Array.prototype.remove = function(val) {
	var index = this.indexOf(val);
	if (index > -1) {
		this.splice(index, 1);
	}
};
Array.prototype.IID = function(iid){
	for (var i = 0; i < this.length; i++) {
		if (this[i].IID == iid) return this[i];
	}
}

window.console = window.console || (function () {
		var c = {}; c.log = c.warn = c.debug = c.info = c.error = c.time = c.dir = c.profile = c.clear = c.exception = c.trace = c.assert = function () { };
		return c;}
)();

var toastr = {
	success : function(msg,title){
		layer.msg("["+title+"]"+msg, {icon: 1});
	},
	error : function(msg,title){
		layer.alert("["+title+"]"+msg, {icon: 2});
	}
};
