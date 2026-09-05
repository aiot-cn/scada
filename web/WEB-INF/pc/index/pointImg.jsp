<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>

<!doctype html>
<html>
	<head>
		<%@include file="../common/page_head.jsp" %>
		<title>图像点位</title>

		<style type="text/css">
			html,body{

			}
			#pointTable td{
				border: 2px solid #fff;
			}
			#pointTable img{
				max-width: 640px;
				max-height: 360px;
			}
		</style>
	</head>
	<body>
		<div>
			<table id="pointTable">
				<tbody>
				<c:forEach begin="0" end="10">
					<tr>
						<c:forEach begin="0" end="50">
						<td></td>
						</c:forEach>
					</tr>
				</c:forEach>
				</tbody>
			</table>
		</div>
	</body>
	<script type="text/javascript">
		var path = "/image/point";
		var imgArr = [];//col-row-name
		var pointTable = document.getElementById("pointTable");
		var pointTbody = pointTable.tBodies[0];
		common.ajax("${base}/file/getList",{"path":path},function(arr){
			$(arr).each(function (){
				if(common.isImg(this))
					return true;
				var a = this.split("-");
				try {
					var img = {
						col:parseInt(a[0]),
						row:parseInt(a[1]),
						name:this
					}
					imgArr.push(img);
				}catch (e) {

				}
			});
			$(imgArr).each(function (){
				var tr = pointTbody.rows[this.row -1];
				var td = tr.cells[this.col -1];
				td.data = this;
				td.innerHTML = "<img src='${base}/image"+(path + "/" + this.name)+"'/>";
			});
		});

		var imgWin,imgData;
		$(pointTable).on("click","img",function (){
			var data = this.parentNode.data;
			var url = "${base}/view"+path + "/" + data.name;
			layer.open({
				type: 2,
				title: false,
				shadeClose:true,
				area: ["80%", "90%"], //宽高
				content: url,
				success : function (layero){
					imgWin = window[layero.find('iframe')[0].name];
					$(imgWin).focus();
					/*var imgs = [];
					vm.dataList.forEach(function (v){
						imgs.push({"id":v.id,"name":"/capture"+v.imgPath,"imgPath":v.imgPath});
					});
					imgWin.setFileList(imgs);*/
					loadLabel(path + "/" + data.name);

					imgWin.listener = {
						load : function (data) {
							imgData = data;
							loadLabel("/"+imgWin.pathName);
						},
						label : {
							dblClick: function (e, target) {
								imgWin.$(target).del();
							},
							changed: function (data,label) {
								var tPoint = {
									"id":label.data.id,
									"target" : data.label + ",0,"+data.left.toFixed(6) + ","+data.top.toFixed(6)
											+ ","+data.width.toFixed(6) + ","+data.height.toFixed(6) + "," + (data.angle || 0)
								};
								//新增
								if(!tPoint.id){
									tPoint.image =  "/"+imgWin.pathName;
									//默认每次保存
									tPoint.recOnEvery = true;
								}

								common.jsonModel("tPoint",tPoint,function(json){
									label.data = json.data;
								},{"action": "save"});
							},
							deleted: function (data, label){
								common.jsonModel("tPoint",label.data, function (json){
								}, {"action": "del"});
							}
						}
					}
				}
			});
		});

		function loadLabel(imgPath){
			common.jsonModel("tPoint",{"image":imgPath},function (json){
				$(json.list).each(function (){
					var label = imgWin.addLabelByStr(this.target);
					label.data = this;
				});
			});
		}
	</script>
</html>