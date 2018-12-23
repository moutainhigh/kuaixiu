<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/commons/taglibs.jsp" %>
<link rel="stylesheet" href="${webResourceUrl}/resource/order/css/order.detail.css">
<script src="${webResourceUrl}/resource/js/address.js" type="text/javascript" charset="utf-8"></script>
      <div class="am-cf am-padding am-padding-bottom-0">
        <div class="am-fl am-cf" style="width: 100%;">
          <strong class="am-text-primary am-text-lg"><a href="javascript:void(0);"  onclick="toList();">订单管理</a></strong> / <small>快速下单</small>
        </div>
      </div>
      
      <hr>
      
      <div class="am-g">
        <form id="insertForm" method="post" class="form-horizontal">
           <div class="form-group">
	          <label for="brandId" class="col-sm-2 control-label"><span style="color:red">*</span>维修方式</label>
	          <div style="position:relative;top:8px;">
               <label style="width:20px;"></label>
	        <label><input name="repairType" type="radio"  value="0"  checked />
	                                 上门维修
	        </label>
	            <label style="width:50px;"></label>
	         <label><input name="repairType" type="radio"  value="3"/>
	                                  寄修
	        </label>
	          </div>
	        </div>
        
        	<div class="form-group">
	          <label for="brandId" class="col-sm-2 control-label"><span style="color:red">*</span>手机品牌</label>
	          <div class="col-sm-9">
	            <select id="brandId" name="brandId" onchange="brandChange(this.value);" class="form-control">
	              <c:forEach items="${brands }" var="item" varStatus="i">
	                <option value="${item.id }">${item.name }</option>
	              </c:forEach>
	            </select>
	          </div>
	        </div>
	        <div class="form-group">
	          <label for="modelId" class="col-sm-2 control-label"><span style="color:red">*</span>维修机型</label>
	          <div class="col-sm-9">
	            <select id="modelId" name="modelId" class="form-control" onchange="selectModel(this.value);">
	              <option value="">--请选择--</option>
	              <c:forEach items="${models }" var="item" varStatus="i">
	                <option value="${item.id }">${item.name }</option>
	              </c:forEach>
	            </select>
	          </div>
	        </div>
	        <div class="form-group">
	          <label for="colorId" class="col-sm-2 control-label"><span style="color:red">*</span>颜色</label>
	          <div class="col-sm-9">
	            <select id="colorId" name="colorId" class="form-control">
	              <option value="">--请选择--</option>
	            </select>
	          </div>
	        </div>
	        <div class="form-group">
	          <input type="hidden" id="projectIds" name="projectIds" />
	          <label for="addColor" class="col-sm-2 control-label"><span style="color:red">*</span> 故障类型</label>
	        </div>
	        <div id="projectDiv" class="form-group" style="display: none;">
		      <div class="col-sm-6 col-sm-offset-2">
		        <div class="checkbox">
		          <label>
		            <input type="checkbox" name="addProjectId" ><span class="projectName">内屏碎裂</span>
		          </label>
		        </div>
		      </div>
		      <div class="col-sm-3 ">
		        <p class="form-control-static">价格： <b class="price" style="color: #e51e1e;">￥200.00</b></p>
		      </div>
		    </div>
		    <div class="form-group" id="projects">
	          <label for="customerName" class="col-sm-2 control-label"><span style="color:red">*</span> 联系人姓名</label>
	          <div class="col-sm-9">
	            <input type="text" id="customerName" name="customerName" class="form-control" placeholder="请输入联系人姓名">
	          </div>
	        </div>
	        <div class="form-group">
	          <label for="customerMobile" class="col-sm-2 control-label"><span style="color:red">*</span> 联系人手机号</label>
	          <div class="col-sm-9">
	            <input type="text" id="customerMobile" name="customerMobile" maxlength="11" class="form-control" placeholder="请输入联系人手机号">
	          </div>
	        </div>
	        <div class="form-group">
	          <input type="hidden" id="addAreas" name="addAreas" >
	          <label for="addArea" class="col-sm-2 control-label"><span style="color:red">*</span> 联系人地址</label>
	          <div class="col-sm-9">
	            <select id="addProvince" name="addProvince" onchange="fn_select_address(2, this.value, '', 'add');" class="form-control-inline">
	              <option value="">--请选择--</option>
	              <c:forEach items="${provinceL }" var="item" varStatus="i">
	                <option value="${item.areaId }">${item.area }</option>
	              </c:forEach>
	            </select>
	            
	            <select id="addCity" name="addCity" onchange="fn_select_address(3, this.value, '', 'add');" class="form-control-inline" style="display: none;">
	              <option value="">--请选择--</option>
	            </select>
	            
	            <select id="addCounty" name="addCounty" onchange="fn_select_address(4, this.value, '', 'add');" class="form-control-inline" style="display: none;">
	              <option value="">--请选择--</option>
	            </select>
	            
	            <select id="addStreet" name="addStreet" class="form-control-inline" style="display: none;">
	              <option value="">--请选择--</option>
	            </select>
	          </div>
	        </div>
	        <div class="form-group">
	          <div class="col-sm-9 col-sm-offset-2">
	            <input type="text" id="addAddress" name="addAddress" class="form-control" placeholder="请输入详细地址">
	          </div>
	        </div>
	        
	        <div class="form-group" id="sendAddress" style="display:none;">
	          <label for="note" class="col-sm-2 control-label"> 邮寄地址</label>
	          <div style="position:relative;top:7px;left:15px;font-size:16px;color:red;">
	                <p>浙江 杭州市 下城区 武林广场东侧3号     </p>
	                <p style="position:relative;left:215px;">测试A收    手机号：1111111111111</p>
	          </div>
	        </div>
	        
	        
	        <div class="form-group" id="projects">
	          <label for="note" class="col-sm-2 control-label"> 备注</label>
	          <div class="col-sm-9">
	            <input type="text" id="note" name="note" class="form-control" placeholder="请输入备注信息">
	          </div>
	        </div>
	        <div class="form-group">
	          <label for="note" class="col-sm-2 control-label"> 优惠码</label>
	          <div class="col-sm-9">
	            <div class="input-group">
	              <input type="text" id="couponCode" name="couponCode" class="form-control" placeholder="请输入优惠码">
	              <span onclick="couponInfo();" class="btn input-group-addon">查看</span>
	            </div>
	          </div>
	        </div>
	        <div class="form-group" id="projects">
	          <div class="col-sm-9 col-sm-offset-2">
	            <div id="couponInfo" ></div>
	          </div>
	        </div>
          <div class="form-group">
            <div class="col-sm-9 col-sm-offset-2">
              <button id="addSaveBtn" type="button" class="btn btn-default fl" style="padding: 6px 80px;" >保 存</button>
              <span class="form-control-static form-control-inline pull-left ml20"></span>
              <span class="form-control-static form-control-inline pull-left ml20"></span>
              <span class="form-control-static form-control-inline pull-left ml20"></span>
              <span class="form-control-static form-control-inline pull-left  ml20"><b>总价 </b> <b id="orderPrice" price="0" style="color: #e51e1e;">¥ 0.00</b></span>
            </div>
          </div>
          <button type="submit" class="hide" id="addSubmitBtn"></button>
	    </form>
      </div><!-- /am-g -->
      

