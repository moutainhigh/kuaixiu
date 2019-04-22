<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/commons/taglibs.jsp" %>
<link rel="stylesheet" href="${webResourceUrl}/resource/order/css/order.detail.css">
<div class="am-cf am-padding am-padding-bottom-0">
    <div class="am-fl am-cf" style="width: 100%;">
        <strong class="am-text-primary am-text-lg"><a href="javascript:void(0);" onclick="toList();">返修订单管理</a></strong>
        /
        <small>返修订单详情</small>
        <strong class="am-text-primary"><a href="javascript:void(0);" onclick="func_to_back();">返回</a></strong>
    </div>
</div>

<hr>

<div class="am-g">
    <div class="panel panel-success index-panel-msg">


        <h4>
            订单状态：
            <c:if test="${rework.orderStatus==50&&rework.isComment!=1}">
                已完成(待评价)
            </c:if>
            <c:if test="${rework.orderStatus==50&&rework.isComment==1}">
                已完成
            </c:if>
            <c:if test="${rework.orderStatus!=50}">
                ${rework.orderStatusName}
            </c:if>

            <c:if test="${rework.orderStatus != 50 && rework.orderStatus != 60}">
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
                        <h4>下单时间：${rework.strInTime }</h4>
                    </div>
                    <!-- /.col -->
                </div>
                <!-- /.row -->

                <div class="row">
                    <div class="col-md-6 col-sm-6 col-xs-12">
                        <c:if test="${order.repairType==3}">
                            <h4>维修方式：寄修</h4>
                        </c:if>
                        <c:if test="${order.repairType==0}">
                            <h4>维修方式：上门维修</h4>
                        </c:if>
                        <c:if test="${order.repairType==1}">
                            <h4>维修方式：到店维修</h4>
                        </c:if>
                        <c:if test="${order.repairType==4}">
                            <h4>维修方式：点对点</h4>
                        </c:if>
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
                        <h4>订单来源：<span style="color:red">${rework.fromSystem }</span></h4>
                    </div>
                    <!-- /.col -->
                    <div class="col-md-6 col-sm-6 col-xs-12">
                        <h4>维修机型：${order.modelName }</h4>
                    </div>
                    <!-- /.col -->
                </div>
                <c:if test="${order.orderStatus<=12}">
                    <div class="row">
                        <div class="col-md-6 col-sm-6 col-xs-12">
                            <h4>预约时间：${rework.strAgreedTime }</h4>
                        </div>
                        <!-- /.col -->
                        <div class="col-md-6 col-sm-6 col-xs-12">
                            <h4>完成时间：${rework.strEndTime }</h4>
                        </div>
                        <!-- /.col -->
                    </div>
                </c:if>
                <div class="row">
                    <div class="col-md-6 col-sm-6 col-xs-12">
                        <h4>打卡时间：${engineerSignIn.strCreateTime }</h4>
                    </div><!-- /.col -->
                    <div class="col-md-6 col-sm-6 col-xs-12">
                        <h4>打卡地点：${engineerSignIn.areas } ${engineerSignIn.address }</h4>
                    </div><!-- /.col -->
                </div><!-- /.row -->
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
                        <c:if test="${rework.reworkReasons==0}">
                            <h4>返修原因：物料原因</h4>
                        </c:if>
                        <c:if test="${rework.reworkReasons==1}">
                            <h4>返修原因：装配原因</h4>
                        </c:if>
                        <c:if test="${rework.reworkReasons==2}">
                            <h4>返修原因：客户原因</h4>
                        </c:if>
                    </div>
                    <div class="col-md-6 col-sm-6 col-xs-12">
                        <h4>返修原因详情：${rework.reasonsDetail }</h4>
                    </div>

                </div>
                <c:if test="${order.orderStatus==60}">
                    <div class="row">
                        <div class="col-md-6 col-sm-6 col-xs-12">
                            <h4>取消订单原因：${rework.cancelReason }</h4>
                        </div>
                    </div>
                </c:if>
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
                                <%--<th>故障类型</th>--%>
                                <th>故障部件</th>
                                <%--<th>故障现象</th>--%>
                                <th>剩余/总质保天数</th>
                                <th>价格（元）</th>
                            </tr>
                            </thead>

                            <c:forEach items="${projects}" var="c" varStatus="stc">
                                <tr>
                                    <td>${stc.index+1}</td>
                                    <td>${c.projectName}</td>
                                    <td>${c.surplusDay}/${c.totalDay}</td>
                                    <td>${c.price}</td>
                                </tr>
                            </c:forEach>
                        </table>
                    </div><!-- /.col -->
                </div><!-- /.row -->
                <div class="row">
                    <div class="text-right col-md-11 col-sm-11 col-xs-12">
                        <h4>订单总价：${realPrice} 元</h4>
                    </div>
                </div>
                <div class="row">
                    <div class="text-right col-md-11 col-sm-11 col-xs-12">
                        <h4>优惠金额：${couponPrice} 元</h4>
                    </div>
                </div>
                <div class="row">
                    <div class="text-right col-md-11 col-sm-11 col-xs-12">
                        <h4>实付金额：0.00 元</h4>
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
<div class="order_cancel">
    <div class="order_cancel_cont">
        <p>能告诉我们原因么？</p>
        <ul class="fault_list" id="selectReason">
            <c:forEach items="${reasonList }" var="item" varStatus="i">
                <li class=""><a href="javascript:void(0);">${item.reason}</a></li>
            </c:forEach>
        </ul>
        <textarea class="reason" name="" id="reason" placeholder="请写下您取消的原因吧！" maxlength="220"></textarea>
        <div class="index_but">
            <a href="javascript:void(0);" class="btn-cancel">取消</a>
            <a href="javascript:void (0);" onclick="orderCancel('${rework.id}','${rework.orderReworkNo}');"
               class="btn-confirm">确认取消订单</a>
        </div>
    </div>
</div>

<script type="text/javascript">
    function toList() {
        func_reload_page("${ctx}/order/list.do");
    }

    $(function () {

        $("#cancelBtn").click(function () {
            $(".order_cancel").show();
        });

        $(".fault_list li").click(function () {
            if ($(this).hasClass("fault_in")) {
                $(this).removeClass("fault_in");
            } else {
                $(this).addClass("fault_in").siblings("li").removeClass("fault_in");
                selectReason = $(this).find("a").eq(0).text();
            }
        });

        $(".btn-cancel").click(function () {
            $(".fault_list li").removeClass("fault_in");
            $(".reason").val("");
            $(".order_cancel").hide();
        });
    });


    //取消订单
    function orderCancel(id, orderReworkNo) {
        var reason = $("#reason").val();
        var url_ = AppConfig.ctx + "/order/reworkOrderCancel.do";
        $.ajax({
            url: url_,
            type: "POST",
            data: {id: id, reason: reason, selectReason: selectReason},
            dataType: "json",
            success: function (result) {
                if (result.success) {
                    func_reload_page("${ctx}/order/reworkOrderDetail.do?reworkNo=" + orderReworkNo);
                } else {
                    AlertText.tips("d_alert", "提示", result.msg);
                }
            },
            error: function () {
                AlertText.tips("d_alert", "提示", "系统异常，请稍后再试");
            }
        });

    }
</script>
