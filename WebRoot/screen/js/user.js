var checkSubmitFlg = false,orderId,code;
$(function(){
    code = GetQueryString("code"); //获取微信重定向过来的code值
    if (!isEmpty(code)){
    	$('#name').val(eCacheUtil.storage.getCache(CacheKey.userName));
        $('#tel').val(eCacheUtil.storage.getCache(CacheKey.userPhone));
        var payUrl = linkUrl + "/wechat/order/payBalance.do"
            base = new Base64(),
            orderId = base.decode(eCacheUtil.storage.getCache(CacheKey.orderId));
        var param = {
            id : orderId,
            code : code
        };
        $.ajax({
            type : 'post',
            url : payUrl,
            dataType : 'json',
            data : {
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
                    $(".payInfo p").remove();
                    $(".payInfo").append('<p><span>商品名称：</span><span>'+eCacheUtil.storage.getCache(CacheKey.phoneBrandName)+'-'+eCacheUtil.storage.getCache(CacheKey.phoneProjectName)+'</span></p>'+'<p><span>购买价：</span><span>¥'+eCacheUtil.storage.getCache(CacheKey.phonePrice)+'元</span></p>');
                    $('#pay').show();
                    $('#payCont').addClass('active');
                } else {
                    alertTip(data.resultMessage);
                }
            },
            error : function(jqXHR) {
                alertTip('系统异常，请稍后再试！');
            }
        });
    }
})

//
$('.agree_clause .checkbox').change(function(){
    if(this.checked){
        $('.submit').removeClass('disabled');
    }else{
        $('.submit').addClass('disabled');
    }
});
//获取验证码
//1.点击发短信
var isMobile=/^(?:13\d|15\d|17\d|18\d|19\d)\d{5}(\d{3}|\*{3})$/;
function sendMsg(obj){
    var dianhua = $("#tel").val();
    if(!isMobile.test(dianhua)){
        alertTip('请输入正确的手机号码');
        $("#tel").focus();
        return false;
    }
    //验证完成，调用短信发送
    Countdown(obj,60);
    var params = {
        mobile:dianhua
    };
    $.ajax({
        type:'POST',
        url:linkUrl+'/wechat/sendSmsCode.do',
        dataType:'json',
        data:{
            params:JSON.stringify(params)
        },
        xhrFields: {
            withCredentials: true
        },
        crossDomain: true,
        success:function (data) {
            if (data.success){
                //验证完成，调用短信发送
                alertTip('验证码发送成功，请注意查收！');
                $(obj).prev('input').focus();
            }else {
                alertTip(data.resultMessage);
            }
        },
        error:function (jqXHR) {
            alertTip('系统异常，请稍后再试！')
        }
    });
}

function Countdown(val,caltime){
    if (caltime == 0) {
        $(val).removeClass('codebutgr');
        val.removeAttribute("disabled");
        val.value="发送验证码";
        $(val).siblings('b').show();
        clearTimeout(countTimer);
        return false;
    }
    $(val).siblings('b').hide();
    val.setAttribute("disabled", "disabled");
    val.value=caltime +"秒重新获取";
    caltime--;
    $(val).addClass('codebutgr');
    //$("#inputtel").attr("disabled", "disabled").css('z-index','2000');
    countTimer = setTimeout(function() {Countdown(val,caltime)},1000);
}

