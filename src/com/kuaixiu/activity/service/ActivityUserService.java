package com.kuaixiu.activity.service;


import com.common.base.service.BaseService;
import com.kuaixiu.activity.dao.ActivityUserMapper;
import com.kuaixiu.activity.entity.ActivityUser;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * ActivityUser Service
 * @CreateDate: 2018-12-25 上午10:12:03
 * @version: V 1.0
 */
@Service("activityUserService")
public class ActivityUserService extends BaseService<ActivityUser> {
    private static final Logger log= Logger.getLogger(ActivityUserService.class);

    @Autowired
    private ActivityUserMapper<ActivityUser> mapper;


    public ActivityUserMapper<ActivityUser> getDao() {
        return mapper;
    }

    //**********自定义方法***********

}