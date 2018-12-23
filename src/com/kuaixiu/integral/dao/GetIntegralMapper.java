package com.kuaixiu.integral.dao;

import com.common.base.dao.BaseDao;
import com.kuaixiu.integral.entity.GetIntegral;

/**
* @author: anson
* @CreateDate: 2017年5月17日 下午4:44:29
* @version: V 1.0
* 
*/
public interface GetIntegralMapper<T> extends BaseDao<T>{

	 /**
     * 根据订单号查询订单
     * @param t
     */
	 T queryByOrderNo(String orderNo);
}
