$(function(){
    var pSize = 10,pIndex = 1;
    fn_loading();
    getSelInfo();

    function searchOrderList(upOrdown) {
        var param = {
            contactphone:eCacheUtil.storage.getCache(CacheKey.loginPhone),
            pageindex:pIndex,
            pagesize:pSize
        };
        $.post(linkUrl+'/recycle/getOrderList.do',{params:JSON.stringify(param)},function (data) {
            if (data.success){
                upOrdownFun(upOrdown,data);
                var dataInfo = data.result.datainfo;
                if (dataInfo.length == 0){
                    $('.baseline').remove();
                    $(".main").append('<p class="baseline blue" style="text-align: center;">——没有更多数据了！——</p>');
                    return false;
                }else {
                    eachFull(dataInfo);
                }
                loading_hide();
            }else {
                loading_hide();
                alertTip(data.resultMessage);
            }
        });
    }

    function getSelInfo() {
        pIndex = 1;//重置page
        searchOrderList("click");
    }

    mui('#scroll').pullRefresh({
        //下拉
        down:{
            contentrefresh:'加载',
            callback:pulldownRefresh
        },
        //上拉
        up:{
            contentrefresh: '正在加载...',
            callback:pullupRefresh
        }
    });

    function pulldownRefresh() {
        pIndex = 1;//重置page
        searchOrderList("down");
    }

    //上拉加载具体业务实现
    function pullupRefresh() {
        pIndex += 1;//上拉直接查询下一页数据
        searchOrderList("up");
    }

    //统一处理下拉上滑动画的显示隐藏
    function upOrdownFun(upOrdown,info) {
        //上拉
        if (upOrdown == "up") {
            if (info.result == "") {
                mui("#scroll").pullRefresh().endPullupToRefresh(true);
            } else {
                mui("#scroll").pullRefresh().endPullupToRefresh(false);
            }
        }
        //下拉
        else if (upOrdown == "down") {
            //清空
            $(".mui-scroll").empty();
            mui("#scroll").pullRefresh().endPulldownToRefresh();
            mui("#scroll").pullRefresh().refresh(true);
        }
        //进入
        else if (upOrdown == "click") {
            //清空
            $(".mui-scroll").empty();
            //重置
            mui("#scroll").pullRefresh().refresh(true);
        }
    }
});
function eachFull(info) {
    $.each(info, function (i, n) {
        var tmp = $("#template").clone();
        tmp.removeAttr("id");
        var orderId = n.orderid;
        $(".contentDiv",tmp).on('tap', function () {
            onDetails(orderId);
        });
        tmp.show();
        $('#orderId',tmp).html('订单号 : '+orderId);
        $('#status',tmp).html(n.processstatus_name);
        $('.image',tmp).html('<img src="'+n.modelpic+'">');
        var item = '';
        for (var j = 0;j < n.checkinfo.length;j++){
            item += n.checkinfo[j].checkname+n.checkinfo[j].levelname+'、';
        }
        $('.text',tmp).html('<p>'+n.modelname+'</p><p>'+item.substr(0,item.length-1)+'</p>');

        $('.priceBox',tmp).html('<p>预付款<span>￥2400</span></p><p>预估价<span>￥'+n.orderprice+'</span></p>');

        //追加到对应区块
        $(".mui-scroll").append(tmp);
    });
}

function onDetails(orderId) {
    window.location.href = "details.html?orderid=" + orderId;
}