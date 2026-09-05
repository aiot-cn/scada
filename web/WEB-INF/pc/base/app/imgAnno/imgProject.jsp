<%@ page import="org.aiot.service.DeviceService" %>
<%@ page import="org.aiot.main.Constants" %>
<%@ page import="org.aiot.device.base.imgAnno.ImgAnnoDevice" %>
<%@ page contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%
	DeviceService ds = Constants.ioc.get(DeviceService.class);
	Long devId = Long.parseLong(request.getParameter("d"));
	ImgAnnoDevice imageDevice = (ImgAnnoDevice) ds.getInstance(devId);
	request.setAttribute("imgPath",imageDevice.getImgPath());
	request.setAttribute("device",imageDevice.getDevice());
%>
<!doctype html>
<html>
	<head>
	<%@include file="../../../common/page_head.jsp" %>
	<title>图像-数据集</title>
		
<style type="text/css">
	html,body{
		height: 100%;
	}
	.tag-color{
		padding: 0 !important;
		border-right: none;
	}
	.tag-color i {
		display: inline-block;
		width: 20px;
		height: 31px;
		float: left;
	}
	.tag-color .layui-input{
		border: none;
		padding: 0;
		width: 20px;
	}
	.layui-tab{
		height: 100%;
	}
	.layui-tab-content{
		height: calc(100% - 65px);
		overflow: auto;
	}

	.pt-des {
		font-size: 12px;
		color: #999;
		overflow: hidden;
		text-overflow: ellipsis;
		white-space: nowrap;
	}
	.pt-del{
		font-size: 20px;
		float: right;
		margin-left: 5px;
		color: #e92511;
		display: none;
		cursor: pointer;
	}
	td:hover .pt-del{
		display: inline;
	}
</style>
</head>
<body>

<div class="layui-fluid sty-auto-h" >

	<div class="layui-row layui-col-space15">
		<div class="layui-col-md9">
			<div class="layui-card">
				<div class="layui-card-header">
					<span class="title">数据集</span>
					<div class="layui-inline">
						<div class="layui-input-inline" style="width: 200px;">
							<input data-search="tImgProject" placeholder="搜索" class="layui-input">
						</div>
					</div>

				</div>
				<div class="layui-card-body">
					<div class="layui-row layui-col-space10">

						<table id="tImgProject" class="layui-table">
							<thead>
								<tr>
									<th data-field="name">名称</th>
									<th data-field="path">目录</th>
									<th data-field="createDate" data-type="dateTime" width="120">时间</th>
									<th data-type="edit" data-render="renderEdit" class="tac" data-class="tac" width="40">操作</th>
								</tr>
							</thead>
						</table>

					</div>
				</div>
			</div>
		</div>
		<div class="layui-col-md3">

			<div class="layui-card">
				<div class="layui-tab layui-tab-brief">
					<ul class="layui-tab-title">
						<li>标签</li>
						<li>分组</li>
						<li class="layui-this">模型</li>
						<li class="li-input"><input data-search="tImgTag" placeholder="搜索" class="layui-input"></li>
					</ul>
					<div class="layui-tab-content">

						<div class="layui-tab-item">
							<table id="tImgTag" class="layui-table" style="margin: 0">
								<thead>
								<tr>
									<th data-field="color" data-edit="true" data-class="tag-color" width="20"></th>
									<th data-field="code" data-edit="true">标签</th>
									<th data-field="name" data-edit="true">名称</th>
									<th data-type="edit" class="tac" data-class="tac" width="40">操作</th>
								</tr>
								</thead>
							</table>
						</div>
						<div class="layui-tab-item">
							<table id="tImgGroup" class="layui-table" style="margin: 0">
								<thead>
								<tr>
									<th data-field="name" data-edit="true">分组</th>
									<th data-type="edit" class="tac" data-class="tac" width="40">操作</th>
								</tr>
								</thead>
							</table>
						</div>
						<div class="layui-tab-item layui-show">
							<table id="tPt" class="layui-table" lay-skin="line" style="table-layout:fixed;margin: 0;border: none;">
								<thead>
								<tr style="display: none">
									<%--<th data-type="rownum" width="20"></th>--%>
									<th data-field="title" data-edit="true">名称</th>
								</tr>
								</thead>
							</table>
						</div>
					</div>
				</div>

			</div>
		</div>

	</div>

