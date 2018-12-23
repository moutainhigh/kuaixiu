package com.kuaixiu.order.dao;

import com.common.base.dao.BaseDao;
import com.kuaixiu.order.entity.OrderDetail;

/**
 * OrderDetail Mapper
 *
 * @param <T>
 * @CreateDate: 2016-08-26 下午10:44:39
 * @version: V 1.0
 */
public interface OrderDetailMapper<T> extends BaseDao<T> {
    /**
     * 根据订单编号，获取订单明细
     * @param orderNo 订单编号
     * @return 订单明细
     */
    OrderDetail queryByOrderNo(String orderNo);
    
    /**
     * 根据订单号删除维修记录
     * @param orderNo
     * @return
     */
    int delRepairByOrderNo(String orderNo);
    
    
}


