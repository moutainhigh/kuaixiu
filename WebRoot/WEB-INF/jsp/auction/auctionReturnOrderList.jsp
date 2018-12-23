<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/commons/taglibs.jsp" %>
<link rel="stylesheet" type="text/css" href="${webResourceUrl}/resource/order/css/alert.tip.css" />
<div class="am-cf am-padding am-padding-bottom-0">
    <div class="am-fl am-cf" style="width: 100%;">
        <strong class="am-text-primary am-text-lg">回收订单管理</strong> / <small>转转退货订单列表</small>
    </div>
</div>

<hr>

<div class="am-g">
    <form id="searchForm" class="form form-horizontal">
        <table id="searchTable">
            <tr>
                <td class="search_th search_th_frist"><label class="control-label">&nbsp&nbsp&nbsp订 单 号 ：</label></td>
                <td class="search_td"><input type="text" name="query_orderNo" class="form-control" ></td>

                <td class="search_th"><label class="control-label">下 单 时 间 ：</label></td>
                <td class="search_td">
                    <div class="am-datepicker-date">
                        <input type="text" id="query_startTime" name="query_startTime" class="form-control am-datepicker-start" data-am-datepicker readonly >
                        <span style="float: left; line-height: 30px; height: 30px; width: 10%; text-align: center;">至</span>
                        <input type="text" id="query_endTime" name="query_endTime" class="form-control am-datepicker-end" data-am-datepicker readonly >
                    </div>
                </td>

                <td class="search_th"><label class="control-label">是否免激活：</label></td>
                <td class="search_td">
                    <select name="query_isActive"  class="form-control">
                        <option value="">--选择状态--</option>
                        <option value="1">是</option>
                        <option value="0">否</option>
                    </select>
                </td>
            </tr>

            <tr>
                <td class="search_th"><label class="control-label">订 单 状 态 ：</label></td>
                <td class="search_td">
                    <select name="query_orderStates" class="form-control">
                        <option value="">--订单状态--</option>
                        <option value="0">未付款</option>
                        <option value="1">已付款</option>
                        <option value="2">退款中</option>
                        <option value="3">已退款</option>
                        <option value="4">提交失败</option>
                        <option value="5">提交成功</option>
                        <option value="10">已取消</option>
                    </select>
                </td>

                <td class="search_th"><label class="control-label">客户手机号：</label></td>
                <td class="search_td"><input type="text" name="query_customerMobile" class="form-control" ></td>


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
                <th class="fontWeight_normal tdwidth120">回收订单号</th>
                <th class="fontWeight_normal tdwidth120">转转订单号</th>
                <th class="fontWeight_normal tdwidth80">物流单号</th>
                <th class="fontWeight_normal tdwidth80">物流公司名称</th>
                <th class="fontWeight_normal tdwidth100">发货时间</th>
                <th class="fontWeight_normal tdwidth70">收货备注</th>
                <th class="fontWeight_normal tdwidth70">邮寄付款方式</th>
                <th class="fontWeight_normal tdwidth130">操作</th>
            </tr>
            </thead>
            <tbody>

            </tbody>
        </table>
    </div>
</div>



<script type="text/javascript">
    $("#query_startTime").datetimepicker({
        format: "yyyy-mm-dd",
        language: "zh-CN",
        autoclose:true,//选中关闭
        minView: "month"//设置只显示到月份
    });

    $("#query_endTime").datetimepicker({
        format: "yyyy-mm-dd",
        language: "zh-CN",
        autoclose:true,//选中关闭
        minView: "month"//设置只显示到月份
    });
    //自定义datatable的数据
    var dto=new DtOptions();
    //设置数据刷新路径
    dto.ajax={
        "url": "${ctx}/recycle/auctionReturnOrderList/queryListForPage.do",
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
        {"data": "id","class":" center"},
        {"data": "orderNo","class":""},
        {"data": "auctionOrderId","class":""},
        {"data": "expressCode","class":""},
        {"data": "expressCompany","class":""},
        {"data": "doneTime","class":""},
        {"data": "cneeRemark","class":""},
        {"data": "expressType","class":""},
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
        {//复选框
            targets: -2,
            render: function (data, type, row, meta) {
                if(row.expressType==1){
                    return '寄付';
                }
                else{
                    return '到付';
                }
            }
        },

        {
            targets: -1,
            render: function (data, type, row, meta) {
                var context = {
                    func: [
                        {"name" : "查看", "fn" : "showOrderDetail(\'" + row.id + "\')","icon" : "am-icon-pencil-square-o","class" : "am-text-secondary"}
                        //  {"name" : "删除", "fn": "delBtnClick(\'" + row.id + "\')", "icon": "am-icon-trash-o","class" : "am-text-danger"}
                    ]
                };
                var html = template_btn(context);
                return html;
            }
        }
    ]);


    dto.sScrollXInner="120%";
    var myTable = $("#dt").DataTable(dto);

    /**
     * 刷新列表
     */
    function refreshPage(){
        $("#pageStatus").val(1);
        myTable.ajax.reload(null, false);
    }

    function agreedTime(id){
        $("#orderId").val(id);
        $("#modal-insertView").modal("show");
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