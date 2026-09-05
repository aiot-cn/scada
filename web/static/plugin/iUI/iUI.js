var iUIcss = document.currentScript.src.replace(".js",".css");
var iUI = {
	clientWidth : function (){
		return window.innerWidth || document.documentElement.clientWidth || document.body.clientWidth
	},
	clientHeight : function (){
		return window.innerHeight || document.documentElement.clientHeight || document.body.clientHeight;
	},
	RMenuUl : function(menus){
		var ul = $("<ul></ul>");
		$(menus).each(function(){
			var li = $('<li><a><i class="'+this.icon+'"></i><span>'+this.title+'</span></a></li>');
			li[0].data = this;
			ul.append(li);
			this.sub && this.sub.length > 0 && iUI.RMenuSub(this.sub,li);
			this.click && li.click(function(e){
				e.stopPropagation();
				this.data.click.call(this);
				$(this).parents(".iui-rmenu").hide();
			});
			li.mousedown(function(e){
				//e.stopPropagation();
				//防止失去Range
				e.preventDefault();
			});
		});
		return ul;
	},
	RMenuSub : function(menus,li){
		var ulSub = iUI.RMenuUl(menus);
		li.append(ulSub).addClass("sub");
		li.mouseover(function(){
			var qDoc = this.ownerDocument;
			//子菜单位置都相对于li
			ulSub.css({
				left : li.offset().left + li.outerWidth() + ulSub.outerWidth() > $(qDoc).width() ? - ulSub.outerWidth() + 2 :  this.offsetWidth - 2,
				 top : Math.max(Math.min(0,iUI.clientHeight()-(li.offset().top + ulSub.outerHeight())-2),-li.offset().top + 2)
			});
		});
		ulSub.mouseover(function(e){
			//阻止子菜单触发上级菜单
			e.stopPropagation();

		});
	},
	//添加菜单的对象,菜单对象，过滤方法
	RMenu : function(query,menus,check){
		var q = $(query)[0];
		var qDoc = q.ownerDocument || q.document || q;

		$(qDoc.head).append('<link rel="stylesheet" type="text/css" href="' + iUIcss + '">');
		var ul = this.RMenuUl(menus);
		ul.addClass("iui-rmenu");
		$(qDoc.body).append(ul);


		//自定义右键菜单
		q.oncontextmenu = function (event){
			var target = event.target;
			if(check && !check(target)){
				return;
			}

			ul.find("li").each(function(){
				this.target = target;
				var liCheck = this.data.check;
				var sub = this.data.renderSub;
				$(this).css("display",!liCheck || liCheck(target,this) ? "block" : "none");
				if(sub){
					$(this).find("ul").remove();
					iUI.RMenuSub(sub(target,this),$(this));
				}
			});

			ul.find("ul").each(function(){
				if($(this).find("li").not('[style*="display: none"]').length == 0)
					$(this.parentNode).css("display","none");
			})
			//最大显示范围
			var maxWidth = iUI.clientWidth() - ul.outerWidth();
			var maxHeight = iUI.clientHeight() - ul.outerHeight();

			var ex = event.pageX;
			var ey = event.pageY;
			ul.css({
				left : ex > maxWidth  ? maxWidth :  ex,
				top  : ey > maxHeight ? maxHeight : ey,
				display : "block"
			});
			return false;
		};

		//点击隐藏菜单
		q.onclick = function (){
			ul.hide();
		};
	}
		
};

var iTimeline = function(arr,updateTime,listCallBack){
	this.arr = arr;
	this.updateTime = updateTime;
	this.map = {};
	this.timeline = $('<div class="lay-con timeline" style="margin-top: 40px;text-align: center"> <ul class="timeline-hor" style="display: inline-block"></ul></div>').appendTo(document.body);
	this.timeHor = this.timeline.find("ul");
	for(var i=0;i<arr.length;i++){
		arr[i].index = i;
		this.map[arr[i].value] = arr[i];
		this.timeHor.append('<li><i class="layui-icon timeline-axis"></i><h3>'+arr[i].name+'</h3></li><li class="timeline-line"></li>');
		if(listCallBack)
			listCallBack(arr[i]);
	}
};

iTimeline.prototype.open = function(t){
	var _this = this;
	var index = this.map[t].index;
	$(".timeline-hor").attr("data-v",index);

	var sBefore = this.arr[index-1];
	var sAfter  = this.arr[index+1];
	var btn = [];
	if(sBefore) btn.push(sBefore.name);
	if(sAfter) btn.push(sAfter.name);

	layer.open({
		type: 1,
		title: "状态",
		btn: btn,
		content:this.timeline,
		area : ["400px","200px"],
		yes : function(index){
			layer.close(index);
			_this.updateTime(sBefore ? sBefore.value : sAfter.value);
		}	,
		btn2: function(index){
			layer.close(index);
			_this.updateTime(sAfter.value);
		}

	});
};