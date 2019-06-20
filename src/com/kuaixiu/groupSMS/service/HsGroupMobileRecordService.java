package com.kuaixiu.groupSMS.service;


import com.common.base.service.BaseService;
import com.common.util.DateUtil;
import com.kuaixiu.groupSMS.dao.HsGroupMobileRecordMapper;
import com.kuaixiu.groupSMS.entity.HsGroupCouponRole;
import com.kuaixiu.groupSMS.entity.HsGroupMobileRecord;

import com.kuaixiu.recycle.entity.RecycleCoupon;
import com.kuaixiu.recycle.service.RecycleCouponService;
import com.kuaixiu.recycleCoupon.entity.HsActivityAndCoupon;
import com.kuaixiu.recycleCoupon.entity.HsActivityCouponRole;
import com.system.basic.user.entity.SessionUser;
import com.system.constant.SystemConstant;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

/**
 * HsGroupMobileRecord Service
 *
 * @CreateDate: 2019-06-19 下午02:38:21
 * @version: V 1.0
 */
@Service("hsGroupMobileRecordService")
public class HsGroupMobileRecordService extends BaseService<HsGroupMobileRecord> {
    private static final Logger log = Logger.getLogger(HsGroupMobileRecordService.class);

    @Autowired
    private HsGroupMobileRecordMapper<HsGroupMobileRecord> mapper;
    @Autowired
    private RecycleCouponService recycleCouponService;

    public HsGroupMobileRecordMapper<HsGroupMobileRecord> getDao() {
        return mapper;
    }

    //**********自定义方法***********

    public RecycleCoupon receiveCoupon(HsGroupCouponRole hsGroupCouponRole, String mobile,SessionUser su) throws Exception {
        RecycleCoupon recycleCoupon = new RecycleCoupon();
        recycleCoupon.setBatchId(SystemConstant.RECYCLE_GROUP_SMS_COUPON_BATCH);
        recycleCoupon.setCreateUserid(su.getUserId());
        recycleCoupon.setUpdateUserid(su.getUserId());
        recycleCoupon.setId(UUID.randomUUID().toString().replace("-", ""));
        recycleCoupon.setCouponName(hsGroupCouponRole.getCouponName());
        recycleCoupon.setPricingType(hsGroupCouponRole.getPricingType());
        recycleCoupon.setRuleDescription(hsGroupCouponRole.getRuleDescription());
        recycleCoupon.setUpperLimit(hsGroupCouponRole.getUpperLimit());
        recycleCoupon.setSubtraction_price(hsGroupCouponRole.getSubtractionPrice());
        recycleCoupon.setStrCouponPrice(hsGroupCouponRole.getCouponPrice());
        recycleCoupon.setBeginTime(DateUtil.getNowyyyyMMdd());
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR) + 7);
        recycleCoupon.setEndTime(DateUtil.getDateYYYYMMDD(calendar.getTime()));
        recycleCoupon.setReceiveMobile(mobile);
        recycleCoupon.setAddPriceUpper(hsGroupCouponRole.getAddPriceUpper());
        recycleCoupon.setIsDel(0);
        recycleCoupon.setIsUse(0);
        recycleCoupon.setIsReceive(1);
        recycleCoupon.setStatus(1);
        recycleCoupon.setNote(hsGroupCouponRole.getNote());
        recycleCoupon.setCouponCode(recycleCouponService.createNewCode());
        recycleCouponService.add(recycleCoupon);
        return recycleCoupon;
    }

}