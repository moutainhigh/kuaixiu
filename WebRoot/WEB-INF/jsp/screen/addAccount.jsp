<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/commons/taglibs.jsp" %>
<div class="modal-backdrop fade in"></div>
<div class="modal-dialog">
  <div class="modal-content">
    <div class="modal-title"><span>新增用户</span>
      <a href="javascript: void(0);" class="close" data-dismiss="modal" aria-label="Close">&times;</a>
    </div>
    <div class="modal-body">
      <form id="insertForm" method="post" class="form-horizontal">
      
        <div class="form-group">
          <label for="addAccount" class="col-sm-3 control-label"><span style="color:red">*</span> 账号</label>
          <div class="col-sm-9">
            <input type="text" id="addAccount" name="addAccount" class="form-control" placeholder="请输入账号">
          </div>
        </div>
        
        <div class="form-group">
          <label for="addPassword" class="col-sm-3 control-label"><span style="color:red">*</span> 密码</label>
          <div class="col-sm-9">
            <input type="text" id="addPassword" name="addPassword" maxlength="11" class="form-control" placeholder="请输入密码">
          </div>
        </div>
        
         <div class="form-group">
          <label for="addName" class="col-sm-3 control-label"><span style="color:red">*</span> 姓名</label>
          <div class="col-sm-9">
            <input type="text" id="addName" name="addName" maxlength="11" class="form-control" placeholder="请输入姓名">
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
                addName: {
                    validators : {
                        notEmpty : {
                            message : "不能为空"
                        }
                    }
                },
                addPassword: {
                    validators : {
                        notEmpty : {
                            message : "不能为空"
                        }
                    }
                },
                addAccount: {
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
                url : "${ctx}/screen/account/save.do",
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