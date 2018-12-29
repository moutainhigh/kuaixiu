<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/commons/taglibs.jsp" %>
<div class="am-cf am-padding am-padding-bottom-0">
    <div class="am-fl am-cf" style="width: 100%;">
        <strong class="am-text-primary am-text-lg">预约管理</strong> /
        <small>列表查询</small>
    </div>
</div>

<hr>

<div class="am-g">
    <form id="searchForm" class="am-form am-form-horizontal">
        <table id="searchTable">
            <tr>
                <td class="search_th search_th_frist"><label class="control-label">企 业 名 称：</label></td>
                <td class="search_td"><input type="text" name="companyName" class="form-control"></td>
                <td class="search_th"><label class="control-label">业 务 类 型：</label></td>
                <td class="search_td">
                    <select id="querategoryId" name="businessType" class="form-control">
                        <option value="">--请选择--</option>
                        <option value="1">快修业务</option>
                        <option value="2">电信业务</option>
                    </select>
                </td>

            </tr>
            <tr>
                <td class="search_th"><label class="control-label">提 交 时 间 ：</label></td>
                <td class="search_td">
                    <div class="am-datepicker-date">
                        <input type="text" id="startLoginTime" name="queryStartTime"
                               class="form-control am-datepicker-start" data-am-datepicker readonly>
                        <span style="float: left; line-height: 30px; height: 30px; width: 10%; text-align: center;">至</span>
                        <input type="text" id="endLoginTime" name="queryStartTime"
                               class="form-control am-datepicker-end" data-am-datepicker readonly>
                    </div>
                </td>
                <td class="search_th"><label class="control-label">预 约 结 果：</label></td>
                <td class="search_td">
                    <select id="query_categoryId" name="estimateResult" class="form-control">
                        <option value="">--请选择--</option>
                        <option value="1">有效</option>
                        <option value="2">已结束</option>
                    </select>
                </td>
            </tr>
            <tr>
                <td class="search_th search_th_frist"><label class="control-label">联系人：</label></td>
                <td class="search_td"><input type="text" name="person" class="form-control"></td>
            </tr>
        </table>

        <div class="form-group">
            <div class="am-u-sm-12 am-u-md-6">
                <div class="am-btn-toolbar">
                    <div class="am-btn-group am-btn-group-sm m20">
                        <button onclick="refreshPage();" class="am-btn am-btn-default search_btn" type="button"> 筛 选
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
                <th class="fontWeight_normal tdwidth60 center">提交时间</th>
                <th class="fontWeight_normal tdwidth60 center">企业名称</th>
                <th class="fontWeight_normal tdwidth90 center">联系人/手机号</th>
                <th class="fontWeight_normal tdwidth60 center">微信绑定手机号</th>
                <th class="fontWeight_normal tdwidth60 center">手机品牌机型</th>
                <th class="fontWeight_normal tdwidth60 center">预约业务类型</th>
                <th class="fontWeight_normal tdwidth60 center">预约业务</th>
                <th class="fontWeight_normal tdwidth60 center">故障现象</th>
                <th class="fontWeight_normal tdwidth60 center">预约结果</th>
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
        "url": "${ctx}/activityCompany/getActivityUserForPage.do",
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
        {"data": "updateTime", "class": ""},
        {"data": "companyName", "class": ""},
        {"data": "person", "class": ""},
        {"data": "loginNumber", "class": ""},
        {"data": "model", "class": ""},
        {"data": "businessType", "class": ""},
        {"data": "project", "class": ""},
        {"data": "fault", "class": ""},
        {"data": "estimateResult", "class": ""},
    ]);
    //设置定义列的初始属性
    dto.setColumnDefs([
        {//复选框
            targets: 0,
            render: function (data, type, row, meta) {
                if (row.isUse == 1) {
                    return meta.row + 1;
                }
                else {
                    var context = {
                        func: [
                            {"id": row.id, "order": meta.row + 1}
                        ]
                    };
                    var html = template_chk(context);
                    return html;
                }
            }
        },
        {//复选框
            targets: 4,
            render: function (data, type, row, meta) {
                return row.person + "/<br/>" + row.number;
            }
        },
        {//复选框
            targets: -4,
            render: function (data, type, row, meta) {
                if (row.businessType == 1) {
                    return "快修";
                } else if (row.businessType == 2) {
                    return "电信";
                }
            }
        },
        {//复选框
            targets: -1,
            render: function (data, type, row, meta) {
                if (row.isEnd == 1) {
                    return "有效";
                } else if (row.isEnd == 2) {
                    return "已结束";
                }
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



    $("#startLoginTime").datetimepicker({
        format: "yyyy-mm-dd",
        language: "zh-CN",
        autoclose: true,//选中关闭
        minView: "month"//设置只显示到月份
    });

    $("#endLoginTime").datetimepicker({
        format: "yyyy-mm-dd",
        language: "zh-CN",
        autoclose: true,//选中关闭
        minView: "month"//设置只显示到月份
    });

</script>