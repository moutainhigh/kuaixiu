<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/commons/taglibs.jsp" %>
<link rel="stylesheet" href="${webResourceUrl}/resource/order/css/order.detail.css">
<link rel="stylesheet" href="${webResourceUrl}/resource/layui/css/modules/laydate/default/laydate.css">
<link rel="stylesheet" href="${webResourceUrl}/resource/layui/css/modules/layer/default/layer.css">
<link rel="stylesheet" href="${webResourceUrl}/resource/layui/css/modules/code.css">
<div class="am-cf am-padding am-padding-bottom-0">
    <div class="am-fl am-cf" style="width: 100%;">
        <strong class="am-text-primary am-text-lg"><a href="javascript:void(0);" onclick="toList();">商机订单管理</a></strong>
        /
        <small>订单详情</small>
        <strong class="am-text-primary"><a href="javascript:void(0);" onclick="func_to_back();">返回</a></strong>
    </div>
</div>

<hr>

<div class="am-g">
    <div class="panel panel-success index-panel-msg">

        <h4>
            订单状态：
            <c:if test="${sjOrder.state==100}">
                待审核
            </c:if>
            <c:if test="${sjOrder.state==200}">
                待派单
            </c:if>
            <c:if test="${sjOrder.state==300}">
                待施工
            </c:if>
            <c:if test="${sjOrder.state==400}">
                待竣工
            </c:if>
            <c:if test="${sjOrder.state==500}">
                已完成
            </c:if>
            <c:if test="${sjOrder.state==600}">
                未通过
            </c:if>
        </h4>
    </div><!-- /panel -->
</div>
<!-- /am-g -->

