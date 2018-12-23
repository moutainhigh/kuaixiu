package com.system.api.dao;

import org.apache.ibatis.annotations.Param;

import com.common.base.dao.BaseDao;
import com.system.api.entity.OauthToken;

/**
 * OauthToken Mapper
 *
 * @param <T>
 * @CreateDate: 2016-09-05 上午01:02:32
 * @version: V 1.0
 */
public interface OauthTokenMapper<T> extends BaseDao<T> {
    
    /**
     * 根据token查询
     * @param token
     * @return
     * @CreateDate: 2016-9-5 上午1:08:36
     */
    OauthToken queryByToken(String token);
    
    /**
     * 根据客户端id查询
     * @param clientId
     * @return
     * @CreateDate: 2016-9-5 上午1:08:36
     */
    OauthToken queryByClientId(@Param(value="clientId")String clientId, @Param(value="tokenType")String tokenType);
}


