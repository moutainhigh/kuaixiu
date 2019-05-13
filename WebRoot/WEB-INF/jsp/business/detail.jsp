<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/commons/taglibs.jsp" %>
<link rel="stylesheet" href="${webResourceUrl}/resource/order/css/order.detail.css">
<div class="am-cf am-padding am-padding-bottom-0">
    <div class="am-fl am-cf" style="width: 100%;">
        <strong class="am-text-primary am-text-lg"><a href="javascript:void(0);" onclick="toList();">商机订单管理</a></strong>
        /
        <small>订单详情</small>
        <strong class="am-text-primary"><a href="javascript:void(0);" onclick="func_to_back();">返回</a></strong>
    </div>
</div>

<hr>

<div class="am-g">
    <div class="panel panel-success index-panel-msg">


        <h4>
            订单状态：
            <c:if test="${sjOrder.state==100}">
                待审核
            </c:if>
            <c:if test="${sjOrder.state==200}">
                待派单
            </c:if>
            <c:if test="${sjOrder.state==300}">
                待施工
            </c:if>
            <c:if test="${sjOrder.state==400}">
                待竣工
            </c:if>
            <c:if test="${sjOrder.state==500}">
                已完成
            </c:if>
            <c:if test="${sjOrder.state==600}">
                未通过
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
                        <h4>订单号：${sjOrder.orderNo }</h4>
                    </div><!-- /.col -->
                    <div class="col-md-6 col-sm-6 col-xs-12">
                        <h4>创建时间：<fmt:formatDate value="${sjOrder.createTime }" pattern="yyyy-MM-dd HH:mm:ss"/></h4>
                    </div><!-- /.col -->
                </div><!-- /.row -->

                <div class="row">
                    <div class="col-md-6 col-sm-6 col-xs-12">
                        <h4>单位名字：${sjOrder.companyName }</h4>
                    </div><!-- /.col -->
                    <div class="col-md-6 col-sm-6 col-xs-12">
                        <h4>联系人/电话：${sjOrder.person }/${sjOrder.phone }</h4>
                    </div><!-- /.col -->
                </div><!-- /.row -->

                <div class="row">
                    <div class="col-md-12 col-sm-12 col-xs-12">
                        <h4>地址：${sjOrder.address }</h4>
                    </div><!-- /.col -->
                </div><!-- /.row -->

                <div class="row">
                    <div class="col-md-12 col-sm-12 col-xs-12">
                        <h4>产品需求：${sjOrder.projectNames }</h4>
                    </div><!-- /.col -->
                </div><!-- /.row -->

                <div class="row">
                    <div class="col-md-12 col-sm-12 col-xs-12">
                        <h4>门头图片：
                            <c:forEach items="${companyPictures }" var="item" varStatus="i">
                                <img src="${item.companyPictureUrl}" width="90" height="80"/>
                            </c:forEach>
                        </h4>
                    </div><!-- /.col -->
                </div><!-- /.row -->
            </td>
        </tr>
        <c:if test="${sjOrder.state==100}">
            <tr>
                <td colspan="3" class="tr-space"></td>
            </tr>
            <tr>
                <td class="td-title">
                    <h4>审批信息：</h4>
                </td>
                <td class="td-space"></td>
                <td class="td-info">
                    <div class="row">
                        <div class="col-md-12 col-sm-12 col-xs-12">
                            <textarea name="note" id="note" cols="79" rows="5" placeholder="请输入审批备注"></textarea>
                        </div><!-- /.col -->
                    </div><!-- /.row -->

                    <div class="index_but">
                        <a href="javascript:void(0);" onclick="orderCancel('${sjOrder.id}','2');"
                           class="btn-cancel">不同意</a>
                        <a href="javascript:void (0);" onclick="orderCancel('${sjOrder.id}','1');"
                           class="btn-confirm">同意</a>
                    </div>

                </td>
            </tr>
        </c:if>
        <c:if test="${sjOrder.state==200||sjOrder.state==300||sjOrder.state==400||sjOrder.state==500||sjOrder.state==600}">
            <tr>
                <td colspan="3" class="tr-space"></td>
            </tr>
            <tr>
                <td class="td-title">
                    <h4>审批信息：</h4>
                </td>
                <td class="td-space"></td>
                <td class="td-info">
                    <div class="row">
                        <div class="col-md-12 col-sm-12 col-xs-12">
                            <h4>备注：${sjOrder.approvalNote }</h4>
                        </div><!-- /.col -->
                    </div><!-- /.row -->
                    <div class="row">
                        <div class="col-md-6 col-sm-6 col-xs-12">
                            <h4>审批人：${sjOrder.approvalPerson }</h4>
                        </div><!-- /.col -->
                        <div class="col-md-6 col-sm-6 col-xs-12">
                            <h4>审批时间：<fmt:formatDate value="${sjOrder.approvalTime }" pattern="yyyy-MM-dd HH:mm:ss"/></h4>
                        </div><!-- /.col -->
                    </div><!-- /.row -->
                </td>
            </tr>
        </c:if>
        <c:if test="${sjOrder.state==300}">
            <tr>
                <td colspan="3" class="tr-space"></td>
            </tr>
            <tr>
                <td class="td-title">
                    <h4>指派信息：</h4>
                </td>
                <td class="td-space"></td>
                <td class="td-info">
                    <div class="row">
                        <div class="col-md-12 col-sm-12 col-xs-12">
                            <h4>指派人：${sjOrder.assignPerson }</h4>
                        </div><!-- /.col -->
                        <div class="col-md-12 col-sm-12 col-xs-12">
                            <h4>审批时间：<fmt:formatDate value="${sjOrder.assignTime }" pattern="yyyy-MM-dd HH:mm:ss"/></h4>
                        </div><!-- /.col -->
                    </div><!-- /.row -->
                    <div class="row">
                        <div class="col-md-6 col-sm-6 col-xs-12">
                            <h4>待施工人：${sjOrder.buildPerson}</h4>
                        </div><!-- /.col -->
                        <div class="col-md-6 col-sm-6 col-xs-12">
                            <h4>单位：${sjOrder.buildCompany }</h4>
                        </div><!-- /.col -->
                    </div><!-- /.row -->
                </td>
            </tr>
        </c:if>
        <c:if test="${sjOrder.state==400 ||sjOrder.state==500}">
            <tr>
                <td colspan="3" class="tr-space"></td>
            </tr>
            <tr>
                <td class="td-title">
                    <h4>指派信息：</h4>
                </td>
                <td class="td-space"></td>
                <td class="td-info">
                    <div class="row">
                        <div class="col-md-12 col-sm-12 col-xs-12">
                            <h4>指派人：${sjOrder.assignPerson }</h4>
                        </div><!-- /.col -->
                        <div class="col-md-12 col-sm-12 col-xs-12">
                            <h4>审批时间：<fmt:formatDate value="${sjOrder.assignTime }" pattern="yyyy-MM-dd HH:mm:ss"/></h4>
                        </div><!-- /.col -->
                    </div><!-- /.row -->
                </td>
            </tr>
            <tr>
                <td colspan="3" class="tr-space"></td>
            </tr>
            <tr>
                <td class="td-title">
                    <h4>指派信息：</h4>
                </td>
                <td class="td-space"></td>
                <td class="td-info">
                    <div class="row">
                        <div class="col-md-6 col-sm-6 col-xs-12">
                            <h4>待施工人：${sjOrder.buildPerson}</h4>
                        </div><!-- /.col -->
                        <div class="col-md-6 col-sm-6 col-xs-12">
                            <h4>单位：${sjOrder.buildCompany }</h4>
                        </div><!-- /.col -->
                    </div><!-- /.row -->
                    <div class="row">
                        <div class="col-md-12 col-sm-12 col-xs-12">
                            <h4>审批时间：<fmt:formatDate value="${sjOrder.endTime }" pattern="yyyy-MM-dd HH:mm:ss"/></h4>
                        </div><!-- /.col -->
                    </div><!-- /.row -->
                </td>
            </tr>
        </c:if>
        <c:if test="${sjOrder.state==500}">
            <tr>
                <td colspan="3" class="tr-space"></td>
            </tr>
            <tr>
                <td class="td-title">
                    <h4>竣工信息：</h4>
                </td>
                <td class="td-space"></td>
                <td class="td-info">
                    <div class="row">
                        <div class="col-md-12 col-sm-12 col-xs-12">
                            <h4>竣工人：${sjOrder.completedPerson }</h4>
                        </div><!-- /.col -->
                        <div class="col-md-12 col-sm-12 col-xs-12">
                            <h4>竣工时间：<fmt:formatDate value="${sjOrder.completedTime }" pattern="yyyy-MM-dd HH:mm:ss"/></h4>
                        </div><!-- /.col -->
                    </div><!-- /.row -->
                    <div class="row">
                        <div class="col-md-12 col-sm-12 col-xs-12">
                            <h4>合同图片：
                                <c:forEach items="${contractPictures }" var="item" varStatus="i">
                                    <img src="${item.contractPictureUrl}" width="260" height="180"/>
                                </c:forEach>
                            </h4>
                        </div><!-- /.col -->
                    </div><!-- /.row -->
                </td>
            </tr>
        </c:if>
    </table>
</div>
<!-- /am-g -->

<script type="text/javascript">
    function toList() {
        func_reload_page("${ctx}/sj/order/list.do");
    }

    function orderCancel(id, isPast) {
        var note = $("#note").val();
        if (note == null) {
            AlertText.tips("d_alert", "提示", "请输入备注");
        }
        var url_ = AppConfig.ctx + "/sj/order/approval.do";
        $.ajax({
            url: url_,
            type: "POST",
            data: {id: id, isPast: isPast, note: note},
            dataType: "json",
            success: function (result) {
                if (result.success) {
                    func_reload_page("${ctx}/sj/order/detail.do?id=" + id);
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
