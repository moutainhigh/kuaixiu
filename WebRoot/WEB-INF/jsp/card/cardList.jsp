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
                <td class="search_th search_th_frist"><label class="control-label">&nbsp&nbsp&nbspICCID ：</label></td>
                <td class="search_td"><input type="text" name="query_iccid" class="form-control"></td>

                <td class="search_th"><label class="control-label">是否已寄出 ：</label></td>
                <td class="search_td">
                    <select name="query_isUse" class="form-control">
                        <option value="">--选择状态--</option>
                        <option value="1">是</option>
                        <option value="0">否</option>
                    </select>
                </td>
                <td></td>
            </tr>

            <tr>
                <td class="search_th search_th_frist"><label class="control-label">&nbsp&nbsp&nbsp批次号 ：</label></td>
                <td class="search_td"><input type="text" name="query_batchId" class="form-control"></td>

                <td class="search_th"><label class="control-label">是否已分配 ：</label></td>
                <td class="search_td">
                    <select name="query_isDistribution" class="form-control">
                        <option value="">--选择状态--</option>
                        <option value="1">是</option>
                        <option value="0">否</option>
                    </select>
                </td>
                <td></td>
            </tr>


            <tr>
                <td class="search_th"><label class="control-label">号卡类别 ：</label></td>
                <td class="search_td">
                    <select name="query_cardType" class="form-control">
                        <option value="">--选择类别--</option>
                        <c:forEach items="${type}" var="item" varStatus="i">
                            <option value="${item.cardNo}">${item.cardName}</option>
                        </c:forEach>
                    </select>
                </td>


                <td class="search_th"><label class="control-label">号卡名称 ：</label></td>
                <td class="search_td">
                    <select name="query_cardName" class="form-control">
                        <option value="">--选择名称--</option>
                        <c:forEach items="${name}" var="item" varStatus="i">
                            <option value="${item.cardNo}">${item.cardName}</option>
                        </c:forEach>
                    </select>
                </td>
                <td></td>
            </tr>


            <tr>
                <td class="search_th"><label class="control-label">导入时间：</label></td>
                <td class="search_td">
                    <div class="am-datepicker-date">
                        <input type="text" id="query_startTime" name="query_startTime"
                               class="form-control am-datepicker-start" data-am-datepicker readonly>
                        <span style="float: left; line-height: 30px; height: 30px; width: 10%; text-align: center;">至</span>
                        <input type="text" id="query_endTime" name="query_endTime"
                               class="form-control am-datepicker-end" data-am-datepicker readonly>
                    </div>
                </td>


                <td class="search_th"><label class="control-label">分配时间：</label></td>
                <td class="search_td">
                    <div class="am-datepicker-date">
                        <input type="text" id="query_startDistributionTime" name="query_startDistributionTime"
                               class="form-control am-datepicker-start" data-am-datepicker readonly>
                        <span style="float: left; line-height: 30px; height: 30px; width: 10%; text-align: center;">至</span>
                        <input type="text" id="query_endDistributionTime" name="query_endDistributionTime"
                               class="form-control am-datepicker-end" data-am-datepicker readonly>
                    </div>
                </td>
                <td></td>
            </tr>

            <tr>
                <c:if test="${userId ne 'kf014'}">
                    <td class="search_th"><label class="control-label">地市 ：</label></td>
                    <td class="search_td">
                        <select name="query_city" class="form-control">
                            <option value="">--选择地市--</option>
                            <c:forEach items="${list}" var="item" varStatus="i">
                                <option value="${item}">${item}</option>
                            </c:forEach>
                        </select>
                    </td>
                </c:if>


                <td class="search_th"><label class="control-label">电渠状态：</label></td>
                <td class="search_td">
                    <select name="query_pushStatus" class="form-control">
                        <option value="">--选择状态--</option>
                        <option value="0">不满足推送条件</option>
                        <option value="1">推送失败</option>
                        <option value="2">推送成功</option>
                    </select>
                </td>
                <td></td>
            </tr>


            <tr>
                <td class="search_th"><label class="control-label">转转推送：</label></td>
                <td class="search_td">
                    <div class="am-datepicker-date">
                        <input type="text" id="query_startZhuangTime" name="query_startZhuangTime"
                               class="form-control am-datepicker-start" data-am-datepicker readonly>
                        <span style="float: left; line-height: 30px; height: 30px; width: 10%; text-align: center;">至</span>
                        <input type="text" id="query_endZhuangTime" name="query_endZhuangTime"
                               class="form-control am-datepicker-end" data-am-datepicker readonly>
                    </div>
                </td>

                <td class="search_th"><label class="control-label">电渠推送：</label></td>
                <td class="search_td">
                    <div class="am-datepicker-date">
                        <input type="text" id="query_startTelecomTime" name="query_startTelecomTime"
                               class="form-control am-datepicker-start" data-am-datepicker readonly>
                        <span style="float: left; line-height: 30px; height: 30px; width: 10%; text-align: center;">至</span>
                        <input type="text" id="query_endTelecomTime" name="query_endTelecomTime"
                               class="form-control am-datepicker-end" data-am-datepicker readonly>
                    </div>
                </td>
                <td></td>


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
                        <c:if test="${userId eq 'kf014'}">
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
                <th class="fontWeight_normal table-title center">批次号</th>
                <th class="fontWeight_normal table-title center">ICCID</th>
                <th class="fontWeight_normal table-title center">号卡类型</th>
                <th class="fontWeight_normal table-title center">号卡名称</th>
                <th class="fontWeight_normal table-title center">所属本地网</th>
                <th class="fontWeight_normal table-title center">分配</th>
                <th class="fontWeight_normal table-title center">寄出</th>
                <th class="fontWeight_normal table-title center">寄出时间</th>
                <th class="fontWeight_normal table-date tdwidth130  center">寄出物流号</th>
                <th class="fontWeight_normal table-set tdwidth130">操作</th>
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

    $("#query_startDistributionTime").datetimepicker({
        format: "yyyy-mm-dd",
        language: "zh-CN",
        autoclose: true,//选中关闭
        minView: "month"//设置只显示到月份
    });

    $("#query_endDistributionTime").datetimepicker({
        format: "yyyy-mm-dd",
        language: "zh-CN",
        autoclose: true,//选中关闭
        minView: "month"//设置只显示到月份
    });


    $("#query_startZhuangTime").datetimepicker({
        format: "yyyy-mm-dd",
        language: "zh-CN",
        autoclose: true,//选中关闭
        minView: "month"//设置只显示到月份
    });

    $("#query_endZhuangTime").datetimepicker({
        format: "yyyy-mm-dd",
        language: "zh-CN",
        autoclose: true,//选中关闭
        minView: "month"//设置只显示到月份
    });


    $("#query_startTelecomTime").datetimepicker({
        format: "yyyy-mm-dd",
        language: "zh-CN",
        autoclose: true,//选中关闭
        minView: "month"//设置只显示到月份
    });

    $("#query_endTelecomTime").datetimepicker({
        format: "yyyy-mm-dd",
        language: "zh-CN",
        autoclose: true,//选中关闭
        minView: "month"//设置只显示到月份
    });


    //自定义datatable的数据
    var dto = new DtOptions();
    //设置数据刷新路径
    dto.ajax = {
        "url": "${ctx}/telecom/card/queryListForPage.do",
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
        {"data": "batch", "class": ""},
        {"data": "iccid", "class": ""},
        {"data": "type", "class": ""},
        {"data": "cardName", "class": ""},
        {"data": "province", "class": ""},
        {"data": "isDistribution", "class": ""},
        {"data": "isUse", "class": ""},
        {"data": "distributionTime", "class": ""},
        {"data": "stationExpressNumber", "class": ""},
        {"defaultContent": "操作", "class": ""}
    ]);
    //设置定义列的初始属性
    dto.setColumnDefs([
        {//复选框
            targets: 0,
            render: function (data, type, row, meta) {
                var context = {
                    func: [
                        {"id": row.iccid, "order": meta.row + 1}
                    ]
                };
                var html = template_chk(context);
                return html;
            }
        },
        {//号卡类型
            targets: 3,
            render: function (data, type, row, meta) {
                if (row.type == 0) {
                    return '小白卡';
                }
                else {
                    return '即买即通卡';
                }
            }
        },
        {
            targets: 4,
            render: function (data, type, row, meta) {
                var state = "";
                switch (row.cardName) {
                    case 0:
                        state = "白金卡";
                        break;
                    case 1:
                        state = "抖音卡";
                        break;
                    case 2:
                        state = "鱼卡";
                        break;
                    case 3:
                        state = "49元不限流量卡";
                        break;
                    case 4:
                        state = "99元不限流量卡";
                        break;
                    case 5:
                        state = "199元不限流量卡";
                        break;
                    case 6:
                        state = "29元不限流量卡";
                        break;
                    default:
                        state = "";
                }
                return state;
            }
        },
        {//分配
            targets: 6,
            render: function (data, type, row, meta) {
                if (row.isDistribution == 1) {
                    return '是';
                }
                else {
                    return '否';
                }
            }
        },
        {//寄出
            targets: 7,
            render: function (data, type, row, meta) {
                if (row.isUse == 1) {
                    return '是';
                }
                else {
                    return '否';
                }
            }
        },
        {
            targets: -1,
            render: function (data, type, row, meta) {
                if (row.isPush == 1) {
                    var context = {
                        func: [
                            {
                                "name": "查看",
                                "fn": "detail(\'" + row.iccid + "\')",
                                "icon": "am-icon-pencil-square-o",
                                "class": "am-text-secondary"
                            },
                            {
                                "name": "重新推送",
                                "fn": "push(\'" + row.iccid + "\')",
                                "icon": "am-icon-pencil-square-o",
                                "class": "am-text-secondary"
                            },
                        ]
                    };
                } else {
                    var context = {
                        func: [
                            {
                                "name": "查看",
                                "fn": "detail(\'" + row.iccid + "\')",
                                "icon": "am-icon-pencil-square-o",
                                "class": "am-text-secondary"
                            }
                        ]
                    };
                }


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

    function addBtnClick() {
        $("#modal-insertView").modal("show");
    }

    function detail(id) {
        func_reload_page("${ctx}/telecom/card/cardDetail.do?id=" + id);
    }

    /**
     * 重新推送
     * @param id
     */
    function push(id) {
        var url_ = AppConfig.ctx + "/telecom/card/push.do";
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
    }

    function format(time) {
        var date = new Date(time);
        var year = date.getFullYear(),
            month = date.getMonth() + 1,//月份是从0开始的
            day = date.getDate();
        var newTime = year + '-' +
            month + '-' +
            day;
        return newTime;
    }

    /**
     * 手动推送
     */
    function adminPush() {
        var url_ = AppConfig.ctx + "/recycle/admin/push.do";
        $.ajax({
            url: url_,
            type: "POST",
            dataType: "json",
            success: function (result) {
                if (result.success) {
                    alert("推送成功");
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
        window.open("${ctx}/file/download.do?fileId=23&ids=" + ids + params, "导出");
    }

</script>