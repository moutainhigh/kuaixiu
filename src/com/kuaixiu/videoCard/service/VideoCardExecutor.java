package com.kuaixiu.videoCard.service;

import com.common.util.SmsSendUtil;
import com.kuaixiu.groupSMS.entity.*;
import com.kuaixiu.groupSMS.service.HsGroupMobileRecordService;
import com.kuaixiu.groupSMS.service.HsGroupMobileService;
import com.kuaixiu.recycle.entity.RecycleCoupon;
import com.kuaixiu.recycle.service.RecycleSystemService;
import com.system.basic.user.entity.SessionUser;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Administrator on 2019/8/23/023.
 */
public class VideoCardExecutor {

    private ExecutorService executor = Executors.newCachedThreadPool();

    public void fun(List<String> mobibles, RecycleSystemService recycleSystemService) throws Exception {

        executor.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                    for(String mobile : mobibles){
                        SmsSendUtil.VideoCardExecutorSendMobile(mobile,recycleSystemService);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new RuntimeException("发送短信，系统异常！！");
                }
            }
        });
    }


    public void youkuSendMsg(List<String> mobibles, RecycleSystemService recycleSystemService) throws Exception {

        executor.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                    for(String mobile : mobibles){
                        SmsSendUtil.YouKuCardExecutorSendMobile(mobile,recycleSystemService);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new RuntimeException("发送短信，系统异常！！");
                }
            }
        });
    }
}
