/** 地址选择工具 */
var zero = 0;
$('#selectAddress,#selectAddressBtn').click(function(){
    if (zero == 0){
        zero = 1;
        $(".selectAddr").append(packageAddress('2',privinceData,'','Province'));
    }
    $('#addrLayer').show();
    $('#addrLayer .layerCont').animate({
        top:"41.229%"
    },300);
})
$('.selectAddr').delegate('li','click',function(){
    $(this).addClass('active').siblings('li').removeClass('active');
    $('#selected span').eq(3).addClass('active').siblings().removeClass('active');
    var level = $(this).parent('ul').attr('data-level'),
        pid = $(this).attr('data-value'),
        current = $(this).html();
    fn_select_address(level,pid,current,'');
});
$('#selected').delegate('span','click',function(){
    var name = $(this).attr('id').substring(2);
    $(this).addClass('active').siblings().removeClass('active');
    $('#'+name).addClass('selectList').siblings('ul').removeClass('selectList');
});
/**
 * 查询地址
 * level: 地址级别 1 省， 2 市， 3 区县， 4 街道
 * pid: 上级地址id
 * current: 当前选中值
 * profix: 前缀
 */
function fn_select_address(level,pid,current,profix){
    var url = linkUrl+"/address/getAreaNews.do",
        params = {
            pid: pid
        };
    if (level == 2){
        $('#s_Province').addClass('show').html(current).attr('data-id',pid);
        $('#s_City,#s_County').removeClass('show').attr('data-id','').empty();
        profix = 'City';
    }else if (level == 3){
        $('#s_City').addClass('show').html(current).attr('data-id',pid);
        $('#s_County').removeClass('show').attr('data-id','').empty();
        profix = 'County';
    }else if (level == 4){
        $('#s_County').addClass('show').html(current).attr('data-id',pid);
        profix = '';
        $('#selectAddress').html('<p>'+$('#s_Province').html()+" "+$('#s_City').html()+" "+$('#s_County').html()+'</p>');
        $('#addrLayer').hide();
        $('#selectAddress').addClass('active');
        return;
    }
    $.post(url, {params:JSON.stringify(params)}, function(data){
        if (data.success){
            var json = data.result.data;
            $(".selectAddr").append(packageAddress((level - 0 +1),json,current,profix));
            $('[data-level = '+level+']').removeClass('selectList');
        }else {
            alertTip(data.resultMessage);
            return false;
        }
    });
}

/**
 * 封装地址
 * @param json
 * @param current
 * @returns {String}
 */
function packageAddress(level,json,current,profix){
    $('#'+profix).remove();
    var select_html = '<ul id="'+profix+'" class="selectList" data-level="'+level+'">';
    for( a in json)
    {
        select_html +='<li data-value="'+json[a]['areaId']+'">'+json[a]['area']+'</li>';
    }
    select_html += '</ul>';
    return select_html;
}

//地址信息
var privinceData = [
    {
        "area":"北京",
        "areaId":"1"
    },
    {
        "area":"上海",
        "areaId":"2"
    },
    {
        "area":"天津",
        "areaId":"3"
    },
    {
        "area":"重庆",
        "areaId":"4"
    },
    {
        "area":"河北省",
        "areaId":"5"
    },
    {
        "area":"山西省",
        "areaId":"6"
    },
    {
        "area":"河南省",
        "areaId":"7"
    },
    {
        "area":"辽宁省",
        "areaId":"8"
    },
    {
        "area":"吉林省",
        "areaId":"9"
    },
    {
        "area":"黑龙江省",
        "areaId":"10"
    },
    {
        "area":"内蒙古省",
        "areaId":"11"
    },
    {
        "area":"江苏省",
        "areaId":"12"
    },
    {
        "area":"山东省",
        "areaId":"13"
    },
    {
        "area":"安徽省",
        "areaId":"14"
    },
    {
        "area":"浙江省",
        "areaId":"15"
    },
    {
        "area":"福建省",
        "areaId":"16"
    },
    {
        "area":"湖北省",
        "areaId":"17"
    },
    {
        "area":"湖南省",
        "areaId":"18"
    },
    {
        "area":"广东省",
        "areaId":"19"
    },
    {
        "area":"广西省",
        "areaId":"20"
    },
    {
        "area":"江西省",
        "areaId":"21"
    },
    {
        "area":"四川省",
        "areaId":"22"
    },
    {
        "area":"海南省",
        "areaId":"23"
    },
    {
        "area":"贵州省",
        "areaId":"24"
    },
    {
        "area":"云南省",
        "areaId":"25"
    },
    {
        "area":"西藏省",
        "areaId":"26"
    },
    {
        "area":"陕西省",
        "areaId":"27"
    },
    {
        "area":"甘肃省",
        "areaId":"28"
    },
    {
        "area":"青海省",
        "areaId":"29"
    },
    {
        "area":"宁夏省",
        "areaId":"30"
    },
    {
        "area":"新疆省",
        "areaId":"31"
    },
    {
        "area":"台湾",
        "areaId":"32"
    },
    {
        "area":"香港",
        "areaId":"42"
    },
    {
        "area":"澳门",
        "areaId":"43"
    },
    {
        "area":"钓鱼岛",
        "areaId":"84"
    }
]

/**
 * 查询地址
 * level: 地址级别 1 省， 2 市， 3 区县， 4 街道
 * pid: 上级地址id
 * current: 当前选中值
 * profix: 前缀
 * callbackFn: 数据请求成功后的回调函数
 */
function fn_select_address_with_callback(level,pid,current,profix, callbackFn ){
    var url = linkUrl+"/address/getAreaNews.do",
        params = {
            pid: pid
        };
    if (level == 2){
        $('#s_Province').addClass('show').html(current).attr('data-id',pid);
        $('#s_City,#s_County').removeClass('show').attr('data-id','').empty();
        profix = 'City';
    }else if (level == 3){
        $('#s_City').addClass('show').html(current).attr('data-id',pid);
        $('#s_County').removeClass('show').attr('data-id','').empty();
        profix = 'County';
    }else if (level == 4){
        $('#s_County').addClass('show').html(current).attr('data-id',pid);
        profix = '';
        $('#selectAddress').html('<p>'+$('#s_Province').html()+" "+$('#s_City').html()+" "+$('#s_County').html()+'</p>');
        $('#addrLayer').hide();
        $('#selectAddress').addClass('active');
        return;
    }
    $.post(url, {params:JSON.stringify(params)}, function(data){
        if (data.success){
            var json = data.result.data;
            $(".selectAddr").append(packageAddress((level - 0 +1),json,current,profix));
            $('[data-level = '+level+']').removeClass('selectList');

            if( typeof callbackFn == 'function' ){
                callbackFn( level - 0 +1 , json );
            }
        }else {
            alertTip(data.resultMessage);
            return false;
        }
    });
}
