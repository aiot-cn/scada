
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
    <title>Braintox - Machine Learning And Data Science HTML5 Template</title>

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
            <h2 class="breadcrumb-title">Login</h2>
            <ul class="breadcrumb-menu">
                <li><a href="index.html"><i class="far fa-home"></i> Home</a></li>
                <li class="active">Login</li>
            </ul>
        </div>
    </div>
    <!-- breadcrumb end -->


    <!-- login area -->
    <div class="login-area py-120">
        <div class="container">
            <div class="col-md-5 mx-auto">
                <div class="login-form">
                    <div class="login-header">
                        <h3>Login</h3>
                        <p>login with your braintox account</p>
                    </div>
                    <form action="#">
                        <div class="form-group">
                            <label>Email Address</label>
                            <input type="email" class="form-control" placeholder="Your Email">
                        </div>
                        <div class="form-group">
                            <label>Password</label>
                            <input type="password" class="form-control" placeholder="Your Password">
                        </div>
                        <div class="d-flex justify-content-between mb-4">
                            <div class="form-check">
                                <input class="form-check-input" type="checkbox" value="" id="remember">
                                <label class="form-check-label" for="remember">
                                    Remember Me
                                </label>
                            </div>
                            <a href="forgot-password.html" class="forgot-pass">Forgot Password?</a>
                        </div>
                        <div class="d-flex align-items-center">
                            <button type="submit" class="theme-btn">Login <i class="far fa-sign-in"></i></button>
                        </div>
                    </form>

                    <div class="login-footer">
                        <p>Don't have an account? <a href="register.html">Register.</a></p>
                    </div>
                </div>
            </div>
        </div>
    </div>
    <!-- login area end -->


</main>


<%@include file="common/footer.jsp" %>

</body>

</html>