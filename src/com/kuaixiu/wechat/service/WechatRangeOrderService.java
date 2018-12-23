package com.kuaixiu.wechat.service;


import com.common.base.service.BaseService;
import com.kuaixiu.wechat.dao.WechatRangeOrderMapper;
import com.kuaixiu.wechat.entity.WechatRangeOrder;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * WechatRangeOrder Service
 * @CreateDate: 2018-10-26 上午10:53:07
 * @version: V 1.0
 */
@Service("wechatRangeOrderService")
public class WechatRangeOrderService extends BaseService<WechatRangeOrder> {
    private static final Logger log= Logger.getLogger(WechatRangeOrderService.class);

    @Autowired
    private WechatRangeOrderMapper<WechatRangeOrder> mapper;


    public WechatRangeOrderMapper<WechatRangeOrder> getDao() {
        return mapper;
    }

    //**********自定义方法***********

}