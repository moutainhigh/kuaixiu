package com.kuaixiu.groupSMS.service;


import com.common.base.service.BaseService;
import com.kuaixiu.groupSMS.dao.HsGroupCouponRoleMapper;
import com.kuaixiu.groupSMS.entity.HsGroupCouponRole;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * HsGroupCouponRole Service
 * @CreateDate: 2019-06-19 上午09:25:21
 * @version: V 1.0
 */
@Service("hsGroupCouponRoleService")
public class HsGroupCouponRoleService extends BaseService<HsGroupCouponRole> {
    private static final Logger log= Logger.getLogger(HsGroupCouponRoleService.class);

    @Autowired
    private HsGroupCouponRoleMapper<HsGroupCouponRole> mapper;


    public HsGroupCouponRoleMapper<HsGroupCouponRole> getDao() {
        return mapper;
    }

    //**********自定义方法***********

}