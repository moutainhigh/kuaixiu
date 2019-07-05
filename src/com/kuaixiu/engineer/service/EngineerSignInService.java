package com.kuaixiu.engineer.service;


import com.common.base.service.BaseService;
import com.kuaixiu.engineer.dao.EngineerSignInMapper;
import com.kuaixiu.engineer.entity.EngineerSignIn;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * EngineerSignIn Service
 * @CreateDate: 2018-11-06 下午04:59:14
 * @version: V 1.0
 */
@Service("engineerSignInService")
public class EngineerSignInService extends BaseService<EngineerSignIn> {
    private static final Logger log= Logger.getLogger(EngineerSignInService.class);

    @Autowired
    private EngineerSignInMapper<EngineerSignIn> mapper;


    public EngineerSignInMapper<EngineerSignIn> getDao() {
        return mapper;
    }

    //**********自定义方法***********

}