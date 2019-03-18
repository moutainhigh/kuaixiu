package com.kuaixiu.recycle.service;


import com.common.base.service.BaseService;
import com.kuaixiu.recycle.dao.RecycleTestMapper;
import com.kuaixiu.recycle.entity.RecycleTest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * RecycleTest Service
 * @CreateDate: 2019-03-12 下午04:08:20
 * @version: V 1.0
 */
@Service("recycleTestService")
public class RecycleTestService extends BaseService<RecycleTest> {
    private static final Logger log= Logger.getLogger(RecycleTestService.class);

    @Autowired
    private RecycleTestMapper<RecycleTest> mapper;


    public RecycleTestMapper<RecycleTest> getDao() {
        return mapper;
    }

    //**********自定义方法***********

}