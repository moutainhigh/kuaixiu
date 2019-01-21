<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/commons/taglibs.jsp" %>
<div class="am-cf am-padding am-padding-bottom-0">
    <div class="am-fl am-cf" style="width: 100%;">
        <strong class="am-text-primary am-text-lg">维修工程师管理</strong> /
        <small>列表查询</small>
    </div>
</div>

<hr>

<div class="am-g">
    <form id="searchForm" class="am-form am-form-horizontal">
        <table id="searchTable">
            <tr>
                <td class="search_th" style="min-width:70px;"><label class="control-label">工 号 ：</label>
                </td>
                <td class="search_td"><input type="text" name="query_number" class="form-control"></td>
                <td class="search_th" style="min-width:70px;"><label class="control-label">姓 名：</label></td>
                <td class="search_td"><input type="text" name="query_name" class="form-control"></td>
                <td class="search_th"><label class="control-label">订 单 状 态 ：</label></td>
                <td class="search_td">
                    <select name="order_status" id="order_status" class="form-control">
                        <option value="">--选择状态--</option>
                        <option value="40">进行中</option>
                        <option value="50">已完成</option>
                        <option value="60">已取消</option>
                    </select>
                </td>
            </tr>
            <tr>
                <td class="search_th"><label class="control-label">完成时间 ：</label></td>
                <td class="search_td">
                    <div class="am-datepicker-date">
                        <input type="text" id="query_startTime" name="query_startTime"
                               class="form-control am-datepicker-start" data-am-datepicker readonly>
                        <span style="float: left; line-height: 30px; height: 30px; width: 10%; text-align: center;">至</span>
                        <input type="text" id="query_endTime" name="query_endTime"
                               class="form-control am-datepicker-end" data-am-datepicker readonly>
                    </div>
                </td>
                <td class="search_th"><label class="control-label">去 掉 贴 膜 ：</label></td>
                <td class="search_td">
                    <select name="isPatch" id="isPatch" class="form-control">
                        <option value="">--是否去掉--</option>
                        <option value="1">去掉</option>
                        <option value="2">不去掉</option>
                    </select>
                </td>
                <td class="search_th"><label class="control-label">订 单 类 别 ：</label></td>
                <td class="search_td">
                    <select name="orderType" id="orderType" class="form-control">
                        <option value="">--请选择--</option>
                        <option value="1">快修单</option>
                        <option value="2">返修单</option>
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
                        <button onclick="addBtnClick();" type="button" class="am-btn am-btn-default"><span
                                class="am-icon-plus"></span> 新增
                        </button>
                        <!-- <button type="button" class="am-btn am-btn-default"><span class="am-icon-save"></span> 保存</button> -->
                        <button onclick="expDataExcel();" type="button" class="am-btn am-btn-default"><span
                                class="am-icon-file-excel-o"></span> 导出
                        </button>
                        <button onclick="batchDelBtnClick();" type="button" class="am-btn am-btn-default"><span
                                class="am-icon-trash-o"></span> 删除
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
                <th class="fontWeight_normal tdwidth70 center">工号</th>
                <th class="fontWeight_normal tdwidth70 center">姓名</th>
                <th class="fontWeight_normal table-title center">所属门店商</th>
                <th class="fontWeight_normal table-title center">所属连锁商</th>
                <th class="fontWeight_normal tdwidth80 center">手机号</th>
                <th class="fontWeight_normal tdwidth60 center">是否在线</th>
                <th class="fontWeight_normal tdwidth70 center">订单数</th>
                <th class="fontWeight_normal tdwidth130 center">操作</th>
            </tr>
            </thead>
            <tbody>

            </tbody>
        </table>
    </div>
</div>

<!-- 新增弹窗 end -->
<div id="modal-insertView" class="modal fade" tabindex="-1" role="dialog" aria-hidden="true" style="display: none;">
</div>
<!-- 新增弹窗 end -->

