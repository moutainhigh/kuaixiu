<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/commons/taglibs.jsp" %>
<footer class="admin-content-footer">
  <hr>
  <p class="am-padding-left">© 2014 AllMobilize, Inc. Licensed under MIT license.</p>
</footer>

<%-- 遮盖层 --%>
<div id="mask_boxs">
    <div id="masks"></div>
    <div id="mcon"></div>
</div>

<script type="text/javascript">
<!--
/**
 * 跳转登录页面
 */
function toLogin(){
    window.location.href = AppConfig.ctx + '/admin/logout.do';
}

/**
 * 处理页面加载之后返回json错误
 */
function func_after_model_load(obj){
	//alert(obj);
	var data = obj.html();
	if(!data){
		return;
	}
	try{
        var dataJson = eval('(' + data + ')');
        if(dataJson.hasOwnProperty("exceptionType")){
            if(dataJson.exceptionType == "sessionError"){
            	AlertText.tips("d_alert", "系统提示", dataJson.msg, function(){
            	    toLogin();
                });
            }else{
                AlertText.tips("d_alert", "系统提示", dataJson.msg);
            }
        }
    }catch(e){
    	obj.modal("show");
    	obj.css("display", "block");
    	obj.addClass("in");
    }
}
//-->
</script>