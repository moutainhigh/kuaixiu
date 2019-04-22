<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/commons/taglibs.jsp" %>
<link rel="stylesheet" href="${webResourceUrl}/resource/order/css/order.detail.css">
<div class="am-cf am-padding am-padding-bottom-0">
    <div class="am-fl am-cf" style="width: 100%;">
        <strong class="am-text-primary am-text-lg"><a href="javascript:void(0);" onclick="toList();">号卡管理</a></strong> /
        <small>号卡详情</small>
        <strong class="am-text-primary"><a href="javascript:void(0);" onclick="func_to_back();">返回</a></strong>
    </div>
</div>

<hr>

<div class="am-g order-info mt20">
    <table class="detail-table">
        <!-----------------------------号卡信息------------------------------------>
        <tr>
            <td class="td-title">
                <h4>号卡信息：</h4>
            </td>
            <td class="td-space"></td>
            <td class="td-info">
                <div class="row">
                    <div class="col-md-6 col-sm-6 col-xs-12">
                        <h4>批次：${card.batch}</h4>
                    </div><!-- /.col -->
                    <div class="col-md-6 col-sm-6 col-xs-12">
                        <h4>导入时间：<fmt:formatDate value="${card.inTime }" pattern="yyyy-MM-dd HH:mm:ss"/></h4>
                    </div><!-- /.col -->
                </div><!-- /.row -->

                <div class="row">
                    <div class="col-md-6 col-sm-6 col-xs-12">
                        <h4>所属本地网：${card.province }</h4>
                    </div><!-- /.col -->

                    <div class="col-md-6 col-sm-6 col-xs-12">
                        <h4>ICCID：${card.iccid}</h4>
                    </div><!-- /.col -->


                </div><!-- /.row -->

                <div class="row">
                    <div class="col-md-6 col-sm-6 col-xs-12">
                        <c:if test="${card.type==0}">
                            <h4>号卡类型：小白卡</h4>
                        </c:if>
                        <c:if test="${card.type==1}">
                            <h4>号卡类型：即买即通卡</h4>
                        </c:if>
                    </div><!-- /.col -->


                    <div class="col-md-6 col-sm-6 col-xs-12">
                        <c:if test="${card.cardName==0}">
                            <h4>号卡名称：小白卡</h4>
                        </c:if>
                        <c:if test="${card.cardName==1}">
                            <h4>号卡名称：抖音卡</h4>
                        </c:if>
                        <c:if test="${card.cardName==2}">
                            <h4>号卡名称：鱼卡</h4>
                        </c:if>
                        <c:if test="${card.cardName==3}">
                            <h4>号卡名称：49元不限流量卡</h4>
                        </c:if>
                        <c:if test="${card.cardName==4}">
                            <h4>号卡名称：99元不限流量卡</h4>
                        </c:if>
                        <c:if test="${card.cardName==5}">
                            <h4>号卡名称：199元不限流量卡</h4>
                        </c:if>
                        <c:if test="${card.cardName==6}">
                            <h4>号卡名称：29元不限流量卡</h4>
                        </c:if>
                    </div><!-- /.col -->
                </div><!-- /.row -->

                <div class="row">
                    <div class="col-md-6 col-sm-6 col-xs-12">
                        <c:choose>
                            <c:when test="${not empty card.distributionTime}">
                                <h4>分配时间：<fmt:formatDate value="${card.distributionTime}" pattern="yyyy-MM-dd"/></h4>
                            </c:when>
                            <c:otherwise>
                                <h4>分配时间：</h4>
                            </c:otherwise>
                        </c:choose>
                    </div><!-- /.col -->
                    <div class="col-md-6 col-sm-6 col-xs-12">
                        <h4>失效时间：<fmt:formatDate value="${card.loseEfficacy }" pattern="yyyy-MM-dd"/></h4>
                    </div><!-- /.col -->
                </div><!-- /.row -->
        </tr>
    </table>
</div>


<!----------------------------- 分配站点信息------------------------------------>
<div class="am-g order-info mt20">
    <table class="detail-table">
        <tr>
            <td class="td-title">
                <h4>分配站点信息：</h4>
            </td>
            <td class="td-space"></td>
            <td class="td-info">
                <div class="row">
                    <div class="col-md-6 col-sm-6 col-xs-12">
                        <h4>站点ID：${station.id}</h4>
                    </div><!-- /.col -->
                    <div class="col-md-6 col-sm-6 col-xs-12">
                        <h4>站点名称：${station.stationName}</h4>
                    </div><!-- /.col -->
                </div><!-- /.row -->

                <div class="row">
                    <div class="col-md-6 col-sm-6 col-xs-12">
                        <h4>联系人：${station.name }</h4>
                    </div><!-- /.col -->

                    <div class="col-md-6 col-sm-6 col-xs-12">
                        <h4>联系电话：${station.tel }</h4>
                    </div><!-- /.col -->
                </div><!-- /.row -->

                <div class="row">
                    <div class="col-md-6 col-sm-6 col-xs-12">
                        <h4>地址：${station.address}</h4>
                    </div><!-- /.col -->
                </div><!-- /.row -->
        </tr>
    </table>
</div>


<!----------------------------- 寄出信息------------------------------------>
<div class="am-g order-info mt20">
    <table class="detail-table">
        <tr>
            <td class="td-title">
                <h4>寄出信息：</h4>
            </td>
            <td class="td-space"></td>
            <td class="td-info">
                <div class="row">
                    <div class="col-md-6 col-sm-6 col-xs-12">
                        <h4>订单ID：${card.successOrderId}</h4>
                    </div><!-- /.col -->
                    <div class="col-md-6 col-sm-6 col-xs-12">
                        <c:choose>
                            <c:when test="${not empty card.sendTime}">
                                <h4>发货时间：<fmt:formatDate value="${card.sendTime}" pattern="yyyy-MM-dd"/></h4>
                            </c:when>
                            <c:otherwise>
                                <h4>发货时间：</h4>
                            </c:otherwise>
                        </c:choose>
                    </div><!-- /.col -->
                </div><!-- /.row -->

                <div class="row">
                    <div class="col-md-6 col-sm-6 col-xs-12">
                        <h4>物流单号：${card.expressNumber }</h4>
                    </div><!-- /.col -->

                    <div class="col-md-6 col-sm-6 col-xs-12">
                        <h4>物流公司：${card.expressName }</h4>
                    </div><!-- /.col -->
                </div><!-- /.row -->

                <div class="row">
                    <div class="col-md-6 col-sm-6 col-xs-12">
                        <h4>发货城市：${card.sendCity}</h4>
                    </div><!-- /.col -->
                    <div class="col-md-6 col-sm-6 col-xs-12">
                        <h4>推送时间：${card.strUpdateTime}</h4>
                    </div><!-- /.col -->
                </div><!-- /.row -->
        </tr>
    </table>

</div>
<!-- /am-g -->
