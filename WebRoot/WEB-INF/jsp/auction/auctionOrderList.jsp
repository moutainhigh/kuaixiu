<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/commons/taglibs.jsp" %>
<link rel="stylesheet" type="text/css" href="${webResourceUrl}/resource/order/css/alert.tip.css" />
<div class="am-cf am-padding am-padding-bottom-0">
    <div class="am-fl am-cf" style="width: 100%;">
        <strong class="am-text-primary am-text-lg">回收订单管理</strong> / <small>转转拍卖订单列表</small>
    </div>
</div>

<hr>

<div class="am-g">
    <form id="searchForm" class="form form-horizontal">
        <table id="searchTable">
            <tr>
                <td class="search_th search_th_frist"><label class="control-label">转转订单号 ：</label></td>
                <td class="search_td"><input type="text" name="query_orderNo" class="form-control" ></td>

                <td class="search_th"><label class="control-label">下 单 时 间 ：</label></td>
                <td class="search_td">
                    <div class="am-datepicker-date">
                        <input type="text" id="query_startTime" name="query_startTime" class="form-control am-datepicker-start" data-am-datepicker readonly >
                        <span style="float: left; line-height: 30px; height: 30px; width: 10%; text-align: center;">至</span>
                        <input type="text" id="query_endTime" name="query_endTime" class="form-control am-datepicker-end" data-am-datepicker readonly >
                    </div>
                </td>

                <td class="search_th"><label class="control-label">是否退货：</label></td>
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

                <td class="search_th"><label class="control-label">物流单号：</label></td>
                <td class="search_td"><input type="text" name="query_expressCode" class="form-control" ></td>


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
                <th class="fontWeight_normal tdwidth80">转转站点id</th>
                <th class="fontWeight_normal tdwidth60">转转站点电话</th>
                <th class="fontWeight_normal tdwidth100">拍卖商品名称</th>
                <th class="fontWeight_normal tdwidth80">物流单号</th>
                <th class="fontWeight_normal tdwidth80">物流公司名称</th>
                <th class="fontWeight_normal tdwidth70">订单状态</th>
                <th class="fontWeight_normal tdwidth70">转转订单生成时间</th>
                <th class="fontWeight_normal tdwidth130">操作</th>
            </tr>
            </thead>
            <tbody>

            </tbody>
        </table>
    </div>
</div>


<div class="order_confirm" style="display: none">
    <div class="order_cancel_cont">
        <p>确定发送该订单到转转平台售卖吗</p>
        <input type="text" class="reason" name="" id="salePrice" placeholder="请输入售卖价格" maxlength="20"/>
        <div class="index_but">
            <a href="javascript:void(0);" class="btn-cancel">取消</a>
            <a href="javascript:void (0);" onclick="confrimOrder()" class="btn-confirm">确认售卖</a>
        </div>
    </div>
</div>



<script type="text/javascript">
    var selectId="";  //选择的订单id

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
        "url": "${ctx}/recycle/auctionOrderList/queryListForPage.do",
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
        {"data": "allyId","class":""},
        {"data": "stationTel","class":""},
        {"data": "productDescription","class":""},
        {"data": "expressCode","class":""},
        {"data": "expressCompany","class":""},
        {"data": "orderStatus","class":""},
        {"data": "createTime","class":""},
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

        {
            targets: -1,
            render: function (data, type, row, meta) {
                var context = {
                    func: [
                        //{"name" : "查看", "fn" : "showOrderDetail(\'" + row.id + "\')","icon" : "am-icon-pencil-square-o","class" : "am-text-secondary"},
                        {"name" : "拍照", "fn" : "photograph(\'" + row.id + "\')","icon" : "am-icon-pencil-square-o","class" : "am-text-secondary"},
                        {"name" : "售卖", "fn" : "confirm(\'" + row.id + "\')","icon" : "am-icon-pencil-square-o","class" : "am-text-secondary"},
                        {"name" : "退货", "fn" : "refund(\'" + row.id + "\')","icon" : "am-icon-pencil-square-o","class" : "am-text-secondary"}
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

    /**
     * 取消弹框
     */
    $(".btn-cancel").click(function () {
        // $(".fault_list li").removeClass("fault_in");
        // $(".reason").val("");
        $(".order_confirm").hide();
    });




    /**
     * 申请拍照
     * @param id
     */
    function photograph(id){
        AlertText.tips("d_confirm", "温馨提示", "确定向转转平台申请拍照吗？", function(){
            $.ajax({
                type:'POST',
                url:"${ctx}/recycle/applyPhotograph.do",
                dataType:'json',
                data:{
                    id:id
                },
                success:function (data) {
                    if (data.success){
                        //转转推送成功
                        refreshPage();
                        alert("操作成功");
                    }else {
                        alert(data.resultMessage);     //失败原因
                    }
                },
                error:function (jqXHR) {
                    alertTip('系统异常，请稍后再试！')
                }
            });
        });


    }


    /**
     * 确认售卖
     * @param id
     */
    function confirm(id){
           $(".order_confirm").show();
           selectId=id;
    }

    /**
     * 确认售卖请求地址
     *
     */
    function confrimOrder() {
        var price=$('#salePrice').val();
        if(isEmpty(price)){
            alertTip("请输入售卖价格");
            return;
        }
        $.ajax({
            type:'POST',
            url:"${ctx}/recycle/confirmSale.do",
            dataType:'json',
            data:{
                id:selectId,
                price:price
            },
            success:function (data) {
                if (data.success){
                    //转转推送成功
                    refreshPage();
                    alert("操作成功");
                }else {
                    alert(data.resultMessage);     //失败原因
                }
            },
            error:function (jqXHR) {
                alertTip('系统异常，请稍后再试！')
            }
        });
    }



    /**
     * 申请退货
     * @param id
     */
    function refund(id){
        AlertText.tips("d_confirm", "温馨提示", "确定发送到转转平台申请退货吗？", function(){
            $.ajax({
                type:'POST',
                url:"${ctx}/recycle/refundApply.do",
                dataType:'json',
                data:{
                    id:id
                },
                success:function (data) {
                    if (data.success){
                        //转转推送成功
                        refreshPage();
                        alert("操作成功");
                    }else {
                        alert(data.resultMessage);     //失败原因
                    }
                },
                error:function (jqXHR) {
                    alertTip('系统异常，请稍后再试！')
                }
            });
        });


    }

</script>