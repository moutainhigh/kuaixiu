<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/commons/taglibs.jsp" %>
<div class="am-cf am-padding am-padding-bottom-0">
    <div class="am-fl am-cf" style="width: 100%;">
        <strong class="am-text-primary am-text-lg">站点管理</strong> / <small>列表查询</small>
    </div>
</div>

<hr>

<div class="am-g">
    <form id="searchForm" class="form form-horizontal">
        <table id="searchTable">
            <tr>
                <td class="search_th search_th_frist"><label class="control-label">&nbsp&nbsp&nbsp站点ID ：</label></td>
                <td class="search_td"><input type="text" name="query_stationId" class="form-control" ></td>

                <td class="search_th search_th_frist"><label class="control-label">站点名称 ：</label></td>
                <td class="search_td"><input type="text" name="query_stationName" class="form-control" ></td>

                <td></td>
            </tr>

            <tr>
                <td class="search_th search_th_frist"><label class="control-label">&nbsp&nbsp&nbsp联系人 ：</label></td>
                <td class="search_td"><input type="text" name="query_name" class="form-control" ></td>

                <td class="search_th search_th_frist"><label class="control-label">验机电话 ：</label></td>
                <td class="search_td"><input type="text" name="query_tel" class="form-control" ></td>

                <td></td>
            </tr>
        </table>


        <div class="form-group">
            <div class="am-u-sm-12 am-u-md-6">
                <div class="am-btn-toolbar">
                    <div class="am-btn-group am-btn-group-sm m20">
                        <button onclick="refreshPage();" class="am-btn am-btn-default search_btn" type="button"> 搜  索 </button>
                        <button onclick="expDataExcel();" type="button" class="am-btn am-btn-default"><span class="am-icon-file-excel-o"></span> 导出</button>
                    </div>
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
                <th class="fontWeight_normal tdwidth50"><input id="check_all_btn" onclick="checkAll(this)" type="checkbox" />序号</th>
                <th class="fontWeight_normal table-title center">站点id</th>
                <th class="fontWeight_normal table-title center">站点名称</th>
                <th class="fontWeight_normal table-title center">联系人</th>
                <th class="fontWeight_normal table-title center">验机电话</th>
                <th class="fontWeight_normal table-title center">业务启停</th>
                <th class="fontWeight_normal table-title center">已分配(张)</th>
                <th class="fontWeight_normal table-title center">库存(张)</th>
                <th class="fontWeight_normal table-date tdwidth130  center">地址</th>
                <th class="fontWeight_normal table-set tdwidth130">操作</th>
            </tr>
            </thead>
            <tbody>

            </tbody>
        </table>
    </div>
</div>


<script type="text/javascript">
    //自定义datatable的数据
    var dto=new DtOptions();
    //设置数据刷新路径
    dto.ajax={
        "url": "${ctx}/telecom/station/queryListForPage.do",
        "data":function(d){
            //将表单中的查询条件追加到请求参数中
            var array = $("#searchForm").serializeArray();
            $.each(array, function() {
                d[this.name] = this.value;
            });
        }
    };

    //设置数据列
    dto.setColumns([
        {"data": "id","class":"tdwidth50 center"},
        {"data": "id","class":""},
        {"data": "stationName","class":""},
        {"data": "name","class":""},
        {"data": "tel","class":""},
        {"data": "isOpen","class":""},
        {"data": "distributionSum","class":""},
        {"data": "repertory","class":""},
        {"data": "address","class":""},
        {"defaultContent": "操作","class":""}
    ]);
    //设置定义列的初始属性
    dto.setColumnDefs([
        {//复选框
            targets: 0,
            render: function (data, type, row, meta) {
                var context = {
                    func: [
                        {"id": row.id, "order" : meta.row + 1}
                    ]
                };
                var html = template_chk(context);
                return html;
            }
        },
        {//号卡类型
            targets: 5,
            render: function (data, type, row, meta) {
                if(row.isOpen==0){
                    return '是';
                }
                else{
                    return '否';
                }
            }
        },
        {
            targets: -1,
            render: function (data, type, row, meta) {
                if((row.isOpen)==0){
                    var context = {
                        func: [
                            {"name" : "停用", "fn" : "update(\'" + row.id+ "\')","icon" : "am-icon-pencil-square-o","class" : "am-text-secondary"},
                            {"name" : "删除", "fn": "delBtnClick(\'" + row.id + "\')", "icon": "am-icon-trash-o","class" : "am-text-danger"}
                        ]
                    };
                }else{
                    var context = {
                        func: [
                            {"name" : "启用", "fn" : "update(\'" + row.id+ "\')","icon" : "am-icon-pencil-square-o","class" : "am-text-secondary"},
                            {"name" : "删除", "fn": "delBtnClick(\'" + row.id + "\')", "icon": "am-icon-trash-o","class" : "am-text-danger"}
                        ]
                    };
                }

                var html = template_btn(context);
                return html;
            }
        }
    ]);

    var myTable = $("#dt").DataTable(dto);

    /**
     * 刷新列表
     */
    function refreshPage(){
        $("#pageStatus").val(1);
        myTable.ajax.reload(null, false);
    }

    /**
     * 全选按钮
     */
    function checkAll(obj){
        $("input[name='item_check_btn']").each(function(){
            $(this).prop("checked", obj.checked);
        });
    }

    function checkItem(obj){
        var checked = true;
        $("input[name='item_check_btn']").each(function(){
            if(!this.checked){
                checked = false;
                return false;
            }
        });
        $("#check_all_btn").prop("checked", checked);
    }



    /**
     * 批量删除
     */
    function batchDelBtnClick(){
        var ids = "";
        $("input[name='item_check_btn']").each(function(){
            if(this.checked){
                ids += this.value + ",";
            }
        });
        if(ids =="")
            AlertText.tips("d_alert", "提示", "请选择删除项！");
        else
            delBtnClick(ids);
    }

    function delBtnClick(id){
        AlertText.tips("d_confirm", "删除提示", "确定要删除吗？", function(){
            //加载等待
            AlertText.tips("d_loading");
            var url_ = AppConfig.ctx + "/telecom/station/delete.do";
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


    /**
     * 启用,停用
     */
    function update(id){
        AlertText.tips("d_confirm", "温馨提示", "确定要修改站点状态吗？", function(){
            //加载等待
            AlertText.tips("d_loading");
            var url_ = AppConfig.ctx + "/telecom/station/updateStatus.do";
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


    /**
     * 导出数据
     */
    function expDataExcel(){
        var params = "";
        var array = $("#searchForm").serializeArray();
        $.each(array, function() {
            params += "&" + this.name + "=" + this.value;
        });
        var ids = "";
        $("input[name='item_check_btn']").each(function(){
            if(this.checked){
                ids += this.value + ",";
            }
        });
        window.open("${ctx}/file/download.do?fileId=21&ids=" + ids + params, "导出");
    }



</script>