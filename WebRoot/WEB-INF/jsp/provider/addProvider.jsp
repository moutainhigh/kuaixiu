<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/commons/taglibs.jsp" %>
<div class="modal-backdrop fade in"></div>
<div class="modal-dialog" style="width: 700px;">
  <div class="modal-content">
    <div class="modal-title"><span>新增连锁商</span>
      <a href="javascript: void(0);" class="close" data-dismiss="modal" aria-label="Close">&times;</a>
    </div>
    <div class="modal-body">
      <form id="insertForm" method="post" class="form-horizontal">
        <div class="form-group">
          <label for="addName" class="col-sm-3 control-label"><span style="color:red">*</span> 连锁商名称</label>
          <div class="col-sm-9">
            <input type="text" id="addName" name="addName" class="form-control" placeholder="请输入连锁商名称">
          </div>
        </div>
        <div class="form-group">
          <label for="addManagerName" class="col-sm-3 control-label"><span style="color:red">*</span> 负责人姓名</label>
          <div class="col-sm-9">
            <input type="text" id="addManagerName" name="addManagerName" class="form-control" placeholder="请输入负责人姓名">
          </div>
        </div>
        <div class="form-group">
          <label for="addManagerMobile" class="col-sm-3 control-label"><span style="color:red">*</span> 负责人手机号</label>
          <div class="col-sm-9">
            <input type="text" id="addManagerMobile" name="addManagerMobile" class="form-control" placeholder="请输入负责人手机号">
          </div>
        </div>
        <div class="form-group">
          <label for="addTel" class="col-sm-3 control-label"><span style="color:red">*</span> 连锁商电话号码</label>
          <div class="col-sm-9">
            <input type="text" id="addTel" name="addTel" class="form-control" placeholder="请输入连锁商电话号码">
          </div>
        </div>
        <div class="form-group">
          <label for="addAccountBank" class="col-sm-3 control-label"><span style="color:red">*</span> 连锁商开户银行</label>
          <div class="col-sm-9">
            <input type="text" id="addAccountBank" name="addAccountBank" class="form-control" placeholder="请输入连锁商开户银行">
          </div>
        </div>
        <div class="form-group">
          <label for="addAccountBankBranch" class="col-sm-3 control-label"><span style="color:red">*</span> 连锁商开户银行支行</label>
          <div class="col-sm-9">
            <input type="text" id="addAccountBankBranch" name="addAccountBankBranch" class="form-control" placeholder="请输入连锁商开户银行支行">
          </div>
        </div>
        <div class="form-group">
          <label for="addAccountName" class="col-sm-3 control-label"><span style="color:red">*</span> 连锁商开户户名</label>
          <div class="col-sm-9">
            <input type="text" id="addAccountName" name="addAccountName" class="form-control" placeholder="请输入连锁商开户户名">
          </div>
        </div>
        <div class="form-group">
          <label for="addAccountNumber" class="col-sm-3 control-label"><span style="color:red">*</span> 连锁商开户账号</label>
          <div class="col-sm-9">
            <input type="text" id="addAccountNumber" name="addAccountNumber" class="form-control" placeholder="请输入连锁商开户账号">
          </div>
        </div>
        <div class="form-group">
          <label for="addProportion" class="col-sm-3 control-label"><span style="color:red">*</span> 手续费扣除比例</label>
          <div class="col-sm-9">
            <div class="input-group">
              <input type="text" id="addProportion" name="addProportion" class="form-control" placeholder="请输入手续费扣除比例">
              <span class="input-group-addon">%</span>
            </div>
          </div>
        </div>
        <div class="form-group">
          <input type="hidden" id="addAreas" name="addAreas" >
          <label for="addArea" class="col-sm-3 control-label"><span style="color:red">*</span> 地址</label>
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
          <div class="col-sm-9 col-sm-offset-3">
            <input type="text" id="addAddress" name="addAddress" class="form-control" placeholder="请输入详细地址">
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
$(document).ready(function(){
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
                addAccountBank: {
                    validators : {
                        notEmpty : {
                            message : "不能为空"
                        }
                    }
                },
                addAccountBankBranch: {
                    validators : {
                        notEmpty : {
                            message : "不能为空"
                        }
                    }
                },
                addAccountName: {
                    validators : {
                        notEmpty : {
                            message : "不能为空"
                        }
                    }
                },
                addAccountNumber: {
                    validators : {
                        notEmpty : {
                            message : "不能为空"
                        },
                        regexp: {
                            regexp: /^[1-9]\d*$/,
                            message: '请输入正确格式'
                        }
                    }
                },
                addProportion: {
                    validators : {
                        notEmpty : {
                            message : "不能为空"
                        },
                        regexp: {
                            regexp: /^([1-9]\d*(?:\.\d+)?$)|(?:^0\.\d*[1-9]\d*)$/,
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
                url : "${ctx}/provider/save.do",
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

</script>