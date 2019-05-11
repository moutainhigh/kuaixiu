package com.kuaixiu.sjUser.service;


import com.common.base.service.BaseService;
import com.kuaixiu.sjUser.dao.UserRoleMapper;
import com.kuaixiu.sjUser.entity.UserRole;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * UserRole Service
 * @CreateDate: 2019-05-06 上午10:37:53
 * @version: V 1.0
 */
@Service("userRoleService")
public class UserRoleService extends BaseService<UserRole> {
    private static final Logger log= Logger.getLogger(UserRoleService.class);

    @Autowired
    private UserRoleMapper<UserRole> mapper;


    public UserRoleMapper<UserRole> getDao() {
        return mapper;
    }

    //**********自定义方法***********

}