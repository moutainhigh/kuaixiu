package com.kuaixiu.activity.service;


import com.common.base.service.BaseService;
import com.kuaixiu.activity.dao.ActivityLoginMapper;
import com.kuaixiu.activity.entity.ActivityLogin;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * ActivityLogin Service
 * @CreateDate: 2019-03-08 下午02:13:48
 * @version: V 1.0
 */
@Service("activityLoginService")
public class ActivityLoginService extends BaseService<ActivityLogin> {
    private static final Logger log= Logger.getLogger(ActivityLoginService.class);

    @Autowired
    private ActivityLoginMapper<ActivityLogin> mapper;


    public ActivityLoginMapper<ActivityLogin> getDao() {
        return mapper;
    }

    //**********自定义方法***********

}