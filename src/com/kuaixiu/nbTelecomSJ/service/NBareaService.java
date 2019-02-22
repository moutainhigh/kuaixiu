package com.kuaixiu.nbTelecomSJ.service;


import com.common.base.service.BaseService;
import com.kuaixiu.nbTelecomSJ.dao.NBAreaMapper;
import com.kuaixiu.nbTelecomSJ.entity.NBArea;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * NBArea Service
 * @CreateDate: 2019-02-22 下午07:26:25
 * @version: V 1.0
 */
@Service("nBAreaService")
public class NBAreaService extends BaseService<NBArea> {
    private static final Logger log= Logger.getLogger(NBAreaService.class);

    @Autowired
    private NBAreaMapper<NBArea> mapper;


    public NBAreaMapper<NBArea> getDao() {
        return mapper;
    }

    //**********自定义方法***********

}