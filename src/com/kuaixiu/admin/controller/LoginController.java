package com.kuaixiu.admin.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSONObject;
import com.common.base.controller.BaseController;
import com.common.exception.SystemException;
import com.common.util.Base64Util;
import com.common.util.Consts;
import com.common.util.CookiesUtil;
import com.common.util.MD5Util;
import com.google.common.collect.Maps;
import com.system.basic.user.entity.SessionUser;
import com.system.basic.user.entity.SysUser;
import com.system.basic.user.service.SessionUserService;
import com.system.basic.user.service.SysMenuService;
import com.system.basic.user.service.SysUserService;


/**
 * 登录控制类.
 *
 * @author: lijx
 * @CreateDate: 2016-8-19 下午11:49:41
 * @version: V 1.0
 */
@Controller
public class LoginController extends BaseController {
    private final Logger logger = Logger.getLogger(LoginController.class);

    @Autowired
    private SysMenuService menuService;
    @Autowired
    private SysUserService userService;
    @Autowired
    private SessionUserService sessionUserService;

    /**
     * 后台首页判断是否登录
     *
     * @return
     */
    @RequestMapping("/admin")
    public String admin() {
        return "admin/loginCheck";
    }
    
    /**
     * 后台首页判断是否登录
     *
     * @return
     */
    @RequestMapping("/admin/")
    public String admin1() {
        return "admin/loginCheck";
    }
    
    /**
     * 登录页面
     *
     * @return
     */
    @RequestMapping("/admin/login")
    public String goLogin() {
        return "admin/login";
    }

    /**
     * 忘记密码
     *
     * @return
     */
    @RequestMapping("/admin/forgot")
    public String forgot() {
        return "admin/forgot";
    }
    
    /**
     * 忘记密码
     *
     * @return
     */
    @RequestMapping("/admin/forgotCheck")
    public void forgotCheck(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        Map<String, Object> resultMap = Maps.newHashMap();
        try{
            //获取用户名称
            String loginId = request.getParameter("loginId");
            //获取手机号
            String mobile = request.getParameter("mobile");
            userService.forgotCheck(loginId, mobile);
            resultMap.put(RESULTMAP_KEY_SUCCESS, RESULTMAP_SUCCESS_TRUE);
            resultMap.put(RESULTMAP_KEY_MSG, "OK");
        }
        catch (SystemException e) {
            //e.printStackTrace();
            resultMap.put(RESULTMAP_KEY_SUCCESS, RESULTMAP_SUCCESS_FALSE);
            resultMap.put(RESULTMAP_KEY_MSG, e.getMessage());
        }
        catch (Exception e) {
            e.printStackTrace();
            resultMap.put(RESULTMAP_KEY_SUCCESS, RESULTMAP_SUCCESS_FALSE);
            resultMap.put(RESULTMAP_KEY_MSG, "系统异常，请稍后再试！");
        }
        renderJson(response, resultMap);
    }
    
    /**
     * 退出
     *
     * @param request
     * @param response
     * @throws Exception
     * @CreateDate: 2016-8-28 下午4:30:18
     */
    @RequestMapping("/admin/logout")
    public ModelAndView logout(HttpServletRequest request,
                               HttpServletResponse response) throws Exception {
        try {
            //初始化SessionUser
            sessionUserService.removeSessionUser(request);
        } 
        catch (Exception e) {
            e.printStackTrace();
        }
        String returnView = "admin/login";
        return new ModelAndView(returnView);
    }

    /**
     * 登录验证
     *
     * @param request
     * @param response
     * @throws Exception
     * @CreateDate: 2016-8-28 下午4:30:18
     */
    @RequestMapping("/admin/checkLogin")
    public void checkLogin(HttpServletRequest request,
                           HttpServletResponse response) throws Exception {
        Map<String, Object> resultMap = Maps.newHashMap();
        try {
            //获取用户名称
            String loginId = request.getParameter("loginId");
            //获取密码
            String passwd = request.getParameter("passwd");
            //是否选中自动登录
            String isChecked = request.getParameter("autoLogin");
            SysUser user = userService.checkLogin(loginId, passwd);
            if (user == null) {
                resultMap.put(RESULTMAP_KEY_SUCCESS, RESULTMAP_SUCCESS_FALSE);
                resultMap.put(RESULTMAP_KEY_MSG, "用户名或密码错误");
            } 
            else if (user.getIsDel() == 1) {
            	resultMap.put(RESULTMAP_KEY_SUCCESS, RESULTMAP_SUCCESS_FALSE);
                resultMap.put(RESULTMAP_KEY_MSG, "用户名已删除");
            }
            else {
                //初始化SessionUser
                sessionUserService.initSessionUser(user, request);

                resultMap.put(RESULTMAP_KEY_SUCCESS, RESULTMAP_SUCCESS_TRUE);
                resultMap.put(RESULTMAP_KEY_MSG, "登录成功");
                String[] dname = request.getServerName().split("\\.");
                //自动登录COOKIE存储
                try {
                    if (!StringUtils.isBlank(isChecked) && isChecked.equals(Consts.AUTO_LOGIN_CHECKED)) {
                        String loginidBase64 = Base64Util.getBase64(loginId);
                        //为了安全,md5后的密码，再次Md5加密一次后再base64加密
                        String pwdBase64 = Base64Util.getBase64(MD5Util.encodePassword(passwd));
                        String userinfoStr = loginidBase64 + Consts.COOKIE_USER_INFO_SIGN + pwdBase64;
                        //用户名base64加密,存入COOKIE
                        CookiesUtil.setCookie(response, Consts.COOKIE_LOGIN_USER_INFO, Base64Util.getBase64(userinfoStr), CookiesUtil.prepare(dname), Consts.COOKIE_LOGIN_USER_EXPIRES_IN);
                    }
                    else {
                        //如果未选择保存则清空cookie
                        CookiesUtil.setCookie(response, Consts.COOKIE_LOGIN_USER_INFO, "", CookiesUtil.prepare(dname), Consts.COOKIE_LOGIN_USER_EXPIRES_IN);
                    }
                }
                catch (Exception e) {
                    System.out.println("ServerName: " + request.getServerName());
                    System.out.println("dname: " + CookiesUtil.prepare(dname));
                    e.printStackTrace();
                }
            }
        } 
        catch (Exception e) {
            e.printStackTrace();
            resultMap.put(RESULTMAP_KEY_SUCCESS, RESULTMAP_SUCCESS_FALSE);
            resultMap.put(RESULTMAP_KEY_MSG, "系统异常请稍后");
        }
        renderJson(response, resultMap);
    }
    
    /**
     * 验证cookie
     *
     * @param request
     * @param response
     * @throws Exception
     * @CreateDate: 2016-8-28 下午4:30:18
     */
    @RequestMapping("/admin/checkCookie")
    public ModelAndView checkCookie(HttpServletRequest request,
                               HttpServletResponse response) throws Exception {
        String returnView = "";
        SessionUser su = getCurrentUser(request);
        if (su != null) {
            returnView = "redirect:/admin/index.do";
        }
        else if (sessionUserService.checkCookieUser(request)){
            returnView = "redirect:/admin/index.do";
        }
        else{
            returnView = "redirect:/admin/login.do";
        }
        return new ModelAndView(returnView);
    }
}
