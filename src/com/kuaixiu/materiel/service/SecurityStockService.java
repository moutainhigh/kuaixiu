package com.kuaixiu.materiel.service;


import com.common.base.service.BaseService;
import com.kuaixiu.materiel.dao.SecurityStockMapper;
import com.kuaixiu.materiel.entity.SecurityStock;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * SecurityStock Service
 * @CreateDate: 2019-03-27 下午04:21:15
 * @version: V 1.0
 */
@Service("securityStockService")
public class SecurityStockService extends BaseService<SecurityStock> {
    private static final Logger log= Logger.getLogger(SecurityStockService.class);

    @Autowired
    private SecurityStockMapper<SecurityStock> mapper;


    public SecurityStockMapper<SecurityStock> getDao() {
        return mapper;
    }

    //**********自定义方法***********

}