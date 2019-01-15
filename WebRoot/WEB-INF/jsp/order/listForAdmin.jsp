<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/commons/taglibs.jsp" %>
<div class="am-cf am-padding am-padding-bottom-0">
    <div class="am-fl am-cf" style="width: 100%;">
        <strong class="am-text-primary am-text-lg">订单管理</strong> /
        <small>列表查询</small>
    </div>
</div>

<hr>

<div class="am-g">
    <form id="searchForm" class="form form-horizontal">
        <table id="searchTable">
            <tr>
                <td class="search_th "><label class="control-label">订 单 号 ：</label></td>
                <td class="search_td"><input type="text" name="query_orderNo" class="form-control"></td>
                <td class="search_th"><label class="control-label">客户手机号：</label></td>
                <td class="search_td"><input type="text" name="query_customerMobile" class="form-control"></td>
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
            </tr>

            <tr>
                <td class="search_th "><label class="control-label">手机品牌：</label></td>
                <td class="search_td">
                    <select name="query_brand" onchange="brandChange(this.value);" class="form-control">
                        <option value="">--选择品牌--</option>
                        <c:forEach items="${brands }" var="item" varStatus="i">
                            <option value="${item.id }">${item.name }</option>
                        </c:forEach>
                    </select>
                </td>
                <td class="search_th"><label class="control-label">维 修 机 型 ：</label></td>
                <td class="search_td">
                    <select id="query_model" name="query_model" class="form-control">
                        <option value="">--选择机型--</option>
                    </select>
                </td>
                <td class="search_th"><label class="control-label">订 单 状 态 ：</label></td>
                <td class="search_td">
                    <select name="query_orderState" class="form-control">
                        <option value="">--订单状态--</option>
                        <c:forEach items="${selectOrderStatus}" var="item" varStatus="i">
                            <option value="${item.key }">${item.value}</option>
                        </c:forEach>
                    </select>
                </td>
            </tr>
            <tr>
                <td class="search_th "><label class="control-label">结算状态：</label></td>
                <td class="search_td">
                    <select name="query_balanceStatus" onchange="brandChange(this.value);" class="form-control">
                        <option value="">--选择状态--</option>
                        <option value="0">未结算</option>
                        <option value="2">已结算</option>
                    </select>
                </td>

                <td class="search_th"><label class="control-label">维 修 方 式：</label></td>
                <td class="search_td">
                    <select name="query_repairType" class="form-control">
                        <option value="">--选择状态--</option>
                        <option value="0">上门维修</option>
                        <option value="1">到店维修</option>
                        <option value="2">返店维修</option>
                        <option value="3">寄修</option>
                        <option value="4">点对点</option>
                    </select>
                </td>
                <td class="search_th"><label class="control-label">工程师工号：</label></td>
                <td class="search_td"><input type="text" name="query_engNumber" class="form-control" ></td>
            </tr>
            <tr>
                <td class="search_th "><label class="control-label">工程师姓名：</label></td>
                <td class="search_td"><input type="text" name="query_engName" class="form-control" ></td>
                <td class="search_th"><label class="control-label">完 成 时 间 ：</label></td>
                <td class="search_td">
                    <div class="am-datepicker-date">
                        <input type="text" id="query_startRepairTime" name="query_startRepairTime"
                               class="form-control am-datepicker-start" data-am-datepicker readonly>
                        <span style="float: left; line-height: 30px; height: 30px; width: 10%; text-align: center;">至</span>
                        <input type="text" id="query_endRepairTime" name="query_endRepairTime"
                               class="form-control am-datepicker-end" data-am-datepicker readonly>
                    </div>
                </td>
                <td class="search_th"><label class="control-label">来 源 系 统 ：</label></td>
                <td class="search_td">
                    <select name="fromSystem" class="form-control-inline">
                        <option value="">--来源系统--</option>
                        <c:forEach items="${fromSystems}" var="item" varStatus="i">
                            <option value="${item.id }">${item.name}</option>
                        </c:forEach>
                    </select>
                </td>
            </tr>
            <tr>
                <td class="search_th "><label class="control-label">下单地址：</label></td>
                <td class="form-group" colspan="3">
                    <select id="queryProvince" name="queryProvince"
                            onchange="fn_select_address(2, this.value, '', 'query');" class="form-control-inline">
                        <option value="">--请选择--</option>
                        <c:forEach items="${provinceL }" var="item" varStatus="i">
                            <option value="${item.areaId }">${item.area }</option>
                        </c:forEach>
                    </select>

                    <select id="queryCity" name="queryCity" onchange="fn_select_address(3, this.value, '', 'query');"
                            class="form-control-inline" style="display: none;">
                        <option value="">--请选择--</option>
                    </select>

                    <select id="queryCounty" name="queryCounty" class="form-control-inline" style="display: none;">
                        <option value="">--请选择--</option>
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
                    <div class="am-btn-group am-btn-group-sm">
                        <!-- <button onclick="addBtnClick();" type="button" class="am-btn am-btn-default"><span class="am-icon-plus"></span> 新增</button> -->
                        <!-- <button type="button" class="am-btn am-btn-default"><span class="am-icon-save"></span> 保存</button> -->
                        <button onclick="expDataExcel();" type="button" class="am-btn am-btn-default"><span
                                class="am-icon-file-excel-o"></span> 导出
                        </button>
                        <!-- <button onclick="batchDelBtnClick();" type="button" class="am-btn am-btn-default"><span class="am-icon-trash-o"></span> 删除</button> -->
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
                <th class="fontWeight_normal tdwidth80">下单时间</th>
                <th class="fontWeight_normal tdwidth160">订单号</th>
                <th class="fontWeight_normal tdwidth70">维修方式</th>
                <th class="fontWeight_normal tdwidth90">金额(元)</th>
                <th class="fontWeight_normal tdwidth90">实付金额(元)</th>
                <th class="fontWeight_normal table-title tdwidth80">联系人/<br/>手机号</th>
                <th class="fontWeight_normal tdwidth100">机型</th>
                <th class="fontWeight_normal tdwidth70">维修项目</th>
                <th class="fontWeight_normal tdwidth80">维修工程师</th>
                <th class="fontWeight_normal tdwidth80">来源系统</th>
                <th class="fontWeight_normal tdwidth100">订单状态</th>
                <th class="fontWeight_normal tdwidth80">结算状态</th>
                <th class="fontWeight_normal tdwidth80">完成时间</th>
                <th class="fontWeight_normal tdwidth70">操作</th>
            </tr>
            </thead>
            <tbody>
            <input id="sessionUserType" hidden="hidden" type="text" value="${sessionScope.session_user_key_.type}"/>
            </tbody>
        </table>
    </div>
