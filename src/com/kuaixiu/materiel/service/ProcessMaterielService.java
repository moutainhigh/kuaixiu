package com.kuaixiu.materiel.service;


import com.common.base.service.BaseService;
import com.kuaixiu.materiel.dao.ProcessMaterielMapper;
import com.kuaixiu.materiel.entity.ProcessMateriel;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * ProcessMateriel Service
 * @CreateDate: 2019-03-28 上午09:09:28
 * @version: V 1.0
 */
@Service("processMaterielService")
public class ProcessMaterielService extends BaseService<ProcessMateriel> {
    private static final Logger log= Logger.getLogger(ProcessMaterielService.class);

    @Autowired
    private ProcessMaterielMapper<ProcessMateriel> mapper;


    public ProcessMaterielMapper<ProcessMateriel> getDao() {
        return mapper;
    }

    //**********自定义方法***********

}