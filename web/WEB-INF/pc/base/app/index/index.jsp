<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>

<html lang="zh-CN">
    <head>

        <meta name="description" content="i物联，万物连">
        <meta name="keywords" content="">

        <title>aiot | i物联 万物连</title>

        <%@include file="common/head.jsp" %>
    </head>

<body>

<%@include file="common/header.jsp" %>

<main class="main">

    <!-- hero area -->
    <div class="hero-section">
        <div class="hero-wrapper">
            <div class="container">
                <div class="row align-items-center">
                    <div class="col-md-6 col-lg-6">
                        <div class="hero-content">
                            <h6 class="hero-sub-title wow animate__ animate__fadeInUp animated" data-wow-duration="1s" data-wow-delay=".25s" style="visibility: visible; animation-duration: 1s; animation-delay: 0.25s; animation-name: fadeInUp;">
                                机器人 物联网 监控 检测 <span>应用</span>一站式解决方案
                            </h6>
                            <h1 class="hero-title wow animate__ animate__fadeInUp animated" data-wow-duration="1s" data-wow-delay=".50s"
                                style="visibility: visible; animation-duration: 1s; animation-delay: 0.5s; animation-name: fadeInUp;">
                                开箱即用的机器学习、<span>AI识别</span>、智能监控的系统，多种接口API。
                            </h1>
                            <p class="wow animate__ animate__fadeInUp animated" data-wow-duration="1s" data-wow-delay=".75s" style="visibility: visible; animation-duration: 1s; animation-delay: 0.75s; animation-name: fadeInUp;">
                                全平台支持：Windows、Linux、移动端、嵌入式
                            </p>
                            <div class="hero-btn wow animate__ animate__fadeInUp animated" data-wow-duration="1s" data-wow-delay="1s" style="visibility: visible; animation-duration: 1s; animation-delay: 1s; animation-name: fadeInUp;">
                                <a href="${base}/base/app/index/quickStart" class="theme-btn">快速开始</a>
                                <div class="video-btn">
                                    <a href="#" class="play-btn popup-youtube"><i class="far fa-play"></i></a>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="col-md-6 col-lg-6">
                        <div class="hero-img">
                            <img src="${res}/app/index/image/hero-1.png" alt="">
                        </div>
                    </div>
                </div>
            </div>
            <div class="hero-shape">
                <img src="${res}/app/index/image/shape-3.png" alt="">
            </div>
        </div>
    </div>
    <!-- hero area end -->



    <!-- feature area -->
    <div class="feature-area pt-120">
        <div class="container">
            <div class="feature-area-wrapper">
                <div class="row">
                    <div class="col-md-6 col-lg-4">
                        <div class="feature-item">
                            <div class="feature-icon">
                                <i class="flaticon-processor"></i>
                            </div>
                            <div class="feature-content">
                                <h5>人工智能</h5>
                                <p>It is a long established fact that a reader will be distracted by the readable
                                    content of a page when looking at its layout.</p>
                            </div>
                        </div>
                    </div>
                    <div class="col-md-6 col-lg-4">
                        <div class="feature-item">
                            <div class="feature-icon">
                                <i class="flaticon-machine-learning"></i>
                            </div>
                            <div class="feature-content">
                                <h5>机器学习</h5>
                                <p>It is a long established fact that a reader will be distracted by the readable
                                    content of a page when looking at its layout.</p>
                            </div>
                        </div>
                    </div>
                    <div class="col-md-6 col-lg-4">
                        <div class="feature-item">
                            <div class="feature-icon">
                                <i class="flaticon-deep-learning"></i>
                            </div>
                            <div class="feature-content">
                                <h5>智能监控</h5>
                                <p>It is a long established fact that a reader will be distracted by the readable
                                    content of a page when looking at its layout.</p>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
    <!-- feature area end -->


    <!-- about area -->
    <div class="about-area py-120">
        <div class="container">
            <div class="row align-items-center">
                <div class="col-lg-6">
                    <div class="about-left">
                        <div class="about-img">
                            <img src="${res}/app/index/image/01.png" alt="">
                        </div>
                    </div>
                </div>
                <div class="col-lg-6">
                    <div class="about-right">
                        <div class="site-heading mb-3">
                            <span class="site-title-tagline">关于我们</span>
                            <h2 class="site-title">我们为您提供 <span>机器学习和人工智能</span> 服务
                            </h2>
                        </div>
                        <p class="about-text">
                            开箱即用的机器人、智能监测系统，多种API接口，无依赖，可直接底层二次开发
                        </p>
                        <div class="about-list-wrapper">
                            <ul class="about-list list-unstyled">
                                <li>
                                    <div class="icon"><span class="fas fa-check-circle"></span></div>
                                    <div class="text">
                                        <p>全平台硬件支持</p>
                                    </div>
                                </li>
                                <li>
                                    <div class="icon"><span class="fas fa-check-circle"></span></div>
                                    <div class="text">
                                        <p>丰富的API接口种类</p>
                                    </div>
                                </li>
                                <li>
                                    <div class="icon"><span class="fas fa-check-circle"></span></div>
                                    <div class="text">
                                        <p>完善的应用场景</p>
                                    </div>
                                </li>
                            </ul>
                        </div>
                        <a href="about.html" class="theme-btn">Discover More</a>
                    </div>
                </div>
            </div>
        </div>
    </div>
    <!-- about area end -->


    <!-- service area -->
    <div class="service-area bg py-120">
        <div class="container">
            <div class="row">
                <div class="col-lg-6 mx-auto">
                    <div class="site-heading text-center">
                        <span class="site-title-tagline">Services</span>
                        <h2 class="site-title">What We <span>Offer</span></h2>
                        <div class="heading-divider"></div>
                    </div>
                </div>
            </div>
            <div class="row">
                <div class="col-md-6 col-lg-4">
                    <div class="service-item">
                        <div class="service-icon">
                            <i class="flaticon-machine-learning"></i>
                        </div>
                        <h3 class="service-title">
                            <a href="#">Machine Learning</a>
                        </h3>
                        <p class="service-text">
                            At vero eos et accusamus iusto odiota kattioda dignissimos ducimus qui blanditiis
                            praesentium voluptatum deleniti.
                        </p>
                        <div class="service-arrow">
                            <a href="#" class="service-read-btn">Learn More<i class="far fa-long-arrow-right"></i></a>
                        </div>
                    </div>
                </div>
                <div class="col-md-6 col-lg-4">
                    <div class="service-item">
                        <div class="service-icon">
                            <i class="flaticon-data-mining"></i>
                        </div>
                        <h3 class="service-title">
                            <a href="#">Data Mining</a>
                        </h3>
                        <p class="service-text">
                            At vero eos et accusamus iusto odiota kattioda dignissimos ducimus qui blanditiis
                            praesentium voluptatum deleniti.
                        </p>
                        <div class="service-arrow">
                            <a href="#" class="service-read-btn">Learn More<i class="far fa-long-arrow-right"></i></a>
                        </div>
                    </div>
                </div>
                <div class="col-md-6 col-lg-4">
                    <div class="service-item">
                        <div class="service-icon">
                            <i class="flaticon-automation"></i>
                        </div>
                        <h3 class="service-title">
                            <a href="#">Robotic Automation</a>
                        </h3>
                        <p class="service-text">
                            At vero eos et accusamus iusto odiota kattioda dignissimos ducimus qui blanditiis
                            praesentium voluptatum deleniti.
                        </p>
                        <div class="service-arrow">
                            <a href="#" class="service-read-btn">Learn More<i class="far fa-long-arrow-right"></i></a>
                        </div>
                    </div>
                </div>
                <div class="col-md-6 col-lg-4">
                    <div class="service-item">
                        <div class="service-icon">
                            <i class="flaticon-deep-learning"></i>
                        </div>
                        <h3 class="service-title">
                            <a href="#">Deep Learning</a>
                        </h3>
                        <p class="service-text">
                            At vero eos et accusamus iusto odiota kattioda dignissimos ducimus qui blanditiis
                            praesentium voluptatum deleniti.
                        </p>
                        <div class="service-arrow">
                            <a href="#" class="service-read-btn">Learn More<i class="far fa-long-arrow-right"></i></a>
                        </div>
                    </div>
                </div>
                <div class="col-md-6 col-lg-4">
                    <div class="service-item">
                        <div class="service-icon">
                            <i class="flaticon-predictive-chart"></i>
                        </div>
                        <h3 class="service-title">
                            <a href="#">Predictive Analytic</a>
                        </h3>
                        <p class="service-text">
                            At vero eos et accusamus iusto odiota kattioda dignissimos ducimus qui blanditiis
                            praesentium voluptatum deleniti.
                        </p>
                        <div class="service-arrow">
                            <a href="#" class="service-read-btn">Learn More<i class="far fa-long-arrow-right"></i></a>
                        </div>
                    </div>
                </div>
                <div class="col-md-6 col-lg-4">
                    <div class="service-item">
                        <div class="service-icon">
                            <i class="flaticon-processor"></i>
                        </div>
                        <h3 class="service-title">
                            <a href="#">Security &amp; Surveillance</a>
                        </h3>
                        <p class="service-text">
                            At vero eos et accusamus iusto odiota kattioda dignissimos ducimus qui blanditiis
                            praesentium voluptatum deleniti.
                        </p>
                        <div class="service-arrow">
                            <a href="#" class="service-read-btn">Learn More<i class="far fa-long-arrow-right"></i></a>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
    <!-- service area end -->



    <!-- choose area -->
    <div class="choose-area py-120">
        <div class="container">
            <div class="row align-items-center">
                <div class="col-lg-6">
                    <div class="choose-img">
                        <img src="${res}/app/index/image/01.svg" alt="">
                    </div>
                </div>
                <div class="col-lg-6">
                    <div class="choose-content">
                        <div class="site-heading mb-3">
                            <span class="site-title-tagline">Why Choose Us</span>
                            <h2 class="site-title my-3">We Provide Best <span>Machine Learning &amp; Data</span> Solutions
                            </h2>
                        </div>
                        <p class="mb-30">There are many variations of passages of Lorem Ipsum available,
                            but the majority have suffered alteration in some form, by injected humour, or
                            randomised words which don't look even.</p>
                        <div class="row">
                            <div class="col-md-6">
                                <ul>
                                    <li>
                                        <h5>Managed Secure Backups</h5>
                                    </li>
                                    <li>
                                        <h5>Advanced Tchnology</h5>
                                    </li>
                                    <li>
                                        <h5>High-Quality Results</h5>
                                    </li>
                                </ul>
                            </div>
                            <div class="col-md-6">
                                <ul class="mt-md-0">
                                    <li>
                                        <h5>Competitive Pricing</h5>
                                    </li>
                                    <li>
                                        <h5>Advance Advisory Team</h5>
                                    </li>
                                    <li>
                                        <h5>Advance Quality Experts</h5>
                                    </li>
                                </ul>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
    <!-- choose area end -->



    <!-- counter area -->
    <div class="counter-area">
        <div class="container">
            <div class="row">
                <div class="col-lg-3 col-sm-6">
                    <div class="counter-box">
                        <div class="icon"><i class="fal fa-layer-group"></i></div>
                        <span class="counter" data-count="+" data-to="500" data-speed="3000">500</span>
                        <h6 class="title">+ Project Done</h6>
                    </div>
                </div>
                <div class="col-lg-3 col-sm-6">
                    <div class="counter-box">
                        <div class="icon"><i class="fal fa-mug-hot"></i></div>
                        <span class="counter" data-count="+" data-to="250" data-speed="3000">250</span>
                        <h6 class="title">+ Cup Of Tea</h6>
                    </div>
                </div>
                <div class="col-lg-3 col-sm-6">
                    <div class="counter-box">
                        <div class="icon"><i class="fal fa-user-friends"></i></div>
                        <span class="counter" data-count="+" data-to="120" data-speed="3000">120</span>
                        <h6 class="title">+ Active Experts</h6>
                    </div>
                </div>
                <div class="col-lg-3 col-sm-6">
                    <div class="counter-box">
                        <div class="icon"><i class="fal fa-headset"></i></div>
                        <span class="counter" data-count="+" data-to="300" data-speed="3000">300</span>
                        <h6 class="title">+ Total Support</h6>
                    </div>
                </div>
            </div>
        </div>
    </div>
    <!-- counter area end -->



    <!-- case area -->
    <div class="case-area py-120">
        <div class="container">
            <div class="row">
                <div class="col-lg-6 mx-auto">
                    <div class="site-heading text-center">
                        <span class="site-title-tagline">Cases</span>
                        <h2 class="site-title">Featured <span>Projects</span></h2>
                        <div class="heading-divider"></div>
                    </div>
                </div>
            </div>
            <div class="filter-controls">
                <ul class="filter-btns">
                    <li class="active" data-filter="*">All</li>
                    <li data-filter=".cat1">Machine</li>
                    <li data-filter=".cat2">Robot</li>
                    <li data-filter=".cat3">Technology</li>
                    <li data-filter=".cat4">Data</li>
                    <li data-filter=".cat5">Science</li>
                </ul>
            </div>
            <div class="row filter-box popup-gallery" style="position: relative; height: 895.876px;">
                <div class="col-md-6 col-lg-4 filter-item cat1 cat2" style="position: absolute; left: 0px; top: 0px;">
                    <div class="case-item">
                        <div class="case-img">
                            <img class="img-fluid" src="${res}/app/index/image/01.jpg" alt="">
                            <a class="popup-img case-link" href="${res}/app/index/image/01.jpg"> <i class="fal fa-plus"></i></a>
                        </div>
                        <div class="case-content">
                            <div class="case-content-info">
                                <small>Science</small>
                                <h4><a href="#">Artificial Intelligence</a></h4>
                            </div>
                            <a href="#" class="case-arrow"><i class="far fa-arrow-right"></i></a>
                        </div>
                    </div>
                </div>
                <div class="col-md-6 col-lg-4 filter-item cat2 cat3" style="position: absolute; left: 395px; top: 0px;">
                    <div class="case-item">
                        <div class="case-img">
                            <img class="img-fluid" src="${res}/app/index/image/02.jpg" alt="">
                            <a class="popup-img case-link" href="${res}/app/index/image/02.jpg"> <i class="fal fa-plus"></i></a>
                        </div>
                        <div class="case-content">
                            <div class="case-content-info">
                                <small>Data</small>
                                <h4><a href="#">Data Science</a></h4>
                            </div>
                            <a href="#" class="case-arrow"><i class="far fa-arrow-right"></i></a>
                        </div>
                    </div>
                </div>
                <div class="col-md-6 col-lg-4 filter-item cat3 cat4 cat5" style="position: absolute; left: 790px; top: 0px;">
                    <div class="case-item">
                        <div class="case-img">
                            <img class="img-fluid" src="${res}/app/index/image/03.jpg" alt="">
                            <a class="popup-img case-link" href="${res}/app/index/image/03.jpg"> <i class="fal fa-plus"></i></a>
                        </div>
                        <div class="case-content">
                            <div class="case-content-info">
                                <small>Machine</small>
                                <h4><a href="#">Machine Learning</a></h4>
                            </div>
                            <a href="#" class="case-arrow"><i class="far fa-arrow-right"></i></a>
                        </div>
                    </div>
                </div>
                <div class="col-md-6 col-lg-4 filter-item cat2 cat4" style="position: absolute; left: 0px; top: 447.938px;">
                    <div class="case-item">
                        <div class="case-img">
                            <img class="img-fluid" src="${res}/app/index/image/04.jpg" alt="">
                            <a class="popup-img case-link" href="${res}/app/index/image/04.jpg"> <i class="fal fa-plus"></i></a>
                        </div>
                        <div class="case-content">
                            <div class="case-content-info">
                                <small>Drone</small>
                                <h4><a href="#">Advanced Drone</a></h4>
                            </div>
                            <a href="#" class="case-arrow"><i class="far fa-arrow-right"></i></a>
                        </div>
                    </div>
                </div>
                <div class="col-md-6 col-lg-4 filter-item cat1 cat4 cat5" style="position: absolute; left: 395px; top: 447.938px;">
                    <div class="case-item">
                        <div class="case-img">
                            <img class="img-fluid" src="${res}/app/index/image/05.jpg" alt="">
                            <a class="popup-img case-link" href="${res}/app/index/image/05.jpg"> <i class="fal fa-plus"></i></a>
                        </div>
                        <div class="case-content">
                            <div class="case-content-info">
                                <small>Technology</small>
                                <h4><a href="#">Modern Technology</a></h4>
                            </div>
                            <a href="#" class="case-arrow"><i class="far fa-arrow-right"></i></a>
                        </div>
                    </div>
                </div>
                <div class="col-md-6 col-lg-4 filter-item cat4 cat3" style="position: absolute; left: 790px; top: 447.938px;">
                    <div class="case-item">
                        <div class="case-img">
                            <img class="img-fluid" src="${res}/app/index/image/06.jpg" alt="">
                            <a class="popup-img case-link" href="${res}/app/index/image/06.jpg"> <i class="fal fa-plus"></i></a>
                        </div>
                        <div class="case-content">
                            <div class="case-content-info">
                                <small>Robot</small>
                                <h4><a href="#">Advanced Robot</a></h4>
                            </div>
                            <a href="#" class="case-arrow"><i class="far fa-arrow-right"></i></a>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
    <!-- case area end -->



    <!-- team-area -->
    <div class="team-area bg py-120">
        <div class="container">
            <div class="row">
                <div class="col-lg-6 mx-auto">
                    <div class="site-heading text-center">
                        <span class="site-title-tagline">Team</span>
                        <h2 class="site-title">Meet <span>Experts</span></h2>
                        <div class="heading-divider"></div>
                    </div>
                </div>
            </div>
            <div class="row">
                <div class="col-md-6 col-lg-4 col-xl-3">
                    <div class="team-item">
                        <img src="${res}/app/index/image/01(1).jpg" alt="thumb">
                        <div class="team-content">
                            <div class="team-social">
                                <a href="#"><i class="fab fa-facebook-f"></i></a>
                                <a href="#"><i class="fab fa-x-twitter"></i></a>
                                <a href="#"><i class="fab fa-instagram"></i></a>
                                <a href="#"><i class="fab fa-linkedin"></i></a>
                            </div>
                            <div class="team-bio">
                                <h5><a href="#">Malissa Fierro</a></h5>
                                <span>AI Expert</span>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="col-md-6 col-lg-4 col-xl-3">
                    <div class="team-item">
                        <img src="${res}/app/index/image/02(1).jpg" alt="thumb">
                        <div class="team-content">
                            <div class="team-social">
                                <a href="#"><i class="fab fa-facebook-f"></i></a>
                                <a href="#"><i class="fab fa-x-twitter"></i></a>
                                <a href="#"><i class="fab fa-instagram"></i></a>
                                <a href="#"><i class="fab fa-linkedin"></i></a>
                            </div>
                            <div class="team-bio">
                                <h5><a href="#">Arron Rodri</a></h5>
                                <span>Project Manager</span>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="col-md-6 col-lg-4 col-xl-3">
                    <div class="team-item active">
                        <img src="${res}/app/index/image/03(1).jpg" alt="thumb">
                        <div class="team-content">
                            <div class="team-social">
                                <a href="#"><i class="fab fa-facebook-f"></i></a>
                                <a href="#"><i class="fab fa-x-twitter"></i></a>
                                <a href="#"><i class="fab fa-instagram"></i></a>
                                <a href="#"><i class="fab fa-linkedin"></i></a>
                            </div>
                            <div class="team-bio">
                                <h5><a href="#">Chad Smith</a></h5>
                                <span>Data Analyst</span>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="col-md-6 col-lg-4 col-xl-3">
                    <div class="team-item">
                        <img src="${res}/app/index/image/04(1).jpg" alt="thumb">
                        <div class="team-content">
                            <div class="team-social">
                                <a href="#"><i class="fab fa-facebook-f"></i></a>
                                <a href="#"><i class="fab fa-x-twitter"></i></a>
                                <a href="#"><i class="fab fa-instagram"></i></a>
                                <a href="#"><i class="fab fa-linkedin"></i></a>
                            </div>
                            <div class="team-bio">
                                <h5><a href="#">Tony Pinto</a></h5>
                                <span>CEO &amp; Founder</span>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
    <!-- team-area end -->



    <!-- pricing area -->
    <div class="pricing-area py-120">
        <div class="container">
            <div class="row">
                <div class="col-lg-6 mx-auto">
                    <div class="site-heading text-center">
                        <span class="site-title-tagline">Pricing</span>
                        <h2 class="site-title">Pricing <span>Plan</span></h2>
                        <div class="heading-divider"></div>
                    </div>
                </div>
            </div>

            <div class="row g-5">
                <div class="col-md-6 col-lg-4">
                    <div class="pricing-item">
                        <div class="pricing-header">
                            <h5>Basic</h5>
                        </div>
                        <div class="pricing-amount">
                            <strong>$59</strong>
                        </div>
                        <div class="pricing-amount-type">
                            <span>/Monthly</span>
                        </div>
                        <div class="pricing-feature">
                            <ul>
                                <li><i class="far fa-check"></i> Face Detect API</li>
                                <li><i class="far fa-check"></i> Upgrade Plan Anytime</li>
                                <li><i class="far fa-check"></i> Bot &amp; Digital Assistants</li>
                                <li><i class="far fa-check"></i> Drag &amp; Drop Page Builder</li>
                                <li><i class="far fa-check"></i> Up to 1000 Subscribers</li>
                                <li><i class="far fa-check"></i> Unlimited Broadcasts</li>
                                <li><i class="far fa-check"></i> 24/7 Support</li>
                            </ul>
                        </div>
                        <div class="pricing-footer">
                            <a href="#" class="theme-btn">Get Started</a>
                        </div>
                    </div>
                </div>
                <div class="col-md-6 col-lg-4">
                    <div class="pricing-item">
                        <div class="pricing-header">
                            <h5>Business</h5>
                        </div>
                        <div class="pricing-amount">
                            <strong>$110</strong>
                        </div>
                        <div class="pricing-amount-type">
                            <span>/Monthly</span>
                        </div>
                        <div class="pricing-feature">
                            <ul>
                                <li><i class="far fa-check"></i> Face Detect API</li>
                                <li><i class="far fa-check"></i> Upgrade Plan Anytime</li>
                                <li><i class="far fa-check"></i> Bot &amp; Digital Assistants</li>
                                <li><i class="far fa-check"></i> Drag &amp; Drop Page Builder</li>
                                <li><i class="far fa-check"></i> Up to 1000 Subscribers</li>
                                <li><i class="far fa-check"></i> Unlimited Broadcasts</li>
                                <li><i class="far fa-check"></i> 24/7 Support</li>
                            </ul>
                        </div>
                        <div class="pricing-footer">
                            <a href="#" class="theme-btn">Get Started</a>
                        </div>
                    </div>
                </div>
                <div class="col-md-6 col-lg-4">
                    <div class="pricing-item">
                        <div class="pricing-header">
                            <h5>Premium</h5>
                        </div>
                        <div class="pricing-amount">
                            <strong>$156</strong>
                        </div>
                        <div class="pricing-amount-type">
                            <span>/Monthly</span>
                        </div>
                        <div class="pricing-feature">
                            <ul>
                                <li><i class="far fa-check"></i> Face Detect API</li>
                                <li><i class="far fa-check"></i> Upgrade Plan Anytime</li>
                                <li><i class="far fa-check"></i> Bot &amp; Digital Assistants</li>
                                <li><i class="far fa-check"></i> Drag &amp; Drop Page Builder</li>
                                <li><i class="far fa-check"></i> Up to 1000 Subscribers</li>
                                <li><i class="far fa-check"></i> Unlimited Broadcasts</li>
                                <li><i class="far fa-check"></i> 24/7 Support</li>
                            </ul>
                        </div>
                        <div class="pricing-footer">
                            <a href="#" class="theme-btn">Get Started</a>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
    <!-- pricing area end -->



    <!-- cta area -->
    <div class="cta-area">
        <div class="container">
            <div class="row">
                <div class="cta-content">
                    <h5>We Offer Quality Service</h5>
                    <h2>We Are Offering 30 Days Free Trial</h2>
                    <p>It is a long established fact that a reader will be distracted by the readable content <br>
                        of a page when looking at its layout.</p>
                    <a href="#" class="theme-btn mt-5">Start Your Free Trial</a>
                </div>
            </div>
        </div>
    </div>
    <!-- cta area end -->



    <!-- faq area -->
    <div class="faq-area py-120">
        <div class="container">
            <div class="row">
                <div class="col-lg-6">
                    <div class="faq-left">
                        <div class="site-heading mb-3">
                            <span class="site-title-tagline">Faq's</span>
                            <h2 class="site-title my-3">General <span>frequently</span> asked questions</h2>
                        </div>
                        <p class="about-text">There are many variations of passages of Lorem Ipsum available,
                            but the majority have suffered alteration in some form, by injected humour, or
                            randomised words which don't look even.</p>
                        <p>It is a long established fact that a reader will be distracted by the readable content of
                            a page when looking at its layout. </p>
                        <a href="#" class="theme-btn mt-5">Ask Question</a>
                    </div>
                </div>
                <div class="col-lg-6">
                    <div class="accordion" id="accordionExample">
                        <div class="accordion-item">
                            <h2 class="accordion-header" id="headingOne">
                                <button class="accordion-button" type="button" data-bs-toggle="collapse" data-bs-target="#collapseOne" aria-expanded="true" aria-controls="collapseOne">
                                    <span><i class="far fa-question"></i></span> Do I Need A Business Plan ?
                                </button>
                            </h2>
                            <div id="collapseOne" class="accordion-collapse collapse show" aria-labelledby="headingOne" data-bs-parent="#accordionExample">
                                <div class="accordion-body">
                                    We denounce with righteous indignation and dislike men who
                                    are so beguiled and demoralized by the charms of pleasure of the moment, so
                                    blinded by desire.
                                </div>
                            </div>
                        </div>
                        <div class="accordion-item">
                            <h2 class="accordion-header" id="headingTwo">
                                <button class="accordion-button collapsed" type="button" data-bs-toggle="collapse" data-bs-target="#collapseTwo" aria-expanded="false" aria-controls="collapseTwo">
                                    <span><i class="far fa-question"></i></span> How Long Should A Business Plan Be
                                    ?
                                </button>
                            </h2>
                            <div id="collapseTwo" class="accordion-collapse collapse" aria-labelledby="headingTwo" data-bs-parent="#accordionExample">
                                <div class="accordion-body">
                                    We denounce with righteous indignation and dislike men who
                                    are so beguiled and demoralized by the charms of pleasure of the moment, so
                                    blinded by desire.
                                </div>
                            </div>
                        </div>
                        <div class="accordion-item">
                            <h2 class="accordion-header" id="headingThree">
                                <button class="accordion-button collapsed" type="button" data-bs-toggle="collapse" data-bs-target="#collapseThree" aria-expanded="false" aria-controls="collapseThree">
                                    <span><i class="far fa-question"></i></span> What Payment Gateway You Support ?
                                </button>
                            </h2>
                            <div id="collapseThree" class="accordion-collapse collapse" aria-labelledby="headingThree" data-bs-parent="#accordionExample">
                                <div class="accordion-body">
                                    We denounce with righteous indignation and dislike men who
                                    are so beguiled and demoralized by the charms of pleasure of the moment, so
                                    blinded by desire.
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
    <!-- faq area end -->



    <!-- testimonial-area -->
    <div class="testimonial-area bg py-120">
        <div class="container">
            <div class="row">
                <div class="col-lg-6 mx-auto">
                    <div class="site-heading text-center">
                        <span class="site-title-tagline">Testimonials</span>
                        <h2 class="site-title">What Client <span>Say's</span></h2>
                        <div class="heading-divider"></div>
                    </div>
                </div>
            </div>
            <div class="testimonial-slider owl-carousel owl-theme">
                <div class="testimonial-single">
                    <div class="testimonial-quote">
                        <span class="testimonial-quote-icon"><i class="fal fa-quote-left"></i></span>
                        <p>
                            There are many variations of passages available but the majority have suffered
                            alteration in some form by injected.
                        </p>
                        <div class="testimonial-rate">
                            <i class="fas fa-star"></i>
                            <i class="fas fa-star"></i>
                            <i class="fas fa-star"></i>
                            <i class="fas fa-star"></i>
                            <i class="fas fa-star"></i>
                        </div>
                    </div>
                    <div class="testimonial-content">
                        <div class="testimonial-author-img">
                            <img src="${res}/app/index/image/01.jpg" alt="">
                        </div>
                        <div class="testimonial-author-info">
                            <h4>Sylvia H Green</h4>
                            <p>Founder & CEO</p>
                        </div>
                    </div>
                </div>
                <div class="testimonial-single">
                    <div class="testimonial-quote">
                        <span class="testimonial-quote-icon"><i class="fal fa-quote-left"></i></span>
                        <p>
                            There are many variations of passages available but the majority have suffered
                            alteration in some form by injected.
                        </p>
                        <div class="testimonial-rate">
                            <i class="fas fa-star"></i>
                            <i class="fas fa-star"></i>
                            <i class="fas fa-star"></i>
                            <i class="fas fa-star"></i>
                            <i class="fas fa-star"></i>
                        </div>
                    </div>
                    <div class="testimonial-content">
                        <div class="testimonial-author-img">
                            <img src="${res}/app/index/image/04.jpg" alt="">
                        </div>
                        <div class="testimonial-author-info">
                            <h4>Gordon D Novak</h4>
                            <p>Founder & CEO</p>
                        </div>
                    </div>
                </div>
                <div class="testimonial-single">
                    <div class="testimonial-quote">
                        <span class="testimonial-quote-icon"><i class="fal fa-quote-left"></i></span>
                        <p>
                            There are many variations of passages available but the majority have suffered
                            alteration in some form by injected.
                        </p>
                        <div class="testimonial-rate">
                            <i class="fas fa-star"></i>
                            <i class="fas fa-star"></i>
                            <i class="fas fa-star"></i>
                            <i class="fas fa-star"></i>
                            <i class="fas fa-star"></i>
                        </div>
                    </div>
                    <div class="testimonial-content">
                        <div class="testimonial-author-img">
                            <img src="${res}/app/index/image/05.jpg" alt="">
                        </div>
                        <div class="testimonial-author-info">
                            <h4>Reid E Butt</h4>
                            <p>Founder & CEO</p>
                        </div>
                    </div>
                </div>
                <div class="testimonial-single">
                    <div class="testimonial-quote">
                        <span class="testimonial-quote-icon"><i class="fal fa-quote-left"></i></span>
                        <p>
                            There are many variations of passages available but the majority have suffered
                            alteration in some form by injected.
                        </p>
                        <div class="testimonial-rate">
                            <i class="fas fa-star"></i>
                            <i class="fas fa-star"></i>
                            <i class="fas fa-star"></i>
                            <i class="fas fa-star"></i>
                            <i class="fas fa-star"></i>
                        </div>
                    </div>
                    <div class="testimonial-content">
                        <div class="testimonial-author-img">
                            <img src="${res}/app/index/image/02.jpg" alt="">
                        </div>
                        <div class="testimonial-author-info">
                            <h4>Parker Jimenez</h4>
                            <p>Founder & CEO</p>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
    <!-- testimonial-area end -->



    <!-- blog-area -->
    <div class="blog-area pt-120">
        <div class="container">
            <div class="row">
                <div class="col-lg-6 mx-auto">
                    <div class="site-heading text-center">
                        <span class="site-title-tagline">Our Blog</span>
                        <h2 class="site-title">News &amp; <span> Blog</span></h2>
                        <div class="heading-divider"></div>
                    </div>
                </div>
            </div>
            <div class="row">
                <div class="col-md-6 col-lg-4">
                    <div class="blog-item">
                        <div class="blog-item-img">
                            <img src="${res}/app/index/image/01(3).jpg" alt="Thumb">
                        </div>
                        <div class="blog-item-info">
                            <div class="blog-item-meta">
                                <ul>
                                    <li><a href="#"><i class="far fa-user-circle"></i> By Alicia Davis</a></li>
                                    <li><a href="#"><i class="far fa-calendar-alt"></i> May 10, 2024</a></li>
                                </ul>
                            </div>
                            <h4 class="blog-title">
                                <a href="#">There are many variates of passages alteration</a>
                            </h4>
                            <p>At vero eos et accusamus et iusto odio ducimus qui blanditiis deleniti atque </p>
                            <a class="theme-btn" href="#">Read More</a>
                        </div>
                    </div>
                </div>
                <div class="col-md-6 col-lg-4">
                    <div class="blog-item">
                        <div class="blog-item-img">
                            <img src="${res}/app/index/image/02(3).jpg" alt="Thumb">
                        </div>
                        <div class="blog-item-info">
                            <div class="blog-item-meta">
                                <ul>
                                    <li><a href="#"><i class="far fa-user-circle"></i> By Alicia Davis</a></li>
                                    <li><a href="#"><i class="far fa-calendar-alt"></i> May 10, 2024</a></li>
                                </ul>
                            </div>
                            <h4 class="blog-title">
                                <a href="#">There are many variates of passages alteration</a>
                            </h4>
                            <p>At vero eos et accusamus et iusto odio ducimus qui blanditiis deleniti atque </p>
                            <a class="theme-btn" href="#">Read More</a>
                        </div>
                    </div>
                </div>
                <div class="col-md-6 col-lg-4">
                    <div class="blog-item">
                        <div class="blog-item-img">
                            <img src="${res}/app/index/image/03(2).jpg" alt="Thumb">
                        </div>
                        <div class="blog-item-info">
                            <div class="blog-item-meta">
                                <ul>
                                    <li><a href="#"><i class="far fa-user-circle"></i> By Alicia Davis</a></li>
                                    <li><a href="#"><i class="far fa-calendar-alt"></i> May 10, 2024</a></li>
                                </ul>
                            </div>
                            <h4 class="blog-title">
                                <a href="#">There are many variates of passages alteration</a>
                            </h4>
                            <p>At vero eos et accusamus et iusto odio ducimus qui blanditiis deleniti atque </p>
                            <a class="theme-btn" href="#">Read More</a>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
    <!-- blog-area end -->


    <!-- partner-area -->
    <div class="partner-area py-120">
        <div class="container">
            <div class="row">
                <div class="col-lg-6 mx-auto">
                    <div class="site-heading text-center">
                        <span class="site-title-tagline">Partners</span>
                        <h2 class="site-title">Our <span>Partners</span></h2>
                        <div class="heading-divider"></div>
                    </div>
                </div>
            </div>
            <div class="partner-wrapper">
                <div class="row justify-content-center">
                    <div class="col-md-2">
                        <div class="partner-item">
                            <img src="${res}/app/index/image/01(4).png" alt="thumb">
                        </div>
                    </div>
                    <div class="col-md-2">
                        <div class="partner-item">
                            <img src="${res}/app/index/image/02.png" alt="thumb">
                        </div>
                    </div>
                    <div class="col-md-2">
                        <div class="partner-item">
                            <img src="${res}/app/index/image/03.png" alt="thumb">
                        </div>
                    </div>
                    <div class="col-md-2">
                        <div class="partner-item">
                            <img src="${res}/app/index/image/04.png" alt="thumb">
                        </div>
                    </div>
                    <div class="col-md-2">
                        <div class="partner-item">
                            <img src="${res}/app/index/image/05.png" alt="thumb">
                        </div>
                    </div>
                    <div class="col-md-2">
                        <div class="partner-item">
                            <img src="${res}/app/index/image/06.png" alt="thumb">
                        </div>
                    </div>
                    <div class="col-md-2">
                        <div class="partner-item">
                            <img src="${res}/app/index/image/07.png" alt="thumb">
                        </div>
                    </div>
                    <div class="col-md-2">
                        <div class="partner-item">
                            <img src="${res}/app/index/image/08.png" alt="thumb">
                        </div>
                    </div>
                    <div class="col-md-2">
                        <div class="partner-item">
                            <img src="${res}/app/index/image/09.png" alt="thumb">
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
    <!-- partner-area end -->
</main>

<%@include file="common/footer.jsp" %>

<!-- scroll-top -->
<a href="#" id="scroll-top" style="display: none;"><i class="far fa-long-arrow-up"></i></a>
<!-- scroll-top end -->


<!-- js -->
<script src="${res}/app/index/js/jquery-3.6.0.min.js"></script>
<script src="${res}/app/index/js/modernizr.min.js"></script>
<script src="${res}/app/index/js/bootstrap.bundle.min.js"></script>
<script src="${res}/app/index/js/imagesloaded.pkgd.min.js"></script>
<script src="${res}/app/index/js/jquery.magnific-popup.min.js"></script>
<script src="${res}/app/index/js/isotope.pkgd.min.js"></script>
<script src="${res}/app/index/js/jquery.appear.min.js"></script>
<script src="${res}/app/index/js/jquery.easing.min.js"></script>
<script src="${res}/app/index/js/owl.carousel.min.js"></script>
<script src="${res}/app/index/js/counter-up.js"></script>
<script src="${res}/app/index/js/wow.min.js"></script>
<script src="${res}/app/index/js/contact-form.js"></script>
<script src="${res}/app/index/js/main.js"></script>



</body></html>