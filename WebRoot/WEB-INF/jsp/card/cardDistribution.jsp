<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/commons/taglibs.jsp" %>
<link rel="stylesheet"
      href="${webResourceUrl}/resource/order/css/order.detail.css">
<div class="am-cf am-padding am-padding-bottom-0">
    <div class="am-fl am-cf" style="width: 100%;">
        <strong class="am-text-primary am-text-lg"><a
                href="javascript:void(0);" onclick="toList();">批次管理</a></strong> /
        <small>批次分配</small>
        <strong class="am-text-primary"><a
                href="javascript:void(0);" onclick="func_to_back();">返回</a></strong>
    </div>
</div>


<div class="am-g order-info mt20">
    <table class="detail-table">
        <tr>
            <td class="td-info">
                <div class="row">
                    <div class="text-left">
                        <h4 style="position:relative;left:25px;">批次信息：</h4>
                    </div>
                </div>
                <div style="height:25px;"></div>
                <table class="table table-bordered">
                    <tr>
                        <th>序号</th>
                        <th style="display: none">id</th>
                        <th>起始ICCID</th>
                        <th>结束ICCID</th>
                        <th>数量（张）</th>
                        <th>当前已分配（张）</th>
                        <th>初始已分配（张）</th>
                        <th>号卡类型</th>
                        <th>号卡名称</th>
                        <th>所属地市</th>
                        <th>失效时间</th>
                        <th>操作</th>
                    </tr>
                    <c:forEach items="${batchCardList }" var="item" varStatus="i">
                        <tr>
                            <td>${i.index+1}</td>
                            <td style="display: none;">${item.id}</td>
                            <td>${item.beginIccid}</td>
                            <td>${item.endIccid}</td>
                            <td>${item.sum}</td>
                            <td>${item.distributionSum}</td>
                            <td>${item.distributionSum}</td>
                            <td>
                                <c:choose>
                                    <c:when test="${item.type == 0 }">
                                        小白卡
                                    </c:when>
                                    <c:when test="${item.type == 1 }">
                                        即买即通卡
                                    </c:when>
                                </c:choose>
                            </td>
                            <td>
                                <c:choose>
                                    <c:when test="${item.cardName == 0 }">
                                        白金卡
                                    </c:when>
                                    <c:when test="${item.cardName == 1 }">
                                        抖音卡
                                    </c:when>
                                    <c:when test="${item.cardName == 2 }">
                                        鱼卡
                                    </c:when>
                                    <c:when test="${item.cardName == 3 }">
                                        49元不限量卡
                                    </c:when>
                                    <c:when test="${item.cardName == 4 }">
                                        99元不限量卡
                                    </c:when>
                                    <c:when test="${item.cardName == 5 }">
                                        199元不限量卡
                                    </c:when>
                                </c:choose>
                            </td>
                            <td>${item.province}</td>
                            <td>
                                <fmt:formatDate value="${item.loseEfficacy}" pattern="yyyy-MM-dd" />
                            </td>
                            <td>
                                <button value=${item.id}  onclick="express(this)">分配</button>
                            </td>
                        </tr>
                    </c:forEach>
                </table>
            </td>
        </tr>
    </table>
</div>



<!--
<div class="am-g order-info mt20">
    <h4 align="center">分配时，如分配的起始ICCID为10000已分配数量为100 那么点击分配 ，输入分配数量100   得到分配的号卡区间为10100-10199 以此类推 </h4>
</div>
-->


