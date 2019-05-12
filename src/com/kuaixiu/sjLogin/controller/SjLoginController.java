package com.kuaixiu.sjLogin.controller;

import com.common.base.controller.BaseController;
import com.common.util.Base64Util;
import com.common.util.Consts;
import com.common.util.CookiesUtil;
import com.common.util.MD5Util;
import com.google.common.collect.Maps;
import com.kuaixiu.sjUser.entity.SjUser;
import com.kuaixiu.sjUser.service.SjSessionUserService;
import com.kuaixiu.sjUser.service.SjUserService;
import com.system.api.entity.ResultData;
import com.system.basic.user.entity.SessionUser;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;


/**
 * 登录控制类.
 *
 * @author: lijx
 * @CreateDate: 2016-8-19 下午11:49:41
 * @version: V 1.0
 */
@Controller
public class SjLoginController extends BaseController {
    private final Logger logger = Logger.getLogger(SjLoginController.class);

    @Autowired
    private SjUserService userService;
    @Autowired
    private SjSessionUserService sessionUserService;

    /**
     * 后台首页判断是否登录
     *
     * @return
     */
    @RequestMapping("/sj/admin")
    public String admin() {
        return "sjAdmin/loginCheck";
    }

    /**
     * 后台首页判断是否登录
     *
     * @return
     */
    @RequestMapping("/sj/admin/")
    public String admin1() {
        return "sjAdmin/loginCheck";
    }

    /**
     * 登录页面
     *
     * @return
     */
    @RequestMapping("/sj/admin/login")
    public String goLogin() {
        return "sjAdmin/login";
    }

//    /**
//     * 忘记密码
//     *
//     * @return
//     */
//    @RequestMapping("/business/forgot")
//    public String forgot() {
//        return "admin/forgot";
//    }
//
//    /**
//     * 忘记密码
//     *
//     * @return
//     */
//    @RequestMapping("/business/forgotCheck")
//    public void forgotCheck(HttpServletRequest request,
//            HttpServletResponse response) throws Exception {
//        Map<String, Object> resultMap = Maps.newHashMap();
//        try{
//            //获取用户名称
//            String loginId = request.getParameter("loginId");
//            //获取手机号
//            String mobile = request.getParameter("mobile");
//            userService.forgotCheck(loginId, mobile);
//            resultMap.put(RESULTMAP_KEY_SUCCESS, RESULTMAP_SUCCESS_TRUE);
//            resultMap.put(RESULTMAP_KEY_MSG, "OK");
//        }
//        catch (SystemException e) {
//            //e.printStackTrace();
//            resultMap.put(RESULTMAP_KEY_SUCCESS, RESULTMAP_SUCCESS_FALSE);
//            resultMap.put(RESULTMAP_KEY_MSG, e.getMessage());
//        }
//        catch (Exception e) {
//            e.printStackTrace();
//            resultMap.put(RESULTMAP_KEY_SUCCESS, RESULTMAP_SUCCESS_FALSE);
//            resultMap.put(RESULTMAP_KEY_MSG, "系统异常，请稍后再试！");
//        }
//        renderJson(response, resultMap);
//    }

    /**
     * 退出
     *
     * @param request
     * @param response
     * @throws Exception
     * @CreateDate: 2016-8-28 下午4:30:18
     */
    @RequestMapping("/sj/admin/logout")
    public ModelAndView logout(HttpServletRequest request,
                               HttpServletResponse response) throws Exception {
        try {
            //初始化SessionUser
            sessionUserService.removeSessionUser(request);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String returnView = "sjAdmin/login";
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
    @RequestMapping("/sj/admin/checkLogin")
    @ResponseBody
    public ResultData checkLogin(HttpServletRequest request,
                                 HttpServletResponse response) throws Exception {
        ResultData result = new ResultData();
        try {
            //获取用户名称
            String loginId = request.getParameter("loginId");
            //获取密码
            String passwd = request.getParameter("passwd");
            //是否选中自动登录
            String isChecked = request.getParameter("autoLogin");
            SjUser user = userService.checkLogin(loginId, passwd);
            if (StringUtils.isBlank(loginId) || StringUtils.isBlank(passwd)) {
                return getSjResult(result, null, false, "2", "用户名或密码不能为空", null);
            }
            if (user == null) {
                return getSjResult(result, null, false, "2", "用户名或密码错误", null);
            } else if (user.getIsDel() == 1) {
                return getSjResult(result, null, false, "3", "用户名已删除", null);
            }
            //初始化SessionUser
            sessionUserService.initSessionUser(user, request);

            getSjResult(result, null, true, "0", "登录成功", null);

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
                } else {
                    //如果未选择保存则清空cookie
                    CookiesUtil.setCookie(response, Consts.COOKIE_LOGIN_USER_INFO, "", CookiesUtil.prepare(dname), Consts.COOKIE_LOGIN_USER_EXPIRES_IN);
                }
            } catch (Exception e) {
                System.out.println("ServerName: " + request.getServerName());
                System.out.println("dname: " + CookiesUtil.prepare(dname));
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
            getSjResult(result, null, false, "5", "系统异常请稍后", null);
        }
        return result;
    }

    /**
     * 验证cookie
     *
     * @param request
     * @param response
     * @throws Exception
     * @CreateDate: 2016-8-28 下午4:30:18
     */
    @RequestMapping("/sj/admin/checkCookie")
    public ModelAndView checkCookie(HttpServletRequest request,
                                    HttpServletResponse response) throws Exception {
        String returnView = "";
        SessionUser su = getCurrentUser(request);
        if (su != null) {
            returnView = "redirect:/sjAdmin/index.do";
        } else if (sessionUserService.checkCookieUser(request)) {
            returnView = "redirect:/sjAdmin/index.do";
        } else {
            returnView = "redirect:/sjAdmin/login.do";
        }
        return new ModelAndView(returnView);
    }
}
