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
<title>订单列表</title>
</head>
<body>
<div class="body_cont details_wrap">
	<div class="top_head">
		 <a class="return" href="javascript:;" onclick="history.go(-1);"></a>
		  订单列表
	</div>
	<div class="tab_list">
		 <ul>
		 	<li class="tabin"><a href="javascript:;"><span>进行中</span></a></li>
		 	<li id="finish"><a href="javascript:;"><span>已完成</span></a></li>
		 </ul>
	</div>
	
	
	
	<div class="new_order graybg">
	  
		    <div id="scroll" class="mui-scroll-wrapper" style="top:155px;">
		          <div class="mui-scroll" >  
	                            
	                   <div class="tab_nav tab_navIn" id="process" >
		               </div>
		 
		               <div class="tab_nav" id="finished" >
		               </div>
		               
		           </div>
	        </div>
		 
	</div>
</div>


  <!-- 订单模板 -->
   <div class="order_cont details_cont" id="template" style="display: none;">
       <a class="list" href="javascript:;">
			 	  <div class="order_order">
			 	  	  <p class="font icon0"> <p class="state" style="position:relative;top:-55px;"></p></p>
				 	  <p class="font icon1"> </p>
				 	  <p class="font icon2"> </p>
			 	  </div>
			 	  <div class="price_orders">
			 	  </div>
	  </a>		 	  
	</div>

<script>
$(function(){
		$(".body_cont").height($(window).height());
		initialize();
	$('.tab_list li').each(function(index){
		$(this).click(function(){
			$(".tabin").removeClass("tabin");
			$(this).addClass("tabin");
			$(".tab_navIn").removeClass("tab_navIn");
			$(".tab_nav").eq(index).addClass("tab_navIn")
			 window.scrollTo(0,0);
		});
	});
});

function searchOrderList(upOrdown) {//上拉事件  
	var realUrl;//加载路径
	var realStart//起始位
	//判断选中的列表状态
	var finish=$("#finish").hasClass("tabin");//为ture表示，选中的已完成
	if(finish){
		realUrl=AppConfig.ctx+"/wap/clerk/finishOrders.do";
		realStart=finishStart;
	}else{
		realUrl=AppConfig.ctx+"/wap/clerk/proceedOrders.do";
	    realStart=start;
	}
	  $.ajax({
	        type: "POST",
	        url:realUrl,
	        data: {
	            "start": realStart,
	            "length": length,
	        },
	        dataType: "json",
	        success: function (info) {
	            if (info.success) {
	                upOrdownFun(upOrdown, info);
	                eachFull(info.list);
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
	   //订单状态值
	   var orderStatus=n.orderStatus;
	   //订单状态名称
	   var status=n.orderStatusName;
	   //该值为1 则追加到已进行列表  为2则追加到已完成列表
	   var orderList;
	   if(orderStatus==50||orderStatus==60){
		    orderList=2;
	   }else{
		    orderList=1;
	   }
	 $('.state', tmp).text(status); 
     $(".icon0", tmp).text(n.inTime);
     $(".icon1", tmp).text(n.model);
     $(".icon2", tmp).text(n.projects);
     $(".price_orders", tmp).text('订单价格：￥'+n.orderPrice+".00");
     
     //查看订单
	 $(".list",tmp).on('tap', function () {
		  window.location.href=AppConfig.ctx+"/wap/clerk/orderDetail.do?orderNo="+n.orderNo;
     });
     if(orderList==1){
    	 $('#process').append(tmp);  
     }else{
    	 $('#finished').append(tmp);
     }
});
};




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
	var finish=$("#finish").hasClass("tabin"); //为ture表示，选中的已完成
	if(finish){
		finishStart=0;
	}else{
		start =0;
	}
searchOrderList("down");
}


/**
* 上拉加载具体业务实现
*/
function pullupRefresh() {
	//判断选中的列表状态
	var finish=$("#finish").hasClass("tabin"); //为ture表示，选中的已完成
	if(finish){
		finishStart=finishStart+length;
	}else{
		start = start + length;
	}
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
	    var finish=$("#finish").hasClass("tabin"); //为ture表示，选中的已完成
		if(finish){
			  $("#finished", $("#scroll")).empty();
		}else{
			  $("#process", $("#scroll")).empty();
		}
   // $(".mui-scroll", $("#scroll")).empty();
    mui("#scroll").pullRefresh().endPulldownToRefresh();
}
}
var start = 1;//进行中列表初始加载
var length = 5;
var finishStart=1;//已完成列表初始加载
//初始化数据 
function initialize() {
	  $.ajax({
	        type: "POST",
	        url:AppConfig.ctx+"/wap/clerk/addOrders.do",
	        data: {
	            "start": 0,
	            "length": 5,
	        },
	        dataType: "json",
	        success: function (info) {
	            if (info.success) {
	                eachFull(info.process);
	                eachFull(info.finish);
	            } else {
	                alertTip(info.msg);
	            }
	        },
	        error: function () {
	            alertTip("数据加载失败,请重新进入");
	        }
	    });
}

</script>
</body>
</html>