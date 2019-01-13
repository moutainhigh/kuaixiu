<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/commons/taglibs.jsp" %>
<link rel="stylesheet" href="${webResourceUrl}/resource/order/css/order.detail.css">
<script src="${webResourceUrl}/resource/js/address.js" type="text/javascript" charset="utf-8"></script>
<div class="am-cf am-padding am-padding-bottom-0">
    <div class="am-fl am-cf" style="width: 100%;">
        <strong class="am-text-primary am-text-lg"><a href="javascript:void(0);" onclick="toList();">订单管理</a></strong> /
        <small>快速下单</small>
    </div>
</div>

<hr>

<div class="am-g">
    <form id="insertForm" method="post" class="form-horizontal">

        <div class="form-group">
            <label for="reworkReason" class="col-sm-2 control-label"><span style="color:red">*</span>选择原因</label>
            <div class="col-sm-9">
                <select id="reworkReason" name="reworkReason" onchange="brandChange(this.value);" class="form-control">
                    <option value="">--请选择--</option>
                    <option value="1">物料原因</option>
                    <option value="2">装配原因</option>
                    <option value="3">客户原因</option>
                </select>
            </div>
        </div>
        <input type="hidden" id="orderNo" value="${orderNo}"/>
        <div class="form-group" id="projects">
            <label for="reasonDetail" class="col-sm-2 control-label"><span style="color:red">*</span>原因详情</label>
            <div class="col-sm-9">
                <textarea class="reasonDetail" name="reasonDetail" id="reason" placeholder="请写下您的原因吧！"
                          maxlength="220"></textarea>
            </div>
        </div>

        <div class="form-group">
            <div class="col-sm-9 col-sm-offset-2">
                <button id="addSaveBtn" type="button" class="btn btn-default fl" style="padding: 6px 80px;">保 存</button>
            </div>
        </div>
        <button type="submit" class="hide" id="addSubmitBtn"></button>
    </form>
</div>
<!-- /am-g -->


<script type="text/javascript">
    function toList() {
        func_reload_page("${ctx}/order/list.do");
    }

    //表单验证
    $(document).ready(function () {
        insertValidatorForm();
    });

    //点击保存按钮,提交form表单，触发校验
    $("#addSaveBtn").click(function () {
        //格式化分类属性信息为JSON串
        $("#addSubmitBtn").click();
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
            var btn = $("#addSaveBtn");
            //让按钮不能点击
            btn.button("loading");
            //遮盖层
            var options = {
                url: "${ctx}/reworkOrder/addRework.do",
                dataType: "JSON",
                success: function (data) {
                    if (data.success) {
                        //保存成功,关闭窗口，刷新列表
                        //refreshPage();
                        AlertText.tips("d_alert", "提示", "订单提交成功", function () {
                            func_reload_page("${ctx}/order/detail.do?id=" + data.data);
                        });
                    } else {
                        addFormReset();
                        //保存失败
                        AlertText.tips("d_alert", "提示", data.msg);
                    }
                },
                error: function () {
                    addFormReset();
                    AlertText.tips("d_alert", "提示", "系统异常，请稍后再试");
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

    var isLoading = false;

    $(function () {
        $("input:radio[name=repairType]").change(function () {
            var choose = $('input:radio[name="repairType"]:checked').val();
            if (choose == 0) {
                //上门维修
                $("#sendAddress").hide();
            }

        });

    });

</script>