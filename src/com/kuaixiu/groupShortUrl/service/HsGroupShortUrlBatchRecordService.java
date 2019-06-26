package com.kuaixiu.groupShortUrl.service;


import com.common.base.service.BaseService;
import com.kuaixiu.groupShortUrl.dao.HsGroupShortUrlBatchRecordMapper;
import com.kuaixiu.groupShortUrl.entity.HsGroupShortUrlBatchRecord;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * HsGroupShortUrlBatchRecord Service
 * @CreateDate: 2019-06-26 上午09:27:50
 * @version: V 1.0
 */
@Service("hsGroupShortUrlBatchRecordService")
public class HsGroupShortUrlBatchRecordService extends BaseService<HsGroupShortUrlBatchRecord> {
    private static final Logger log= Logger.getLogger(HsGroupShortUrlBatchRecordService.class);

    @Autowired
    private HsGroupShortUrlBatchRecordMapper<HsGroupShortUrlBatchRecord> mapper;


    public HsGroupShortUrlBatchRecordMapper<HsGroupShortUrlBatchRecord> getDao() {
        return mapper;
    }

    //**********自定义方法***********

}