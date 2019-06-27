<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/commons/taglibs.jsp" %>
<link rel="stylesheet" href="${webResourceUrl}/resource/order/css/order.detail.css">
<div class="am-cf am-padding am-padding-bottom-0">
    <div class="am-fl am-cf" style="width: 100%;">
        <strong class="am-text-primary am-text-lg"><a href="javascript:void(0);"
                                                      onclick="toList();">群发短信记录管理</a></strong> /
        <small>群发短信记录详情</small>
        <strong class="am-text-primary"><a href="javascript:void(0);" onclick="func_to_back();">返回</a></strong>
    </div>
</div>

<hr>


<div class="am-g mt20">
    <div class="panel panel-success index-panel-msg">
        <div class="row">
            <div class="col-md-12 col-sm-12 col-xs-12">
                <h4>群发短信记录${batchRecord.batchId}详情：</h4>
            </div><!-- /.col -->
        </div><!-- /.row -->

        <form id="searchForm" class="form form-horizontal">
            <table id="searchTable">
                <input type="hidden" name="batchId" value="${batchRecord.id}"/>
                <tr>
                    <td class="search_th"><label class="control-label">创建时间：</label></td>
                    <td class="search_td">
                        <div class="am-datepicker-date">
                            <input type="text" id="query_startTime" name="queryStartTime"
                                   class="form-control am-datepicker-start" data-am-datepicker readonly>
                            <span style="float: left; line-height: 30px; height: 30px; width: 10%; text-align: center;">至</span>
                            <input type="text" id="query_endTime" name="queryEndTime"
                                   class="form-control am-datepicker-end" data-am-datepicker readonly>
                        </div>
                    </td>
                    <td class="search_th"><label class="control-label">手机号：</label></td>
                    <td class="search_td"><input type="text" name="mobile" class="form-control"></td>
                </tr>
            </table>
            <div class="form-group">
                <div class="am-u-sm-12 am-u-md-6">
                    <div class="am-btn-toolbar">
                        <div class="am-btn-group am-btn-group-sm m20">
                            <button onclick="refreshPage();" class="am-btn am-btn-default search_btn"
                                    type="button"> 搜 索
                            </button>
                        </div>
                    </div>
                </div>
            </div>
        </form>

        <div class="row">
            <div class="col-md-12 col-sm-12 col-xs-12">
                <table id="dt" class="table table-striped table-bordered table-radius table-hover">
                    <thead>
                    <tr>
                        <th class="fontWeight_normal tdwidth30">序号
                        </th>
                        <th class="fontWeight_normal tdwidth80 center">手机号</th>
                        <th class="fontWeight_normal tdwidth50 center">加价券编码</th>
                        <th class="fontWeight_normal tdwidth60 center">短信模板名字</th>
                        <th class="fontWeight_normal tdwidth60 center">发起人</th>
                        <th class="fontWeight_normal tdwidth80 center">发起时间</th>
                    </tr>
                    </thead>
                    <tbody>

                    </tbody>
                </table><!-- /table -->
            </div><!-- /.col -->
        </div><!-- /.row -->

    </div><!-- /panel -->
</div>
<!-- /am-g -->

<script type="text/javascript">
    function toList() {
        func_reload_page("${ctx}/groupSms/toGroupMobileBatchRecord.do");
    }
    //自定义datatable的数据
    var dto = new DtOptions();
    //设置数据刷新路径
    dto.ajax = {
        "url": "${ctx}/groupSms/groupMobileRecordForPage.do",
        "data": function (d) {
            //将表单中的查询条件追加到请求参数中
            var array = $("#searchForm").serializeArray();
            $.each(array, function () {
                d[this.name] = this.value;
            });
        }
    };

    //设置数据列
    dto.setColumns([
        {"data": "id", "class": "tdwidth50 center"},
        {"data": "mobile", "class": ""},
        {"data": "couponCode", "class": ""},
        {"data": "smsTemplate", "class": ""},
        {"data": "createUserid", "class": ""},
        {"data": "strCreateTime", "class": ""}
    ]);
    //设置定义列的初始属性
    dto.setColumnDefs([
        {//复选框
            targets: 0,
            render: function (data, type, row, meta) {
//                var context = {
//                    func: [
//                        {"id": row.id, "order": meta.row + 1}
//                    ]
//                };
//                var html = template_chk(context);
                return meta.row + 1;
            }
        }
    ]);

    dto.sScrollXInner = "100%";
    var myTable = $("#dt").DataTable(dto);

    /**
     * 刷新列表
     */
    function refreshPage() {
        $("#pageStatus").val(1);
        myTable.ajax.reload(null, false);
    }

    /**
     * 全选按钮
     */
    function checkAll(obj) {
        $("input[name='item_check_btn']").each(function () {
            $(this).prop("checked", obj.checked);
        });
    }

    $("#query_startTime").datetimepicker({
        format: "yyyy-mm-dd",
        language: "zh-CN",
        autoclose: true,//选中关闭
        minView: "month"//设置只显示到月份
    });

    $("#query_endTime").datetimepicker({
        format: "yyyy-mm-dd",
        language: "zh-CN",
        autoclose: true,//选中关闭
        minView: "month"//设置只显示到月份
    });
</script>