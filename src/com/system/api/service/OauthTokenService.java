package com.system.api.service;


import com.common.base.service.BaseService;
import com.system.api.dao.OauthTokenMapper;
import com.system.api.entity.OauthToken;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * OauthToken Service
 * @CreateDate: 2016-09-05 上午01:02:32
 * @version: V 1.0
 */
@Service("oauthTokenService")
public class OauthTokenService extends BaseService<OauthToken> {
    private static final Logger log= Logger.getLogger(OauthTokenService.class);

    @Autowired
    private OauthTokenMapper<OauthToken> mapper;


    public OauthTokenMapper<OauthToken> getDao() {
        return mapper;
    }

    //**********自定义方法***********

    /**
     * 根据token查询
     * @param token
     * @return
     * @CreateDate: 2016-9-5 上午1:08:36
     */
    public OauthToken queryByToken(String token){
        return getDao().queryByToken(token);
    }
    
    /**
     * 根据客户端id查询
     * @param clientId
     * @return
     * @CreateDate: 2016-9-5 上午1:08:36
     */
    public OauthToken queryByClientId(String clientId, String tokenType){
        return getDao().queryByClientId(clientId, tokenType);
    }
}