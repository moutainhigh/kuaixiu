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
<title>修改信息</title>

</head>
<body>
<div class="body_cont">
	<div class="top_head">
		 <!--<a class="return" href="javascript:;" onclick="history.go(-1);"></a>-->
		  我的信息
	</div>
	
	<div class="index_bottom">
		 <ul class="bottom_list">
		 	 <li><a href="${webResourceUrl}/wap/clerk/index.do"><i class="icon1"></i>主页</a></li>
		 	 <li><a href="${webResourceUrl}/wap/integral/convert.do"><i class="icon2"></i>积分</a></li>
		 	 <li class="bottom_in"><a href="${webResourceUrl}/wap/clerk/myNews.do"><i class="icon3"></i>我的</a></li>
		 </ul>
	</div>
	
	<div class="new_order graybg">
		 <div class="order_cont">
		 	  <div class="record_list new_input">
		 	  	   <div class="half_cont">
			 	  		<p class="time">姓名：</p>
			 	   </div>
			 	   <div class="half_cont txtright">
			 	  		<p class="audit_state"> ${name}</p>
			 	   </div>
		 	  </div>
		 	  <div class="record_list new_input">
		 	  	   <div class="half_cont">
			 	  		<p class="time">手机号：</p>
			 	   </div>
			 	   <div class="half_cont txtright">
			 	  		<p class="audit_state">${tel}</p>
			 	   </div>
		 	  </div>
		 	  <div class="record_list new_input" onclick="window.location.href='${webResourceUrl}/wap/clerk/updateIdentityCard.do' ">
		 	  	   <div class="half_cont">
			 	  		<p class="time">身份证号码：${identityCard}</p>
			 	   </div>
			 	   <div class="half_cont txtright">
			 	  		<p class="audit_state">修改></p>
			 	   </div>
		 	  </div>
		 	  <div class="record_list new_input" onclick="window.location.href='${webResourceUrl}/wap/clerk/updatePassword.do' ">
		 	  	   <div class="half_cont">
			 	  		<p class="time">登录密码：</p>
			 	   </div>
			 	   <div class="half_cont txtright">
			 	  		<p class="audit_state">修改></p>
			 	   </div>
		 	  </div>
		 	  <div class="record_list new_input">
		 	  	   <div class="half_cont">
			 	  		<p class="time">地址：${address}</p>
			 	   </div>
		 	  </div> 
		 </div>	 
		 
		 <div class="index_but">
	 	     <a class="buthollow" id="logout" href="javascript:;">退出账号</a>
	  	 </div>
	</div>

</body>

<script type="text/javascript">
$(function(){
	$(".body_cont").height($(window).height());
	 $("#logout").click(function(){
		 confirmTip("系统提示", "确定要退出吗？", function(){
				window.location.href=AppConfig.ctx+"/wap/clerk/logout.do";  
			});
	 });
});

</script>
</html>