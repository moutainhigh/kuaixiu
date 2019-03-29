package com.kuaixiu.materiel.service;


import com.common.base.service.BaseService;
import com.kuaixiu.materiel.dao.ProcessMapper;
import com.kuaixiu.materiel.entity.Process;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Process Service
 * @CreateDate: 2019-03-28 上午09:07:03
 * @version: V 1.0
 */
@Service("processService")
public class ProcessService extends BaseService<Process> {
    private static final Logger log= Logger.getLogger(ProcessService.class);

    @Autowired
    private ProcessMapper<Process> mapper;


    public ProcessMapper<Process> getDao() {
        return mapper;
    }

    //**********自定义方法***********

}