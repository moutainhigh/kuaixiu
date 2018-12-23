<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/commons/taglibs.jsp" %>
      <div class="am-cf am-padding am-padding-bottom-0">
        <div class="am-fl am-cf" style="width: 100%;">
          <strong class="am-text-primary am-text-lg">维修工程师管理</strong> / <small>列表查询</small>
        </div>
      </div>
      
      <hr>
      
      <div class="am-g">
	    <form id="searchForm" class="form form-horizontal">
	      <input type="hidden" name="query_orderStates" value="2,11,12">
		  <div class="form-group">
		    <div class="am-u-sm-12 am-u-md-6">
		      <h4><label class="radio-inline">派单模式：</label>
		          <label class="radio-inline">
                    <input type="radio" onchange="setDispatchType('${shop.id }', this.value);" name="dispatchType" value="0" ${shop.dispatchType == 0 ? 'checked="checked"' : '' }> 自动派单
                  </label>
                  <label class="radio-inline">
                    <input type="radio" onchange="setDispatchType('${shop.id }', this.value);" name="dispatchType" value="1" ${shop.dispatchType == 1 ? 'checked="checked"' : '' }> 手动派单
                  </label>
              </h4>
		      
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
                  <th class="fontWeight_normal tdwidth80">接单剩余时间</th>
                  <th class="fontWeight_normal tdwidth60">金额(元)</th>
                  <th class="fontWeight_normal tdwidth60">兑换新机型</th>
                  <th class="fontWeight_normal tdwidth100">订单状态</th>
                  <th class="fontWeight_normal tdwidth80">转派工程师</th>
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
    {"data": "id","class":"tdwidth50 center"},
    {"data": "inTime","class":""},
    {"data": "orderNo","class":""},
    {"data": "dispatchTime","class":""},
    {"data": "orderPrice","class":""},
    {"data": "newMobile","class":""},
    {"data": "orderStatus","class":""},
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
    {//倒计时
        targets: 3,
        render: function (data, type, row, meta) {
        	var ts = (0 - row.waitTime) + 15 * 60 * 1000;//计算剩余的毫秒数
        	var remainTime = ts / 1000;
        	if(remainTime < 0){
        		remainTime = 0;
        	}
            var html = "<div class='dispatchTimeCountDown' remainTime='" + remainTime + "'>" + getTimeStr(remainTime) + "</div>"
        	return html;
        }
    },
    {//状态  订单状态  0 生成订单  2 待门店派单 11 待工程师预约  12 工程师已预约  22 待付款 50 订单完成 55 订单已评价 60 订单取消
    	targets: -3,
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
        	var tmp = $("#engSelectTemp").clone();
            tmp.attr("id","engSelect_" + row.id);
            if(row.engineerId){
            	var html = "<option value='" + row.engineerId + "' selected='selected'>" + row.engineerName + "</option>";
            	$(html).insertAfter($('option:first', tmp));
            }
            tmp.show();
            return tmp[0].outerHTML;
        }
    },
    {
        targets: -1,
        render: function (data, type, row, meta) {
        	var name = "接单";
			if(row.engineerId){
				name = "重新派单";
			}
            var context = {
                func: [
                    {"name" : name, "fn" : "dispatch(\'" + row.id + "\')","icon" : "am-icon-wrench","class" : "am-text-secondary"}
                ]
            };
            var html = template_btn(context);
            return html;
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
 * 查看订单详情
 */
function showOrderDetail(id){
    func_reload_page("${ctx}/newOrder/detail.do?id="+id);
}

function dispatch(id){
    var engId = $("#engSelect_" + id).val();
    if(!engId){
    	AlertText.tips("d_alert", "提示", "请选择工程师！");
    	return false;
    }
    var url_ = AppConfig.ctx + "/newOrder/dispatch.do";
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

if("undefined" != typeof(countIntervalProcess)){
	clearInterval(countIntervalProcess);
}
countIntervalProcess = setInterval("updateRemainTime()", 1000);
</script>