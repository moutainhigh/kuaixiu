<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/commons/taglibs.jsp" %>
<div class="am-cf am-padding am-padding-bottom-0">
    <div class="am-fl am-cf" style="width: 100%;">
        <strong class="am-text-primary am-text-lg">抬价订单管理</strong> /
        <small>列表查询</small>
    </div>
</div>

<hr>

<div class="am-g">
    <form id="searchForm" class="form form-horizontal">
        <table id="searchTable">
            <tr>
                <td class="search_th search_th_frist"><label class="control-label">&nbsp&nbsp&nbsp抬价订单号 ：</label></td>
                <td class="search_td"><input type="text" name="increase_order_no" class="form-control"></td>

                <td class="search_th"><label class="control-label">回收订单号：</label></td>
                <td class="search_td"><input type="text" name="recycle_order_no" class="form-control"></td>
                <td></td>
            </tr>

            <tr>
                <td class="search_th"><label class="control-label">开 始 时 间 ：</label></td>
                <td class="search_td">
                    <div class="am-datepicker-date">
                        <input type="text" id="start_increase_startTime" name="start_increase_startTime"
                               class="form-control am-datepicker-start" data-am-datepicker readonly>
                        <span style="float: left; line-height: 30px; height: 30px; width: 10%; text-align: center;">至</span>
                        <input type="text" id="start_increase_endTime" name="start_increase_endTime"
                               class="form-control am-datepicker-end" data-am-datepicker readonly>
                    </div>
                </td>

                <td class="search_th"><label class="control-label">结 束 时 间 ：</label></td>
                <td class="search_td">
                    <div class="am-datepicker-date">
                        <input type="text" id="end_increase_startTime" name="end_increase_startTime"
                               class="form-control am-datepicker-start" data-am-datepicker readonly>
                        <span style="float: left; line-height: 30px; height: 30px; width: 10%; text-align: center;">至</span>
                        <input type="text" id="end_increase_endTime" name="end_increase_endTime"
                               class="form-control am-datepicker-end" data-am-datepicker readonly>
                    </div>
                </td>
            </tr>

            <tr>
                <td class="search_th"><label class="control-label">订 单 状 态 ：</label></td>
                <td class="search_td">
                    <select name="increase_success" class="form-control">
                        <option value="">--订单状态--</option>
                        <option value="0">加价中</option>
                        <option value="1">加价失败</option>
                        <option value="2">加价成功</option>
                    </select>
                </td>
            </tr>


        </table>
        <div class="form-group">
            <div class="am-u-sm-12 am-u-md-6">
                <div class="am-btn-toolbar">
                    <div class="am-btn-group am-btn-group-sm m20">
                        <button onclick="refreshPage();" class="am-btn am-btn-default search_btn" type="button"> 搜 索
                        </button>
                    </div>
                </div>
            </div>
        </div>
    </form>
</div>

<div class="am-g">
    <div class="am-u-sm-12">
        <table id="dt" class="table table-striped table-bordered table-radius table-hover">
            <thead>
            <tr>
                <th class="fontWeight_normal tdwidth30"><input id="check_all_btn" onclick="checkAll(this)"
                                                               type="checkbox"/>序号
                </th>
                <th class="fontWeight_normal tdwidth50">抬价订单号</th>
                <th class="fontWeight_normal tdwidth60">回收订单号</th>
                <th class="fontWeight_normal tdwidth60">订单金额</th>
                <th class="fontWeight_normal tdwidth80">抬价状态</th>
                <th class="fontWeight_normal tdwidth70">开始时间</th>
                <th class="fontWeight_normal tdwidth70">结束时间</th>
                <th class="fontWeight_normal tdwidth70">抬价剩余时间</th>
                <th class="fontWeight_normal tdwidth70">抬价进度</th>
                <th class="fontWeight_normal tdwidth70">抬价次数</th>
                <th class="fontWeight_normal tdwidth50">操作</th>
            </tr>
            </thead>
            <tbody>

            </tbody>
        </table>
    </div>
</div>

