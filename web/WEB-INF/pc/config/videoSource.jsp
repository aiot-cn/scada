<%@ page import="org.aiot.device.base.ZLMediaKit" %>
<%@ page import="org.aiot.service.DeviceService" %>
<%@ page import="org.aiot.main.Constants" %>
<%@ page contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%
	DeviceService ds = Constants.ioc.get(DeviceService.class);
	ZLMediaKit mediaKit = ds.getDevice(ZLMediaKit.class);
	request.setAttribute("mediaKit",mediaKit);
%>
<!doctype html>
<html>
	<head>
	<%@include file="../common/page_head.jsp" %>
	<title>流媒体</title>
		
<style type="text/css">
	html,body{
		height: 100%
	}
	#d-service-info{
		padding: 0 10px;
		margin-bottom: 0;
		margin-right: -15px;
	}
	.scroll-wrapper {
		width: 100%;
		height: 100%;
		padding: 0;
		margin: 0;
	}
	.camera-info{
		border: 1px solid #1779cf;
		border-radius: 3px;
		font-size: 12px;
		padding: 2px 0;
		margin-right: 5px;
	}
	.camera-info a{
		border-right: 1px solid #1779cf;
		padding: 3px 5px;
	}

	.schema-info{
		border: 1px solid #009688;
		border-radius: 3px;
		font-size: 12px;
		padding: 2px 0;
		margin-right: 5px;
		text-transform: uppercase;
	}
	.schema-info:empty{
		display: none;
	}
	.schema-info a{
		border-right: 1px solid #009688;
		color:#009688;
		padding: 3px 5px;
	}
	.camera-info a:last-child,
	.schema-info a:last-child{
		border-right: none;
	}
</style>
</head>
<body>
<div class="layui-fluid sty-auto-h">

<div class="layui-row layui-col-space15">
  <div class="layui-col-md6">
		<div class="layui-card">
          <div class="layui-card-header">
			<span class="title">流媒体</span>
			<input data-search="tDictType" placeholder="搜索">
			  <button data-itable="create_tVideoSource" class="layui-btn layui-btn-normal layui-btn-sm">
				  <i class="layui-icon layui-icon-addition"></i> 添加
			  </button>
			  <div style="float: right">
				  <c:if test="${!mediaKit.hasLib}">
					  <blockquote id="d-service-info" class="aiot-info-error emoji-font">
						  ⚠ 还未安装流媒体服务，点击 <a onclick="installLib()">安装</a>
					  </blockquote>
				  </c:if>
			  </div>
		  </div>
          <div class="layui-card-body">
            <div class="layui-row layui-col-space10">
 
				<table id="tVideoSource">
					<thead>
						<tr>
							<th data-field="isRemoved" data-type="switch" width="30" data-class="tac switch-contrary-no" class="tac switch-contrary-no itable-sort">序号</th>
							<th data-field="name">名称</th>
							<th data-field="url">地址</th>
							<th data-render="schemaRender">在线</th>
							<th data-field="workId" data-type="select">工作</th>
							<th data-type="edit" class="tac" data-class="tac" width="70">编辑</th>
						</tr>
					</thead>
				</table>
	
            </div>
        </div>
      </div>    
  </div>
  <div class="layui-col-md6">
		<div class="layui-card">
          <div class="layui-card-header">
		      	视频预览
			  <div data-itable="tool_tDictValue" class="itable-tool"></div>
		  </div>
          <div class="layui-card-body">
            <div class="layui-row layui-col-space10">
				<iframe class="scroll-wrapper" frameborder="0" name="fm"></iframe>
            </div>
        </div>
      </div>    
  </div>
</div>

</div>

