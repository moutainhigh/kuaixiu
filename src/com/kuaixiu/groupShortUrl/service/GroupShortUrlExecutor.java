package com.kuaixiu.groupShortUrl.service;

import com.common.util.SmsSendUtil;
import com.kuaixiu.groupSMS.entity.*;
import com.kuaixiu.groupSMS.service.HsGroupMobileRecordService;
import com.kuaixiu.groupSMS.service.HsGroupMobileService;
import com.kuaixiu.groupShortUrl.entity.HsGroupShortUrlRecord;
import com.kuaixiu.recycle.entity.RecycleCoupon;
import com.system.basic.user.entity.SessionUser;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class GroupShortUrlExecutor {

    private ExecutorService executor = Executors.newCachedThreadPool();

    public void fun(SessionUser su, List<HsGroupMobile> groupMobiles,
                    HsGroupShortUrlRecordService hsGroupShortUrlRecordService, HsGroupMobileAddress groupMobileAddress,
                    HsGroupMobileSms hsGroupMobileSms,HsGroupMobileService hsGroupMobileService,
                    HsGroupMobileBatchRecord groupMobileBatchRecord) throws Exception {

        executor.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);

                    for (HsGroupMobile groupMobile : groupMobiles) {
                        HsGroupShortUrlRecord groupMobileRecord = new HsGroupShortUrlRecord();
                        groupMobileRecord.setId(UUID.randomUUID().toString().replace("-", ""));
                        groupMobileRecord.setBatchId(groupMobileBatchRecord.getId());
                        groupMobileRecord.setMobile(groupMobile.getMobile());
                        groupMobileRecord.setCreateUserid(su.getUserId());
                        groupMobileRecord.setAddressId(groupMobileAddress.getId());
                        groupMobileRecord.setSmsId(hsGroupMobileSms.getId());
                        hsGroupShortUrlRecordService.add(groupMobileRecord);

                        SmsSendUtil.groupMobileSendCoupon(hsGroupMobileSms.getSmsTemplate(), groupMobile.getMobile(), groupMobileAddress.getAddress());
                    }
                    hsGroupMobileService.getDao().deleteNull();
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new RuntimeException("发送短信，系统异常！！");
                }
            }
        });
    }

}
