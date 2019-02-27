<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/commons/taglibs.jsp" %>
<div class="am-cf am-padding am-padding-bottom-0">
    <div class="am-fl am-cf" style="width: 100%;">
        <strong class="am-text-primary am-text-lg">卡号管理</strong> /
        <small>列表查询</small>
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
                    <select id="areaId"  name="areaId" class="form-control">
                        <option value="">--选择名称--</option>
                    </select>
                </td>
                <td></td>
            </tr>

            <tr>
                <td class="search_th search_th_frist"><label class="control-label">单位名称：</label></td>
                <td class="search_td"><input type="text" name="companyName" class="form-control"></td>

                <td class="search_th"><label class="control-label">固定电话：</label></td>
                <td class="search_td">
                    <select name="landline" class="form-control">
                        <option value="">--选择--</option>
                        <option value="1">联通</option>
                        <option value="2">电信</option>
                        <option value="3">移动</option>
                        <option value="4">无</option>
                    </select>
                </td>
                <td></td>
            </tr>


            <tr>
                <td class="search_th"><label class="control-label">宽带：</label></td>
                <td class="search_td">
                    <select name="broadband" class="form-control">
                        <option value="">--选择--</option>
                        <option value="1">联通</option>
                        <option value="2">电信</option>
                        <option value="3">移动</option>
                        <option value="4">无</option>
                    </select>
                </td>

                <td class="search_th"><label class="control-label">地址属性：</label></td>
                <td class="search_td">
                    <select name="addressType" class="form-control">
                        <option value="">--选择--</option>
                        <option value="1">楼宇</option>
                        <option value="2">园区</option>
                        <option value="3">市场</option>
                        <option value="4">沿街</option>
                        <option value="5">工厂</option>
                    </select>
                </td>
            </tr>

            <tr>
                <td class="search_th"><label class="control-label">通信需求：</label></td>
                <td class="search_td">
                    <select name="demand" class="form-control">
                        <option value="">--选择--</option>
                        <option value="1">无需求</option>
                        <option value="2">宽带体验</option>
                        <option value="3">专线体验</option>
                        <option value="4">战狼体验</option>
                        <option value="5">其他需求</option>
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
                        <!--
                        <button onclick="adminPush();" class="am-btn am-btn-default search_btn" type="button"> 当日号卡推送(超人-电渠) </button>
                        -->
                            <button onclick="expDataExcel();" type="button" class="am-btn am-btn-default"><span
                                    class="am-icon-file-excel-o"></span> 导出
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
                <th class="fontWeight_normal table-title center">创建时间</th>
                <th class="fontWeight_normal table-title center">县分</th>
                <th class="fontWeight_normal table-title center">支局</th>
                <th class="fontWeight_normal table-title center">包区</th>
                <th class="fontWeight_normal table-title center">单位名称</th>
                <th class="fontWeight_normal table-title center">固定电话</th>
                <th class="fontWeight_normal table-title center">宽带</th>
                <th class="fontWeight_normal table-title center">地址属性</th>
                <th class="fontWeight_normal table-title center ">详细地址</th>
                <th class="fontWeight_normal table-title center ">通信需求</th>
                <th class="fontWeight_normal table-title center ">备注</th>
                <th class="fontWeight_normal table-title center ">联系人/手机号</th>
                <th class="fontWeight_normal table-title center ">走访人/手机号</th>
            </tr>
            </thead>
            <tbody>

            </tbody>
        </table>
    </div>
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
        "url": "${ctx}/nbTelecomSJ/queryListForPage.do",
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
        {"data": "business_id", "class": "tdwidth50 center"},
        {"data": "createTime", "class": ""},
        {"data": "county", "class": ""},
        {"data": "branch_office", "class": ""},
        {"data": "area_name", "class": ""},
        {"data": "company_name", "class": ""},
        {"data": "landline", "class": ""},
        {"data": "broadband", "class": ""},
        {"data": "address_type", "class": ""},
        {"data": "address", "class": ""},
        {"data": "demand", "class": ""},
        {"data": "remarks", "class": ""},
        {"data": "coutomer_name", "class": ""},
        {"data": "manager_name", "class": ""}
    ]);
    //设置定义列的初始属性
    dto.setColumnDefs([
        {//复选框
            targets: 0,
            render: function (data, type, row, meta) {
                var context = {
                    func: [
                        {"id": row.business_id, "order": meta.row + 1}
                    ]
                };
                var html = template_chk(context);
                return html;
            }
        },
        {//号卡类型
            targets: -6,
            render: function (data, type, row, meta) {
                var state = "";
                switch (row.address_type) {
                    case 1:
                        state = "楼宇";
                        break;
                    case 2:
                        state = "园区";
                        break;
                    case 3:
                        state = "市场";
                        break;
                    case 4:
                        state = "沿街";
                        break;
                    case 5:
                        state = "工厂";
                        break;
                    default:
                        state = "";
                }
                return state;
            }
        },
        {
            targets: -4,
            render: function (data, type, row, meta) {
                var state = "";
                switch (row.demand) {
                    case 1:
                        state = "无需求";
                        break;
                    case 2:
                        state = "宽带体验";
                        break;
                    case 3:
                        state = "专线体验";
                        break;
                    case 4:
                        state = "战狼办理";
                        break;
                    case 5:
                        state = "其他需求";
                        break;
                    default:
                        state = "";
                }
                return state;
            }
        },
        {//分配
            targets: -2,
            render: function (data, type, row, meta) {
                    return row.coutomer_name+"/"+row.telephone;
            }
        },
        {//寄出
            targets: -1,
            render: function (data, type, row, meta) {
                return row.manager_name+"/"+row.manager_tel;
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
        window.open("${ctx}/file/download.do?fileId=24&ids=" + ids + params, "导出");
    }

</script>