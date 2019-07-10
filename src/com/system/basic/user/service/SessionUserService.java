package com.system.basic.user.service;


import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.system.basic.user.entity.SysMenu;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.common.exception.SystemException;
import com.common.util.Base64Util;
import com.common.util.Consts;
import com.common.util.CookiesUtil;
import com.kuaixiu.shop.entity.Shop;
import com.kuaixiu.shop.service.ShopService;
import com.system.api.entity.ResultData;
import com.system.basic.user.entity.LoginUser;
import com.system.basic.user.entity.SessionUser;
import com.system.basic.user.entity.SysUser;
import com.system.constant.ApiResultConstant;
import com.system.constant.SystemConstant;

/**
 * SysUser Service
 *
 * @CreateDate: 2016-08-26 下午10:27:15
 * @version: V 1.0
 */
@Service("sessionUserService")
public class SessionUserService {
    private static final Logger log = Logger.getLogger(SessionUserService.class);

    @Autowired
    private SysRoleService roleService;
    @Autowired
    private SysMenuService menuService;
    @Autowired
    private ShopService shopService;
    @Autowired
    private SysUserService userService;
    @Autowired
    private LoginUserService loginUserService;
    //**********自定义方法***********

    /**
     * 初始化SessionUser
     *
     * @param user
     * @param request
     * @author: lijx
     * @CreateDate: 2016-8-28 下午6:46:35
     */
    public void initSessionUser(SysUser user, HttpServletRequest request) {
        if (user == null) {
            throw new SystemException("登录用户为空，初始化失败");
        }
        SessionUser su = new SessionUser();
        su.setUser(user);
        su.setUserId(user.getUid());
        su.setUserName(user.getUname());
        su.setType(user.getType());
        su.setSysUserRoleList(roleService.queryRolesByUserId(su.getUserId()));
        //家族用户权限
        su.setUserAuthoritys(menuService.queryMenusByUserId(su.getUserId()));
        //设置用户菜单
        su.setSysMenuList(menuService.dealSysMenusByUserAuthoritys(su.getUserAuthoritys()));

        //根据用户类型加载用户信息
        if (su.getType() == SystemConstant.USER_TYPE_PROVIDER) {
            //如果是连锁商
            su.setProviderCode(su.getUserId());
            su.setProviderName(su.getUserName());
        } else if (su.getType() == SystemConstant.USER_TYPE_SHOP) {
            su.setShopCode(su.getUserId());
            su.setShopName(su.getUserName());
            Shop shop = shopService.queryByCode(su.getUserId());
            su.setProviderCode(shop.getProviderCode());
            su.setProviderName(shop.getProviderName());
        }
        //保存用户到session
        HttpSession session = request.getSession();
        try {
            List<SysMenu> menuList = su.getSysMenuList();
            SysMenu menu = menuList.get(0);
            List<SysMenu> menuList1 = menu.getSubMenuList();
            SysMenu menu2 = menuList1.get(0);
            List<SysMenu> menuList2 = menu2.getSubMenuList();
            session.setAttribute("indexUrl", menuList2.get(0).getHref());
        } catch (IndexOutOfBoundsException i) {

        }
        session.setAttribute(SystemConstant.SESSION_USER_KEY, su);
        session.setAttribute("sysMenuList", su.getSysMenuList());
        session.setAttribute("loginUserName", su.getUserName());
        session.setAttribute("loginUserType", su.getType());
        session.setAttribute("loginUserId", su.getUserId());
    }

    /**
     * 客户端登录初始化
     */
    public void customerInitSessionUser(SysUser user, HttpServletRequest request, String accessToken) {
        if (user == null) {
            throw new SystemException("登录用户为空，初始化失败");
        }
        SessionUser su = new SessionUser();
        su.setUser(user);
        su.setUserId(user.getUid());
        su.setUserName(user.getUname());
        su.setType(user.getType());
        su.setSysUserRoleList(roleService.queryRolesByUserId(su.getUserId()));
        //家族用户权限
        su.setUserAuthoritys(menuService.queryMenusByUserId(su.getUserId()));
        //设置用户菜单
        su.setSysMenuList(menuService.dealSysMenusByUserAuthoritys(su.getUserAuthoritys()));

        //根据用户类型加载用户信息
        if (su.getType() == SystemConstant.USER_TYPE_PROVIDER) {
            //如果是连锁商
            su.setProviderCode(su.getUserId());
            su.setProviderName(su.getUserName());
        } else if (su.getType() == SystemConstant.USER_TYPE_SHOP) {
            su.setShopCode(su.getUserId());
            su.setShopName(su.getUserName());
            Shop shop = shopService.queryByCode(su.getUserId());
            su.setProviderCode(shop.getProviderCode());
            su.setProviderName(shop.getProviderName());
        }

        //保存用户到session
        HttpSession session = request.getSession();
        session.setAttribute(SystemConstant.SESSION_USER_KEY, su);
        session.setAttribute("sysMenuList", su.getSysMenuList());
        session.setAttribute("loginUserName", su.getUserName());
        session.setAttribute("loginUserType", su.getType());
        session.setAttribute("loginUserId", su.getUserId());
        session.setAttribute("accessToken", accessToken);
    }

