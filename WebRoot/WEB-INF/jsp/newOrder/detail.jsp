<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/commons/taglibs.jsp" %>
<link rel="stylesheet" href="${webResourceUrl}/resource/order/css/order.detail.css">
      <div class="am-cf am-padding am-padding-bottom-0">
        <div class="am-fl am-cf" style="width: 100%;">
          <strong class="am-text-primary am-text-lg"><a href="javascript:void(0);"  onclick="toList();">新订单管理</a></strong> / <small>订单详情</small>
          <strong class="am-text-primary"><a href="javascript:void(0);"  onclick="func_to_back();">返回</a></strong>
        </div>
      </div>
      
      <hr>
      
      <div class="am-g">
        <div class="panel panel-success index-panel-msg">
          <h4>订单状态：
            <c:choose>
              <c:when test="${order.orderStatus == 0}">新生成订单、待提交</c:when>
              <c:when test="${order.orderStatus == 2}">等待派单</c:when>
              <c:when test="${order.orderStatus == 11}">待预约</c:when>
              <c:when test="${order.orderStatus == 12}">已预约</c:when>
              <c:when test="${order.orderStatus == 50}">
                <c:if test="${order.isComment == 0 }">已完成(待评价)</c:if>
                <c:if test="${order.isComment == 1 }">已完成</c:if>
              </c:when>
              <c:when test="${order.orderStatus == 60}">
                <%-- 取消订单类型 0 未取消， 1 用户自行取消，  2 工程师取消， 3 管理员取消 --%>
                <c:if test="${order.cancelType == 1 }">用户取消</c:if>
                <c:if test="${order.cancelType == 2 }">工程师取消</c:if>
                <c:if test="${order.cancelType == 3 }">管理员取消</c:if>
                <c:if test="${order.cancelType == 4 }">客服取消</c:if>
              </c:when>
            </c:choose>
            <c:if test="${order.orderStatus != 50 && order.orderStatus != 60}">
	            <div class="am-btn-group am-btn-group-sm fr mt-5">
	                <button id="cancelBtn" type="button" class="am-btn am-btn-default">
	                    <span class="am-icon-trash-o"></span> 取消订单
	                </button>
	            </div>
            </c:if>
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
		          <h4>创建时间：<fmt:formatDate value="${order.inTime }" pattern="yyyy-MM-dd HH:mm:ss"/></h4>
		        </div><!-- /.col -->
	          </div><!-- /.row -->
	          
              <div class="row">
                <div class="col-md-6 col-sm-6 col-xs-12">
                  <h4>维修门店：${order.shopName }</h4>
                </div><!-- /.col -->
                <div class="col-md-6 col-sm-6 col-xs-12">
                  <h4>维修人员姓名：${order.engineerName }</h4>
                </div><!-- /.col -->
              </div><!-- /.row -->
              
              <div class="row">
                <div class="col-md-6 col-sm-6 col-xs-12">
                  <h4>维修人员工号：${order.engineerNumber }</h4>
                </div><!-- /.col -->
                <div class="col-md-6 col-sm-6 col-xs-12">
                  <h4>旧手机：${order.oldMobile}</h4>
                </div><!-- /.col -->
              </div><!-- /.row -->
              
              <div class="row">
                <div class="col-md-6 col-sm-6 col-xs-12">
                  <h4>新手机：${order.newMobile}</h4>
                </div><!-- /.col -->
                <c:if test="${order.orderStatus>11}">
                <div class="col-md-6 col-sm-6 col-xs-12">
                   <c:if test="${order.isUseCoupon==1}">
                  <h4>应付金额：${order.realPrice-order.couponPrice}元</h4>
                  </c:if>
                   <c:if test="${order.isUseCoupon!=1}">
                  <h4>应付金额：${order.realPrice}元</h4>
                  </c:if>
                </div><!-- /.col -->
                </c:if>
                 <c:if test="${order.orderStatus<=11}">
                <div class="col-md-6 col-sm-6 col-xs-12">
                  <h4>订单金额: 0.00元</h4>
                </div><!-- /.col -->
                </c:if>
                
              </div><!-- /.row -->
              
              <c:if test="${order.isUseCoupon==1}">
               <div class="row">
                <div class="col-md-6 col-sm-6 col-xs-12">
                  <h4>优惠卷金额：${order.couponPrice}元</h4>
                </div><!-- /.col -->
                <div class="col-md-6 col-sm-6 col-xs-12">
                  <h4>优惠卷号码：${order.couponCode}</h4>
                </div>
              </div>
              </c:if>
              
              <div class="row">
                <div class="col-md-6 col-sm-6 col-xs-12">
                  <h4>支付方式：
                    <c:if test="${orderPay.payStatus < 3}">未支付</c:if>
                    <c:if test="${orderPay.payStatus >= 3 && orderPay.payType == 0}">微信支付</c:if>
                  </h4>
                </div>
              </div>
              
              
              <div class="row">
                <div class="col-md-6 col-sm-6 col-xs-12">
                  <h4>兑换类型：
                    <c:if test="${order.selectType == 0}">换手机</c:if>
                    <c:if test="${order.selectType ==1}">换话费</c:if>
                  </h4>
                </div>
              </div>
              
              
              
              
                  <c:if test="${order.cancelReason!=null&&order.orderStatus==60}">
                <div class="row">
                <div class="col-md-6 col-sm-6 col-xs-12">
                  <h4>取消原因： ${order.cancelReason}
                  </h4>
                </div>
              </div>
                  </c:if>
              
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
		          <h4>用户姓名：${order.customerName }</h4>
		        </div><!-- /.col -->
	            <div class="col-md-6 col-sm-6 col-xs-12">
	              <h4>电话：${order.customerMobile}</h4>
	            </div><!-- /.col -->
              </div><!-- /.row -->
              
              <div class="row">
                <div class="col-md-12 col-sm-12 col-xs-12">
                  <h4>用户地址：${order.fullAddress}</h4>
                </div><!-- /.col -->
              </div><!-- /.row -->
              <div class="row">
                <div class="col-md-12 col-sm-12 col-xs-12">
                  <h4>用户备注：${order.postScript }</h4>
                </div><!-- /.col -->
              </div><!-- /.row -->
            </td>
          </tr>
          
             <tr><td colspan="3" class="tr-space"></td></tr>
          <tr>
            <td class="td-title">
              <h4>预约信息：</h4>
            </td>
            
            <td class="td-space"></td>
            <td class="td-info">
              <div class="row">
	            <div class="col-md-6 col-sm-6 col-xs-12">
		          <h4>预约上门时间：${order.agreedTime}</h4>
		        </div><!-- /.col -->
	            <div class="col-md-6 col-sm-6 col-xs-12">
	              <h4>预约新手机：
	              <c:if test="${order.agreedModel!=null}">
	                  ${order.agreedModel}&nbsp&nbsp   ${order.memory}G&nbsp&nbsp   ${order.color }
	                  &nbsp${order.edition }
	              </c:if>
	               <c:if test="${order.agreedModel==null}">
	                  ${order.agreedOther}
	              </c:if>
	              </h4>
	            </div>
              </div>
              
              <div class="row">
              
                <div class="col-md-6 col-sm-6 col-xs-12">
                  <h4>订单价格：
                  <c:if test="${order.agreedPrice!=null}">
                  ${order.agreedPrice}元
                  </c:if>
                  </h4>
                </div><!-- /.col -->
                
              </div>
            </td>
          </tr>
        </table>
      </div><!-- /am-g -->
      
    <div class="order_cancel">
 	<div class="order_cancel_cont">
 		<p>能告诉我们原因么？</p>
 		<ul class="fault_list" id="selectReason">
			    <c:forEach items="${reasonList }" var="item" varStatus="i"> 
			  <li class=""><a href="javascript:void(0);">${item.reason}</a></li>
			     </c:forEach>
		</ul>	 
		<textarea class="reason" name="" id="reason" placeholder="请写下您取消的原因吧！" maxlength="220"></textarea>
 		<div class="index_but">
			<a href="javascript:void(0);" class="btn-cancel">取消</a>
			<a href="javascript:void (0);" onclick="orderCancel('${order.id}');" class="btn-confirm">确认取消订单</a>
		</div>
 	</div>	 
</div>  
      

<script type="text/javascript">
function toList(){
	func_reload_page("${ctx}/newOrder/list.do");
}



var selectReason;//选中原因
$(function(){
    
	$("#cancelBtn").click(function () {
		$(".order_cancel").show();
	});
	
	$(".fault_list li").click(function () {
		if($(this).hasClass("fault_in")){
			$(this).removeClass("fault_in");
		}else{
		    $(this).addClass("fault_in").siblings("li").removeClass("fault_in");
		  selectReason=$(this).find("a").eq(0).text();
		}
   });
	
	$(".btn-cancel").click(function () {
		$(".fault_list li").removeClass("fault_in");
		$(".reason").val("");
		$(".order_cancel").hide();
	});
	
	
	
	
});

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