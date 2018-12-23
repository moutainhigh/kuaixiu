<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/commons/taglibs.jsp" %>
<link rel="stylesheet" href="${webResourceUrl}/resource/order/css/order.detail.css">
      <div class="am-cf am-padding am-padding-bottom-0">
        <div class="am-fl am-cf" style="width: 100%;">
          <strong class="am-text-primary am-text-lg"><a href="javascript:void(0);"  onclick="toList();">维修门店管理</a></strong> / <small>维修门店详情</small>
        </div>
      </div>
      
      <hr>
      
      <div class="am-g order-info">
        <table class="detail-table">
          <tr>
            <td class="td-title">
              <h4>门店信息：</h4>
            </td>
            <td class="td-space"></td>
            <td class="td-info">
              <div class="row">
	            <div class="col-md-6 col-sm-6 col-xs-12">
		          <h4>门店名称：${shop.name }</h4>
	            </div><!-- /.col -->
	            <div class="col-md-6 col-sm-6 col-xs-12">
		          <h4>负责人名称：${shop.managerName }</h4>
		        </div><!-- /.col -->
	          </div><!-- /.row -->
	          
              <div class="row">
                <div class="col-md-6 col-sm-6 col-xs-12">
                  <h4>门店账号：${shop.code }</h4>
                </div><!-- /.col -->
                <div class="col-md-6 col-sm-6 col-xs-12">
                  <h4>负责人手机号：${shop.managerMobile }</h4>
                </div><!-- /.col -->
              </div><!-- /.row -->
              
              <div class="row">
                <div class="col-md-6 col-sm-6 col-xs-12">
                  <h4>备用手机号1：${shop.managerMobile1 }</h4>
                </div><!-- /.col -->
                <div class="col-md-6 col-sm-6 col-xs-12">
                  <h4>备用手机号 2 ：${shop.managerMobile2 }</h4>
                </div><!-- /.col -->
              </div><!-- /.row -->
              
              <div class="row">
                <div class="col-md-6 col-sm-6 col-xs-12">
                  <h4>门店电话：${shop.tel }</h4>
                </div><!-- /.col -->
                <div class="col-md-6 col-sm-6 col-xs-12">
                  <h4>工程师人数：${engNum }</h4>
                </div><!-- /.col -->
              </div><!-- /.row -->
              
	          <div class="row">
                <div class="col-md-12 col-sm-12 col-xs-12">
                  <h4>地址：${shop.fullAddress }</h4>
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
              <h4>门店维修工程师信息：</h4>
            </div><!-- /.col -->
          </div><!-- /.row -->
          
          <div class="row hide">
            <div class="col-md-12 col-sm-12 col-xs-12">
              <form id="searchForm">
                <input type="hidden" name="hide_shopCode" value="${shop.code }" />
              </form>
            </div><!-- /.col -->
          </div><!-- /.row -->
          
          <div class="row">
            <div class="col-md-12 col-sm-12 col-xs-12">
            <table id="dt" class="table table-striped table-bordered table-radius table-hover">
              <thead>
                <tr>
                  <th class="fontWeight_normal tdwidth60">工号</th>
                  <th class="fontWeight_normal table-title">所属连锁商</th>
                  <th class="fontWeight_normal table-title">所属门店商</th>
                  <th class="fontWeight_normal tdwidth140">姓名/手机号</th>
                  <th class="fontWeight_normal tdwidth60">状态</th>
                  <th class="fontWeight_normal tdwidth80">今日订单数</th>
                  <th class="fontWeight_normal tdwidth160">身份证号码</th>
                  <th class="fontWeight_normal tdwidth70">操作</th>
                </tr>
              </thead>
              <tbody>
              
              </tbody>
            </table><!-- /table -->
            </div><!-- /.col -->
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
        "url": "${ctx}/engineer/queryListForPage.do",
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
    {"data": "number","class":""},
    {"data": "providerName","class":""},
    {"data": "shopName","class":""},
    {"data": "name","class":""},
    {"data": "isDispatch","class":""},
    {"data": "orderDayNum","class":""},
    {"data": "idcard","class":""},
    {"defaultContent": "操作","class":""}   
]);
//设置定义列的初始属性
dto.setColumnDefs([
    {
        targets: 3,//工程师工号
        render: function (data, type, row, meta) {
            return row.name + "/" + row.mobile;
        }
    },
    {//状态
        targets: 4,
        render: function (data, type, row, meta) { 
        	if(row.isDispatch == 2){
        		return "下线";
        	}else if(row.isDispatch == 1){
                return "已派出";
            }else{
                return "空闲";
            }
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
        var url_ = AppConfig.ctx + "/engineer/delete.do";
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