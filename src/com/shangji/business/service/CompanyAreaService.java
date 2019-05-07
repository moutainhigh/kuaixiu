package com.shangji.business.service;


import com.common.base.service.BaseService;
import com.shangji.business.dao.CompanyAreaMapper;
import com.shangji.business.entity.CompanyArea;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * CompanyArea Service
 * @CreateDate: 2019-05-06 上午10:53:07
 * @version: V 1.0
 */
@Service("companyAreaService")
public class CompanyAreaService extends BaseService<CompanyArea> {
    private static final Logger log= Logger.getLogger(CompanyAreaService.class);

    @Autowired
    private CompanyAreaMapper<CompanyArea> mapper;


    public CompanyAreaMapper<CompanyArea> getDao() {
        return mapper;
    }

    //**********自定义方法***********

}