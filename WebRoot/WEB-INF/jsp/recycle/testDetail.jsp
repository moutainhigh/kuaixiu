<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/commons/taglibs.jsp" %>
<link rel="stylesheet" href="${webResourceUrl}/resource/order/css/order.detail.css">
<div class="am-cf am-padding am-padding-bottom-0">
    <div class="am-fl am-cf" style="width: 100%;">
        <strong class="am-text-primary am-text-lg"><a href="javascript:void(0);" onclick="backList();">回收订单管理</a></strong>
        /
        <small>回收检测详情</small>
        <strong class="am-text-primary"><a href="javascript:void(0);" onclick="backList();">返回</a></strong>
    </div>
</div>

<hr>

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
                        <h4>创建时间：<fmt:formatDate value="${checkItems.inTime }" pattern="yyyy-MM-dd HH:mm:ss"/></h4>
                    </div><!-- /.col -->
                    <div class="col-md-6 col-sm-6 col-xs-12">
                        <%--<h4>检测型号：${checkItems.recycleModel }</h4>--%>
                            <h4>检测型号：
                                <c:if test="${checkItems.brand==null&&checkItems.recycleModel!=null}">
                                    ${checkItems.recycleModel }
                                </c:if>
                                <c:if test="${checkItems.brand!=null&&checkItems.recycleModel!=null}">
                                    ${checkItems.brand }
                                </c:if>
                                <c:if test="${checkItems.brand!=null&&checkItems.recycleModel!=null}">
                                    ${checkItems.brand }/ ${checkItems.recycleModel }
                                </c:if>
                            </h4>
                    </div><!-- /.col -->
                </div><!-- /.row -->

                <div class="row">
                    <div class="col-md-6 col-sm-6 col-xs-12">
                        <h4>检测项目：${itemName}</h4>
                    </div><!-- /.col -->
                    <div class="col-md-6 col-sm-6 col-xs-12">
                        <h4>检测报价：${checkItems.price }元</h4>
                    </div><!-- /.col -->
                </div><!-- /.row -->


                <div class="row">
                    <div class="col-md-6 col-sm-6 col-xs-12">
                        <h4>监测渠道：集团欢GO抽奖</h4>
                    </div><!-- /.col -->
                    <div class="col-md-6 col-sm-6 col-xs-12">
                        <h4>手机号：${checkItems.loginMobile }</h4>
                    </div><!-- /.col -->
                </div><!-- /.row -->

                <div class="row">
                    <div class="col-md-6 col-sm-6 col-xs-12">
                        <h4>成单：
                            <c:if test="${recycleTest!=null}">
                                <c:if test="${recycleTest.recycleId!=null}">
                                    是
                                </c:if>
                                <c:if test="${recycleTest.recycleId==null}">
                                    否
                                </c:if>
                            </c:if>
                        </h4>
                    </div><!-- /.col -->
                    <div class="col-md-6 col-sm-6 col-xs-12">
                        <h4>是否回访：
                            <c:if test="${recycleTest!=null}">
                                是
                            </c:if>
                            <c:if test="${recycleTest==null}">
                                否
                            </c:if>
                        </h4>
                    </div><!-- /.col -->

                </div><!-- /.row -->
                <div class="row">
                    <c:if test="${recycleOrderNo!=null}">
                        <div class="col-md-6 col-sm-6 col-xs-12">
                            <h4>回收订单号：<a href="javascript:void(0);" onclick="showOrderDetail('${recycleOrderId}')">${recycleOrderNo}</a></h4>
                        </div>
                        <!-- /.col -->
                    </c:if>
                </div><!-- /.row -->
            </td>
        </tr>

        <tr>
            <td colspan="3" class="tr-space"></td>
        </tr>
        <tr>
            <td class="td-title">
                <h4>回访信息：</h4>
            </td>
            <td class="td-space"></td>
            <td class="td-info">
                <div class="row">
                    <div class="col-md-6 col-sm-6 col-xs-12">
                        <h4>回访人：${recycleTest.recordName }</h4>
                    </div><!-- /.col -->
                    <div class="col-md-6 col-sm-6 col-xs-12">
                        <h4>回访时间：<fmt:formatDate value="${recycleTest.createTime }" pattern="yyyy-MM-dd HH:mm:ss"/></h4>
                    </div><!-- /.col -->
                </div><!-- /.row -->

                <div class="row">
                    <div class="col-md-6 col-sm-6 col-xs-12">
                        <h4>回访备注：${recycleTest.note} </h4>
                    </div><!-- /.col -->
                </div><!-- /.row -->
            </td>
        </tr>

    </table>
</div>
<!-- /am-g -->


<script type="text/javascript">
    function backList() {
        func_reload_page("${ctx}/recycle/recycleTestList.do");
    }

    /**
     * 查看订单详情
     */
    function showOrderDetail(id) {
        func_reload_page("${ctx}/recycle/order/detail.do?id=" + id);
    }

</script>