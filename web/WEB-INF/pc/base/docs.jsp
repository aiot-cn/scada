<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${doc.name} - 文档</title>
    <link rel="icon" href="${res}/images/favicon.ico" type="image/x-icon">
    <script src="${res}/plugin/code-prettify/prettify.js"></script>
    <link href="${res}/plugin/code-prettify/prettify.css" rel="stylesheet" >
    <link href="${res}/plugin/docs/docs.css" rel="stylesheet" >
    <style>

    </style>
</head>
<body>

<!-- 顶部导航 -->
<header class="header">
    <div class="nav-bar">
        <a href="${base}" class="nav-logo">
            <img src="${res}/app/index/image/logo-dark.png"  alt="aiot-logo" style="height: 40px">
            <span>文档</span>
        </a>

        <div class="nav-search">
            <button>
                <span>搜索文档...</span>
                <kbd>Ctrl K</kbd>
            </button>
        </div>

        <nav class="nav-links">
            <c:forEach items="${docProject}" var="v">
                <a href="${base}/docs/${v.code}" class="${v.code == docProCode ? "active" : ""}">${v.name}</a>
            </c:forEach>
        </nav>

        <div class="nav-actions">
            <button class="icon-btn" aria-label="切换主题">
                <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="5"/><path d="M12 1v2M12 21v2M4.22 4.22l1.42 1.42M18.36 18.36l1.42 1.42M1 12h2M21 12h2M4.22 19.78l1.42-1.42M18.36 5.64l1.42-1.42"/></svg>
            </button>
        </div>
    </div>
</header>

<!-- 移动端侧边栏遮罩 -->
<div class="nav-backdrop" id="navBackdrop"></div>

<!-- 页面主体 -->
<div class="page">

    <!-- 左侧菜单（桌面端） -->
    <aside class="sidebar">
        <div class="sidebar-group">
            <ul>
                <c:forEach items="${docTree}" var="item">
                    <li class="sidebar-item level-${item.level}<c:if test="${item.id == doc.id}"> active</c:if>">
                        <c:if test="${!empty item.url}">
                            <a href="${base}/docs/${item.url}">${item.name}</a>
                        </c:if>
                        <c:if test="${empty item.url}">
                            <a>${item.name}</a>
                        </c:if>
                    </li>
                </c:forEach>
            </ul>
        </div>

    </aside>

    <!-- 中间内容 -->
    <main class="content">
        <nav class="breadcrumb">
            <button class="nav-more" type="button" aria-label="打开菜单">
                <svg width="18" height="18" viewBox="0 0 18 18" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true">
                    <path d="M2.25 3.75H15.75"></path>
                    <path d="M2.25 9H15.75"></path>
                    <path d="M2.25 14.25H15.75"></path>
                </svg>
            </button>
            <a href="${base}/docs/${docProCode}">${docProjectName}</a>
            <span>/</span>
            <span>${doc.name}</span>
        </nav>

        <article class="markdown-body">
            ${docContentHtml}
        </article>
    </main>

    <!-- 右侧目录 -->
    <aside class="toc">
        <div class="toc-title">
            <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><line x1="3" y1="6" x2="21" y2="6"></line><line x1="3" y1="12" x2="21" y2="12"></line><line x1="3" y1="18" x2="15" y2="18"></line></svg>
            <span>在此页面</span>
        </div>
        <ul id="toc-list">
            <!-- 由 JS 根据正文标题生成 -->
        </ul>
    </aside>

</div>

<!-- 页脚 -->
<footer class="footer">
    © 2026 aiot 爱物联 · 保留所有权利
</footer>
<script src="${res}/plugin/docs/docs.js"></script>
<script>

</script>

</body>
</html>
