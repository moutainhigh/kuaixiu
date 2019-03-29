package com.kuaixiu.materiel.service;


import com.common.base.service.BaseService;
import com.kuaixiu.materiel.dao.ProcessImageMapper;
import com.kuaixiu.materiel.entity.ProcessImage;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * ProcessImage Service
 * @CreateDate: 2019-03-28 上午09:08:41
 * @version: V 1.0
 */
@Service("processImageService")
public class ProcessImageService extends BaseService<ProcessImage> {
    private static final Logger log= Logger.getLogger(ProcessImageService.class);

    @Autowired
    private ProcessImageMapper<ProcessImage> mapper;


    public ProcessImageMapper<ProcessImage> getDao() {
        return mapper;
    }

    //**********自定义方法***********

}