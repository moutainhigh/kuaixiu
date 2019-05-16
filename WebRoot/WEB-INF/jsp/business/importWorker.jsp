<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/commons/taglibs.jsp" %>
<link rel="stylesheet" href="${webResourceUrl}/resource/order/css/order.detail.css">
      <div class="am-cf am-padding am-padding-bottom-0">
        <div class="am-fl am-cf" style="width: 100%;">
          <strong class="am-text-primary am-text-lg">商机员工管理</strong> / <small>批量导入</small>
        </div>
      </div>
      
      <hr>
      
      <div class="am-g">
	    <form id="importForm" class="form-horizontal" role="form">
		  <div class="form-group">
	        <label class="col-sm-3 control-label">注意：</label>
	        <div class="col-sm-9">
	          <p class="form-control-static">1、导入数据时请仔细填写表格的每一项数据，并严格按照指定的格式录入。</p>
	        </div>
	      </div>
	      <div class="form-group">
            <label class="col-sm-3 control-label">步骤：</label>
            <div class="col-sm-9">
              <p class="form-control-static">1、点击 <a href="${ctx}/sj/file/download.do?fileId=30" target="_blank">这里</a> 打开Excel表格，在Excel表格中录入数据。</p>
            </div>
          </div>
          <div class="form-group">
            <div class="col-sm-9 col-sm-offset-3">
              <span class="form-control-static form-control-inline pull-left">2、选择Excel数据文件：</span>
              <span class="form-control-static form-control-inline pull-left"><input type="file" class="file" id="fileInput" name="fileInput"></span>
            </div>
          </div>
          <div class="form-group">
            <div class="col-sm-9 col-sm-offset-3">
              <span class="form-control-static form-control-inline pull-left">3、点我、马上导入。</span>
              <button type="button" class="btn btn-default" onclick="uploadModelExcel();">导入数据</button>
            </div>
          </div>
	    </form>
      </div>
      
      <div class="am-g order-info mt20">
        <div id="error_panel" class="panel panel-success index-panel-msg" style="display: none;">
          <div class="row">
            <div id="import_report_id" class="col-md-12 col-sm-12 col-xs-12">
            </div>
          </div>
        </div>
      </div>

<script type="text/javascript" src="${webResourceUrl}/plugins/jquery/ajaxfileupload.js"></script>
<script type="text/javascript">

//导入机型
function uploadModelExcel(){
	var fileName=$("#fileInput").val();
	if(fileName == ""){
		AlertText.tips("d_alert", "提示", "请先选择要导入的Excel数据文件！");
		return false;
	}
	//加载等待
    AlertText.tips("d_loading");
    $.ajaxFileUpload({
        url: "${ctx}/sj/worker/import.do",
        type: "post",
        secureuri: false, //一般设置为false
        fileElementId: "fileInput", // 上传文件的name属性名
        dataType: "json", //返回值类型，一般设置为json、application/json
        //elementIds: elementIds, //传递参数到服务器
        success: function(result){
        	//隐藏等待
            AlertText.hide();
        	if(result.success && result.data && result.data.pass){
        		AlertText.tips("d_alert", "提示", "导入成功！");
        	}else{
        		errorMsg(result.data);
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

function errorMsg(data){
	var rhtml = "";
 	if(!data.continueNext){
        rhtml+="<span class=\"h4 text-danger\">模板不正确，请检查！</span>";
    }else if(!data.pass){
        rhtml+="<table class=\"table table-hover\" style=\"margin-bottom: 0px;\"> ";
        rhtml+="<tr><th>序号</th><th>位置</th><th>错误类型</th><th>错误描述</th></tr>";
        $.each(data.errorList,function(i,o){
            var x=i+1;
            rhtml+="<tr>";
            rhtml+="<td>"+x+"</td>";
            rhtml+="<td>"+o.position+"</td>";
            rhtml+="<td>"+o.msgType+"</td>";
            rhtml+="<td class=\"text-danger\">"+o.message+"</td>";
            rhtml+="</tr>";
        });
        rhtml+="</table>";
    }
 	$("#import_report_id").html(rhtml);
 	if (rhtml == "") {
 		$("#error_panel").hide();
 	} else {
 		$("#error_panel").show();
 	}
}

</script>