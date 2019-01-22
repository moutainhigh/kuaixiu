<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/commons/taglibs.jsp" %>
<div class="am-cf am-padding am-padding-bottom-0">
    <div class="am-fl am-cf" style="width: 100%;">
        <strong class="am-text-primary am-text-lg">订单管理</strong> /
        <small>订单调控</small>
    </div>
</div>

<hr>

<div class="am-g">
    <form id="searchForm" class="form form-horizontal">
        <input type="hidden" name="query_orderStates" value="11,12,20,30">
        <input type="hidden" id="query_endTime" name="query_endTime" value="">
        <table id="searchTable">
            <tr>
                <td class="search_th search_th_frist"><label class="control-label">订 单 号 ：</label></td>
                <td class="search_td"><input type="text" name="query_orderNo" class="form-control"></td>
                <td class="search_th"><label class="control-label">客户手机号：</label></td>
                <td class="search_td"><input type="text" name="query_customerMobile" class="form-control"></td>
                <td class="search_th"></td>
                <td class="search_td"></td>
            </tr>

            <tr>
                <td class="search_th_frist"><label class="control-label">等待时间：</label></td>
                <td class="form-group" colspan="5">
                    <select onchange="changeWaitTime(this.value);" class="form-control-inline">
                        <option value="0" selected="selected">请选择</option>
                        <option value="-3">3小时以上</option>
                        <option value="-4">4小时以上</option>
                        <option value="-5">5小时以上</option>
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
                        <button onclick="expDataExcel8();" type="button" class="am-btn am-btn-default"><span
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
                <th class="fontWeight_normal tdwidth130">下单时间</th>
                <th class="fontWeight_normal tdwidth160">订单号</th>
                <th class="fontWeight_normal tdwidth90">客户名称</th>
                <th class="fontWeight_normal tdwidth160">维修门店</th>
                <th class="fontWeight_normal tdwidth60">门店电话</th>
                <th class="fontWeight_normal tdwidth90">门店负责人</th>
                <th class="fontWeight_normal tdwidth90">维修工程师</th>
                <th class="fontWeight_normal tdwidth60">等待时间</th>
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
</div>
<div id="modal-againOrderView" class="modal fade" tabindex="-1" role="dialog" aria-hidden="true" style="display: none;">
    <%@ include file="againOrder.jsp" %>
</div>
<!-- 新增弹窗 end -->

