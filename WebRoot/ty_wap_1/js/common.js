(function(window){

    var pageHeight = window.innerHeight;
    if(typeof pageHeight != "number"){
        if(document.compatMode == "CSS1Compat"){//判断页面是否标准模式
            pageHeight = document.documentElement.clientHeight;
        }else{
            pageHeight = document.body.clientHeight;
        }
    }
    document.getElementById('wrap').style.cssText = "height:"+pageHeight+"px";

	cacheUtil = function(){};
	cacheUtil.IsNull = function(data){
		if(data == undefined || data == null){
			return true;
		}
		var t = data.replace(/(^\s*)|(\s*$)/g, "");
		if (t == "" || t == null || t == "undefined") {
			return true;
		} else {
			return false;
		}
	};
	cacheUtil.storage = {
		_storage : sessionStorage,
		cache : function(key,value){
			var str;
			if(Object.prototype.toString.call(value) === '[object Array]' ||
				Object.prototype.toString.call(value) === '[object Object]'	){
				str = JSON.stringify(value);
			}else{
				str = value;
			}
			this._storage.setItem(key,str);
		},
		getCache : function(key){
			var value = this._storage.getItem(key);
			return cacheUtil.IsNull(value) ? undefined : value;
		},
		removeCache : function(key){
			this._storage.removeItem(key);
		}
	};
	if(!window.eCacheUtil){
		window.eCacheUtil = cacheUtil;
	}
})(window);

//存值方法,新的值添加在首位
function setHistoryItems(keyword,keyid,imghref) {
    var _localStorage = localStorage,
        historyItems = _localStorage.historyItems;

    if (historyItems === undefined) {
        localStorage.historyItems = [keyword,keyid,imghref];
    } else {
        historyItems = [keyword,keyid,imghref] + '|' + historyItems.split('|').filter(function (e) {
            return e != keyword+','+keyid+','+imghref;
        }).join('|');
        localStorage.historyItems = historyItems;
    }
}

//缓存key
var CacheKey = {
    quoteId:"quote_id",
    //订单号
    orderId : "order_id",
	//选择机型的name
    ModelName : "model_name",
	//微信号OpenId
	OpenId:"open_id",
    //选择机型的projectid
    ProjectId : "project_id",
	//上次评估的价格
	LastPrice : "last_price",
	//img地址
	imgHref :"img_href",
	//测评故障name
	itemsName:"items_name",
	//测评故障id
	SelectItems : "select_items",
	isSelect : "is_select",
	//预付金额
    prePrice : "pre_price",
	//取件时间
	pickTime : "pick_time",
	resultMsg : "result_msg",
    //登录手机
    loginPhone : 'login_phone',
    //来源
    source : "fm",
    happyGo : "happy_go",
    //快递类型
    mailType : "mail_type",
	//参加抽奖的手机号
	prizeMobile:"prize_mobile",

	//手机品牌id
	BrandId : "brand_id",
	//品牌name
	BrandName : "brand_name",
	//型号id
	ModelId : "model_id",
	//选择机型后的类型projectName
	ProjectName : "project_name",
	
	userName:"user_name",
	userPhone:"user_phone",
	
	//手机的估价
	ModelPrice:"model_price",
	//击败用户百分百
	ModelPercent:"model_percent",
	
	//欢Go获取到的手机号
	HappyGoMobile:"happygo_mobile",
	
	//填写收货地址后跳转的页面
	ToHappyGoPage:"to_happygo_page",
	
	    //可用优惠券
    CouponsAvailable:"coupons_available",
    //默认可用优惠券
    CouponsAvailableDefault:"coupons_available_default",
    //不可用优惠券
    CouponNotAvailable:"coupon_not_available",
	//订单页用户输入信息
	OrderUserInput: "order_user_input",
};
/**
 * 域名的路径
 * @returns {String}
 */
function getRealPath(){
	var pathName=location.pathname;  
	var allPath=location.href;
	var pos=allPath.indexOf(pathName);
	var linkUrl=allPath.substring(0,pos);
	var u='zj.189.cn'; //欢GO
	if(linkUrl.indexOf(u)>0){
		linkUrl='http://m-super.com';
	}
	return linkUrl;
}
//绝对路径
     var linkUrl=getRealPath();
     var appId='wx2f9fa0184228af25';    // 超人真实公众号

/**
 * 提示
 */
