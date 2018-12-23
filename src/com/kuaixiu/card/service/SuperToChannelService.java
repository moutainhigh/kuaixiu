package com.kuaixiu.card.service;

import com.common.base.service.BaseService;
import com.kuaixiu.card.dao.SuperToChannelMapper;
import com.kuaixiu.card.entity.SuperToChannel;
import com.kuaixiu.card.entity.TelecomCard;
import com.kuaixiu.zhuanzhuan.service.AuctionOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

/**
 * @Auther: anson
 * @Date: 2018/7/27
 * @Description:超人-电渠 号卡推送
 */
@Service
public class SuperToChannelService extends BaseService<SuperToChannel> {

    @Autowired
    private TelecomCardService telecomCardService;

    @Autowired
    private AuctionOrderService auctionOrderService;

    @Autowired
    private SuperToChannelMapper<SuperToChannel> mapper;

    @Override
    public SuperToChannelMapper<SuperToChannel> getDao() {
        return mapper;
    }




    /***
     * 单个推送
     * @param c
     */
    public void singletonPush(TelecomCard c) {
        SimpleDateFormat s=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SuperToChannel superToChannel = this.queryById(c.getIccid());
        if (superToChannel != null && superToChannel.getStatus() == 0) {
            //已推送过 且推送成功  无需再次推送
            c.setIsPush(2);
            telecomCardService.saveUpdate(c);
        } else {
            //开始推送
            if (superToChannel == null) {
                superToChannel = new SuperToChannel();
                //新增一个
                superToChannel.setIccid(c.getIccid());
                superToChannel.setOrderNo(c.getSuccessOrderId());
                superToChannel.setExpressName(c.getExpressName());
                superToChannel.setExpressNumber(c.getExpressNumber());
                superToChannel.setSendStationId(c.getSendStationId());
                superToChannel.setSendCity(c.getSendCity());
                superToChannel.setSendTime(new Date());
                superToChannel.setOperateTime(s.format(c.getSendTime()));
                this.add(superToChannel);
            }
            //开始请求
            Map map = auctionOrderService.sendToChannel(superToChannel);

        }


    }
}
