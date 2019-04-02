<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/commons/taglibs.jsp" %>
<div class="modal-backdrop fade in"></div>
<div class="modal-dialog" style="width: 700px;">
    <div class="modal-content">
        <div class="modal-title"><span>回访备注</span>
            <a href="javascript: void(0);" class="close" data-dismiss="modal" aria-label="Close">&times;</a>
        </div>
        <div class="modal-body">
            <form id="recordForm" method="post" class="form-horizontal">
                <div class="form-group">
                    <label for="note" class="col-sm-3 control-label"><span style="color:red">*</span> 加价券类型</label>
                    <div class="col-sm-6">
                        <select id="couponType" name="couponType" class="form-control">
                            <option value="">--请选择--</option>
                            <option value="1">20元通用优惠券</option>
                            <option value="2">30元屏幕优惠券</option>
                            <option value="3">50元屏幕优惠券</option>
                        </select>
                    </div>
                </div>
                <div class="form-group">
                    <input type="hidden" id="recordOrderId" name="recordOrderId">
                    <label for="note" class="col-sm-3 control-label"><span style="color:red">*</span> 回访备注</label>
                    <div class="col-sm-6">
                        <textarea name="note" id="note" cols="50" rows="10" placeholder="请写下回访备注"></textarea>
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
        var orderId = $("#recordOrderId").val();
        var note = $("#note").val();
        var couponType = $("#couponType").val();
        //加载等待
        var url_ = AppConfig.ctx + "/order/record.do";
        var data_ = {orderId: orderId, note: note,couponType:couponType};
        $.ajax({
            url: url_,
            data: data_,
            type: "POST",
            dataType: "json",
            success: function (result) {
                if (result.success) {
                    //重置表单数据
                    document.getElementById("recordForm").reset();
                    showOrderDetail(orderId);
                } else {
                    AlertText.tips("d_alert", "提示", result.resultMessage);
                    return false;
                }
                //隐藏等待
                AlertText.hide();
            }
        });
    });
</script>