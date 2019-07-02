<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/commons/taglibs.jsp" %>
<div class="modal-backdrop fade in"></div>
<div class="modal-dialog" style="width: 700px;">
  <div class="modal-content">
    <div class="modal-title"><span>编辑短信模板</span>
      <a href="javascript: void(0);" class="close" data-dismiss="modal" aria-label="Close">&times;</a>
    </div>
    <div class="modal-body">
      <form id="editForm" method="post" class="form-horizontal">
        <input type="hidden" name="id" value="${groupMobileSms.id }"/>
        <div class="form-group" id="nameLabels">
          <label for="nameLabel" class="col-sm-2 control-label"><span style="color:red">*</span>模板名称</label>
          <div class="col-sm-9">
            <input type="text" id="nameLabel" name="nameLabel" value="${groupMobileSms.nameLabel}" class="form-control" placeholder="请输入模板名称">
          </div>
        </div>

        <div class="form-group" id="couponNames">
          <label for="smsTemplate" class="col-sm-2 control-label"><span style="color:red">*</span> 模板</label>
          <div class="col-sm-9">
            <input type="text" id="smsTemplate" name="smsTemplate" value="${groupMobileSms.smsTemplate}" class="form-control" placeholder="请输入短信模板">
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
    $(document).ready(function () {
        insertValidatorForm();
    });


    //点击保存按钮,提交form表单，触发校验
    $("#editSaveBtn").click(function () {
        //格式化分类属性信息为JSON串
        $("#editSubmitBtn").click();
    });
    //console.log("d");
    //初始化表单
    function insertValidatorForm() {
        $("#editForm")
            .bootstrapValidator({
                message: "不能为空",
                feedbackIcons: {
                    valid: 'glyphicon glyphicon-ok',
                    invalid: 'glyphicon glyphicon-remove',
                    validating: 'glyphicon glyphicon-refresh'
                },
                fields: {

                }// end fields
            }).on("success.form.bv", function (e) {
            // 阻止表单提交
            e.preventDefault();
            // 验证颜色是否添加
            //console.log("ddd");
            //加载等待
            AlertText.tips("d_loading");
            //校验成功后的操作
            var btn = $("#editSaveBtn");
            //让按钮不能点击
            btn.button("loading");
            //遮盖层
            var options = {
                url: "${ctx}/groupShort/editSms.do",
                dataType: "JSON",
                success: function (data) {
                    if (data.success) {
                        AlertText.tips("d_alert", "提示", "修改成功");
                        refreshPage();
                        //全部更新完后关闭弹窗
                        $("#editMissBtn").click();
                        //重置表单数据
                        document.getElementById("editForm").reset();
                    } else {
                        //保存失败
                        AlertText.tips("d_alert", "提示", data.resultMessage, function () {
                            addFormReset();
                        });
                    }
                },
                error: function () {
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
    function addFormReset() {
        //重置表单验证
        $("#editForm").data("bootstrapValidator").resetForm();
        //让按钮重新能点击
        $("#editSaveBtn").button("reset");
        //隐藏等待
        AlertText.hide();
    }


</script>