<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/commons/taglibs.jsp" %>
<link rel="stylesheet" href="${webResourceUrl}/resource/order/css/order.detail.css">
      <div class="am-cf am-padding am-padding-bottom-0">
        <div class="am-fl am-cf" style="width: 100%;">
          <strong class="am-text-primary am-text-lg"><a href="javascript:void(0);"  onclick="toList();">碎屏险订单管理</a></strong> / <small>订单详情</small>
          <strong class="am-text-primary"><a href="javascript:void(0);"  onclick="func_to_back();">返回</a></strong>
        </div>
      </div>
      
      <hr>
      
      <div class="am-g">
        <div class="panel panel-success index-panel-msg">
          <h4>订单状态：
            <c:choose>
              <c:when test="${order.orderStatus == 0}">未付款</c:when>
              <c:when test="${order.orderStatus == 1}">已付款</c:when>
              <c:when test="${order.orderStatus == 2}">退款中</c:when>
              <c:when test="${order.orderStatus == 3}">已退款</c:when>
              <c:when test="${order.orderStatus == 4}">提交失败</c:when>
              <c:when test="${order.orderStatus == 5}">提交成功</c:when>
              <c:when test="${order.orderStatus == 6}">已激活</c:when>
              <c:when test="${order.orderStatus == 10}">已取消</c:when>
            </c:choose>
          </h4>
        </div><!-- /panel -->
      </div><!-- /am-g -->
      
      <div class="am-g order-info mt20">
        <table class="detail-table">
          <tr>
            <td class="td-title">
              <h4>订单信息：</h4>
            </td>
            <td class="td-space"></td>
            <td class="td-info">
              <div class="row">
	            <div class="col-md-6 col-sm-6 col-xs-12">
		          <h4>订单号：${order.orderNo }</h4>
	            </div><!-- /.col -->
	            <div class="col-md-6 col-sm-6 col-xs-12">
		          <h4>购买时间：<fmt:formatDate value="${order.inTime }" pattern="yyyy-MM-dd HH:mm:ss"/></h4>
		        </div><!-- /.col -->
	          </div><!-- /.row -->
	          
              <div class="row">
                <div class="col-md-6 col-sm-6 col-xs-12">
                  <h4>碎屏险名称：${order.projectName }</h4>
                </div><!-- /.col -->
                  <div class="col-md-6 col-sm-6 col-xs-12">
                   <c:if test="${order.isPayment==0}">
                  <h4>是否付款：未付款</h4>
                  </c:if>
                   <c:if test="${order.isPayment==1}">
                  <h4>是否付款：已付款</h4>
                  </c:if>
                </div><!-- /.col -->
              </div><!-- /.row -->
              
              
              <div class="row">
                <div class="col-md-6 col-sm-6 col-xs-12">
                  <h4>碎屏险购买金额：${order.orderPrice }元</h4>
                </div><!-- /.col -->
                <div class="col-md-6 col-sm-6 col-xs-12">
                  <h4>碎屏险最高保额：${order.maxPrice}元</h4>
                </div><!-- /.col -->
              </div><!-- /.row -->
              
              
              <c:if test="${order.refundReason!=null}"> 
              <div class="row">
                <div class="col-md-6 col-sm-6 col-xs-12">
                  <h4>退款原因：${order.refundReason }</h4>
                </div><!-- /.col -->
              </div><!-- /.row -->
              </c:if>
              
                <c:if test="${order.refundTime!=null}"> 
              <div class="row">
                <div class="col-md-6 col-sm-6 col-xs-12">
                  <h4>退款时间：${order.refundTime }</h4>
                </div><!-- /.col -->
              </div><!-- /.row -->
              </c:if>
              
                 <c:if test="${order.errorReason!=null}"> 
              <div class="row">
                <div class="col-md-6 col-sm-6 col-xs-12">
                  <h4>错误原因：${order.errorReason }</h4>
                </div><!-- /.col -->
              </div><!-- /.row -->
              </c:if>
              
              <c:if test="${order.refundResult!=null}"> 
              <div class="row">
                <div class="col-md-6 col-sm-6 col-xs-12">
                  <h4>退款处理结果：${order.refundResult}</h4>
                </div><!-- /.col -->
              </div><!-- /.row -->
              </c:if>
              
              
               <c:if test="${order.endTime!=null}"> 
              <div class="row">
                <div class="col-md-6 col-sm-6 col-xs-12">
                  <h4>支付时间：<fmt:formatDate value="${order.endTime }" pattern="yyyy-MM-dd HH:mm:ss"/></h4>
                </div><!-- /.col -->
                 <div class="col-md-6 col-sm-6 col-xs-12">
                  <h4>手机品牌：${order.projectBrand}</h4>
                </div><!-- /.col -->
              </div><!-- /.row -->
              </c:if>
              
              
              
             
              <div class="row">
               <c:if test="${order.mobileModel!=null}"> 
                <div class="col-md-6 col-sm-6 col-xs-12">
                   <h4>手机机型：${order.mobileModel}</h4>
                </div><!-- /.col -->
                </c:if>
                <c:if test="${order.mobileCode!=null}"> 
                <div class="col-md-6 col-sm-6 col-xs-12">
                   <h4>手机串码：${order.mobileCode}</h4>
                </div><!-- /.col -->
                </c:if>
              </div><!-- /.row -->
            
              <div class="row">
               <c:if test="${order.isActive==1}"> 
                <div class="col-md-6 col-sm-6 col-xs-12">
                   <h4>保修时间：<fmt:formatDate value="${order.inTime }" pattern="yyyy-MM-dd HH:mm:ss"/>——<fmt:formatDate value="${outOfTime}" pattern="yyyy-MM-dd HH:mm:ss"/></h4>
                </div><!-- /.col -->
                </c:if>
                 <c:if test="${importTime!=null}"> 
                <div class="col-md-6 col-sm-6 col-xs-12">
                   <h4>导入时间：<fmt:formatDate value="${importTime}" pattern="yyyy-MM-dd HH:mm:ss"/></h4>
                </div><!-- /.col -->
                </c:if>
              </div><!-- /.row -->
            
            
            </td>
          </tr>
          
          
          
          <tr><td colspan="3" class="tr-space"></td></tr>
          <tr>
            <td class="td-title">
              <h4>用户信息：</h4>
            </td>
            <td class="td-space"></td>
            <td class="td-info">
              <div class="row">
	            <div class="col-md-6 col-sm-6 col-xs-12">
		          <h4>用户姓名：${order.name }</h4>
		        </div><!-- /.col -->
	            <div class="col-md-6 col-sm-6 col-xs-12">
	              <h4>电话：${order.mobile}</h4>
	            </div><!-- /.col -->
              </div><!-- /.row -->
            </td>
          </tr>
        </table>
      </div><!-- /am-g -->
      

<script type="text/javascript">
function toList(){
	func_reload_page("${ctx}/newOrder/list.do");
}



//取消订单
function orderCancel(id){
	    var reason=$("#reason").val();
	    var url_ = AppConfig.ctx + "/newOrder/orderCancel.do";
        $.ajax({
            url: url_,
            type: "POST",
            data: {id:id,reason:reason,selectReason:selectReason},
            dataType: "json",
            success: function (result) {
                if (result.success) {
                	func_reload_page("${ctx}/newOrder/detail.do?id="+id);
                } else {
                    AlertText.tips("d_alert", "提示", result.msg);
                }
            },
            error : function() {
                AlertText.tips("d_alert", "提示", "系统异常，请稍后再试");
            }
        });
        
}



</script>