package com.kuaixiu.version.dao;

import com.common.base.dao.BaseDao;

/**
 * Version Mapper
 *
 * @param <T>
 * @CreateDate: 2017-05-02 下午09:55:22
 * @version: V 1.0
 */
public interface VersionMapper<T> extends BaseDao<T> {
	
	/**
     * 根据名称查询最新版本
     * @param name
     * @return
     */
    T queryNewVersion(String name);

}


