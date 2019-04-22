package com.kuaixiu.nbTelecomSJ.dao;

import com.common.base.dao.BaseDao;

/**
 * NBCounty Mapper
 *
 * @param <T>
 * @CreateDate: 2019-02-22 下午06:31:52
 * @version: V 1.0
 */
public interface NBCountyMapper<T> extends BaseDao<T> {

    T queryByName(String countyName);
}


