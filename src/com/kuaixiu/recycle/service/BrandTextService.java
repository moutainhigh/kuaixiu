package com.kuaixiu.recycle.service;


import com.common.base.service.BaseService;
import com.kuaixiu.recycle.dao.BrandTextMapper;
import com.kuaixiu.recycle.entity.BrandText;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * BrandText Service
 * @CreateDate: 2018-12-04 下午05:02:18
 * @version: V 1.0
 */
@Service("brandTextService")
public class BrandTextService extends BaseService<BrandText> {
    private static final Logger log= Logger.getLogger(BrandTextService.class);

    @Autowired
    private BrandTextMapper<BrandText> mapper;


    public BrandTextMapper<BrandText> getDao() {
        return mapper;
    }

    //**********自定义方法***********

}