<div class="am-g order-info mt20">
    <table class="detail-table">
        <tr>
            <td class="td-title">
                <h4>订单信息：</h4>
            </td>
            <td class="td-space"></td>
            <td class="td-info">
                <div class="row">
                    <div class="col-md-6 col-sm-6 col-xs-12">
                        <h4>订单号：${sjOrder.orderNo }</h4>
                    </div><!-- /.col -->
                    <div class="col-md-6 col-sm-6 col-xs-12">
                        <c:if test="${sjOrder.type==1}">
                            <h4>订单类型：商机单</h4>
                        </c:if>
                        <c:if test="${sjOrder.type==2}">
                            <h4>订单类型：派单</h4>
                        </c:if>
                    </div><!-- /.col -->

                </div><!-- /.row -->

                <div class="row">
                    <div class="col-md-6 col-sm-6 col-xs-12">
                        <h4>创建时间：<fmt:formatDate value="${sjOrder.createTime }" pattern="yyyy-MM-dd HH:mm:ss"/></h4>
                    </div><!-- /.col -->
                    <div class="col-md-6 col-sm-6 col-xs-12">
                        <h4>单位名字：${sjOrder.companyName }</h4>
                    </div><!-- /.col -->
                </div><!-- /.row -->

                <div class="row">
                    <div class="col-md-6 col-sm-6 col-xs-12">
                        <h4>联系人/电话：${sjOrder.person }/${sjOrder.phone }</h4>
                    </div><!-- /.col -->
                    <div class="col-md-6 col-sm-6 col-xs-12">
                        <h4>CRM编号：${sjOrder.crmNo }</h4>
                    </div><!-- /.col -->
                </div><!-- /.row -->
                <div class="row">
                    <div class="col-md-6 col-sm-6 col-xs-12">
                        <h4>负责人姓名/身份证号：${sjOrder.responsibleName }/${sjOrder.responsibleIdNumber }</h4>
                    </div><!-- /.col -->
                </div><!-- /.row -->
                <div class="row">
                    <div class="col-md-12 col-sm-12 col-xs-12">
                        <h4>地址：${sjOrder.address} ${sjOrder.addressDetail}</h4>
                    </div><!-- /.col -->
                </div><!-- /.row -->

                <div class="row">
                    <div class="col-md-12 col-sm-12 col-xs-12">
                        <h4>产品需求：${sjOrder.projectNames }</h4>
                    </div><!-- /.col -->
                </div><!-- /.row -->

                <div class="row">
                    <div class="col-md-12 col-sm-12 col-xs-12">
                        <h4>
                            <c:if test="${sjOrder.type==1}">
                                <h4>门头图片：</h4>
                            </c:if>
                            <c:if test="${sjOrder.type==2}">
                                <h4>受理单或合同图片：</h4>
                            </c:if>
                            <c:forEach items="${companyPictures }" var="item" varStatus="i">
                                <img src="${item.companyPictureUrl}" class="layui-upload-img"
                                     onclick="zoomImage('${item.companyPictureUrl}')" width="90" height="80"/>
                            </c:forEach>
                        </h4>
                    </div><!-- /.col -->
                </div><!-- /.row -->
            </td>
        </tr>

        <c:if test="${isWifi!=4}">
            <c:if test="${loginUserType==4||loginUserType==1}">
                <td colspan="3" class="tr-space"></td>
                </tr>
                <tr>
                    <td class="td-title">
                        <h4>录单信息：</h4>
                    </td>
                    <td class="td-space"></td>
                    <td class="td-info">
                        <div class="row">
                            <div class="col-md-12 col-sm-12 col-xs-12">
                                <c:if test="${isWifi==2||isWifi==0}">
                                    <button onclick="registerForm('${sjOrder.id}',1);"
                                            class="am-btn am-btn-default search_btn" type="button">
                                        <c:if test="${sjOrder.mealId!=0}">
                                            云监控编辑录单
                                        </c:if>
                                        <c:if test="${sjOrder.mealId==0}">
                                            云监控录单
                                        </c:if>
                                    </button>
                                </c:if>
                                <c:if test="${isWifi==1||isWifi==0}">
                                    <button onclick="registerForm('${sjOrder.id}',2);"
                                            class="am-btn am-btn-default search_btn" type="button">
                                        <c:if test="${sjOrder.mealWifiId!=0}">
                                            云wifi编辑录单
                                        </c:if>
                                        <c:if test="${sjOrder.mealWifiId==0}">
                                            云wifi录单
                                        </c:if>
                                    </button>
                                </c:if>
                            </div>
                        </div><!-- /.row -->
                    </td>
                </tr>
            </c:if>
            <c:if test="${sjOrder.mealId!=0}">
                <tr>
                    <td colspan="3" class="tr-space"></td>
                </tr>
                <tr>
                    <td class="td-title">
                        <h4>云监控信息：</h4>
                    </td>
                    <td class="td-space"></td>
                    <td class="td-info">
                        <div class="row">
                            <div class="col-md-6 col-sm-6 col-xs-12">
                                <h4>监控型号：${sjOrder.modelName }</h4>
                            </div><!-- /.col -->
                            <div class="col-md-6 col-sm-6 col-xs-12">
                                <h4>监控型号个数：${sjOrder.modelNum }</h4>
                            </div><!-- /.col -->
                        </div><!-- /.row -->
                        <div class="row">
                            <div class="col-md-6 col-sm-6 col-xs-12">
                                <h4>poe：${sjOrder.poeName }</h4>
                            </div><!-- /.col -->
                            <div class="col-md-6 col-sm-6 col-xs-12">
                                <h4>poe个数：${sjOrder.poeNum }</h4>
                            </div><!-- /.col -->
                        </div><!-- /.row -->
                        <div class="row">
                            <div class="col-md-6 col-sm-6 col-xs-12">
                                <h4>存储：${sjOrder.storageName }</h4>
                            </div><!-- /.col -->
                            <div class="col-md-6 col-sm-6 col-xs-12">
                                <h4>存储个数：${sjOrder.storageNum }</h4>
                            </div><!-- /.col -->
                        </div><!-- /.row -->
                    </td>
                </tr>
            </c:if>
            <c:if test="${sjOrder.mealWifiId!=0}">
                <tr>
                    <td colspan="3" class="tr-space"></td>
                </tr>
                <tr>
                    <td class="td-title">
                        <h4>云wifi信息：</h4>
                    </td>
                    <td class="td-space"></td>
                    <td class="td-info">
                        <div class="row">
                            <div class="col-md-6 col-sm-6 col-xs-12">
                                <h4>无线wifi：${sjOrder.modelWifiName }</h4>
                            </div><!-- /.col -->
                            <div class="col-md-6 col-sm-6 col-xs-12">
                                <h4>无线wifi个数：${sjOrder.modelWifiNum }</h4>
                            </div><!-- /.col -->
                        </div><!-- /.row -->
                        <div class="row">
                            <div class="col-md-6 col-sm-6 col-xs-12">
                                <h4>poe：${sjOrder.poeWifiName }</h4>
                            </div><!-- /.col -->
                            <div class="col-md-6 col-sm-6 col-xs-12">
                                <h4>poe个数：${sjOrder.poeWifiNum }</h4>
                            </div><!-- /.col -->
                        </div><!-- /.row -->
                        <div class="row">
                            <div class="col-md-6 col-sm-6 col-xs-12">
                                <h4>NET/网关/路由：${sjOrder.storageWifiName }</h4>
                            </div><!-- /.col -->
                            <div class="col-md-6 col-sm-6 col-xs-12">
                                <h4>NET/网关/路由个数：${sjOrder.storageWifiNum }</h4>
                            </div><!-- /.col -->
                        </div><!-- /.row -->
                    </td>
                </tr>
            </c:if>
        </c:if>
        <c:if test="${sjOrder.state==100}">
            <tr>
                <td colspan="3" class="tr-space"></td>
            </tr>
            <tr>
                <td class="td-title">
                    <h4>审批信息：</h4>
                </td>
                <td class="td-space"></td>
                <td class="td-info">
                    <div class="row">
                        <div class="col-md-12 col-sm-12 col-xs-12">
                            <textarea name="note" id="note" cols="79" rows="5" placeholder="请输入审批备注"></textarea>
                        </div><!-- /.col -->
                    </div><!-- /.row -->

                    <div class="index_but">
                        <button onclick="orderCancel('${sjOrder.id}','2');" class="am-btn am-btn-default search_btn"
                                type="button"> 不同意
                        </button>
                        <button id="orderDetailApprovalButton" onclick="orderCancel('${sjOrder.id}','1');"
                                class="am-btn am-btn-default search_btn" type="button"> 同意
                        </button>
                    </div>
                </td>
            </tr>
        </c:if>
        <c:if test="${sjOrder.state==200||sjOrder.state==300||sjOrder.state==400||sjOrder.state==500||sjOrder.state==600}">
            <tr>
                <td colspan="3" class="tr-space"></td>
            </tr>
            <tr>
                <td class="td-title">
                    <h4>审批信息：</h4>
                </td>
                <td class="td-space"></td>
                <td class="td-info">
                    <div class="row">
                        <div class="col-md-12 col-sm-12 col-xs-12">
                            <h4>备注：${sjOrder.approvalNote }</h4>
                        </div><!-- /.col -->
                    </div><!-- /.row -->
                    <div class="row">
                        <div class="col-md-6 col-sm-6 col-xs-12">
                            <h4>审批人：${sjOrder.approvalPerson }</h4>
                        </div><!-- /.col -->
                        <div class="col-md-6 col-sm-6 col-xs-12">
                            <h4>审批时间：<fmt:formatDate value="${sjOrder.approvalTime }"
                                                     pattern="yyyy-MM-dd HH:mm:ss"/></h4>
                        </div><!-- /.col -->
                    </div><!-- /.row -->
                </td>
            </tr>
        </c:if>

        <c:if test="${sjOrder.state==300}">
            <tr>
                <td colspan="3" class="tr-space"></td>
            </tr>
            <tr>
                <td class="td-title">
                    <h4>指派信息：</h4>
                </td>
                <td class="td-space"></td>
                <td class="td-info">
                    <div class="row">
                        <div class="col-md-12 col-sm-12 col-xs-12">
                            <h4>指派人：${sjOrder.assignPerson }</h4>
                        </div><!-- /.col -->
                        <div class="col-md-12 col-sm-12 col-xs-12">
                            <h4>审批时间：<fmt:formatDate value="${sjOrder.assignTime }" pattern="yyyy-MM-dd HH:mm:ss"/></h4>
                        </div><!-- /.col -->
                    </div><!-- /.row -->
                    <div class="row">
                        <div class="col-md-6 col-sm-6 col-xs-12">
                            <h4>待施工人：${sjOrder.buildPerson}</h4>
                        </div><!-- /.col -->
                        <div class="col-md-6 col-sm-6 col-xs-12">
                            <h4>单位：${sjOrder.buildCompany }</h4>
                        </div><!-- /.col -->
                    </div><!-- /.row -->
                </td>
            </tr>
        </c:if>
        <c:if test="${sjOrder.state==400 ||sjOrder.state==500}">
            <tr>
                <td colspan="3" class="tr-space"></td>
            </tr>
            <tr>
                <td class="td-title">
                    <h4>指派信息：</h4>
                </td>
                <td class="td-space"></td>
                <td class="td-info">
                    <div class="row">
                        <div class="col-md-12 col-sm-12 col-xs-12">
                            <h4>指派人：${sjOrder.assignPerson }</h4>
                        </div><!-- /.col -->
                        <div class="col-md-12 col-sm-12 col-xs-12">
                            <h4>审批时间：<fmt:formatDate value="${sjOrder.assignTime }"
                                                     pattern="yyyy-MM-dd HH:mm:ss"/></h4>
                        </div><!-- /.col -->
                    </div><!-- /.row -->
                </td>
            </tr>
        </c:if>
        <c:if test="${sjOrder.state>200 && isWifi!=4 && sjOrder.state!=600}">
            <tr>
                <td colspan="3" class="tr-space"></td>
            </tr>
            <tr>
                <td class="td-title">
                    <h4>工单导出：</h4>
                </td>
                <td class="td-space"></td>
                <td class="td-info">
                    <div class="row">
                        <div class="col-md-12 col-sm-12 col-xs-12">
                            <button onclick="constructionList('${sjOrder.id}');"
                                    class="am-btn am-btn-default search_btn"
                                    type="button">施工单导出
                            </button>
                        </div>
                    </div><!-- /.row -->
                </td>
            </tr>
        </c:if>
        <c:if test="${sjOrder.state==400 ||sjOrder.state==500}">
            <tr>
                <td colspan="3" class="tr-space"></td>
            </tr>
            <tr>
                <td class="td-title">
                    <h4>施工信息：</h4>
                </td>
                <td class="td-space"></td>
                <td class="td-info">
                    <div class="row">
                        <div class="col-md-6 col-sm-6 col-xs-12">
                            <h4>待施工人：${sjOrder.buildPerson}</h4>
                        </div><!-- /.col -->
                        <div class="col-md-6 col-sm-6 col-xs-12">
                            <h4>单位：${sjOrder.buildCompany }</h4>
                        </div><!-- /.col -->
                    </div><!-- /.row -->
                    <div class="row">
                        <div class="col-md-12 col-sm-12 col-xs-12">
                            <h4>完成时间：<fmt:formatDate value="${sjOrder.endTime }"
                                                     pattern="yyyy-MM-dd HH:mm:ss"/></h4>
                        </div><!-- /.col -->
                    </div><!-- /.row -->
                </td>
            </tr>
        </c:if>
        <c:if test="${sjOrder.state==300}">
            <c:if test="${loginUserType==8||loginUserType==1}">
                <tr>
                    <td colspan="3" class="tr-space"></td>
                </tr>
                <tr>
                    <td class="td-title">
                        <h4>竣工信息：</h4>
                    </td>
                    <td class="td-space"></td>
                    <td class="td-info">
                        <div class="row">
                            <div class="col-md-12 col-sm-12 col-xs-12">
                                <form enctype="multipart/form-data" id="uploadForm">
                                    <input class="col-sm-12" type="file" name="file" id="pic_img"
                                           accept="image/*"
                                        <%--onchange="contractImgChange(this);"--%>
                                    />
                                    <button onclick="upContractImage('${sjOrder.orderNo}','${sjOrder.id}');"
                                            class="am-btn am-btn-default search_btn"
                                            type="button">点击上传
                                    </button>
                                    <button id="contract" onclick="submitContract('${sjOrder.id}');"
                                            class="am-btn am-btn-default search_btn" type="button"> 竣工
                                    </button>
                                </form>
                            </div>
                        </div><!-- /.row -->
                        <div class="row">
                            <div class="col-md-12 col-sm-12 col-xs-12">
                                <h4>合同图片：
                                    <c:forEach items="${contractPictures }" var="item" varStatus="i">
                                        <img src="${item.contractPictureUrl}" class="layui-upload-img"
                                             onclick="zoomImage('${item.contractPictureUrl}')" width="90" height="80"/>
                                    </c:forEach>
                                </h4>
                            </div><!-- /.col -->
                        </div><!-- /.row -->
                    </td>
                </tr>
            </c:if>
        </c:if>
        <c:if test="${sjOrder.state==400}">
            <c:if test="${loginUserType==1 || loginUserType==3 ||loginUserType==8}">
                <tr>
                    <td colspan="3" class="tr-space"></td>
                </tr>
                <tr>
                    <td class="td-title">
                        <h4>完成操作：</h4>
                    </td>
                    <td class="td-space"></td>
                    <td class="td-info">
                        <div class="row">
                            <div class="col-md-12 col-sm-12 col-xs-12">
                                <button onclick="endOrder('${sjOrder.id}');"
                                        class="am-btn am-btn-default search_btn"
                                        type="button">完成
                                </button>
                            </div>
                        </div><!-- /.row -->
                    </td>
                </tr>
            </c:if>
        </c:if>
        <c:if test="${sjOrder.state==500}">
            <tr>
                <td colspan="3" class="tr-space"></td>
            </tr>
            <tr>
                <td class="td-title">
                    <h4>竣工信息：</h4>
                </td>
                <td class="td-space"></td>
                <td class="td-info">
                    <div class="row">
                        <div class="col-md-12 col-sm-12 col-xs-12">
                            <h4>竣工人：${sjOrder.completedPerson }</h4>
                        </div><!-- /.col -->
                        <div class="col-md-12 col-sm-12 col-xs-12">
                            <h4>竣工时间：<fmt:formatDate value="${sjOrder.completedTime }"
                                                     pattern="yyyy-MM-dd HH:mm:ss"/></h4>
                        </div><!-- /.col -->
                    </div><!-- /.row -->
                    <div class="row">
                        <div class="col-md-12 col-sm-12 col-xs-12">
                            <h4>合同图片：
                                <c:forEach items="${contractPictures }" var="item" varStatus="i">
                                    <img src="${item.contractPictureUrl}" class="layui-upload-img"
                                         onclick="zoomImage('${item.contractPictureUrl}')" width="90" height="80"/>
                                </c:forEach>
                            </h4>
                        </div><!-- /.col -->
                    </div><!-- /.row -->
                </td>
            </tr>

        </c:if>
    </table>
