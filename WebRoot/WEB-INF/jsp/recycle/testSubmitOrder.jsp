<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/commons/taglibs.jsp" %>
<div class="modal-backdrop fade in"></div>
<div class="modal-dialog" style="width: 700px;">
    <div class="modal-content">
        <div class="modal-title"><span>回访创建回收订单</span>
            <a href="javascript: void(0);" class="close" data-dismiss="modal" aria-label="Close">&times;</a>
        </div>
        <div class="modal-body">
            <form id="submitInsertForm" method="post" class="form-horizontal">
                <input type="hidden" id="submitCheckId" name="submitCheckId" value="${checkItems.id}"/>
                <input type="hidden" id="itemName" name="itemName" value="${itemName}"/>
                <input type="hidden" id="imagePath" name="imagePath" value="${imagePath}"/>
                <input type="hidden" id="source" name="source" value="14"/>
                <div class="form-group">
                    <label for="time" class="col-sm-2 control-label">检测时间：</label>
                    <div class="col-sm-4">
                        <input type="text" id="time" name="time" value="${checkItems.isVisit}" disabled="disabled"
                               class="form-control">
                    </div>
                    <label for="chnnal" class="col-sm-2 control-label">渠道：</label>
                    <div class="col-sm-4">
                        <input type="text" id="chnnal" name="chnnal" disabled="disabled" class="form-control"
                               value="集团欢GO抽奖"/>
                    </div>
                </div>
                <div class="form-group">
                    <label for="model" class="col-sm-2 control-label">检测机型：</label>
                    <div class="col-sm-4">
                        <input type="text" id="model" name="model" value="${checkItems.recycleModel}"
                               disabled="disabled" class="form-control">
                    </div>
                    <label for="price" class="col-sm-2 control-label">报价：</label>
                    <div class="col-sm-4">
                        <input type="text" id="price" name="price" value="${checkItems.price}" disabled="disabled"
                               class="form-control">
                    </div>
                </div>
                <div class="form-group">
                    <label for="name" class="col-sm-2 control-label">用户姓名：</label>
                    <div class="col-sm-9">
                        <input type="text" id="name" name="name" class="form-control" placeholder="请输入用户姓名">
                    </div>
                </div>
                <div class="form-group">
                    <label for="mobile" class="col-sm-2 control-label">联系电话：</label>
                    <div class="col-sm-9">
                        <input type="text" id="mobile" name="mobile" value="${checkItems.loginMobile}"
                               class="form-control" placeholder="请输入手机号">
                    </div>
                </div>
                <div class="form-group">
                    <input type="hidden" id="addAreas" name="addAreas">
                    <label for="addArea" class="col-sm-2 control-label">地址：</label>
                    <div class="col-sm-9">
                        <select id="addProvince" name="addProvince"
                                onchange="fn_select_address(2, this.value, '', 'add');" class="form-control-inline">
                            <option value="">--请选择--</option>
                            <c:forEach items="${provinceL}" var="item" varStatus="i">
                                <option value="${item.areaId }">${item.area }</option>
                            </c:forEach>
                        </select>

                        <select id="addCity" name="addCity" onchange="fn_select_address(3, this.value, '', 'add');"
                                class="form-control-inline" style="display: none;">
                            <option value="">--请选择--</option>
                        </select>

                        <select id="addCounty" name="addCounty" onchange="fn_select_address(4, this.value, '', 'add');"
                                class="form-control-inline" style="display: none;">
                            <option value="">--请选择--</option>
                        </select>

                        <select id="addStreet" name="addStreet" class="form-control-inline" style="display: none;">
                            <option value="">--请选择--</option>
                        </select>
                    </div>
                </div>
                <div class="form-group">
                    <div class="col-sm-9 col-sm-offset-2">
                        <input type="text" id="addAddress" name="addAddress" class="form-control" placeholder="请输入详细地址">
                    </div>
                </div>
                <div class="form-group">
                    <label for="name" class="col-sm-2 control-label">订单备注：</label>
                    <div class="col-sm-9">
                        <textarea name="note" id="note" cols="79" rows="5" placeholder="请写下订单备注"></textarea>
                    </div>
                </div>
                <div class="form-group">
                    <label for="mailType" class="col-sm-2 control-label"><span style="color:red"></span>快递方式：</label>
                    <div class="col-sm-9">
                        <div class="oldToNew">
                            <input name="mailType" id="mailType1" type="radio" value="1"/>顺丰上门回收
                            &nbsp&nbsp&nbsp&nbsp
                            <input name="mailType" id="mailType2" type="radio" value="2"/>自行邮寄
                        </div>
                    </div>
                </div>
                <div class="form-group" id="pushTime">
                    <div class="col-sm-9 col-sm-offset-2">
                        <div class="takeTime">
                            <%--<input type="text" id="takeTime" name="takeTime"--%>
                                   <%--class="form-control am-datepicker-start" data-am-datepicker readonly>--%>
                            <input type="text" id="takeTime" name="takeTime" class="form-control" placeholder="请选择预约时间">
                        </div>
                    </div>
                </div>
                <div class="form-group">
                    <label for="payType" class="col-sm-2 control-label">回收方式：</label>
                    <div class="col-sm-9">
                        <div class="payType">
                            <input name="payType" id="payType1" type="radio" value="2"/>话费充值
                            &nbsp&nbsp&nbsp&nbsp
                            <input name="payType" id="payType2" type="radio" value="1"/>支付宝付款
                        </div>
                    </div>
                </div>
                <div class="form-group">
                    <div class="col-sm-9 col-sm-offset-2">
                        <input type="text" id="payMobile" name="payMobile" class="form-control" placeholder="请输入手机充值号码">
                    </div>
                </div>
                <div class="form-group">
                    <label for="couponCode" class="col-sm-2 control-label">加价券：</label>
                    <div class="col-sm-9">
                        <input type="text" id="couponCode" name="couponCode" class="form-control"
                               placeholder="请输入加价券编码">
                    </div>
                </div>
                <div class="form-group">
                    <label for="recordNote" class="col-sm-2 control-label">回访备注：</label>
                    <div class="col-sm-9">
                        <textarea name="recordNote" id="recordNote" cols="79" rows="5" placeholder="请写下回访备注"></textarea>
                    </div>
                </div>
                <button type="submit" class="hide" id="submitSubmitBtn"></button>
            </form>
        </div>
        <div class="modal-footer">
            <button type="button" id="submitSaveBtn" class="btn modal-btn"><span class="am-icon-save icon-save"></span>提交
            </button>
            <button type="button" id="submitMissBtn" class="btn modal-btn" data-dismiss="modal" aria-label="Close"><span
                    class="am-icon-close icon-close"></span>取消
            </button>
        </div>
    </div><!-- /.modal-content -->
