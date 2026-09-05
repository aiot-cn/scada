
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
            <h2 class="breadcrumb-title">Forgot Password</h2>
            <ul class="breadcrumb-menu">
                <li><a href="index.html"><i class="far fa-home"></i> Home</a></li>
                <li class="active">Forgot Password</li>
            </ul>
        </div>
    </div>
    <!-- breadcrumb end -->



    <!-- forgot password -->
    <div class="login-area py-120">
        <div class="container">
            <div class="col-md-5 mx-auto">
                <div class="login-form">
                    <div class="login-header">
                        <h3>Forgot Password</h3>
                        <p>reset your braintox account</p>
                    </div>
                    <form action="#">
                        <div class="form-group">
                            <label>Email Address</label>
                            <input type="email" class="form-control" placeholder="Your Email">
                        </div>
                        <div class="d-flex align-items-center">
                            <button type="submit" class="theme-btn">Send Reset
                                Link <i class="far fa-key"></i></button>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    </div>
    <!-- forgot password end -->



</main>


<%@include file="common/footer.jsp" %>

</body>

</html>