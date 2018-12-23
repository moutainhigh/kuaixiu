package com.system.basic.user.service;


import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.common.base.service.BaseService;
import com.system.basic.user.entity.SysUserRole;
import com.system.basic.user.dao.SysUserRoleMapper;

/**
 * SysUserRole Service
 * @CreateDate: 2016-08-26 下午10:30:27
 * @version: V 1.0
 */
@Service("sysUserRoleService")
public class SysUserRoleService extends BaseService<SysUserRole> {
    private static final Logger log= Logger.getLogger(SysUserRoleService.class);

    @Autowired
    private SysUserRoleMapper<SysUserRole> mapper;


    public SysUserRoleMapper<SysUserRole> getDao() {
        return mapper;
    }

    //**********自定义方法***********

}