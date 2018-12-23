<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/commons/taglibs.jsp" %>
<div class="modal-backdrop fade in"></div>
<div class="modal-dialog">
  <div class="modal-content">
    <div class="modal-title"><span>编辑碎屏险项目</span>
      <a href="javascript: void(0);" class="close" data-dismiss="modal" aria-label="Close">&times;</a>
    </div>
    <div class="modal-body">
      <form id="updateForm" method="post" class="form-horizontal">
        <input type="hidden" id="id" name="id" value="${project.id}" />
        <div class="form-group">
          <label for="upBrandId" class="col-sm-2 control-label"><span style="color:red">*</span> 机型品牌</label>
          <div class="col-sm-10">
          	<select id="upBrandId" name="upBrandId" class="form-control">
              <option value="">--选择品牌--</option>
              <c:forEach items="${brands }" var="item" varStatus="i">
	            <option value="${item.id }" ${project.brandId eq item.id ? 'selected="selected"' : ''}>${item.name }</option>
	          </c:forEach>
            </select>
          </div>
        </div>
        <div class="form-group">
          <label for="upName" class="col-sm-2 control-label"><span style="color:red">*</span> 碎屏险名称</label>
          <div class="col-sm-10">
            <input type="text" id="upName" name="upName" value="${project.name }" class="form-control" placeholder="请输入碎屏险名称">
          </div>
        </div>
        
        <div class="form-group">
          <label for="upPrice" class="col-sm-2 control-label"><span style="color:red">*</span>购买价格</label>
          <div class="col-sm-10">
            <input type="text" id="upPrice" name="upPrice" value="${project.price }" class="form-control" placeholder="请输入购买价格">
          </div>
        </div>
        
        
        <div class="form-group">
          <label for="upMaxPrice" class="col-sm-2 control-label"><span style="color:red">*</span> 最高保额</label>
          <div class="col-sm-10">
            <input type="text" id="upMaxPrice" name="upMaxPrice" value="${project.maxPrice }" class="form-control" placeholder="请输入最高保额">
          </div>
        </div>
        
        
        <div class="form-group">
          <label for="upProductCode" class="col-sm-2 control-label"><span style="color:red">*</span> 产品代码</label>
          <div class="col-sm-10">
            <input type="text" id="upProductCode" name="upProductCode" value="${project.productCode }" class="form-control" placeholder="请输入最高保额">
          </div>
        </div>
        
         <div class="form-group">
          <label for="upDetail" class="col-sm-2 control-label"> 有效期(月)</label>
          <div class="col-sm-10">
            <input type="number" id="maxTime" name="maxTime" value="${project.maxTime }" class="form-control" placeholder="请输入保修时长">
          </div>
        </div>
        
          <div class="form-group">
          <label for="upDetail" class="col-sm-2 control-label"> 项目描述</label>
          <div class="col-sm-10">
            <input type="text" id="upDetail" name="upDetail" value="${project.detail }" class="form-control" placeholder="请输入项目描述">
          </div>
        </div>
        
        
        <div class="form-group">
          <label for="upSort" class="col-sm-2 control-label">排序</label>
          <div class="col-sm-10">
            <input type="text" id="upSort" name="upSort" value="${project.sort }"class="form-control" placeholder="请输入显示序号" value="${project.sort }">
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
                upName: {
                    validators : {
                        notEmpty : {
                            message : "不能为空"
                        }
                    }
                },
			upPrice : {
				validators : {
					notEmpty : {
						message : "不能为空"
					}
				}
			},
			upProductCode : {
				validators : {
					notEmpty : {
						message : "不能为空"
					}
				}
			},
			upMaxPrice : {
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
            var btn = $("#upSaveBtn");
            //让按钮不能点击
            btn.button("loading");
            //遮盖层
            var options = {
                url : "${ctx}/screen/project/update.do",
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

</script>