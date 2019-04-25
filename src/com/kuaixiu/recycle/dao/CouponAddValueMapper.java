package com.kuaixiu.recycle.dao;

import com.common.base.dao.BaseDao;
import org.apache.ibatis.annotations.Param;

/**
 * CouponAddValue Mapper
 *
 * @param <T>
 * @CreateDate: 2019-04-23 上午09:14:46
 * @version: V 1.0
 */
public interface CouponAddValueMapper<T> extends BaseDao<T> {

    T queryByType(@Param("type")Integer type);
}


