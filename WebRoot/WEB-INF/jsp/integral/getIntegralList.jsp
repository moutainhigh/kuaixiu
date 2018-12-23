<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/commons/taglibs.jsp" %>
      <div class="am-cf am-padding am-padding-bottom-0">
        <div class="am-fl am-cf" style="width: 100%;">
          <strong class="am-text-primary am-text-lg">积分获得记录</strong> / <small>列表查询</small>
        </div>
      </div>
      
      <hr>
      
      <div class="am-g">
	    <form id="searchForm" class="form form-horizontal">
	      <table id="searchTable">
            <tr>
            <td class="search_th"><label class="control-label">&nbsp&nbsp&nbsp手 机 号：</label></td>
            <td class="integral_search_td"><input  name="query_customerMobile" class="getIntegral"  type="number"  oninput="if(value.length>11)value=value.slice(0,11)" ></td>
              <td class="search_th"><label class="control-label">获 得 时 间 ：</label></td>
              <td class="search_td">
                <div class="am-datepicker-date">
                <input type="text" id="query_startTime" name="query_startTime" class="form-control am-datepicker-start" data-am-datepicker readonly >
                <span style="float: left; line-height: 30px; height: 30px; width: 10%; text-align: center;">至</span>
                <input type="text" id="query_endTime" name="query_endTime" class="form-control am-datepicker-end" data-am-datepicker readonly >
                </div>
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
	              <th class="fontWeight_normal tdwidth100">积分获得人</th>
                  <th class="fontWeight_normal tdwidth100">手机号码</th>
                  <th class="fontWeight_normal tdwidth140">获得积分</th>
                  <th class="fontWeight_normal table-title">获得时间</th>
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
	    "url": "${ctx}/wap/integral/getIntegralListForPage.do",
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
    {"data": "integral","class":""},
    {"data": "inTime","class":""},
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
	    targets: -1,
	    render: function (data, type, row, meta) {
	        var context = {
	        		 func: [
	        		        {"name" : "查看", "fn" : "showOrderDetail(\'" + row.orderId + "\')","icon" : "am-icon-pencil-square-o","class" : "am-text-secondary"}	
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
	$("#pageStatus").val(1);
	myTable.ajax.reload();
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
	console.log(id);
	func_reload_page("${ctx}/order/detail.do?id="+id);
}



function editBtnClick(id){
	//$("#modal-insertView").html("");
 //   $("#modal-insertView").load("${ctx}/webpc/activity/edit.do?id="+id, function(){
  //  	func_after_model_load(this);
  //  });
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
/**
 * 批量删除
 */
function batchDelBtnClick(){
	var ids = "";
    $("input[name='item_check_btn']").each(function(){
    	if(this.checked){
    		ids += this.value + ",";
        }
    });
    if(ids =="")
    	AlertText.tips("d_alert", "提示", "请选择删除项！");
    else
    		delBtnClick(ids);
}

function delBtnClick(id){
	AlertText.tips("d_confirm", "删除提示", "确定要删除吗？", function(){
		//加载等待
        AlertText.tips("d_loading");
		var url_ = AppConfig.ctx + "/webpc/activity/delete.do";
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