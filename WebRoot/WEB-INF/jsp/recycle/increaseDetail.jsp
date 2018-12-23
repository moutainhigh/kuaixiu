<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/commons/taglibs.jsp" %>
<link rel="stylesheet" href="${webResourceUrl}/resource/order/css/order.detail.css">
<div class="am-cf am-padding am-padding-bottom-0">
    <div class="am-fl am-cf" style="width: 100%;">
        <strong class="am-text-primary am-text-lg"><a href="javascript:void(0);" onclick="toList();">抬价订单管理</a></strong>
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
                <c:when test="${increaseOrder.isSuccess == 0}">抬价中</c:when>
                <c:when test="${increaseOrder.isSuccess == 1}">抬价失败</c:when>
                <c:when test="${increaseOrder.isSuccess == 2}">抬价成功</c:when>
                <c:otherwise>
                    待定
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
                        <h4 id="orderNo">订单号：${increaseOrder.orderNo }</h4>
                    </div><!-- /.col -->
                    <div class="col-md-6 col-sm-6 col-xs-12">
                        <h4>回收订单号：${increaseOrder.recycleOrderNo }</h4>
                    </div><!-- /.col -->
                </div><!-- /.row -->

                <div class="row">
                    <div class="col-md-6 col-sm-6 col-xs-12">
                        <h4>开始时间：${increaseOrder.increaseStartTime }</h4>
                    </div><!-- /.col -->
                    <div class="col-md-6 col-sm-6 col-xs-12">
                        <h4>结束时间：${increaseOrder.increaseEndTime }</h4>
                    </div><!-- /.col -->
                </div><!-- /.row -->


                <div class="row">
                    <div class="col-md-6 col-sm-6 col-xs-12">
                        <h4>剩余时间：${increaseOrder.remainingTime }</h4>
                    </div><!-- /.col -->
                    <div class="col-md-6 col-sm-6 col-xs-12">
                        <h4>订单金额：${increaseOrder.strPrice }元</h4>
                    </div><!-- /.col -->
                </div><!-- /.row -->

                <div class="row">
                    <div class="col-md-6 col-sm-6 col-xs-12">
                        <h4>抬价进度：${increaseOrder.plan }%</h4>
                    </div><!-- /.col -->
                    <div class="col-md-6 col-sm-6 col-xs-12">
                        <h4>抬价次数：${increaseOrder.times }次</h4>
                    </div><!-- /.col -->
                </div><!-- /.row -->
            </td>
        </tr>
    </table>
</div>
<!-- /am-g -->
<div class="am-g mt20">
    <div class="panel panel-success index-panel-msg">
        <div class="row">
            <div class="col-md-12 col-sm-12 col-xs-12">
                <h4>抬价信息：</h4>
            </div><!-- /.col -->
        </div><!-- /.row -->

        <div class="row hide">
            <div class="col-md-12 col-sm-12 col-xs-12">
                <form id="searchForm">
                    <input type="hidden" name="recycleOrderNo" value="${increaseOrder.recycleOrderNo}" />
                </form>
            </div><!-- /.col -->
        </div><!-- /.row -->

        <div class="row">
            <div class="am-u-sm-12">
                <table id="dt" class="table table-striped table-bordered table-radius table-hover">
                    <thead>
                    <tr>
                        <th class="fontWeight_normal table-title">微信id</th>
                        <th class="fontWeight_normal table-title">微信昵称</th>
                        <th class="fontWeight_normal table-title">加价百分比</th>
                        <th class="fontWeight_normal table-title">加价金额</th>
                        <th class="fontWeight_normal tdwidth100">加价时间</th>
                        <%--<th class="fontWeight_normal tdwidth70">操作</th>--%>
                    </tr>
                    </thead>
                    <tbody>

                    </tbody>
                </table>
            </div>
        </div><!-- /.row -->

    </div><!-- /panel -->
</div><!-- /am-g -->

<script type="text/javascript">
    function toList() {
        func_reload_page("${ctx}/recycle/increaseDetail.do");
    }

    //自定义datatable的数据
    var dto=new DtOptions();
    //设置数据刷新路径
    dto.ajax={
        "url": "${ctx}/recycle/increase/detailList.do",
        "data":function(d){
            //将表单中的查询条件追加到请求参数中
            var array = $("#searchForm").serializeArray();
            $.each(array, function() {
                d[this.name] = this.value;
            });
        }
    };

    //设置数据列
    dto.setColumns([
        {"data": "openId","class":""},
        {"data": "nickname","class":""},
        {"data": "plan","class":""},
        {"data": "price","class":""},
        {"data": "inTime","class":""}
//        {"defaultContent": "操作","class":""}
    ]);
    //设置定义列的初始属性
    dto.setColumnDefs([
        {
            targets: 2,
            render: function (data, type, row, meta) {
                return row.plan+"%";
            }
        },
        {
            targets: 3,
            render: function (data, type, row, meta) {
                return row.plan+"元";
            }
        }
//        {
//            targets: -1,
//            render: function (data, type, row, meta) {
//                var context = {
//                    func: [
//                        {"name" : "删除", "fn": "delBtnClick(\'" + row.id + "\')", "icon": "am-icon-trash-o","class" : "am-text-danger"}
//                    ]
//                };
//                var html = template_btn(context);
//                return html;
//            }
//        }
    ]);

    var myTable = $("#dt").DataTable(dto);

    /**
     * 刷新列表
     */
    function refreshPage(){
        myTable.ajax.reload(null, false);
    }
</script>