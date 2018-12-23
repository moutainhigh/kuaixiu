<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/commons/taglibs.jsp" %>
      <div class="am-cf am-padding am-padding-bottom-0">
        <div class="am-fl am-cf" style="width: 100%;">
          <strong class="am-text-primary am-text-lg">店员管理</strong> / <small>列表查询</small>
        </div>
      </div>
      
      <hr>
      
      <div class="am-g">
	    <form id="searchForm" class="form form-horizontal">
	      <table id="searchTable">
            <tr>
            
              <td class="search_th"><label class="control-label">店员手机号：</label></td>
              <td class="integral_search_td" ><input  name="query_customerMobile" class="getIntegral" type="number"  oninput="if(value.length>11)value=value.slice(0,11)"  >   </td>
         
              <td class="search_th"><label class="control-label">注 册 时 间 ：</label></td>
              <td class="search_td">
                <div class="am-datepicker-date">
                <input type="text" id="query_startTime" name="query_startTime" class="form-control am-datepicker-start" data-am-datepicker readonly >
                <span style="float: left; line-height: 30px; height: 30px; width: 10%; text-align: center;">至</span>
                <input type="text" id="query_endTime" name="query_endTime" class="form-control am-datepicker-end" data-am-datepicker readonly >
                </div>
              </td>
              
             <td class="search_th"><label class="control-label">是否实名制 :</label></td>
              <td class="search_td">
                <select name="addRealName" class="form-control">
                  <option value="">--选择状态--</option>
                  <option value="1">是</option>
                  <option value="2">否</option>
                </select>
              </td>
               <td></td>
             </tr>
             <tr> 
                  <td class="search_th"><label class="control-label">地&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp址：</label></td>
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
	              <th class="fontWeight_normal tdwidth100">姓名</th>
                  <th class="fontWeight_normal tdwidth100">手机号码</th>
                  <th class="fontWeight_normal tdwidth140">身份证号</th>
                  <th class="fontWeight_normal tdwidth50">积分</th>
                  <th class="fontWeight_normal tdwidth100">微信号</th>
                  <th class="fontWeight_normal tdwidth70">是否实名制</th>
                  <th class="fontWeight_normal table-title">地址</th>
                  <th class="fontWeight_normal table-title">注册时间</th>
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
	    "url": "${ctx}/wap/clerk/queryListForPage.do",
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
    {"data": "identityCard","class":""},
    {"data": "integral","class":""},
    {"data": "wechatId","class":""},
    {"data": "isRealName","class":""},
    {"data": "fullAddress","class":""},
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
    {//实名制状态  1  表示是    2  表示否
        targets: -4,
        render: function (data, type, row, meta) { 
            var state = "";
            switch(row.isRealName){
            case 2:
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
	        var context = {
	            func: [
	                {"name" : "编辑", "fn" : "editBtnClick(\'" + row.id + "\')","icon" : "am-icon-pencil-square-o","class" : "am-text-secondary"}, 
	                {"name" : "删除", "fn": "delBtnClick(\'" + row.id + "\')", "icon": "am-icon-trash-o","class" : "am-text-danger"}
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



function editBtnClick(id){
	$("#modal-insertView").html("");
    $("#modal-insertView").load("${ctx}/wap/clerk/edit.do?id="+id, function(){
    	func_after_model_load(this);
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
		var url_ = AppConfig.ctx + "/wap/clerk/delete.do";
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