package com.kuaixiu.sjSetMeal.service;


import com.common.base.service.BaseService;
import com.kuaixiu.sjSetMeal.dao.SjSaveNetMapper;
import com.kuaixiu.sjSetMeal.entity.SjSaveNet;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * SjSaveNet Service
 * @CreateDate: 2019-05-23 下午02:27:41
 * @version: V 1.0
 */
@Service("sjSaveNetService")
public class SjSaveNetService extends BaseService<SjSaveNet> {
    private static final Logger log= Logger.getLogger(SjSaveNetService.class);

    @Autowired
    private SjSaveNetMapper<SjSaveNet> mapper;


    public SjSaveNetMapper<SjSaveNet> getDao() {
        return mapper;
    }

    //**********自定义方法***********

}