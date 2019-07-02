<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/commons/taglibs.jsp" %>
<link rel="stylesheet" href="${webResourceUrl}/resource/order/css/order.detail.css">
<link rel="stylesheet" href="${webResourceUrl}/resource/layui/css/modules/laydate/default/laydate.css">
<link rel="stylesheet" href="${webResourceUrl}/resource/layui/css/modules/layer/default/layer.css">
<link rel="stylesheet" href="${webResourceUrl}/resource/layui/css/modules/code.css">
<link rel="stylesheet" href="${webResourceUrl}/resource/zoom/css/zoom.css">
<div class="am-cf am-padding am-padding-bottom-0">
    <div class="am-fl am-cf" style="width: 100%;">
        <strong class="am-text-primary am-text-lg"><a href="javascript:void(0);" onclick="toList();">商机报障管理</a></strong>
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
                已提交
            </c:if>
            <c:if test="${sjOrder.state==200}">
                待分配
            </c:if>
            <c:if test="${sjOrder.state==300}">
                待施工
            </c:if>
            <c:if test="${sjOrder.state==400}">
                待完成
            </c:if>
            <c:if test="${sjOrder.state==500}">
                已完成
            </c:if>
            <c:if test="${sjOrder.state==600}">
                已取消
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
                        <h4>报障单号：${sjOrder.reworkOrderNo }</h4>
                    </div><!-- /.col -->
                    <div class="col-md-6 col-sm-6 col-xs-12">
                        <h4>原订单号：${sjOrder.orderNo }</h4>
                    </div><!-- /.col -->
                </div><!-- /.row -->

                <div class="row">
                    <div class="col-md-6 col-sm-6 col-xs-12">
                        <h4>报障时间：<fmt:formatDate value="${sjOrder.createTime }" pattern="yyyy-MM-dd HH:mm:ss"/></h4>
                    </div><!-- /.col -->
                    <div class="col-md-6 col-sm-6 col-xs-12">
                        <h4>报障产品：${sjOrder.projectName}</h4>
                    </div><!-- /.col -->
                </div><!-- /.row -->

                <div class="row">
                    <div class="col-md-6 col-sm-6 col-xs-12">
                        <h4>创建人电话：${sjOrder.createUserid }</h4>
                    </div><!-- /.col -->
                    <div class="col-md-6 col-sm-6 col-xs-12">
                        <h4>备注：${sjOrder.note }</h4>
                    </div><!-- /.col -->
                </div><!-- /.row -->
                <c:if test="${sjOrder.companyId!=null}">
                    <div class="row">
                        <div class="col-md-6 col-sm-6 col-xs-12">
                            <h4>处理公司：${sjOrder.companyName }</h4>
                        </div><!-- /.col -->
                        <c:if test="${sjOrder.workerId!=null}">
                            <div class="col-md-6 col-sm-6 col-xs-12">
                                <h4>处理人员：${sjOrder.workerName }</h4>
                            </div>
                        </c:if>
                    </div>
                </c:if>
                <div class="row">
                    <c:if test="${sjOrder.workerTakeOrderTime!=null}">
                        <div class="col-md-6 col-sm-6 col-xs-12">
                            <h4>工人接单时间：<fmt:formatDate value="${sjOrder.workerTakeOrderTime }"
                                                       pattern="yyyy-MM-dd HH:mm:ss"/></h4>
                        </div>
                        <!-- /.col -->
                    </c:if>
                    <c:if test="${sjOrder.endTime!=null}">
                        <div class="col-md-6 col-sm-6 col-xs-12">
                            <h4>完成时间：<fmt:formatDate value="${sjOrder.endTime }" pattern="yyyy-MM-dd HH:mm:ss"/></h4>
                        </div>
                        <!-- /.col -->
                    </c:if>
                </div>
                <c:if test="${sjOrder.state==500}">
                    <div class="row">
                        <div class="col-md-12 col-sm-12 col-xs-12">
                            <h4>客户签字图片：
                                <c:forEach items="${pictures }" var="item" varStatus="i">
                                    <img src="${item.signPictureUrl}" class="layui-upload-img"
                                         onclick="zoomImage('${item.signPictureUrl}')" width="90" height="80"/>
                                </c:forEach>
                            </h4>
                                <%--<img src="${sjOrder.pictureUrl}" class="layui-upload-img"--%>
                                <%--onclick="zoomImage('${sjOrder.pictureUrl}')"--%>
                                <%--width="90" height="80"--%>
                                <%--style="cursor:pointer"/>--%>
                        </div><!-- /.col -->
                    </div>
                    <!-- /.row -->
                </c:if>
            </td>
        </tr>
        <c:if test="${(loginUserType==8||loginUserType==1)&&sjOrder.state==400}">
            <td colspan="3" class="tr-space"></td>
            </tr>
            <tr>
                <td class="td-title">
                    <h4>上传客户签字图片：</h4>
                </td>
                <td class="td-space"></td>
                <td class="td-info">
                    <div class="row">
                        <div class="col-md-12 col-sm-12 col-xs-12">
                            <form enctype="multipart/form-data" id="upSignForm">
                                <input class="col-sm-12" type="file" name="file" id="pic_img"
                                       accept="image/*"
                                    <%--onchange="contractImgChange(this);"--%>
                                />
                                <button onclick="upSignImage('${sjOrder.id}');"
                                        class="am-btn am-btn-default search_btn"
                                        type="button">点击上传
                                </button>
                                <button id="contract" onclick="submitReworkSign('${sjOrder.id}');"
                                        class="am-btn am-btn-default search_btn" type="button"> 完成
                                </button>
                            </form>
                        </div>
                    </div><!-- /.row -->
                    <div class="row">
                        <div class="col-md-12 col-sm-12 col-xs-12">
                            <h4>签字图片：
                                <c:forEach items="${pictures }" var="item" varStatus="i">
                                    <img src="${item.signPictureUrl}" class="layui-upload-img"
                                         onclick="zoomImage('${item.signPictureUrl}')" width="90" height="80"/>
                                </c:forEach>
                            </h4>
                        </div><!-- /.col -->
                    </div><!-- /.row -->
                </td>
            </tr>
        </c:if>
    </table>
