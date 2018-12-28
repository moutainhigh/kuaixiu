<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/commons/taglibs.jsp" %>
<link rel="stylesheet" href="${webResourceUrl}/resource/order/css/order.detail.css">
<script src="${webResourceUrl}/resource/js/address.js" type="text/javascript" charset="utf-8"></script>
<div class="am-cf am-padding am-padding-bottom-0">
    <div class="am-fl am-cf" style="width: 100%;">
        <strong class="am-text-primary am-text-lg"><a href="javascript:void(0);" onclick="toList();">活动管理</a></strong>
        /
        <small>编辑活动</small>
    </div>
</div>

<hr>

<div class="am-g">

    <form id="insertForm" method="post" class="form-horizontal"  enctype="multipart/form-data">
        <input type="text" hidden="hidden" id="activityIdentification" value="${activity.activityIdentification}" name="activityIdentification" class="form-control">
        <div class="form-group">
            <label  class="col-sm-2 control-label"><span style="color:red">*</span>企业名称</label>
            <div class="col-sm-9">
                <input type="text" id="companyName" value="${activity.companyName}" name="companyName" class="form-control">
            </div>
        </div>
        <div class="form-group">
            <label class="col-sm-2 control-label"><span style="color:red">*</span>上传图片</label>
            <input class="col-sm-9" type="file" name="file" id="file" accept="image/*" onchange="imgChange(this);"/> <!--文件上传选择按钮-->
            <div id="preview" class="col-sm-9">
                <img id="imghead" src="${activity.activityImgUrl}" width="260" height="180"/> <!--图片显示位置-->
            </div>
        </div>

        <div class="form-group">
            <label  class="col-sm-2 control-label"><span style="color:red">*</span>预约时间</label>
            <div class="am-datepicker-date col-sm-9" >
                <input type="text" id="startTime" name="startTime"
                       class="form-control am-datepicker-start" data-am-datepicker readonly value="${activity.startTime}">
                <span style="float: left; line-height: 30px; height: 30px; width: 10%; text-align: center;">至</span>
                <input type="text" id="endTime" name="endTime"
                       class="form-control am-datepicker-end" data-am-datepicker readonly value="${activity.endTime}">
            </div>
        </div>

        <div class="form-group">
            <label  class="col-sm-2 control-label"><span style="color:red">*</span>快修业务标题</label>
            <div class="col-sm-9">
                <input type="text" id="kxBusinessTitle" name="kxBusinessTitle" value="${activity.kxBusinessTitle}" class="form-control" placeholder="快修业务">
            </div>
        </div>
        <div class="form-group">
            <label  class="col-sm-2 control-label"><span style="color:red">*</span>快修业务说明</label>
            <div class="col-sm-9">
                <textarea name="kxBusiness" id="kxBusiness"  cols="30" rows="10">${activity.kxBusiness}</textarea>
            </div>
        </div>
        <div class="form-group">
            <label class="col-sm-2 control-label"><span style="color:red">*</span>快修活动页地址</label>
            <div class="col-sm-9">
                <input type="text" id="kxBusinessDetail" name="kxBusinessDetail" value="${activity.kxBusinessDetail}" class="form-control" placeholder="快修业务">
            </div>
        </div>
        <div class="form-group">
            <label  class="col-sm-2 control-label"><span style="color:red">*</span>电信业务标题</label>
            <div class="col-sm-9">
                <input type="text" value="${activity.dxIncrementBusinessTitle}" name="dxIncrementBusinessTitle" id="dxIncrementBusinessTitle" class="form-control" placeholder="电信业务">
            </div>
        </div>
        <div class="form-group">
            <label class="col-sm-2 control-label"><span style="color:red">*</span>电信业务说明</label>
            <div class="col-sm-9">
                <textarea name=" dxIncrementBusiness" id="dxIncrementBusiness" cols="30" rows="10">${activity.dxIncrementBusiness}</textarea>
            </div>
        </div>
        <div class="form-group">
            <label class="col-sm-2 control-label"><span style="color:red">*</span>电信活动页地址</label>
            <div class="col-sm-9">
                <input type="text" id="dxIncrementBusinessDetail" value="${activity.dxIncrementBusinessDetail}" name="dxIncrementBusinessDetail" class="form-control" placeholder="快修业务">
            </div>
        </div>
        <div class="form-group">
            <label class="col-sm-2 control-label"><span style="color:red">*</span>电信活动负责人</label>
            <div class="col-sm-9">
                <input type="text" id="dxBusinessPerson" value="${activity.dxBusinessPerson}" name="dxBusinessPerson" class="form-control" placeholder="快修业务">
            </div>
        </div>
        <div class="form-group">
            <label class="col-sm-2 control-label"><span style="color:red">*</span>电信活动负责人电话</label>
            <div class="col-sm-9">
                <input type="text" id="dxBusinessPersonNumber" value="${activity.dxBusinessPersonNumber}" name="dxBusinessPersonNumber" class="form-control" placeholder="快修业务">
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
    // 选择图片显示
    function imgChange(obj) {
//获取点击的文本框
        var file = document.getElementById("file");
        var imgUrl = window.URL.createObjectURL(file.files[0]);
        var img = document.getElementById('imghead');
        img.setAttribute('src', imgUrl); // 修改img标签src属性值
    };

    //点击保存按钮,提交form表单，触发校验
    $("#addSaveBtn").click(function(){
        var formdata = new FormData($("#insertForm")[0]);
        $.ajax({
            url: "${ctx}/activityCompany/update.do",
            type: "POST",
            data:formdata,
            dataType: "json",
            processData: false,  // 告诉jQuery不要去处理发送的数据
            contentType: false,   // 告诉jQuery不要去设置Content-Type请求头
            success: function (result) {
                if(result.success){
                    alert("修改成功");
                    addFormReset();
                }else{
                    alert("失败");
                }

            },
            error:function () {
                alert("异常");
            }

        })
    })
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



</script>