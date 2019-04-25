package com.kuaixiu.recycle.service;


import com.common.base.service.BaseService;
import com.kuaixiu.recycle.dao.SourceMapper;
import com.kuaixiu.recycle.entity.Source;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Source Service
 * @CreateDate: 2019-04-22 下午04:52:20
 * @version: V 1.0
 */
@Service("sourceService")
public class SourceService extends BaseService<Source> {
    private static final Logger log= Logger.getLogger(SourceService.class);

    @Autowired
    private SourceMapper<Source> mapper;


    public SourceMapper<Source> getDao() {
        return mapper;
    }

    //**********自定义方法***********

}