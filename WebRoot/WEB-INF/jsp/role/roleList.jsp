<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/commons/taglibs.jsp" %>
<div class="am-cf am-padding am-padding-bottom-0">
    <div class="am-fl am-cf" style="width: 100%;">
        <strong class="am-text-primary am-text-lg">权限管理</strong> /
        <small>列表查询</small>
    </div>
</div>

<hr>
<div class="am-g">
    &nbsp;&nbsp;&nbsp;&nbsp;当前编辑信息:
    <label>工号：</label><span id="code"> </span>
    <label>姓名：</label><span id="name"> </span>
    <label>角色：</label><span id="role"> </span>
</div>
<div class="am-g">
    <form id="searchForm">
        <div>
            <div class="am-input-group am-input-group-sm">
                <span class="am-input-group-btn">
		          <button onclick="submitCodes();" class="am-btn am-btn-default" type="button">确认提交</button>
		        </span>
            </div>
        </div>
        <div>
            <ul id="browser">
                <c:forEach items="${menus }" var="item" varStatus="i">
                    <li>
                        <span><input type="checkbox" name="roleMenu" value="${item.code }">${item.name }</span>
                        <ul>
                            <c:forEach items="${item.subMenuList }" var="item1" varStatus="i">
                                <li class="closed">
                                <span><input type="checkbox" name="roleMenu"
                                             value="${item1.code }"> ${item1.name }</span>
                                    <ul>
                                        <c:forEach items="${item1.subMenuList }" var="item2" varStatus="i">
                                            <li class="closed">
                                            <span><input type="checkbox" name="roleMenu"
                                                         value="${item2.code }"> ${item2.name }</span>
                                                <ul>
                                                    <c:forEach items="${item2.subMenuList }" var="item3" varStatus="i">
                                                        <li class="closed">
                                                        <span><input type="checkbox" name="roleMenu"
                                                                     value="${item3.code }"> ${item3.name }</span>
                                                            <ul>
                                                                <c:forEach items="${item3.subMenuList }" var="item4"
                                                                           varStatus="i">
                                                                    <li class="closed">
                                                                       <span><input type="checkbox" name="roleMenu"
                                                                                    value="${item4.code }"> ${item4.name }</span>
                                                                    </li>
                                                                </c:forEach>
                                                            </ul>
                                                        </li>
                                                    </c:forEach>
                                                </ul>
                                            </li>
                                        </c:forEach>
                                    </ul>
                                </li>
                            </c:forEach>
                        </ul>
                    </li>
                </c:forEach>
            </ul>
        </div>
    </form>
</div>
<div id="modal-insertView" class="modal fade" tabindex="-1" role="dialog" aria-hidden="true" style="display: none;">
</div>
<script src="${webResourceUrl}/plugins/jquery/jquery.cookie.js" type="text/javascript"
        charset="utf-8"></script>
<script src="${webResourceUrl}/plugins/treeView/jquery.treeview.js" type="text/javascript" charset="utf-8"></script>
<script src="${webResourceUrl}/plugins/treeView/demo.js" type="text/javascript" charset="utf-8"></script>
<link rel="stylesheet" href="${webResourceUrl}/plugins/treeView/jquery.treeview.css"/>
<link rel="stylesheet" href="${webResourceUrl}/plugins/treeView/screen.css"/>

<script type="text/javascript">
    window.onload = addBtnClick();
    function addBtnClick() {
        $("#modal-insertView").html("");
        $("#modal-insertView").load("${ctx}/role/search.do", function () {
            func_after_model_load(this);
        });
    }

    $('input').click(function () {
        if ($(this).prop('checked')) {
            $(this).parents('li').children('span').children('input').prop('checked', true);//上一级被选中或上上一级
            $(this).closest('li').find('input').prop('checked', true);
        } else {
            $(this).closest('li').find('input').removeAttr('checked', false);
        }
    });

    /**
     * 刷新列表
     */
    function refreshPage(result) {
        $("#code").html(result.code);
        $("#name").html(result.name);
        $("#role").html(result.role);
        var menus = result.menus;
        $.each($('input:checkbox'), function () {
            for (a in menus) {
                if (menus[a].code == $(this).val()) {
                    $(this).attr("checked", true);
                }
            }
        })
    }


    function submitCodes(id) {
        var url_ = AppConfig.ctx + "/sysMenu/update.do";
        var codes = "";
        $.each($('input:checkbox'), function () {
            if (this.checked) {
                codes += $(this).val() + ",";
            }
        });
        var roleName = $("#roleName").val();
        var userId = $("#userId").val();
        var userName = $("#userName").val();
        var data_ = {id: id, codes: codes, userName: userName, userId: userId, roleName: roleName};
        $.ajax({
            url: url_,
            data: data_,
            type: "POST",
            dataType: "json",
            success: function (result) {
                if (result.success) {
                    document.getElementById("insertForm").reset();
                    alert(result.resultMessage);
                } else {
                    alert(result.resultMessage);
                }
                //隐藏等待
                AlertText.hide();
            }
        });
    }


</script>