    /**
     * 删除SessionUser
     *
     * @param request
     * @author: lijx
     * @CreateDate: 2016-8-28 下午6:46:35
     */
    public void removeSessionUser(HttpServletRequest request) {
        //从session中移除用户信息
        HttpSession session = request.getSession();
        session.setAttribute(SystemConstant.SESSION_USER_KEY, null);
        session.setAttribute("sysMenuList", null);
        session.setAttribute("loginUserName", null);
        session.setAttribute("loginUserType", null);
        String openId = (String) session.getAttribute(SystemConstant.SESSION_OPENID);
        if (openId != null) {
            LoginUser loginUser = loginUserService.findLoginUserByOpenId(openId);
            loginUserService.updateLoginUser(loginUser);
        }
    }


    /**
     * 验证Cookie用户
     *
     * @param request
     * @return
     * @author: lijx
     * @CreateDate: 2016-9-8 下午11:05:24
     */
    public boolean checkCookieUser(HttpServletRequest request) {
        //如果session为空，则取Cookie验证
        Cookie cookie = CookiesUtil.getCookieByName(request, Consts.COOKIE_LOGIN_USER_INFO);
        if (cookie == null || StringUtils.isBlank(cookie.getValue())) {
            return false;
        }
        //Cookie不过期
        String userInfoStr = cookie.getValue();
        try {
            userInfoStr = URLDecoder.decode(userInfoStr, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        //1.先base64解密成拼接字符串
        userInfoStr = Base64Util.getFromBase64(userInfoStr);
        //2.分别取出用户名密码
        String loginIdBase64 = userInfoStr.substring(0, userInfoStr.indexOf(Consts.COOKIE_USER_INFO_SIGN));
        String pwdBase64 = userInfoStr.substring(userInfoStr.indexOf(Consts.COOKIE_USER_INFO_SIGN) + Consts.COOKIE_USER_INFO_SIGN.length());
        //3.base64解密用户名
        String loginId = Base64Util.getFromBase64(loginIdBase64);
        //4.base64解密密码
        String pwd = Base64Util.getFromBase64(pwdBase64);

        SysUser user = userService.checkCookieLogin(loginId, pwd);
        if (user == null) {
            log.error("!![cookie login]loginid or pwd is wrong.");
            return false;
        } else {
            //初始化SessionUser
            initSessionUser(user, request);
            log.info("##[cookie login]login success");
            return true;
        }
    }


    /**
     * 业务异常返回  success=false resultMessage=SystemException.msg  resultCode=ApiServiceException.resultCode
     */
    public void getSystemException(SystemException e, ResultData result) {
        e.printStackTrace();
        String msg = e.getMessage();
        if (msg == null || msg == "") {
            msg = "系统繁忙，请稍后重试";
        }
        result.setResultMessage(msg);
        result.setResultCode(e.getBackUrl());
        result.setSuccess(false);
    }

    /**
     * 成功返回数据  success=true result=JSONbject resuleCode=0
     */
    public void getSuccessResult(ResultData result, JSONObject jsonResult) {
        result.setSuccess(true);
        result.setResult(jsonResult);
        result.setResultCode("0");
    }

    /**
     * 系统异常返回 success=false resultMessage=服务异常，请稍后重试  resuleCode=5001
     */
    public void getException(ResultData result) {
        result.setSuccess(false);
        result.setResultCode(ApiResultConstant.resultCode_5001);
        result.setResultMessage(ApiResultConstant.resultCode_str_5001);
    }
}