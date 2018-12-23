<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/commons/taglibs.jsp" %>
<link rel="stylesheet" href="${webResourceUrl}/resource/order/css/order.detail.css">
      <div class="am-cf am-padding am-padding-bottom-0">
        <div class="am-fl am-cf" style="width: 100%;">
          <strong class="am-text-primary am-text-lg"><a href="javascript:void(0);"  onclick="toList();">连锁商管理</a></strong> / <small>连锁商详情</small>
        </div>
      </div>
      
      <hr>
      
      <div class="am-g order-info">
        <table class="detail-table">
          <tr>
            <td class="td-title">
              <h4>连锁商信息：</h4>
            </td>
            <td class="td-space"></td>
            <td class="td-info">
              <div class="row">
	            <div class="col-md-6 col-sm-6 col-xs-12">
		          <h4>名称：${provider.name }</h4>
	            </div><!-- /.col -->
	            <div class="col-md-6 col-sm-6 col-xs-12">
		          <h4>负责人名称：${provider.managerName }</h4>
		        </div><!-- /.col -->
	          </div><!-- /.row -->
	          
              <div class="row">
                <div class="col-md-6 col-sm-6 col-xs-12">
                  <h4>账号：${provider.code }</h4>
                </div><!-- /.col -->
                <div class="col-md-6 col-sm-6 col-xs-12">
                  <h4>负责人手机号：${provider.managerMobile }</h4>
                </div><!-- /.col -->
              </div><!-- /.row -->
              
              <div class="row">
                <div class="col-md-6 col-sm-6 col-xs-12">
                  <h4>开户银行：${provider.accountBank }</h4>
                </div><!-- /.col -->
                <div class="col-md-6 col-sm-6 col-xs-12">
                  <h4>开户银行支行：${provider.accountBankBranch }</h4>
                </div><!-- /.col -->
              </div><!-- /.row -->
              
              <div class="row">
                <div class="col-md-6 col-sm-6 col-xs-12">
                  <h4>开户户名：${provider.accountName }</h4>
                </div><!-- /.col -->
                <div class="col-md-6 col-sm-6 col-xs-12">
                  <h4>开户账号：${provider.accountNumber }</h4>
                </div><!-- /.col -->
              </div><!-- /.row -->
              <div class="row">
                <div class="col-md-6 col-sm-6 col-xs-12">
                  <h4>下属网点数：${shopNum }</h4>
                </div><!-- /.col -->
                <div class="col-md-6 col-sm-6 col-xs-12">
                  <h4>连锁商电话：${provider.tel }</h4>
                </div><!-- /.col -->
              </div><!-- /.row -->
              
	          <div class="row">
                <div class="col-md-12 col-sm-12 col-xs-12">
                  <h4>地址：${provider.fullAddress }</h4>
                </div><!-- /.col -->
              </div><!-- /.row -->
              
            </td>
          </tr>
        </table>
      </div><!-- /am-g -->
      
      <div class="am-g mt20">
        <div class="panel panel-success index-panel-msg">
          <div class="row">
            <div class="col-md-12 col-sm-12 col-xs-12">
              <h4>下属网点信息：</h4>
            </div><!-- /.col -->
          </div><!-- /.row -->
          
          <div class="row hide">
            <div class="col-md-12 col-sm-12 col-xs-12">
              <form id="searchForm">
		        <input type="hidden" name="hide_providerCode" value="${provider.code }" />
		      </form>
            </div><!-- /.col -->
          </div><!-- /.row -->
          
          <div class="row">
            <div class="am-u-sm-12">
	          <table id="dt" class="table table-striped table-bordered table-radius table-hover">
	            <thead>
	              <tr>
	                <th class="fontWeight_normal table-title">门店名称</th>
	                <th class="fontWeight_normal table-title">门店账号</th>
	                <th class="fontWeight_normal table-title">负责人/手机号</th>
                    <th class="fontWeight_normal table-title">门店电话</th>
	                <th class="fontWeight_normal table-title">地址</th>
	                <th class="fontWeight_normal tdwidth100">工程师数量</th>
	                <th class="fontWeight_normal tdwidth70">操作</th>
	              </tr>
	            </thead>
	            <tbody>
	            
	            </tbody>
	          </table>
            </div>
          </div><!-- /.row -->
          
        </div><!-- /panel -->
      </div><!-- /am-g -->

<script type="text/javascript">
function toList(){
	func_reload_page("${ctx}/shop/list.do");
}

//自定义datatable的数据
var dto=new DtOptions();
//设置数据刷新路径
dto.ajax={
        "url": "${ctx}/shop/queryListForPage.do",
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
    {"data": "name","class":""},
    {"data": "code","class":""},
    {"data": "managerName","class":""},
    {"data": "tel","class":""},
    {"data": "fullAddress","class":""},
    {"data": "engineerNum","class":""},
    {"defaultContent": "操作","class":""}   
]);
//设置定义列的初始属性
dto.setColumnDefs([
    {//负责人
        targets: 2,
        render: function (data, type, row, meta) {                           
            return row.managerName + "/" + row.managerMobile;
        }
    },
    {
        targets: -1,
        render: function (data, type, row, meta) {
            var context = {
                func: [
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

function delBtnClick(id){
    AlertText.tips("d_confirm", "删除提示", "确定要删除吗？", function(){
        //加载等待
        AlertText.tips("d_loading");
        var url_ = AppConfig.ctx + "/shop/delete.do";
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