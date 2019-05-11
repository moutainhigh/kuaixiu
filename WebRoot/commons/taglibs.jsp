<%@page pageEncoding="UTF-8"%>
<%@page import="com.system.constant.SystemConstant" %> 
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<c:set var="webResourceUrl" value="${pageContext.request.contextPath}"/>


<c:set var="commonurl_home" value="${ctx}/admin/index.do" />

<c:set var="commonurl_login" value="${ctx}/admin/login.do" />

<c:set var="commonurl_logout" value="${ctx}/admin/logout.do" />


<c:set var="commonurl_sj_home" value="${ctx}/sj/admin/index.do" />

<c:set var="commonurl_sj_logout" value="${ctx}/sj/admin/logout.do" />

<c:set var="pc_home" value="${ctx}/index.do" />


<c:set var="USER_TYPE_SUPPER" value="<%=SystemConstant.USER_TYPE_SUPPER %>" />
<c:set var="USER_TYPE_SYSTEM" value="<%=SystemConstant.USER_TYPE_SYSTEM %>" />
<c:set var="USER_TYPE_PROVIDER" value="<%=SystemConstant.USER_TYPE_PROVIDER %>" />
<c:set var="USER_TYPE_SHOP" value="<%=SystemConstant.USER_TYPE_SHOP %>" />
<c:set var="USER_TYPE_ENGINEER" value="<%=SystemConstant.USER_TYPE_ENGINEER %>" />
<c:set var="USER_TYPE_CUSTOMER" value="<%=SystemConstant.USER_TYPE_CUSTOMER %>" />
<c:set var="USER_TYPE_CUSTOMER_SERVICE" value="<%=SystemConstant.USER_TYPE_CUSTOMER_SERVICE %>" />
