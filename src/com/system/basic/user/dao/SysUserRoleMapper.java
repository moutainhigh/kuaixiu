package com.system.basic.user.dao;

import com.common.base.dao.BaseDao;

/**
 * SysUserRole Mapper
 *
 * @param <T>
 * @CreateDate: 2016-08-26 下午10:30:27
 * @version: V 1.0
 */
public interface SysUserRoleMapper<T> extends BaseDao<T> {

    int deleteByUserId(String userId);
}


