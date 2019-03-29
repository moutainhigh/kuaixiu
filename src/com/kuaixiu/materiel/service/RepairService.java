package com.kuaixiu.materiel.service;


import com.common.base.service.BaseService;
import com.kuaixiu.materiel.dao.RepairMapper;
import com.kuaixiu.materiel.entity.Repair;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Repair Service
 * @CreateDate: 2019-03-28 上午09:10:10
 * @version: V 1.0
 */
@Service("repairService")
public class RepairService extends BaseService<Repair> {
    private static final Logger log= Logger.getLogger(RepairService.class);

    @Autowired
    private RepairMapper<Repair> mapper;


    public RepairMapper<Repair> getDao() {
        return mapper;
    }

    //**********自定义方法***********

}