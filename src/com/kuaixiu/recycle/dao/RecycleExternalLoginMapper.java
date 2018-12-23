package com.kuaixiu.recycle.dao;

import com.common.base.dao.BaseDao;

/**
 * RecycleExternalLogin Mapper
 *
 * @param <T>
 * @CreateDate: 2018-09-29 上午09:09:53
 * @version: V 1.0
 */
public interface RecycleExternalLoginMapper<T> extends BaseDao<T> {

    T queryByToken(String token);
}


