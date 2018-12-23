<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/commons/taglibs.jsp" %>
      <div class="am-cf am-padding am-padding-bottom-0">
        <div class="am-fl am-cf" style="width: 100%;">
          <strong class="am-text-primary am-text-lg">订单管理</strong> / <small>实时监控</small>
        </div>
      </div>
      
      <hr>
      
      <div class="am-g">
	    <form id="searchForm" class="form form-horizontal">
          <input type="hidden" name="query_orderStates" value="2,3">
          <!-- 
          <input type="hidden" name="query_isDispatch" value="0">
           -->
		  <div class="form-group">
		    <div class="am-u-sm-12 am-u-md-6">
		      <label class="control-label">刷新时间：</label>
	          <select id="refreshTime" onchange="changeRefreshTime(this.value);" class="form-control-inline">
                  <option value="30" selected="selected">30秒</option>
                  <option value="60">60秒</option>
                  <option value="120">2分钟</option>
                  <option value="0">停止刷新</option>
              </select>
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
                  <th class="fontWeight_normal tdwidth120">下单时间</th>
                  <th class="fontWeight_normal tdwidth160">订单号</th>
                  <th class="fontWeight_normal tdwidth80">维修方式</th>
                  <th class="fontWeight_normal tdwidth80">订单状态</th>
                  <th class="fontWeight_normal tdwidth140">客户姓名/手机号</th>
                  <th class="fontWeight_normal tdwidth150">下单地址</th>
                  <th class="fontWeight_normal tdwidth150">维修门店</th>
                  <th class="fontWeight_normal tdwidth60">门店电话</th>
                  <th class="fontWeight_normal tdwidth140">负责人/手机号</th>
                  <th class="fontWeight_normal tdwidth60">空闲人数</th>
                  <th class="fontWeight_normal tdwidth60">剩余时间</th>
	              <th class="fontWeight_normal tdwidth60">等待时间</th>
	            </tr>
              </thead>
              <tbody>
              
              </tbody>
            </table>
        </div>
      </div>

    <!-- 新增弹窗 end -->
    <select id="engSelectTemp" style="display: none;">
        <option value="">--选择--</option>
        <c:forEach items="${engList }" var="item" varStatus="i" >
            <option value="${item.id }">${item.name }</option>
        </c:forEach>
    </select>
    <!-- 新增弹窗 end -->
    
