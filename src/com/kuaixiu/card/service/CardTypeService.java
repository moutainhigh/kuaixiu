package com.kuaixiu.card.service;


import com.common.base.service.BaseService;
import com.kuaixiu.card.dao.CardTypeMapper;
import com.kuaixiu.card.entity.CardType;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * CardType Service
 * @CreateDate: 2018-12-27 下午03:29:34
 * @version: V 1.0
 */
@Service("cardTypeService")
public class CardTypeService extends BaseService<CardType> {
    private static final Logger log= Logger.getLogger(CardTypeService.class);

    @Autowired
    private CardTypeMapper<CardType> mapper;


    public CardTypeMapper<CardType> getDao() {
        return mapper;
    }

    //**********自定义方法***********

}