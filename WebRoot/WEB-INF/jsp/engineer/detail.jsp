<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ include file="/commons/taglibs.jsp"%>
<link rel="stylesheet"
	href="${webResourceUrl}/resource/order/css/order.detail.css">
<div class="am-cf am-padding am-padding-bottom-0">
	<div class="am-fl am-cf" style="width: 100%;">
		<strong class="am-text-primary am-text-lg"><a
			href="javascript:void(0);" onclick="toList();">维修工程师管理</a></strong> / <small>工程师信息统计</small>
	</div>
</div>

<hr>

<div class="am-g">
	<div class="panel panel-success index-panel-msg">
		<div class="row">
			<div class="col-md-6 col-sm-6 col-xs-12">
				<h4>姓名：${eng.name }</h4>
			</div>
			<!-- /.col -->
			<div class="col-md-6 col-sm-6 col-xs-12">
				<h4>性别：${eng.gender }</h4>
			</div>
			<!-- /.col -->
		</div>
		<!-- /.row -->

		<div class="row">
			<div class="col-md-6 col-sm-6 col-xs-12">
				<h4>工号：${eng.number }</h4>
			</div>
			<!-- /.col -->
			<div class="col-md-6 col-sm-6 col-xs-12">
				<h4>身份证号码：${eng.idcard }</h4>
			</div>
			<!-- /.col -->
		</div>
		<!-- /.row -->

		<div class="row">
			<div class="col-md-6 col-sm-6 col-xs-12">
				<h4>手机号：${eng.mobile }</h4>
			</div>
			<!-- /.col -->
			<div class="col-md-6 col-sm-6 col-xs-12">
				<h4>
					状态：
					<c:if test="${eng.isDispatch != 2}">在线</c:if>
					<c:if test="${eng.isDispatch == 2}">下线</c:if>
				</h4>
			</div>
			<!-- /.col -->
		</div>
		<!-- /.row -->
	</div>
	<!-- /panel -->
</div>

<!-- /am-g -->
<div class="am-g mt20">
	<div class="panel panel-success index-panel-msg">
		<div class="row">
			<div class="col-md-12 col-sm-12 col-xs-12">
				<h4>维修工程师订单信息：</h4>
			</div><!-- /.col -->
		</div><!-- /.row -->
		<form id="searchForm" class="form form-horizontal">
			<table id="searchTable">
				<tr>
					<td class="search_th"><label class="control-label">完 成 时 间 ：</label></td>
					<td class="search_td">
						<div class="am-datepicker-date">
							<input type="text" id="query_startRepairTime" name="query_startRepairTime"
								   class="form-control am-datepicker-start" data-am-datepicker readonly>
							<span style="float: left; line-height: 30px; height: 30px; width: 10%; text-align: center;">至</span>
							<input type="text" id="query_endRepairTime" name="query_endRepairTime"
								   class="form-control am-datepicker-end" data-am-datepicker readonly>
						</div>
					</td>
					<td class="search_th"><label class="control-label">订 单 状 态 ：</label></td>
					<td class="search_td">
						<select name="query_orderState" id="query_orderState" class="form-control">
							<option value="">--订单状态--</option>
							<c:forEach items="${selectOrderStatus}" var="item" varStatus="i">
								<option value="${item.key }">${item.value}</option>
							</c:forEach>
						</select>
					</td>
				</tr>
				<tr>
					<td class="search_th"><label class="control-label">去 掉 贴 膜 ：</label></td>
					<td class="search_td">
						<select name="isPatch" id="isPatch" class="form-control">
							<option value="">--是否去掉--</option>
							<option value="1">去掉</option>
							<option value="2">不去掉</option>
						</select>
					</td>
					<td class="search_th"><label class="control-label">订 单 类 型 ：</label></td>
					<td class="search_td">
						<select name="isRework" id="isRework" class="form-control">
							<option value="">--请选择--</option>
							<option value="0">快修单</option>
							<option value="1">返修单</option>
						</select>
					</td>
					<td>
						<input type="hidden" id="query_engNumber" name="query_engNumber" value="${eng.number }" />
					</td>
				</tr>
			</table>
			<div class="form-group">
				<div class="am-u-sm-12 am-u-md-6">
					<div class="am-btn-toolbar">
						<div class="am-btn-group am-btn-group-sm m20">
							<button onclick="refreshPage();" class="am-btn am-btn-default search_btn" type="button"> 搜 索
							</button>
							<button onclick="expDataExcel();" type="button" class="am-btn am-btn-default"><span
									class="am-icon-file-excel-o"></span> 导出
							</button>
						</div>
					</div>
				</div>
			</div>
		</form>
		<div class="row">
			<div class="col-md-12 col-sm-12 col-xs-12">
				<table id="dt" class="table table-striped table-bordered table-radius table-hover">
					<thead>
					<tr>
						<th class="fontWeight_normal tdwidth50"><input id="check_all_btn" onclick="checkAll(this)"
																	   type="checkbox"/>序号
						</th>
						<th class="fontWeight_normal tdwidth80">下单时间</th>
						<th class="fontWeight_normal tdwidth160">订单号</th>
						<th class="fontWeight_normal tdwidth70">维修方式</th>
						<th class="fontWeight_normal tdwidth90">金额(元)</th>
						<th class="fontWeight_normal tdwidth90">实付金额(元)</th>
						<th class="fontWeight_normal table-title tdwidth80">联系人/<br/>手机号</th>
						<th class="fontWeight_normal tdwidth100">机型</th>
						<th class="fontWeight_normal tdwidth70">维修项目</th>
						<th class="fontWeight_normal tdwidth80">来源系统</th>
						<th class="fontWeight_normal tdwidth100">订单状态</th>
						<th class="fontWeight_normal tdwidth100">是否售后单</th>
						<th class="fontWeight_normal tdwidth80">结算状态</th>
						<th class="fontWeight_normal tdwidth80">完成时间</th>
					</tr>
					</thead>
					<tbody>
					<input id="sessionUserType" hidden="hidden" type="text" value="${sessionScope.session_user_key_.type}"/>
					</tbody>
				</table>
			</div><!-- /.col -->
		</div><!-- /.row -->

	</div><!-- /panel -->