function alertTip(text) {
    $('.remind_delivery_bg .remind_delivery_cont p').text(text);
    $(".remind_delivery_bg").show();
    window.setTimeout(function(){ $(".remind_delivery_bg").fadeOut();},2000);
}

/**
 * 遮盖方法
 */
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
 * 带取消按钮提示框
 */
function confirmTip(title, msg, callback){
    if(!msg){
        return;
    }
    if(!title){
        title = "系统提示";
    }
    $(".popup_content .popup_title").text(title);
    $(".popup_content .popup_font").text(msg);
    $(".popup_but .cancel").show();
    $(".popup_bg").show();
    //点击确认
    $(".popup_but .confirm").unbind("click").click(function(){
        $(".popup_bg").hide();
        if (callback) {
            callback();
        }
    });
}

/**
 * 确认提示框
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
    $(".popup_bg").show();
    //点击确认
    $(".popup_but .confirm").unbind("click").click(function(){
        $(".popup_bg").hide();
        if (callback) {
            callback();
        }
    });
}

//关闭弹出框
$(".popup_but .cancel").click(function(){
    $(".popup_bg").hide();
});

//获取验证码
//1.点击发短信
var isMobile=/^(?:13\d|15\d|17\d|18\d|19\d)\d{5}(\d{3}|\*{3})$/;
function sendMsg(obj){
    var dianhua = $("#inputtel").val();
    if(!isMobile.test(dianhua)){
        alertTip('请输入正确的手机号码');
        $("#inputtel").focus();
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
                if (data.exceptionType){
                    eCacheUtil.storage.cache(CacheKey.exceptionType,data.exceptionType);
                    eCacheUtil.storage.cache(CacheKey.errorExceptionMsg,data.msg);
                    window.location.href = 'error.html';
                }else {
                    alertTip(data.resultMessage)
                }
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
        val.innerHTML="发送验证码";
        $(val).siblings('b').show();
        //$("#inputtel").removeAttr("disabled").css('z-index','0');
        return false;
    }
    $(val).siblings('b').hide();
    val.setAttribute("disabled", "disabled");
    val.innerHTML=caltime +"秒重新获取";
    caltime--;
    $(val).addClass('codebutgr');
    //$("#inputtel").attr("disabled", "disabled").css('z-index','2000');
    setTimeout(function() {Countdown(val,caltime)},1000);
}

/**
 * 选择登录判定
 */
function selectLogin() {
    console.log('登录判定');
    var url=linkUrl+"/wechat/login.html";//微信重定向的地址  要绝对路径
    url = encodeURIComponent(url);
    var wechatUrl="https://open.weixin.qq.com/connect/oauth2/authorize?appid="+appId+"&redirect_uri="+ url + "&response_type=code&scope=snsapi_base&state=STATE&connect_redirect=1#wechat_redirect";
    var is_login=getCookie("is_login"); //是否已登录过
    var code=GetQueryString("code");//判断是否获取了 code
    if(is_login!=undefined||!isWechat()){
    //如果已登录过   或者来自非微信浏览器的登录 则不需要code  
        if(code!=undefined){
            toLogin(code);//服务器重启的问题
        }else{
            login();
        }
    }else{
        if(code!=undefined){
            toLogin(code);
        }else{
            window.location.href=wechatUrl;//向微信服务器发起请求获取授权code
        }
    }
}

/**
 * 无需传递code的登录请求   
 * 满足条件：  1.非微信浏览器的登录     2.已经登录过的再次点击该页面
 */
function login(){
     var url=linkUrl+'/wechat/login.html';//微信重定向的地址
     url = encodeURIComponent(url);
     var wechatUrl="https://open.weixin.qq.com/connect/oauth2/authorize?appid="+appId+"&redirect_uri="+ url + "&response_type=code&scope=snsapi_base&state=STATE&connect_redirect=1#wechat_redirect";
	 $.ajax({
        type:'POST',
        url:linkUrl+'/wechat/order/wechatLogin.do',
        dataType:'json',
        xhrFields: {
            withCredentials: true
        },
        crossDomain: true,
        success:function (data) {
            if (data.success){
                if (data.result.is_login == 1){
                    sessionStorage.setItem('mobile',data.result.mobile);
                    sessionStorage.setItem('access_token',data.result.access_token);
                    location.href=linkUrl+'/wechat/login_sel.html';
                }else if(data.result.is_login==3){
                	location.href=wechatUrl;//向微信服务器发起请求获取授权code
                }else{
                	location.href=linkUrl+"/wechat/login.html";
                }
            }else {
                alertTip(data.resultMessage);
            }
        },
        error:function () {
            alertTip('系统异常，请稍后再试！');
        }

    });
}

