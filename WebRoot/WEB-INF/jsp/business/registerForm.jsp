<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/commons/taglibs.jsp" %>
<div class="modal-backdrop fade in"></div>
<div class="modal-dialog" style="width: 700px;">
    <div class="modal-content">
        <div class="modal-title"><span>录单</span>
            <a href="javascript: void(0);" class="close" data-dismiss="modal" aria-label="Close">&times;</a>
        </div>
        <div class="modal-body">
            <form id="insertRegisterForm" method="post" class="form-horizontal">
                <input type="hidden" id="orderId" name="orderId" value="${orderId}"/><br/>

                <div class="form-group">
                    <label for="mealId" class="col-sm-3 control-label"><span style="color:red">*</span> 套餐名字</label>
                    <div class="col-sm-9">
                        <select id="mealId" name="mealId" onchange="registerFormChange(1,this.value);"
                                class="form-control">
                            <option value="">--请选择--</option>
                            <c:forEach items="${registerForms }" var="item" varStatus="i">
                                <option value="${item.mealId }">${item.mealName }</option>
                            </c:forEach>
                        </select>
                    </div>
                </div>
                <div class="form-group">
                    <label for="modelId" class="col-sm-3 control-label"><span style="color:red">*</span>
                        监控型号/无线wifi</label>
                    <div class="col-sm-9">
                        <select id="modelId" name="modelId" onchange="registerFormChange(2,this.value);"
                                class="form-control">
                            <option value="">--请选择--</option>
                        </select>
                    </div>
                </div>
                <div class="form-group">
                    <label for="modelNum" class="col-sm-3 control-label"><span style="color:red">*</span> 监控型号/无线wifi 个数</label>
                    <div class="col-sm-9">
                        <input type="text" id="modelNum" name="modelNum" class="form-control">
                    </div>
                </div>
                <div class="form-group">
                    <label for="poeId" class="col-sm-3 control-label"><span style="color:red">*</span> POE</label>
                    <div class="col-sm-9">
                        <select id="poeId" name="poeId" onchange="registerFormChange(3,this.value);"
                                class="form-control">
                            <option value="">--请选择--</option>
                        </select>
                    </div>
                </div>
                <div class="form-group">
                    <label for="poeNum" class="col-sm-3 control-label"><span style="color:red">*</span> POE 个数</label>
                    <div class="col-sm-9">
                        <input type="text" id="poeNum" name="poeNum" class="form-control">
                    </div>
                </div>
                <div class="form-group">
                    <label for="storageId" class="col-sm-3 control-label"><span style="color:red">*</span> 存储/NET/网关/路由</label>
                    <div class="col-sm-9">
                        <select id="storageId" name="storageId" class="form-control">
                            <option value="">--请选择--</option>
                        </select>
                    </div>
                </div>
                <div class="form-group">
                    <label for="storageNum" class="col-sm-3 control-label"><span style="color:red">*</span> 存储/NET/网关/路由
                        个数</label>
                    <div class="col-sm-9">
                        <input type="text" id="storageNum" name="storageNum" class="form-control">
                    </div>
                </div>
                <button type="submit" class="hide" id="addRegisterFormSubmitBtn"></button>
            </form>
        </div>
        <div class="modal-footer">
            <button type="button" id="addRegisterFormSaveBtn" class="btn modal-btn"><span
                    class="am-icon-save icon-save"></span>提交
            </button>
            <button type="button" id="addRegisterFormMissBtn" class="btn modal-btn" data-dismiss="modal" aria-label="Close">
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

    //初始化表单
    function insertValidatorForm() {
        $("#insertRegisterForm")
            .bootstrapValidator({
                message: "不能为空",
                feedbackIcons: {
                    valid: 'glyphicon glyphicon-ok',
                    invalid: 'glyphicon glyphicon-remove',
                    validating: 'glyphicon glyphicon-refresh'
                },
                fields: {
                    mealId: {
                        validators: {
                            notEmpty: {
                                message: "不能为空"
                            }
                        }
                    },
                    modelId: {
                        validators: {
                            notEmpty: {
                                message: "不能为空"
                            }
                        }
                    },
                    modelNum: {
                        validators: {
                            notEmpty: {
                                message: "不能为空"
                            }
                        }
                    },
                    poeId: {
                        validators: {
                            notEmpty: {
                                message: "不能为空"
                            }
                        }
                    },
                    poeNum: {
                        validators: {
                            notEmpty: {
                                message: "不能为空"
                            }
                        }
                    },
                    storageId: {
                        validators: {
                            notEmpty: {
                                message: "不能为空"
                            }
                        }
                    },
                    storageNum: {
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
            var btn = $("#addRegisterFormSaveBtn");
            //让按钮不能点击
            btn.button("loading");
            //遮盖层
            var options = {
                url: "${ctx}/sj/order/setRegisterForm.do",
                dataType: "JSON",
                success: function (data) {
                    if (data.success) {
                        //保存成功,关闭窗口，刷新列表
                        refreshPage();
                        //全部更新完后关闭弹窗
                        $("#addRegisterFormMissBtn").click();
                        $("#orderDetailApprovalButton").attr("disabled", false);
                        //重置表单数据
                        document.getElementById("insertRegisterForm").reset();
                    } else {
                        //保存失败
                        alert(data.resultMessage);
                    }
                    addFormReset();
                },
                error: function () {
                    alert("系统异常，请稍后再试");
                    addFormReset();
                }
            }; // end options
            $("#insertRegisterForm").ajaxSubmit(options);
        }); // end on("success.form.bv"
    }

    /**
     * 重置表单
     */
    function addFormReset() {
        //重置表单验证
        $("#insertRegisterForm").data("bootstrapValidator").resetForm();
        //让按钮重新能点击
        $("#addRegisterFormSaveBtn").button("reset");
        //隐藏等待
        AlertText.hide();
    }


    //点击保存按钮,提交form表单，触发校验
    $("#addRegisterFormSaveBtn").click(function () {
        //格式化分类属性信息为JSON串
        $("#addRegisterFormSubmitBtn").click();
    });

    function registerFormChange(type, id) {
        var body = null;
        if (type) {
            if (type == 1) {
                $("#modelId option[value!='']").remove();
                $("#poeId option[value!='']").remove();
                $("#storageId option[value!='']").remove();
                body = {mealId: id};
            } else if (type == 2) {
                $("#poeId option[value!='']").remove();
                $("#storageId option[value!='']").remove();
                var mealId = $('#mealId option:selected').val();
                body = {modelId: id, mealId: mealId};
            } else if (type == 3) {
                $("#storageId option[value!='']").remove();
                var mealId = $('#mealId option:selected').val();
                var modelId = $('#modelId option:selected').val();
                body = {poeId: id, mealId: mealId, modelId: modelId};
            }
            if (id) {
                var url = AppConfig.ctx + "/sj/order/getRegisterForm.do";
                $.get(url, body, function (result) {
                    if (!result.success) {
                        return false;
                    }
                    var json = result.result;
                    var select_html = '';
                    if (json.length > 0) {
                        for (a in json) {
                            if (type == 1) {
                                select_html += '<option value="' + json[a]['modelId'] + '">' + json[a]['modelName'] + '</option>';
                            } else if (type == 2) {
                                select_html += '<option value="' + json[a]['poeId'] + '">' + json[a]['poeName'] + '</option>';
                            } else if (type == 3) {
                                select_html += '<option value="' + json[a]['storageId'] + '">' + json[a]['storageName'] + '</option>';
                            }
                        }
                    }
                    if (type == 1) {
                        $("#modelId").append(select_html);
                    } else if (type == 2) {
                        $("#poeId").append(select_html);
                    } else if (type == 3) {
                        $("#storageId").append(select_html);
                    }
                });
            }
        }

    }
</script>