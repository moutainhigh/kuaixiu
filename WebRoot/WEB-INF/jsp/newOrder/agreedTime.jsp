<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/commons/taglibs.jsp" %>
<div class="modal-backdrop fade in"></div>
<div class="modal-dialog">
  <div class="modal-content">
    <div class="modal-title"><span>预约维修时间</span>
      <a href="javascript: void(0);" class="close" data-dismiss="modal" aria-label="Close">&times;</a>
    </div>
    <div class="modal-body">
      <form id="insertForm" method="post" class="form-horizontal">
      	<input type="hidden" id="orderId" name="orderId" />
        <div class="form-group">
          <label for="agreedTime" class="col-sm-3 control-label"><span style="color:red">*</span> 约定时间</label>
          <div class="col-sm-7">
            <input type="text" id="agreedTime" name="agreedTime" class="form-control" placeholder="请选择约定时间">
          </div>
        </div>
        
       <!-- 
       <label><input name="choose" type="radio"  value="0"  checked style="position:relative;top:25px;left:50px;z-index:10;"/>
        -->
          
        <!-- 
          <label for="agreedModel" class="col-sm-3 control-label"><span style="color:red"></span> 已有机型</label>
             -->
             
             <div class="form-group" >
              <label for="agreedTime" class="col-sm-3 control-label"><span style="color:red">*</span>品牌</label>
             <div class="col-sm-7">
                <select id="query_brand" name="query_brand" onchange="brandChange(this.value);" class="form-control-inline">
	               <option value="">--请选择品牌--</option>
	              <c:forEach items="${newBrands }" var="item" varStatus="i">
	                <option value="${item.id }">${item.name }</option>
	              </c:forEach>
	            </select>
	            </div>
	         </div>
	         
	            
	            
	             <div class="form-group" >
	             <label for="agreedTime" class="col-sm-3 control-label"><span style="color:red">*</span> 机型</label>
                     <div class="col-sm-7">
	             <select id="query_model" name="query_model"   onchange="selectModel(this.value);" class="form-control-inline" >
	              <option value="">--请选择机型--</option>
	            </select>
	                </div>
	            </div>
	            
	            
	             <div class="form-group" >
	             <label for="agreedTime" class="col-sm-3 control-label"><span style="color:red">*</span> 颜色</label>
                     <div class="col-sm-7">
	             <select id="query_color" name="query_color"   onchange="selectColor(this.value);" class="form-control-inline" >
	              <option value="">--请选择颜色--</option>
	             </select>
	                 </div>
                 </div>




          <div class="form-group" id="modelPrice" style="display:none">
             <label for="agreedPrice" class="col-sm-3 control-label" style="position:relative;top:-5px;">订单价格</label>
          <div class="col-sm-7" >
          <span id="price" style="color:red"></span>
          <input type="hidden" id="hiddenPrice" name="price"/>
          </div>
          </div>
          <!-- 
           </label>
           -->
          <!-- 
          <label><input name="choose" type="radio" value="1" style="position:relative;top:25px;left:50px;z-index:10;"/>
          <div style="position:relative;left:40px;">
          
          <label for="agreedOther" class="col-sm-3 control-label"><span style="color:red"></span>其他机型</label>
          <div class="col-sm-7" >
          <table>
        
           <tr>
       <td> <input type="text" id="otherModel" name="otherModel" class="form-control" placeholder="请填写机型信息"></td>
       <td>&nbsp&nbsp</td>
       <td> <input type="text" id="otherPrice" name="otherPrice" class="form-control" placeholder="机型价格,单位(元)"></td>
           </tr>
          </table>
          </div>
          </div>
          </label>
             -->
        <button type="submit" class="hide" id="addSubmitBtn"></button>
      </form>
    </div>
    <div class="modal-footer">
      <button type="button" id="addSaveBtn" class="btn modal-btn" ><span class="am-icon-save icon-save"></span>提交</button>
      <button type="button" id="addMissBtn" class="btn modal-btn" data-dismiss="modal" aria-label="Close"><span class="am-icon-close icon-close"></span>取消</button>
    </div>
  </div><!-- /.modal-content -->
</div><!-- /.modal-dialog -->

<script type="text/javascript">



//表单验证
$(document).ready(function() {
    insertValidatorForm();
    $("#agreedTime").datetimepicker({
    	  format: "yyyy-mm-dd HH:ii",
    	  language: "zh-CN",
    	  autoclose:true//选中关闭
    	});
});

//初始化表单
var choose=$('input:radio[name="choose"]:checked').val(); 
function insertValidatorForm() {
    $("#insertForm")
        .bootstrapValidator({
            message : "不能为空",
            feedbackIcons : {
                valid : 'glyphicon glyphicon-ok',
                invalid : 'glyphicon glyphicon-remove',
                validating : 'glyphicon glyphicon-refresh'
            },
            fields : {
            	 query_brand: {
                     validators : {
                         notEmpty : {
                             message : "品牌不能为空"
                         }
                     }
                 },
            	query_model: {
                    validators : {
                        notEmpty : {
                            message : "机型不能为空"
                        }
                    }
                },
                query_color: {
                    validators : {
                        notEmpty : {
                            message : "颜色不能为空"
                        }
                    }
                },
                otherModel: {
                    validators : {
                        notEmpty : {
                            message : "请填写机型信息"
                        }
                    }
                },
                otherPrice: {
                    validators : {
                        notEmpty : {
                            message : "请填写价格"
                        }
                    }
                },
            	agreedTime: {
                    validators : {
                        notEmpty : {
                            message : "不能为空"
                        }
                    }
                }
            }// end fields
        }).on("success.form.bv", function(e) {
            // 阻止表单提交
            e.preventDefault();
            //加载等待
            AlertText.tips("d_loading");
            //校验成功后的操作
            var btn = $("#addSaveBtn");
            //让按钮不能点击
            btn.button("loading");
            //遮盖层
            var options = {
                url : "${ctx}/newOrder/agreedTime.do",
                dataType : "JSON",
                success : function(data) {
                    if(data.success){
                        //保存成功,关闭窗口，刷新列表
                        refreshPage();
                        //全部更新完后关闭弹窗
                        $("#addMissBtn").click();
                        //重置表单数据
                        document.getElementById("insertForm").reset();
                        $("#price").text("");
                    	$("#modelPrice").hide();
                    }else{
                        //保存失败
                        alert(data.msg);
                    }
                    addFormReset();
                },
                error : function() {
                    alert("系统异常，请稍后再试");
                    addFormReset();
                }
            }; // end options
            $("#insertForm").ajaxSubmit(options);
        }); // end on("success.form.bv"
}

/**
 * 重置表单
 */
function addFormReset(){
    //重置表单验证
    $("#insertForm").data("bootstrapValidator").resetForm();
    //让按钮重新能点击
    $("#addSaveBtn").button("reset");
    //隐藏等待
    AlertText.hide();
}


//点击保存按钮,提交form表单，触发校验
$("#addSaveBtn").click(function() {
    //格式化分类属性信息为JSON串
    $("#addSubmitBtn").click();
});

	



</script>