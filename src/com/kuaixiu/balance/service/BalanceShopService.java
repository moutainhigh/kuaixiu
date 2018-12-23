package com.kuaixiu.balance.service;


import com.common.base.service.BaseService;
import com.kuaixiu.balance.dao.BalanceShopMapper;
import com.kuaixiu.balance.entity.BalanceShop;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * BalanceShop Service
 * @CreateDate: 2016-10-15 下午02:52:11
 * @version: V 1.0
 */
@Service("balanceShopService")
public class BalanceShopService extends BaseService<BalanceShop> {
    private static final Logger log= Logger.getLogger(BalanceShopService.class);

    @Autowired
    private BalanceShopMapper<BalanceShop> mapper;


    public BalanceShopMapper<BalanceShop> getDao() {
        return mapper;
    }

    //**********自定义方法***********

}