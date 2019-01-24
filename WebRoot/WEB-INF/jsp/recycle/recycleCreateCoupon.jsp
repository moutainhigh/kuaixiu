<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/commons/taglibs.jsp" %>
<link rel="stylesheet" href="${webResourceUrl}/resource/order/css/order.detail.css">
<script src="${webResourceUrl}/resource/js/address.js" type="text/javascript" charset="utf-8"></script>
<div class="am-cf am-padding am-padding-bottom-0">
    <div class="am-fl am-cf" style="width: 100%;">
        <strong class="am-text-primary am-text-lg"><a href="javascript:void(0);" onclick="toList();">加价券管理</a></strong>
        /
        <small>生成优惠券</small>
    </div>
</div>

<hr>

<div class="am-g">

    <form id="insertForm" method="post" class="form-horizontal">


        <div class="form-group" id="couponNames">
            <label for="couponName" class="col-sm-2 control-label"><span style="color:red">*</span> 加价券名称</label>
            <div class="col-sm-9">
                <input type="text" id="couponName" name="name" class="form-control" placeholder="请输入加价券名称">
            </div>
        </div>
        <div class="form-group" id="upbtractionPrices">
            <label for="couponPrice" class="col-sm-2 control-label"><span style="color:red">*</span> 加价券额度条件下限</label>
            <div class="col-sm-9">
                <input type="text" id="upbtractionPrice" name="subtractionPrice" class="form-control"
                       placeholder="请输入加价券额度条件下限">
            </div>
        </div>
        <div class="form-group" id="ruleDescriptions">
            <label for="couponPrice" class="col-sm-2 control-label"><span style="color:red">*</span> 加价券规则描述</label>
            <div class="col-sm-9">
                <input type="text" id="ruleDescription" name="ruleDescription" class="form-control"
                       placeholder="请输入加价券规则描述">
            </div>
        </div>
        <div class="form-group" id="pricingTypes">
            <label for="count" class="col-sm-2 control-label"><span style="color:red">*</span>优惠卷类型</label>
            <p></p>
            <label><input name="pricingType" type="radio" value="1" checked/>
                百分比加价卷
            </label>
            <label><input name="pricingType" type="radio" value="2"/>
                固定金额加价卷
            </label>
        </div>
        <div class="form-group" id="upperLimits">
            <label for="couponPrice" class="col-sm-2 control-label"><span style="color:red">*</span> 加价券额度条件上限</label>
            <div class="col-sm-9">
                <input type="text" id="upperLimit" name="upperLimit" class="form-control" placeholder="请输入加价券额度条件上限">
            </div>
        </div>
        <div class="form-group" id="couponPrices">
            <label for="couponPrice" class="col-sm-2 control-label"><span style="color:red">*</span>
                加价券金额。如果是百分比加价：无需输入百分号，输入分子即可</label>
            <div class="col-sm-9">
                <input type="text" id="couponPrice" name="price" class="form-control" placeholder="请输入加价券金额">
            </div>
        </div>
        <div class="form-group" id="validTimes">
            <input type="hidden" id="validBeginTime" name="startTime"/>
            <input type="hidden" id="validEndTime" name="endTime"/>
            <label for="validTime" class="col-sm-2 control-label"><span style="color:red">*</span> 有效时间</label>
            <div class="col-sm-9">
                <input type="text" id="validTime" class="form-control" placeholder="请选择加价券有效时间" readonly="readonly">
            </div>
        </div>
        <div class="form-group" id="numbers">
            <label for="count" class="col-sm-2 control-label"><span style="color:red">*</span> 生成数量</label>
            <div class="col-sm-9">
                <input type="text" id="count" name="number" class="form-control" maxlength="5"
                       placeholder="请输入本次生成数量，最大支持一次生成10000张加价券">
            </div>
        </div>


        <div class="form-group" id="notes">
            <label for="note" class="col-sm-2 control-label"> 备注</label>
            <div class="col-sm-9">
                <input type="text" id="note" name="note" class="form-control" placeholder="请输入备注信息">
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
    function toList() {
        func_reload_page("${ctx}/coupon/list.do");
    }
    $(function () {
        $('input:radio[name="pricingType"]').change(function () {
            var v = $(this).val();
            if (v == 2) {
                $("#upperLimits").hide();
            } else {
                $("#upperLimits").show();
            }
        });
    });

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
    }

    /**
     * 品牌全选
     */
    function checkAllBrands(obj) {
        $("input[name='addBrand']").each(function () {
            $(this).prop("checked", obj.checked);
        });
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
                    couponName: {
                        validators: {
                            notEmpty: {
                                message: "不能为空"
                            }
                        }
                    },
                    couponPrice: {
                        validators: {
                            notEmpty: {
                                message: "不能为空"
                            },
                            regexp: {
                                regexp: /^(?!0+(?:\.0+)?$)(?:[1-9]\d*|0)(?:\.\d{1,2})?$/,
                                message: '请输入正确格式，支持两位小数'
                            }
                        }
                    },
                    validTime: {
                        validators: {
                            notEmpty: {
                                message: "不能为空"
                            }
                        }
                    },
                    count: {
                        validators: {
                            notEmpty: {
                                message: "不能为空"
                            },
                            regexp: {
                                regexp: /^(([1-9]\d{0,3})|(10000))$/,
                                message: '请输入正确数量，最大支持一次生成10000张优惠券'
                            }
                        }
                    }
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
                url: "${ctx}/recycle/addCoupon.do",
                dataType: "JSON",
                success: function (data) {
                    if (data.success) {
                        //保存成功,关闭窗口，刷新列表
                        //refreshPage();
                        var html = "<div id='couponView'>已生成优惠券<span id='couponBatchId' count='" + data.count + "' batchId='" + data.batchId + "' style='color:red; font-size:22px; margin: 0 10px;'>" + data.count + "</span>个</div>";
                        AlertText.tips("d_alert", "正在生成优惠券。。。", html, function () {
                            func_reload_page("${ctx}/recycle/couponList.do");
                        });

                        reflashCount();
                    } else {
                        //保存失败
                        alert(data.msg);
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
     * 刷新个数
     */
    function reflashCount() {
        var batchId = $("#couponBatchId").attr("batchId");
        var url_ = AppConfig.ctx + "/recycle/reflashCount.do";
        var data_ = {batchId: batchId};
        $.ajax({
            url: url_,
            data: data_,
            type: "POST",
            dataType: "json",
            success: function (result) {
                if (result.success) {
                    $("#couponBatchId").html(result.count);
                    if (result.count >= $("#couponBatchId").attr("count")) {
                        var html = "已生成优惠券<span id='couponBatchId' count='" + result.count + "' batchId='" + batchId + "' style='color:red; font-size:22px; margin: 0 10px;'>" + result.count + "</span>个<br/>";
                        html += "优惠券生成完成！！！";
                        $("#couponView").html(html);
                    } else {
                        setTimeout("reflashCount()", 600);
                    }
                } else {
                    setTimeout("reflashCount()", 600);
                }
            }
        });
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

    //限制优惠卷类型
    $(function () {
        $("#tip").hide();
        $("#screen_tip").hide();
        $("input:radio[name=chooseCoupon]").change(function () {
            var choose = $('input:radio[name="chooseCoupon"]:checked').val();
            if (choose == 0) {    //维修
                $("#tip").hide();
                $("#screen_tip").hide();
                $("#supportBrands").show();
                $("#supportProjects").show();
                $("#prices").show();
            } else if (choose == 1) {
                $("#tip").show();
                $("#screen_tip").hide();
                $("input[name='addBrand']").each(function () {
                    this.checked = false;
                });
                $("input[name='addProject']").each(function () {
                    this.checked = false;
                });
                $("#supportBrands").hide();
                $("#supportProjects").hide();
                $("#prices").show();
            } else {
                $("#screen_tip").show();
                $("#tip").hide();
                $("#supportBrands").hide();
                $("#supportProjects").hide();
                $("#prices").hide();
                $("input[name='addBrand']").each(function () {
                    this.checked = false;
                });
                $("input[name='addProject']").each(function () {
                    this.checked = false;
                });
                $("#validTime").daterangepicker({
                    "startDate": getDateDayFormat(0),
                    "endDate": getDateDayFormat(365)
                }, function (start, end, label) {
                    $("#validBeginTime").val(start.format("YYYY-MM-DD"));
                    $("#validEndTime").val(end.format("YYYY-MM-DD"));
                });
            }

        });

    });

</script>