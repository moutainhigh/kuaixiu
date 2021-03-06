<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/commons/taglibs.jsp" %>
<div class="modal-backdrop fade in"></div>
<div class="modal-dialog">
  <div class="modal-content">
    <div class="modal-title"><span>修改品牌</span>
      <a href="javascript: void(0);" class="close" data-dismiss="modal" aria-label="Close">&times;</a>
    </div>
    <div class="modal-body">
      <form id="updateForm" method="post" class="form-horizontal">
        <input type="hidden" name="id" value="${brand.id }" />
        <div class="form-group">
          <label for="upName" class="col-sm-2 control-label"><span style="color:red">*</span> 品牌名称</label>
          <div class="col-sm-10">
            <input type="text" id="upName" name="upName" value="${brand.name }" class="form-control" placeholder="请输入品牌名称">
          </div>
        </div>
         <div class="form-group">
          <label for="maxPrice" class="col-sm-2 control-label"><span style="color:red">*</span> 最高保额</label>
          <div class="col-sm-10">
            <input type="text" id="maxPrice" name="maxPrice" value="${brand.maxPrice }" class="form-control" placeholder="请输入最高保额   单位元">
          </div>
        </div>
        <div class="form-group">
          <label for="upSort" class="col-sm-2 control-label">排序</label>
          <div class="col-sm-10">
            <input type="text" id="upSort" name="upSort" class="form-control" placeholder="请输入显示序号" value="${brand.sort }">
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
            //加载等待
            AlertText.tips("d_loading");
            //校验成功后的操作
            var btn = $("#upSaveBtn");
            //让按钮不能点击
            btn.button("loading");
            //遮盖层
            var options = {
                url : "${ctx}/screen/brand/update.do",
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