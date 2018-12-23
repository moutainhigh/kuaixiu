//
$('.agree_clause .checkbox').change(function(){
    if(this.checked){
        $('.agree_clause').addClass('active');
        $('.submit').removeClass('disabled');
    }else{
        $('.submit').addClass('disabled');
        $('.agree_clause').removeClass('active');
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
    val.value=caltime +"秒";
    caltime--;
    $(val).addClass('codebutgr');
    //$("#inputtel").attr("disabled", "disabled").css('z-index','2000');
    countTimer = setTimeout(function() {Countdown(val,caltime)},1000);
}
var brand = 0,model = 0;
// 搜索“品牌”
function selectBrand(obj){
    if (brand == 0){
        $.post(linkUrl+'/screen/selectBrand.do',function(data){
            if (data.success){
                var info = data.result.brand;
                eachFull(obj,info);
                brand = 1;
            }else{
                alertTip(data.resultMessage);
            }
        });
    }else{
        $('.selectLay').addClass('active');
        $(obj).next('.selectList').addClass('active');
        return model = 0;
    }
}
// 搜索机型
function selectModel(obj){
    if (model == 0){
        var param = {
            brand : $('#brand').val()
        }
        if (isEmpty(param.brand)){
            alertTip('请先选择手机品牌');
            return false;
        }else{
            $.post(linkUrl+'/screen/selectModel.do',{params:JSON.stringify(param)},function(data){
                if (data.success){
                    var info = data.result.model;
                    eachFull(obj,info);
                    model = 1;
                }else{
                    alertTip(data.resultMessage);
                }
            });
        }
    }else{
        $('.selectLay').addClass('active');
        $(obj).next('.selectList').addClass('active');
    }
    
}
// 展示
function eachFull(obj,info){
    var s = '';
    $.each(info, function (i, n) {
        s += '<p>'+n+'</p>';
    });
    $('.selectLay').addClass('active');
    $(obj).next('.selectList').html(s).addClass('active');
    $('.selectList.active').delegate('p','click',function(){
        $(this).addClass('active').siblings('p').removeClass('active');
        $(this).parent('.selectList').siblings('input').val($(this).html());
        $('.selectLay,.selectList').removeClass('active');
    });
}
// 信息核对-弹框
function confirmFun(obj){
    if (!$(obj).hasClass('disabled')){
        var dianhua = $('#tel').val(),
            param = {
                name:$('#name').val(),
                code:$('#code').val(),
                brand:$('#brand').val(),
                model:$('#model').val()
            };
        if (isEmpty(param.name) || isEmpty(param.code) || isEmpty(param.brand) || isEmpty(param.model)){
            alertTip('请将信息填写完整');
            return false;
        }else if(!isMobile.test(dianhua)){
            alertTip('请输入正确的手机号码');
            $("#tel").focus();
            return false;
        }
        $('#confirmName').html(param.name);
        $('#confirmTel').html(dianhua);
        $('#confirmBrand').html(param.brand);
        $('#confirmModel').html(param.model);
        $('#confirmTime').html(eCacheUtil.storage.getCache(CacheKey.time)+'天');
        $('.confirmLay').addClass('active');
    }
}
// 关闭-弹框
function confirmClose(){
    $('.confirmLay').removeClass('active');
}
// 确定-提交订单
var checkSubmitFlg = false;
function submitOrder(obj) {
    if(checkSubmitFlg == true){
        return false; //当表单被提交过一次后checkSubmitFlg将变为true,根据判断将无法进行提交。
    }else{
        let source = eCacheUtil.storage.getCache(CacheKey.source);
        if (isEmpty(source)){
            source = '';
        }
        var dianhua = $('#tel').val(),
            param = {
                name:$('#name').val(),
                mobile:dianhua,
                code:$('#code').val(),
                fm:source,
                brand:$('#brand').val(),
                model:$('#model').val()
            };
            if (isEmpty(param.name) || isEmpty(param.code) || isEmpty(param.brand) || isEmpty(param.model)){
                alertTip('请将信息填写完整');
                return false;
            }else if(!isMobile.test(dianhua)){
                alertTip('请输入正确的手机号码');
                $("#tel").focus();
                return false;
            }
            checkSubmitFlg = true;
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
                        $('.confirmLay').removeClass('active');
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

