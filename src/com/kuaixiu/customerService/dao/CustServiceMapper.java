package com.kuaixiu.customerService.dao;

import com.common.base.dao.BaseDao;

/**
 * CustService Mapper
 *
 * @param <T>
 * @CreateDate: 2016-12-18 下午10:13:47
 * @version: V 1.0
 */
public interface CustServiceMapper<T> extends BaseDao<T> {
	
	T queryByNumber(String number);
	//根据工号查询客服信息
	T queryByCustNumber(String number);
}


