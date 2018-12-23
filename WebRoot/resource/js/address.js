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
	var choose=$('input:radio[name="repairType"]:checked').val(); 
	if(level==3&&choose==3){
		    //通过选择的地市信息 判断邮寄地址
		    var city = $("#addCity").val();  //地址表中的地址编号
		    var addCityName = $("#addCity").find("option:selected").text();
			findSendAddress(city);
	}
    var url = AppConfig.ctx + "/address/getArea.do";
    $.post(url, {pid: pid}, function(result){
    	if(!result.success){
    		return false;
    	}
    	var json = result.data;
        //地市级   
        if(level==2){
            $("#"+profix+"City option[value!='']").remove();
            $("#"+profix+"County option[value!='']").remove();
            $("#"+profix+"County").hide();
            if($("#"+profix+"Street").length > 0){
            	$("#"+profix+"Street option[value!='']").remove();
            	$("#"+profix+"Street").hide();
            }
            $("#"+profix+"City").append(packageAddress(json,current));
            $("#"+profix+"City").show();
        }
        //区县级
        if(level==3)
        {
            $("#"+profix+"County option[value!='']").remove();
            if($("#"+profix+"Street").length > 0){
            	$("#"+profix+"Street option[value!='']").remove();
            	$("#"+profix+"Street").hide();
            }
            $("#"+profix+"County").append(packageAddress(json,current));
            $("#"+profix+"County").show();
        }
        //乡镇级
        if(level== 4)
        {
            $("#"+profix+"Street option[value!='']").remove();
            var select_html = packageAddress(json,current);
            if(select_html!=''){
                $("#"+profix+"Street").show();         
                $("#"+profix+"Street").append(select_html);
            }else{
                $("#"+profix+"Street").hide(); 
                $("#"+profix+"Street").append('<option value="0" selected="selected"></option>');              
            }

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
            if(current == json[a]['areaId'])
                select_html +='<option value="'+json[a]['areaId']+'" selected="selected">'+json[a]['area']+'</option>';
            else
                select_html +='<option value="'+json[a]['areaId']+'">'+json[a]['area']+'</option>';
        }
    }
    return select_html;
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
