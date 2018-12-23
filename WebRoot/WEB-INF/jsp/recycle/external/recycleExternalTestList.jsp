<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/commons/taglibs.jsp" %>
<div class="am-cf am-padding am-padding-bottom-0">
    <div class="am-fl am-cf" style="width: 100%;">
        <strong class="am-text-primary am-text-lg">检测计费管理</strong> /
        <small>列表查询</small>
    </div>
</div>

<hr>

<div class="am-g">
    <form id="searchForm" class="am-form am-form-horizontal">
        <table id="searchTable">
            <tr>
                <td class="search_th search_th_frist"><label class="control-label">&nbsp&nbsp&nbsp手 机 号 ：</label></td>
                <td class="search_td"><input type="text" name="loginMobile" class="form-control"></td>

                <td class="search_th"><label class="control-label">登 录 时 间 ：</label></td>
                <td class="search_td">
                    <div class="am-datepicker-date">
                        <input type="text" id="startLoginTime" name="startLoginTime"
                               class="form-control am-datepicker-start" data-am-datepicker readonly>
                        <span style="float: left; line-height: 30px; height: 30px; width: 10%; text-align: center;">至</span>
                        <input type="text" id="endLoginTime" name="endLoginTime"
                               class="form-control am-datepicker-end" data-am-datepicker readonly>
                    </div>
                </td>
            </tr>
            <tr>
                <td class="search_th"><label class="control-label">检 测 时 间 ：</label></td>
                <td class="search_td">
                    <div class="am-datepicker-date">
                        <input type="text" id="startTestTime" name="startTestTime"
                               class="form-control am-datepicker-start" data-am-datepicker readonly>
                        <span style="float: left; line-height: 30px; height: 30px; width: 10%; text-align: center;">至</span>
                        <input type="text" id="endTestTime" name="endTestTime"
                               class="form-control am-datepicker-end" data-am-datepicker readonly>
                    </div>
                </td>

                <td class="search_th"><label class="control-label">成 单 时 间 ：</label></td>
                <td class="search_td">
                    <div class="am-datepicker-date">
                        <input type="text" id="startSubmitTime" name="startSubmitTime"
                               class="form-control am-datepicker-start" data-am-datepicker readonly>
                        <span style="float: left; line-height: 30px; height: 30px; width: 10%; text-align: center;">至</span>
                        <input type="text" id="endSubmitTime" name="endSubmitTime"
                               class="form-control am-datepicker-end" data-am-datepicker readonly>
                    </div>
                </td>
            </tr>
            <tr>
                <td class="search_th"><label class="control-label"> 类目：</label></td>
                <td class="search_td">
                    <select id="query_categoryId" name="categoryId" onchange="CategoryChange(this.value);"
                            class="form-control">
                        <option value="">--选择类目--</option>
                        <option value="0">手机</option>
                        <option value="1">平板</option>
                    </select>
                </td>

            </tr>
            <tr>
                <td class="search_th"><label class="control-label"> 手机品牌：</label></td>
                <td class="search_td">
                    <select id="query_brand" name="brandId" onchange="brandChange(this.value);" class="form-control">
                        <option value="">--选择品牌--</option>
                    </select>
                </td>
                <td class="search_th"><label class="control-label">维 修 机 型 ：</label></td>
                <td class="search_td">
                    <select id="query_model" name="modelId" class="form-control">
                        <option value="">--选择机型--</option>
                    </select>
                </td>

            </tr>
            <tr>
                <td class="search_th"><label class="control-label">是否成单 ：</label></td>
                <td>
                    <select name="isOrder" class="am-form-field">
                        <option value="">请选择&nbsp;&nbsp;&nbsp;&nbsp;</option>
                        <option value="0">未成单</option>
                        <option value="1">已成单</option>
                    </select>
                </td>
            </tr>

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
                <th class="fontWeight_normal tdwidth60 center">手机号</th>
                <th class="fontWeight_normal tdwidth90 center">登录时间</th>
                <th class="fontWeight_normal tdwidth60 center">品牌</th>
                <th class="fontWeight_normal tdwidth60 center">机型</th>
                <th class="fontWeight_normal tdwidth60 center">检测时间</th>
                <th class="fontWeight_normal tdwidth60 center">成单时间</th>
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
        "url": "${ctx}/recycle/getListForPage.do",
        "data": function (d) {
            //将表单中的查询条件追加到请求参数中
            var array = $("#searchForm").serializeArray();
            $.each(array, function () {
                d[this.name] = this.value;
            });
            CategoryChange(null);
        }
    };

    //设置数据列
    dto.setColumns([
        {"data": "id", "class": "center"},
        {"data": "loginMobile", "class": ""},
        {"data": "loginTime", "class": ""},
        {"data": "brandName", "class": ""},
        {"data": "modelName", "class": ""},
        {"data": "createTime", "class": ""},
        {"data": "updateTime", "class": ""},
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
        {
            targets: -1,
            render: function (data, type, row, meta) {
//                if (row.isUse == 1) {
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

    function createCoupon() {
        func_reload_page("${ctx}/recycle/createCoupon.do");
    }

    /**
     *  查看
     */
    function editBtnClick(id) {
        func_reload_page("${ctx}/recycle/testDetail.do?id=" + id);
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


    function CategoryChange(id) {
        $("#query_brand option[value!='']").remove();
//        if (id) {
            var url = AppConfig.ctx + "/recycle/getTestbrandList.do";
            $.get(url, {categoryId: id}, function (result) {
                if (!result.success) {
                    return false;
                }
                var json = result.data;
                var select_html = '';
                if (json.length > 0) {
                    for (a in json) {
                        select_html += '<option value="' + json[a]['brandid'] + '">' + json[a]['brandname'] + '</option>';
                    }
                }
                $("#query_brand").append(select_html);
            });
//        }
    }


    function brandChange(id) {
        $("#query_model option[value!='']").remove();
        var categoryId = $("#query_categoryId").val();
        if (id) {
            var url = AppConfig.ctx + "/recycle/getTestModelList.do";
            $.get(url, {brandId: id, categoryId: categoryId}, function (result) {
                if (!result.success) {
                    return false;
                }
                var json = result.data;
                var select_html = '';
                if (json.length > 0) {
                    for (a in json) {
                        select_html += '<option value="' + json[a]['productid'] + '">' + json[a]['modelname'] + '</option>';
                    }
                }
                $("#query_model").append(select_html);
            });
        }
    }
</script>