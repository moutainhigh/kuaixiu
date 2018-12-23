var isIdentityCard=/(^\d{15}$)|(^\d{18}$)|(^\d{17}(\d|X|x)$)/;
$(function(){
	$("#news").click(function(){
		var isMobile=/^(?:13\d|15\d|17\d|18\d)\d{5}(\d{3}|\*{3})$/;
		var name=$("#name").val();
        var identityCard=$("#identityCard").val();
        var tel=$("#tel").val();
		 if(name==""){
			 alertTip("请输入姓名");
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
		 if(tel==""){
			 alertTip("请输入手机号");
		     return;
		 }
		 if(!isMobile.test(tel)){
			 alertTip("请输入正确的手机号");
			 return;
		 }
		 
		 $.ajax({
	     	   url:AppConfig.ctx+"/wap/clerk/saveForgot.do",
	     	  data:{identityCard:identityCard,tel:tel,name:name
	     	  },	
	     	  type:"post", 
	       dataType:"json",
	        success:function(result){
	     	   if(result.success){
	     		   console.log("重置");
	     		   alertTip("重置密码已发送至注册手机上,请注意查收");
	     		   window.setTimeout(skip,2000);
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



