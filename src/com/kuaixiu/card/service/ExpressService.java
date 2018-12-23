package com.kuaixiu.card.service;

import com.common.base.service.BaseService;
import com.kuaixiu.card.dao.ExpressMapper;
import com.kuaixiu.card.entity.Express;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Auther: anson
 * @Date: 2018/7/25
 * @Description:
 */
@Service
public class ExpressService extends BaseService<Express>{

    @Autowired
    private ExpressMapper<Express> mapper;

    @Override
    public ExpressMapper<Express> getDao() {
        return mapper;
    }
}
