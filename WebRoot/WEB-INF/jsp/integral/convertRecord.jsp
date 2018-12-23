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

<title>积分兑换记录</title>

</head>
<body >
<div class="body_cont">
	<div class="top_head">
		 <a class="return" href="javascript:;" onclick="history.go(-1);"></a>
		  积分兑换记录
	</div>
	
	<div class="new_order graybg" style="position:relative;">
		<div id="scroll" class="mui-scroll-wrapper">
				<div class="mui-scroll order_cont">
        
		 	  
				</div>
		</div>
	</div>	 
	
	
	 <!-- 积分显示记录模板 -->
	<div class="record_list new_input convert_input" id="template" style="display: none;">
		 	  	   <div class="half_cont">
			 	  		<p class="time">2017-04-09 12:12:23</p>
			 	  		<p class="font_convert">兑换100分</p>
			 	   </div>
			 	   <div class="half_cont txtright">
			 	  		<p class="audit_state red">审核中</p>
			 	   </div>
	</div>
	          
	
	
</div>	     
</body>

<script type="text/javascript">


$(function(){
	$(".body_cont").height($(window).height());
	 getSelInfo();
});

function searchOrderList(upOrdown) {//上拉事件  
	  $.ajax({
	        type: "POST",
	        url:AppConfig.ctx+"/wap/integral/addOrders.do",
	        data: {
	            "start": start,
	            "length": length
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
  	   //订单状态
  	   var orderStatus
  	   if(n.status==2){
  		   orderStatus='审核中';
  	   }
  	   if(n.status==1){
  		   orderStatus='交易成功';
  	   }
  	 
       $(".time", tmp).text(n.inTime);
       $(".font_convert", tmp).text('兑换'+n.integral+'分');
       $(".audit_state .red", tmp).text(orderStatus);
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
var length = 10;


</script>

</html>