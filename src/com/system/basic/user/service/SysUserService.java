package com.system.basic.user.service;


import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.common.base.service.BaseService;
import com.common.exception.SystemException;
import com.common.util.MD5Util;
import com.common.util.SmsSendUtil;
import com.kuaixiu.provider.entity.Provider;
import com.kuaixiu.provider.service.ProviderService;
import com.kuaixiu.shop.entity.Shop;
import com.kuaixiu.shop.service.ShopService;
import com.system.basic.user.entity.SysUser;
import com.system.basic.user.entity.SysUserRole;
import com.system.basic.user.dao.SysUserMapper;
import com.system.constant.SystemConstant;

/**
 * SysUser Service
 * @CreateDate: 2016-08-26 下午10:27:15
 * @version: V 1.0
 */
@Service("sysUserService")
public class SysUserService extends BaseService<SysUser> {
    private static final Logger log= Logger.getLogger(SysUserService.class);

    @Autowired
    private SysUserMapper<SysUser> mapper;
    @Autowired
    private SysUserRoleService sysUserRoleService;
    @Autowired
    private ProviderService providerService;
    @Autowired
    private ShopService shopService;


    public SysUserMapper<SysUser> getDao() {
        return mapper;
    }

    //**********自定义方法***********
    
    /**
     * 验证登录用户
     * @param loginId
     * @param passwd
     * @return
     * @CreateDate: 2016-8-28 下午4:54:14
     */
    public void forgotCheck(String loginId, String mobile){
        log.info("忘记密码：loginId：" + loginId + ", mobile: "+ mobile);
        if(StringUtils.isBlank(loginId) || StringUtils.isBlank(mobile)){
            throw new SystemException("账号或手机号输入错误！");
        }
        SysUser user = getDao().queryById(loginId);
        if (user == null) {
            throw new SystemException("账号或手机号输入错误！");
        }
        if (user.getType() == SystemConstant.USER_TYPE_PROVIDER) {
            Provider p = providerService.queryByCode(loginId);
            if (p == null) {
                throw new SystemException("账号或手机号输入错误！");
            }
            if (!mobile.equals(p.getManagerMobile())) {
                throw new SystemException("账号或手机号输入错误！");
            }
            String newPasswd = SmsSendUtil.randomCode();
            resetPasswd(loginId, newPasswd, loginId);
            if(!SmsSendUtil.sendNewPasswd(mobile, newPasswd)){
                throw new SystemException("系统异常，请稍后再试！");
            }
        }
        else if (user.getType() == SystemConstant.USER_TYPE_SHOP) {
            Shop p = shopService.queryByCode(loginId);
            if (p == null) {
                throw new SystemException("账号或手机号输入错误！");
            }
            if (!mobile.equals(p.getManagerMobile())) {
                throw new SystemException("账号或手机号输入错误！");
            }
            String newPasswd = SmsSendUtil.randomCode();
            resetPasswd(loginId, newPasswd, loginId);
            if(!SmsSendUtil.sendNewPasswd(mobile, newPasswd)){
                throw new SystemException("系统异常，请稍后再试！");
            }
        }
        else {
            throw new SystemException("只有连锁商管理员或门店管理员允许找回密码！");
        }
    }
    
    /**
     * 验证登录用户
     * @param loginId
     * @param passwd
     * @return
     * @CreateDate: 2016-8-28 下午4:54:14
     */
    public SysUser checkLogin(String loginId, String passwd){
        //log.info("用户登录：loginId：" + loginId + ", passwd: "+ passwd);
        SysUser user = getDao().queryById(loginId);
        if (user == null) {
            return null;
        }
        else if (!passwd.equalsIgnoreCase(user.getPassword())){
            return null;
        }
        return user;
    }

    /**
     * 验证微信登录用户
     * @param mobile
     * @return
     */
    public SysUser checkWechatLogin(String mobile){
        log.info("微信用户登录：mobile：" + mobile);
        SysUser user = getDao().queryById(mobile);
        if (user == null) {
            return null;
        }
        return user;
    }
    
