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
<script src="${webResourceUrl}/resource/wap/clerk/js/createOrder.js"></script>  
<title>创建订单</title>

</head>
<body>
      
<div class="body_cont new_wrap">
	<div class="top_head">
		 <a class="return" href="javascript:;" onclick="history.go(-1);"></a>
		  创建订单
	</div>
	<div class="new_order pdlr25">
		<div class="new_input">
		 	  <div class="fonts">手机品牌：</div>
		 	  <div class="input_text">
		 	  	   <select id="brandId" name="brandId" onchange="brandChange(this.value);" class="select">
	              <c:forEach items="${brands }" var="item" varStatus="i">
	                <option value="${item.id }">${item.name }</option>
	              </c:forEach>
	            </select>
		 	  </div>
		 </div>
		 <div class="new_input">
		 	  <div class="fonts">型　　号：</div>
		 	  <div class="input_text">
		 	  	   <select id="modelId" name="modelId" class="select" onchange="selectModel(this.value);">
	              <option value="">--请选择--</option>
	              <c:forEach items="${models }" var="item" varStatus="i">
	                <option value="${item.id }">${item.name }</option>
	              </c:forEach>
	            </select>
		 	  </div>
		 </div>
		 <div class="new_input">
		 	  <div class="fonts">颜　　色：</div>
		 	  <div class="input_text">
		 	  	    <select id="colorId" name="colorId" class="select">
	              <option value="">--请选择--</option>
	            </select>
		 	  </div>
		 </div>
		 
		 
		 <div class="new_input">
		 <input type="hidden" id="projectIds" name="projectIds" />
		  <label for="addColor" class="fonts">故障类型:</label>
		 </div>
		 
		  <div id="projectDiv"  style="display: none;">
		   <div class="new_input service_cont">
		 	   <div class="half_cont">
		 	   <table>
		 	      <tr>
		 	        <td>
		 	        <input type="checkbox" class="radio" name="addProjectId" id="addProjectId" ><span class="projectName">内屏碎裂</span>
		 	        </td>
		 	        <td style="position:absolute;right:20px;">
		 	                  价格：<b class="price" style="color: #e51e1e;">￥200.00</b>
		 	        </td>
		 	      <tr>
		 	   </table>
		          
		      </div>
		    </div>
		   </div>
		 
		 
		 
		 	  
		 <div class="new_input" id="projects">
		 	  <div class="fonts">联系人姓名：</div>
		 	  <div class="input_text">
		 	  	<input class="text" type="text" id="customerName" name="customerName" />
		 	  </div>
		 </div>
		 <div class="new_input">
		 	  <div class="fonts">手机号码 ：</div>
		 	  <div class="input_text">
		 	  	<input class="text" type="text" id="customerMobile" name="customerMobile" maxlength="11" />
		 	  </div>
		 </div>
		 <div class="new_input">
		 	  <div class="fonts">地址：</div>
		 	  <div class="input_text">
		 	  		<ul class="select_addr">
  	   			 	 <li> <select  id="s_Province" name="s_Province" onchange="fn_select_address(2, this.value, '', 's_');"><option value="">--省份--</option></select></li>
                     <li> <select  id="s_City" name="s_City" onchange="fn_select_address(3, this.value, '', 's_');"><option value="">--地级市--</option></select></li>
                     <li> <select  id="s_County" name="s_County" onchange="fn_select_address(4, this.value, '', 's_');"><option value="">--区/县--</option></select></li>
  	   			   </ul>
		 	  </div>
		 </div>
		 <div class="new_input">
		 	  <div class="input_text">
		 	  		<input class="text" type="text" id="addAddress" name="addAddress" placeholder="街道/小区/楼号等" />
		 	  </div>
		 </div>
		 <div class="new_input" id="projects">
		 	  <div class="fonts">备注：</div>
		 	  <div class="input_text">
		 	  	<input class="text" type="text" id="note" name="note" />
		 	  </div>
		 </div>
		 
		  <div class="new_input" id="projects">
		    <div class="fonts">优惠劵：</div>
		    <div class="input_text" > 
		       <input class="text" type="text" id="couponCode" name="couponCode" maxlength="12"/>
  	  	   	   <input class="get_validate" type="button" style="float:right"  value="查看"  onclick="couponInfo();" />
			</div>
  	  	 </div>
		   <div class="form-group" id="projects">
	          <div class="col-sm-9 col-sm-offset-2">
	            <div id="couponInfo" ></div>
	          </div>
	        </div>
				 <div class="index_but">
				 <span ><b>总价 </b> <b id="orderPrice" price="0" style="color: #e51e1e;">¥ 0.00</b></span>
		    <a class="but" href="javascript:;" id="addSaveBtn">提   交</a>
		 </div>

		 
		 
	</div>
</div>
      


</body>
</html>