<div>
	<form name="f1" data-for="tVideoSource" class="layui-form-pane" style="padding:15px 15px 0 15px"
		  data-layer='{title : "视频源",offset:["30px","90px"],area : ["500px","auto"]}'>
	    <input type="hidden" name="id">
		<div class="layui-form-item">
			<label class="layui-form-label">名称</label>
			<div class="layui-input-block">
				<input class="layui-input" name="name" required="required">
			</div>
		</div>
		<div class="layui-form-item">
			<label class="layui-form-label">品牌</label>
			<div class="layui-input-block">
				<select class="layui-input" name="cameraBrand" onchange="changeBrand(this.value)">
					<option value="">自定义</option>
				</select>
			</div>
		</div>
		<div class="layui-form-item">
			<label class="layui-form-label">地址</label>
			<div class="layui-input-block">
				<input class="layui-input" name="url">
			</div>
		</div>
		<div class="layui-form-item">
			<label class="layui-form-label">账号</label>
			<div class="layui-input-block">
				<input class="layui-input" name="account">
			</div>
		</div>
		<div class="layui-form-item">
			<label class="layui-form-label">密码</label>
			<div class="layui-input-block">
				<input type="password" class="layui-input" name="password">
			</div>
		</div>
		<div class="layui-form-item">
			<label class="layui-form-label">工作</label>
			<div class="layui-input-block">
				<select class="layui-input" name="workId"></select>
			</div>
		</div>
	<div id="videoAccount" style="visibility: hidden">
		<div class="layui-form-item">
			<label class="layui-form-label">通道</label>
			<div class="layui-input-block">
				<input class="layui-input" name="channel">
			</div>
		</div>
		<div class="layui-form-item">
			<label class="layui-form-label">码流</label>
			<div class="layui-input-block">
				<select name="streamType" class="layui-input">
					<option value="0">主码流</option>
					<option value="1">子码流</option>
					<option value="2">第三码流</option>
				</select>
			</div>
		</div>
	</div>


	</form>

</div>
</body>
<script type="text/javascript">
	param.d = "ZLMediaKit";
	var cameraBrandMap = {};

	common.jsonEnum("cameraBrand",function (list){
		common.renderSelect(f1.cameraBrand,list,{empty:false,value:"code"});
		$(list).each(function (){
			cameraBrandMap[this.code] = this;
		});
	});
	common.jsonModel("TWorkflow",{pid:"null"},function(json){
		var list = json.list;
		$(list).each(function (){
			this.name = this.name || this.code;
		});
		common.renderSelect(f1.workId,list,{def:""});
		tVideoSource.load();
	});

	var tVideoSource = new iTables("#tVideoSource",{},{
		baseOption : common.iTableModel("tVideoSource","sequence"),
		inline_edit : false,
		loadOnInit : false,
		render : {
			"url" : function (td,data){
				if(!data.cameraBrand)
					return data.url;
				var cb = cameraBrandMap[data.cameraBrand].name;
				var streamName = ["主码流","子码流","第三码流"];
				return "<span class='camera-info'><a>"+ cb +"</a><a>通道"+data.channel+"</a><a>"+streamName[data.streamType]+"</a></span>"+data.url;
			}
		},
		schemaRender:function(td,data){
			return "<span class='schema-info' data-id='"+data.id+"'></span>";
		},
		onSelect : function (tr,data){
			var suffix = common.isWindowsBrowser() ? "mp4" : "m3u8"
			$('[name="fm"]').attr("src","http://localhost:${mediaKit.serverPort}/live/"+data.id+".live."+suffix);
		},
		loadAfter : function (){
			this.table.find("tbody tr").each(function (){
				if(this.data.isRemoved == 0){
					tVideoSource._selectTr(this);
					return false;
				}
			});
			setInterval(getMediaList,10000);
		},
		beforeStats : function (){
			getMediaList();
		}
	});

	function changeBrand(type){
		$("#videoAccount").css("visibility",type ? "visible" : "hidden");
		f1.url.placeholder = type ? "192.168.1.64" : "192.168.1.64:554/h264/ch1/main/av_stream";
	}

	function installLib(){
		common.devExec2("installLib",{},function (json){
			$("#d-service-info").hide();
		},{maskType: 1});
	}

	function getMediaList(){
		common.devExec2("getMediaList",{},function (json){
			var schemaSpan = $(".schema-info").empty();
			var list = json.data.data;
			$(list).each(function (){
				var span = schemaSpan.filter("[data-id='"+this.stream+"']");
				var title = [];
				if(this.app == "live" && this.originTypeStr == "pull"){
					$(this.tracks).each(function (){
						title.push(this.codec_id_name);
						if(this.fps)
							title.push(this.fps + "FPS");
					});
					var a = $("<a>"+this.schema+"</a>").appendTo(span);
					a.attr("title",title.join(" "));
				}
			});
		});
	}

</script>
</html>