package com.kuaixiu.sjSetMeal.service;


import com.common.base.service.BaseService;
import com.kuaixiu.sjSetMeal.dao.SjPoeMapper;
import com.kuaixiu.sjSetMeal.entity.SjPoe;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * SjPoe Service
 * @CreateDate: 2019-05-23 下午02:27:05
 * @version: V 1.0
 */
@Service("sjPoeService")
public class SjPoeService extends BaseService<SjPoe> {
    private static final Logger log= Logger.getLogger(SjPoeService.class);

    @Autowired
    private SjPoeMapper<SjPoe> mapper;


    public SjPoeMapper<SjPoe> getDao() {
        return mapper;
    }

    //**********自定义方法***********

}