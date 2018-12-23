<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/commons/taglibs.jsp" %>
<link rel="stylesheet" type="text/css" href="${webResourceUrl}/resource/order/css/alert.tip.css" />
      <div class="am-cf am-padding am-padding-bottom-0">
        <div class="am-fl am-cf" style="width: 100%;">
          <strong class="am-text-primary am-text-lg">碎屏险订单管理</strong> / <small>免激活用户列表</small>
        </div>
      </div>
      
      <hr>
      
      <div class="am-g">
	    <form id="searchForm" class="form form-horizontal">
	      <table id="searchTable">
            <tr>
              <td class="search_th"><label class="control-label">客户手机号：</label></td>
              <td class="search_td"><input type="text" name="query_customerMobile" class="form-control" ></td>
             
              <td class="search_th"><label class="control-label">导 入 时 间 ：</label></td>
              <td class="search_td">
                <div class="am-datepicker-date">
                <input type="text" id="query_startTime" name="query_startTime" class="form-control am-datepicker-start" data-am-datepicker readonly >
                <span style="float: left; line-height: 30px; height: 30px; width: 10%; text-align: center;">至</span>
                <input type="text" id="query_endTime" name="query_endTime" class="form-control am-datepicker-end" data-am-datepicker readonly >
                </div>
              </td>
              
                <td class="search_th"><label class="control-label"></label></td>
                <td class="search_td"></td>
            </tr>
            
            <tr>
                <td class="search_th"><label class="control-label">是否激活：</label></td>
                    <td class="search_td">
                      <select name="query_isActive"  class="form-control">
                        <option value="">--选择状态--</option>
                        <option value="1">是</option>
                        <option value="0">否</option>
                      </select>
                    </td>
                    
                    
               <td class="search_th"><label class="control-label">是否过期：</label></td>
                    <td class="search_td">
                      <select name="query_isOutOfTime"  class="form-control">
                        <option value="">--选择状态--</option>
                        <option value="0">是</option>
                        <option value="1">否</option>
                      </select>
               </td>
               
                <td class="search_th"><label class="control-label"></label></td>
                <td class="search_td"></td>
            </tr>
	      </table>
		  <div class="form-group">
		    <div class="am-u-sm-12 am-u-md-6">
		      <div class="am-btn-toolbar">
		        <div class="am-btn-group am-btn-group-sm m20">
		          <button onclick="refreshPage();" class="am-btn am-btn-default search_btn" type="button"> 搜  索 </button>
		          <button onclick="expDataExcel();" type="button" class="am-btn am-btn-default search_btn"><span class="am-icon-file-excel-o"></span> 导出</button>
		          <button onclick="expOutputExcel();" type="button" class="am-btn am-btn-default search_btn"><span class="am-icon-file-excel-o"></span> 批量导入</button>
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
                  <th class="fontWeight_normal tdwidth80">手机号</th>
                  <th class="fontWeight_normal tdwidth60">品牌</th>
                  <th class="fontWeight_normal tdwidth100">机型</th>
                  <th class="fontWeight_normal tdwidth80">手机串号</th>
                  <th class="fontWeight_normal tdwidth80">是否激活</th>
                  <th class="fontWeight_normal tdwidth80">是否过期</th>
                  <th class="fontWeight_normal tdwidth70">导入时间</th>
	            </tr>
              </thead>
              <tbody>
              
              </tbody>
            </table>
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
	    "url": "${ctx}/screen/freeCustomer/queryListForPage.do",
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
    {"data": "mobile","class":""},
    {"data": "brand","class":""},
    {"data": "model","class":""},
    {"data": "imei","class":""},
    {"data": "isActive","class":""},
    {"data": "isOutOfTime","class":""},
    {"data": "inTime","class":""}
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
        targets: 5,
        render: function (data, type, row, meta) {     
        	if(row.isActive==0){
        		return "未激活";
        	}else if(row.isActive==1){
        		return "已激活";
        	}else{
        		return "状态异常";
        	}
        }
    },
    {//复选框
        targets: 6,
        render: function (data, type, row, meta) {     
        	if(row.isOutOfTime==0){
        		return "未过期";
        	}else if(row.isOutOfTime==1){
        	    return "已过期";
        	}
        }
    }
]);
dto.sScrollXInner="100%";
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
 	window.open("${ctx}/file/download.do?fileId=17&ids=" + ids + params, "导出");
 }


  /**
  * 批量导入
  */
  function expOutputExcel(){
	  func_reload_page("${ctx}/screen/importIndex.do");
  }


</script>