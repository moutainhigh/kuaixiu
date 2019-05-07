package com.shangji.user.dao;

import com.common.base.dao.BaseDao;

/**
 * User Mapper
 *
 * @param <T>
 * @CreateDate: 2019-05-06 上午10:19:30
 * @version: V 1.0
 */
public interface UserMapper<T> extends BaseDao<T> {

    T queryByLoginId(String loginId);
}


