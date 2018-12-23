$(function(){
    //获取订单信息
    var access_token = sessionStorage.getItem('access_token'),
        base = new Base64();
    //获取订单id
    var orderId = base.decode(eCacheUtil.storage.getCache(CacheKey.orderId));
    //向后台发送微信授权code换取调用js-sdk所必须注入的参数
    var code = GetQueryString("code"); //获取微信重定向过来的code值
    var payUrl = linkUrl + "/wechat/order/payBalance.do";
    var param = {
        id : orderId,
        code : code
    };

    $.ajax({
        type : 'post',
        url : payUrl,
        dataType : 'json',
        data : {
            access_token : access_token,
            params : JSON.stringify(param)
        },
        xhrFields : {
            withCredentials : true
        },
        crossDomain : true,
        success : function(data) {
            if (data.success) {
                var wxJsapiSign = data.result.wxJsapiSign;
                wx.config({
                    debug : false, // 开启调试模式,调用的所有api的返回值会在客户端alert出来，若要查看传入的参数，可以在pc端打开，参数信息会通过log打出，仅在pc端时才会打印。
                    appId : wxJsapiSign.appId, // 必填，公众号的唯一标识
                    timestamp : wxJsapiSign.timestamp, // 必填，生成签名的时间戳
                    nonceStr : wxJsapiSign.noncestr, // 必填，生成签名的随机串
                    signature : wxJsapiSign.signature,// 必填，签名，见附录1
                    jsApiList : [ 'checkJsApi', 'chooseWXPay' ]
                // 必填，需要使用的JS接口列表，所有JS接口列表见附录2
                });
            } else {
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
        error : function(jqXHR) {
            alertTip('系统异常，请稍后再试！');
        }
    });

    var params = {
        id : orderId
    };
    $.ajax({
        type : 'POST',
        url : linkUrl + '/wechat/order/orderDetail.do',
        dataType : 'json',
        data : {
            access_token : access_token,
            params : JSON.stringify(params)
        },
        success : function(data) {
            if (data.success) {
                var order = data.result.order;
                if (order.orderType == 0) {//维修
                    $('.model_name').html(order.modelName + ' ' + order.color);
                    $('.selected_list').remove();   
                    for (var i = 0; i < order.details.length; i++) {
                        if (order.details[i].type == 1) {
                            $('#faultList').append('<div class="selected_list">'+
                            '<div class="font">'+order.details[i].projectName+'</div>'+
                            '<div class="right">'+
                                '<span>¥ '+order.details[i].price.toFixed(2)+'</span>'+
                            '</div>'+
                            '</div>');
                        }
                    }
                    if (order.isUseCoupon == 1){//1用优惠券
                        $('#totalPrice').empty().append('<ul class="account_details">' +
                            '<li><span>维修总价</span><span class="color-red">¥ '+order.realPrice.toFixed(2)+'</span>' +
                            '</li>' +
                            '<li><span>优惠券抵用</span><span class="color-red">-¥ '+order.coupon.couponPrice.toFixed(2)+'</span></li>' +
                            '</ul>' +
                            '<ul class="account_details">' +
                            '<li><span>订单需付金额</span><span class="color-red">¥ '+(order.realPrice-order.coupon.couponPrice).toFixed(2)+'</span></li>' +
                            '</ul>');
                    }else {
                        $('#totalPrice').empty().append('<ul class="account_details">' +
                            '<li><span>维修总价</span><span class="color-red">¥ '+order.realPrice.toFixed(2)+'</span></li>' +
                            '</ul>');
                    }
                    $('#faultList').show();
                } else {//以旧换新
                    $('.user_model .hint_title').html("以旧换新机型:");
                    $('.model_name').html(order.agreedNews.agreedModel
                        + ' ' + order.agreedNews.color + order.agreedNews.memory + 'G' + order.agreedNews.edition);
                    $('#totalPrice').empty().append('<ul class="account_details">' +
                        '<li><span>订单金额</span><span class="color-red">¥ '+order.realPrice.toFixed(2)+'</span></li>' +
                        '</ul>');
                }
            } else {
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
        error : function() {
            alertTip('系统异常，请稍后再试！');
        }
    });
    //支付
    $("#payBalanceBtn").click(function() {
        fn_loading();
        //微信下单
        var url_ = linkUrl+"/wechat/order/startPayBalance.do",
            params = {
                id : orderId,
                code : code
            };
        $.ajax({
            url : url_,
            type : "POST",
            data : {
                access_token : access_token,
                params : JSON.stringify(params)
            },
            dataType : "json",
            success : function(data) {
                loading_hide();
                if (data.success) {
                    if (data.result.pay_status == 3) {//订单已支付
                        realAlert("系统提示",data.resultMessage,function() {
                            window.location.reload();
                        });

                    } else {
                        // 生成微信支付订单成功，调用微信jsApi支付接口
                        wxJsapi_chooseWXPay(data.result.paySign);
                    }

                } else {
                    loading_hide();
                    if (data.resultCode == "1008") {
                        //用户授权code错误或已过期   则重新获取code
                        var url = linkUrl + '/wechat/payBlance.html';//微信重定向的地址
                        var wechatUrl = "https://open.weixin.qq.com/connect/oauth2/authorize?appid="+appId+"redirect_uri="
                            + url
                            + "&response_type=code&scope=snsapi_base&state=STATE&connect_redirect=1#wechat_redirect";
                        window.location.href = wechatUrl;
                    } else {
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
                }
            },
            error : function() {
                loading_hide();
                alertTip("系统异常，请稍后再试");
            }
        });

    });

    /**
     * 调用微信jsApi支付接口
     */
    function wxJsapi_chooseWXPay(paySign) {
        wx.chooseWXPay({
            timestamp : paySign.timeStamp, // 支付签名时间戳，注意微信jssdk中的所有使用timestamp字段均为小写。但最新版的支付后台生成签名使用的timeStamp字段名需大写其中的S字符
            nonceStr : paySign.nonceStr, // 支付签名随机串，不长于 32 位
            package : paySign.package, // 统一支付接口返回的prepay_id参数值，提交格式如：prepay_id=***）
            signType : paySign.signType, // 签名方式，默认为'SHA1'，使用新版支付需传入'MD5'
            paySign : paySign.paySign, // 支付签名
            success : function(res) {
                // 支付成功后的回调函数
                // alert(JSON.stringify(res));
                // if(res.errMsg == "chooseWXPay:ok")
                // 查询订单状态
                //每隔两秒刷新一次订单状态
                if ("undefined" != typeof (refreshIntervalProcess)) {
                    clearInterval(refreshIntervalProcess);
                }
                refreshIntervalProcess = setInterval("queryorderStatus()", 2 * 1000);
            },
            fail : function(res) {
                loading_hide();
                try {
                    cancleWechatOrder();
                } catch (e) {

                }
            }
        });
    }

    /**
     * 关闭微信订单
     */
    function cancleWechatOrder() {
        //调用微信支付接口
        //关闭时查询订单状态如果支付成功的跳转下单成功页面
        var url_ = linkUrl + "/wechat/order/cancelWechatOrder.do";
        $.ajax({
            url : url_,
            type : "POST",
            data : {
                id : orderId
            },
            dataType : "json",
            success : function(result) {
                if (result.success) {

                }
            },
            error : function() {
            }
        });
    }
    function queryorderStatus() {
        $.ajax({
            url : linkUrl + "/wechat/order/orderDetail.do",
            type : "POST",
            data : {
                access_token : access_token,
                params : JSON.stringify(params)
            },
            dataType : "json",
            success : function(data) {
                if (data.success && data.result.order.orderStatus == 50) {
                    realAlert("系统提示","支付成功",function() {
                        window.location.href = "order_details.html?r=" + getRandomStr();
                    });
                }
            },
            error : function() {
            }
        });

    }
})