</div>

<!-- 新增弹窗 end -->
<div id="modal-insertView" class="modal fade" tabindex="-1" role="dialog" aria-hidden="true" style="display: none;">
    <%@ include file="agreedTime.jsp" %>
</div>
<div id="modal-updatePriceView" class="modal fade" tabindex="-1" role="dialog" aria-hidden="true" style="display: none;">
    <%@ include file="updatePrice.jsp" %>
</div>
<div id="modal-createReworkView" class="modal fade" tabindex="-1" role="dialog" aria-hidden="true" style="display: none;">
    <%@ include file="addRewordOrder.jsp" %>
</div>
<!-- 新增弹窗 end -->

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

    $("#query_startRepairTime").datetimepicker({
        format: "yyyy-mm-dd",
        language: "zh-CN",
        autoclose: true,//选中关闭
        minView: "month"//设置只显示到月份
    });
    $("#query_endRepairTime").datetimepicker({
        format: "yyyy-mm-dd",
        language: "zh-CN",
        autoclose: true,//选中关闭
        minView: "month"//设置只显示到月份
    });
    //自定义datatable的数据
    var dto = new DtOptions();
    //设置数据刷新路径
    dto.ajax = {
        "url": "${ctx}/order/queryListForPage.do",
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
        {"data": "inTime", "class": ""},
        {"data": "orderNo", "class": ""},
        {"data": "repairType", "class": ""},
        {"data": "realPrice", "class": ""},
        {"data": "realPriceSubCoupon", "class": ""},
        {"data": "customerName", "class": ""},
        {"data": "modelName", "class": ""},
        {"data": "projectName", "class": ""},
        {"data": "engineerNumber", "class": ""},
        {"data": "fromSystemName", "class": ""},
        {"data": "orderStatusName", "class": ""},
        {"data": "balanceStatus", "class": ""},
        {"data": "endTime", "class": ""},
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
        {//复选框
            targets: 3,
            render: function (data, type, row, meta) {
                var state = '';
                switch (row.repairType) {
                    case 0:
                        state = "上门维修";
                        break;
                    case 1:
                        state = "到店维修";
                        break;
                    case 2:
                        state = "返店维修";
                        break;
                    case 3:
                        state = "寄修";
                        break;
                    case 4:
                        state = "点对点";
                        break;
                }
                return state;
            }
        },
        {
            targets: 4,
            render: function (data, type, row, meta) {
                return row.realPrice;
            }
        },
        {
            targets: 5,
            render: function (data, type, row, meta) {
                if (row.orderStatus < 50 || row.orderStatus == 60) {
                    return "0";
                } else {
                    return row.realPriceSubCoupon;
                }
            }
        },
        {//复选框
            targets: 6,
            render: function (data, type, row, meta) {
                return row.customerName + "/<br/>" + row.mobile;
            }
        },
        {//复选框
            targets: 7,
            render: function (data, type, row, meta) {
                return row.modelName + "<br/>（" + row.color + "）";
            }
        },
        {//复选框
            targets: -6,
            render: function (data, type, row, meta) {
                if (row.engineerName) {
                    return row.engineerNumber + "/<br/>" + row.engineerName;
                }
                else {
                    return "";
                }
            }
        },
        {//复选框
            targets: -4,
            render: function (data, type, row, meta) {
                if (row.orderStatus == 50) {
                    return '已完成';
                }
                else {
                    return row.orderStatusName;
                }
            }
        },
        {//结算状态（-1不需要 0：未结算对账；1：待结算；2：结算单生成）
            targets: -3,
            render: function (data, type, row, meta) {
                var state = "";
                switch (row.balanceStatus) {
                    case 0:
                        state = "未结算";
                        break;
                    case 1:
                        state = "待结算";
                        break;
                    case 2:
                        state = "已结算";
                        break;
                    default:
                        state = "不需要";
                }
                return state;
            }
        },
        {
            targets: -1,
            render: function (data, type, row, meta) {
                var context = {
                    func: [
                        {
                            "name": "查看",
                            "fn": "showOrderDetail(\'" + row.id + "\')",
                            "icon": "am-icon-pencil-square-o",
                            "class": "am-text-secondary"
                        }
                    ]
                };
                var html = template_btn(context);
                if (row.orderStatus == 11) {
                    context = {
                        func: [
                            {
                                "name": "预约",
                                "fn": "agreedTime(\'" + row.id + "\')",
                                "icon": "am-icon-pencil-square-o",
                                "class": "am-text-secondary"
                            }
                        ]
                    };
                    html += template_btn(context);
                }
                if (row.orderStatus == 50) {
                    context = {
                        func: [
                            {
                                "name": "售后",
                                "fn": "rewordOrder(\'" + row.orderNo + "\')",
                                "icon": "am-icon-pencil-square-o",
                                "class": "am-text-secondary"
                            }
                        ]
                    };
                    html += template_btn(context);
                }
                var sessionUser=$("#sessionUserType").val();
                if (row.orderStatus < 50) {
                    if (sessionUser == 7 || sessionUser==2) {
                    context = {
                        func: [
                            {
                                "name": "修改金额",
                                "fn": "updatePrice(\'" + row.id + "\')",
                                "icon": "am-icon-pencil-square-o",
                                "class": "am-text-secondary"
                            }
                        ]
                    };
                    html += template_btn(context);
                    }
                }

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

    function updatePrice(id) {
        $("#orderPriceId").val(id);
        $("#modal-updatePriceView").modal("show");
    }


    function rewordOrder(orderNo) {
        $("#orderNo").val(orderNo);
        $("#modal-createReworkView").modal("show");
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
        window.open("${ctx}/file/download.do?fileId=7&ids=" + ids + params, "导出");
    }
    /**
     * 查看订单详情
     */
    function showOrderDetail(id) {
        func_reload_page("${ctx}/order/detail.do?id=" + id);
    }

    /**
     * 查看订单详情
     */
    function brandChange(id) {
        $("#query_model option[value!='']").remove();
        if (id) {
            var url = AppConfig.ctx + "/model/queryByBrandId.do";
            $.get(url, {brandId: id}, function (result) {
                if (!result.success) {
                    return false;
                }
                var json = result.data;
                var select_html = '';
                if (json.length > 0) {
                    for (a in json) {
                        select_html += '<option value="' + json[a]['id'] + '">' + json[a]['name'] + '</option>';
                    }
                }
                $("#query_model").append(select_html);
            });
        }
    }

</script>