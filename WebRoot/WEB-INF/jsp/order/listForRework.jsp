<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/commons/taglibs.jsp" %>
<div class="am-cf am-padding am-padding-bottom-0">
    <div class="am-fl am-cf" style="width: 100%;">
        <strong class="am-text-primary am-text-lg">售后管理</strong> /
        <small>列表查询</small>
    </div>
</div>

<hr>

<div class="am-g">
    <form id="searchForm" class="form form-horizontal">
        <table id="searchTable">
            <tr>
                <td class="search_th"><label class="control-label">客户手机号：</label></td>
                <td class="search_td"><input type="text" name="engineerNumber" class="form-control"></td>
                <td class="search_th "><label class="control-label">手 机 品 牌：</label></td>
                <td class="search_td">
                    <select name="brandId" onchange="brandChange(this.value);" class="form-control">
                        <option value="">--请选择--</option>
                        <c:forEach items="${brands }" var="item" varStatus="i">
                            <option value="${item.id }">${item.name }</option>
                        </c:forEach>
                    </select>
                </td>
            </tr>

            <tr>
                <td class="search_th"><label class="control-label">创 建 时 间 ：</label></td>
                <td class="search_td">
                    <div class="am-datepicker-date">
                        <input type="text" id="query_startTime" name="queryStartTime"
                               class="form-control am-datepicker-start" data-am-datepicker readonly>
                        <span style="float: left; line-height: 30px; height: 30px; width: 10%; text-align: center;">至</span>
                        <input type="text" id="query_endTime" name="queryEndTime"
                               class="form-control am-datepicker-end" data-am-datepicker readonly>
                    </div>
                </td>
                <td class="search_th"><label class="control-label">维 修 机 型 ：</label></td>
                <td class="search_td">
                    <select id="query_model" name="modelId" class="form-control">
                        <option value="">--请选择--</option>
                        <c:forEach items="${models }" var="item" varStatus="i">
                            <option value="${item.id }">${item.name }</option>
                        </c:forEach>
                    </select>
                </td>
            </tr>
            <tr>
                <td class="search_th"><label class="control-label">返修订单号：</label></td>
                <td class="search_td"><input type="text" name="orderReworkNo" class="form-control" ></td>

                <td class="search_th"><label class="control-label">故障类型 ：</label></td>
                <td class="search_td">
                    <select name="projectId" class="form-control-inline">
                        <option value="">--请选择--</option>
                        <c:forEach items="${projects}" var="item" varStatus="i">
                            <option value="${item.id }">${item.name}</option>
                        </c:forEach>
                    </select>
                </td>
            </tr>
            <tr>
                <td class="search_th "><label class="control-label">母订单号：</label></td>
                <td class="search_td"><input type="text" name="parentOrder" class="form-control" ></td>
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
                <th class="fontWeight_normal tdwidth80">创建时间</th>
                <th class="fontWeight_normal tdwidth160">返修订单号</th>
                <th class="fontWeight_normal tdwidth70">母订单号</th>
                <th class="fontWeight_normal tdwidth90">客户姓名/电话</th>
                <th class="fontWeight_normal tdwidth90">品牌/机型/颜色</th>
                <th class="fontWeight_normal tdwidth90">故障类型</th>
                <th class="fontWeight_normal tdwidth70">剩余/总质保天数</th>
                <th class="fontWeight_normal tdwidth80">上次维修人员</th>
                <th class="fontWeight_normal tdwidth80">维修工程师</th>
                <th class="fontWeight_normal tdwidth80">订单状态</th>
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
<!-- 新增弹窗 end -->

<script src="${webResourceUrl}/resource/js/address.js" type="text/javascript" charset="utf-8"></script>
<script type="text/javascript">

    //自定义datatable的数据
    var dto = new DtOptions();
    //设置数据刷新路径
    dto.ajax = {
        "url": "${ctx}/reworkOrder/listForPage.do",
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
        {"data": "reworkNo", "class": ""},
        {"data": "parentNo", "class": ""},
        {"data": "name", "class": ""},
        {"data": "brandName", "class": ""},
        {"data": "projectNames", "class": ""},
        {"data": "surplusDay", "class": ""},
        {"data": "beforeEngName", "class": ""},
        {"data": "EngName", "class": ""},
        {"data": "orderStatus", "class": ""},
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
            targets: 4,
            render: function (data, type, row, meta) {
                return row.name + "/" + row.mobile;
            }
        },
        {
            targets: 5,
            render: function (data, type, row, meta) {
                return row.brandName + "/" + row.modelName + "/" + row.color;
            }
        },
        {
            targets: 7,
            render: function (data, type, row, meta) {
                return row.surplusDay + "/" + row.totalDay;
            }
        },
        {//复选框
            targets: -4,
            render: function (data, type, row, meta) {
                return row.beforeEngNumber + "/<br/>" + row.beforeEngName;
            }
        },
        {//复选框
            targets: -3,
            render: function (data, type, row, meta) {
                return row.engNumber + "/<br/>" + row.EngName;
            }
        },
        {//复选框
            targets: -2,
            render: function (data, type, row, meta) {
                if (row.orderStatus == 50) {
                    return '已完成';
                }
                else {
                    return row.orderStatusName;
                }
            }
        },
        {
            targets: -1,
            render: function (data, type, row, meta) {
                var context = {
                    func: [
                        {
                            "name": "取消",
                            "fn": "showtail(\'" + row.reworkNo + "\')",
                            "icon": "am-icon-pencil-square-o",
                            "class": "am-text-secondary"
                        },
                        {
                            "name": "查看",
                            "fn": "showOrderDetail(\'" + row.reworkNo + "\')",
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




    /**
     * 全选按钮
     */
    function checkAll(obj) {
        $("input[name='item_check_btn']").each(function () {
            $(this).prop("checked", obj.checked);
        });
    }


    /**
     * 查看订单详情
     */
    function showOrderDetail(id) {
        func_reload_page("${ctx}/reworkOrder/reworkOrderDetail.do?reworkOrderNo=" + id);
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
</script>