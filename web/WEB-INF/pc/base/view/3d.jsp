<%@ page contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ page import="org.aiot.model.lang.SRes" %>

<!doctype html>
<html>
<head>
	<%@include file="../../common/page_head.jsp" %>
	<meta name="viewport" content="width=device-width, initial-scale=1.0">
	<title>${SRes.name}</title>
	<style>
		html,body{
			height: 100%;
		}

		#canvas-container {
			height: 100%;
		}

		@keyframes spin {
			to { transform: rotate(360deg); }
		}

	</style>
</head>
<body>
<div id="canvas-container"></div>
</body>
<script src="${res}/plugin/three/r123/three.min.js"></script>
<script src="${res}/plugin/three/r123/GLTFLoader.js"></script>
<script src="${res}/plugin/three/r123/OrbitControls.js"></script>

<script>

	var scene, camera, renderer, controls, model;
	var container;
	var rotationSpeed = 0.005;
	var autoRotate = true;
	var clock = new THREE.Clock();

	// 初始化函数
	function init() {
		container = document.getElementById('canvas-container');

		// 创建场景
		scene = new THREE.Scene();
		scene.background = new THREE.Color(0x0d1117);

		// 添加环境光
		var ambientLight = new THREE.AmbientLight(0xffffff, 0.6);
		scene.add(ambientLight);

		// 添加定向光
		var directionalLight = new THREE.DirectionalLight(0xffffff, 0.8);
		directionalLight.position.set(10, 20, 5);
		scene.add(directionalLight);

		// 创建相机
		camera = new THREE.PerspectiveCamera(
				45, // 视野角度
				container.clientWidth / container.clientHeight, // 宽高比
				0.1, // 近截面
				1000 // 远截面
		);
		camera.position.set(5, 5, 5);

		// 创建渲染器
		renderer = new THREE.WebGLRenderer({ antialias: true });
		renderer.setSize(container.clientWidth, container.clientHeight);
		renderer.setPixelRatio(window.devicePixelRatio);
		renderer.shadowMap.enabled = true;
		container.appendChild(renderer.domElement);

		// 添加轨道控制器
		controls = new THREE.OrbitControls(camera, renderer.domElement);
		controls.enableDamping = true;
		controls.dampingFactor = 0.05;
		controls.minDistance = 2;
		controls.maxDistance = 20;

		loadGLBModel();

		// 添加窗口大小调整监听
		window.addEventListener('resize', onWindowResize);

		// 开始动画循环
		animate();
	}

	// 加载GLB模型
	function loadGLBModel() {
		var loader = new THREE.GLTFLoader();
		loader.load(
				'${base}/file/download/${SRes.pathName}',
				// 加载完成回调
				function(gltf) {
					model = gltf.scene;
					// 计算模型的边界框
					var box = new THREE.Box3().setFromObject(model);
					var boxMax = box.max;
					var size = box.getSize(new THREE.Vector3()).length();
					var center = box.getCenter(new THREE.Vector3());
					//var scale = 1/Math.max(boxMax.x,boxMax.y,boxMax.z);
					// 计算合适的缩放比例
					//const maxSize = Math.max(container.clientWidth, container.clientHeight);
					//const scale = maxSize / size  * 0.8; // 0.8为边距系数

					//model.position.set(0, 0, 0);
					//model.scale.set(scale, scale, scale);

					// 启用模型阴影
					model.traverse(function(child) {
						if (child.isMesh) {
							child.castShadow = true;
							child.receiveShadow = true;
						}
					});

					// 将模型添加到场景
					scene.add(model);

					console.log('GLB模型加载成功');
				},

				// 加载进度回调
				function(xhr) {
					var percentComplete = (xhr.loaded / xhr.total) * 100;
					console.log('模型加载进度: ' + percentComplete.toFixed(2) + '%');

					// 可以在这里更新加载进度条
					if (percentComplete >= 100) {
						// 加载完成，但需要等待渲染完成
					}
				},

				// 加载错误回调
				function(error) {
					console.error('加载模型时出错:', error);

					// 创建备用立方体模型
					createFallbackModel();
				}
		);
	}

	// 创建备用模型（当GLB加载失败时）
	function createFallbackModel() {
		// 创建一个几何体和材质
		var geometry = new THREE.BoxGeometry(2, 2, 2);
		var material = new THREE.MeshStandardMaterial({
			color: 0x4361ee,
			metalness: 0.7,
			roughness: 0.2
		});

		// 创建网格
		model = new THREE.Mesh(geometry, material);
		model.castShadow = true;
		model.receiveShadow = true;

		// 添加到场景
		scene.add(model);

		// 添加线框
		var wireframe = new THREE.WireframeGeometry(geometry);
		var line = new THREE.LineSegments(wireframe);
		line.material.color.setHex(0xffffff);
		line.material.opacity = 0.25;
		line.material.transparent = true;
		model.add(line);

	}

	//缩放 model.scale.set(scale, scale, scale);

	//重置
	function controlReset(){
		controls.reset();
		camera.position.set(5, 5, 5);
		controls.update();
	}

	// 窗口大小调整处理
	function onWindowResize() {
		camera.aspect = container.clientWidth / container.clientHeight;
		camera.updateProjectionMatrix();
		renderer.setSize(container.clientWidth, container.clientHeight);
	}

	// 动画循环
	function animate() {
		requestAnimationFrame(animate);

		var delta = clock.getDelta();

		// 自动旋转模型
		if (model && autoRotate) {
			model.rotation.y += rotationSpeed;
		}

		// 更新控制器
		controls.update();

		// 渲染场景
		renderer.render(scene, camera);
	}

	// 页面加载完成后初始化
	window.addEventListener('DOMContentLoaded', init);

</script>
</html>