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
<script src="${webResourceUrl}/resource/wap/clerk/js/register.js"></script>  
<title>注册</title>
</head>
<body>
  <div class="body_cont">
	<div class="top_head">
		 <a class="return" href="javascript:;" onclick="history.go(-1);"></a>
		  注册
	</div>
	<div class="new_order pdlr25">
		 <div class="new_input">
		 	  <div class="fonts">姓名：</div>
		 	  <div class="input_text">
		 	  	<input class="text" type="text" maxlength="8" id="name" />
		 	  </div>
		 </div>
		<div class="new_input">手机号：
		    <div class="input_text"><input class="text" id="inputtel"  type="number" oninput="if(value.length>11)value=value.slice(0,11)"  /></div>
  	  	 </div>
  	  	 <div class="new_input">验证码：
		    <div class="input_text" > <input class="text" type="text" id="code" value="" maxlength="6"/>
  	  	   	   <input class="get_validate" type="button" style="float:right" id="getCode" value="获取验证码" data-verify="发送短信" onclick="sendMsg(this)" />
			</div>
  	  	 </div>
  	  	<div class="new_input">微信号：
		    <div class="input_text"><input class="text" id="wechatId" type="text"  maxlength="32" /></div>
  	  	 </div>
				 <div class="new_input">
				 微信是否实名制:&nbsp&nbsp&nbsp&nbsp
				 	  <div class="half_cont">
				 	  	   <label class="label"><input class="radio"  type="radio" name="wheth" value="1"><span>是</span></label>
				 	  </div>
				 	  <div class="half_cont">
				 	  	    <label class="label"><input class="radio"  type="radio" name="wheth" value="2"><span>否</span></label>
				 	  </div>
				 </div>
		  
		<div class="new_input">身份证号码：
		    <div class="input_text"><input class="text" id="identityCard" type="text"  maxlength="18" /></div>
  	  	 </div>
		 <div class="new_input">登录密码：
		    <div class="input_text"><input class="text" id="password" type="password" maxlength="18" placeholder="密码长度为6-18个字符"/></div>
  	  	 </div>
		 <div class="new_input">确认密码：
		    <div class="input_text"><input class="text" id="repassword" type="password" maxlength="18"  /></div>
  	  	 </div>
		 	  
		
		
		 <div class="new_input">
		 	  <div class="fonts">地址：</div>
		 	  <div class="input_text">
		 	  		<ul class="select_addr">
  	   			 	 <li> <select  id="s_Province" name="s_Province" onchange="fn_select_address(2, this.value, '', 's_');"><option value="">--省份--</option></select></li>
                     <li> <select  id="s_City" name="s_City" onchange="fn_select_address(3, this.value, '', 's_');"><option value="">--地级市--</option></select></li>
                     <li> <select  id="s_County" name="s_County" onchange="fn_select_address(4, this.value, '', 's_');"><option value="">--区/县--</option></select></li>
  	   			   </ul>
		 	  </div>
		 </div>
		 <div class="new_input">
		 	  <div class="input_text">
		 	  		<input class="text" type="text" id="street" maxlength="16" placeholder="街道/小区/楼号等" />
		 	  </div>
		 </div>
		
		 
		 <div class="index_but">
		   <input class="but" type="submit" id="news" value="确认提交" />
		 </div>
		 
		 
	</div>
</div>
</body>



</html>