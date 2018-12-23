package com.kuaixiu.brand.dao;

import java.util.List;
import java.util.Map;

import com.common.base.dao.BaseDao;

/**
 * Brand Mapper
 *
 * @param <T>
 * @CreateDate: 2016-08-25 上午03:57:21
 * @version: V 1.0
 */
public interface BrandMapper<T> extends BaseDao<T> {
	
	/**
     * 获取品牌项目
     * @return
     */
    List<Map<String, Object>> queryProjectName();
    
    /**
     * 根据名称查询品牌
     * @param name
     * @return
     * @CreateDate: 2016-9-29 下午7:55:09
     */
    List<T> queryByName(String name);
}


