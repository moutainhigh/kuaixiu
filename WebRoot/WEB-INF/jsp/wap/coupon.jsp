<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/commons/taglibs.jsp" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
<meta charset="utf-8">
<meta name="viewport" content="width=640,target-densitydpi=320, user-scalable=no"/> 
<meta name="apple-mobile-web-app-capable" content="yes">
<meta name="apple-mobile-web-app-status-bar-style" content="black">
<meta name="format-detection" content="telephone=no">
<link rel="stylesheet" type="text/css" href="${webResourceUrl}/resource/wap/coupon20170319/css/style.css" /> 
<title>优惠券</title>
</head>
<body class="coupon_bg">
	  <div class="coupon_content">
	  	   <!--<div class="title_icon icon_bg0"></div>类名为：icon_bg1就是iphone换电池-->
	  	   <!--<div class="title_icon icon_bg1"></div>类名为：icon_bg0就是手机换屏-->
	  	   <div class="receive">恭喜你，成功领取了<font class="red">${coupon.couponName }</font></div>
	  	   <div class="coupon_money">
	  	   		<div class="value_voucher"><span class="money">${coupon.couponPrice }</span><span class="font">&nbsp;&nbsp;</span></div>
	  	   		<div class="code">优惠码：${coupon.couponCode }</div>
	  	   		<div class="time">有效期至 ${coupon.endTime }</div>
	  	   </div>
	  	   <div class="coupon_but"><a class="but orange" href="http://m-super.com/wechat/repair/index.do">立即使用</a></div>
	  	   <div class="activity_rules">
	  	   		<div class="title">活动规则</div>
	  	   		<div class="rules_cont">
	  	   			 <p>1、每类优惠码每个手机号仅能领取有效优惠码一张</p>
	  	   			 <p>2、优惠码可与工程师返红包活动叠加</p>
	  	   			 <p>3、优惠码仅限在M超人快修公众号、M超人快修专属链接下单使用有效</p>
	  	   			 <p>4、如有其他疑问请电话联系M超人客服0571-88803875</p>
	  	   		</div>
	  	   </div>
	  </div>
	
</body>
</html>