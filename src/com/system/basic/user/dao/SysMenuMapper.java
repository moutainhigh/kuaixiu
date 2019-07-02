package com.system.basic.user.dao;

import java.util.List;

import com.common.base.dao.BaseDao;
import com.system.basic.user.entity.SysMenu;

/**
 * SysMenu Mapper
 *
 * @param <T>
 * @CreateDate: 2016-08-26 下午10:26:05
 * @version: V 1.0
 */
public interface SysMenuMapper<T> extends BaseDao<T> {

    /**
     * 根据用户id查询用户菜单
     * @param uid
     * @return
     * @CreateDate: 2016-8-27 上午1:01:20
     */
    List<SysMenu> queryMenusByUserId(String uid);

    List<SysMenu> queryMenusByUserIdType(T t);
    
    /**
     * 根据角色id查询角色菜单
     * @param uid
     * @return
     * @CreateDate: 2016-8-27 上午1:01:20
     */
    List<SysMenu> queryMenusByRoleId(String roleId);
    //查询权限
    List<T> queryMenuList(T t);
}


