<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>

<!doctype html>
<html>
	<head>
	<%@include file="../common/page_head.jsp" %>
	<title>图标</title>
	<link href="${res}/font/aiotfont/iconfont.css" rel="stylesheet" >
<style type="text/css">
	.dib{
		box-sizing: border-box;
		float: left;
		width: 80px;
		height: 80px;
		color: #333;
		text-align: center;
		overflow: hidden;
		margin-bottom: 10px;
		padding: 5px;
	}
	.dib.active,
	.dib:hover{
		border: 1px solid #91cbf3;
		background-color: #f7fbff;
		border-radius: 5px;
		cursor: pointer;
		color: #0e76d3;
	}
	.aiot-icon{
		font-size: 32px;
	}
	.name{
		margin-top: 5px;
	}
	.code-name{
		line-height: 20px;
	}
	.name,.code-name{
		font-size: 12px;
		color: #666;
		word-break: break-all;
	}
</style>
</head>
<body>

<div class="layui-fluid">

	<ul class="icon_lists dib-box">
		<li class="dib">
			<span class="icon aiot-icon aiot-icon-img-recognition"></span>
			<div class="name">
				图像识别
			</div>
			<div class="code-name">img-recognition
			</div>
		</li>

		<li class="dib">
			<span class="icon aiot-icon aiot-icon-help"></span>
			<div class="name">
				帮助
			</div>
			<div class="code-name">help
			</div>
		</li>

		<li class="dib">
			<span class="icon aiot-icon aiot-icon-model-train"></span>
			<div class="name">
				模型训练
			</div>
			<div class="code-name">model-train
			</div>
		</li>

		<li class="dib">
			<span class="icon aiot-icon aiot-icon-download"></span>
			<div class="name">
				下载
			</div>
			<div class="code-name">download
			</div>
		</li>

		<li class="dib">
			<span class="icon aiot-icon aiot-icon-openai"></span>
			<div class="name">
				AI模型
			</div>
			<div class="code-name">openai
			</div>
		</li>

		<li class="dib">
			<span class="icon aiot-icon aiot-icon-property"></span>
			<div class="name">
				属性
			</div>
			<div class="code-name">property
			</div>
		</li>

		<li class="dib">
			<span class="icon aiot-icon aiot-icon-scissors-cut-line"></span>
			<div class="name">
				剪切
			</div>
			<div class="code-name">scissors-cut-line
			</div>
		</li>

		<li class="dib">
			<span class="icon aiot-icon aiot-icon-scaleplate"></span>
			<div class="name">
				标尺
			</div>
			<div class="code-name">scaleplate
			</div>
		</li>

		<li class="dib">
			<span class="icon aiot-icon aiot-icon-crop-tool"></span>
			<div class="name">
				图片裁剪
			</div>
			<div class="code-name">crop-tool
			</div>
		</li>

		<li class="dib">
			<span class="icon aiot-icon aiot-icon-load"></span>
			<div class="name">
				加载
			</div>
			<div class="code-name">load
			</div>
		</li>

		<li class="dib">
			<span class="icon aiot-icon aiot-icon-imp-exp"></span>
			<div class="name">
				导入导出
			</div>
			<div class="code-name">imp-exp
			</div>
		</li>

		<li class="dib">
			<span class="icon aiot-icon aiot-icon-label"></span>
			<div class="name">
				标签
			</div>
			<div class="code-name">label
			</div>
		</li>

		<li class="dib">
			<span class="icon aiot-icon aiot-icon-dir-open"></span>
			<div class="name">
				打开文件
			</div>
			<div class="code-name">dir-open
			</div>
		</li>

		<li class="dib">
			<span class="icon aiot-icon aiot-icon-auxiliary-line"></span>
			<div class="name">
				辅助线
			</div>
			<div class="code-name">auxiliary-line
			</div>
		</li>

		<li class="dib">
			<span class="icon aiot-icon aiot-icon-dir"></span>
			<div class="name">
				文件夹
			</div>
			<div class="code-name">dir
			</div>
		</li>

		<li class="dib">
			<span class="icon aiot-icon aiot-icon-node-io"></span>
			<div class="name">
				节点
			</div>
			<div class="code-name">node-io
			</div>
		</li>

		<li class="dib">
			<span class="icon aiot-icon aiot-icon-rename"></span>
			<div class="name">
				重命名
			</div>
			<div class="code-name">rename
			</div>
		</li>

		<li class="dib">
			<span class="icon aiot-icon aiot-icon-polygon"></span>
			<div class="name">
				多边形
			</div>
			<div class="code-name">polygon
			</div>
		</li>

		<li class="dib">
			<span class="icon aiot-icon aiot-icon-rotate-3d"></span>
			<div class="name">
				3D旋转
			</div>
			<div class="code-name">rotate-3d
			</div>
		</li>

		<li class="dib">
			<span class="icon aiot-icon aiot-icon-rectangle"></span>
			<div class="name">
				矩形框
			</div>
			<div class="code-name">rectangle
			</div>
		</li>

		<li class="dib">
			<span class="icon aiot-icon aiot-icon-stream-media"></span>
			<div class="name">
				流媒体
			</div>
			<div class="code-name">stream-media
			</div>
		</li>

		<li class="dib">
			<span class="icon aiot-icon aiot-icon-rotate"></span>
			<div class="name">
				旋转
			</div>
			<div class="code-name">rotate
			</div>
		</li>

		<li class="dib">
			<span class="icon aiot-icon aiot-icon-data-source"></span>
			<div class="name">
				数据源
			</div>
			<div class="code-name">data-source
			</div>
		</li>

		<li class="dib">
			<span class="icon aiot-icon aiot-icon-d3"></span>
			<div class="name">
				3D
			</div>
			<div class="code-name">d3
			</div>
		</li>

		<li class="dib">
			<span class="icon aiot-icon aiot-icon-device"></span>
			<div class="name">
				设备
			</div>
			<div class="code-name">device</div>
		</li>

		<li class="dib">
			<span class="icon aiot-icon aiot-icon-material-fill"></span>
			<div class="name">
				物料-填充
			</div>
			<div class="code-name">material-fill</div>
		</li>

		<li class="dib">
			<span class="icon aiot-icon aiot-icon-clock"></span>
			<div class="name">
				时钟
			</div>
			<div class="code-name">clock</div>
		</li>

		<li class="dib">
			<span class="icon aiot-icon aiot-icon-warehouse"></span>
			<div class="name">
				仓库
			</div>
			<div class="code-name">warehouse</div>
		</li>

		<li class="dib">
			<span class="icon aiot-icon aiot-icon-api"></span>
			<div class="name">
				API
			</div>
			<div class="code-name">api</div>
		</li>

		<li class="dib">
			<span class="icon aiot-icon aiot-icon-chip"></span>
			<div class="name">
				芯片
			</div>
			<div class="code-name">chip</div>
		</li>

		<li class="dib">
			<span class="icon aiot-icon aiot-icon-chem-comp"></span>
			<div class="name">
				成分
			</div>
			<div class="code-name">chem-comp</div>
		</li>

		<li class="dib">
			<span class="icon aiot-icon aiot-icon-url"></span>
			<div class="name">
				url
			</div>
			<div class="code-name">url</div>
		</li>

		<li class="dib">
			<span class="icon aiot-icon aiot-icon-console"></span>
			<div class="name">
				控制台
			</div>
			<div class="code-name">console
			</div>
		</li>

		<li class="dib">
			<span class="icon aiot-icon aiot-icon-snowflake"></span>
			<div class="name">
				雪花
			</div>
			<div class="code-name">snowflake
			</div>
		</li>

		<li class="dib">
			<span class="icon aiot-icon aiot-icon-dehumidification"></span>
			<div class="name">
				除湿
			</div>
			<div class="code-name">dehumidification
			</div>
		</li>

		<li class="dib">
			<span class="icon aiot-icon aiot-icon-dehumidifier"></span>
			<div class="name">
				除湿器
			</div>
			<div class="code-name">dehumidifier
			</div>
		</li>

		<li class="dib">
			<span class="icon aiot-icon aiot-icon-material"></span>
			<div class="name">
				物料
			</div>
			<div class="code-name">material
			</div>
		</li>

		<li class="dib">
			<span class="icon aiot-icon aiot-icon-computer"></span>
			<div class="name">
				电脑
			</div>
			<div class="code-name">computer
			</div>
		</li>

		<li class="dib">
			<span class="icon aiot-icon aiot-icon-sensor"></span>
			<div class="name">
				传感器
			</div>
			<div class="code-name">sensor
			</div>
		</li>

		<li class="dib">
			<span class="icon aiot-icon aiot-icon-workbench"></span>
			<div class="name">
				工作台
			</div>
			<div class="code-name">workbench
			</div>
		</li>

		<li class="dib">
			<span class="icon aiot-icon aiot-icon-picture"></span>
			<div class="name">
				图片
			</div>
			<div class="code-name">picture
			</div>
		</li>

		<li class="dib">
			<span class="icon aiot-icon aiot-icon-controller"></span>
			<div class="name">
				控制器
			</div>
			<div class="code-name">controller
			</div>
		</li>

		<li class="dib">
			<span class="icon aiot-icon aiot-icon-o3"></span>
			<div class="name">
				臭氧
			</div>
			<div class="code-name">o3
			</div>
		</li>

		<li class="dib">
			<span class="icon aiot-icon aiot-icon-alarm-lamp"></span>
			<div class="name">
				警灯
			</div>
			<div class="code-name">alarm-lamp
			</div>
		</li>

		<li class="dib">
			<span class="icon aiot-icon aiot-icon-draught-fan"></span>
			<div class="name">
				风机
			</div>
			<div class="code-name">draught-fan
			</div>
		</li>

		<li class="dib">
			<span class="icon aiot-icon aiot-icon-fence"></span>
			<div class="name">
				围栏
			</div>
			<div class="code-name">fence
			</div>
		</li>

		<li class="dib">
			<span class="icon aiot-icon aiot-icon-air-conditioner"></span>
			<div class="name">
				空调
			</div>
			<div class="code-name">air-conditioner
			</div>
		</li>

		<li class="dib">
			<span class="icon aiot-icon aiot-icon-sf6"></span>
			<div class="name">
				六氟化硫
			</div>
			<div class="code-name">sf6
			</div>
		</li>

		<li class="dib">
			<span class="icon aiot-icon aiot-icon-water-level"></span>
			<div class="name">
				水位
			</div>
			<div class="code-name">water-level
			</div>
		</li>

		<li class="dib">
			<span class="icon aiot-icon aiot-icon-camera-gun-right"></span>
			<div class="name">
				枪机-右
			</div>
			<div class="code-name">camera-gun-right
			</div>
		</li>

		<li class="dib">
			<span class="icon aiot-icon aiot-icon-camera-gun-left"></span>
			<div class="name">
				枪机-左
			</div>
			<div class="code-name">camera-gun-left
			</div>
		</li>

		<li class="dib">
			<span class="icon aiot-icon aiot-icon-camera-ballhead"></span>
			<div class="name">
				球机
			</div>
			<div class="code-name">camera-ballhead
			</div>
		</li>

		<li class="dib">
			<span class="icon aiot-icon aiot-icon-flame"></span>
			<div class="name">
				火焰
			</div>
			<div class="code-name">flame
			</div>
		</li>

		<li class="dib">
			<span class="icon aiot-icon aiot-icon-volume"></span>
			<div class="name">
				音量
			</div>
			<div class="code-name">volume
			</div>
		</li>

		<li class="dib">
			<span class="icon aiot-icon aiot-icon-gas"></span>
			<div class="name">
				气体
			</div>
			<div class="code-name">gas
			</div>
		</li>

		<li class="dib">
			<span class="icon aiot-icon aiot-icon-o2"></span>
			<div class="name">
				氧气
			</div>
			<div class="code-name">o2
			</div>
		</li>

		<li class="dib">
			<span class="icon aiot-icon aiot-icon-navigation"></span>
			<div class="name">
				指南针
			</div>
			<div class="code-name">navigation
			</div>
		</li>

		<li class="dib">
			<span class="icon aiot-icon aiot-icon-entrance-guard"></span>
			<div class="name">
				门禁
			</div>
			<div class="code-name">entrance-guard
			</div>
		</li>

		<li class="dib">
			<span class="icon aiot-icon aiot-icon-path"></span>
			<div class="name">
				路径
			</div>
			<div class="code-name">path
			</div>
		</li>

		<li class="dib">
			<span class="icon aiot-icon aiot-icon-humiture"></span>
			<div class="name">
				温湿度
			</div>
			<div class="code-name">humiture
			</div>
		</li>

		<li class="dib">
			<span class="icon aiot-icon aiot-icon-theft"></span>
			<div class="name">
				防盗
			</div>
			<div class="code-name">theft
			</div>
		</li>

		<li class="dib">
			<span class="icon aiot-icon aiot-icon-smoke"></span>
			<div class="name">
				烟雾
			</div>
			<div class="code-name">smoke
			</div>
		</li>

		<li class="dib">
			<span class="icon aiot-icon aiot-icon-db"></span>
			<div class="name">
				噪声
			</div>
			<div class="code-name">db
			</div>
		</li>

		<li class="dib">
			<span class="icon aiot-icon aiot-icon-transformer"></span>
			<div class="name">
				变压器
			</div>
			<div class="code-name">transformer
			</div>
		</li>

		<li class="dib">
			<span class="icon aiot-icon aiot-icon-water-level2"></span>
			<div class="name">
				水位2
			</div>
			<div class="code-name">water-level2
			</div>
		</li>

		<li class="dib">
			<span class="icon aiot-icon aiot-icon-aim"></span>
			<div class="name">
				瞄准
			</div>
			<div class="code-name">aim
			</div>
		</li>

		<li class="dib">
			<span class="icon aiot-icon aiot-icon-logo-stategrid"></span>
			<div class="name">
				国家电网
			</div>
			<div class="code-name">logo-stategrid
			</div>
		</li>

		<li class="dib">
			<span class="icon aiot-icon aiot-icon-temperature"></span>
			<div class="name">
				温度
			</div>
			<div class="code-name">temperature
			</div>
		</li>

		<li class="dib">
			<span class="icon aiot-icon aiot-icon-tvoc"></span>
			<div class="name">
				TVOC
			</div>
			<div class="code-name">tvoc
			</div>
		</li>

		<li class="dib">
			<span class="icon aiot-icon aiot-icon-humidity"></span>
			<div class="name">
				湿度
			</div>
			<div class="code-name">humidity
			</div>
		</li>

		<li class="dib">
			<span class="icon aiot-icon aiot-icon-save"></span>
			<div class="name">
				保存
			</div>
			<div class="code-name">save
			</div>
		</li>

		<li class="dib">
			<span class="icon aiot-icon aiot-icon-run"></span>
			<div class="name">
				执行
			</div>
			<div class="code-name">run
			</div>
		</li>

		<li class="dib">
			<span class="icon aiot-icon aiot-icon-rainfall"></span>
			<div class="name">
				降雨量
			</div>
			<div class="code-name">rainfall
			</div>
		</li>

	</ul>


</div>

</body>
<script type="text/javascript">
$(".icon_lists").on("click", "li", function () {
	$(this).addClass("active").siblings().removeClass("active");
});

function getValue(){
	var c =  $("li.active").find(".icon").attr("class");
	if(!c)
		return "";
	return c.replace("icon ","");
}
</script>
</html>