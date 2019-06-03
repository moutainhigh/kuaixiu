<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/commons/taglibs.jsp" %>
<div class="am-cf am-padding am-padding-bottom-0">
    <div class="am-fl am-cf" style="width: 100%;">
        <strong class="am-text-primary am-text-lg">优惠券管理</strong> /
        <small>列表查询</small>
    </div>
</div>

<hr>

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
                <th class="fontWeight_normal tdwidth60 center">活动标识</th>
                <th class="fontWeight_normal tdwidth80 center">加价券规则名字</th>
                <th class="fontWeight_normal tdwidth90 center">加价券名字</th>
                <th class="fontWeight_normal tdwidth90 center">加价券类型</th>
                <th class="fontWeight_normal tdwidth50 center">加价券金额</th>
                <th class="fontWeight_normal tdwidth160 center">满加额度上限</th>
                <th class="fontWeight_normal tdwidth40 center">加价额度</th>
                <th class="fontWeight_normal tdwidth60 center">加价规则描述</th>
                <th class="fontWeight_normal tdwidth80 center">备注</th>
                <th class="fontWeight_normal tdwidth60 center">加价额度上限</th>
                <th class="fontWeight_normal tdwidth60 center">开始时间</th>
                <th class="fontWeight_normal tdwidth60 center">结束时间</th>
                <th class="fontWeight_normal tdwidth80 center">活动结束时间</th>
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
        {"data": "activityLabel", "class": ""},
        {"data": "nameLabel", "class": ""},
        {"data": "couponName", "class": ""},
        {"data": "pricingType", "class": ""},
        {"data": "couponPrice", "class": ""},
        {"data": "upperLimit", "class": ""},
        {"data": "subtractionPrice", "class": ""},
        {"data": "ruleDescription", "class": ""},
        {"data": "note", "class": ""},
        {"data": "addPriceUpper", "class": ""},
        {"data": "beginTime", "class": ""},
        {"data": "endTime", "class": ""},
        {"data": "activityEndTime", "class": ""},
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
            targets: 4,
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

    function toDetail(id) {
        func_reload_page("${ctx}/coupon/detail.do?id=" + id);
    }

    function createCoupon() {
        func_reload_page("${ctx}/coupon/create.do");
    }

    /**
     *  编辑
     */
    function editBtnClick(id) {
        $("#modal-insertView").html("");
        $("#modal-insertView").load("${ctx}/coupon/edit.do?id=" + id, function () {
            func_after_model_load(this);
        });
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
        window.open("${ctx}/file/download.do?fileId=12&ids=" + ids + params, "导出");
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
        if (ids == "") {
            AlertText.tips("d_alert", "提示", "请选择删除项！");
        }
        else {
            delBtnClick(ids);
        }
    }

    /**
     * 按批次删除
     */
    function delByBatchIdBtnClick() {
        AlertText.tips("d_confirm", "按批次删除", "请输入要删除批次：<input id='batchId' style='margin-left: 80px;' />", function () {
            var batchId = $("#batchId").val();
            if (batchId == "") {
                AlertText.tips("d_alert", "提示", "请输入要删除批次！", function () {
                    delByBatchIdBtnClick();
                });
                return false;
            }
            delByBatchId(batchId);
        });
    }

    function delByBatchId(batchId) {
        AlertText.tips("d_confirm", "按批次删除", "确定要删除当前批次吗？确定后会删除当前批次未使用的优惠券。", function () {
            //加载等待
            AlertText.tips("d_loading");
            var url_ = AppConfig.ctx + "/coupon/delete.do";
            var data_ = {batchId: batchId};
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

    /**
     *  按批次编辑优惠券
     */
    function editByBatchIdBtnClick() {
        AlertText.tips("d_confirm", "按批次编辑", "请输入要编辑批次：<input id='batchId' style='margin-left: 80px;' />", function () {
            var batchId = $("#batchId").val();
            if (batchId == "") {
                AlertText.tips("d_alert", "提示", "请输入要编辑批次！", function () {
                    editByBatchIdBtnClick();
                });
                return false;
            }
            editByBatchId(batchId);
        });
    }


    function editByBatchId(batchId) {
        $("#modal-insertView").html("");
        $("#modal-insertView").load("${ctx}/coupon/editByBatch.do?batchId=" + batchId, function () {
            func_after_model_load(this);
        });
    }


    function delBtnClick(id) {
        AlertText.tips("d_confirm", "删除提示", "确定要删除吗？", function () {
            //加载等待
            AlertText.tips("d_loading");
            var url_ = AppConfig.ctx + "/coupon/delete.do";
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
</script>