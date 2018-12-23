package com.kuaixiu.recycle.service;


import com.common.base.service.BaseService;
import com.common.exception.SystemException;
import com.common.wechat.common.util.StringUtils;
import com.kuaixiu.recycle.dao.RecycleExternalLoginMapper;
import com.kuaixiu.recycle.entity.RecycleExternalLogin;

import com.system.api.entity.ResultData;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * RecycleExternalLogin Service
 * @CreateDate: 2018-09-29 上午09:09:53
 * @version: V 1.0
 */
@Service("recycleExternalLoginService")
public class RecycleExternalLoginService extends BaseService<RecycleExternalLogin> {
    private static final Logger log= Logger.getLogger(RecycleExternalLoginService.class);

    @Autowired
    private RecycleExternalLoginMapper<RecycleExternalLogin> mapper;


    public RecycleExternalLoginMapper<RecycleExternalLogin> getDao() {
        return mapper;
    }

    //**********自定义方法***********

    public String addLogin(String loginMobile,String source){
        if(StringUtils.isBlank(loginMobile)){
            throw new SystemException("登录手机号为空");
        }
        RecycleExternalLogin login=new RecycleExternalLogin();
        login.setId(UUID.randomUUID().toString().replace("-","-"));
        login.setLoginMobile(loginMobile);
        login.setSource(source);//存储来源
        //用于验证用户是否是本次登录操作
        login.setToken(UUID.randomUUID().toString().replace("-",""));
        getDao().add(login);//保存登录信息
        return login.getToken();
    }

    //根据token查询
    public RecycleExternalLogin queryLogin(String token){
        return getDao().queryByToken(token);
    }


}