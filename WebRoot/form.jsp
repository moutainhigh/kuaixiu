<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ include file="/commons/taglibs.jsp"%>
<%
	String fm = request.getParameter("fm");
	if (fm != null && fm != "") {
		request.setAttribute("from_system_", fm);
	}
%>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
<meta charset="utf-8">
<meta name="viewport"
	content="width=640,target-densitydpi=320, user-scalable=no" />
<meta name="apple-mobile-web-app-capable" content="yes">
<meta name="apple-mobile-web-app-status-bar-style" content="black">
<meta name="format-detection" content="telephone=no">
<meta HTTP-EQUIV="pragma" CONTENT="no-cache">
<meta HTTP-EQUIV="Cache-Control" CONTENT="no-cache, must-revalidate">
<meta HTTP-EQUIV="expires" CONTENT="0">
<script src="${webResourceUrl}/resource/js/jquery.js"></script>
<title></title>
</head>
<body>
	
</body>
<script type="text/javascript">
	function getRealPath() {
		var pathName = location.pathname;
		var allPath = location.href;
		var pos = allPath.indexOf(pathName);
		var linkUrl = allPath.substring(0, pos);
		return linkUrl;
	}
	var linkUrl = getRealPath();
	window.onload = function() {
		window.location.href = linkUrl + "/wechat/form.html";
	}
</script>
</html>