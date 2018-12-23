<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/commons/taglibs.jsp" %>
<link rel="stylesheet" type="text/css" href="${webResourceUrl}/resource/order/css/alert.tip.css" />
      <div class="am-cf am-padding am-padding-bottom-0">
        <div class="am-fl am-cf" style="width: 100%;">
          <strong class="am-text-primary am-text-lg">碎屏险订单管理</strong> / <small>列表查询</small>
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
              
              <td class="search_th"><label class="control-label">是否免激活：</label></td>
                    <td class="search_td">
                      <select name="query_isActive"  class="form-control">
                        <option value="">--选择状态--</option>
                        <option value="1">是</option>
                        <option value="0">否</option>
                      </select>
                    </td>
            </tr>
            
            <tr>
              <td class="search_th"><label class="control-label">订 单 状 态 ：</label></td>
              <td class="search_td">
                <select name="query_orderStates" class="form-control">
                  <option value="">--订单状态--</option>
                  <option value="0">未付款</option>
                  <option value="1">已付款</option>
                  <option value="2">退款中</option>
                  <option value="3">已退款</option>
                  <option value="4">提交失败</option>
                  <option value="5">提交成功</option>
                  <option value="10">已取消</option>
                </select>
              </td>
              
              <td class="search_th"><label class="control-label">客户手机号：</label></td>
              <td class="search_td"><input type="text" name="query_customerMobile" class="form-control" ></td>
              
              
              <td></td>
            </tr>
            
	      </table>
		  <div class="form-group">
		    <div class="am-u-sm-12 am-u-md-6">
		      <div class="am-btn-toolbar">
		        <div class="am-btn-group am-btn-group-sm m20">
		          <button onclick="refreshPage();" class="am-btn am-btn-default search_btn" type="button"> 搜  索 </button>
		          <button onclick="expDataExcel();" type="button" class="am-btn am-btn-default"><span class="am-icon-file-excel-o"></span> 导出</button>
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
                  <th class="fontWeight_normal tdwidth150">订单号</th>
                  <th class="fontWeight_normal table-title tdwidth80">联系人/<br/>手机号</th>
                  <th class="fontWeight_normal tdwidth70">品牌</th>
                  <th class="fontWeight_normal tdwidth80">碎屏险类型</th>
                  <th class="fontWeight_normal tdwidth60">价格(元)</th>
                  <th class="fontWeight_normal tdwidth100">订单状态</th>
                  <th class="fontWeight_normal tdwidth80">下单时间</th>
                  <th class="fontWeight_normal tdwidth80">完成时间</th>
                  <th class="fontWeight_normal tdwidth70">结算状态</th>
	              <th class="fontWeight_normal tdwidth130">操作</th>
	            </tr>
              </thead>
              <tbody>
              
              </tbody>
            </table>
        </div>
      </div>

   <div class="alert_box" style="display: none;">
		<div class="masks"></div>
		<div class="mcon">
			<div class="alertOrder">
				<div class="title">
					<span>退款提示</span><span class="close_alertOrder"></span>
				</div>
				<div class="tip">
					<i></i><span>确认要退款吗？</span>
				</div>
				<p>
				<input type="text" id="reason" class="reason" placeholder="请写下您的退款原因吧！" maxlength="120">
				</p>
				<p>
					<button class="affirmBtn" onclick="refund();">确&nbsp;&nbsp;定</button>
					<button class="cancelBtn" >取&nbsp;&nbsp;&nbsp;消</button>
				</p>
			</div>
		</div>
	</div>

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
	    "url": "${ctx}/screen/order/queryListForPage.do",
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
    {"data": "name","class":""},
    {"data": "projectBrand","class":""},
    {"data": "projectName","class":""},
    {"data": "orderPrice","class":""},
    {"data": "orderStatus","class":""},
    {"data": "inTime","class":""},
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
        targets: 2,
        render: function (data, type, row, meta) {                             
            return row.name + "/<br/>" + row.mobile;
        }
    },
    {//复选框
        targets: -5,
        render: function (data, type, row, meta) {     
        	if(row.orderStatus==0){
        		return "未付款";
        	}else if(row.orderStatus==1){
        		return "已付款";
        	}else if(row.orderStatus==2){
        		return "退款中";
        	}else if(row.orderStatus==3){
        		return "已退款";
        	}else if(row.orderStatus==4){
        		return "提交失败";
        	}else if(row.orderStatus==5){
        		return "提交成功";
        	}else if(row.orderStatus==6){
        		return "已激活";
        	}else if(row.orderStatus==10){
        		return  "已取消";
        	}
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
	    	if(row.orderStatus==1||row.orderStatus==4||row.orderStatus==5&&row.orderPrice!=0){
	        var context = {
	            func: [
	                {"name" : "查看", "fn" : "showOrderDetail(\'" + row.id + "\')","icon" : "am-icon-pencil-square-o","class" : "am-text-secondary"},
	                {"name" : "退款", "fn" : "refundOrder(\'" + row.id + "\')","icon" : "am-icon-pencil-square-o","class" : "am-text-secondary"}
	                ]
	        };
	    	}else if(row.orderStatus==2){
	    		 var context = {
	    		            func: [
	    		                {"name" : "查看", "fn" : "showOrderDetail(\'" + row.id + "\')","icon" : "am-icon-pencil-square-o","class" : "am-text-secondary"},
	    		                {"name" : "退款结果查询", "fn" : "queryRefundNews(\'" + row.id + "\')","icon" : "am-icon-pencil-square-o","class" : "am-text-secondary"}
	    		                ]
	    		        };
	    	}else if(row.orderStatus==0||row.orderStatus==10){
	    		 var context = {
	    	            	func: [
		                        {"name" : "查看", "fn" : "showOrderDetail(\'" + row.id + "\')","icon" : "am-icon-pencil-square-o","class" : "am-text-secondary"},
		                        {"name" : "删除", "fn" : "deleteOrder(\'" + row.id + "\')","icon" : "am-icon-trash-o","class" : "am-text-danger"}
		                        ]
		                };        
	    	}else{
	    		 var context = {
	    	            	func: [
		                        {"name" : "查看", "fn" : "showOrderDetail(\'" + row.id + "\')","icon" : "am-icon-pencil-square-o","class" : "am-text-secondary"}
		                        ]
		                };      
	    	}
	        var html = template_btn(context);
	        if(row.orderStatus==4){
		        context = {
			            func: [
			                {"name" : "提交订单", "fn" : "commitOrder(\'" + row.id + "\')","icon" : "am-icon-pencil-square-o","class" : "am-text-secondary"}
			            ]
			        };
		        html += template_btn(context);
	        }
	        
	        return html;
	    }
	}
]);
dto.sScrollXInner="120%";
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
 * 查看订单详情
 */
