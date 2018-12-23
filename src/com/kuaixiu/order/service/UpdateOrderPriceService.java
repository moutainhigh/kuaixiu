package com.kuaixiu.order.service;


import com.common.base.service.BaseService;
import com.kuaixiu.order.dao.UpdateOrderPriceMapper;
import com.kuaixiu.order.entity.UpdateOrderPrice;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * UpdateOrderPrice Service
 * @CreateDate: 2018-11-06 上午10:36:10
 * @version: V 1.0
 */
@Service("updateOrderPriceService")
public class UpdateOrderPriceService extends BaseService<UpdateOrderPrice> {
    private static final Logger log= Logger.getLogger(UpdateOrderPriceService.class);

    @Autowired
    private UpdateOrderPriceMapper<UpdateOrderPrice> mapper;


    public UpdateOrderPriceMapper<UpdateOrderPrice> getDao() {
        return mapper;
    }

    //**********自定义方法***********

}