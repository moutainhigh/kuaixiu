package com.system.api.service;


import java.util.Map;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.common.exception.ApiServiceException;
import com.common.util.DateUtil;
import com.common.util.MD5Util;
import com.common.util.SmsSendUtil;
import com.kuaixiu.engineer.entity.Engineer;
import com.kuaixiu.engineer.service.EngineerService;
import com.system.api.entity.OauthToken;
import com.system.basic.user.entity.SysUser;
import com.system.basic.user.service.SysUserService;
import com.system.constant.ApiResultConstant;
import com.system.constant.SystemConstant;
import com.system.util.SystemUtil;

/**
 * openApi Service
 * @CreateDate: 2016-09-05 上午01:02:32
 * @version: V 1.0
 */
@Service("openApiService")
public class OpenApiService {
    private static final Logger log= Logger.getLogger(OpenApiService.class);

    @Autowired
    private OauthTokenService oauthTokenService;
    @Autowired
    private SysUserService userService;
    @Autowired
    private EngineerService engService;

    /**
     * 毫秒换算单位
     */
    private static final int SECOND_TO_MS = 1000;
    //**********自定义方法***********

    /**
     * 获取token
     * @param number 用户名称
     * @param mobile 用户密码
     * @param idcard 授权范围
     * @return 
     * @CreateDate: 2016-9-5 上午1:23:51
     */
    public String forgotPassword(String number, String mobile, String idcard){
        //验证用户信息
        Engineer eng = engService.queryByEngineerNumber(number);
        //判断用户类型是允许授权
        if(eng == null){
            throw new ApiServiceException(ApiResultConstant.resultCode_1003, "用户名或密码错误");
        }
        if(!mobile.equals(eng.getMobile()) || !idcard.equals(eng.getIdcard())){
            throw new ApiServiceException(ApiResultConstant.resultCode_2001, "手机号或身份证错误");
        }
        //生成密码
        String passwd = MD5Util.getRandomString(6).toLowerCase();//全部转化为小写输出
        SysUser user = userService.resetPasswd(number, passwd, number);
        if(user == null){
            throw new ApiServiceException(ApiResultConstant.resultCode_3002, "重置密码错误");
        }
        //查询是否有存在的token
        OauthToken token = getToken(number, "password");
        if (token != null) {
            //移除原有token
            SystemUtil.removeToken(token);
            token.setAccessToken(null);
            token.setExpiresIn(0L);
            //更新token
            oauthTokenService.saveUpdate(token);
        }
        //发送短信
        try{
            SmsSendUtil.sendNewPasswd(eng.getMobile(), passwd);
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return passwd;
    }
    
    /**
     * 获取token
     * @param grantType token类型
     * @param username 用户名称
     * @param password 用户密码
     * @param scope 授权范围
     * @param ip ip地址
     * @return 
     * @CreateDate: 2016-9-5 上午1:23:51
     */
    public OauthToken getAccessToken(String grantType, String username, 
            String password, String scope, String ip){
        //查询用户信息
        SysUser user = userService.checkLogin(username, password);
        //判断用户类型是允许授权
        if(user == null){
            throw new ApiServiceException("2001", "用户名或密码错误");
        }
        if(user.getType() != SystemConstant.USER_TYPE_ENGINEER){
            throw new ApiServiceException("2001", "只有工程师可以授权");
        }
        //查询是否有存在的token
        OauthToken token = getToken(username, grantType);
        if (token != null) {
            //移除原有token
            SystemUtil.removeToken(token);
        }
        else{
            token = new OauthToken();
        }
        
        //生成token
        String nowDate = DateUtil.getNowyyyyMMddHHmmssSSS();
        String plainText = username + nowDate + grantType;
        String accessToken = MD5Util.md5Encode(plainText, SystemUtil.getSysCfgProperty(SystemConstant.ACCESS_TOKEN_SALT_KEY));
        token.setClientId(username);
        token.setTokenType(grantType);
        token.setAccessToken(accessToken);
        token.setTime(System.currentTimeMillis());
        token.setExpiresIn(Long.parseLong(SystemUtil.getSysCfgProperty(SystemConstant.ACCESS_TOKEN_EXPIRES_KEY)));
        
        if(token.getId() != null){
            //更新token
            oauthTokenService.saveUpdate(token);
        }
        else{
            //保存token
            token.setId(UUID.randomUUID().toString());
            oauthTokenService.add(token);
        }
        //将token添加到内存
        SystemUtil.saveToken(token);
        return token;
    }
    
    /**
     * 获取token
     * @param refreshToken token
     * @param scope 授权范围
     * @param ip ip地址
     * @return 
     * @CreateDate: 2016-9-5 上午1:23:51
     */
    public OauthToken refreshToken(String refreshToken, String scope, String ip){
        //获取token
        OauthToken token = getToken(refreshToken);
        //移除原有token
        SystemUtil.removeToken(token);
        
        //生成token
        String nowDate = DateUtil.getNowyyyyMMddHHmmssSSS();
        String plainText = token.getClientId() + nowDate + token.getTokenType();
        String accessToken = MD5Util.md5Encode(plainText, SystemUtil.getSysCfgProperty(SystemConstant.ACCESS_TOKEN_SALT_KEY));

        token.setAccessToken(accessToken);
        token.setTime(System.currentTimeMillis());
        token.setExpiresIn(Long.parseLong(SystemUtil.getSysCfgProperty(SystemConstant.ACCESS_TOKEN_EXPIRES_KEY)));
        
        //更新token
        oauthTokenService.saveUpdate(token);

        //将token添加到内存
        SystemUtil.saveToken(token);
        return token;
    }
    
    /**
     * 1、判断内存中是否存在token
     * 2、判断数据库中是否存在token
     * 3、保存token到内存
     * 4、判断token是否过期
     * 5、保存用户信息到params中
     * 
     * @param token
     * @param params
     * @CreateDate: 2016-9-5 下午9:25:38
     */
    public void checkToken(String accessToken, Map<String, String> params){
        //获取token
        OauthToken token = getToken(accessToken);
        
        //验证token是否过期
        Long nowTime = System.currentTimeMillis();
        if(token.getTime() + token.getExpiresIn() * SECOND_TO_MS < nowTime){
            throw new ApiServiceException(ApiResultConstant.resultCode_2002, ApiResultConstant.resultCode_str_2002);
        }
        
        //设置用户数据
        params.put("pmClientId", token.getClientId());
    }

    /**
     * 获取token
     * 1、先从内存中查找
     * 2、如果未找到则从数据库中查询
     * 3、如果还是为找到则抛出异常
     * @param accessToken
     * @return
     * @CreateDate: 2016-9-14 下午8:31:19
     */
    private OauthToken getToken(String accessToken) {
        //查询数据中是否存在token
        OauthToken token = SystemUtil.getToken(accessToken);
        if (token == null) {
            token = oauthTokenService.queryByToken(accessToken);
            if (token == null) {
                throw new ApiServiceException(ApiResultConstant.resultCode_2003, ApiResultConstant.resultCode_str_2003);
            }
            else {
                //保存token到内存
                SystemUtil.saveToken(token);
            }
        }
        return token;
    }
    
    /**
     * 获取token
     * 1、先从内存中查找
     * 2、如果未找到则从数据库中查询
     * @param accessToken
     * @return
     * @CreateDate: 2016-9-14 下午8:31:19
     */
    private OauthToken getToken(String username, String grantType) {
        //查询数据中是否存在token
        OauthToken token = SystemUtil.getTokenByClientId(username, grantType);
        if(token == null){
            //查询数据库中是否已有token
            token = oauthTokenService.queryByClientId(username, grantType);
        }
        return token;
    }
}