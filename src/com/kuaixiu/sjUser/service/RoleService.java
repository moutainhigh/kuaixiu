package com.kuaixiu.sjUser.service;


import com.common.base.service.BaseService;
import com.kuaixiu.sjUser.dao.RoleMapper;
import com.kuaixiu.sjUser.entity.Role;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Role Service
 * @CreateDate: 2019-05-06 上午10:28:21
 * @version: V 1.0
 */
@Service("roleService")
public class RoleService extends BaseService<Role> {
    private static final Logger log= Logger.getLogger(RoleService.class);

    @Autowired
    private RoleMapper<Role> mapper;


    public RoleMapper<Role> getDao() {
        return mapper;
    }

    //**********自定义方法***********
    /**
     * 根据用户id查询用户角色
     * @param uid
     * @return
     * @CreateDate: 2016-8-27 上午12:52:54
     */
    public List<Role> queryRolesByUserId(String uid){
        return getDao().queryRolesByUserId(uid);
    }
}