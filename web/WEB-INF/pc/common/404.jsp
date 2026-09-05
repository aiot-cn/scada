<%@ page contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>

<!DOCTYPE html>
<html>
<head>
  <meta charset="utf-8">
  <title>aito - 页面不存在</title>
  <meta name="renderer" content="webkit">
  <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
  <meta name="viewport" content="width=device-width, initial-scale=1.0, minimum-scale=1.0, maximum-scale=1.0, user-scalable=0">

</head>
<style>
  html, body {
    height: 100%;
    margin: 0;
    padding: 0;
  }
  body {
    display: table;
    width: 100%;
  }
  .wrap {
    display: table-cell;
    vertical-align: middle;
    text-align: center;
  }
  .back-home {
    display: inline-block;
    margin-top: 20px;
    color: #1890ff;
    text-decoration: none;
    font-size: 16px;
  }
  .back-home:hover {
    color: #40a9ff;
  }
</style>
</head>
<body>
<div class="wrap">
  <img src="${res}/images/404.png">
  <div><a class="back-home" href="${base}">回到首页</a></div>
</div>
</body>
</html>