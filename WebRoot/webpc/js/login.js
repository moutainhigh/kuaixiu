$(function () {
    $('.placeholder').bind({
        focus:function(){
            if($(this).val() == $(this).attr('placeholder').valueOf()){
                $(this).val('');
            }
        },
        blur:function(){
            if($(this).val()==''){
                $(this).val($(this).attr('placeholder').valueOf());
            }
        }
    }).keydown(function(e){
        if(e.keyCode == 32){
            return false;
        }
    });
//点查询支付弹出框
    $('.index_topmenubg .index_topright .inquiry').click(function(){
        $('.loginPopup').fadeIn(500);
        $('.loginPopup .login_popup').animate({top:"17%"},800);

    });


    var login = new Vue({
        el:'.loginPopup',
        data:{
            tel:'',
            checkCode:''
        },
        methods:{
            sendMsg:function (event) {
                //1.点击发短信
                var isMobile=/^(?:13\d|15\d|17\d|18\d)\d{5}(\d{3}|\*{3})$/;
                var dianhua = $("#inputtel").val();
                if(!isMobile.test(dianhua)){
                    $('.remind_delivery_bg .remind_delivery_cont p').text('请输入正确的手机号码');
                    $(".remind_delivery_bg").show();
                    window.setTimeout(function(){ $(".remind_delivery_bg").fadeOut(); $("#inputtel").focus();},2000);
                    return false;
                }
                var obj = event.currentTarget;
                this.Countdown(obj,60);

                var params = {
                    mobile:this.tel
                };
                $.ajax({
                    type:'POST',
                    url:linkUrl+'/wechat/sendSmsCode.do',
                    dataType:'json',
                    data:{
                        params:JSON.stringify(params)
                    },
                    success:function (data) {
                        if (data.success){
                            //验证完成，调用短信发送
                            $('.remind_delivery_bg .remind_delivery_cont p').text('验证码发送成功，请注意查收！');
                            $(".remind_delivery_bg").show();
                            window.setTimeout(function(){ $(".remind_delivery_bg").fadeOut(); $(".psw").focus();},2000);
                        }else {
                            dataError(data.resultMessage);
                        }
                    },
                    error:function (jqXHR) {

                    }
                });
            },
            Countdown:function (val,caltime) {
                if (caltime == 0) {
                    $(val).removeClass('codebutgr');
                    val.removeAttribute("disabled");
                    val.value="发送验证码";
                    $(val).siblings('b').show();
                    //$("#inputtel").removeAttr("disabled").css('z-index','0');
                    return false;
                }
                $(val).siblings('b').hide();
                val.setAttribute("disabled", "disabled");
                val.value=caltime +"秒重新获取";
                caltime--;
                $(val).addClass('codebutgr');
                //$("#inputtel").attr("disabled", "disabled").css('z-index','2000');
                setTimeout(function() {login.Countdown(val,caltime)},1000);
            },
            colseLogin:function () {//关闭登录弹出框
                $('.login_popupbg').hide();
                $('.loginPopup .login_popup').animate({top:"-100%"},0);
            },
            Login:function () {
                var myform = $(".login_popup .placeholder");
                for(var i=0;i<myform.length;i++){
                    var textval = myform.eq(i).val();
                    if(textval == '' || textval == myform.eq(i).attr('placeholder')){
                        $('.remind_delivery_bg .remind_delivery_cont p').text(myform.eq(i).attr('placeholder'));
                        $(".remind_delivery_bg").show();
                        window.setTimeout(function(){ $(".remind_delivery_bg").fadeOut(); myform.eq(i).focus();},2000);
                        return false;
                    }
                }
                var params={
                    mobile:this.tel,
                    checkCode:this.checkCode
                }
                $.ajax({
                    type:'POST',
                    url:linkUrl+'/wechat/order/checkLogin.do',
                    dataType:'json',
                    data:{
                        params:JSON.stringify(params)
                    },
                    success:function (data) {
                        if (data.success){
                            sessionStorage.setItem('access_token',data.result.access_token);
                            $('.login_popupbg').hide();
                            $('.loginPopup .login_popup').animate({top:"-100%"},0);
                            location.href = 'orderList.html';
                        }else {
                            dataError(data.resultMessage);
                        }
                    },
                    error:function (jqXHR) {

                    }
                })
            }

        }
    });
});
function dataError(text) {
    $('.remind_delivery_bg .remind_delivery_cont p').text(text);
    $(".remind_delivery_bg").show();
    window.setTimeout(function(){ $(".remind_delivery_bg").fadeOut();},2000);
}
function GetQueryString(name){
    var reg = new RegExp("(^|&)"+ name +"=([^&]*)(&|$)");
    var r = window.location.search.substr(1).match(reg);
    if(r!=null){
        return  unescape(r[2]);
    }
    return null;
}
function fn_loading(){
    $("#mask_boxs").show();
}

/**
 * 隐藏遮盖层
 */
function loading_hide(){
    $("#mask_boxs").hide();
}
/**
 * 带确认按钮提示框
 */
function realAlert(title, msg, callback){
    if(!msg){
        return;
    }
    if(!title){
        title = "系统提示";
    }
    $(".popup_content .popup_title").text(title);
    $(".popup_content .popup_font").text(msg);
    //隐藏取消按钮
    $(".popup_but .cancel").hide();
    $(".popup_bg, .popup_content").show();
    //点击确认
    $(".popup_but .confirm").unbind("click").click(function(){
        $(".popup_bg, .popup_content").hide();
        if (callback) {
            callback();
        }
    });
}

//关闭弹出框
$(".popup_but .cancel,.popup_content .popup_exit").click(function(){
    $(".popup_bg, .popup_content").hide();
});

$.ajaxSetup( {
    xhrFields: {
        withCredentials: true
    },
    crossDomain: true
} );