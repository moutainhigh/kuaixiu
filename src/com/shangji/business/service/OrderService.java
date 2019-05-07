package com.shangji.business.service;


import com.common.base.service.BaseService;
import com.shangji.business.dao.OrderMapper;
import com.shangji.business.entity.Order;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Order Service
 * @CreateDate: 2019-05-06 上午10:44:49
 * @version: V 1.0
 */
@Service("orderService")
public class OrderService extends BaseService<Order> {
    private static final Logger log= Logger.getLogger(OrderService.class);

    @Autowired
    private OrderMapper<Order> mapper;


    public OrderMapper<Order> getDao() {
        return mapper;
    }

    //**********自定义方法***********

}