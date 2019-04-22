package com.kuaixiu.activity.service;


import com.common.base.service.BaseService;
import com.kuaixiu.activity.dao.ActivityModelMapper;
import com.kuaixiu.activity.entity.ActivityModel;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * ActivityModel Service
 * @CreateDate: 2019-03-08 下午12:38:37
 * @version: V 1.0
 */
@Service("activityModelService")
public class ActivityModelService extends BaseService<ActivityModel> {
    private static final Logger log= Logger.getLogger(ActivityModelService.class);

    @Autowired
    private ActivityModelMapper<ActivityModel> mapper;


    public ActivityModelMapper<ActivityModel> getDao() {
        return mapper;
    }

    //**********自定义方法***********

}