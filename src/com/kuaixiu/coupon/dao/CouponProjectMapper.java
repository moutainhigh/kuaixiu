package com.kuaixiu.coupon.dao;

import com.common.base.dao.BaseDao;
import com.kuaixiu.coupon.entity.Coupon;
import com.kuaixiu.coupon.entity.CouponProject;

import java.util.List;

/**
 * CouponProject Mapper
 *
 * @param <T>
 * @CreateDate: 2017-02-21 下午11:53:44
 * @version: V 1.0
 */
public interface CouponProjectMapper<T> extends BaseDao<T> {
	/**
	 * 批量添加故障
	 * @param c
	 * @return
	 */
	int addBatch(Coupon c);
	
	/**
	 * 根据优惠券批次id添加支持故障
	 */
	int addByBatchId(Coupon c);
	
    /**
     * 根据ID删除
     * @param cid
     * @return
     */
    int delByCouponId(String cid);
    
    /**
     * 根据优惠券批次删除
     * @param cid
     * @return
     */
    int delByBatchId(String batchId);

    List<T> queryList2(CouponProject couponProject);
}


