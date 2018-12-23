package com.kuaixiu.brand.dao;

import java.util.List;
import java.util.Map;

import com.common.base.dao.BaseDao;

/**
* @author: anson
* @CreateDate: 2017年6月13日 下午4:17:13
* @version: V 1.0
* 
*/
public interface NewBrandMapper<T> extends BaseDao<T> {

	/**
     * 获取品牌项目
     */
    List<Map<String, Object>> queryProjectName();
    
    /**
     * 根据名称查询品牌
     */
    List<T> queryByName(String name);
	
}
