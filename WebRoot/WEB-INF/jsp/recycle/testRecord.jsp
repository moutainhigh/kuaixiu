<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/commons/taglibs.jsp" %>
<link rel="stylesheet" href="${webResourceUrl}/resource/order/css/order.detail.css">
<div class="am-cf am-padding am-padding-bottom-0">
    <div class="am-fl am-cf" style="width: 100%;">
        <strong class="am-text-primary am-text-lg"><a href="javascript:void(0);" onclick="toList();">回收订单管理</a></strong>
        /
        <small>回收检测详情</small>
        <strong class="am-text-primary"><a href="javascript:void(0);" onclick="toList();">返回</a></strong>
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
            </td>
        </tr>
        <input type="hidden" id="checkId" value="${checkItems.id}"/>
        <input type="hidden" id="itemName" value="${itemName}"/>
    </table>
    </br>
    <div class="form-group">
        <div class="am-u-sm-6 am-u-md-6 col-md-offset-4">
            <div class="am-btn-toolbar">
                <div class="am-btn-group am-btn-group-sm m20">
                    <button onclick="addNotes();" class="am-btn am-btn-default search_btn" type="button"> 备&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;注
                    </button>
                    <button onclick="submitOrder();" class="am-btn am-btn-default search_btn" type="button"> 创建订单
                    </button>
                </div>
            </div>
        </div>
    </div>
</div>
<!-- /am-g -->
<div id="modal-insertView" class="modal fade" tabindex="-1" role="dialog" aria-hidden="true" style="display: none;">
    <%@ include file="testAddNote.jsp" %>
</div>
<div id="modal-submitOrder" class="modal fade" tabindex="-1" role="dialog" aria-hidden="true" style="display: none;">
    <%@ include file="testSubmitOrder.jsp" %>
</div>

<script type="text/javascript">
    function toList() {
        func_reload_page("${ctx}/recycle/testList.do");
    }
    function addNotes() {
        var id = $("#checkId").val();
        $("#checkItemsId").val(id);
        $("#modal-insertView").modal("show");
    }
    function submitOrder() {
        var id = $("#checkId").val();
        $("#modal-submitOrder").html("");
        $("#modal-submitOrder").load("${ctx}/recycle/goTestSubmit.do?id=" + id, function () {
            func_after_model_load(this);
        });
    }
</script>