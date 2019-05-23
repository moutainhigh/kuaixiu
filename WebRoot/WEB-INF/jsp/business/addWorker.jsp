<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/commons/taglibs.jsp" %>
<div class="modal-backdrop fade in"></div>
<div class="modal-dialog" style="width: 700px;">
  <div class="modal-content">
    <div class="modal-title"><span>新增员工</span>
      <a href="javascript: void(0);" class="close" data-dismiss="modal" aria-label="Close">&times;</a>
    </div>
    <div class="modal-body">
      <form id="insertWorkerForm" method="post" class="form-horizontal">
        <input type="hidden" id="type" name="type" value="4"/><br/>
        <div class="form-group">
          <label for="name" class="col-sm-3 control-label"><span style="color:red">*</span> 员工名称</label>
          <div class="col-sm-9">
            <input type="text" id="name" name="name" class="form-control" placeholder="请输入企业名称">
          </div>
        </div>
        <div class="form-group">
          <label for="phone" class="col-sm-3 control-label"><span style="color:red">*</span> 员工手机号</label>
          <div class="col-sm-9">
            <input type="text" id="phone" name="phone" class="form-control" placeholder="请输入负责人手机号">
          </div>
        </div>
        <div class="form-group">
          <label for="companyId" class="col-sm-3 control-label"><span style="color:red">*</span> 所属企业名字</label>
          <div class="col-sm-9">
            <select id="companyId" name="companyId" class="form-control">
              <option value="">--请选择--</option>
              <c:forEach items="${companys }" var="item" varStatus="i">
                <option value="${item.id }">${item.name }</option>
              </c:forEach>
            </select>
          </div>
        </div>
        <button type="submit" class="hide" id="addWorkerSubmitBtn"></button>
      </form>
    </div>
    <div class="modal-footer">
      <button type="button" id="addWorkerSaveBtn" class="btn modal-btn" ><span class="am-icon-save icon-save"></span>提交</button>
      <button type="button" id="addWorkerMissBtn" class="btn modal-btn" data-dismiss="modal" aria-label="Close"><span class="am-icon-close icon-close"></span>取消</button>
    </div>
  </div><!-- /.modal-content -->
</div><!-- /.modal-dialog -->

<script type="text/javascript">

    //表单验证
    $(document).ready(function() {
        insertValidatorForm();
    });

    //初始化表单
    function insertValidatorForm() {
        $("#insertWorkerForm")
            .bootstrapValidator({
                message : "不能为空",
                feedbackIcons : {
                    valid : 'glyphicon glyphicon-ok',
                    invalid : 'glyphicon glyphicon-remove',
                    validating : 'glyphicon glyphicon-refresh'
                },
                fields : {
                    name: {
                        validators : {
                            notEmpty : {
                                message : "不能为空"
                            }
                        }
                    },
                    phone: {
                        validators : {
                            notEmpty : {
                                message : "不能为空"
                            }
                        }
                    },
                    companyId: {
                        validators : {
                            notEmpty : {
                                message : "不能为空"
                            },
                        }
                    }
                }// end fields
            }).on("success.form.bv", function(e) {
            // 阻止表单提交
            e.preventDefault();
            //加载等待
            AlertText.tips("d_loading");
            //校验成功后的操作
            var btn = $("#addWorkerSaveBtn");
            //让按钮不能点击
            btn.button("loading");
            //遮盖层
            var options = {
                url : "${ctx}/sj/order/register.do",
                dataType : "JSON",
                success : function(data) {
                    if(data.success){
                        //保存成功,关闭窗口，刷新列表
                        refreshPage();
                        //全部更新完后关闭弹窗
                        $("#addWorkerMissBtn").click();
                        //重置表单数据
                        document.getElementById("insertWorkerForm").reset();
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
            $("#insertWorkerForm").ajaxSubmit(options);
        }); // end on("success.form.bv"
    }

    /**
     * 重置表单
     */
    function addFormReset(){
        //重置表单验证
        $("#insertWorkerForm").data("bootstrapValidator").resetForm();
        //让按钮重新能点击
        $("#addWorkerSaveBtn").button("reset");
        //隐藏等待
        AlertText.hide();
    }


    //点击保存按钮,提交form表单，触发校验
    $("#addWorkerSaveBtn").click(function() {
        //格式化分类属性信息为JSON串
        $("#addWorkerSubmitBtn").click();
    });

</script>