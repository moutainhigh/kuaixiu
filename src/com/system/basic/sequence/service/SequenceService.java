package com.system.basic.sequence.service;


import com.common.base.service.BaseService;
import com.system.basic.sequence.dao.SequenceMapper;
import com.system.basic.sequence.entity.Sequence;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Sequence Service
 * @CreateDate: 2016-09-03 下午11:14:22
 * @version: V 1.0
 */
@Service("sequenceService")
public class SequenceService extends BaseService<Sequence> {
    private static final Logger log= Logger.getLogger(SequenceService.class);

    @Autowired
    private SequenceMapper<Sequence> mapper;


    public SequenceMapper<Sequence> getDao() {
        return mapper;
    }

    //**********自定义方法***********
    
    /**
     * 根据key和类型查询当前序列
     * @param key
     * @param type
     * @return
     * @author: lijx
     * @CreateDate: 2016-9-3 下午11:23:35
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public String getNext(String key, String type){
        return getDao().getNext(key, type);
    }
}