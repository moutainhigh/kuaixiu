package com.kuaixiu.sjBusiness.service;


import com.common.base.service.BaseService;
import com.kuaixiu.sjBusiness.dao.ApprovalNoteMapper;
import com.kuaixiu.sjBusiness.entity.ApprovalNote;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * ApprovalNote Service
 * @CreateDate: 2019-05-13 下午12:42:33
 * @version: V 1.0
 */
@Service("approvalNoteService")
public class ApprovalNoteService extends BaseService<ApprovalNote> {
    private static final Logger log= Logger.getLogger(ApprovalNoteService.class);

    @Autowired
    private ApprovalNoteMapper<ApprovalNote> mapper;


    public ApprovalNoteMapper<ApprovalNote> getDao() {
        return mapper;
    }

    //**********自定义方法***********

}