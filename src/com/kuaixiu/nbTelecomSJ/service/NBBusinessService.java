package com.kuaixiu.nbTelecomSJ.service;


import com.common.base.service.BaseService;
import com.kuaixiu.nbTelecomSJ.dao.NBBusinessMapper;
import com.kuaixiu.nbTelecomSJ.entity.NBBusiness;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * NBBusiness Service
 * @CreateDate: 2019-02-23 上午11:53:31
 * @version: V 1.0
 */
@Service("nBBusinessService")
public class NBBusinessService extends BaseService<NBBusiness> {
    private static final Logger log= Logger.getLogger(NBBusinessService.class);

    @Autowired
    private NBBusinessMapper<NBBusiness> mapper;


    public NBBusinessMapper<NBBusiness> getDao() {
        return mapper;
    }

    //**********自定义方法***********

}