package com.kuaixiu.sjBusiness.service;


import com.common.base.service.BaseService;
import com.kuaixiu.sjBusiness.dao.SjOrderMapper;
import com.kuaixiu.sjBusiness.entity.SjOrder;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Order Service
 * @CreateDate: 2019-05-06 上午10:44:49
 * @version: V 1.0
 */
@Service("sjOrderService")
public class SjOrderService extends BaseService<SjOrder> {
    private static final Logger log= Logger.getLogger(SjOrderService.class);

    @Autowired
    private SjOrderMapper<SjOrder> mapper;


    public SjOrderMapper<SjOrder> getDao() {
        return mapper;
    }

    //**********自定义方法***********

}