/**
 * 需要提交code的请求登录
 */
function toLogin(code){
	 $.ajax({
         type:'POST',
         url:linkUrl+'/wechat/order/wechatLogin.do',
         dataType:'json',
         data:{
             code:code
         },
         xhrFields: {
             withCredentials: true
         },
         crossDomain: true,
         success:function (data) {
             if (data.success){
                 if (data.result.is_login == 1){
                     sessionStorage.setItem('mobile',data.result.mobile);
                     sessionStorage.setItem('access_token',data.result.access_token);
                     location.href=linkUrl+'/wechat/login_sel.html';
                 }else if(data.result.is_login==3){
                     location.href=wechatUrl;//向微信服务器发起请求获取授权code
                 }else{
                     location.href=linkUrl+"/wechat/login.html";
                 }
             }else {
                 alertTip(data.resultMessage);
             }
         },
         error:function () {

         }
     });
}

/**
 * 判断是否是微信浏览器的函数
 */
function isWechat(){
  var ua = window.navigator.userAgent.toLowerCase();
  if(ua.match(/MicroMessenger/i) == 'micromessenger'){
       return true;
  }else{
       return false;
  }
}

/**
 * 获取url后附带参数值
 */
function GetQueryString(name){
     var reg = new RegExp("(^|&)"+ name +"=([^&]*)(&|$)");
     var r = window.location.search.substr(1).match(reg);
     if(r!=null){
    	 return  unescape(r[2]); 
     }
     return null;
}

/**
 * 获取cookie中的值
 */
function getCookie(name){
var arr,reg=new RegExp("(^| )"+name+"=([^;]*)(;|$)");
    if(arr=document.cookie.match(reg)){
        return unescape(arr[2]);
    }else{
        return null;
    }
}

//支付余款
function payBalance(){
    var url=linkUrl+'/wechat/payblance.html?r='+getRandomStr();//微信重定向的地址
    url = encodeURIComponent(url);
    var wechatUrl="https://open.weixin.qq.com/connect/oauth2/authorize?appid="+appId+"&redirect_uri="+ url + "&response_type=code&scope=snsapi_base&state=STATE&connect_redirect=1#wechat_redirect";
    window.location.href=wechatUrl;
}

