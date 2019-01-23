$(function(){
    var access_token = sessionStorage.getItem('access_token'),
        base = new Base64(),
        orderId = base.decode(eCacheUtil.storage.getCache(CacheKey.orderId)),
		isRework = base.decode(eCacheUtil.storage.getCache(CacheKey.isRework)),
        params={
            id:orderId,
			isRework:isRework
        };
    $.ajax({
        type:'POST',
        url:linkUrl+'/wechat/order/orderDetail.do',
        dataType:'json',
        data:{
            access_token:access_token,
            params:JSON.stringify(params)
        },
        xhrFields: {
            withCredentials: true
        },
        crossDomain: true,
        success:function (data) {
            if (data.success){
                var order = data.result.order,
                    referPrice = 0;

                $('#orderNO').html(order.orderNo);
                $('#orderTime').html(order.inTime);
                var message = statusMes(order.orderStatus,order.isComment);
				
				//是否返修订单  0否  1是
				if(isRework == 1){
					$('#status').html(message+"(售后)");
				}else{
					$('#status').html(message);
				}
                

                $('.addr_font').empty().append('<p>'+order.fullAddress+'</p>' +
                    '<p>'+order.customerName+'  '+order.mobile+'</p>');

                if (order.selectType == 0){
                    if (order.isAgreed==0){
                        $('#model').html(order.agreedNews.agreedModel + order.agreedNews.color + order.agreedNews.memory + 'G' + order.agreedNews.edition);
                    }else {
                        $('#model').html(order.oldModel);
                    }
                }else {
                    $('#model').html("换话费");
                }
                $('#money').html(order.realPrice.toFixed(2));
                $('#shopName').html(order.shopName);
                $('#engineer').html(order.shopManagerName);
                $('#hotline').html(order.shopTel).attr('href','tel:'+order.shopTel);
                $('#totalPrice').html(order.realPrice.toFixed(2));

                switch (order.orderStatus){
                    case 12:
                        $('.btn-box').empty().append('<a onclick="cancelReason()" class="btn-normal" href="javascript:void(0);">取消订单</a><a class="btn-normal btn-normal-confirm" href="javascript:;" onclick="payBalance()" target="_blank">确认并支付</a>');
                        break;
                    case 50:
                        $('.detailsBox').eq(0).append('<li><span class="font">完成时间：</span><span class="cont">'+order.endTime+'</span></li>');
                        if (order.isComment == 0){
                            $('.btn-box').empty().append('<a id="commentBtn" class="btn-large" href="evaluation.html?r='+getRandomStr()+'">评 价</a>');
                        }
                        break;
                    case 60:
                        $('.detailsBox').eq(0).append('<li><span class="font">取消时间：</span><span class="cont">'+order.endTime+'</span></li>');
                        $('.btn-box').empty();
                        break;
                    default:
                    $('.btn-box').empty().append('<a onclick="cancelReason()" class="btn-large" href="javascript:void(0);">取消订单</a>');
                }
                loading_hide();
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
        },
        error:function (jqXHR) {
            loading_hide();
            alertTip('系统异常，请稍后再试！');
        }
    });
});