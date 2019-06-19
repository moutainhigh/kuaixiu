package com.kuaixiu.groupSMS.service;


import com.common.base.service.BaseService;
import com.kuaixiu.groupSMS.dao.HsGroupMobileAddressMapper;
import com.kuaixiu.groupSMS.entity.HsGroupMobileAddress;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * HsGroupMobileAddress Service
 * @CreateDate: 2019-06-19 下午04:31:51
 * @version: V 1.0
 */
@Service("hsGroupMobileAddressService")
public class HsGroupMobileAddressService extends BaseService<HsGroupMobileAddress> {
    private static final Logger log= Logger.getLogger(HsGroupMobileAddressService.class);

    @Autowired
    private HsGroupMobileAddressMapper<HsGroupMobileAddress> mapper;


    public HsGroupMobileAddressMapper<HsGroupMobileAddress> getDao() {
        return mapper;
    }

    //**********自定义方法***********

}