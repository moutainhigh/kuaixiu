<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/commons/taglibs.jsp" %>
      <div class="am-cf am-padding am-padding-bottom-0">
        <div class="am-fl am-cf" style="width: 100%;">
          <strong class="am-text-primary am-text-lg">兑换机型管理</strong> / <small>列表查询</small>
        </div>
      </div>
      
      <hr>
      
      <div class="am-g">
	    <form id="searchForm" class="am-form am-form-horizontal">
		  <div class="am-form-group">
		    <div class="am-u-sm-12 am-u-md-6">
		      <div class="am-btn-toolbar">
		        <div class="am-btn-group am-btn-group-sm">
		          <button onclick="addBtnClick();" type="button" class="am-btn am-btn-default"><span class="am-icon-plus"></span> 新增</button>
		          <!-- <button type="button" class="am-btn am-btn-default"><span class="am-icon-save"></span> 保存</button> -->
		   <!--   <button  onclick="expDataExcel();" type="button" class="am-btn am-btn-default"><span class="am-icon-file-excel-o"></span> 导出</button>  -->    
		          <button onclick="batchDelBtnClick();" type="button" class="am-btn am-btn-default"><span class="am-icon-trash-o"></span> 删除</button>
		        </div>
		      </div>
		    </div>
		    <div class="am-u-sm-12 am-u-md-4">
		      <div class="am-input-group am-input-group-sm">
		        <table style="width:180px;position:relative;left:50px;">
		         <tr>
		         <td>品牌：</td>
		         <td>
		         <select id="query_brand" name="query_brand"  class="form-control-inline">
	              <option value="">--请选择--</option>

	              <c:forEach items="${brands}" var="item" varStatus="i">
	                <option value="${item.id }">${item.name }</option>
	               <!--  
	                 <c:choose>
	               <c:when test="${item.id=='c70be5d5-5bc6-11e7-98d3-d4bed949bdc0' }">
	               <option value="${item.id }" selected>${item.name }</option>
	               </c:when>
	                  <c:otherwise>
	                <option value="${item.id }">${item.name }</option>
	                  </c:otherwise>
	                </c:choose>
	                -->
	              </c:forEach>

	            </select>
	              </td>
	            </tr>
		       </table> 
		        
		        
		        <span class="am-input-group-btn">
		          <button onclick="refreshPage();" class="am-btn am-btn-default" type="button">搜索</button>
		        </span>
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
	              <th class="fontWeight_normal table-title">排序</th>
	              <th class="fontWeight_normal table-title">手机名称</th>
                  <th class="fontWeight_normal table-title">品牌</th>
                  <th class="fontWeight_normal table-title">颜色</th>
                  <th class="fontWeight_normal table-title">内存(G)</th>
                  <th class="fontWeight_normal table-title">网络类型</th>
                  <th class="fontWeight_normal table-title">价格(元)</th>
                  <th class="fontWeight_normal table-title">上/下架</th>
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
      <%@ include file="addModel.jsp" %>
    </div>
    <!-- 新增弹窗 end -->
    <!-- 修改弹窗 end -->
    <div id="modal-updateView" class="modal fade" tabindex="-1" role="dialog" aria-hidden="true" style="display: none;">
    </div>
    <!-- 修改弹窗 end -->
<script type="text/javascript">
//自定义datatable的数据
var dto=new DtOptions();
//设置数据刷新路径
dto.ajax={
	    "url": "${ctx}/newModel/queryListForPage.do",
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
    {"data": "sort","class":""},
    {"data": "name","class":""},
    {"data": "brandName","class":""},
    {"data": "color","class":""},
    {"data": "memory","class":""},
    {"data": "edition","class":""},
    {"data": "price","class":""},
    {"data": "isPutaway","class":""},
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
    {//是否上架
        targets: -2,
        render: function (data, type, row, meta) {
        	  var state = "";
              switch(row.isPutaway){
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
	$("#modal-insertView").html("");
    $("#modal-insertView").load("${ctx}/newModel/add.do", function(){
    	func_after_model_load(this);
    });
} 

function editBtnClick(id){
	//console.log("d");
	$("#modal-updateView").html("");
    $("#modal-updateView").load("${ctx}/newModel/edit.do?id="+id, function(){
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
	window.open("${ctx}/file/download.do?fileId=6&ids=" + ids + params, "导出");
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
		var url_ = AppConfig.ctx + "/newModel/delete.do";
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