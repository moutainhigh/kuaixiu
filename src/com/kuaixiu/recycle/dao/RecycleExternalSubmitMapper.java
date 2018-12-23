package com.kuaixiu.recycle.dao;

import com.common.base.dao.BaseDao;
import com.kuaixiu.recycle.entity.RecycleExternalSubmit;

/**
 * RecycleExternalSubmit Mapper
 *
 * @param <T>
 * @CreateDate: 2018-09-29 上午09:23:19
 * @version: V 1.0
 */
public interface RecycleExternalSubmitMapper<T> extends BaseDao<T> {


    Integer updateByToken(RecycleExternalSubmit submit);
}


