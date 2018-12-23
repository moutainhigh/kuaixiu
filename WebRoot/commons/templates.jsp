<%@page pageEncoding="UTF-8"%>
<%-- 列表复选框模板 --%>
<script id="tpl_chk" type="text/x-handlebars-template">
    {{#each func}}
        <input name="item_check_btn" onclick="checkItem(this)" value="{{this.id}}" type="checkbox" />
        {{this.order}}
    {{/each}}
</script>
<%-- 列表操作按钮模板 --%>
<script id="tpl_btn" type="text/x-handlebars-template">
    <div class="am-btn-toolbar">
        <div class="am-btn-group am-btn-group-xs">
            {{#each func}}
                <button type="button" onclick="{{this.fn}};" class="am-btn am-btn-default am-btn-xs {{this.class}}">
                    <span class="{{this.icon}}"></span> {{this.name}}
                </button>
            {{/each}}
        </div>
    </div>
</script>

<script type="text/javascript">
<!--
    //获取模板
	var tpl_chk = $("#tpl_chk").html();
    var tpl_btn = $("#tpl_btn").html();
    //预编译模板
    var template_chk = Handlebars.compile(tpl_chk);
    var template_btn = Handlebars.compile(tpl_btn);

//-->
</script>
