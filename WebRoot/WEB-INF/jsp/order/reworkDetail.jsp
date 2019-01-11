<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/commons/taglibs.jsp" %>
<link rel="stylesheet" href="${webResourceUrl}/resource/order/css/order.detail.css">
<div class="am-cf am-padding am-padding-bottom-0">
    <div class="am-fl am-cf" style="width: 100%;">
        <strong class="am-text-primary am-text-lg"><a href="javascript:void(0);" onclick="toList();">订单管理</a></strong> /
        <small>订单详情</small>
        <strong class="am-text-primary"><a href="javascript:void(0);" onclick="func_to_back();">返回</a></strong>
    </div>
</div>

<hr>

<div class="am-g">
    <div class="panel panel-success index-panel-msg">


        <h4>
            订单状态：
            <c:if test="${order.orderStatus==50&&order.isComment!=1}">
                已完成(待评价)
            </c:if>
            <c:if test="${order.orderStatus==50&&order.isComment==1}">
                已完成
            </c:if>
            <c:if test="${order.orderStatus!=50}">
                ${order.orderStatusName}
            </c:if>

            <c:if test="${order.orderStatus != 50 && order.orderStatus != 60}">
                <div class="am-btn-group am-btn-group-sm fr mt-5">
                    <button id="cancelBtn" type="button" class="am-btn am-btn-default">
                        <span class="am-icon-trash-o"></span> 取消订单
                    </button>
                </div>
            </c:if>
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
                        <h4>订单号：${rework.orderReworkNo }</h4>
                    </div>
                    <!-- /.col -->
                    <div class="col-md-6 col-sm-6 col-xs-12">
                        <h4>下单时间：${rework.inTime }</h4>
                    </div>
                    <!-- /.col -->
                </div>
                <!-- /.row -->

                <div class="row">
                    <div class="col-md-6 col-sm-6 col-xs-12">
                        <h4>维修方式：${order.repairType }</h4>
                    </div>
                    <!-- /.col -->
                    <div class="col-md-6 col-sm-6 col-xs-12">
                        <h4>维修单位：${rework.providerName }</h4>
                    </div>
                    <!-- /.col -->
                </div>
                <!-- /.row -->

                <div class="row">
                    <div class="col-md-6 col-sm-6 col-xs-12">
                        <h4>订单来源：${rework.fromSystem }</h4>
                    </div>
                    <!-- /.col -->
                    <div class="col-md-6 col-sm-6 col-xs-12">
                        <h4>维修机型：${order.modelName }</h4>
                    </div>
                    <!-- /.col -->
                </div>
                <div class="row">
                    <div class="col-md-6 col-sm-6 col-xs-12">
                        <h4>预约时间：${rework.agreedTime }</h4>
                    </div>
                    <!-- /.col -->
                    <div class="col-md-6 col-sm-6 col-xs-12">
                        <h4>完成时间：${rework.endTime }</h4>
                    </div>
                    <!-- /.col -->
                </div>
                <div class="row">
                    <div class="col-md-6 col-sm-6 col-xs-12">
                        <h4>打卡时间：${rework.number }</h4>
                    </div>
                    <!-- /.col -->
                    <div class="col-md-6 col-sm-6 col-xs-12">
                        <h4>打卡地址：${rework.idcard }</h4>
                    </div>
                    <!-- /.col -->
                </div>
                <div class="row">
                    <div class="col-md-6 col-sm-6 col-xs-12">
                        <h4>母订单号：${rework.parentOrder }</h4>
                    </div>
                    <!-- /.col -->
                    <div class="col-md-6 col-sm-6 col-xs-12">
                        <h4>上次维修人员：${order.engineerName }</h4>
                    </div>
                    <!-- /.col -->
                </div>
                <div class="row">
                    <div class="col-md-6 col-sm-6 col-xs-12">
                        <h4>维修人员：${rework.engineerName }</h4>
                    </div>

                </div>
                <div class="row">
                    <div class="col-md-6 col-sm-6 col-xs-12">
                        <h4>取消订单原因：${rework.cancelReason }</h4>
                    </div>
                </div>
                <div class="row">
                    <div class="col-md-12 col-sm-12 col-xs-12">
                        <h4>维修工程师订单信息：</h4>
                    </div><!-- /.col -->
                </div><!-- /.row -->
                <div class="row">
                    <div class="col-md-12 col-sm-12 col-xs-12">
                        <table class="table table-bordered">
                            <thead>
                            <tr>
                                <th><input type="checkbox" id="checkAll" disabled autocomplete="off"/></th>
                                <th>故障类型</th>
                                <th>故障部件</th>
                                <th>故障现象</th>
                                <th>剩余/总质保天数</th>
                                <th>价格（元）</th>
                            </tr>
                            </thead>

                            <c:forEach items="${rework}" var="c" varStatus="stc">
                                <tr>
                                    <td>${stc.index+1}</td>
                                    <td>${c.nickname}</td>
                                    <td>${c.email}</td>
                                    <td>${c.status}</td>
                                    <td>${c.create_time}</td>
                                    <td>${c.last_login_time}</td>

                                </tr>
                            </c:forEach>
                        </table>
                    </div><!-- /.col -->
                </div><!-- /.row -->
                <div class="row">
                    <div class="col-md-6 col-sm-6 col-xs-12">
                        <h4>订单总价：${rework.orderReworkNo }</h4>
                    </div>
                </div>
                <div class="row">
                    <div class="col-md-6 col-sm-6 col-xs-12">
                        <h4>优惠金额：${rework.orderReworkNo }</h4>
                    </div>
                </div>
                <div class="row">
                    <div class="col-md-6 col-sm-6 col-xs-12">
                        <h4>实付金额：${rework.orderReworkNo }</h4>
                    </div>
                </div>

            </td>
        </tr>
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
                        <h4>用户名称：${order.customerName }</h4>
                    </div><!-- /.col -->
                    <div class="col-md-6 col-sm-6 col-xs-12">
                        <h4>电话：${order.mobile }</h4>
                    </div><!-- /.col -->
                </div><!-- /.row -->

                <div class="row">
                    <div class="col-md-12 col-sm-12 col-xs-12">
                        <h4>地址名称：${order.fullAddress }</h4>
                    </div><!-- /.col -->
                </div><!-- /.row -->

                <div class="row">
                    <div class="col-md-12 col-sm-12 col-xs-12">
                        <h4>用户备注：${order.postscript }</h4>
                    </div><!-- /.col -->
                </div><!-- /.row -->
            </td>
        </tr>
    </table>
</div>
<!-- /am-g -->


<script type="text/javascript">
    function toList() {
        func_reload_page("${ctx}/order/list.do");
    }

</script>
