$(function(){
	
	  
	  var isMobile=/^(?:13\d|15\d|17\d|18\d)\d{5}(\d{3}|\*{3})$/; 
	  $("#news").click(function(){
		  var tel=$("#inputtel").val();
		  var password=$("#password").val();
		  
		  if(tel==""){
			  alertTip("请输入手机号");
			  return;
		  }
		  if(!isMobile.test(tel)){
			  alertTip("请输入正确的手机号");
			  return;
		  }
		  if(password==""){
			  alertTip("请输入密码");
			  return;
		  }
		  $.ajax({
			  url:AppConfig.ctx+"/wap/clerk/checkLogin.do",
		     data:{tel:tel,password:password},
		     type:"POST",
	     dataType:"JSON",
	      success:function(result){
	    	  if(result.success){
	    		  window.location.href=AppConfig.ctx+"/wap/clerk/index.do";
	    	  }else{
	    		  alertTip(result.msg);
	    	  }
	      },
			error:function(result){
				  alertTip("系统异常");
			}  
			  
		  });
			  
		 
		  
	  });
	
});