<!--------------------------分配物流信息-------------------------------->
<div class="am-g order-info mt20">
    <table class="detail-table">
        <tr>
            <td class="td-info">
                <div class="row">
                    <div class="text-left">
                        <table>
                            <tr>
                            <td><h4 style="position:relative;left:25px;">物流信息： </h4></td>
                        </tr>
                        </table>
                    </div>
                </div>
                <div style="height:25px;"></div>


                <div class="row">
                    <div class="col-md-1 col-sm-1 col-xs-6"></div>

                    <div class="col-md-4 col-sm-4 col-xs-6">
                        <table>
                            <tr>
                                <td><h4>号卡数量：</h4></td>
                                <td><input type="text" id="addSum"  class="form-control" placeholder="请输入号卡数量" value=""></td>
                            </tr>
                        </table>
                    </div><!-- /.col -->


                    <div class="col-md-4 col-sm-4 col-xs-6">
                        <table>
                            <tr>
                                <td><h4>发货时间：</h4></td>
                                <td><input type="text" id="addTime" name="addTime" class="form-control" placeholder="请输入发货时间" ></td>
                            </tr>
                        </table>
                    </div><!-- /.col -->
                </div><!-- /.row -->

                <div class="row"></div>

                <div class="row">
                    <div class="col-md-1 col-sm-1 col-xs-6"></div>

                    <div class="col-md-4 col-sm-4 col-xs-6">
                        <table>
                            <tr>
                                <td><h4>物流公司：</h4></td>
                                <td> <select id="expressId">
                                    <option value="">-请选择-</option>
                                    <c:forEach items="${expressList}" var="item">
                                        <option value=${item.expressCode}>${item.expressName}</option>
                                    </c:forEach>
                                </select>
                                </td>
                            </tr>
                        </table>
                    </div><!-- /.col -->


                    <div class="col-md-4 col-sm-4 col-xs-6">
                        <table>
                            <tr>
                                <td><h4>物流单号：</h4></td>
                                <td><input type="text" id="addExpressNumber" class="form-control" placeholder="请输入物流单号" value=""></td>
                            </tr>
                        </table>
                    </div><!-- /.col -->
                </div><!-- /.row -->

                <div class="row"></div>


                <div class="row">
                    <div class="col-md-1 col-sm-1 col-xs-6"></div>

                    <div class="col-md-4 col-sm-4 col-xs-6">
                        <table>
                            <tr>
                                <td><h4><a href="javascript:void(0);" onclick="selectStaion()">发往的站点：</a></h4></td>
                                <td><select id="stationId">
                                    <option value="">-请选择-</option>
                                    <c:forEach items="${stationList}" var="item">
                                        <option value=${item.id}>${item.stationName}</option>
                                    </c:forEach>
                                </select>
                                </td>
                            </tr>
                        </table>
                    </div><!-- /.col -->
                </div><!-- /.row -->

                <div class="btn-group" role="group" aria-label="...">
                    <button type="button" class="btn btn-default" id="addSaveBtn">确定物流信息</button>
                </div>



            </td>
        </tr>
    </table>
</div>





<!--分配后的信息-->
<div id='distribution' class="am-g order-info mt20" style="display: none">
    <table class="detail-table">
        <tr>
            <td class="td-info">
                <div class="row">
                    <div class="text-left">
                        <h4 style="position:relative;left:25px;">已分配信息：</h4>
                    </div>
                </div>
                <div style="height:25px;"></div>
                <table class="table table-bordered" id="distribution_express">
                    <tr>
                        <th>序号</th>
                        <th style="display: none;">id</th>
                        <th>起始ICCID</th>
                        <th>结束ICCID</th>
                        <th>数量（张）</th>
                        <th>号卡类型</th>
                        <th>号卡名称</th>
                        <th>收货站点</th>
                        <th>物流公司</th>
                        <th>发货单号</th>
                        <th>发货时间</th>
                        <%--<th>操作</th>--%>
                    </tr>
                </table>

                <div class="btn-group" role="group" aria-label="...">
                    <button type="button" class="btn btn-default" onclick="createOrder()">提交分配订单</button>
                </div>

            </td>
        </tr>
    </table>

</div>

<!-- 配置物流信息弹窗 end -->
<div id="modal-insertView" class="modal fade" tabindex="-1" role="dialog" aria-hidden="true" style="display: none;">
    <%@ include file="addExpressNews.jsp" %>
