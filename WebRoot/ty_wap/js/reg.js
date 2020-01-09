(function () {
    init();
    bind();
})($)
var linkUrl="";
function init(){

//绝对路径
  getRealPath();
}

function bind(){
$("#telNumberClick").click(function(e){
    var temb=$(".telNumber").val();
    selcetNnmberFun(temb);
})


    $(".weui-btn_primary").click(function(e){
        addNumberFunc();
    })

}
function  preBindFunc(){
    var orderNumb="";
    $(".jiuPhone .radioList").on("click",function(e){
        if(!e.isPropagationStopped()){//确定stopPropagation是否被调用过
            $(".radioList .radio").attr("checked","false").removeClass("on")
            $(this).find(".radio").attr("checked","true").addClass("on")
        }
        e.stopPropagation();//必须要，不然e.isPropagationStopped()无法判断stopPropagation是否调用过
    })
}

//旧手机列表
function  selcetNnmberFun(temb){
    var data={
        mobile:temb
    }
    $.post(linkUrl+'/registration/getOrderList.do',{params:JSON.stringify(data)},
        function(data){
            if(data.success) {
               var  html="";
               data.result.list.forEach((item,index)=>{
                   html +='   <label class="radioList">\n' +
                       '<input type="radio" orderNo="'+item.orderNo+'" value="'+item+'" price="'+item.price+'" class="radio" name="radio1" id="x11">'+
                       '<div class="radioCont">\n' +
                       '<p><span>订单号</span><span>价格</span></p>\n' +
                       '<p><span>'+item.orderNo+'</span><span>'+item.price+'</span></p>\n' +
                       '</div>\n' +
                       '</label>'
               })
               $(".jiuPhone").html(html);
                preBindFunc();
            }else {
                alertTip(data.resultMessage);
            }
        })
}

// 提交接口
function addNumberFunc(){
    if(!$(".telNumber").val()) {
        alertTip('请输入手机号码');
        return false;
    }else if($(".telNumber").val().length!=11) {
        alertTip('手机号码不正确，请重新输入');
        return false;
    }else if(!$(".jiuPhone .radioList .radio:checked").attr("price")){
        alertTip('请选择旧机信息');
        return false;
    }else if(!$('.chuanmaNumb').val()){
        alertTip('串码不能为空');
        return false;
    }else{
      /*if($(".jiuPhone .radioList .radio").hasClass("on")){
         var price= $(".jiuPhone .radioList .radio.on").attr("price")
         var orderNo= $(".jiuPhone .radioList .radio.on").attr("orderNo")
      }*/
        var  datas={
            orderNo:$(".jiuPhone .radioList .radio:checked").attr("orderNo"),
            orderPrice:$(".jiuPhone .radioList .radio:checked").attr("price"),
            barcode:$('.chuanmaNumb').val(),
            userMobile:$(".telNumber").val(),
        }
        console.log(datas)
        $.post(linkUrl+'/registration/add.do',{params:JSON.stringify(datas)},
            function(data){
                if(data.success) {
                    alertTip(data.msg);
                }else{
                    alertTip(data.msg);
                }
            })
    }

}

/**
 * 域名的路径
 * @returns {String}
 */
function getRealPath(){
    var pathName=location.pathname;
    var allPath=location.href;
    var pos=allPath.indexOf(pathName);
    linkUrl=allPath.substring(0,pos);
    var u='zj.189.cn'; //欢GO
    if(linkUrl.indexOf(u)>0){
        linkUrl='http://m-super.com';
    }
    return linkUrl
}
/**
 * 提示
 */
function alertTip(text) {
    $('.remind_delivery_bg .remind_delivery_cont p').text(text);
    $(".remind_delivery_bg").show();
    window.setTimeout(function(){ $(".remind_delivery_bg").fadeOut();},2000);
}

//规则
var pop = document.getElementById("pop")
function showPop() {
    pop.style.display = "block"
}
function closefun() {
    pop.style.display = "none"
}