/**
 * Created by chengmeng on 2015/5/13.
 */


/**
 * 创建类 模板,需要实现initialize方法
 * @type {{create: Function}}
 */

var AlertText = {
	time_out_id : [],
    //失败弹出层提示，type:1,延迟消失，2：不延迟；operationType：按钮格式控制，d_alert，d_confirm，确定和取消按钮;text：显示错误内容
    tips: function (operationType,txt, text, callback) {
        $('#mcon').html('');
//        var content = text.split(":")[1];
//        var regex = /\([^\)]+\)/g
//        var v = '';
//        if(text.split(":")[0] == "400"){
//            v=""+regex.exec(content);
//            v= v.substring(1,v.length-1)
//        }else{
//            v= content
//        }
        // var html_text = '<div class="error_word"><span class="fl error_icon"></span><p class="pop_big_word">'+text+'</p></div>'
        var html_btn = '';
        switch (operationType) {
            case "d_alert" :
                html_btn = '<div id="cancelOrder"><div class="titles"><span>'+txt+'</span></div><div class="neirong"><i style="float: left;"></i><div style="float: left; margin: 10px 0 0 10px;">' + text + '</div></div><p><button class="sure_btn">确&nbsp;&nbsp;定</button></p></div>';
                break;
            case "d_List" :
                html_btn = '<div id="cancelOrder"><div class="titles"><span>'+txt+'</span></div><div class="neirong"><span>' + text + '</span></div><p><button class="sure_btn">确&nbsp;&nbsp;定</button></p></div>';
                break;
            case "d_confirm" :
                html_btn = '<div id="cancelOrder"><div class="titles"><span>'+txt+'</span><span class="close_cancelOrder"></span></div><div class="neirong"><i></i><span>' + text + '</span></div><p><button id="sureBtn" class="affirm_btn">确&nbsp;&nbsp;定</button><button class="return_back" onclick="AlertText.closeTips()">取&nbsp;&nbsp;&nbsp;消</button></p></div>';
                break;
            case "d_load" :
            	if(!text){
            		text = "努力为您加载，请稍后"
            	}
                html_btn = '<div class="pop_cancel_order"><a href="javascript:;" class="btn_flight_close"></a><div class="clearfix flight_tip_box"><div class="tip_icon"></div><div class="tip_worp">'+text+'</div></div></div>';
                break;
            case "d_loading":
            	html_btn = "<div class='pop_cancel_order1'><img src='"+AppConfig.ctx+"/resource/login/images/loadings.gif' style='width: 80px;height:80px;'/></div>";
//            	html_btn = "<div class='pop_cancel_order1'><img src='../../source/images/ebt/loading.gif'/></div>";
            	break;
            case "d_pop" :            	
                html_btn = '';
                break;
            case "dely_load" :
                html_btn = '<div class="pop_cancel_order"><a href="javascript:;" class="btn_flight_close"></a><div class="clearfix flight_tip_box"><div class="tip_icon"></div><div class="tip_worp">努力为您加载，请稍后!</div></div></div>';
                break;
            case "hide" :
                //    html_btn = '<div style="margin-top:20px;" class="text_center pop_btn_box"><a class="pop_btn_grey" href="javascript:void(0)" onclick="AlertText.closeTips()">确定</a></div>';
                //   setTimeout(AlertText.closeTips,5000)
                break;
            case "d_success" :
                html_btn = '<div id="cancelOrder"><span class="close_cancelOrder">x</span><div><i class="icon_Prosperity"></i><span>' + text + '</span></div><p><button class="sure_btn">确定</button></p></div>';
                break;
            case "auto_close" :
                html_btn = '<div id="cancelOrder"><span class="close_cancelOrder">x</span><div><i></i><span>' + text + '</span></div><p><button class="sure_btn">确定</button></p></div>';
                if (callback) {
                    setTimeout(callback, 3000);
                } else {
                    setTimeout(AlertText.closeTips, 5000);
                }
                break;
            case "hide_s" :
                //     html_btn = '<div style="margin-top:20px;" class="text_center pop_btn_box"><a class="pop_btn_grey" href="javascript:void(0)" onclick="AlertText.closeTips()">确定</a></div>';
//                setTimeout(function(){
//                    window.location.href ="/serchTracking.html"
//                },5000);
                break;
            case "payment":
            	html_btn='<div id="cancelOrder"><div class="titles"><span>'+txt+'</span></div><div class="neirong"><span>' + text + '</span></div><p><button class="payBtn">已完成支付</button><button class="payBtn">支付遇到问题</button></p></div>';
            	break;
        }
        //$('#mcon').append(html_btn);
        	$(html_btn).appendTo('#mcon');

            $(".close_cancelOrder").click(function () {
                AlertText.closeTips();
            });
            $(".sure_btn").click(function () {
                AlertText.calTips(callback);
            });
            $(".affirm_btn").click(function () {
            	AlertText.calTips(callback);
            });
            if(operationType=="dely_load"){
            	var this_id_ = setTimeout(function(){
            		if(AlertText.time_out_id.length>0){
            			$('#mask_boxs').show();
            		}
            	},3000);
            	AlertText.time_out_id.push(this_id_);
//            	$('#mask_boxs').show();
            	
            }else{
            	$('#mask_boxs').show();
            }

        // $(".pop_cover").show();
        // $("#booking_pop_content").show().append(html_text+html_btn)
    },
    calTips: function (callback) {
        $('#mask_boxs').hide();
        if(AlertText.time_out_id){
        	for(out_id_ in AlertText.time_out_id){
        		clearTimeout(out_id_);
        	}
        	AlertText.time_out_id = [];
        }
        if (callback) {
            callback();
        }
    },
    closeTips: function () {
    	 if(AlertText.time_out_id){
         	for(out_id_ in AlertText.time_out_id){
         		clearTimeout(out_id_);
         	}
         	AlertText.time_out_id = [];
         }
        $('#mcon').html('');
        $('#mask_boxs').hide();
//        $(".pop_cover").hide();
//        $("#booking_pop_content").empty().hide();
//        $("#loading_jpg").hide();
        //window.location.reload()
    },
    isShow : function(){
    	if($('#mask_boxs').css("display")=="none"){
    		return false;
    	}else{
    		return true;
    	}
    	
    },
    hide : function(){
    	$('#mask_boxs').hide();
    }
}
/**
 * 字符串替换
 * @param content 原始字符串
 * @param keys key串
 * @param rStr 替换串
 * @returns 替换后整个字符串
 */
function replaceStr(content, keys, rStr) {
    while (content.indexOf(keys) != -1) {
        content = content.replace(keys, rStr);
    }
    return content;
}
