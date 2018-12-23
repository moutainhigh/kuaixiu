$(function(){
    var checkSubmitFlg = false;
    $('#submit').click(function(){
        var param = {
            name:$('#name').val(),
            mobile:$('#tel').val(),
            province:$('[data-name="s_Province"]').attr('data-id'),
            city:$('[data-name="s_City"]').attr('data-id'),
            area:$('[data-name="s_Area"]').attr('data-id'),
            street:$('#address').val()
        };
        if (isEmpty(param.name) || isEmpty(param.mobile) || isEmpty(param.province) || isEmpty(param.city) || isEmpty(param.area) || isEmpty(param.street)){
            alertTip('请将信息填写完整');
            return false;
        }else if(checkSubmitFlg == true){
            return false;
        }
        checkSubmitFlg = true;
        $.post(linkUrl+"/join/add.do",{params:JSON.stringify(param)},function(data){
            if (data.success){
                alertTip('提交成功！');
                $('input[type="text"]').val('');
            }else{
                alertTip(data.resultMessage);
            }
        });
    });
     $('.addressList').bind('touchmove', function(event) {
         event.stopPropagation();
     });

    $('.selMash').click(function(e){
        var con = $(".addressList");
        if(!con.is(e.target) && con.has(e.target).length === 0){
            $('.main,.address li').removeClass('active');
            $('.selMash,.addressList').hide();
            $('.addressList').unbind('touchmove');
        }
    });
    $('.addressList').delegate('p','click',function(){
        var did = $(this).attr('data-value');
        $('.address li.active').attr('data-id',did).html($(this).html());
        if ($('.address li.active').attr('data-level') !== '3'){
            $('.address li.active').next('li').addClass('active').siblings('li').removeClass('active');
            var current = $('.address li.active'),
                level = $(current).attr('data-level');
            $(current).attr('data-id','');
            $(current).next('li').attr('data-id','');
            fn_select_address(level,current,1);
        }else{
            $('.main,.address li.active').removeClass('active');
            $('.selMash,.addressList').hide();
        }
    });
});
function fn_select_address(level,current,init){
    $('.addressList').empty().html('<img src="img/loadings.gif">');
    var url = linkUrl+"/address/getAreaNews.do",
        params = {
            pid: $(current).prev('li').attr('data-id')
        };
    $.post(url, {params:JSON.stringify(params)}, function(data){
        if (data.success){
            var json = data.result.data;
            packageAddress(level,json,current);
            if (init == 1){
                $(current).html(json[0].area).attr('data-id',json[0].areaId);
                
            }
        }else {
            alertTip(data.resultMessage);
            $('.selMash,.addressList').hide();
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
    $('.main').addClass('active');
    if (level == 1){
        json = privinceData;
    }
    var select_html = '';
    for( a in json)
    {
        select_html +='<p data-value="'+json[a]['areaId']+'">'+json[a]['area']+'</p>';
    }
    $('.addressList').html(select_html);
    $(current).addClass('active').siblings('li').removeClass('active');
    $('.selMash,.addressList').show();
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
        "area":"河北",
        "areaId":"5"
    },
    {
        "area":"山西",
        "areaId":"6"
    },
    {
        "area":"河南",
        "areaId":"7"
    },
    {
        "area":"辽宁",
        "areaId":"8"
    },
    {
        "area":"吉林",
        "areaId":"9"
    },
    {
        "area":"黑龙江",
        "areaId":"10"
    },
    {
        "area":"内蒙古",
        "areaId":"11"
    },
    {
        "area":"江苏",
        "areaId":"12"
    },
    {
        "area":"山东",
        "areaId":"13"
    },
    {
        "area":"安徽",
        "areaId":"14"
    },
    {
        "area":"浙江",
        "areaId":"15"
    },
    {
        "area":"福建",
        "areaId":"16"
    },
    {
        "area":"湖北",
        "areaId":"17"
    },
    {
        "area":"湖南",
        "areaId":"18"
    },
    {
        "area":"广东",
        "areaId":"19"
    },
    {
        "area":"广西",
        "areaId":"20"
    },
    {
        "area":"江西",
        "areaId":"21"
    },
    {
        "area":"四川",
        "areaId":"22"
    },
    {
        "area":"海南",
        "areaId":"23"
    },
    {
        "area":"贵州",
        "areaId":"24"
    },
    {
        "area":"云南",
        "areaId":"25"
    },
    {
        "area":"西藏",
        "areaId":"26"
    },
    {
        "area":"陕西",
        "areaId":"27"
    },
    {
        "area":"甘肃",
        "areaId":"28"
    },
    {
        "area":"青海",
        "areaId":"29"
    },
    {
        "area":"宁夏",
        "areaId":"30"
    },
    {
        "area":"新疆",
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