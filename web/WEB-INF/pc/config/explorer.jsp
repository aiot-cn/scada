<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="org.aiot.main.Constants" %>

<!doctype html>
<html>
<head>
	<title>资源管理器</title>
	<%@include file="../common/page_head.jsp" %>
	<script src="${res}/plugin/iUI/iUI.js"></script>
	<style type="text/css">
		html,body,#area{
			height: 100%;
			overflow: hidden;
		}
		.tool-bar{
			padding: 15px 40px;
			border-bottom: 1px solid #ccc;
		}
		.list-d{
			margin: 10px 0 0 10px;
			height: calc(100% - 71px);
			overflow: auto;
		}
		.list-d li{
			float: left;
			height: 95px;
			width: 100px;
			text-align: center;
			margin: 0 5px 5px 0;
			box-sizing: border-box;
			border: 1px solid rgba(0,0,0,0);
			border-radius: 3px;
			position: relative;
			overflow: hidden;
		}

		i[class^="ico-"]{
			display: inline-block;
			width: 100%;
			height: 60px;
			background: url("${res}/images/ico/normal.png") center no-repeat;
			-border: 1px solid red;
		}
		.ico-webp{
			background-size: 70px !important;
		}
		<c:forEach items="${Constants.prop}" var="i">
		<c:if test="${i.key.contains('ico.')}">
		i.ico-${i.key.substring(4)}{
			background-image: url("${res}/images/ico/${i.value}.png");
		}
		</c:if>
		</c:forEach>

		i.ico-dir-AppData{
			background-image: url("${res}/images/ico/dir-AppData.png");
		}
		i.ico-dir-audio{
			background-image: url("${res}/images/ico/dir-audio.png");
		}
		i.ico-dir-document{
			background-image: url("${res}/images/ico/dir-document.png");
		}
		i.ico-dir-image{
			background-image: url("${res}/images/ico/dir-image.png");
		}
		i.ico-dir-lib{
			background-image: url("${res}/images/ico/dir-lib.png");
		}
		i.ico-dir-temp{
			background-image: url("${res}/images/ico/dir-temp.png");
		}
		i.ico-dir-video{
			background-image: url("${res}/images/ico/dir-video.png");
		}
		i.ico-dir-ram{
			background-image: url("${res}/images/ico/dir-ram.png");
		}
		i.ico-dir-root{
			background-image: url("${res}/images/ico/dir-root.png");
		}

		.list-d li div{
			word-wrap: break-word;
			margin-top: -5px;
			font-size: 12px;
		}

		.list-d li:hover{
			border: 1px solid #b8d6fb;
			background-color: #eef5fd;
			overflow: visible;
		}
		.list-d li.selected{
			border: 1px solid #7da2ce;
			background-color: #cde2fc;
		}

		.list-d li:hover .layui-icon-close{
			display: inline;
			position: absolute;
			right: 0;
			top: 0;
			background-color: #24A6E8;
			color: #fff;
		}

		.t1{
			width: 100%;
			line-height: 30px;
		}
		.t1 tr td:nth-child(1){
			color: #5e460c;
		}

	</style>
</head>
<body>
<div id="area">
	<div class="tool-bar">
		<img src="${res}/images/ico/arrows.png" style="width: 24px;transform: rotate(90deg);" onclick="pathBack()" title="上级">
		<input id="path" style="width: 400px;height: 24px;padding: 0 10px;" placeholder="/">
		<label for="file" style="cursor: pointer"><img src="${res}/images/ico/dir3.png" style="width: 30px" title="上传"></label>
		<input id="file" multiple="multiple" type="file" onchange="upload()" style="display: none">
		<input id="search" style="width: 150px;height: 24px;padding: 0 10px;" placeholder="搜索">
	</div>
	<div class="list-d"></div>
</div>

<div>
	<input id="url">
</div>

