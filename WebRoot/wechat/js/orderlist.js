$(function(){
    getSelInfo('1');
});

var pIndex = 1;
var off_on = false,//分页开关
    num = 1;
    
var access_token = sessionStorage.getItem('access_token');

function getSelInfo(index) {
    var params = {
        pageIndex:pIndex,
        pageSize:5,
        status:'0,2,3,5,11,12,20,30,35,40,50,60',
        newStatus:'0,2,11,12,50,60'
    };
    $.ajax({
        type:'POST',
        url:linkUrl+'/wechat/order/getOrderList.do',
        dataType:'json',
        data:{
            access_token:access_token,
            params:JSON.stringify(params)
        },
        xhrFields: {
            withCredentials: true
        },
        crossDomain: true,
        success: function(data){
            if (data.success){
                var result = data.result.orderList;
                if (result.length == 0){
                    $('.baseline').remove();
                    $(".orderList").append('<p class="baseline color-light-blue">——没有更多数据了！——</p>');
                    return false;
                }
                $('.baseline').remove();
                $(".main").append('<p class="baseline color-light-blue"><img src="img/loadings.gif"/></p>');
                eachFull(result);
                off_on = true;
                document.cookie="is_login=1"; //存入登录状态
            }else {
                if (data.hasOwnProperty("exceptionType")) {
                    eCacheUtil.storage.cache(CacheKey.exceptionType,data.exceptionType);
                    eCacheUtil.storage.cache(CacheKey.errorExceptionMsg, data.msg);
                    window.location.href = 'error.html';
                } else {
                    alertTip(data.resultMessage);
                    if (data.resultCode!=null&&data.resultCode.indexOf('200')>=0){
                        window.location.href = 'index.html';
                    }
                }
            }
            loading_hide();
        },
        error: function(jqXHR){
            loading_hide();
            alertTip('系统异常，请稍后再试！');
        }

    });
}
function eachFull(info) {
    var base = new Base64();
    $.each(info, function (i, n) {
        var tmp,orderid,type,isRework = 0;
        if(n.orderType==0){//是维修订单
            tmp = $("#template").clone();
            tmp.removeAttr("id");
            orderid = n.id;
			orderNo=n.orderNo;
			isRework= n.isRework;
            type = 0;
            $(".order_inside",tmp).on('click', function () {
                onDetails(orderid,type,isRework,orderNo);
            });
            tmp.show();
            
            //如果该订单还未派单
            if(n.shopName==null){
                $(".shop_name", tmp).text("维修订单");
            }else{
                $(".shop_name", tmp).text(n.shopName);
            }

			//是否返修订单  0否 1是
		  /*	if(n.isRework == 1){
				$(".model", tmp).append(n.modelName+n.color+"(售后)");
			}else{
				$(".model", tmp).append(n.modelName+n.color);
			}   */
			
			$(".model", tmp).append(n.modelName+n.color);
            
            var cusPro = [],spePro = [];
            for (var j = 0;j<n.projects.length;j++){
                if (n.projects[j].type == 1){
                    spePro.push(n.projects[j].projectName);
                }else {
                    cusPro.push(n.projects[j].projectName);
                }
            }
            if (spePro.length != 0){
                $(".repair",tmp).append(spePro.join('、'));
            }else {
                $(".repair",tmp).append(cusPro.join('、'));
            }
            $(".right p", tmp).text(n.realPrice.toFixed(2));

        }else{//是以旧换新订单
            tmp = $("#oldtonew").clone();
            tmp.removeAttr("id");
            orderid = n.id;
			isRework= n.isRework;
            type = 1;
            $(".order_inside",tmp).on('click', function () {
                onDetails(orderid,type,isRework,orderNo);
            });
            tmp.show();
            //如果订单还未派单
            if(n.shopName==null){
                $(".shop_name", tmp).text("以旧换新订单");
            }else{
                $(".shop_name", tmp).text(n.shopName);
            }
            if (n.selectType == 0){//换手机
                if(n.isAgreed==0){
                    $(".left p", tmp).text(n.agreedNews.agreedModel+" "+n.agreedNews.color+" "+n.agreedNews.memory+"G "+n.agreedNews.edition);
                }else if(n.isAgreed==1){
                    $(".left p", tmp).text(n.oldModel);
                }
            }else {
                $(".left p", tmp).text("换话费");
            }
            $(".right p", tmp).text(n.realPrice.toFixed(2));
        }

        $(".time", tmp).text(n.inTime);
         //订单状态
         var orderStatus = n.orderStatus;
            //订单状态对应名称
            var orderStatusName=orderStatusM[orderStatus];
            //是否已评价(0:未评价)
            var isComment = n.isComment;
			
			//是否返修订单  0否 1是
			if(n.isRework == 1){
				//订单状态字体为蓝色,“已完成”为黑色
				if(orderStatus == "50"){//已完成状态，判断是滞已评价：0为未评价，则订单状态为“待评价”，否则为“已完成”
					if(isComment=="0"){
						$(".status", tmp).text("待评价(售后)");
						orderStatus = "50_0";
					}else{
						$(".status", tmp).text("已完成(售后)").addClass('color-black');
					}
				}else {
					$(".status", tmp).text(orderStatusName + "(售后)");
				}
			}else{
				//订单状态字体为蓝色,“已完成”为黑色
				if(orderStatus == "50"){//已完成状态，判断是滞已评价：0为未评价，则订单状态为“待评价”，否则为“已完成”
					if(isComment=="0"){
						$(".status", tmp).text("待评价");
						orderStatus = "50_0";
					}else{
						$(".status", tmp).text("已完成").addClass('color-black');
					}
				}else {
					$(".status", tmp).text(orderStatusName);
				}
			}
            
            //按钮是否显示
            var orderStatusStr = orderStatusForBtn[orderStatus];
            if(orderStatusStr != undefined && orderStatusStr != ""){
                $(".order_but",tmp).css("display","flex");
                $("[name=btn_status]", tmp).text(orderStatusStr);
                if(orderStatus == "50_0"){//评价按钮
                    $("[name=rebtn_status]", tmp).text("查看订单").on('click', function () {
                        onDetails(orderid,type,isRework,orderNo);
                    });
                    $("[name=btn_status]", tmp).text("去评价").on('click', function () {
                        var randomValue = getRandomStr();
                        eCacheUtil.storage.cache(CacheKey.orderType,type);
                        eCacheUtil.storage.cache(CacheKey.orderId,base.encode(orderid));
						eCacheUtil.storage.cache(CacheKey.orderNo,orderNo);
						eCacheUtil.storage.cache(CacheKey.isRework,isRework);
						
                        window.location.href = "evaluation.html?r=" + randomValue;
                    });
                }else if(orderStatus == "30"){
                    $("[name=rebtn_status]", tmp).text("查看订单").on('click', function () {
                        onDetails(orderid,type,isRework,orderNo);
                    });
                    //确认付款按钮
                    $("[name=btn_status]", tmp).text("确认付款").on('click', function () {
                        eCacheUtil.storage.cache(CacheKey.orderId,base.encode(orderid));
						eCacheUtil.storage.cache(CacheKey.orderNo,orderNo);
						eCacheUtil.storage.cache(CacheKey.isRework,isRework);
                        payBalance();
                    });
                }else if(n.orderType == 1&&orderStatus == "12"){
                    $("[name=rebtn_status]", tmp).text("查看订单").on('click', function () {
                        onDetails(orderid,type,isRework,orderNo);
                    });
                    //确认付款按钮
                    $("[name=btn_status]", tmp).text("确认付款").on('click', function () {
                        eCacheUtil.storage.cache(CacheKey.orderId,base.encode(orderid));
						eCacheUtil.storage.cache(CacheKey.orderNo,orderNo);
						eCacheUtil.storage.cache(CacheKey.isRework,isRework);
                        payBalance();
                    });
                }else{
                    $("[name=rebtn_status]",tmp).css("display","none");
                    $("[name=btn_status]", tmp).text("查看订单").on('click', function () {
                        onDetails(orderid,type,isRework,orderNo);
                    });
                }
            }else{
                //查看订单按钮
                $(".order_but",tmp).css("display","flex");
                $("[name=rebtn_status]",tmp).css("display","none");
                $("[name=btn_status]", tmp).text("查看订单").on('click', function () {
                    onDetails(orderid,type,isRework,orderNo);
                });
            }
            //追加到对应区块
            $(".orderList").append(tmp);
    });
}

