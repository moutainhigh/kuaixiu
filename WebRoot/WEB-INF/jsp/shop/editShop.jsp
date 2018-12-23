<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/commons/taglibs.jsp" %>
<div class="modal-backdrop fade in"></div>
<div class="modal-dialog" style="width: 700px;">
  <div class="modal-content">
    <div class="modal-title"><span>编辑门店商</span>
      <a href="javascript: void(0);" class="close" data-dismiss="modal" aria-label="Close">&times;</a>
    </div>
    <div class="modal-body">
      <form id="insertForm" method="post" class="form-horizontal">
        <input type="hidden" name="id" value="${shop.id }" />
        <div class="form-group">
          <label for="addName" class="col-sm-3 control-label"><span style="color:red">*</span> 维修门店名称</label>
          <div class="col-sm-9">
            <input type="text" id="addName" name="addName" value="${shop.name }" class="form-control" placeholder="请输入连锁商名称">
          </div>
        </div>
        <div class="form-group">
          <label for="addManagerName" class="col-sm-3 control-label"><span style="color:red">*</span> 负责人姓名</label>
          <div class="col-sm-9">
            <input type="text" id="addManagerName" name="addManagerName" value="${shop.managerName }" class="form-control" placeholder="请输入负责人姓名">
          </div>
        </div>
        <div class="form-group">
          <label for="addManagerMobile" class="col-sm-3 control-label"><span style="color:red">*</span> 负责人手机号</label>
          <div class="col-sm-9">
            <input type="text" id="addManagerMobile" name="addManagerMobile" value="${shop.managerMobile }" class="form-control" placeholder="请输入负责人手机号">
          </div>
        </div>
        <div class="form-group">
          <label for="addManagerMobile1" class="col-sm-3 control-label">备用手机号1</label>
          <div class="col-sm-9">
            <input type="text" id="addManagerMobile1" name="addManagerMobile1" value="${shop.managerMobile1 }" class="form-control" placeholder="请输入手机号">
          </div>
        </div>
        <div class="form-group">
          <label for="addManagerMobile2" class="col-sm-3 control-label">备用手机号2</label>
          <div class="col-sm-9">
            <input type="text" id="addManagerMobile2" name="addManagerMobile2" value="${shop.managerMobile2 }" class="form-control" placeholder="请输入手机号">
          </div>
        </div>
        <div class="form-group">
          <label for="addTel" class="col-sm-3 control-label"><span style="color:red">*</span> 维修门店电话号码</label>
          <div class="col-sm-9">
            <input type="text" id="addTel" name="addTel" value="${shop.tel }" class="form-control" placeholder="请输入连锁商电话号码">
          </div>
        </div>
        <div class="form-group">
          <input type="hidden" id="addAreas" name="addAreas" >
          <label for="addArea" class="col-sm-3 control-label"><span style="color:red">*</span> 地址</label>
          <div class="col-sm-9">
            <select id="addProvince" name="addProvince" onchange="fn_select_address(2, this.value, '', 'add');" class="form-control-inline">
              <option value="">--请选择--</option>
              <c:forEach items="${provinceL }" var="item" varStatus="i">
                <option value="${item.areaId }" ${item.areaId == shop.province ? 'selected="selected"' : '' }>${item.area }</option>
              </c:forEach>
            </select>
            
            <select id="addCity" name="addCity" onchange="fn_select_address(3, this.value, '', 'add');" class="form-control-inline">
              <option value="">--请选择--</option>
              <c:forEach items="${cityL }" var="item" varStatus="i">
                <option value="${item.areaId }" ${item.areaId == shop.city ? 'selected="selected"' : '' }>${item.area }</option>
              </c:forEach>
            </select>
            
            <select id="addCounty" name="addCounty" onchange="fn_select_address(4, this.value, '', 'add');" class="form-control-inline">
              <option value="">--请选择--</option>
              <c:forEach items="${countyL }" var="item" varStatus="i">
                <option value="${item.areaId }" ${item.areaId == shop.county ? 'selected="selected"' : '' }>${item.area }</option>
              </c:forEach>
            </select>
            
            <select id="addStreet" name="addStreet" class="form-control-inline" style="${empty streetL ? 'display: none;' : '' }">
              <option value="">--请选择--</option>
              <c:if test="${empty streetL}"><option value="0" selected="selected"></option></c:if>
              <c:forEach items="${streetL }" var="item" varStatus="i">
                <option value="${item.areaId }" ${item.areaId == shop.street ? 'selected="selected"' : '' }>${item.area }</option>
              </c:forEach>
            </select>
          </div>
        </div>
        <div class="form-group">
          <div class="col-sm-9 col-sm-offset-3">
            <input type="text" id="addAddress" name="addAddress" value="${shop.address }" class="form-control" placeholder="请输入详细地址">
          </div>
        </div>
        <c:if test="${loginUserType != USER_TYPE_PROVIDER }">
        <div class="form-group">
          <label for="addProviderCode" class="col-sm-3 control-label"><span style="color:red">*</span> 所属连锁商账号</label>
          <div class="col-sm-9">
            <select id="addProviderCode" name="addProviderCode" class="form-control">
              <option value="">--请选择--</option>
              <c:forEach items="${providerL }" var="item" varStatus="i">
                <option value="${item.code }" ${item.code == shop.providerCode ? 'selected="selected"' : ''} >${item.name }</option>
              </c:forEach>
            </select>
          </div>
        </div>
        </c:if>
       
        <div class="form-group">
          <label for="addBrand" class="col-sm-3 control-label">支持品牌</label>
          <div class="col-sm-9">
          	  <c:forEach items="${brandL }" var="item" varStatus="i">
          	  	<c:set var="ckd" value="${null }"></c:set>
		        <c:forEach items="${shopModels}" var="sm">
		          <c:if test="${item.id == sm.brandId }">
		            <c:set var="ckd" value="${sm }"></c:set>
		          </c:if>
		        </c:forEach>
				<label class="checkbox-inline" style="margin-left: 0px; margin-right: 10px;">
				  <input type="checkbox" name="addBrand" value="${item.id }" ${ckd != null ? 'checked="checked"' : '' }> ${item.name }
				</label>
			  </c:forEach>
          </div>
        </div>
        
        <div class="form-group">
          <label for="oldToNew" class="col-sm-3 control-label"><span style="color:red"></span>是否支持以旧换新</label>
          <div class="col-sm-9">
            <div class="oldToNew">
              <input name="oldToNew" type="radio"  value="0" ${shop.oldToNew == 0 ? 'checked="checked"' : '' }/>是
              &nbsp&nbsp&nbsp&nbsp
              <input name="oldToNew" type="radio"  value="1" ${shop.oldToNew == 1 ? 'checked="checked"' : '' }/>否
            </div>
          </div>
        </div>
        
        
        <div class="form-group">
          <label for="oldToNew" class="col-sm-3 control-label"><span style="color:red"></span>是否支持寄修</label>
          <div class="col-sm-9">
            <div class="oldToNew">
              <input name="isSendRepair" type="radio"  value="1" ${shop.isSendRepair == 1 ? 'checked="checked"' : '' }/>是
              &nbsp&nbsp&nbsp&nbsp
              <input name="isSendRepair" type="radio"  value="0" ${shop.isSendRepair == 0 ? 'checked="checked"' : '' }/>否
            </div>
          </div>
        </div>
        
         <!-- 
         <div class="form-group">
          <label for="addNewBrand" class="col-sm-3 control-label">支持兑换品牌</label>
          <div class="col-sm-9">
          	  <c:forEach items="${newBrand}" var="item" varStatus="i">
          	  	<c:set var="ckd" value="${null }"></c:set>
		        <c:forEach items="${newShopModels}" var="sm">
		          <c:if test="${item.id == sm.brandId }">
		            <c:set var="ckd" value="${sm }"></c:set>
		          </c:if>
		        </c:forEach>
				<label class="checkbox-inline" style="margin-left: 0px; margin-right: 10px;">
				  <input type="checkbox" name="addNewBrand" value="${item.id }" ${ckd != null ? 'checked="checked"' : '' }> ${item.name }
				</label>
			  </c:forEach>
          </div>
        </div>
         -->
        
        <button type="submit" class="hide" id="addSubmitBtn"></button>
      </form>
    </div>
    <div class="modal-footer">
      <button type="button" id="addSaveBtn" class="btn modal-btn" ><span class="am-icon-save icon-save"></span>提交</button>
      <button type="button" id="addMissBtn" class="btn modal-btn" data-dismiss="modal" aria-label="Close"><span class="am-icon-close icon-close"></span>取消</button>
    </div>
  </div><!-- /.modal-content -->
