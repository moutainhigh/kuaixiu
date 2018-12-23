<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/commons/taglibs.jsp" %>
<div class="modal-backdrop fade in"></div>
<div class="modal-dialog">
  <div class="modal-content">
    <div class="modal-title"><span>编辑维修机型</span>
      <a href="javascript: void(0);" class="close" data-dismiss="modal" aria-label="Close">&times;</a>
    </div>
    <div class="modal-body">
      <form id="updateForm" method="post" class="form-horizontal">
        <input type="hidden" id="id" name="id" value="${model.id}" />
        <div class="form-group">
          <label for="upBrandId" class="col-sm-2 control-label"><span style="color:red">*</span> 机型品牌</label>
          <div class="col-sm-10">
          	<select id="upBrandId" name="upBrandId" class="form-control">
              <option value="">--选择品牌--</option>
              <c:forEach items="${brands }" var="item" varStatus="i">
	            <option value="${item.id }" ${model.brandId eq item.id ? 'selected="selected"' : ''}>${item.name }</option>
	          </c:forEach>
            </select>
          </div>
        </div>
        <div class="form-group">
          <label for="upName" class="col-sm-2 control-label"><span style="color:red">*</span> 维修机型</label>
          <div class="col-sm-10">
            <input type="text" id="upName" name="upName" value="${model.name }" class="form-control" placeholder="请输入维修机型名称">
          </div>
        </div>
        <div class="form-group">
          <input type="hidden" id="upColorValue" name="upColorValue" value="${model.color }" />
          <label for="upColor" class="col-sm-2 control-label"><span style="color:red">*</span> 颜色</label>
          <div id="upColorLabelDiv" class="col-sm-10">
            <c:forEach items="${model.colors}" var="item" varStatus="i">
              <c:if test="${item != ''}">
	          <div class="label alert label-warning alert-dismissible" role="alert">  
	              <button onclick="upRemoveColor('${item}');" type="button" class="close" data-dismiss="alert">
	                  <span aria-hidden="true">×</span><span class="sr-only">Close</span>
	              </button>  
	              <strong>${item }</strong>
	          </div>
	          </c:if>
            </c:forEach>
          </div>
        </div>
        <div id="upColorBtn" class="form-group">
          <div class="col-sm-10 col-sm-offset-2">
            <div class="input-group">
              <input type="text" id="upColorInput" name="upColorInput" class="form-control" placeholder="请输入维修机型支持的颜色">
              <span onclick="upColor();" class="btn input-group-addon">添加</span>
            </div>
          </div>
        </div>
        <div class="form-group">
          <label for="upSort" class="col-sm-2 control-label">排序</label>
          <div class="col-sm-10">
            <input type="text" id="upSort" name="upSort" class="form-control" placeholder="请输入显示序号" value="${model.sort }">
          </div>
        </div>
        <div class="form-group">
          <input type="hidden" id="upRepairCostValue" name="upRepairCostValue" />
          <label for="upColor" class="col-sm-2 control-label"><span style="color:red">*</span> 故障类型</label>
        </div>
        <c:forEach items="${projects}" var="item" varStatus="i">
          <c:set var="cost" value="${null }"></c:set>
          <c:forEach items="${repairCosts}" var="repCost">
            <c:if test="${item.id == repCost.projectId }">
              <c:set var="cost" value="${repCost }"></c:set>
            </c:if>
          </c:forEach>
          <div class="form-group">
            <div class="col-sm-3 col-sm-offset-1">
              <div class="checkbox">
                <label>
                  <input type="checkbox" name="upProjectId" ${cost != null ? 'checked="checked"' : '' } value="${item.id }">${item.name }
                </label>
              </div>
            </div>
            <div class="col-sm-8 ">
              <div class="">
                <label for="upRepairCost" class="col-sm-4 control-label"> 价格：</label>
                <div class="col-sm-8">
                  <input type="text" name="upRepairCost" value="${cost.price }" class="form-control" placeholder="请输入维修价格">
                </div>
              </div>
            </div>
          </div>
        </c:forEach>
        <button type="submit" class="hide" id="upSubmitBtn"></button>
      </form>
    </div>
    <div class="modal-footer">
      <button type="button" id="upSaveBtn" class="btn modal-btn" ><span class="am-icon-save icon-save"></span>提交</button>
      <button type="button" id="upMissBtn" class="btn modal-btn" data-dismiss="modal" aria-label="Close"><span class="am-icon-close icon-close"></span>取消</button>
    </div>
  </div><!-- /.modal-content -->
</div><!-- /.modal-dialog -->

<script type="text/javascript">

//表单验证
$(document).ready(function() {
    updateValidatorForm();
});

