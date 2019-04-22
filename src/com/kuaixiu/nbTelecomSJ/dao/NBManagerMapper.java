package com.kuaixiu.nbTelecomSJ.dao;

import com.common.base.dao.BaseDao;

/**
 * NBManager Mapper
 *
 * @param <T>
 * @CreateDate: 2019-02-22 下午06:34:26
 * @version: V 1.0
 */
public interface NBManagerMapper<T> extends BaseDao<T> {

    int deleteByManagerId(T t);
}


