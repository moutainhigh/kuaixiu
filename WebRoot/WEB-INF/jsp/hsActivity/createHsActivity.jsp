<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/commons/taglibs.jsp" %>
<div class="modal-backdrop fade in"></div>
<div class="modal-dialog" style="width: 700px;">
    <div class="modal-content">
        <div class="modal-title"><span>新增回收加价券活动</span>
            <a href="javascript: void(0);" class="close" data-dismiss="modal" aria-label="Close">&times;</a>
        </div>
        <div class="modal-body">
            <form id="insertForm" method="post" class="form-horizontal" enctype="multipart/form-data">
                <div class="form-group">
                    <label for="sources" class="col-sm-2 control-label"><span style="color:red">*</span> 来源</label>
                    <div class="col-sm-9">
                        <c:forEach items="${recycleSystems }" var="item" varStatus="i">
                            <label class="checkbox-inline" style="margin-left: 0px; margin-right: 10px;">
                                <input type="checkbox" name="source" value="${item.id }"> ${item.name }
                            </label>
                        </c:forEach>
                    </div>
                </div>
                <div class="form-group">
                    <label class="col-sm-2 control-label"><span style="color:red">*</span>上传头图</label>
                    <input class="col-sm-9" type="file" style="width:420px" name="headFile" id="headFile"
                           accept="image/*"
                           onchange="imgChange(this);"/>
                    <!--文件上传选择按钮-->
                    <div id="preview" hidden="hidden" class="col-sm-9">
                        <img id="imghead" src="" width="260" height="180"/> <!--图片显示位置-->
                    </div>
                </div>
                <div class="form-group">
                    <label class="col-sm-7 control-label">（图片尺寸为750*532大小控制200k以下）</label><!--图片显示位置-->
                </div>
                <div class="form-group">
                    <label class="col-sm-2 control-label"><span style="color:red">*</span>上传加价券图</label>
                    <input class="col-sm-9" type="file" type="file" style="width:420px" name="centerFile"
                           id="centerFile" accept="image/*"
                           onchange="imgCenterChange(this);"/>
                    <!--文件上传选择按钮-->
                    <div id="previewCenter" hidden="hidden" class="col-sm-9">
                        <img id="imgCenter" src="" width="260" height="180"/> <!--图片显示位置-->
                    </div>
                </div>
                <div class="form-group">
                    <label class="col-sm-7 control-label">（图片尺寸为750*532大小控制200k以下）</label><!--图片显示位置-->
                </div>

                <div class="form-group">
                    <label class="col-sm-2 control-label"><span style="color:red">*</span>加价券图片色值</label>
                    <div class="col-sm-9">
                        <input type="text" id="centercolorValue" style="width:400px;" name="centercolorValue"
                               class="form-control"
                               placeholder="加价券图片色值">
                    </div>
                </div>
                <div class="form-group">
                    <label class="col-sm-2 control-label"><span style="color:red">*</span>活动结束时间</label>
                    <div class="am-datepicker-date col-sm-9">
                        <input type="text" style="width:400px;" id="endTime" name="actvityEndTime"
                               class="form-control am-datepicker-end" data-am-datepicker readonly>
                    </div>
                </div>
                <div class="form-group">
                    <label class="col-sm-2 control-label"><span style="color:red">*</span>活动规则</label>
                    <div class="col-sm-9" id="activityRole">
                        <input style="width:400px;" type="text" id="activityRoles" name="activityRoles"
                               class="form-control"
                               placeholder="活动规则">
                    </div>
                </div>
                <div class="form-group">
                    <div class="col-sm-9 col-sm-offset-2">
                        <button onclick="addRole();" class="am-btn am-btn-default" type="button">增添规则</button>
                    </div>
                </div>
                <div class="form-group">
                    <label for="addBrand" class="col-sm-2 control-label"><span style="color:red">*</span>选择加价券</label>
                    <div class="col-sm-9">
                        <c:forEach items="${couponRoles }" var="item" varStatus="i">
                            <label class="checkbox-inline" style="margin-left: 0px; margin-right: 10px;">
                                <input type="checkbox" name="couponRoles" value="${item.id }"> ${item.nameLabel }
                            </label>
                        </c:forEach>
                    </div>
                </div>

                <button type="submit" class="hide" id="addSubmitBtn"></button>
            </form>
        </div>
        <div class="modal-footer">
            <button type="button" id="addSaveBtn" class="btn modal-btn"><span class="am-icon-save icon-save"></span>提交
            </button>
            <button type="button" id="addMissBtn" class="btn modal-btn" data-dismiss="modal" aria-label="Close"><span
                    class="am-icon-close icon-close"></span>取消
            </button>
        </div>
    </div><!-- /.modal-content -->
</div>
<!-- /.modal-dialog -->

