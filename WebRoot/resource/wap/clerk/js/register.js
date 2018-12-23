var button=0;
var isMobile=/^(?:13\d|15\d|17\d|18\d)\d{5}(\d{3}|\*{3})$/;
var isIdentityCard=/(^\d{15}$)|(^\d{18}$)|(^\d{17}(\d|X|x)$)/;
$(function(){
	$("#news").click(function(){
		var name=$("#name").val();
		var tel=$("#inputtel").val();
		var code=$("#code").val();
		var password=$("#password").val();
		var repassword=$("#repassword").val();
		var identityCard=$("#identityCard").val();
		var wechatId=$("#wechatId").val();
		var isRealName=$('input:radio[name="wheth"]:checked').val();
		 if(name==""){
			 alertTip("请填写姓名");
			 return;
		 }
		 if(tel==""){
			 alertTip("请输入手机号");
		     return;
		 }
		 if(!isMobile.test(tel)){
			 alertTip("请输入正确的手机号");
			 return;
		 }
		 if(code==""){
			 alertTip("请输入验证码");
			 return;
		 }
		 else {
			 if(!button==1){
			 alertTip("请先获取验证码");
			 return; 
			 }	
		 }
		 if(wechatId==""){
			 alertTip("请输入微信号");
			 return;
		 }
		 if(isRealName==undefined){
			 alertTip("请选择是否实名制");
			 return; 
		 }
		 if(identityCard==""){
			 alertTip("请输入身份证号码");
			 return;
		 }
		 if(!isIdentityCard.test(identityCard)){
			 alertTip("请输入正确的身份证号");
			 return;
		 }
		 if(password==""){
			 alertTip("登录密码不能为空");
			 return;
		 }
		 if(password.length<6){
			 alertTip("密码长度为6-18个字符");
			 return;
		 }
		 if(repassword==""){
			 alertTip("请填写确认密码"); 
			 return; 
		 }
		 if(password!=repassword){
			 alertTip("两次密码不匹配");
			 return;
		 }
		 //地址填写判断
		 var province = $("#s_Province").val();
		    //验证地址选择
		    if(province == ''){
		        alertTip("请选择省份！");
		        return;
		    }
		    var addProvinceName = $("#s_Province").find("option:selected").text();
		    
		    var city = $("#s_City").val();
		    if(city == ''){
		    	alertTip("请选择地市！");
		        return;
		    }
		    var addCityName = $("#s_City").find("option:selected").text();
		    
		    var county = $("#s_County").val();
		    if(county == ''){
		    	alertTip("请选择区县！");
		        return;
		    }
		    var street = $("#street").val();
		    if(street == ''){
		    	alertTip("请填写街道/小区/楼号等！");
		        return;
		    }
		    var addCountyName = $("#s_County").find("option:selected").text();
		    
		    var areas=addProvinceName + " " + addCityName + " " + addCountyName;
		    var homeAddress = addProvinceName + " " + addCityName + " " + addCountyName +" "+street;
		    console.log(street);
		    console.log(areas);
         $.ajax({
      	   url:AppConfig.ctx+"/wap/clerk/addClerk.do",
      	  data:{name:name,tel:tel,code:code,password:password,identityCard:identityCard,homeAddress:homeAddress
      		  ,province:province,city:city,county:county,areas:areas,street:street,wechatId:wechatId,isRealName:isRealName
      	  },	
      	  type:"post", 
        dataType:"json",
         success:function(result){
      	   if(result.success){
      		   alertTip("提交成功");
      		   window.setTimeout(skip,1500);
      	   }else{
      		   alertTip(result.msg);
      	   }
         }
         });
			 
		 
	});
	   //多拉选初始化
	   $("#s_Province").append(packageAddress(privinceData,""));
});

function skip(){ 
	window.location.href=AppConfig.ctx+"/wap/clerk/login.do";
	} 


//1.点击发短信
function sendMsg(obj){
	var dianhua = $("#inputtel").val(); 
	if(!isMobile.test(dianhua)){
		alertTip("请输入正确的手机号");
		return false; 
	}
	button=1;
	Countdown(obj,60);
	//验证完成，调用短信发送
	var url = AppConfig.ctx + "/webpc/sendSmsCode.do";
	 $.post(url, {mobile: dianhua}, function(result){
	    	if(!result.success){
	    		alertTip(result.msg);
	    		return false;
	    	}else{
	    		alertTip("验证码发送成功，请注意查收！");
	    		//$("#checkCode").val(result.data);
	    	}
	    });
}

var countTimer = null;
function Countdown(val,caltime){
	if (caltime == 0) {
		$(val).removeClass('codebutgr');
		val.removeAttribute("disabled"); 
		val.value="获取验证码"; 
		$(val).siblings('b').show();
		clearTimeout(countTimer);
		return;
		//$("#inputtel").removeAttr("disabled").css('z-index','0'); 
	}
	$(val).siblings('b').hide();
	val.setAttribute("disabled", true); 
	val.value=caltime +"秒重新获取"; 
	caltime--; 
	$(val).addClass('codebutgr');
	//$("#inputtel").attr("disabled", "disabled").css('z-index','2000');		
	countTimer = setTimeout(function() {Countdown(val,caltime)},1000);
}





