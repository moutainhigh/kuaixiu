<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/commons/taglibs.jsp" %>
<div class="am-cf am-padding am-padding-bottom-0">
    <div class="am-fl am-cf" style="width: 100%;">
        <strong class="am-text-primary am-text-lg">订单管理</strong> /
        <small>派单列表查询</small>
    </div>
</div>

<hr>

<div class="am-g">
    <form id="searchForm" class="form form-horizontal">
        <table id="searchTable">
            <input type="hidden" id="type" name="type" value="2"/><br/>
            <tr>
                <td class="search_th "><label class="control-label">单  号 ：</label></td>
                <td class="search_td"><input type="text" name="orderNo" class="form-control"></td>
                <td class="search_th"><label class="control-label">状   态：</label></td>
                <td class="search_td">
                    <select name="state" class="form-control">
                        <option value="">--选择状态--</option>
                        <option value="100">待审核</option>
                        <option value="200">待指派</option>
                        <option value="300">待施工</option>
                        <option value="400">待竣工</option>
                        <option value="500">已完成</option>
                        <option value="600">未通过</option>
                    </select>
                </td>
            </tr>


            <tr>
                <td class="search_th"><label class="control-label">下 单 时 间 ：</label></td>
                <td class="search_td">
                    <div class="am-datepicker-date">
                        <input type="text" id="query_startTime" name="query_startTime"
                               class="form-control am-datepicker-start" data-am-datepicker readonly>
                        <span style="float: left; line-height: 30px; height: 30px; width: 10%; text-align: center;">至</span>
                        <input type="text" id="query_endTime" name="query_endTime"
                               class="form-control am-datepicker-end" data-am-datepicker readonly>
                    </div>
                </td>

                <td class="search_th"><label class="control-label">提交人/账号：</label></td>
                <td class="search_td"><input type="text" name="createUser" class="form-control"></td>
            </tr>
            <tr>
                <td class="search_th "><label class="control-label">企业名字：</label></td>
                <td class="search_td"><input type="text" name="companyName" class="form-control"></td>
                <td class="search_th "><label class="control-label">负责人姓名：</label></td>
                <td class="search_td"><input type="text" name="responsibleName" class="form-control"></td>
            </tr>
            <tr>
                <td class="search_th "><label class="control-label">负责人身份证号：</label></td>
                <td class="search_td"><input type="number" name="responsibleIdNumber" class="form-control"></td>
            </tr>

        </table>
        <div class="form-group">
            <div class="am-u-sm-12 am-u-md-6">
                <div class="am-btn-toolbar">
                    <div class="am-btn-group am-btn-group-sm m20">
                        <button onclick="refreshPage();" class="am-btn am-btn-default search_btn" type="button"> 开始查找
                        </button>
                        <c:if test="${loginUserType==1}">
                            <button onclick="expDataExcel();" type="button" class="am-btn am-btn-default"><span
                                    class="am-icon-file-excel-o"></span> 导出
                            </button>
                        </c:if>
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
                <th class="fontWeight_normal tdwidth100">单号</th>
                <th class="fontWeight_normal tdwidth80">创建时间</th>
                <th class="fontWeight_normal tdwidth50">类型</th>
                <th class="fontWeight_normal tdwidth90">提交人/账号</th>
                <th class="fontWeight_normal tdwidth90">CRM编号</th>
                <th class="fontWeight_normal tdwidth90">企业名字</th>
                <th class="fontWeight_normal table-title tdwidth80">企业负责人/电话</th>
                <th class="fontWeight_normal table-title tdwidth80">负责人姓名/身份证号</th>
                <th class="fontWeight_normal tdwidth100">需求</th>
                <th class="fontWeight_normal tdwidth50">状态</th>
                <th class="fontWeight_normal tdwidth70">操作</th>
            </tr>
            </thead>
            <tbody>

            </tbody>
        </table>
    </div>
</div>


<script src="${webResourceUrl}/resource/js/address.js" type="text/javascript" charset="utf-8"></script>
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
        "url": "${ctx}/sj/order/queryListForPage.do",
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
        {"data": "orderNo", "class": ""},
        {"data": "strCreateTime", "class": ""},
        {"data": "type", "class": ""},
        {"data": "createUserid", "class": ""},
        {"data": "crmNo", "class": ""},
        {"data": "companyName", "class": ""},
        {"data": "person", "class": ""},
        {"data": "responsibleName", "class": ""},
        {"data": "projectNames", "class": ""},
        {"data": "state", "class": ""},
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
                var state = '';
                switch (row.type) {
                    case 1:
                        state = "商机";
                        break;
                    case 2:
                        state = "派单";
                        break;
                }
                return state;
            }
        },
        {
            targets: 4,
            render: function (data, type, row, meta) {
                return row.createName+"/<br/>"+row.createUserid;
            }
        },
        {
            targets: -5,
            render: function (data, type, row, meta) {
                    return row.person+"/<br/>"+row.phone;
            }
        },
        {
            targets: -4,
            render: function (data, type, row, meta) {
                if(row.responsibleName!=null&&row.responsibleIdNumber!=null){
                    return row.responsibleName+"/<br/>"+row.responsibleIdNumber;
                }else{
                    return "";
                }
            }
        },
        {//订单状态  待审核100，带指派200，待施工300，待竣工400，已完成500，未通过600
            targets: -2,
            render: function (data, type, row, meta) {
                var state = "";
                switch (row.state) {
                    case 100:
                        state = "待审核";
                        break;
                    case 200:
                        state = "待指派";
                        break;
                    case 300:
                        state = "待施工";
                        break;
                    case 400:
                        state = "待竣工";
                        break;
                    case 500:
                        state = "已完成";
                        break;
                    case 600:
                        state = "未通过";
                        break;
                    default:
                        state = "未知";
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
                            "name": "查看",
                            "fn": "showOrderDetail(\'" + row.id + "\')",
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

    /**
     * 查看订单详情
     */
    function showOrderDetail(id) {
        func_reload_page("${ctx}/sj/order/detail.do?id=" + id);
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
        window.open("${ctx}/sj/file/download.do?fileId=36&ids=" + ids + params, "导出");
    }
</script>