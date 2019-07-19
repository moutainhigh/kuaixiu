package com.kuaixiu.recycle.service;


import com.common.base.service.BaseService;
import com.kuaixiu.recycle.dao.CouponAddValueMapper;
import com.kuaixiu.recycle.entity.CouponAddValue;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * CouponAddValue Service
 * @CreateDate: 2019-04-23 上午09:14:46
 * @version: V 1.0
 */
@Service("couponAddValueService")
public class CouponAddValueService extends BaseService<CouponAddValue> {
    private static final Logger log= Logger.getLogger(CouponAddValueService.class);

    @Autowired
    private CouponAddValueMapper<CouponAddValue> mapper;


    public CouponAddValueMapper<CouponAddValue> getDao() {
        return mapper;
    }

    //**********自定义方法***********

}