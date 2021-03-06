<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/commons/taglibs.jsp" %>
<<style>
.logo {
position: absolute;
top: 40%;
left: 45%;
display: none;
width: 20%;
height: 20%;
background: white;
overflow: auto;
z-index: 10;
border: 2px dashed #C8CBCE;
border-top-color: rgb(200, 203, 206);
border-top-style: dashed;
border-top-width: 2px;
border-right-color: rgb(200, 203, 206);
border-right-style: dashed;
border-right-width: 2px;
border-bottom-color: rgb(200, 203, 206);
border-bottom-style: dashed;
border-bottom-width: 2px;
border-left-color: rgb(200, 203, 206);
border-left-style: dashed;
border-left-width: 2px;
border-image-source: initial;
border-image-slice: initial;
border-image-width: initial;
border-image-outset: initial;
border-image-repeat: initial;
}
</style>
<div class="am-cf am-padding am-padding-bottom-0">
    <div class="am-fl am-cf" style="width: 100%;">
        <strong class="am-text-primary am-text-lg">品牌管理</strong> /
        <small>列表查询</small>
    </div>
</div>

<hr>

<div class="am-g">
    <form id="searchForm" class="am-form am-form-horizontal">
        <div class="am-form-group">
            <div class="am-u-sm-12 am-u-md-6">
                <div class="am-btn-toolbar">
                    <div class="am-btn-group am-btn-group-sm">
                        <button onclick="addBtnClick();" type="button" class="am-btn am-btn-default"><span
                                class="am-icon-plus"></span> 新增
                        </button>
                    </div>
                </div>
            </div>
            <div class="am-u-sm-12 am-u-md-4">
                <div class="am-input-group am-input-group-sm">
                    <input type="text"  style="display:none">
                    <input type="text" onkeydown="refreshPage()" name="query_name" class="am-form-field" placeholder="品牌名称">
                    <span class="am-input-group-btn">
		          <button onclick="refreshPage();" class="am-btn am-btn-default" type="button">搜索</button>
		        </span>
                </div>
            </div>
        </div>
    </form>
</div>

<div class="am-g">
    <div class="am-u-sm-12">
        <table id="dt" class="table table-striped table-bordered table-radius table-hover">
            <thead>
            <tr>
                <th class="fontWeight_normal tdwidth50"><input id="check_all_btn" onclick="checkAll(this)"
                                                               type="checkbox"/>序号
                </th>
                <th class="fontWeight_normal table-title center">品牌名称</th>
                <th class="fontWeight_normal table-title tdwidth80">最高保额</th>
                <th class="fontWeight_normal table-title tdwidth80">排序</th>
                <th class="fontWeight_normal table-date tdwidth130  center">添加时间</th>
                <th class="fontWeight_normal table-set tdwidth130">操作</th>
            </tr>
            </thead>
            <tbody>

            </tbody>
        </table>
    </div>
</div>

<div id="logo" class="logo">
    <form id="uploadForm">
        <p><input type="hidden" name="filename" id="filename" value="11"/></p>
        <p>上传图片： <input type="file" name="file"/></p>
        <div style="height:60px;">
            <img alt="haha" src="" id="logoUrl" width="45" ,height="54">
        </div>
        <p>
            <input type="button" value="上传" onclick="doUpload()"/>
            <input type="button" value="取消" onclick="cancel()"/>
        </p>
    </form>
</div>
<!-- 新增弹窗 end -->
<div id="modal-insertView" class="modal fade" tabindex="-1" role="dialog" aria-hidden="true" style="display: none;">
    <%@ include file="addBrand.jsp" %>
