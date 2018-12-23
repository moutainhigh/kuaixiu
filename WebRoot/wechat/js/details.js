var access_token = sessionStorage.getItem('access_token'),
base = new Base64(),
orderId = base.decode(eCacheUtil.storage.getCache(CacheKey.orderId));
$(function(){
    var descNews = '',//分享描述
        shareTitle='M-超人|手机上门维修专家，换碎屏、换电池、换听筒，快捷！实惠！',//分享标题
        link = linkUrl+'/wechat/share.html?id='+orderId,  //分享页面地址
        imgRealUrl=linkUrl+"/wechat/img/logo.jpg";          //分享定义图标地址
    var params={
            id:orderId
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
                var order = data.result.order,cusPro = [],specialPro = []
                    ,referPrice = 0.00;
                $('#orderNO').html(order.orderNo);
                $('#orderTime').html(order.inTime);
                var message = statusMes(order.orderStatus,order.isComment);
                $('#status').html(message);

                $('.addr_font').empty().append('<p>'+order.fullAddress+'</p>' +
                    '<p>'+order.customerName+'  '+order.mobile+'</p>');

                $('#model').html(order.modelName);

                for (var i = 0;i < order.details.length;i++){
                    if (order.details[i].type == 0){//0用户 1工程师
                        cusPro.push(order.details[i].projectName);
                        referPrice = parseFloat(referPrice) + parseFloat(order.details[i].price);
                    }else {
                        specialPro.push({
                            projectName:order.details[i].projectName,
                            price:order.details[i].price.toFixed(2)
                        })
                    }
                }
                $('#cusfault').html(cusPro.join('、'));
                $('#money').html('¥ '+referPrice.toFixed(2));
                $('#note').html(order.note);
                if (order.repairType == 3){
                    $('#repairType').html('寄修');
                    $('.post_details').empty().append('<p><span>邮寄地址：</span>'+order.shopFullAddress+'</p><p>'+order.shopManagerName+'收　手机号：'+order.shopManagerMobile+'</p>').show();

                }else {
                    $('#repairType').html('上门维修');
                }
                $('#shopName').html(order.shopName);
                $('#hotline').attr('href','tel:'+order.shopTel).html(order.shopTel);

                $('.faulted_list').empty();
                if (specialPro.length !=0){
                    $('.faulted').show();
                    for (var j=0;j<specialPro.length;j++){
                        $('.faulted_list').append('<li>' +
                            '<span>'+specialPro[j].projectName+'</span>' +
                            '<span>¥ '+specialPro[j].price+'</span>' +
                            '</li>');
                    }
                }
				
                if (order.isUseCoupon == 1){//1用优惠券
				   if(order.isUpdatePrice == 0){//管理员未改过价格
					  $('#totalPrice').empty().append('<ul class="account_details">' +
                        '<li><span>维修总价</span><span class="color-red">¥ '+order.realPrice.toFixed(2)+'</span>' +
                        '</li>' +
                        '<li><span>优惠券抵用</span><span class="color-red">-¥ '+order.coupon.couponPrice.toFixed(2)+'</span></li>' +
                        '</ul>' +
                        '<ul class="account_details">' +
                        '<li><span>订单需付金额</span><span class="color-red">¥ '+(order.realPrice-order.coupon.couponPrice).toFixed(2)+'</span></li>' +
                        '</ul>'); 
				   }else{
					   $('#totalPrice').empty().append('<ul class="account_details">' +
                        '<li><span>维修总价</span><span class="color-red">¥ '+order.realPrice.toFixed(2)+'</span>' +
                        '</li>' +
                        '<li><span>优惠券抵用</span><span class="color-red">-¥ '+order.coupon.couponPrice.toFixed(2)+'</span></li>' +
                        '</ul>' +
                        '<ul class="account_details">' +
                        '<li><span>订单需付金额</span><span class="color-red">¥ '+order.realPrice.toFixed(2)+'</span></li>' +
                        '</ul>');
				   }
                    
                }else {
                    $('#totalPrice').empty().append('<ul class="account_details">' +
                        '<li><span>维修总价</span><span class="color-red">¥ '+order.realPrice.toFixed(2)+'</span></li>' +
                        '</ul>');
                }

                switch (order.orderStatus){
                    case 30:
                        $('.btn-box').empty().append('<a onclick="cancelReason()" class="btn-normal" href="javascript:void(0);">取消订单</a><a class="btn-normal btn-normal-confirm" href="javascript:;" onclick="payBalance()" target="_blank">确认并支付</a>');
                        break;
                    case 40:
                        $('.btn-box').empty().append('<a id="confirmBtn" onclick="orderConfirm()" class="btn-large" href="javascript:void(0);">确认收货</a>');
                        break;
                    case 50:
                        $('.detailsBox').eq(0).append('<div class="eachRow"><span class="font">完成时间：</span><span class="cont">'+order.endTime+'</span></div>');
                        if (order.isComment == 0){
                            $('.btn-box').empty().append('<a id="commentBtn" class="btn-large" href="evaluation.html?r='+getRandomStr()+'">评 价</a>');
                        }
                        break;
                    case 60:
                        $('.detailsBox').eq(0).append('<div class="eachRow"><span class="font">取消时间：</span><span class="cont">'+order.endTime+'</span></div>');
                        $('.btn-box').empty();
                        break;
                    default:
                        $('.btn-box').empty().append('<a onclick="cancelReason()" class="btn-large" href="javascript:void(0);">取消订单</a>');
                }
                if (order.orderStatus == 50){
                    wxConfig();
                    var sharePro = [];
                    for (i in specialPro){
                        sharePro.push(specialPro[i].projectName);
                    }
                    console.log(sharePro);
                    if (order.isUseCoupon == 1){
                        //调用自定义的发送内容
                        descNews ='我的'+'('+order.modelName+')手机，M超人帮我维修了'+sharePro.join("、")+'，只花了'
                            +(order.realPrice-order.coupon.couponPrice)+'元,解决了我的烦恼，M超人就是专业';
                    }else {
                        descNews ='我的'+'('+order.modelName+')手机，M超人帮我维修了'+sharePro.join("、")+'，只花了'
                            +order.realPrice+'元,解决了我的烦恼，M超人就是专业';
                    }
                }
                loading_hide();
            }else {
                if (data.hasOwnProperty("exceptionType")){
                    eCacheUtil.storage.cache(CacheKey.exceptionType,data.exceptionType);
                    eCacheUtil.storage.cache(CacheKey.errorExceptionMsg,data.msg);
                    window.location.href ='error.html';
                }else {
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

    function wxConfig(){
        var url = linkUrl+"/wechat/repair/shareForData.do";
        $.ajax({
            url: url,
            type: "POST",
            data: {
                access_token:access_token
            },
            dataType: "json",
            success: function (data) {
                if (data.success) {
                    var wxJsapiSign=data.result.wxJsapiSign;
                    wx.config({
                        debug: false, // 开启调试模式,调用的所有api的返回值会在客户端alert出来，若要查看传入的参数，可以在pc端打开，参数信息会通过log打出，仅在pc端时才会打印。
                        appId: wxJsapiSign.appId, // 必填，公众号的唯一标识
                        timestamp: wxJsapiSign.timestamp, // 必填，生成签名的时间戳
                        nonceStr: wxJsapiSign.noncestr, // 必填，生成签名的随机串
                        signature: wxJsapiSign.signature,// 必填，签名，见附录1
                        jsApiList: ['checkJsApi', 'chooseWXPay','onMenuShareAppMessage',
                            'onMenuShareTimeline','onMenuShareQQ','onMenuShareQZone'] // 必填，需要使用的JS接口列表，所有JS接口列表见附录2
                    });
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
            error : function() {
                alertTip("系统异常，请稍后再试");
            }
        });
    }

    wx.ready(function(){
        //分享给好友
        wx.onMenuShareAppMessage({
            title:shareTitle,
            desc: descNews,
            link: link,
            imgUrl: imgRealUrl,
            success: function () {
            },
            cancel: function () {
            }
        });

        //分享到朋友圈
        wx.onMenuShareTimeline({
            title: shareTitle,
            link: link,
            imgUrl: imgRealUrl,
            success: function () {
            },
            cancel: function () {
            }
        });

        //分享到QQ
        wx.onMenuShareQQ({
            title:shareTitle,
            desc: descNews,
            link: linkUrl,
            imgUrl: imgRealUrl,
            success: function () {
            },
            cancel: function () {
            }
        });

        //分享到QQ空间
        wx.onMenuShareQZone({
            title:shareTitle,
            desc: descNews,
            link: link,
            imgUrl: imgRealUrl,
            success: function () {
            },
            cancel: function () {
            }
        });

    });

});

function orderConfirm() {
    fn_loading();
    var params = {
        id:orderId
    };
    $.ajax({
        type:'POST',
        url:linkUrl+'/wechat/order/orderConfirmToFinish.do',
        dataType:'json',
        data:{
            access_token:access_token,
            params:JSON.stringify(params)
        },
        success:function (data) {
            loading_hide();
            if (data.success){
                realAlert('系统提示','订单已确认',function () {
                    window.location.reload();
                });
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
}