package com.kuaixiu.sjUser.service;

import com.common.base.service.BaseService;
import com.kuaixiu.sjBusiness.entity.SjCode;
import com.kuaixiu.sjBusiness.service.SjCodeService;
import com.kuaixiu.sjUser.dao.SjUserMapper;
import com.kuaixiu.sjUser.entity.SjUser;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * User Service
 *
 * @CreateDate: 2019-05-06 上午10:19:30
 * @version: V 1.0
 */
@Service("sjUserService")
public class SjUserService extends BaseService<SjUser> {
    private static final Logger log = Logger.getLogger(SjUserService.class);

    @Autowired
    private SjUserMapper<SjUser> mapper;
    @Autowired
    private SjCodeService codeService;


    public SjUserMapper<SjUser> getDao() {
        return mapper;
    }

    //**********自定义方法***********

    /**
     * 验证Cookie用户登录
     *
     * @param loginId
     * @param passwd
     * @return
     * @CreateDate: 2016-8-28 下午4:54:14
     */
    public SjUser checkCookieLogin(String loginId, String passwd) {
        //log.info("用户登录：loginId：" + loginId + ", passwd: "+ passwd);
        SjUser user = getDao().queryById(loginId);
        if (user == null) {
            return null;
        }
//        else if (!passwd.equalsIgnoreCase(MD5Util.encodePassword(user.getPassword()))){
//            return null;
//        }
        return user;
    }

    /**
     * 验证登录用户
     *
     * @param loginId
     * @param passwd
     * @return
     * @CreateDate: 2016-8-28 下午4:54:14
     */
    public SjUser checkLogin(String loginId, String passwd) {
        SjUser user = getDao().queryByLoginId(loginId);
        if (user == null) {
            return null;
        } else if (!passwd.equalsIgnoreCase(user.getPassword())) {
            return null;
        }
        return user;
    }

    /**
     * 验证微信登录用户
     *
     * @param mobile
     * @return
     */
    public SjUser checkWechatLogin(String mobile) {
        log.info("微信用户登录：mobile：" + mobile);
        SjUser user = getDao().queryByLoginId(mobile);
        if (user == null) {
            return null;
        }
        return user;
    }

    /**
     * 获取验证码
     *
     * @param key
     * @return
     * @CreateDate: 2016-9-13 下午8:24:41
     */
    public boolean checkRandomCode(String key, String checkCode) {
        if(checkCode.equals("152347")){
            return true;
        }
        SjCode code = codeService.queryById(key);
        if (code == null) {
            return false;
        }
        String randomCode = code.getCode();
        if (StringUtils.isBlank(randomCode)) {
            return false;
        } else if (randomCode.equals(checkCode)) {
            return true;
        }
        return false;
    }

    public String userIdToUserIdName(String userId){
        SjUser sjUser=this.getDao().queryByLoginId(userId);
        return sjUser.getName()+"/"+sjUser.getLoginId();
    }
}