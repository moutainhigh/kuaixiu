<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/commons/taglibs.jsp" %>
<div class="modal-backdrop fade in"></div>
<div class="modal-dialog">
    <div class="modal-content">
        <div class="modal-title"><span>创建售后订单</span>
            <a href="javascript: void(0);" class="close" data-dismiss="modal" aria-label="Close">&times;</a>
        </div>
        <div class="modal-body">
            <form id="reworkForm" method="post" class="form-horizontal">
                <input type="hidden" id="orderNo" name="orderNo"/>
                <div class="form-group">
                    <label for="reworkReason" class="col-sm-2 control-label"><span
                            style="color:red">*</span>选择原因</label>
                    <div class="col-sm-9">
                        <select id="reworkReason" style="width:400px" name="reworkReason" onchange="brandChange(this.value);"
                                class="form-control">
                            <option value="">--请选择--</option>
                            <option value="1">物料原因</option>
                            <option value="2">装配原因</option>
                            <option value="3">客户原因</option>
                        </select>
                    </div>
                    <br/><br/>
                    <label for="reasonDetail" class="col-sm-2 control-label"><span
                            style="color:red">*</span>原因详情</label>
                    <div class="col-sm-9">
                <textarea class="reasonDetail" name="reasonDetail" style="width:400px; height:100px" id="reason" placeholder="请写下您的原因吧！"
                          maxlength="1220"></textarea>
                    </div>
                </div>
                <button type="submit" class="hide" id="addSubmitReworkBtn"></button>
            </form>
        </div>
        <div class="modal-footer">
            <button type="button" id="addSaveReworkBtn" class="btn modal-btn"><span
                    class="am-icon-save icon-save"></span>提交
            </button>
            <button type="button" id="addMissReworkBtn" class="btn modal-btn" data-dismiss="modal" aria-label="Close">
                <span class="am-icon-close icon-close"></span>取消
            </button>
        </div>
    </div><!-- /.modal-content -->
</div>
<!-- /.modal-dialog -->


<script type="text/javascript">
    //表单验证
    $(document).ready(function () {
        insertValidatorForm();
    });

    //点击保存按钮,提交form表单，触发校验
    $("#addSaveReworkBtn").click(function () {
        //格式化分类属性信息为JSON串
        $("#addSubmitReworkBtn").click();
    });

    //初始化表单
    function insertValidatorForm() {
        $("#reworkForm")
            .bootstrapValidator({
                message: "不能为空",
                feedbackIcons: {
                    valid: 'glyphicon glyphicon-ok',
                    invalid: 'glyphicon glyphicon-remove',
                    validating: 'glyphicon glyphicon-refresh'
                },
                fields: {
                    reworkReason: {
                        validators: {
                            notEmpty: {
                                message: "不能为空"
                            }
                        }
                    },
                    reasonDetail: {
                        validators: {
                            notEmpty: {
                                message: "不能为空"
                            }
                        }
                    }
                }// end fields
            }).on("success.form.bv", function (e) {
            // 阻止表单提交
            e.preventDefault();
            //加载等待
            AlertText.tips("d_loading");
            //校验成功后的操作
            var btn = $("#addSaveReworkBtn");
            //让按钮不能点击
            btn.button("loading");
            //遮盖层
            var options = {
                url: "${ctx}/reworkOrder/endAddRework.do",
                dataType: "JSON",
                success: function (data) {
                    if (data.success) {
                        //保存成功,关闭窗口，刷新列表
                        //refreshPage();
                        addFormReset();
                        AlertText.tips("d_alert", "提示", "订单提交成功", function () {
                            func_reload_page("${ctx}/order/reworkOrderDetail.do?reworkNo=" + data.result.orderReworkNo);
                        });

                    } else {
                        addFormReset();
                        //保存失败
                        if(result.resultMessage==null){
                            AlertText.tips("d_alert", "提示", result.msg);
                        }else{
                            AlertText.tips("d_alert", "提示", result.resultMessage);
                        }
                    }
                },
                error: function () {
                    addFormReset();
                    AlertText.tips("d_alert", "提示", "系统异常，请稍后再试");
                }
            }; // end options
            $("#reworkForm").ajaxSubmit(options);
        }); // end on("success.form.bv"
    }

    /**
     * 重置表单
     */
    function addFormReset() {
        //重置表单验证
        $("#reworkForm").data("bootstrapValidator").resetForm();
        //让按钮重新能点击
        $("#addSaveReworkBtn").button("reset");
        //隐藏等待
        AlertText.hide();
    }

    var isLoading = false;

</script>