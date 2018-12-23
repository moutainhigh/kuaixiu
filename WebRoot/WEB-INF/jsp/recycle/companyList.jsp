<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ include file="/commons/taglibs.jsp"%>
<div class="am-cf am-padding am-padding-bottom-0">
	<div class="am-fl am-cf" style="width: 100%;">
		<strong class="am-text-primary am-text-lg">企业回收</strong> / <small>列表查询</small>
	</div>
</div>

<hr>

<div class="am-g">
	<form id="searchForm" class="form form-horizontal">
		<table id="searchTable">
			<tr>
				<td class="search_th search_th_frist"><label
					class="control-label">&nbsp&nbsp&nbsp企业名称 ：</label></td>
				<td class="search_td"><input type="text" name="query_name"
					class="form-control"></td>

				<td class="search_th"><label class="control-label">客户经理电话
						：</label></td>
				<td class="search_td"><input type="text" name="query_mobile"
					class="form-control"></td>
			</tr>

			<tr>
				<td class="search_th"><label class="control-label">提交时间 ：</label></td>
				<td class="search_td">
					<div class="am-datepicker-date">
						<input type="text" id="query_startTime" name="query_startTime"
							class="form-control am-datepicker-start" data-am-datepicker
							readonly> <span
							style="float: left; line-height: 30px; height: 30px; width: 10%; text-align: center;">至</span>
						<input type="text" id="query_endTime" name="query_endTime"
							class="form-control am-datepicker-end" data-am-datepicker
							readonly>
					</div>
				</td>

				<td class="search_th"><label class="control-label">回收时间 ：</label></td>
				<td class="search_td">
					<div class="am-datepicker-date">
						<input type="text" id="recycle_startTime"
							name="recycle_startTime"
							class="form-control am-datepicker-start" data-am-datepicker
							readonly> <span
							style="float: left; line-height: 30px; height: 30px; width: 10%; text-align: center;">至</span>
						<input type="text" id="recycle_endTime" name="recycle_endTime"
							class="form-control am-datepicker-end" data-am-datepicker
							readonly>
					</div>
				</td>
			</tr>


			<tr>
				<td class="search_th search_th_frist"><label
					class="control-label">&nbsp&nbsp&nbsp订单状态：</label></td>
				<td class="search_td"><select name="query_orderStatus"
					class="form-control">
						<option value="">--选择状态--</option>
						<option value="0">待处理</option>
						<option value="1">已处理</option>
				</select></td>
			</tr>
		</table>
		<div class="form-group">
			<div class="am-u-sm-12 am-u-md-6">
				<div class="am-btn-toolbar">
					<!--  
		       <div class="am-btn-group am-btn-group-sm">
		          <button onclick="batchDelBtnClick();" type="button" class="am-btn am-btn-default"><span class="am-icon-trash-o"></span>批量删除</button> 
		       </div>
		        -->
					<div class="am-btn-group am-btn-group-sm m20">
						<button onclick="refreshPage();"
							class="am-btn am-btn-default search_btn" type="button">
							搜 索</button>
					</div>
				</div>
			</div>
		</div>
	</form>
</div>

<div class="am-g">
	<div class="am-u-sm-12">
		<table id="dt"
			class="table table-striped table-bordered table-radius table-hover">
			<thead>
				<tr>
					<th class="fontWeight_normal tdwidth50"><input
						id="check_all_btn" onclick="checkAll(this)" type="checkbox" />序号</th>
					<th class="fontWeight_normal tdwidth160 center">企业名称</th>
					<th class="fontWeight_normal tdwidth70  center">客户经理</th>
					<th class="fontWeight_normal tdwidth110  center">客户经理电话</th>
					<th class="fontWeight_normal table-title center">回收机型</th>
					<th class="fontWeight_normal tdwidth140  center">回收时间</th>
					<th class="fontWeight_normal tdwidth140  center">提交时间</th>
					<th class="fontWeight_normal tdwidth70  center">状态</th>
					<th class="fontWeight_normal tdwidth130">操作</th>
				</tr>
			</thead>
			<tbody>

			</tbody>
		</table>
	</div>
</div>

<!-- 新增弹窗 end -->
<div id="modal-insertView" class="modal fade" tabindex="-1"
	role="dialog" aria-hidden="true" style="display: none;">
	<%@ include file="add.jsp"%>
