<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/commons/taglibs.jsp" %>

<div class="am-topbar-brand">
    <strong>欢迎登陆M-超人</strong> -- 
    <c:if test="${loginUserType == USER_TYPE_SYSTEM }">
        <small>后台管理系统</small>
    </c:if>
    <c:if test="${loginUserType == USER_TYPE_PROVIDER }">
        <small>连锁商版</small>
    </c:if>
    <c:if test="${loginUserType == USER_TYPE_SHOP }">
        <small>门店商版</small>
    </c:if>
    <c:if test="${loginUserType == USER_TYPE_CUSTOMER_SERVICE }">
        <small>客服管理</small>
    </c:if>
</div>

<button class="am-topbar-btn am-topbar-toggle am-btn am-btn-sm am-btn-success am-show-sm-only" data-am-collapse="{target: '#topbar-collapse'}"><span class="am-sr-only">导航切换</span> <span class="am-icon-bars"></span></button>

<div class="am-collapse am-topbar-collapse" id="topbar-collapse">
	<ul class="am-nav am-nav-pills am-topbar-nav am-topbar-right admin-header-list">
		<li>
            <span class="am-icon-user"></span> <span style="line-height: 50px; margin-right: 10px;"> &nbsp; ${loginUserName } </span>
		</li>
		<li class="am-hide-sm-only">
		  <a href="${commonurl_sj_logout }" id="admin-fullscreen">
		    <span class="am-icon-sign-out"></span> <span class="admin-fullText">退出</span>
		  </a>
	    </li>
	</ul>
</div>
