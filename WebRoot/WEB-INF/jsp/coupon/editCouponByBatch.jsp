<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/commons/taglibs.jsp" %>
<div class="modal-backdrop fade in"></div>
<div class="modal-dialog" style="width: 700px;">
    <div class="modal-content">
        <div class="modal-title"><span>按批次编辑优惠券</span>
            <a href="javascript: void(0);" class="close" data-dismiss="modal" aria-label="Close">&times;</a>
        </div>
        <div class="modal-body">
            <form id="insertForm" method="post" class="form-horizontal">
                <input type="hidden" name="id" value="${coupon.id }"/>
                <div class="form-group">
                    <label for="addBatchId" class="col-sm-2 control-label"> 优惠券批次</label>
                    <div class="col-sm-9">
                        <p class="form-control-static">${batchId}</p>
                        <input type="hidden" name="BatchId" value="${batchId }"/>
                    </div>
                </div>
                <div class="form-group">
                    <label for="addManagerName" class="col-sm-2 control-label"><span style="color:red">*</span>
                        优惠券名称</label>
                    <div class="col-sm-9">
                        <input type="text" id="couponName" name="couponName" value="${coupon.couponName }"
                               class="form-control" placeholder="请输入优惠券名称">
                    </div>
                </div>
                <div class="form-group">
                    <label for="addManagerMobile" class="col-sm-2 control-label"><span style="color:red">*</span> 优惠券金额</label>
                    <div class="col-sm-9">
                        <input type="text" id="couponPrice" name="couponPrice" value="${coupon.couponPrice }"
                               class="form-control" placeholder="请输入优惠券金额">
                    </div>
                </div>
                <div class="form-group" id="projects">
                    <input type="hidden" id="validBeginTime" name="validBeginTime" value="${coupon.beginTime}"/>
                    <input type="hidden" id="validEndTime" name="validEndTime" value="${coupon.endTime}"/>
                    <label for="validTime" class="col-sm-2 control-label"><span style="color:red">*</span> 有效时间</label>
                    <div class="col-sm-9">
                        <input type="text" id="validTime" value="${coupon.beginTime}-${coupon.endTime}"
                               class="form-control" placeholder="请选择优惠券有效时间" readonly="readonly">
                    </div>
                </div>
                <c:if test="${coupon.type==0 }">
                    <div class="form-group">
                        <label for="addBrand" class="col-sm-2 control-label">选择支持品牌</label>
                        <input id="checkAll_brand" type="hidden" name="checkAllBrand" value="${coupon.isBrandCurrency}"/>
                        <div class="col-sm-9">
                            <input id="check_all_brand" style="position: relative;top:6px;" ${coupon.isBrandCurrency == 1 ? 'checked="checked"' : '' }
                                   onclick="checkAllBrands(this)" type="checkbox"/><span
                                style="position: relative;top:5px;">全选</span>
                            <c:forEach items="${brandL }" var="item" varStatus="i">
                                <c:set var="ckd" value="${null }"></c:set>
                                <c:if test="${coupon.isBrandCurrency == 0}">
                                    <c:forEach items="${couModels}" var="sm">
                                        <c:if test="${item.id == sm.brandId }">
                                            <c:set var="ckd" value="${sm }"></c:set>
                                        </c:if>
                                    </c:forEach>
                                </c:if>
                                <c:if test="${coupon.isBrandCurrency == 1}">
                                    <c:set var="ckd" value="1"></c:set>
                                </c:if>
                                <label class="checkbox-inline" style="margin-left: 0px; margin-right: 10px;">
                                    <input type="checkbox" name="addBrand"
                                           value="${item.id }" ${ckd != null ? 'checked="checked"' : '' }> ${item.name }
                                </label>
                            </c:forEach>
                        </div>
                    </div>

                    <div class="form-group">
                        <label for="addBrand" class="col-sm-2 control-label">选择支持故障</label>
                        <input id="checkAll_project" type="hidden" name="checkAllProject" value="${coupon.isProjectCurrency}"/>
                        <div class="col-sm-9">
                            <input id="check_all_btn_2" style="position: relative;top:6px;" onclick="checkAll(this)"
                                ${coupon.isProjectCurrency == 1 ? 'checked="checked"' : '' }
                                   type="checkbox"/><span style="position: relative;top:5px;">全选</span>
                            <c:forEach items="${projectL }" var="item" varStatus="i">
                                <c:set var="ckd" value="${null }"></c:set>
                                <c:if test="${coupon.isProjectCurrency == 0}">
                                    <c:forEach items="${couProjects}" var="sm">
                                        <c:if test="${item.id == sm.projectId }">
                                            <c:set var="ckd" value="${sm }"></c:set>
                                        </c:if>
                                    </c:forEach>
                                </c:if>
                                <c:if test="${coupon.isProjectCurrency == 1}">
                                    <c:set var="ckd" value="1"></c:set>
                                </c:if>
                                <label class="checkbox-inline" style="margin-left: 0px; margin-right: 10px;">
                                    <input type="checkbox" name="addProject"
                                           value="${item.id }" ${ckd != null ? 'checked="checked"' : '' }> ${item.name }
                                </label>
                            </c:forEach>
                        </div>
                    </div>

                </c:if>
                <div class="form-group" id="projects">
                    <label for="note" class="col-sm-2 control-label"> 备注</label>
                    <div class="col-sm-9">
                        <input type="text" id="note" name="note" value="${coupon.note }" class="form-control"
                               placeholder="请输入备注信息">
                    </div>
                </div>

                <button type="submit" class="hide" id="addSubmitBtn"></button>
            </form>
        </div>
        <div class="modal-footer">
            <c:if test="${coupon.isUse == 0}">
                <button type="button" id="addSaveBtn" class="btn modal-btn"><span class="am-icon-save icon-save"></span>提交
                </button>
            </c:if>
            <button type="button" id="addMissBtn" class="btn modal-btn" data-dismiss="modal" aria-label="Close"><span
                    class="am-icon-close icon-close"></span>取消
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
        $("#insertForm")
            .bootstrapValidator({
                message: "不能为空",
                feedbackIcons: {
                    valid: 'glyphicon glyphicon-ok',
                    invalid: 'glyphicon glyphicon-remove',
                    validating: 'glyphicon glyphicon-refresh'
                },
                fields: {
                    addBatchId: {
                        validators: {
                            notEmpty: {
                                message: "不能为空"
                            }
                        }
                    },
                    addManagerName: {
                        validators: {
                            notEmpty: {
                                message: "不能为空"
                            }
                        }
                    },
                    addManagerMobile: {
                        validators: {
                            notEmpty: {
                                message: "不能为空"
                            },
                            regexp: {
                                regexp: /^1[0-9]{10}$/,
                                message: '请输入正确格式'
                            }
                        }
                    },
                    addManagerMobile1: {
                        validators: {
                            regexp: {
                                regexp: /^1[0-9]{10}$/,
                                message: '请输入正确格式'
                            }
                        }
                    },
                    addManagerMobile2: {
                        validators: {
                            regexp: {
                                regexp: /^1[0-9]{10}$/,
                                message: '请输入正确格式'
                            }
                        }
                    },
                    addTel: {
                        validators: {
                            notEmpty: {
                                message: "不能为空"
                            },
                            regexp: {
                                regexp: /^(\(\d{3,4}\)|\d{3,4}-|\s)?\d{7,8}$/,
                                message: '请输入正确格式'
                            }
                        }
                    },
                    addProviderCode: {
                        validators: {
                            notEmpty: {
                                message: "不能为空"
                            }
                        }
                    },
                    addAddress: {
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
            var btn = $("#addSaveBtn");
            //让按钮不能点击
            btn.button("loading");
            //遮盖层
            var options = {
                url: "${ctx}/coupon/updateByBatchId.do",
                dataType: "JSON",
                success: function (data) {
                    if (data.success) {
                        //保存成功,关闭窗口，刷新列表
                        refreshPage();
                        //全部更新完后关闭弹窗
                        $("#addMissBtn").click();
                        //重置表单数据
                        document.getElementById("insertForm").reset();
                    } else {
                        //保存失败
                        alert(data.msg);
                    }
                    addFormReset();
                },
                error: function () {
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
    function addFormReset() {
        //重置表单验证
        $("#insertForm").data("bootstrapValidator").resetForm();
        //让按钮重新能点击
        $("#addSaveBtn").button("reset");
        //隐藏等待
        AlertText.hide();
    }

    /**
     * 故障全选
     */
    function checkAll(obj) {
        $("input[name='addProject']").each(function () {
            $(this).prop("checked", obj.checked);
        });
        var isTrue = $("#check_all_btn_2").prop("checked");
        if (isTrue == true) {
            $('#checkAll_project').val(1);
        }
        if (isTrue == false) {
            $('#checkAll_project').val(0);
        }
    }

    /**
     * 品牌全选
     */
    function checkAllBrands(obj) {
        $("input[name='addBrand']").each(function () {
            $(this).prop("checked", obj.checked);
        });
        var isTrue = $("#check_all_brand").prop("checked");
        if (isTrue == true) {
            $('#checkAll_brand').val(1);
        }
        if (isTrue == false) {
            $('#checkAll_brand').val(0);
        }
    }

    //点击保存按钮,提交form表单，触发校验
    $("#addSaveBtn").click(function () {
        //格式化分类属性信息为JSON串
        $("#addSubmitBtn").click();
    });

</script>