$(function(){
	 $("#news").click(function(){
		 var oldPassword=$("#oldPassword").val();
		 var newPassword=$("#newPassword").val();
		 var rePassword=$("#rePassword").val();
		 if(oldPassword==""){
			 alertTip("请输入旧密码");
			 return;
		 }
		 if(newPassword==""){
			 alertTip("请输入新密码");
			 return;
		 }
         if(rePassword==""){
        	 alertTip("请输入确认密码");
        	 return;
         }	
         if(oldPassword.length<6){
			 alertTip("密码长度为6-18个字符");
			 return;
		 }
         if(newPassword.length<6){
			 alertTip("密码长度为6-18个字符");
			 return;
		 }
         if(rePassword.length<6){
        	 alertTip("密码长度为6-18个字符");
			 return;
         }
         if(newPassword!=rePassword){
        	 alertTip("两次密码不相同");
        	 return;
         }
         
		 $.ajax({
	     	   url:AppConfig.ctx+"/wap/clerk/savePassword.do",
	     	  data:{oldPassword:oldPassword,newPassword:newPassword,rePassword:rePassword
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
	window.location.href=AppConfig.ctx+"/wap/clerk/login.do";
	} 