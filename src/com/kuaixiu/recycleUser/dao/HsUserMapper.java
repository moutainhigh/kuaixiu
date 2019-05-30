package com.kuaixiu.recycleUser.dao;

import com.common.base.dao.BaseDao;

/**
 * HsUser Mapper
 *
 * @param <T>
 * @CreateDate: 2019-05-30 上午10:24:26
 * @version: V 1.0
 */
public interface HsUserMapper<T> extends BaseDao<T> {

    T queryByPhone(String phone);
}


