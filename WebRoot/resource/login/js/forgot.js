$(function () {
    //回车提交事件
    $(document).keydown(function() {
        if (event.keyCode == "13") {
            $('#subBtn').click();
        }
    });
    $("#subBtn").click(function () {
        var username = $("#username").val();
        var mobile = $("#mobile").val();
        
        if (username == "") {
            AlertText.tips("d_alert", "提示", "请输入用户名!");
            return false;
        }
        if (mobile == "") {
            AlertText.tips("d_alert", "提示", "请输入负责人手机号!");
            return false;
        }
        var url_ = AppConfig.ctx + "/admin/forgotCheck.do";
        var data_ = {loginId: username, mobile: mobile};
        AlertText.tips("d_loading");//加载等待
        $.ajax({
            url: url_,
            data: data_,
            type: "POST",
            dataType: "json",
            success: function (result) {
                if (result.success) {
                	AlertText.tips("d_alert", "提示", "重置密码已发送至负责人手机上，请注意查收", function(){
                		window.location.href = AppConfig.ctx + "/admin/login.do";
                	});
                } else {
                	AlertText.hide();//隐藏等待
                    AlertText.tips("d_alert", "提示", result.msg);
                    return false;
                }
            }
        });
    });
});