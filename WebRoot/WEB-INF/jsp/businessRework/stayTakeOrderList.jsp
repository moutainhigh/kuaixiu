<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/commons/taglibs.jsp" %>
<div class="am-cf am-padding am-padding-bottom-0">
    <div class="am-fl am-cf" style="width: 100%;">
        <strong class="am-text-primary am-text-lg">订单管理</strong> /
        <small>待接单列表查询</small>
    </div>
</div>

<hr>

<div class="am-g">
    <form id="searchForm" class="form form-horizontal">
        <table id="searchTable">
            <input type="hidden" id="state" name="state" value="200"/><br/>
            <tr>
                <td class="search_th "><label class="control-label">单 号 ：</label></td>
                <td class="search_td"><input type="text" name="orderNo" class="form-control"></td>
                <td class="search_th "><label class="control-label">企业名字：</label></td>
                <td class="search_td"><input type="text" name="companyName" class="form-control"></td>
            </tr>

            <tr>
                <td class="search_th"><label class="control-label">下 单 时 间 ：</label></td>
                <td class="search_td">
                    <div class="am-datepicker-date">
                        <input type="text" id="query_startTime" name="query_startTime"
                               class="form-control am-datepicker-start" data-am-datepicker readonly>
                        <span style="float: left; line-height: 30px; height: 30px; width: 10%; text-align: center;">至</span>
                        <input type="text" id="query_endTime" name="query_endTime"
                               class="form-control am-datepicker-end" data-am-datepicker readonly>
                    </div>
                </td>

                <td class="search_th"><label class="control-label">提交人手机号：</label></td>
                <td class="search_td"><input type="text" name="createUser" class="form-control"></td>
            </tr>

        </table>
        <div class="form-group">
            <div class="am-u-sm-12 am-u-md-6">
                <div class="am-btn-toolbar">
                    <div class="am-btn-group am-btn-group-sm m20">
                        <button onclick="refreshPage();" class="am-btn am-btn-default search_btn" type="button"> 开始查找
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
                <th class="fontWeight_normal tdwidth50"><input id="check_all_btn" onclick="checkAll(this)"
                                                               type="checkbox"/>序号
                </th>
                <th class="fontWeight_normal tdwidth80">报障单号</th>
                <th class="fontWeight_normal tdwidth80">原订单号</th>
                <th class="fontWeight_normal tdwidth50">报障时间</th>
                <th class="fontWeight_normal tdwidth90">报障产品</th>
                <th class="fontWeight_normal tdwidth90">备注</th>
                <th class="fontWeight_normal tdwidth80">处理公司</th>
                <th class="fontWeight_normal tdwidth100">等待时间</th>
                <th class="fontWeight_normal tdwidth50">状态</th>
            </tr>
            </thead>
            <tbody>

            </tbody>
        </table>
    </div>
</div>


<script src="${webResourceUrl}/resource/js/address.js" type="text/javascript" charset="utf-8"></script>
<script type="text/javascript">


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

    //自定义datatable的数据
    var dto = new DtOptions();
    //设置数据刷新路径
    dto.ajax = {
        "url": "${ctx}/sj/order/reworkQueryListForPage.do",
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
        {"data": "id", "class": " center"},
        {"data": "reworkOrderNo", "class": ""},
        {"data": "orderNo", "class": ""},
        {"data": "strCreateTime", "class": ""},
        {"data": "projectName", "class": ""},
        {"data": "note", "class": ""},
        {"data": "companyName", "class": ""},
        {"data": "nowTime", "class": ""},
        {"data": "state", "class": ""}
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
            targets: 2,//原订单号
            render: function (data, type, row, meta) {
                var html = "<a href=\"javascript:void(0);\" onclick=\"toDetail('" + row.orderId + "');\">" + row.orderNo + "</a>";
                return html;
            }
        },
        {
            targets: -2,
            render: function (data, type, row, meta) {
                var ts = (new Date(row.nowTime)) - (new Date(row.strCreateTime));//计算已等待的毫秒数
                var remainTime = ts / 1000 + 1;
                if (remainTime < 0) {
                    remainTime = 0;
                }
                var html = "<div class='waitTimeCountUp' remainTime='" + remainTime + "'>" + getHourTimeStr(remainTime) + "</div>"
                return html;
            }
        },
        {//订单状态  待审核100，带指派200，待施工300，待竣工400，已完成500，未通过600
            targets: -1,
            render: function (data, type, row, meta) {
                var state = "";
                switch (row.state) {
                    case 100:
                        state = "已创建";
                        break;
                    case 200:
                        state = "待分配";
                        break;
                    case 300:
                        state = "待施工";
                        break;
                    case 400:
                        state = "待完成";
                        break;
                    case 500:
                        state = "已完成";
                        break;
                    case 600:
                        state = "已取消";
                        break;
                    default:
                        state = "未知";
                }
                return state;
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

    function toDetail(id) {
        func_reload_page("${ctx}/sj/order/detail.do?id=" + id);
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
        window.open("${ctx}/sj/file/download.do?fileId=36&ids=" + ids + params, "导出");
    }
</script>