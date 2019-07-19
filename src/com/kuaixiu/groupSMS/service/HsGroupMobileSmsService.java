package com.kuaixiu.groupSMS.service;


import com.common.base.service.BaseService;
import com.kuaixiu.groupSMS.dao.HsGroupMobileSmsMapper;
import com.kuaixiu.groupSMS.entity.HsGroupMobileSms;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * HsGroupMobileSms Service
 * @CreateDate: 2019-06-20 下午03:44:16
 * @version: V 1.0
 */
@Service("hsGroupMobileSmsService")
public class HsGroupMobileSmsService extends BaseService<HsGroupMobileSms> {
    private static final Logger log= Logger.getLogger(HsGroupMobileSmsService.class);

    @Autowired
    private HsGroupMobileSmsMapper<HsGroupMobileSms> mapper;


    public HsGroupMobileSmsMapper<HsGroupMobileSms> getDao() {
        return mapper;
    }

    //**********自定义方法***********

}