<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/commons/taglibs.jsp" %>
      <div class="am-cf am-padding am-padding-bottom-0">
        <div class="am-fl am-cf" style="width: 100%;">
          <strong class="am-text-primary am-text-lg">以旧换新管理</strong> / <small>订单调控</small>
        </div>
      </div>
      
      <hr>
      
      <div class="am-g">
	    <form id="searchForm" class="form form-horizontal">
	      <input type="hidden" name="query_orderStates" value="11,12">
          <input type="hidden" id="query_endTime" name="query_endTime" value="">
	      <table id="searchTable">
            <tr>
              <td class="search_th search_th_frist"><label class="control-label">&nbsp&nbsp&nbsp订 单 号 ：</label></td>
              <td class="search_td"><input type="text" name="query_orderNo" class="form-control" ></td>
              <td class="search_th"><label class="control-label">客户手机号：</label></td>
              <td class="search_td"><input type="text" name="query_customerMobile" class="form-control" ></td>
              <td class="search_th"></td>
              <td class="search_td"></td>
            </tr>
            
            <tr>
              <td class="search_th"><label class="control-label">下 单 时 间 ：</label></td>
              <td class="search_td">
                <div class="am-datepicker-date">
                <input type="text" id="query_startTime" name="query_startTime" class="form-control am-datepicker-start" data-am-datepicker readonly >
                <span style="float: left; line-height: 30px; height: 30px; width: 10%; text-align: center;">至</span>
                <input type="text" id="query_endTimes" name="query_endTime" class="form-control am-datepicker-end" data-am-datepicker readonly >
                </div>
              </td>
              
               <td class="search_th"><label class="control-label">地址：</label></td>
              <td class="form-group" colspan="3">
                <select id="queryProvince" name="queryProvince" onchange="fn_select_address(2, this.value, '', 'query');" class="form-control-inline">
	              <option value="">--请选择--</option>
	              <c:forEach items="${provinceL }" var="item" varStatus="i">
	                <option value="${item.areaId }">${item.area }</option>
	              </c:forEach>
	            </select>
	            
	            <select id="queryCity" name="queryCity" onchange="fn_select_address(3, this.value, '', 'query');" class="form-control-inline" style="display: none;">
	              <option value="">--请选择--</option>
	            </select>
	            
	            <select id="queryCounty" name="queryCounty" class="form-control-inline" style="display: none;">
	              <option value="">--请选择--</option>
	            </select>
              </td>
              
            </tr>
	      </table>
		  <div class="form-group">
		    <div class="am-u-sm-12 am-u-md-6">
		      <div class="am-btn-toolbar">
		        <div class="am-btn-group am-btn-group-sm m20">
		          <button onclick="refreshPage();" class="am-btn am-btn-default search_btn" type="button"> 搜  索 </button>
		        </div>
		        <div class="am-btn-group am-btn-group-sm">
		          <!-- <button onclick="addBtnClick();" type="button" class="am-btn am-btn-default"><span class="am-icon-plus"></span> 新增</button> -->
		          <!-- <button type="button" class="am-btn am-btn-default"><span class="am-icon-save"></span> 保存</button> -->
		          <!--  <button onclick="expDataExcel8();" type="button" class="am-btn am-btn-default"><span class="am-icon-file-excel-o"></span> 导出</button>-->
		          <!-- <button onclick="batchDelBtnClick();" type="button" class="am-btn am-btn-default"><span class="am-icon-trash-o"></span> 删除</button> -->
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
	              <th class="fontWeight_normal tdwidth50"><input id="check_all_btn" onclick="checkAll(this)" type="checkbox" />序号</th>
                  <th class="fontWeight_normal tdwidth130">订单创建时间</th>
                  <th class="fontWeight_normal tdwidth160">订单号</th>
                  <th class="fontWeight_normal tdwidth90">订单金额</th>
                  <th class="fontWeight_normal table-title tdwidth80">联系人/<br/>手机号</th>
                  <th class="fontWeight_normal tdwidth60">新手机</th>
                  <th class="fontWeight_normal tdwidth90">维修单位</th>
                  <th class="fontWeight_normal tdwidth90">处理工程师</th>
                  <th class="fontWeight_normal tdwidth60">等待时间</th>
                  <th class="fontWeight_normal tdwidth70">订单状态</th>
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
    
