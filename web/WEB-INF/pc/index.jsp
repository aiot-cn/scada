<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <title>AIot - 工作</title>
    <%@include file="common/page_head.jsp" %>
    <style>
        html, body {
            height: 100%;
            background-color: #f2f4f7;
        }
        .header{
            height:55px;
            border-bottom: 1px solid #e0e2e6;
           text-align: center;
        }
        .header-left{
            line-height: 55px;
            position: absolute;
            left: 15px;
        }
        .header-center {
            padding-top: 14px;
        }

        .nav-item{
            display: inline-block;
            color: #495464;
            padding: 5px 12px;
            border-radius: 12px;
        }
        .nav-item:hover{
            cursor: pointer;
            background-color: #1018280a;
        }
        .nav-item.active{
            color: #155aef;
            background-color: #fff;
            font-weight: bold;
            box-shadow: 0 0 #0000,0 0 #0000,0px 2px 4px -2px #1018280f, 0px 4px 8px -2px #1018281a;
        }

        .header-right{
            position: absolute;
            right: 15px;
            top: 0;
            line-height: 55px;
        }
        .header-right a{
            padding: 6px 12px;
            border-radius: 10px;
        }
        .header-right a:hover{
            background-color: #1018280a;
        }
        .time-now{
            color: #666;
            font-family: ui-monospace;
        }
        .main{
            height: calc(100% - 60px);
        }
        .scroll-wrapper { -webkit-overflow-scrolling: touch;overflow-y: scroll;width: 100%; height: 100%;}
    </style>
</head>
<body>
<div class="header">
    <div class="header-left">
        <img alt="" src="${res}/images/logo.png" style="height: 30px">
        <span style="font-size: 18px;vertical-align: middle;color: #6e7989;margin-left: 5px;">爱物联SCADA系统V3.0</span>
    </div>
    <div class="header-center">
        <a class="nav-item active" target="frame" href="${base}/index/device">
            <i class="layui-icon layui-icon-component"></i> &nbsp;设备 <span class="nav-sub"></span>
        </a>
        <a class="nav-item" target="frame" href="${base}/index/work">
            <i class="layui-icon layui-icon-console"></i> &nbsp;工作 <span class="nav-sub"></span>
        </a>
        <a class="nav-item" target="frame" href="${base}/index/template">
            <i class="layui-icon layui-icon-read"></i> &nbsp;视图 <span class="nav-sub"></span>
        </a>
        <a class="nav-item" target="frame" href="${base}/index/record">
            <i class="layui-icon layui-icon-chart-screen"></i> &nbsp;记录 <span class="nav-sub"></span>
        </a>
    </div>
    <div class="header-right">
        <span class="time-now"></span>
        <a href="${base}/config" target="_blank"><i class="layui-icon layui-icon-set-sm"></i> 设置</a>
    </div>
</div>
<div class="main">
    <iframe class="scroll-wrapper" src="${base}/index/device" frameborder="0" name="frame"></iframe>
</div>
</body>
<script>
    //时间误差
    var timeOffset = 0;
    var timeText = $(".time-now").text(new Date().format("yyyy-MM-dd hh:mm:ss"));
    $(function () {
        $(".nav-item").click(function () {
            $(".nav-sub").empty();
            $(this).addClass("active").siblings().removeClass("active");
        });
    });

    common.ajax("${base}/config/getConfig",{key:"time_now"},function(json){
        timeOffset = json.time - new Date().getTime();
        timeText.attr("title","误差"+timeOffset+"ms")
        setInterval(function () {
            var time = new Date().getTime() + timeOffset;
            timeText.text(new Date(time).format("yyyy-MM-dd hh:mm:ss"));
        }, 1000);
    });

    function setNavSub(html){
        $(".active .nav-sub").html("/ "+html);
    }
</script>
</html>


