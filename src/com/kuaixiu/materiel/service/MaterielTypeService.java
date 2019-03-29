package com.kuaixiu.materiel.service;


import com.common.base.service.BaseService;
import com.kuaixiu.materiel.dao.MaterielTypeMapper;
import com.kuaixiu.materiel.entity.MaterielType;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * MaterielType Service
 * @CreateDate: 2019-03-27 下午04:15:21
 * @version: V 1.0
 */
@Service("materielTypeService")
public class MaterielTypeService extends BaseService<MaterielType> {
    private static final Logger log= Logger.getLogger(MaterielTypeService.class);

    @Autowired
    private MaterielTypeMapper<MaterielType> mapper;


    public MaterielTypeMapper<MaterielType> getDao() {
        return mapper;
    }

    //**********自定义方法***********

}