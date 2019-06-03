<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/commons/taglibs.jsp" %>
<link rel="stylesheet" href="${webResourceUrl}/resource/order/css/order.detail.css">
<script src="${webResourceUrl}/resource/js/address.js" type="text/javascript" charset="utf-8"></script>
<div class="am-cf am-padding am-padding-bottom-0">
    <div class="am-fl am-cf" style="width: 100%;">
        <strong class="am-text-primary am-text-lg"><a href="javascript:void(0);" onclick="toList();">活动管理</a></strong>
        /
        <small>创建活动</small>
    </div>
</div>

<hr>

<div class="am-g">

    <form id="insertForm" method="post" class="form-horizontal" enctype="multipart/form-data">
        <div class="form-group">
            <label for="source" class="col-sm-2 control-label"><span style="color:red">*</span> 来源</label>
            <div class="col-sm-9">
                <select id="source" name="source" style="width:400px;" class="form-control">
                    <option value="">--请选择--</option>
                    <c:forEach items="${recycleSystems }" var="item" varStatus="i">
                        <option value="${item.id }">${item.name }</option>
                    </c:forEach>
                </select>
            </div>
        </div>
        <div class="form-group">
            <label class="col-sm-2 control-label"><span style="color:red">*</span>上传头图</label>
            <input class="col-sm-9" type="file" name="headFile" id="headFile" accept="image/*"
                   onchange="imgChange(this);"/>
            <!--文件上传选择按钮-->
            <div id="preview" hidden="hidden" class="col-sm-9">
                <img id="imghead" src="" width="260" height="180"/> <!--图片显示位置-->
            </div>
        </div>
        <div class="form-group">
            <label class="col-sm-5 control-label">（图片尺寸为600*300大小控制200k以下）</label><!--图片显示位置-->
        </div>
        <div class="form-group">
            <label class="col-sm-2 control-label"><span style="color:red">*</span>头图高度</label>
            <div class="col-sm-9">
                <input type="text" style="width:400px;" id="headHeight" name="headHeight" class="form-control">
            </div>
        </div>
        <div class="form-group">
            <label class="col-sm-2 control-label"><span style="color:red">*</span>头图宽度</label>
            <div class="col-sm-9">
                <input type="text" style="width:400px;" id="headWide" name="headWide" class="form-control">
            </div>
        </div>
        <div class="form-group">
            <label class="col-sm-2 control-label"><span style="color:red">*</span>上传加价券图</label>
            <input class="col-sm-9" type="file" name="centerFile" id="centerFile" accept="image/*"
                   onchange="imgCenterChange(this);"/>
            <!--文件上传选择按钮-->
            <div id="previewCenter" hidden="hidden" class="col-sm-9">
                <img id="imgCenter" src="" width="260" height="180"/> <!--图片显示位置-->
            </div>
        </div>
        <div class="form-group">
            <label class="col-sm-5 control-label">（图片尺寸为600*300大小控制200k以下）</label><!--图片显示位置-->
        </div>

        <div class="form-group">
            <label class="col-sm-2 control-label"><span style="color:red">*</span>加价券边框高度</label>
            <div class="col-sm-9">
                <input type="text" style="width:400px;" id="marginHeight" name="marginHeight" class="form-control"
                       placeholder="加价券边框高度">
            </div>
        </div>
        <div class="form-group">
            <label class="col-sm-2 control-label"><span style="color:red">*</span>加价券边框宽度</label>
            <div class="col-sm-9">
                <input type="text" style="width:400px;" id="marginWide" name="marginWide" class="form-control"
                       placeholder="加价券边框宽度">
            </div>
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
            <label class="col-sm-2 control-label"><span style="color:red">*</span>加价券图片高度</label>
            <div class="col-sm-9">
                <input type="text" style="width:400px;" name="centerHeight" id="centerHeight"
                       class="form-control"
                       placeholder="加价券图片高度">
            </div>
        </div>
        <div class="form-group">
            <label class="col-sm-2 control-label"><span style="color:red">*</span>加价券图片宽度</label>
            <div class="col-sm-9">
                <input type="text" style="width:400px;" name="centerWide" id="centerWide"
                       class="form-control"
                       placeholder="加价券图片宽度">
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
            <label for="isDefault" class="col-sm-2 control-label"><span style="color:red"></span>是否默认</label>
            <div class="col-sm-9">
                <div class="oldToNew">
                    <input name="isDefault" type="radio" value="1" checked/>是
                    &nbsp&nbsp&nbsp&nbsp
                    <input name="isDefault" type="radio" value="0"/>否
                </div>
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
    function addRole() {
        $("#activityRole").append("<input style='width:400px;' type='text' name='activityRoles'" +
            "class='form-control' placeholder='活动规则'>");
    }

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
                    if (width < 751 && height < 493) {
                        var img = document.getElementById('imghead');
                        img.setAttribute('src', imgUrl); // 修改img标签src属性值
                        $("#preview").show();
                    } else {
                        var msg = "文件尺寸应小于：750*492！,当前图片" + width + "*" + height;
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
                    if (width < 751 && height < 493) {
                        var img = document.getElementById('imgCenter');
                        img.setAttribute('src', imgUrl); // 修改img标签src属性值
                        $("#previewCenter").show();
                    } else {
                        var msg = "文件尺寸应小于：750*492！,当前图片" + width + "*" + height;
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
        var formdata = new FormData($("#insertForm")[0]);
        var headFile = $('#headFile').get(0).files[0];
        var centerFile = $('#centerFile').get(0).files[0];
        console.info(headFile);
        console.info(centerFile);
        if (headFile&&centerFile) {
            $.ajax({
                url: "${ctx}/hsActivity/addActivity.do",
                type: "POST",
                data: formdata,
                dataType: "json",
                processData: false,  // 告诉jQuery不要去处理发送的数据
                contentType: false,   // 告诉jQuery不要去设置Content-Type请求头
                success: function (result) {
                    if (result.success) {
                        AlertText.tips("d_alert", "提示", result.resultMessage);
                        $("#insertForm")[0].reset();
                        var img = document.getElementById('imghead');
                        img.setAttribute('src', '');
                    } else {
                        AlertText.tips("d_alert", "提示", result.resultMessage);
                    }
                },
                error: function () {
                    alert("异常");
                }

            })
        } else {
            AlertText.tips("d_alert", "提示", "请选择上传文件！");
        }
    })
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