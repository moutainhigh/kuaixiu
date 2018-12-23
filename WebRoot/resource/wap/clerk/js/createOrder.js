
			

	$(function(){
		var isMobile=/^(?:13\d|15\d|17\d|18\d)\d{5}(\d{3}|\*{3})$/;  
		$("#addSaveBtn").click(function(){
			console.log('测试');
			var obj=document.getElementsByName("addProjectId");
			var selectProjectIds=[]; //维修项目
			for(var i=0; i<obj.length; i++){ 
			if(obj[i].checked) 
				selectProjectIds.push(obj[i].value); 
			} 
			var brandId=$("#brandId").val();
			var modelId=$("#modelId").val();
			var colorId=$("#colorId").val();
			var projectIds=selectProjectIds;
			var customerName=$("#customerName").val();
			var customerMobile=$("#customerMobile").val();
			var note=$("#note").val();
			var couponCode=$("#couponCode").val();
			 if(brandId==""){
				 alertTip("请选择手机品牌");
				 return;
			 }
			 if(modelId==""){
				 alertTip("请选择维修机型");
			     return;
			 }
			 if(colorId==""){
				 alertTip("请选择机型颜色");
				  return;
			 }
			 if(selectProjectIds==""){
				 alertTip("请选择维修项目");
				  return;
			 }
			 if(customerName==""){
				 alertTip("请填写姓名");
				 return;
			 }
			 if(customerMobile==""){
				 alertTip("请填写手机号");
				 return;
			 }
			 if(!isMobile.test(customerMobile)){
				 alertTip("请输入正确的手机号");
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
			    var addCountyName = $("#s_County").find("option:selected").text();
			    var address=$("#addAddress").val();
			    if(address == ''){
			    	alertTip("请选择街道！");
			        return;
			    }
			    //省市县
			    var homeAddress = addProvinceName + " " + addCityName + " " + addCountyName ;
			    //验证完成 限制其多次点击
			  
			    
			$.ajax({
				  url:AppConfig.ctx+"/wap/clerk/saveOrder.do",
	        	  data:{brandId:brandId,modelId:modelId,colorId:colorId,projectId:JSON.stringify(selectProjectIds),customerName:customerName,customerMobile:customerMobile
	        		  ,note:note,couponCode:couponCode,province:province,city:city,county:county,areas:address,homeAddress:homeAddress
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
	           },
	             error:function(){
	            	 alertTip("系统异常，请稍后再试");
	             }
	           });
				
				
				
				
			});
			
		   //多拉选初始化
		   $("#s_Province").append(packageAddress(privinceData,""));
			
		});
		
	function skip(){ 
		 window.location.href=AppConfig.ctx+"/wap/clerk/index.do";
		} 	
	
	var isLoading = false;
	//获取优惠码信息
	function couponInfo(obj){
		if(isLoading){
			return;
		}
		isLoading = true;
		
		var couponCode = $("#couponCode").val();

		if(couponCode == ""){
			alertTip("请输入优惠码！");
	        $("#couponCode").focus();
			isLoading = false;
	        return false;
		}
		//加载等待
	  //  alertTip("d_loading");
		/**
	     * 获取优惠码信息
	     */
	    var url_ = AppConfig.ctx + "/wap/clerk/couponInfo.do";
	    $.ajax({
	        url: url_,
	        type: "POST",
	        data: {couponCode:couponCode},
	        dataType: "json",
	        success: function (result) {
	            if (result.success) {
	        		//显示优惠码
	            	var c = result.data;
	            	var html = "<br/>优惠码名称：<span style='color: #333;'>";
	            	html += c.couponName;
	            	html += "</span><br/>优惠金额：<span style='color: #e51e1e; font-size: 28px;'>¥ ";
	            	html += c.couponPrice;
	            	html += "</span><br/>有效时间：<span style='color: #666;'>";
	            	html += c.beginTime + " - " + c.endTime + "</span>";
	            	if(result.projects && result.projects.length > 0){
		            	html += "<br/>限定项目：<span style='color: #666;'>";
		            	$.each(result.projects, function (i, value) {
		            		if(i > 0){
		            			html += "、";
		            		}
		            		html += value.projectName;
		            	});
		            	html += "</span>";
		            }
		            if(result.models && result.models.length > 0){
		            	html += "<br/>限定品牌：<span style='color: #666;'>";
		            	$.each(result.models, function (i, value) {
		            		if(i > 0){
		            			html += "、";
		            		}
		            		html += value.brandName;
		            	});
		            	html += "</span>";
		            }
		            html += "<br/>备&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp注：<span style='color: #666;'>";
	            	html += c.note + "</span>";
		            $("#couponInfo").html(html);
	            	isLoading = false;
	            } else {
	            	$("#couponInfo").html("");
	            	alertTip(result.msg);
	            	isLoading = false;
	            }
	        },
	        error : function() {
	        	$("#couponInfo").html("");
	        	alertTip("系统异常，请稍后再试");
	        	isLoading = false;
	        }
	    });
	}

	/**
	 * 查看模型
	 */
	function brandChange(id){
	    $("#modelId option[value!='']").remove();
	    if(id){
	    	var url = AppConfig.ctx + "/webpc/repair/modelList.do";
	        $.get(url, {brandId: id}, function(result){
	            if(!result.success){
	                return false;
	            }
	            var json = result.data;
	            var select_html = '';
	            if(json.length>0){
	                for( a in json){
	                    select_html +='<option value="'+json[a]['id']+'">'+json[a]['name']+'</option>';
	                }
	            }
	            $("#modelId").append(select_html);
	        });
	    }
	}

	/**
	 * 根据选择机型加载机型颜色和机型对应维修项目
	 * @param id
	 */
	function selectModel(id){
		/**
	     * 获得所选机型和所有可维修项目
	     */
	    var url_ = AppConfig.ctx + "/webpc/repair/modelInfo.do";
	    $.ajax({
	        url: url_,
	        type: "POST",
	        data: {modelId: id},
	        dataType: "json",
	        success: function (result) {
	            if (result.success) {
	                eachFullColor(result.modelColor);
	                eachFullProject(result.modelFault);
	            } else {
	            	alertTip(result.msg);
	            }
	        },
	        error : function() {
	        	alertTip("系统异常，请稍后再试");
	        	loading_hide();
	        	isLoading = false;
	        }
	    });
	    return true;
	}

	//遍历填颜色
	function eachFullColor(info) {
		$("#colorId option[value!='']").remove();
	    var select_html = '';
	    $.each(info, function (i, value) {
	    	select_html +='<option value="'+ value +'">'+ value +'</option>';
	    });
	    $("#colorId").append(select_html);
	}
	var screenBroken="#89971c83-d95c-11e6-a112-00163e04c890";//屏幕破碎(华为)
	var outsideScreenBroken="#db3c6320-1aa1-11e7-932d-00163e04c890";//外屏破碎(华为)
	//遍历填可维修项目
	function eachFullProject(info) {
		$(".projectItem").remove();
		$("#orderPrice").text(" ¥ 0.00");
	    $("#orderPrice").attr("price", 0);
	    $.each(info, function (i, value) {
	        var tmp = $("#projectDiv").clone();
	        var projectId = value.projectId;
	        $(tmp).addClass("projectItem");
	        $(".projectName", tmp).html(value.projectName);
	        $(".price", tmp).html("¥" + dataFormat(value.price));
	        $("[name=addProjectId]", tmp).val(projectId);
	        $("[name=addProjectId]", tmp).attr("id", projectId);
	        $("[name=addProjectId]", tmp).attr("projectName", value.projectName);
	        $("[name=addProjectId]", tmp).attr("price", value.price);
	        //重新计算价格
	        //点击事件
	        $("[name=addProjectId]", tmp).click(function () {
	        	var price = parseFloat($(this).attr("price"));
	        	if (this.checked) {
		        	var orderPrice = parseFloat($("#orderPrice").attr("price"));
		        	orderPrice = orderPrice + price;
		        	//显示订单总价
		    	    $("#orderPrice").text(" ¥ " + dataFormat(orderPrice));
		    	    $("#orderPrice").attr("price", orderPrice);
		    	    if(this.value == "6aaa3f6e-6f71-11e6-b930-10c37b579295"){
		    	    	if($("#fe750fc5-70ff-11e6-80cd-10c37b579295").length > 0
		    	    			&& $("#fe750fc5-70ff-11e6-80cd-10c37b579295").get(0).checked){
		    	    		$("#fe750fc5-70ff-11e6-80cd-10c37b579295").prop("checked", false);
		    	    		price = parseFloat($("#fe750fc5-70ff-11e6-80cd-10c37b579295").attr("price"));
		    	    		//显示订单总价
		    	    	    $("#orderPrice").text(" ¥ " + dataFormat(orderPrice - price));
		    	    	    $("#orderPrice").attr("price", orderPrice - price);
		    	    	}
		    	    }
		    	    else if(this.value == "fe750fc5-70ff-11e6-80cd-10c37b579295"){
						if($("#6aaa3f6e-6f71-11e6-b930-10c37b579295").length > 0
								&& $("#6aaa3f6e-6f71-11e6-b930-10c37b579295").get(0).checked){
							$("#6aaa3f6e-6f71-11e6-b930-10c37b579295").prop("checked", false);
							price = parseFloat($("#6aaa3f6e-6f71-11e6-b930-10c37b579295").attr("price"));
							//显示订单总价$(this).prop("checked", obj.checked);
				    	    $("#orderPrice").text(" ¥ " + dataFormat(orderPrice - price));
				    	    $("#orderPrice").attr("price", orderPrice - price);
		    	    	}
		    	    }else if(this.value=="89971c83-d95c-11e6-a112-00163e04c890"){
		    	    	if($(outsideScreenBroken).length > 0
								&& $(outsideScreenBroken).get(0).checked){
							$(outsideScreenBroken).prop("checked", false);
							price = parseFloat($(outsideScreenBroken).attr("price"));
				    	    $("#orderPrice").text(" ¥ " + dataFormat(orderPrice - price));
				    	    $("#orderPrice").attr("price", orderPrice - price);
		    	    	}
		    	    }else if(this.value=="db3c6320-1aa1-11e7-932d-00163e04c890"){
		    	    	if($(screenBroken).length > 0
								&& $(screenBroken).get(0).checked){
							$(screenBroken).prop("checked", false);
							price = parseFloat($(screenBroken).attr("price"));
				    	    $("#orderPrice").text(" ¥ " + dataFormat(orderPrice - price));
				    	    $("#orderPrice").attr("price", orderPrice - price);
		    	    	}
		    	    }
	        	}
	        	else {
	        		var orderPrice = parseFloat($("#orderPrice").attr("price"));
		        	//显示订单总价
		    	    $("#orderPrice").text(" ¥ " + dataFormat(orderPrice - price));
		    	    $("#orderPrice").attr("price", orderPrice - price);
	        	}
	        });
	        //追加到对应区块
	        $("#projects").before(tmp);
	        tmp.show();
	    });
	}
	

	
	
	
	
	
	
	
	
