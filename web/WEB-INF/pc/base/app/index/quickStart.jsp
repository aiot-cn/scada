<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="zh-CN">

<head>

    <meta name="description" content="">
    <meta name="keywords" content="">

    <!-- title -->
    <title>快速开始</title>

    <%@include file="common/head.jsp" %>
    <style>
        .command {
            font-family: ui-monospace, SFMono-Regular, Menlo, Monaco, Consolas, Liberation Mono, Courier New, monospace;
        }
    </style>
</head>

<body>

<%@include file="common/header.jsp" %>

<main class="main">

    <!-- breadcrumb -->
    <div class="site-breadcrumb" style="padding-top: 180px;padding-bottom: 100px">
        <div class="container">
            <h2 class="breadcrumb-title">快速开始</h2>
            <ul class="breadcrumb-menu">
                <li><a href="/"><i class="far fa-home"></i> Home</a></li>
                <li class="active">快速开始</li>
            </ul>
        </div>
    </div>
    <!-- breadcrumb end -->


    <!-- service-single-area -->
    <div class="service-single-area" style="padding-top: 60px">
        <div class="container">
            <div class="service-single-wrapper">
                <div class="row">
                    <div class="col-xl-4 col-lg-4">
                        <div class="service-sidebar">
                            <div class="widget category">
                                <h4 class="widget-title">快速开始</h4>
                                <div class="category-list">
                                    <a href="#"><i class="far fa-angle-double-right"></i>环境准备</a>
                                    <a href="#"><i class="far fa-angle-double-right"></i>下载安装</a>
                                    <a href="#"><i class="far fa-angle-double-right"></i>Windows</a>
                                    <a href="#"><i class="far fa-angle-double-right"></i>Linux</a>
                                    <a href="#"><i class="far fa-angle-double-right"></i>Docker</a>
                                </div>
                            </div>
                            <div class="widget service-download">
                                <h4 class="widget-title">下载</h4>
                                <a href="${base}/file/download/release/aiot.zip"><i class="far fa-file-alt"></i> aiot.zip</a>
                                <a href="${base}/file/download/release/apache-tomcat-9.0.115.zip"><i class="far fa-file-alt"></i> apache-tomcat-9.0.115.zip</a>
                                <a href="${base}/file/download/release/jdk-8u481-windows-x64.zip"><i class="far fa-file-alt"></i> jdk-8u481-windows-x64.zip</a>
                                <a href="${base}/file/download/release/jdk-8u481-windows-i586.zip"><i class="far fa-file-alt"></i> jdk-8u481-windows-i586.zip</a>
                                <a href="${base}/file/download/release/jdk-8u481-linux-x64.tar.gz"><i class="far fa-file-alt"></i> jdk-8u481-linux-x64.tar.gz</a>
                                <a href="${base}/file/download/release/jdk-8u481-linux-i586.tar.gz"><i class="far fa-file-alt"></i> jdk-8u481-linux-i586.tar.gz</a>
                                <a href="${base}/file/download/release/jdk-8u481-linux-aarch64.tar.gz"><i class="far fa-file-alt"></i> jdk-8u481-linux-aarch64.tar.gz</a>
                            </div>
                        </div>
                    </div>
                    <div class="col-xl-8 col-lg-8">
                        <div class="service-details">
                            <div class="service-details">
                                <%--<h3 class="mb-30">环境准备</h3>
                                <ul class="service-single-list">
                                    <li><i class="far fa-check"></i>JDK >= 1.8</li>
                                    <li><i class="far fa-check"></i>Tomcat >= 8.0</li>
                                </ul>--%>

                                <div class="my-4">
                                    <div class="mb-3">
                                        <h3 class="mb-3">下载安装</h3>
                                        <h6>Windows PowerShell:</h6>
                                        <code class="command">
                                            irm http://ai-ot.cn/install.ps1 | iex
                                        </code>
<pre>粘贴命定到 PowerShell，或者下载 <a href="http://ai-ot.cn/file/download/release/windows/x86_64/aiotPro.zip" target="_blank">aiot.zip</a> （绿色免安装）
解压后执行目录中的 <code style="color: #0baf91">startup</code>
浏览器访问 <a href="http://localhost:8080/aiot" target="_blank" style="color: #0baf91">http://localhost:8080/aiot</a>
</pre>

                                        <h6>Linux,macOS,WSL:</h6>
                                        <code class="command">
                                            curl -fsSL http://ai-ot.cn/install.sh | bash
                                        </code>
                                    </div>

                                </div>
                                <div class="my-4">
                                    <h3 class="mb-3">Docker</h3>
<pre>
<span>sudo</span> docker login -u cn-east-3@HPUAR0O4PR3AS6XPUK05 -p e38e838cf01e2d92a11c644d7c83e9d32eb49831b9d0896ce0ae96fb0fab111f swr.cn-east-3.myhuaweicloud.com
sudo docker run -d --name aiot -p 8080:80 -v ~/aiot:/root/aiot swr.cn-east-3.myhuaweicloud.com/ai-ot/core:latest

</pre>
                                </div>

<div class="my-4">
    <h3 class="mb-3">附录 - windows</h3>
<pre>
Java环境变量
    JAVA_HOME=C:\jdk1.8.0_481
    Path=%JAVA_HOME%\bin

端口占用
    netstat -ano | findstr :8080
</pre>
</div>

<div class="my-4">
<h3 class="mb-3">Linux</h3>
<pre>
Java环境变量
    sudo vim /etc/profile 配置环境变量，文件最后增加
    export JAVA_HOME=/opt/jdk1.8.0_481
    export PATH=$JAVA_HOME/bin:$PATH

端口占用
    sudo lsof -i :8080
    sudo netstat -tulnp | grep 8080
</pre>
</div>

                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
    <!-- service-single-area end -->

</main>

<%@include file="common/footer.jsp" %>

</body>

</html>