<script type="text/javascript">
    function addRole() {
        $("#activityRole").append("</br><input style='width:400px;' type='text' name='activityRoles'" +
            "class='form-control' placeholder='活动规则'>");
    }

    $("#actvityEndTime").datetimepicker({
        format: "yyyy-mm-dd",
        language: "zh-CN",
        autoclose: true,//选中关闭
        minView: "month"//设置只显示到月份
    });

    // 选择图片显示
    function imgChange(obj) {
        var fileUrl = obj.value;
        if (!/.(gif|jpg|jpeg|png|GIF|JPG|bmp)$/.test(fileUrl)) {
            AlertText.tips("d_alert", "提示", "图片类型必须是.gif,jpeg,jpg,png,bmp中的一种");
            return false;
        } else {
            if (((obj.files[0].size).toFixed(2)) >= (200 * 1024)) {
                AlertText.tips("d_alert", "提示", "请上传小于200K的图片");
                return false;
            } else {
                var file = document.getElementById("headFile");
                var imgUrl = window.URL.createObjectURL(file.files[0]);
                var image = new Image();
                image.src = imgUrl;
                image.onload = function () {
                    //加载图片获取图片真实宽度和高度
                    var width = image.width;
                    var height = image.height;
                    if (width < 751 && height < 533) {
                        var img = document.getElementById('imghead');
                        img.setAttribute('src', imgUrl); // 修改img标签src属性值
                        $("#preview").show();
                    } else {
                        var msg = "文件尺寸应小于：750*532！,当前图片" + width + "*" + height;
                        AlertText.tips("d_alert", "提示", msg);
                        file.value = "";
                        return false;
                    }
                };
            }
        }
    }

    function imgCenterChange(obj) {
        var fileUrl = obj.value;
        if (!/.(gif|jpg|jpeg|png|GIF|JPG|bmp)$/.test(fileUrl)) {
            AlertText.tips("d_alert", "提示", "图片类型必须是.gif,jpeg,jpg,png,bmp中的一种");
            return false;
        } else {
            if (((obj.files[0].size).toFixed(2)) >= (200 * 1024)) {
                AlertText.tips("d_alert", "提示", "请上传小于200K的图片");
                return false;
            } else {
                var file = document.getElementById("centerFile");
                var imgUrl = window.URL.createObjectURL(file.files[0]);
                var image = new Image();
                image.src = imgUrl;
                image.onload = function () {
                    //加载图片获取图片真实宽度和高度
                    var width = image.width;
                    var height = image.height;
                    if (width < 751 && height < 533) {
                        var img = document.getElementById('imgCenter');
                        img.setAttribute('src', imgUrl); // 修改img标签src属性值
                        $("#previewCenter").show();
                    } else {
                        var msg = "文件尺寸应小于：750*532！,当前图片" + width + "*" + height;
                        AlertText.tips("d_alert", "提示", msg);
                        file.value = "";
                        return false;
                    }
                };
            }
        }
    }
    //点击保存按钮,提交form表单，触发校验
    $("#addSaveBtn").click(function () {
        //格式化分类属性信息为JSON串
        $("#addSubmitBtn").click();
    });

    //表单验证
    $(document).ready(function () {
        insertValidatorForm();
    });
    //初始化表单
    function insertValidatorForm() {
//        var headFile = $('#headFile').get(0).files[0];
//        var centerFile = $('#centerFile').get(0).files[0];
//        if (headFile && centerFile) {
        $("#insertForm")
            .bootstrapValidator({
                message: "不能为空",
                feedbackIcons: {
                    valid: 'glyphicon glyphicon-ok',
                    invalid: 'glyphicon glyphicon-remove',
                    validating: 'glyphicon glyphicon-refresh'
                },
                fields: {
                    source: {
                        validators: {
                            notEmpty: {
                                message: "不能为空"
                            }
                        }
                    },
                    headFile: {
                        validators: {
                            notEmpty: {
                                message: "图片不能为空"
                            }
                        }
                    },
                    centerFile: {
                        validators: {
                            notEmpty: {
                                message: "图片不能为空"
                            }
                        }
                    },
                    centercolorValue: {
                        validators: {
                            notEmpty: {
                                message: "不能为空"
                            }
                        }
                    },
                    activityRoles: {
                        validators: {
                            notEmpty: {
                                message: "不能为空"
                            }
                        }
                    },
                    couponRoles: {
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
                url: "${ctx}/hsActivity/addActivity.do",
                dataType: "json",
                success: function (result) {
                    if (result.success) {
                        AlertText.tips("d_alert", "提示", result.resultMessage);
                        refreshPage();
                        //全部更新完后关闭弹窗
                        $("#addMissBtn").click();
                        //重置表单数据
                        document.getElementById("insertForm").reset();
                    } else {
                        AlertText.tips("d_alert", "提示", result.resultMessage, function () {
                            addFormReset();
                        });
                    }
                },
                error: function () {
                    alert("系统异常，请稍后再试");
                    addFormReset();
                }
            }; // end options
            $("#insertForm").ajaxSubmit(options);
        }); // end on("success.form.bv"
//        } else {
//            AlertText.tips("d_alert", "提示", "请选择上传文件！");
//        }
    }
    function addFormReset() {
        //重置表单验证
        $("#insertForm").data("bootstrapValidator").resetForm();
        //让按钮重新能点击
        $("#addSaveBtn").button("reset");
        //隐藏等待
        AlertText.hide();
    }
    $("#startTime").datetimepicker({
        format: "yyyy-mm-dd",
        language: "zh-CN",
        autoclose: true,//选中关闭
        minView: "month"//设置只显示到月份
    });

    $("#endTime").datetimepicker({
        format: "yyyy-mm-dd",
        language: "zh-CN",
        autoclose: true,//选中关闭
        minView: "month"//设置只显示到月份
    });
    $("#activityTime").datetimepicker({
        format: "yyyy-mm-dd",
        language: "zh-CN",
        pickerPosition: 'top-right',
        autoclose: true,//选中关闭
        minView: "month"//设置只显示到月份
    });

</script>