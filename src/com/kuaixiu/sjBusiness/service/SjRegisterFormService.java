package com.kuaixiu.sjBusiness.service;


import com.common.base.service.BaseService;
import com.kuaixiu.sjBusiness.dao.SjRegisterFormMapper;
import com.kuaixiu.sjBusiness.entity.SjRegisterForm;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * SjRegisterForm Service
 * @CreateDate: 2019-05-21 下午12:04:52
 * @version: V 1.0
 */
@Service("sjRegisterFormService")
public class SjRegisterFormService extends BaseService<SjRegisterForm> {
    private static final Logger log= Logger.getLogger(SjRegisterFormService.class);

    @Autowired
    private SjRegisterFormMapper<SjRegisterForm> mapper;


    public SjRegisterFormMapper<SjRegisterForm> getDao() {
        return mapper;
    }

    //**********自定义方法***********

    public List<SjRegisterForm> getSjRegisterForms(Integer poeId,Integer modelId,Integer mealId){
        if(poeId!=null){
            return this.getDao().queryByPoeId(poeId,modelId,mealId);
        }else if(modelId!=null){
            return this.getDao().queryByModelId(modelId,mealId);
        }else if(mealId!=null){
            return this.getDao().queryByMealId(mealId);
        }else{
            return this.getDao().queryByNull();
        }
    }
}