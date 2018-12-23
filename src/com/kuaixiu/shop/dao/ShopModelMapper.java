package com.kuaixiu.shop.dao;

import java.util.Map;

import com.common.base.dao.BaseDao;

/**
 * ShopModel Mapper
 *
 * @param <T>
 * @CreateDate: 2017-02-12 上午12:32:01
 * @version: V 1.0
 */
public interface ShopModelMapper<T> extends BaseDao<T> {

	/**
     * 批量添加品牌
     * @param params
     * @return
     */
    int addBatch(Map<String, Object> params);
    
    /**
     * 根据商店code删除品牌
     * @param shopCode
     * @return
     */
    int deleteByShopCode(String shopCode);
}


