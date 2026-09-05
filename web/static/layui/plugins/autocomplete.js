layui.define(['jquery','laytpl'], function (exports) {
  "use strict";
  layui.link(document.currentScript.src.replace("js","css")); 
  var hint = layui.hint(),
    $ = layui.jquery,
    laytpl = layui.laytpl,
    filter = 'layui-autocomplete',
    container = 'layui-form-autocomplete',
    container_focus = 'layui-form-autocomplete-focus',
    system = {
      config: {
        template: '<div class="layui-form-autocomplete"><dl class="layui-anim layui-anim-upbit"></dl></div>',
        name : "name",//显示的名称
        code : "code",//名称后显示的
        term : "term",//后台请求条件
        keyword : function (v) {
          var str = "";
          for(var k in v)
            if(!(v[k] instanceof Object))
              str += v[k];
            return str;
        }
      },
      init : function (e, c) {
    	    var c = c || {}, _self = this, _elems = $(e ? 'input[lay-filter="' + e + '"]' : 'input[' + filter + ']');
    	    _elems.each(function (_i, _e) {
    	      var _elem = $(_e),
    	        _lay_data = _elem.attr('lay-data');
    	      try {
    	        _lay_data = new Function("return " + _lay_data)()
    	      } catch (ex) {
    	        return hint.error("autocomplete元素属性lay-data配置项存在语法错误：" + _lay_data)
    	      }
    	      var _config = $.extend({ elem: this }, system.config, c, _lay_data);
    	      _config.url == undefined && (_config.data == undefined || _config.length === 0) && hint.error("autocomplete配置有误，缺少获取数据方式");
    	      system.render(_config);
    	    })
      },
      render : function (e) {
    	  var j = new job(e);
    	  return callback.call(j)
      }
      
    },
    callback = function () {
      var _self = this,
        _config = _self.config;
      return {
        call: function (handle, params) {
          if (!_self.handles[handle]) 
        	  return hint.error(handle + " handle is not defined");
          _self.handles[handle].call(_self, params)
        }
      }
    },
    job = function (e) {
      var _self = this;
      _self.config = $.extend({}, _self.config, system.config, e);
      _self.render();
    };
    
  job.prototype.config = {
    response: {
      code: 'code',
      data: 'list',
      msg: 'msg'
    },
    time_limit: 300,
    pullTimer: null,
    params: {},
    filter: '',
    method: 'get',
    ajaxParams: {}
  };

  //参数方法的统一调用
  job.prototype.callback = function(fName,p1,p2,p3){
    var f = this.config[fName];
    if(f){
      return f.call(this,p1,p2,p3);
    }
  };

  job.prototype.render = function () {
    var _self = this, _config = _self.config;
    if (_config.elem = $(_config.elem), _config.where = _config.where || {}, !_config.elem[0]) return _self;
    var _elem = _config.elem,
      _container = _elem.next('.' + container),
      _html = _self.elem = $(laytpl(_config.template).render({}));
    _config.id = _self.id, _container && _container.remove();
    _elem.attr('autocomplete', 'off').addClass("layui-elem-autocomplete").after(_html);
    _self.events()
  };
  
  job.prototype.pullData = function (elem,key) {

    var _self = this,_config = _self.config,_elem = $(elem),_container = _elem.next('.' + container);

    if (_config.data)
      return _self.renderData(elem,_config.data,key);

    var params = {};
    params[_config.term] = "%" + (key || "") + "%";

    var $loading = $('<i class="layui-icon layui-icon-loading layui-anim layui-anim-rotate layui-anim-loop"></i>');
    $.ajax($.extend({
      type: _config.method,
      url: _config.url,
      data: $.extend(params, _config.params instanceof Function ? _config.params() :_config.params),
      contentType: 'text/json,charset=utf-8',
      dataType: "json",
      beforeSend: function () {
        $loading.attr('style', [
          'position:absolute', 
          'left:' + (_elem.offset().left + _elem.outerWidth() - 20) + 'px', 
          'top:' + _elem.offset().top + 'px',
          'height:' + _elem.height() + 'px',
          'line-height:' + _elem.height() + 'px'
        ].join(';'));
        $loading.appendTo('body');
      },
      success: function (resp) {
        $loading.remove();
        _self.renderData(elem,resp);
      },
      error: function () {
        hint.error("请求失败")
      }
    }, _config.ajaxParams))
  };
  
  job.prototype.renderData = function (elem,resp,key) {
    var _self = this,
      _config = _self.config,
      _elem = $(elem),
      _container = _elem.next('.' + container),
      _dom = _container.find('dl');
    
    _dom.empty();
    var list = resp.list || resp;
    list = this.callback("loadData",resp,elem) || list;
    if(key){
      list = $.grep(list,function (v,i) {
          return _config.keyword(v).toUpperCase().indexOf(key.toUpperCase()) > -1;
      })
    }
    layui.each(list, function (i, e) {
        var dd = $('<dd data-index="'+i+'"><span class="ac-name">'+e[_config.name]+'</span> </dd>');
        var c = e[_config.code];
        if(c)
          dd.append("<span class='layui-badge layui-bg-gray'>"+c+"</span>");
        dd.click(function(){
            elem.data = e;
        	_elem.val(dd.find(".ac-name").text());
            _self.callback("onselect",e,elem);
        });
        _dom.append(dd);

    });
    list && list.length > 0 ? _container.addClass(container_focus) : _container.removeClass(container_focus);
  };
  
  job.prototype.handles = {
    addData: function (data) {
      var _self = this,
        _config = _self.config;
      if (data instanceof Array) {
        _config.data = _config.data.concat(data)
      } else {
        _config.data.push(data)
      }
    },
    setData: function (data) {
      var _self = this,_config = _self.config;_config.data = data;
    }
  };
  
  job.prototype.events = function () {
    var _self = this,_config = _self.config;
    //.unbind('focus')
    //.unbind('input propertychange')
    _config.elem.click(function(e){
    	e.stopPropagation();
    })
    .on('focus', function (e) {
      _self.pullData(e.target);
    })
    .on('blur', function (e) {
      if(_config.onblur){
    	  _config.onblur(e.target);
      }
    })
    .on('keyup', function (e) {
      var value = _self.callback("renderVal",this.value) || this.value;
      clearTimeout(_config.pullTimer);
      _config.pullTimer = setTimeout(function (){
      	_self.pullData(e.target,value);
      }, _config.time_limit)
    });
    
    $(document).on('click', function (e) {
      $("."+container_focus).removeClass(container_focus);
    })
  };

  system.init();
  
  exports("autocomplete", system);
  
});