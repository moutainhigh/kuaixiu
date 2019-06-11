package com.kuaixiu.recycleCoupon.service;


import com.common.base.service.BaseService;
import com.kuaixiu.recycleCoupon.dao.HsActivityAndCouponMapper;
import com.kuaixiu.recycleCoupon.entity.HsActivityAndCoupon;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * HsActivityAndCoupon Service
 * @CreateDate: 2019-06-10 下午05:04:01
 * @version: V 1.0
 */
@Service("hsActivityAndCouponService")
public class HsActivityAndCouponService extends BaseService<HsActivityAndCoupon> {
    private static final Logger log= Logger.getLogger(HsActivityAndCouponService.class);

    @Autowired
    private HsActivityAndCouponMapper<HsActivityAndCoupon> mapper;


    public HsActivityAndCouponMapper<HsActivityAndCoupon> getDao() {
        return mapper;
    }

    //**********自定义方法***********

}