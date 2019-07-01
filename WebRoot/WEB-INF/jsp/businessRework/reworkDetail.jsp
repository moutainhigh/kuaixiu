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
                            <h4>
                                <h4>客户签字图片：</h4>
                                <img src="${sjOrder.pictureUrl}" class="layui-upload-img"
                                     onclick="zoomImage('${sjOrder.pictureUrl}')"
                                     width="90" height="80"
                                     style="cursor:pointer"/>
                            </h4>
                        </div><!-- /.col -->
                    </div>
                    <!-- /.row -->
                </c:if>
            </td>
        </tr>
        <c:if test="${loginUserType==3&&sjOrder.state==200}">
            <td colspan="3" class="tr-space"></td>
            </tr>
            <tr>
                <td class="td-title">
                    <h4>订单分配：</h4>
                </td>
                <td class="td-space"></td>
                <td class="td-info">
                    <div class="row">
                        <div class="col-md-12 col-sm-12 col-xs-12">
                            <button onclick="assign('${sjOrder.id}');"
                                    class="am-btn am-btn-default search_btn" type="button">
                                分配订单
                            </button>
                        </div>
                    </div><!-- /.row -->
                </td>
            </tr>
        </c:if>
        <c:if test="${loginUserType==8&&sjOrder.state==400}">
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
                                <input class="col-sm-12" type="file" style="width:420px" name="pic_img" id="pic_img"
                                       accept="image/*"
                                       onchange="imgChange(this);"/>
                                <div id="preview" hidden="hidden" class="col-sm-9">
                                    <img id="imghead" src="" width="260" height="180"/> <!--图片显示位置-->
                                </div>
                            </form>
                        </div>
                        <div class="col-md-12 col-sm-12 col-xs-12">
                            <button onclick="upSignImage('${sjOrder.id}');"
                                    class="am-btn am-btn-default search_btn"
                                    type="button">点击上传图片并完成订单
                            </button>
                        </div>
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
        func_reload_page("${ctx}/sj/order/list.do");
    }

    // 选择图片显示
    function imgChange(obj) {
        var fileUrl = obj.value;
        if (!/.(gif|jpg|jpeg|png|GIF|JPG|bmp)$/.test(fileUrl)) {
            AlertText.tips("d_alert", "提示", "图片类型必须是.gif,jpeg,jpg,png,bmp中的一种");
            return false;
        } else {
            if (((obj.files[0].size).toFixed(2)) >= (200 * 1024)) {
                AlertText.tips("d_alert", "提示", "请上传小于200K的图片");
                return false;
            } else {
                var file = document.getElementById("pic_img");
                var imgUrl = window.URL.createObjectURL(file.files[0]);
                var image = new Image();
                image.src = imgUrl;
                image.onload = function () {
                    //加载图片获取图片真实宽度和高度
                    var width = image.width;
                    var height = image.height;
                    if (width < 751 && height < 533) {
                        var img = document.getElementById('imghead');
                        img.setAttribute('src', imgUrl); // 修改img标签src属性值
                        $("#preview").show();
                    } else {
                        var msg = "文件尺寸应小于：750*532！,当前图片" + width + "*" + height;
                        AlertText.tips("d_alert", "提示", msg);
                        file.value = "";
                        return false;
                    }
                };
            }
        }
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
//            cancel: function () {
//                layer.msg('捕获就是从页面已经存在的元素上，包裹layer的结构', { time: 5000, icon: 6 });
//            }
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
                            func_reload_page("${ctx}/sj/order/reworkOrderDetail.do?id=" + id);
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
</script>
