<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/commons/taglibs.jsp" %>
<div class="am-cf am-padding am-padding-bottom-0">
    <div class="am-fl am-cf" style="width: 100%;">
        <strong class="am-text-primary am-text-lg">对账管理</strong> /
        <small>新增结算批次</small>
    </div>
</div>

<hr>

<div class="am-g">
    <form id="searchForm" class="form form-horizontal">
        <table id="searchTable">
            <tr>
                <td class="search_th"><label class="control-label">结算时间：</label></td>
                <td class="search_td">
                    <div class="am-datepicker-date">
                        <input type="text" id="query_startTime" name="query_startTime"
                               class="form-control am-datepicker-start" data-am-datepicker readonly>
                        <span style="float: left; line-height: 30px; height: 30px; width: 10%; text-align: center;">至</span>
                        <input type="text" id="query_endTime" name="query_endTime"
                               class="form-control am-datepicker-end" data-am-datepicker readonly>
                    </div>
                </td>
                <td>
                    <div class="am-btn-group am-btn-group-sm ml20">
                        <button onclick="refreshPage();" class="am-btn am-btn-default search_btn" type="button"> 查 询
                        </button>
                    </div>
                </td>
                <td>
                    <div class="am-btn-group am-btn-group-sm fr mr10">
                        <button onclick="expDataExcel();" type="button" class="am-btn am-btn-default">
                            <span class="am-icon-file-excel-o"></span> 导出数据
                        </button>
                    </div>
                </td>
            </tr>
        </table>
    </form>
</div>

<div class="am-g">
    <div class="am-u-sm-12">
        <table id="dt" class="table table-striped table-bordered table-radius table-hover">
            <thead>
            <tr>
                <th class="fontWeight_normal tdwidth30">序号</th>
                <th class="fontWeight_normal ">连锁商</th>
                <th class="fontWeight_normal tdwidth160">结算日期</th>
                <th class="fontWeight_normal tdwidth50">订单数</th>
                <th class="fontWeight_normal tdwidth140">负责人/手机号</th>
                <th class="fontWeight_normal ">开户银行</th>
                <th class="fontWeight_normal tdwidth60">银行户名</th>
                <th class="fontWeight_normal tdwidth100">银行帐号</th>
                <th class="fontWeight_normal tdwidth70">金额(元)</th>
            </tr>
            </thead>
            <tbody>
            </tbody>
        </table>
    </div>
    <div style="margin-left: 20px">
        <sapn style="color:red">总计：
            <sapn id="sumMoney">0</sapn>
            (元)
        </sapn>
        <br/><br/>
        <button class="am-btn am-btn-default search_btn" style="width:100px;" onclick="addBalance()" type="button">确 定</button>
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

    /**
     * 校验所选日期
     */
    function checkSelDate() {
        var startDate = $("#query_startTime").val();
        var endDate = $("#query_endTime").val();
        if (startDate == null || startDate == '' || endDate == null || endDate == '') {
            AlertText.tips("d_alert", "提示", "请输入结算起止日期!");
            return false;
        }
        return true;
    }
    
    
    //自定义datatable的数据
    var dto = new DtOptions();
    //设置数据刷新路径 
    dto.ajax = {
        "url": "${ctx}/order/getSummaryOrder.do",
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
        {"data": "providerName", "class": ""},
        {"data": "balanceDate", "class": ""},
        {"data": "orderCount", "class": ""},
        {"data": "personInfo", "class": ""},
        {"data": "accountBank", "class": ""},
        {"data": "accountName", "class": ""},
        {"data": "accountNumber", "class": ""},
        {"data": "amountPrice", "class": ""}
    ]);
    //设置定义列的初始属性
    dto.setColumnDefs([
        {//序号
            targets: 0,
            render: function (data, type, row, meta) {
                return meta.row + 1;
            }
        },
        {//结算日期
            targets: 2,
            render: function (data, type, row, meta) {
                if(row.id != null) {
                    return $("#query_startTime").val() + "至" + $("#query_endTime").val();
                }
            }
        },
        {//订单数
            targets: 3,
            render: function (data, type, row, meta) {
                if(row.id != null) {
                    return row.orderCount;
                }
            }
        },
        {//负责人/手机号
            targets: 4,
            render: function (data, type, row, meta) {
                if(row.id != null) {
                    return row.managerName + "/" + row.managerMobile;
                }
            }
        }
    ]);
    
    var myTable = $("#dt").DataTable(dto);

    /**
     * 刷新列表
     */
    function refreshPage() {
        var startDate = $("#query_startTime").val();
        var endDate = $("#query_endTime").val();
        if (startDate == null || startDate == '' || endDate == null || endDate == '') {
            AlertText.tips("d_alert", "提示", "请选择结算起止日期!");
            return false;
        }
        
        $.ajax({
            url: AppConfig.ctx + "/order/queryBalanceAmount.do",
            type: "POST",
            dataType: "json",
            data: {startDate:startDate, endDate:endDate},
            success: function (result) {
                if (result.success) {
                    $("#sumMoney").text(result.data.amount_price);
                    $("#sumMoney").attr("amount_price", result.data.amount_price);
                } else {
	                AlertText.tips("d_alert", "提示", "加载结算总计异常，请稍后重试");
                }
            },
            error: function () {
                AlertText.tips("d_alert", "提示", "加载结算总计异常，请稍后重试");
            }
        });
        
        myTable.ajax.reload(null, false);
    }
    /**
     * 确定按钮事件，新增结算批次
     */
    function addBalance() {
		var startDate = $("#query_startTime").val();
		var endDate = $("#query_endTime").val();
		if (startDate == null || startDate == '' || endDate == null || endDate == '') {
		    AlertText.tips("d_alert", "提示", "请选择结算起止日期!");
		    return false;
		}
        AlertText.tips("d_confirm", "提示", "是否确认生成 "+ startDate + "至" + endDate +" 的结算账单?", function(){
            $.ajax({
                type: "POST",
                url: AppConfig.ctx + "/balance/addBalance.do",
                data: {startDate:startDate, endDate:endDate, amount_price: $("#sumMoney").attr("amount_price")},
                dataType: "json",
                success: function (info) {
                    if (info.success) {
                    	func_reload_page("${ctx}/balance/batchDetail.do?batchNo=" + info.data);
                    } else {
                        AlertText.tips("d_alert", "提示", info.msg);
                    }
                },
                error: function () {
                    AlertText.tips("d_alert", "提示", "出现错误，请稍后重试");
                }
            });
        });
    }

    /**
     * 导出数据
     */
    function expDataExcel() {
    	var params = "";
        var array = $("#searchForm").serializeArray();
        $.each(array, function() {
            params += "&" + this.name + "=" + this.value;
        });
        window.open("${ctx}/file/download.do?fileId=9" + params, "导出");
    }
</script>