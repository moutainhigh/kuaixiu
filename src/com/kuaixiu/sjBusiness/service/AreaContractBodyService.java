package com.kuaixiu.sjBusiness.service;


import com.common.base.service.BaseService;
import com.kuaixiu.sjBusiness.dao.AreaContractBodyMapper;
import com.kuaixiu.sjBusiness.entity.AreaContractBody;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * AreaContractBody Service
 * @CreateDate: 2019-05-08 上午09:26:04
 * @version: V 1.0
 */
@Service("areaContractBodyService")
public class AreaContractBodyService extends BaseService<AreaContractBody> {
    private static final Logger log= Logger.getLogger(AreaContractBodyService.class);

    @Autowired
    private AreaContractBodyMapper<AreaContractBody> mapper;


    public AreaContractBodyMapper<AreaContractBody> getDao() {
        return mapper;
    }

    //**********自定义方法***********

}