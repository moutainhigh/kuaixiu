package com.kuaixiu.coupon.dao;

import com.common.base.dao.BaseDao;
import com.kuaixiu.coupon.entity.Coupon;
import com.kuaixiu.coupon.entity.CouponModel;

import java.util.List;

/**
 * CouponModel Mapper
 *
 * @param <T>
 * @CreateDate: 2017-02-19 下午11:44:13
 * @version: V 1.0
 */
public interface CouponModelMapper<T> extends BaseDao<T> {
	/**
	 * 批量添加品牌
	 * @param c
	 * @return
	 */
	int addBatch(Coupon c);

    /**
     * 根据ID删除
     * @param cid
     * @return
     */
    int delByCouponId(String cid);
    
    /**
     * 根据批次删除
     */
    int delByCouponBatchId(String batchId);
    
    /**
     * 根据批次批量添加该批次支持的品牌
     */
    int addByBatchId(Coupon c);

    List<T> queryList2(CouponModel c);
}


