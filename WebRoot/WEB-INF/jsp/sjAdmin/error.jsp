<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/commons/taglibs.jsp" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <link rel="stylesheet" href="${webResourceUrl}/plugins/alert/alerta.css" type="text/css">
    
	<!--[if (gte IE 9)|!(IE)]><!-->
	<script src="${webResourceUrl}/plugins/assets/js/jquery.min.js"></script>
	<!--<![endif]-->
	<!--[if lte IE 8 ]>
	<script src="${webResourceUrl}/plugins/jquery/jquery-1.11.3.js"></script>
	<script src="http://cdn.staticfile.org/modernizr/2.8.3/modernizr.js"></script>
	<script src="${webResourceUrl}/plugins/assets/js/amazeui.ie8polyfill.min.js"></script>
	<![endif]-->
	
	<script src="${webResourceUrl}/plugins/alert/alerta.js" type="text/javascript"></script>
	<%@ include file="/commons/appConfig.jsp" %>

	<script type="text/javascript">
		$(document).ready(function() {
			var exceptionType = '${exceptJson.exceptionType }';
			var errorExceptionMsg = '${exceptJson.errorExceptionMsg }';

			AlertText.tips("d_alert", "系统提示", errorExceptionMsg, function(){
				if (exceptionType == "sessionError") {
					window.location.href = AppConfig.ctx + "/sj/admin/logout.do";
				}
			});
		});


	</script>

</head>

<body class="no-skin">
    <%-- 遮盖层 --%>
	<div id="mask_boxs">
	    <div id="masks"></div>
	    <div id="mcon"></div>
	</div>

</body>
</html>
