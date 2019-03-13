package com.kuaixiu.recycle.dao;

import com.common.base.dao.BaseDao;

import java.util.List;
import java.util.Map;

public interface RecycleCheckItemsMapper<T> extends BaseDao<T>{
   
	/**
	 * 
	
	 * Description:通过微信openId修改 
	
	 * @param t
	 * @return
	 */
	int updateByWechatId(T t);

	List<Map> queryTestListForPage(T t);
}