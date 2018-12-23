<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/commons/taglibs.jsp" %>
<div class="am-cf am-padding am-padding-bottom-0">
    <div class="am-fl am-cf" style="width: 100%;">
        <strong class="am-text-primary am-text-lg">计费统计管理</strong> /
        <small>列表查询</small>
    </div>
</div>

<hr>

<div class="am-g">
    <form id="searchForm" class="am-form am-form-horizontal">
        <table id="searchTable">
            <tr>
                <td class="search_th search_th_frist"><label class="control-label">&nbsp&nbsp&nbsp手 机 号 ：</label></td>
                <td class="search_td"><input type="text" id="loginMobile" name="loginMobile" class="form-control"></td>

                <td class="search_th"><label class="control-label">检 测 时 间 ：</label></td>
                <td class="search_td">
                    <div class="am-datepicker-date">
                        <input type="text" id="startLoginTime" name="queryStartTime"
                               class="form-control am-datepicker-start" data-am-datepicker readonly>
                        <span style="float: left; line-height: 30px; height: 30px; width: 10%; text-align: center;">至</span>
                        <input type="text" id="endLoginTime" name="queryEndTime"
                               class="form-control am-datepicker-end" data-am-datepicker readonly>
                    </div>
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
                <th class="fontWeight_normal tdwidth80 center">品牌</th>
                <th class="fontWeight_normal tdwidth90 center">机型</th>
                <th class="fontWeight_normal tdwidth90 center">检测次数</th>
                <th class="fontWeight_normal tdwidth90 center">计费（元）</th>
                <th class="fontWeight_normal tdwidth90 center">计费时间</th>
            </tr>
            </thead>
            <tbody>

            </tbody>
        </table>
    </div>
</div>
<div class="am-g">
    <div class="am-u-sm-12">
        <table class="table">
            <tr>
                <td>
                    <div class="row">
                        <div class="col-md-6 col-sm-6 col-xs-12">
                            <h4>总计：
                                <sapn id="sumMoney">0</sapn>
                                （元）
                            </h4>
                        </div><!-- /.col -->
                    </div><!-- /.row --></td>
            </tr>
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
        "url": "${ctx}/recycle/totalPriceListForPage.do",
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
        {"data": "loginMobile", "class": ""},
        {"data": "brandName", "class": ""},
        {"data": "modelName", "class": ""},
        {"data": "number", "class": ""},
        {"data": "price", "class": ""},
        {"data": "strCreateTime", "class": ""}
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


    ]);

    dto.sScrollXInner = "130%";
    var myTable = $("#dt").DataTable(dto);
    window.onload=getTotalPrice();

    function getTotalPrice() {
        var loginMobile = $("#loginMobile").val();
        var queryStartTime = $("#startLoginTime").val();
        var queryEndTime = $("#endLoginTime").val();
        $.ajax({
            url: AppConfig.ctx + "/recycle/getTotalPrice.do",
            type: "POST",
            dataType: "json",
            data: {loginMobile: loginMobile, queryStartTime: queryStartTime, queryEndTime: queryEndTime},
            success: function (result) {
                if (result.success) {
                    $("#sumMoney").text(result.data);
                } else {
                    AlertText.tips("d_alert", "提示", "加载结算总计异常，请稍后重试");
                }
            },
            error: function () {
                AlertText.tips("d_alert", "提示", "加载结算总计异常，请稍后重试");
            }
        });
    }
    /**
     * 刷新列表
    */
    function refreshPage() {
        myTable.ajax.reload(null, false);
        getTotalPrice();
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