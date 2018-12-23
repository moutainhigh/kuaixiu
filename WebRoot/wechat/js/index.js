$(function () {
    sessionStorage.clear();
    var source = GetQueryString('fm'),repairType = 0;
    if (!isEmpty(source)){
        eCacheUtil.storage.cache(CacheKey.fm,source);
    }
    var type = eCacheUtil.storage.getCache(CacheKey.repairWay);
    if (!isEmpty(type)){
        $('[data-type ='+ type +']').addClass('active').siblings().removeClass('active');
        repairType = type;
    }
    //维修方式
    $('.typeClass').click(function(){
        $(this).addClass('active').siblings().removeClass('active');
        return repairType = $(this).attr("data-type");
    });

    $('#nextStep').click(function () {
        eCacheUtil.storage.cache(CacheKey.repairWay,repairType);
        window.location.href = "sel_brand.html";
    });

    var linkInfo = [
            {repairType:0,url:'sel_model.html',brandId:'1',projectId:'50a8d609-ecf9-11e6-93f3-00163e04c890'},
            {repairType:0,url:'sel_other.html',brandId:'1',modelId:'29ce0489-533c-4689-a3cd-411e67716cd4',projectId:"fe750fc5-70ff-11e6-80cd-10c37b579295"},
            {repairType:0,url:'sel_other.html',brandId:'ab5d307a-e335-11e6-a80a-00163e04c890',modelId:'fb914ac4-2c12-47a7-8315-3ec4e26fdf7d',projectId:"89971c83-d95c-11e6-a112-00163e04c890"},
            {repairType:0,url:'sel_other.html',brandId:'1',modelId:'6d3ed3b8-0a7e-463a-95f3-439899afd72a',projectId:"fe750fc5-70ff-11e6-80cd-10c37b579295"},
            {repairType:0,url:'sel_other.html',brandId:'1',modelId:'b2e12d18-9ce6-4d44-a282-a00e95b8cf3b',projectId:"fe750fc5-70ff-11e6-80cd-10c37b579295"}
        ];
    $('.purchase a').click(function () {
        var i = $(this).attr('data-value'),
            selFaultId = new Array();
        eCacheUtil.storage.cache(CacheKey.repairWay,linkInfo[i].repairType);
        eCacheUtil.storage.cache(CacheKey.phoneBrandId,linkInfo[i].brandId);

        selFaultId.push(linkInfo[i].projectId);
        if (linkInfo[i].hasOwnProperty('modelId')){
            eCacheUtil.storage.cache(CacheKey.phoneModelId,linkInfo[i].modelId);
            eCacheUtil.storage.cache(CacheKey.phoneModelName,$(this).find('.title').html());
            eCacheUtil.storage.cache(CacheKey.faultId,selFaultId);
        }else {
            eCacheUtil.storage.cache(CacheKey.tempFaultId,selFaultId);
        }
        window.location.href = linkInfo[i].url;
    });

});
