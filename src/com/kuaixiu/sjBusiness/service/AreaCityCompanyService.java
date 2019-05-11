package com.kuaixiu.sjBusiness.service;


import com.common.base.service.BaseService;
import com.kuaixiu.sjBusiness.dao.AreaCityCompanyMapper;
import com.kuaixiu.sjBusiness.entity.AreaCityCompany;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * AreaCityCompany Service
 * @CreateDate: 2019-05-08 上午09:22:35
 * @version: V 1.0
 */
@Service("areaCityCompanyService")
public class AreaCityCompanyService extends BaseService<AreaCityCompany> {
    private static final Logger log= Logger.getLogger(AreaCityCompanyService.class);

    @Autowired
    private AreaCityCompanyMapper<AreaCityCompany> mapper;


    public AreaCityCompanyMapper<AreaCityCompany> getDao() {
        return mapper;
    }

    //**********自定义方法***********

}