package com.system.basic.user.service;


import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.common.base.service.BaseService;
import com.system.basic.user.entity.SysRoleMenu;
import com.system.basic.user.dao.SysRoleMenuMapper;

/**
 * SysRoleMenu Service
 * @CreateDate: 2016-08-26 下午10:28:52
 * @version: V 1.0
 */
@Service("sysRoleMenuService")
public class SysRoleMenuService extends BaseService<SysRoleMenu> {
    private static final Logger log= Logger.getLogger(SysRoleMenuService.class);

    @Autowired
    private SysRoleMenuMapper<SysRoleMenu> mapper;


    public SysRoleMenuMapper<SysRoleMenu> getDao() {
        return mapper;
    }

    //**********自定义方法***********

}