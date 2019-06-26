package com.kuaixiu.groupShortUrl.service;


import com.common.base.service.BaseService;
import com.kuaixiu.groupShortUrl.dao.HsGroupShortUrlAddressMapper;
import com.kuaixiu.groupShortUrl.entity.HsGroupShortUrlAddress;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * HsGroupShortUrlAddress Service
 * @CreateDate: 2019-06-26 上午09:26:11
 * @version: V 1.0
 */
@Service("hsGroupShortUrlAddressService")
public class HsGroupShortUrlAddressService extends BaseService<HsGroupShortUrlAddress> {
    private static final Logger log= Logger.getLogger(HsGroupShortUrlAddressService.class);

    @Autowired
    private HsGroupShortUrlAddressMapper<HsGroupShortUrlAddress> mapper;


    public HsGroupShortUrlAddressMapper<HsGroupShortUrlAddress> getDao() {
        return mapper;
    }

    //**********自定义方法***********

}