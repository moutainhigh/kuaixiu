package com.kuaixiu.recycleCoupon.dao;

import com.common.base.dao.BaseDao;

import java.util.List;

/**
 * HsActivityCouponRole Mapper
 *
 * @param <T>
 * @CreateDate: 2019-05-30 上午11:27:08
 * @version: V 1.0
 */
public interface HsActivityCouponRoleMapper<T> extends BaseDao<T> {

    List<T> queryByActivityId(String activityId);
}


