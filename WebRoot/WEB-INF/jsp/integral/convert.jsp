<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/commons/taglibs.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta name="viewport" content="width=640,target-densitydpi=320, user-scalable=no"/> 
<meta name="apple-mobile-web-app-capable" content="yes">
<meta name="apple-mobile-web-app-status-bar-style" content="black">
<meta name="format-detection" content="telephone=no">
<%@ include file="/commons/configuration.jsp" %>
<link rel="stylesheet" type="text/css" href="${webResourceUrl}/resource/wap/clerk/css/style.css" />
<script src="${webResourceUrl}/resource/wap/integral/js/convert.js"></script>  
<title>积分兑换</title>
<c:set var="integral" value="${integral}"/>
<script type="text/javascript">
var integral="${integral}"
</script>
</head>
<body>
 <div class="body_cont">
	<div class="top_head">
		 <!--<a class="return" href="javascript:;" onclick="history.go(-1);"></a>-->
		  积分兑换
	</div>
	
	<div class="index_bottom">
		 <ul class="bottom_list">
		 	 <li><a href="${webResourceUrl}/wap/clerk/index.do"><i class="icon1"></i>主页</a></li>
		 	 <li class="bottom_in"><a href="${webResourceUrl}/wap/integral/convert.do"><i class="icon2"></i>积分</a></li>
		 	 <li><a href="${webResourceUrl}/wap/clerk/myNews.do"><i class="icon3"></i>我的</a></li>
		 </ul>
	</div>
	
	<div class="new_order">
		 <div class="integral_cont">
		 	  <div class="name">${name}</div>
		 	  <div class="font">剩余积分</div>
		 	  <div class="integral">${integral}</div>
		 	  <div class="record_cont">
		 	  	   <div class="record"><a href="${webResourceUrl}/wap/integral/convertRecord.do">积分兑换记录</a></div>
		 	  	   <div class="record"><a href="${webResourceUrl}/wap/integral/getRecord.do">积分获得记录</a></div>
		 	  </div>
		 </div>
		 
		 <div class="new_input exchange_input pdlr40">
		 	  <div class="fonts">兑换积分：</div>
		 	  <div class="input_text">
		 	  	<input class="text" type="number" value="" id="input" placeholder="1积分=1元" />
		 	  </div>
		 </div>
		 
		 <div class="index_but">
		    <a class="butgreen" id="news" href="javascript:;">提交兑换申请</a>
		 </div>
		 
		 <div class="declaration">
		 	<span>温馨提示：</span>
		 	<p>1、积分提现规则为1积分=1元；</p>
            <p>2、提交兑换申请后客服审核通过后会在1-2个工作日内打款；</p>
            <p>3、提现金额会打入当前微信，依据微信转账规则，微信账户需要实制。</p>
            <p>4、如有其他疑问请咨询客服（<a href="tel:057188803875">0571-88803875</a>）。</p>
		 </div>
	</div>
</div>
	
<script>
$(function(){
	$(".body_cont").height($(window).height());
	//底部菜单
	$('.index_bottom .bottom_list li').click(function(){
		$(this).siblings().removeClass('bottom_in');
		$(this).addClass('bottom_in');
	});
});



</script>
</body>
</html>