<script src="${webResourceUrl}/resource/js/address.js" type="text/javascript" charset="utf-8"></script>
<script type="text/javascript">
    var endTime = getDateHourFormat(0);
    document.getElementById("query_endTime").value = endTime;
    //console.log(endTime);
    function changeWaitTime(value) {
        endTime = getDateHourFormat(Number(value));
        document.getElementById("query_endTime").value = endTime;
        refreshPage();
    }
    //自定义datatable的数据
    var dto = new DtOptions();
    //设置数据刷新路径
    dto.ajax = {
        "url": "${ctx}/order/queryListMapForPage.do",
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
        {"data": "id", "class": "center"},
        {"data": "in_time", "class": ""},
        {"data": "order_no", "class": ""},
        {"data": "customer_name", "class": ""},
        {"data": "shop_name", "class": ""},
        {"data": "shop_tel", "class": ""},
        {"data": "shop_manager_name", "class": ""},
        {"data": "engineer_name", "class": ""},
        {"data": "dispatch_time", "class": ""},
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
            targets: 2,
            render: function (data, type, row, meta) {
                var html = "<a href='javascript:void(0);' onclick=\"showOrderDetail('" + row.id + "')\" >" + row.order_no + "</a>";
                var state = row.orderStatusName;
                switch (row.order_status) {
                    case 11:
                        state = "待预约";
                        break;
                    case 12:
                        state = "已预约";
                        break;
                    case 20:
                        state = "定位故障";
                        break;
                    case 30:
                        state = "待用户付款";
                        break;
                }
                html += "<br/>" + state;
                return html;
            }
        },
        {
            targets: 3,
            render: function (data, type, row, meta) {
                return row.customer_name + "/<br/>" + row.mobile;
            }
        },
        {
            targets: 4,
            render: function (data, type, row, meta) {
                if (row.shop_name != null) {
                    if (row.shop_name.length > 8) {
                        var html = "<a href='javascript:void(0);' onclick=\"toShopDetail('" + row.shop_code + "')\" title='" + row.shop_name + "' >" + row.shop_name.substr(0, 7) + "...</a>";
                        return html;
                    }
                    else {
                        var html = "<a href='javascript:void(0);' onclick=\"toShopDetail('" + row.shop_code + "')\" title='" + row.shop_name + "' >" + row.shop_name + "</a>";
                        return html;
                    }
                } else {
                    var html = "<a href='javascript:void(0);' onclick=\"toShopDetail('" + row.shop_code + "')\" title='" + row.shop_name + "' >" + row.shop_name + "</a>";
                    return html;
                }
            }
        },
        {//复选框
            targets: 5,
            render: function (data, type, row, meta) {
                if (row.shop_tel == null) {
                    return "";
                }else {
                    return row.shop_tel;
                }
            }
        },
        {//复选框
            targets: -4,
            render: function (data, type, row, meta) {
                if (row.shop_manager_name == null && row.shop_manager_mobile != null) {
                    return "" + "/<br/>" + row.shop_manager_mobile;
                } else if (row.shop_manager_name != null && row.shop_manager_mobile == null) {
                    return row.shop_manager_name + "/<br/>" + "";
                } else if (row.shop_manager_name == null && row.shop_manager_mobile == null) {
                    return "";
                } else {
                    return row.shop_manager_name + "/<br/>" + row.shop_manager_mobile;
                }
            }
        },
        {//复选框
            targets: -3,
            render: function (data, type, row, meta) {
                return row.engineer_name + "/<br/>" + row.engineer_mobile;
            }
        },
        {
            targets: -2,
            render: function (data, type, row, meta) {
                var ts = (new Date(row.sys_time)) - (new Date(row.dispatch_time));//计算已等待的毫秒数
                var remainTime = ts / 1000 + 1;
                if (remainTime < 0) {
                    remainTime = 0;
                }
                var html = "<div class='waitTimeCountUp' remainTime='" + remainTime + "'>" + getHourTimeStr(remainTime) + "</div>"
                return html;
            }
        },
        {
            targets: -1,

            render: function (data, type, row, meta) {
                var sessionUser=$("#sessionUserType").val();
                if (row.order_status == 11 || row.order_status == 12 || row.order_status == 20) {
                    if (sessionUser == 7 || sessionUser==2) {
                        var context = {
                            func: [
                                {
                                    "name": "重新派单",
                                    "fn": "againOrderView(\'" + row.order_no + "\')",
                                    "icon": "am-icon-pencil-square-o",
                                    "class": "am-text-secondary"
                                }
                            ]
                        };
                        var html = template_btn(context);
                        return html;
                    }
                } else if (row.order_status == 30) {
                    var context = {
                        func: [
                            {
                                "name": "重置故障",
                                "fn": "resetRepair(\'" + row.order_no + "\')",
                                "icon": "am-icon-pencil-square-o",
                                "class": "am-text-secondary"
                            }
                        ]
                    };
                    var html = template_btn(context);
                    return html;
                }
            }
        }
    ]);

    var myTable = $("#dt").DataTable(dto);

    /**
     * 刷新列表
     */
    function refreshPage() {
        myTable.ajax.reload(null, false);
    }

    function againOrderView(order_no) {
        $("#againOrderNo").val(order_no);
        $("#modal-againOrderView").modal("show");
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
    function expDataExcel8() {
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
        window.open("${ctx}/file/download.do?fileId=8&ids=" + ids + params, "导出");
    }
    /**
     * 查看订单详情
     */
    function showOrderDetail(id) {
        func_reload_page("${ctx}/order/detail.do?id=" + id);
    }

    function toShopDetail(code) {
        func_reload_page("${ctx}/shop/detail.do?code=" + code);
    }

    /**
     * 重置故障
     */
    function resetRepair(order_no) {
        AlertText.tips("d_confirm", "系统提示", "确定要重置故障吗？ 重置故障后需要工程师重新提交检修结果。", function () {
            //加载等待
            AlertText.tips("d_loading");
            var url_ = AppConfig.ctx + "/order/resetRepair.do";
            var data_ = {order_no: order_no};
            $.ajax({
                url: url_,
                data: data_,
                type: "POST",
                dataType: "json",
                success: function (result) {
                    if (result.success) {
                        AlertText.tips("d_alert", "提示", "重置故障成功", function () {
                            refreshPage();
                        });
                    } else {
                        AlertText.tips("d_alert", "提示", result.msg);
                        return false;
                    }
                },
                error: function () {
                    AlertText.tips("d_alert", "提示", "系统异常，请稍后再试");
                    loading_hide();
                    isLoading = false;
                }
            });
        });
    }

    function updateWaitTime() {
        $(".waitTimeCountUp").each(function () {
            var obj = $(this);
            var remainTime = obj.attr("remainTime");
            remainTime = Number(remainTime) + 1;
            if (remainTime < 0) {
                remainTime = 0;
            }
            obj.attr("remainTime", remainTime);
            obj.html(getHourTimeStr(remainTime));
        });
    }

    if ("undefined" != typeof(countIntervalProcess)) {
        clearInterval(countIntervalProcess);
    }
    countIntervalProcess = setInterval("updateWaitTime()", 1000);


    ///**
    // * 重新派单
    // */
    //function reDispatch(id){
    //	AlertText.tips("d_confirm", "系统提示", "确定要重新派单吗？ 请确认维修工程师是否已经出发。", function(){
    //        //加载等待
    //        AlertText.tips("d_loading");
    //	    var url_ = AppConfig.ctx + "/order/reDispatch.do";
    //	    var data_ = {id: id};
    //	    $.ajax({
    //	        url: url_,
    //	        data: data_,
    //	        type: "POST",
    //	        dataType: "json",
    //	        success: function (result) {
    //	            if (result.success) {
    //	                AlertText.tips("d_alert", "提示", "重新派单成功", function(){
    //	                    refreshPage();
    //	                });
    //	            } else {
    //	                AlertText.tips("d_alert", "提示", result.msg);
    //	                return false;
    //	            }
    //	        },
    //	        error : function() {
    //	        	AlertText.tips("d_alert", "提示", "系统异常，请稍后再试");
    //	            loading_hide();
    //	            isLoading = false;
    //	        }
    //	    });
    //	});
    //}
</script>