</div><!-- /.modal-dialog -->

<script type="text/javascript">

//表单验证
$(document).ready(function() {
    insertValidatorForm();
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
                addName: {
                    validators : {
                        notEmpty : {
                            message : "不能为空"
                        }
                    }
                },
                addManagerName: {
                    validators : {
                        notEmpty : {
                            message : "不能为空"
                        }
                    }
                },
                addManagerMobile: {
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
                addManagerMobile1: {
                    validators : {
                        regexp: {
                            regexp: /^1[0-9]{10}$/,
                            message: '请输入正确格式'
                        }
                    }
                },
                addManagerMobile2: {
                    validators : {
                        regexp: {
                            regexp: /^1[0-9]{10}$/,
                            message: '请输入正确格式'
                        }
                    }
                },
                addTel: {
                    validators : {
                        notEmpty : {
                            message : "不能为空"
                        },
                        regexp: {
                            regexp: /^(\(\d{3,4}\)|\d{3,4}-|\s)?\d{7,8}$/,
                            message: '请输入正确格式'
                        }
                    }
                },
                addProviderCode: {
                    validators : {
                        notEmpty : {
                            message : "不能为空"
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
            
            //加载等待
            AlertText.tips("d_loading");
            //校验成功后的操作
            var btn = $("#addSaveBtn");
            //让按钮不能点击
            btn.button("loading");
            //遮盖层
            var options = {
                url : "${ctx}/shop/update.do",
                dataType : "JSON",
                success : function(data) {
                    if(data.success){
                        //保存成功,关闭窗口，刷新列表
                        refreshPage();
                        //全部更新完后关闭弹窗
                        $("#addMissBtn").click();
                        //重置表单数据
                        document.getElementById("insertForm").reset();
                        }else if(data.already){
                    	    //门店id
                        	var shopId=data.shopId;
                            //返回描述
                            var msg=data.msg;
                            updateShop(shopId,msg);
                        }else{
                        //保存失败
                        alert(data.msg);
                    }
                    addFormReset();
                },
                error : function() {
                    alert("系统异常，请稍后再试");
                    addFormReset();
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


//点击保存按钮,提交form表单，触发校验
$("#addSaveBtn").click(function() {
    //格式化分类属性信息为JSON串
    $("#addSubmitBtn").click();
});

//确认修改寄修门店
function updateShop(id,msg){
	    var select=confirm(msg);
	    if(select==true) {   
	        var url_ = AppConfig.ctx + "/webpc/activity/changeSendShop.do";
		    var data_ = {id: id};
		    $.ajax({
		        url: url_,
		        data: data_,
		        type: "POST",
		        dataType: "json",
		        success: function (result) {
		            if (result.success) {
		            	    alert('编辑门店成功');
		            }else {
		                    alert(result.msg);
		            }
		        },
		        error : function() {
		        	alert("系统异常，请稍后再试");
		        }
		    });
	    }  
	    //保存成功,关闭窗口，刷新列表
        refreshPage();
        //全部更新完后关闭弹窗
        $("#addMissBtn").click();
        //重置表单数据
        document.getElementById("insertForm").reset();
        
}

</script>