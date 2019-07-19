package com.kuaixiu.recycleCoupon.service;


import com.common.base.service.BaseService;
import com.kuaixiu.recycleCoupon.dao.HsUserActivityCouponMapper;
import com.kuaixiu.recycleCoupon.entity.HsUserActivityCoupon;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * HsUserActivityCoupon Service
 * @CreateDate: 2019-05-30 上午11:28:06
 * @version: V 1.0
 */
@Service("hsUserActivityCouponService")
public class HsUserActivityCouponService extends BaseService<HsUserActivityCoupon> {
    private static final Logger log= Logger.getLogger(HsUserActivityCouponService.class);

    @Autowired
    private HsUserActivityCouponMapper<HsUserActivityCoupon> mapper;


    public HsUserActivityCouponMapper<HsUserActivityCoupon> getDao() {
        return mapper;
    }

    //**********自定义方法***********

}