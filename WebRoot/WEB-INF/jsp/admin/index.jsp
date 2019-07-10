<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/commons/taglibs.jsp" %>
<!doctype html>
<html class="no-js fixed-layout">
<head>
    <%@ include file="include/inc_comm_metalink.jsp" %>
    <%@ include file="include/inc_comm_script.jsp" %>
</head>

<body>
    <%-- 头部导航 --%>
    <header class="am-topbar am-topbar-inverse admin-header">
        <%@ include file="include/inc_comm_header.jsp" %>
    </header>
    <%-- 头部导航 end --%>
    
    <div class="am-cf admin-main">
        <%-- 左侧菜单 --%>
        <%@ include file="include/inc_comm_left.jsp" %>
        <%-- 左侧菜单 end --%>
        
        <%-- 主体内容 --%>
        <div id="admin-content_div" class="admin-content">
            <%-- 展示内容 --%>
            <div id="content_bady_area_0" class="admin-content-body">
                <c:if test="${loginUserType == USER_TYPE_SYSTEM }">
	                <%@ include file="index_content.jsp" %>
			    </c:if>
			    <c:if test="${loginUserType == USER_TYPE_PROVIDER }">
                    <%@ include file="index_content_provider.jsp" %>
			    </c:if>
			    <c:if test="${loginUserType == USER_TYPE_SHOP }">
	                <%@ include file="index_content_shop.jsp" %>
			    </c:if>
			    <c:if test="${loginUserType == USER_TYPE_CUSTOMER_SERVICE }">
                    <script type="text/javascript">
                        $(function () {
                            func_reload_page("${ctx}${indexUrl}");
                        });
                    </script>
                    <%--<c:if test="${loginUserId ne 'kf014' && loginUserId ne 'kf015' && loginUserId ne 'kf016' && loginUserId ne 'kf018'}">--%>
	                <%--<script type="text/javascript">--%>
	                <%--$(function () {--%>
	                	<%--func_reload_page("${ctx}/order/list.do");--%>
	                <%--});--%>
	                <%--</script>--%>
                    <%--</c:if>--%>
                    <%--<c:if test="${loginUserId eq 'kf018' }">--%>
                        <%--<script type="text/javascript">--%>
                            <%--$(function () {--%>
                                <%--func_reload_page("${ctx}/newOrder/list.do");--%>
                            <%--});--%>
                        <%--</script>--%>
                    <%--</c:if>--%>
                    <%--<c:if test="${loginUserId eq 'kf014' }">--%>
                        <%--<script type="text/javascript">--%>
                            <%--$(function () {--%>
                                <%--func_reload_page("${ctx}/telecom/card.do");--%>
                            <%--});--%>
                        <%--</script>--%>
                    <%--</c:if>--%>
                    <%--<c:if test="${loginUserId eq 'kf015' ||loginUserId eq 'kf016'}">--%>
                        <%--<script type="text/javascript">--%>
                            <%--$(function () {--%>
                                <%--func_reload_page("${ctx}/nbTelecomSJ/list.do");--%>
                            <%--});--%>
                        <%--</script>--%>
                    <%--</c:if>--%>
			    </c:if>
            </div>
            <%-- 展示内容 end --%>
            
            <%-- 底部链接 --%>
            <%@ include file="include/inc_comm_footer.jsp" %>
            <%-- 底部链接 end --%>
        </div>
        <%-- 主体内容 end --%>
    </div>
    <a href="#" class="am-icon-btn am-icon-th-list am-show-sm-only admin-menu" data-am-offcanvas="{target: '#admin-offcanvas'}"></a>
</body>
</html>
