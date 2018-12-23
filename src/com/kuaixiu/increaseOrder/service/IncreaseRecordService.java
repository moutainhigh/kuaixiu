package com.kuaixiu.increaseOrder.service;

import com.common.base.service.BaseService;
import com.kuaixiu.increaseOrder.dao.IncreaseRecordMapper;
import com.kuaixiu.increaseOrder.entity.IncreaseRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Auther: anson
 * @Date: 2018/8/17
 * @Description:
 */
@Service
public class IncreaseRecordService extends BaseService<IncreaseRecord> {


    @Autowired
    private IncreaseRecordMapper<IncreaseRecord>  mapper;


    @Override
    public IncreaseRecordMapper<IncreaseRecord> getDao() {
        return mapper;
    }
}
