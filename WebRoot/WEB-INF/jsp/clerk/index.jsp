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
<link rel="stylesheet" type="text/css" href="${webResourceUrl}/plugins/mui/css/mui.update.css"/>
<link rel="stylesheet" type="text/css" href="${webResourceUrl}/resource/wap/clerk/css/style.css" />
<script src="${webResourceUrl}/resource/wap/clerk/js/index.js"></script>  
<title>创建订单</title>

</head>
<body>
<div class="body_cont">
	<div class="top_head" >
		 <a class="return" href="javascript:;" onclick="history.go(-1);"></a>
		  手机超人快修店员平台
		  
		 
		    
	</div>
	
	      
              
	 
	
	 <div class="index_bottom">
		 <ul class="bottom_list">
		 	 <li class="bottom_in"><a href="${webResourceUrl}/wap/clerk/index.do"><i class="icon1"></i>主页</a></li>
		 	 <li><a href="${webResourceUrl}/wap/integral/convert.do"><i class="icon2"></i>积分</a></li>
		 	 <li><a href="${webResourceUrl}/wap/clerk/myNews.do"><i class="icon3"></i>我的</a></li>
		 </ul>
	 </div>
	
	<div class="new_order index_order graybg" style="position:relative;">
	          <div class="order_cont" >
		 	                 <div class="new_input">
		 	  	   <div class="half_cont index_icon">
			 	  		<a class="order_indexicon" href="${webResourceUrl}/wap/clerk/createOrder.do">
			 	  			<i class="icon0"></i><p>创建订单</p>
			 	  		</a>
			 	   </div>
			 	   <div class="half_cont index_icon">
			 	  		<a class="order_indexicon" href="${webResourceUrl}/wap/clerk/clerkOrders.do">
			 	  			<i class="icon1"></i><p>订单列表</p>
			 	  		</a>
			 	   </div>
		 	  </div>
              </div>  
              
              
              <div id="scroll" class="mui-scroll-wrapper" style="top:270px;bottom:86px;">
				<div class="mui-scroll ">
                      
		 	  
				</div>
		      </div>
      </div>     
	
	
	<!-- 订单模板 -->
		<div class="order_cont dope_list" name="orderId" id="template" style="display: none;">
			<a class="close" href="javascript:;" style="font-size:35px;">×</a>
			<a class="list" href="javascript:;">
				<i class="icon icon1"></i>
				<p class="title">订单信息</p>
				<p class="time">05-10 12:19</p>
				<p class="cont">提交了新订单</p>
				<p class="look">立即查看</p>
			</a>
		</div>
</div>
<script>
	$(function () {
		$(".body_cont").height($(window).height());
		 getSelInfo();
	});
	
	
	function searchOrderList(upOrdown) {
		  $.ajax({
		        type: "POST",
		        url:AppConfig.ctx+"/wap/clerk/loadMoreOrders.do",
		        data: {
		            "start": start,
		            "length": length
		        },
		        dataType: "json",
		        success: function (info) {
		            if (info.success) {
		                upOrdownFun(upOrdown, info);
		                eachFull(info.orderList);
		            } else {
		                alertTip(info.msg);
		            }
		        },
		        error: function () {
		            alertTip("数据加载失败,请重新进入");
		        }
		    });
	}





	function eachFull(info) {
	    $.each(info, function (i, n) {
	    	 var tmp = $("#template").clone();
	    	 tmp.removeAttr("id");
	    	 tmp.show();
	    	 //积分信息订单
	    	 if(n.type==1){
	    		  //给该div设置id
	    		  tmp.attr("id", n.integralNo);
	    		  //移除订单显示
	    		  $(".title", tmp).text('积分信息');
	    		  $(".time", tmp).text(n.inTime);
	    		  $(".cont", tmp).text('获得'+n.integrals+'积分');
	    		  //查看订单
	    		  $(".list",tmp).on('tap', function () {
	    			  window.location.href=AppConfig.ctx+"/wap/clerk/orderDetail.do?orderNo="+n.orderNo;
	              });
	    		  //移除订单显示
	    		  $(".close",tmp).on('tap', function () {
	    			  floatIntegral(n.integralNo);
	              });
	    		  
	    	 }
	    	 //订单信息
	    	 if(n.type==2){
	    		  tmp.attr("id", n.orderNo);
	    		  $(".title", tmp).text('订单信息');
	    		  $(".time", tmp).text(n.inTime);
	    		  $(".cont", tmp).text('提交了新订单');
	    		//查看订单
	    		  $(".list",tmp).on('tap', function () {
	    			  window.location.href=AppConfig.ctx+"/wap/clerk/orderDetail.do?orderNo="+n.orderNo;
	              });
	    		  //移除订单显示
	    		  $(".close",tmp).on('tap', function () {
	    			  floatOrder(n.orderNo);
	              });
	    	 }
	    	 
	       
	          //追加到对应区块
	         $(".mui-scroll").append(tmp);
	    });
	};
	 
	

	function getSelInfo() {
	    //重置page
	    start = 0;
	    searchOrderList("click");
	}

	/* 初始化绑定上下拉动刷新事件 */
	mui('#scroll').pullRefresh({
	    //下拉
	    down: {
	        callback: pulldownRefresh
	    },
	    //上拉
	    up: {
	        contentrefresh: '正在加载...',
	        callback: pullupRefresh
	    }
	});


	/**
	 * 下拉刷新具体业务实现
	 */
	function pulldownRefresh() {
	    //重置page
	    start = 0;
	    searchOrderList("down");
	}


	/**
	 * 上拉加载具体业务实现
	 */
	function pullupRefresh() {
	    //上拉直接查询下一页数据
	    start = start + length;
	    searchOrderList("up");
	}


	//统一处理下拉上划动画的显示隐藏
	function upOrdownFun(upOrdown, info) {
	    //上拉
	    if (upOrdown == "up") {
	        if (info.orderList == "") {
	            mui("#scroll").pullRefresh().endPullupToRefresh(true);
	        } else {
	            mui("#scroll").pullRefresh().endPullupToRefresh(false);
	        }
	    }
	    //下拉
	    else if (upOrdown == "down") {
	        //清空
	        $(".mui-scroll", $("#scroll")).empty();
	        mui("#scroll").pullRefresh().endPulldownToRefresh();
	        mui("#scroll").pullRefresh().refresh(true);
	    }
	    //进入
	    else if (upOrdown == "click") {
	        //清空
	        $(".mui-scroll", $("#scroll")).empty();
	        //重置
	        mui("#scroll").pullRefresh().refresh(true);
	    }
	}
	var start = 0;
	var length = 5;	
	
	
	function floatIntegral(c){
		$("#"+c).slideToggle(400);
		  setTimeout(function () {
		        $("#"+c).remove();
		    },400);
		$.ajax({
			url:AppConfig.ctx+'/wap/clerk/integralShow.do',
	    	  data:{id:c},
	    	  type:"post", 
	      dataType:"json",
	       success:function(result){
	    	   if(result.success){
	    	   }else{
	    		   alertTip(result.msg);
	    	   }
	       }
			
		});
		
	};
		
	function floatOrder(c){
		$("#"+c).slideToggle(400);
		  setTimeout(function () {
		        $("#"+c).remove();
		    },400);
		  
		$.ajax({
			url:AppConfig.ctx+'/wap/clerk/orderShow.do',
	    	  data:{orderNo:c},
	    	  type:"post", 
	      dataType:"json",
	       success:function(result){
	    	   if(result.success){
	    	   }else{
	    		   alertTip(result.msg);
	    	   }
	       }
			
		});
	};
</script>
</body>
</html>