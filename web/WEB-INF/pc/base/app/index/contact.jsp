
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
            <h2 class="breadcrumb-title">Contact Us</h2>
            <ul class="breadcrumb-menu">
                <li><a href="index.html"><i class="far fa-home"></i> Home</a></li>
                <li class="active">Contact Us</li>
            </ul>
        </div>
    </div>
    <!-- breadcrumb end -->



    <!-- contact area -->
    <div class="contact-area py-120">
        <div class="container">
            <div class="contact-wrapper">
                <div class="row">
                    <div class="col-md-4">
                        <div class="contact-content">
                            <div class="contact-info">
                                <div class="contact-info-icon">
                                    <i class="fal fa-map-marker-alt"></i>
                                </div>
                                <div class="contact-info-content">
                                    <h5>Office Address</h5>
                                    <p>25/B Milford, New York, USA</p>
                                </div>
                            </div>
                            <div class="contact-info">
                                <div class="contact-info-icon">
                                    <i class="fal fa-phone"></i>
                                </div>
                                <div class="contact-info-content">
                                    <h5>Call Us</h5>
                                    <p>+2 123 654 7898</p>
                                </div>
                            </div>
                            <div class="contact-info">
                                <div class="contact-info-icon">
                                    <i class="fal fa-envelope"></i>
                                </div>
                                <div class="contact-info-content">
                                    <h5>Email Us</h5>
                                    <p>info@example.com</p>
                                </div>
                            </div>
                            <div class="contact-info">
                                <div class="contact-info-icon">
                                    <i class="fal fa-clock"></i>
                                </div>
                                <div class="contact-info-content">
                                    <h5>Office Open</h5>
                                    <p>Sun - Fri (08AM - 10PM)</p>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="col-md-8 align-self-center">
                        <div class="contact-form">
                            <div class="contact-form-header">
                                <h2>Get In Touch</h2>
                                <p>It is a long established fact that a reader will be distracted by the readable
                                    content of a page when looking at its layout. </p>
                            </div>
                            <form method="post" action="/braintox/assets/php/contact.php" id="contact-form">
                                <div class="row">
                                    <div class="col-md-6">
                                        <div class="form-group">
                                            <input type="text" class="form-control" name="name"
                                                   placeholder="Your Name" required>
                                        </div>
                                    </div>
                                    <div class="col-md-6">
                                        <div class="form-group">
                                            <input type="email" class="form-control" name="email"
                                                   placeholder="Your Email" required>
                                        </div>
                                    </div>
                                </div>
                                <div class="form-group">
                                    <input type="text" class="form-control" name="subject"
                                           placeholder="Your Subject" required>
                                </div>
                                <div class="form-group">
                                        <textarea name="message" cols="30" rows="5" class="form-control"
                                                  placeholder="Write Your Message"></textarea>
                                </div>
                                <button type="submit" class="theme-btn">Send
                                    Message <i class="far fa-paper-plane"></i></button>
                                <div class="col-md-12 mt-3">
                                    <div class="form-messege text-success"></div>
                                </div>
                            </form>

                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
    <!-- end contact area -->

    <!-- contact map -->
    <div class="contact-map">
        <iframe
                src="https://www.amap.com/" sandbox=""
                style="border:0;" allowfullscreen="" loading="lazy"></iframe>
    </div>


</main>

<%@include file="common/footer.jsp" %>
</body>

</html>