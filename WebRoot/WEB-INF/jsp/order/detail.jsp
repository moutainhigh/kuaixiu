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
                        <h4>订单号：${order.orderNo }</h4>
                    </div><!-- /.col -->
                    <div class="col-md-6 col-sm-6 col-xs-12">
                        <h4>下单时间：<fmt:formatDate value="${order.inTime }" pattern="yyyy-MM-dd HH:mm:ss"/></h4>
                    </div><!-- /.col -->
                </div><!-- /.row -->

                <div class="row">
                    <div class="col-md-6 col-sm-6 col-xs-12">
                        <h4>手机品牌：${order.brandName }</h4>
                    </div><!-- /.col -->
                    <div class="col-md-6 col-sm-6 col-xs-12">
                        <h4>维修单位：${order.shopName }</h4>
                    </div><!-- /.col -->
                </div><!-- /.row -->

                <div class="row">
                    <div class="col-md-6 col-sm-6 col-xs-12">
                        <h4>维修机型：${order.modelName }</h4>
                    </div><!-- /.col -->
                    <div class="col-md-6 col-sm-6 col-xs-12">
                        <h4>机型颜色：${order.color }</h4>
                    </div><!-- /.col -->
                </div><!-- /.row -->

                <div class="row">
                    <div class="col-md-6 col-sm-6 col-xs-12">
                        <h4>工程师工号：${order.engineerNumber }</h4>
                    </div><!-- /.col -->
                    <div class="col-md-6 col-sm-6 col-xs-12">
                        <h4>工程师名称：${order.engineerName }</h4>
                    </div><!-- /.col -->
                </div><!-- /.row -->
                <div class="row">
                    <div class="col-md-6 col-sm-6 col-xs-12">
                        <h4>订单金额：¥ ${order.realPrice }</h4>
                    </div><!-- /.col -->
                    <div class="col-md-6 col-sm-6 col-xs-12">
                        <h4>支付方法：
                            <c:if test="${order.payStatus == 0}">未支付</c:if>
                            <c:if test="${order.payStatus >= 1 && order.payType == 0}">微信支付</c:if>
                            <c:if test="${order.payStatus >= 1 && order.payType == 1}">支付宝支付</c:if>
                        </h4>
                    </div><!-- /.col -->
                </div><!-- /.row -->
                <div class="row">
                    <div class="col-md-6 col-sm-6 col-xs-12">
                        <h4>来源系统：${order.fromSystemName }</h4>
                    </div><!-- /.col -->

                    <div class="col-md-6 col-sm-6 col-xs-12">
                        <c:choose>
                            <c:when test="${order.orderStatus<50||order.orderStatus==60}">
                                <h4>实付金额：&nbsp 0</h4>
                            </c:when>
                            <c:otherwise>
                                <h4>实付金额：¥ <fmt:formatNumber value="${order.realPriceSubCoupon - order.depositPrice }"
                                                             pattern="0.00" maxFractionDigits='2'/></h4>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div><!-- /.row -->
                <c:if test="${order.isUseCoupon == 1}">
                    <div class="row">
                        <div class="col-md-6 col-sm-6 col-xs-12">
                            <h4>优惠券金额：¥ <fmt:formatNumber value="${order.couponPrice }" pattern="0.00"
                                                          maxFractionDigits='2'/>
                            </h4>
                        </div><!-- /.col -->
                        <div class="col-md-6 col-sm-6 col-xs-12">
                            <h4>优惠券号码：${order.couponCode }</h4>
                        </div><!-- /.col -->
                    </div>
                    <!-- /.row -->

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
                        <h4>维修项目：
                            <c:set var="pjct" value="0"/>
                            <c:forEach items="${details }" var="item" varStatus="i">
                                <c:if test="${not empty order.endCheckTime && item.type ==1}">
                                    <c:if test="${pjct > 0 }">、</c:if>
                                    <c:set var="pjct" value="${pjct + 1}"/>
                                    ${item.projectName }
                                </c:if>
                                <c:if test="${empty order.endCheckTime && item.type ==0}">
                                    <c:if test="${pjct > 0 }">、</c:if>
                                    <c:set var="pjct" value="${pjct + 1}"/>
                                    ${item.projectName }
                                </c:if>
                            </c:forEach>
                        </h4>
                    </div><!-- /.col -->
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

                </div><!-- /.row -->

                <div class="row">
                    <div class="col-md-6 col-sm-6 col-xs-12">
                        <h4>原订单总价：${upOrderPrice.upBeginPrice }</h4>
                    </div><!-- /.col -->
                    <c:if test="${order.repairType!=3}">
                        <div class="col-md-6 col-sm-6 col-xs-12">
                            <h4>预约时间：${order.agreedTime }</h4>
                        </div>
                        <!-- /.col -->
                    </c:if>
                </div>

                <div class="row">
                    <div class="col-md-6 col-sm-6 col-xs-12">
                        <h4>修改后订单总价：${upOrderPrice.upEndPrice }</h4>
                    </div><!-- /.col -->
                    <div class="col-md-6 col-sm-6 col-xs-12">
                        <h4>预约备注：${order.engNote }</h4>
                    </div><!-- /.col -->
                </div><!-- /.row -->
                <div class="row">
                    <div class="col-md-6 col-sm-6 col-xs-12">
                        <h4>修改价格备注：${upOrderPrice.upReason }</h4>
                    </div><!-- /.col -->
                    <div class="col-md-6 col-sm-6 col-xs-12">
                        <h4>返修订单号：
                            <c:forEach items="${reworks}" var="reworks" varStatus="i">
                                <a href="javascript:void(0);"
                                   onclick="showReworkOrderDetail('${reworks.orderReworkNo}')"> ${reworks.orderReworkNo}</a>，
                            </c:forEach>
                        </h4>
                    </div><!-- /.col -->
                </div><!-- /.row -->


                <c:if test="${order.cancelReason!=null&&order.orderStatus==60}">
                    <div class="row">
                        <div class="col-md-12 col-sm-12 col-xs-12">
                            <h4>取消原因： ${order.cancelReason}  </h4>
                        </div>
                    </div>
                </c:if>

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

                <c:if test="${comment!=null}">
                    <div class="row">
                        <div class="col-md-12 col-sm-12 col-xs-12">
                            <h4>用户评价：${comment.content}</h4>
                        </div>
                    </div>
                </c:if>

            </td>
        </tr>
        <c:if test="${orderRecord!=null}">
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
                            <h4>回访人：${orderRecord.recordName }</h4>
                        </div><!-- /.col -->
                        <div class="col-md-6 col-sm-6 col-xs-12">
                            <h4>回访时间：<fmt:formatDate value="${orderRecord.createTime }"
                                                     pattern="yyyy-MM-dd HH:mm:ss"/></h4>
                        </div><!-- /.col -->
                    </div><!-- /.row -->

                    <div class="row">
                        <div class="col-md-6 col-sm-6 col-xs-12">
                            <h4>回访备注：${orderRecord.note} </h4>
                        </div><!-- /.col -->
                        <div class="col-md-6 col-sm-6 col-xs-12">
                            <h4>优惠券类型：
                                <c:if test="${orderRecord.couponType==1}">
                                    20元通用优惠券
                                </c:if>
                                <c:if test="${orderRecord.couponType==2}">
                                    30元屏幕优惠券
                                </c:if>
                                <c:if test="${orderRecord.couponType==3}">
                                    50元屏幕优惠券
                                </c:if>
                            </h4>
                        </div><!-- /.col -->
                    </div><!-- /.row -->
                    <div class="row">
                        <div class="col-md-6 col-sm-6 col-xs-12">
                            <h4>优惠券编码：${orderRecord.couponCode }</h4>
                        </div><!-- /.col -->
                    </div><!-- /.row -->
                </td>
            </tr>
        </c:if>
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
            <a href="javascript:void (0);" onclick="orderCancel('${order.id}');" class="btn-confirm">确认取消订单</a>
        </div>
    </div>
</div>

<script type="text/javascript">
    function toList() {
        func_reload_page("${ctx}/order/list.do");
    }


    var selectReason;//选中原因
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
    function orderCancel(id) {
        var reason = $("#reason").val();
        var url_ = AppConfig.ctx + "/order/orderCancel.do";
        $.ajax({
            url: url_,
            type: "POST",
            data: {id: id, reason: reason, selectReason: selectReason},
            dataType: "json",
            success: function (result) {
                if (result.success) {
                    func_reload_page("${ctx}/order/detail.do?id=" + id);
                } else {
                    AlertText.tips("d_alert", "提示", result.msg);
                }
            },
            error: function () {
                AlertText.tips("d_alert", "提示", "系统异常，请稍后再试");
            }
        });

    }

    /**
     * 查看订单详情
     */
    function showReworkOrderDetail(id) {
        func_reload_page("${ctx}/order/reworkOrderDetail.do?reworkNo=" + id);
    }
</script>
