<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/commons/taglibs.jsp" %>
<div class="am-cf am-padding am-padding-bottom-0">
    <div class="am-fl am-cf" style="width: 100%;">
        <strong class="am-text-primary am-text-lg">维修门店管理</strong> /
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


<div class="am-g">
    <form id="searchForm" class="am-form am-form-horizontal">

        <table>
            <tr style="width:100%;">
                <td style="width:2%;"></td>
                <td style="width:4%;">地址：</td>
                <td style="width:6%;">
                    <select id="queryProvince" name="queryProvince"
                            onchange="fn_select_address(2, this.value, '', 'query');" class="form-control-inline">
                        <option value="">--请选择--</option>
                        <c:forEach items="${provinceL }" var="item" varStatus="i">
                            <option value="${item.areaId }">${item.area }</option>
                        </c:forEach>
                    </select>
                </td>
                <td style="width:6%;">
                    <select id="queryCity" name="queryCity" onchange="fn_select_address(3, this.value, '', 'query');"
                            class="form-control-inline" style="display: none;">
                        <option value="">--请选择--</option>
                    </select>
                </td>
                <td style="width:6%;">
                    <select id="queryCounty" name="queryCounty" class="form-control-inline" style="display: none;">
                        <option value="">--请选择--</option>
                    </select>
                </td>

                <td style="width:5%;"></td>
                <td style="width:7%;">是否支持寄修：</td>
                <td style="width:5%;">
                    <select id="query_isSendRepair" name="query_isSendRepair" class="form-control-inline">
                        <option value="">--请选择--</option>
                        <option value="1">是</option>
                        <option value="0">否</option>
                    </select>
                </td>
                <td style="width:3%;"></td>
                <td style="width:8%;">是否支持以旧换新：</td>
                <td style="width:5%;">
                    <select id="query_oldtonew" name="query_oldtonew" class="form-control-inline">
                        <option value="">--请选择--</option>
                        <option value="0">是</option>
                        <option value="1">否</option>
                    </select>
                </td>

                <td style="width:5%;"></td>
                <td style="width:15%;">
                    <div class="am-input-group am-input-group-sm">
                        <input type="text" style="display:none">
                        <input type="text" onkeydown="refreshPage()" name="query_name" class="am-form-field" placeholder="维修门店名称">
                        <span class="am-input-group-btn">
		          <button onclick="refreshPage();" class="am-btn am-btn-default" type="button">搜索</button>
		        </span>
                    </div>
                </td>
            </tr>
        </table>
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
                <th class="fontWeight_normal tdwidth100">门店名称</th>
                <th class="fontWeight_normal tdwidth80 center">门店账号</th>
                <th class="fontWeight_normal tdwidth140 center">负责人/手机号</th>
                <th class="fontWeight_normal table-title center">电话</th>
                <th class="fontWeight_normal table-title center">地址</th>
                <th class="fontWeight_normal tdwidth80 center">工程师数量</th>
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
        "url": "${ctx}/shop/queryListForPage.do",
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
        {"data": "name", "class": ""},
        {"data": "code", "class": ""},
        {"data": "managerName", "class": ""},
        {"data": "tel", "class": ""},
        {"data": "fullAddress", "class": ""},
        {"data": "engineerNum", "class": ""},
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
            targets: 1,//门店名称
            render: function (data, type, row, meta) {
                var html = "<a href=\"javascript:void(0);\" onclick=\"toDetail('" + row.id + "');\">" + row.name + "</a>";
                return html;
            }
        },
        {//负责人
            targets: 3,
            render: function (data, type, row, meta) {
                return row.managerName + "/" + row.managerMobile;
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
        func_reload_page("${ctx}/shop/detail.do?id=" + id);
    }

    function addBtnClick() {
        $("#modal-insertView").html("");
        $("#modal-insertView").load("${ctx}/shop/add.do", function () {
            func_after_model_load(this);
        });
    }

    function editBtnClick(id) {
        $("#modal-insertView").html("");
        $("#modal-insertView").load("${ctx}/shop/edit.do?id=" + id, function () {
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
        window.open("${ctx}/file/download.do?fileId=3&ids=" + ids + params, "导出");
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

    function delBtnClick(id) {
        AlertText.tips("d_confirm", "删除提示", "确定要删除吗？", function () {
            //加载等待
            AlertText.tips("d_loading");
            var url_ = AppConfig.ctx + "/shop/delete.do";
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