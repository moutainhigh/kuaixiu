var height = $(window).height();
window.res = null;
$(window).resize(function () {
    if ($(this).height() < height){
        $(".bottom").css({'position':'static'});
    }else {
        $(".bottom").css({'position':'absolute'});
    }
});
function Login() {
    clearTimeout(Countdown);
    var params={
        mobile:$('#inputtel').val(),
        checkCode:$('#code').val()
    };
    fn_loading();
    $.ajax({
        type:'POST',
        url:linkUrl+'/wechat/order/checkLogin.do',
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
                sessionStorage.setItem('access_token',data.result.access_token);
                loading_hide();
                location.href = 'orderlist.html?h='+$('#wrap').height();
            }else {
                loading_hide();
                if (data.exceptionType){
                    eCacheUtil.storage.cache(CacheKey.exceptionType,data.exceptionType);
                    eCacheUtil.storage.cache(CacheKey.errorExceptionMsg,data.msg);
                    window.location.href = 'error.html';
                }else {
                    alertTip(data.resultMessage);
                }
            }
        },
        error:function (jqXHR) {
            loading_hide();
            alertTip('系统异常，请稍后再试！');
        }
    })
}
var b = window.setInterval(chgbt,500);
function chgbt(){
    if($('#inputtel').val().length < 11 || $('#code').val().length < 6){
        $('.login_submit #submit').addClass("disabled");
        return false;
    }else{
        $('.login_submit #submit').removeClass('disabled');
        return true;
    }
}
function changeUser() {
    $('.logAgainbg').hide();
}
$('#already').on('click',function () {
    window.location.href = 'orderlist.html';
    $('.logAgainbg').hide();
});