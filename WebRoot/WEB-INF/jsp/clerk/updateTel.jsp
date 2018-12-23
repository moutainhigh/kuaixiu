<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/commons/taglibs.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta charset="utf-8">
<meta name="viewport" content="width=720,target-densitydpi=320, user-scalable=no"/> 
<meta name="apple-mobile-web-app-capable" content="yes">
<meta name="apple-mobile-web-app-status-bar-style" content="black">
<meta name="format-detection" content="telephone=no">
<%@ include file="/commons/configuration.jsp" %>
<script src="${webResourceUrl}/resource/wap/clerk/js/updateTel.js"></script>  
<title>修改手机号</title>
<c:set var="tel" value="${tel}"></c:set>
<script type="text/javascript">
  var tel="${tel}";
</script>
<style type="text/css">
div{
  font-size:30px;
}
input{
  width:300px;
  height:30px;
}
</style>
</head>
<body>
   <h1 align="center">修改手机号</h1>
   <div>
    <p>旧手机号:${tel}</p>
    <p>新手机号:<input type="text" maxlength="11" id="newTel"/></p>
      <div class="login_input">
  	  	  <p>验证码:<input class="text code_text verification" type="tel" id="code" value="" maxlength="6" placeholder="请输入验证码">
  	  	           <input class="get_validate" type="button" value="获取短信验证码" id="getCode" data-verify="发送短信" onclick="sendMsg(this)">
  	       </p>
  	  </div>
     <input class="but3 submit" type="submit" id="news" value="确定"  />
   </div>
</body>
</html>