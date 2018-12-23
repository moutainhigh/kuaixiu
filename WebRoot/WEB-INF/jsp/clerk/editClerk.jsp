<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/commons/taglibs.jsp" %>
<div class="modal-backdrop fade in"></div>
<div class="modal-dialog" style="width: 700px;">
  <div class="modal-content">
    <div class="modal-title"><span>编辑店员</span>
      <a href="javascript: void(0);" class="close" data-dismiss="modal" aria-label="Close">&times;</a>
    </div>
    <div class="modal-body">
      <form id="insertForm" method="post" class="form-horizontal">
        <input type="hidden" name="id" value="${clerk.id}" />
        <div class="form-group">
          <label for="addName" class="col-sm-3 control-label"><span style="color:red">*</span> 姓名</label>
          <div class="col-sm-9">
            <input type="text" id="name" name="name" value="${clerk.name}" class="form-control" maxlength="8" placeholder="请输入姓名">
          </div>
        </div>
        <div class="form-group">
          <label for="addManagerMobile" class="col-sm-3 control-label"><span style="color:red">*</span> 手机号码</label>
          <div class="col-sm-9">
            <input type="text" id="tel" name="tel" value="${clerk.tel}" class="form-control" type="number"  oninput="if(value.length>11)value=value.slice(0,11)"  placeholder="请输入手机号">
          </div>
        </div>
         <div class="form-group">
          <label for="addManagerName" class="col-sm-3 control-label"><span style="color:red">*</span> 身份证号</label>
          <div class="col-sm-9">
            <input type="text" id="identityCard" name="identityCard" value="${clerk.identityCard}" class="form-control" maxlength="18" placeholder="请输入身份证号">
          </div>
        </div>
        <div class="form-group">
          <label for="addTel" class="col-sm-3 control-label"><span style="color:red">*</span> 微信号</label>
          <div class="col-sm-9">
            <input type="text" id="wechatId" name="wechatId" value="${clerk.wechatId }" class="form-control" maxlength="32" placeholder="请输入微信号">
          </div>
        </div>
        
          <div class="form-group">
          <label for="addGender" class="col-sm-3 control-label"><span style="color:red">*</span> 是否实名制</label>
          <div class="col-sm-9">
            <label class="radio-inline">
              <input type="radio" name="addRealName" value="1" ${clerk.isRealName == 1 ? 'checked="checked"' : '' }> 是
            </label>
            <label class="radio-inline">
              <input type="radio" name="addRealName" value="2" ${clerk.isRealName == 2 ? 'checked="checked"' : '' }> 否
            </label>
          </div>
        </div>
       
       
        <div class="form-group">
          <input type="hidden" id="addAreas" name="addAreas" >
          <label for="addArea" class="col-sm-3 control-label"><span style="color:red">*</span> 地址</label>
          <div class="col-sm-9">
            <select id="addProvince" name="addProvince" onchange="fn_select_address(2, this.value, '', 'add');" class="form-control-inline">
              <option value="">--请选择--</option>
              <c:forEach items="${provinceL }" var="item" varStatus="i">
                <option value="${item.areaId }" ${item.areaId == clerk.province ? 'selected="selected"' : '' }>${item.area }</option>
              </c:forEach>
            </select>
            
            <select id="addCity" name="addCity" onchange="fn_select_address(3, this.value, '', 'add');" class="form-control-inline">
              <option value="">--请选择--</option>
              <c:forEach items="${cityL }" var="item" varStatus="i">
                <option value="${item.areaId }" ${item.areaId == clerk.city ? 'selected="selected"' : '' }>${item.area }</option>
              </c:forEach>
            </select>
            
            <select id="addCounty" name="addCounty" onchange="fn_select_address(4, this.value, '', 'add');" class="form-control-inline">
              <option value="">--请选择--</option>
              <c:forEach items="${countyL }" var="item" varStatus="i">
                <option value="${item.areaId }" ${item.areaId == clerk.county ? 'selected="selected"' : '' }>${item.area }</option>
              </c:forEach>
            </select>
            
           
          </div>
        </div>
        <div class="form-group">
          <div class="col-sm-9 col-sm-offset-3">
            <input type="text" id="addAddress" name="addAddress" value="${clerk.street }" class="form-control" maxlength="16" placeholder="请输入详细地址">
          </div>
        </div>
        
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
                name: {
                    validators : {
                        notEmpty : {
                            message : "不能为空"
                        }
                    }
                },
                wechatId: {
                    validators : {
                        notEmpty : {
                            message : "不能为空"
                        }
                    }
                },
                tel: {
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
                identityCard: {
                    validators : {
                        notEmpty : {
                            message : "不能为空"
                        },
                        regexp: {
                            regexp: /(^\d{15}$)|(^\d{18}$)|(^\d{17}(\d|X|x)$)/,
                            message: '请输入正确格式'
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
            
            if($("#addAddress").val() == ''){
                AlertText.tips("d_alert", "提示", "请填写详细地址！");
                $("#insertForm").data("bootstrapValidator").resetForm();
                return;
            }
            var areas = addProvinceName + " " + addCityName + " " + addCountyName ;
            $("#addAreas").val(areas);
            //加载等待
            AlertText.tips("d_loading");
            //校验成功后的操作
            var btn = $("#addSaveBtn");
            //让按钮不能点击
            btn.button("loading");
            //遮盖层
            
            var options = {
                url : "${ctx}/wap/clerk/update.do",
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
                        console.log("失败");
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

</script>