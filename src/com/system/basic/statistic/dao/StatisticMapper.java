package com.system.basic.statistic.dao;

import java.util.List;
import java.util.Map;

import com.common.base.dao.BaseDao;
import com.system.basic.statistic.entity.Statistic;

/**
 * Statistic Mapper
 *
 * @param <T>
 * @CreateDate: 2016-09-24 上午01:21:24
 * @version: V 1.0
 */
public interface StatisticMapper<T> extends BaseDao<T> {
    
    /**
     * 查询统计数据
     * @param s
     * @return
     * @author: lijx
     * @CreateDate: 2016-9-25 下午3:10:22
     */
    List<Statistic> queryStatistic(T t);
    
    /**
     * 查询最后一次统计日期
     * @param t
     * @return
     */
    String queryLastCountDay(T t);
    
    /**
     * 统计订单数量
     * @param params
     * @return
     */
    long queryOrderCountByDay(Map<String, Object> params);
    
    /**
     * 统计连锁商数量
     * @param params
     * @return
     */
    long queryProviderCountByDay(Map<String, Object> params);
    
    /**
     * 统计客户数量
     * @param params
     * @return
     */
    long queryCustomerCountByDay(Map<String, Object> params);
    
    /**
     * 统计交易额数量
     * @param params
     * @return
     */
    long querySumMomeyByDay(Map<String, Object> params);
}


