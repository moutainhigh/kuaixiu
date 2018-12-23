<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/commons/taglibs.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta charset="utf-8">
<meta name="viewport" content="width=640,target-densitydpi=320, user-scalable=no"/> 
<meta name="apple-mobile-web-app-capable" content="yes">
<meta name="apple-mobile-web-app-status-bar-style" content="black">
<meta name="format-detection" content="telephone=no">
<%@ include file="/commons/configuration.jsp" %>
<link rel="stylesheet" type="text/css" href="${webResourceUrl}/resource/wap/clerk/css/style.css" />
<script src="${webResourceUrl}/resource/wap/clerk/js/login.js"></script>  
<title>店员登录</title>
</head>
<body>
    
   
    
    <div class="index_content ">
		 <div class="login_font">超人快修店员平台</div>
		 <div class="login_login">
  	  	   		<div class="login_input"><label>
  	  	   			  账号：<input class="text" id="inputtel" type="text"  maxlength="11" placeholder="请输入登录帐号" /></label>
  	  	   		</div>
  	  	   		<div class="login_input"><label>
  	  	   			  密码：<input class="text " type="password" id="password" placeholder="请输入密码" /></label>
  	  	   		</div>
  	  	   		
  	  	   		<div class="login_submit">
  	  	   		
  	  	   		<p style="position: absolute; right: 30px;top: 30px;"><a href="${webResourceUrl}/wap/clerk/Instructions.do" >
  	  	   		   <span style="color:#439AFF;" >操作说明</span></a>
                 </p>
  	  	   			  <input class="login_but" type="submit" id="news" value="登    录" />
  	  	   		</div>
  	  	   		
  	  	   		<div class="new_input service_cont">
				 	  <div class="half_cont"><a class="blue" href="${webResourceUrl}/wap/clerk/forgot.do">忘记密码？</a></div>
				 	  <div class="half_cont txtright"><a class="blue" href="${webResourceUrl}/wap/clerk/register.do">新用户注册</a></div>
				</div>
  	  	 </div>
	</div>
</body>
</html>