package com.kuaixiu.recycleCoupon.service;


import com.common.base.service.BaseService;
import com.kuaixiu.recycleCoupon.dao.HsActivityCouponRoleMapper;
import com.kuaixiu.recycleCoupon.entity.HsActivityCouponRole;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * HsActivityCouponRole Service
 * @CreateDate: 2019-05-30 上午11:27:08
 * @version: V 1.0
 */
@Service("hsActivityCouponRoleService")
public class HsActivityCouponRoleService extends BaseService<HsActivityCouponRole> {
    private static final Logger log= Logger.getLogger(HsActivityCouponRoleService.class);

    @Autowired
    private HsActivityCouponRoleMapper<HsActivityCouponRole> mapper;


    public HsActivityCouponRoleMapper<HsActivityCouponRole> getDao() {
        return mapper;
    }

    //**********自定义方法***********

}