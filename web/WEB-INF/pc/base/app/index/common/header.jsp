<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<header class="header">

    <div class="main-navigation">
        <nav class="navbar navbar-expand-lg">
            <div class="container">
                <a class="navbar-brand" href="${base}/base/app/index/index">
                    <img src="${res}/app/index/image/logo.png?v=1" class="logo-display" alt="logo">
                    <img src="${res}/app/index/image/logo-dark.png?v=1" class="logo-scrolled" alt="logo">
                </a>
                <div class="mobile-menu-right">
                    <a href="#" class="mobile-search-btn search-box-outer"><i class="far fa-search"></i></a>
                    <button class="navbar-toggler" type="button" data-bs-toggle="collapse"
                            data-bs-target="#main_nav" aria-expanded="false" aria-label="Toggle navigation">
                        <span class="navbar-toggler-icon"><i class="far fa-bars"></i></span>
                    </button>
                </div>
                <div class="collapse navbar-collapse" id="main_nav">
                    <ul class="navbar-nav ms-auto">
                        <%--<li class="nav-item"><a class="nav-link" href="${base}/base/app/index/index#about"> 关于我们 </a></li>
                        <li class="nav-item dropdown">
                            <a class="nav-link dropdown-toggle" href="#" data-bs-toggle="dropdown">Pages</a>
                            <ul class="dropdown-menu fade-up">
                                <li><a class="dropdown-item" href="about.html">About Us</a></li>
                                <li><a class="dropdown-item" href="team.html">Team Members</a></li>
                                <li><a class="dropdown-item" href="pricing.html">Our Pricing</a></li>
                                <li><a class="dropdown-item" href="contact.html">Contact Us</a></li>
                                <li><a class="dropdown-item" href="faq.html">Faq</a></li>
                                <li><a class="dropdown-item" href="testimonial.html">Testimonials</a></li>
                                <li><a class="dropdown-item" href="login.html">Login</a></li>
                                <li><a class="dropdown-item" href="register.html">Register</a></li>
                                <li><a class="dropdown-item" href="forgot-password.html">Forgot Password</a></li>
                                <li><a class="dropdown-item" href="404.html">404 Error</a></li>
                                <li><a class="dropdown-item" href="coming-soon.html">Coming Soon</a></li>
                                <li><a class="dropdown-item" href="terms.html">Terms Of Service</a></li>
                                <li><a class="dropdown-item" href="privacy.html">Privacy Policy</a></li>
                            </ul>
                        </li>--%>
                        <li class="nav-item dropdown">
                            <a class="nav-link dropdown-toggle" href="#" data-bs-toggle="dropdown">服务支持</a>
                            <ul class="dropdown-menu fade-up">
                                <li><a class="dropdown-item" href="service.html">轨道机器人</a></li>
                                <li><a class="dropdown-item" href="service-2.html">轮式机器人</a></li>
                                <li><a class="dropdown-item" href="service-single.html">图像识别</a></li>
                                <li><a class="dropdown-item" href="service-single.html">传感器</a></li>
                                <li><a class="dropdown-item" href="service-single.html">协议转换</a></li>
                                <li><a class="dropdown-item" href="service-single.html">数据分析统计</a></li>
                            </ul>
                        </li>
                        <li class="nav-item"><a class="nav-link" href="${base}/base/app/index/project/single"> 项目 </a></li>
                        <li class="nav-item dropdown">
                            <a class="nav-link dropdown-toggle" href="#" data-bs-toggle="dropdown">文档</a>
                            <ul class="dropdown-menu fade-up">
                                <li><a class="dropdown-item" href="${base}/base/app/index/quickStart">快速开始</a></li>
                                <li><a class="dropdown-item" href="${base}/base/app/index/instruction">使用说明</a></li>
                                <li><a class="dropdown-item" href="blog-single.html">相关资料</a></li>
                            </ul>
                        </li>
                        <li class="nav-item"><a class="nav-link" href="${base}/base/app/index/contact">联系我们</a></li>
                        <li class="nav-item"><a class="nav-link" href="${base}/index">在线预览</a></li>
                    </ul>
                    <div class="header-nav-right">
                        <div class="header-nav-search">
                            <a href="#" class="search-box-outer"><i class="far fa-search"></i></a>
                        </div>
                        <%--<div class="header-btn">
                            <a href="#" class="theme-btn">获取报价</a>
                        </div>--%>
                    </div>
                </div>
            </div>
        </nav>
    </div>
</header>

<!-- popup search -->
<div class="search-popup">
    <button class="close-search"><span class="far fa-times"></span></button>
    <form action="#">
        <div class="form-group">
            <input type="search" name="search-field" placeholder="Search Here..." required="">
            <button type="submit"><i class="far fa-search"></i></button>
        </div>
    </form>
</div>
<!-- popup search end -->