</div><!-- /am-g -->
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

<script type="text/javascript">
	function toList() {
		func_reload_page("${ctx}/engineer/list.do");
	}


    $("#query_startRepairTime").datetimepicker({
        format: "yyyy-mm-dd",
        language: "zh-CN",
        autoclose: true,//选中关闭
        minView: "month"//设置只显示到月份
    });
    $("#query_endRepairTime").datetimepicker({
        format: "yyyy-mm-dd",
        language: "zh-CN",
        autoclose: true,//选中关闭
        minView: "month"//设置只显示到月份
    });
    //自定义datatable的数据
    var dto = new DtOptions();
    //设置数据刷新路径
    dto.ajax = {
        "url": "${ctx}/order/queryEngineerListForPage.do",
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
        {"data": "inTime", "class": ""},
        {"data": "orderNo", "class": ""},
        {"data": "repairType", "class": ""},
        {"data": "realPrice", "class": ""},
        {"data": "realPriceSubCoupon", "class": ""},
        {"data": "customerName", "class": ""},
        {"data": "modelName", "class": ""},
        {"data": "projectName", "class": ""},
        {"data": "fromSystemName", "class": ""},
        {"data": "orderStatusName", "class": ""},
        {"data": "isRework", "class": ""},
        {"data": "balanceStatus", "class": ""},
        {"data": "endTime", "class": ""}
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
            targets: 4,
            render: function (data, type, row, meta) {
                return row.realPrice;
            }
        },
        {
            targets: 5,
            render: function (data, type, row, meta) {
                if (row.orderStatus < 50 || row.orderStatus == 60) {
                    return "0";
                } else {
                    return row.realPriceSubCoupon;
                }
            }
        },
        {//复选框
            targets: 6,
            render: function (data, type, row, meta) {
                return row.customerName + "/<br/>" + row.mobile;
            }
        },
        {//复选框
            targets: 7,
            render: function (data, type, row, meta) {
                return row.modelName + "<br/>（" + row.color + "）";
            }
        },
        {//复选框
            targets: -4,
            render: function (data, type, row, meta) {
                if (row.orderStatus == 50) {
                    return '已完成';
                }
                else {
                    return row.orderStatusName;
                }
            }
        },
        {//复选框
            targets: -3,
            render: function (data, type, row, meta) {
                if (row.isRework == 1) {
                    return '是';
                }
                else {
                    return "否";
                }
            }
        },
        {//结算状态（-1不需要 0：未结算对账；1：待结算；2：结算单生成）
            targets: -2,
            render: function (data, type, row, meta) {
                var state = "";
                switch (row.balanceStatus) {
                    case 0:
                        state = "未结算";
                        break;
                    case 1:
                        state = "待结算";
                        break;
                    case 2:
                        state = "已结算";
                        break;
                    default:
                        state = "不需要";
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
        if($("#query_orderState").val()!=50 && $("#query_orderState").val()!=60
			&& $("#query_orderState").val()!="" && $("#query_orderState").val()!=null){
            if($("#query_startRepairTime").val()!="" || $("#query_endRepairTime").val()!=""){
                alert("订单进行中状态不可与时间同时筛选");
            }else{
                myTable.ajax.reload(null, false);
			}
        }else {
            myTable.ajax.reload(null, false);
		}
        getTotalPrice();
    }

    window.onload=getTotalPrice();

    function getTotalPrice() {
        var query_startRepairTime = $("#query_startRepairTime").val();
        var query_endRepairTime = $("#query_endRepairTime").val();
        var isPatch = $("#isPatch").val();
        var query_orderState = $("#query_orderState").val();
        var query_engNumber = $("#query_engNumber").val();
        $.ajax({
            url: AppConfig.ctx + "/engineer/getTotalPrice.do",
            type: "POST",
            dataType: "json",
            data: {query_engNumber:query_engNumber,query_startRepairTime: query_startRepairTime, query_endRepairTime: query_endRepairTime, isPatch: isPatch,query_orderState:query_orderState},
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
        window.open("${ctx}/file/download.do?fileId=22&ids=" + ids + params, "导出");
    }
</script>