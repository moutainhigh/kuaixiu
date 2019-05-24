package com.kuaixiu.sjSetMeal.service;


import com.common.base.service.BaseService;
import com.kuaixiu.sjSetMeal.dao.SjSetMealMapper;
import com.kuaixiu.sjSetMeal.entity.SjSetMeal;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * SjSetMeal Service
 * @CreateDate: 2019-05-23 下午02:25:52
 * @version: V 1.0
 */
@Service("sjSetMealService")
public class SjSetMealService extends BaseService<SjSetMeal> {
    private static final Logger log= Logger.getLogger(SjSetMealService.class);

    @Autowired
    private SjSetMealMapper<SjSetMeal> mapper;


    public SjSetMealMapper<SjSetMeal> getDao() {
        return mapper;
    }

    //**********自定义方法***********

}