package com.shangji.business.service;


import com.common.base.service.BaseService;
import com.shangji.business.dao.CodeMapper;
import com.shangji.business.entity.Code;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Code Service
 * @CreateDate: 2019-05-06 上午10:53:39
 * @version: V 1.0
 */
@Service("codeService")
public class CodeService extends BaseService<Code> {
    private static final Logger log= Logger.getLogger(CodeService.class);

    @Autowired
    private CodeMapper<Code> mapper;


    public CodeMapper<Code> getDao() {
        return mapper;
    }

    //**********自定义方法***********

}