</div>
<div id="modal-toAssignReworkFormView" class="modal fade" tabindex="-1" role="dialog" aria-hidden="true"
     style="display: none;">
</div>
<!-- /am-g -->
<script src="${webResourceUrl}/resource/zoom/js/zoom.js" type="text/javascript" charset="utf-8"></script>
<%--<script src="${webResourceUrl}/plugins/assets/js/" type="text/javascript" charset="utf-8"></script>--%>
<script src="${webResourceUrl}/resource/layui/layui.all.js" type="text/javascript" charset="utf-8"></script>
<script type="text/javascript">
    function toList() {
        func_reload_page("${ctx}/sj/order/reworkList.do");
    }

    $(function () {
        /*
         smallimg   // 小图
         bigimg  //点击放大的图片
         mask   //黑色遮罩
         */
        var obj = new zoom('mask', 'bigimg', 'smallimg');
        obj.init();
    })

    function toBigImg(obj) {
//        alert(parseInt(obj.style.zoom,10));
        var zoom = parseInt(obj.style.zoom, 10) || 100;
        zoom += event.wheelDelta / 12;
        if (zoom > 0) {
            obj.style.zoom = zoom + '%';
        }
        return false;
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
        });
    }

    function assign(reworkId) {
        $("#modal-toAssignReworkFormView").html("");
        $("#modal-toAssignReworkFormView").load("${ctx}/sj/order/toSelectWorker.do?reworkId=" + reworkId, function () {
            func_after_model_load(this);
        });
    }

    function upSignImage(reworkId) {
        var formData = new FormData($("#upSignForm")[0]); //创建一个forData
        var image = $('#pic_img').get(0).files[0];
        console.info(image);
        if (image) {
            formData.append('img', $('#pic_img')[0].files[0]); //把file添加进去 name命名为img
            formData.append('reworkId', reworkId);
            var url_ = AppConfig.ctx + "/sj/order/upSignImage.do";
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
                            func_reload_page("${ctx}/sj/order/reworkOrderDetail.do?id=" + reworkId);
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

    function submitReworkSign(id) {
        var url_ = AppConfig.ctx + "/sj/order/submitReworkSign.do";
        $.ajax({
            url: url_,
            type: "POST",
            data: {id: id},
            dataType: "json",
            success: function (result) {
                if (result.success) {
                    AlertText.tips("d_alert", "提示", "完成成功", function () {
                        func_reload_page("${ctx}/sj/order/reworkOrderDetail.do?id=" + id);
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
</script>
