<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/commons/taglibs.jsp" %>
<link rel="stylesheet" href="${webResourceUrl}/resource/order/css/order.detail.css">
<div class="am-cf am-padding am-padding-bottom-0">
    <div class="am-fl am-cf" style="width: 100%;">
        <strong class="am-text-primary am-text-lg"><a href="javascript:void(0);"  onclick="toList();">批次管理</a></strong> / <small>批次详情</small>
        <strong class="am-text-primary"><a href="javascript:void(0);"  onclick="func_to_back();">返回</a></strong>
    </div>
</div>

<hr>

<div class="am-g order-info mt20">
    <table class="detail-table">
        <tr>
            <td class="td-title">
                <h4>批次信息：</h4>
            </td>
            <td class="td-space"></td>
            <td class="td-info">
                <div class="row">
                    <div class="col-md-6 col-sm-6 col-xs-12">
                        <h4>批次：${batch.batchId}</h4>
                    </div><!-- /.col -->
                    <div class="col-md-6 col-sm-6 col-xs-12">
                        <h4>导入时间：<fmt:formatDate value="${batch.inTime }" pattern="yyyy-MM-dd HH:mm:ss"/></h4>
                    </div><!-- /.col -->
                </div><!-- /.row -->

                <div class="row">
                    <div class="col-md-6 col-sm-6 col-xs-12">
                        <h4>所属本地网：${batch.province }</h4>
                    </div><!-- /.col -->
                    <div class="col-md-6 col-sm-6 col-xs-12">
                        <c:if test="${batch.type==0}">
                            <h4>号卡类型：小白卡</h4>
                        </c:if>
                        <c:if test="${batch.type==1}">
                            <h4>号卡类型：即买即通卡</h4>
                        </c:if>
                    </div><!-- /.col -->
                </div><!-- /.row -->

                <div class="row">
                    <div class="col-md-6 col-sm-6 col-xs-12">
                        <c:if test="${batch.cardName==0}">
                            <h4>号卡名称：小白卡</h4>
                        </c:if>
                        <c:if test="${batch.cardName==1}">
                            <h4>号卡名称：抖音卡</h4>
                        </c:if>
                        <c:if test="${batch.cardName==2}">
                            <h4>号卡名称：鱼卡</h4>
                        </c:if>
                        <c:if test="${batch.cardName==3}">
                            <h4>号卡名称：49元不限流量卡</h4>
                        </c:if>
                        <c:if test="${batch.cardName==4}">
                            <h4>号卡名称：99元不限流量卡</h4>
                        </c:if>
                        <c:if test="${batch.cardName==5}">
                            <h4>号卡名称：199元不限流量卡</h4>
                        </c:if>
                        <c:if test="${batch.cardName==6}">
                            <h4>号卡名称：29元不限流量卡</h4>
                        </c:if>
                    </div><!-- /.col -->

                    <div class="col-md-6 col-sm-6 col-xs-12">
                        <h4>总数量(张)：${batch.sum}</h4>
                    </div><!-- /.col -->
                </div><!-- /.row -->

                <div class="row">
                    <div class="col-md-6 col-sm-6 col-xs-12">
                        <h4>失效时间：<fmt:formatDate value="${batch.loseEfficacy }" pattern="yyyy-MM-dd"/></h4>
                    </div><!-- /.col -->
                    <div class="col-md-6 col-sm-6 col-xs-12">
                        <h4>待分配张数(张)：${batch.sum-batch.distributionSum}</h4>
                    </div><!-- /.col -->
                </div><!-- /.row -->



                <div style="height:25px;"></div>
                <table class="table table-bordered">
                    <tr>
                        <th>序号</th>
                        <th>起始ICCID</th>
                        <th>结束ICCID</th>
                        <th>号段数量（张）</th>
                        <th>分配状态</th>
                        <th>分配时间</th>
                    </tr>
                    <c:forEach items="${batchCardList }" var="item" varStatus="i">
                        <tr>
                            <td>${i.index+1}</td>
                            <td>${item.beginIccid}</td>
                            <td>${item.endIccid}</td>
                            <td>${item.sum}</td>
                            <td>
                                <c:choose>
                                    <c:when test="${item.isDistribution == 0 }">
                                        待分配
                                    </c:when>
                                    <c:when test="${item.isDistribution == 1 }">
                                        已分配
                                    </c:when>
                                </c:choose>
                            </td>
                            <td>
                                <c:choose>
                                    <c:when test="${not empty item.distributionTime}">
                                        <fmt:formatDate value="${item.distributionTime}" pattern="yyyy-MM-dd" />
                                    </c:when>
                                    <c:otherwise>

                                    </c:otherwise>
                                </c:choose>
                            </td>
                        </tr>
                    </c:forEach>
                </table>
        </tr>





    </table>
</div><!-- /am-g -->