function showOrderDetail(id){
	func_reload_page("${ctx}/screen/order/detail.do?id="+id);
}

/**
 * 退款页面
 */
var orderId;    //订单id
function refundOrder(id){
	$(".alert_box").show();
	orderId=id;
}

$(".cancelBtn").click(function () {
	$(".alert_box").hide();
});

/**
 * 退款请求
 */
function refund(){
	 var reason=$('#reason').val();
	 $.ajax({
	        type:'POST',
	        url:"${ctx}/screen/order/refund.do",
	        dataType:'json',
	        data:{
	            id:orderId,
	            reason:reason
	        },
	        success:function (data) {
	            if (data.success){
	            	    //退款申请成功
                        refreshPage();
	            	    alert("退款成功");
	            }else {
	                    alert(data.resultMessage);     //失败原因
	                    $(".order_cancel").hide();
	                }
	        },
	        error:function (jqXHR) {
	            alert('系统异常，请稍后再试！');
	            $(".order_cancel").hide();
	        }
	    });
}


/**
 *  重新向碎屏险服务商提交订单
 */
function commitOrder(id){
	 AlertText.tips("d_confirm", "温馨提示", "确定向碎屏险公司提交订单吗？", function(){
	 $.ajax({
	        type:'POST',
	        url:"${ctx}/screen/order/commitOrder.do",
	        dataType:'json',
	        data:{
	            id:id
	        },
	        success:function (data) {
	            if (data.success){
	            	    //退款申请成功
                        refreshPage();
	            	    alert("订单提交成功");
	            }else {
	                    alert(data.resultMessage);     //失败原因
	                }
	        },
	        error:function (jqXHR) {
	            alertTip('系统异常，请稍后再试！')
	        }
	    });
	 });
}


/**
 *   查询退款订单   退款情况
 */
function queryRefundNews(id){
	 $.ajax({
	        type:'POST',
	        url:"${ctx}/screen/order/queryRefundNews.do",
	        dataType:'json',
	        data:{
	            id:id
	        },
	        success:function (data) {
	            if (data.success){
	            	    //退款申请成功
                        refreshPage();
	            	    alert("订单退款金额已到账");
	            }else {
	                    alert(data.resultMessage);     //失败原因
	                }
	        },
	        error:function (jqXHR) {
	            alertTip('系统异常，请稍后再试！')
	        }
	    });
}


/**
 * 删除订单
 */
 function deleteOrder(id){
	 AlertText.tips("d_confirm", "温馨提示", "确定要删除此订单吗？", function(){
	 $.ajax({
	        type:'POST',
	        url:"${ctx}/screen/order/delete.do",
	        dataType:'json',
	        data:{
	            id:id
	        },
	        success:function (data) {
	            if (data.success){
	            	    //退款申请成功
                        refreshPage();
	            	    alert("删除成功");
	            }else {
	                    alert(data.resultMessage);     //失败原因
	                }
	        },
	        error:function (jqXHR) {
	            alertTip('系统异常，请稍后再试！')
	        }
	    });
	 });
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
 	window.open("${ctx}/file/download.do?fileId=15&ids=" + ids + params, "导出");
 }
 



</script>