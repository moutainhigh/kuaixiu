package com.kuaixiu.sjUser.service;


import com.common.base.service.BaseService;
import com.kuaixiu.sjUser.dao.SjWorkerMapper;
import com.kuaixiu.sjUser.entity.SjWorker;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * SjWorker Service
 * @CreateDate: 2019-05-09 下午06:30:39
 * @version: V 1.0
 */
@Service("sjWorkerService")
public class SjWorkerService extends BaseService<SjWorker> {
    private static final Logger log= Logger.getLogger(SjWorkerService.class);

    @Autowired
    private SjWorkerMapper<SjWorker> mapper;


    public SjWorkerMapper<SjWorker> getDao() {
        return mapper;
    }

    //**********自定义方法***********

}