</div>
<!-- 新增弹窗 end -->
<!-- 修改弹窗 end -->
<div id="modal-updateView" class="modal fade" tabindex="-1"
	role="dialog" aria-hidden="true" style="display: none;"></div>
<!-- 修改弹窗 end -->
<script type="text/javascript">
	$("#query_startTime").datetimepicker({
		format : "yyyy-mm-dd",
		language : "zh-CN",
		autoclose : true,//选中关闭
		minView : "month"//设置只显示到月份
	});

	$("#query_endTime").datetimepicker({
		format : "yyyy-mm-dd",
		language : "zh-CN",
		autoclose : true,//选中关闭
		minView : "month"//设置只显示到月份
	});

	$("#recycle_startTime").datetimepicker({
		format : "yyyy-mm-dd",
		language : "zh-CN",
		autoclose : true,//选中关闭
		minView : "month"//设置只显示到月份
	});

	$("#recycle_endTime").datetimepicker({
		format : "yyyy-mm-dd",
		language : "zh-CN",
		autoclose : true,//选中关闭
		minView : "month"//设置只显示到月份
	});

	//自定义datatable的数据
	var dto = new DtOptions();
	//设置数据刷新路径
	dto.ajax = {
		"url" : "${ctx}/recycle/companyList/queryListForPage.do",
		"data" : function(d) {
			//将表单中的查询条件追加到请求参数中
			var array = $("#searchForm").serializeArray();
			$.each(array, function() {
				d[this.name] = this.value;
			});
		}
	};

	//设置数据列
	dto.setColumns([ {
		"data" : "id",
		"class" : " center"
	}, {
		"data" : "name",
		"class" : " center"
	}, {
		"data" : "customerManager",
		"class" : ""
	}, {
		"data" : "mobile",
		"class" : ""
	}, {
		"data" : "model",
		"class" : ""
	}, {
		"data" : "recycleTime",
		"class" : ""
	}, {
		"data" : "inTime",
		"class" : ""
	}, {
		"data" : "orderStatus",
		"class" : ""
	}, {
		"defaultContent" : "操作",
		"class" : ""
	} ]);
	//设置定义列的初始属性
	dto.setColumnDefs([ {//复选框
		targets : 0,
		render : function(data, type, row, meta) {
			var context = {
				func : [ {
					"id" : row.id,
					"order" : meta.row + 1
				} ]
			};
			var html = template_chk(context);
			return html;
		}
	}, 
	{
		 targets: -2,
	        render: function (data, type, row, meta) {      
	        	 var state='';
	        	 switch(row.orderStatus){
	             case 0:
	                 state = "待处理";
	                 break;
	             case 1:
	                 state = "已处理";
	                 break;
	             }
	            return state;
		
	        }
	  },
	  {
		targets : -1,
		render : function(data, type, row, meta) {
			var context = {
				func : [ {
					"name" : "查看",
					"fn" : "showOrderDetail(\'" + row.id + "\')",
					"icon" : "am-icon-pencil-square-o",
					"class" : "am-text-secondary"
				}, {
					"name" : "删除",
					"fn" : "delBtnClick(\'" + row.id + "\')",
					"icon" : "am-icon-trash-o",
					"class" : "am-text-danger"
				} ]
			};
			var html = template_btn(context);
			return html;
		}
	} ]);
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
		$("input[name='item_check_btn']").each(function() {
			$(this).prop("checked", obj.checked);
		});
	}

	function checkItem(obj) {
		var checked = true;
		$("input[name='item_check_btn']").each(function() {
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

	/**
	 * 批量删除
	 */
	function batchDelBtnClick() {
		var ids = "";
		$("input[name='item_check_btn']").each(function() {
			if (this.checked) {
				ids += this.value + ",";
			}
		});
		if (ids == "")
			AlertText.tips("d_alert", "提示", "请选择删除项！");
		else
			delBtnClick(ids);
	}

	/**
	 * 查看订单详情
	 */
	function showOrderDetail(id) {
		func_reload_page("${ctx}/recycle/company/detail.do?id=" + id);
	}

	function delBtnClick(id) {
		AlertText.tips("d_confirm", "删除提示", "确定要删除吗？", function() {
			//加载等待
			AlertText.tips("d_loading");
			var url_ = AppConfig.ctx + "/recycle/company/delete.do";
			var data_ = {
				id : id
			};
			$.ajax({
				url : url_,
				data : data_,
				type : "POST",
				dataType : "json",
				success : function(result) {
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