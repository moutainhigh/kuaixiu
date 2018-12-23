package com.kuaixiu.order.service;


import java.util.List;

import com.common.base.service.BaseService;
import com.kuaixiu.order.dao.OrderDetailMapper;
import com.kuaixiu.order.entity.OrderDetail;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * OrderDetail Service
 * @CreateDate: 2016-08-26 下午10:44:39
 * @version: V 1.0
 */
@Service("orderDetailService")
public class OrderDetailService extends BaseService<OrderDetail> {
    private static final Logger log= Logger.getLogger(OrderDetailService.class);

    @Autowired
    private OrderDetailMapper<OrderDetail> mapper;


    public OrderDetailMapper<OrderDetail> getDao() {
        return mapper;
    }

    //**********自定义方法***********

    /**
     * 根据订单号查询订单明细
     * @param orderNo
     * @return
     * @author: lijx
     * @CreateDate: 2016-9-10 下午8:28:54
     */
    public List<OrderDetail> queryByOrderNo(String orderNo){
        OrderDetail od = new OrderDetail();
        od.setOrderNo(orderNo);
        return getDao().queryList(od);
    }
    
    /**
     * 根据订单号删除维修记录
     * @param orderNo
     * @return
     */
    public int delRepairByOrderNo(String orderNo){
    	return getDao().delRepairByOrderNo(orderNo);
    }
}