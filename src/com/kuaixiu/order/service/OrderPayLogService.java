package com.kuaixiu.order.service;


import java.text.SimpleDateFormat;
import java.util.List;
import java.util.UUID;

import com.common.base.service.BaseService;
import com.common.util.DateUtil;
import com.common.wechat.api.WxMpService;
import com.common.wechat.bean.result.WxMpPayResult;
import com.kuaixiu.order.constant.OrderConstant;
import com.kuaixiu.order.dao.OrderPayLogMapper;
import com.kuaixiu.order.entity.Order;
import com.kuaixiu.order.entity.OrderPayLog;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * OrderPayLog Service
 * @CreateDate: 2016-09-16 下午10:39:34
 * @version: V 1.0
 */
@Service("orderPayLogService")
public class OrderPayLogService extends BaseService<OrderPayLog> {

    @Autowired
    private OrderPayLogMapper<OrderPayLog> mapper;


    public OrderPayLogMapper<OrderPayLog> getDao() {
        return mapper;
    }

    //**********自定义方法***********

    /**
     * 根据订单号查询未结束的支付单
     * @param orderNo
     * @return
     * @author: lijx
     * @CreateDate: 2016-10-6 上午3:20:33
     */
    public OrderPayLog queryUnFinishedByOrderNo(String orderNo){
        return getDao().queryUnFinishedByOrderNo(orderNo);
    }
    public OrderPayLog queryAppUnFinishedByOrderNo(OrderPayLog orderPayLog){
        return getDao().queryAppUnFinishedByOrderNo(orderPayLog);
    }
    ////查詢所有已提交支付日志
    public List<OrderPayLog> queryPayLogSubmit(OrderPayLog orderPayLog){
        return getDao().queryPayLogSubmit(orderPayLog);
    }
    /**
     * 根据订单号查询支付单
     * @param orderNo
     * @return
     * @author: lijx
     * @CreateDate: 2016-10-6 上午3:20:33
     */
    public List<OrderPayLog> queryByOrderNo(String orderNo){
        OrderPayLog t = new OrderPayLog();
        t.setOrderNo(orderNo);
        return getDao().queryList(t);
    }
    
    /**
     * 根据支付订单号查询支付单
     * @param orderNo
     * @return
     * @author: lijx
     * @CreateDate: 2016-10-6 上午3:20:33
     */
    public OrderPayLog queryByPayOrderNo(String payOrderNo){
        return getDao().queryByPayOrderNo(payOrderNo);
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
    public int save(OrderPayLog payLog){
        payLog.setId(UUID.randomUUID().toString());
        return getDao().add(payLog);
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
    public int update(OrderPayLog payLog){
        return getDao().update(payLog);
    }

}