<script src="${webResourceUrl}/resource/js/address.js" type="text/javascript" charset="utf-8"></script>
<script type="text/javascript">
    //自定义datatable的数据
    var dto = new DtOptions();
    //设置数据刷新路径
    dto.ajax = {
        "url": "${ctx}/engineer/queryAchievementForPage.do",
        "data": function (d) {
            //将表单中的查询条件追加到请求参数中
            var array = $("#searchForm").serializeArray();
            $.each(array, function () {
                d[this.name] = this.value;
            });
        },
        "error": function (xhr, error, thrown) {
            alert("1");
        }

    };

    //设置数据列
    dto.setColumns([
        {"data": "id", "class": "tdwidth50 center"},
        {"data": "number", "class": ""},
        {"data": "name", "class": ""},
        {"data": "shopName", "class": ""},
        {"data": "providerName", "class": ""},
        {"data": "mobile", "class": ""},
        {"data": "isDispatch", "class": ""},
        {"data": "orderDayNum", "class": ""},
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
            targets: 1,//工程师工号
            render: function (data, type, row, meta) {
                var html = "<a href=\"javascript:void(0);\" onclick=\"toDetail('" + row.id + "');\">" + row.number + "</a>";
                return html;
            }
        },
        {//状态
            targets: -3,
            render: function (data, type, row, meta) {
                if (row.isDispatch == 2) {
                    return "<i class='am-icon-circle am-text-danger'></i> 下线";
                } else {
                    return "<i class='am-icon-circle am-text-success'></i> 在线";
                }
            }
        },
        {//状态
            targets: -2,
            render: function (data, type, row, meta) {
                if (row.orderType == 1) {
                    return row.orderDayNum;
                } else if (row.orderType == 2) {
                    return row.reworkOrderNum;
                } else {
                    return row.reworkOrderNum + row.orderDayNum;
                }
            }
        },
        {
            targets: -1,
            render: function (data, type, row, meta) {
                var context = {
                    func: [
                        {
                            "name": "编辑",
                            "fn": "editBtnClick(\'" + row.id + "\')",
                            "icon": "am-icon-pencil-square-o",
                            "class": "am-text-secondary"
                        },
                        {
                            "name": "删除",
                            "fn": "delBtnClick(\'" + row.id + "\')",
                            "icon": "am-icon-trash-o",
                            "class": "am-text-danger"
                        }
                    ]
                };
                var html = template_btn(context);
                return html;
            }
        }
    ]);

    var myTable = $("#dt").DataTable(dto);

    /**
     * 刷新列表
     */
    function refreshPage() {
        $("#pageStatus").val(1);
        if ($("#order_status").val() == 40) {
            if ($("#query_startTime").val() != "" || $("#query_endTime").val() != "") {
                alert("订单进行中状态不可与时间同时筛选");
            } else {
                myTable.ajax.reload(null, false);
            }
        } else {
            myTable.ajax.reload(null, false);
        }
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

    function toDetail(id) {
        func_reload_page("${ctx}/engineer/detail.do?id=" + id);
    }

    function addBtnClick() {
        $("#modal-insertView").html("");
        $("#modal-insertView").load("${ctx}/engineer/add.do", function () {
            func_after_model_load(this);
        });
    }

    function editBtnClick(id) {
        $("#modal-insertView").html("");
        $("#modal-insertView").load("${ctx}/engineer/edit.do?id=" + id, function () {
            func_after_model_load(this);
        });
    }

    /**
     * 批量删除
     */
    function batchDelBtnClick() {
        var ids = "";
        $("input[name='item_check_btn']").each(function () {
            if (this.checked) {
                ids += this.value + ",";
            }
        });
        if (ids == "")
            AlertText.tips("d_alert", "提示", "请选择删除项！");
        else
            delBtnClick(ids);
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
        window.open("${ctx}/file/download.do?fileId=4&ids=" + ids + params, "导出");
    }

    function delBtnClick(id) {
        AlertText.tips("d_confirm", "删除提示", "确定要删除吗？", function () {
            //加载等待
            AlertText.tips("d_loading");
            var url_ = AppConfig.ctx + "/engineer/delete.do";
            var data_ = {id: id};
            $.ajax({
                url: url_,
                data: data_,
                type: "POST",
                dataType: "json",
                success: function (result) {
                    if (result.success) {
                        //保存成功,关闭窗口，刷新列表
                        refreshPage();
                    } else {
                        AlertText.tips("d_alert", "提示", result.msg);
                        return false;
                    }
                    //隐藏等待
                    AlertText.hide();
                }
            });
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