</div>
<!-- /.modal-dialog -->

<script type="text/javascript">
    $("#takeTime").datetimepicker({
        format: "yyyy-mm-dd HH:ii",
        language: "zh-CN",
        autoclose: true,//选中关闭
//        minView: "month"//设置只显示到月份
    });
    $("#mailType1").attr("checked","checked");
    $("#payType1").attr("checked","checked");
    $("input[name='mailType']").change(function () {
        if($("#mailType1").is(":checked")) {
            $("#pushTime").show();
        }
        if($("#mailType2").is(":checked")){
            $("#pushTime").hide();
        }
    });
    $("input[name='payType']").change(function () {
        var payType=$("input[name='payType']").val();
        if($("#payType2").is(":checked")) {
            $("#payMobile").attr('placeholder',"请输入支付宝账号");
        }
        if($("#payType1").is(":checked")) {
            $("#payMobile").attr('placeholder',"请输入手机充值号码");
        }
    });
    //表单验证
    $(document).ready(function () {
        insertValidatorForm();
    });

    //初始化表单
    function insertValidatorForm() {
        $("#submitInsertForm")
            .bootstrapValidator({
                message: "不能为空",
                feedbackIcons: {
                    valid: 'glyphicon glyphicon-ok',
                    invalid: 'glyphicon glyphicon-remove',
                    validating: 'glyphicon glyphicon-refresh'
                },
                fields: {
                    addAddress: {
                        validators: {
                            notEmpty: {
                                message: "不能为空"
                            }
                        }
                    }
                }// end fields
            }).on("success.form.bv", function (e) {
            // 阻止表单提交
            e.preventDefault();
            //验证地址选择
            if ($("#addProvince").val() == '') {
                AlertText.tips("d_alert", "提示", "请选择省份！");
                $("#submitInsertForm").data("bootstrapValidator").resetForm();
                return;
            }
            var addProvinceName = $("#addProvince").find("option:selected").text();

            if ($("#addCity").val() == '') {
                AlertText.tips("d_alert", "提示", "请选择地市！");
                $("#submitInsertForm").data("bootstrapValidator").resetForm();
                return;
            }
            var addCityName = $("#addCity").find("option:selected").text();

            if ($("#addCounty").val() == '') {
                AlertText.tips("d_alert", "提示", "请选择区县！");
                $("#submitInsertForm").data("bootstrapValidator").resetForm();
                return;
            }
            var addCountyName = $("#addCounty").find("option:selected").text();

            if ($("#addStreet").val() == '') {
                AlertText.tips("d_alert", "提示", "请选择街道/乡镇！");
                $("#submitInsertForm").data("bootstrapValidator").resetForm();
                return;
            }
            var addStreetName = "";
            if ($("#addStreet").val() != '0') {
                addStreetName = " " + $("#addStreet").find("option:selected").text();
            }

            var areas = addProvinceName + " " + addCityName + " " + addCountyName + addStreetName;
            $("#addAreas").val(areas);

            //加载等待
            AlertText.tips("d_loading");
            //校验成功后的操作
            var btn = $("#submitSaveBtn");
            //让按钮不能点击
            btn.button("loading");
            //遮盖层
            var options = {
                url: "${ctx}/recycle/testSubmitOrder.do",
                dataType: "JSON",
                success: function (data) {
                    if (data.success) {
                        //保存成功,关闭窗口，刷新列表
                        refreshPage();
                        //全部更新完后关闭弹窗
                        $("#submitMissBtn").click();
                        //重置表单数据
                        document.getElementById("submitInsertForm").reset();
                        var submitCheckId = $("#submitCheckId").val();itemName
                        var itemName = $("#itemName").val();
                        func_reload_page("${ctx}/recycle/recycleTestDetail.do?id=" + submitCheckId+"&product="+itemName);
                    } else {
                        //保存失败
                        alert(data.resultMessage);
                    }
                    addFormReset();
                },
                error: function () {
                    alert("系统异常，请稍后再试");
                    addFormReset();
                }
            }; // end options
            $("#submitInsertForm").ajaxSubmit(options);
        }); // end on("success.form.bv"
    }

    /**
     * 重置表单
     */
    function addFormReset() {
        //重置表单验证
        $("#submitInsertForm").data("bootstrapValidator").resetForm();
        //让按钮重新能点击
        $("#submitSaveBtn").button("reset");
        //隐藏等待
        AlertText.hide();
    }


    //点击保存按钮,提交form表单，触发校验
    $("#submitSaveBtn").click(function () {
        //格式化分类属性信息为JSON串
        $("#submitSubmitBtn").click();
    });
    /** 地址选择工具 */

    /**
     * 查询地址
     * level: 地址级别 1 省， 2 市， 3 区县， 4 街道
     * pid: 上级地址id
     * current: 当前选中值
     * profix: 前缀
     */

    function fn_select_address(level, pid, current, profix) {
        //如果是寄修模式
        var choose = $('input:radio[name="repairType"]:checked').val();
        if (level == 3 && choose == 3) {
            //通过选择的地市信息 判断邮寄地址
            var city = $("#addCity").val();  //地址表中的地址编号
            var addCityName = $("#addCity").find("option:selected").text();
            findSendAddress(city);
        }
        var url = AppConfig.ctx + "/address/getArea.do";
        $.post(url, {pid: pid}, function (result) {
            if (!result.success) {
                return false;
            }
            var json = result.data;
            //地市级
            if (level == 2) {
                $("#" + profix + "City option[value!='']").remove();
                $("#" + profix + "County option[value!='']").remove();
                $("#" + profix + "County").hide();
                if ($("#" + profix + "Street").length > 0) {
                    $("#" + profix + "Street option[value!='']").remove();
                    $("#" + profix + "Street").hide();
                }
                $("#" + profix + "City").append(packageAddress(json, current));
                $("#" + profix + "City").show();
            }
            //区县级
            if (level == 3) {
                $("#" + profix + "County option[value!='']").remove();
                if ($("#" + profix + "Street").length > 0) {
                    $("#" + profix + "Street option[value!='']").remove();
                    $("#" + profix + "Street").hide();
                }
                $("#" + profix + "County").append(packageAddress(json, current));
                $("#" + profix + "County").show();
            }
            //乡镇级
            if (level == 4) {
                $("#" + profix + "Street option[value!='']").remove();
                var select_html = packageAddress(json, current);
                if (select_html != '') {
                    $("#" + profix + "Street").show();
                    $("#" + profix + "Street").append(select_html);
                } else {
                    $("#" + profix + "Street").hide();
                    $("#" + profix + "Street").append('<option value="0" selected="selected"></option>');
                }

            }
        });
    }

    /**
     * 封装地址
     * @param json
     * @param current
     * @returns {String}
     */
    function packageAddress(json, current) {
        var select_html = '';
        if (json.length > 0) {
            for (a in json) {
                if (current == json[a]['areaId'])
                    select_html += '<option value="' + json[a]['areaId'] + '" selected="selected">' + json[a]['area'] + '</option>';
                else
                    select_html += '<option value="' + json[a]['areaId'] + '">' + json[a]['area'] + '</option>';
            }
        }
        return select_html;
    }

    //地址信息
    function getArea(id, name) {
        var area = new Object();
        area.areaId = id;
        area.area = name;
        return area;
    }

    var privinceData = new Array();
    privinceData.push(getArea("1", "北京"));
    privinceData.push(getArea("2", "上海"));
    privinceData.push(getArea("3", "天津"));
    privinceData.push(getArea("4", "重庆"));
    privinceData.push(getArea("5", "河北"));
    privinceData.push(getArea("6", "山西"));
    privinceData.push(getArea("7", "河南"));
    privinceData.push(getArea("8", "辽宁"));
    privinceData.push(getArea("9", "吉林"));
    privinceData.push(getArea("10", "黑龙江"));
    privinceData.push(getArea("11", "内蒙古"));
    privinceData.push(getArea("12", "江苏"));
    privinceData.push(getArea("13", "山东"));
    privinceData.push(getArea("14", "安徽"));
    privinceData.push(getArea("15", "浙江"));
    privinceData.push(getArea("16", "福建"));
    privinceData.push(getArea("17", "湖北"));
    privinceData.push(getArea("18", "湖南"));
    privinceData.push(getArea("19", "广东"));
    privinceData.push(getArea("20", "广西"));
    privinceData.push(getArea("21", "江西"));
    privinceData.push(getArea("22", "四川"));
    privinceData.push(getArea("23", "海南"));
    privinceData.push(getArea("24", "贵州"));
    privinceData.push(getArea("25", "云南"));
    privinceData.push(getArea("26", "西藏"));
    privinceData.push(getArea("27", "陕西"));
    privinceData.push(getArea("28", "甘肃"));
    privinceData.push(getArea("29", "青海"));
    privinceData.push(getArea("30", "宁夏"));
    privinceData.push(getArea("31", "新疆"));
    privinceData.push(getArea("32", "台湾"));
    privinceData.push(getArea("42", "香港"));
    privinceData.push(getArea("43", "澳门"));
    privinceData.push(getArea("84", "钓鱼岛"));

</script>