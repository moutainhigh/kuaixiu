package com.kuaixiu.groupSMS.dao;

import com.common.base.dao.BaseDao;

/**
 * HsGroupCouponRole Mapper
 *
 * @param <T>
 * @CreateDate: 2019-06-19 上午09:25:21
 * @version: V 1.0
 */
public interface HsGroupCouponRoleMapper<T> extends BaseDao<T> {

    T queryByNameLabel(String nameLabel);

    int deleteByIsDel(String id);
}


