package com.kuaixiu.activity.service;


import com.common.base.service.BaseService;
import com.kuaixiu.activity.dao.ActivityCompanyMapper;
import com.kuaixiu.activity.entity.ActivityCompany;

import org.apache.commons.collections.map.HashedMap;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * ActivityCompany Service
 * @CreateDate: 2018-12-27 上午11:24:16
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

    public Map<String,Object> objectToMap(ActivityCompany ac){
        Map<String,Object> map=new HashedMap();
        map.put("companyName",ac.getCompanyName());
        map.put("activityImgUrl",ac.getActivityImgUrl());
        map.put("isEnd",ac.getIsEnd());
        map.put("activityTime",ac.getActivityTime());
        Map<String,Object> kxMap=new HashedMap();
        kxMap.put("kxBusinessTitle",ac.getKxBusinessTitle());
        kxMap.put("kxBusiness",ac.getKxBusiness());
        kxMap.put("kxBusinessDetail",ac.getKxBusinessDetail());
        map.put("kx",kxMap);
        Map<String,Object> dxMap=new HashedMap();
        dxMap.put("dxIncrementBusinessTitle",ac.getDxIncrementBusinessTitle());
        dxMap.put("dxIncrementBusiness",ac.getDxIncrementBusiness());
        dxMap.put("dxIncrementBusinessDetail",ac.getDxIncrementBusinessDetail());
        dxMap.put("dxBusinessPersonNumber",ac.getDxBusinessPersonNumber());
        dxMap.put("dxBusinessPerson",ac.getDxBusinessPerson());
        map.put("dx",dxMap);
        return map;
    }
}