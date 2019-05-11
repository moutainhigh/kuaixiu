package com.kuaixiu.sjBusiness.service;


import com.common.base.service.BaseService;
import com.kuaixiu.sjBusiness.dao.AreaBranchOfficeMapper;
import com.kuaixiu.sjBusiness.entity.AreaBranchOffice;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * AreaBranchOffice Service
 * @CreateDate: 2019-05-08 上午09:24:49
 * @version: V 1.0
 */
@Service("areaBranchOfficeService")
public class AreaBranchOfficeService extends BaseService<AreaBranchOffice> {
    private static final Logger log= Logger.getLogger(AreaBranchOfficeService.class);

    @Autowired
    private AreaBranchOfficeMapper<AreaBranchOffice> mapper;


    public AreaBranchOfficeMapper<AreaBranchOffice> getDao() {
        return mapper;
    }

    //**********自定义方法***********

}