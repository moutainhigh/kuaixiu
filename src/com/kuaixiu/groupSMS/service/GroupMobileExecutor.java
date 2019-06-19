package com.kuaixiu.groupSMS.service;

import com.alibaba.fastjson.JSONObject;
import com.common.wechat.common.util.StringUtils;
import com.kuaixiu.groupSMS.entity.HsGroupCouponRole;
import com.kuaixiu.groupSMS.entity.HsGroupMobile;
import com.kuaixiu.groupSMS.entity.HsGroupMobileRecord;
import com.kuaixiu.recycle.entity.RecycleCheckItems;
import com.kuaixiu.recycle.entity.RecycleCoupon;
import com.kuaixiu.recycle.service.RecycleCheckItemsService;
import com.system.basic.user.entity.SessionUser;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class GroupMobileExecutor {
    @Autowired
    private HsGroupMobileRecordService hsGroupMobileRecordService;

    private ExecutorService executor = Executors.newCachedThreadPool();

    public void fun(SessionUser su,List<HsGroupMobile> groupMobiles,HsGroupCouponRole hsGroupCouponRole) throws Exception {

        executor.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                    for(HsGroupMobile groupMobile:groupMobiles){
                        RecycleCoupon recycleCoupon =hsGroupMobileRecordService.receiveCoupon(hsGroupCouponRole,groupMobile.getMobile());
                        HsGroupMobileRecord groupMobileRecord=new HsGroupMobileRecord();
                        groupMobileRecord.setId(UUID.randomUUID().toString().replace("-", ""));
                        groupMobileRecord.setCouponId(recycleCoupon.getId());
                        groupMobileRecord.setCouponCode(recycleCoupon.getCouponCode());
                        groupMobileRecord.setMobile(groupMobile.getMobile());
                        groupMobileRecord.setCreateUserid(su.getUserId());
                        hsGroupMobileRecordService.add(groupMobileRecord);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new RuntimeException("报错啦！！");
                }
            }
        });
    }

}
