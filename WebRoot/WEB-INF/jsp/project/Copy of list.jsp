<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/commons/taglibs.jsp" %>
      <div class="am-cf am-padding am-padding-bottom-0">
        <div class="am-fl am-cf" style="width: 100%;">
          <strong class="am-text-primary am-text-lg">维修项目管理</strong> / <small>列表查询</small>
        </div>
      </div>
      
      <hr>
      
      <div class="am-g">
	    <form class="am-form am-form-horizontal">
		  <div class="am-form-group">
		    <div class="am-u-sm-12 am-u-md-6">
		      <div class="am-btn-toolbar">
		        <div class="am-btn-group am-btn-group-sm">
		          <button onclick="addBtnClick();" type="button" class="am-btn am-btn-default"><span class="am-icon-plus"></span> 新增</button>
		          <!-- <button type="button" class="am-btn am-btn-default"><span class="am-icon-save"></span> 保存</button> -->
		          <button type="button" class="am-btn am-btn-default"><span class="am-icon-file-excel-o"></span> 导出</button>
		          <button onclick="batchDelBtnClick();" type="button" class="am-btn am-btn-default"><span class="am-icon-trash-o"></span> 删除</button>
		        </div>
		      </div>
		    </div>
		    <div class="am-u-sm-12 am-u-md-4">
		      <div class="am-input-group am-input-group-sm">
		        <input type="text" class="am-form-field" placeholder="项目名称" >
		        <span class="am-input-group-btn">
		          <button class="am-btn am-btn-default" type="button">搜索</button>
		        </span>
		      </div>
		    </div>
	      </div>
	    </form>
      </div>

      <div class="am-g">
        <div class="am-u-sm-12">
          <form class="am-form">
            <table class="am-table am-table-striped am-table-bordered am-table-radius am-table-hover">
              <thead>
	            <tr>
	              <th class="table-check"><input id="check_all_btn" onclick="checkAll(this)" type="checkbox" /></th>
	              <th class="table-id">序号</th>
	              <th class="table-title">项目名称</th>
	              <th class="table-date am-hide-sm-only">添加时间</th>
	              <th class="table-set">操作</th>
	            </tr>
              </thead>
              <tbody>
              <c:forEach items="${list}" var="item" varStatus="i" >
	            <tr>
	              <td><input name="item_check_btn" onclick="checkItem(this)" value="${item.id }" type="checkbox" /></td>
	              <td>${i.index + 1 }</td>
	              <td><a href="#">${item.name }</a></td>
	              <td class="am-hide-sm-only"><fmt:formatDate value="${item.createTime }" pattern="yyyy-MM-dd HH:mm:ss"/></td>
	              <td>
	                <div class="am-btn-toolbar">
	                  <div class="am-btn-group am-btn-group-xs">
	                    <button type="button" onclick="editBtnClick('${item.id}');" class="am-btn am-btn-default am-btn-xs am-text-secondary"><span class="am-icon-pencil-square-o"></span> 编辑</button>
	                    <button type="button" onclick="delBtnClick('${item.id}');" class="am-btn am-btn-default am-btn-xs am-text-danger am-hide-sm-only"><span class="am-icon-trash-o"></span> 删除</button>
	                  </div>
	                </div>
	              </td>
	            </tr>
              </c:forEach>
              </tbody>
            </table>
             <%@ include file="/WEB-INF/jsp/admin/include/inc_page_style1.jsp" %>
          </form>
        </div>
      </div>

    <!-- 新增弹窗 end -->
    <div id="modal-insertView" class="modal fade" tabindex="-1">
      <%@ include file="addProject.jsp" %>
    </div>
    <!-- 新增弹窗 end -->
    <!-- 修改弹窗 end -->
    <div id="modal-updateView" class="modal fade" tabindex="-1">
    </div>
    <!-- 修改弹窗 end -->
<script type="text/javascript">

function refreshPage(){
	ajaxNextPageMethod(1);
}

function ajaxNextPageMethod(page){
	var url_ = AppConfig.ctx + "/project/list.do";
	var data_ = {p:page};
	func_reload_page(url_, data_, 0);
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
    $("#modal-insertView").modal({closeViaDimmer : false});
} 

function editBtnClick(id){
	//console.log("d");
	$("#modal-updateView").html("");
    $("#modal-updateView").load("${ctx}/project/edit.do?id="+id);
    $("#modal-updateView").modal("show");
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
    delBtnClick(ids);
}

function delBtnClick(id){
	AlertText.tips("d_confirm", "删除提示", "确定要删除吗？", function(){
		//加载等待
        AlertText.tips("d_loading");
		var url_ = AppConfig.ctx + "/project/delete.do";
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