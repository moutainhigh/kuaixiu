<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/commons/taglibs.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta charset="utf-8">
<meta name="viewport" content="width=640,target-densitydpi=320, user-scalable=no"/> 
<meta name="apple-mobile-web-app-capable" content="yes">
<meta name="apple-mobile-web-app-status-bar-style" content="black">
<meta name="format-detection" content="telephone=no">
<%@ include file="/commons/configuration.jsp" %>
<link rel="stylesheet" type="text/css" href="${webResourceUrl}/resource/wap/clerk/css/style.css" />
<title>订单详情</title>
<c:set var="projects" value="${projects}"/>
</head>
<body>
<div class="body_cont">
	<div class="top_head">
		 <a class="return" href="javascript:;" onclick="history.go(-1);"></a>
		  订单详情
	</div>
	
	<div class="new_order graybg">
		 <div class="tab_nav tab_navIn">
		 	 <div class="locating_fault"><i class="icon"></i>
			     ${order.orderStatusName}
			     <c:if test="${order.orderStatus ==60 }">
			     <span class="cancel">
                                获得积分：0
                 </span>
			           </c:if>
				 	   <c:if test="${order.orderStatus ==50 }">
				 <span class="success">	   
                                 获得积分：${getIntegral.integral}
			     </span>   
			           </c:if>
			 </div>
		 	 <div class="order_cont">
			 	  <div class="order_order">
			 	  	  <p class="font icon3"><span class="mr40">${order.customerName} </span><span>${order.mobile}</span></p>
				 	  <p class="font ">${order.areas}&nbsp${order.address}</p>
				 	  <p class="font ">备注:${order.postscript}</p>
			 	  </div>
			 </div>
			 <div class="order_cont">
			 	  <div class="order_title">${order.shopName}</div>
			 	   <c:if test="${fn:length(order.shopName)>24 }">
			 	     <div class="order_title"></div>
			 	  </c:if>
			 	  <div class="order_order">
				 	  <p class="font icon1"> ${order.modelName} &nbsp&nbsp&nbsp&nbsp ${order.color} </p>
				 	  <p class="font icon2">  ${projects} </p>
			 	  </div>
			 	  <div class="price_orders txtright">
			 	  	   订单价格：<font class="red">¥ ${order.realPrice}</font>
			 	  </div>
			 </div>
			 
			 <c:if test="${ !empty c}">
			 <div class="order_cont"> 
			 <div class="order_order">
				 	  <p> 优惠券名称:${c.couponName} </p>
				 	  <p>优 惠 码:${c.couponCode} </p>
				 	  <p>有效时间:${c.beginTime}—${c.endTime}</p>
				 	  <p> 限定品牌:
				 	    <c:forEach items="${models}" var="item" varStatus="i">
				 	     ${item.brandName}&nbsp&nbsp
				 	  </c:forEach>
				 	  </p>
				 	  
				 	  <p> 限定项目:
				 	    <c:forEach items="${Couponprojects}" var="item" varStatus="i">
				 	    ${item.projectName}&nbsp&nbsp
				 	  </c:forEach>
				 	  </p>
				 	  
			 	  </div>
			 	  <div class="price_orders txtright">
			 	  	   优惠金额：<font class="red">&nbsp¥&nbsp ${c.couponPrice}</font>
			 	  </div>
			 </div>	  
			 </c:if>
			 
			 
			<c:if test="${order.orderStatus >=21}">
			 <div class="order_cont">
			 	  <p class="font_title">工程师检测故障为:</p>
			 	  <c:forEach items="${detail}" var="item" varStatus="i">
			 	  <c:if test="${item.type==1}">
			 	  <div class="new_input service_cont">
				 	  <div class="half_cont">
				 	  	   <label class="label">
				 	  	   <input class="radio" checked="checked" type="checkbox" disabled name="checks" value=${item.projectName}><span>${item.projectName}</span>
				 	  	   </label>
				 	  </div>
				 	  <div class="half_cont txtright">¥ ${item.price}</div>
				 </div>
				  </c:if>
				 </c:forEach>
				 <div class="price_orders txtright">
			 	  	   实付金额：<font class="red">¥ ${price}</font>
			 	  </div>
			 	 
			 </div>
			 
			 <div class="order_cont">
			 	  <p class="font_title">是否返店维修:</p>
			 	  
				 <div class="new_input service_cont">
				 	  <div class="half_cont">
				 	  	   <label class="label"><input class="radio" disabled checked="checked" type="radio" name="wheth" value=""><span>是</span></label>
				 	  </div>
				 	  <div class="half_cont">
				 	  	    <label class="label"><input class="radio" disabled checked="checked" type="radio" name="wheth" value=""><span>否</span></label>
				 	  </div>
				 </div>
			 </div>
			 </c:if>
			 
			 <div class="order_cont">
			 	<p class="font_title">订单号:${order.orderNo}</p>
				<p class="font_title">下单时间:${time}</p>
				<c:if test="${order.orderStatus ==50}">
				<p class="font_title">完成时间:<fmt:formatDate value="${order.endTime}" pattern="yy-MM-dd  HH:mm:ss "/></p>
				</c:if>
				<c:if test="${order.isComment==1}">
				<p class="font_title">用户评价:${comment}</p>
				</c:if>
			</div>
		    
			 
		 </div>
		 
	</div>
	
</div>
<script type="text/javascript">
$(function(){
	var strs= new Array(); //定义一数组 ,储存勾选了的维修项目
if(projects.indexOf(",")>0){
       strs=projects.split(","); //字符分割 
var obj=document.getElementsByName("checks");
    var selectProjectIds=[]; //该机型支持的所有维修项目   
for(var i=0; i<obj.length; i++){ 
     if(obj[i].checked){
    	 selectProjectIds.push(obj[i].value); 
     }	
     
}
  //判断哪个维修项目事没有被勾选的
   var c = [];//表示不该被勾选的
   var tmp = selectProjectIds.concat(strs);
   var o = {};
   for (var i = 0; i < tmp.length; i ++){
	   (tmp[i] in o) ? o[tmp[i]] ++ : o[tmp[i]] = 1;
   }
   for (x in o) if (o[x] == 1) c.push(x);
   for(var i=0; i<obj.length; i++){ 
	     if(obj[i].value==c){
	    	 obj[i].checked=false;
	     }	
	}  
}else{
		var obj=document.getElementsByName("checks");
		for(var i=0; i<obj.length; i++){ 
		     if(obj[i].value!=projects){
		    	  obj[i].checked=false;
		     }	
		}	
	}

});
	var projects="${projects}"
</script>

</body>
</html>