<script type="text/javascript">
function toList(){
	func_reload_page("${ctx}/order/list.do");
}

//表单验证
$(document).ready(function() {
    insertValidatorForm();
});

//点击保存按钮,提交form表单，触发校验
$("#addSaveBtn").click(function() {
    //格式化分类属性信息为JSON串
    $("#addSubmitBtn").click();
});

//初始化表单
function insertValidatorForm() {
    $("#insertForm")
        .bootstrapValidator({
            message : "不能为空",
            feedbackIcons : {
                valid : 'glyphicon glyphicon-ok',
                invalid : 'glyphicon glyphicon-remove',
                validating : 'glyphicon glyphicon-refresh'
            },
            fields : {
            	brandId: {
                    validators : {
                        notEmpty : {
                            message : "不能为空"
                        }
                    }
                },
                modelId: {
                    validators : {
                    	notEmpty : {
                            message : "不能为空"
                        }
                    }
                },
                colorId: {
                    validators : {
                    	notEmpty : {
                            message : "不能为空"
                        }
                    }
                },
                customerName: {
                    validators : {
                    	notEmpty : {
                            message : "不能为空"
                        }
                    }
                },
                customerMobile: {
                    validators : {
                    	notEmpty : {
                            message : "不能为空"
                        },
                        regexp: {
                            regexp: /^1[0-9]{10}$/,
                            message: '请输入正确格式'
                        }
                    }
                },
                addAddress: {
                    validators : {
                    	notEmpty : {
                            message : "不能为空"
                        }
                    }
                }
            }// end fields
        }).on("success.form.bv", function(e) {
            // 阻止表单提交
            e.preventDefault();
            // 验证颜色是否添加
            //console.log("ddd");
            // 验证是否选择故障
            var chkboxs = $("input[name='addProjectId']");
            var ids = "";
            for(var i = 0; i < chkboxs.size(); i++){
            	if(chkboxs[i].checked){
            		ids += ",'" + chkboxs[i].value + "'";
            	}
            }
            if (ids != "") {
            	ids = "[" + ids.substring(1, ids.length) + "]";
            } else {
            	AlertText.tips("d_alert", "提示", "请选择故障类型！");
            	$("#insertForm").data("bootstrapValidator").resetForm();
                return false;
            }
            $("#projectIds").val(ids);
            //验证地址选择
            if($("#addProvince").val() == ''){
                AlertText.tips("d_alert", "提示", "请选择省份！");
                $("#insertForm").data("bootstrapValidator").resetForm();
                return;
            }
            var addProvinceName = $("#addProvince").find("option:selected").text();
            
            if($("#addCity").val() == ''){
                AlertText.tips("d_alert", "提示", "请选择地市！");
                $("#insertForm").data("bootstrapValidator").resetForm();
                return;
            }
            var addCityName = $("#addCity").find("option:selected").text();
            
            if($("#addCounty").val() == ''){
                AlertText.tips("d_alert", "提示", "请选择区县！");
                $("#insertForm").data("bootstrapValidator").resetForm();
                return;
            }
            var addCountyName = $("#addCounty").find("option:selected").text();
            
            if($("#addStreet").val() == ''){
            	 AlertText.tips("d_alert", "提示", "请选择街道/乡镇！");
                 $("#insertForm").data("bootstrapValidator").resetForm();
                 return;
            }
            var addStreetName = "";
            if($("#addStreet").val() != '0'){
                addStreetName = " " + $("#addStreet").find("option:selected").text();  
            }
            
            var areas = addProvinceName + " " + addCityName + " " + addCountyName + addStreetName;
            $("#addAreas").val(areas);
            // 及是否输入金额
            //加载等待
            AlertText.tips("d_loading");
            //校验成功后的操作
            var btn = $("#addSaveBtn");
            //让按钮不能点击
            btn.button("loading");
            //遮盖层
            var options = {
                url : "${ctx}/order/save.do",
                dataType : "JSON",
                success : function(data) {
                    if(data.success){
                        //保存成功,关闭窗口，刷新列表
                        //refreshPage();
                    	AlertText.tips("d_alert", "提示", "订单提交成功", function (){
                    		func_reload_page("${ctx}/order/detail.do?id=" + data.data);
                    	});
                    }else if(data.sendType!=null){
                    	var select=confirm(data.msg);
                    	if(select==true){
                    		$("input:radio[name=repairType]").eq(1).attr("checked","checked");
                    		window.location.reload;//页面刷新
                    		findSendAddress($("#addCity").val());//得到寄修地址
                    		$('#mask_boxs').hide();
                    		$('#addSaveBtn').removeClass("disabled");
                    		$('#addSaveBtn').attr("disabled",false);
                    		$('#addSaveBtn').text("保存");
                    	}else{
                    		addFormReset();
                    	}
                    }else{
	                    addFormReset();
                        //保存失败
                        AlertText.tips("d_alert", "提示", data.msg);
                    }
                },
                error : function() {
                    addFormReset();
                    AlertText.tips("d_alert", "提示", "系统异常，请稍后再试");
                }
            }; // end options
            $("#insertForm").ajaxSubmit(options);
        }); // end on("success.form.bv"
}