function onDetails(orderId,num,isRework,orderNo) {
    var randomValue = getRandomStr(),
        base = new Base64();
    eCacheUtil.storage.cache(CacheKey.orderId,base.encode(orderId));
	eCacheUtil.storage.cache(CacheKey.orderNo,orderNo);
	eCacheUtil.storage.cache(CacheKey.isRework,isRework);
    if (num == 0){
        window.location.href = "order_details.html?r=" + randomValue;
    }else {
        window.location.href = "new_details.html?r=" + randomValue;
    }
}

//订单状态
var orderStatusM = {
    "0":"生成订单",
    "2": "待派单",
    "3": "待门店收件",
    "5": "门店已收件",
    "11": "待预约",
    "12": "已预约",
    "20": "定位故障",
    "30": "待用户付款",
    "35": "正在维修",
    "40": "待客户收件",
    "50": "已完成",
    "60": "已取消"
};

//订单状态相对应的按钮
var orderStatusForBtn = {
    "12":"确认付款",
    "30": "确认付款",
    "50_0": "评价"
};

//滚动加载
$('.main').scroll(function() {
    //当时滚动条离底部10px时开始加载下一页的内容
    if (($(this)[0].scrollTop + $(this)[0].offsetHeight + 10) >= $(this)[0].scrollHeight) {
        //这里用 [ off_on ] 来控制是否加载 （这样就解决了 当上页的条件满足时，一下子加载多次的问题啦）   
        if (off_on) {
            off_on = false;
            pIndex++;
            console.log("第"+pIndex+"页");
            getSelInfo(pIndex);  //调用执行上面的加载方法
        }
    }
});