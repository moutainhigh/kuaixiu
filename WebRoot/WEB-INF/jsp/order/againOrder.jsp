<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/commons/taglibs.jsp" %>
<div class="modal-backdrop fade in"></div>
<div class="modal-dialog">
  <div class="modal-content">

    <div class="modal-title"><span>重新派单</span>
      <a href="javascript: void(0);" class="close" onclick="clean()" data-dismiss="modal" aria-label="Close">&times;</a>
    </div>
    <div class="modal-body">

      <form id="againOrderSearchForm" method="post" class="form-horizontal">
          <label class="col-sm-3 control-label">筛选条件</label><br/>
          <input type="hidden" id="againOrderId" name="againOrderId" /><br/>
        <div class="form-group">
          <label for="engName" class="col-sm-3 control-label">工程师姓名</label>
          <div class="col-sm-7">
            <input type="text" id="engName" name="engName" class="form-control" placeholder="请填写工程师姓名"><br/>
          </div>
          <label for="engCode" class="col-sm-3 control-label">工程师工号</label>
          <div class="col-sm-7">
            <input type="text" id="engCode" name="engCode" class="form-control" placeholder="请填写工程师工号">
          </div>
        </div>
        <button type="submit" class="hide" id="againOrderBtn"></button>
      </form>
    </div>
    <div class="modal-footer">
      <button type="button" id="queryListAgainOrder" class="btn modal-btn" ><span class="am-icon-save icon-save"></span>开始查找</button>
      <button type="button" id="addMissAgainOrderBtn" onclick="clean()" class="btn modal-btn" data-dismiss="modal" aria-label="Close"><span class="am-icon-close icon-close"></span>取消</button>
    </div>

    <div class="am-g">
      <div class="am-u-sm-12">
        <table id="dtAgain" class="table table-striped table-bordered table-radius table-hover">
          <thead>
          <tr>
            <th class="fontWeight_normal tdwidth50">序号</th>
            <th class="fontWeight_normal tdwidth90">工号/名称</th>
            <th class="fontWeight_normal tdwidth90">是否在线</th>
            <th class="fontWeight_normal tdwidth160">所属门店</th>
            <th class="fontWeight_normal tdwidth160">所属商</th>
            <th class="fontWeight_normal tdwidth90">未完成订单数</th>
            <th class="fontWeight_normal tdwidth70">操作</th>
          </tr>
          </thead>
          <tbody>

          </tbody>
        </table>
      </div>
    </div>

  </div><!-- /.modal-content -->
</div><!-- /.modal-dialog -->

<script type="text/javascript">

    <%--清空--%>
    function clean() {
        $('#dtAgain tbody').html("");
        $('#engName').val("");
        $('#engCode').val("");
    }

    var myTable2=null;

    $("#queryListAgainOrder").click(function() {
        var engCode=$("#engCode").val();
        var engName=$("#engName").val();
        if(engName==""&&engCode==""){
            alert("不能为空");
        } else {
            if(myTable2==null){
                myTable2=$("#dtAgain").DataTable(dtAgain);
            }else{
                myTable2.ajax.reload(null, false);
            }
        }
    });

    //自定义datatable的数据
    var dtAgain=new DtOptions();
    //设置数据刷新路径
    dtAgain.ajax={
        "url": "${ctx}/order/againOrder.do",
        "data":function(d){
            //将表单中的查询条件追加到请求参数中
            var array = $("#againOrderSearchForm").serializeArray();
            $.each(array, function() {
                d[this.name] = this.value;
            });
        }
    };

    //设置数据列
    dtAgain.setColumns([
        {"data": "id","class":"center"},
        {"data": "number","class":""},
        {"data": "isDispatch","class":""},
        {"data": "shopName","class":""},
        {"data": "providerName","class":""},
        {"data": "orderDayNum","class":""},
        {"defaultContent": "操作","class":""}
    ]);
    //设置定义列的初始属性
    dtAgain.setColumnDefs([
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
            targets: 1,
            render: function (data, type, row, meta) {
                return row.number + "/<br/>" + row.name;
            }
        },
        {//复选框
            targets: 2,
            render: function (data, type, row, meta) {
                if(row.isDispatch!=null) {
                    if (row.isDispatch==2) {
                        return "已下线";
                    }else{
                        return "在线"
                    }
                }
            }
        },
        {
            targets: -1,
            render: function (data, type, row, meta) {
                    var context = {
                        func: [
                            {"name" : "确认派单", "fn" : "resetOrder(\'" + row.id + "\')","icon" : "am-icon-pencil-square-o","class" : "am-text-secondary"}
                        ]
                    };
                    var html = template_btn(context);
                    return html;
            }
        }
    ]);

    function resetOrder(id){
        AlertText.tips("d_confirm", "系统提示", "确定要重新派单吗？。", function() {
            var againOrderId = $("#againOrderId").val();
            //加载等待
            AlertText.tips("d_loading");
            var url_ = AppConfig.ctx + "/order/reDispatch.do";
            var data_ = {orderId: againOrderId,engineerId:id};
            $.ajax({
                url: url_,
                data: data_,
                type: "POST",
                dataType: "json",
                success: function (result) {
                    if (result.success) {
                        AlertText.tips("d_alert", "提示", "重新派单成功", function(){
                            clean();
                            refreshPage();
                            //全部更新完后关闭弹窗
                            $("#addMissAgainOrderBtn").click();
                        });
                    } else {
                        AlertText.tips("d_alert", "提示", result.msg);
                        clean();
                        return false;
                    }
                },
                error : function() {
                    AlertText.tips("d_alert", "提示", "系统异常，请稍后再试");
                    loading_hide();
                    clean();
                    isLoading = false;
                }
            });
        });
    }




</script>