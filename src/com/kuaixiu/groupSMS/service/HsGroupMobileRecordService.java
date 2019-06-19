package com.kuaixiu.groupSMS.service;


import com.common.base.service.BaseService;
import com.kuaixiu.groupSMS.dao.HsGroupMobileRecordMapper;
import com.kuaixiu.groupSMS.entity.HsGroupMobileRecord;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * HsGroupMobileRecord Service
 * @CreateDate: 2019-06-19 下午02:38:21
 * @version: V 1.0
 */
@Service("hsGroupMobileRecordService")
public class HsGroupMobileRecordService extends BaseService<HsGroupMobileRecord> {
    private static final Logger log= Logger.getLogger(HsGroupMobileRecordService.class);

    @Autowired
    private HsGroupMobileRecordMapper<HsGroupMobileRecord> mapper;


    public HsGroupMobileRecordMapper<HsGroupMobileRecord> getDao() {
        return mapper;
    }

    //**********自定义方法***********

}