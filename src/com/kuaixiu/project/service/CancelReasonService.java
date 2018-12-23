package com.kuaixiu.project.service;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.common.base.service.BaseService;
import com.common.exception.SystemException;
import com.kuaixiu.project.dao.CancelReasonMapper;
import com.kuaixiu.project.dao.ProjectMapper;
import com.kuaixiu.project.entity.CancelReason;
import com.kuaixiu.project.entity.Project;
import com.system.basic.user.entity.SessionUser;

import net.sf.jxls.exception.ParsePropertyException;
import net.sf.jxls.transformer.XLSTransformer;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;



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