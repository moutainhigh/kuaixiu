var userInfo = new Vue({
    el:'.main',
    data:{
        repairType:0,
        modelName:'',//机型
        selProjectIds:[],
        selProjectNames:[],//故障集合
        proName:'',//故障
        proPrice:'0.00',//维修价
        mobile:'',//手机号
        checkCode:'',//验证码
        ProId:'',
        cityId:'',
        areaId:'',
        countyId:'',
        Address:'',
        node:'',
        couponCode:'',//优惠码
        isCoupon:false,
        couponName:'',
        couponPrice:'',
        couponTime:'',
        couponProject:'',
        couponBrand:'',
        couponRemark:'',
        isPost:false,
        fullAddress:'',
        managerInfo:'',
        customerName:'',
        checkSubmitFlg:false
    },
    create:function () {
        this.proPrice = 0;
    },
    mounted:function () {
        //获取维修方式展示不同页面
        this.repairType = eCacheUtil.storage.getCache(CacheKey.repairWay);
        //所选机型和颜色
        this.modelName = eCacheUtil.storage.getCache(CacheKey.phoneModelName)
            + " " + eCacheUtil.storage.getCache(CacheKey.phoneSelColor);

        //获取选择的故障类型
        var idsObj = eCacheUtil.storage.getCache(CacheKey.phoneSelProjectId);
        var namesObj = eCacheUtil.storage.getCache(CacheKey.phoneSelProjectName);
        if (!isEmpty(idsObj) && !isEmpty(namesObj)) {
            this.selProjectIds = JSON.parse(idsObj);
            this.selProjectNames = JSON.parse(namesObj);
        }else{
            dataError("维修故障未找到，请重新选择");
            return false;
        }
        this.proName = this.selProjectNames.join('、');
        this.proPrice = eCacheUtil.storage.getCache(CacheKey.orderPrice);
        if (this.couponCode == ''){
            this.isCoupon = false;
        }
        $(".main").show();
    },
    methods:{
        //验证码
        sendCode:function (event) {
            //1.点击发短信
            var isMobile=/^(?:13\d|15\d|17\d|18\d)\d{5}(\d{3}|\*{3})$/;
            if (this.mobile == ''){
                $('.remind_delivery_bg .remind_delivery_cont p').text($("#inputte2").attr('placeholder'));
                $(".remind_delivery_bg").show();
                window.setTimeout(function(){ $(".remind_delivery_bg").fadeOut(); $("#inputte2").focus();},2000);
                return false;
            }else if(!isMobile.test(this.mobile)){
                $('.remind_delivery_bg .remind_delivery_cont p').text('请输入正确的手机号码');
                $(".remind_delivery_bg").show();
                window.setTimeout(function(){ $(".remind_delivery_bg").fadeOut(); $("#inputte2").focus();},2000);
                return false;
            }
            var obj = event.currentTarget;
            this.Countdown(obj,60);
            var params = {
                mobile:this.mobile
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
                        window.setTimeout(function(){ $(".remind_delivery_bg").fadeOut(); $(obj).prev('input').focus();},2000);
                    }else {
                        dataError(data.resultMessage);
                    }
                },
                error:function (jqXHR) {

                }
            });
        },
        //优惠码
        sendCouponCode:function () {
            var _self = this;
            var params = {
                mobile:_self.mobile,
                checkCode:_self.checkCode,
                couponCode:_self.couponCode
            };
            $.ajax({
                type:'POST',
                url:linkUrl+'/wechat/repair/couponInfo.do',
                dataTyp:'json',
                data:{
                    params:JSON.stringify(params)
                },
                xhrFields: {
                    withCredentials: true
                },
                crossDomain: true,
                success:function (data) {
                    if (data.success){
                        _self.isCoupon = true;
                        var result = data.result;
                        _self.couponName = result.data.couponName;
                        _self.couponPrice = result.data.couponPrice;
                        _self.couponTime = result.data.beginTime + ' - ' + result.data.endTime;
                        _self.couponRemark = result.data.note;

                        var temporary = [];
                        for (var i = 0;i < result.models.length;i++){
                            temporary.push(result.models[i].brandName);
                        }
                        _self.couponBrand = temporary.join('、');

                        temporary = [];
                        for (var j = 0;j < result.projects.length;j++){
                            temporary.push(result.projects[j].projectName);
                        }
                        _self.couponProject = temporary.join("、");

                    }else {
                        dataError(data.resultMessage);
                    }
                },
                error:function (jqXHR) {

                }
            })
        },
        changeCode:function (e) {
            if ($(e.target).val().length<5){
                this.isCoupon = false
            }
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
            setTimeout(function() {userInfo.Countdown(val,caltime)},1000);
        },
        Submit:function () {
            var _self = this;
            if (_self.checkSubmitFlg == true){
                return false;
            }
            _self.checkSubmitFlg = true;

            if (_self.customerName == ''){
                dataError('请填写您的姓名!');
                _self.checkSubmitFlg = false;
            }else if (_self.mobile == ''){
                dataError('请输入下单手机号!');
                _self.checkSubmitFlg = false;
            }else if (_self.checkCode == ''){
                dataError('请填写验证码!');
                _self.checkSubmitFlg = false;
            }else if (_self.ProId == ''){
                dataError('请选择省份!');
                _self.checkSubmitFlg = false;
            }else if (_self.cityId == ''){
                dataError('请选择城市!');
                _self.checkSubmitFlg = false;
            }else if (_self.areaId == ''){
                dataError('请选择区县!');
                _self.checkSubmitFlg = false;
            }else if (_self.Address == ''){
                dataError('请填写你的住址');
                _self.checkSubmitFlg = false;
            }else {
                var pro=[];
                for (var i = 0;i < _self.selProjectIds.length;i++){
                    pro[i] = {projectId:_self.selProjectIds[i]};
                }
                var params = {
                    brandId:eCacheUtil.storage.getCache(CacheKey.phoneBrandId),
                    modelId:eCacheUtil.storage.getCache(CacheKey.phoneModelId),
                    color:eCacheUtil.storage.getCache(CacheKey.phoneSelColor),
                    repairType:_self.repairType,
                    customerName:_self.customerName,
                    mobile:_self.mobile,
                    checkCode:_self.checkCode,
                    province:_self.ProId,
                    city:_self.cityId,
                    area:_self.areaId,
                    county:_self.countyId,
                    street:_self.Address,
                    note:_self.note,
                    couponCode:_self.couponCode,
                    projects:pro,
                    fm:''
                };
                $.ajax({
                    type:'POST',
                    url:linkUrl+'/wechat/repair/saveOrder.do',
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
                            eCacheUtil.storage.cache(CacheKey.customerMobile,_self.mobile);
                            sessionStorage.setItem("access_token",data.result.access_token);
                            window.location.href = 'order_details.html?id='+data.result.id;
                        }else {
                            if(_self.repairType == 0 && data.resultCode == 3004){
                                confirmTip('系统提示','亲，附近没有维修门店，是否选择寄修？',function () {
                                    fn_loading();
                                    _self.repairType = 3;
                                    findSendAddress($('#s_Province').val(),$('#s_City').val());
                                    _self.checkSubmitFlg = false;
                                    loading_hide();
                                })
                            }else {
                                dataError(data.resultMessage);
                                _self.checkSubmitFlg = false;
                            }
                        }
                    },
                    error:function () {

                    }
                })
            }

        }
    }
});