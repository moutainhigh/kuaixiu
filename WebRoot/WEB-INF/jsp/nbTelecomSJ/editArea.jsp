<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/commons/taglibs.jsp" %>
<div class="modal-backdrop fade in"></div>
<div class="modal-dialog" style="width: 700px;">
  <div class="modal-content">
    <div class="modal-title"><span>编辑走访人</span>
      <a href="javascript: void(0);" class="close" data-dismiss="modal" aria-label="Close">&times;</a>
    </div>
    <div class="modal-body">
      <form id="editForm" method="post" class="form-horizontal">
        <input type="hidden" name="offIceId" value="${nbArea.officeId }" />
        <div class="form-group">
          <label for="county" class="col-sm-3 control-label"><span style="color:red">*</span> 县分</label>
          <div class="col-sm-9">
            <input type="text" id="county" name="county" value="${nbArea.county }" class="form-control" placeholder="请输入县分">
          </div>
        </div>

        <div class="form-group">
          <label for="officeName" class="col-sm-3 control-label"><span style="color:red">*</span> 支局</label>
          <div class="col-sm-9">
            <input type="text" id="officeName" name="officeName" value="${nbArea.branchOffice }" class="form-control" placeholder="请输入支局">
          </div>
        </div>
        <div class="form-group">
          <label for="areaName" class="col-sm-3 control-label"><span style="color:red">*</span> 包区</label>
          <div class="col-sm-9">
            <input type="text" id="areaName" name="areaName" value="${nbArea.areaName }" class="form-control" placeholder="请输入包区">
          </div>
        </div>
        <div class="form-group">
          <label for="areaPerson" class="col-sm-3 control-label"><span style="color:red">*</span> 包区人</label>
          <div class="col-sm-9">
            <input type="text" id="areaPerson" name="areaPerson" value="${nbArea.areaPerson }" class="form-control" placeholder="请输入包区人">
          </div>
        </div>
        <div class="form-group">
          <label for="personTel" class="col-sm-3 control-label"><span style="color:red">*</span> 包区人手机号</label>
          <div class="col-sm-9">
            <input type="text" id="personTel" name="personTel" value="${nbArea.personTel }" class="form-control" placeholder="请输入包区人手机号">
          </div>
        </div>
        <button type="submit" class="hide" id="editSubmitBtn"></button>
      </form>
    </div>
    <div class="modal-footer">
      <button type="button" id="editSaveBtn" class="btn modal-btn" ><span class="am-icon-save icon-save"></span>提交</button>
      <button type="button" id="editMissBtn" class="btn modal-btn" data-dismiss="modal" aria-label="Close"><span class="am-icon-close icon-close"></span>取消</button>
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
    $("#editForm")
        .bootstrapValidator({
            message : "不能为空",
            feedbackIcons : {
                valid : 'glyphicon glyphicon-ok',
                invalid : 'glyphicon glyphicon-remove',
                validating : 'glyphicon glyphicon-refresh'
            },
            fields : {
                county: {
                    validators : {
                        notEmpty : {
                            message : "不能为空"
                        }
                    }
                },
                officeName: {
                    validators : {
                        notEmpty : {
                            message : "不能为空"
                        }
                    }
                },
                areaName: {
                    validators : {
                        notEmpty : {
                            message : "不能为空"
                        }
                    }
                },
                areaPerson: {
                    validators : {
                        notEmpty : {
                            message : "不能为空"
                        }
                    }
                },
                personTel: {
                    validators : {
                        notEmpty : {
                            message : "不能为空"
                        },
                        regexp: {
                            regexp: /^1[0-9]{10}$/,
                            message: '请输入正确格式'
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
            var btn = $("#editSaveBtn");
            //让按钮不能点击
            btn.button("loading");
            //遮盖层
            var options = {
                url : "${ctx}/nbTelecomSJ/updateNBArea.do",
                dataType : "JSON",
                success : function(data) {
                    if(data.success){
                        //保存成功,关闭窗口，刷新列表
                        refreshPage();
                        //全部更新完后关闭弹窗
                        $("#editMissBtn").click();
                        //重置表单数据
                        document.getElementById("editForm").reset();

                        }else{
                        //保存失败
                        alert(data.resultMessage);
                    }
                    addFormReset();
                },
                error : function() {
                    alert("系统异常，请稍后再试");
                    addFormReset();
                }
            }; // end options
            $("#editForm").ajaxSubmit(options);
        }); // end on("success.form.bv"
}

/**
 * 重置表单
 */
function addFormReset(){
    //重置表单验证
    $("#editForm").data("bootstrapValidator").resetForm();
    //让按钮重新能点击
    $("#editSaveBtn").button("reset");
    //隐藏等待
    AlertText.hide();
}


//点击保存按钮,提交form表单，触发校验
$("#editSaveBtn").click(function() {
    //格式化分类属性信息为JSON串
    $("#editSubmitBtn").click();
});

</script>