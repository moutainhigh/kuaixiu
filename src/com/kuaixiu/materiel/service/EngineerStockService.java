package com.kuaixiu.materiel.service;


import com.common.base.service.BaseService;
import com.kuaixiu.materiel.dao.EngineerStockMapper;
import com.kuaixiu.materiel.entity.EngineerStock;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * EngineerStock Service
 * @CreateDate: 2019-03-27 下午04:31:52
 * @version: V 1.0
 */
@Service("engineerStockService")
public class EngineerStockService extends BaseService<EngineerStock> {
    private static final Logger log= Logger.getLogger(EngineerStockService.class);

    @Autowired
    private EngineerStockMapper<EngineerStock> mapper;


    public EngineerStockMapper<EngineerStock> getDao() {
        return mapper;
    }

    //**********自定义方法***********

}