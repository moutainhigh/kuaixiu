<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/commons/taglibs.jsp" %>
<div class="am-cf am-padding am-padding-bottom-0">
    <div class="am-fl am-cf" style="width: 100%;">
        <strong class="am-text-primary am-text-lg">维修订单管理</strong> /
        <small>超出范围订单列表查询</small>
    </div>
</div>

<hr>

<div class="am-g">
    <form id="searchForm" class="form form-horizontal">
        <table id="searchTable">
        </table>
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
                <th class="fontWeight_normal tdwidth80">用户id</th>
                <th class="fontWeight_normal table-title tdwidth80">联系人/<br/>手机号</th>
                <th class="fontWeight_normal tdwidth70">品牌名称</th>
                <th class="fontWeight_normal tdwidth90">机型名称</th>
                <th class="fontWeight_normal tdwidth90">维修方式</th>
                <th class="fontWeight_normal tdwidth100">订单金额(元)</th>
                <th class="fontWeight_normal tdwidth70">是否使用优惠券</th>
                <th class="fontWeight_normal tdwidth80">优惠码金额(元)</th>
                <th class="fontWeight_normal tdwidth80">系统来源</th>
                <th class="fontWeight_normal tdwidth80">地区名称</th>
                <th class="fontWeight_normal tdwidth80">详细地址</th>
                <th class="fontWeight_normal tdwidth70">用户备注</th>
                <th class="fontWeight_normal tdwidth70">创建时间</th>
            </tr>
            </thead>
            <tbody>

            </tbody>
        </table>
    </div>
</div>

<!-- 新增弹窗 end -->
<div id="modal-insertView" class="modal fade" tabindex="-1" role="dialog" aria-hidden="true" style="display: none;">
    <%@ include file="agreedTime.jsp" %>
</div>
<!-- 新增弹窗 end -->
<script src="${webResourceUrl}/resource/js/address.js" type="text/javascript" charset="utf-8"></script>
<script type="text/javascript">
    //自定义datatable的数据
    var dto = new DtOptions();
    //设置数据刷新路径
    dto.ajax = {
        "url": "${ctx}/rangeOrder/queryListForPage.do",
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
        {"data": "customerId", "class": ""},
        {"data": "customerName", "class": ""},
        {"data": "brandName", "class": ""},
        {"data": "modelName", "class": ""},
        {"data": "repairType", "class": ""},
        {"data": "orderPrice", "class": ""},
        {"data": "isUseCoupon", "class": ""},
        {"data": "couponPrice", "class": ""},
        {"data": "fromSystem", "class": ""},
        {"data": "areas", "class": ""},
        {"data": "address", "class": ""},
        {"data": "postscript", "class": ""},
        {"data": "strInTime", "class": ""}
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
            targets: 2,
            render: function (data, type, row, meta) {
                return row.customerName + "<br/>/" + row.mobile;
            }
        },
        {
            targets: 4,
            render: function (data, type, row, meta) {
                return row.modelName + "<br/>（" + row.color + "）";
            }
        },
        {
            targets: 5,
            render: function (data, type, row, meta) {
                var state = '';
                switch (row.repairType) {
                    case 0:
                        state = "上门维修";
                        break;
                    case 1:
                        state = "到店维修";
                        break;
                    case 2:
                        state = "返店维修";
                        break;
                    case 3:
                        state = "寄修";
                        break;
                    case 4:
                        state = "点对点";
                        break;
                }
                return state;
            }
        },
        {
            targets: 7,
            render: function (data, type, row, meta) {
                var state = '';
                switch (row.isUseCoupon) {
                    case 0:
                        state = "否";
                        break;
                    case 1:
                        state = "是";
                        break;
                }
                return state;
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
        window.open("${ctx}/file/download.do?fileId=7&ids=" + ids + params, "导出");
    }

</script>