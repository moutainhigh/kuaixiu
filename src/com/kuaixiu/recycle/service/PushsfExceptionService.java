package com.kuaixiu.recycle.service;


import com.common.base.service.BaseService;
import com.kuaixiu.recycle.dao.PushsfExceptionMapper;
import com.kuaixiu.recycle.entity.PushsfException;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * pushsfException Service
 * @CreateDate: 2018-10-19 上午09:04:35
 * @version: V 1.0
 */
@Service("PushsfExceptionService")
public class PushsfExceptionService extends BaseService<PushsfException> {
    private static final Logger log= Logger.getLogger(PushsfExceptionService.class);

    @Autowired
    private PushsfExceptionMapper<PushsfException> mapper;


    public PushsfExceptionMapper<PushsfException> getDao() {
        return mapper;
    }

    //**********自定义方法***********

}