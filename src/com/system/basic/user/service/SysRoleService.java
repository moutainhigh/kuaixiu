package com.system.basic.user.service;


import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.common.base.service.BaseService;
import com.system.basic.user.entity.SysRole;
import com.system.basic.user.dao.SysRoleMapper;

/**
 * SysRole Service
 * @CreateDate: 2016-08-26 下午10:26:44
 * @version: V 1.0
 */
@Service("sysRoleService")
public class SysRoleService extends BaseService<SysRole> {
    private static final Logger log= Logger.getLogger(SysRoleService.class);

    @Autowired
    private SysRoleMapper<SysRole> mapper;


    public SysRoleMapper<SysRole> getDao() {
        return mapper;
    }

    //**********自定义方法***********

    /**
     * 根据用户id查询用户角色
     * @param uid
     * @return
     * @CreateDate: 2016-8-27 上午12:52:54
     */
    public List<SysRole> queryRolesByUserId(String uid){
        return getDao().queryRolesByUserId(uid);
    }
    public List<SysRole> queryRoles1ByUserId(String uid){
        return getDao().queryRoles1ByUserId(uid);
    }
}