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
        <div class="am-form-group">
            <div class="am-u-sm-12 am-u-md-5">
                <div class="am-btn-toolbar">
                    <div class="am-btn-group am-btn-group-sm">
                        <button onclick="createCoupon();" type="button" class="am-btn am-btn-default"><span
                                class="am-icon-plus"></span> 生成优惠券
                        </button>
                        <button onclick="expDataExcel();" type="button" class="am-btn am-btn-default"><span
                                class="am-icon-file-excel-o"></span> 导出
                        </button>
                        <button onclick="batchDelBtnClick();" type="button" class="am-btn am-btn-default"><span
                                class="am-icon-trash-o"></span> 删除
                        </button>
                        <button onclick="delByBatchIdBtnClick();" type="button" class="am-btn am-btn-default"><span
                                class="am-icon-trash-o"></span> 按批次删除
                        </button>
                        <button onclick="editByBatchIdBtnClick();" type="button" class="am-btn am-btn-default"><span
                                class="am-icon-pencil-square-o"></span> 按批次编辑
                        </button>
                    </div>
                </div>
            </div>
            <div class="am-u-sm-12 am-u-md-1 col-sm-offset-1" style="padding: 0px 5px;">
                <div class="am-input-group am-input-group-sm">
                    <input type="text" name="query_batchId" class="am-form-field" style="float: left;" placeholder="批次">
                </div>
            </div>
            <div class="am-u-sm-12 am-u-md-1" style="padding: 0px 5px;">
                <div class="am-input-group am-input-group-sm">
                    <select name="query_isUse" class="am-form-field">
                        <option value="">是否使用&nbsp;&nbsp;&nbsp;&nbsp;</option>
                        <option value="0">未使用</option>
                        <option value="1">已使用</option>
                    </select>
                </div>
            </div>
            <div class="am-u-sm-12 am-u-md-1" style="padding: 0px 5px;">
                <div class="am-input-group am-input-group-sm">
                    <select name="query_isReceive" class="am-form-field">
                        <option value="">是否领用&nbsp;&nbsp;&nbsp;&nbsp;</option>
                        <option value="0">未领用</option>
                        <option value="1">已领用</option>
                    </select>
                </div>
            </div>

            <div class="am-u-sm-12 am-u-md-1" style="padding: 0px 5px;">
                <div class="am-input-group am-input-group-sm">
                    <select name="query_type" class="am-form-field">
                        <option value="">类型&nbsp;&nbsp;&nbsp;&nbsp;</option>
                        <option value="0">维修优惠券</option>
                        <option value="1">换新优惠券</option>
                        <option value="2">换膜优惠券</option>
                    </select>
                </div>
            </div>

            <div class="am-u-sm-12 am-u-md-2" style="padding-left: 5px; padding-right: 3px;">
                <div class="am-input-group am-input-group-sm">
                    <input type="text" onkeydown="refreshPage()" name="query_code" class="am-form-field" style="float: left;" placeholder="优惠码">
                    <span class="am-input-group-btn">
		          <button onclick="refreshPage();" class="am-btn am-btn-default" type="button">搜索</button>
		        </span>
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
                <th class="fontWeight_normal tdwidth60 center">批次</th>
                <th class="fontWeight_normal tdwidth80 center">优惠券号码</th>
                <th class="fontWeight_normal tdwidth90 center">优惠券类型</th>
                <th class="fontWeight_normal tdwidth90 center">优惠券名称</th>
                <th class="fontWeight_normal tdwidth50 center">优惠券<br/>金额</th>
                <th class="fontWeight_normal tdwidth160 center">有效时间</th>
                <th class="fontWeight_normal tdwidth40 center">状态</th>
                <th class="fontWeight_normal tdwidth60 center">是否使用</th>
                <th class="fontWeight_normal tdwidth80 center">使用时间</th>
                <th class="fontWeight_normal tdwidth60 center">是否领用</th>
                <th class="fontWeight_normal tdwidth60 center">品牌通用</th>
                <th class="fontWeight_normal tdwidth60 center">故障通用</th>
                <th class="fontWeight_normal tdwidth80 center">领用号码</th>
                <th class="fontWeight_normal tdwidth150 center">操作</th>
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
        "url": "${ctx}/coupon/queryListForPage.do",
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
        {"data": "batchId", "class": ""},
        {"data": "couponCode", "class": ""},
        {"data": "type", "class": ""},
        {"data": "couponName", "class": ""},
        {"data": "couponPrice", "class": ""},
        {"data": "beginTime", "class": ""},
        {"data": "status", "class": ""},
        {"data": "isUse", "class": ""},
        {"data": "useTime", "class": ""},
        {"data": "isReceive", "class": ""},
        {"data": "isBrandCurrency", "class": ""},
        {"data": "isProjectCurrency", "class": ""},
        {"data": "receiveMobile", "class": ""},
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
        { //优惠券类型
            targets: 3,
            render: function (data, type, row, meta) {
                var state = "";
                switch (row.type) {
                    case 0:
                        state = "维修优惠券";
                        break;
                    case 1:
                        state = "以旧换新优惠券";
                        break;
                    case 2:
                        state = "换膜优惠券";
                }
                return state;
            }
        },
        {//有效时间
            targets: 6,
            render: function (data, type, row, meta) {
                return row.beginTime + "-" + row.endTime;
            }
        },
        {//状态
            targets: -8,
            render: function (data, type, row, meta) {
                var state = "";
                switch (row.status) {
                    case 1:
                        state = "可用";
                        break;
                    case 2:
                        state = "失效";
                        break;
                    default:
                        state = "异常";
                }
                return state;
            }
        },
        {//是否使用
            targets: -7,
            render: function (data, type, row, meta) {
                var state = "";
                switch (row.isUse) {
                    case 0:
                        state = "否";
                        break;
                    case 1:
                        state = "是";
                        break;
                    default:
                        state = "异常";
                }
                return state;
            }
        },
        {//是否使用
            targets: -5,
            render: function (data, type, row, meta) {
                var state = "";
                switch (row.isReceive) {
                    case 0:
                        state = "否";
                        break;
                    case 1:
                        state = "是";
                        break;
                    default:
                        state = "异常";
                }
                return state;
            }
        },
        {//是否使用
            targets: -4,
            render: function (data, type, row, meta) {
                var state = "";
                switch (row.isBrandCurrency) {
                    case 0:
                        state = "否";
                        break;
                    case 1:
                        state = "是";
                        break;
                    default:
                        state = "异常";
                }
                return state;
            }
        },
        {//是否使用
            targets: -3,
            render: function (data, type, row, meta) {
                var state = "";
                switch (row.isProjectCurrency) {
                    case 0:
                        state = "否";
                        break;
                    case 1:
                        state = "是";
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
                if (row.isUse == 1) {
                    var context = {
                        func: [
                            {
                                "name": "查看",
                                "fn": "editBtnClick(\'" + row.id + "\')",
                                "icon": "am-icon-search",
                                "class": "am-text-secondary"
                            },
                        ]
                    };
                    var html = template_btn(context);
                    return html;
                }
                else {
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