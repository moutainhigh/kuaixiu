package com.kuaixiu.groupSMS.service;


import com.common.base.service.BaseService;
import com.kuaixiu.groupSMS.dao.HsGroupMobileBatchRecordMapper;
import com.kuaixiu.groupSMS.entity.HsGroupMobileBatchRecord;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * HsGroupMobileBatchRecord Service
 * @CreateDate: 2019-06-21 上午11:07:52
 * @version: V 1.0
 */
@Service("hsGroupMobileBatchRecordService")
public class HsGroupMobileBatchRecordService extends BaseService<HsGroupMobileBatchRecord> {
    private static final Logger log= Logger.getLogger(HsGroupMobileBatchRecordService.class);

    @Autowired
    private HsGroupMobileBatchRecordMapper<HsGroupMobileBatchRecord> mapper;


    public HsGroupMobileBatchRecordMapper<HsGroupMobileBatchRecord> getDao() {
        return mapper;
    }

    //**********自定义方法***********

}