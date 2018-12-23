<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/commons/taglibs.jsp" %>
      <div class="am-cf am-padding am-padding-bottom-0">
        <div class="am-fl am-cf" style="width: 100%;">
          <strong class="am-text-primary am-text-lg">对账管理</strong> / <small>结算批次查询</small>
        </div>
      </div>
      
      <hr>
      
      <div class="am-g">
	    <form id="searchForm" class="form form-horizontal">
		  <div class="form-group">
		    <div class="am-u-sm-12 am-u-md-6">
		      <div class="am-btn-toolbar">
		        <div class="am-btn-group am-btn-group-sm m20">
		          <button onclick="addBalance();" class="am-btn am-btn-default search_btn" type="button"> 新增结算批次 </button>
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
	              <th class="fontWeight_normal tdwidth30">序号</th>
                  <th class="fontWeight_normal tdwidth150">批次号</th>
                  <th class="fontWeight_normal ">结算批次</th>
                  <th class="fontWeight_normal tdwidth80">连锁商数</th>
                  <th class="fontWeight_normal tdwidth60">订单数</th>
                  <th class="fontWeight_normal tdwidth100">结算金额(元)</th>
	              <th class="fontWeight_normal tdwidth140">操作</th>
	            </tr>
              </thead>
              <tbody>
              
              </tbody>
            </table>
        </div>
      </div>

<script type="text/javascript">

//自定义datatable的数据
var dto=new DtOptions();
//设置数据刷新路径
dto.ajax={
	    "url": "${ctx}/balance/queryListForPage.do"
};

//设置数据列
dto.setColumns([ 
    {"data": "id","class":"tdwidth50 center"},
    {"data": "batchNo","class":""},
    {"data": "batchName","class":""},
    {"data": "providerCount","class":""},
    {"data": "orderCount","class":""},
    {"data": "amountPrice","class":""},
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
	{
	    targets: -1,
	    render: function (data, type, row, meta) {
	        var context = {
	            func: [
	                {"name" : "明细", "fn" : "toDetail(\'" + row.id + "\')","icon" : "am-icon-pencil-square-o","class" : "am-text-secondary"},
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
 * 查看订单详情
 */
function toDetail(id){
	func_reload_page("${ctx}/balance/batchDetail.do?id="+id);
}

/**
 * 查看订单详情
 */
function addBalance(){
    func_reload_page("${ctx}/balance/orderBalance.do");
}

function delBtnClick(id){
    AlertText.tips("d_confirm", "删除提示", "确定要此结算批次吗？注意删除后不可恢复，请删除后重新制作结算单。", function(){
        //加载等待
        AlertText.tips("d_loading");
        var url_ = AppConfig.ctx + "/balance/delete.do";
        var data_ = {id: id};
        $.ajax({
            url: url_,
            data: data_,
            type: "POST",
            dataType: "json",
            success: function (result) {
                //隐藏等待
                AlertText.hide();
                if (result.success) {
                    //保存成功,关闭窗口，刷新列表
                    refreshPage();
                } else {
                    AlertText.tips("d_alert", "提示", result.msg);
                    return false;
                }
            }
        });
    });
}
</script>