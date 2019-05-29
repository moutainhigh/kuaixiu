<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/commons/taglibs.jsp" %>
<div class="modal-backdrop fade in"></div>
<div class="modal-dialog" style="width: 700px;">
  <div class="modal-content">
    <div class="modal-title"><span>新增企业单位</span>
      <a href="javascript: void(0);" class="close" data-dismiss="modal" aria-label="Close">&times;</a>
    </div>
    <div class="modal-body">
      <form id="insertForm" method="post" class="form-horizontal">
        <input type="hidden" id="type" name="type" value="1"/><br/>
        <div class="form-group">
          <label for="name" class="col-sm-3 control-label"><span style="color:red">*</span> 企业名称</label>
          <div class="col-sm-9">
            <input type="text" id="name" name="name" class="form-control" placeholder="请输入企业名称">
          </div>
        </div>
        <div class="form-group">
          <label for="person" class="col-sm-3 control-label"><span style="color:red">*</span> 对接人姓名</label>
          <div class="col-sm-9">
            <input type="text" id="person" name="person" class="form-control" placeholder="请输入负责人姓名">
          </div>
        </div>
        <div class="form-group">
          <label for="personPhone" class="col-sm-3 control-label"><span style="color:red">*</span> 对接人手机号</label>
          <div class="col-sm-9">
            <input type="text" id="phone" name="phone" class="form-control" placeholder="请输入负责人手机号">
          </div>
        </div>
        <div class="form-group">
          <input type="hidden" id="addAreas" name="addAreas" >
          <label for="addProvince" class="col-sm-3 control-label"><span style="color:red">*</span> 地址</label>
          <div class="col-sm-9">
            <select id="addProvince" name="addProvince" onchange="fn_select_address(2, this.value, '', 'add');" class="form-control-inline">
              <option value="">--请选择--</option>
              <c:forEach items="${provinceL}" var="item" varStatus="i">
                <option value="${item.areaId }">${item.area }</option>
              </c:forEach>
            </select>
            
            <select id="addCity" name="addCity" onchange="fn_select_address(3, this.value, '', 'add');" class="form-control-inline" style="display: none;">
              <option value="">--请选择--</option>
            </select>
            
            <select id="addCounty" name="addCounty" onchange="fn_select_address(4, this.value, '', 'add');" class="form-control-inline" style="display: none;">
              <option value="">--请选择--</option>
            </select>
          </div>
        </div>
        <div class="form-group">
          <div class="col-sm-9 col-sm-offset-3">
            <input type="text" id="addAddress" name="addAddress" class="form-control" placeholder="请输入详细地址">
          </div>
        </div>
        <div class="form-group">
          <label for="countyList" name="countyList" class="col-sm-3 control-label">服务区域</label>
          <div class="col-sm-9">
            <c:forEach items="${countyList }" var="item" varStatus="i">
              <label class="checkbox-inline" style="margin-left: 0px; margin-right: 10px;">
                <input type="checkbox" name="countyList" value="${item.areaId }"> ${item.area }
              </label>
            </c:forEach>
          </div>
        </div>
        <div class="form-group">
          <label for="projects" class="col-sm-3 control-label">产品需求</label>
          <div class="col-sm-9">
            <c:forEach items="${projects }" var="item" varStatus="i">
              <label class="checkbox-inline" style="margin-left: 0px; margin-right: 10px;">
                <input type="checkbox" name="projects" value="${item.id }"> ${item.project }
              </label>
            </c:forEach>
          </div>
        </div>
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
});

//初始化表单
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
                name: {
                    validators : {
                        notEmpty : {
                            message : "不能为空"
                        }
                    }
                },
                person: {
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
                        },
                        regexp: {
                            regexp: /^1[0-9]{10}$/,
                            message: '请输入正确格式'
                        }
                    }
                },
                projects: {
                    validators : {
                        notEmpty : {
                            message : "不能为空"
                        }
                    }
                },
                addAddress: {
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
            //验证地址选择
            if($("#addProvince").val() == ''){
                AlertText.tips("d_alert", "提示", "请选择省份！");
                $("#insertForm").data("bootstrapValidator").resetForm();
                return;
            }
            var addProvinceName = $("#addProvince").find("option:selected").text();
            
            if($("#addCity").val() == ''){
                AlertText.tips("d_alert", "提示", "请选择地市！");
                $("#insertForm").data("bootstrapValidator").resetForm();
                return;
            }
            var addCityName = $("#addCity").find("option:selected").text();
            
            if($("#addCounty").val() == ''){
                AlertText.tips("d_alert", "提示", "请选择区县！");
                $("#insertForm").data("bootstrapValidator").resetForm();
                return;
            }
            var addCountyName = $("#addCounty").find("option:selected").text();
            
            var areas = addProvinceName + " " + addCityName + " " + addCountyName;
            $("#addAreas").val(areas);
            
            //加载等待
            AlertText.tips("d_loading");
            //校验成功后的操作
            var btn = $("#addSaveBtn");
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
                        $("#addMissBtn").click();
                        //重置表单数据
                        document.getElementById("insertForm").reset();
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