<script type="text/javascript">
    $("#start_increase_startTime").datetimepicker({
        format: "yyyy-mm-dd",
        language: "zh-CN",
        autoclose: true,//选中关闭
        minView: "month"//设置只显示到月份
    });

    $("#start_increase_endTime").datetimepicker({
        format: "yyyy-mm-dd",
        language: "zh-CN",
        autoclose: true,//选中关闭
        minView: "month"//设置只显示到月份
    });

    $("#end_increase_startTime").datetimepicker({
        format: "yyyy-mm-dd",
        language: "zh-CN",
        autoclose: true,//选中关闭
        minView: "month"//设置只显示到月份
    });

    $("#end_increase_endTime").datetimepicker({
        format: "yyyy-mm-dd",
        language: "zh-CN",
        autoclose: true,//选中关闭
        minView: "month"//设置只显示到月份
    });
    //自定义datatable的数据
    var dto = new DtOptions();
    //设置数据刷新路径
    dto.ajax = {
        "url": "${ctx}/recycle/increase/queryListForPage.do",
        "data": function (d) {
            //将表单中的查询条件追加到请求参数中
            var array = $("#searchForm").serializeArray();
            $.each(array, function () {
                d[this.name] = this.value;
            });
        }
    };

    /**
     * 查看订单详情
     */
    function showOrderDetail(id) {
        func_reload_page("${ctx}/recycle/increase/detail.do?id=" + id);
    }

    //设置数据列
    dto.setColumns([
        {"data": "id", "class": "center"},
        {"data": "orderNo", "class": ""},
        {"data": "recycleOrderNo", "class": ""},
        {"data": "price", "class": ""},
        {"data": "isSuccess", "class": ""},
        {"data": "increaseStartTime", "class": ""},
        {"data": "increaseEndTime", "class": ""},
        {"data": "remainingTime", "class": ""},
        {"data": "plan", "class": ""},
        {"data": "times", "class": ""},
        {"defaultContent": "操作", "class": ""}
    ]);
    //设置定义列的初始属性
    dto.setColumnDefs([
        {//复选框
            targets: 0,
            render: function (data, type, row, meta) {
                var context = {
                    func: [
                        {"id": row.id, "order": meta.row + 1}
                    ]
                };
                var html = template_chk(context);
                return html;
            }
        },
        {
            targets: 4,
            render: function (data, type, row, meta) {
                if (row.isSuccess == 0) {
                    return "抬价中";
                } else if (row.isSuccess == 1) {
                    return "抬价失败";
                } else if (row.isSuccess == 2) {
                    return "抬价成功";
                } else {
                    return " ";
                }
            }
        },
        {
            targets: -3,
            render: function (data, type, row, meta) {
                return row.plan + " %";
            }
        },
        {
            targets: -1,
            render: function (data, type, row, meta) {
                var context = {
                    func: [
                        {
                            "name": "查看",
                            "fn": "showOrderDetail(\'" + row.orderNo + "\')",
                            "icon": "am-icon-pencil-square-o",
                            "class": "am-text-secondary"
                        }
                    ]
                };
                var html = template_btn(context);
                return html;
            }
        }
    ]);
    dto.sScrollXInner = "130%";
    var myTable = $("#dt").DataTable(dto);

    /**
     * 刷新列表
     */
    function refreshPage() {
        $("#pageStatus").val(1);
        myTable.ajax.reload(null, false);
    }

    function agreedTime(id) {
        $("#orderId").val(id);
        $("#modal-insertView").modal("show");
    }


    /**
     * 全选按钮
     */
    function checkAll(obj) {
        $("input[name='item_check_btn']").each(function () {
            $(this).prop("checked", obj.checked);
        });
    }

    function checkItem(obj) {
        var checked = true;
        $("input[name='item_check_btn']").each(function () {
            if (!this.checked) {
                checked = false;
                return false;
            }
        });
        $("#check_all_btn").prop("checked", checked);
    }
    /**
     * 导出数据
     */
    function expDataExcel() {
        var params = "";
        var array = $("#searchForm").serializeArray();
        $.each(array, function () {
            params += "&" + this.name + "=" + this.value;
        });
        var ids = "";
        $("input[name='item_check_btn']").each(function () {
            if (this.checked) {
                ids += this.value + ",";
            }
        });
        window.open("${ctx}/file/download.do?fileId=14&ids=" + ids + params, "导出");
    }


</script>