    /**
     * 修改密码
     * @param loginId 用户ID
     * @param passwd 用户原密码
     * @param newPasswd 用户新密码
     */
    @Transactional
    public int updateUserPasswd(String loginId, String passwd, String newPasswd) throws SystemException{
        //验证用户名密码是否正确
        SysUser sysUser = checkLogin(loginId, passwd);
        if(sysUser == null){
            throw new SystemException("原密码不正确");
        }
        
        //新密码格式粗略校验
        if(newPasswd.length() != 32){
            throw new SystemException("新密码MD5值格式不正确");
        }
        sysUser.setPassword(newPasswd);
        sysUser.setUpdateUserid(sysUser.getUid());
        return getDao().update(sysUser);
    }
    
    /**
     * 验证Cookie用户登录
     * @param loginId
     * @param passwd
     * @return
     * @CreateDate: 2016-8-28 下午4:54:14
     */
    public SysUser checkCookieLogin(String loginId, String passwd){
        //log.info("用户登录：loginId：" + loginId + ", passwd: "+ passwd);
        SysUser user = getDao().queryById(loginId);
        if (user == null) {
            return null;
        }
        else if (!passwd.equalsIgnoreCase(MD5Util.encodePassword(user.getPassword()))){
            return null;
        }
        return user;
    }

    /**
     * 创建登录用户
     * 创建用户时会根据用户类型给用户分配角色
     * @param loginId 登录账号
     * @param passwd 登录密码
     * @param uid 业务id
     * @param uname 用户名称
     * @param type 用户类型
     * @return
     * @CreateDate: 2016-9-6 下午11:03:37
     */
    public SysUser createUser(String loginId, String passwd, 
            String uid, String uname, Integer type, String createUserId){
        //创建登录用户
        SysUser user = new SysUser();
        user.setLoginId(loginId);
        user.setPassword(MD5Util.encodePassword(passwd));
        user.setUid(uid);
        user.setUname(uname);
        user.setType(type);
        user.setIsDel(0);
        user.setCreateUserid(createUserId);
        getDao().add(user);
        SysUserRole userRole = new SysUserRole();
        userRole.setUserId(uid);
        userRole.setState(0);
        //分配角色
        if (type == SystemConstant.USER_TYPE_PROVIDER) {
            //连锁商
            userRole.setRoleId(SystemConstant.USER_ROLE_PROVIDER);
        }
        else if (type == SystemConstant.USER_TYPE_SHOP) {
            //门店商
            userRole.setRoleId(SystemConstant.USER_ROLE_SHOP);
        }
        else if (type == SystemConstant.USER_TYPE_ENGINEER) {
            //工程师
            userRole.setRoleId(SystemConstant.USER_ROLE_ENGINEER);
        }
        else if (type == SystemConstant.USER_TYPE_CUSTOMER) {
            //客户
            userRole.setRoleId(SystemConstant.USER_ROLE_CUSTOMER);
        }
        else if (type == SystemConstant.USER_TYPE_CUSTOMER_SERVICE) {
            //客服
            userRole.setRoleId(SystemConstant.USER_ROLE_CUSTOMER_SERVICE);
        }
        sysUserRoleService.add(userRole);
        return user;
    }
    
    /**
     * 重置密码
     * @param loginId 登录账号
     * @param newPasswd 登录密码
     * @param updateUserid 业务id
     * @return
     * @CreateDate: 2016-9-6 下午11:03:37
     */
    public SysUser resetPasswd(String loginId, String newPasswd, String updateUserid){
        SysUser user = getDao().queryById(loginId);
        if (user == null) {
            return null;
        }
        user.setPassword(MD5Util.encodePassword(newPasswd));
        user.setUpdateUserid(updateUserid);
        getDao().update(user);
        return user;
    }

    /**
     * 重置用户名称
     * @param loginId 登录账号
     * @param newUName 登录密码
     * @param updateUserid 业务id
     * @return
     * @CreateDate: 2016-9-6 下午11:03:37
     */
    public SysUser resetUserName(String loginId, String newUName, String updateUserid){
        SysUser user = getDao().queryById(loginId);
        if (user == null) {
            return null;
        }
        user.setUname(newUName);
        user.setUpdateUserid(updateUserid);
        getDao().update(user);
        return user;
    }
    
    /**
     * 删除用户
     * @param loginId 登录账号
     * @param updateUserid 业务id
     * @return
     * @CreateDate: 2016-9-6 下午11:03:37
     */
    public SysUser deleteUser(String loginId, String updateUserid){
        SysUser user = getDao().queryById(loginId);
        if (user == null) {
            return null;
        }
        user.setIsDel(1);
        user.setUpdateUserid(updateUserid);
        getDao().update(user);
        return user;
    }
}