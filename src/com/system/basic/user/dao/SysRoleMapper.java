package com.system.basic.user.dao;

import java.util.List;

import com.common.base.dao.BaseDao;
import com.system.basic.user.entity.SysRole;

/**
 * SysRole Mapper
 *
 * @param <T>
 * @CreateDate: 2016-08-26 下午10:26:44
 * @version: V 1.0
 */
public interface SysRoleMapper<T> extends BaseDao<T> {
    
    /**
     * 根据用户id查询用户角色
     * @param uid
     * @return
     * @CreateDate: 2016-8-27 上午12:52:54
     */
    List<SysRole> queryRolesByUserId(String uid);

    SysRole queryRolesByRoleName(String roleName);
}


