package com.kuaixiu.sjUser.service;


import com.common.base.service.BaseService;
import com.kuaixiu.sjUser.dao.ConstructionCompanyMapper;
import com.kuaixiu.sjUser.entity.ConstructionCompany;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * ConstructionCompany Service
 * @CreateDate: 2019-05-06 上午10:51:22
 * @version: V 1.0
 */
@Service("constructionCompanyService")
public class ConstructionCompanyService extends BaseService<ConstructionCompany> {
    private static final Logger log= Logger.getLogger(ConstructionCompanyService.class);

    @Autowired
    private ConstructionCompanyMapper<ConstructionCompany> mapper;


    public ConstructionCompanyMapper<ConstructionCompany> getDao() {
        return mapper;
    }

    //**********自定义方法***********

}