</div>
<!-- 新增弹窗 end -->
<!-- 修改弹窗 end -->
<div id="modal-updateView" class="modal fade" tabindex="-1" role="dialog" aria-hidden="true" style="display: none;">
</div>
<!-- 修改弹窗 end -->
<script type="text/javascript">
    //自定义datatable的数据
    var dto = new DtOptions();
    //设置数据刷新路径
    dto.ajax = {
        "url": "${ctx}/screen/brand/queryListForPage.do",
        "data": function (d) {
            //将表单中的查询条件追加到请求参数中
            var array = $("#searchForm").serializeArray();
            $.each(array, function () {
                d[this.name] = this.value;
            });
        }
    };

    //设置数据列
    dto.setColumns([
        {"data": "id", "class": "tdwidth50 center"},
        {"data": "name", "class": ""},
        {"data": "maxPrice", "class": ""},
        {"data": "sort", "class": ""},
        {"data": "createTime", "class": "am-hide-sm-only"},
        {"defaultContent": "操作", "class": ""}
    ]);
    //设置定义列的初始属性
    dto.setColumnDefs([
        {//复选框
            targets: 0,
            render: function (data, type, row, meta) {
                var context = {
                    func: [
                        {"id": row.id, "order": meta.row + 1}
                    ]
                };
                var html = template_chk(context);
                return html;
            }
        },
        {
            targets: -1,
            render: function (data, type, row, meta) {
                var context = {
                    func: [
                        {
                            "name": "编辑",
                            "fn": "editBtnClick(\'" + row.id + "\')",
                            "icon": "am-icon-pencil-square-o",
                            "class": "am-text-secondary"
                        },
                        {
                            "name": "删除",
                            "fn": "delBtnClick(\'" + row.id + "\')",
                            "icon": "am-icon-trash-o",
                            "class": "am-text-danger"
                        }
                        // {"name" : "更换图标", "fn": "editLogo(\'" + row.id+"','"+row.logo + "\')", "icon": "am-icon-pencil-square-o","class" : "am-text-secondary"}
                    ]
                };
                var html = template_btn(context);
                return html;
            }
        }
    ]);

    var myTable = $("#dt").DataTable(dto);

    /**
     * 刷新列表
     */
    function refreshPage() {
        myTable.ajax.reload(null, false);
    }

    /**
     * 全选按钮
     */
    function checkAll(obj) {
        $("input[name='item_check_btn']").each(function () {
            $(this).prop("checked", obj.checked);
        });
    }

    function checkItem(obj) {
        var checked = true;
        $("input[name='item_check_btn']").each(function () {
            if (!this.checked) {
                checked = false;
                return false;
            }
        });
        $("#check_all_btn").prop("checked", checked);
    }

    function addBtnClick() {
        $("#modal-insertView").modal("show");
    }

    function editBtnClick(id) {
        $("#modal-updateView").html("");
        $("#modal-updateView").load("${ctx}/screen/brand/edit.do?id=" + id, function () {
            func_after_model_load(this);
        });
    }
    /**
     * 导出数据
     */
    function expDataExcel() {
        var params = "";
        var array = $("#searchForm").serializeArray();
        $.each(array, function () {
            params += "&" + this.name + "=" + this.value;
        });
        var ids = "";
        $("input[name='item_check_btn']").each(function () {
            if (this.checked) {
                ids += this.value + ",";
            }
        });
        window.open("${ctx}/file/download.do?fileId=5&ids=" + ids + params, "导出");
    }
    /**
     * 批量删除
     */
    function batchDelBtnClick() {
        var ids = "";
        $("input[name='item_check_btn']").each(function () {
            if (this.checked) {
                ids += this.value + ",";
            }
        });
        if (ids == "")
            AlertText.tips("d_alert", "提示", "请选择删除项！");
        else
            delBtnClick(ids);
    }

    function delBtnClick(id) {
        AlertText.tips("d_confirm", "删除提示", "确定要删除吗？", function () {
            //加载等待
            AlertText.tips("d_loading");
            var url_ = AppConfig.ctx + "/screen/brand/delete.do";
            var data_ = {id: id};
            $.ajax({
                url: url_,
                data: data_,
                type: "POST",
                dataType: "json",
                success: function (result) {
                    if (result.success) {
                        //保存成功,关闭窗口，刷新列表
                        refreshPage();
                    } else {
                        AlertText.tips("d_alert", "提示", result.msg);
                        return false;
                    }
                    //隐藏等待
                    AlertText.hide();
                }
            });
        });
    }
    var brandId;    //品牌id
    var logoId;     //图标路径
    function editLogo(id, logo) {
        $('#logo').show();
        brandId = id;
        logoId = logo;
        $('#logoUrl').attr("src", logoId);
    }

    function doUpload() {
        var url = AppConfig.ctx + "/screen/uploadLogo.do";
        $('#filename').val(brandId);
        var formData = new FormData($("#uploadForm")[0]);
        $.ajax({
            url: url,
            type: 'POST',
            data: formData,
            async: false,
            cache: false,
            contentType: false,
            processData: false,
            success: function (result) {
                if (result.success) {
                    alert('提交成功');
                    $('#logo').hide();
                    refreshPage();
                } else {
                    alert(result.resultMessage);
                    $('#logo').hide();
                }
            },
            error: function (returndata) {
                alert("系统异常,请稍后重试！");
                $('#logo').hide();
            }
        });
    }

    function cancel() {
        $('#logo').hide();
    }
</script>