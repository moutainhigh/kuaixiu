<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/commons/taglibs.jsp" %>
<div class="am-cf am-padding am-padding-bottom-0">
    <div class="am-fl am-cf" style="width: 100%;">
        <strong class="am-text-primary am-text-lg">群发手机号管理</strong> /
        <small>列表查询</small>
    </div>
</div>


<hr>
<div class="am-g">
    <div class="am-u-sm-12 am-u-md-4">
        <div class="am-btn-toolbar">
            <div class="am-btn-group am-btn-group-sm">
                <button onclick="sendSms();" type="button" class="am-btn am-btn-default"><span
                        class="am-icon-plus"></span> 选择规则/下单地址
                </button>
            </div>
        </div>
    </div>
</div>
<div class="am-g">
    <form id="searchForm" class="am-form am-form-horizontal">

    </form>
</div>

<div class="am-g">
    <div class="am-u-sm-12">
        <table id="dt" class="table table-striped table-bordered table-radius table-hover">
            <thead>
            <tr>
                <th class="fontWeight_normal tdwidth30">序号
                </th>
                <th class="fontWeight_normal tdwidth200 center">手机号</th>
            </tr>
            </thead>
            <tbody>

            </tbody>
        </table>
    </div>
</div>
<div id="modal-insertView" class="modal fade" tabindex="-1" role="dialog" aria-hidden="true" style="display: none;">
</div>

<script src="${webResourceUrl}/resource/js/address.js" type="text/javascript" charset="utf-8"></script>
<script type="text/javascript">
    function sendSms() {
        $("#modal-insertView").html("");
        $("#modal-insertView").load("${ctx}/groupShort/toGroupSendSms.do", function () {
            func_after_model_load(this);
        });
    }
    //自定义datatable的数据
    var dto = new DtOptions();
    //设置数据刷新路径
    dto.ajax = {
        "url": "${ctx}/groupShort/groupMobileForPage.do",
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
        {"data": "mobile", "class": ""}
    ]);
    //设置定义列的初始属性
    dto.setColumnDefs([
        {//复选框
            targets: 0,
            render: function (data, type, row, meta) {
//                    var context = {
//                        func: [
//                            {"id": row.id, "order": meta.row + 1}
//                        ]
//                    };
//                    var html = template_chk(context);
                    return meta.row + 1;
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

</script>