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
<meta HTTP-EQUIV="Pragma" CONTENT="no-cache"> 
<meta HTTP-EQUIV="Cache-Control" CONTENT="no-cache, must-revalidate"> 
<meta HTTP-EQUIV="Expires" CONTENT="0">
<script src="${webResourceUrl}/resource/wap/oldToNew/js/jQuery 1.7.1.js"></script>
<script type="text/javascript" src="http://int.dpool.sina.com.cn/iplookup/iplookup.php?
format=js"></script>
<title></title>
</head>
<body>

</body>
<script type="text/javascript">
function getRealPath(){
	var pathName=location.pathname;  
	var allPath=location.href;
	var pos=allPath.indexOf(pathName);
	var linkUrl=allPath.substring(0,pos);
	return linkUrl;
}
var linkUrl=getRealPath();
window.onload = function(){
	window.location.href =linkUrl+"/wechat/oldToNew.html";
}
</script>
</html>