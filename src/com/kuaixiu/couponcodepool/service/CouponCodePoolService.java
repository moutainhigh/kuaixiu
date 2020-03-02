package com.kuaixiu.couponcodepool.service;


import com.common.base.service.BaseService;

import com.kuaixiu.couponcodepool.dao.CouponCodePoolMapper;
import com.kuaixiu.couponcodepool.entity.CouponCodePool;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * CouponCodePool Service
 * @CreateDate: 2019-11-07 上午10:00:26
 * @version: V 1.0
 */
@Service("couponCodePoolService")
public class CouponCodePoolService extends BaseService<CouponCodePool> {
    private static final Logger log= Logger.getLogger(CouponCodePoolService.class);

    @Autowired
    private CouponCodePoolMapper<CouponCodePool> mapper;

    @Override
    public CouponCodePoolMapper<CouponCodePool> getDao() {
        return mapper;
    }

    //**********自定义方法***********

    public CouponCodePool queryByRandom(){
        return mapper.queryByRandom();
    }

    public int  updateDel(String id,String userMobile,String fmName){
        return mapper.updateDel(id,userMobile,fmName);
    }

    public List<CouponCodePool> queryPrizeByUserMobile(){
        return mapper.queryPrizeByUserMobile();
    }

}