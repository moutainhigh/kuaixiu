package com.kuaixiu.order.service;


import com.common.base.service.BaseService;
import com.kuaixiu.order.dao.OrderLogMapper;
import com.kuaixiu.order.entity.OrderLog;
import com.kuaixiu.order.entity.OrderPayLog;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * OrderLog Service
 * @CreateDate: 2016-08-26 下午10:45:29
 * @version: V 1.0
 */
@Service("orderLogService")
public class OrderLogService extends BaseService<OrderLog> {
    private static final Logger log= Logger.getLogger(OrderLogService.class);

    @Autowired
    private OrderLogMapper<OrderLog> mapper;


    public OrderLogMapper<OrderLog> getDao() {
        return mapper;
    }

    //**********自定义方法***********

}