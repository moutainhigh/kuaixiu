package com.kuaixiu.groupShortUrl.service;


import com.common.base.service.BaseService;
import com.kuaixiu.groupShortUrl.dao.HsGroupShortUrlRecordMapper;
import com.kuaixiu.groupShortUrl.entity.HsGroupShortUrlRecord;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * HsGroupShortUrlRecord Service
 * @CreateDate: 2019-06-26 上午09:28:49
 * @version: V 1.0
 */
@Service("hsGroupShortUrlRecordService")
public class HsGroupShortUrlRecordService extends BaseService<HsGroupShortUrlRecord> {
    private static final Logger log= Logger.getLogger(HsGroupShortUrlRecordService.class);

    @Autowired
    private HsGroupShortUrlRecordMapper<HsGroupShortUrlRecord> mapper;


    public HsGroupShortUrlRecordMapper<HsGroupShortUrlRecord> getDao() {
        return mapper;
    }

    //**********自定义方法***********

}