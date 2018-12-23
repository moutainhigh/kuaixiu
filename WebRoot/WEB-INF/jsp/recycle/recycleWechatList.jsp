<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/commons/taglibs.jsp" %>
      <div class="am-cf am-padding am-padding-bottom-0">
        <div class="am-fl am-cf" style="width: 100%;">
          <strong class="am-text-primary am-text-lg">微信用户</strong> / <small>列表查询</small>
        </div>
      </div>
      
      <hr>
      
      <div class="am-g">
	    <form id="searchForm" class="am-form am-form-horizontal">
		     <table id="searchTable">
            <tr>
              <td class="search_th search_th_frist"><label class="control-label">&nbsp&nbsp&nbsp手 机 号 ：</label></td>
              <td class="search_td" style="width:15%"><input type="text" name="query_mobile" class="form-control" ></td>
             
                 
            
              <td class="search_th"><label class="control-label">记 录 时 间 ：</label></td>
              <td class="search_td">
                <div class="am-datepicker-date">
                <input type="text" id="query_startTime" name="query_startTime" class="form-control am-datepicker-start" data-am-datepicker readonly >
                <span style="float: left; line-height: 30px; height: 30px; width: 10%; text-align: center;">至</span>
                <input type="text" id="query_endTime" name="query_endTime" class="form-control am-datepicker-end" data-am-datepicker readonly >
                </div>
              </td>
              
              <td class="search_th search_th_frist"><label class="control-label">&nbsp&nbsp&nbsp</label></td>
              <td class="search_th search_th_frist"><label class="control-label">&nbsp&nbsp&nbsp</label></td>
              <td class="search_th search_th_frist"><label class="control-label">&nbsp&nbsp&nbsp</label></td>      
            </tr>
                 <!--
            <tr>
               <td class="search_th"><label class="control-label">省 份：</label></td>
                    <td class="search_td">
                      <select name="query_province"  class="form-control" id="selectProvince" >
                        <option value="">--选择省份--</option>
                        <c:forEach items="${provinces }" var="item" varStatus="i">
	                        <option value="${item}">${item}</option>
	                    </c:forEach>
                      </select>
                    </td>
                    <td class="search_th"><label class="control-label">城 市：</label></td>
                    <td class="search_td">
                      <select name="query_city"  class="form-control" id="selectCity">
                        <option value="">--选择城市--</option>
                      </select>
                    </td>
            
              <td class="search_th search_th_frist"><label class="control-label">&nbsp&nbsp&nbsp</label></td>
              <td class="search_th search_th_frist"><label class="control-label">&nbsp&nbsp&nbsp</label></td>
              <td class="search_th search_th_frist"><label class="control-label">&nbsp&nbsp&nbsp</label></td>
            </tr>

             <tr>
               <td class="search_th"><label class="control-label">品 牌：</label></td>
                    <td class="search_td">
                      <select name="query_brand"  class="form-control" id="selectBrand" >
                        <option value="">--选择品牌--</option>
                        <c:forEach items="${brands }" var="item" varStatus="i">
	                        <option value="${item}">${item}</option>
	                    </c:forEach>
                      </select>
                    </td>
                    <td class="search_th"><label class="control-label">机 型：</label></td>
                    <td class="search_td">
                      <select name="query_model"  class="form-control" id="selectModel">
                        <option value="">--选择机型--</option>
                      </select>
                    </td>
            
              <td class="search_th search_th_frist"><label class="control-label">&nbsp&nbsp&nbsp</label></td>
              <td class="search_th search_th_frist"><label class="control-label">&nbsp&nbsp&nbsp</label></td>
              <td class="search_th search_th_frist"><label class="control-label">&nbsp&nbsp&nbsp</label></td>
            </tr>
              -->
	      </table>
	      
	       <div class="form-group">
		    <div class="am-u-sm-12 am-u-md-6">
		      <div class="am-btn-toolbar">
		        <div class="am-btn-group am-btn-group-sm m20">
		          <button onclick="refreshPage();" class="am-btn am-btn-default search_btn" type="button"> 搜  索 </button>
		        </div>
                  <!--
		        <div class="am-btn-group am-btn-group-sm">
		          <button onclick="expDataExcel();" type="button" class="am-btn am-btn-default"><span class="am-icon-file-excel-o"></span> 导出</button>
		        </div>
		        -->
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
	              <th class="fontWeight_normal tdwidth60 center">姓名</th>
	              <th class="fontWeight_normal tdwidth70  center">手机号</th>
	              <th class="fontWeight_normal tdwidth110  center">品牌</th>
	              <th class="fontWeight_normal tdwidth110  center">机型</th>
	              <th class="fontWeight_normal table-title center">地址</th>
	              <th class="fontWeight_normal tdwidth140  center">记录时间</th>
	               <th class="fontWeight_normal tdwidth130">操作</th>
	            </tr>
              </thead>
              <tbody>
              
              </tbody>
            </table>
        </div>
      </div>

    <!-- 新增弹窗 end -->
    <div id="modal-insertView" class="modal fade" tabindex="-1" role="dialog" aria-hidden="true" style="display: none;">
      <%@ include file="add.jsp" %>
    </div>
    <!-- 新增弹窗 end -->
    <!-- 修改弹窗 end -->
    <div id="modal-updateView" class="modal fade" tabindex="-1" role="dialog" aria-hidden="true" style="display: none;">
    </div>
    <!-- 修改弹窗 end -->
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
	    "url": "${ctx}/recycle/recycleWechatList/queryListForPage.do",
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
    {"data": "prizeName","class":""},
    {"data": "lotteryMobile","class":""},
    {"data": "brand","class":""},
    {"data": "model","class":""},
    {"data": "address","class":""},
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
    {//复选框
        targets:-3,
        render: function (data, type, row, meta) {
            return row.prizeProvince+row.prizeCity+row.prizeArea+row.prizeStreet;
        }
    },
    {
	    targets: -1,
	    render: function (data, type, row, meta) {
	        var context = {
                func: [
                    {"name" : "查看", "fn" : "showOrderDetail(\'" + row.id + "\')","icon" : "am-icon-pencil-square-o","class" : "am-text-secondary"},
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

function addBtnClick(){
    $("#modal-insertView").modal("show");
} 

//选择省份
$("select#selectProvince").change(function(){
	 province=$("#selectProvince").find("option:selected").text();
	 selectCity(province);
});


//获取省份下城市
function selectCity(param){
	 var url_ = AppConfig.ctx + "/recycle/recycleWechatList/getCitys.do";
	 $.post(url_,{province:province},function(data){
        if (data.success){
       	     var info=data.result.citys;
       	     //将之前的数据清空
       	      $('#selectCity option').not(":first").remove();
       	 for(var i = 0; i <info.length; i++){  
                $("#selectCity").append("<option value='"+info[i]+"'>"+info[i]+"</option>");  
            }  
        }else{
            alertTip(data.resultMessage);
        }
    });
	
}



//选择品牌
$("select#selectBrand").change(function(){
	 brand=$("#selectBrand").find("option:selected").text();
	 selectModel(brand);
});


//获取品牌下机型
function selectModel(brand){
	 var url_ = AppConfig.ctx + "/recycle/recycleWechatList/getModels.do";
	 $.post(url_,{brand:brand},function(data){
        if (data.success){
       	     var info=data.result.models;
       	     //将之前的数据清空
       	      $('#selectModel option').not(":first").remove();
       	 for(var i = 0; i <info.length; i++){  
                $("#selectModel").append("<option value='"+info[i]+"'>"+info[i]+"</option>");  
            }  
        }else{
            alertTip(data.resultMessage);
        }
    });
	
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
		var url_ = AppConfig.ctx + "/recycle/recycleWechatList/delete.do";
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
	window.open("${ctx}/file/download.do?fileId=18&ids=" + ids + params, "导出");
}


/**
 * 查看订单详情
 */
function showOrderDetail(id) {
    func_reload_page("${ctx}/recycle/wechat/detail.do?id=" + id);
}

</script>