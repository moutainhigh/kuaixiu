package com.kuaixiu.nbTelecomSJ.service;


import com.common.base.service.BaseService;
import com.kuaixiu.nbTelecomSJ.dao.NBCountyMapper;
import com.kuaixiu.nbTelecomSJ.entity.NBCounty;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * NBCounty Service
 * @CreateDate: 2019-02-22 下午06:31:52
 * @version: V 1.0
 */
@Service("nBCountyService")
public class NBCountyService extends BaseService<NBCounty> {
    private static final Logger log= Logger.getLogger(NBCountyService.class);

    @Autowired
    private NBCountyMapper<NBCounty> mapper;


    public NBCountyMapper<NBCounty> getDao() {
        return mapper;
    }

    //**********自定义方法***********

}