package com.kuaixiu.sjBusiness.service;


import com.common.base.service.BaseService;
import com.kuaixiu.sjBusiness.dao.SjProjectMapper;
import com.kuaixiu.sjBusiness.entity.SjProject;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Project Service
 * @CreateDate: 2019-05-06 上午10:39:49
 * @version: V 1.0
 */
@Service("sjProjectService")
public class SjProjectService extends BaseService<SjProject> {
    private static final Logger log= Logger.getLogger(SjProjectService.class);

    @Autowired
    private SjProjectMapper<SjProject> mapper;


    public SjProjectMapper<SjProject> getDao() {
        return mapper;
    }

    //**********自定义方法***********

}