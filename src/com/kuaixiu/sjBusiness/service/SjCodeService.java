package com.kuaixiu.sjBusiness.service;


import com.common.base.service.BaseService;
import com.kuaixiu.sjBusiness.dao.SjCodeMapper;
import com.kuaixiu.sjBusiness.entity.SjCode;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Code Service
 * @CreateDate: 2019-05-06 上午10:53:39
 * @version: V 1.0
 */
@Service("sjCodeService")
public class SjCodeService extends BaseService<SjCode> {
    private static final Logger log= Logger.getLogger(SjCodeService.class);

    @Autowired
    private SjCodeMapper<SjCode> mapper;


    public SjCodeMapper<SjCode> getDao() {
        return mapper;
    }

    //**********自定义方法***********

}