//初始化表单
function updateValidatorForm() {
    $("#updateForm")
        .bootstrapValidator({
            message : "不能为空",
            feedbackIcons : {
                valid : 'glyphicon glyphicon-ok',
                invalid : 'glyphicon glyphicon-remove',
                validating : 'glyphicon glyphicon-refresh'
            },
            fields : {
            	upBrandId: {
                    validators : {
                        notEmpty : {
                            message : "不能为空"
                        }
                    }
                },
                upName: {
                    validators : {
                        notEmpty : {
                            message : "不能为空"
                        }
                    }
                },
                upSort: {
                    validators : {
                    	regexp: {
                            regexp: /^\d+$/,
                            message: '请输入正确数字'
                        }
                    }
                }
            }// end fields
        }).on("success.form.bv", function(e) {
            // 阻止表单提交
            e.preventDefault();
            // 验证颜色是否添加
            //console.log("ddd");
            // 如果需要保存输入框的颜色，先调用添加按钮事件在判断
            var color = $("#upColorValue").val();
            if(color == ""){
                AlertText.tips("d_alert", "提示", "请添加颜色！");
                $("#updateForm").data("bootstrapValidator").resetForm();
                return false;
            }
            // 验证是否选择故障
            var chkboxs = $("input[name='upProjectId']");
            var costs = $("input[name='upRepairCost']");
            var json = "";
            for(var i = 0; i < chkboxs.size(); i++){
                if(chkboxs[i].checked){
                    if(isNumber(costs[i].value)){
                        json += ",{projectId:'" + chkboxs[i].value + "',cost:'" + costs[i].value + "'}";
                    }else{
                        AlertText.tips("d_alert", "提示", "请给选择维修类型填写维修金额！");
                        $("#updateForm").data("bootstrapValidator").resetForm();
                        return false;
                    }
                }
            }
            if (json != "") {
                json = "[" + json.substring(1, json.length) + "]";
            } else {
                AlertText.tips("d_alert", "提示", "请选择维修类型！");
                $("#updateForm").data("bootstrapValidator").resetForm();
                return false;
            }
            $("#upRepairCostValue").val(json);
            // 及是否输入金额
            //加载等待
            AlertText.tips("d_loading");
            //校验成功后的操作
            var btn = $("#upSaveBtn");
            //让按钮不能点击
            btn.button("loading");
            //遮盖层
            var options = {
                url : "${ctx}/model/update.do",
                dataType : "JSON",
                success : function(data) {
                    if(data.success){
                        //保存成功,关闭窗口，刷新列表
                        refreshPage();
                        //全部更新完后关闭弹窗
                        $("#upMissBtn").click();
                        //重置表单数据
                        document.getElementById("updateForm").reset();
                    }else{
                        //保存失败
                        alert(data.msg);
                    }
                    upFormReset();
                },
                error : function() {
                    alert("系统异常，请稍后再试");
                    upFormReset();
                }
            }; // end options
            $("#updateForm").ajaxSubmit(options);
        }); // end on("success.form.bv"
}

/**
 * 重置表单
 */
function upFormReset(){
    //重置表单验证
    $("#updateForm").data("bootstrapValidator").resetForm();
    //让按钮重新能点击
    $("#upSaveBtn").button("reset");
    //隐藏等待
    AlertText.hide();
}

//点击保存按钮,提交form表单，触发校验
$("#upSaveBtn").click(function() {
    //格式化分类属性信息为JSON串
    $("#upSubmitBtn").click();
});

function upColor(){
    //获取输入的颜色
    var color = $("#upColorInput").val();
    $("#upColorInput").val("")
    color = color.trim();
    //console.log("ddd");
    if(color != ""){
        upbuildColorLabel(color)
    }else{
        AlertText.tips("d_alert", "提示", "请输入颜色！");
    }
}

function upbuildColorLabel(color){
    var colors = color.replace("，", ",").split(",");
    var html = "";
    //判断颜色是否已经存在
    var colorOld = $("#upColorValue").val().split(",");
    $.each(colors,function(i,item){
        if(colorOld.indexOf(item) < 0){
            //alert(item);
            html += "<div class='label alert label-warning alert-dismissible' role='alert'>";
            html += "  <button onclick=\"upRemoveColor('" + item + "');\" type='button' class='close' data-dismiss='alert'><span aria-hidden='true'>&times;</span><span class='sr-only'>Close</span></button>";
            html += "  <strong>" + item + "</strong>";
            html += "</div>";
            colorOld.push(item);
        }
    });
    //清空内容
    $("#upColorLabelDiv").append(html);
    $("#upColorValue").val(colorOld);
}

function upRemoveColor(color){
    var colorOld = $("#upColorValue").val().split(",");
    var i = colorOld.indexOf(color);
    if(i >=0){
        colorOld.splice(i,1);
        $("#upColorValue").val(colorOld);
    }
}

</script>