package com.kuaixiu.recycle.dao;

import java.util.List;

import com.common.base.dao.BaseDao;
import com.kuaixiu.recycle.entity.RecycleCompanyNews;

public interface RecycleCompanyMapper<T> extends BaseDao<T>{
   
	/**
	 * 根据企业id查询对应回收信息 
	 * @param companyId
	 * @return
	 */
	List<RecycleCompanyNews> queryCompanyNewsList(String companyId);
	
	/**
	 * 插入回收信息
	 * @param t
	 * @return
	 */
	int insertCompany(RecycleCompanyNews t);
}