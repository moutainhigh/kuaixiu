<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/commons/taglibs.jsp" %>
<div class="modal-backdrop fade in"></div>
<div class="modal-dialog" style="width: 700px;">
  <div class="modal-content">
    <div class="modal-title"><span>新增加价券规则</span>
      <a href="javascript: void(0);" class="close" data-dismiss="modal" aria-label="Close">&times;</a>
    </div>
    <div class="modal-body">
      <form id="insertForm" method="post" class="form-horizontal">
        <div class="form-group" id="nameLabels">
          <label for="couponName" class="col-sm-2 control-label"><span style="color:red">*</span> 规则名称</label>
          <div class="col-sm-9">
            <input type="text" id="nameLabel" name="nameLabel" class="form-control" placeholder="请输入规则名称">
          </div>
        </div>

        <div class="form-group" id="couponNames">
          <label for="couponName" class="col-sm-2 control-label"><span style="color:red">*</span> 加价券名称</label>
          <div class="col-sm-9">
            <input type="text" id="couponName" name="couponName" class="form-control" placeholder="请输入加价券名称">
          </div>
        </div>
        <div class="form-group">
          <label for="pricingType" class="col-sm-2 control-label"><span style="color:red">*</span>加价券类型</label>
          <div class="col-sm-9">
            <div class="oldToNew">
              <input name="pricingType" type="radio"  value="1"checked/>百分比加价
              &nbsp&nbsp&nbsp&nbsp
              <input name="pricingType" type="radio"  value="2"/>固定加价
            </div>
          </div>
        </div>
        <div class="form-group" id="prices">
          <label for="price" class="col-sm-2 control-label"><span style="color:red">*</span>加价金额</label>
          <div class="col-sm-9">
            <input type="number" oninput="if(value>9999999)value=9999999" id="price" name="price" class="form-control" placeholder="请输入加价金额">
          </div>
        </div>
        <div class="form-group">
          <label class="col-sm-9 control-label">（加价金额。如果是百分比加价：无需输入百分号，输入分子即可）</label>
        </div>
        <div class="form-group" id="addPriceUppers">
          <label for="addPriceUpper" class="col-sm-2 control-label"> <span style="color:red">*</span>最高加价金额</label>
          <div class="col-sm-9">
            <input type="number" oninput="if(value>9999999)value=9999999" id="addPriceUpper" name="addPriceUpper" class="form-control" placeholder="请输入最高加价金额">
          </div>
        </div>
        <div class="form-group" id="subtractionPrices">
          <label for="subtractionPrice" class="col-sm-2 control-label"><span style="color:red">*</span> 订单金额下限</label>
          <div class="col-sm-9">
            <input type="number" oninput="if(value>9999999)value=9999999" id="subtractionPrice" name="subtractionPrice" class="form-control" placeholder="请输入订单金额下限">
          </div>
        </div>
        <div class="form-group" id="upperLimits">
          <label for="upperLimit" class="col-sm-2 control-label"> <span style="color:red">*</span>订单金额上限</label>
          <div class="col-sm-9">
            <input type="number" oninput="if(value>9999999)value=9999999" id="upperLimit" name="upperLimit" class="form-control" placeholder="请输入订单金额上限">
          </div>
        </div>

        <div class="form-group" id="ruleDescriptions">
          <label for="ruleDescription" class="col-sm-2 control-label"> <span style="color:red">*</span>规则描述</label>
          <div class="col-sm-9">
            <input type="text" id="ruleDescription" name="ruleDescription" class="form-control" placeholder="请输入规则描述">
          </div>
        </div>
        <div class="form-group" id="projects">
          <label for="note" class="col-sm-2 control-label"> <span style="color:red">*</span>备注</label>
          <div class="col-sm-9">
            <input type="text" id="note" name="note" class="form-control" placeholder="请输入备注">
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


    $(function () {
        $('input:radio[name="pricingType"]').change(function () {
            var v = $(this).val();
            if (v == 2) {
                $("#addPriceUppers").hide();
            } else {
                $("#addPriceUppers").show();
            }
        });
    });

    //表单验证
    $(document).ready(function () {
        insertValidatorForm();
        $("#validBeginTime").val(getDateDayFormat(1));
        $("#validEndTime").val(getDateDayFormat(181));
    });

    /**
     * 故障全选
     */
    function checkAll(obj) {
        $("input[name='addProject']").each(function () {
            $(this).prop("checked", obj.checked);
        });
        var isTrue = $("#check_all_btn").prop("checked");
        if (isTrue == true) {
            $('#checkAll_project').val(1);
        }
        if (isTrue == false) {
            $('#checkAll_project').val(0);
        }

    }


    //点击保存按钮,提交form表单，触发校验
    $("#addSaveBtn").click(function () {
        //格式化分类属性信息为JSON串
        $("#addSubmitBtn").click();
    });
    //console.log("d");
    //初始化表单
    function insertValidatorForm() {
        $("#insertForm")
            .bootstrapValidator({
                message: "不能为空",
                feedbackIcons: {
                    valid: 'glyphicon glyphicon-ok',
                    invalid: 'glyphicon glyphicon-remove',
                    validating: 'glyphicon glyphicon-refresh'
                },
                fields: {

                }// end fields
            }).on("success.form.bv", function (e) {
            // 阻止表单提交
            e.preventDefault();
            // 验证颜色是否添加
            //console.log("ddd");
            //加载等待
            AlertText.tips("d_loading");
            //校验成功后的操作
            var btn = $("#addSaveBtn");
            //让按钮不能点击
            btn.button("loading");
            //遮盖层
            var options = {
                url: "${ctx}/groupSms/addCouponRole.do",
                dataType: "JSON",
                success: function (data) {
                    if (data.success) {
                        AlertText.tips("d_alert", "提示", "创建成功");
                        refreshPage();
                        //全部更新完后关闭弹窗
                        $("#addMissBtn").click();
                        //重置表单数据
                        document.getElementById("insertForm").reset();
                    } else {
                        //保存失败
                        AlertText.tips("d_alert", "提示", data.resultMessage, function () {
                            addFormReset();
                        });
                    }
                },
                error: function () {
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
    function addFormReset() {
        //重置表单验证
        $("#insertForm").data("bootstrapValidator").resetForm();
        //让按钮重新能点击
        $("#addSaveBtn").button("reset");
        //隐藏等待
        AlertText.hide();
    }


</script>