<script src="${webResourceUrl}/resource/js/address.js" type="text/javascript" charset="utf-8"></script>
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
	    "url": "${ctx}/order/queryListMapForPage.do",
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
    {"data": "in_time","class":""},
    {"data": "order_no","class":""},
    {"data": "repair_type","class":""},
    {"data": "order_status","class":""},
    {"data": "customer_name","class":""},
    {"data": "address","class":""},
    {"data": "shop_name","class":""},
    {"data": "shop_tel","class":""},
    {"data": "shop_manager_name","class":""},
    {"data": "eng_count","class":""},
    {"data": "dispatch_time","class":""},
    {"data": "dispatch_time","class":""},
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
        targets: 2,
        render: function (data, type, row, meta) {
            var html = "<a href='javascript:void(0);' onclick=\"showOrderDetail('" + row.id + "')\" >" + row.order_no + "</a>";
            return html;
        }
    },
    {
        targets: 3,
        render: function (data, type, row, meta) {
        	 var state='';
        	 switch(row.repair_type){
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
        }
        	  return state;
        }
    },
    {
        targets: 4,
        render: function (data, type, row, meta) {
        	 var state='';
        	 switch(row.order_status){
             case 2:
                 state = "待派单";
                 break;
             case 3:
                 state = "待门店收件";
                 break;
        }
        	  return state;
        }
    },
    {
        targets: 5,
        render: function (data, type, row, meta) {
        	return row.customer_name + "/" + row.mobile;
        }
    },
    {
        targets: 6,
        render: function (data, type, row, meta) {
            var html = row.areas+row.address;
            return html;
        }
    },
    {
    	targets: -4,
        render: function (data, type, row, meta) { 
        	if(row.shop_manager_name==null){
        		return "";
        	}else{
        		return row.shop_manager_name + "/" + row.shop_manager_mobile;
        	}
        }
    },
    {
    	targets: -3,
        render: function (data, type, row, meta) { 
        	if(row.shop_manager_name==null){
        		return "";
        	}else{
        		return row.eng_count;
        	}
        }
    },
    {
        targets: -2,
        render: function (data, type, row, meta) {
            var ts = (new Date(row.dispatch_time)) - (new Date(row.sys_time)) + 15 * 60 * 1000;//计算剩余的毫秒数
            var remainTime = ts / 1000;
            if(remainTime < 0){
                remainTime = 0;
            }
            var html = "<div class='dispatchTimeCountDown' remainTime='" + remainTime + "'>" + getTimeStr(remainTime) + "</div>"
            return html;
        }
    },
    {
        targets: -1,
        render: function (data, type, row, meta) {
            var ts = (new Date(row.sys_time)) - (new Date(row.in_time));
            var remainTime = ts / 1000+1;
            if(remainTime < 0){
                remainTime = 0;
            }
            var html = "<div class='waitTimeCountUp' remainTime='" + remainTime + "'>" + getHourTimeStr(remainTime) + "</div>"
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
	myTable.ajax.reload(null, false);
}

/**
 * 调整刷新时间
 */
function changeRefreshTime(time){
	if("undefined" != typeof(refreshIntervalProcess)){
	    clearInterval(refreshIntervalProcess);
	}
	if(time > 0){
		refreshIntervalProcess = setInterval("refreshPage()", time * 1000);
	}
}

if("undefined" != typeof(refreshIntervalProcess)){
    clearInterval(refreshIntervalProcess);
}
refreshIntervalProcess = setInterval("refreshPage()", 30 * 1000);

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
    func_reload_page("${ctx}/order/detail.do?id="+id);
}

function dispatch(id){
    var engId = $("#engSelect_" + id).val();
    if(!engId){
    	AlertText.tips("d_alert", "提示", "请选择工程师！");
    	return false;
    }
    var url_ = AppConfig.ctx + "/order/dispatch.do";
    var data_ = {id: id, engId: engId};
    $.ajax({
        url: url_,
        data: data_,
        type: "POST",
        dataType: "json",
        success: function (result) {
            if (result.success) {
                AlertText.tips("d_alert", "提示", "派单成功", function(){
                	refreshPage();
                });
            } else {
                AlertText.tips("d_alert", "提示", result.msg);
                return false;
            }
        }
    });
}

function setDispatchType(id, dispatchType){
    var url_ = AppConfig.ctx + "/shop/setDispatchType.do";
    var data_ = {id: id, dispatchType: dispatchType};
    $.ajax({
        url: url_,
        data: data_,
        type: "POST",
        dataType: "json",
        success: function (result) {
            if (result.success) {
            	AlertText.tips("d_alert", "提示", "派单模式已修改");
            } else {
                AlertText.tips("d_alert", "提示", result.msg);
                return false;
            }
        }
    });
}
function updateWaitTime(){
	$(".dispatchTimeCountDown").each(function(){
		var obj = $(this);
		var remainTime = obj.attr("remainTime");
		remainTime = Number(remainTime) - 1;
		if(remainTime < 0){
			remainTime = 0;
		}
		obj.attr("remainTime", remainTime);
		obj.html(getTimeStr(remainTime));
	});
	
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

//确认收货
function affirm(id){
	AlertText.tips("d_confirm", "系统提示", "确定要收货吗？", function(){
        //加载等待
        AlertText.tips("d_loading");
	    var url_ = AppConfig.ctx + "/webpc/activity/affirm.do";
	    var data_ = {id: id};
	    $.ajax({
	        url: url_,
	        data: data_,
	        type: "POST",
	        dataType: "json",
	        success: function (result) {
	            if (result.success) {
	                AlertText.tips("d_alert", "提示", "确认收货成功", function(){
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


</script>