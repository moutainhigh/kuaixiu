package com.kuaixiu.couponcodepool.dao;

import com.common.base.dao.BaseDao;
import com.kuaixiu.couponcodepool.entity.CouponCodePool;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * CouponCodePool Mapper
 *
 * @param <T>
 * @CreateDate: 2019-11-07 上午10:00:26
 * @version: V 1.0
 */
public interface CouponCodePoolMapper<T> extends BaseDao<T> {

    /**
     * 随机抽奖
     * @return
     */
    CouponCodePool queryByRandom();

    /**
     * 中奖更新状态
     * @param id
     * @return
     */
     int  updateDel(@Param("id") String id, @Param("userMobile") String userMobile, @Param("fmName") String fmName);


    /**
     * 中奖广播栏查询
     * @return
     */
    List<CouponCodePool> queryPrizeByUserMobile();
}


