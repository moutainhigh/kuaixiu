package com.system.api;

import com.common.base.service.BaseService;
import com.system.api.dao.CodeMapper;
import com.system.api.entity.Code;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Auther: anson
 * @Date: 2018/8/6
 * @Description:
 */
@Service
public class CodeService extends BaseService<Code> {

    @Autowired
    private CodeMapper<Code> mapper;

    @Override
    public CodeMapper<Code> getDao() {
        return mapper;
    }

    
}
