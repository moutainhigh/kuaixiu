package com.kuaixiu.nbTelecomSJ.service;


import com.common.base.service.BaseService;
import com.kuaixiu.nbTelecomSJ.dao.NBareaMapper;
import com.kuaixiu.nbTelecomSJ.entity.NBarea;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * NBarea Service
 * @CreateDate: 2019-02-22 下午06:31:04
 * @version: V 1.0
 */
@Service("nBareaService")
public class NBareaService extends BaseService<NBarea> {
    private static final Logger log= Logger.getLogger(NBareaService.class);

    @Autowired
    private NBareaMapper<NBarea> mapper;


    public NBareaMapper<NBarea> getDao() {
        return mapper;
    }

    //**********自定义方法***********

}