</div>
<div id="modal-registerFormView" class="modal fade" tabindex="-1" role="dialog" aria-hidden="true"
     style="display: none;">
</div>
<!-- /am-g -->
<script src="${webResourceUrl}/resource/layui/layui.all.js" type="text/javascript" charset="utf-8"></script>
<script type="text/javascript">

    // 选择图片显示
    function contractImgChange(obj) {
        var fileUrl = obj.value;
        if (!/.(gif|jpg|jpeg|png|GIF|JPG|bmp)$/.test(fileUrl)) {
            AlertText.tips("d_alert", "提示", "图片类型必须是.gif,jpeg,jpg,png,bmp中的一种");
            return false;
        } else {
            if (((obj.files[0].size).toFixed(2)) >= (200 * 1024)) {
                AlertText.tips("d_alert", "提示", "请上传小于200K的图片");
                return false;
            } else {
                var file = document.getElementById("file");
                var imgUrl = window.URL.createObjectURL(file.files[0]);
                var image = new Image();
                image.src = imgUrl;
                image.onload = function () {
                    //加载图片获取图片真实宽度和高度
                    var width = image.width;
                    var height = image.height;
                    if (width < 751 && height < 493) {
                        var img = document.getElementById('imghead');
                        img.setAttribute('src', imgUrl); // 修改img标签src属性值
                        $("#preview").show();
                    } else {
                        var msg = "文件尺寸应小于：750*492！,当前图片" + width + "*" + height;
                        AlertText.tips("d_alert", "提示", msg);
                        file.value = "";
                        return false;
                    }
                };
            }
        }
    }

    function upContractImage(orderNo, id) {
        var formData = new FormData($("#uploadForm")[0]) //创建一个forData
        var image = $('#pic_img').get(0).files[0];
        console.info(image);
        if (image) {
            formData.append('img', $('#pic_img')[0].files[0]) //把file添加进去 name命名为img
            formData.append('orderNo', orderNo)
            var url_ = AppConfig.ctx + "/sj/order/upContractImage.do";
            $.ajax({
                url: url_,
                data: formData,
                type: "POST",
                async: false,
                cache: false,
                contentType: false,
                processData: false,
                success: function (result) {
                    if (result.success) {
                        AlertText.tips("d_alert", "提示", "上传成功", function () {
                            func_reload_page("${ctx}/sj/order/detail.do?id=" + id);
                        });
                    } else {
                        AlertText.tips("d_alert", "提示", result.resultMessage);
                    }
                },
                error: function () {
                    AlertText.tips("d_alert", "提示", "系统异常，请稍后再试");
                }
            })
        } else {
            AlertText.tips("d_alert", "提示", "请选择上传文件！");
        }
    }

    function endOrder(id) {
        var url_ = AppConfig.ctx + "/sj/order/endOrder.do";
        $.ajax({
            url: url_,
            data: {id: id},
            type: "POST",
            dataType: "json",
            success: function (result) {
                if (result.success) {
                    AlertText.tips("d_alert", "提示", "操作成功", function () {
                        func_reload_page("${ctx}/sj/order/detail.do?id=" + id);
                    });
                } else {
                    AlertText.tips("d_alert", "提示", result.resultMessage);
                }
            },
            error: function () {
                AlertText.tips("d_alert", "提示", "系统异常，请稍后再试");
            }
        })
    }
    function toBigImg(obj) {
//        alert(parseInt(obj.style.zoom,10));
        var zoom = parseInt(obj.style.zoom, 10) || 100;
        zoom += event.wheelDelta / 12;
        if (zoom > 0) {
            obj.style.zoom = zoom + '%';
        }
        return false;
    }

    function toList() {
        func_reload_page("${ctx}/sj/order/list2.do");
    }

    function zoomImage(url) {
        var imgHtml = "<img src='" + url + "' width='auto' height='500' onmousewheel='return toBigImg(this)' style='cursor:pointer'/>";
        //弹出层
        layer.open({
            type: 1,
            shade: 0.8,
            offset: 'auto',
            area: ['auto', 'auto'],
            shadeClose: true,
            scrollbar: false,
            title: "图片预览", //不显示标题
            content: imgHtml //捕获的元素，注意：最好该指定的元素要存放在body最外层，否则可能被其它的相对元素所影响
            // cancel: function () {
            // layer.msg('捕获就是从页面已经存在的元素上，包裹layer的结构', { time: 5000, icon: 6 });
            // }
        });
    }

    function orderCancel(id, isPast) {
        var note = $("#note").val();
        if (note == null || note == "") {
            AlertText.tips("d_alert", "提示", "请输入备注");
        }
        var url_ = AppConfig.ctx + "/sj/order/approval.do";
        $.ajax({
            url: url_,
            type: "POST",
            data: {id: id, isPast: isPast, note: note},
            dataType: "json",
            success: function (result) {
                if (result.success) {
                    func_reload_page("${ctx}/sj/order/detail.do?id=" + id);
                } else {
                    AlertText.tips("d_alert", "提示", result.resultMessage);
                }
            },
            error: function () {
                AlertText.tips("d_alert", "提示", "系统异常，请稍后再试");
            }
        });
    }

    function registerForm(orderId, isWifi) {
        $("#modal-registerFormView").html("");
        $("#modal-registerFormView").load("${ctx}/sj/order/registerForm.do?orderId=" + orderId + "&isWifi=" + isWifi, function () {
            func_after_model_load(this);
        });
    }

    function submitContract(id) {
        var url_ = AppConfig.ctx + "/sj/order/contract.do";
        $.ajax({
            url: url_,
            type: "POST",
            data: {id: id},
            dataType: "json",
            success: function (result) {
                if (result.success) {
                    AlertText.tips("d_alert", "提示", "竣工成功", function () {
                        func_reload_page("${ctx}/sj/order/detail.do?id=" + id);
                    });
                } else {
                    AlertText.tips("d_alert", "提示", result.resultMessage);
                }
            },
            error: function () {
                AlertText.tips("d_alert", "提示", "系统异常，请稍后再试");
            }
        });
    }

    function constructionList(orderId) {
        window.open("${ctx}/sj/file/download.do?fileId=32&orderId=" + orderId, "导出");
    }
</script>
