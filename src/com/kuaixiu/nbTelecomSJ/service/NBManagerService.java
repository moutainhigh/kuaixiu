package com.kuaixiu.nbTelecomSJ.service;


import com.common.base.service.BaseService;
import com.kuaixiu.nbTelecomSJ.dao.NBManagerMapper;
import com.kuaixiu.nbTelecomSJ.entity.NBManager;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * NBManager Service
 * @CreateDate: 2019-02-22 下午06:34:26
 * @version: V 1.0
 */
@Service("nBManagerService")
public class NBManagerService extends BaseService<NBManager> {
    private static final Logger log= Logger.getLogger(NBManagerService.class);

    @Autowired
    private NBManagerMapper<NBManager> mapper;


    public NBManagerMapper<NBManager> getDao() {
        return mapper;
    }

    //**********自定义方法***********

}