package com.kuaixiu.oldtonew.dao;

import com.common.base.dao.BaseDao;

/**
* @author: anson
* @CreateDate: 2017年6月19日 下午5:13:44
* @version: V 1.0
* 
*/
public interface NewOrderPayMapper<T> extends BaseDao<T> {

	/**
     * 根据订单号查询
     */
    T queryByOrderNo(String orderNo);
}
