$(function () {
    //回车提交事件
    $(document).keydown(function() {
        if (event.keyCode == "13") {
            $('#subBtn').click();
        }
    });
    $("#subBtn").click(function () {
        var username = $("#username").val();
        var userpwd = $("#userpwd").val();
        //去除所有空格
        username=username.replace(/\s|\xA0/g,"");    
        userpwd=userpwd.replace(/\s|\xA0/g,"");    
        var autoLogin = "0";
        if($("#autologin").is(':checked')){
            autoLogin = "1";
        }

        if (username == "") {
            AlertText.tips("d_alert", "提示", "请输入用户名!");
            return false;
        }
        if (userpwd == "") {
            AlertText.tips("d_alert", "提示", "请输入密码!");
            return false;
        }
        var md5PassWord = hex_md5(userpwd);
        var url_ = AppConfig.ctx + "/sj/admin/checkLogin.do";
        var data_ = {loginId: username, passwd: md5PassWord,autoLogin:autoLogin};
        AlertText.tips("d_loading");//加载等待
        $.ajax({
            url: url_,
            data: data_,
            type: "POST",
            dataType: "json",
            success: function (result) {
                if (result.success) {
                  window.location.href = AppConfig.ctx + "/sj/admin/index.do";
                } else {
                	AlertText.hide();//隐藏等待
                    AlertText.tips("d_alert", "提示", result.msg);
                    return false;
                }
            }
        });
    });
});