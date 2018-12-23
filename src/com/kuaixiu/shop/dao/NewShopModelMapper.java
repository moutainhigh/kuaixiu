package com.kuaixiu.shop.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.common.base.dao.BaseDao;
import com.kuaixiu.order.entity.Order;
import com.kuaixiu.shop.entity.Shop;

/**
* @author: anson
* @CreateDate: 2017年6月15日 上午11:30:32
* @version: V 1.0
* 
*/
public interface NewShopModelMapper<T> extends BaseDao<T>{

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
