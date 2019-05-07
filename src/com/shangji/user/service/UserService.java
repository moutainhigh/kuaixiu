package com.shangji.user.service;

import com.common.base.service.BaseService;
import com.shangji.business.entity.Code;
import com.shangji.business.service.CodeService;
import com.shangji.user.dao.UserMapper;
import com.shangji.user.entity.User;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

/**
 * User Service
 *
 * @CreateDate: 2019-05-06 上午10:19:30
 * @version: V 1.0
 */
@Service("userService")
public class UserService extends BaseService<User> {
    private static final Logger log = Logger.getLogger(UserService.class);

    @Autowired
    private UserMapper<User> mapper;
    @Autowired
    private CodeService codeService;


    public UserMapper<User> getDao() {
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
    public User checkCookieLogin(String loginId, String passwd) {
        //log.info("用户登录：loginId：" + loginId + ", passwd: "+ passwd);
        User user = getDao().queryById(loginId);
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
    public User checkLogin(String loginId, String passwd) {
        User user = getDao().queryByLoginId(loginId);
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
    public User checkWechatLogin(String mobile) {
        log.info("微信用户登录：mobile：" + mobile);
        User user = getDao().queryByLoginId(mobile);
        if (user == null) {
            return null;
        }
        return user;
    }

    /**
     * 获取验证码
     *
     * @param request
     * @return
     * @CreateDate: 2016-9-13 下午8:24:41
     */
    public boolean checkRandomCode(String key, String checkCode) {
        Code code = codeService.queryById(key);
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
}