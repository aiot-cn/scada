<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>

<!doctype html>
<html>
	<head>
	<%@include file="../common/page_head.jsp" %>
	<title>点位配置</title>
		
<style type="text/css">
    html,body{
		height: 100%;
        background-color: #F2F2F2;
    }

	[data-state='1']{
		color: #ef8a0c;
	}
	[data-state='2']{
		color: red;
	}
	td.p-img{
		padding: 0 !important;
	}
	.code-dev{
		color: #4d71a9;
	}
	.p-img-target{
		padding: 0 0 0 5px !important;
		text-align: center;
	}
	.p-img-target img{
		max-width: 100px;
		max-height: 60px;
	}
	.s-tag{
		position: absolute;
		left: 5px;
		top: 0;
		line-height: 16px;
		background: #1e9fff;
		color: #fff;
		font-size: 12px;
		padding: 0 3px;
		cursor: pointer;
	}
	/*保存时机 勾选框：方框内名称，左上角小勾。勾选彩色，未选灰色*/
	.rec-box{
		display: inline-block;
		position: relative;
		margin-right: 5px;
		padding: 3px 5px;
		border: 1px solid #c2c2c2;
		border-radius: 3px;
		background: #fafafa;
		color: #999;
		line-height: 16px;
		cursor: pointer;
		white-space: nowrap;
		user-select: none;
	}
	td .rec-box:last-child{
		margin-right: 0;
	}

	.rec-box.on{
		color: #fff;
	}
	.rec-box.on:before{
		color: rgba(255,255,255,0.9);
	}
	.rec-every.on{background: #009688;border-color: #009688;}
	.rec-time.on{background: #1e9fff;border-color: #1e9fff;}
	.rec-state.on{background: #ffb800;border-color: #ffb800;}
</style>
</head>
<body>
<div class="layui-fluid sty-auto-h">
	<div class="layui-row layui-col-space15">

	  <div class="layui-col-md12">
		<div class="layui-card">
	          <div class="layui-card-header">
				  <span class="title">点位</span>
				  <input data-search="tPoint" placeholder="搜索">
				  <a class="layui-btn layui-btn-normal layui-btn-sm" onclick="devPropToPoint()">
					  <i class="layui-icon layui-icon-addition"></i>
					  设备属性
				  </a>
				  <a class="layui-btn layui-btn-normal layui-btn-sm" href="pointImg">
					  <i class="layui-icon layui-icon-picture"></i>
					  图像点位
				  </a>
				  <div data-itable="tool_tPoint" class="itable-tool"></div>
			  </div>
	          <div class="layui-card-body d-table">
	            <div class="layui-row layui-col-space10">
				    <table id="tPoint">
						<thead>
							<tr>
								<th data-field="isRemoved" data-type="switch" width="30" data-class="tac switch-contrary">状态</th>
								<th data-field="id" width="20">ID</th>
								<th data-field="name" data-edit="true">名称</th>
								<th data-field="code" data-edit="true">编号</th>
								<th data-field="image" data-class="p-img" width="80">图像</th>
								<th data-render="renderImg" data-class="p-img-target" width="80">目标</th>
								<th data-render="renderVal">值</th>
								<%--<th data-field="typeId" data-translate="select" data-edit="true">类型</th>
								<th data-field="placeId" data-translate="select" data-edit="true">位置</th>--%>
								<th data-field="unit" data-edit="true">单位</th>
								<th data-render="renderRec" data-class="tac" width="140">保存时机</th>
								<th data-field="recOnValue" data-edit="true">差异保存</th>
								<th data-field="alarmRule" data-edit="true">报警规则</th>
								<th data-type="edit" width="40" class="tac" data-class="tac">操作</th>
							</tr>
						</thead>
					</table>
	            </div>
	        </div>
	      </div>    
	  </div>
  
	</div>
</div>

<div>
	<form data-for="tPoint" class="form-horizontal">
		<input type="hidden" name="id">
		<input class="layui-input" name="image" onclick="common.openFile(this)">
	    <input class="layui-input" name="name" required="required">
		<select class="layui-input" name="deviceId" data-clear="false"></select>
		<select class="layui-input" name="typeId" data-clear="false"></select>
		<select class="layui-input" name="placeId" data-clear="false"></select>
		<input class="layui-input" name="code">
		<input class="layui-input" name="alarmRule">
		<input class="layui-input" name="remark">
		<input class="layui-input" name="unit">
		<input class="layui-input" name="recOnValue" type="number" step="0.0001">
		<select class="layui-input" name="recOnTime" lay-ignore="">
			<option value="false">否</option>
			<option value="true">是</option>
		</select>
		<select class="layui-input" name="recOnState" lay-ignore="">
			<option value="false">否</option>
			<option value="true">是</option>
		</select>
		<select class="layui-input" name="recOnEvery" lay-ignore="">
			<option value="false">否</option>
			<option value="true">是</option>
		</select>
	</form>
</div>
</body>
<script type="text/javascript">
	/*var typeMap = {};
	common.jsonModel("tPointType",{},function(json){
		common.renderSelect("[name='typeId']",json.list);
		$(json.list).each(function (){
			typeMap[this.id] = this;
		});
	});*/

	var tPoint = new iTables("#tPoint",{pageSize:0},{
		baseOption : common.iTableModel("TPoint"),
		//loadOnInit:false,
		render : {
			code : function (td,data){
				var code = data.code || '';
				var span = $("<span>"+code+"</span>").appendTo(td);
				if(code.indexOf("dev-") === 0)
					span.addClass("code-dev");
			},
			image : function (td,data){
				if(data.image)
					$("<img>").appendTo(td).attr({
						"src":"${base}/image"+data.image+"?width=100",
						"data-target" : data.target || ""
					});
			}
		},
		renderVal : function (td,data){
			$(td.parentNode).attr("data-id",data.id);
			$(td).append("<span class='p-val'></span>");
			/*var type = typeMap[data.typeId] || {};
			$(td).append("<span class='p-unit'> "+(type.unit || '')+"</span>");*/
		},
		renderImg : function (td,data){
			if(!data.target)
				return;
			$("<img>").appendTo(td).attr({
				"src":"${base}/image"+data.image+"?target="+data.target
			});
			$(td).append("<span class='s-tag'>"+data.target.split(",")[0]+"</span>");
		},
		/*保存时机：每次/定时/状态 三个勾选框，单击切换并保存*/
		renderRec : function (td,data,icolumn){
			var _self = this;
			var items = [
				["recOnEvery","rec-every","每次","每次保存"],
				["recOnTime","rec-time","定时","定时保存"],
				["recOnState","rec-state","状态","状态保存"]
			];
			$(items).each(function (){
				var field = this[0];
				var box = $("<span class='rec-box "+this[1]+(data[field] ? " on" : "")+"' title='"+this[3]+"'>"+this[2]+"</span>").appendTo(td);
				box.click(function (e) {
					e.stopPropagation();
					var p = {};
					p[_self.primaryKey] = data[_self.primaryKey];
					p[field] = data[field] ? 0 : 1;
					_self.saveData(p);
				});
			});
		},
		/*renderImg : function (td,data){
			$("<img alt='' src=''>").appendTo(td).click(function (){
				layer.open({type : 2,btn : false,shade : 0,title: data.name || data.code || data.id,
					content : "${base}/view/" + data.id + ".point",
					area : ["80%","80%"],scrollbar: false,maxmin: true,
					success: function(layero, index){
						imgWin = window[layero.find('iframe')[0].name];
					}
				});
			});
		},*/
		callback : function (){
			loadData();
		}
	});

	common.ajaxStop(function () {
		tPoint.load();
	});

	function loadData(){
		common.jsonCont("getPointData",{},function (list){
			$(list).each(function (){
				var tr = tPoint.rows[this.id];
				$(tr).find(".p-val").text(this.value).attr({
					"data-state":this.state,
					"title" : common.getAge(this.time)
				});
				/*if(this.image)
					$(tr).find("img").attr("lay-src","${base}/image"+this.image+"?width=100");*/
			});
			/*layui.use('flow', function(){
				layui.flow.lazyimg({
					elem:"img",
					scrollElem : ".d-table"
				});
			});*/
		});
	}

	function devPropToPoint(){
		common.ajax("${base}/device/propToPoint",{},function (json){
			tPoint.load();
		});
	}

	var imgWin,imgData;
	$("#tPoint").on("click","img[data-target]",function (){
		var pointData = this.parentNode.parentNode.data;
		var url = "${base}/view"+pointData.image;
		layer.open({
			type: 2,
			title: false,
			shadeClose:true,
			area: ["80%", "90%"], //宽高
			content: url,
			success : function (layero){
				imgWin = window[layero.find('iframe')[0].name];
				$(imgWin).focus();
				loadLabel("/"+imgWin.pathName);

				imgWin.listener = {
					load : function (data) {
						imgData = data;
						loadLabel("/"+imgWin.pathName);
					},
					label : {
						dblClick: function (e,target) {
							if(!target.data.shape)
								imgWin.$(target).del();
						},
						changed: function (data,label) {
							var point = {
								"id":label.data.id,
								"target" : data.label + ",0,"+data.left.toFixed(6) + ","+data.top.toFixed(6)
										+ ","+data.width.toFixed(6) + ","+data.height.toFixed(6) + "," + (data.angle || 0)
							};
							//新增
							if(!point.id){
								if(tPoint._data.target){
									point.image =  "/"+imgWin.pathName;
									point.code = tPoint._data.code;
									//默认每次保存
									point.recOnEvery = true;
								}else{
									//原图没有标签则保存到原图
									point.id = tPoint._data.id;
									tPoint._data.target = point.target;
								}

							}

							common.jsonModel("tPoint",point,function(json){
								label.data = json.data;
							},{"action": "save"});
						},
						deleted: function (data, label){
							common.jsonModel("tPoint",label.data, function (json){
								tPoint.removeRecord(label.data);
							}, {"action": "del"});
						}
					}
				}
			},
			cancel: function(index, layero) {
				tPoint.load({"image":pointData.image},{"noClear":true});
			}
		});
	}).on("click",".s-tag",function (){
		var tagCode = this.innerText;
		var triggerParam = {"deviceId":-5,"member":tagCode};
		common.jsonModel("sysTrigger",triggerParam,function (json){
			var trigger = json.list[0];
			if(trigger){
				openTriggerWork(trigger.id);
			}else {
				layer.confirm("还未配置标签【"+tagCode+"】的解析，要创建吗？", {icon: 3}, function(index){
					layer.close(index);
					triggerParam.name = "点位标签";
					common.jsonModel("sysTrigger",triggerParam,function (json){
						openTriggerWork(json.data.id);
					},{action:"save"});
				});
			}
		});
	});

	function openTriggerWork(wordId){
		layer.open({
			type: 2,
			title: false,
			shadeClose:true,
			area: ["90%", "90%"],
			content: "${base}/base/editor/workflow?PROTOCOL=tWorkflow-SysTrigger/"+wordId
		});
	}

	function loadLabel(imgPath){
		common.jsonModel("tPoint",{"image":imgPath},function (json){
			$(json.list).each(function (){
				if(!this.target)
					return;
				var label = imgWin.addLabelByStr(this.target);
				label.data = this;
				if(this.shape){
					var label2 = imgWin.addLabelByStr(",0,"+this.shape);
					$(label2).addClass("event-none");
				}
			});
		});
	}


</script>
</html>