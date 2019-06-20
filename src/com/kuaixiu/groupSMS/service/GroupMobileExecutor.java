package com.kuaixiu.groupSMS.service;

import com.common.util.SmsSendUtil;
import com.kuaixiu.groupSMS.entity.HsGroupCouponRole;
import com.kuaixiu.groupSMS.entity.HsGroupMobile;
import com.kuaixiu.groupSMS.entity.HsGroupMobileAddress;
import com.kuaixiu.groupSMS.entity.HsGroupMobileRecord;
import com.kuaixiu.recycle.entity.RecycleCoupon;
import com.system.basic.user.entity.SessionUser;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class GroupMobileExecutor {

    private ExecutorService executor = Executors.newCachedThreadPool();

    public void fun(SessionUser su,List<HsGroupMobile> groupMobiles,HsGroupCouponRole hsGroupCouponRole,
                    HsGroupMobileRecordService hsGroupMobileRecordService,HsGroupMobileAddress groupMobileAddress) throws Exception {

        executor.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);

                    for(HsGroupMobile groupMobile:groupMobiles){
                        RecycleCoupon recycleCoupon =hsGroupMobileRecordService.receiveCoupon(hsGroupCouponRole,groupMobile.getMobile(),su);
                        HsGroupMobileRecord groupMobileRecord=new HsGroupMobileRecord();
                        groupMobileRecord.setId(UUID.randomUUID().toString().replace("-", ""));
                        groupMobileRecord.setCouponId(recycleCoupon.getId());
                        groupMobileRecord.setCouponCode(recycleCoupon.getCouponCode());
                        groupMobileRecord.setMobile(groupMobile.getMobile());
                        groupMobileRecord.setCreateUserid(su.getUserId());
                        groupMobileRecord.setAddressId(groupMobileAddress.getId());
                        hsGroupMobileRecordService.add(groupMobileRecord);

                        SmsSendUtil.groupMobileSendCoupon(groupMobile.getMobile(),recycleCoupon.getCouponCode(),groupMobileAddress.getAddress());
//                        System.out.print(groupMobile.getMobile());
//                        System.out.print("\n");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new RuntimeException("报错啦！！");
                }
            }
        });
    }

}
