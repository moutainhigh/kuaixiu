package com.kuaixiu.join.dao;

import com.common.base.dao.BaseDao;

public interface JoinNewsMapper<T> extends BaseDao<T>{
    
	/**
	 * 根据手机号查询
	 * @param t
	 * @return
	 */
	T queryByMobile(String mobile);
}