<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>

<!doctype html>
<html>
	<head>
	<%@include file="../common/page_head.jsp" %>
	<title>业务字典</title>
		
<style type="text/css">
	html,body{
		height: 100%
	}

	.layui-colorpicker{
		border: none;
	}
	.has-type .layui-col-md3{
		display: none;
	}
	.has-type .layui-col-md9 {
		width: 100%;
	}
</style>
</head>
<body class="${empty param.type ? 'no-type':"has-type"}">
<div class="layui-fluid sty-auto-h">

<div class="layui-row layui-col-space15">
  <div class="layui-col-md3">    
		<div class="layui-card">
          <div class="layui-card-header">
			  <span class="title">字典类型</span>
		      	<input data-search="tDictType" placeholder="搜索">
		  </div>
          <div class="layui-card-body">
            <div class="layui-row layui-col-space10">
 
	    <table id="tDictType" class="layui-table">
		<thead>
				<tr>
					<th data-field="name">名称</th>
					<th data-field="code">类型</th>
				</tr>
			</thead>
		</table>
	
            </div>
        </div>
      </div>    
  </div>
  <div class="layui-col-md9">    
		<div class="layui-card">
          <div class="layui-card-header">
			  <span class="title">字典数据</span>
			  <div id="c1"></div>
			  <div data-itable="tool_tDictValue" class="itable-tool"></div>
		  </div>
          <div class="layui-card-body">
            <div class="layui-row layui-col-space10">
 
	    <table id="tDictValue" class="layui-table">
		<thead>
				<tr>
					<th data-field="isRemoved" data-type="switch" data-class="tac switch-contrary" width="30">状态</th>
					<th data-field="id" data-show="false">ID</th>
					<th data-field="name" data-type="level">名称</th>
					<th data-field="code">CODE</th>
					<th data-field="value">值</th>
					<th data-field="v2">值2</th>
					<th data-field="v3" data-show="false">值3</th>
					<th data-field="helpCode" data-show="false">助记符</th>
					<th data-field="color" width="40">颜色</th>
					<th data-field="backColor" width="50" data-class="tac">背景色</th>
					<th data-field="icon"  data-show="false" width="30">图标</th>
					<th data-field="remark" data-show="false">备注</th>
					<th data-field="isDefault" data-type="switch" data-class="tac" width="30" data-show="false">默认</th>
					<th data-type="edit" width="60" data-class="tac">操作</th>
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
	<form data-for="tDictType">
	    <input type="hidden" name="dicttypeid">
	    <input type="text" class="layui-input" name="name">
	    <input type="text" class="layui-input" name="code">
	</form>
	<form data-for="tDictValue">
	    <input type="hidden" name="id">
		<input type="text" class="layui-input" name="code">
	    <input type="text" class="layui-input" name="name">
	    <input type="text" class="layui-input" name="value">
	    <input type="text" class="layui-input" name="v2">
	    <input type="text" class="layui-input" name="v3">
	    <input type="text" class="layui-input" name="icon">
	    <input type="text" class="layui-input" name="color" >
		<input type="text" class="layui-input" name="backColor">
	    <input type="text" class="layui-input" name="status">
	    <input type="text" class="layui-input" name="helpCode">
	    <input type="text" class="layui-input" name="remark">
	    <select name="isDefault" class="layui-input">
	    	<option value="0">否</option>
	    	<option value="1">是</option>
	    </select>
		<select class="layui-input" name="actionType">
			<option value="">--</option>
			<option value="1">动作</option>
			<option value="2">脚本</option>
			<option value="3">流程</option>
		</select>
		<input class="layui-input" name="actionCode">
	</form>
</div>
</body>
<script type="text/javascript">
	var type = param.type;
	layui.colorpicker.render({
		elem: '#c1'
		,color: '#0000ff'
		,predefine: true // 开启预定义颜色
		,done: function(color){
			tDictValue._form.color.value = color;
		}
	});


var tDictType = new iTables("#tDictType",{},{
	getController  : "${base}/json/getDictType",
	onSelect:function(tr,data){
		type = data.code;
		tDictValue.load();
	},
	beforeStats:function(){
		if(type){
			tDictValue.load();
		}else{
			this.selectIndex(0);
		}

	}
});

var tDictValue = new iTables("#tDictValue",{},{
	baseOption : common.iTableModel("sysDict","sequence"),
	order : "sequence",
	parentName : "parentId",
	loadOnInit : false,
	render : {
		color : function(td,data){
			if(data.color){
				$(td).css({"color": data.color});
				$(td).append(data.color);
			}
		},
		backColor : function(td,data){
			if(data.backColor){
				$(td).css({"color": data.color,"background":data.backColor}).text(data.backColor);
			}
		},
		icon : function (td,data){
			$("<i class='"+data.icon+"'></i>").appendTo(td);
		}
	},

	callLoadParams : function(params){
		params.type = type;
	},
	callForm:function(params){
		params.type = type;
	}
});

</script>
</html>