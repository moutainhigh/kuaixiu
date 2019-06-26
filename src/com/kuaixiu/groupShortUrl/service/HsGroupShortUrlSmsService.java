package com.kuaixiu.groupShortUrl.service;


import com.common.base.service.BaseService;
import com.kuaixiu.groupShortUrl.dao.HsGroupShortUrlSmsMapper;
import com.kuaixiu.groupShortUrl.entity.HsGroupShortUrlSms;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * HsGroupShortUrlSms Service
 * @CreateDate: 2019-06-26 上午09:29:12
 * @version: V 1.0
 */
@Service("hsGroupShortUrlSmsService")
public class HsGroupShortUrlSmsService extends BaseService<HsGroupShortUrlSms> {
    private static final Logger log= Logger.getLogger(HsGroupShortUrlSmsService.class);

    @Autowired
    private HsGroupShortUrlSmsMapper<HsGroupShortUrlSms> mapper;


    public HsGroupShortUrlSmsMapper<HsGroupShortUrlSms> getDao() {
        return mapper;
    }

    //**********自定义方法***********

}