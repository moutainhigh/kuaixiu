$(function(){
    //维修方式
    $("#way .label").click(function () {
        var target = vue.val;
        if (target == 3){
            $(".jiXiu_title").show().prev().hide();
        }else {
            $(".jiXiu_title").hide().prev().show();
            if (target == 1){

                var geolocation = new BMap.Geolocation();
                geolocation.getCurrentPosition(function(r){
                    if(this.getStatus() == BMAP_STATUS_SUCCESS){
                        eCacheUtil.storage.cache(CacheKey.lang,r.point.lng);
                        eCacheUtil.storage.cache(CacheKey.latitude,r.point.lat);
                    }
                    else {
                        alert('failed'+this.getStatus());
                    }
                },{enableHighAccuracy: true});
            }
        }
    });

});