/**
 * 重置表单
 */
function addFormReset(){
    //重置表单验证
    $("#insertForm").data("bootstrapValidator").resetForm();
    //让按钮重新能点击
    $("#addSaveBtn").button("reset");
    //隐藏等待
    AlertText.hide();
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
		AlertText.tips("d_alert", "提示", "请输入优惠码！");
        $("#couponCode").focus();
		isLoading = false;
        return false;
	}
	//加载等待
    AlertText.tips("d_loading");
	/**
     * 获取优惠码信息
     */
    var url_ = AppConfig.ctx + "/order/couponInfo.do";
    $.ajax({
        url: url_,
        type: "POST",
        data: {couponCode:couponCode},
        dataType: "json",
        success: function (result) {
            if (result.success) {
        		//显示优惠码
            	var c = result.data;
            	var html = "优惠码名称：<span style='color: #333;'>";
            	html += c.couponName;
            	html += "</span>，优惠金额：<span style='color: #e51e1e; font-size: 28px;'>¥ ";
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
	            html += "<br/>备注：<span style='color: #666;'>";
            	html += c.note + "</span>";
	            $("#couponInfo").html(html);
	            addFormReset();
            	isLoading = false;
            } else {
            	$("#couponInfo").html("");
            	AlertText.tips("d_alert", "提示", result.msg);
            	isLoading = false;
            }
        },
        error : function() {
        	$("#couponInfo").html("");
        	AlertText.tips("d_alert", "提示", "系统异常，请稍后再试");
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
    	var url = AppConfig.ctx + "/model/queryByBrandId.do";
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
            	//console.log("ss");
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
$(function(){
	 $("input:radio[name=repairType]").change(function(){
		 var choose=$('input:radio[name="repairType"]:checked').val(); 
		 if(choose==0){
			//上门维修
		    $("#sendAddress").hide();
		 }
	 
	 });
	
});


//查询邮寄地址
function findSendAddress(city){
	  
	//获取品牌id
	var brandId = $("#brandId").find("option:selected").val();
	//获取机型id
	var modelId = $("#modelId").find("option:selected").val();
	var color = $("#colorId").find("option:selected").val();
	//获取选择的故障类型
	 var chkboxs = $("input[name='addProjectId']");
     var idsObj = "";
     for(var i = 0; i < chkboxs.size(); i++){
     	if(chkboxs[i].checked){
     		idsObj += ",'" + chkboxs[i].value + "'";
     	}
     }
    var province = $("#addProvince").val();
    var city = $("#addCity").val();
    
	  var url_ = AppConfig.ctx + "/wechat/repair/sendAddress.do";
	    var params={"province":province,"city":city};
	    $.ajax({
	        url: url_,
	        type: "POST",
	        data: {params:JSON.stringify(params) },
	        dataType: "json",
	        success: function (info) {
	            if (info.success) {
	            var shop=info.result.shop  //邮寄店铺信息
	            $('#sendAddress').show();
	 		    $('#sendAddress').find('p').eq(0).text('   '+shop.fullAddress);
	 		    $('#sendAddress').find('p').eq(1).text('收件人：'+shop.managerName+'    手机号：'+shop.managerMobile);
	            }else{
	            	alert(result.resultMessage);
	            } 
	        },
	        error : function() {
	        	alert("系统异常，请稍后再试");
	        }
	    });
}




</script>