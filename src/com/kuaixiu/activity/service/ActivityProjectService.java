package com.kuaixiu.activity.service;


import com.common.base.service.BaseService;
import com.kuaixiu.activity.dao.ActivityProjectMapper;
import com.kuaixiu.activity.entity.ActivityProject;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * ActivityProject Service
 * @CreateDate: 2018-12-28 下午05:06:44
 * @version: V 1.0
 */
@Service("activityProjectService")
public class ActivityProjectService extends BaseService<ActivityProject> {
    private static final Logger log= Logger.getLogger(ActivityProjectService.class);

    @Autowired
    private ActivityProjectMapper<ActivityProject> mapper;


    public ActivityProjectMapper<ActivityProject> getDao() {
        return mapper;
    }

    //**********自定义方法***********

}