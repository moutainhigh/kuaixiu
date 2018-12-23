<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/commons/taglibs.jsp" %>
      <div class="am-cf am-padding am-padding-bottom-0">
        <div class="am-fl am-cf" style="width: 100%;">
          <strong class="am-text-primary am-text-lg">积分兑换管理</strong> / <small>列表查询</small>
        </div>
      </div>
      
      <hr>
      
      <div class="am-g">
	    <form id="searchForm" class="form form-horizontal">
	      <table id="searchTable">
            <tr>
            
            
              <td class="search_th"><label class="control-label">兑换人手机号：</label></td>
              <td class="integral_search_td"><input type="number"  oninput="if(value.length>11)value=value.slice(0,11)"  name="query_customerMobile" class="getIntegral" ></td>
              <td class="search_th"><label class="control-label">订 单 时 间 ：</label></td>
              <td class="search_td">
                <div class="am-datepicker-date">
                <input type="text" id="query_startTime" name="query_startTime" class="form-control am-datepicker-start" data-am-datepicker readonly >
                <span style="float: left; line-height: 30px; height: 30px; width: 10%; text-align: center;">至</span>
                <input type="text" id="query_endTime" name="query_endTime" class="form-control am-datepicker-end" data-am-datepicker readonly >
                </div>
              </td>
              <td class="search_th"><label class="control-label">兑 换 状 态 :</label></td>
              <td class="search_td">
                <select name="query_orderState" class="form-control">
                  <option value="">--选 择 状 态--</option>
                  <option value="2">审核中</option>
                  <option value="1">已兑换</option>
                </select>
              </td>
                <td></td>
            </tr>
	      </table>
		  <div class="form-group">
		    <div class="am-u-sm-12 am-u-md-6">
		      <div class="am-btn-toolbar">
		        <div class="am-btn-group am-btn-group-sm m20">
		          <button onclick="refreshPage();" class="am-btn am-btn-default search_btn" type="button"> 搜  索 </button>
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
	              <th class="fontWeight_normal tdwidth30"><input id="check_all_btn" onclick="checkAll(this)" type="checkbox" />序号</th>
	              <th class="fontWeight_normal tdwidth100">兑换人</th>
                  <th class="fontWeight_normal tdwidth100">手机号码</th>
                  <th class="fontWeight_normal tdwidth120">微信号</th>
                  <th class="fontWeight_normal tdwidth45">实名制</th>
                  <th class="fontWeight_normal tdwidth80">兑换积分</th>
                  <th class="fontWeight_normal tdwidth80">兑换状态</th>
                  <th class="fontWeight_normal table-title">订单时间</th>
                  <th class="fontWeight_normal table-title">支付时间</th>
	              <th class="fontWeight_normal tdwidth150">操作</th>
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
	    "url": "${ctx}/wap/integral/queryListForPage.do",
	    "type": "POST",
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
    {"data": "name","class":""},
    {"data": "tel","class":""},
    {"data": "wechatId","class":""},
    {"data": "isRealName","class":""},
    {"data": "integral","class":""},
    {"data": "status","class":""},
    {"data": "inTime","class":""},
    {"data": "updateTime","class":""},
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
    {//状态  订单状态 0 审核中
    	targets: -4,
        render: function (data, type, row, meta) { 
        	var state = "";
        	switch(row.status){
        	case 2:
        		state = "审核中";
        	    break;
            case 1:
                state = "已兑换";
                break;
        	default:
        		state = "订单异常";   
        	}
        	return state;
        }
    },
    {//实名制状态  1  表示是    0  表示否
        targets: 4,
        render: function (data, type, row, meta) { 
            var state = "";
            switch(row.isRealName){
            case 0:
                state = "否";
                break;
            case 1:
                state = "是";
                break;
            default:
                state = "";   
            }
            return state;
        }
    },
    
    
	{
	    targets: -1,
	    render: function (data, type, row, meta) {
	      
	    	if(row.status==2){
	    	var context = {
	            func: [
	                   {"name" : "付款", "fn": "payBtnClick(\'" + row.id + "\')","icon" : "am-icon-pencil-square-o","class" : "am-text-secondary"}
	            ]
	        };
	        var html = template_btn(context);
	        return html;
	    	}
	         
	    	if(row.status==1){
		    	var context = {
		            func: [
		                   {"name" : "已付款", "icon" : "am-icon-pencil-square-o","class" : "am-text-secondary"}
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
	$("#pageStatus").val(1);
	myTable.ajax.reload();
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
	window.open("${ctx}/file/download.do?fileId=13&ids=" + ids + params, "导出");
}

function payBtnClick(id){
	AlertText.tips("d_confirm", "删除提示", "确定要付款吗？", function(){
		//加载等待
        AlertText.tips("d_loading");
		var url_ = AppConfig.ctx + "/wap/integral/payIntegral.do";
	    var data_ = {id: id};
	    $.ajax({
	        url: url_,
	        data: data_,
	        type: "POST",
	        dataType: "json",
	        success: function (result) {
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