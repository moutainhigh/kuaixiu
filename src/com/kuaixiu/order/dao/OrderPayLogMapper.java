package com.kuaixiu.order.dao;

import com.common.base.dao.BaseDao;
import com.kuaixiu.order.entity.OrderPayLog;

import java.util.List;

/**
 * OrderPayLog Mapper
 *
 * @param <T>
 * @CreateDate: 2016-09-16 下午10:39:34
 * @version: V 1.0
 */
public interface OrderPayLogMapper<T> extends BaseDao<T> {

    /**
     * 根据订单号查询未结束的支付单
     * @param orderNo
     * @return
     * @author: lijx
     * @CreateDate: 2016-10-6 上午3:20:33
     */
    OrderPayLog queryUnFinishedByOrderNo(String orderNo);
    OrderPayLog queryAppUnFinishedByOrderNo(T T);
    //查詢所有已提交支付日志
    List<T> queryPayLogSubmit(T t);
    /**
     * 根据支付订单号查询支付单
     * @return
     * @author: lijx
     * @CreateDate: 2016-10-6 上午3:20:33
     */
    OrderPayLog queryByPayOrderNo(String payOrderNo);
}


