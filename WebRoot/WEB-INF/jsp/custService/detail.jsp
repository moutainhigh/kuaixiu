<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/commons/taglibs.jsp" %>
<link rel="stylesheet" href="${webResourceUrl}/resource/order/css/order.detail.css">
      <div class="am-cf am-padding am-padding-bottom-0">
        <div class="am-fl am-cf" style="width: 100%;">
          <strong class="am-text-primary am-text-lg"><a href="javascript:void(0);"  onclick="toList();">维修工程师管理</a></strong> / <small>工程师信息统计</small>
        </div>
      </div>
      
      <hr>
      
      <div class="am-g">
        <div class="panel panel-success index-panel-msg">
          <div class="row">
            <div class="col-md-6 col-sm-6 col-xs-12">
              <h4>姓名：${eng.name }</h4>
            </div><!-- /.col -->
            <div class="col-md-6 col-sm-6 col-xs-12">
              <h4>性别：${eng.gender }</h4>
            </div><!-- /.col -->
          </div><!-- /.row -->
          
          <div class="row">
            <div class="col-md-6 col-sm-6 col-xs-12">
              <h4>工号：${eng.number }</h4>
            </div><!-- /.col -->
            <div class="col-md-6 col-sm-6 col-xs-12">
              <h4>身份证号码：${eng.idcard }</h4>
            </div><!-- /.col -->
          </div><!-- /.row -->
          
          <div class="row">
            <div class="col-md-6 col-sm-6 col-xs-12">
              <h4>手机号：${eng.mobile }</h4>
            </div><!-- /.col -->
            <div class="col-md-6 col-sm-6 col-xs-12">
              <h4>状态：
                <c:if test="${eng.isDispatch == 0}">空闲</c:if>
                <c:if test="${eng.isDispatch == 1}">已派出</c:if>
              </h4>
            </div><!-- /.col -->
          </div><!-- /.row -->
        </div><!-- /panel -->
      </div><!-- /am-g -->
      
      <div class="am-g mt20">
        <div class="panel panel-success index-panel-msg">
          
          <div class="row">
            <div class="col-md-12 col-sm-12 col-xs-12">
              <div id="echart-panel" style="width: 100%; height: 200px;"></div>
            </div><!-- /.col -->
          </div><!-- /.row -->
          
        </div><!-- /panel -->
      </div><!-- /am-g -->

<script type="text/javascript">
function toList(){
	func_reload_page("${ctx}/engineer/list.do");
}

// 初始化echarts实例
var myChart = echarts.init(document.getElementById("echart-panel"),"macarons");

function reloadEchart(){
    var url_ = AppConfig.ctx + "/order/queryStatisticByEngineer.do";
    $.ajax({
        url: url_,
        type: "POST",
        data: {"engineerId" : "${eng.id}"},
        dataType: "json",
        success: function (result) {
            if (result.success) {
                myChart.setOption(result.data);
            }
        },
        error : function() {
        }
    });
}

reloadEchart();
</script>