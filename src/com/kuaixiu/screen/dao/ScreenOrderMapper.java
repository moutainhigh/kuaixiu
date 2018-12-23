package com.kuaixiu.screen.dao;

import com.common.base.dao.BaseDao;

/**
* @author: anson
* @CreateDate: 2017年10月17日 下午4:28:09
* @version: V 1.0
* 
*/
public interface ScreenOrderMapper<T> extends BaseDao<T> {

	
	/**
     * 根据订单号查询订单
     * @param t
     */
    T queryByOrderNo(String orderNo);
}
