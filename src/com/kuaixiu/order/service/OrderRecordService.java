package com.kuaixiu.order.service;


import com.common.base.service.BaseService;
import com.kuaixiu.order.dao.OrderRecordMapper;
import com.kuaixiu.order.entity.OrderRecord;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * OrderRecord Service
 * @CreateDate: 2019-03-29 下午03:38:02
 * @version: V 1.0
 */
@Service("orderRecordService")
public class OrderRecordService extends BaseService<OrderRecord> {
    private static final Logger log= Logger.getLogger(OrderRecordService.class);

    @Autowired
    private OrderRecordMapper<OrderRecord> mapper;


    public OrderRecordMapper<OrderRecord> getDao() {
        return mapper;
    }

    //**********自定义方法***********

}