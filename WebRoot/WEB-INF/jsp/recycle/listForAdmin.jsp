<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/commons/taglibs.jsp" %>
<div class="am-cf am-padding am-padding-bottom-0">
    <div class="am-fl am-cf" style="width: 100%;">
        <strong class="am-text-primary am-text-lg">回收订单管理</strong> /
        <small>列表查询</small>
    </div>
</div>

<hr>

<div class="am-g">
    <form id="searchForm" class="form form-horizontal">
        <table id="searchTable">
            <tr>
                <td class="search_th search_th_frist"><label class="control-label">&nbsp&nbsp&nbsp订 单 号 ：</label></td>
                <td class="search_td"><input type="text" name="query_orderNo" class="form-control"></td>

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
                <td class="search_th"><label class="control-label">订 单 状 态 ：</label></td>
                <td class="search_td">
                    <select name="query_orderStates" class="form-control">
                        <option value="">--订单状态--</option>
                        <option value="0">取消订单</option>
                        <option value="1">创建订单</option>
                        <option value="2">待客户发件</option>
                        <option value="3">已发货待收件</option>
                        <option value="4">门店收件</option>
                        <option value="5">提交质检报告</option>
                        <option value="6">需议价订单</option>
                        <option value="7">议价结束</option>
                        <option value="8">待支付订单</option>
                        <option value="9">支付完成（结束）</option>
                        <option value="10">预支付转账成功</option>
                        <option value="11">预支付转账失败</option>
                        <option value="12">支付尾款失败</option>
                        <option value="13">扣款失败</option>

                    </select>
                </td>

                <td class="search_th"><label class="control-label">客户手机号：</label></td>
                <td class="search_td"><input type="text" name="query_customerMobile" class="form-control"></td>
                <td></td>

                <td class="search_th"><label class="control-label">使用加价券 ：</label></td>
                <td class="search_td">
                    <select name="query_orderStates" class="form-control">
                        <option value="">--请选择--</option>
                        <option value="0">否</option>
                        <option value="1">是</option>
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
                <th class="fontWeight_normal tdwidth50"><input id="check_all_btn" onclick="checkAll(this)"
                                                               type="checkbox"/>序号
                </th>
                <th class="fontWeight_normal tdwidth70">订单号</th>
                <th class="fontWeight_normal tdwidth40">快递单号</th>
                <th class="fontWeight_normal tdwidth80">订单状态</th>
                <th class="fontWeight_normal table-title tdwidth80">联系人/<br/>手机号</th>
                <th class="fontWeight_normal tdwidth70">回收类型</th>
                <th class="fontWeight_normal tdwidth70">支付类型</th>
                <th class="fontWeight_normal tdwidth70">回收价格</th>
                <th class="fontWeight_normal tdwidth70">订单来源</th>
                <th class="fontWeight_normal tdwidth70">下单时间</th>
                <th class="fontWeight_normal tdwidth50">操作</th>
            </tr>
            </thead>
            <tbody>

            </tbody>
        </table>
    </div>
