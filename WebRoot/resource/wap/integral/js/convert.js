$(function(){
	$("#news").click(function(){
		var input=$("#input").val();
		console.log(integral);
        if(input==""){
        	alertTip("请输入积分");
        	return;
        }		
		if(input==0){
			alertTip("兑换积分不能为0");
			return;
		}
		
		if(parseInt(input)>parseInt(integral)){
			alertTip("兑换积分不能大于剩余积分");
			return;
		}
		$.ajax({
			
			url:AppConfig.ctx+"/wap/integral/commitIntegral.do",
		   data:{input:input},
		   type:"POST",
	   dataType:"JSON",
	    success:function(result){
	    	if(result.success){
	    		alertTip("兑换申请成功");
	    		window.setTimeout(skip,1500);
	    	}else{
	    		alertTip(result.msg);
	    	}
	    },
	     error:function(){
	    	 alertTip("系统异常，请稍后再试");
	     }
		});
	});
});

function skip(){ 
	window.location.href=AppConfig.ctx+"/wap/integral/convert.do";
	} 