package com.kuaixiu.recycle.dao;

import com.common.base.dao.BaseDao;
import com.kuaixiu.recycle.entity.RecycleCoupon;

import java.util.List;

/**
 * Created by Administrator on 2018/9/13/013.
 */
public interface RecycleCouponMapper<T> extends BaseDao<T> {

    int updateUpdateRecycleOrder();

    /**
     * 根据优惠券查询
     */
    T queryByCode(String code);

    /**
     * 根据批次id删除
     * @param t
     * @return
     */
    int deleteByBatchId(T t);

    /**
     * 根据加价券编id修改记录,改为未使用-
     * @param t
     * @return
     */
    int updateForNoUse(String t);

    /**
     * 根据id删除
     * @param t
     * @return
     */
    int deleteById(T t);

    /**
     * 根据id修改
     * @param t
     * @return
     */
    int updateForReceive(T t);

    /**
     * 根据编码修改
     * @param t
     * @return
     */
    int updateForUse(T t);

    /**
     * 根据批次修改失效数据
     * @param batchId
     * @return
     */
    int updateStatusByBatchId(String batchId);

    int updateStatusByCouponCode(String batchId);

    /**
     * 查询列表。可通过手机号查询
     * @param t
     * @return
     */
    List<T> queryUnReceive(T t);

    /**
     * 查询批次
     * @return
     */
    List<String> queryBybatch();

    /**
     * 查询数量
     * @param t
     * @return
     */
    int queryCount(T t);

    /**
     * 根据批次修改
     * @return
     */
    int updateByBatchId(T t);

    /**
     * 根据优惠码修改
     * @return
     */
    int couponCodeUpdate(T t);

    List<T> queryCouponListForPage(T t);

}
