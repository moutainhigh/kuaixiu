<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/commons/taglibs.jsp" %>
<div class="am-cf am-padding am-padding-bottom-0">
    <div class="am-fl am-cf" style="width: 100%;">
        <strong class="am-text-primary am-text-lg">加价券规则管理</strong> /
        <small>列表查询</small>
    </div>
</div>


<hr>
<div class="am-g">
    <div class="am-u-sm-12 am-u-md-4">
        <div class="am-btn-toolbar">
            <div class="am-btn-group am-btn-group-sm">
                <button onclick="addBtnClick();" type="button" class="am-btn am-btn-default"><span
                        class="am-icon-plus"></span> 新增
                </button>
            </div>
        </div>
    </div>
</div>

<div class="am-g">
    <form id="searchForm" class="am-form am-form-horizontal">

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
                <th class="fontWeight_normal tdwidth80 center">加价券规则名字</th>
                <th class="fontWeight_normal tdwidth90 center">加价券名字</th>
                <th class="fontWeight_normal tdwidth90 center">加价券类型</th>
                <th class="fontWeight_normal tdwidth50 center">加价金额</th>
                <th class="fontWeight_normal tdwidth50 center">最高加价金额</th>
                <th class="fontWeight_normal tdwidth50 center">订单金额上限</th>
                <th class="fontWeight_normal tdwidth50 center">订单金额下限</th>
                <th class="fontWeight_normal tdwidth60 center">加价规则描述</th>
                <th class="fontWeight_normal tdwidth80 center">备注</th>
                <th class="fontWeight_normal tdwidth50 center">操作</th>
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

    function addBtnClick() {
        $("#modal-insertView").html("");
        $("#modal-insertView").load("${ctx}/hsActivity/toAddCouponRole.do", function () {
            func_after_model_load(this);
        });
    }
    //自定义datatable的数据
    var dto = new DtOptions();
    //设置数据刷新路径
    dto.ajax = {
        "url": "${ctx}/hsActivity/couponRoleForPage.do",
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
        {"data": "id", "class": "tdwidth50 center"},
        {"data": "nameLabel", "class": ""},
        {"data": "couponName", "class": ""},
        {"data": "pricingType", "class": ""},
        {"data": "couponPrice", "class": ""},
        {"data": "addPriceUpper", "class": ""},
        {"data": "upperLimit", "class": ""},
        {"data": "subtractionPrice", "class": ""},
        {"data": "ruleDescription", "class": ""},
        {"data": "note", "class": ""},
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
        {//状态
            targets: 3,
            render: function (data, type, row, meta) {
                var state = "";
                switch (row.pricingType) {
                    case 1:
                        state = "百分比加价";
                        break;
                    case 2:
                        state = "固定加价";
                        break;
                    default:
                        state = "异常";
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
                            "name": "删除",
                            "fn": "delBtnClick(\'" + row.id + "\')",
                            "icon": "am-icon-search",
                            "class": "am-text-secondary"
                        }
                    ]
                };
                var html = template_btn(context);
                return html;
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


    function delBtnClick(id) {
        AlertText.tips("d_confirm", "删除提示", "确定要删除吗？", function () {
            //加载等待
            AlertText.tips("d_loading");
            var url_ = AppConfig.ctx + "/hsActivity/delHsCouponRole.do";
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
                        AlertText.tips("d_alert", "提示", result.resultMessage);
                        return false;
                    }
                    //隐藏等待
                    AlertText.hide();
                }
            });
        });
    }
</script>