</div>

<form data-for="tImgProject" class="form-horizontal">
	<input type="hidden" name="id">
	<input class="layui-input" name="name">
	<input class="layui-input" name="path" required="required" readonly onclick="common.openFile({input:this,base:'${imgPath}',suffix:'..'})">
</form>

<form data-for="tImgTag" class="form-horizontal">
	<input type="hidden" name="id">
	<input class="layui-input" name="name">
	<input class="layui-input" name="code" required="required">
	<input class="layui-input" name="color" type="color" data-default="(randomColor())">
</form>

<form data-for="tImgGroup" class="form-horizontal">
	<input type="hidden" name="id">
	<input class="layui-input" name="name" required="required">
</form>
</body>
<script type="text/javascript">
	var imgPath = "${imgPath}";

	var tImgProject = new iTables("#tImgProject",{},{
		baseOption : common.iTableModel("ImgProject"),
		DESC : "id",
		onSelect : function(tr,data){
			tImgTag.load({pid:data.id});
			tImgGroup.load({pid:data.id});
			tPt.load({"plass":"IMGTAG"+data.id});
		},
		loadAfter:function(){
			//this.selectIndex(0);
			tPt.load({"plass":"%IMGTAG%"});
		},
		render : {
			name : function (td,data) {
				var url = 'imgAnnotation?d=' + param.d + '&id='+data.id;
				return $("<a>"+data.name+"</a>").click(function (){
					location.href = url;
					parent.setNavSub("${device.name} - "+data.name)
				});
			}
		},
		renderEdit : function (td,data){
			$("<i title='文档' class='layui-icon layui-icon-file' style='color: #246DBD;'></i>").appendTo(td).click(function (){
				common.openFrame(data.name,"${base}/view/ttxt:/imgProject/"+data.id+".htm",{area:["900px","90%"]});
			});
		}
	});

	var tImgTag = new iTables("#tImgTag",{},{
		baseOption : common.iTableModel("ImgTag","sequence"),
		loadOnInit : false,
		callForm : function(params){
			params.pid = tImgProject._data.id;
		},
		render : {
			color : function (td,data) {
				return "<i style='background-color: "+data.color+"'></i>";
			}
		}
	});

	var tImgGroup = new iTables("#tImgGroup",{},{
		baseOption : common.iTableModel("ImgGroup"),
		searchInput : '[data-search="tImgTag"]',
		loadOnInit : false,
		callForm : function(params){
			params.pid = tImgProject._data.id;
		}
	});

	var tPt = new iTables("#tPt",{"pageSize":300},{
		baseOption : common.iTableModel("tFile"),
		delController  : "${base}/json/devExec/"+param.d+"/delPt",
		searchInput : '[data-search="tImgTag"]',
		DESC : "id",
		inline_edit :false,
		loadOnInit : false,
		callForm : function(params){

		},
		render : {
			title : function (td,data){
				var t1 = data.pathName.split("/");
				//td.title = data.description;
				var t0 = data.title || (data.pathName.indexOf("best.pt") > 0 ? t1[t1.length-3] : t1[t1.length-1]);
				var ctime = data.createDate.slice(0,16);

				var url = "${config.domain}${base}/view"+data.pathName;
				var proId = data.plass.replace("IMGTAG","");
				var proPath = tImgProject.rows[proId].data.path;
				var dir = data.pathName.split(proPath)[1].split("/train/")[0];
				var proUrl = "${base}/plugin/image/imgAnnotation?d="+param.d+"&id="+proId+"&dir="+dir;
				$(td).append("<a target='_blank' href='"+url+"' title='"+(dir)+"'>"+t0+"</a>");
				$("<i class='layui-icon layui-icon-delete pt-del'></i>").appendTo(td).click(function (){
					tPt._onRemove(data);
				});
				$(td).append("<a target='_blank' href='"+proUrl+"'  style='float: right;color:#666'>"+ctime+"</a>");
				$("<div class='pt-des'>"+(data.description || '')+"</div>").appendTo(td)[0].title = data.description;
			}
		}

	});

	function randomColor(){
		var c = "000000"+Math.floor(Math.random() * 0xFFFFFF).toString(16);
		return "#"+c.slice(-6);
	}

</script>
</html>