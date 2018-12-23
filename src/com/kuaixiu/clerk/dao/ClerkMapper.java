package com.kuaixiu.clerk.dao;

import com.common.base.dao.BaseDao;

/**
* @author: anson
* @CreateDate: 2017年5月17日 下午3:46:36
* @version: V 1.0
* 
*/
public interface ClerkMapper<T> extends BaseDao<T> {

	/**
	 * 通过手机号查询店员信息
	 */
	T queryByTel(Object tel);
	
	/**
	 * 店员完成订单增加积分
	 */
	void addIntegralById(T t);
}
