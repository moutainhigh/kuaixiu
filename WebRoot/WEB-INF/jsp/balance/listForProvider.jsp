<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/commons/taglibs.jsp" %>
      <div class="am-cf am-padding am-padding-bottom-0">
        <div class="am-fl am-cf" style="width: 100%;">
          <strong class="am-text-primary am-text-lg">对账管理</strong> / <small>结算单查询</small>
        </div>
      </div>
      
      <hr>
      
      <div class="am-g hide">
	    <form id="searchForm" class="form form-horizontal">
		  <div class="form-group">
		    <input type="file" id="uploadFile" name="uploadFile" onchange="uploadImg();" style="display: none;" />
		    <div class="am-u-sm-12 am-u-md-6">
		      <h4><label class="radio-inline"></label></h4>
		    </div>
	      </div>
	    </form>
      </div>

      <div class="am-g">
        <div class="am-u-sm-12">
            <table id="dt" class="table table-striped table-bordered table-radius table-hover">
              <thead>
	            <tr>
	              <th class="fontWeight_normal tdwidth30">序号</th>
                  <th class="fontWeight_normal tdwidth100">结算单号</th>
                  <th class="fontWeight_normal tdwidth150">结算日期</th>
                  <th class="fontWeight_normal tdwidth80">订单数量</th>
                  <th class="fontWeight_normal tdwidth100">结算金额(元)</th>
                  <th class="fontWeight_normal tdwidth60">打款回执</th>
	              <th class="fontWeight_normal tdwidth70">操作</th>
	            </tr>
              </thead>
              <tbody>
              
              </tbody>
            </table>
        </div>
      </div>

<!-- 新增弹窗 end -->
<div id="modal-imgView" class="modal fade" tabindex="-1" role="dialog" aria-hidden="true" style="display: none;">
    <div class="modal-backdrop fade in"></div>
	<div class="modal-dialog">
	  <div class="modal-content">
	    <div class="modal-title"><span>查看打款回执</span>
	      <a href="javascript: void(0);" class="close" data-dismiss="modal" aria-label="Close">&times;</a>
	    </div>
	    <div class="modal-body" style="text-align: center;">
	      <img id="bankSlipImg" alt="查看打款回执" src="" style="max-width: 100%; max-height: 360px;">
	    </div>
	    <div class="modal-footer">
	      <button type="button" id="addMissBtn" class="btn modal-btn" data-dismiss="modal" aria-label="Close"><span class="am-icon-close icon-close"></span>关闭</button>
	    </div>
	  </div><!-- /.modal-content -->
    </div><!-- /.modal-dialog -->
</div>
<!-- 新增弹窗 end -->  

<script type="text/javascript" src="${webResourceUrl}/plugins/jquery/ajaxfileupload.js"></script>
<script type="text/javascript">

//自定义datatable的数据
var dto=new DtOptions();
//设置数据刷新路径
dto.ajax={
	    "url": "${ctx}/balanceProvider/queryListForPage.do",
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
    {"data": "balanceNo","class":""},
    {"data": "balanceBeginTime","class":""},
    {"data": "orderCount","class":""},
    {"data": "amountPrice","class":""},
    {"data": "status","class":"center"},
    {"defaultContent": "操作","class":""}   
]);
//设置定义列的初始属性
dto.setColumnDefs([
    {//复选框
    	targets: 0,
        render: function (data, type, row, meta) {
            return meta.row + 1;
        }
    },
    {//负责人
        targets: 2,
        render: function (data, type, row, meta) {                           
            return row.balanceBeginTime + " 至 " + row.balanceEndTime;
        }
    },
    {//负责人
        targets: -2,
        render: function (data, type, row, meta) {        
        	var html = "";
        	if(row.status == 0){
                html += "未上传";
            }else{
                html += "<a onclick=\"showImg('"+ row.bankSlipImg +"');\" href='javascript:void(0);'>查看</a> ";
            }
        	return html;
        }
    },
	{
	    targets: -1,
	    render: function (data, type, row, meta) {
	        var context = {
	            func: [
	                {"name" : "明细", "fn" : "toDetail(\'" + row.id + "\')","icon" : "am-icon-pencil-square-o","class" : "am-text-secondary"}
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
function refreshPage(){
	myTable.ajax.reload(null, false);
}

/**
 * 查看打款回执
 */
function showImg(src){
	$("#bankSlipImg").attr("src", AppConfig.ctx + src)
	$("#modal-imgView").modal("show");
    $("#modal-imgView").css("display", "block");
    $("#modal-imgView").addClass("in");
}

/**
 * 查看订单详情
 */
function toDetail(id){
	func_reload_page("${ctx}/balanceProvider/balanceDetail.do?id="+id);
}

//选择文件
function selectFile(id){
	var uploadFile = document.getElementById("uploadFile");
	$(uploadFile).attr("balanceId", id);
	uploadFile.click();
}

var isImg=/.+\.bmp$|.+\.jpeg$|.+\.jpg$|.+\.gif$|.+\.png$/;
//导入打款回执
function uploadImg(){
    var fileName=$("#uploadFile").val();
    if(fileName == ""){
        AlertText.tips("d_alert", "提示", "请先选择要上传打款回执单，允许的图片格式：bmp, jpeg, jpg, gif, png！不可大于10M");
        return false;
    }
    if(!isImg.test(fileName)){
    	AlertText.tips("d_alert", "提示", "请先选择要上传打款回执单，允许的图片格式：bmp, jpeg, jpg, gif, png！不可大于10M");
        return false;
    }
    var balanceId = $("#uploadFile").attr("balanceId");
    //加载等待
    AlertText.tips("d_loading");
    $.ajaxFileUpload({
        url: "${ctx}/balanceProvider/uploadImg.do", 
        type: "post",
        secureuri: false, //一般设置为false
        fileElementId: "uploadFile", // 上传文件的name属性名
        dataType: "json", //返回值类型，一般设置为json、application/json
        data: {id: balanceId}, //传递参数到服务器
        success: function(result){
            //隐藏等待
            AlertText.hide();
            if(result.success){
                AlertText.tips("d_alert", "提示", "上传成功！", function(){
                	refreshPage();
                });
            }else{
                errorMsg(result.msg);
            }
            //writeExcelReport(data);
        },
        error: function(data, status, e){ 
            //隐藏等待
            AlertText.hide();
            AlertText.tips("d_alert", "提示", "上传失败,请稍后再试！");
        }
    });
}

function deleteImg(id){
    AlertText.tips("d_confirm", "删除提示", "确定要删除打款回执吗？删除后请重新上传", function(){
        //加载等待
        AlertText.tips("d_loading");
        var url_ = AppConfig.ctx + "/balanceProvider/deleteImg.do";
        var data_ = {id: id};
        $.ajax({
            url: url_,
            data: data_,
            type: "POST",
            dataType: "json",
            success: function (result) {
                //隐藏等待
                AlertText.hide();
                if (result.success) {
                    //保存成功,关闭窗口，刷新列表
                    AlertText.tips("d_alert", "提示", "删除成功！", function(){
                        refreshPage();
                    });
                } else {
                    AlertText.tips("d_alert", "提示", result.msg);
                    return false;
                }
            },
            error: function(data, status, e){ 
                //隐藏等待
                AlertText.hide();
                AlertText.tips("d_alert", "提示", "系统异常,请稍后再试！");
            }
        });
    });
}

</script>