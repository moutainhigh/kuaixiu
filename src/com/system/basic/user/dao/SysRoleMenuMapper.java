package com.system.basic.user.dao;

import com.common.base.dao.BaseDao;
import com.system.basic.user.entity.SysMenu;

/**
 * SysRoleMenu Mapper
 *
 * @param <T>
 * @CreateDate: 2016-08-26 下午10:28:52
 * @version: V 1.0
 */
public interface SysRoleMenuMapper<T> extends BaseDao<T> {


    Integer deleteBYCode(SysMenu sysMenu);
}


