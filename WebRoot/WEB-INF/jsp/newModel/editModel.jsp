<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/commons/taglibs.jsp" %>
<div class="modal-backdrop fade in"></div>
<div class="modal-dialog">
  <div class="modal-content">
    <div class="modal-title"><span>编辑兑换机型</span>
      <a href="javascript: void(0);" class="close" data-dismiss="modal" aria-label="Close">&times;</a>
    </div>
    <div class="modal-body">
      <form id="updateForm" method="post" class="form-horizontal">
        <input type="hidden" id="id" name="id" value="${model.id}" />
        <div class="form-group">
          <label for="upBrandId" class="col-sm-2 control-label"><span style="color:red">*</span> 机型品牌</label>
          <div class="col-sm-10">
          	<select id="upBrandId" name="upBrandId" class="form-control" disabled>
              <option value="">--选择品牌--</option>
              <c:forEach items="${brands }" var="item" varStatus="i">
	            <option value="${item.id }" ${model.brandId eq item.id ? 'selected="selected"' : ''}>${item.name }</option>
	          </c:forEach>
            </select>
          </div>
        </div>
        <div class="form-group">
          <label for="upName" class="col-sm-2 control-label"><span style="color:red">*</span> 机型名称</label>
          <div class="col-sm-10">
            <input disabled type="text" id="upName" name="upName" value="${model.name }" class="form-control" placeholder="请输入维修机型名称">
          </div>
        </div>
        <div class="form-group">
          <input type="hidden" id="upColorValue" name="upColorValue" value="${model.color }" />
          <label for="upColor" class="col-sm-2 control-label"><span style="color:red"></span> 颜色</label>
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
              <input type="text" id="upColorInput" name="upColorInput" class="form-control" placeholder="请输入机型支持的颜色">
              <span onclick="upColor();" class="btn input-group-addon">添加</span>
            </div>
          </div>
        </div>
        
         <div class="form-group">
          <label for="upMemory" class="col-sm-2 control-label"><span style="color:red">*</span>内 存</label>
          <div class="col-sm-10">
            <input type="text" disabled id="upMemory" name="upMemory" class="form-control" value="${model.memory}" placeholder="请输入机身内存,单位(G)"><span ></span>
          </div>
        </div>
        
         <div class="form-group" >
          <label for="addEdition" class="col-sm-2 control-label"><span style="color:red">*</span>网络类型</label>
          <div class="col-sm-10" >
            <div class="edition">
              <input disabled name="edition" type="radio"  value="全网通" ${model.edition == "全网通" ? 'checked="checked"' : '' }/>全网通&nbsp
              <input disabled name="edition" type="radio"  value="电信4G" ${model.edition == "电信4G" ? 'checked="checked"' : '' }/>电信4G&nbsp
              <input disabled name="edition" type="radio"  value="移动4G" ${model.edition == "移动4G" ? 'checked="checked"' : '' }/>移动4G&nbsp
              <input disabled name="edition" type="radio"  value="联通4G" ${model.edition == "联通4G" ? 'checked="checked"' : '' }/>联通4G&nbsp
            </div>
            <div class="edition">  
              <input disabled name="edition" type="radio"  value="移动4G/联通4G" ${model.edition == "移动4G/联通4G" ? 'checked="checked"' : '' }/>移动4G/联通4G&nbsp
              <input disabled name="edition" type="radio"  value="移动4G/电信4G" ${model.edition == "移动4G/电信4G" ? 'checked="checked"' : '' }/>移动4G/电信4G&nbsp
              <input disabled name="edition" type="radio"  value="联通4G/电信4G" ${model.edition == "联通4G/电信4G" ? 'checked="checked"' : '' }/>联通4G/电信4G&nbsp
              <input disabled name="edition" type="radio"  value="其他" ${model.edition == "其他" ? 'checked="checked"' : '' }/>&nbsp其他
            </div>
          </div>
        </div>
        
        <div class="form-group">
          <label for="upPrice" class="col-sm-2 control-label"><span style="color:red"></span> 价格</label>
          <div class="col-sm-10">
            <input type="text" id="upPrice" name="upPrice" class="form-control" placeholder="请输入机型价格,单位(元)" value="${model.price}">
          </div>
        </div>
        
          <div class="form-group">
          <label for="addEdition" class="col-sm-2 control-label"><span style="color:red"></span>是否上架</label>
          <div class="col-sm-10">
            <div class="edition">
              <input type="radio" name="addPutaway" value="1" ${model.isPutaway == 1 ? 'checked="checked"' : '' }> 是
              <input type="radio" name="addPutaway" value="2" ${model.isPutaway == 2 ? 'checked="checked"' : '' }> 否
            </div>
          </div>
        </div>
        
        <div class="form-group">
          <label for="upSort" class="col-sm-2 control-label">排序</label>
          <div class="col-sm-10">
            <input type="text" id="upSort" name="upSort" class="form-control" placeholder="请输入显示序号" value="${model.sort }">
          </div>
        </div>
       
           
        
      
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
                upMemory: {
                	 validators : {
                     	notEmpty : {
                              message : "不能为空"
                          },
                     	regexp: {
                             regexp: /^\d+$/,
                             message: '请输入正确数字'
                         }
                     }
                },
                upPrice: {
                	 validators : {
                     	notEmpty : {
                              message : "不能为空"
                          },
                          regexp: {
                              regexp: /^\d/,
                              message: '请输入正确数字'
                          }
                     }
                },
                upSort: {
                    validators : {
                    	notEmpty : {
                            message : "不能为空"
                        },
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
            
            //加载等待
            AlertText.tips("d_loading");
            //校验成功后的操作
            var btn = $("#upSaveBtn");
            //让按钮不能点击
            btn.button("loading");
            //遮盖层
            var options = {
                url : "${ctx}/newModel/update.do",
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