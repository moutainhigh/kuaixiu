<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/commons/taglibs.jsp" %>
<link rel="stylesheet" href="${webResourceUrl}/resource/order/css/order.detail.css">
<div class="am-cf am-padding am-padding-bottom-0">
    <div class="am-fl am-cf" style="width: 100%;">
        <strong class="am-text-primary am-text-lg"><a href="javascript:void(0);" onclick="toList();">检测计费管理</a></strong>
        /
        <small>检测详情</small>
        <strong class="am-text-primary"><a href="javascript:void(0);" onclick="func_to_back();">返回</a></strong>
    </div>
</div>

<hr>

<div class="am-g">
    <div class="panel panel-success index-panel-msg">
        <h4>订单状态：
            <c:choose>
                <c:when test="${detail.isOrder == 0}">否</c:when>
                <c:when test="${detail.isOrder == 1}">是</c:when>
                <c:otherwise>
                    订单已生成
                </c:otherwise>
            </c:choose>
        </h4>
    </div><!-- /panel -->
</div>
<!-- /am-g -->

<div class="am-g order-info mt20">
    <table class="detail-table">
        <tr>
            <td class="td-title">
                <h4>检测信息：</h4>
            </td>
            <td class="td-space"></td>
            <td class="td-info">
                <div class="row">
                    <div class="col-md-6 col-sm-6 col-xs-12">
                        <h4>手机号：${testDetail.loginMobile }</h4>
                    </div><!-- /.col -->
                    <div class="col-md-6 col-sm-6 col-xs-12">
                        <h4>登录时间：<fmt:formatDate value="${testDetail.loginTime }" pattern="yyyy-MM-dd HH:mm:ss"/></h4>
                    </div><!-- /.col -->
                </div><!-- /.row -->

                <div class="row">
                    <div class="col-md-6 col-sm-6 col-xs-12">
                        <h4>手机品牌：${testDetail.brandName }</h4>
                    </div><!-- /.col -->
                    <div class="col-md-6 col-sm-6 col-xs-12">
                        <h4>手机机型：${testDetail.modelName }</h4>
                    </div><!-- /.col -->
                </div><!-- /.row -->


                <div class="row">
                    <div class="col-md-6 col-sm-6 col-xs-12">
                        <h4>预估金额：${testDetail.testPrice }</h4>
                    </div><!-- /.col -->
                    <div class="col-md-6 col-sm-6 col-xs-12">
                        <h4>检测时间：<fmt:formatDate value="${testDetail.createTime }" pattern="yyyy-MM-dd HH:mm:ss"/></h4>
                    </div><!-- /.col -->
                </div><!-- /.row -->

                <div class="row">
                    <div class="col-md-12 col-sm-12 col-xs-12">
                        <h4>来源：${source}</h4>
                    </div><!-- /.col -->
                    <div class="col-md-12 col-sm-12 col-xs-12">
                        <h4>检测详情：${testDetail.detail}</h4>
                    </div><!-- /.col -->
                </div>

        <tr>
            <td colspan="3" class="tr-space"></td>
        </tr>
        <tr>
            <td class="td-title">
                <h4>订单信息：</h4>
            </td>
            <td class="td-space"></td>
            <td class="td-info">
                <div class="row">
                    <div class="col-md-6 col-sm-6 col-xs-12">
                        <h4>用户姓名：${submits.name }</h4>
                    </div><!-- /.col -->
                    <div class="col-md-6 col-sm-6 col-xs-12">
                        <h4>电话：${submits.mobile}</h4>
                    </div><!-- /.col -->
                </div><!-- /.row -->

                <div class="row">
                    <div class="col-md-12 col-sm-12 col-xs-12">
                        <h4>用户地址：${submits.area}&nbsp ${submits.street} </h4>
                    </div><!-- /.col -->
                </div><!-- /.row -->
                <div class="row">
                    <div class="col-md-12 col-sm-12 col-xs-12">
                        <h4>用户备注：${submits.note} </h4>
                    </div><!-- /.col -->
                </div><!-- /.row -->
                <div class="row">
                    <div class="col-md-6 col-sm-6 col-xs-12">
                        <h4>提交时间：<fmt:formatDate value="${submits.createTime }" pattern="yyyy-MM-dd HH:mm:ss"/></h4>
                    </div><!-- /.col -->
                </div><!-- /.row -->
            </td>
        </tr>

        <tr>
            <td colspan="3" class="tr-space"></td>
        </tr>
        <tr>

    </table>
</div>
<!-- /am-g -->


<script type="text/javascript">
    function toList() {
        func_reload_page("${ctx}/newOrder/list.do");
    }

</script>