package com.kuaixiu.activity.dao;

import com.common.base.dao.BaseDao;

/**
 * ActivityProject Mapper
 *
 * @param <T>
 * @CreateDate: 2018-12-28 下午05:06:44
 * @version: V 1.0
 */
public interface ActivityProjectMapper<T> extends BaseDao<T> {

    T queryByProjectNo(T t);
}


