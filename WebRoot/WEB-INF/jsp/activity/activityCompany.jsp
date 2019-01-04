<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/commons/taglibs.jsp" %>
<div class="am-cf am-padding am-padding-bottom-0">
    <div class="am-fl am-cf" style="width: 100%;">
        <strong class="am-text-primary am-text-lg">活动管理</strong> /
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

                <td class="search_th"><label class="control-label">预 约 时 间 ：</label></td>
                <td class="search_td">
                    <div class="am-datepicker-date">
                        <input type="text" id="startLoginTime" name="startTime"
                               class="form-control am-datepicker-start" data-am-datepicker readonly>
                        <span style="float: left; line-height: 30px; height: 30px; width: 10%; text-align: center;">至</span>
                        <input type="text" id="endLoginTime" name="endTime"
                               class="form-control am-datepicker-end" data-am-datepicker readonly>
                    </div>
                </td>
            </tr>
            <tr>
                <td class="search_th search_th_frist"><label class="control-label">创 建 时 间 ：</label></td>
                <td class="search_td">
                    <div class="am-datepicker-date">
                        <input type="text" id="startTestTime" name="queryStartTime"
                               class="form-control am-datepicker-start" data-am-datepicker readonly>
                        <span style="float: left; line-height: 30px; height: 30px; width: 10%; text-align: center;">至</span>
                        <input type="text" id="endTestTime" name="queryEndTime"
                               class="form-control am-datepicker-end" data-am-datepicker readonly>
                    </div>
                </td>
                <td class="search_th"><label class="control-label"> 预 约 状 态：</label></td>
                <td class="search_td">
                    <select id="query_categoryId" name="isEnd" class="form-control">
                        <option value="">--选择状态--</option>
                        <option value="0">预约中</option>
                        <option value="1">未开始</option>
                        <option value="2">已结束</option>
                    </select>
                </td>
            </tr>
            <tr>
                <td class="search_th search_th_frist"><label class="control-label">渠道标识：</label></td>
                <td class="search_td"><input type="text" name="activityIdentification" class="form-control"></td>

            </tr>
            <div hidden="hidden">
                <%--/images/activityCompany/default.png 任务记录.txt--%>
                <a id="savePng"  href="/images/activityCompany/default.png" download="文件名.png">点击下载</a>
            </div>
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
                <th class="fontWeight_normal tdwidth60 center">创建时间</th>
                <th class="fontWeight_normal tdwidth60 center">企业名称</th>
                <th class="fontWeight_normal tdwidth90 center">预约时间</th>
                <th class="fontWeight_normal tdwidth60 center">快修业务说明</th>
                <th class="fontWeight_normal tdwidth60 center">电信增值业务说明</th>
                <th class="fontWeight_normal tdwidth60 center">电信业务负责人</th>
                <th class="fontWeight_normal tdwidth60 center">渠道标识</th>
                <th class="fontWeight_normal tdwidth60 center">预约状态</th>
                <th class="fontWeight_normal tdwidth60 center">操作</th>
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
        "url": "${ctx}/activityCompany/getActivityForPage.do",
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
        {"data": "createTime", "class": ""},
        {"data": "companyName", "class": ""},
        {"data": "startTime", "class": ""},
        {"data": "kxBusiness", "class": ""},
        {"data": "dxIncrementBusiness", "class": ""},
        {"data": "dxBusinessPerson", "class": ""},
        {"data": "activityIdentification", "class": ""},
        {"data": "isEnd", "class": ""},
        {"defaultContent": "操作", "class": ""}
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
            targets: 3,
            render: function (data, type, row, meta) {
                if (row.startTime != null && row.endTime != null) {
                    return row.startTime + "至" + row.endTime;
                } else {
                    return "";
                }
            }
        },
        {//复选框
            targets: -4,
            render: function (data, type, row, meta) {
                return row.dxBusinessPerson + "/<br/>" + row.dxBusinessPersonNumber;
            }
        },
        {//复选框
            targets: -2,
            render: function (data, type, row, meta) {
                if (row.isEnd == 0) {
                    return "预约中";
                } else if (row.isEnd == 1) {
                    return "未开始";
                } else {
                    return "已结束";
                }
            }
        },
        {
            targets: -1,
            render: function (data, type, row, meta) {
//                if (row.isUse == 1) {
                var context = {
                    func: [
                        {
                            "name": "编辑",
                            "fn": "editBtnClick(\'" + row.activityIdentification + "\')",
                            "icon": "am-icon-search",
                            "class": "am-text-secondary"
                        },
                        {
                            "name": "保存二维码",
                            "fn": "saveBtnClick(\'" + row.activityIdentification + "\',\'"+row.companyName+"\')",
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

    function saveBtnClick(Identification,companyName) {
        $.ajax({
            url: "${ctx}/activityCompany/getCode.do",
            type: "POST",
            data: {Identification:Identification},
            dataType: "json",
            success: function (result) {
                if (result.success) {
                    $('#savePng').attr('href',result.result.path);
                    $('#savePng').attr('download',companyName);
                    document.getElementById("savePng").click();
                } else {
                    AlertText.tips("d_alert", "提示", result.result.resultMessage);
                }
            },
            error: function () {
                alert("异常");
            }

        })
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
     *  查看
     */
    function editBtnClick(identification) {
        func_reload_page("${ctx}/activityCompany/getActivity.do?activityIdentification=" + identification);
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
    $("#startTestTime").datetimepicker({
        format: "yyyy-mm-dd",
        language: "zh-CN",
        autoclose: true,//选中关闭
        minView: "month"//设置只显示到月份
    });

    $("#endTestTime").datetimepicker({
        format: "yyyy-mm-dd",
        language: "zh-CN",
        autoclose: true,//选中关闭
        minView: "month"//设置只显示到月份
    });
    $("#startSubmitTime").datetimepicker({
        format: "yyyy-mm-dd",
        language: "zh-CN",
        autoclose: true,//选中关闭
        minView: "month"//设置只显示到月份
    });

    $("#endSubmitTime").datetimepicker({
        format: "yyyy-mm-dd",
        language: "zh-CN",
        autoclose: true,//选中关闭
        minView: "month"//设置只显示到月份
    });

</script>