//取消订单原因列表
function cancelReason() {
    fn_loading();
    $.ajax({
        type:'POST',
        url:linkUrl+'/wechat/order/cancelReason.do',
        dataType:'json',
        xhrFields: {
            withCredentials: true
        },
        crossDomain: true,
        success:function (data) {
            if (data.success){
                $('#reason_list').empty();
                var reasons = data.result.data;
                for (var i = 0;i<reasons.length;i++){
                    $('#reason_list').append('<li class="">'+reasons[i].reason+'</li>');
                    $('.layer').slideDown();
                    // $(".layer_box").css({
                    //     "margin-top":($(window).height()-$(".layer_box").height())/2,
                    //     "margin-bottom":($(window).height()-$(".layer_box").height())/2
                    // });
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
}
//选择取消原因
$('#reason_list').delegate('li','click',function () {
    if ($(this).hasClass("select_tabin")){
        $(this).removeClass("select_tabin")
    }else {
        $(this).addClass("select_tabin");
    }
});
$(".close").click(function () {
    $('.layer').hide();
});
//确认取消
function orderCancel() {
    var selReason = [],
        reason_font = $('#reason').val().trim(),
        isFont = /^[\u4e00-\u9fa5\w]{1,5}$/;
    $(".select_tabin").each(function () {
        selReason.push($(this).html());
    });
    if (selReason.length == 0 || !isFont.test(reason_font)){
        alertTip('请选择或写下不少于5个字符您取消的原因！');
        return false;
    }
    
    var params = {
        id:orderId,
        reason:$('#reason').val(),
        selectReason:selReason.join("、")
    };
    $.ajax({
        type:'POST',
        url:linkUrl+'/wechat/order/orderCancel.do',
        dataType:'json',
        data:{
            params:JSON.stringify(params),
            access_token:access_token
        },
        xhrFields: {
            withCredentials: true
        },
        crossDomain: true,
        success:function (data) {
            if (data.success){
                alertTip('订单取消成功!');
                $('.layer').hide();
                window.location.href=window.location.href;
                window.location.reload;
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
            alertTip('系统异常，请稍后再试！')
        }
    })
}

//订单状态
function statusMes(status,isComment) {
    var message = '';
    switch (status){
        case 0:
            message = "生成订单";
            break;
        case 2:
            message = "待派单";
            break;
        case 3:
            message = "待门店收件";
            break;
        case 5:
            message = "门店已收件";
            break;
        case 11:
            message = "待预约";
            break;
        case 12:
            message = "已预约";
            break;
        case 20:
            message = "定位故障";
            break;
        case 30:
            message = "待付款";
            break;
        case 35:
            message = "正在维修";
            break;
        case 40:
            message = "待收货";
            break;
        case 50:
            if (isComment == 0){
                message = "待评价";
            }else {
                message = "已评价";
            }
            break;
        case 60:
            message = "已取消";
            break;
    }
    return message;
}
/**
 * 关闭窗口
 */
function closeWindow(){
    var userAgent = navigator.userAgent;
    if (userAgent.indexOf("Firefox") != -1 || userAgent.indexOf("Chrome") !=-1) {
        //window.location.replace("about:blank");
        var w = window.open('about:blank', '_self', '');
        w.close();
    }else{
        window.opener=null;
        window.open('', '_self', '');
        window.close();
    }
}

String.prototype.trim=function(){
    return this.replace(/(^\s*)|(\s*$)/g, "");
}
String.prototype.ltrim=function(){
    return this.replace(/(^\s*)/g,"");
}
String.prototype.rtrim=function(){
    return this.replace(/(\s*$)/g,"");
}

Number.prototype.fixed=function(n){  
	with(Math)return round(Number(this)*pow(10,n))/pow(10,n);
};

//判断是否有indexOf方法
if (!Array.indexOf) {  
    Array.prototype.indexOf = function (obj) {  
        for (var i = 0; i < this.length; i++) {  
            if (this[i] == obj) {  
                return i;  
            }  
        }  
        return -1;  
    }  
}

//对Date的扩展，将 Date 转化为指定格式的String   
//月(M)、日(d)、小时(h)、分(m)、秒(s)、季度(q) 可以用 1-2 个占位符，   
//年(y)可以用 1-4 个占位符，毫秒(S)只能用 1 个占位符(是 1-3 位的数字)   
//例子：   
//(new Date()).Format("yyyy-MM-dd hh:mm:ss.S") ==> 2006-07-02 08:09:04.423   
//(new Date()).Format("yyyy-M-d h:m:s.S")      ==> 2006-7-2 8:9:4.18   
Date.prototype.format = function(fmt){
	var o = {   
			"M+" : this.getMonth()+1,                 //月份   
			"d+" : this.getDate(),                    //日   
			"h+" : this.getHours(),                   //小时   
			"m+" : this.getMinutes(),                 //分   
			"s+" : this.getSeconds(),                 //秒   
			"q+" : Math.floor((this.getMonth()+3)/3), //季度   
			"S"  : this.getMilliseconds()             //毫秒   
			};   
	if(/(y+)/.test(fmt)){
		fmt=fmt.replace(RegExp.$1, (this.getFullYear()+"").substr(4 - RegExp.$1.length));   
	}
	for(var k in o){
		if(new RegExp("("+ k +")").test(fmt)){
			fmt = fmt.replace(RegExp.$1, (RegExp.$1.length==1) ? (o[k]) : (("00"+ o[k]).substr((""+ o[k]).length)));   
		}
	}
	return fmt;   
}  

function getDateDayFormat(day){
	var now = new Date();
	now.setDate(now.getDate() + day);
	return now.format("yyyy-MM-dd");
}

function getDateHourFormat(hour){
	var now = new Date();
	now.setHours(now.getHours() + hour);
	return now.format("yyyy-MM-dd hh:mm:ss");
}

function getTimeStr(remainTime){
    var hh = parseInt(remainTime / 60 / 60, 10);//计算剩余的分钟数  
    var mm = parseInt(remainTime / 60 % 60, 10);//计算剩余的分钟数  
    var ss = parseInt(remainTime % 60, 10);//计算剩余的秒数  
    if(hh == 0){
	    return checkTime(mm) + ":" + checkTime(ss);
    }else{
    	return checkTime(hh) + ":" + checkTime(mm) + ":" + checkTime(ss);
    }
}

function getHourTimeStr(remainTime){
    var hh = parseInt(remainTime / 60 / 60, 10);//计算剩余的分钟数  
    var mm = parseInt(remainTime / 60 % 60, 10);//计算剩余的分钟数  
    var ss = parseInt(remainTime % 60, 10);//计算剩余的秒数  
    return checkTime(hh) + ":" + checkTime(mm) + ":" + checkTime(ss);
}

//获取当月中最后一天
function getlastday(year,month)
{
    var new_year = year;    //取当前地年份
    var new_month = month++;//取下一个月地第一天，方便计算（最后一天不固定）
    if(month>12)            //如果当前大于12月，则年份转到下一年
    {
        new_month -=12;        //月份减
        new_year++;            //年份增
    }
    var new_date = new Date(new_year,new_month,1);                //取当年当月中地第一天
    return (new Date(new_date.getTime()-1000*60*60*24)).getDate();//获取当月最后一天日期
}

function checkTime(i){    
    if (i < 10) {    
        i = "0" + i;    
    }    
    return i;
}

/**
 * 判断是否是空
 * @param value
 */
function isEmpty(value){
	if(value == null || value == "" || value == "undefined" || value == undefined || value == "null"){
		return true;
	}
	else{
		value = value.replace(/\s/g,"");
		if(value == ""){
			return true;
		}
		return false;
	}
}

/**
 * 判断是否是数字
 */
function isNumber(value){
	if(!value || !value.trim()){
		return false;
	}
	if(isNaN(value)){
		return false;
	}
	else{
		return true;
	}
}

/*
* 数据格式化
* @param val 要格式化的数据
* @param n 格式化后的小数位数
*/
function dataFormat(val, n) {
	if (isNaN(val)) return "";
	if (n == undefined || n == null || isNaN(n)) n = 2;
 
	return new Number(val).toFixed(n);
}


/**
 * 只包含中文和英文
 * @param cs
 * @returns {Boolean}
 */
function isGbOrEn(value){
    var regu = "^[a-zA-Z\u4e00-\u9fa5]+$";
    var re = new RegExp(regu);
    if (value.search(re) != -1){
      return true;
    } else {
      return false;
    }
}

/**
 * 检查邮箱格式
 * @param email
 * @returns {Boolean}
 */
function check_email(email){  
   if(email){
   var myReg=/(^\s*)\w+([-+.']\w+)*@\w+([-.]\w+)*\.\w+([-.]\w+)*(\s*$)/;
   if(!myReg.test(email)){return false;}
   return true;
   }
   return false;
}


/**
 * 检查邮编
 * 
 * @param zip
 * @returns {Boolean}
 */
function check_zip(zip) {
	var regu = /^\d{6}$/;
	var re = new RegExp(regu);
	if (!re.test(zip)) {
		return false;
	}
	return true;
}

/**
 * 检查手机号码
 * 
 * @param mobile
 * @returns {Boolean}
 */
function check_mobile(mobile) {
	var regu = /^\d{11}$/;
	var re = new RegExp(regu);
	if (!re.test(mobile)) {
		return false;
	}
	return true;
}

/**
 * 检查身份证号码(中间位为*)
 * 
 * @param idCard
 * @returns {Boolean}
 */
function check_idCard_new(idCard) {
	var regu = null;
	var re = null;
	if(idCard.length == 15){
		regu =  /^\d{4}\*\*\*\*\*\*\*\d{4}$/;
	}else if(idCard.length == 18){
		regu =  /^\d{4}\*\*\*\*\*\*\*\*\*\*\d{3}(\d|X|x)$/;
	}else{
		return false;
	}
	re = new RegExp(regu);
	if (!(re.test(idCard) || check_idCard(idCard))) {
		return false;
	}
	return true;
}

/**
 * 获取随机数
 */
function getRandomStr(len){
	var chars = ['0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z'];
	var v_len = len;
	if(len == undefined || len == 0){
		v_len = 20;
	}
	var res = "";
	for(var i = 0; i < v_len ; i ++) {
		var id = Math.ceil(Math.random()*35);
		res += chars[id];
	}
	return res;
}
