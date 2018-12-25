package com.kuaixiu.activity.service;


import com.common.base.service.BaseService;
import com.kuaixiu.activity.dao.ActivityCompanyMapper;
import com.kuaixiu.activity.entity.ActivityCompany;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * ActivityCompany Service
 * @CreateDate: 2018-12-25 上午10:13:01
 * @version: V 1.0
 */
@Service("activityCompanyService")
public class ActivityCompanyService extends BaseService<ActivityCompany> {
    private static final Logger log= Logger.getLogger(ActivityCompanyService.class);

    @Autowired
    private ActivityCompanyMapper<ActivityCompany> mapper;


    public ActivityCompanyMapper<ActivityCompany> getDao() {
        return mapper;
    }

    //**********自定义方法***********

}