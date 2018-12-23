<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/commons/taglibs.jsp" %>
      <div class="am-cf am-padding am-padding-bottom-0">
        <div class="am-fl am-cf" style="width: 100%;">
          <strong class="am-text-primary am-text-lg">订单管理</strong> / <small>列表查询</small>
        </div>
      </div>
      
      <hr>
      
      <div class="am-g">
	    <form id="searchForm" class="form form-horizontal">
	      <input type="hidden" name="query_repairType" value="3">
	      <input type="hidden" name="query_orderStates" value="3,5,20">
		  <div class="form-group">
		    <div class="am-u-sm-12 am-u-md-6">
		      
		    </div>
	      </div>
	    </form>
      </div>

      <div class="am-g">
        <div class="am-u-sm-12">
            <table id="dt" class="table table-striped table-bordered table-radius table-hover">
              <thead>
	            <tr>
	              <th class="fontWeight_normal tdwidth30"><input id="check_all_btn" onclick="checkAll(this)" type="checkbox" />序号</th>
                  <th class="fontWeight_normal tdwidth130">下单时间</th>
                  <th class="fontWeight_normal tdwidth160">订单号</th>
                  <th class="fontWeight_normal tdwidth60">客户姓名</th>
                  <th class="fontWeight_normal tdwidth60">客户手机号</th>
                  <th class="fontWeight_normal tdwidth80">等待时间</th>
                  <th class="fontWeight_normal tdwidth60">金额(元)</th>
                  <th class="fontWeight_normal tdwidth60">机型</th>
                  <th class="fontWeight_normal tdwidth100">订单状态</th>
                  <th class="fontWeight_normal tdwidth60">指派工程师</th>
	              <th class="fontWeight_normal tdwidth70">操作</th>
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
        <c:forEach items="${engList}" var="item" varStatus="i" >
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
	    "url": "${ctx}/order/queryListForPage.do",
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
    {"data": "id","class":"tdwidth50 center"},
    {"data": "inTime","class":""},
    {"data": "orderNo","class":""},
    {"data": "customerName","class":""},
    {"data": "mobile","class":""},
    {"data": "dispatchTime","class":""},
    {"data": "realPrice","class":""},
    {"data": "modelName","class":""},
    {"data": "orderStatusName","class":""},
    {"data": "engineerNumber","class":""},
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
        targets: 2,
        render: function (data, type, row, meta) {
            var html = "<a href='javascript:void(0);' onclick=\"showOrderDetail('" + row.id + "')\" >" + row.orderNo + "</a>";
            return html;
        }
    },
    {
        targets: 5,
        render: function (data, type, row, meta) {
            var ts = (new Date()) - (new Date(row.inTime));
            var remainTime = ts / 1000+1;
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
        	if(row.orderStatus==3){
        		return '';
        	}else{
        	var tmp = $("#engSelectTemp").clone();
            tmp.attr("id","engSelect_" + row.id);
            if(row.engineerId){
            	var html = "<option value='" + row.engineerId + "' selected='selected'>" + row.engineerName + "</option>";
            	$(html).insertAfter($('option:first', tmp));
            }
            tmp.show();
            return tmp[0].outerHTML;
        }
        }
    },
    {
        targets: -1,
        render: function (data, type, row, meta) {
        	var name = "接单";
        	if(row.orderStatus==3){
        		name = "确认收货";
        		 var context = {
        	                func: [
        	                    {"name" : name, "fn" : "affirm(\'" + row.id + "\')","icon" : "am-icon-wrench","class" : "am-text-secondary"}
        	                ]
        	            };
        	}else{
			if(row.engineerId){
				name = "重新派单";
			}
            var context = {
                func: [
                    {"name" : name, "fn" : "dispatch(\'" + row.id + "\')","icon" : "am-icon-wrench","class" : "am-text-secondary"}
                ]
            };
            
        	} 
            var html = template_btn(context);
            return html;
        }
    }
]);
dto.sScrollXInner="110%";
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


function updateRemainTime(){
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