<div class="lay-con">
	<table class="t1">
		<tr>
			<td width="50">大小</td>
			<td data-field="usize"></td>
		</tr>
		<tr>
			<td>时间</td>
			<td data-field="createDate"></td>
		</tr>
		<tr>
			<td>MD5</td>
			<td data-field="md5"></td>
		</tr>
		<tr>
			<td style="vertical-align: top">描述</td>
			<td>
				<textarea name="description" class="layui-input" style="height: 100px;margin-top: 8px;padding: 10px;"></textarea>
			</td>
		</tr>
	</table>
</div>

<script type="text/javascript">
	var imgType = ["jpg","jpeg","bmp","png","gif","tif","tiff","webp","dng","mpo","pfm"];
	var rootDir = ["AppData","audio","document","image","lib","temp","video","ram","root"];
	var fdata = {};
	var imgIndex = 0;fileInfo = {};
	var $path = $("#path");
	var $listDiv = $(".list-d");
	var $description = $("textarea[name='description']");
	var $title = $("[name='title']");

	var basePath = decodeURIComponent(param.base || "");
	var isRoot = false;
	var lis = [],nameArr = [];

	$(function(){
		var path = decodeURIComponent(location.hash.substring(1) || "");
		setPath(path);
	});

	function path() {
		return ("/" + (basePath || "") + "/" + $path.val()).replace(/\/+/g, "/");
	}

	function setPath(path){
		if(/\.[a-zA-Z0-9]{1,4}$/.test(path)){
			var i = path.lastIndexOf("/");
			var name = path.substring(i+1);
			path = path.substring(0,i);
			$("#search").val(name);
		}
		isRoot = !path;
		$path.val(path);
		location.hash = "#"+path;
		load();
	}

	function absoluteFile(){
		return path() + "/" + fdata.name;
	}

	function callBackPath(){
		var pathArr = [];
		$(".selected").each(function (){
			pathArr.push($path.val() + "/" + this.data.name);
		});
		if(pathArr.length == 0)
			return $path.val();
		if(pathArr.length == 1)
			return pathArr[0];
		return pathArr;
	}

	function load(){
		$(".list-d li").remove();
		fdata = {};
		lis = [];
		common.ajax("${base}/file/getList", {path:path()}, function(arr){
			nameArr = arr || [];
			var list = nameArr.sort(function (s1,s2) {
				var i1 = s1.lastIndexOf(".");
				var x = s1.substring(i1) + s1.substring(0,i1);
				//文件夹
				if(i1 == -1){
					x = (isRoot && rootDir.indexOf(x) > -1 ? " " : "!")+x;
				}

				var i2 = s2.lastIndexOf(".");
				var y = s2.substring(i2) + s2.substring(0,i2);
				if(i2 == -1)
					y = (isRoot && rootDir.indexOf(y) > -1 ? " " : "!")+y;
				return x.localeCompare(y);
			});
			imgIndex = 0;
			var searchName = $("#search").val();
			for(var i=0;i<list.length;i++){
				if(param.suffix){
					var i1 = list[i].lastIndexOf(".");
					var suffix2 = list[i].slice(i1+1).toLowerCase();
					if(i1 != -1 && param.suffix.indexOf(suffix2) == -1)
						continue;
				}
				if(list[i] == "@eaDir"){
					continue;
				}
				var li = addFile(list[i]);
				if(searchName && searchName == list[i]){
					li.addClass("selected");
				}
			}
		});
	}

	function addFile(name) {
		name = name.substring(name.lastIndexOf("/") + 1);
		var type = "dir";
		var t1 = name.lastIndexOf(".");
		if(t1 > -1)
			type = name.substring(t1 + 1).toLowerCase();

		var className = "ico-"+type;
		if(type == "dir" && isRoot){
			className += " ico-dir-"+ name;
		}
		var li = $('<li><i class="'+className+'"></i><div>'+ name + '</div></li>');
		if(imgIndex < 50 &&isImg(type)){
		var pathName = path() + "/" + name;
		li.find("i").css("background-image",'url("${base}/image'+pathName+'?width=70")');
			imgIndex ++;
		}
		li[0].data = {"type":type,"name":name};
		lis.push(li[0]);
		$listDiv.append(li);
		return li;
	}

	function createFile(name){
		common.ajax("${base}/file/create",{"path":path(),"name":name},function(){
			addFile(name);
		});
	}

	function isImg(type) {
		return type && imgType.indexOf(type.toLowerCase()) > -1;
	}

	function pathBack() {
		var path = $path.val();
		setPath(path.substring(0,path.lastIndexOf("/")))
	}

	function upload() {
		uploadFile(document.getElementById("file").files);
	}

	function uploadFile(files){
		common.uploadFile(files,{path:path()},function(json){
			addFile(json.message);
		});
	}

	document.addEventListener('paste', function(e) {
		e.preventDefault();
		e.stopPropagation();
		var items = e.clipboardData && e.clipboardData.items;
		if (!items || items.length === 0) {
			return;
		}
		var files = [];
		for (var i = 0; i < items.length; i++) {
			if (items[i].kind === 'file') {
				var file = items[i].getAsFile();
				if (file) {
					files.push(file);
				}
			}
		}
		if (files.length > 0) {
			uploadFile(files);
		}
	});

	$path.keyup(function (e) {
		if(e.keyCode == 13)
			setPath($path.val());
	});

	$("#search").keyup(function (e) {
		var v = this.value;
		$(lis).each(function (){
			if(!v || this.data.name.indexOf(v)>-1)
				this.style.display = "list-item";
			else
				this.style.display = "none";
		});
	});

	$listDiv.on("mousedown","li",function () {
		fdata = this.data;
		$("li.selected").removeClass("selected");
		$(this).addClass("selected")
	}).on("dblclick","li",function () {
		var pathName = $path.val() + "/" + this.data.name.replace(/#/g,"%23");
		if(this.data.type == "dir"){
			setPath(pathName);
		}else if(parent.explorerCallback){
			parent.explorerCallback(pathName);
		}else{
			layer.open({
				type : 2,
				btn : false,
				shade : 0,
				title: this.data.name,
				content : "${base}/view" + pathName,
				area : ["80%","80%"],
				scrollbar: false,
				maxmin: true,
				success : function (layero){
					if(common.isImg(pathName)){
						imgWin = window[layero.find('iframe')[0].name];
						var pathNameArr = [];
						for(var i=0;i<nameArr.length;i++){
							pathNameArr.push({"name" : path() + "/" + nameArr[i]});
						}
						imgWin.setFileList(pathNameArr);
					}
				}
			});
		}
	});

	document.body.ondrop = function (e) {
		e.preventDefault();
		e.stopPropagation();
		var files = e.dataTransfer.files; //获取文件对象
		uploadFile(files);
	};

	//ondragover 会频繁触发不给ondrop机会
	document.body.ondragover = function(){
		return false;
	};

	var m1 = [
		{
			title : "下载",
			icon : "layui-icon layui-icon-download-circle",
			check : function(node){
				return node.data || node.parentNode.data;
			},
			click : function(){
				window.open("${base}/file/download"+absoluteFile());
			}
		},{
			title : "删除",
			icon : "layui-icon layui-icon-delete",
			check : function(node){
				return node.data || node.parentNode.data;
			},
			click : function(){
				layer.confirm( fdata.name, {icon: 3, title:'删除'}, function(index){
					layer.close(index);
					common.ajax("${base}/file/del",{"name":absoluteFile()},function () {
						$("li.selected").remove();
					});
				});
			}
		},{
			title : "新建文件",
			icon : "layui-icon layui-icon-file",
			click : function(){
				layer.prompt({"formType":0,title:"新建文件名称"}, function (value, index, elem){
							createFile(value);
							layer.close(index);
						}
				);
			}
		},{
			title : "新建文件夹",
			icon : "layui-icon layui-icon-list",
			click : function(){
				layer.prompt({"formType":0,title:"新建文件夹名称"}, function (value, index, elem) {
					layer.close(index);
					createFile(value);
				});
			}
		},{
			title : "重命名",
			check : function(node){
				return node.data || node.parentNode.data;
			},
			click : function(){
				var oldName = fdata.name;
				var suffix = "";
				if(fdata.type != "dir"){
					var i = fdata.name.lastIndexOf(".");
					oldName = fdata.name.substring(0,i);
					suffix = fdata.name.substring(i);
				}

				layer.prompt({"formType":0,title:"重命名","value":oldName}, function (value, index, elem) {
					layer.close(index);
					common.ajax("${base}/file/rename",{"name":absoluteFile(),"newName":value + suffix},function () {
						fdata.name = value + suffix;
						$("li.selected").find("div").text(fdata.name);
					});
				});
			}
		},{
			title : "解压",
			check : function(node){
				var d = node.data || node.parentNode.data;
				return d && d.type == "zip";
			},
			click : function(){
				common.ajax("${base}/file/unzip",{"name":absoluteFile()},function(json) {
					layer.msg("解压"+json.data+"个文件");
					load();
				});
			}
		},{
			title : "发送到",
			icon : "layui-icon layui-icon-share",
			check : function(node){
				var d = node.data || node.parentNode.data;
				return d && d.type != "dir";
			},
			sub : [{
				title : "钉钉",
				click : function(){
					var url = ("${config.domain}" || location.origin)+"${base}/view";
					var a = ("/"+basePath+"/"+$path.val()+"/"+fdata.name).replace(/\/\/+/g,"/").substring(1).split("\/");
					$(a).each(function (){
						url += "/"+encodeURIComponent(this);
					});
					$("#url").val(url).select();
					document.execCommand("copy");
					window.open("dingtalk://dingtalkclient/page/link");
				}
			}]
		},{
			title : "属性",
			icon : "layui-icon layui-icon-reply-fill",
			check : function(node){
				var d = node.data || node.parentNode.data;
				return d && d.type != "dir";
			},
			click : function(){
				common.ajax("${base}/file/getInfo",{name:absoluteFile()},function (json) {
					json.usize = json.size.toFixed(1) + "kb";
					if(json.size > 1024)
						json.usize = (json.size / 1024).toFixed(1) + "Mb";
					fileInfo = json;
					$("[data-field]").each(function () {
						$(this).text(json[$(this).data("field")] || "");
					});
					$description.val(json.description || "");
					$title.val(json.title || "");
				});
				layer.open({
					type: 1,
					title : fdata.name,
					btn: ['保存'],
					content : $(".lay-con"),
					area :["500px","auto"],
					yes : function (index) {
						layer.close(index);
						fileInfo.description = $description.val();
						fileInfo.title = $title.val();
						common.ajax("${base}/file/saveInfo",fileInfo);
					}
				});
			}
		}];

	common.addDevExtend(m1,{
		getVal : absoluteFile,
		getType : function (node){
			var d = node.data || node.parentNode.data || {};
			return d.type;
		},
		callback : function (json,data){
			var view = (data || {}).view;
			if(view){
				if(view == "img"){
					layer.open({
						type : 2,
						btn : false,
						shade : 0,
						title: data.data,
						content : "${base}/view/" + data.data,
						area : ["80%","80%"],
						scrollbar: false,
						maxmin: true
					});
				}
				return true;
			}else if(json.message != undefined){

			}else if(data.path && data.size && addFile){
				addFile(data.path)
			}
			return false;
		}
	});

	//不能将网址转换为页签
	function dingUserSelect(){
		var msg = {
			sengMsgScene : true,
			defaultMsg : location.origin+"${base}/base/file?path="+basePath+"&name="+$path.val()+"/"+fdata.name
		};
		window.open("dingtalk://dingtalkclient/page/open_user_select?wnd_caption=发送文件&url_param="+encodeURIComponent(JSON.stringify(msg)));
	}

	common.ajaxStop(function (){
		iUI.RMenu("body",m1,function (node) {
			//return node.data || node.parentNode.data;
			return true;
		});
	});

</script>
</body>
</html>