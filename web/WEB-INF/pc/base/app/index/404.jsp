<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">

<head>
    <!-- meta tags -->
    <meta charset="UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="description" content="">
    <meta name="keywords" content="">

    <!-- title -->
    <title>页面未找到</title>

    <!-- favicon -->
    <link rel="icon" type="image/x-icon" href="${res}/app/index/image/favicon.png">

    <!-- css -->
    <link rel="stylesheet" href="${res}/app/index/css/bootstrap.min.css">
    <link rel="stylesheet" href="${res}/app/index/css/all-fontawesome.min.css">
    <link rel="stylesheet" href="${res}/app/index/css/flaticon.css">
    <link rel="stylesheet" href="${res}/app/index/css/animate.min.css">
    <link rel="stylesheet" href="${res}/app/index/css/magnific-popup.min.css">
    <link rel="stylesheet" href="${res}/app/index/css/owl.carousel.min.css">
    <link rel="stylesheet" href="${res}/app/index/css/style.css">

</head>

<body>

<%@include file="common/header.jsp" %>

<main class="main">

    <!-- breadcrumb -->
    <div class="site-breadcrumb">
        <div class="container">
            <h2 class="breadcrumb-title">404 错误</h2>
            <ul class="breadcrumb-menu">
                <li><a href="${base}/base/app/index/index"><i class="far fa-home"></i> 首页</a></li>
                <li class="active">404 错误</li>
            </ul>
        </div>
    </div>
    <!-- breadcrumb end -->


    <!-- error area -->
    <div class="error-area py-120">
        <div class="container">
            <div class="col-md-6 mx-auto">
                <div class="error-wrapper">
                    <div class="error-img">
                        <img src="${res}/app/index/image/404.png" alt="">
                    </div>
                    <h2>页面不存在</h2>
                    <p>The page you looking for not found may be it not exist or removed.</p>
                    <a href="index.html" class="theme-btn">回到首页 <i class="far fa-home"></i></a>
                </div>
            </div>
        </div>
    </div>
    <!-- error area end -->


</main>

<%@include file="common/footer.jsp" %>

</body>

</html>