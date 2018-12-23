package com.kuaixiu.oldtonew.dao;

import com.common.base.dao.BaseDao;
import com.kuaixiu.oldtonew.entity.Agreed;

/**
* @author: anson
* @CreateDate: 2017年6月19日 下午4:32:10
* @version: V 1.0
* 
*/
public interface AgreedMapper<T> extends BaseDao<T> {

	/**
     * 根据订单号查询订单
     */
    T queryByOrderNo(String orderNo);

	
}