</div>


<script type="text/javascript">
    //时间控件
    $("#addTime").datetimepicker({
        format: "yyyy-mm-dd",
        language: "zh-CN",
        autoclose:true,//选中关闭
        minView: "month"//设置只显示到月份
    });


    var jsonstr="[]";
    var jsonarray = eval('('+jsonstr+')');   //创建json数据  提交到后台需要

    var sum=0;         //分配的序号
    var id="";         //分配的id
    var batchId="";    //分配的批次id
    var beginIccid=""; //开始分配的iccid
    var endIccid="";   //结束分配的iccid
    var cardType="";   //号卡类型
    var cardName="";   //号卡名称
    var totalSum=0;    //该批次号卡的总数量
    var useSum=0;      //已分配的号卡数量
    var es="";          //操作的dom对象
    /**
     *  弹出物流分配信息填写框
     */
    function express(e) {
        //防止多次点击颜色冲突
        $(es).parent().parent().css("color","#333");
        es="";

        //选中记录加红
        $(e).parent().parent().css("color","green");

        //清空物流信息的信息  和选中框
        $('#addSum').val("");
        $('#addTime').val("");
        $('#addExpressNumber').val("");
        $('select').prop('selectedIndex', 0);

        es=e;
       // $("#modal-insertView").modal("show");
        batchId=e.getAttribute("value");
        id=$(e).parent().parent().children("td").eq(1).text();
        beginIccid=$(e).parent().parent().children("td").eq(2).text();
        totalSum=$(e).parent().parent().children("td").eq(4).text();              //该批次总数量
        useSum=$(e).parent().parent().children("td").eq(5).text();                //当前已分配的张数
        beginIccid=new BigDecimal(beginIccid).add(new BigDecimal(useSum)).toString();
        console.log(beginIccid+" 总数量"+totalSum);
        cardType=$(e).parent().parent().children("td").eq(7).text();
        cardName=$(e).parent().parent().children("td").eq(8).text();
    }





    /**
     **确认物流信息
     */
    function  confrimNews(express) {


        $('#distribution').show();
        sum=sum+1;                  //序号
        var appendNews="<tr>" +
            "<td>"+sum+"</td>" +
            "<td style='display: none'>"+batchId+"</td>" +
            "<td>"+beginIccid+"</td>" +
            "<td>"+endIccid+"</td>" +
            "<td>"+express.sum+"</td>" +
            "<td>"+cardType+"</td>" +
            "<td>"+cardName+"</td>" +
            "<td>"+express.stationName+"</td>" +
            "<td>"+express.expressName+"</td>" +
            "<td>"+express.expressNumber+"</td>" +
            "<td>"+express.time+"</td>" +
            // "<td> <button onclick='delExpress(this)'>取消</button></td>" +
            "<td style='display:none'>"+express.expressId+"</td>" +
            "<td style='display:none'>"+express.stationId+"</td>" +
            "</tr>";
        $('#distribution_express').append(appendNews);
        //确认分配后  消除颜色加红   清除当前选中的dom
        $(es).parent().parent().css("color","#333");
        es="";

    }



    /**
     **创建订单
     */
    function  createOrder() {
        AlertText.tips("d_confirm", "温馨提示", "确定创建订单吗？", function(){
            //加载等待
            AlertText.tips("d_loading");
            var url_ = AppConfig.ctx + "/telecom/telecom/createOrder.do";
           //在提交订单的时候 循环取出table里的值 添加到jsonarray里
            $("#distribution_express").find("tr").each(function(){
                var tdArr = $(this).children();
                var realBatchId = tdArr.eq(1).text();         //批次id
                var beginIccid = tdArr.eq(2).text();          //起始iccid
                var endIccid = tdArr.eq(3).text();            //结束iccid
                var realSum = tdArr.eq(4).text();             //数量
                var realStationId = tdArr.eq(12).text();      //站点id
                var realExpress =tdArr.eq(11).text();         //物流编码
                var realDeliverOrder = tdArr.eq(9).text();    //物流单号
                var realDeliverTime = tdArr.eq(10).text();    //发货时间
                console.log('站点id：'+realStationId);
                //封装对象
                var real = {
                    batchId:realBatchId,
                    beginIccid:beginIccid,
                    endIccid:endIccid,
                    expressName:realExpress,
                    stationId:realStationId,
                    sum:realSum,
                    time:realDeliverTime,
                    expressNumber:realDeliverOrder
                };
                jsonarray.push(real);        //添加数据到数组
            });
            jsonarray=jsonarray.splice(1);   //删除导航栏数据
            $.ajax({
                url: url_,
                data: JSON.stringify(jsonarray),
                type: "POST",
                dataType: "json",
                contentType: "application/json",
                success: function (result) {
                    if (result.success) {
                        //提交成功后跳转到批次列表
                        func_reload_page("${ctx}/telecom/batch/card.do?");
                        // refreshPage();
                        //提交成功 数据重置
                    } else {
                        if(isEmpty(result.resultMessage)){
                            AlertText.tips("d_alert", "提示", result.msg);
                        }else{
                            AlertText.tips("d_alert", "提示", result.resultMessage);
                        }

                        return false;
                    }
                    //隐藏等待
                    AlertText.hide();
                }
            });
        });
    }

    /**
     * 删除分配信息
     * @param e
     */
    function delExpress(e) {
        console.log("删除物流信息");
        //更新分配数量
        var isUse=$(e).parent().parent().children("td").eq(4).text();
        $(es).parent().parent().children("td").eq(5).html(parseInt(useSum)-parseInt(isUse));
        $(e).parent().parent().remove();
    }



    //点击保存物流信息按钮,分配数据
    $("#addSaveBtn").click(function() {
        if(es==""){
            alert('请先点击分配按钮');
            return;
        }
        //$("#addMissBtn").click(); //关闭弹窗
        //获取填写的数据
        var option1=$("#expressId option:selected");  //获取选中的物流公司编码
        var expressId=option1.val();
        var expressName=option1.text();
        var option2=$("#stationId option:selected");  //获取选中的站点
        var stationId=option2.val();
        var stationName=option2.text();

        //获取数量 时间 物流号
        var sum=$('#addSum').val();
        var time=$('#addTime').val();
        var expressNumber=$('#addExpressNumber').val();
        if(isEmpty(sum)||isEmpty(time)||isEmpty(expressNumber)||expressName=='-请选择-'||stationName=='-请选择-'){
            alert("请输入物流信息");
            return;
        }
        //封装传递对象
        var express = {
            batchId:batchId,
            expressId:expressId,
            expressName:expressName,
            stationId:stationId,
            stationName:stationName,
            sum:sum,
            time:time,
            expressNumber:expressNumber
        };
        var resetSum=useSum;
        useSum=parseInt(useSum)+parseInt(sum);
        //判断更新后的次数是否超过该批次的总数量
    //    console.log('已使用：'+useSum+"  总数："+totalSum);
        if(useSum>totalSum){
            //重置数量
            useSum=resetSum;
            alert('分配数额已超过该批次可分配数量');
            return;
        }
        //更新已分配数量   增加高亮
        $(es).parent().parent().children("td").eq(5).html(useSum);
        $(es).parent().parent().children("td").addClass("color:red");
        //设置结束iccid的值
        endIccid=  new BigDecimal(beginIccid).add(new BigDecimal((parseInt(sum)-parseInt(1)).toString())).toString();
        confrimNews(express);  //开始分配


    });

    /**
     * 站点搜索弹出框
     */
    function selectStaion(){
        $("#modal-insertView").modal("show");
        queryStaion();  //初始化数据
    }


</script>



