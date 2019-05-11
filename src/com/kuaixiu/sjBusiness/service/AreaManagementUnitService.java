package com.kuaixiu.sjBusiness.service;


import com.common.base.service.BaseService;
import com.kuaixiu.sjBusiness.dao.AreaManagementUnitMapper;
import com.kuaixiu.sjBusiness.entity.AreaManagementUnit;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * AreaManagementUnit Service
 * @CreateDate: 2019-05-08 上午09:25:22
 * @version: V 1.0
 */
@Service("areaManagementUnitService")
public class AreaManagementUnitService extends BaseService<AreaManagementUnit> {
    private static final Logger log= Logger.getLogger(AreaManagementUnitService.class);

    @Autowired
    private AreaManagementUnitMapper<AreaManagementUnit> mapper;


    public AreaManagementUnitMapper<AreaManagementUnit> getDao() {
        return mapper;
    }

    //**********自定义方法***********

}