function submitOrder(obj) {
    if(checkSubmitFlg == true){
        return false; //当表单被提交过一次后checkSubmitFlg将变为true,根据判断将无法进行提交。
    }else{
        if (!$(obj).hasClass('disabled')){
            checkSubmitFlg = true;
            var dianhua = $('#tel').val(),
                base = new Base64();
            if(!isMobile.test(dianhua)){
                alertTip('请输入正确的手机号码');
                $("#tel").focus();
                return false;
            }
            let source = eCacheUtil.storage.getCache(CacheKey.source);
            if (isEmpty(source)){
                source = '';
            }

            if (eCacheUtil.storage.getCache(CacheKey.phoneProjectName) == "VIP免费碎屏保"){
                var param = {
                        name:$('#name').val(),
                        mobile:dianhua,
                        code:$('#code').val(),
                        fm:source,
                        brand:eCacheUtil.storage.getCache(CacheKey.brand),
                        model:''
                    };
                $.ajax({
                    type:'POST',
                    url:linkUrl+'/screen/active.do',
                    data:{
                        params:JSON.stringify(param)
                    },
                    dataType:'json',
                    xhrFields: {
                        withCredentials: true
                    },
                    crossDomain: true,
                    success:function(data){
                        if (data.success){
                            alertTip("购买成功！");
                            $('#tel').val('');
                            $('#name').val('');
                            $('#code').val('');
                            $('.agree_clause .checkbox').prop('checked',false);
                            var getCode=document.getElementById("getCode");
                       	    Countdown(getCode,0);
                        }else {
                            alertTip(data.resultMessage);
                            checkSubmitFlg = false;
                        }
                    },
                    error:function(){
                        alertTip('系统异常，请稍后再试！');
                        checkSubmitFlg = false;
                    }
                });
            }else{
                var projectId = base.decode(eCacheUtil.storage.getCache(CacheKey.phoneProjectId)),
                    param = {
                        name:$('#name').val(),
                        mobile:dianhua,
                        code:$('#code').val(),
                        projectId:projectId,
                        fm:source
                    };
                $.ajax({
                    type:'POST',
                    url:linkUrl+'/screen/order/save.do',
                    data:{
                        params:JSON.stringify(param)
                    },
                    dataType:'json',
                    xhrFields: {
                        withCredentials: true
                    },
                    crossDomain: true,
                    success:function(data){
                        if (data.success){
                            eCacheUtil.storage.cache(CacheKey.orderId,base.encode(data.result.id));
                            eCacheUtil.storage.cache(CacheKey.userName,$('#name').val());
                            eCacheUtil.storage.cache(CacheKey.userPhone,dianhua);
                            $('.agree_clause .checkbox').prop('checked',false);
                            if(isEmpty(data.result.pay)){
                                payBalances();
                            }else{
                                alertTip("购买成功！");
                                location.href = 'http://api.linshaolong.cn/Telinfo/login?openid=tel';
                            }
                        }else {
                            alertTip(data.resultMessage);
                            checkSubmitFlg = false;
                        }
                    },
                    error:function(){
                        alertTip('系统异常，请稍后再试！');
                        checkSubmitFlg = false;
                    }
                });
            }

            
        }
    }
    
}
//支付余款
function payBalances(){
	var url=linkUrl+'/screen/userInfo.html';
    url=encodeURIComponent(url);
    var wechatUrl="https://open.weixin.qq.com/connect/oauth2/authorize?appid="+appId+"&redirect_uri="+url+ "&response_type=code&scope=snsapi_base&state=STATE&connect_redirect=1#wechat_redirect";
    window.location.href=wechatUrl;
}
//支付
$("#payBalanceBtn").click(function() {
    fn_loading();
    //微信下单
    var url_ = linkUrl+ "/wechat/order/startPayBalance.do",
        params = {
            id : orderId,
            code : code
        };
    $.ajax({
        url : url_,
        type : "POST",
        data : {
            params : JSON.stringify(params)
        },
        dataType : "json",
        xhrFields : {
            withCredentials : true
        },
        crossDomain : true,
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
                if (data.resultCode == "1008") {
                    //用户授权code错误或已过期   则重新获取code
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
            window.location.href = "http://api.linshaolong.cn/Telinfo/login?openid=tel";
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

//关闭支付
function orderCancel(){
    fn_loading();
    confirmTip('系统提示','确认要放弃付款吗？',function(){
        $.ajax({
            type:'post',
            url:linkUrl + '/screen/order/cancel.do',
            dataType:'json',
            data:{
                id:orderId
            },
            xhrFields : {
                withCredentials : true
            },
            crossDomain : true,
            success:function(data){
                loading_hide();
                if (data.success){
                    $('#pay').hide();
                    $('#payCont').removeClass('active');
                }else{
                    alertTip(data.resultMessage);
                }
            },
            error:function(){
                loading_hide();
                alertTip('系统异常，请稍后再试！');
            }
        })
    })
}