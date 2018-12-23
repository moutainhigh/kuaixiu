package com.kuaixiu.oldtonew.dao;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.common.base.dao.BaseDao;
import com.kuaixiu.oldtonew.entity.NewOrder;
import com.kuaixiu.order.entity.Order;

/**
* @author: anson
* @CreateDate: 2017年6月15日 下午4:03:02
* @version: V 1.0
* 
*/
public interface NewOrderMapper<T> extends BaseDao<T> {

	/**
     * 根据订单号查询订单
     * @param t
     */
    T queryByOrderNo(String orderNo);
    
    /**
    * 根据订单号更新订单状态
    * @param t
    */
    void updateByOrderNo(T t);

    /**
     * 查询订单数量
     * @param t
     * @return
     * @CreateDate: 2016-9-15 下午11:23:11
     */
    int queryCount(T t);
    
    /**
     * 查询订单交易量
     * @param t
     * @return
     * @CreateDate: 2016-9-15 下午11:23:11
     */
    BigDecimal queryAmount(T t);
    
    /**
     * 统计工程师本月订单数
     * @param t
     * @return
     * @CreateDate: 2016-9-25 下午11:12:28
     */
    List<T> queryStatisticByEngineer(T t);
    
    /**
     * 查询未处理订单
     * @param t
     * @return
     * @author: lijx
     * @CreateDate: 2016-10-10 下午11:38:30
     */
    List<Map<String, Object>> queryMapForPage(T t);
    
    /**
     * 查询未处理订单
     * @param t
     * @return
     * @author: lijx
     * @CreateDate: 2016-10-10 下午11:38:30
     */
    List<Map<String, Object>> queryListMap(T t);

    /**
     * 得到指定日期内各连锁商未结算订单信息
     * @return
     */
    List<Map<String,Object>> getSummaryOrderListForPage(T t);
    
    /**
     * 得到指定日期内各连锁商结算总金额
     */
    Map<String,Object> queryBalanceAmount(NewOrder order);

    /**
     * 更改订单balance状态信息
     * @param t
     * @return
     */
    int updateBalanceStatus(T t);
    
    /**
     * 查询待结算的订单
     * @param t
     * @return
     * @author: lijx
     * @CreateDate: 2016-10-20 下午7:32:20
     */
    List<T> queryOrderForBalance(T t);
    
    /**
     * 清楚balance状态
     * @param t
     * @return
     * @author: lijx
     * @CreateDate: 2016-10-26 下午10:47:16
     */
    int deleteBalanceStatus(T t);
    
    	
}
