/***************************************
由于Chrome、IOS10等已不再支持非安全域的浏览器定位请求，为保证定位成功率和精度，请尽快升级您的站点到HTTPS。
***************************************/

var map, geolocation;
var positionData = {};

//获取定位地址
function getAddr(){

    //加载地图，调用浏览器定位服务
    map = new AMap.Map('container', {
        resizeEnable: true
    });

    map.plugin('AMap.Geolocation', function() {
        geolocation = new AMap.Geolocation({
            enableHighAccuracy: true,//是否使用高精度定位，默认:true
            timeout: 10000,          //超过10秒后停止定位，默认：无穷大
            buttonOffset: new AMap.Pixel(10, 20),//定位按钮与设置的停靠位置的偏移量，默认：Pixel(10, 20)
            zoomToAccuracy: true,      //定位成功后调整地图视野范围使定位位置及精度范围视野内可见，默认：false
            buttonPosition:'RB'
        });
        map.addControl(geolocation);
        geolocation.getCurrentPosition();
        AMap.event.addListener(geolocation, 'complete', onComplete);//返回定位信息
        AMap.event.addListener(geolocation, 'error', onError);      //返回定位出错信息
    });
}

//解析定位结果
function onComplete(data) {
    zero = 1;            //定义在address.js中的标记变量，确认是否已经完成 省份 级别的初始化
    $(".selectAddr").append(packageAddress('2',privinceData,'','Province'));

    if ( data.addressComponent ) {         
        positionData = {
            '2': data.addressComponent.province,
            '3': data.addressComponent.city,
            '4': data.addressComponent.district
        }  
    }
    else{
        return ;
    }

    setPositionResult( positionData ); 

    var initLevel = 2;
    var areaName = positionData[initLevel];
    var areaId = getAreaIdByName( privinceData, areaName );
    
    if( areaId != '' ){
        fn_select_address_with_callback( initLevel , areaId , areaName , '' , function( level ,  areaList ){
            if( level == 3 || level == 4 ){
                var cName = positionData[level] ;
                var cId = getAreaIdByName( areaList , cName );

                if( cId != '' ){
                    fn_select_address_with_callback( level , cId , cName , '' , function( nextLevel , nextAreaList ){
                        var nextName = positionData[nextLevel] ;
                        var nextId = getAreaIdByName( nextAreaList , nextName ); 

                        fn_select_address_with_callback( nextLevel , nextId , nextName , '' );
                    });
                }
                else{
                    positionData['4'] = '' ;
                }

                //设置定位结果
                setPositionResult( positionData );
            }
        });
    }
    else{
        //省份名称不匹配,清除城市\区县的定位结果
        positionData['3'] = '' ;
        positionData['4'] = '' ;

         //设置定位结果
        setPositionResult( positionData );  
    }

}

//解析定位错误信息
function onError(data) {
    // alert('定位JS失败');
}

//获取身份的区域ID
function getAreaIdByName(areaList, areaName){

    var result = '';

    for(var areaItem of areaList ){
        if( areaItem.area == areaName ){
            result = areaItem.areaId
        }
    }

    return result;

}

//设置定位结果
function setPositionResult( positionData ){
    var positionResult = positionData['2'] + ' ' + positionData['3'] + ' ' + positionData['4'] ;
    $('.default-addr').text( positionResult );
}