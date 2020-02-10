<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/commons/taglibs.jsp" %>
<link rel="stylesheet" href="${webResourceUrl}/resource/order/css/order.detail.css">
<div class="am-cf am-padding am-padding-bottom-0">
    <div class="am-fl am-cf" style="width: 100%;">
        <strong class="am-text-primary am-text-lg"><a href="javascript:void(0);" onclick="toList();">回收订单管理</a></strong>
        /
        <small>订单详情</small>
        <strong class="am-text-primary"><a href="javascript:void(0);" onclick="func_to_back();">返回</a></strong>
    </div>
</div>

<hr>

<div class="am-g">
    <div class="panel panel-success index-panel-msg">
        <h4>订单状态：
            <c:choose>
                <c:when test="${order.orderStatus == 0}">取消订单</c:when>
                <c:when test="${order.orderStatus == 1}">创建订单</c:when>
                <c:when test="${order.orderStatus == 2}">待客户发件</c:when>
                <c:when test="${order.orderStatus == 3}">已发货待收件</c:when>
                <c:when test="${order.orderStatus == 4}">门店收件</c:when>
                <c:when test="${order.orderStatus == 5}">提交质检报告</c:when>
                <c:when test="${order.orderStatus == 6}">需议价订单</c:when>
                <c:when test="${order.orderStatus == 7}">议价结束</c:when>
                <c:when test="${order.orderStatus == 8}">待支付订单</c:when>
                <c:when test="${order.orderStatus == 9}">支付完成（结束）</c:when>
                <c:when test="${order.orderStatus == 10}">预支付转账成功</c:when>
                <c:when test="${order.orderStatus == 11}">预支付转账失败</c:when>
                <c:when test="${order.orderStatus == 12}">支付尾款失败</c:when>
                <c:when test="${order.orderStatus == 13}">扣款失败</c:when>
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
                <h4>订单信息：</h4>
            </td>
            <td class="td-space"></td>
            <td class="td-info">
                <div class="row">
                    <div class="col-md-6 col-sm-6 col-xs-12">
                        <h4>订单号：${order.orderNo }</h4>
                    </div><!-- /.col -->
                    <div class="col-md-6 col-sm-6 col-xs-12">
                        <h4>创建时间：<fmt:formatDate value="${order.inTime }" pattern="yyyy-MM-dd HH:mm:ss"/></h4>
                    </div><!-- /.col -->
                </div><!-- /.row -->

                <div class="row">
                    <div class="col-md-6 col-sm-6 col-xs-12">
                        <h4>姓名：${cust.name }</h4>
                    </div><!-- /.col -->
                    <div class="col-md-6 col-sm-6 col-xs-12">
                        <h4>下单人：${order.phone }</h4>
                    </div><!-- /.col -->
                    <div class="col-md-6 col-sm-6 col-xs-12">
                        <h4>寄件人：${order.mobile }</h4>
                    </div><!-- /.col -->
                </div><!-- /.row -->


                <div class="row">
                    <div class="col-md-6 col-sm-6 col-xs-12">
                        <h4>回收类型：
                            <c:choose>
                                <c:when test="${order.recycleType == 0}">信用回收</c:when>
                                <c:when test="${order.recycleType == 1}">普通回收</c:when>
                            </c:choose>
                        </h4>
                    </div><!-- /.col -->
                    <div class="col-md-6 col-sm-6 col-xs-12">
                        <h4>支付类型：
                            <c:choose>
                                <c:when test="${order.exchangeType == 1}">支付宝收款</c:when>
                                <c:when test="${order.exchangeType == 2}">话费充值</c:when>
                            </c:choose>
                        </h4>
                    </div><!-- /.col -->
                </div><!-- /.row -->

                <div class="row">
                    <div class="col-md-6 col-sm-6 col-xs-12">
                        <h4>
                            <c:choose>
                                <c:when test="${order.exchangeType == 1}">支付宝账号：${order.payMobile}</c:when>
                                <c:when test="${order.exchangeType == 2}">充值手机号：${order.payMobile}</c:when>
                            </c:choose>
                        </h4>
                    </div><!-- /.col -->
                    <div class="col-md-6 col-sm-6 col-xs-12">
                        <h4>预支付金额：${order.preparePrice}元</h4>
                    </div><!-- /.col -->
                </div><!-- /.row -->

                <div class="row">
                    <div class="col-md-6 col-sm-6 col-xs-12">
                        <h4>预估总金额：${order.strPrice}元</h4>
                    </div><!-- /.col -->
                    <div class="col-md-6 col-sm-6 col-xs-12">
                        <c:choose>
                            <c:when test="${order.mailType == 1}"><h4>快递取件时间：${order.takeTime}</h4></c:when>
                            <c:when test="${order.mailType == 2}"><h4>快递取件时间：无</h4></c:when>
                        </c:choose>
                    </div><!-- /.col -->
                </div><!-- /.row -->

                <div class="row">
                    <div class="col-md-6 col-sm-6 col-xs-12">
                        <h4>顺丰订单号：${order.sfOrderNo}</h4>
                    </div><!-- /.col -->
                    <div class="col-md-6 col-sm-6 col-xs-12">
                        <c:choose>
                            <c:when test="${order.mailType == 1}"><h4>邮寄类型：超人系统推送</h4></c:when>
                            <c:when test="${order.mailType == 2}"><h4>邮寄类型：用户自行邮寄</h4></c:when>
                        </c:choose>
                    </div><!-- /.col -->
                </div><!-- /.row -->

                <div class="row">
                    <div class="col-md-6 col-sm-6 col-xs-12">
                        <h4>产品名称：${order.productName}</h4>
                    </div><!-- /.col -->
                    <div class="col-md-6 col-sm-6 col-xs-12">
                        <h4>订单来源:
                            ${order.fm}
                        </h4>
                    </div><!-- /.col -->
                </div><!-- /.row -->
                <c:choose>
                <c:when test="${coupon.couponCode != null}">
                <div class="row">
                    <div class="col-md-6 col-sm-6 col-xs-12">
                        <h4>加价券编码：${coupon.couponCode}</h4>
                    </div><!-- /.col -->
                    <c:if test="${coupon.pricingType == 1}">
                    <div class="col-md-6 col-sm-6 col-xs-12">
                        <h4>加价券金额：${coupon.couponPrice}元</h4>
                    </div><!-- /.col -->
                    </c:if>
                    <c:if test="${coupon.pricingType == 2}">
                        <div class="col-md-6 col-sm-6 col-xs-12">
                            <h4>加价券金额：${coupon.strCouponPrice}元</h4>
                        </div><!-- /.col -->
                    </c:if>
                </div><!-- /.row --></c:when>
                </c:choose>


                <div class="row">
                    <div class="col-md-12 col-sm-12 col-xs-12">
                        <h4>检测详情：${order.detail}</h4>
                    </div><!-- /.col -->
                    <c:if test="${order.orderStatus == 9}">
                        <div class="col-md-6 col-sm-6 col-xs-12">
                            <h4>完成时间：<fmt:formatDate value="${order.updateTime }" pattern="yyyy-MM-dd HH:mm:ss"/></h4>
                        </div><!-- /.col -->
                    </c:if>
                </div>

        <tr>
            <td colspan="3" class="tr-space"></td>
        </tr>
        <tr>
            <td class="td-title">
                <h4>用户信息：</h4>
            </td>
            <td class="td-space"></td>
            <td class="td-info">
                <div class="row">
                    <div class="col-md-6 col-sm-6 col-xs-12">
                        <h4>用户姓名：${cust.name }</h4>
                    </div><!-- /.col -->
                    <div class="col-md-6 col-sm-6 col-xs-12">
                        <h4>电话：${cust.mobile}</h4>
                    </div><!-- /.col -->
                </div><!-- /.row -->

                <div class="row">
                    <div class="col-md-6 col-sm-6 col-xs-12">
                        <h4>地址：${cust.area}&nbsp ${cust.address} </h4>
                    </div><!-- /.col -->
                    <div class="col-md-6 col-sm-6 col-xs-12">
                        <h4>备注：${order.note}</h4>
                    </div>

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