package com.kuaixiu.apiService;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.common.exception.ApiServiceException;
import com.kuaixiu.engineer.entity.Engineer;
import com.kuaixiu.engineer.service.EngineerService;
import com.kuaixiu.shop.entity.Shop;
import com.kuaixiu.shop.service.ShopService;
import com.system.api.ApiServiceInf;
import com.system.constant.ApiResultConstant;
import org.apache.commons.collections.MapUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;


/**
 * 工程师操作接口实现类
 * @author wugl
 *
 */
@Service("enginnerApiService")
public class EnginnerApiService implements ApiServiceInf {
    private static final Logger log= Logger.getLogger(EnginnerApiService.class);

    @Autowired
    private ShopService shopService;
    @Autowired
    private EngineerService engService;
    
    @Override
    public Object process(Map<String, String> params) {
        System.out.println("查询工程师");
        Engineer eng = engService.queryByEngineerNumber(MapUtils.getString(params, "pmClientId"));
        
        if(eng == null){
            throw new ApiServiceException(ApiResultConstant.resultCode_1003, "工程师信息未找到");
        }
        JSONObject json = new JSONObject();
        JSONArray jsonNames=new JSONArray();

        //如果是多个门店，就便利查找名称
        if(eng.getShopCode().contains(",")){
            List<String> shopCodeList= Arrays.asList(eng.getShopCode().split(","));

            for(String shopCodeList1:shopCodeList){
                JSONObject shopName=new JSONObject();
                Shop shop=shopService.queryByCode(shopCodeList1);
                shopName.put("shopName",shop.getName());
                jsonNames.add(shopName);
            }
        }else {
            JSONObject shopName=new JSONObject();
            Shop shop=shopService.queryByCode(eng.getShopCode());
            shopName.put("shopName",shop.getName());
            jsonNames.add(shopName);
        }
        json.put("id", eng.getId());
        json.put("provider_name", eng.getProviderName());
        json.put("shop_name", jsonNames);
        json.put("number", eng.getNumber());
        json.put("name", eng.getName());
        json.put("gender", eng.getGender());
        json.put("mobile", eng.getMobile());
        json.put("idcard", eng.getIdcard());
        json.put("is_dispatch", eng.getIsDispatch());
        return json;
    }

}
