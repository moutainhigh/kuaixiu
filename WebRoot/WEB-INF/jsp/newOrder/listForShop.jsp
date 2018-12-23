<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/commons/taglibs.jsp" %>
      <div class="am-cf am-padding am-padding-bottom-0">
        <div class="am-fl am-cf" style="width: 100%;">
          <strong class="am-text-primary am-text-lg">新订单管理</strong> / <small>列表查询</small>
        </div>
      </div>
      
      <hr>
      
      <div class="am-g">
	    <form id="searchForm" class="form form-horizontal">
	      <table id="searchTable">
            <tr>
              <td class="search_th search_th_frist"><label class="control-label">&nbsp&nbsp&nbsp订 单 号 ：</label></td>
              <td class="search_td"><input type="text" name="query_orderNo" class="form-control" ></td>
              <td class="search_th"><label class="control-label">下 单 时 间 ：</label></td>
              <td class="search_td">
                <div class="am-datepicker-date">
                <input type="text" id="query_startTime" name="query_startTime" class="form-control am-datepicker-start" data-am-datepicker readonly >
                <span style="float: left; line-height: 30px; height: 30px; width: 10%; text-align: center;">至</span>
                <input type="text" id="query_endTime" name="query_endTime" class="form-control am-datepicker-end" data-am-datepicker readonly >
                </div>
              </td>
             
            </tr>
            
            <tr>
              <td class="search_th"><label class="control-label">订 单 状 态 ：</label></td>
              <td class="search_td">
                <select name="query_orderState" class="form-control">
                  <option value="">--订单状态--</option>
                  <option value="0">待提交</option>
                  <option value="2">待派单</option>
                  <option value="11">待预约</option>
                  <option value="12">已预约</option>
                  <option value="50">已完成</option>
                  <option value="55">已评价</option>
                  <option value="60">已取消</option>
                </select>
              </td>
              
              <td class="search_th"><label class="control-label">客户手机号：</label></td>
              <td class="search_td"><input type="text" name="query_customerMobile" class="form-control" ></td>
              <td></td>
            </tr>
            <tr>
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
		          <button onclick="expDataExcel();" type="button" class="am-btn am-btn-default"><span class="am-icon-file-excel-o"></span> 导出</button>
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
                  <th class="fontWeight_normal tdwidth70">订单号</th>
                  <th class="fontWeight_normal tdwidth80">下单时间</th>
                  <th class="fontWeight_normal table-title tdwidth80">联系人/<br/>手机号</th>
                  <th class="fontWeight_normal tdwidth70">旧机型</th>
                  <th class="fontWeight_normal tdwidth70">新机型</th>
                  <th class="fontWeight_normal tdwidth70">兑换类型</th>
                  <th class="fontWeight_normal tdwidth80">门店信息</th>
                  <th class="fontWeight_normal tdwidth80">处理工程师</th>
                  <th class="fontWeight_normal tdwidth100">地址</th>
                  <th class="fontWeight_normal tdwidth80">订单状态</th>
                  <th class="fontWeight_normal tdwidth80">完成时间</th>
                  <th class="fontWeight_normal tdwidth50">结算状态</th>
	              <th class="fontWeight_normal tdwidth50">操作</th>
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
<script src="${webResourceUrl}/resource/js/agreedModel.js" type="text/javascript" charset="utf-8"></script>
<script type="text/javascript">
$("#query_startTime").datetimepicker({
  format: "yyyy-mm-dd",
  language: "zh-CN",
  autoclose:true,//选中关闭
  minView: "month"//设置只显示到月份
});

$("#query_endTime").datetimepicker({
  format: "yyyy-mm-dd",
  language: "zh-CN",
  autoclose:true,//选中关闭
  minView: "month"//设置只显示到月份
});
//自定义datatable的数据
var dto=new DtOptions();
//设置数据刷新路径
dto.ajax={
	    "url": "${ctx}/newOrder/queryListForPage.do",
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
    {"data": "id","class":" center"},
    {"data": "orderNo","class":""},
    {"data": "inTime","class":""},
    {"data": "customerName","class":""},
    {"data": "oldMobile","class":""},
    {"data": "agreedModel","class":""},
    {"data": "selectType","class":""},
    {"data": "shopName","class":""},
    {"data": "engineerNumber","class":""},
    {"data": "address","class":""},
    {"data": "orderStatus","class":""},
    {"data": "endTime","class":""},
    {"data": "balanceStatus","class":""},
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
    {//复选框
        targets: 3,
        render: function (data, type, row, meta) {                             
            return row.customerName + "/<br/>" + row.customerMobile;
        }
    },
    {//复选框
        targets: 6,
        render: function (data, type, row, meta) {     
        	if(row.selectType==0){
        		return "换手机";
        	}else if(row.selectType==1){
        		return "换话费";
        	}else{
        		return "";
        	}
        	
        }
    },
    {//复选框
        targets: 8,
        render: function (data, type, row, meta) {                             
        	if(row.orderStatus<=2){
        		return "";
        	}else{
        		return row.engineerNumber + "/<br/>" + row.engineerName;
        	}
        }
    },
    {//状态  订单状态  0 生成订单  2 待门店派单 11 待工程师预约  12 工程师已预约  22 待付款 50 订单完成 55 订单已评价 60 订单取消
    	targets: -4,
        render: function (data, type, row, meta) { 
        	var state = "";
        	switch(row.orderStatus){
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
    	targets: -2,
        render: function (data, type, row, meta) { 
        	var state = "";
        	switch(row.balanceStatus){
        	case -1:
        		state = "不需要";
        	    break;
            case 0:
                state = "未结算";
                break;
            case 1:
                state = "待结算";
                break;
            case 2:
                state = "结算单生成";
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
	        var context = {
	            func: [
	                {"name" : "查看", "fn" : "showOrderDetail(\'" + row.id + "\')","icon" : "am-icon-pencil-square-o","class" : "am-text-secondary"}
	            ]
	        };
	        var html = template_btn(context);
	        if(row.orderStatus == 11){
		        context = {
			            func: [
			                {"name" : "预约", "fn" : "agreedTime(\'" + row.id + "\')","icon" : "am-icon-pencil-square-o","class" : "am-text-secondary"}
			            ]
			        };
		        html += template_btn(context);
	        }
	        
	        return html;
	    }
	}
]);
dto.sScrollXInner="130%";
var myTable = $("#dt").DataTable(dto);

/**
 * 刷新列表
 */
function refreshPage(){
	$("#pageStatus").val(1);
	myTable.ajax.reload(null, false);
}

function agreedTime(id){
	$("#orderId").val(id);
    $("#modal-insertView").modal("show");
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
function expDataExcel(){
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
	window.open("${ctx}/file/download.do?fileId=14&ids=" + ids + params, "导出");
}
/**
 * 查看订单详情
 */
function showOrderDetail(id){
	func_reload_page("${ctx}/newOrder/detail.do?id="+id);
}



</script>