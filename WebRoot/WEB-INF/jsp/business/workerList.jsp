<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/commons/taglibs.jsp" %>
<div class="am-cf am-padding am-padding-bottom-0">
    <div class="am-fl am-cf" style="width: 100%;">
        <strong class="am-text-primary am-text-lg">员工管理</strong> /
        <small>列表查询</small>
    </div>
</div>

<hr>

<div class="am-g">
    <form id="searchForm" class="form form-horizontal">
        <table id="searchTable">


        </table>
        <div class="form-group">
            <div class="am-u-sm-12 am-u-md-6">
                <div class="am-btn-toolbar">
                    <div class="am-btn-group am-btn-group-sm m20">
                        <button onclick="addBtnClick();" type="button" class="am-btn am-btn-default"><span
                                class="am-icon-plus"></span> 员工新增
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
                <th class="fontWeight_normal tdwidth100">登录ID</th>
                <th class="fontWeight_normal tdwidth80">姓名</th>
                <th class="fontWeight_normal tdwidth50">手机号</th>
                <th class="fontWeight_normal tdwidth90">隶属单位</th>
                <th class="fontWeight_normal tdwidth90">创建时间</th>
                <th class="fontWeight_normal table-title tdwidth60">进行中订单数量</th>
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
<div id="modal-insertView" class="modal fade" tabindex="-1" role="dialog" aria-hidden="true" style="display: none;">
</div>
<!-- 新增弹窗 end -->
<div id="modal-insertWorkerView" class="modal fade" tabindex="-1" role="dialog" aria-hidden="true" style="display: none;">
</div>
<script src="${webResourceUrl}/resource/js/address.js" type="text/javascript" charset="utf-8"></script>
<script type="text/javascript">

    function addBtnClick() {
            $("#modal-insertWorkerView").html("");
            $("#modal-insertWorkerView").load("${ctx}/sj/order/toRegisterWorker.do", function () {
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
        "url": "${ctx}/sj/order/queryWorkerForPage.do",
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
        {"data": "login_id", "class": ""},
        {"data": "name", "class": ""},
        {"data": "phone", "class": ""},
        {"data": "companyName", "class": ""},
        {"data": "create_time", "class": ""},
        {"data": "building_num", "class": ""},
        {"data": "is_cancel", "class": ""},
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
            targets: -2,
            render: function (data, type, row, meta) {
                var state = "";
                switch (row.is_cancel) {
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
                if (row.is_cancel == 1) {
                    var context = {
                        func: [
                            {
                                "name": "恢复",
                                "fn": "cancel(\'" + row.login_id + "\',isCancel=0)",
                                "icon": "am-icon-pencil-square-o",
                                "class": "am-text-secondary"
                            }
                        ]
                    };
                    var html = template_btn(context);
                    return html;
                } else {
                    var context = {
                        func: [
                            {
                                "name": "注销",
                                "fn": "cancel(\'" + row.login_id + "\',isCancel=1)",
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
            var data_ = {loginId: loginId,type:8,isCancel:isCancel};
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