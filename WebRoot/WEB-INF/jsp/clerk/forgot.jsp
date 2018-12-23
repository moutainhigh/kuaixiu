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
<script src="${webResourceUrl}/resource/wap/clerk/js/forgot.js"></script>  
<title>忘记密码</title>
</head>
<body>
  <div class="body_cont">
	<div class="top_head">
		 <a class="return" href="javascript:;" onclick="history.go(-1);"></a>
		  忘记密码
	</div>
	<div class="new_order pdlr25">
		 <div class="new_input">
		 	  <div class="fonts">姓名：</div>
		 	  <div class="input_text">
		 	  	<input class="text" type="text" id="name" />
		 	  </div>
		 </div>
		<div class="new_input">手机号：
		    <div class="input_text"><input class="text" id="tel" type="number" value="" oninput="if(value.length>11)value=value.slice(0,11)" /></div>
  	  	 </div>
		<div class="new_input">身份证号码：
		    <div class="input_text"><input class="text" id="identityCard" type="text" value="" maxlength="18" /></div>
  	  	 </div>
		
		 
		 <div class="index_but">
		 	 <a class="but" href="javascript:;" id="news">确认提交</a>
		 </div>
		 
		 
	</div>
</div>
</body>
</html>