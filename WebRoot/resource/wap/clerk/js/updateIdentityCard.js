var isMobile=/^(?:13\d|15\d|17\d|18\d)\d{5}(\d{3}|\*{3})$/;
var isIdentityCard=/(^\d{15}$)|(^\d{18}$)|(^\d{17}(\d|X|x)$)/;
$(function(){
	$(".body_cont").height($(window).height());
	$("#news").click(function(){
        var identityCard=$("#identityCard").val();
        var tel=$("#inputTel").val();
        var code=$("#code").val();
		var password=$("#password").val();
		if(identityCard==""){
			 alertTip("请输入身份证号码");
			 return;
		 }
		 if(!isIdentityCard.test(identityCard)){
			 alertTip("请输入正确的身份证号");
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
		 if(password==""){
			 alertTip("请输入密码");
			 return;
		 }
		 
		 $.ajax({
	     	   url:AppConfig.ctx+"/wap/clerk/saveIdentityCard.do",
	     	  data:{identityCard:identityCard,tel:tel,code:code,password:password
	     	  },	
	     	  type:"post", 
	       dataType:"json",
	        success:function(result){
	     	   if(result.success){
	     		   alertTip("修改成功");
	     		  window.setTimeout(skip,1500);
	     	   }else{
	     		   alertTip(result.msg);
	     	   }
	        },
	         error:function(){
	        	 alertTip("系统异常请稍后再试");
	         }
	        });
		
	});
	
});

function skip(){ 
	window.location.href=AppConfig.ctx+"/wap/clerk/index.do";
	} 

//1.点击发短信
function sendMsg(obj){
	var dianhua =$("#inputTel").val(); 
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
	}
	$(val).siblings('b').hide();
	val.setAttribute("disabled", true); 
	val.value=caltime +"秒重新获取"; 
	caltime--; 
	$(val).addClass('codebutgr');
	countTimer = setTimeout(function() {Countdown(val,caltime)},1000);
}






