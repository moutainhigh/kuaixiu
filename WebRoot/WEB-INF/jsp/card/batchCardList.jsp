<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/commons/taglibs.jsp" %>
<div class="am-cf am-padding am-padding-bottom-0">
    <div class="am-fl am-cf" style="width: 100%;">
        <strong class="am-text-primary am-text-lg">批次管理</strong> / <small>列表查询</small>
    </div>
</div>

<hr>


<div class="am-g">
    <form id="searchForm" class="form form-horizontal">
        <table id="searchTable">
            <tr>
                <td class="search_th search_th_frist"><label class="control-label">&nbsp&nbsp&nbsp批次号 ：</label></td>
                <td class="search_td"><input type="text" name="query_batchId" class="form-control" ></td>

                <td class="search_th"><label class="control-label">导入时间：</label></td>
                <td class="search_td">
                    <div class="am-datepicker-date">
                        <input type="text" id="query_startTime" name="query_startTime" class="form-control am-datepicker-start" data-am-datepicker readonly >
                        <span style="float: left; line-height: 30px; height: 30px; width: 10%; text-align: center;">至</span>
                        <input type="text" id="query_endTime" name="query_endTime" class="form-control am-datepicker-end" data-am-datepicker readonly >
                    </div>
                </td>
            </tr>

            <tr>
                <td class="search_th"><label class="control-label">号卡类别 ：</label></td>
                <td class="search_td">
                    <select name="query_cardType" class="form-control">
                        <option value="">--选择类别--</option>
                        <option value="0">小白卡</option>
                        <option value="1">即买即通卡</option>
                    </select>
                </td>

                <td class="search_th"><label class="control-label">是否分配 ：</label></td>
                <td class="search_td">
                    <select name="query_isDistribution" class="form-control">
                        <option value="">--选择状态--</option>
                        <option value="1">是</option>
                        <option value="0">否</option>
                    </select>
                </td>
                <td></td>
            </tr>



            <tr>
                <td class="search_th"><label class="control-label">号卡名称 ：</label></td>
                <td class="search_td">
                    <select name="query_cardName" class="form-control">
                        <option value="">--选择名称--</option>
                        <option value="0">白金卡</option>
                        <option value="1">抖音卡</option>
                        <option value="2">鱼卡</option>
                        <option value="3">49元不限流量卡</option>
                        <option value="4">99元不限流量卡</option>
                        <option value="5">199元不限流量卡</option>
                        <option value="6">29元不限流量卡</option>
                    </select>
                </td>

                <td class="search_th"><label class="control-label">地市 ：</label></td>
                <td class="search_td">
                    <select name="query_city" class="form-control">
                        <option value="">--选择地市--</option>
                        <c:forEach items="${list}" var="item" varStatus="i">
                            <option value="${item}">${item}</option>
                        </c:forEach>
                    </select>
                </td>
                <td></td>
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
                <th class="fontWeight_normal table-title center">导入时间</th>
                <th class="fontWeight_normal table-title center">导入批次</th>
                <th class="fontWeight_normal table-title center">所属本地网</th>
                <th class="fontWeight_normal table-title center">总数量(张)</th>
                <th class="fontWeight_normal table-title center">待分配数量(张)</th>
                <th class="fontWeight_normal table-title center">号卡类别</th>
                <th class="fontWeight_normal table-title center">号卡名称</th>
                <th class="fontWeight_normal table-date tdwidth130  center">失效时间</th>
                <th class="fontWeight_normal table-set tdwidth200">操作</th>
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
        "url": "${ctx}/telecom/batch/queryListForPage.do",
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
        {"data": "batchId","class":"tdwidth50 center"},
        {"data": "inTime","class":""},
        {"data": "batchId","class":""},
        {"data": "province","class":""},
        {"data": "sum","class":""},
        {"data": "distributionSum","class":""},
        {"data": "type","class":""},
        {"data": "cardName","class":""},
        {"data": "loseEfficacy","class":""},
        {"defaultContent": "操作","class":""}
    ]);
    //设置定义列的初始属性
    dto.setColumnDefs([
        {//复选框
            targets: 0,
            render: function (data, type, row, meta) {
                var context = {
                    func: [
                        {"id": row.batchId, "order" : meta.row + 1}
                    ]
                };
                var html = template_chk(context);
                return html;
            }
        },
        {
            targets: 5,
            render: function (data, type, row, meta) {
                    return row.sum-row.distributionSum;
            }
        },
        {//号卡类型
            targets: 6,
            render: function (data, type, row, meta) {
                if(row.type==0){
                    return '小白卡';
                }
                else{
                    return '即买即通卡';
                }
            }
        },
        {
            targets: 7,
            render: function (data, type, row, meta) {
                var state = "";
                switch(row.cardName){
                    case 0:
                        state = "白金卡";
                        break;
                    case 1:
                        state = "抖音卡";
                        break;
                    case 2:
                        state = "鱼卡";
                        break;
                    case 3:
                        state = "49元不限流量卡";
                        break;
                    case 4:
                        state = "99元不限流量卡";
                        break;
                    case 5:
                        state = "199元不限流量卡";
                        break;
                    case 6:
                        state = "29元不限流量卡";
                        break;
                    default:
                        state = "";
                }
                return state;
            }
        },
        {//失效时间  格式化
            targets: -2,
            render: function (data, type, row, meta) {
                return format(row.loseEfficacy);
            }
        },
        {
            targets: -1,
            render: function (data, type, row, meta) {
                if((row.sum-row.distributionSum)<=0){
                    var context = {
                        func: [
                            {"name" : "详情", "fn" : "detail(\'" + row.batchId + "\')","icon" : "am-icon-pencil-square-o","class" : "am-text-secondary"},
                            {"name" : "删除", "fn": "delBtnClick(\'" + row.batchId + "\')", "icon": "am-icon-trash-o","class" : "am-text-danger"}
                        ]
                    };
                }else{
                    var context = {
                        func: [
                            {"name" : "分配", "fn" : "distributionBtnClick(\'" + row.batchId + "\')","icon" : "am-icon-pencil-square-o","class" : "am-text-secondary"},
                            {"name" : "详情", "fn" : "detail(\'" + row.batchId + "\')","icon" : "am-icon-pencil-square-o","class" : "am-text-secondary"},
                            {"name" : "删除", "fn": "delBtnClick(\'" + row.batchId + "\')", "icon": "am-icon-trash-o","class" : "am-text-danger"}
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

    function addBtnClick(){
        $("#modal-insertView").modal("show");
    }

    /**
     * 查看批次详情
     */
    function detail(id){
        func_reload_page("${ctx}/telecom/batch/batchDetail.do?id="+id);
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



    // function delBtnClick(id){
    //     AlertText.tips("d_confirm", "删除提示", "确定要删除吗？", function(){
    //         //加载等待
    //         AlertText.tips("d_loading");
    //         var url_ = AppConfig.ctx + "/brand/delete.do";
    //         var data_ = {id: id};
    //         $.ajax({
    //             url: url_,
    //             data: data_,
    //             type: "POST",
    //             dataType: "json",
    //             success: function (result) {
    //                 if (result.success) {
    //                     //保存成功,关闭窗口，刷新列表
    //                     refreshPage();
    //                 } else {
    //                     AlertText.tips("d_alert", "提示", result.msg);
    //                     return false;
    //                 }
    //                 //隐藏等待
    //                 AlertText.hide();
    //             }
    //         });
    //     });
    // }


    /**
     * 分配页面
     */
    function distributionBtnClick(id){
        func_reload_page("${ctx}/telecom/card/distribution.do?batchId="+id);
    }


    function format(time){
        var date = new Date(time);
        var year = date.getFullYear(),
            month = date.getMonth() + 1,//月份是从0开始的
            day = date.getDate();
        var newTime = year + '-' +
            month + '-' +
            day;
        return newTime;
    }

</script>