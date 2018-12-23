package com.system.util;

import com.kuaixiu.card.entity.TelecomCard;
import com.kuaixiu.card.service.SuperToChannelService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;

/**
 * @Auther: anson
 * @Date: 2018/8/10
 * @Description:线程请求
 */
@Service
public class MyRunnable implements Runnable {

    private static final Log log= LogFactory.getLog(MyRunnable.class);


    private SuperToChannelService superToChannelService;


    private TelecomCard card;

    public MyRunnable(){

    }

    public MyRunnable(TelecomCard c,SuperToChannelService s){
               this.card=c;
               this.superToChannelService=s;
    }

    @Override
    public void run() {
        System.out.println(Thread.currentThread().getName()+"执行");
        try {
            System.out.println("当前iccid："+card.getIccid());
            superToChannelService.singletonPush(card);
        } catch (Exception e) {
            e.printStackTrace();
            log.info("推送电渠失败："+e.getMessage());

        }
    }




}
