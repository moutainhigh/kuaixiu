package com.kuaixiu.recycleUser.service;


import com.common.base.service.BaseService;
import com.kuaixiu.recycleUser.dao.HsUserMapper;
import com.kuaixiu.recycleUser.entity.HsUser;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * HsUser Service
 * @CreateDate: 2019-05-30 上午10:24:26
 * @version: V 1.0
 */
@Service("hsUserService")
public class HsUserService extends BaseService<HsUser> {
    private static final Logger log= Logger.getLogger(HsUserService.class);

    @Autowired
    private HsUserMapper<HsUser> mapper;


    public HsUserMapper<HsUser> getDao() {
        return mapper;
    }

    //**********自定义方法***********

}