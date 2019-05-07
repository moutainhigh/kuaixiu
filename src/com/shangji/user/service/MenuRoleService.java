package com.shangji.user.service;


import com.common.base.service.BaseService;
import com.shangji.user.dao.MenuRoleMapper;
import com.shangji.user.entity.MenuRole;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * MenuRole Service
 * @CreateDate: 2019-05-06 上午10:45:46
 * @version: V 1.0
 */
@Service("menuRoleService")
public class MenuRoleService extends BaseService<MenuRole> {
    private static final Logger log= Logger.getLogger(MenuRoleService.class);

    @Autowired
    private MenuRoleMapper<MenuRole> mapper;


    public MenuRoleMapper<MenuRole> getDao() {
        return mapper;
    }

    //**********自定义方法***********

}