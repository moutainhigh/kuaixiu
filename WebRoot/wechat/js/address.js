/** 地址选择工具 */

/**
 * 查询地址
 * level: 地址级别 1 省， 2 市， 3 区县， 4 街道
 * pid: 上级地址id
 * current: 当前选中值
 * profix: 前缀
 */

function fn_select_address(level,pid,current,profix){
	//如果是寄修模式
	var choose=eCacheUtil.storage.getCache(CacheKey.repairWay);
    var url = linkUrl+"/address/getAreaNews.do",
        params = {
            pid: pid
        };
    $.post(url, {params:JSON.stringify(params)}, function(data){
        if (data.success){
            var json = data.result.data;


            //地市级
            if(level==2){
                $("#"+profix+"City option[value!='']").remove();
                $("#"+profix+"County option[value!='']").remove();
                $("#"+profix+"Street option[value!='']").remove();
                $("#"+profix+"City").append(packageAddress(json,current));
            }
            //区县级
            if(level==3){
                $("#"+profix+"County option[value!='']").remove();
                $("#"+profix+"Street option[value!='']").remove();
                $("#"+profix+"County").append(packageAddress(json,current));
                if(choose==3){
                    //通过选择的地市信息 判断邮寄地址
                    var province = $("#s_Province").val();  //地址表中的地址编号
                    var city = $("#s_City").val();
                    findSendAddress(province,city);
                }
                return;
            }
            //乡镇级
            if(level== 4){
                $("#"+profix+"Street option[value!='']").remove();
                var select_html = packageAddress(json,current);
                if(select_html!=''){
                    $("#"+profix+"Street").show();
                    $("#"+profix+"Street").append(select_html);
                }else{
                    $("#"+profix+"Street").hide();
                    $("#"+profix+"Street").append('<option value="" selected="selected"></option>');
                }
            }

            
        }else {
            alertTip(data.resultMessage);
            return false;
        }
    });
}

// 获取地址带回调
function fn_select_address_with_callback( level, pid, current, profix, json, callbackFn  ){

    if( level == 2 ){
        current = current.replace('省','')
    }

    //如果是寄修模式
    var choose=eCacheUtil.storage.getCache(CacheKey.repairWay);
    var url = linkUrl+"/address/getAreaNews.do",
        params = {
            pid: pid
        };

    if (level == 2){
        $("#s_City option[value!='']").remove();
        $("#s_County option[value!='']").remove();
        $("#s_Street option[value!='']").remove();
        $("#s_Province").append(packageAddress(json,pid));
    }else if (level == 3){
        $("#s_County option[value!='']").remove();
        $("#s_Street option[value!='']").remove();
        $("#s_City").append(packageAddress(json,pid));
        if(choose==3){
            //通过选择的地市信息 判断邮寄地址
            var province = $("#s_Province").val();  //地址表中的地址编号
            var city = $("#s_City").val();
            findSendAddress(province,city);
        }
    }else if (level == 4){
        $("#s_Street option[value!='']").remove();
        var select_html = packageAddress(json,pid);
        if(select_html!=''){
            $("#s_Street").show();
            $("#s_County").append(select_html);
        }else{
            $("#s_Street").hide();
            $("#s_County").append('<option value="" selected="selected"></option>');
        }

        // 获取街道
        fn_select_township(4, pid);
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

// 获取街道
function fn_select_township(level, pid){
    //如果是寄修模式
    var choose=eCacheUtil.storage.getCache(CacheKey.repairWay);
    var url = linkUrl+"/address/getAreaNews.do",
        params = {
            pid: pid
        };
    $.post(url, {params:JSON.stringify(params)}, function(data){
        if (data.success){
            var json = data.result.data;
            $('#s_Street').append(packageAddress(json,pid));
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
function packageAddress(json,current){  
    var select_html = '';
    if(json.length>0){
        for( a in json)
        {
            
            if(current == json[a]['areaId']) {
                select_html +='<option value="'+json[a]['areaId']+'" selected="selected">'+json[a]['area']+'</option>';
            }
            else {
                select_html +='<option value="'+json[a]['areaId']+'">'+json[a]['area']+'</option>';
            }
        }
    }
    return select_html;
}

function findSendAddress(province,city) {
    var url =linkUrl+'/wechat/repair/sendAddress.do',
        params = {
            province:province,
            city:city
        };
    $.post(url, {params:JSON.stringify(params)}, function(data){
        if(data.success){
            var json = data.result.shop;
            $('.mailing_addr').empty();
            $('.mailing_addr').append('<p>邮寄地址：'+json.fullAddress+'</p>' +
                '<p>'+json.managerName+'收  '+json.managerMobile+'</p>');
            $('.mailing_addr').show();
        }else {
            return false;
        }


    });
}

//地址信息
function getArea(id,name){
	var area = new Object();
	area.areaId = id;
	area.area = name;
	return area;
}

var privinceData = new Array();
privinceData.push(getArea("1","北京"));
privinceData.push(getArea("2","上海"));
privinceData.push(getArea("3","天津"));
privinceData.push(getArea("4","重庆"));
privinceData.push(getArea("5","河北"));
privinceData.push(getArea("6","山西"));
privinceData.push(getArea("7","河南"));
privinceData.push(getArea("8","辽宁"));
privinceData.push(getArea("9","吉林"));
privinceData.push(getArea("10","黑龙江"));
privinceData.push(getArea("11","内蒙古"));
privinceData.push(getArea("12","江苏"));
privinceData.push(getArea("13","山东"));
privinceData.push(getArea("14","安徽"));
privinceData.push(getArea("15","浙江"));
privinceData.push(getArea("16","福建"));
privinceData.push(getArea("17","湖北"));
privinceData.push(getArea("18","湖南"));
privinceData.push(getArea("19","广东"));
privinceData.push(getArea("20","广西"));
privinceData.push(getArea("21","江西"));
privinceData.push(getArea("22","四川"));
privinceData.push(getArea("23","海南"));
privinceData.push(getArea("24","贵州"));
privinceData.push(getArea("25","云南"));
privinceData.push(getArea("26","西藏"));
privinceData.push(getArea("27","陕西"));
privinceData.push(getArea("28","甘肃"));
privinceData.push(getArea("29","青海"));
privinceData.push(getArea("30","宁夏"));
privinceData.push(getArea("31","新疆"));
privinceData.push(getArea("32","台湾"));
privinceData.push(getArea("42","香港"));
privinceData.push(getArea("43","澳门"));
privinceData.push(getArea("84","钓鱼岛"));
for (var i= 0;i<privinceData.length;i++){
    $('#s_Province').append('<option value="'+privinceData[i]['areaId']+'">'+privinceData[i]['area']+'</option>')
}
