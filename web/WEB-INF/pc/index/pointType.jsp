<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>

<!doctype html>
<html>
	<head>
	<%@include file="../common/page_head.jsp" %>
	<title>点位类型</title>
		
<style type="text/css">
    html,body{
        background-color: #F2F2F2;
    }
	html,body,.layui-fluid,.layui-row,.layui-card{
		height: 100%;
	}
	.ch-1,.ch-2{
		height: 100%;
	}
	
	.layui-card-body{
		height: calc(100% - 75px);
		overflow: auto;
	}
	
	.layui-fluid{
		box-sizing: border-box;
    	padding-bottom: 0;
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
<div class="layui-fluid">
	<div class="layui-row layui-col-space15">

	  <div class="layui-col-md12 ch-2">
		<div class="layui-card">
	          <div class="layui-card-header">
				  <span class="title">点位类型</span>
				  <input data-search="tPointType" placeholder="搜索">
			  </div>
	          <div class="layui-card-body">
	            <div class="layui-row layui-col-space10">
				    <table id="tPointType">
						<thead>
							<tr>
								<th data-field="id" width="20">ID</th>
								<th data-field="name" data-edit="true">名称</th>
								<th data-field="unit" data-edit="true">单位</th>
								<th data-render="renderRec" data-class="tac" width="140">保存时机</th>
								<th data-field="recOnValue" width="60">差异保存</th>
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

	<form data-for="tPointType" class="form-horizontal">
		<input type="hidden" name="id">
	    <input class="layui-input" name="name" required="required">
		<input class="layui-input" name="unit">
		<input class="layui-input" name="recOnValue" type="number" step="0.001">
		<textarea class="layui-input" name="alarmRule" rows="2" style="line-height: 12px;"></textarea>
	</form>

</div>
</body>
<script type="text/javascript">

	var tPointType = new iTables("#tPointType",{pageSize:0},{
		baseOption : common.iTableModel("tPointType"),
		render : {

		},
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
		}
	});

</script>
</html>