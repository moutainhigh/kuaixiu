//根据品牌id得到该品牌下所有机型
var allData="";//全局后台参数
var modelPrice='';//机型价格
function brandChange(id){
  $("#query_model option[value!='']").remove();
  $("#query_color option[value!='']").remove();
  $("#price").text("");
  $("#modelPrice").hide();
  if(id){
  	var url = AppConfig.ctx + "/webpc/repair/modelLists.do";
      $.get(url, {brandId: id}, function(result){
          if(!result.success){
              return false;
          }
          allData=result.data;
          var json =[];
          json = result.data;
          var select_html = '';
          if(json.length>0){
          	//去除重复的名字
              for(var a=0; a<json.length;a++){
              	for(var j=json.length-1;j>a;j--){
             		if(json[a]['name']==json[j]['name']){
             		//	json.splice(j,1);
              		}
              	}
              }
              	
             for(var a=0; a<json.length;a++){
                  select_html +='<option value="'+json[a]['id']+'">'+json[a]['name']+" "+json[a]['memory']+"G "+json[a]['edition'] +'</option>';
              }
          }
          $("#query_model").append(select_html);
      });
  }
}

//根据机型 得到颜色和价格
function selectModel(id){
	    $("#query_color option[value!='']").remove();
	    $("#price").text("");
	    $("#modelPrice").hide();
	    var color="";//拱筛选的颜色
	    	for(a in allData){
	    		if(allData[a]['id']==id){
	    			color=allData[a]['color'];
	    			modelPrice=allData[a]['price'];
	    		}
	    	}
	    	   //将得到的颜色拆分成单选模式
		    color=color.substr(1);
		    var strs=new Array();//颜色数组
		    strs=color.split(",");
	                   var select_color='';
	                    if(strs.length>0){
	                       for( a in strs){
	                            select_color +='<option value="'+strs[a]+'">'+strs[a]+'</option>';
	                        }
	                    }
	                    $("#query_color").append(select_color);
	   
}

//根据机型得到价格
function selectColor(id){
	   $("#modelPrice").show();
	   $("#price").text(modelPrice+"元");
	   //通过隐藏域表单提交价格和机型id
     $("#hiddenPrice").val(modelPrice); 
}

$(function(){
	$("#otherModel").attr("disabled", true); 
	$("#otherPrice").attr("disabled", true);
	
	
	//限制单选
	 $("input:radio[name=choose]").change(function(){
		 var choose=$('input:radio[name="choose"]:checked').val(); 
			if(choose==0){
				$("#query_brand").attr("disabled", false); 
				$("#query_model").attr("disabled", false); 
				$("#query_color").attr("disabled", false); 
				
				$("#otherModel").attr("disabled", true); 
				$("#otherPrice").attr("disabled", true); 
				
				$("#otherModel").val("");
				$("#otherPrice").val("");
			}else{
				$("#otherModel").attr("disabled", false); 
				$("#otherPrice").attr("disabled", false); 
				
				$("#query_brand").val("");
				$("#query_model").val("");
				$("#query_color").val("");
				
				$("#query_brand").attr("disabled", true); 
				$("#query_model").attr("disabled", true); 
				$("#query_color").attr("disabled", true); 
				$("#price").text("");
			    $("#modelPrice").hide();
			}
	 });
	 
});
