package com.kuaixiu.newtamps.service;


import com.common.base.service.BaseService;
import com.kuaixiu.newtamps.dao.NewTampsMapper;
import com.kuaixiu.newtamps.entity.NewTamps;

import org.apache.ibatis.annotations.Param;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * NewTamps Service
 * @CreateDate: 2019-11-08 上午09:39:09
 * @version: V 1.0
 */
@Service("newTampsService")
public class NewTampsService extends BaseService<NewTamps> {
    private static final Logger log= Logger.getLogger(NewTampsService.class);

    @Autowired
    private NewTampsMapper<NewTamps> mapper;


    public NewTampsMapper<NewTamps> getDao() {
        return mapper;
    }

    //**********自定义方法***********

    public List<NewTamps> queryListByUserMobile(String userMobile){
        return mapper.queryListByUserMobile(userMobile);
    }

}