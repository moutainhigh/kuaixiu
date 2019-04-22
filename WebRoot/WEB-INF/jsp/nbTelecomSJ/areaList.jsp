<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/commons/taglibs.jsp" %>
<div class="am-cf am-padding am-padding-bottom-0">
    <div class="am-fl am-cf" style="width: 100%;">
        <strong class="am-text-primary am-text-lg">商机甩单管理</strong> /
        <small>包区列表</small>
    </div>
</div>

<hr>

<div class="am-g">
    <form id="searchForm" class="form form-horizontal">
        <table id="searchTable">
            <tr>
                <td class="search_th"><label class="control-label">创建时间：</label></td>
                <td class="search_td">
                    <div class="am-datepicker-date">
                        <input type="text" id="query_startTime" name="queryStartTime"
                               class="form-control am-datepicker-start" data-am-datepicker readonly>
                        <span style="float: left; line-height: 30px; height: 30px; width: 10%; text-align: center;">至</span>
                        <input type="text" id="query_endTime" name="queryEndTime"
                               class="form-control am-datepicker-end" data-am-datepicker readonly>
                    </div>
                </td>

                <td class="search_th"><label class="control-label">县分 ：</label></td>
                <td class="search_td">
                    <select id="countyId" name="countyId" onchange="CountyChange(this.value);" class="form-control">
                        <option value="">--选择类别--</option>
                        <c:forEach items="${counties}" var="item" varStatus="i">
                            <option value="${item.countyId}">${item.county}</option>
                        </c:forEach>
                    </select>
                </td>
            </tr>


            <tr>
                <td class="search_th"><label class="control-label">支局 ：</label></td>
                <td class="search_td">
                    <select id="officeId" name="officeId" onchange="OfficeChange(this.value);" class="form-control">
                        <option value="">--选择类别--</option>
                    </select>
                </td>

                <td class="search_th"><label class="control-label">包区 ：</label></td>
                <td class="search_td">
                    <select id="areaId" name="areaId" class="form-control">
                        <option value="">--选择名称--</option>
                    </select>
                </td>
                <td></td>
            </tr>

            <tr>
                <td class="search_th"><label class="control-label">包区人：</label></td>
                <td class="search_td"><input type="text" name="areaPerson" class="form-control"></td>
                <td class="search_th"><label class="control-label">包区人手机号：</label></td>
                <td class="search_td"><input type="text" name="personTel" class="form-control"></td>
            </tr>

        </table>
        <div class="form-group">
            <div class="am-u-sm-12 am-u-md-6">
                <div class="am-btn-toolbar">
                    <div class="am-btn-group am-btn-group-sm m20">
                        <button onclick="refreshPage();" class="am-btn am-btn-default search_btn" type="button"> 搜 索
                        </button>
                        <!--
                        <button onclick="adminPush();" class="am-btn am-btn-default search_btn" type="button"> 当日号卡推送(超人-电渠) </button>
                        -->
                        <button onclick="expDataExcel();" type="button" class="am-btn am-btn-default"><span
                                class="am-icon-file-excel-o"></span> 导出
                        </button>
                        <button onclick="addArea();" type="button" class="am-btn am-btn-default"><span
                                class="am-icon-file-excel-o"></span> 新增
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
                <th class="fontWeight_normal tdwidth50"><input id="check_all_btn" onclick="checkAll(this)"
                                                               type="checkbox"/>序号
                </th>
                <th class="fontWeight_normal tdwidth70">创建时间</th>
                <th class="fontWeight_normal tdwidth40">县分</th>
                <th class="fontWeight_normal tdwidth70">支局</th>
                <th class="fontWeight_normal tdwidth80">包区</th>
                <th class="fontWeight_normal table-title tdwidth80 ">包区人/<br/>手机号</th>
                <th class="fontWeight_normal tdwidth150 center">操作</th>
            </tr>
            </thead>
            <tbody>

            </tbody>
        </table>
    </div>
</div>
<div id="modal-insertView" class="modal fade" tabindex="-1" role="dialog" aria-hidden="true" style="display: none;">
    <%@ include file="addArea.jsp" %>
</div>
<div id="modal-updateView" class="modal fade" tabindex="-1" role="dialog" aria-hidden="true" style="display: none;">
</div>

<script type="text/javascript">
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

    //自定义datatable的数据
    var dto = new DtOptions();
    //设置数据刷新路径
    dto.ajax = {
        "url": "${ctx}/nbTelecomSJ/nbAreaForPage.do",
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
        {"data": "officeId", "class": " center"},
        {"data": "createTime", "class": ""},
        {"data": "county", "class": ""},
        {"data": "branchOffice", "class": ""},
        {"data": "areaName", "class": ""},
        {"data": "areaPerson", "class": ""},
        {"defaultContent": "操作", "class": ""}
    ]);
    //设置定义列的初始属性
    dto.setColumnDefs([
        {//复选框
            targets: 0,
            render: function (data, type, row, meta) {
                var context = {
                    func: [
                        {"id": row.officeId, "order": meta.row + 1}
                    ]
                };
                var html = template_chk(context);
                return html;
            }
        },

        {//寄出
            targets: -2,
            render: function (data, type, row, meta) {
                if (row.areaPerson == "" && row.personTel == "") {
                    return "";
                } else if (row.areaPerson == null && row.personTel == null) {
                    return "";
                } else {
                    return row.areaPerson + "/" + row.personTel;
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
                            "fn": "editBtnClick(\'" + row.officeId + "\')",
                            "icon": "am-icon-pencil-square-o",
                            "class": "am-text-secondary"
                        },
                        {
                            "name": "删除",
                            "fn": "delBtnClick(\'" + row.officeId + "\')",
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
        myTable.ajax.reload(null, false);
    }
    function addArea() {
        $("#modal-insertView").modal("show");
    }

    function editBtnClick(id) {
        $("#modal-updateView").html("");
        $("#modal-updateView").load("${ctx}/nbTelecomSJ/updateArea.do?offIceId=" + id, function () {
            func_after_model_load(this);
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


    function CountyChange(id) {
        $("#officeId option[value!='']").remove();
        $("#areaId option[value!='']").remove();
        var url = AppConfig.ctx + "/nbTelecomSJ/getOffice.do";
        $.get(url, {countyId: id}, function (result) {
            if (!result.success) {
                return false;
            }
            var json = result.result;
            var select_html = '';
            if (json.length > 0) {
                for (a in json) {
                    select_html += '<option value="' + json[a]['officeId'] + '">' + json[a]['branchOffice'] + '</option>';
                }
            }
            $("#officeId").append(select_html);
        });
    }

    function OfficeChange(id) {
        $("#areaId option[value!='']").remove();
        var countyId = $("#countyId").val();
        if (id) {
            var url = AppConfig.ctx + "/nbTelecomSJ/getArea.do";
            $.get(url, {officeId: id, countyId: countyId}, function (result) {
                if (!result.success) {
                    return false;
                }
                var json = result.result;
                var select_html = '';
                if (json.length > 0) {
                    for (a in json) {
                        select_html += '<option value="' + json[a]['areaId'] + '">' + json[a]['areaPerson'] + '</option>';
                    }
                }
                $("#areaId").append(select_html);
            });
        }
    }
    function delBtnClick(id) {
        AlertText.tips("d_confirm", "删除提示", "确定要删除吗？", function () {
            //加载等待
            AlertText.tips("d_loading");
            var url_ = AppConfig.ctx + "/nbTelecomSJ/deleteNBArea.do";
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
        window.open("${ctx}/file/download.do?fileId=28&ids=" + ids + params, "导出");
    }

</script>