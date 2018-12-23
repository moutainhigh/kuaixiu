<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/commons/taglibs.jsp" %>
      <div class="am-cf am-padding am-padding-bottom-0">
        <div class="am-fl am-cf" style="width: 100%;">
          <strong class="am-text-primary am-text-lg">对账管理</strong> / <small>结算单详情</small>
        </div>
      </div>
      
      <hr>
      
      <div class="am-g">
	    <form id="searchForm" class="form form-horizontal">
	      
		  <div class="form-group">
		    <input type="hidden" name="balanceNo" value="${balance.balanceNo }">
		    <div class="am-u-sm-12 am-u-md-6">
		      <h4><label class="radio-inline">${balance.balanceName }</label></h4>
		    </div>
	      </div>
	    </form>
      </div>

      <div class="am-g">
        <div class="am-u-sm-12">
            <table id="dt" class="table table-striped table-bordered table-radius table-hover">
              <thead>
	            <tr>
	              <th class="fontWeight_normal tdwidth30">序号</th>
                  <th class="fontWeight_normal tdwidth130">下单时间</th>
                  <th class="fontWeight_normal tdwidth160">订单号</th>
                  <th class="fontWeight_normal tdwidth60">金额(元)</th>
                  <th class="fontWeight_normal table-title">联系人/手机号</th>
                  <th class="fontWeight_normal tdwidth90">机型</th>
                  <th class="fontWeight_normal tdwidth80">维修工程师</th>
                  <th class="fontWeight_normal tdwidth100">订单状态</th>
	              <th class="fontWeight_normal tdwidth70">操作</th>
	            </tr>
              </thead>
              <tbody>
              
              </tbody>
            </table>
        </div>
        <div style="margin-left: 20px">
            <sapn style="color:red">总计：
                <sapn id="sumMoney"><fmt:formatNumber value="${balance.amountPrice }" pattern="0.00" maxFractionDigits='2'/></sapn>(元)
            </sapn>
        </div>
      </div>

    <!-- 新增弹窗 end -->
    <div id="modal-insertView" class="modal fade" tabindex="-1" role="dialog" aria-hidden="true" style="display: none;">
    </div>
    <!-- 新增弹窗 end -->
    
<script src="${webResourceUrl}/resource/js/address.js" type="text/javascript" charset="utf-8"></script>
<script type="text/javascript">
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
    {"data": "realPrice","class":""},
    {"data": "customerName","class":""},
    {"data": "modelName","class":""},
    {"data": "engineerNumber","class":""},
    {"data": "orderStatus","class":""},
    {"defaultContent": "操作","class":""}   
]);
//设置定义列的初始属性
dto.setColumnDefs([
    {//复选框
        targets: 0,
        render: function (data, type, row, meta) {
            return meta.row + 1;
        }
    },
    {//复选框
        targets: 4,
        render: function (data, type, row, meta) {                             
            return row.customerName + "/" + row.mobile;
        }
    },
    {
    	targets: -2,
        render: function (data, type, row, meta) { 
        	var state = "";
        	switch(row.orderStatus){
        	case 0:
        		state = "待提交";
        	    break;
            case 1:
                state = "待支付保证金";
                break;
            case 2:
                state = "待派单";
                break;
            case 11:
                state = "已派单";
                break;
            case 20:
                state = "开始检修";
                break;
            case 21:
                state = "待支付";
                break;
            case 22:
                state = "已支付";
                break;
            case 30:
                state = "完成维修";
                break;
            case 50:
                state = "已完成";
                break;
            case 60:
                state = "取消";
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
 * 导出数据
 */
function expDataExcel(){
	var params = "";
    var array = $("#searchForm").serializeArray();
    $.each(array, function() {
        params += "&" + this.name + "=" + this.value;
    });
    window.open("${ctx}/file/download.do?fileId=7" + params, "导出");
}

/**
 * 查看订单详情
 */
function showOrderDetail(id){
    func_reload_page("${ctx}/order/detail.do?id="+id);
}
</script>