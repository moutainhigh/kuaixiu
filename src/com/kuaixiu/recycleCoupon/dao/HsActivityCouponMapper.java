package com.kuaixiu.recycleCoupon.dao;

import com.common.base.dao.BaseDao;
import org.apache.ibatis.annotations.Param;

/**
 * HsActivityCoupon Mapper
 *
 * @param <T>
 * @CreateDate: 2019-05-30 上午11:26:38
 * @version: V 1.0
 */
public interface HsActivityCouponMapper<T> extends BaseDao<T> {

    T queryBySource(@Param("source") Integer source);

    T queryBySourceActivityLabel(@Param("source") Integer source, @Param("activityLabel") String activityLabel);

    //查询默认活动
    T queryByIsDefault(@Param("source") Integer source);

    int updateIsDefault1(@Param("id") String id);

    int updateIsDefault0(@Param("id") String id);

}


