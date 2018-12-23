<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/commons/taglibs.jsp" %>
<link rel="stylesheet" href="${webResourceUrl}/resource/order/css/order.detail.css">
<div class="am-cf am-padding am-padding-bottom-0">
    <div class="am-fl am-cf" style="width: 100%;">
        <strong class="am-text-primary am-text-lg"><a href="javascript:void(0);"  onclick="toList();">微信用户</a></strong> / <small>用户详情</small>
        <strong class="am-text-primary"><a href="javascript:void(0);"  onclick="func_to_back();">返回</a></strong>
    </div>
</div>





<div class="am-g order-info mt20">
    <table class="detail-table">
        <tr>
            <td class="td-title">
                <h4>用户微信信息：</h4>
            </td>
            <td class="td-space"></td>
            <td class="td-info">

                <div class="row">
                    <div class="col-md-6 col-sm-6 col-xs-12">
                        <h4>微信昵称：${wechat.nick}</h4>
                    </div><!-- /.col -->
                    <div class="col-md-6 col-sm-6 col-xs-12">
                        <h4>手机号：${wechat.prizeMobile}</h4>
                    </div><!-- /.col -->
                </div><!-- /.row -->

                <div class="row">
                    <div class="col-md-6 col-sm-6 col-xs-12">
                        <h4>手机品牌：${wechat.brand }</h4>
                    </div><!-- /.col -->
                    <div class="col-md-6 col-sm-6 col-xs-12">
                        <h4>手机机型：${wechat.model}</h4>
                    </div><!-- /.col -->
                </div><!-- /.row -->

        </tr>
        <tr><td colspan="3" class="tr-space"></td></tr>



        <tr>
            <td class="td-title">
                <h4>用户抽奖信息：</h4>
            </td>
            <td class="td-space"></td>
            <td class="td-info">

                <div class="row">
                    <div class="col-md-6 col-sm-6 col-xs-12">
                        <h4>收货人姓名：${wechat.prizeName }</h4>
                    </div><!-- /.col -->
                    <div class="col-md-6 col-sm-6 col-xs-12">
                        <h4>收货人手机号：${wechat.prizeMobile}</h4>
                    </div><!-- /.col -->
                </div><!-- /.row -->

                <div class="row">
                    <div class="col-md-12 col-sm-12 col-xs-12">
                        <h4>收货人地址：${wechat.prizeProvince} ${wechat.prizeCity} ${wechat.prizeArea} ${wechat.prizeStreet}</h4>
                    </div><!-- /.col -->
                    <div class="col-md-12 col-sm-12 col-xs-12">
                        <h4>抽奖手机号：${wechat.lotteryMobile }</h4>
                    </div><!-- /.col -->
                </div><!-- /.row -->


            </td>
        </tr>


    </table>
</div><!-- /am-g -->




