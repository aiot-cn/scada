<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="org.nutz.lang.Strings" %>
<%@ page import="java.io.File" %>
<%@ page import="org.aiot.util.FileUtil" %>
<%@ page import="org.nutz.lang.Files" %>
<%@ page import="java.util.List" %>
<%@ page import="org.nutz.lang.util.NutMap" %>
<%@ page import="java.util.ArrayList" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
    String name = Strings.sBlank(request.getParameter("name"),"index.md");
    File file = FileUtil.toFile("document/instruction",name);
    request.setAttribute("content",file.isFile()? Files.read(file) : "");
    List<File> fileList = FileUtil.getFilesBySuffix(FileUtil.toFile("document/instruction"),".md");
    List<NutMap> docList = new ArrayList<>();
    for(File f : fileList){
        NutMap nm = new NutMap();
        nm.put("name",f.getName().replaceAll(".md",""));
        nm.put("path",FileUtil.toPath(f).substring(22));
        docList.add(nm);
    }
    request.setAttribute("docList",docList);
%>
<!DOCTYPE html>
<html lang="zh-CN">

<head>

    <meta name="description" content="">
    <meta name="keywords" content="">

    <!-- title -->
    <title>使用说明</title>

    <%@include file="common/head.jsp" %>
    <script src="${res}/plugin/cherry-markdown/cherry-markdown.js"></script>
    <link rel="stylesheet" type="text/css" href="${res}/plugin/cherry-markdown/cherry-markdown.css" />

    <style>
        .cherry{
            box-shadow: none;
        }
        .cherry-previewer{
            border-left: none;
        }
        .cherry-flex-toc{
            position: fixed;
            width: 300px;
        }
        .cherry-flex-toc:hover{
            width: 300px;
        }
    </style>
</head>

<body>

<%@include file="common/header.jsp" %>

<main class="main">

    <!-- breadcrumb -->
    <div class="site-breadcrumb" style="padding-top: 180px;padding-bottom: 100px">
        <div class="container">
            <h2 class="breadcrumb-title">使用说明</h2>
            <ul class="breadcrumb-menu">
                <li><a href="${base}"><i class="far fa-home"></i> 首页</a></li>
                <li class="active">使用说明</li>
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
                                <h4 class="widget-title">使用说明</h4>
                                <div class="category-list">
                                    <c:forEach items="${docList}" var="doc">
                                        <a href="instruction?name=${doc.path}"><i class="far fa-angle-double-right"></i>${doc.name}</a>
                                    </c:forEach>
                                </div>
                            </div>

                        </div>
                    </div>
                    <div class="col-xl-8 col-lg-8">
                        <div class="service-details">
                            <div id="markdown"></div>

<textarea id="code">${content}</textarea>

                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
    <!-- service-single-area end -->

</main>

<%@include file="common/footer.jsp" %>

<script>
    var codeNode = $("#code").hide();
    var cherry = new Cherry({
        id: 'markdown',
        value: codeNode.val(),
        editor: {
            defaultModel: 'previewOnly'
        },
        toolbars: {
            toc: {
                // updateLocationHash: false, // 要不要更新URL的hash
                defaultModel: 'full', // pure: 精简模式/缩略模式，只有一排小点； full: 完整模式，会展示所有标题
            }
        }
    });
</script>
</body>

</html>