<script src="${webResourceUrl}/resource/js/address.js" type="text/javascript" charset="utf-8"></script>
<script type="text/javascript">
var endTime = getDateHourFormat(0);
document.getElementById("query_endTime").value = endTime;
//console.log(endTime);
function changeWaitTime(value){
	endTime = getDateHourFormat(Number(value));
	document.getElementById("query_endTime").value = endTime;
	refreshPage();
}

    $("#query_startTime").datetimepicker({
	  format: "yyyy-mm-dd",
	  language: "zh-CN",
	  autoclose:true,//选中关闭
	  minView: "month"//设置只显示到月份
	});

	$("#query_endTimes").datetimepicker({
	  format: "yyyy-mm-dd",
	  language: "zh-CN",
	  autoclose:true,//选中关闭
	  minView: "month"//设置只显示到月份
	});
	
//自定义datatable的数据
var dto=new DtOptions();
//设置数据刷新路径
dto.ajax={
	    "url": "${ctx}/newOrder/queryListMapForPage.do",
		"data":function(d){
			//将表单中的查询条件追加到请求参数中
			var array = $("#searchForm").serializeArray();
			$.each(array, function() {
			    d[this.name] = this.value;
			});
        }
};

//设置数据列
dto.setColumns([ 
    {"data": "id","class":"center"},
    {"data": "in_time","class":"center"},
    {"data": "order_no","class":""},
    {"data": "real_price","class":""},
    {"data": "customer_name","class":""},
    {"data": "new_model","class":""},
    {"data": "shop_name","class":""},
    {"data": "engineer_name","class":""},
    {"data": "dispatch_time","class":""},
    {"data": "order_status","class":""},
    {"defaultContent": "操作","class":""}   
]);
//设置定义列的初始属性
dto.setColumnDefs([
    {//复选框
    	targets: 0,
        render: function (data, type, row, meta) {
            var context = {
                func: [
                    {"id": row.id, "order" : meta.row + 1}
                ]
            };
            var html = template_chk(context);                               
            return html;
        }
    },
    {
        targets: 3,
        render: function (data, type, row, meta) {
        	return row.real_price+"元";
        }
    },
    {
        targets: 4,
        render: function (data, type, row, meta) {
        	return row.customer_name + "/<br/>" + row.customer_mobile;
        }
    },
    {
        targets: 6,
        render: function (data, type, row, meta) {
        	if(row.shop_name.length > 8){
	            var html = "<a href='javascript:void(0);' onclick=\"toShopDetail('" + row.shop_code + "')\" title='"+ row.shop_name +"' >" + row.shop_name.substr(0,7) + "...</a>";
	            return html;
        	}
        	else {
        		var html = "<a href='javascript:void(0);' onclick=\"toShopDetail('" + row.shop_code + "')\" title='"+ row.shop_name +"' >" + row.shop_name + "</a>";
	            return html;
        	}
        }
    },
    {//复选框
        targets: -4,
        render: function (data, type, row, meta) {                             
            return row.engineer_name + "/<br/>" + row.engineer_mobile;
        }
    },
    {
        targets: -3,
        render: function (data, type, row, meta) {
            var ts = (new Date(row.sys_time)) - (new Date(row.dispatch_time));//计算已等待的毫秒数
            var remainTime = ts / 1000 + 1;
            if(remainTime < 0){
                remainTime = 0;
            }
            var html = "<div class='waitTimeCountUp' remainTime='" + remainTime + "'>" + getHourTimeStr(remainTime) + "</div>"
            return html;
        }
    },
    {
        targets: -2,
        render: function (data, type, row, meta) {
            var state = "";
            switch(row.order_status){
            case 0:
        		state = "待提交";
        	    break;
            case 2:
                state = "待派单";
                break;
            case 11:
                state = "待预约";
                break;
            case 12:
                state = "已预约";
                break;
            case 50:
                state = "已完成";
                break;
            case 55:
                state = "用户已评价";
                break;
            case 60:
                if(row.cancelType == 1){state="用户取消"}
                if(row.cancelType == 2){state="工程师取消"}
                if(row.cancelType == 3){state="管理员取消"}
                if(row.cancelType == 4){state="客服取消"}
                break;
        	default:
        		state = "订单异常";   
            }
            return state;
        }
    },
	{
	    targets: -1,
	    render: function (data, type, row, meta) {
	        if(row.order_status == 11||row.order_status == 12||row.order_status == 30){
		        var context = {
		            func: [
			            {"name" : "重新派单", "fn" : "reDispatch(\'" + row.id + "\')","icon" : "am-icon-pencil-square-o","class" : "am-text-secondary"}
		            ]
		        };
		        var html = template_btn(context);
		        return html;
	        }
	    }
	}
]);

