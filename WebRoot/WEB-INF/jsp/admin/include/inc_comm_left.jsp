<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/commons/taglibs.jsp" %>
<!-- sidebar start -->
<div class="admin-sidebar am-offcanvas" id="admin-offcanvas">
  <div class="am-offcanvas-bar admin-offcanvas-bar">
    <ul class="am-list admin-sidebar-list">
      <li><a href="${commonurl_home }"><span class="am-icon-home"></span> 首页</a></li>
      <%-- 循环输出菜单 --%>
      <c:forEach items="${sysMenuList }" var="menu" varStatus="i" >
	    <li class="admin-parent">
	      <a class="am-cf am-collapsed" onclick="func_admin_parent_click(this);" data-am-collapse="{target: '#collapse-nav_${i.index}'}">
	        <span class="${menu.icon }"></span> ${menu.name }<span class="am-icon-angle-down am-fr am-margin-right"></span>
	      </a>
	      <%-- 循环子菜单 --%>
	      <c:if test="${not empty menu.subMenuList }">
		    <ul class="am-list am-collapse admin-sidebar-sub" id="collapse-nav_${i.index}">
		    <c:forEach items="${menu.subMenuList }" var="subMenu" varStatus="sub" >
		      <li><a href="javascript:void(0);" onclick="func_menu_click(this,'','${ctx}${subMenu.href }',0);" class="am-cf">
		          <span class="${subMenu.icon }"></span> ${subMenu.name }</a></li>
		    </c:forEach>
		    </ul>
	      </c:if>
	      <%-- 循环子菜单 end --%>
	    </li>
      </c:forEach>
      <%-- 循环输出菜单 end --%>
      <li><a href="${commonurl_logout }"><span class="am-icon-sign-out"></span> 退出</a></li>
    </ul>

  </div>
</div>
<!-- sidebar end -->
<script type="text/javascript">

/**
 * 父菜单点击事件
 */
function func_admin_parent_click(obj){
	if($(obj).find(".am-fr").hasClass("am-icon-angle-down")){
		$(obj).find(".am-fr").removeClass("am-icon-angle-down");
		$(obj).find(".am-fr").addClass("am-icon-angle-up");
	}else{
		$(obj).find(".am-fr").removeClass("am-icon-angle-up");
        $(obj).find(".am-fr").addClass("am-icon-angle-down");
	}
}

/**
 * 最后点击的菜单
 */
var lastActive = null;

/**
 * 历史页面
 */
var historyId = 0;

/**
 * 点击菜单按钮
 * obj: 按钮对象
 * activeCode: 菜单编码
 * url: 菜单链接
 * target_: 链接打开方式
 */
function func_menu_click(obj,activeCode,url,target_){
	if (lastActive) {
		lastActive.removeClass("am-active");
	}
	lastActive = $(obj);
	lastActive.addClass("am-active");
	switch (target_){
	case 1: 
		window.open(url);
		break;
	case 2:
		window.document.location.href=url;
		break;
	default:
		func_clear_history();
		loadUrl(url);
    }
}

/**
 * 刷新页面
 * url_: 菜单链接
 * data_: 请求数据
 * target_: 链接打开方式
 */
function func_reload_page(url_,data_,target_){
    switch (target_){
    case 1: 
        window.open(url_);
        break;
    case 2:
        window.document.location.href=url_;
        break;
    default:
    	loadUrl(url_);
    }
}

/**
 * 后退
 */
function func_to_back(){
	if(historyId > 0){
		$("#content_bady_area_" + historyId).remove();
		historyId--;
		$("#content_bady_area_" + historyId).show();
	}
}


/**
 * 清空
 */
function func_clear_history(){
	for(var i = historyId; i > 0; i--){
		$("#content_bady_area_" + historyId).remove();
	}
	historyId = 0;
}

function loadUrl(url_){
	//清楚计算器
	if("undefined" != typeof(countIntervalProcess)){
	    clearInterval(countIntervalProcess);
	}
	if("undefined" != typeof(refreshIntervalProcess)){
	    clearInterval(refreshIntervalProcess);
	}
	//console.log(url_);
	$.ajax({
        url:url_,
        type:"POST",
        dataType:"html",
        success:function(data){
            //处理自定义异常返回Json错误
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
                $("#div_loading").hide();
                //保存原页面
                $("#content_bady_area_" + historyId).hide();
                historyId++;
                var tmp = $("<div class='admin-content-body'></div>");
                tmp.attr("id", "content_bady_area_" + historyId);
                tmp.html(data);
                $("#admin-content_div").prepend(tmp);
            }
        }
    });
}
</script>