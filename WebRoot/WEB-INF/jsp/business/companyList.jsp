<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/commons/taglibs.jsp" %>
<div class="am-cf am-padding am-padding-bottom-0">
    <div class="am-fl am-cf" style="width: 100%;">
        <strong class="am-text-primary am-text-lg">订单管理</strong> /
        <small>列表查询</small>
    </div>
</div>

<hr>

<div class="am-g">
    <form id="searchForm" class="form form-horizontal">
        <table id="searchTable">
            <tr>
                <td class="search_th "><label class="control-label">单位名字 ：</label></td>
                <td class="search_td"><input type="text" name="companyName" class="form-control"></td>
                <td class="search_th "><label class="control-label">区域 ：</label></td>
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
            </tr>


            <tr>
                <td class="search_th"><label class="control-label">注 册 时 间 ：</label></td>
                <td class="search_td">
                    <div class="am-datepicker-date">
                        <input type="text" id="query_startTime" name="query_startTime"
                               class="form-control am-datepicker-start" data-am-datepicker readonly>
                        <span style="float: left; line-height: 30px; height: 30px; width: 10%; text-align: center;">至</span>
                        <input type="text" id="query_endTime" name="query_endTime"
                               class="form-control am-datepicker-end" data-am-datepicker readonly>
                    </div>
                </td>

                <td class="search_th"><label class="control-label">对接人/电话号：</label></td>
                <td class="search_td"><input type="text" name="personPhone" class="form-control"></td>
            </tr>

        </table>
        <div class="form-group">
            <div class="am-u-sm-12 am-u-md-6">
                <div class="am-btn-toolbar">
                    <div class="am-btn-group am-btn-group-sm m20">
                        <button onclick="refreshPage();" class="am-btn am-btn-default search_btn" type="button"> 开始查找
                        </button>
                        <button onclick="addBtnClick();" type="button" class="am-btn am-btn-default"><span
                                class="am-icon-plus"></span> 企业新增
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
                <th class="fontWeight_normal tdwidth100">单位名字</th>
                <th class="fontWeight_normal tdwidth80">单位地址</th>
                <th class="fontWeight_normal tdwidth50">区域</th>
                <th class="fontWeight_normal tdwidth90">对接人/电话</th>
                <th class="fontWeight_normal tdwidth90">施工项目</th>
                <th class="fontWeight_normal table-title tdwidth80">注册时间</th>
                <th class="fontWeight_normal tdwidth100">施工人数</th>
                <th class="fontWeight_normal tdwidth50">完成单数</th>
                <th class="fontWeight_normal tdwidth50">状态</th>
                <th class="fontWeight_normal tdwidth70">操作</th>
            </tr>
            </thead>
            <tbody>

            </tbody>
        </table>
    </div>
</div>

<!-- 新增弹窗 end -->
<div id="modal-insertCompanyView" class="modal fade" tabindex="-1" role="dialog" aria-hidden="true" style="display: none;">
</div>
<!-- 新增弹窗 end -->
<!-- 新增弹窗 end -->
<div id="modal-insertWorkerView" class="modal fade" tabindex="-1" role="dialog" aria-hidden="true" style="display: none;">
</div>
<!-- 新增弹窗 end -->
<script src="${webResourceUrl}/resource/js/address.js" type="text/javascript" charset="utf-8"></script>
<script type="text/javascript">

    function addBtnClick() {
            $("#modal-insertCompanyView").html("");
            $("#modal-insertCompanyView").load("${ctx}/sj/order/toRegisterCompany.do", function () {
                func_after_model_load(this);
            });

    }

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
        "url": "${ctx}/sj/order/queryCompanyListForPage.do",
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
        {"data": "companyName", "class": ""},
        {"data": "address", "class": ""},
        {"data": "areaAddress", "class": ""},
        {"data": "person", "class": ""},
        {"data": "projectName", "class": ""},
        {"data": "createTime", "class": ""},
        {"data": "personNum", "class": ""},
        {"data": "endOrderNum", "class": ""},
        {"data": "isCancel", "class": ""},
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
            targets: 4,
            render: function (data, type, row, meta) {
                return row.person+"/<br/>"+row.phone;
            }
        },
        {
            targets: -2,
            render: function (data, type, row, meta) {
                var state = "";
                switch (row.isCancel) {
                    case 1:
                        state = "已注销";
                        break;
                    case 0:
                        state = "正常";
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
                if(row.isCancel==1){
                    var context = {
                        func: [
                            {
                                "name": "恢复",
                                "fn": "cancel(\'" + row.loginId + "\',isCancel=0)",
                                "icon": "am-icon-pencil-square-o",
                                "class": "am-text-secondary"
                            }
                        ]
                    };
                    var html = template_btn(context);
                    return html;
                }else{
                    var context = {
                        func: [
                            {
                                "name": "注销",
                                "fn": "cancel(\'" + row.loginId + "\',isCancel=1)",
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

    function cancel(loginId,isCancel){
        var html1="";
        var html2="";
        if(isCancel==1){
            html1="注销提示";
            html2="确定要注销吗?"
        }else{
            html1="恢复提示";
            html2="确定要恢复吗?"
        }
        AlertText.tips("d_confirm", html1, html2, function(){
            //加载等待
            AlertText.tips("d_loading");
            var url_ = AppConfig.ctx + "/sj/company/isCancellation.do";
            var data_ = {loginId: loginId,type:3,isCancel:isCancel};
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