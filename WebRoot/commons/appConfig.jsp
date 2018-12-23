<%@page import="com.system.constant.SystemConstant"%>
<%@page pageEncoding="UTF-8"%>
<meta HTTP-EQUIV="Pragma" CONTENT="no-cache"> 
<meta HTTP-EQUIV="Cache-Control" CONTENT="no-cache"> 
<meta HTTP-EQUIV="Expires" CONTENT="0">
<script type="text/javascript">

    /**
     * 配置公共参数
     * @returns {AppConfig}
     */
    function AppConfig() {
    }
    AppConfig.ctx = "${ctx}";
    AppConfig.webResourceUrl = "${webResourceUrl}";
    AppConfig.appId = "<%=SystemConstant.APP_ID%>";
    AppConfig.domain = "<%=SystemConstant.WECHAT_PAY_DOMAIN%>";

</script>