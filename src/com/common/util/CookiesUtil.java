package com.common.util;

import org.apache.commons.lang3.StringUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 自定义cookie
 * 
 * @author yq
 */
public class CookiesUtil {
    /**
     * 设置cookie
     * 
     * @param response
     * @param name
     *            cookie名字
     * @param value
     *            cookie值
     * @param domainName
     *            域名
     * @param maxAge
     *            cookie生命周期 以秒为单位 (值为-1时不设置cookie时间，值为0时清除cookie)
     * @throws UnsupportedEncodingException 
     */
    public static void setCookie(HttpServletResponse response, String name, String value, String domainName, int maxAge) throws UnsupportedEncodingException {
        value=URLEncoder.encode(value,"UTF-8");
        Cookie cookie = new Cookie(name, value);
        cookie.setPath("/");
        if (StringUtils.isBlank(domainName)){
            cookie.setDomain(domainName);
        }
        if (maxAge >= 0){
            cookie.setMaxAge(maxAge);
        }
        response.addCookie(cookie);
    }

    /**
     * 根据名字获取cookie
     * 
     * @param request
     * @param name cookie名字
     * @return
     */
    public static Cookie getCookieByName(HttpServletRequest request, String name) {
        Map<String, Cookie> cookieMap = readCookieMap(request);
        if (cookieMap.containsKey(name)) {
            Cookie cookie = (Cookie) cookieMap.get(name);
            return cookie;
        } 
        else {
            return null;
        }
    }

    /**
     * 将cookie封装到Map里面
     * 
     * @param request
     * @return
     */
    private static Map<String, Cookie> readCookieMap(HttpServletRequest request) {
        Map<String, Cookie> cookieMap = new HashMap<String, Cookie>();
        Cookie[] cookies = request.getCookies();
        if (null != cookies) {
            for (Cookie cookie : cookies) {
                cookieMap.put(cookie.getName(), cookie);
            }
        }
        return cookieMap;
    }
    
    
    /**
     * 获取根域名
     * 
     * @param dname
     * @return
     * @throws Exception
     *
     */
    public static String prepare(String[] dname) throws Exception {
        // 根域名
        String rootDomainName = null; 

        if (dname.length == 2) {
            rootDomainName = dname[0] + "." + dname[1];
        }
        else if (dname.length == 3) {
            rootDomainName = dname[1] + "." + dname[2];
        } 
        else if (dname.length == 4) {
            rootDomainName = dname[1] + "." + dname[2] + "." + dname[3];
        }
        if (dname.length > 0 && rootDomainName == null) {
            if (dname[0].equals("localhost")){
                rootDomainName = "localhost";
            }
        }
        return rootDomainName;
    }

}
