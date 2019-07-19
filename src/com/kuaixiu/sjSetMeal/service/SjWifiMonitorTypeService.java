package com.kuaixiu.sjSetMeal.service;


import com.common.base.service.BaseService;
import com.kuaixiu.sjSetMeal.dao.SjWifiMonitorTypeMapper;
import com.kuaixiu.sjSetMeal.entity.SjWifiMonitorType;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * SjWifiMonitorType Service
 * @CreateDate: 2019-05-23 下午02:26:41
 * @version: V 1.0
 */
@Service("sjWifiMonitorTypeService")
public class SjWifiMonitorTypeService extends BaseService<SjWifiMonitorType> {
    private static final Logger log= Logger.getLogger(SjWifiMonitorTypeService.class);

    @Autowired
    private SjWifiMonitorTypeMapper<SjWifiMonitorType> mapper;


    public SjWifiMonitorTypeMapper<SjWifiMonitorType> getDao() {
        return mapper;
    }

    //**********自定义方法***********

}