package com.kuaixiu.project.service;

import com.common.base.service.BaseService;
import com.kuaixiu.project.dao.CancelReasonMapper;
import com.kuaixiu.project.entity.CancelReason;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;



@Service("cancelReasonService")
public class CancelReasonService extends BaseService<CancelReason> {
    private static final Logger log= Logger.getLogger(CancelReasonService.class);

    @Autowired
    private CancelReasonMapper<CancelReason> mapper;


    public CancelReasonMapper<CancelReason> getDao() {
        return mapper;
    }

    //**********自定义方法***********
 
  
}