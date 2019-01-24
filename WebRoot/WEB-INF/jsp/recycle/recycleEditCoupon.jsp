<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/commons/taglibs.jsp" %>
<div class="modal-backdrop fade in"></div>
<div class="modal-dialog" style="width: 700px;">
  <div class="modal-content">
    <div class="modal-title"><span>编辑优惠券</span>
      <a href="javascript: void(0);" class="close" data-dismiss="modal" aria-label="Close">&times;</a>
    </div>
    <div class="modal-body">
      <form id="insertForm" method="post" class="form-horizontal">
        <input type="hidden" name="id" value="${coupon.id }" />
        <div class="form-group">
          <label for="addName" class="col-sm-2 control-label"> 加价券编码</label>
          <div class="col-sm-9">
            <input type="text" id="couponCode" readOnly="true" name="couponCode" value="${coupon.couponCode }" class="form-control" placeholder="请输入负责人姓名">
          </div>
        </div>
        <div class="form-group">
          <label for="addManagerName" class="col-sm-2 control-label"><span style="color:red">*</span> 加价券名称</label>
          <div class="col-sm-9">
            <input type="text" id="couponName" name="name" value="${coupon.couponName }" class="form-control" placeholder="请输入负责人姓名">
          </div>
        </div>
        <div class="form-group">
          <label for="addManagerMobile" class="col-sm-2 control-label"><span style="color:red">*</span> 加价券额度条件上限</label>
          <div class="col-sm-9">
            <input type="text" id="upperLimit" name="upperLimit" value="${coupon.upperLimit }" class="form-control" placeholder="请输入负责人手机号">
          </div>
        </div>
          <div class="form-group">
              <label for="addManagerMobile" class="col-sm-2 control-label"><span style="color:red">*</span> 加价券额度条件下限</label>
              <div class="col-sm-9">
                  <input type="text" id="subtractionPrice" name="subtractionPrice" value="${coupon.subtraction_price }" class="form-control" placeholder="请输入负责人手机号">
              </div>
          </div>
          <div class="form-group">
              <label for="addManagerMobile" class="col-sm-2 control-label"><span style="color:red">*</span> 加价券类型  1：百分比 2:：固定加价</label>
              <div class="col-sm-9">
                  <input type="text" id="pricingType" name="pricingType" value="${coupon.pricingType }" class="form-control" placeholder="请输入负责人手机号">
              </div>
          </div>
          <div class="form-group" id="prices">
              <label for="couponPrice" class="col-sm-2 control-label"><span style="color:red">*</span> 加价券规则描述</label>
              <div class="col-sm-9">
                  <input type="text" id="ruleDescription" name="ruleDescription" value="${coupon.ruleDescription }" class="form-control" placeholder="请输入加价券规则描述">
              </div>
          </div>
        <div class="form-group">
          <label for="addManagerMobile" class="col-sm-2 control-label"><span style="color:red">*</span> 加价券金额</label>
          <div class="col-sm-9">
            <input type="text" id="couponPrice" name="price" value="${coupon.couponPrice }" class="form-control" placeholder="请输入负责人手机号">
          </div>
        </div>
        <div class="form-group" id="projects">
       	  <input type="hidden" id="validBeginTime" name="startTime" value="${coupon.beginTime}" />
       	  <input type="hidden" id="validEndTime" name="endTime" value="${coupon.endTime}" />
          <label for="validTime" class="col-sm-2 control-label"><span style="color:red">*</span> 有效时间</label>
          <div class="col-sm-9">
            <input type="text" id="validTime" value="${coupon.beginTime}-${coupon.endTime}" class="form-control" placeholder="请选择加价券有效时间" readonly="readonly">
          </div>
        </div>
        <div class="form-group" id="projects">
          <label for="note" class="col-sm-2 control-label"> 备注</label>
          <div class="col-sm-9">
            <input type="text" id="note" name="note" value="${coupon.note }" class="form-control" placeholder="请输入备注信息">
          </div>
        </div>
        
        <button type="submit" class="hide" id="addSubmitBtn"></button>
      </form>
    </div>
    <div class="modal-footer">
      <c:if test="${coupon.isUse == 0}">
        <button type="button" id="addSaveBtn" class="btn modal-btn" ><span class="am-icon-save icon-save"></span>提交</button>
      </c:if>
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
                url : "${ctx}/recycle/updateCoupons.do",
                dataType : "JSON",
                success : function(data) {
                    if(data.success){
                        //保存成功,关闭窗口，刷新列表
                        refreshPage();
                        //全部更新完后关闭弹窗
                        $("#addMissBtn").click();
                        //重置表单数据
                        document.getElementById("insertForm").reset();
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

/**
 * 故障全选
 */
function checkAll(obj){
	$("input[name='addProject']").each(function(){
		$(this).prop("checked", obj.checked);
    });
}

/**
 * 品牌全选
 */
 function checkAllBrands(obj){
		$("input[name='addBrand']").each(function(){
			$(this).prop("checked", obj.checked);
	    });
	}
 
 
//点击保存按钮,提交form表单，触发校验
$("#addSaveBtn").click(function() {
    //格式化分类属性信息为JSON串
    $("#addSubmitBtn").click();
});

</script>