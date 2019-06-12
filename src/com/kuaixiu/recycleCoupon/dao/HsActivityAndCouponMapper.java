package com.kuaixiu.recycleCoupon.dao;

import com.common.base.dao.BaseDao;

import java.util.List;

/**
 * HsActivityAndCoupon Mapper
 *
 * @param <T>
 * @CreateDate: 2019-06-10 下午05:04:01
 * @version: V 1.0
 */
public interface HsActivityAndCouponMapper<T> extends BaseDao<T> {

    List<T> queryByActivityId(String activityId);

    int deleteByCouponId(String couponId);

    int deleteByActivityId(String couponId);
}


