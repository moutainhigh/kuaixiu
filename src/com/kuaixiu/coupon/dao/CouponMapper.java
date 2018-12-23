package com.kuaixiu.coupon.dao;


import com.common.base.dao.BaseDao;

import java.util.List;

/**
 * Coupon Mapper
 *
 * @param <T>
 * @CreateDate: 2017-02-19 下午11:43:18
 * @version: V 1.0
 */
public interface CouponMapper<T> extends BaseDao<T> {

    /**
     * 批量添加
     * @param t
     * @return
     */
    int addList(List<T> t);

	/**
     * 根据账号查询
     * @param code
     * @return
     * @CreateDate: 2016-9-6 下午11:57:44
     */
    T queryByCode(String code);
    
    /**
     * 查询个数
     * @param code
     * @return
     * @CreateDate: 2016-9-6 下午11:57:44
     */
    int queryCount(T t);
    
    /**
     * 修改优惠券已经使用
     * @param c
     * @return
     */
    int updateForUse(T t);

    /**
     * 修改优惠券已经领用
     * @param c
     * @return
     */
    int updateForReceive(T t);
    
    /**
     * 删除优惠券
     * @param p
     * @return
     * @CreateDate: 2016-8-31 下午7:09:59
     */
    int deleteByBatchId(T t);
    
    /**
     * 查询未领用优惠券
     * @param code
     * @return
     * @CreateDate: 2016-9-6 下午11:57:44
     */
    T queryUnReceive(T t);
    /**
     * 根据批次号批量更新优惠券信息
     */
    int updateByBatchId(T t);
}


