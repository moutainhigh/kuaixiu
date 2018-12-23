package com.kuaixiu.recycle.dao;

import java.util.List;

import com.common.base.dao.BaseDao;

/**
 * FromSystem Mapper
 *
 * @param <T>
 * @CreateDate: 2017-03-11 下午10:40:00
 * @version: V 1.0
 */
public interface RecycleSystemMapper<T> extends BaseDao<T> {
	
	/**
     * 根据名称查询项目
     * @param name
     * @return
     * @CreateDate: 2016-9-29 下午7:55:09
     */
    List<T> queryByName(String name);
}


