package com.kuaixiu.coupon.service;


import com.common.base.service.BaseService;
import com.kuaixiu.coupon.dao.CouponModelMapper;
import com.kuaixiu.coupon.entity.Coupon;
import com.kuaixiu.coupon.entity.CouponModel;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * CouponModel Service
 * @CreateDate: 2017-02-19 下午11:44:13
 * @version: V 1.0
 */
@Service("couponModelService")
public class CouponModelService extends BaseService<CouponModel> {
    private static final Logger log= Logger.getLogger(CouponModelService.class);

    @Autowired
    private CouponModelMapper<CouponModel> mapper;


    public CouponModelMapper<CouponModel> getDao() {
        return mapper;
    }

    //**********自定义方法***********
    
    /**
     * 批量添加品牌
     * @param c
     * @return
     */
    public int addBatch(Coupon c){
    	return getDao().addBatch(c);
    }
    
    /**
     * 根据优惠券ID查询
     * @param id
     * @return
     */
    public List<CouponModel> queryListByCouponId(String id){
    	CouponModel t = new CouponModel();
    	t.setCouponId(id);
    	return getDao().queryList2(t);
    }
    
    /**
     * 根据优惠券ID删除
     * @param cid
     * @return
     */
    public int delByCouponId(String cid){
    	return getDao().delByCouponId(cid);
    }
    
    
    
    /**
     * 根据批次删除
     * @param cid
     * @return
     */
    public int delByCouponBatchId(String batchId){
    	return getDao().delByCouponBatchId(batchId);
    }
    
    
    /**
     * 根据优惠券批次批量增加支持品牌
     */
    public int addByBatchId(Coupon c){
    	return getDao().addByBatchId(c);
    }
}