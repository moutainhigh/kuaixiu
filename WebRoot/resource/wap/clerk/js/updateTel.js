var isMobile=/^(?:13\d|15\d|17\d|18\d)\d{5}(\d{3}|\*{3})$/; 
$(function(){
	$("#news").click(function(){
	var oldTel=tel;
	var newTel=$("#newTel").val();
	var code=$("#code").val();
	 if(newTel==""){
		 alert("请输入新手机号");
	     return;
	 }
	 if(!isMobile.test(newTel)){
		 alert("请输入正确的手机号");
		 return;
	 }
	 if(code==""){
		 alert("请输入验证码");
		 return;
	 }
	
	  $.ajax({
     	   url:AppConfig.ctx+"/wap/clerk/saveUpdateTel.do",
     	  data:{oldTel:oldTel,newTel:newTel,code:code
     	  },	
     	  type:"post", 
       dataType:"json",
        success:function(result){
     	   if(result.success){
     		   alert("修改成功");
     		   window.location.href=AppConfig.ctx+"/wap/clerk/login.do";
     	   }else{
     		   alert(result.msg);
     	   }
        },
         error:function(){
        	 alert("系统异常请稍后再试");
         }
        });
	  
	  
	});
});



//1.点击发短信
function sendMsg(obj){
	var dianhua =tel; 
	if(!isMobile.test(dianhua)){
		alert("请输入正确的手机号");
		return false; 
	}
	button=1;
	Countdown(obj,60);
	//验证完成，调用短信发送
	var url = AppConfig.ctx + "/webpc/sendSmsCode.do";
	 $.post(url, {mobile: dianhua}, function(result){
		    alert('验证码啦：'+result.result);
	    	if(!result.success){
	    		alert(result.msg);
	    		return false;
	    	}else{
	    		alert("验证码发送成功，请注意查收！");
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