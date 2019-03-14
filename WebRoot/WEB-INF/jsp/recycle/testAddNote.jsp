<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/commons/taglibs.jsp" %>
<div class="modal-backdrop fade in"></div>
<div class="modal-dialog" style="width: 700px;">
    <div class="modal-content">
        <div class="modal-title"><span>新增门店商</span>
            <a href="javascript: void(0);" class="close" data-dismiss="modal" aria-label="Close">&times;</a>
        </div>
        <div class="modal-body">
            <form id="insertForm" method="post" class="form-horizontal">
                <div class="form-group">
                    <input type="hidden" id="checkItemsId" name="checkItemsId">
                    <label for="note" class="col-sm-3 control-label"><span style="color:red">*</span> 回访备注</label>
                    <div class="col-sm-9">
                        <textarea id="note" name="note" placeholder="请写下回访备注！" maxlength="220"></textarea>
                    </div>
                </div>

                <%--<button type="submit" class="hide" id="addSubmitBtn"></button>--%>
            </form>
        </div>
        <div class="modal-footer">
            <button type="button" id="addNoteSaveBtn" class="btn modal-btn"><span class="am-icon-save icon-save"></span>提交
            </button>
            <button type="button" id="addNoteMissBtn" class="btn modal-btn" data-dismiss="modal" aria-label="Close">
                <span class="am-icon-close icon-close"></span>取消
            </button>
        </div>
    </div><!-- /.modal-content -->
</div>
<!-- /.modal-dialog -->

<script type="text/javascript">


    //点击保存按钮,提交form表单，触发校验
    $("#addNoteSaveBtn").click(function () {
        var id = $("#checkItemsId").val();
        var note = $("#note").val();
        //加载等待
        var url_ = AppConfig.ctx + "/recycle/saveTestNote.do";
        var data_ = {id: id, note: note};
        $.ajax({
            url: url_,
            data: data_,
            type: "POST",
            dataType: "json",
            success: function (result) {
                if (result.success) {
                    //保存成功,关闭窗口，刷新列表
                    refreshPage();
                } else {
                    AlertText.tips("d_alert", "提示", result.msg);
                    return false;
                }
                //隐藏等待
                AlertText.hide();
            }
        });
    });
    })
    ;
</script>