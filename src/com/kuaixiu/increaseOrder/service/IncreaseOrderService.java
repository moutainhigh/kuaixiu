package com.kuaixiu.increaseOrder.service;

import com.common.base.service.BaseService;
import com.kuaixiu.increaseOrder.dao.IncreaseOrderMapper;
import com.kuaixiu.increaseOrder.entity.IncreaseOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Auther: anson
 * @Date: 2018/8/17
 * @Description: 抬价订单业务处理
 */
@Service
public class IncreaseOrderService extends BaseService<IncreaseOrder> {

    @Autowired
    private IncreaseOrderMapper<IncreaseOrder>  mapper;

    @Override
    public IncreaseOrderMapper<IncreaseOrder> getDao() {
        return mapper;
    }
}
