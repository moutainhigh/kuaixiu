package com.kuaixiu.shop.service;


import com.common.base.service.BaseService;
import com.kuaixiu.shop.dao.ShopModelMapper;
import com.kuaixiu.shop.entity.ShopModel;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * ShopModel Service
 * @CreateDate: 2017-02-12 上午12:32:01
 * @version: V 1.0
 */
@Service("shopModelService")
public class ShopModelService extends BaseService<ShopModel> {
    private static final Logger log= Logger.getLogger(ShopModelService.class);

    @Autowired
    private ShopModelMapper<ShopModel> mapper;


    public ShopModelMapper<ShopModel> getDao() {
        return mapper;
    }

    //**********自定义方法***********
    
    /**
     * 批量添加品牌
     * @param params
     * @return
     */
    public int addBatch(Map<String, Object> params){
    	return getDao().addBatch(params);
    }

    /**
     * 根据商店code删除品牌
     * @param shopCode
     * @return
     */
    public int deleteByShopCode(String shopCode){
    	return getDao().deleteByShopCode(shopCode);
    }
    
    
    
}