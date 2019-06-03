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

        </table>

        <%--<div class="form-group">--%>
        <%--<div class="am-u-sm-12 am-u-md-6">--%>
        <%--<div class="am-btn-toolbar">--%>
        <%--<div class="am-btn-group am-btn-group-sm m20">--%>
        <%--<button onclick="refreshPage();" class="am-btn am-btn-default search_btn" type="button"> 筛 选--%>
        <%--</button>--%>
        <%--</div>--%>
        <%--</div>--%>
        <%--</div>--%>
        <%--</div>--%>
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
                <th class="fontWeight_normal tdwidth60 center">活动标识</th>
                <th class="fontWeight_normal tdwidth60 center">来源</th>
                <th class="fontWeight_normal tdwidth90 center">是否默认</th>
                <th class="fontWeight_normal tdwidth60 center">头图片高度</th>
                <th class="fontWeight_normal tdwidth60 center">头图片宽度</th>
                <th class="fontWeight_normal tdwidth60 center">边框图片高度</th>
                <th class="fontWeight_normal tdwidth60 center">边框图片宽度</th>
                <th class="fontWeight_normal tdwidth60 center">加价券图片色值</th>
                <th class="fontWeight_normal tdwidth60 center">加价券图片高度</th>
                <th class="fontWeight_normal tdwidth60 center">加价券图片宽度</th>
                <th class="fontWeight_normal tdwidth60 center">活动规则</th>
                <th class="fontWeight_normal tdwidth60 center">创建时间</th>
                <th class="fontWeight_normal tdwidth60 center">操作</th>
            </tr>
            </thead>
            <tbody>

            </tbody>
        </table>
    </div>
</div>

<script src="${webResourceUrl}/resource/js/address.js" type="text/javascript" charset="utf-8"></script>
<script type="text/javascript">

    //自定义datatable的数据
    var dto = new DtOptions();
    //设置数据刷新路径
    dto.ajax = {
        "url": "${ctx}/hsActivity/activityForPage.do",
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
        {"data": "activityLabel", "class": ""},
        {"data": "source", "class": ""},
        {"data": "isDefault", "class": ""},
        {"data": "headHeight", "class": ""},
        {"data": "headWide", "class": ""},
        {"data": "marginHeight", "class": ""},
        {"data": "marginWide", "class": ""},
        {"data": "centercolorValue", "class": ""},
        {"data": "centerHeight", "class": ""},
        {"data": "centerWide", "class": ""},
        {"data": "activityRole", "class": ""},
        {"data": "strCreateTime", "class": ""},
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
                if (row.isDefault == 1) {
                    return "是";
                } else {
                    return "否";
                }
            }
        },
        {
            targets: -1,
            render: function (data, type, row, meta) {
                if (row.isDefault == 1) {
                    var context = {
                        func: [
                            {
                                "name": "已默认",
//                                "fn": "editBtnClick(\'" + row.id + "\')",
                                "icon": "am-icon-search",
                                "class": "am-text-secondary"
                            },
                        ]
                    };
                    var html = template_btn(context);
                    return html;
                } else {
                    var context = {
                        func: [
                            {
                                "name": "默认",
                                "fn": "editBtnClick(\'" + row.id + "\')",
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

    dto.sScrollXInner = "100%";
    var myTable = $("#dt").DataTable(dto);

    /**
     * 刷新列表
     */
    function refreshPage() {
        $("#pageStatus").val(1);
        myTable.ajax.reload(null, false);
    }

    function editBtnClick(id) {
        AlertText.tips("d_confirm", "默认提示", "确定要默认显示吗？", function () {
            //加载等待
            AlertText.tips("d_loading");
            var url_ = AppConfig.ctx + "/hsActivity/updateIsDefault.do";
            var data_ = {activityId: id,isDefault:1};
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
                        AlertText.tips("d_alert", "提示", result.resultMessage);
                        return false;
                    }
                    //隐藏等待
                    AlertText.hide();
                }
            });
        });
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