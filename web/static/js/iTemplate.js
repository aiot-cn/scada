/**
 * 模板功能
 * data-ite='cameraJpg'
 * data-val='c1'
 * data-arg='a:1,b:2'
 */
var iTemplate = {
	xhr : new XMLHttpRequest(),
	nodes : {},
	timer : null,
	init : function (timeout){
		iTemplate.nodes = $("[data-ite]");
		iTemplate.render();
		iTemplate.timer = setInterval(iTemplate.render,timeout || 2000);
	},

	render : function(){
		common.jsonCont("getRefVal",{},function (json){
			iTemplate.nodes.each(function () {
				var ite = $(this).data("ite");
				var val = $(this).data("val");
				iTemplate["re_"+ite](this,json[val],json);
				this.renderTime = new Date().getTime();
			});
			iTemplate.callBack(json);
		},{"xhr" : iTemplate.xhr});
	},
	callBack : function (json){

	},
	re_text : function(node,param){
		$(node).text(param);
	},
	re_state : function(node,param){
		if(param)
			$(node).attr("data-state",param.state);
	},
	re_node : function(node,param,json){
		var nodeUl = node.nodeUl;
		if(!nodeUl){
			nodeUl = node.nodeUl = $("<ul class='node-ul'></ul>").appendTo(node);

			common.jsonModel("deviceReflect",{"type":2,"isRemoved":0},function(json){
				$(json.list).each(function(){
					var t = "<li>" +
						"<span class='node-name'>"+this.name+"</span>" +
						"<i class='node-state'> </i>" +
						"<span class='node-time'></span>" +
						"<select class='node-sel'>" +
						"<option value>自动</option>" +
						"<option value='1'>ON</option>" +
						"<option value='0'>OFF</option>" +
						"</select>" +
						"</li>";
					var li = $(t).appendTo(".node-ul");
					li[0].data = this;
					var p = {
						"deviceId": this.deviceId,
						"analysis": this.analysisType,
					};
					li.find("select").change(function(){
						p.value = this.value;
						common.jsonCont("setDevToVal",p,function(data){});
					});
				});

			});
		}else{
			nodeUl.find("li").each(function (){
				var data = json["DR_"+this.data.id];
				if(data){
					$(this).find(".node-state").attr("data-state",data.value);
					$(this).find(".node-sel").val(data.toVal);
				}
			});
		}
	},
	re_cameraJpg : function(node,param){
		var arg = $(node).data("arg");
		if(arg){
			if(node.nodeName != "IMG"){
				var img = $(node).find("img")[0];
				if(img){
					node = img;
				}else{
					node = $("<img style='width:100%;height:100%'>").appendTo(node);
				}

			}
			var p = arg.split(",");
			if(p.length > 1)
				$(node).attr("src",base+"/json/devExec/"+p[0]+"/getChannelJpg?channel="+p[1]+"&w="+node.width+"&t="+new Date().getTime());
		}

	},
	re_vlc : function(node,param){
		if(node.vlc)
			return;
		node.vlc = true;
		var args = $(node).data("arg");
		var arg = eval('({' + args + '})');
		var v = "<object type='application/x-vlc-plugin' width='"+node.offsetWidth+"' height='"+node.offsetHeight+"'>" +
			"<param name='mrl' value='"+arg.url+"' />" +
			"<param name='controls' value='false' />" +
			"</object>";
		node.vlc = $(v).appendTo(node)[0];
		node.vlc.video.aspectRatio = node.offsetWidth + ":" +node.offsetHeight;
	},
	re_table : function(node,param){
		var table = node.table;
		if(!table){
			var nTable = $(node).find("table")[0];
			if(nTable){
				node.table = new iTables(nTable,{},{});
			}else{
				// name,姓名;sex,性别;state,状态,state
				var args = $(node).data("arg");
				var t = "<table><thead><tr>";
				var f = args.split(";");
				$(f).each(function (){
					var d = this.split(",");
					t += "<th data-field='"+d[0]+"' data-type='"+(d[2] || '')+"'>"+d[1]+"</th>";
				});
				t += "</tr></thead></table>";
				node.table = new iTables($(t).appendTo(node),{},{});
			}
		}
		node.table._onLoaded(param)

	},
	re_callback : function(node,param,json){
		var args = $(node).data("arg");
		eval('(' + args + ')');
	},
	re_echars : function (node,param,json){
		if(!node.echarts)
			node.echarts = echarts.init(node);

		var args = $(node).data("arg");
		var arg = eval('({' + args + '})');
		var arr = [];
		if(arg.xAxis)
			arr = arg.xAxis.split(",");
		else
			for (var i = 0; i < arg.total / arg.step; i++) {
				arr.push(arg.step*i);
			}
		var series = [];
		$(arg.series).each(function (){
			var dataArr = [];
			$(this.data.split(",")).each(function (){
				var v = json[this];
				if(v instanceof Array){
					dataArr = dataArr.concat(v);
				}else{
					dataArr.push(v);
				}
			});
			this.data = dataArr;
			series.push(this);
		});

		var option = {
			/*title: {
				text: '折线图堆叠'
			},*/
			tooltip: {
				trigger: 'axis' //鼠标悬停显示
			},
			xAxis: {
				type: "category",
				//boundaryGap: false //坐标轴两端空白策略，数组内数值代表百分比
				data: arr,
			},
			yAxis: {
				type: 'value'
			},
			grid: {
				top: "20px",
				bottom: "20px",
				left: "20px",
				right: "20px",
				//containLabel: true //坐标轴标签溢出
			},
			series: [
				{
					name : 'aaa',
					type: 'line',//柱状bar
					smooth: true,//平滑
					data: [],
				}
			]
		};
		option.series = series;
		node.echarts.setOption(option);
	}
};




