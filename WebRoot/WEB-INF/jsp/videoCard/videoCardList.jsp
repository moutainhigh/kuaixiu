<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/commons/taglibs.jsp" %>
<div class="am-cf am-padding am-padding-bottom-0">
    <div class="am-fl am-cf" style="width: 100%;">
        <strong class="am-text-primary am-text-lg">卡密管理</strong> / <small>列表查询</small>
    </div>
</div>

<hr>

<div class="am-g">
    <form id="searchForm" class="form form-horizontal">
        <table id="searchTable">
            <tr>
                <td class="search_th "><label class="control-label">类型：</label></td>
                <td class="search_td">
                    <select name="type" onchange="brandChange(this.value);" class="form-control">
                        <option value="">--选择类型--</option>
                        <option value="1">周卡</option>
                        <option value="2">月卡</option>
                        <option value="3">季卡</option>
                        <option value="4">半年卡</option>
                        <option value="5">年卡</option>
                    </select>
                </td>

                <td class="search_th "><label class="control-label">是否领用：</label></td>
                <td class="search_td">
                    <select name="isUse" onchange="brandChange(this.value);" class="form-control">
                        <option value="">--请选择--</option>
                        <option value="1">是</option>
                        <option value="0">否</option>
                    </select>
                </td>
            </tr>
            <tr>
                <td class="search_th "><label class="control-label">卡密ID：</label></td>
                <td class="search_td"><input type="text" name="cardId" class="form-control"></td>
            </tr>

        </table>


        <div class="form-group">
            <div class="am-u-sm-12 am-u-md-6">
                <div class="am-btn-toolbar">
                    <div class="am-btn-group am-btn-group-sm m20">
                        <button onclick="refreshPage();" class="am-btn am-btn-default search_btn" type="button"> 搜  索 </button>
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
                <th class="fontWeight_normal table-title center">id</th>
                <th class="fontWeight_normal table-title center">卡密</th>
                <th class="fontWeight_normal table-title center">类型</th>
                <th class="fontWeight_normal table-title center">价格</th>
                <th class="fontWeight_normal table-title center">有效期</th>
                <th class="fontWeight_normal table-title center">状态</th>
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
        "url": "${ctx}/videoCard/queryListForPage.do",
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
        {"data": "cardId","class":""},
        {"data": "type","class":""},
        {"data": "price","class":""},
        {"data": "validityTime","class":""},
        {"data": "isUse","class":""}
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
            targets: 3,
            render: function (data, type, row, meta) {
                if(row.type==1){
                    return '周卡';
                }else if(row.type==2){
                    return '月卡';
                }else if(row.type==3){
                    return '季卡';
                }else if(row.type==4){
                    return '半年卡';
                }else if(row.type==5){
                    return '年卡';
                }else{
                    return '';
                }
            }
        },
        {//号卡类型
            targets: 6,
            render: function (data, type, row, meta) {
                if(row.isUse==0){
                    return '未领取';
                }
                else{
                    return '已领取';
                }
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




</script>