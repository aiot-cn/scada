<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<!DOCTYPE html>
<html>
<head>
  <meta charset="utf-8">
  <title>AIot - 配置</title>
  <meta name="renderer" content="webkit">
  <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
  <meta name="viewport" content="width=device-width, initial-scale=1.0, minimum-scale=1.0, maximum-scale=1.0, user-scalable=0">
  <c:import url="common/page_head.jsp"></c:import>
  <link rel="stylesheet" href="${res}/layuiadmin/style/admin.css" media="all">
  
  <style type="text/css">
    html {
      background-color: #f5f6fa;
    }

  .layui-logo span{
  	font-size: 14px;
  	vertical-align: middle;
  }
    /*左侧收缩后的样式*/
    .layadmin-side-shrink .layui-side-menu .layui-nav-item {
      margin-bottom: 2px;
    }

    .layadmin-side-shrink .layui-side-menu .layui-nav .layui-nav-item > a {
      padding-right: 15px;
    }


  </style>

</head>
<body class="layui-layout-body">
  
  <div id="LAY_app">
    <div class="layui-layout layui-layout-admin">
      <div class="layui-header">
        <!-- 头部区域 -->
        <ul class="layui-nav layui-layout-left">
          <li class="layui-nav-item layadmin-flexible" lay-unselect>
            <a href="javascript:;" layadmin-event="flexible" title="侧边伸缩">
              <i class="layui-icon layui-icon-shrink-right" id="LAY_app_flexible"></i>
            </a>
          </li>
          <li class="layui-nav-item" lay-unselect>
            <a href="javascript:;" layadmin-event="refresh" title="刷新">
              <i class="layui-icon layui-icon-refresh-3"></i>
            </a>
          </li>
          <%--<li class="layui-nav-item layui-hide-xs" lay-unselect>
            <input type="text" placeholder="搜索..." autocomplete="off" class="layui-input layui-input-search" layadmin-event="serach" lay-action="template/search.html?keywords="> 
          </li>--%>
        </ul>
        <ul class="layui-nav layui-layout-right" lay-filter="layadmin-layout-right">
          
          <li class="layui-nav-item" lay-unselect>
            <a lay-href="${base }/common/message" layadmin-event="message" lay-text="消息中心">
              <i class="layui-icon layui-icon-notice"></i>  
              
              <!-- 如果有新消息，则显示小圆点 -->
              <span class="layui-badge-dot"></span>
            </a>
          </li>
          <%--<li class="layui-nav-item layui-hide-xs" lay-unselect>
            <a href="javascript:;" layadmin-event="theme">
              <i class="layui-icon layui-icon-theme"></i>
            </a>
          </li>--%>
          <li class="layui-nav-item layui-hide-xs" lay-unselect>
            <a href="javascript:;" layadmin-event="note">
              <i class="layui-icon layui-icon-note"></i>
            </a>
          </li>
          <li class="layui-nav-item layui-hide-xs" lay-unselect>
            <a href="javascript:;" layadmin-event="fullscreen">
              <i class="layui-icon layui-icon-screen-full"></i>
            </a>
          </li>
           <li class="layui-nav-item layui-hide-xs" lay-unselect>
            <a href="javascript:common.selectOrg();">
              ${hospital.name } ${dept.name }${ward.name }
            </a>
          </li>
          <li class="layui-nav-item" lay-unselect>
            <a href="javascript:;">
              <cite>${principal }</cite>
            </a>
            <dl class="layui-nav-child">
              <dd><a>${sysRole.name}</a></dd>
              <hr>
              <dd><a href="javascript:common.personEdit(${user.personId })">基本资料</a></dd>
              <dd><a lay-href="${base }/user/password">修改密码 </a></dd>
              <hr>
              <dd layadmin-event="logout" style="text-align: center;"><a>退出</a></dd>
            </dl>
          </li>
          
          <li class="layui-nav-item layui-hide-xs" lay-unselect>
            <a href="javascript:;" layadmin-event="about"><i class="layui-icon layui-icon-more-vertical"></i></a>
          </li>
          <li class="layui-nav-item layui-show-xs-inline-block layui-hide-sm" lay-unselect>
            <a href="javascript:;" layadmin-event="more"><i class="layui-icon layui-icon-more-vertical"></i></a>
          </li>
        </ul>
      </div>
      
      <!-- 侧边菜单 -->
      <div class="layui-side layui-side-menu">
        <div class="layui-side-scroll">
          <div class="layui-logo" lay-href="${base }/index/point">
          	<img alt="" src="${res }/images/logo.png" style="height: 30px">
            <span>爱物联SCADA系统V3.0</span>
          </div>
          
          <ul class="layui-nav layui-nav-tree" lay-shrink="all" id="LAY-system-side-menu" lay-filter="layadmin-system-side-menu">
            <li data-name="home" class="layui-nav-item layui-nav-itemed">
              <a href="javascript:;" lay-tips="主页" lay-direction="2">
                <i class="layui-icon layui-icon-home"></i>
                <cite>主页</cite>
              <span class="layui-nav-more"></span></a>
              <dl class="layui-nav-child">
                <dd class="layui-this"><a lay-href="${base }/config/aiModel">模型</a></dd>
                <dd><a lay-href="${base }/config/device">设备</a></dd>
                <dd><a lay-href="${base }/config/communication">通讯</a></dd>
                <dd><a lay-href="${base }/index/point">点位</a></dd>
                <dd><a lay-href="${base }/config/videoSource">流媒体</a></dd>

                <%--<dd><a lay-href="${base }/index/pointType">点位类型</a></dd>--%>
                <dd><a lay-href="${base }/index/deviceType">设备类型</a></dd>
                <dd><a lay-href="${base }/index/record">历史记录</a></dd>
                <dd><a lay-href="${base }/config/explorer">资源管理</a></dd>
              </dl>
            </li>

            <li data-name="config" class="layui-nav-item layui-nav-itemed">
              <a href="javascript:;" lay-tips="配置" lay-direction="2">
                <i class="layui-icon layui-icon-set-fill"></i>
                <cite>配置</cite>
                <span class="layui-nav-more"></span></a>
              <dl class="layui-nav-child">
                <dd data-name="dict"><a lay-href="${base }/config/dict">字典</a></dd>
                <dd data-name="url"><a lay-href="${base }/config/url">URL</a></dd>
                <dd data-name="script"><a lay-href="${base }/config/script">脚本</a></dd>
                <dd data-name="workflow"><a lay-href="${base }/config/workflow">工作流</a></dd>
               <%-- <dd data-name="workflow"><a lay-href="${base }/config/action">动作连</a></dd>
                <dd data-name="workflow"><a lay-href="${base }/config/crontab">定时任务</a></dd>--%>
              </dl>
            </li>

            <li data-name="sys" class="layui-nav-item layui-nav-itemed">
              <a href="javascript:;" lay-tips="系统" lay-direction="2">
                <i class="layui-icon layui-icon-windows"></i>
                <cite>系统</cite>
                <span class="layui-nav-more"></span></a>
              <dl class="layui-nav-child">
                <dd><a lay-href="${base }/config/sqlCode">SQLCode</a></dd>
                <dd><a lay-href="${base }/index/debug">调试</a></dd>
              </dl>
            </li>

            <li data-name="app" class="layui-nav-item layui-nav-itemed">
              <a href="javascript:;" lay-tips="文档" lay-direction="2">
                <i class="layui-icon layui-icon-app"></i>
                <cite>应用</cite>
                <span class="layui-nav-more"></span></a>
              <dl class="layui-nav-child">
                <dd><a lay-href="${base }/config/docs">文档</a></dd>
              </dl>
            </li>

          </ul>
        </div>
      </div>

      <!-- 页面标签 -->
      <div class="layadmin-pagetabs" id="LAY_app_tabs">
        <div class="layui-icon layadmin-tabs-control layui-icon-prev" layadmin-event="leftPage"></div>
        <div class="layui-icon layadmin-tabs-control layui-icon-next" layadmin-event="rightPage"></div>
        <div class="layui-icon layadmin-tabs-control layui-icon-down">
          <ul class="layui-nav layadmin-tabs-select" lay-filter="layadmin-pagetabs-nav">
            <li class="layui-nav-item" lay-unselect>
              <a href="javascript:;"></a>
              <dl class="layui-nav-child layui-anim-fadein">
                <dd layadmin-event="closeThisTabs"><a href="javascript:;">关闭当前标签页</a></dd>
                <dd layadmin-event="closeOtherTabs"><a href="javascript:;">关闭其它标签页</a></dd>
                <dd layadmin-event="closeAllTabs"><a href="javascript:;">关闭全部标签页</a></dd>
              </dl>
            </li>
          </ul>
        </div>
        <div class="layui-tab" lay-unauto lay-allowClose="true" lay-filter="layadmin-layout-tabs">
          <ul class="layui-tab-title" id="LAY_app_tabsheader">
            <li lay-id="${base }/config/aiModel" lay-attr="${base }/config/aiModel" class="layui-this">
              <i class="layui-icon layui-icon-home"></i> AI 模型
            </li>
          </ul>
        </div>
      </div>
      
      
      <!-- 主体内容 -->
      <div class="layui-body" id="LAY_app_body">
        <div class="layadmin-tabsbody-item layui-show">
          <iframe src="${base }/config/aiModel" frameborder="0" class="layadmin-iframe"></iframe>
        </div>
      </div>
      
      <!-- 辅助元素，一般用于移动设备下遮罩 -->
      <div class="layadmin-body-shade" layadmin-event="shade"></div>
    </div>
  </div>

  <script>
  var userId = "${user.id}";
  layui.config({
    base: '${res}/layuiadmin/' //静态资源所在路径
  }).extend({
    index: 'lib/index' //主入口模块
  }).use('index');
  
  layui.element.render();

  </script>
</body>
</html>


