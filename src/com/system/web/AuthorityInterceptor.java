package com.system.web;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.common.exception.SessionInvalidateException;
import com.system.api.entity.ResultData;
import com.system.basic.user.entity.LoginUser;
import com.system.basic.user.entity.SessionUser;
import com.system.basic.user.entity.SysMenu;
import com.system.basic.user.service.LoginUserService;
import com.system.basic.user.service.SessionUserService;
import com.system.constant.SystemConstant;
import org.apache.cxf.common.i18n.Exception;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

/**
 * 权限拦截器
 */
public class AuthorityInterceptor extends HandlerInterceptorAdapter {
    private static final Logger log = Logger.getLogger(AuthorityInterceptor.class);
    //匿名用户可访问的地址
    private String[] anonymousUrls;
    @Autowired
    private SessionUserService sessionUserService;
    @Autowired
    private LoginUserService loginUserService;

    private static final SerializeConfig mapping = new SerializeConfig();
    private static final String DEFAULT_ENCODING = "UTF-8";
    public static final String JSON_TYPE = "application/json";

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response, Object handler) throws Exception, IOException {
        String sessionId = request.getSession().getId(); //获取sessionID
        ResultData result = new ResultData();
        //    log.info("IP : " + BaseController.getIpAddress(request)+"   sessionId  "+sessionId);
        //获取请求地址
        String uri = request.getRequestURI();
        //获取应用名称
        String ctx = request.getContextPath();

        String url = request.getScheme() + "://" + request.getServerName() + request.getRequestURI();
        log.info("地址：" + url);
        //去掉项目名称
        uri = uri.replaceFirst(ctx, "");
        if (uri.indexOf(";") > 0) {
            uri = uri.substring(0, uri.indexOf(";"));
        }
        //获取登录用户
        SessionUser sessionUser = (SessionUser) (request.getSession().getAttribute(SystemConstant.SESSION_USER_KEY));


        //对于手机端的用户登录 还需判定用户是否处于唯一登录环境 
        if (sessionUser != null && uri.startsWith("/wechat/order/wechatLogin")) {
            verifyLogin(sessionUser, sessionId, request);
        }
        //访问无权限页面，直接放行
        if (checkUriMatch(uri, anonymousUrls)) {
            // log.info("符合无session通过条件");
            return true;
        }


        if (sessionUser == null) {
            //验证cookie用户是否存在
            if (sessionUserService.checkCookieUser(request)) {
                return true;
            } else {
                //判断是否是因为session丢失
                //throw new SessionInvalidateException("您离开系统时间过长，请重新登录");
                result.setResultMessage("您离开系统时间过长，请重新登录");
                renderJson(response, result);
                return false;
            }
        } else {
            //如果是客户操作限制手机端唯一登录
            if (uri.startsWith("/admin/checkLogin")) {
                verifyLogin(sessionUser, sessionId, request);
            }
            //验证用户权限
            if (checkUriFromList(uri, sessionUser.getUserAuthoritys())) {
                return true;
            }
            if (sessionUser.getType() == 8) {
                //特殊用户登录后台
                return true;
            }

        }
        result.setResultMessage("对不起，您没有访问权限！");
        renderJson(response, result);
        return false;
    }
    /**
     * 以Json格式输出
     *
     * @param response
     * @param result
     * @throws IOException
     */
    public void renderJson(HttpServletResponse response, Object result) throws IOException {
        initContentType(response, JSON_TYPE);
        // 输入流
        PrintWriter out = response.getWriter();
        String outrs = JSON.toJSONString(result, mapping, SerializerFeature.WriteMapNullValue);
        out.print(outrs);
        out.flush();
    }

    /**
     * 初始HTTP内容类型.
     *
     * @param response
     * @param contentType
     */
    private void initContentType(HttpServletResponse response, String contentType) {
        response.setContentType(contentType + ";charset=" + DEFAULT_ENCODING);
    }

    @Override
    public void postHandle(HttpServletRequest request,
                           HttpServletResponse response, Object handler, ModelAndView mav)
            throws Exception {
        //  log.info("front action postHandle....");
    }

    /**
     *
     */
    public void afterCompletion(HttpServletRequest request,
                                HttpServletResponse response, Object handler, Exception ex)
            throws Exception {

    }

    /**
     * 验证用户手机端是否唯一登录
     */
    public void verifyLogin(SessionUser sessionUser, String sessionId, HttpServletRequest request) {
        String ua = request.getHeader("user-agent").toLowerCase();
        List<LoginUser> list = loginUserService.findLoginUserBysessionId(sessionId);
        if (sessionUser != null && list.size() == 0 && checkAgentIsMobile(ua)) {
            request.getSession().invalidate();
            throw new SessionInvalidateException("您的账号已在其他地方登录，请重新登录");
        }
        if (sessionUser != null && list.size() > 0 && checkAgentIsMobile(ua)) {//来自手机端的登录
            boolean tip = false;
            for (LoginUser u : list) {
                if (u.getUid().equals(sessionUser.getUserId())) {
                    tip = true;
                }
            }
            if (!tip) {
                request.getSession().invalidate();
                throw new SessionInvalidateException("您的账号已在其他地方登录，请重新登录");
            }
        }
    }


    /**
     * 检测当前拦截的uri 是否在权限范围内
     *
     * @param uri
     * @param list
     * @return
     */
    private boolean checkUriFromList(String uri, List<SysMenu> list) {
        if (list != null) {
            for (SysMenu menu : list) {
                String href = menu.getHref();
                if (href != null) {
                    String[] hrefFront = href.split("\\?");
                    if (uri.equals(hrefFront[0])) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * 验证uri是否配置 配置的url规则（pattenUrls需符合java正则表达式的格式）
     *
     * @param uri
     * @param pattenUrls
     * @return
     */
    private boolean checkUriMatch(String uri, String[] pattenUrls) {
        for (String pattenUrl : pattenUrls) {
            if (uri.matches(pattenUrl)) {
                return true;
            }
        }

        if (uri.equals("/")) {
            return true;
        }

        return false;
    }

    public String[] getAnonymousUrls() {
        return anonymousUrls;
    }

    public void setAnonymousUrls(String[] anonymousUrls) {
        this.anonymousUrls = anonymousUrls;
    }

    //手机识别
    private final static String[] agent = {"android", "iphone", "ipod", "ipad", "windows phone", "mqqbrowser"};

    private static boolean checkAgentIsMobile(String ua) {
        boolean flag = false;
        if (!ua.contains("windows nt")
                || (ua.contains("windows nt")
                && ua.contains("compatible; msie 9.0;"))) {
            // 排除 苹果桌面系统
            if (!ua.contains("windows nt") && !ua.contains("macintosh")) {
                for (String item : agent) {
                    if (ua.contains(item)) {
                        flag = true;
                        break;
                    }
                }
            }
        }
        return flag;
    }

}