var myTable = $("#dt").DataTable(dto);

/**
 * 刷新列表
 */
function refreshPage(){
	myTable.ajax.reload(null, false);
}

/**
 * 全选按钮
 */
function checkAll(obj){
	$("input[name='item_check_btn']").each(function(){
		$(this).prop("checked", obj.checked);
    });
}

function checkItem(obj){
	var checked = true;
    $("input[name='item_check_btn']").each(function(){
        if(!this.checked){
            checked = false;
            return false;
        }
    });
    $("#check_all_btn").prop("checked", checked);
}
/**
 * 导出数据
 */
function expDataExcel8(){
	var params = "";
	var array = $("#searchForm").serializeArray();
    $.each(array, function() {
        params += "&" + this.name + "=" + this.value;
    });
    var ids = "";
    $("input[name='item_check_btn']").each(function(){
    	if(this.checked){
    		ids += this.value + ",";
        }
    });
	window.open("${ctx}/file/download.do?fileId=8&ids=" + ids + params, "导出");
}
/**
 * 查看订单详情
 */
function showOrderDetail(id){
	func_reload_page("${ctx}/order/detail.do?id="+id);
}

function toShopDetail(code){
    func_reload_page("${ctx}/shop/detail.do?code=" + code);
}

/**
 * 重新派单
 */
function reDispatch(id){
	AlertText.tips("d_confirm", "系统提示", "确定要重新派单吗？ 请确认维修工程师是否已经出发。", function(){
        //加载等待
        AlertText.tips("d_loading");
	    var url_ = AppConfig.ctx + "/newOrder/reDispatch.do";
	    var data_ = {id: id};
	    $.ajax({
	        url: url_,
	        data: data_,
	        type: "POST",
	        dataType: "json",
	        success: function (result) {
	            if (result.success) {
	                AlertText.tips("d_alert", "提示", "重新派单成功", function(){
	                    refreshPage();
	                });
	            } else {
	                AlertText.tips("d_alert", "提示", result.msg);
	                return false;
	            }
	        },
	        error : function() {
	        	AlertText.tips("d_alert", "提示", "系统异常，请稍后再试");
	            loading_hide();
	            isLoading = false;
	        }
	    });
	});
}

/**
 * 重置故障
 */
function resetRepair(id){
	AlertText.tips("d_confirm", "系统提示", "确定要重置故障吗？ 重置故障后需要工程师重新提交检修结果。", function(){
        //加载等待
        AlertText.tips("d_loading");
	    var url_ = AppConfig.ctx + "/order/resetRepair.do";
	    var data_ = {id: id};
	    $.ajax({
	        url: url_,
	        data: data_,
	        type: "POST",
	        dataType: "json",
	        success: function (result) {
	            if (result.success) {
	                AlertText.tips("d_alert", "提示", "重置故障成功", function(){
	                    refreshPage();
	                });
	            } else {
	                AlertText.tips("d_alert", "提示", result.msg);
	                return false;
	            }
	        },
	        error : function() {
	        	AlertText.tips("d_alert", "提示", "系统异常，请稍后再试");
	            loading_hide();
	            isLoading = false;
	        }
	    });
	});
}

function updateWaitTime(){
    $(".waitTimeCountUp").each(function(){
        var obj = $(this);
        var remainTime = obj.attr("remainTime");
        remainTime = Number(remainTime) + 1;
        if(remainTime < 0){
            remainTime = 0;
        }
        obj.attr("remainTime", remainTime);
        obj.html(getHourTimeStr(remainTime));
    });
}

if("undefined" != typeof(countIntervalProcess)){
    clearInterval(countIntervalProcess);
}
countIntervalProcess = setInterval("updateWaitTime()", 1000);
</script>