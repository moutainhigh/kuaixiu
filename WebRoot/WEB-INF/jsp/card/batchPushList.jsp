<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/commons/taglibs.jsp" %>
<div class="am-cf am-padding am-padding-bottom-0">
    <div class="am-fl am-cf" style="width: 100%;">
        <strong class="am-text-primary am-text-lg">批次管理</strong> / <small>推送列表查询</small>
    </div>
</div>

<hr>

<div class="am-g">
    <form id="searchForm" class="form form-horizontal">
        <table id="searchTable">
            <tr>
                <td class="search_th search_th_frist"><label class="control-label">&nbsp&nbsp&nbsp批次号 ：</label></td>
                <td class="search_td"><input type="text" name="query_batchId" class="form-control" ></td>

                <td class="search_th search_th_frist"><label class="control-label">&nbsp&nbsp&nbsp站点ID ：</label></td>
                <td class="search_td"><input type="text" name="query_stationId" class="form-control" ></td>
                <td></td>
            </tr>

            <tr>
                <td class="search_th search_th_frist"><label class="control-label">&nbsp&nbsp&nbsp物流单号 ：</label></td>
                <td class="search_td"><input type="text" name="query_number" class="form-control" ></td>

                <td class="search_th"><label class="control-label">推送状态 ：</label></td>
                <td class="search_td">
                    <select name="query_status" class="form-control">
                        <option value="">--选择状态--</option>
                        <option value="0">成功</option>
                        <option value="1">失败</option>
                    </select>
                </td>
                <td></td>
            </tr>



            <tr>
                <td class="search_th"><label class="control-label">号卡类别 ：</label></td>
                <td class="search_td">
                    <select name="query_type" class="form-control">
                        <option value="">--选择类别--</option>
                        <option value="0">小白卡</option>
                        <option value="1">即买即通卡</option>
                    </select>
                </td>


                <td class="search_th"><label class="control-label">号卡名称 ：</label></td>
                <td class="search_td">
                    <select name="query_name" class="form-control">
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
                <th class="fontWeight_normal table-title center">批次号</th>
                <th class="fontWeight_normal table-title center">站点id</th>
                <th class="fontWeight_normal table-title center">发货时间</th>
                <th class="fontWeight_normal table-title center">物流单号</th>
                <th class="fontWeight_normal table-title center">物流公司</th>
                <th class="fontWeight_normal table-title center">起始ICCID</th>
                <th class="fontWeight_normal table-title center">结束ICCID</th>
                <th class="fontWeight_normal table-title center">数量</th>
                <th class="fontWeight_normal table-title center">号卡类型</th>
                <th class="fontWeight_normal table-title center">号卡名称</th>
                <th class="fontWeight_normal table-title center">推送状态</th>
                <th class="fontWeight_normal table-title center">推送时间</th>
                <th class="fontWeight_normal table-title center">异常信息</th>
                <th class="fontWeight_normal table-set tdwidth70">操作</th>
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

    $("#query_startDistributionTime").datetimepicker({
        format: "yyyy-mm-dd",
        language: "zh-CN",
        autoclose:true,//选中关闭
        minView: "month"//设置只显示到月份
    });

    $("#query_endDistributionTime").datetimepicker({
        format: "yyyy-mm-dd",
        language: "zh-CN",
        autoclose:true,//选中关闭
        minView: "month"//设置只显示到月份
    });


    //自定义datatable的数据
    var dto=new DtOptions();
    //设置数据刷新路径
    dto.ajax={
        "url": "${ctx}/telecom/push/queryListForPage.do",
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
        {"data": "batch","class":""},
        {"data": "stationId","class":""},
        {"data": "sendTime","class":""},
        {"data": "expressNumber","class":""},
        {"data": "expressCompany","class":""},
        {"data": "startIccid","class":""},
        {"data": "endIccid","class":""},
        {"data": "sum","class":""},
        {"data": "cardType","class":""},
        {"data": "cardName","class":""},
        {"data": "status","class":""},
        {"data": "inTime","class":""},
        {"data": "msg","class":""},
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
        {//发货时间
            targets: 3,
            render: function (data, type, row, meta) {
                return format(row.sendTime);
            }
        },
        {//号卡类型
            targets: -6,
            render: function (data, type, row, meta) {
                if(row.cardType==0){
                    return '小白卡';
                }
                else{
                    return '即买即通卡';
                }
            }
        },
        {
            targets: -5,
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
        {//推送状态
            targets: -4,
            render: function (data, type, row, meta) {
                if(row.status==1){
                    return '失败';
                }
                else{
                    return '成功';
                }
            }
        },
        {
            targets: -1,
            render: function (data, type, row, meta) {
                if(row.status==1){
                    var context = {
                        func: [
                            {"name" : "重新推送", "fn" : "push(\'" + row.id + "\')","icon" : "am-icon-pencil-square-o","class" : "am-text-secondary"}
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
     * 重新推送
     * @param id
     */
    function push(id) {
        var url_ = AppConfig.ctx + "/telecom/batch/rePush.do";
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
                    refreshPage();
                    return false;
                }
                //隐藏等待
                AlertText.hide();
            }
        });
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