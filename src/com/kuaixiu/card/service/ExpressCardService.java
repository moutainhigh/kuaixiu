package com.kuaixiu.card.service;

import com.common.base.service.BaseService;
import com.kuaixiu.card.dao.ExpressCardMapper;
import com.kuaixiu.card.entity.ExpressCard;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Auther: anson
 * @Date: 2018/7/25
 * @Description:
 */
@Service
public class ExpressCardService extends BaseService<ExpressCard> {

    @Autowired
    private ExpressCardMapper<ExpressCard> mapper;

    @Override
    public ExpressCardMapper<ExpressCard> getDao() {
        return mapper;
    }
}
