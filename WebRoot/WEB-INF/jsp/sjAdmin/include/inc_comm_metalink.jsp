<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1" />
<c:if test="${loginUserType == 1}">
    <title>宁波商机单 -- 后台管理系统</title>
</c:if>
<c:if test="${loginUserType == 3}">
    <title>宁波商机单 -- 施工单位版</title>
</c:if>
<c:if test="${loginUserType == 8}">
    <title>宁波商机单 -- 施工员工版</title>
</c:if>
<meta name="viewport" content="width=device-width, initial-scale=1">

<!-- bootstrap & fontawesome -->
<link rel="stylesheet" href="${webResourceUrl}/plugins/bootstrapvalidator/dist/css/bootstrapValidator.css"/>
<link rel="stylesheet" href="${webResourceUrl}/plugins/assets/css/amazeui.min.css"/>
<link rel="stylesheet" href="${webResourceUrl}/plugins/assets/css/admin.css">

<link rel="stylesheet" href="${webResourceUrl}/plugins/bootstrap/css/bootstrap.css"/>
<link rel="stylesheet" href="${webResourceUrl}/plugins/dataTables/css/dataTables.bootstrap.css">
<link rel="stylesheet" href="${webResourceUrl}/plugins/datetimepicker/amazeui.datetimepicker.css"/>
<link rel="stylesheet" href="${webResourceUrl}/plugins/daterangepicker/daterangepicker.css" type="text/css">


<link rel="stylesheet" href="${webResourceUrl}/resource/css/amazeui.costomer.css?2017">
<link rel="stylesheet" href="${webResourceUrl}/resource/pagination/css/pagination.style1.css?12">
<link rel="stylesheet" href="${webResourceUrl}/plugins/alert/alerta.css" type="text/css">

<link rel="stylesheet" href="${webResourceUrl}/resource/css/common.css">

