package com.kuaixiu.order.service;


import com.common.base.service.BaseService;
import com.kuaixiu.order.constant.OrderConstant;
import com.kuaixiu.order.dao.OrderRefundLogMapper;
import com.kuaixiu.order.entity.OrderRefundLog;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * OrderRefundLog Service
 * @CreateDate: 2016-10-23 下午07:30:27
 * @version: V 1.0
 */
@Service("orderRefundLogService")
public class OrderRefundLogService extends BaseService<OrderRefundLog> {
    private static final Logger log= Logger.getLogger(OrderRefundLogService.class);

    @Autowired
    private OrderRefundLogMapper<OrderRefundLog> mapper;


    public OrderRefundLogMapper<OrderRefundLog> getDao() {
        return mapper;
    }

    //**********自定义方法***********

    /**
     * 根据订单号和支付订单号查询退款记录
     * @param orderNo
     * @param payOrderNo
     * @return
     * @author: lijx
     * @CreateDate: 2016-10-23 下午7:46:10
     */
    public List<OrderRefundLog> queryAllByOrderNo(String orderNo, String payOrderNo){
        OrderRefundLog t = new OrderRefundLog();
        t.setOrderNo(orderNo);
        t.setPayOrderNo(payOrderNo);
        return getDao().queryList(t);
    }
    
    
    /**
     * 根据退款单号payRefundNo  查询退款记录  该记录应该是唯一的
     */
    public List<OrderRefundLog> queryByPayRefundNo(String orderNo){
        OrderRefundLog t = new OrderRefundLog();
        t.setPayRefundNo(orderNo);
        return getDao().queryList(t);
    }
    
    
    /**
     * 根据超人订单号查询退款记录
     * 针对申请退款成功了  但是可能还未到账的退款订单
     */
    public List<OrderRefundLog> queryByPayOrderNo(String orderNo){
        OrderRefundLog t = new OrderRefundLog();
        t.setOrderNo(orderNo);
        t.setRefundStatus(OrderConstant.ORDER_REFUND_STATUS_SUCCESS);
        return getDao().queryList(t);
    }
    
    
    
    /**
     * 根据订单号和支付订单号查询退款记录
     * @param orderNo
     * @param payOrderNo
     * @return
     * @author: lijx
     * @CreateDate: 2016-10-23 下午7:46:10
     */
    public OrderRefundLog queryFinishedByOrderNo(String orderNo, String payOrderNo){
        OrderRefundLog t = new OrderRefundLog();
        t.setOrderNo(orderNo);
        t.setPayOrderNo(payOrderNo);
        t.setRefundStatus(OrderConstant.ORDER_REFUND_STATUS_SUCCESS);
        List<OrderRefundLog> list = getDao().queryList(t);
        if(list != null && list.size() > 0){
            return list.get(0);
        }
        return null;
    }
    
    /**
     * 根据订单号和支付订单号查询退款记录
     * @param orderNo
     * @param payOrderNo
     * @return
     * @author: lijx
     * @CreateDate: 2016-10-23 下午7:46:10
     */
    public OrderRefundLog queryUnFinishedByOrderNo(String orderNo, String payOrderNo){
        OrderRefundLog t = new OrderRefundLog();
        t.setOrderNo(orderNo);
        t.setPayOrderNo(payOrderNo);
        t.setQueryStatusArray(Arrays.asList(1,2));
        List<OrderRefundLog> list = getDao().queryList(t);
        if(list != null && list.size() > 0){
            return list.get(0);
        }
        return null;
    }
    
    /**
     * 保存日志
     * 开启新事务
     * @param payLog
     * @return
     * @author: lijx
     * @CreateDate: 2016-10-7 上午4:17:55
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public int save(OrderRefundLog refundLog){
        refundLog.setId(UUID.randomUUID().toString());
        return getDao().add(refundLog);
    }
    
    /**
     * 更新日志
     * 开启新事务
     * @param payLog
     * @return
     * @author: lijx
     * @CreateDate: 2016-10-7 上午4:17:55
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public int update(OrderRefundLog refundLog){
        return getDao().update(refundLog);
    }
}