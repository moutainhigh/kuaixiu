package com.kuaixiu.nbTelecomSJ.dao;

import com.common.base.dao.BaseDao;

/**
 * NBArea Mapper
 *
 * @param <T>
 * @CreateDate: 2019-02-22 下午07:26:25
 * @version: V 1.0
 */
public interface NBAreaMapper<T> extends BaseDao<T> {

    T queryByAreaId(String areaId);
}