</div>

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
        "url": "${ctx}/recycle/order/queryListForPage.do",
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
        {"data": "orderNo", "class": ""},
        {"data": "sfOrderNo", "class": ""},
        {"data": "orderStatus", "class": ""},
        {"data": "name", "class": ""},
        {"data": "recycleType", "class": ""},
        {"data": "exchangeType", "class": ""},
        {"data": "price", "class": ""},
        {"data": "fm", "class": ""},
        {"data": "inTime", "class": ""},
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
            targets: 3,
            render: function (data, type, row, meta) {
                if (row.orderStatus == 0) {
                    return "订单已取消";
                } else if (row.orderStatus == 1) {
                    return "创建订单";
                } else if (row.orderStatus == 2) {
                    return "待客户发件";
                } else if (row.orderStatus == 3) {
                    return "已发货待收件";
                } else if (row.orderStatus == 4) {
                    return "门店收件";
                } else if (row.orderStatus == 5) {
                    return "提交质检报告";
                } else if (row.orderStatus == 6) {
                    return "需议价订单";
                } else if (row.orderStatus == 7) {
                    return "议价结束";
                } else if (row.orderStatus == 8) {
                    return "待支付订单";
                } else if (row.orderStatus == 9) {
                    return "支付完成（结束）";
                } else if (row.orderStatus == 10) {
                    return "预支付转账成功";
                } else if (row.orderStatus == 11) {
                    return "预支付转账失败";
                } else if (row.orderStatus == 12) {
                    return "支付尾款失败";
                } else if (row.orderStatus == 13) {
                    return "扣款失败";
                } else {
                    return " ";
                }
            }
        },
        {
            targets: 4,
            render: function (data, type, row, meta) {
                return row.name + "/<br/>" + row.mobile;
            }
        },
        {
            targets: 5,
            render: function (data, type, row, meta) {
                if (row.recycleType == "0") {
                    return "信用回收";
                } else {
                    return "普通回收";
                }
            }
        },
        {
            targets: 6,
            render: function (data, type, row, meta) {
                if (row.exchangeType == "1") {
                    return "支付宝收款";
                } else {
                    return "话费充值";
                }
            }
        },
        {
            targets: -4,
            render: function (data, type, row, meta) {
                var price = row.price + '元';
                return price;
            }
        },
        {
            targets: -1,
            render: function (data, type, row, meta) {
//                if (row.orderStatus == 3) {
//                    var context = {
//                        func: [
//                            {
//                                "name": "查看",
//                                "fn": "showOrderDetail(\'" + row.id + "\')",
//                                "icon": "am-icon-pencil-square-o",
//                                "class": "am-text-secondary"
//                            },
//                           {
//                                "name": "预支付",
//                                "fn": "preparePay(\'" + row.id + "\')",
//                                "icon": "am-icon-pencil-square-o",
//                                "class": "am-text-secondary"
//                            }
//                        ]
//                    };
//                } else if (row.orderStatus == 4) {
//                    var context = {
//                        func: [
//                            {
//                                "name": "查看",
//                                "fn": "showOrderDetail(\'" + row.id + "\')",
//                                "icon": "am-icon-pencil-square-o",
//                                "class": "am-text-secondary"
//                            },
//                            {
//                                "name": "支付尾款",
//                                "fn": "endPay(\'" + row.id + "\')",
//                                "icon": "am-icon-pencil-square-o",
//                                "class": "am-text-secondary"
//                            }
//                        ]
//                    };
//                } else if (row.orderStatus == 5) {
//                    var context = {
//                        func: [
//                            {
//                                "name": "查看",
//                                "fn": "showOrderDetail(\'" + row.id + "\')",
//                                "icon": "am-icon-pencil-square-o",
//                                "class": "am-text-secondary"
//                            },
//                            {
//                                "name": "扣款",
//                                "fn": "deductPay(\'" + row.id + "\')",
//                                "icon": "am-icon-trash-o",
//                                "class": "am-text-danger"
//                            }
//                        ]
//                    };
//                } else {
//                    if (row.isSale == 0) {
//                        var context = {
//                            func: [
//                                {
//                                    "name": "查看",
//                                    "fn": "showOrderDetail(\'" + row.id + "\')",
//                                    "icon": "am-icon-pencil-square-o",
//                                    "class": "am-text-secondary"
//                                }
//                                //     {"name" : "拍卖", "fn" : "sale(\'" + row.id + "\')","icon" : "am-icon-pencil-square-o","class" : "am-text-secondary"}
//                            ]
//                        };
//                    } else {
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
//                    }
//
//                }
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
    /**
     * 查看订单详情
     */
    function showOrderDetail(id) {
        func_reload_page("${ctx}/recycle/order/detail.do?id=" + id);
    }

    /**
     * 预支付
     */
    function preparePay(id) {
        AlertText.tips("d_confirm", "温馨提示", "确定重新发起预支付转账吗？", function () {
            $.ajax({
                type: 'POST',
                url: "${ctx}/recycle/preparePay.do",
                dataType: 'json',
                data: {
                    id: id
                },
                success: function (data) {
                    if (data.success) {
                        //退款申请成功
                        refreshPage();
                        alert("操作成功");
                    } else {
                        alert(data.resultMessage);     //失败原因
                    }
                },
                error: function (jqXHR) {
                    alertTip('系统异常，请稍后再试！')
                }
            });
        });
    }

    /**
     * 支付尾款
     */
    function endPay(id) {
        AlertText.tips("d_confirm", "温馨提示", "确定重新发起支付尾款吗？", function () {
            $.ajax({
                type: 'POST',
                url: "${ctx}/screen/endPay.do",
                dataType: 'json',
                data: {
                    id: id
                },
                success: function (data) {
                    if (data.success) {
                        //退款申请成功
                        refreshPage();
                        alert("操作成功");
                    } else {
                        alert(data.resultMessage);     //失败原因
                    }
                },
                error: function (jqXHR) {
                    alertTip('系统异常，请稍后再试！')
                }
            });
        });
    }


    /**
     * 发起扣款
     */
    function deductPay(id) {
        AlertText.tips("d_confirm", "温馨提示", "确定重新发起扣款吗？", function () {
            $.ajax({
                type: 'POST',
                url: "${ctx}/screen/deductPay.do",
                dataType: 'json',
                data: {
                    id: id
                },
                success: function (data) {
                    if (data.success) {
                        //退款申请成功
                        refreshPage();
                        alert("操作成功");
                    } else {
                        alert(data.resultMessage);     //失败原因
                    }
                },
                error: function (jqXHR) {
                    alertTip('系统异常，请稍后再试！')
                }
            });
        });
    }

    /**
     * 发起转转拍卖 针对已成功回收未拍卖的订单
     * @param id
     */
    function sale(id) {
        AlertText.tips("d_confirm", "温馨提示", "确定发送到转转拍卖平台吗？", function () {
            $.ajax({
                type: 'POST',
                url: "${ctx}/recycle/createSaleOrder.do",
                dataType: 'json',
                data: {
                    id: id
                },
                success: function (data) {
                    if (data.success) {
                        //转转推送成功
                        refreshPage();
                        alert("操作成功");
                    } else {
                        alert(data.resultMessage);     //失败原因
                    }
                },
                error: function (jqXHR) {
                    alertTip('系统异常，请稍后再试！')
                }
            });
        });


    }

</script>