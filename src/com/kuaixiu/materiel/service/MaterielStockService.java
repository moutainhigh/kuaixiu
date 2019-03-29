package com.kuaixiu.materiel.service;


import com.common.base.service.BaseService;
import com.kuaixiu.materiel.dao.MaterielStockMapper;
import com.kuaixiu.materiel.entity.MaterielStock;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * MaterielStock Service
 * @CreateDate: 2019-03-27 下午04:14:22
 * @version: V 1.0
 */
@Service("materielStockService")
public class MaterielStockService extends BaseService<MaterielStock> {
    private static final Logger log= Logger.getLogger(MaterielStockService.class);

    @Autowired
    private MaterielStockMapper<MaterielStock> mapper;


    public MaterielStockMapper<MaterielStock> getDao() {
        return mapper;
    }

    //**********自定义方法***********

}