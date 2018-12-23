var button=0;  //当该值为1时表示用户已点击发送短信按钮
$(function(){
    var checkSubmitFlg = false;
    $("#news").click(function(){
        if(checkSubmitFlg == true){
            return false; //当表单被提交过一次后checkSubmitFlg将变为true,根据判断将无法进行提交。
        }
        checkSubmitFlg ==true;
        var name = $("#cusName").val(),
            tel = $("#inputtel").val(),
            code = $("#code").val(),
            oldMobile = $("#oldMobile").val(),
            province = $('#s_Province').val(),
            city = $('#s_City').val(),
            county = $('#s_County').val(),
            street = $('#address').val();
            
        var choose=$('input:radio[name="way"]:checked').val();
        if(name==""){
            alertTip("请填写姓名");
            checkSubmitFlg = false;
            return;
        }else if(tel==""){
            alertTip("请输入手机号");
            checkSubmitFlg = false;
            return;
        }else if(!isMobile.test(tel)){
            alertTip("请输入正确的手机号");
            checkSubmitFlg = false;
            return;
        }else if(code==""){
            alertTip("请输入验证码");
            checkSubmitFlg = false;
            return;
        }else if(oldMobile==""){
            alertTip("旧机型不能为空");
            checkSubmitFlg = false;
            return;
        }else if(province == ''){
            alertTip("请选择省份！");
            checkSubmitFlg = false;
            return;
        }else if(city == ''){
            alertTip("请选择地市！");
            checkSubmitFlg = false;
            return;
        }else if(county == ''){
            alertTip("请选择区县！");
            checkSubmitFlg = false;
            return;
        }else if (street == ''){
            alertTip('请输入街道');
            checkSubmitFlg = false;
            return;
        }
        var fm = eCacheUtil.storage.getCache(CacheKey.fm);
        if (isEmpty(fm)) {
            fm = ''
        }
        var params = {
            name:name,
            tel:tel,
            code:code,
            oldMobile:oldMobile,
            province:province,
            city:city,
            county:county,
            street:street,
            selectType:choose,
            fm:fm
        };

        fn_loading();
        $.ajax({
            url:linkUrl+"/webpc/activity/addNews.do",
            data:{
                params:JSON.stringify(params)
            },
            type:"post",
            dataType:"json",
            xhrFields: {
                withCredentials: true
            },
            crossDomain: true,
            success:function(data){
                loading_hide();
                if(data.success){
                    alertTip("提交成功");
                    window.location.href="http://www.m-super.cn/";
                }else{
                    if (data.exceptionType){
                        eCacheUtil.storage.cache(CacheKey.exceptionType,data.exceptionType);
                        eCacheUtil.storage.cache(CacheKey.errorExceptionMsg,data.msg);
                        window.location.href = 'error.html';
                    }else {
                        alertTip(data.resultMessage);
                        checkSubmitFlg = false;
                    }
                }
            },
            error:function(jqXHR){
                loading_hide();
                alertTip("注册失败");
            }
        });
    });
});

