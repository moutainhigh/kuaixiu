<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/commons/taglibs.jsp" %>
<link rel="stylesheet" href="${webResourceUrl}/resource/order/css/order.detail.css">
<script src="${webResourceUrl}/resource/js/address.js" type="text/javascript" charset="utf-8"></script>
<div class="am-cf am-padding am-padding-bottom-0">
    <div class="am-fl am-cf" style="width: 100%;">
        <strong class="am-text-primary am-text-lg"><a href="javascript:void(0);" onclick="toList();">回收管理</a></strong>
        /
        <small>创建加价券规则</small>
    </div>
</div>

<hr>

<div class="am-g">

    <form id="insertForm" method="post" class="form-horizontal">
        <div class="form-group" id="nameLabels">
            <label for="couponName" class="col-sm-2 control-label"><span style="color:red">*</span> 规则名称</label>
            <div class="col-sm-9">
                <input type="text" id="nameLabel" name="nameLabel" class="form-control" placeholder="请输入优惠券名称">
            </div>
        </div>

        <div class="form-group" id="couponNames">
            <label for="couponName" class="col-sm-2 control-label"><span style="color:red">*</span> 加价券名称</label>
            <div class="col-sm-9">
                <input type="text" id="couponName" name="couponName" class="form-control" placeholder="请输入优惠券名称">
            </div>
        </div>
        <div class="form-group">
            <label for="pricingTypes" class="col-sm-2 control-label"><span style="color:red"></span>加价券类型</label>
            <div class="col-sm-9">
                <div class="oldToNew">
                    <input name="pricingType" type="radio"  value="1"checked/>百分比加价
                    &nbsp&nbsp&nbsp&nbsp
                    <input name="pricingType" type="radio"  value="2"/>固定加价
                </div>
            </div>
        </div>
        <div class="form-group" id="subtractionPrices">
            <label for="subtractionPrice" class="col-sm-2 control-label"><span style="color:red">*</span> 加价券金额</label>
            <div class="col-sm-9">
                <input type="text" id="subtractionPrice" name="subtractionPrice" class="form-control" placeholder="请输入优惠券金额">
            </div>
        </div>

        <div class="form-group" id="upperLimits">
            <label for="upperLimit" class="col-sm-2 control-label"> 订单金额上限</label>
            <div class="col-sm-9">
                <input type="text" id="upperLimit" name="upperLimit" class="form-control" placeholder="订单金额上限">
            </div>
        </div>
        <div class="form-group" id="addPriceUppers">
            <label for="upperLimit" class="col-sm-2 control-label"> 加价额度上限</label>
            <div class="col-sm-9">
                <input type="text" id="addPriceUpper" name="addPriceUpper" class="form-control" placeholder="订单金额上限">
            </div>
        </div>
        <div class="form-group" id="prices">
            <label for="upperLimit" class="col-sm-2 control-label"> 加价额度</label>
            <div class="col-sm-9">
                <input type="text" id="price" name="price" class="form-control" placeholder="订单金额上限">
            </div>
        </div>
        <div class="form-group" id="ruleDescriptions">
            <label for="upperLimit" class="col-sm-2 control-label"> 规则描述</label>
            <div class="col-sm-9">
                <input type="text" id="ruleDescription" name="ruleDescription" class="form-control" placeholder="订单金额上限">
            </div>
        </div>
        <div class="form-group" id="validBeginTimes">
            <input type="hidden" id="validBeginTime" name="validBeginTime"/>
            <input type="hidden" id="validEndTime" name="validEndTime"/>
            <label for="validTime" class="col-sm-2 control-label"><span style="color:red">*</span> 有效时间</label>
            <div class="col-sm-9">
                <input type="text" id="validTime" class="form-control" placeholder="请选择优惠券有效时间" readonly="readonly">
            </div>
        </div>

        <div class="form-group" id="projects">
            <label for="upperLimit" class="col-sm-2 control-label"> 备注</label>
            <div class="col-sm-9">
                <input type="text" id="note" name="note" class="form-control" placeholder="备注">
            </div>
        </div>
        <div class="form-group">
            <div class="col-sm-9 col-sm-offset-2">
                <button id="addSaveBtn" type="button" class="btn btn-default fl" style="padding: 6px 80px;">保 存</button>
                <span class="form-control-static form-control-inline pull-left ml20"></span>
                <span class="form-control-static form-control-inline pull-left ml20"></span>
                <span class="form-control-static form-control-inline pull-left ml20"></span>
            </div>
        </div>
        <button type="submit" class="hide" id="addSubmitBtn"></button>
    </form>
</div>
<!-- /am-g -->


<script type="text/javascript">

    //初始化时间选择控件
    $("#validTime").daterangepicker({
        "startDate": getDateDayFormat(1),
        "endDate": getDateDayFormat(181)
    }, function (start, end, label) {
        $("#validBeginTime").val(start.format("YYYY-MM-DD"));
        $("#validEndTime").val(end.format("YYYY-MM-DD"));
    });

    //表单验证
    $(document).ready(function () {
        insertValidatorForm();
        $("#validBeginTime").val(getDateDayFormat(1));
        $("#validEndTime").val(getDateDayFormat(181));
    });

    /**
     * 故障全选
     */
    function checkAll(obj) {
        $("input[name='addProject']").each(function () {
            $(this).prop("checked", obj.checked);
        });
        var isTrue = $("#check_all_btn").prop("checked");
        if (isTrue == true) {
            $('#checkAll_project').val(1);
        }
        if (isTrue == false) {
            $('#checkAll_project').val(0);
        }

    }


    //点击保存按钮,提交form表单，触发校验
    $("#addSaveBtn").click(function () {
        //格式化分类属性信息为JSON串
        $("#addSubmitBtn").click();
    });
    //console.log("d");
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

                }// end fields
            }).on("success.form.bv", function (e) {
            // 阻止表单提交
            e.preventDefault();
            // 验证颜色是否添加
            //console.log("ddd");
            //加载等待
            AlertText.tips("d_loading");
            //校验成功后的操作
            var btn = $("#addSaveBtn");
            //让按钮不能点击
            btn.button("loading");
            //遮盖层
            var options = {
                url: "${ctx}/hsActivity/addCouponRole.do",
                dataType: "JSON",
                success: function (data) {
                    if (data.success) {
                        AlertText.tips("d_alert", "提示", "创建成功");
                    } else {
                        //保存失败
                        alert(data.resultMessage);
                        addFormReset();
                    }
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

</script>