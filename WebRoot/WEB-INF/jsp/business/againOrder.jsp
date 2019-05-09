<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/commons/taglibs.jsp" %>
<div class="modal-backdrop fade in"></div>
<div class="modal-dialog">
    <div class="modal-content">

        <div class="modal-title"><span>查询单位</span>
            <a href="javascript: void(0);" class="close" onclick="clean()" data-dismiss="modal" aria-label="Close">&times;</a>
        </div>
        <div class="modal-body">

            <form id="againOrderSearchForm" method="post" class="form-horizontal">
                <label class="col-sm-3 control-label">筛选条件</label><br/>
                <input type="hidden" id="orderId" name="orderId"/><br/>
                <input type="hidden" id="projectIds" name="projectIds"/><br/>
                <div class="form-group">
                    <input type="hidden" id="addAreas" name="addAreas">
                    <label for="addArea" class="col-sm-3 control-label"><span style="color:red">*</span> 地址</label>
                    <div class="col-sm-9">
                        <select id="addProvince" name="addProvince"
                                onchange="fn_select_address(2, this.value, '', 'add');" class="form-control-inline">
                            <option value="">--请选择--</option>
                            <c:forEach items="${provinceL }" var="item" varStatus="i">
                                <option value="${item.areaId }">${item.area }</option>
                            </c:forEach>
                        </select>

                        <select id="addCity" name="addCity" onchange="fn_select_address(3, this.value, '', 'add');"
                                class="form-control-inline" style="display: none;">
                            <option value="">--请选择--</option>
                        </select>

                        <select id="addCounty" name="addCounty" onchange="fn_select_address(4, this.value, '', 'add');"
                                class="form-control-inline" style="display: none;">
                            <option value="">--请选择--</option>
                        </select>

                        <select id="addStreet" name="addStreet" class="form-control-inline" style="display: none;">
                            <option value="">--请选择--</option>
                        </select>
                    </div>
                </div>
                <div class="form-group">
                    <div class="col-sm-9 col-sm-offset-3">
                        <input type="text" id="addAddress" name="addAddress" class="form-control" placeholder="请输入详细地址">
                    </div>
                </div>
                <button type="submit" class="hide" id="againOrderBtn"></button>
            </form>
        </div>
        <div class="modal-footer">
            <button type="button" id="queryListAgainOrder" class="btn modal-btn"><span
                    class="am-icon-save icon-save"></span>开始查找
            </button>
            <button type="button" id="addMissAgainOrderBtn" onclick="clean()" class="btn modal-btn" data-dismiss="modal"
                    aria-label="Close"><span class="am-icon-close icon-close"></span>取消
            </button>
        </div>

        <div class="am-g">
            <div class="am-u-sm-12">
                <table id="dtAgain" class="table table-striped table-bordered table-radius table-hover">
                    <thead>
                    <tr>
                        <th class="fontWeight_normal tdwidth50">序号</th>
                        <th class="fontWeight_normal tdwidth90">单位名字</th>
                        <th class="fontWeight_normal tdwidth90">地址</th>
                        <th class="fontWeight_normal tdwidth160">对接人/电话</th>
                        <th class="fontWeight_normal tdwidth160">施工项目</th>
                        <th class="fontWeight_normal tdwidth90">企业人数</th>
                        <th class="fontWeight_normal tdwidth90">完成订单数</th>
                        <th class="fontWeight_normal tdwidth70">操作</th>
                    </tr>
                    </thead>
                    <tbody>

                    </tbody>
                </table>
            </div>
        </div>

    </div><!-- /.modal-content -->
</div>
<!-- /.modal-dialog -->

<script type="text/javascript">

    <%--清空--%>
    function clean() {
        $('#dtAgain tbody').html("");
        $('#engName').val("");
        $('#engCode').val("");
    }

    var myTable2 = null;

    $("#queryListAgainOrder").click(function () {
        if (myTable2 == null) {
            myTable2 = $("#dtAgain").DataTable(dtAgain);
        } else {
            myTable2.ajax.reload(null, false);
        }
    });

    //自定义datatable的数据
    var dtAgain = new DtOptions();
    //设置数据刷新路径
    dtAgain.ajax = {
        "url": "${ctx}/sj/order/queryCompanyForPage.do",
        "data": function (d) {
            //将表单中的查询条件追加到请求参数中
            var array = $("#againOrderSearchForm").serializeArray();
            $.each(array, function () {
                d[this.name] = this.value;
            });
        }
    };

    //设置数据列
    dtAgain.setColumns([
        {"data": "id", "class": "center"},
        {"data": "companyName", "class": ""},
        {"data": "address", "class": ""},
        {"data": "person", "class": ""},
        {"data": "projectNames", "class": ""},
        {"data": "personNum", "class": ""},
        {"data": "endOrderNum", "class": ""},
        {"defaultContent": "操作", "class": ""}
    ]);
    //设置定义列的初始属性
    dtAgain.setColumnDefs([
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
                return row.person + "/" + row.phone;
            }
        },
        {
            targets: -1,
            render: function (data, type, row, meta) {
                var context = {
                    func: [
                        {
                            "name": "指派",
                            "fn": "resetOrder(\'" + row.loginId + "\')",
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

    function resetOrder(loginId) {
        AlertText.tips("d_confirm", "系统提示", "确定要重新派单吗？。", function () {
            var orderId = $("#orderId").val();
            //加载等待
            AlertText.tips("d_loading");
            var url_ = AppConfig.ctx + "/sj/order/assign.do";
            var data_ = {orderId: orderId, userId: loginId};
            $.ajax({
                url: url_,
                data: data_,
                type: "POST",
                dataType: "json",
                success: function (result) {
                    if (result.success) {
                        AlertText.tips("d_alert", "提示", "派单成功", function () {
                            clean();
                            refreshPage();
                            //全部更新完后关闭弹窗
                            $("#addMissAgainOrderBtn").click();
                        });
                    } else {
                        AlertText.tips("d_alert", "提示", "错误");
                        clean();
                        return false;
                    }
                },
                error: function () {
                    AlertText.tips("d_alert", "提示", "系统异常，请稍后再试");
                    loading_hide();
                    clean();
                    isLoading = false;
                }
            });
        });
    }


</script>