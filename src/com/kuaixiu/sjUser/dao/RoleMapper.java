package com.kuaixiu.sjUser.dao;

import com.common.base.dao.BaseDao;

import java.util.List;

/**
 * Role Mapper
 *
 * @param <T>
 * @CreateDate: 2019-05-06 上午10:28:21
 * @version: V 1.0
 */
public interface RoleMapper<T> extends BaseDao<T> {

    List<T> queryRolesByUserId(String userId);
}


