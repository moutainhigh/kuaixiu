<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/commons/taglibs.jsp" %>
<div class="modal-backdrop fade in"></div>
<div class="modal-dialog">
  <div class="modal-content">
    <div class="modal-title"><span>编辑维修工程师</span>
      <a href="javascript: void(0);" class="close" data-dismiss="modal" aria-label="Close">&times;</a>
    </div>
    <div class="modal-body">
      <form id="insertForm" method="post" class="form-horizontal">
        <input type="hidden" name="id" value="${engineer.id }" />
        <div class="form-group">
          <label for="addName" class="col-sm-3 control-label"><span style="color:red">*</span> 姓名</label>
          <div class="col-sm-9">
            <input type="text" id="addName" name="addName" value="${engineer.name }" class="form-control" placeholder="请输入维修工程师姓名">
          </div>
        </div>
        <div class="form-group">
          <label for="addGender" class="col-sm-3 control-label"><span style="color:red">*</span> 性别</label>
          <div class="col-sm-9">
            <label class="radio-inline">
              <input type="radio" name="addGender" value="1" ${engineer.gender == '男' ? 'checked="checked"' : '' }> 男
            </label>
            <label class="radio-inline">
              <input type="radio" name="addGender" value="0" ${engineer.gender == '女' ? 'checked="checked"' : '' }> 女
            </label>
          </div>
        </div>
        <div class="form-group">
          <label for="addMobile" class="col-sm-3 control-label"><span style="color:red">*</span> 手机号</label>
          <div class="col-sm-9">
            <input type="text" id="addMobile" name="addMobile" value="${engineer.mobile }" maxlength="11" class="form-control" placeholder="请输入维修工程师手机号">
          </div>
        </div>
        <div class="form-group">
          <label for="addIdcard" class="col-sm-3 control-label"><span style="color:red">*</span> 身份证号码</label>
          <div class="col-sm-9">
            <input type="text" id="addIdcard" name="addIdcard" value="${engineer.idcard }" maxlength="18" class="form-control" placeholder="请输入维修工程师身份证号码">
          </div>
        </div>
        <div class="form-group">
          <label for="addDispatch" class="col-sm-3 control-label"><span style="color:red">*</span>是否在线</label>
          <div class="col-sm-9">
            <label class="radio-inline">
              <input type="radio" name="addDispatch" value="2" ${engineer.isDispatch == 2 ? 'checked="checked"' : '' }> 下线
            </label>
            <label class="radio-inline">
              <input type="radio" name="addDispatch" value="0" ${engineer.isDispatch != 2 ? 'checked="checked"' : '' }> 在线
            </label>
          </div>
        </div>
        <c:if test="${loginUserType != USER_TYPE_SHOP }">
        <c:if test="${loginUserType != USER_TYPE_PROVIDER }">
        <div class="form-group">
          <label for="addProviderCode" class="col-sm-3 control-label"><span style="color:red">*</span> 所属连锁商</label>
          <div class="col-sm-9">
            <select id="addProviderCode" name="addProviderCode" onchange="fn_providerChange(this.value);" class="form-control">
              <option value="">--请选择--</option>
              <c:forEach items="${providerL }" var="item" varStatus="i">
                <option value="${item.code }" ${item.code == engineer.providerCode ? 'selected="selected"' : ''} >${item.name }</option>
              </c:forEach>
            </select>
          </div>
        </div>
        </c:if>
        <div class="form-group">
          <label for="addShopCode" class="col-sm-3 control-label"><span style="color:red">*</span> 所属门店商</label>
          <div class="col-sm-9" id="addShopCode" name="addShopCode">
              <c:forEach items="${shopL }" var="item" varStatus="i">
                <c:set var="ckd" value="${null }"></c:set>
                <c:forEach items="${engineer.shopCodes}" var="sc">
                  <c:if test="${item.code == sc.jsonCode }">
                    <c:set var="ckd" value="${1}"></c:set>
                  </c:if>
                </c:forEach>
                <label class="checkbox-inline" style="margin-left: 0px; margin-right: 10px;">
                  <input type="checkbox" name="addShopCode" value="${item.code }" ${ckd != null ? 'checked="checked"' : '' }> ${item.name }
                </label>
                <br/>
              </c:forEach>
          </div>
        </div>
        </c:if>
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
                addGender: {
                    validators : {
                        notEmpty : {
                            message : "不能为空"
                        }
                    }
                },
                addMobile: {
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
                addIdcard: {
                    validators : {
                        notEmpty : {
                            message : "不能为空"
                        },
                        regexp: {
                            regexp: /^(\d{6})(\d{4})(\d{2})(\d{2})(\d{3})([0-9]|X)$/,
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
                }
            }// end fields
        }).on("success.form.bv", function(e) {
            // 阻止表单提交
            e.preventDefault();
            //加载等待
            AlertText.tips("d_loading");
            //校验成功后的操作
            var btn = $("#addSaveBtn");
            //让按钮不能点击
            btn.button("loading");
            //遮盖层
            var options = {
                url : "${ctx}/engineer/update.do",
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


//点击保存按钮,提交form表单，触发校验
$("#addSaveBtn").click(function() {
    //格式化分类属性信息为JSON串
    $("#addSubmitBtn").click();
});

function fn_providerChange(proCode){
    if(proCode){
        $("#addShopCode option[value!='']").remove();
        $.ajax({
            url : "${ctx}/shop/queryByProCode.do",
            data: {proCode : proCode},
            type: "POST",
            dataType:"JSON",
            success:function(result){
                if(result.success){
                    $("#addShopCode").html("");
                    var html = "";
                    for( a in result.data)
                    {
                        <%--html +='<c:set var="ckd" value="${null }"></c:set>';--%>
                        <%--html +='<c:if test="${result.data[a]['code'] == shops.shop.shopCode}">';--%>
                        <%--html +='<c:set var="ckd" value="1"></c:set>';--%>
                        <%--html +='</c:if>';--%>
                        html +='<label class="checkbox-inline" style="margin-left: 0px; margin-right: 10px;">';
                        html +='<input type="checkbox" name="addShopCode" value="'+result.data[a]['code']+'" ${ckd != null ? 'checked="checked"' : '' }> '+result.data[a]['name']+'';
                        html +='</label>';
                        html +='<br/>';
//                        html +='<option value="'+result.data[a]['code']+'">'+result.data[a]['name']+'</option>';
                    }
                    $("#addShopCode").append(html);
                }else{
                    AlertText.tips("d_alert", "提示", "维修门店加载失败，请稍后再试！");
                }
            }
        });
    }
}

</script>